package tum.cms.sim.momentum.model.operational.walking.csvPlackback;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import tum.cms.sim.momentum.configuration.model.output.WriterSourceConfiguration.OutputType;
import tum.cms.sim.momentum.data.agent.pedestrian.state.operational.WalkingState;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IOperationalPedestrian;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrianExtension;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.operational.walking.WalkingModel;
import tum.cms.sim.momentum.utility.geometry.GeometryFactory;
import tum.cms.sim.momentum.utility.geometry.Vector2D;

/**
 * This operational model will get a csv data set and will steer pedestrians
 * which exists in the simulation (id mapping) regarding the csv data.
 * 
 * Furthermore, the model provides a writer source to extract detailed movement
 * and perception data of the agents for later computations (e.g. ML).
 * 
 * The input may have a different time scale (1 time step = x seconds).
 * This is be solved by providing the timeStepMapping property for this class.
 * 
 * Warning: This class works only with ShadowPerceptionModel
 * @author Peter Kielar
 *
 */
public class CsvPlaybackOperational extends WalkingModel {

	private static String csvInputName = "csvInput";
	private static String csvMappingName = "csvMapping";
	private static String timeStepMappingName = "timeStepMapping";
	private static String containsHeaderName = "containsHeader";
	private static String numberForMeanName = "numberForMean";
	private static String numberOfLastCategoriesName = "numberOfLastCategories";
	
	private static String velocityClassesName = "velocityClasses";
	private static String angleClassesName = "angleClasses";
	
	private int velocityClasses = 1;
	private int angleClasses = 1;
	
	private double timeStepMapping = 0.0;
	private int timeStepIndex = -1;
	private int idIndex = -1;
	private int xIndex = -1;
	private int yIndex = -1;
	private int numberForMean = 5;
	private int numberOfLastCategories = 2;

	private HashMap<Long, ArrayList<ArrayList<Double>>> movementData = new HashMap<>();
	private HashMap<Integer, ArrayList<Double>> nextMovementData = new HashMap<>();
	
	@Override
	public IPedestrianExtension onPedestrianGeneration(IRichPedestrian pedestrian) {

		return new CsvPlaybackPedestrianExtensions();
	}

	@Override
	public void onPedestrianRemoval(IRichPedestrian pedestrian) {
		// Nothing to do
	}

	@Override
	public void callBeforeBehavior(SimulationState simulationState, Collection<IRichPedestrian> pedestrians) {
		
		// Read the next time step
		ArrayList<ArrayList<Double>> currentData = movementData.get(simulationState.getCurrentTimeStep() + 1);
		
		if(currentData != null) {
			currentData.stream().forEach(dataSet -> nextMovementData.put(dataSet.get(idIndex).intValue(), dataSet));
		}
	}

	@Override
	public void callAfterBehavior(SimulationState simulationState, Collection<IRichPedestrian> pedestrians) {
		
		nextMovementData.clear();
	}

	@Override
	public void callPedestrianBehavior(IOperationalPedestrian pedestrian, SimulationState simulationState) {

		CsvPlaybackPedestrianExtensions extension = (CsvPlaybackPedestrianExtensions) pedestrian.getExtensionState(this); 
		
		// Get the data for pedestrian id
		// in case there is multiple data mapped, it will automatically get last data step (close to current time)
		
		ArrayList<Double> pedestrianDataSet = nextMovementData.get(pedestrian.getId());
		
		double x = pedestrian.getPosition().getXComponent();
		double y = pedestrian.getPosition().getYComponent();

		double xNext = pedestrianDataSet.get(xIndex);
		double yNext = pedestrianDataSet.get(yIndex);	

		double velocityXNext = (xNext - x); // distance m in timeStepDuration seconds
		double velocityYNext = (yNext - y); // distance m in timeStepDuration seconds
		double headingXNext = (xNext - x);
		double headingYNext = (yNext - y);
		
		Vector2D heading = extension.updateHeadings(GeometryFactory.createVector(headingXNext, headingYNext).getNormalized(),
				this.numberForMean);
		
		WalkingState newWalkingState = new WalkingState(
				GeometryFactory.createVector(xNext, yNext),
				GeometryFactory.createVector(velocityXNext, velocityYNext),
				heading);
	
		extension.updatePerceptionSpace(pedestrian, this.perception, simulationState);
		extension.updatePedestrianSpace(pedestrian, simulationState, velocityClasses, angleClasses, this.numberOfLastCategories);
		extension.updatePedestrianTeach(pedestrian, newWalkingState, simulationState, velocityClasses, angleClasses, this.numberOfLastCategories);
		extension.setCurrentPosition(newWalkingState.getWalkingPosition());
		
		pedestrian.setWalkingState(newWalkingState);
	}

	@Override
	public void callPreProcessing(SimulationState simulationState) {
		
		this.velocityClasses = this.properties.getIntegerProperty(velocityClassesName);
		this.angleClasses = this.properties.getIntegerProperty(angleClassesName);
		this.numberForMean = this.properties.getIntegerProperty(numberForMeanName);
		this.numberOfLastCategories = this.properties.getIntegerProperty(numberOfLastCategoriesName);
		this.timeStepMapping = this.properties.getDoubleProperty(timeStepMappingName);
		List<String> csvInput = this.properties.<String>getListProperty(csvMappingName);
		
		CsvPlaybackPedestrianExtensions.setxMaxCut(this.properties.getDoubleProperty("xMaxCut"));
		CsvPlaybackPedestrianExtensions.setxMinCut(this.properties.getDoubleProperty("xMinCut"));
		CsvPlaybackPedestrianExtensions.setyMaxCut(this.properties.getDoubleProperty("yMaxCut"));
		CsvPlaybackPedestrianExtensions.setyMinCut(this.properties.getDoubleProperty("yMinCut"));
		
		for(int iter = 0; iter < csvInput.size(); iter++) {
			
			if(OutputType.valueOf(csvInput.get(iter)) == OutputType.timeStep) {
				
				timeStepIndex = iter;
			}
			
			if(OutputType.valueOf(csvInput.get(iter)) == OutputType.id) {
				
				idIndex = iter;
			}
			
			if(OutputType.valueOf(csvInput.get(iter)) == OutputType.x) {
						
				xIndex = iter;
			}
			
			if(OutputType.valueOf(csvInput.get(iter)) == OutputType.y) {
				
				yIndex = iter;
			}
		}
		
		HashMap<Integer, ArrayList<Double>> csvMapping = this.properties.<Double>getMatrixProperty(csvInputName);
		
		if(this.properties.getBooleanProperty(containsHeaderName)) {
			csvMapping.remove(0);
		}
		
		for(ArrayList<Double> data : csvMapping.values()) {
			
			long dataTimeStep = data.get(timeStepIndex).longValue();
			long simulationTimeStep = simulationState.getScaledTimeStep(dataTimeStep, this.timeStepMapping);
			
			// add time step			
			if(!this.movementData.containsKey(simulationTimeStep)) {
				
				this.movementData.put(simulationTimeStep, new ArrayList<>());
			}
			
			// add pedestrian movement data
			this.movementData.get(simulationTimeStep).add(data);
		}
	}

	@Override
	public void callPostProcessing(SimulationState simulationState) {
		// Nothing to do
	}
}
