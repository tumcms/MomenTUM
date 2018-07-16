package tum.cms.sim.momentum.model.operational.walking.csvPlackback;

import java.util.ArrayList;
import java.util.List;
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
	
	private static double freeCode = 0.0;
	private static double groupCode = 0.4;
	private static double pedestrianCode = 0.5;
	private static double obstacleCode = 1.0;
	
	private static Vector2D zeroVector = GeometryFactory.createVector(0.0, 0.0);
	private boolean firstDataSet = true;
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

	private static int memorySize = 10; // 0.04 * 10 = 0.4 seconds
	private static boolean singlePercept = true;

	private Vector2D currentPosition = null;

	public static boolean isSinglePercept() {
		return singlePercept;
	}

	public static void setSinglePercept(boolean singlePercept) {
		CsvPlaybackPedestrianExtensions.singlePercept = singlePercept;
	}

	public ArrayList<CsvPlaybackDataItem> getMemory() {
		return memory;
	}

	public static int getMemorySize() {
		return memorySize;
	}
	
	public static void setMemorySize(int memorySize) {
		CsvPlaybackPedestrianExtensions.memorySize = memorySize;
	}

	public Vector2D getCurrentPosition() {
		return currentPosition;
	}

	public boolean isMemoryReady() {
		
		if(memory.size() < memorySize) {
			return false;
		}
		
		if(this.memory.get(CsvPlaybackPedestrianExtensions.getMemorySize() - 1).containsEmpty()) {
			return false;
		}
		
		if(this.memory.get(0).containsEmpty()) {
			return false;
		}
		
		return true;
	}
	
	public CsvPlaybackDataItem getCurrent() {
		
		return this.memory.get(CsvPlaybackPedestrianExtensions.getMemorySize() - 1);
	}
	
	public CsvPlaybackDataItem getLast() {
		
		return this.memory.get(0);
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

	// Training data
	private ArrayList<CsvPlaybackDataItem> memory = new ArrayList<>();
	
	// Teaching data
	private Double velocityMagnitudeCategorie = null;
	
	public Double getCurrentVelocityCategorie() {
		return velocityMagnitudeCategorie;
	}
	
	private Double velocityAngleCategorie = null;
	
	public Double getCurrentAngleCategorie() {
		return velocityAngleCategorie;
	}
	private static Integer countItems = -1;
	
	public static Integer getCountItems() {
		return countItems;
	}

	public static void setCountItems(Integer countItems) {
		CsvPlaybackPedestrianExtensions.countItems = countItems;
	}

	public void updateTrainingData(IOperationalPedestrian pedestrian,
			PerceptionalModel perception,
			SimulationState simulationState) {
		
		List<CsvPlaybackPerceptionItem> perceptionItems = this.computePerceptionTraining(pedestrian,
				perception,
				simulationState);
		CsvPlaybackGoalItem goalItem = this.computeGoalTraining(pedestrian);
		CsvPlaybackMovementItem movementItem = this.computeMovementTraining();
		
		CsvPlaybackDataItem dataItem = new CsvPlaybackDataItem();
		dataItem.setPerceptionItems(perceptionItems);
		dataItem.setGoalItem(goalItem);
		dataItem.setMovementItem(movementItem);
		
		if(this.memory.size() >= memorySize) {
			this.memory.remove(0);
		}
		
		this.memory.add(dataItem);
	}

	public void addDefaultMemoryItem(IOperationalPedestrian pedestrian,
			PerceptionalModel perception,
			SimulationState simulationState,
			int velocityClasses,
			int angleClasses) {
	
		List<CsvPlaybackPerceptionItem> perceptionItems = this.computePerceptionTraining(pedestrian,
				perception,
				simulationState);
		CsvPlaybackGoalItem goalItem = this.computeGoalTraining(pedestrian);
		CsvPlaybackMovementItem movementItem = this.computeDefaultMovementTraining(pedestrian,
				simulationState,
				velocityClasses,
				angleClasses);
		
		CsvPlaybackDataItem dataItem = new CsvPlaybackDataItem();
		dataItem.setPerceptionItems(perceptionItems);
		dataItem.setGoalItem(goalItem);
		dataItem.setMovementItem(movementItem);
		this.memory.add(dataItem);
	}
	
	
	public double denormAngle(double normAngle) {
		
		return (normAngle * 2.0 * Math.PI) - Math.PI;
	}
		
	public double denormVelo(double normVelo, double maximalVelocity, double simulationTimeStep) {
		
		return normVelo * maximalVelocity * simulationTimeStep;
	}

	public void computeMovementTeaching(IOperationalPedestrian lastWalkingPedestrian,
			WalkingState teachingWalkingState,
			SimulationState state,
			int velocityClasses,
			int angleClasses,
			double velocityScaling,
			double angleScaling) {
		
		CsvPlaybackMovementItem movementTeachingItem = new CsvPlaybackMovementItem();
		
		// The teaching walking state is already scaled regarding the current time step!
		double currentTeachingVelocity = velocityScaling * teachingWalkingState.getWalkingVelocity().getMagnitude();
		double currentTeachingAngle = angleScaling * 
				GeometryAdditionals.angleBetweenPlusMinus180(teachingWalkingState.getWalkingVelocity(),
						zeroVector, lastWalkingPedestrian.getVelocity());
		
		double currentTeachingVelocityNorm = normVelo(currentTeachingVelocity,
				lastWalkingPedestrian.getMaximalVelocity(),
				state.getTimeStepDuration());
		double currentTeachingAngleNorm = normAngle(currentTeachingAngle);
		
		movementTeachingItem.setAngleClasses(angleClasses);
		movementTeachingItem.setVelocityClasses(velocityClasses);
		movementTeachingItem.setMaximalVelocity(lastWalkingPedestrian.getMaximalVelocity());
		movementTeachingItem.setSimulationTimeStep(state.getTimeStepDuration());
		movementTeachingItem.setAngleNormValue(currentTeachingAngleNorm);
		movementTeachingItem.setVelocityNormValue(currentTeachingVelocityNorm);
		
		this.memory.get(this.memory.size() - 1).setMovementTeachingItem(movementTeachingItem);
	}

	
	public double getNormForClassId(int classId, int classes) {
		
		double min = 0.0;
		double max = 1.0;
		
		double classRange = (max - min) / classes;
		double current = min + classId * classRange - 0.5 * classRange;
		
		return current;
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
	
	private List<CsvPlaybackPerceptionItem> computePerceptionTraining(IOperationalPedestrian pedestrian,
			PerceptionalModel perception,
			SimulationState simulationState) {

		Vector2D position = pedestrian.getPosition();
		Vector2D heading = pedestrian.getHeading();
		
		// this collection is of constant size, null if no obstacle was found
		List<Vector2D> obstaclePositions = perception.getPerceptedObstaclePositions(pedestrian, simulationState);
	
		// this collection is of constant size, null if no pedestrian was found
		List<IPedestrian> pedestrianPositions = perception.getPerceptedPedestrianPositions(pedestrian, simulationState);
		List<Vector2D> freePositions = perception.getPerceptedFreePositions(pedestrian, simulationState);
		
		ArrayList<CsvPlaybackPerceptionItem> perceptItems = new ArrayList<>();
		for(int iter = 0; iter < obstaclePositions.size(); iter++) {
			
			CsvPlaybackPerceptionItem item = new CsvPlaybackPerceptionItem();
			
			if(obstaclePositions.get(iter) != null) {
				
				Vector2D obstaclePosition = obstaclePositions.get(iter);
				
				double distance = position.distance(obstaclePosition) - pedestrian.getBodyRadius();
				
				item.setDistanceToPercept(distance < 0.0 ? 0.0 : distance / perception.getPerceptionDistance());
				item.setAngleToPercept(normAngle(GeometryAdditionals.angleBetweenPlusMinus180(obstaclePosition.subtract(position), zeroVector, heading)));
				item.setVelocityMagnitudeOfPercept(0.0);
				item.setVelocityAngleDifferenceToPercept(1.0);
				item.setTypeOfPercept(obstacleCode);
			}
			else if(pedestrianPositions.get(iter) != null) {
				
				IPedestrian other = pedestrianPositions.get(iter);
				
				double distance = position.distance(other.getPosition()) - (other.getBodyRadius() + pedestrian.getBodyRadius());
				
				item.setDistanceToPercept(distance < 0.0 ? 0.0 : distance / perception.getPerceptionDistance());
				item.setAngleToPercept(normAngle(GeometryAdditionals.angleBetweenPlusMinus180(other.getPosition().subtract(position), zeroVector, heading)));
				item.setVelocityMagnitudeOfPercept(normVelo(other.getVelocity().getMagnitude(),
						pedestrian.getMaximalVelocity(),
						simulationState.getTimeStepDuration()));
				item.setVelocityAngleDifferenceToPercept(normAngle(
						GeometryAdditionals.angleBetweenPlusMinus180(other.getHeading(), zeroVector, heading)));
				item.setTypeOfPercept(other.getGroupId() == pedestrian.getGroupId() ? groupCode : pedestrianCode);
			}
			else {
			
				Vector2D freePosition = freePositions.get(iter);
				
				double distance = perception.getPerceptionDistance() - pedestrian.getBodyRadius();
				item.setDistanceToPercept(distance < 0.0 ? 0.0 : distance / perception.getPerceptionDistance());
				item.setAngleToPercept(normAngle(GeometryAdditionals.angleBetweenPlusMinus180(freePosition.subtract(position), zeroVector, heading)));
				item.setVelocityMagnitudeOfPercept(0.0);
				item.setVelocityAngleDifferenceToPercept(0.5);
				item.setTypeOfPercept(freeCode); 
			}
			
			perceptItems.add(item);
		}
		
		if(CsvPlaybackPedestrianExtensions.isSinglePercept()) {
			CsvPlaybackPerceptionItem closestPerceptItem = perceptItems.stream()
					.sorted()
					.findFirst()
					.orElse(null);
			perceptItems.clear();
			perceptItems.add(closestPerceptItem);
		}
		
		return perceptItems;
	}
	
	private CsvPlaybackGoalItem computeGoalTraining(IOperationalPedestrian pedestrian) {

		if(pedestrian.getNextWalkingTarget() == null) {
			
			return null;
		}
		
		CsvPlaybackGoalItem goalItem = new CsvPlaybackGoalItem();
			 		
		Vector2D towardsGoal = pedestrian.getNextWalkingTarget().subtract(pedestrian.getPosition());
		double angleToGoal = normAngle(GeometryAdditionals.angleBetweenPlusMinus180(towardsGoal, zeroVector, pedestrian.getHeading()));
		double distanceToGoal = pedestrian.getNextWalkingTarget().distance(pedestrian.getPosition());
		goalItem.setAngleToGoal(angleToGoal);
		goalItem.setDistanceToGoal(distanceToGoal);
		
		return goalItem;
	}

	private CsvPlaybackMovementItem computeMovementTraining() {
	
		if(this.memory.size() == 0) {
			return null;
		}
		
		return this.memory.get(0).getMovementTeachingItem(); 
	}
	
	private CsvPlaybackMovementItem computeDefaultMovementTraining(IOperationalPedestrian pedestrian,
			SimulationState state,
			int velocityClasses,
			int angleClasses) {
		
		CsvPlaybackMovementItem movementItem = new CsvPlaybackMovementItem();
		
		double velocityNormValue = normVelo(pedestrian.getDesiredVelocity() * state.getTimeStepDuration(),
				pedestrian.getMaximalVelocity(),
				state.getTimeStepDuration());
		double angleNormValue = normAngle(0.0);
		movementItem.setVelocityNormValue(velocityNormValue);
		movementItem.setAngleNormValue(angleNormValue);
		movementItem.setAngleClasses(angleClasses);
		movementItem.setVelocityClasses(velocityClasses);
		movementItem.setMaximalVelocity(pedestrian.getMaximalVelocity());
		movementItem.setSimulationTimeStep(state.getTimeStepDuration());
		
		return movementItem;
	}
	
	private double normAngle(double angle) {
		
		return (angle + Math.PI)/ (2.0*Math.PI);
	}
	
	private int getClassForNorm(double value, int classes) {
		
		double min = 0.0;
		double max = 1.0;
		
		double classRange = (max - min) / classes;
		double current = min + classRange;
		
		int classId = 0;
		
		while(current <= max) {
			
			classId++; // max / classRanges number of class Ids
			
			if(value < current) { // is in current class range
				
				 break;
			}
			
			current += classRange;
		}
		
		return classId;
	}
	
	private double normVelo(double velo, double maximalVelocity, double simulationTimeStep) {
		
		return velo / (maximalVelocity * simulationTimeStep);
	}
	
	public class CsvPlaybackDataItem {
		
		private List<CsvPlaybackPerceptionItem> perceptionItems;
		
		private CsvPlaybackMovementItem movementItem;
		private CsvPlaybackGoalItem goalItem;
		private CsvPlaybackMovementItem movementTeachingItem;
		
		public boolean containsEmpty() {
			return perceptionItems == null ||
					perceptionItems.isEmpty() ||
					movementItem == null ||
					goalItem == null;
		}
		
		public List<CsvPlaybackPerceptionItem> getPerceptionItems() {
			return perceptionItems;
		}
		public CsvPlaybackPerceptionItem getSinglePerceptionItem() {
			return perceptionItems.get(0);
		}
		public void setPerceptionItems(List<CsvPlaybackPerceptionItem> perceptionItems) {
			this.perceptionItems = perceptionItems;
		}
		public CsvPlaybackMovementItem getMovementItem() {
			return movementItem;
		}
		public void setMovementItem(CsvPlaybackMovementItem movementItem) {
			this.movementItem = movementItem;
		}
		public CsvPlaybackGoalItem getGoalItem() {
			return goalItem;
		}
		public void setGoalItem(CsvPlaybackGoalItem goalItem) {
			this.goalItem = goalItem;
		}
		public CsvPlaybackMovementItem getMovementTeachingItem() {
			return movementTeachingItem;
		}
		public void setMovementTeachingItem(CsvPlaybackMovementItem movementTeachingItem) {
			this.movementTeachingItem = movementTeachingItem;
		}
	}
	
	public class CsvPlaybackMovementItem {
		
		private Double velocityNormValue = null;
		private Double angleNormValue = null;
		private Integer angleClasses = null;
		private Integer velocityClasses = null;
		private Double simulationTimeStep = null;
		private Double maximalVelocity = null;
		
		public Integer getAngleClasses() {
			return angleClasses;
		}
		public void setAngleClasses(Integer angleClasses) {
			this.angleClasses = angleClasses;
		}
		public Integer getVelocityClasses() {
			return velocityClasses;
		}
		public void setVelocityClasses(Integer velocityClasses) {
			this.velocityClasses = velocityClasses;
		}
		public Double getSimulationTimeStep() {
			return simulationTimeStep;
		}
		public void setSimulationTimeStep(Double simulationTimeStep) {
			this.simulationTimeStep = simulationTimeStep;
		}
		public Double getMaximalVelocity() {
			return maximalVelocity;
		}
		public void setMaximalVelocity(Double maximalVelocity) {
			this.maximalVelocity = maximalVelocity;
		}
		public Double getVelocityNormValue() {
			return velocityNormValue;
		}
		public void setVelocityNormValue(Double velocityNormValue) {
			this.velocityNormValue = velocityNormValue;
		}
		public Double getAngleNormValue() {
			return angleNormValue;
		}
		public void setAngleNormValue(Double angleNormValue) {
			this.angleNormValue = angleNormValue;
		}
		public Double getAngleValue() {
			return CsvPlaybackPedestrianExtensions.this.denormAngle(this.angleNormValue);
		}
		public Double getVelocityValue() {
			return CsvPlaybackPedestrianExtensions.this.denormVelo(this.velocityNormValue,
					maximalVelocity,
					simulationTimeStep);
		}
		public double getAngleClassValue() {
			return CsvPlaybackPedestrianExtensions.this.getClassForNorm(this.angleNormValue, angleClasses);
		}
		public double getVelocityClassValue() {
			return CsvPlaybackPedestrianExtensions.this.getClassForNorm(this.velocityNormValue, velocityClasses);
		}
		
	}
	
	public class CsvPlaybackGoalItem {
		
		private Double distanceToGoal = null;
		private Double angleToGoal = null;
		
		public Double getDistanceToGoal() {
			return distanceToGoal;
		}
		public void setDistanceToGoal(Double distanceToGoal) {
			this.distanceToGoal = distanceToGoal;
		}
		public Double getAngleToGoal() {
			return angleToGoal;
		}
		public void setAngleToGoal(Double angleToGoal) {
			this.angleToGoal = angleToGoal;
		}	
	}
	
	public class CsvPlaybackPerceptionItem implements Comparable<CsvPlaybackPerceptionItem>{
		
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
		
		@Override
		public int compareTo(CsvPlaybackPerceptionItem o) {
			
			return Double.compare(this.getDistanceToPercept(), o.getDistanceToPercept());
		}
	}


//	private static final Comparator<CsvPlaybackPerceptionWriterItem> ComperatorAngle = new Comparator<CsvPlaybackPerceptionWriterItem>() {
//		
//		@Override
//		public int compare(CsvPlaybackPerceptionWriterItem left, CsvPlaybackPerceptionWriterItem right) {
//		
//			return Double.compare(left.getAngleToPercept(), right.getAngleToPercept());
//		}
//	};


//	public void initializeLastTeach(IOperationalPedestrian pedestrian,
//			SimulationState state,
//			double velocityNoCategory,
//			double angleNoCategory,
//			int velocityClasses,
//			int angleClasses) {
//		
//		// in case we do not have previous data we just fill the list with the data
//		
//		if(lastAngleNormValue == null) {
//						
//			lastVelocityNormValue = this.normVelo(velocityNoCategory, pedestrian, state);
//			lastAngleNormValue = this.normAngle(angleNoCategory);
//		}
////		if(velocityMagnitudeCategorie == null && lastVelocityNormValue == null) {
////			lastVelocityNormValue = normVelo(pedestrian.getVelocity().getMagnitude(), pedestrian, state);
////			lastAngleNormValue = normAngle(0.0);
////		} 
//
////		else if (velocityMagnitudeCategorie != null) { // set last
////			lastVelocityNormValue = this.getNormForValue(velocityMagnitudeCategorie.intValue(), velocityClasses);
////			lastAngleNormValue = this.getNormForValue(velocityAngleCategorie.intValue(), angleClasses);
////		}
//		
//	}
}