package tum.cms.sim.momentum.model.generator.generatorTypes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import tum.cms.sim.momentum.configuration.model.output.WriterSourceConfiguration.OutputType;
import tum.cms.sim.momentum.data.agent.pedestrian.state.other.StaticState;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.generator.Generator;
import tum.cms.sim.momentum.utility.geometry.GeometryFactory;

/**
 * This generator model will get a csv data set and will create pedestrians
 * which exists in the csv data set for a given point in time.
 * The input may have a different time scale (1 time step = x seconds).
 * This is be solved by providing the timeStepMapping property for this class.
 * 
 * @author Peter Kielar
 *
 */
public class CsvGenerator extends Generator {

	private static String startTimeName = "startTime";
	private static String endTimeName = "endTime";
	private static String shiftTimeLineName = "shiftTimeLine";
	
	private static String csvInputName = "csvInput";
	private static String csvMappingName = "csvMapping";
	private static String timeStepMappingName = "timeStepMapping";
	private static String containsHeaderName = "containsHeader";
	
	private double shiftTimeLine = 0.0;
	private double timeStepMapping = 0.0;
	private int timeStepIndex = -1;
	private int idIndex = -1;
	private int xIndex = -1;
	private int yIndex = -1;
	
	private double generatorStartTime;
	private double generatorEndTime;
	
	private HashMap<Integer, ArrayList<Double>> csvMapping;
	private HashMap<Long, HashMap<Integer, CsvGeneratorDataObject>> generationSet = new HashMap<>();
	// the next set is used to compute the heading of a pedestrian and the velocity
	private HashMap<Integer, CsvGeneratorDataObject> generationNextSet = new HashMap<>();

	
	@Override
	public void callPreProcessing(SimulationState simulationState) {
		
		generatorStartTime = this.properties.getDoubleProperty(startTimeName);
		generatorEndTime = this.properties.getDoubleProperty(endTimeName);
		
		if(this.properties.getDoubleProperty(shiftTimeLineName) != null) {
			
			shiftTimeLine = this.properties.getDoubleProperty(shiftTimeLineName);
		}
		
		this.timeStepMapping = this.properties.getDoubleProperty(timeStepMappingName);
		List<String> csvInput = this.properties.<String>getListProperty(csvMappingName);
		
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
		
		this.csvMapping = this.properties.<Double>getMatrixProperty(csvInputName);
		
		if(this.properties.getBooleanProperty(containsHeaderName)) {
			this.csvMapping.remove(0);
		}
		
		// compute generation map
		HashSet<Integer> knownIds = new HashSet<>();
		HashSet<Integer> knownNextIds = new HashSet<>();
		
		for(ArrayList<Double> data : csvMapping.values()) {
			
			int id = data.get(idIndex).intValue();
			long dataTimeStep = data.get(timeStepIndex).longValue() + (long)shiftTimeLine;
			
			long simulationTimeStep = simulationState.getScaledTimeStep(dataTimeStep, this.timeStepMapping);
			
			this.generationSet.putIfAbsent(simulationTimeStep, new HashMap<>());
			
			// add pedestrian to be generated in the generation set
			if(!knownIds.contains(id)) {
				
				data.set(timeStepIndex, (double) simulationTimeStep);
				this.generationSet.get(simulationTimeStep).put(id, new CsvGeneratorDataObject(data));
				this.generationNextSet.put(id, new CsvGeneratorDataObject(data));
				knownIds.add(id);
			} 
			else if(!knownNextIds.contains(id)) {
				
				if(simulationTimeStep > (long)this.generationNextSet.get(id).getValue(timeStepIndex)) {
					
					data.set(timeStepIndex, (double) simulationTimeStep);
					this.generationNextSet.put(id, new CsvGeneratorDataObject(data));
					knownNextIds.add(id);
				}
			}
		}
	}

	@Override
	public void execute(Collection<? extends Void> splittTask, SimulationState simulationState) {
		
		if(this.generatorStartTime <= simulationState.getCurrentTime() &&
		   simulationState.getCurrentTime() <= this.generatorEndTime) {
			
			if(this.generationSet.containsKey(simulationState.getCurrentTimeStep())) {
				
				for(Entry<Integer, CsvGeneratorDataObject> data : this.generationSet.get(simulationState.getCurrentTimeStep()).entrySet()) {
					
					int id = data.getKey();
					double x = data.getValue().getValue(xIndex);
					double y = data.getValue().getValue(yIndex);
					
					double velocityX = 0.0;
					double velocityY = 0.0;
					double headingX = 0.0;
					double headingY = 1.0;
					
					// get next state if exists 
					if(this.generationNextSet.containsKey(id)) {
						
						double xNext = this.generationNextSet.get(id).getValue(xIndex);
						double yNext = this.generationNextSet.get(id).getValue(yIndex);	
						
						// compute velocity
						velocityX = (xNext - x);
						velocityY = (yNext - y);
						
						// compute heading
						headingX = (xNext - x);
						headingY = (yNext - y);
					}
				
					StaticState staticState = pedestrianSeed.generateStaticState(-1, this.scenarioManager.getScenarios().getId());
					staticState.setId(id);
					staticState.setGroupId(id);
					pedestrianManager.createPedestrian(staticState, 
							null,
							GeometryFactory.createVector(x, y), 
							GeometryFactory.createVector(headingX, headingY).getNormalized(), 
							GeometryFactory.createVector(velocityX, velocityY), 
							simulationState.getCurrentTime());
				}
				
				this.generationSet.remove(simulationState.getCurrentTimeStep());
			}
		}
	}

	@Override
	public void callPostProcessing(SimulationState simulationState) {
		// Nothing to do
	}
	
	private class CsvGeneratorDataObject {
		
		private ArrayList<Double> dataObject = null;
		
		CsvGeneratorDataObject(ArrayList<Double> dataObject) {
			
			this.dataObject = dataObject;
		}
		
		public double getValue(int index) {
			
			return dataObject.get(index);
		}
	}
}
