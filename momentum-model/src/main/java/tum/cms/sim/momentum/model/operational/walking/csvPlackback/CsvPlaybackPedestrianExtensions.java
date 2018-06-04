package tum.cms.sim.momentum.model.operational.walking.csvPlackback;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import tum.cms.sim.momentum.data.agent.pedestrian.state.operational.WalkingState;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IOperationalPedestrian;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrian;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrianExtension;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.operational.walking.csvPlackback.CsvPlaybackPedestrianExtensions.CsvPlaybackPerceptionWriterItem;
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
	
	private double initialWalkingTargetDistance = -1.0;
	private Vector2D currentWalkingTarget = null;
	
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
	
	private Double distanceToGoal = null;
	private Double lastDistancetoGoal = null;
	
	public Double getDistanceToGoal() {
		return distanceToGoal;
	}
	
	public Double getLastDistanceToGoal() {
		return lastDistancetoGoal;
	}

	private Double lastVelocityNormValue = null;

	public Double getLastVelocityNormValue() {
		return lastVelocityNormValue;
	}

	private Double lastAngleNormValue = null;
	
	public Double getLastAngleNormValue() {
		return lastAngleNormValue;
	}

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
	
	
	public void updatePerceptionSpace(IOperationalPedestrian pedestrian,
			PerceptionalModel perception,
			SimulationState simulationState) {

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
				item.setVelocityMagnitudeOfPercept(normVelo(other.getVelocity().getMagnitude(), pedestrian, simulationState));
				item.setVelocityAngleDifferenceToPercept(normAngle(GeometryAdditionals.angleBetweenPlusMinus180(other.getVelocity(), zeroVector, pedestrian.getVelocity())));
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
			
			this.perceptItems.add(item);
		}
		
		if(countItems != null) {
			
			this.perceptItems = new ArrayList<>(this.perceptItems.stream()
					.sorted()
					.collect(Collectors.toList()));
			
			ArrayList<CsvPlaybackPedestrianExtensions.CsvPlaybackPerceptionWriterItem> closeItems = new ArrayList<>();
			
			while (closeItems.size() < countItems) {
				if(this.perceptItems.size() < countItems) {
					return;
				}
				closeItems = new ArrayList<>(this.perceptItems.subList(0, countItems));
				
				for(int iter = 0; iter < closeItems.size() - 1; iter++) {
					if(closeItems.get(iter).angleToPercept == closeItems.get(iter+1).angleToPercept) {
						
						if(this.perceptItems.size() > countItems) {
							closeItems.remove(iter);
							this.perceptItems.remove(iter);
							iter--;
						}
					}
				}
			}
			
			this.perceptItems = new ArrayList<CsvPlaybackPedestrianExtensions.CsvPlaybackPerceptionWriterItem>();
			this.perceptItems.addAll(closeItems);
		}
	}
	
	public void updatePedestrianSpace(IOperationalPedestrian pedestrian,
			SimulationState simulationState,
			int velocityClasses,
			int angleClasses,
			PerceptionalModel perception) {

		if(pedestrian.getNextWalkingTarget() != null || currentWalkingTarget != null) {
			 
			if(currentWalkingTarget == null || !pedestrian.getNextWalkingTarget().equals(this.currentWalkingTarget)) {
				currentWalkingTarget = pedestrian.getNextWalkingTarget();
				initialWalkingTargetDistance = pedestrian.getNextWalkingTarget()
						.distance(pedestrian.getPosition());
				lastDistancetoGoal = null;
			}
			
			Vector2D towardsGoal = currentWalkingTarget.subtract(pedestrian.getPosition());
			angleToGoal = normAngle(GeometryAdditionals.angleBetweenPlusMinus180(towardsGoal, zeroVector, pedestrian.getHeading()));
			// distance to target is 4 times larger
			// reduce distance by multiple 4
			lastDistancetoGoal = distanceToGoal;
			distanceToGoal = (currentWalkingTarget.distance(pedestrian.getPosition()))  / initialWalkingTargetDistance;
			
			if(lastDistancetoGoal == null ) {
				lastDistancetoGoal = distanceToGoal;
			}			
		}
	}

	public void initializeLastTeach(IOperationalPedestrian pedestrian,
			SimulationState state,
			double velocityNoCategory,
			double angleNoCategory,
			int velocityClasses,
			int angleClasses) {
		
		// in case we do not have previous data we just fill the list with the data
		
		if(lastAngleNormValue == null) {
						
			lastVelocityNormValue = this.normVelo(velocityNoCategory, pedestrian, state);
			lastAngleNormValue = this.normAngle(angleNoCategory);
		}
	}
	
	public double denormAngle(double normAngle) {
		
		return (normAngle * 2.0 * Math.PI) - Math.PI;
	}
	
	private double normAngle(double angle) {
		
		return (angle + Math.PI)/ (2.0*Math.PI);
	}
	
	public double denormVelo(double normVelo, IOperationalPedestrian pedestrian, double simulationTimeStep) {
		
		return normVelo * pedestrian.getMaximalVelocity() * simulationTimeStep;
	}
	
	private double normVelo(double velo, IOperationalPedestrian pedestrian, SimulationState simulationState) {
	
		return velo / (pedestrian.getMaximalVelocity() * simulationState.getTimeStepDuration());
	}
	
	public void updatePedestrianTeach(IOperationalPedestrian pedestrian,
			WalkingState newWalkingState,
			SimulationState state,
			int velocityClasses,
			int angleClasses,
			double velocityScaling,
			double angleScaling) {

		// new - old > 0 faster
		// new - old < 0 slower
		
		if(velocityMagnitudeCategorie == null && lastVelocityNormValue == null) {
			lastVelocityNormValue = normVelo(pedestrian.getVelocity().getMagnitude(), pedestrian, state);
			lastAngleNormValue = normAngle(0.0);
		} 
		else if (velocityMagnitudeCategorie != null) { // set last
			lastVelocityNormValue = this.getNormForValue(velocityMagnitudeCategorie.intValue(), velocityClasses);
			lastAngleNormValue = this.getNormForValue(velocityAngleCategorie.intValue(), angleClasses);
		}
		
		// set new
		double newVelocityMagnitudeChangeNoCategory = velocityScaling * 
				newWalkingState.getWalkingVelocity().getMagnitude();
		double newVelocityAngleChangeNoCategory = angleScaling * 
				GeometryAdditionals.angleBetweenPlusMinus180(newWalkingState.getWalkingVelocity(), zeroVector, pedestrian.getVelocity());
		
		double newMagnitudeNorm = normVelo(newVelocityMagnitudeChangeNoCategory, pedestrian, state);
		double newAngleNorm = normAngle(newVelocityAngleChangeNoCategory);
		
		velocityMagnitudeCategorie = (double)this.getClassForNorm(newMagnitudeNorm, velocityClasses);
		velocityAngleCategorie = (double)this.getClassForNorm(newAngleNorm, angleClasses);
	}
	
	public class CsvPlaybackPerceptionWriterItem implements Comparable<CsvPlaybackPerceptionWriterItem>{
		
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
		public int compareTo(CsvPlaybackPerceptionWriterItem o) {
			
			return Double.compare(this.getDistanceToPercept(), o.getDistanceToPercept());
		}
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
	
	public double getNormForValue(int classId, int classes) {
		
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

//	private static final Comparator<CsvPlaybackPerceptionWriterItem> ComperatorAngle = new Comparator<CsvPlaybackPerceptionWriterItem>() {
//		
//		@Override
//		public int compare(CsvPlaybackPerceptionWriterItem left, CsvPlaybackPerceptionWriterItem right) {
//		
//			return Double.compare(left.getAngleToPercept(), right.getAngleToPercept());
//		}
//	};
//	
}