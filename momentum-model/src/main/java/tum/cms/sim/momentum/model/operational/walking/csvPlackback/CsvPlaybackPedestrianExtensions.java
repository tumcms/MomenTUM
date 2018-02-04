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
	private double maximalVelocityInfluence = 2.0; 
	private static Double xMinCut = null;
	private static Double xMaxCut = null;
	private static Double yMinCut = null;
	private static Double yMaxCut = null;
	
	public static double getxMinCut() {
		return xMinCut == null ? Double.MIN_VALUE : xMinCut;
	}

	public static void setxMinCut(Double xMinCut) {
		CsvPlaybackPedestrianExtensions.xMinCut = xMinCut;
	}

	public static double getxMaxCut() {
		return xMaxCut == null ? Double.MAX_VALUE : xMaxCut;
	}

	public static void setxMaxCut(Double xMaxCut) {
		CsvPlaybackPedestrianExtensions.xMaxCut = xMaxCut;
	}

	public static double getyMinCut() {
		return yMinCut == null ? Double.MIN_VALUE : yMinCut;
	}

	public static void setyMinCut(Double yMinCut) {
		CsvPlaybackPedestrianExtensions.yMinCut = yMinCut;
	}

	public static double getyMaxCut() {
		return yMaxCut == null ? Double.MAX_VALUE : yMaxCut;
	}

	public static void setyMaxCut(Double yMaxCut) {
		CsvPlaybackPedestrianExtensions.yMaxCut = yMaxCut;
	}

	private Vector2D currentPosition = null;
	
	public Vector2D getCurrentPosition() {
		return currentPosition;
	}

	public void setCurrentPosition(Vector2D currentPosition) {
		this.currentPosition = currentPosition;
	}

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

	// Training data
	private List<CsvPlaybackPerceptionWriterItem> perceptItems = new ArrayList<>();

	public List<CsvPlaybackPerceptionWriterItem> getPerceptItems() {
		return perceptItems;
	}
	
	private Double angleToGoal = 0.0;	
	
	public Double getAngleToGoal() {
		return angleToGoal;
	}
	
	private ArrayList<Double> lastVelocityMagnitudeCategories = new ArrayList<Double>();

	public ArrayList<Double> getLastVelocityMagnitudeCategories() {
		return lastVelocityMagnitudeCategories;
	}

	private ArrayList<Double> lastVelocityAngleCategories = new ArrayList<Double>();
	

	public ArrayList<Double> getLastVelocityAngleCategories() {
		return lastVelocityAngleCategories;
	}

	public void setLastVelocityAngleCategories(ArrayList<Double> lastVelocityAngleCategories) {
		this.lastVelocityAngleCategories = lastVelocityAngleCategories;
	}

	// Teaching data
	private Double velocityMagnitude = null;
	
	public Double getVelocityMagnitude() {
		return velocityMagnitude;
	}
	
	private Double velocityAngleChange = null;
	
	public Double getVelocityAngleChange() {
		return velocityAngleChange;
	}

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

		perceptItems.clear();
		
		for(int iter = 0; iter < obstaclePositions.size(); iter++) {
			
			CsvPlaybackPerceptionWriterItem item = new CsvPlaybackPerceptionWriterItem();
			
			if(obstaclePositions.get(iter) != null) {
				
				Vector2D obstaclePosition = obstaclePositions.get(iter);
				
				double distance = position.distance(obstaclePosition) - pedestrian.getBodyRadius();
				
				item.setDistanceToPercept(distance < 0.0 ? 0.0 : distance);
				item.setAngleToPercept(GeometryAdditionals.angleBetweenPlusMinus180(obstaclePosition.subtract(position), zeroVector, heading));
				item.setVelocityMagnitudeOfPercept(0.0);
				item.setVelocityAngleDifferenceToPercept(0.0);
				item.setTypeOfPercept(obstacleCode);
			}
			else if(pedestrianPositions.get(iter) != null) {
				
				IPedestrian other = pedestrianPositions.get(iter);
				
				double distance = position.distance(other.getPosition()) - (other.getBodyRadius() + pedestrian.getBodyRadius());
				
				item.setDistanceToPercept(distance < 0.0 ? 0.0 : distance);
				item.setAngleToPercept(GeometryAdditionals.angleBetweenPlusMinus180(other.getPosition().subtract(position), zeroVector, heading));
				item.setVelocityMagnitudeOfPercept(other.getVelocity().getMagnitude());
				item.setVelocityAngleDifferenceToPercept(GeometryAdditionals.angleBetweenPlusMinus180(other.getVelocity(), zeroVector, pedestrian.getVelocity()));
				item.setTypeOfPercept(other.getGroupId() == pedestrian.getGroupId() ? groupCode : pedestrianCode);
			}
			else {
			
				Vector2D freePosition = freePositions.get(iter);
				
				double distance = perception.getPerceptionDistance() - pedestrian.getBodyRadius();
				item.setDistanceToPercept(distance < 0.0 ? 0.0 : distance);
				item.setAngleToPercept(GeometryAdditionals.angleBetweenPlusMinus180(freePosition.subtract(position), zeroVector, heading));
				item.setVelocityMagnitudeOfPercept(0.0);
				item.setVelocityAngleDifferenceToPercept(0.0);
				item.setTypeOfPercept(freeCode); 
			}
			
			this.perceptItems.add(item);
		}
		 
		this.perceptItems = this.perceptItems.stream()
				.sorted(CsvPlaybackPedestrianExtensions.Comperator)
				.collect(Collectors.toList());
	}

	public void updatePedestrianSpace(IOperationalPedestrian pedestrian,
			SimulationState state,
			int velocityClasses,
			int angleClasses,
			int numberOfLastCategories) {
		
		if(pedestrian.getNextWalkingTarget() != null) {
			
			Vector2D towardsGoal = pedestrian.getNextWalkingTarget().subtract(pedestrian.getPosition());
			angleToGoal = GeometryAdditionals.angleBetweenPlusMinus180(towardsGoal.subtract(pedestrian.getPosition()), zeroVector,pedestrian.getHeading());
			//angleToGoal = (FastMath.PI + GeometryAdditionals.angleBetweenPlusMinus180(towardsGoal.subtract(pedestrian.getPosition()), zeroVector,pedestrian.getHeading()))/(2.0*FastMath.PI);
		}
		
		if(velocityMagnitude != null) {
			
			lastVelocityMagnitudeCategories.add(this.getVelocityForClassification(velocityMagnitude.intValue(), velocityClasses, pedestrian, state));
			lastVelocityAngleCategories.add(this.getAngleForClassification(velocityAngleChange.intValue(), angleClasses));
		}
		
		// in case we do not have a lot of data data we crop the list
		while(lastVelocityMagnitudeCategories.size() > numberOfLastCategories) {
			
			lastVelocityMagnitudeCategories.remove(0);
			lastVelocityAngleCategories.remove(0);
		}
	}

	public void initializeLastTeach(IOperationalPedestrian pedestrian,
			SimulationState state,
			double velocityMagnitudeChangeNoCategory,
			double velocityAngleChangeNoCategory,
			int velocityClasses,
			int angleClasses,
			int numberOfLastCategories) {

		Double velocityAngleChange = (double)this.getClassForAngle(velocityAngleChangeNoCategory, angleClasses);
		Double velocityMagnitude = (double)this.getClassForVelocity(velocityMagnitudeChangeNoCategory, velocityClasses, pedestrian, state);
		
		// in case we do not have previous data we just fill the list with the data
		while(lastVelocityMagnitudeCategories.size() < numberOfLastCategories) {
			
			lastVelocityMagnitudeCategories.add(this.getVelocityForClassification(velocityMagnitude.intValue(), velocityClasses, pedestrian, state));
			lastVelocityAngleCategories.add(this.getAngleForClassification(velocityAngleChange.intValue(), angleClasses));
		}
	}
	
	public void updatePedestrianTeach(IOperationalPedestrian pedestrian,
			WalkingState newWalkingState,
			SimulationState state,
			int velocityClasses,
			int angleClasses,
			int numberOfLastCategories) {

		double velocityMagnitudeChangeNoCategory = newWalkingState.getWalkingVelocity().getMagnitude();
		double velocityAngleChangeNoCategory = GeometryAdditionals.angleBetweenPlusMinus180(newWalkingState.getWalkingVelocity(), zeroVector, pedestrian.getVelocity());

		velocityAngleChange = (double)this.getClassForAngle(velocityAngleChangeNoCategory, angleClasses);
		velocityMagnitude = (double)this.getClassForVelocity(velocityMagnitudeChangeNoCategory, velocityClasses, pedestrian, state);
		
		// in case we do not have previous data we just fill the list with the data
		while(lastVelocityMagnitudeCategories.size() < numberOfLastCategories) {
			
			lastVelocityMagnitudeCategories.add(this.getVelocityForClassification(velocityMagnitude.intValue(), velocityClasses, pedestrian, state));
			lastVelocityAngleCategories.add(this.getAngleForClassification(velocityAngleChange.intValue(), angleClasses));
		}
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

	public int getClassForAngle(double angle, int classes) {
		
		double min = -FastMath.PI/2.0;
		double max = FastMath.PI/2.0;
		
		double classRange = (max - min) / classes;
		double current = min + classRange;
		
		int classId = 0;
		
		while(current <= max) {
			
			classId++; // max / classRanges number of class Ids
			
			if(angle < current) { // is in current class range
				
				 break;
			}
			
			current += classRange;
		}
		
		return classId;
	}
	
	public double getAngleForClassification(int classId, int classes) {
		
		double min = -FastMath.PI/4.0;
		double max = FastMath.PI/4.0;
		
		double classRange = (max - min) / classes;
		double current = min + classId * classRange - 0.5 * classRange;
		
		return current;
	}

	private int getClassForVelocity(double velocity, int classes, IPedestrian pedestrian, SimulationState state) {
		
		double min = 0;
		double max = this.maximalVelocityInfluence * pedestrian.getMaximalVelocity() * state.getTimeStepDuration();
		
		double classRange = (max - min) / classes;
		double current = min + classRange;
		
		int classId = 0;
		
		while(current <= max) {
			
			classId++; // max / classRanges number of class Ids
			
			if(velocity < current) { // is in current class range
				
				 break;
			}
			
			current += classRange;
		}
		
		return classId;
	}
	
	/**
	 * Class ids start with 1 and ends size of classifications
	 * @param classifications
	 * @return
	 */
	public int findClassId(double[] classifications) {
		
		int classId = 0;
		
		for(int iter = 0; iter < classifications.length; iter++) {
			
			classId++;
			if(classifications[iter] != 0) {
				
				break;
			}
		}
		
		return classId;
	}
	
	
	public int findClassIdByMaxValue(double[] classifications) {
		
		int classIdMax = 0;
		double maxValue = 0;
		
		for(int iter = 0; iter < classifications.length; iter++) {
			
			if(maxValue < classifications[iter]) {
				
				maxValue = classifications[iter];
				classIdMax = iter;
			}
		}
		
		return classIdMax;
	}
	
	public double getVelocityForClassification(int classId, int classes, IPedestrian pedestrian, SimulationState state) {
		
		double min = 0;
		double max = this.maximalVelocityInfluence * pedestrian.getMaximalVelocity() * state.getTimeStepDuration();
		
		double classRange = (max - min) / classes;
		double current = min + classId * classRange - 0.5 * classRange;
		
		return current;
	}

	private static final Comparator<CsvPlaybackPerceptionWriterItem> Comperator = new Comparator<CsvPlaybackPerceptionWriterItem>() {
		
		@Override
		public int compare(CsvPlaybackPerceptionWriterItem left, CsvPlaybackPerceptionWriterItem right) {
		
			return Double.compare(left.getAngleToPercept(), right.getAngleToPercept());
		}
	};
}


//private List<Double> perceptionDistanceSpace = new ArrayList<Double>();
//private List<Double> perceptionVelocityXSpace = new ArrayList<Double>();
//private List<Double> perceptionVelocityYSpace = new ArrayList<Double>();
//private List<Double> perceptionTypeSpace = new ArrayList<Double>();
//
//private Double pedestrianWalkingGoalX = 0.0;
//private Double pedestrianWalkingGoalY = 0.0;
//private Double pedestrianVelocityX = 0.0;
//private Double pedestrianVelocityY = 0.0;
//private Double pedestrianVelocityXLast = 0.0;
//private Double pedestrianVelocityYLast = 0.0;
//private Double pedestrianVelocityXLastSec = 0.0;
//private Double pedestrianVelocityYLastSec = 0.0;


//private List<Double> distancesToPercepts = new ArrayList<Double>();
//
//public List<Double> getDistancesToPercepts() {
//	return distancesToPercepts;
//}
//
//private List<Double> anglesToPercepts = new ArrayList<Double>();
//
//public List<Double> getAnglesToPercepts() {
//	return anglesToPercepts;
//}
//
//private List<Double> typesOfPercepts = new ArrayList<Double>();
//
//public List<Double> getTypesOfPercepts() {
//	return typesOfPercepts;
//}
//
//private List<Double> velocityAngleDifferencesToPercepts = new ArrayList<Double>();
//
//public List<Double> getVelocityAngleDifferencesToPercepts() {
//	return velocityAngleDifferencesToPercepts;
//}
//
//private List<Double> velocityMagnitudesOfPercepts = new ArrayList<Double>();
//
//public List<Double> getVelocityMagnitudesOfPercepts() {
//	return velocityMagnitudesOfPercepts;
//}

//public List<Double> getPerceptionDistanceSpace() {
//	return perceptionDistanceSpace;
//}
//
//public List<Double> getPerceptionVelocityXSpace() {
//	return perceptionVelocityXSpace;
//}
//
//public List<Double> getPerceptionVelocityYSpace() {
//	return perceptionVelocityYSpace;
//}
//
//public List<Double> getPerceptionTypeSpace() {
//	return perceptionTypeSpace;
//}
//
//public Double getPedestrianWalkingGoalX() {
//	return pedestrianWalkingGoalX;
//}
//
//public Double getPedestrianWalkingGoalY() {
//	return pedestrianWalkingGoalY;
//}
//
//public Double getPedestrianVelocityX() {
//	return pedestrianVelocityX;
//}
//
//public Double getPedestrianVelocityY() {
//	return pedestrianVelocityY;
//}
//
//public Double getPedestrianVelocityXLast() {
//	return pedestrianVelocityXLast;
//}
//
//public Double getPedestrianVelocityYLast() {
//	return pedestrianVelocityYLast;
//}
//
//public Double getPedestrianVelocityXLastSec() {
//	return pedestrianVelocityXLastSec;
//}
//
//public Double getPedestrianVelocityYLastSec() {
//	return pedestrianVelocityYLastSec;
//}