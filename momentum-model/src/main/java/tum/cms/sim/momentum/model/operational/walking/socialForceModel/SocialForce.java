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

package tum.cms.sim.momentum.model.operational.walking.socialForceModel;

import java.util.Collection;

import tum.cms.sim.momentum.data.agent.pedestrian.types.IOperationalPedestrian;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrian;
import tum.cms.sim.momentum.data.layout.obstacle.Obstacle;
import tum.cms.sim.momentum.model.operational.walking.WalkingModel;
import tum.cms.sim.momentum.utility.generic.PropertyBackPack;
import tum.cms.sim.momentum.utility.geometry.GeometryFactory;
import tum.cms.sim.momentum.utility.geometry.Segment2D;
import tum.cms.sim.momentum.utility.geometry.Vector2D;

public class SocialForce {
	
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
	private double physical_interaction_kappa = Double.NEGATIVE_INFINITY;	// [kg / s^2]	
	private double physical_interaction_k = Double.NEGATIVE_INFINITY;	 // [kg / (m * s)]
	
	/**
	 * Constant to prevent division by zero and therefore the corruption of the algorithm.
	 */
	public double koesterEpsilon = 10e-6;
	
	/**
	 * Herding like behavior. Also know as panic degree in helbing's papers.
	 */
	public double herding = 0.0;
	
	private WalkingModel model;
	
	public SocialForce(WalkingModel model) {
		
		this.model = model;
		
		PropertyBackPack properties = model.getPropertyBackPack();
		
		relaxation_time = properties.getDoubleProperty("relaxation_time");
		
		physical_interaction_kappa = properties.getDoubleProperty("physical_interaction_kappa");
		physical_interaction_k = properties.getDoubleProperty("physical_interaction_k");	
		
		mass_behaviour_A = properties.getDoubleProperty("mass_behaviour_A");
		mass_behaviour_B = properties.getDoubleProperty("mass_behaviour_B");
				
		herding = properties.getDoubleProperty("panic_degree") != null ? 
				properties.getDoubleProperty("panic_degree") :
					0.0;
	}
	
	public Vector2D computeNewAcceleration(IOperationalPedestrian pedestrian,
			Collection<IPedestrian> otherPedestrians,
			Collection<Obstacle> obstacles) {
		
		SocialForcePedestrianExtension ext = (SocialForcePedestrianExtension) pedestrian.getExtensionState(model);
		
		Vector2D currentVelocity = pedestrian.getVelocity();

		double desiredVelocity = pedestrian.getDesiredVelocity();			
		
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

		Vector2D individualDirection = this.computeIndividualDirection(pedestrian, otherPedestrians);
		Vector2D selfDrivingForce = individualDirection.multiply(desiredVelocity).subtract(currentVelocity).multiply(1.0/relaxation_time);
		Vector2D newAcceleration = selfDrivingForce.sum(sumOfObstacleInteractionForces).sum(sumOfPedestrianInteractionForces);
		
		if(ext != null) {
			
			ext.setPedestrianInteractionForce(sumOfPedestrianInteractionForces);
			ext.setObstacleInteractionForce(sumOfObstacleInteractionForces);
			ext.setIndividualDirection(individualDirection);
			ext.setAcceleration(newAcceleration);
			ext.setSelfDrivingForce(selfDrivingForce);
		}

		return newAcceleration;
	}
	
	public Vector2D computePedestrianInteractionForce(IOperationalPedestrian me, IPedestrian you) {
		
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
			Vector2D tangentialDirection = GeometryFactory.createVector(-normalizedDirection.getComponents()[1], normalizedDirection.getComponents()[0]);
			
			Vector2D meVelocity = me.getVelocity();			
			Vector2D youVelocity = you.getVelocity();			
			double tangentialVelocityDifference = youVelocity.subtract(meVelocity).dot(tangentialDirection);
			
			slidingFrictionForce = tangentialDirection.multiply(physical_interaction_kappa * (sumOfRadii - distance) 
																* tangentialVelocityDifference);
		}
		
		return slidingFrictionForce;
	}
	
	private Vector2D computeRepulsiveInteractionForce(IOperationalPedestrian me, IPedestrian you) {
		
		double massFactor = 1.0;
		
		//if(me.getMotoricTask() == Motoric.Walking && you.getMotoricTask() == Motoric.Standing) {
		//		
		//	massFactor = massFactor * 0.25;
		//}
				
		double sumOfRadii = me.getBodyRadius() + you.getBodyRadius();		
		Vector2D mePosition = me.getPosition();
		Vector2D youPosition = you.getPosition();
		double distance = mePosition.distance(youPosition);
		Vector2D normalizedDirection = (mePosition.subtract(youPosition)).multiply(1 / (distance + koesterEpsilon));
		
		Vector2D repulsiveInteractionForce = normalizedDirection.multiply(mass_behaviour_A * Math.exp(-1.0 *((distance - sumOfRadii)
				/ (massFactor * mass_behaviour_B))));
		
		return repulsiveInteractionForce;
	}
	
	public Vector2D computeObstacleInteractionForce(IOperationalPedestrian pedestrian, Segment2D obstaclePart) {
		
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

			Vector2D tangentialDirection = GeometryFactory.createVector(- normalizedDirection.getComponents()[1], normalizedDirection.getComponents()[0]);
			Vector2D meVelocity = pedestrian.getVelocity();
			double tangentialVelocityDifference = meVelocity.dot(tangentialDirection);
			
			slidingFrictionForce = tangentialDirection.multiply(physical_interaction_kappa * (pedestrianRadius - distance) * tangentialVelocityDifference);
		}
		
		return slidingFrictionForce;
	}
	
	private Vector2D computeRepulsiveInteractionForce(IOperationalPedestrian pedestrian, Segment2D obstaclePart) {
				
		double pedestrianRadius = pedestrian.getBodyRadius();			
		Vector2D pedestrianPosition = pedestrian.getPosition();		
		Vector2D inBetweenVector = obstaclePart.vectorBetween(pedestrianPosition).negate();
		double distance = inBetweenVector.getMagnitude();
		
		Vector2D normalizedDirection = inBetweenVector.multiply(1 / (distance + koesterEpsilon));
	
		Vector2D repulsiveInteractionForce = normalizedDirection.multiply(mass_behaviour_A * Math.exp(-1.0 *((distance - pedestrianRadius) / mass_behaviour_B)));		
		
		return repulsiveInteractionForce;
	}

	private Vector2D computeIndividualDirection(IOperationalPedestrian pedestrian, Collection<IPedestrian> otherPedestrians) {

		
		Vector2D averageDirection = GeometryFactory.createVector(0, 0);
		otherPedestrians.forEach(other -> averageDirection.sum(other.getHeading()));

		Vector2D finalDirection = pedestrian.getHeading().getNormalized()
				.multiply(1 - herding)
				.sum(averageDirection.multiply(herding))
				.getNormalized();
		
		return finalDirection;
	}

}
