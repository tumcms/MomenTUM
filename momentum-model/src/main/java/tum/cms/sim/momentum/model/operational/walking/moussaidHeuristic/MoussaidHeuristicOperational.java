/*******************************************************************************
 * Welcome to the pedestrian simulation framework MomenTUM. 
 * This file belongs to the MomenTUM version 2.0.2.
 * 
 * This software was developed under the lead of Dr. Peter M. Kielar at the
 * Chair of Computational Modeling and Simulation at the Technical University Munich.
 * 
 * All rights reserved. Copyright (C) 2017.
 * 
 * Contact: peter.kielar@tum.de, https://www.cms.bgu.tum.de/en/
 * 
 * Permission is hereby granted, free of charge, to use and/or copy this software
 * for non-commercial research and education purposes if the authors of this
 * software and their research papers are properly cited.
 * For citation information visit:
 * https://www.cms.bgu.tum.de/en/31-forschung/projekte/456-momentum
 * 
 * However, further rights are not granted.
 * If you need another license or specific rights, contact us!
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/

package tum.cms.sim.momentum.model.operational.walking.moussaidHeuristic;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.util.FastMath;

import tum.cms.sim.momentum.data.agent.pedestrian.state.operational.WalkingState;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IOperationalPedestrian;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrian;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrianExtension;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;
import tum.cms.sim.momentum.data.layout.obstacle.Obstacle;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.operational.walking.WalkingModel;
import tum.cms.sim.momentum.utility.geometry.GeometryFactory;
import tum.cms.sim.momentum.utility.geometry.Segment2D;
import tum.cms.sim.momentum.utility.geometry.Vector2D;
import tum.cms.sim.momentum.utility.geometry.operation.GeometryAdditionals;

public class MoussaidHeuristicOperational extends WalkingModel {

	private int angleSteps = 2;
	private int angleDeviation = 90;
	private double horizonDistance = 10.0;
	private double relaxation_time;
	private double physical_interaction_k = Double.NEGATIVE_INFINITY;	 // [kg / (m * s)]

	private ArrayList<Segment2D> collisionsInSight = new ArrayList<>();
	
	@Override
	public void callPreProcessing(SimulationState simulationState) {
		
		angleSteps = this.properties.getIntegerProperty("angleSteps");
		relaxation_time = this.properties.getDoubleProperty("relaxation_time");
		
		if( this.properties.getDoubleProperty("horizonDistance") != null) {
			
			horizonDistance = this.properties.getDoubleProperty("horizonDistance");
		}

		physical_interaction_k = this.properties.getDoubleProperty("physical_interaction_k");		
		angleDeviation = this.properties.getIntegerProperty("angleDeviation");

	}

	@Override
	public void callBeforeBehavior(SimulationState simulationState, Collection<IRichPedestrian> pedestrians) {
		// nothing to do
	}
	
	@Override
	public void callPostProcessing(SimulationState simulationState) {		
		// nothing to do
	}
	
	@Override
	public void callPedestrianBehavior(IOperationalPedestrian pedestrian, SimulationState simulationState) {
		
		try {

		double distanceMaximal = horizonDistance;// FastMath.min(minimalDistance, pedestrian.getNextWalkingTarget().subtract(pedestrian.getPosition()).getMagnitude());	
		
		if(!pedestrian.getNextWalkingTarget().equals(pedestrian.getPosition()) && distanceMaximal > 0.01) {
			
			Collection<IPedestrian> otherPedestrians = perception.getAllPedestrians(pedestrian);
			Pair<Double, Double> nearestObjectProperty = this.computeNextIntercept(pedestrian, 
					pedestrian.getNextWalkingTarget(), 
					otherPedestrians, 
					this.scenarioManager.getObstacles(),
					distanceMaximal);
			
			double distanceIntercept = nearestObjectProperty.getRight();
			double headingRotation = nearestObjectProperty.getLeft();
			double deltaHeadingRotation = headingRotation;// * simulationState.getTimeStepDuration();
	
			Vector2D heading = pedestrian.getHeading().rotate(deltaHeadingRotation);
			
			Vector2D forces = computeNewAcceleration(distanceIntercept,
					heading, 
					pedestrian,
					otherPedestrians,
					this.scenarioManager.getObstacles()); 
			
			// Moussaid concept
			double desiredVelocity = FastMath.min(pedestrian.getDesiredVelocity(), distanceIntercept / relaxation_time);	
			Vector2D velocityDirected = heading.multiply(desiredVelocity);
			Vector2D currentVelocity = pedestrian.getVelocity();
			
			Vector2D deltaVelocity = velocityDirected.subtract(currentVelocity)
					.multiply(1.0/relaxation_time)
					.sum(forces)
					.multiply(simulationState.getTimeStepDuration());
			
			Vector2D velocity = pedestrian.getVelocity().sum(deltaVelocity);
	
			if(velocity.getMagnitude() > pedestrian.getMaximalVelocity() ) {
			
				velocity = velocity.getNormalized()
						.multiply(pedestrian.getMaximalVelocity());
			}
			
			Vector2D deltaPosition = velocity.multiply(simulationState.getTimeStepDuration());
			Vector2D position = pedestrian.getPosition().sum(deltaPosition);
	
			WalkingState novelState = new WalkingState(position, velocity, heading);
			pedestrian.setWalkingState(novelState);
		}
		else {
			WalkingState novelState = new WalkingState(pedestrian.getPosition(), pedestrian.getVelocity(), pedestrian.getHeading());
			pedestrian.setWalkingState(novelState);
		}

		collisionsInSight.clear();
		}
		catch(Exception ex) {
			
			ex = null;
		}
	}	

	//------------------------------------------------------------------------------------------------------------------
	
	/**
	 * First return is the heading, second is the distance to collision
	 * @param me
	 * @param target
	 * @param otherPedestrians
	 * @param obstacles
	 * @return
	 */
	private Pair<Double, Double> computeNextIntercept(IOperationalPedestrian me,
			Vector2D target,
			Collection<IPedestrian> otherPedestrians,
			Collection<Obstacle> obstacles,
			double distanceMaximal) { 
	
		double[] f_a = new double[angleDeviation * 2 + 1];
		double angle_a = 0.0;

		Vector2D to_dest = target.subtract(me.getPosition()).getNormalized();
		double a_0 = (me.getHeading().getNormalized().getAngleBetween(to_dest));
		double interceptDistance = Double.MAX_VALUE;
		
		for(int iter = 0; iter < angleDeviation * 2 + 1; iter += angleSteps) {
			
			f_a[iter] = collisionInSight(me, otherPedestrians, 
					obstacles,
					GeometryAdditionals.translateToRadiant(iter - angleDeviation),
					distanceMaximal);
			
			interceptDistance = FastMath.min(f_a[iter], interceptDistance);
		}
		
		double d_a = Double.MAX_VALUE;//this.distanceMaximal + 1.0;

		double squaredDistance = FastMath.pow(distanceMaximal, 2.0);

		for(int iter = 0; iter < angleDeviation * 2 + 1; iter += angleSteps) {
			
			double lineRadiant = GeometryAdditionals.translateToRadiant(iter - angleDeviation);
			
			double dAlphaLast = (squaredDistance + f_a[iter] * f_a[iter]) -
					2.0 * distanceMaximal * f_a[iter] * FastMath.cos(a_0 - lineRadiant);

			if(dAlphaLast < d_a) {
				
				Vector2D a_now = me.getPosition().sum(me.getHeading().rotate(lineRadiant).scale(f_a[iter]));
				boolean wayBlocked = false;
				
				if(FastMath.abs(me.getPosition().distance(a_now) - distanceMaximal) < 0.1) {
					
					Segment2D lineSightA = GeometryFactory.createSegment(target, a_now);
					
					for(Obstacle obstacle : obstacles) {	
						
						for(Segment2D part : obstacle.getObstacleParts()) {
										
							if(part.getIntersection(lineSightA).size() > 0) {
								
								wayBlocked = true;
								break;
							}
						}
						
						if(wayBlocked) {
							break;
						}
					}	
				}
				
				if(!wayBlocked) {

					d_a = dAlphaLast; 
					angle_a = lineRadiant;
				}
			}
		}
		
		return new MutablePair<Double, Double>(angle_a, interceptDistance);
	}
	
	private double collisionInSight(IOperationalPedestrian me, 
			Collection<IPedestrian> otherPedestrians,
			Collection<Obstacle> obstacles,
			double angle,
			double d_max) {
		
		double nearestDistance = Double.MAX_VALUE;
		Vector2D a_now = me.getPosition().sum(me.getHeading().rotate(angle).scale(d_max));
		Segment2D lineSightA = GeometryFactory.createSegment(me.getPosition(), a_now);
		
		double distance = d_max;

		for(IPedestrian other : otherPedestrians) {
			
			if(lineSightA.distanceBetween(other.getPosition()) < other.getBodyRadius()) {
				
				Vector2D onSegementPoint = lineSightA.getPointOnSegmentClosestToVector(other.getPosition());
				distance = onSegementPoint.distance(me.getPosition());
				nearestDistance = FastMath.min(nearestDistance, distance);
			}
		}

		distance = d_max;
		
		for(Obstacle obstacle : obstacles) {	
			
			for(Segment2D part : obstacle.getObstacleParts()) {
							
				for(Vector2D intersection : part.getIntersection(lineSightA)) {
					
					//collisionsInSight.add(part);
					distance = FastMath.min(distance, intersection.distance(me.getPosition()));
					nearestDistance = FastMath.min(nearestDistance, distance);
				}
			}
		}										

		return FastMath.min(nearestDistance, d_max);
	}
	
	private Vector2D computeNewAcceleration(double distanceIntercept,
			Vector2D heading,
			IOperationalPedestrian pedestrian,
			Collection<IPedestrian> otherPedestrians,
			Collection<Obstacle> obstacles) {
		
		Vector2D sumOfPedestrianInteractionForces = GeometryFactory.createVector(0, 0);
		Vector2D pedestrianInteractionForce = null;
		
		for(IPedestrian other : otherPedestrians) {

			pedestrianInteractionForce = interactionPedestrian(pedestrian, other);
			sumOfPedestrianInteractionForces = sumOfPedestrianInteractionForces.sum(pedestrianInteractionForce);				
		}
		
		Vector2D sumOfObstacleInteractionForces = GeometryFactory.createVector(0, 0);
		Vector2D obstacleInteractionForce = null;
		
		for(Obstacle obstacle : obstacles) {

			for(Segment2D part : obstacle.getObstacleParts()) {
			
				obstacleInteractionForce = interactionObstacle(pedestrian, part);
				sumOfObstacleInteractionForces = sumOfObstacleInteractionForces.sum(obstacleInteractionForce);			
			}
		}

		sumOfPedestrianInteractionForces.multiply(1.0/pedestrian.getMass());
		sumOfObstacleInteractionForces.multiply(1.0/pedestrian.getMass());

		return sumOfObstacleInteractionForces.sum(sumOfPedestrianInteractionForces);
	}
	
//	private Vector2D computePedestrianInteractionForce(IOperationalPedestrian me, IPedestrian you) {
//		
//		//Vector2D bodyForce = this.computeBodyForce(me, you);
//		//Vector2D slidingFrictionForce = this.computeSlidingFrictionForce(me, you);
//		//Vector2D repulsiveInteractionForce = this.computeRepulsiveInteractionForce(me, you);
//		
//		Vector2D pedestrianInteractionForce = interactionPedestrian(me, you);//bodyForce.sum(slidingFrictionForce); //sum(repulsiveInteractionForce)
//	
//		return pedestrianInteractionForce;
//	}
		
	private Vector2D interactionPedestrian(IOperationalPedestrian me, IPedestrian you) {
		
		double sumOfRadii = me.getBodyRadius() + you.getBodyRadius();		
		Vector2D mePosition = me.getPosition();
		Vector2D youPosition = you.getPosition();
		double distance = mePosition.distance(youPosition);
		
		Vector2D force = GeometryFactory.createVector(0, 0);
		
		if (distance <= sumOfRadii) {
			
			Vector2D normalizedDirection = mePosition.subtract(youPosition).getNormalized();
			force = normalizedDirection.multiply(physical_interaction_k * (sumOfRadii - distance));
		}
				
		return force;
	}

//	private Vector2D computeBodyForce(IOperationalPedestrian me, IPedestrian you) {
//		
//		double sumOfRadii = me.getBodyRadius() + you.getBodyRadius();		
//		Vector2D mePosition = me.getPosition();
//		Vector2D youPosition = you.getPosition();
//		double distance = mePosition.distance(youPosition);
//		
//		Vector2D bodyForce = null;
//		
//		if (distance > sumOfRadii) {
//			
//			bodyForce = GeometryFactory.createVector(0, 0);
//		}
//		else {
//
//			Vector2D normalizedDirection = (mePosition.subtract(youPosition)).multiply(1 / (distance + koesterEpsilon));			
//			bodyForce = normalizedDirection.multiply(physical_interaction_k * (sumOfRadii - distance));
//		}
//				
//		return bodyForce;
//	}

//	private Vector2D computeSlidingFrictionForce(IOperationalPedestrian me, IPedestrian you) {
//		
//		double sumOfRadii = me.getBodyRadius() + you.getBodyRadius();				
//		Vector2D mePosition = me.getPosition();		
//		Vector2D youPosition = you.getPosition();		
//		double distance = mePosition.distance(youPosition);
//		
//		Vector2D slidingFrictionForce = null;
//		
//		if (distance > sumOfRadii) {
//			
//			slidingFrictionForce = GeometryFactory.createVector(0, 0);
//		}
//		else {
//		
//			Vector2D normalizedDirection = (mePosition.subtract(youPosition)).multiply(1 / (distance + koesterEpsilon));
//			Vector2D tangentialDirection = GeometryFactory.createVector(-normalizedDirection.getComponents()[1], normalizedDirection.getComponents()[0]);
//			
//			Vector2D meVelocity = me.getVelocity();			
//			Vector2D youVelocity = you.getVelocity();			
//			double tangentialVelocityDifference = youVelocity.subtract(meVelocity).dot(tangentialDirection);
//			
//			slidingFrictionForce = tangentialDirection.multiply(physical_interaction_kappa * (sumOfRadii - distance) 
//																* tangentialVelocityDifference);
//		}
//		
//		return slidingFrictionForce;
//	}
	
//	private Vector2D computeRepulsiveInteractionForce(IOperationalPedestrian me, IPedestrian you) {
//		
//		double massFactor = 1.0;
//		
////		if(me.getMotoricTask() == Motoric.Walking && you.getMotoricTask() == Motoric.Standing) {
////				
////			massFactor = massFactor * 1.25;
////		}
//				
//		double sumOfRadii = me.getBodyRadius() + you.getBodyRadius();		
//		Vector2D mePosition = me.getPosition();
//		Vector2D youPosition = you.getPosition();
//		double distance = mePosition.distance(youPosition);
//		Vector2D normalizedDirection = (mePosition.subtract(youPosition)).multiply(1 / (distance + koesterEpsilon));
//		
//		Vector2D repulsiveInteractionForce = normalizedDirection.multiply(mass_behaviour_A * FastMath.exp(-1.0 *((distance - sumOfRadii)
//				/ (massFactor * mass_behaviour_B))));
//		
//		return repulsiveInteractionForce;
//	}
	
//	private Vector2D computeObstacleInteractionForce(IOperationalPedestrian pedestrian, Segment2D obstaclePart) {
//		
////		Vector2D bodyForce = this.computeBodyForce(pedestrian, obstaclePart);		
////		Vector2D slidingFrictionForce = this.computeSlidingFrictionForce(pedestrian, obstaclePart);		
////		Vector2D repulsiveInteractionForce = this.computeRepulsiveInteractionForce(pedestrian, obstaclePart);
//		 
//		Vector2D obstacleInteractionForce = interactionObstacle(pedestrian, obstaclePart);//bodyForce.sum(repulsiveInteractionForce).subtract(slidingFrictionForce);
//		
//		return obstacleInteractionForce;
//	}
	
	private Vector2D interactionObstacle(IOperationalPedestrian pedestrian, Segment2D obstaclePart) {
		
		double pedestrianRadius = pedestrian.getBodyRadius();		
		Vector2D pedestrianPosition = pedestrian.getPosition();
		Vector2D inBetweenVector = obstaclePart.vectorBetween(pedestrianPosition).negate();
		double distance = inBetweenVector.getMagnitude();
	
		Vector2D force = GeometryFactory.createVector(0, 0);
		
		if (distance <= pedestrianRadius) {
					
			Vector2D normalizedDirection = inBetweenVector.getNormalized();		
			force = normalizedDirection.multiply(physical_interaction_k * (pedestrianRadius - distance));
		}
		
		return force;
	}	
	
//	private Vector2D computeBodyForce(IOperationalPedestrian pedestrian, Segment2D obstaclePart) {
//		
//		double pedestrianRadius = pedestrian.getBodyRadius();		
//		Vector2D pedestrianPosition = pedestrian.getPosition();
//		Vector2D inBetweenVector = obstaclePart.vectorBetween(pedestrianPosition).negate();
//		double distance = inBetweenVector.getMagnitude();
//	
//		Vector2D bodyForce = null;
//		
//		if (distance > pedestrianRadius) {
//			bodyForce = GeometryFactory.createVector(0, 0);
//		}
//		else {
//					
//			Vector2D normalizedDirection = inBetweenVector.multiply(1 / (distance + koesterEpsilon));		
//			bodyForce = normalizedDirection.multiply(physical_interaction_k * (pedestrianRadius - distance));
//		}
//		
//		return bodyForce;
//	}
	
//	private Vector2D computeSlidingFrictionForce(IOperationalPedestrian pedestrian, Segment2D obstaclePart) {
//		
//		double pedestrianRadius = pedestrian.getBodyRadius();			
//		Vector2D pedestrianPosition = pedestrian.getPosition();		
//		Vector2D inBetweenVector = obstaclePart.vectorBetween(pedestrianPosition).negate();
//		double distance = inBetweenVector.getMagnitude();
//		
//		Vector2D slidingFrictionForce = null;
//		
//		if (distance > pedestrianRadius) {
//			
//			slidingFrictionForce = GeometryFactory.createVector(0, 0);
//		}
//		else {
//		
//			Vector2D normalizedDirection = inBetweenVector.multiply(1 / (distance + koesterEpsilon));
//
//			Vector2D tangentialDirection = GeometryFactory.createVector(- normalizedDirection.getComponents()[1], normalizedDirection.getComponents()[0]);
//			Vector2D meVelocity = pedestrian.getVelocity();
//			double tangentialVelocityDifference = meVelocity.dot(tangentialDirection);
//			
//			slidingFrictionForce = tangentialDirection.multiply(physical_interaction_kappa * (pedestrianRadius - distance) * tangentialVelocityDifference);
//		}
//		
//		return slidingFrictionForce;
//	}
	
//	private Vector2D computeRepulsiveInteractionForce(IOperationalPedestrian pedestrian, Segment2D obstaclePart) {
//				
//		double pedestrianRadius = pedestrian.getBodyRadius();			
//		Vector2D pedestrianPosition = pedestrian.getPosition();		
//		Vector2D inBetweenVector = obstaclePart.vectorBetween(pedestrianPosition).negate();
//		double distance = inBetweenVector.getMagnitude();
//		
//		Vector2D normalizedDirection = inBetweenVector.multiply(1 / (distance + koesterEpsilon));
//	
//		Vector2D repulsiveInteractionForce = normalizedDirection.multiply(mass_behaviour_A * FastMath.exp(-1.0 *((distance - pedestrianRadius) / mass_behaviour_B)));		
//		
//		return repulsiveInteractionForce;
//	}

	@Override
	public void callAfterBehavior(SimulationState simulationState, Collection<IRichPedestrian> pedestrians) {
		
		// nothing to do, no overall pedestrian behavior
	}

	@Override
	public IPedestrianExtension onPedestrianGeneration(IRichPedestrian pedestrian) {
		return null;
	}

	@Override
	public void onPedestrianRemoval(IRichPedestrian pedestrian) {
		
	}
}
