package tum.cms.sim.momentum.model.operational.walking.csvPlackback;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.math3.util.FastMath;

import tum.cms.sim.momentum.data.agent.pedestrian.state.operational.WalkingState;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IOperationalPedestrian;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrian;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrianExtension;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.perceptional.PerceptionalModel;
import tum.cms.sim.momentum.utility.geometry.GeometryFactory;
import tum.cms.sim.momentum.utility.geometry.Vector2D;
import tum.cms.sim.momentum.utility.geometry.operation.GeometryAdditionals;


public class CsvPlaybackPedestrianExtensions implements IPedestrianExtension {
	
	private static double freeCode = 0.25;
	private static double groupCode = 0.4;
	private static double pedestrianCode = 0.75;
	private static double obstacleCode = 1.0;
	
	private static Vector2D zeroVector = GeometryFactory.createVector(0.0, 0.0);
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
	
	private ArrayList<Vector2D> headingsList = new ArrayList<Vector2D>();

	public ArrayList<Vector2D> getHeadingsList() {
		return headingsList;
	}
	
//	private List<Double> perceptionDistanceSpace = new ArrayList<Double>();
//	private List<Double> perceptionVelocityXSpace = new ArrayList<Double>();
//	private List<Double> perceptionVelocityYSpace = new ArrayList<Double>();
//	private List<Double> perceptionTypeSpace = new ArrayList<Double>();
//
//	private Double pedestrianWalkingGoalX = 0.0;
//	private Double pedestrianWalkingGoalY = 0.0;
//	private Double pedestrianVelocityX = 0.0;
//	private Double pedestrianVelocityY = 0.0;
//	private Double pedestrianVelocityXLast = 0.0;
//	private Double pedestrianVelocityYLast = 0.0;
//	private Double pedestrianVelocityXLastSec = 0.0;
//	private Double pedestrianVelocityYLastSec = 0.0;
	
	
	// Training data
	private List<CsvPlaybackPerceptionWriterItem> perceptItems = new ArrayList<>();

	public List<CsvPlaybackPerceptionWriterItem> getPerceptItems() {
		return perceptItems;
	}
	
//	private List<Double> distancesToPercepts = new ArrayList<Double>();
//	
//	public List<Double> getDistancesToPercepts() {
//		return distancesToPercepts;
//	}
//
//	private List<Double> anglesToPercepts = new ArrayList<Double>();
//	
//	public List<Double> getAnglesToPercepts() {
//		return anglesToPercepts;
//	}
//
//	private List<Double> typesOfPercepts = new ArrayList<Double>();
//	
//	public List<Double> getTypesOfPercepts() {
//		return typesOfPercepts;
//	}
//
//	private List<Double> velocityAngleDifferencesToPercepts = new ArrayList<Double>();
//	
//	public List<Double> getVelocityAngleDifferencesToPercepts() {
//		return velocityAngleDifferencesToPercepts;
//	}
//
//	private List<Double> velocityMagnitudesOfPercepts = new ArrayList<Double>();
//	
//	public List<Double> getVelocityMagnitudesOfPercepts() {
//		return velocityMagnitudesOfPercepts;
//	}

	private Double angleToGoal = 0.0;	
	
	public Double getAngleToGoal() {
		return angleToGoal;
	}
	
	private Double lastVelocityMagnitude = -1.0;
	
	public Double getLastVelocityMagnitude() {
		return lastVelocityMagnitude;
	}

	private Double lastVelocityAngleChange = FastMath.PI;
	
	public Double getLastVelocityAngleChange() {
		return lastVelocityAngleChange;
	}

	private Double lastLastVelocityMagnitude = 0.0;
	
	public Double getLastLastVelocityMagnitude() {
		return lastLastVelocityMagnitude;
	}

	private Double lastLastVelocityAngleChange = FastMath.PI; 
	
	public Double getLastLastVelocityAngleChange() {
		return lastLastVelocityAngleChange;
	}

	// Teaching data
	private Double velocityMagnitude = 0.0;
	
	public Double getVelocityMagnitude() {
		return velocityMagnitude;
	}
	
	private Double velocityAngleChange = FastMath.PI;
	
	public Double getVelocityAngleChange() {
		return velocityAngleChange;
	}


//	public List<Double> getPerceptionDistanceSpace() {
//		return perceptionDistanceSpace;
//	}
//
//	public List<Double> getPerceptionVelocityXSpace() {
//		return perceptionVelocityXSpace;
//	}
//
//	public List<Double> getPerceptionVelocityYSpace() {
//		return perceptionVelocityYSpace;
//	}
//
//	public List<Double> getPerceptionTypeSpace() {
//		return perceptionTypeSpace;
//	}
//
//	public Double getPedestrianWalkingGoalX() {
//		return pedestrianWalkingGoalX;
//	}
//
//	public Double getPedestrianWalkingGoalY() {
//		return pedestrianWalkingGoalY;
//	}
//
//	public Double getPedestrianVelocityX() {
//		return pedestrianVelocityX;
//	}
//
//	public Double getPedestrianVelocityY() {
//		return pedestrianVelocityY;
//	}
//
//	public Double getPedestrianVelocityXLast() {
//		return pedestrianVelocityXLast;
//	}
//
//	public Double getPedestrianVelocityYLast() {
//		return pedestrianVelocityYLast;
//	}
//	
//	public Double getPedestrianVelocityXLastSec() {
//		return pedestrianVelocityXLastSec;
//	}
//
//	public Double getPedestrianVelocityYLastSec() {
//		return pedestrianVelocityYLastSec;
//	}
	
	private double magScale = 1.0;
	/**
	 * Returns heading vector mean over the last numberForMean headings
	 * @param currentEstimatedHeading
	 * @return new heading (integrated over last numberForMean)
	 */
	public Vector2D updateHeadings(Vector2D currentEstimatedHeading, int numberForMean) {
		
		this.headingsList.add(currentEstimatedHeading);
		Vector2D integratedHeading = GeometryFactory.createVector(0.0, 0.0);
		
		if(this.headingsList.size() > numberForMean) {
			
			this.headingsList.remove(0);
		}
			
		for(int iter = 0; iter < this.headingsList.size(); iter++) {
			
			integratedHeading = integratedHeading.sum(headingsList.get(iter));
		}
		
		integratedHeading = integratedHeading.multiply(1.0 / this.headingsList.size()).getNormalized();
		
		return integratedHeading;
	}
	
	
	public void updatePerceptionSpace(IOperationalPedestrian pedestrian, PerceptionalModel perception, SimulationState simulationState) {

		Vector2D position = pedestrian.getPosition();
		Vector2D heading = pedestrian.getHeading();
		
		// this collection is of constant size, null if no obstacle was found
		List<Vector2D> obstaclePositions = perception.getPerceptedObstaclePositions(pedestrian, simulationState);
	
		// this collection is of constant size, null if no pedestrian was found
		List<IPedestrian> pedestrianPositions = perception.getPerceptedPedestrianPositions(pedestrian, simulationState);
	
		List<Vector2D> freePositions = perception.getPerceptedFreePositions(pedestrian, simulationState);

		double scaleDistance = 1.0/perception.getPerceptionDistance();
		perceptItems.clear();
		
		for(int iter = 0; iter < obstaclePositions.size(); iter++) {
			
			CsvPlaybackPerceptionWriterItem item = new CsvPlaybackPerceptionWriterItem();
			
			if(obstaclePositions.get(iter) != null) {
				
				Vector2D obstaclePosition = obstaclePositions.get(iter);
				
				double distance = position.distance(obstaclePosition) - pedestrian.getBodyRadius();
				
				item.setDistanceToPercept(distance < 0.0 ? 0.0 : distance * scaleDistance);
				item.setAngleToPercept(FastMath.PI + GeometryAdditionals.angleBetweenPlusMinus180(obstaclePosition.subtract(position), zeroVector, heading));
				//item.setAngleToPercept((FastMath.PI + GeometryAdditionals.angleBetweenPlusMinus180(obstaclePosition.subtract(position), zeroVector, heading))/(2.0*FastMath.PI));
				item.setVelocityMagnitudeOfPercept(0.0);
				item.setVelocityAngleDifferenceToPercept(FastMath.PI * 2.0);
				//item.setVelocityAngleDifferenceToPercept(1.0);
				item.setTypeOfPercept(obstacleCode);
			}
			else if(pedestrianPositions.get(iter) != null) {
				
				IPedestrian other = pedestrianPositions.get(iter);
				
				double distance = position.distance(other.getPosition())
						- (other.getBodyRadius() + pedestrian.getBodyRadius());
				
				item.setDistanceToPercept(distance < 0.0 ? 0.0 : distance * scaleDistance);
				item.setAngleToPercept(FastMath.PI + GeometryAdditionals.angleBetweenPlusMinus180(other.getPosition().subtract(position), zeroVector, heading));
				//item.setAngleToPercept((FastMath.PI + GeometryAdditionals.angleBetweenPlusMinus180(other.getPosition().subtract(position), zeroVector, heading))/(2.0*FastMath.PI));
				item.setVelocityMagnitudeOfPercept(other.getVelocity().getMagnitude() * magScale);
				item.setVelocityAngleDifferenceToPercept(FastMath.PI + GeometryAdditionals.angleBetweenPlusMinus180(other.getVelocity(), zeroVector, pedestrian.getVelocity()));
				//item.setVelocityAngleDifferenceToPercept((FastMath.PI + GeometryAdditionals.angleBetweenPlusMinus180(other.getVelocity(), zeroVector, pedestrian.getVelocity()))/(2.0*FastMath.PI));
				item.setTypeOfPercept(other.getGroupId() == pedestrian.getGroupId() ? groupCode : pedestrianCode);
			}
			else {
			
				Vector2D freePosition = freePositions.get(iter);
				
				double distance = perception.getPerceptionDistance() - pedestrian.getBodyRadius();
				item.setDistanceToPercept(distance < 0.0 ? 0.0 : distance * scaleDistance);
				item.setAngleToPercept(FastMath.PI + GeometryAdditionals.angleBetweenPlusMinus180(freePosition.subtract(position), zeroVector, heading));
				//item.setAngleToPercept((FastMath.PI + GeometryAdditionals.angleBetweenPlusMinus180(freePosition.subtract(position), zeroVector, heading))/(2.0*FastMath.PI));
				item.setVelocityMagnitudeOfPercept(0.0);
				item.setVelocityAngleDifferenceToPercept(FastMath.PI);
				//item.setVelocityAngleDifferenceToPercept(0.0)
				item.setTypeOfPercept(freeCode); 
			}
			
			this.perceptItems.add(item);
		}
		 
		this.perceptItems = this.perceptItems.stream()
				.sorted(CsvPlaybackPedestrianExtensions.Comperator)
				.collect(Collectors.toList());
	}

	public void updatePedestrianSpace(IOperationalPedestrian pedestrian) {
		
		if(pedestrian.getNextWalkingTarget() != null) {
			
			Vector2D towardsGoal = pedestrian.getNextWalkingTarget().subtract(pedestrian.getPosition());
			angleToGoal = FastMath.PI + GeometryAdditionals.angleBetweenPlusMinus180(towardsGoal.subtract(pedestrian.getPosition()), zeroVector,pedestrian.getHeading());
			//angleToGoal = (FastMath.PI + GeometryAdditionals.angleBetweenPlusMinus180(towardsGoal.subtract(pedestrian.getPosition()), zeroVector,pedestrian.getHeading()))/(2.0*FastMath.PI);
		}
		
		if(lastVelocityMagnitude == -1) {
			
			lastLastVelocityMagnitude = pedestrian.getVelocity().getMagnitude() * magScale;
		}
		else {
			
			lastLastVelocityMagnitude = lastVelocityMagnitude;
		}
		
		lastLastVelocityAngleChange = lastVelocityAngleChange;
		
		lastVelocityMagnitude = pedestrian.getVelocity().getMagnitude() * magScale;
		lastVelocityAngleChange = velocityAngleChange;
	}
	
	public void updatePedestrianTeach(IOperationalPedestrian pedestrian, WalkingState newWalkingState) {

		velocityMagnitude = newWalkingState.getWalkingVelocity().getMagnitude() * magScale;
		velocityAngleChange = FastMath.PI + GeometryAdditionals.angleBetweenPlusMinus180(newWalkingState.getWalkingVelocity(), zeroVector, pedestrian.getVelocity());
		//velocityAngleChange = (FastMath.PI + GeometryAdditionals.angleBetweenPlusMinus180(newWalkingState.getWalkingVelocity(), zeroVector, pedestrian.getVelocity()))/(2.0*FastMath.PI);
	}
	
	public class CsvPlaybackPerceptionWriterItem {
		
		private double distanceToPercept = 0.0;
		private double angleToPercept = 0.0;
		private double velocityMagnitudeOfPercept = 0.0;
		private double velocityAngleDifferenceToPercept = 0.0;
		private double typeOfPercept = 0.0;
		
		public double getDistanceToPercept() {
			return distanceToPercept;
		}
		public void setDistanceToPercept(double distanceToPercept) {
			this.distanceToPercept = distanceToPercept;
		}
		public double getAngleToPercept() {
			return angleToPercept;
		}
		public void setAngleToPercept(double angleToPercept) {
			this.angleToPercept = angleToPercept;
		}
		public double getVelocityMagnitudeOfPercept() {
			return velocityMagnitudeOfPercept;
		}
		public void setVelocityMagnitudeOfPercept(double velocityMagnitudeOfPercept) {
			this.velocityMagnitudeOfPercept = velocityMagnitudeOfPercept;
		}
		public double getVelocityAngleDifferenceToPercept() {
			return velocityAngleDifferenceToPercept;
		}
		public void setVelocityAngleDifferenceToPercept(double velocityAngleDifferenScesToPercept) {
			this.velocityAngleDifferenceToPercept = velocityAngleDifferenScesToPercept;
		}
		public double getTypeOfPercept() {
			return typeOfPercept;
		}
		public void setTypeOfPercept(double typeOfPercept) {
			this.typeOfPercept = typeOfPercept;
		}
	}
	
	private static final Comparator<CsvPlaybackPerceptionWriterItem> Comperator = new Comparator<CsvPlaybackPerceptionWriterItem>() {
		
		@Override
		public int compare(CsvPlaybackPerceptionWriterItem left, CsvPlaybackPerceptionWriterItem right) {
		
			return Double.compare(left.getAngleToPercept(), right.getAngleToPercept());
		}
	};
}
