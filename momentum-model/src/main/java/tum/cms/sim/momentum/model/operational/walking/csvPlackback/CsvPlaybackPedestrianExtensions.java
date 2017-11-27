package tum.cms.sim.momentum.model.operational.walking.csvPlackback;

import java.util.ArrayList;
import java.util.List;

import tum.cms.sim.momentum.data.agent.pedestrian.state.operational.WalkingState;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IOperationalPedestrian;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrian;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrianExtension;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.perceptional.PerceptionalModel;
import tum.cms.sim.momentum.utility.geometry.Vector2D;


public class CsvPlaybackPedestrianExtensions implements IPedestrianExtension {
	
	private static double freeCode = -0.25;
	private static double groupCode = 0.0;
	private static double pedestrianCode = 0.25;
	private static double obstacleCode = 0.5;
	
	private boolean firstDataSet = true;
	
	public boolean isFirstDataSet() {
		return firstDataSet;
	}

	public void setFirstDataSet(boolean firstDataSet) {
		this.firstDataSet = firstDataSet;
	}

	private double distancePerception = 0.0;
	
	public double getDistancePerception() {
		return distancePerception;
	}

	public void setDistancePerception(double distancePerception) {
		this.distancePerception = distancePerception;
	}
	
	private List<Double> perceptionDistanceSpace = new ArrayList<Double>();
	private List<Double> perceptionVelocityXSpace = new ArrayList<Double>();
	private List<Double> perceptionVelocityYSpace = new ArrayList<Double>();
	private List<Double> perceptionTypeSpace = new ArrayList<Double>();
	
	private Double pedestrianWalkingGoalX = 0.0;
	private Double pedestrianWalkingGoalY = 0.0;
	private Double pedestrianVelocityX = 0.0;
	private Double pedestrianVelocityY = 0.0;
	private Double pedestrianVelocityXLast = 0.0;
	private Double pedestrianVelocityYLast = 0.0;
	
	public List<Double> getPerceptionDistanceSpace() {
		return perceptionDistanceSpace;
	}

	public List<Double> getPerceptionVelocityXSpace() {
		return perceptionVelocityXSpace;
	}

	public List<Double> getPerceptionVelocityYSpace() {
		return perceptionVelocityYSpace;
	}

	public List<Double> getPerceptionTypeSpace() {
		return perceptionTypeSpace;
	}

	public Double getPedestrianWalkingGoalX() {
		return pedestrianWalkingGoalX;
	}

	public Double getPedestrianWalkingGoalY() {
		return pedestrianWalkingGoalY;
	}

	public Double getPedestrianVelocityX() {
		return pedestrianVelocityX;
	}

	public Double getPedestrianVelocityY() {
		return pedestrianVelocityY;
	}

	public Double getPedestrianVelocityXLast() {
		return pedestrianVelocityXLast;
	}

	public Double getPedestrianVelocityYLast() {
		return pedestrianVelocityYLast;
	}

	public void updatePerceptionSpace(IOperationalPedestrian pedestrian,PerceptionalModel perception, SimulationState simulationState) {
		
		perceptionDistanceSpace.clear();
		perceptionVelocityXSpace.clear();
		perceptionVelocityYSpace.clear();
		perceptionTypeSpace.clear();
		
		Vector2D pedestrianPosition = pedestrian.getPosition();
		
		// this collection is of constant size, null if no obstacle was found
		List<Vector2D> obstaclePositions = perception.getPerceptedObstaclePositions(pedestrian, simulationState);
	
		// this collection is of constant size, null if no pedestrian was found
		List<IPedestrian> pedestrianPositions = perception.getPerceptedPedestrianPositions(pedestrian, simulationState);
		
		// gives the walked distance between to time steps thus it is already in distance/timeStepDuration
		// e.g. 5cm (/0.05sec), now we scale it to seconds = 5*20cm/1s = 1m/1s
		// time scale
		double scaleTime = 1.0/simulationState.getTimeStepDuration();
		double scaleDistance = 1.0/perception.getPerceptionDistance();
				
		for(int iter = 0; iter < obstaclePositions.size(); iter++) {
			
			if(obstaclePositions.get(iter) != null) {
				
				perceptionDistanceSpace.add((pedestrianPosition.distance(obstaclePositions.get(iter)) 
						- pedestrian.getBodyRadius()) * scaleDistance);
				perceptionVelocityXSpace.add(-pedestrian.getVelocity().getXComponent() * scaleTime);
				perceptionVelocityYSpace.add(-pedestrian.getVelocity().getXComponent() * scaleTime);
				perceptionTypeSpace.add(obstacleCode); 
			}
			else if(pedestrianPositions.get(iter) != null) {
				
				IPedestrian other = pedestrianPositions.get(iter);
				perceptionDistanceSpace.add((pedestrianPosition.distance(pedestrianPositions.get(iter).getPosition()) 
						- pedestrian.getBodyRadius()) * scaleDistance);
				perceptionVelocityXSpace.add(other.getVelocity().getXComponent()  * scaleTime); //- pedestrian.getVelocity().getXComponent()) * scaleTime); 
				perceptionVelocityYSpace.add(other.getVelocity().getYComponent()  * scaleTime); //- pedestrian.getVelocity().getYComponent()) * scaleTime);
				perceptionTypeSpace.add(other.getGroupId() == pedestrian.getGroupId() ? groupCode : pedestrianCode);
			}
			else {
			
				perceptionDistanceSpace.add(perception.getPerceptionDistance() * scaleDistance);
				perceptionVelocityXSpace.add(0.0);
				perceptionVelocityYSpace.add(0.0);
				perceptionTypeSpace.add(freeCode); 
			}
		}
	}

	public void updatePedestrianSpace(IOperationalPedestrian pedestrian, WalkingState newWalkingStat, SimulationState simulationState) {
		// gives the walked distance between to time steps thus it is already in distance/timeStepDuration
		// e.g. 5cm (/0.05sec), now we scale it to seconds = 5*20cm/1s = 1m/1s
		// time scale
		double scaleTime = 1.0/simulationState.getTimeStepDuration();
		if(pedestrian.getNextWalkingTarget() != null) {
			
			Vector2D towardsGoal = pedestrian.getNextWalkingTarget().subtract(pedestrian.getPosition()).getNormalized();
			pedestrianWalkingGoalX = towardsGoal.getXComponent();
			pedestrianWalkingGoalY = towardsGoal.getYComponent();
		}
		
		pedestrianVelocityX = newWalkingStat.getWalkingVelocity().getXComponent() * scaleTime;
		pedestrianVelocityY = newWalkingStat.getWalkingVelocity().getYComponent() * scaleTime;
		pedestrianVelocityXLast = pedestrian.getVelocity().getXComponent() * scaleTime;
		pedestrianVelocityYLast = pedestrian.getVelocity().getYComponent() * scaleTime;
	}
}
