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
	
	private static int freeCode = 0;
	private static int groupCode = 1;
	private static int pedestrianCode = 2;
	private static int obstacleCode = 3;
	
	private boolean firstDataSet = true;
	
	public boolean isFirstDataSet() {
		return firstDataSet;
	}

	public void setFirstDataSet(boolean firstDataSet) {
		this.firstDataSet = firstDataSet;
	}

	private List<Double> perceptionDistanceSpace = new ArrayList<Double>();
	private List<Double> perceptionVelocityXSpace = new ArrayList<Double>();
	private List<Double> perceptionVelocityYSpace = new ArrayList<Double>();
	private List<Integer> perceptionTypeSpace = new ArrayList<Integer>();
	
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

	public List<Integer> getPerceptionTypeSpace() {
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

	public void updatePerceptionSpace(IOperationalPedestrian pedestrian, PerceptionalModel perception, SimulationState simulationState) {
		
		Vector2D pedestrianPosition = pedestrian.getPosition();
		
		// this collection is of constant size, null if no obstacle was found
		List<Vector2D> obstaclePositions = perception.getPerceptedObstaclePositions(pedestrian, simulationState);
	
		// this collection is of constant size, null if no pedestrian was found
		List<IPedestrian> pedestrianPositions = perception.getPerceptedPedestrianPositions(pedestrian, simulationState);
	
		for(int iter = 0; iter < obstaclePositions.size(); iter++) {
			
			if(obstaclePositions.get(iter) != null) {
				
				perceptionDistanceSpace.add(pedestrianPosition.distance(obstaclePositions.get(iter)) - pedestrian.getBodyRadius());
				perceptionVelocityXSpace.add(0.0);
				perceptionVelocityYSpace.add(0.0);
				perceptionTypeSpace.add(obstacleCode);
			}
			else if(pedestrianPositions.get(iter) != null) {
				
				IPedestrian other = pedestrianPositions.get(iter);
				perceptionDistanceSpace.add(pedestrianPosition.distance(pedestrianPositions.get(iter).getPosition()) 
						- (pedestrian.getBodyRadius() + other.getBodyRadius()));
				perceptionVelocityXSpace.add(other.getVelocity().getXComponent());
				perceptionVelocityYSpace.add(other.getVelocity().getYComponent());
				perceptionTypeSpace.add(other.getGroupId() == pedestrian.getGroupId() ? groupCode : pedestrianCode);
			}
			else {
			
				perceptionDistanceSpace.add(perception.getPerceptionDistance());
				perceptionVelocityXSpace.add(0.0);
				perceptionVelocityYSpace.add(0.0);
				perceptionTypeSpace.add(freeCode);
			}
		}
	}

	public void updatePedestrianSpace(IOperationalPedestrian pedestrian, WalkingState newWalkingStat) {
		
		if(pedestrian.getNextWalkingTarget() != null) {
			
			pedestrianWalkingGoalX = pedestrian.getNextWalkingTarget().getXComponent();
			pedestrianWalkingGoalY = pedestrian.getNextWalkingTarget().getXComponent();
		}
		
		pedestrianVelocityX = newWalkingStat.getWalkingVelocity().getXComponent();
		pedestrianVelocityY = newWalkingStat.getWalkingVelocity().getYComponent();
		pedestrianVelocityXLast = pedestrian.getVelocity().getXComponent();
		pedestrianVelocityYLast = pedestrian.getVelocity().getYComponent();
	}
}
