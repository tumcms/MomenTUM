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

/**
 * Works only with ShadowPerceptionModel
 * @author ga37sib
 *
 */
public class CsvPlaybackOperational extends WalkingModel {

	private static String csvInputName = "csvInput";
	private static String csvMappingName = "csvMapping";
	private static String timeStepMappingName = "timeStepMapping";
	private static String containsHeaderName = "containsHeader";
	
	private double timeStepMapping = 0.0;
	private int timeStepIndex = -1;
	private int idIndex = -1;
	private int xIndex = -1;
	private int yIndex = -1;
	
	private HashMap<Long, ArrayList<ArrayList<String>>> movementData = new HashMap<>();
	private HashMap<Integer, ArrayList<String>> nextMovementData = new HashMap<>();
	
	@Override
	public IPedestrianExtension onPedestrianGeneration(IRichPedestrian pedestrian) {

		return new CsvPlaybackPedestrianExtensions();
	}

	@Override
	public void onPedestrianRemoval(IRichPedestrian pedestrian) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void callBeforeBehavior(SimulationState simulationState, Collection<IRichPedestrian> pedestrians) {
		
		// Read the next time step
		ArrayList<ArrayList<String>> currentData = movementData.get(simulationState.getCurrentTimeStep() + 1);
		currentData.stream().forEach(dataSet -> nextMovementData.put(Integer.parseInt(dataSet.get(idIndex)), dataSet));
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
		
		ArrayList<String> pedestrianDataSet = nextMovementData.get(pedestrian.getId());
		
		double x = pedestrian.getPosition().getXComponent();
		double y = pedestrian.getPosition().getYComponent();

		double xNext = Double.parseDouble(pedestrianDataSet.get(xIndex));
		double yNext = Double.parseDouble(pedestrianDataSet.get(yIndex));	
		
		double velocityXNext = (xNext - x) * simulationState.getTimeStepDuration();
		double velocityYNext = (yNext - y) * simulationState.getTimeStepDuration();
		double headingXNext = (xNext - x);
		double headingYNext = (yNext - y);
		
		WalkingState newWalkingState = new WalkingState(
				GeometryFactory.createVector(xNext, yNext),
				GeometryFactory.createVector(velocityXNext, velocityYNext),
				GeometryFactory.createVector(headingXNext, headingYNext));
	
		extension.updatePerceptionSpace(pedestrian, this.perception, simulationState);
		extension.updatePedestrianSpace(pedestrian, newWalkingState);
		
		pedestrian.setWalkingState(newWalkingState);
	}

	@Override
	public void callPreProcessing(SimulationState simulationState) {
		
		this.timeStepMapping = this.properties.getDoubleProperty(timeStepMappingName);
		List<String> csvInput = this.properties.<String>getListProperty(csvInputName);
		
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
		
		HashMap<Integer, ArrayList<String>> csvMapping = this.properties.<String>getMatrixProperty(csvMappingName);
		
		if(this.properties.getBooleanProperty(containsHeaderName)) {
			csvMapping.remove(0);
		}
		
		for(ArrayList<String> data : csvMapping.values()) {
			
			long dataTimeStep = Long.parseLong(data.get(timeStepIndex));
			long simulationTimeStep = simulationState.getScaledTimeStep(dataTimeStep, this.timeStepMapping);
			
			// add time step			
			this.movementData.putIfAbsent(simulationTimeStep, new ArrayList<>());
			// add pedestrian movement data
			this.movementData.get(simulationTimeStep).add(data);
		}
	}

	@Override
	public void callPostProcessing(SimulationState simulationState) {
		// Nothing to do
	}
}
