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

package tum.cms.sim.momentum.model.operational.walking.empiricallyGrounded_Bonneaud2014;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import tum.cms.sim.momentum.data.agent.pedestrian.state.operational.WalkingState;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IOperationalPedestrian;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrian;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrianExtansion;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;
import tum.cms.sim.momentum.data.layout.obstacle.Obstacle;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.operational.walking.WalkingModel;
import tum.cms.sim.momentum.utility.geometry.Segment2D;
import tum.cms.sim.momentum.utility.geometry.Vector2D;
import org.apache.commons.math3.util.FastMath;
//positive AngularVelocity = turn clockwise in Simulation (positive y-axis is pointing downwards)
//negative AngularVelocity = turn anticlockwise in Simulation (positive y-axis is pointing downwards)

public class BonneaudOperational extends WalkingModel {
	
	// distance between pedestrian and other pedestrian or obstacles shouldn't be Zero. If it's zero (or less) a ZeroVectorException is thrown
	// if activateException is true
	boolean activateException = true;
	boolean debugOutput = false;

	@Override
	public void callPreProcessing(SimulationState simulationState) {
			
		BonneaudConstant.STATIC_TARGET_ATTRACT_ASSURANCE_COEF = this.properties.getDoubleProperty("STATIC_TARGET_ATTRACT_ASSURANCE");
		BonneaudConstant.STATIC_TARGET_ATTRACT_COEF = this.properties.getDoubleProperty("STATIC_TARGET_ATTRACT");
		BonneaudConstant.STATIC_TARGET_DAMPING_COEF = this.properties.getDoubleProperty("STATIC_TARGET_DAMPING");
		BonneaudConstant.STATIC_TARGET_DISTANCE_COEF = this.properties.getDoubleProperty("STATIC_TARGET_DISTANCE");
		
		BonneaudConstant.STATIC_OBSTACLE_DISTANCE_COEF = this.properties.getDoubleProperty("STATIC_OBSTACLE_DISTANCE");
		BonneaudConstant.STATIC_OBSTACLE_REPULSION_COEF = this.properties.getDoubleProperty("STATIC_OBSTACLE_REPULSION");
		BonneaudConstant.STATIC_OBSTACLE_REPULSION_DECAY_COEF = this.properties.getDoubleProperty("STATIC_OBSTACLE_REPULSION_DECAY");
		
		BonneaudConstant.MOVING_OBSTACLE_DISTANCE_COEF = this.properties.getDoubleProperty("MOVING_OBSTACLE_DISTANCE");
		BonneaudConstant.MOVING_OBSTACLE_REPULSION_COEF = this.properties.getDoubleProperty("MOVING_OBSTACLE_REPULSION");
		BonneaudConstant.MOVING_OBSTACLE_HEADING_COEF = this.properties.getDoubleProperty("MOVING_OBSTACLE_HEADING");
		
		BonneaudConstant.MOVING_OBSTACLE_CHANGE_IN_DISTANCE_COEF = this.properties.getDoubleProperty("MOVING_OBSTACLE_CHANGE_DISTANCE");
		
		BonneaudConstant.TAU_SPEED_BEARING_COEF = this.properties.getDoubleProperty("TAU_SPEED_BEARING");
		BonneaudConstant.TAU_SPEED_DEACCEL_COEF = this.properties.getDoubleProperty("TAU_SPEED_DEACCEL");
		BonneaudConstant.TAU_SPEED_PREFERED_COEF = this.properties.getDoubleProperty("TAU_SPEED_PREFER");
		
		BonneaudConstant.MAX_OBSTACLE_SIZE = this.properties.getDoubleProperty("MAX_OBSTACLE_SIZE");
	}

	@Override
	public void callBeforeBehavior(SimulationState simulationState, Collection<IRichPedestrian> pedestrians) {
		// nothing to do
	}
	
	@Override
	public void callPostProcessing(SimulationState simulationState) { /* nothing to do */ }

	@Override
	public void callPedestrianBehavior(IOperationalPedestrian pedestrian, SimulationState simulationState) {
		
	//	Break Helper, faster than conditional breakpoints
		/* if(simulationState.getCurrentTime() >= 130.0 ) {//&& ( pedestrian.getId() == 13 )) {
		*
		*	int stopHere = 0;
		*}
		*/
		
		double currentTimeStepDuration = simulationState.getTimeStepDuration();
		BonneaudPedestrianExtension extension = (BonneaudPedestrianExtension)pedestrian.getExtensionState(this);
		
		// Model is implemented only for one target
		Vector2D target = pedestrian.getNextWalkingTarget();
		Collection<Obstacle> obstacles = this.scenarioManager.getObstacles();
		Collection<IPedestrian> otherPedestrians = perception.getAllPedestrians(pedestrian);
		
		double acceleration = 0;
		
		try {
			acceleration = calculateAcceleration(pedestrian, obstacles, otherPedestrians);
		} catch (ZeroVectorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Vector2D velocityVec = calculateVelocity(pedestrian, acceleration, currentTimeStepDuration);
		
		//not in Model: restrict velocity to maxVelocity
//		if(velocityVec.getMagnitude() >= pedestrian.getStaticState().getMaximalVelocity()) {
//			
//			if(debugOutput) { 
//				System.out.println("pedestrian: " +pedestrian.getId()+" would have a too high velocity at"+"\tx: "+pedestrian.getPosition().getXComponent()+"\tVelocity would be: "+velocityVec.getMagnitude() );
//			}
//			velocityVec = velocityVec.scale(pedestrian.getStaticState().getMaximalVelocity());
//		} 
				
		//positive AngularVelocity = turn clockwise 
		double angularAcceleration = 0;
		try {
			angularAcceleration = calculateAngularAcceleration(pedestrian, target, obstacles, otherPedestrians, currentTimeStepDuration);
		} catch (ZeroVectorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		//not in Model: restrict angular Acceleration to 1/2 rounds per timestep (if initial angularVelocity is 0)
		//therefore the pedestrian could not be angular accelerated more than changing the heading direction from looking in front to looking back
		angularAcceleration = FastMath.signum(angularAcceleration) 
							* FastMath.min(FastMath.abs(angularAcceleration),FastMath.PI/(currentTimeStepDuration*currentTimeStepDuration));
		
		if(debugOutput &&  FastMath.abs(angularAcceleration) == FastMath.PI/(currentTimeStepDuration*currentTimeStepDuration)) {
			System.out.println("pedestrian: " +pedestrian.getId() + 
					" would have a too high angularAcceleration at"+
					"\tx: "+pedestrian.getPosition().getXComponent());
		}

		double angularVelocity = calculateAngularVelocity(pedestrian, angularAcceleration, currentTimeStepDuration);
		
		//not in model
		//restrict angular velocity to 1/2 round per timestep (highest change in heading is 180� -> from looking in front to looking backwards
		angularVelocity = FastMath.signum(angularVelocity) 
				* FastMath.min(FastMath.abs(angularVelocity),FastMath.PI/(currentTimeStepDuration));
		if( debugOutput && FastMath.abs(angularVelocity) == FastMath.PI/(currentTimeStepDuration)) {
			System.out.println("pedestrian: " +pedestrian.getId() + 
					" would have a too high angularVelocity at"+
					"\tx: "+pedestrian.getPosition().getXComponent());
		}
		
		WalkingState newState = createNewOpState(pedestrian, velocityVec, angularVelocity, currentTimeStepDuration);
		extension.setAngularVelocity(angularVelocity);
		
		pedestrian.setWalkingState(newState);
		//pedestrian.setExtensionState(newExtension, this);
	}

	/**
	 * 
	 * @param pedestrian
	 * @param obstacles
	 * @param otherPedestrians
	 * @return
	 * @throws ZeroVectorException 
	 */
	private double calculateAcceleration(IOperationalPedestrian pedestrian,
			Collection<Obstacle> obstacles,
			Collection<IPedestrian> otherPedestrians) throws ZeroVectorException {
		
		Vector2D headingVec = pedestrian.getHeading();
		double velocity = 0.0;
		double sumAcceleration = 0.0;
		
		//get a List of all Obstacles and divide them into smaller obstacle parts with maximum size MAX_OBSTACLE_SIZE:		
		List<Segment2D> allSegments = obstacles.stream().
				flatMap(obst -> obst.getObstacleParts().stream()).
				flatMap(part ->part.getLineSegmentsSplittedEqually(BonneaudConstant.MAX_OBSTACLE_SIZE, null).stream()).
				collect(Collectors.toList());
		
//		List<Segment2D> segmentsInFront = new ArrayList<Segment2D>();
		// filter all the obstacles which lay behind, so only obstacles in Front are taken into account
//		for (Segment2D obstacle : allSegments) {
//			
//			Vector2D toObstacle = calculateVectorFromPedToObstacle(pedestrian, obstacle);
//			double headingError = toObstacle.getAngleBetween(headingVec);
//			if (FastMath.abs(headingError) <= FastMath.PI / 2) {
//				segmentsInFront.add(obstacle);
//			}
//		}		
		
		// the same is done with pedestrians (just consider the pedestrians in front
//		List<IImmutablePedestrian> pedsInFront = new ArrayList<IImmutablePedestrian>();
//		for (IImmutablePedestrian curPed : otherPedestrians) {
//			
//			Vector2D pedToPed = calculateVectorFromPedToOtherPed(pedestrian, curPed);
//			
//			//check if distance is ZeroVector -> Exception
//			if (activateException && pedToPed.getMagnitude() == 0.0) {
//				throw new ZeroVectorException("undefined operation for ZeroVector");
//			}
//			
//			if(FastMath.abs(pedToPed.getAngleBetween(headingVec)) <= FastMath.PI / 2) {
//				
//				pedsInFront.add(curPed);
//			}
//		}
		
		// not in Model: a normalizing coefficient is introduced, because if otherwise, the more pedestrians or obstacles there are,
		// the higher the acceleration would be, disregarding the distances and bearings to the pedestrians and obstacles,
		// because the first summing term of the respectively formula is just dependent on the pedestrian itself

		//double attractor = 0.0;
//		double currentVelocity = pedestrian.geetVelocity().getMagnitude();
//		double maximalVelocity = pedestrian.getStaticState().getMaximalVelocity();
		
		for(Segment2D obstaclePart : allSegments) {
				
			Vector2D toObstacle = calculateVectorFromPedToObstacle(pedestrian, obstaclePart);
			
			if(activateException && toObstacle.getMagnitude() == 0.0) {
				throw new ZeroVectorException("Undefined Operation for Zero Vector");
				
			}
			double headingError = toObstacle.getAngleBetween(headingVec);
					
			//attractor = BonneaudConstant.TAU_SPEED_PREFERED_COEF * (maximalVelocity - currentVelocity);
			//deaccelartion = -1.0 * - BonneaudConstant.TAU_SPEED_DEACCEL_COEF 
			
			sumAcceleration += - BonneaudConstant.TAU_SPEED_DEACCEL_COEF 
						* (FastMath.pow(velocity + 0.01, 2) / (2 * toObstacle.getMagnitude()))
						* FastMath.exp(-BonneaudConstant.TAU_SPEED_BEARING_COEF * FastMath.abs(headingError));
		} //end obstacles
				
		Vector2D toOtherPed = null;
		
		for (IPedestrian otherPed : otherPedestrians) {
			
			toOtherPed = calculateVectorFromPedToOtherPed(pedestrian,otherPed);
			//toOtherPed is not a ZeroVector. This was checked above while filtering the pedestrians in front.
		
			double headingError = toOtherPed.getAngleBetween(headingVec);
			
			sumAcceleration += - BonneaudConstant.TAU_SPEED_DEACCEL_COEF
						* (FastMath.pow(velocity + 0.01, 2) / (2 * toOtherPed.getMagnitude()))
						* FastMath.exp(-BonneaudConstant.TAU_SPEED_BEARING_COEF * FastMath.abs(headingError));
			} //end otherPedestrian
		
		return sumAcceleration / (allSegments.size() + otherPedestrians.size());
	}

	/**
	 * 
	 * @param pedestrian
	 * @param acceleration
	 * @param currentTimeStep
	 * @return
	 */
	private Vector2D calculateVelocity(IOperationalPedestrian pedestrian, double acceleration, double currentTimeStep) {
		
		double newVelocityMagnitude = acceleration * currentTimeStep + pedestrian.getVelocity().getMagnitude();
		
		//not in Model: restrict pedestrian to forward movements. Backwards movements are only possible
		//if pedestrian turns backwards and then moves with a positive velocity
		if (newVelocityMagnitude < 0.0) {
			newVelocityMagnitude = 0.0;
			if(debugOutput) {
				System.out.println("pedestrian: " +pedestrian.getId() + 
						" would have a negative Velocity at"+
						"\tx: "+pedestrian.getPosition().getXComponent());
			}
		}
		// pedestrian moves in the heading direction (pedestrian is turned later when creating the new OperationalState)
		Vector2D newVelocity = pedestrian.getHeading().getNormalized().scale(newVelocityMagnitude);
		
		return newVelocity;
	}

	/**
	 * 
	 * @param pedestrian
	 * @param target
	 * @param obstacles
	 * @return Acceleration in (rad/s^2)
	 * @throws ZeroVectorException 
	 */
	private double calculateAngularAcceleration(IOperationalPedestrian pedestrian,
			Vector2D target, 
			Collection<Obstacle> obstacles,
			Collection<IPedestrian> otherPedestrians, double timeStep) throws ZeroVectorException {
		
		double dummy = 0.0;
		double dummy2 = 0.0;
		double dummy3 = 0.0;
		double angularAcc = 0.0;
		
		//Angular Acceleration for target
		angularAcc += angularAccForStationaryTarget(target, pedestrian);
		
		if(debugOutput) {
			dummy = angularAcc;
		}
		
//		moving target is not implemented yet
//		angularAcc += AngularAccForMovingTarget(pedestrian);
		
		//normal, stationary obstacles like walls
		angularAcc += angularAccForStationaryObstacle(pedestrian, obstacles);
		
		if(debugOutput) {
			dummy2 = angularAcc - dummy;
		}

		// Moving Obstacles (except for pedestrians -> see underneath) are not implemented yet
		//angularAcc += angularAccForMovingObstacle(pedestrian, obstacles);
		
		//Angular Acceleration for avoiding Pedestrians 
		 angularAcc += angularAccForAvoidPedestrians(pedestrian, otherPedestrians, timeStep);
		
		if(debugOutput) {
			dummy3 = angularAcc - dummy - dummy2;
			System.out.println("AngularAcceleration distribution: target: "+dummy+"\tobstacle: "+dummy2+"\tped: "+dummy3);
		}
		
		return angularAcc;
	}

	/**
	 * 
	 * @param pedestrian
	 * @param angularAcceleration
	 * @param currentTimeStep
	 * @return AngularVelocity in rad/s
	 */
	private double calculateAngularVelocity(IOperationalPedestrian pedestrian, double angularAcceleration, double currentTimeStep) {
		
		BonneaudPedestrianExtension tes = ((BonneaudPedestrianExtension) pedestrian.getExtensionState(this));
		
		// assumption: constant angularAcceleration during the timeStep
		double currentAngularVelocity = tes.getAngularVelocity() + angularAcceleration * currentTimeStep;
	
		return currentAngularVelocity;
	}

	private double angularAccForStationaryTarget(Vector2D target, IOperationalPedestrian pedestrian) throws ZeroVectorException {
		
		Vector2D headingVec = pedestrian.getHeading();
		
		Vector2D toTarget = target.subtract(pedestrian
				.getPosition());
		
		// contemplate the perimeter of the pedestrian: adjust the toTarget
		// vector: subtract the radius of the body
		double radius = pedestrian.getBodyRadius();
		
		if (toTarget.getMagnitude() <= radius) {
			
			//set vector to ZeroVector (negative distances are not allowed)
			toTarget = toTarget.scale(0.0);
			
			//Zero distance is also not allowed, because if one wants to take the angle between ZeroVector and another vector
			//one gets undefined results
			if(activateException) {
				throw new ZeroVectorException("undefined operation for ZeroVector");
			}
		} else {
			
		toTarget = toTarget.subtract(toTarget.scale(radius));
		}

		double headingError = toTarget.getAngleBetween(headingVec); // in radians!
																
		double damping = BonneaudConstant.STATIC_TARGET_DAMPING_COEF
				* ((BonneaudPedestrianExtension) pedestrian.getExtensionState(this)).getAngularVelocity();
		
		return -damping
				- BonneaudConstant.STATIC_TARGET_ATTRACT_COEF * headingError
				* (FastMath.exp(-BonneaudConstant.STATIC_TARGET_DISTANCE_COEF * toTarget.getMagnitude()) 
						+ BonneaudConstant.STATIC_TARGET_ATTRACT_ASSURANCE_COEF);
	}

	private double angularAccForStationaryObstacle(IOperationalPedestrian pedestrian, Collection<Obstacle> obstacles) throws ZeroVectorException {
		
		double result = 0.0;
		
		Vector2D headingVec = pedestrian.getHeading();
		
		//get all the obstacles and divide them into smaller parts with maximum size MAX_OBSTACLE_SIZE and do 
		//the calculation with each of them
		for (Obstacle currentObstacle : obstacles) {
			
			for(Segment2D obstaclePartBig : currentObstacle.getObstacleParts()) {

				for(Segment2D obstaclePart : obstaclePartBig.getLineSegmentsSplittedEqually(BonneaudConstant.MAX_OBSTACLE_SIZE, null)) {

					Vector2D toObstacle = calculateVectorFromPedToObstacle(pedestrian, obstaclePart);
					
					//getAngleBetween not defined for ZeroVector
					if(activateException && toObstacle.getMagnitude() == 0.0) {
						throw new ZeroVectorException("undefined operation for ZeroVector");
					}
					double headingError = toObstacle.getAngleBetween(headingVec); // in radians

					//just consider obstacles which lay in front
					if (FastMath.abs(headingError) <= FastMath.PI / 2) {
				
						result += BonneaudConstant.STATIC_OBSTACLE_REPULSION_COEF
								// improvement: instead of multiplying with "headingError" one only considers the sign of it
								// otherwise it would have a convers effect
								*FastMath.signum(headingError)
								* FastMath.exp(-BonneaudConstant.STATIC_OBSTACLE_REPULSION_DECAY_COEF
										* FastMath.abs(headingError))
										* FastMath.exp(-BonneaudConstant.STATIC_OBSTACLE_DISTANCE_COEF
												* toObstacle.getMagnitude());
					} 
				} // end for all obstacleParts
			} 
		}
		return result;
	}


	private double angularAccForAvoidPedestrians(IOperationalPedestrian pedestrian,
			Collection<IPedestrian> otherPedestrians,
			double timeStep) throws ZeroVectorException {
		
		double result = 0.0;
		
		// headingVec used to determine if pedestrian has already passed the
		// otherPedestrian (angle between the heading of currentPedestrian 
		//and the vector to the other Pedestrian is bigger than pi/2 if the pedestrian is passed)
		//-> see later
		Vector2D headingVec = pedestrian.getHeading();
		
		for (IPedestrian otherPed : otherPedestrians) {
			
			Vector2D toPedPresent = calculateVectorFromPedToOtherPed(pedestrian, otherPed);
			
			//getAngleBetween not defined for ZeroVector
			if(activateException && toPedPresent.getMagnitude() == 0.0) {
				throw new ZeroVectorException("undefined operation for ZeroVector");
			}

			// other pedestrian only repulses current pedestrian if he isn't
			// passed by already
			
			double angleToPedPresent = toPedPresent.getAngleBetween(headingVec);
			
			if (FastMath.abs(angleToPedPresent) <= FastMath.PI / 2) {
				
				//meFuture is the approximated position (given the last known velocity of the pedestrian) at the next time step.
				//Used for calculating the angle between the future position of this pedestrian and all the others
				Vector2D meFuture = pedestrian.getVelocity().multiply(timeStep);
				
				// toPedFuture is the estimated vector to the otherPed, at the
				// next time step.
				// It's used to calculate the change of the relative position to
				// the current pedestrian
				Vector2D toPedFuture = toPedPresent.sum(otherPed
						.getVelocity().multiply(timeStep)) //sees last state of otherPed
						.subtract(meFuture); //considers the own movement, too
				
				//getAngleBetween not defined for ZeroVector
				if(activateException && toPedFuture.getMagnitude() == 0.0) {
					throw new ZeroVectorException("undefined operation for ZeroVector");
				}
				
				double angleToPedFuture = toPedFuture.getAngleBetween(headingVec);
				
				//saves the relative direction to the otherPedestrian
				//The correct direction of turning away from the pedestrian is determined by the relative angle between the 2 pedestrians
				double turnClockWiseAway = FastMath.signum(angleToPedPresent);
				
				double changeOfAngle = angleToPedFuture - angleToPedPresent;
				changeOfAngle *= turnClockWiseAway;		
				//if pedestrian should move away from the other pedestrian, than changeOfAngle should be always positive
				// then one can distinguish later between positive changeOfAngle ( should move away) 
				// and negative changeOfAngle (should move towards the other Pedestrian)
				
				double changeOfDistance = toPedFuture.getMagnitude() - toPedPresent.getMagnitude();
				//if Distance between the peds become bigger, than changeOfDistance is positive
				
				double changeAll;
				//Model improvement:
				if(changeOfAngle == 0.0 && changeOfDistance == 0.0 && pedestrian.getVelocity().getMagnitude() == 0.0) {
					//Both peds are not moving
					//then turn away from pedestrian
					changeAll = - 1/angleToPedPresent;
				} else {
					changeAll = changeOfAngle += BonneaudConstant.MOVING_OBSTACLE_CHANGE_IN_DISTANCE_COEF * changeOfDistance;
				}
				//if pedestrian moves towards other ped (collision danger), then changeAll is negative
				
				//one has to differentiate if the angle Between the 2 Peds is positive or negative
				//changeAll should be positive to turn clockwise and negative to change anticlockwise
				//if angleToPedPresent is negative, then the ped has to turn anticlockwise to avoid collision -> negative changeAll
				//otherwise clockwise -> positive changeAll
								
				changeAll *= ( - turnClockWiseAway);
				
				result += BonneaudConstant.MOVING_OBSTACLE_REPULSION_COEF
						* (FastMath.signum(changeAll)) //improvement of model
						* FastMath.exp(-BonneaudConstant.MOVING_OBSTACLE_HEADING_COEF * FastMath.abs(changeAll)
								- BonneaudConstant.MOVING_OBSTACLE_DISTANCE_COEF * toPedPresent.getMagnitude());
			} // end if (pedestrian is already passed)
		} // end for

		return result;
	}
	
//	private double angularAccForMovingObstacle(IPedestrian pedestrian, Collection<Obstacle> obstacles) {
//		// need model of moving obstacle
//		return 0.0;
//	}
	
	private WalkingState createNewOpState(IOperationalPedestrian pedestrian, 
			Vector2D velocityVec, 
			double angularVelocity, 
			double currentTimeStep) {
		
		//assumption linear angularVelocity during timeStep
		double turnAngle = angularVelocity * currentTimeStep;
		
		// at every time step, the agent is first turned and then he moves into
		// that new direction with the speed calculated from the Tau-Dot Method.
		Vector2D newVelocity = velocityVec.rotate(turnAngle);
		Vector2D newPosition = pedestrian.getPosition().sum(newVelocity.multiply(currentTimeStep));
		Vector2D newHeading;
		
		// not in Model: if the velocity is too small, one takes as heading the last heading of the pedestrian
		if (newVelocity.getMagnitude() <= 0.001) {
			
			newHeading = pedestrian.getHeading().rotate(turnAngle);
		}else {
			// otherwise one takes as heading the new Velocity direction
			newHeading = newVelocity.getNormalized();
		}
		
		WalkingState walkingState = new WalkingState(newPosition, newVelocity, newHeading);
		
		return walkingState;
	}

	private Vector2D calculateVectorFromPedToObstacle(IOperationalPedestrian pedestrian,
			Segment2D obstaclePart) {
		
		double radius = pedestrian.getBodyRadius();
		Vector2D toObstacle = obstaclePart.vectorBetween(pedestrian.getPosition()); 
		toObstacle = toObstacle.subtract(toObstacle.scale(radius));
		// contemplate the perimeter of the pedestrian:
		// adjust the
		// toObstacle vector: subtract the radius of the body
		

		// means to improve model workability: even if the distance between pedestrian and objects are
		//smaller than the radius respectively, this method returns a very small vector though. 
		// model should be tested without this trick, too.
//		if(toObstacle.getMagnitude() <= radius) {
//			//return very small vector
//			return toObstacle.scale(0.0000001);
//		} else {
//
//			toObstacle = toObstacle.subtract(toObstacle.scale(radius));
//		}
		return toObstacle;
	}
	
	private Vector2D calculateVectorFromPedToOtherPed(IOperationalPedestrian pedestrian, IPedestrian otherPed) {
		
		Vector2D toOtherPed = otherPed
				.getPosition()
				.subtract(pedestrian.getPosition());
		// consider the perimeter of the pedestrian: adjust the
		// toOtherPed vector: subtract the radius' of the bodies
		double radius1 = pedestrian.getBodyRadius();
		double radius2 = otherPed.getBodyRadius();

		// means to improve model workability: even if the distance between pedestrian and pedestrian are
		//smaller than the radius respectively, this method returns a very small vector though. 
		// model should be tested without this trick, too.
		if(toOtherPed.getMagnitude() <= radius1 + radius2) {
			//return very small vector
			return toOtherPed.scale(0.0000001);
		} else {

			toOtherPed = toOtherPed.subtract(toOtherPed.scale(radius1 + radius2));
		}
		return toOtherPed;
	}

	@Override
	public void callAfterBehavior(SimulationState simulationState, Collection<IRichPedestrian> pedestrians) {

		// nothing to do, no behavior or all pedestrians
	}
	
	@Override
	public IPedestrianExtansion onPedestrianGeneration(IRichPedestrian pedestrian) {
		
		return new BonneaudPedestrianExtension();
	}	
	
	@Override
	public void onPedestrianRemoval(IRichPedestrian pedestrian) {

		// nothing to do
	}
	
	private class ZeroVectorException extends Exception {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public ZeroVectorException(String text) {
			super(text);
		}
	}
}
