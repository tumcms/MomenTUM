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

package tum.cms.sim.momentum.model.operational.walking.groupBehaviour_Moussaid2010;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.math3.util.FastMath;

import tum.cms.sim.momentum.data.agent.pedestrian.state.operational.WalkingState;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IOperationalPedestrian;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrian;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrianExtansion;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;
import tum.cms.sim.momentum.data.layout.obstacle.Obstacle;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.operational.walking.WalkingModel;
import tum.cms.sim.momentum.utility.geometry.GeometryFactory;
import tum.cms.sim.momentum.utility.geometry.Segment2D;
import tum.cms.sim.momentum.utility.geometry.Vector2D;

public class MoussaidOperational extends WalkingModel {
	
	@Override
	public void callPreProcessing(SimulationState simulationState) {
		
		MoussaidConstant.attraction_effects_strength = this.properties.getDoubleProperty("attraction_effects_strength");
		MoussaidConstant.social_interaction_strength = this.properties.getDoubleProperty("social_interaction_strength");
		MoussaidConstant.repulsion_strength = this.properties.getDoubleProperty("repulsion_strength");
		
		MoussaidConstant.relaxation_time = this.properties.getDoubleProperty("relaxation_time");
		MoussaidConstant.physical_interaction_kappa = this.properties.getDoubleProperty("physical_interaction_kappa");
		MoussaidConstant.physical_interaction_k = this.properties.getDoubleProperty("physical_interaction_k");		
		MoussaidConstant.panic_degree = this.properties.getDoubleProperty("panic_degree");		
		MoussaidConstant.mass_behaviour_A = this.properties.getDoubleProperty("mass_behaviour_A");
		MoussaidConstant.mass_behaviour_B = this.properties.getDoubleProperty("mass_behaviour_B");
		MoussaidConstant.koesterEpsilon = this.properties.getDoubleProperty("koester_Epsilon");
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
		
		Vector2D heading = this.computeHeading(pedestrian, pedestrian.getNextWalkingTarget());
		Vector2D acceleration = this.computeNewAcceleration(pedestrian, 
				perception.getAllPedestrians(pedestrian), 
				this.scenarioManager.getObstacles(),
				simulationState);
		
		Vector2D deltaVelocity = acceleration.multiply(simulationState.getTimeStepDuration());
		Vector2D velocity = pedestrian.getVelocity().sum(deltaVelocity);

		if(velocity.getMagnitude() > pedestrian.getMaximalVelocity() ) {
		
			velocity = velocity.getNormalized()
					.multiply(pedestrian.getMaximalVelocity());
		}
		
		Vector2D deltaPosition = velocity.multiply(simulationState.getTimeStepDuration());
		Vector2D position = pedestrian.getPosition().sum(deltaPosition);

		WalkingState walkingState = new WalkingState(position, velocity, heading);
	
		pedestrian.setWalkingState(walkingState);
	}	
	
	@Override
	public IPedestrianExtansion onPedestrianGeneration(IRichPedestrian pedestrian) {
		
		MoussaidPedestrianExtension newExtension = new MoussaidPedestrianExtension();
		newExtension.setAttractionEffectsStrength(MoussaidConstant.attraction_effects_strength);
		newExtension.setRepulsionStrength(MoussaidConstant.repulsion_strength);
		newExtension.setSocialInteractionStrength(MoussaidConstant.social_interaction_strength);	
		
		newExtension.setRelaxationTime(MoussaidConstant.relaxation_time);	
		newExtension.setMassBehaviourConstants(new double[]{MoussaidConstant.mass_behaviour_A, MoussaidConstant.mass_behaviour_B});
		newExtension.setPanicDegree(MoussaidConstant.panic_degree);
		
		return newExtension;
	}
	
	@Override
	public void onPedestrianRemoval(IRichPedestrian pedestrian) {

		// nothing to do
	}

	//------------------------------------------------------------------------------------------------------------------
	
	private Vector2D computeNewAcceleration(IOperationalPedestrian pedestrian,
			Collection<IPedestrian> otherPedestrians,
			Collection<Obstacle> obstacles,
			SimulationState simulationState) {
		
		Vector2D currentVelocity = pedestrian.getVelocity();
		Vector2D gazingDirection = this.computeHeading(pedestrian, pedestrian.getNextWalkingTarget());
		((MoussaidPedestrianExtension)pedestrian.getExtensionState(this)).setGazingDirection(gazingDirection);
		
		double desiredVelocity = pedestrian.getDesiredVelocity();
		double relaxationTime = ((MoussaidPedestrianExtension)pedestrian.getExtensionState(this)).getRelaxationTime();
				
		Vector2D newGazingDirection = this.adjustGazingDirection(pedestrian, otherPedestrians);
		((MoussaidPedestrianExtension)pedestrian.getExtensionState(this)).setGazingDirection(newGazingDirection);
		
		//<Start Helbing part of algorithm
		Vector2D sumOfPedestrianInteractionForces = GeometryFactory.createVector(0, 0);
		Vector2D pedestrianInteractionForce = null;
		
		for(IPedestrian other : otherPedestrians) {
				
			pedestrianInteractionForce = this.computePedestrianInteractionForce(pedestrian, other);
			sumOfPedestrianInteractionForces = sumOfPedestrianInteractionForces.sum(pedestrianInteractionForce);			
		}
		
		Vector2D sumOfObstacleInteractionForces = GeometryFactory.createVector(0, 0);
		Vector2D obstacleInteractionForce = null;
		
		for(Obstacle obstacle : obstacles) {

			for(Segment2D part : obstacle.getObstacleParts()) {
			
				obstacleInteractionForce = this.computeObstacleInteractionForce(pedestrian, part);	
				sumOfObstacleInteractionForces = sumOfObstacleInteractionForces.sum(obstacleInteractionForce);			
			}
		}
		
		Vector2D individualDirection = this.computeIndividualDirection(pedestrian, simulationState);
		
		Vector2D selfDrivingForce = individualDirection.multiply(desiredVelocity).subtract(currentVelocity).multiply(1.0/relaxationTime);
		// End Helbing part of algorithm />
		
		Vector2D socialInteractionForce = this.computeSocialInteractionForce(pedestrian, otherPedestrians);
		Vector2D newAcceleration =  selfDrivingForce.sum(sumOfObstacleInteractionForces).sum(sumOfPedestrianInteractionForces)
													.sum(socialInteractionForce);
		
		return newAcceleration;
	}

	private Vector2D computeSocialInteractionForce(IOperationalPedestrian pedestrian, Collection<IPedestrian> otherPedestrians) {
		
		Vector2D socialInteractionForce = GeometryFactory.createVector(0, 0);
				
		if (this.getGroupMembers(pedestrian, otherPedestrians).size() > 0) {

			Vector2D visualForce = this.computeVisualForce(pedestrian);
			Vector2D attractionForce = this.computeAttractionForce(pedestrian, otherPedestrians);
			Vector2D repulsionForce = this.computeRepulsionForce(pedestrian, this.getGroupMembers(pedestrian, otherPedestrians));
		
			socialInteractionForce = visualForce.sum(attractionForce).sum(repulsionForce);
		}
		
		return socialInteractionForce;
	}
	
	private Vector2D computeVisualForce(IOperationalPedestrian pedestrian) {
		
		Vector2D visualForce = GeometryFactory.createVector(0, 0);
		double socialInteractionStrength = ((MoussaidPedestrianExtension)pedestrian.getExtensionState(this)).getSocialInteractionStrength();
		Vector2D currentHeading = pedestrian.getHeading();
		Vector2D currentVelocity = pedestrian.getVelocity();
		Vector2D gazingDirection = ((MoussaidPedestrianExtension)pedestrian.getExtensionState(this)).getGazingDirection();
		
		double angleBetweenHeadingAndGazingDirection = FastMath.acos(FastMath.abs(currentHeading.dot(gazingDirection)) / (currentHeading.getMagnitude() * gazingDirection.getMagnitude() + MoussaidConstant.koesterEpsilon));
		
		visualForce = currentVelocity.multiply(-1).multiply(socialInteractionStrength).multiply(angleBetweenHeadingAndGazingDirection);
		
		return visualForce;
	}
	
	private Vector2D computeAttractionForce(IOperationalPedestrian pedestrian, Collection<IPedestrian> otherPedestrians) {
		
		Vector2D attractionForce = GeometryFactory.createVector(0, 0);
		Collection<IPedestrian> groupMembers = this.getGroupMembers(pedestrian, otherPedestrians);
		Vector2D centerOfMass = this.computeGroupsCenterOfMass(groupMembers);
		Vector2D currentPosition = pedestrian.getPosition();
		double attractionEffectsStrength = ((MoussaidPedestrianExtension)pedestrian.getExtensionState(this)).getAttractionEffectsStrength();
		int groupMembersCount = this.getGroupMembers(pedestrian, otherPedestrians).size();
		
		Vector2D unitVectorToCenter = GeometryFactory.createVector(currentPosition, centerOfMass).getNormalized();
		
		double distanceThresholdValue = (groupMembersCount - 1) / 2;
		double distanceToCenter = currentPosition.distance(centerOfMass);
		
		/*
		 * In paper: q_A
		 * Value = 1 if the distance between pedestrian i and the group's center of mass exceeds a threshold value, 0 otherwise.
		 */
		int distanceExceeding = 0;
		
		if(distanceToCenter > distanceThresholdValue) {
			distanceExceeding = 1;
		}
		
		attractionForce = unitVectorToCenter.multiply(distanceExceeding).multiply(attractionEffectsStrength);
		
		return attractionForce;
	}
	
	private Vector2D computeGroupsCenterOfMass(Collection<IPedestrian> groupMembers) {
		
		Vector2D centerOfMass = null;
		double sum_x = 0;
		double sum_y = 0;
		
		for (IPedestrian currentPedestrian:groupMembers) {
			
			double[] currentsPosition = currentPedestrian.getPosition().getComponents();
			sum_x += currentsPosition[0];
			sum_y += currentsPosition[1];
		}
		
		double x_s = sum_x / groupMembers.size();
		double y_s = sum_y / groupMembers.size();
		
		centerOfMass = GeometryFactory.createVector(x_s, y_s);
		
		return centerOfMass;
	}
	
	private Collection<IPedestrian> getGroupMembers(IOperationalPedestrian pedestrian, Collection<IPedestrian> otherPedestrians) {
		
		Collection<IPedestrian> groupMembers = otherPedestrians.stream()
									.filter(member -> pedestrian.getGroupId() == member.getGroupId())
									.collect(Collectors.toList());
		
		return groupMembers;
	}
	
	private Vector2D adjustGazingDirection(IOperationalPedestrian pedestrian, Collection<IPedestrian> otherPedestrians) {
		
		Vector2D gazingDirection = ((MoussaidPedestrianExtension)pedestrian.getExtensionState(this)).getGazingDirection();
		Collection<IPedestrian> groupMembers = this.getGroupMembers(pedestrian, otherPedestrians);
		
		if (groupMembers.size() > 0) {
			
			Vector2D centerOfMass = this.computeGroupsCenterOfMass(groupMembers);
			
			Vector2D position = pedestrian.getPosition();
			Vector2D unitVectorToCenter = position.to(centerOfMass).getNormalized();
			
			double angle_gazingDirectionToCenter = FastMath.acos(FastMath.abs(gazingDirection.dot(unitVectorToCenter)) / (gazingDirection.getMagnitude() * unitVectorToCenter.getMagnitude() + MoussaidConstant.koesterEpsilon));
			double spinning = gazingDirection.getXComponent() * unitVectorToCenter.getYComponent() - gazingDirection.getYComponent() * unitVectorToCenter.getXComponent();
			double adjustmentAngle = 0;
			
			if (angle_gazingDirectionToCenter > (FastMath.PI / 2)) {
				
				if (spinning > 0) {

					adjustmentAngle = angle_gazingDirectionToCenter - FastMath.PI;
				}
				else {

					adjustmentAngle = (-1) * (angle_gazingDirectionToCenter - FastMath.PI);
				}
				
				double gazing_x = gazingDirection.getComponents()[0];
				double gazing_y = gazingDirection.getComponents()[1];
				
				gazing_x = FastMath.cos(adjustmentAngle) * gazing_x - FastMath.sin(adjustmentAngle) * gazing_y;
				gazing_y = FastMath.sin(adjustmentAngle) * gazing_x + FastMath.cos(adjustmentAngle) * gazing_y;
				
				gazingDirection.set(gazing_x, gazing_y);
				gazingDirection = gazingDirection.getNormalized();
			}
		}
		
		return gazingDirection;
	}
	
	private Vector2D computeRepulsionForce(IOperationalPedestrian pedestrian, Collection<IPedestrian> groupMembers) {
		
		double repulsionStrength = ((MoussaidPedestrianExtension)pedestrian.getExtensionState(this)).getRepulsionStrength();
		Vector2D repulsionForce = GeometryFactory.createVector(0, 0);
		Vector2D unitVectorToNeighbour = null;
		
		/*
		 * In paper: q_R
		 * Value = 1 if pedestrian i and k overlap each other, = 0 otherwise.
		 */
		int pedestriansOverlapping = 0;
		
		for (IPedestrian member : groupMembers) {
			
			if(pedestrian.getPosition().distance(member.getPosition())
					< (pedestrian.getBodyRadius() + member.getBodyRadius())) {
				pedestriansOverlapping = 1;
			}
			
			unitVectorToNeighbour = GeometryFactory.createVector(pedestrian.getPosition(),
																	member.getPosition()).getNormalized();
			
			repulsionForce.sum(unitVectorToNeighbour.multiply(pedestriansOverlapping).multiply(repulsionStrength));
		}
		
		return repulsionForce;
	}
	
	private Vector2D computeHeading(IOperationalPedestrian me, Vector2D target) {
		
		return target.subtract(me.getPosition()).getNormalized();
	}
	
	//-------------------------------------------------------------------------------------------------------------------------------------
	// <Start of Helbing methods.
	// The following methods have been copied from the class HelbingOperational. Moussaid used Helbing's basic acceleration algorithm and added his group effect terms.
	private Vector2D computeIndividualDirection(IOperationalPedestrian pedestrian, SimulationState simulationState) {
		
		Collection<Vector2D> neighboursDirections = this.perception.getPerceptedPedestrians(pedestrian, simulationState)
															.stream()
															.map(IPedestrian::getHeading)
															.collect(Collectors.toList());

		Vector2D averageDirection = GeometryFactory.createVector(0, 0);
		neighboursDirections.forEach(direction -> averageDirection.sum(direction));
		double panicDegree = ((MoussaidPedestrianExtension)pedestrian.getExtensionState(this)).getPanicDegree();
		
		Vector2D finalDirection = pedestrian.getHeading()
											.multiply(1 - panicDegree)
											.sum(averageDirection.multiply(panicDegree))
											.getNormalized();
		
		return finalDirection;
	}
	
	private Vector2D computeObstacleInteractionForce(IOperationalPedestrian pedestrian, Segment2D obstaclePart) {
		
		Vector2D bodyForce = this.computeBodyForce(pedestrian, obstaclePart);		
		Vector2D slidingFrictionForce = this.computeSlidingFrictionForce(pedestrian, obstaclePart);		
		Vector2D repulsiveInteractionForce = this.computeRepulsiveInteractionForce(pedestrian, obstaclePart);
		 
		Vector2D obstacleInteractionForce = bodyForce.sum(repulsiveInteractionForce).subtract(slidingFrictionForce);
		
		return obstacleInteractionForce;
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

			Vector2D normalizedDirection = (mePosition.subtract(youPosition)).multiply(1 / (distance + MoussaidConstant.koesterEpsilon));			
			bodyForce = normalizedDirection.multiply(MoussaidConstant.physical_interaction_k * (sumOfRadii - distance));
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
		
			Vector2D normalizedDirection = (mePosition.subtract(youPosition)).multiply(1 / (distance + MoussaidConstant.koesterEpsilon));
			Vector2D tangentialDirection = GeometryFactory.createVector(-normalizedDirection.getComponents()[1], normalizedDirection.getComponents()[0]);
			
			Vector2D meVelocity = me.getVelocity();			
			Vector2D youVelocity = you.getVelocity();			
			double tangentialVelocityDifference = youVelocity.subtract(meVelocity).dot(tangentialDirection);
			
			slidingFrictionForce = tangentialDirection.multiply(MoussaidConstant.physical_interaction_kappa * (sumOfRadii - distance) 
																* tangentialVelocityDifference);
		}
		
		return slidingFrictionForce;
	}
	
	private Vector2D computeRepulsiveInteractionForce(IOperationalPedestrian me, IPedestrian you) {
		
		double sumOfRadii = me.getBodyRadius() + you.getBodyRadius();		
		Vector2D mePosition = me.getPosition();
		Vector2D youPosition = you.getPosition();
		double distance = mePosition.distance(youPosition);
		Vector2D normalizedDirection = (mePosition.subtract(youPosition)).multiply(1 / (distance + MoussaidConstant.koesterEpsilon));
		
		double A = ((MoussaidPedestrianExtension)me.getExtensionState(this)).getMassBehaviourConstants()[0];
		double B = ((MoussaidPedestrianExtension)me.getExtensionState(this)).getMassBehaviourConstants()[1];
		
		Vector2D repulsiveInteractionForce = normalizedDirection.multiply(A * FastMath.exp((sumOfRadii - distance) / B));		
		
		return repulsiveInteractionForce;
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
					
			Vector2D normalizedDirection = inBetweenVector.multiply(1 / (distance + MoussaidConstant.koesterEpsilon));		
			bodyForce = normalizedDirection.multiply(MoussaidConstant.physical_interaction_k * (pedestrianRadius - distance));
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
		
			Vector2D normalizedDirection = inBetweenVector.multiply(1 / (distance + MoussaidConstant.koesterEpsilon));

			Vector2D tangentialDirection = GeometryFactory.createVector(- normalizedDirection.getComponents()[1], normalizedDirection.getComponents()[0]);
			Vector2D meVelocity = pedestrian.getVelocity();
			double tangentialVelocityDifference = meVelocity.dot(tangentialDirection);
			
			slidingFrictionForce = tangentialDirection.multiply(MoussaidConstant.physical_interaction_kappa * (pedestrianRadius - distance) * tangentialVelocityDifference);
		}
		
		return slidingFrictionForce;
	}
	
	private Vector2D computeRepulsiveInteractionForce(IOperationalPedestrian pedestrian, Segment2D obstaclePart) {
				
		double pedestrianRadius = pedestrian.getBodyRadius();			
		Vector2D pedestrianPosition = pedestrian.getPosition();		
		Vector2D inBetweenVector = obstaclePart.vectorBetween(pedestrianPosition).negate();
		double distance = inBetweenVector.getMagnitude();
		
		Vector2D normalizedDirection = inBetweenVector.multiply(1 / (distance + MoussaidConstant.koesterEpsilon));

		double A = ((MoussaidPedestrianExtension)pedestrian.getExtensionState(this)).getMassBehaviourConstants()[0];
		double B = ((MoussaidPedestrianExtension)pedestrian.getExtensionState(this)).getMassBehaviourConstants()[1];
		
		Vector2D repulsiveInteractionForce = normalizedDirection.multiply(A * FastMath.exp((pedestrianRadius - distance) / B));		
		
		return repulsiveInteractionForce;
	}
	// End of Helbing methods />
	//-------------------------------------------------------------------------------------------------------------------------------------
		
	@Override
	public void callAfterBehavior(SimulationState simulationState, Collection<IRichPedestrian> pedestrians) {
		
		// nothing to do, no overall pedestrian behavior
	}
}
