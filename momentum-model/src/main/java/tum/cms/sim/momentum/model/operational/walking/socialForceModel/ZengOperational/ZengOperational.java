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

package tum.cms.sim.momentum.model.operational.walking.socialForceModel.ZengOperational;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import tum.cms.sim.momentum.data.agent.car.CarManager;
import tum.cms.sim.momentum.data.agent.car.types.IRichCar;
import tum.cms.sim.momentum.data.agent.pedestrian.state.operational.WalkingState;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IOperationalPedestrian;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrian;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrianExtension;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;
import tum.cms.sim.momentum.data.layout.area.TaggedArea;
import tum.cms.sim.momentum.data.layout.obstacle.Obstacle;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.operational.walking.WalkingModel;
import tum.cms.sim.momentum.model.operational.walking.socialForceModel.SocialForce;
import tum.cms.sim.momentum.utility.geometry.GeometryFactory;
import tum.cms.sim.momentum.utility.geometry.Rectangle2D;
import tum.cms.sim.momentum.utility.geometry.Segment2D;
import tum.cms.sim.momentum.utility.geometry.Vector2D;

public class ZengOperational extends WalkingModel {

	private SocialForce socialForce;
	private CarManager carManager = null;
	private ZengModelParameters zengModelParameters = null;

	public CarManager getCarManager() {
		return carManager;
	}
	public void setCarManager(CarManager carManager) {
		this.carManager = carManager;
	}

	//private boolean verboseMode = true;


	private boolean fallbackPedestrianInteractionHelbingKoester = false;

	@Override
	public IPedestrianExtension onPedestrianGeneration(IRichPedestrian pedestrian) {
		ZengPedestrianExtension newExtension = new ZengPedestrianExtension();

		return newExtension;
	}

	@Override
	public void onPedestrianRemoval(IRichPedestrian pedestrian) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void callBeforeBehavior(SimulationState simulationState, Collection<IRichPedestrian> pedestrians) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void callAfterBehavior(SimulationState simulationState, Collection<IRichPedestrian> pedestrians) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void callPedestrianBehavior(IOperationalPedestrian pedestrian, SimulationState simulationState) {

		Vector2D acceleration = computeSharedSpaceAcceleration(pedestrian, simulationState);
		
		Vector2D deltaVelocity = acceleration.multiply(simulationState.getTimeStepDuration());
		Vector2D velocity = pedestrian.getVelocity().sum(deltaVelocity);

		if(velocity.getMagnitude() > pedestrian.getMaximalVelocity() ) {
		
			velocity = velocity.getNormalized()
					.multiply(pedestrian.getMaximalVelocity());
		}
		
		Vector2D deltaPosition = velocity.multiply(simulationState.getTimeStepDuration());
		Vector2D position = pedestrian.getPosition().sum(deltaPosition);
		
		Vector2D heading = this.computeHeading(pedestrian, pedestrian.getNextWalkingTarget());
		WalkingState novelState = new WalkingState(position, velocity, heading);
		
		pedestrian.setWalkingState(novelState);

        ZengPedestrianExtension ext = (ZengPedestrianExtension) pedestrian.getExtensionState(this);
        if(ext != null) {
            ext.setAcceleration(acceleration);
            ext.setIndividualDirection(heading);
        }
	}

	@Override
	public void callPreProcessing(SimulationState simulationState) {

		socialForce = new SocialForce(this);
		zengModelParameters = new ZengModelParameters(properties);
		this.fallbackPedestrianInteractionHelbingKoester = properties.getBooleanProperty("pedestrian_interaction_helbing_koester");
	}

	private Vector2D computeHeading(IOperationalPedestrian me, Vector2D target) {
		
		return target.subtract(me.getPosition()).getNormalized();
	}
	
	@Override
	public void callPostProcessing(SimulationState simulationState) {
		
	}
	
	private Vector2D computeSharedSpaceAcceleration(IOperationalPedestrian pedestrian, SimulationState simulationState)
	{
        ZengPedestrianExtension ext = (ZengPedestrianExtension) pedestrian.getExtensionState(this);

		Vector2D drivingForce = this.computeDrivingForce(pedestrian);
		Vector2D repulsiveForceConflictingPedestrians = this.computeRepulsiveConflictingPedestrians(pedestrian, simulationState);
		Vector2D repulsiveForceConflictingVehicle = this.computeRepulsiveForceConflictingVehicle(pedestrian);
		Vector2D forceCrosswalkBoundary = this.computeForceCrosswalkBoundary(pedestrian);
		Vector2D forceObstacle = this.computeObstacleInteractionForce(pedestrian);

        if(ext != null) {
            ext.setSelfDrivingForce(drivingForce);
            ext.setPedestrianInteractionForce(repulsiveForceConflictingPedestrians);
            ext.setCarInteractionForce(repulsiveForceConflictingVehicle);
            ext.setCrosswalkInteractionForce(forceCrosswalkBoundary);
            ext.setObstacleInteractionForce(forceObstacle);
        }

		return drivingForce.sum(repulsiveForceConflictingPedestrians)
				.sum(repulsiveForceConflictingVehicle)
				.sum(forceCrosswalkBoundary)
				.sum(forceObstacle);
	}
	
	private Vector2D computeDrivingForce(IOperationalPedestrian pedestrian)
	{
		// driving force at time
		Vector2D individualDirection = pedestrian.getHeading().getNormalized();
		double desiredVelocity = pedestrian.getDesiredVelocity();
		Vector2D currentVelocity = pedestrian.getVelocity();

		return individualDirection.multiply(desiredVelocity).subtract(currentVelocity).multiply(1.0/ zengModelParameters.getRelaxationTime());
	}
	
	private Vector2D computeRepulsiveConflictingPedestrians(IOperationalPedestrian pedestrian, SimulationState simulationState)
	{
		// repulsive force from conflicting pedestrian

		Collection<IPedestrian> otherPedestriansInVisualRange = perception.getPerceptedPedestrians(pedestrian, simulationState);

		Vector2D repulsiveForceConflictingPedestrians = GeometryFactory.createVector(0, 0);
		if(!fallbackPedestrianInteractionHelbingKoester)
		{
			repulsiveForceConflictingPedestrians = ZengAdditionalComputations.computeRepulsiveForceConflictingPedestrians(pedestrian,
					otherPedestriansInVisualRange, simulationState.getTimeStepDuration(),
					zengModelParameters.getConflictingPedestrianInteractionStrength(), zengModelParameters.getConflictingPedestrianInteractionRangeRelativeDistance(),
					zengModelParameters.getConflictingPedestrianInteractionRangeRelativeTime(), zengModelParameters.getConflictingPedestrianStrengthAngularEffect());
		}
		else {
			Vector2D pedestrianInteractionForce;
			for(IPedestrian other : this.perception.getAllPedestrians(pedestrian)) {
				pedestrianInteractionForce = this.socialForce.computePedestrianInteractionForce(pedestrian, other);
				repulsiveForceConflictingPedestrians = repulsiveForceConflictingPedestrians.sum(pedestrianInteractionForce);				
			}
		}
		
		return repulsiveForceConflictingPedestrians;
	}

	
	//private Vector2D computeAttractiveForceLeadingPedestrians()
	//{
	//	// attractive force from leading pedestrians
	//	return GeometryFactory.createVector(0, 0);
	//}

	private Vector2D computeRepulsiveForceConflictingVehicle(IOperationalPedestrian pedestrian)
	{
		// repulsive force from conflicting vehicle

		Collection<IRichCar> allCars = carManager.getAllCars();
		List<IRichCar> carsInVisualRange =  allCars.stream()
				.filter(currentCar -> this.perception.isVisible(pedestrian, currentCar.getRectangle().getVertices()))
				.collect(Collectors.toList());

		Vector2D force = GeometryFactory.createVector(0, 0);
		for(IRichCar car : carsInVisualRange) {
			Rectangle2D carRectangle = car.getRectangle();

			Vector2D closestPoint = carRectangle.getPointClosestToVector(pedestrian.getPosition());
			Vector2D carToPedestrianVector = pedestrian.getPosition().subtract(closestPoint);
			Vector2D pedestrianToCarVector = carToPedestrianVector.multiply(-1);

			if ( pedestrian.getVelocity().dot(pedestrianToCarVector) > 0) {

				double multiplier = zengModelParameters.getInteractionStrengthRepulsiveForceFromConflictingVehicle() *
						Math.exp(-zengModelParameters.getCarRepulsiveInteractionRange() *
								pedestrian.getPosition().subtract(closestPoint).getMagnitude());

				Vector2D forceFromCurrentVehicle = carToPedestrianVector.getNormalized().multiply(multiplier);
				force = force.sum(forceFromCurrentVehicle);
			}

		}

		return force;
	}
	
	private Vector2D computeForceCrosswalkBoundary(IOperationalPedestrian pedestrian)
	{
		// repulsive force or attractive force from the crosswalk boundary
        List<TaggedArea> crosswalkAreas = this.scenarioManager.getTaggedAreas(TaggedArea.Type.Crosswalk);

        TaggedArea nextCrosswalk = ZengAdditionalComputations.findCorrespondingCrosswalk(pedestrian, crosswalkAreas);
		Vector2D nearestCrosswalkBoundaryPoint = ZengAdditionalComputations.findNearestCorsswalkBoundaryPoint(pedestrian, nextCrosswalk, zengModelParameters.getComputationalPrecision());

		if(nearestCrosswalkBoundaryPoint == null) {
			return GeometryFactory.createVector(0.0, 0.0);
		}

		double distancePedestrianToCrosswalkPoint = pedestrian.getPosition().subtract(nearestCrosswalkBoundaryPoint).getMagnitude();
		double multiplier;
		Vector2D forceNormal;

		if(nextCrosswalk.getGeometry().contains(pedestrian.getPosition())) {
			// pedestrian inside the crosswalk
			multiplier = zengModelParameters.getCrosswalkRepulsiveInteractionStrength() *
					Math.exp(-zengModelParameters.getCrosswalkRepulsiveInteractionRange() * distancePedestrianToCrosswalkPoint);
			forceNormal = pedestrian.getPosition().subtract(nearestCrosswalkBoundaryPoint);

		} else {
			// pedestrian outside the crosswalk
			multiplier = zengModelParameters.getCrosswalkAttractiveInteractionStrength() *
					Math.exp(-zengModelParameters.getCrosswalkAttractiveInteractionRange() * distancePedestrianToCrosswalkPoint);

			forceNormal = nearestCrosswalkBoundaryPoint.subtract(pedestrian.getPosition());
		}

		return forceNormal.multiply(multiplier);
	}

	private Vector2D computeObstacleInteractionForce(IOperationalPedestrian pedestrian) {
		List<Obstacle> obstacles = this.scenarioManager.getObstacles()
				.stream()
				.filter(obstacle -> obstacle.getGeometry().distanceBetween(pedestrian.getPosition()) < 5.0)
				.collect(Collectors.toList());

		Vector2D sumOfObstacleInteractionForces = GeometryFactory.createVector(0, 0);
		Vector2D obstacleInteractionForce = null;

		for(Obstacle obstacle : obstacles) {

			for(Segment2D part : obstacle.getObstacleParts()) {

				obstacleInteractionForce = this.socialForce.computeObstacleInteractionForce(pedestrian, part);
				sumOfObstacleInteractionForces = sumOfObstacleInteractionForces.sum(obstacleInteractionForce);
			}
		}

		return sumOfObstacleInteractionForces;
	}
	
}
