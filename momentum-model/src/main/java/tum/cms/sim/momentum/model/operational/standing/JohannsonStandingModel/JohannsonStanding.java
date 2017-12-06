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

package tum.cms.sim.momentum.model.operational.standing.JohannsonStandingModel;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.math3.util.FastMath;

import tum.cms.sim.momentum.data.agent.pedestrian.state.operational.StandingState;
import tum.cms.sim.momentum.data.agent.pedestrian.state.tactical.TacticalState.Motoric;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IOperationalPedestrian;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrian;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrianExtension;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;
import tum.cms.sim.momentum.data.layout.obstacle.Obstacle;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.operational.standing.StandingModel;
import tum.cms.sim.momentum.utility.geometry.GeometryFactory;
import tum.cms.sim.momentum.utility.geometry.Segment2D;
import tum.cms.sim.momentum.utility.geometry.Vector2D;

public class JohannsonStanding extends StandingModel {

	/**
	 * Relaxation time [s] (necessary for acceleration).
	 */
	private double relaxation_time;

	/**
	 * Parameters for level of mass behaviour.
	 */
	private double mass_behaviour_A; // e.g. = 1.0;
	private double mass_behaviour_B;

	/**
	 * Large constants for body force and sliding friction force.
	 */
	private double physical_interaction_kappa = Double.NEGATIVE_INFINITY; // [kg / s^2]
	private double physical_interaction_k = Double.NEGATIVE_INFINITY; // [kg / (m*s)]
	
	/**
	 * Constant to prevent division by zero and therefore the corruption of the
	 * algorithm.
	 */
	public double koesterEpsilon = 10e-6;
	
	private int waitingCase;
	/*
	 * f�r die verschiedenen Ans�tze von Johannson.
	 * 0= Prefered Velocity; 1= Prefered Position;2=Adapted Prefered
	 * Position
	 */
	private double massWaitingPoint = 0.0;

	public void callPreProcessing(SimulationState simulationState) {

		relaxation_time = this.properties.getDoubleProperty("relaxation_time");
		physical_interaction_kappa = this.properties.getDoubleProperty("physical_interaction_kappa");
		physical_interaction_k = this.properties.getDoubleProperty("physical_interaction_k");
		mass_behaviour_A = this.properties.getDoubleProperty("mass_behaviour_A");
		mass_behaviour_B = this.properties.getDoubleProperty("mass_behaviour_B");
		waitingCase = this.properties.getIntegerProperty("waiting_case"); 
		massWaitingPoint =this.properties.getDoubleProperty("massWaitingPoint");
	}

	public void callPostProcessing(SimulationState simulationState) {

	}

	public void callBeforeBehavior(SimulationState simulationState, Collection<IRichPedestrian> pedestrians) {
		
		pedestrians.stream().forEach(pedestrian -> {
			
			if(pedestrian.getMotoricTask() == null || pedestrian.getMotoricTask() != Motoric.Standing) {
				
				JohannsonPedestrianExtension extension = (JohannsonPedestrianExtension) pedestrian.getExtensionState(this);
				extension.setPositionWaitingPoint(null);
				extension.setVelocityWaitingPoint(null);
			}
		});
	}

	public void callPedestrianBehavior(IOperationalPedestrian pedestrian, SimulationState simulationState) {

		JohannsonPedestrianExtension extension = (JohannsonPedestrianExtension) pedestrian.getExtensionState(this);
		
		if(extension.getPositionWaitingPoint() == null) {
			extension.setPositionWaitingPoint(pedestrian.getNextWalkingTarget().copy());
			extension.setVelocityWaitingPoint(GeometryFactory.createVector(0, 0));
		}
		
		List<IPedestrian> otherPedestrians = perception.getAllPedestrians(pedestrian)
				.stream()
				.filter(ped -> ped.getPosition().distance(pedestrian.getPosition()) < 5.0)
				.collect(Collectors.toList());;//.getNearestPedestrians(pedestrian, 3.0);
	
		List<Obstacle> obstacles = this.scenarioManager.getObstacles()
				.stream()
				.filter(obstacle -> obstacle.getGeometry().distanceBetween(pedestrian.getPosition()) < 5.0)
				.collect(Collectors.toList());

		Vector2D acceleration = this.computeNewAcceleration(pedestrian,
				otherPedestrians,
				obstacles);
		
		Vector2D deltaVelocity = acceleration.multiply(simulationState.getTimeStepDuration());
		Vector2D velocity = pedestrian.getVelocity().sum(deltaVelocity);

		if (velocity.getMagnitude() > pedestrian.getMaximalVelocity()) {
			velocity = velocity.getNormalized().multiply(pedestrian.getMaximalVelocity());
		}

		Vector2D deltaPosition = velocity.multiply(simulationState.getTimeStepDuration());
		Vector2D position = pedestrian.getPosition().sum(deltaPosition);
		
		Vector2D heading;
		
		if (waitingCase == 2) {// Berechnung des verschobenen Wartestandpunktes fur die Case2
		
			Vector2D velocityWaitingPoint = extension.getVelocityWaitingPoint();
			Vector2D positionWaitingPoint = extension.getPositionWaitingPoint();
			
			Vector2D accelerationWaitingPoint = this.computeAccelerationWaitingPoint(pedestrian, velocityWaitingPoint);
			Vector2D deltaVelocityWaitingPoint = accelerationWaitingPoint
					.multiply(simulationState.getTimeStepDuration());
		
			Vector2D NewVelocityWaitingPoint = velocityWaitingPoint.sum(deltaVelocityWaitingPoint);
			Vector2D deltaPositionWaitingPoint = NewVelocityWaitingPoint
					.multiply(simulationState.getTimeStepDuration());
			Vector2D NewPositionWaitingPoint = positionWaitingPoint.sum(deltaPositionWaitingPoint);

			extension.setPositionWaitingPoint(NewPositionWaitingPoint);
			extension.setVelocityWaitingPoint(NewVelocityWaitingPoint);

			heading = this.computeHeading(pedestrian,extension.getPositionWaitingPoint());
		} 
		else {
			heading = pedestrian.getNextHeading();
		}	

		StandingState novelState = new StandingState(position, velocity, heading);
		pedestrian.setStandingState(novelState);
	}

	public IPedestrianExtension onPedestrianGeneration(IRichPedestrian pedestrian) {
		
		return new JohannsonPedestrianExtension();
	}

	public void onPedestrianRemoval(IRichPedestrian pedestrian) {
		// nothing to do
	}

	private Vector2D computeAccelerationWaitingPoint(IOperationalPedestrian pedestrian, Vector2D velocityWaitingPoint) {

		Vector2D currentVelocity = pedestrian.getVelocity();
		Vector2D individualDirection = this.computeIndividualDirection(pedestrian);
		Vector2D desiredVelocityVector = this.computeDesiredVelocityVector(pedestrian,
				individualDirection);
		Vector2D selfDrivingForce = (desiredVelocityVector.subtract(currentVelocity)).multiply(1.0 / relaxation_time);
	
		
		Vector2D acceleration = (selfDrivingForce
				.sum(velocityWaitingPoint.multiply((massWaitingPoint + 1) / relaxation_time)))
				.multiply(1 / massWaitingPoint)
				.getNegative(); 
		
		return acceleration;
	}

	private Vector2D computeHeading(IOperationalPedestrian me, Vector2D target) {
		
		return target.subtract(me.getPosition()).getNormalized();
	}

	private Vector2D computeNewAcceleration(IOperationalPedestrian pedestrian, Collection<IPedestrian> otherPedestrians,
			Collection<Obstacle> obstacles) {

		Vector2D currentVelocity = pedestrian.getVelocity();

		Vector2D individualDirection = this.computeIndividualDirection(pedestrian);
		Vector2D desiredVelocityVector = this.computeDesiredVelocityVector(pedestrian, 
				individualDirection);

		Vector2D sumOfPedestrianInteractionForces = GeometryFactory.createVector(0, 0);
		Vector2D pedestrianInteractionForce = null;
		
		for (IPedestrian other : otherPedestrians) {

			pedestrianInteractionForce = this.computePedestrianInteractionForce(pedestrian, other);
			sumOfPedestrianInteractionForces = sumOfPedestrianInteractionForces.sum(pedestrianInteractionForce);
		}

		Vector2D sumOfObstacleInteractionForces = GeometryFactory.createVector(0, 0);
		Vector2D obstacleInteractionForce = null;

		for (Obstacle obstacle : obstacles) {
			for (Segment2D part : obstacle.getObstacleParts()) {
				obstacleInteractionForce = this.computeObstacleInteractionForce(pedestrian, part);
				sumOfObstacleInteractionForces = sumOfObstacleInteractionForces.sum(obstacleInteractionForce);
			}
		}
		
		Vector2D selfDrivingForce = desiredVelocityVector.subtract(currentVelocity).multiply(1.0 / relaxation_time);
		Vector2D newAcceleration = selfDrivingForce.sum(sumOfObstacleInteractionForces)
				.sum(sumOfPedestrianInteractionForces);

		return newAcceleration;
	}
	
	//Berechnung der Geschwindigkeit f�r SelfDrivingForce
	private Vector2D computeDesiredVelocityVector(IOperationalPedestrian pedestrian, Vector2D individualDirection) { 
		
		JohannsonPedestrianExtension extension = (JohannsonPedestrianExtension) pedestrian.getExtensionState(this);
		Vector2D desiredVelocity_vector = null;
		
		switch (waitingCase) {
		case 0:
			desiredVelocity_vector = individualDirection.multiply(0);
			break;
		case 1:
		case 2:
			double radiusPreferedPosition;
																					
			Vector2D target;
			if (waitingCase == 1) {
				target = pedestrian.getNextWalkingTarget();
				radiusPreferedPosition  = 4 * pedestrian.getDesiredVelocity() * relaxation_time; 
			} else {
				target = extension.getPositionWaitingPoint();
				radiusPreferedPosition  = 4 * pedestrian.getDesiredVelocity() * relaxation_time*((massWaitingPoint +1)/massWaitingPoint);
			}
		
			double distance = target.distance(pedestrian.getPosition());

			if (distance <= radiusPreferedPosition) {
				desiredVelocity_vector = target.subtract(pedestrian.getPosition())
						.multiply(pedestrian.getDesiredVelocity() / radiusPreferedPosition);
			} else
				desiredVelocity_vector = target.subtract(pedestrian.getPosition())
						.multiply(pedestrian.getDesiredVelocity() / distance);
			break;
		}
		
		return desiredVelocity_vector;
	}

	private Vector2D computePedestrianInteractionForce(IOperationalPedestrian me, IPedestrian you) {

		Vector2D bodyForce = this.computeBodyForce(me, you);
		Vector2D slidingFrictionForce = this.computeSlidingFrictionForce(me, you);
		Vector2D repulsiveInteractionForce = this.computeRepulsiveInteractionForce(me, you);

		Vector2D pedestrianInteractionForce = bodyForce.sum(repulsiveInteractionForce).sum(slidingFrictionForce);

		return pedestrianInteractionForce;
	}

	private Vector2D computeBodyForce(IOperationalPedestrian me, IPedestrian you) {

		double sumOfRadii = me.getBodyRadius() + you.getBodyRadius();
		Vector2D mePosition = me.getPosition();
		Vector2D youPosition = you.getPosition();
		double distance = mePosition.distance(youPosition);

		Vector2D bodyForce = null;

		if (distance > sumOfRadii) {

			bodyForce = GeometryFactory.createVector(0, 0);
		} 
		else {

			Vector2D normalizedDirection = (mePosition.subtract(youPosition)).multiply(1 / (distance + koesterEpsilon));
			bodyForce = normalizedDirection.multiply(physical_interaction_k * (sumOfRadii - distance));
		}

		return bodyForce;
	}

	private Vector2D computeSlidingFrictionForce(IOperationalPedestrian me, IPedestrian you) {

		double sumOfRadii = me.getBodyRadius() + you.getBodyRadius();
		Vector2D mePosition = me.getPosition();
		Vector2D youPosition = you.getPosition();
		double distance = mePosition.distance(youPosition);

		Vector2D slidingFrictionForce = null;

		if (distance > sumOfRadii) {

			slidingFrictionForce = GeometryFactory.createVector(0, 0);
		} 
		else {

			Vector2D normalizedDirection = (mePosition.subtract(youPosition)).multiply(1 / (distance + koesterEpsilon));
			Vector2D tangentialDirection = GeometryFactory.createVector(-normalizedDirection.getComponents()[1],
					normalizedDirection.getComponents()[0]);

			Vector2D meVelocity = me.getVelocity();
			Vector2D youVelocity = you.getVelocity();
			double tangentialVelocityDifference = youVelocity.subtract(meVelocity).dot(tangentialDirection);

			slidingFrictionForce = tangentialDirection
					.multiply(physical_interaction_kappa * (sumOfRadii - distance) * tangentialVelocityDifference);
		}

		return slidingFrictionForce;
	}

	private Vector2D computeRepulsiveInteractionForce(IOperationalPedestrian me, IPedestrian you) {

		double massFactor = 1.0;
		double sumOfRadii = me.getBodyRadius() + you.getBodyRadius();
		Vector2D mePosition = me.getPosition();
		Vector2D youPosition = you.getPosition();
		double distance = mePosition.distance(youPosition);
		Vector2D normalizedDirection = (mePosition.subtract(youPosition)).multiply(1 / (distance + koesterEpsilon));

		Vector2D repulsiveInteractionForce = normalizedDirection.multiply(
				mass_behaviour_A * FastMath.exp(-1.0 * ((distance - sumOfRadii) / (massFactor * mass_behaviour_B))));

		return repulsiveInteractionForce;
	}

	private Vector2D computeObstacleInteractionForce(IOperationalPedestrian pedestrian, Segment2D obstaclePart) {

		Vector2D bodyForce = this.computeBodyForce(pedestrian, obstaclePart);
		Vector2D slidingFrictionForce = this.computeSlidingFrictionForce(pedestrian, obstaclePart);
		Vector2D repulsiveInteractionForce = this.computeRepulsiveInteractionForce(pedestrian, obstaclePart);

		Vector2D obstacleInteractionForce = bodyForce.sum(repulsiveInteractionForce).subtract(slidingFrictionForce);

		return obstacleInteractionForce;
	}

	private Vector2D computeBodyForce(IOperationalPedestrian pedestrian, Segment2D obstaclePart) {

		double pedestrianRadius = pedestrian.getBodyRadius();
		Vector2D pedestrianPosition = pedestrian.getPosition();
		Vector2D inBetweenVector = obstaclePart.vectorBetween(pedestrianPosition).negate();
		double distance = inBetweenVector.getMagnitude();

		Vector2D bodyForce = null;

		if (distance > pedestrianRadius) {
			
			bodyForce = GeometryFactory.createVector(0, 0);
		} 
		else {

			Vector2D normalizedDirection = inBetweenVector.multiply(1 / (distance + koesterEpsilon));
			bodyForce = normalizedDirection.multiply(physical_interaction_k * (pedestrianRadius - distance));
		}

		return bodyForce;
	}

	private Vector2D computeSlidingFrictionForce(IOperationalPedestrian pedestrian, Segment2D obstaclePart) {

		double pedestrianRadius = pedestrian.getBodyRadius();
		Vector2D pedestrianPosition = pedestrian.getPosition();
		Vector2D inBetweenVector = obstaclePart.vectorBetween(pedestrianPosition).negate();
		double distance = inBetweenVector.getMagnitude();

		Vector2D slidingFrictionForce = null;

		if (distance > pedestrianRadius) {

			slidingFrictionForce = GeometryFactory.createVector(0, 0);
		} 
		else {

			Vector2D normalizedDirection = inBetweenVector.multiply(1 / (distance + koesterEpsilon));

			Vector2D tangentialDirection = GeometryFactory.createVector(-normalizedDirection.getComponents()[1],
					normalizedDirection.getComponents()[0]);
			Vector2D meVelocity = pedestrian.getVelocity();
			double tangentialVelocityDifference = meVelocity.dot(tangentialDirection);

			slidingFrictionForce = tangentialDirection.multiply(
					physical_interaction_kappa * (pedestrianRadius - distance) * tangentialVelocityDifference);
		}

		return slidingFrictionForce;
	}

	private Vector2D computeRepulsiveInteractionForce(IOperationalPedestrian pedestrian, Segment2D obstaclePart) {

		double pedestrianRadius = pedestrian.getBodyRadius();
		Vector2D pedestrianPosition = pedestrian.getPosition();
		Vector2D inBetweenVector = obstaclePart.vectorBetween(pedestrianPosition).negate();
		double distance = inBetweenVector.getMagnitude();

		Vector2D normalizedDirection = inBetweenVector.multiply(1 / (distance + koesterEpsilon));

		Vector2D repulsiveInteractionForce = normalizedDirection
				.multiply(mass_behaviour_A * FastMath.exp(-1.0 * ((distance - pedestrianRadius) / mass_behaviour_B)));

		return repulsiveInteractionForce;
	}

	private Vector2D computeIndividualDirection(IOperationalPedestrian pedestrian) {

		return pedestrian.getHeading().getNormalized();
	}

	public void callAfterBehavior(SimulationState simulationState, Collection<IRichPedestrian> pedestrians) {
		// nothing to do, no overall pedestrian behavior
	}
}
