package tum.cms.sim.momentum.model.absorber.guided;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import tum.cms.sim.momentum.configuration.model.output.WriterSourceConfiguration.OutputType;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrian;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.absorber.Absorber;

public class CsvAbsorber extends Absorber {

	private static String csvInputName = "csvInput";
	private static String csvMappingName = "csvMapping";
	private static String timeStepMappingName = "timeStepMapping";
	private static String containsHeaderName = "containsHeader";
	
	private double timeStepMapping = 0.0;
	private int timeStepIndex = -1;
	private int idIndex = -1;
	private int xIndex = -1;
	private int yIndex = -1;
 
	private HashMap<Integer, ArrayList<String>> csvMapping;
	private HashMap<Long, HashSet<Integer>> absorberSet = new HashMap<>();

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
		
		this.csvMapping = this.properties.<String>getMatrixProperty(csvMappingName);
		
		if(this.properties.getBooleanProperty(containsHeaderName)) {
			this.csvMapping.remove(0);
		}
		
		// compute generation map
		for(ArrayList<String> data : csvMapping.values()) {
			
			int id = Integer.parseInt(data.get(idIndex));
			long dataTimeStep = Long.parseLong(data.get(timeStepIndex));
			
			long simulationTimeStep = simulationState.getScaledTimeStep(dataTimeStep, this.timeStepMapping);
			
			this.absorberSet.putIfAbsent(simulationTimeStep, new HashSet<>());
			this.absorberSet.get(simulationTimeStep).add(id);
		}
	}

	@Override
	public void execute(Collection<? extends Void> splittTask, SimulationState simulationState) {
		
		if(this.absorberSet.containsKey(simulationState.getCurrentTimeStep() + 1)) {
			
			ArrayList<Integer> pedestriansToDelete = new ArrayList<>();
			boolean exists = false;
			for(IPedestrian existingPedestrian : this.pedestrianManager.getAllPedestrians()) {
				
				for(Integer pedestrianID : this.absorberSet.get(simulationState.getCurrentTimeStep())) {
					
					if(pedestrianID.equals(existingPedestrian.getId())) {
						
						exists = true;
					}
				}
				
				if(!exists) {
					
					pedestriansToDelete.add(existingPedestrian.getId());
				}
			}
			
			if(pedestriansToDelete.size() > 0) {
				
				this.pedestrianManager.removePedestrians(pedestriansToDelete);
			}
		}
	}

	@Override
	public void callPostProcessing(SimulationState simulationState) {
		// Nothing to do
	}
	
	private class CsvGeneratorDataObject {
		
		private ArrayList<String> dataObject = null;
		
		CsvGeneratorDataObject(ArrayList<String> dataObject) {
			
			this.dataObject = dataObject;
		}
		
		public int getInteger(int index) {
			
			return Integer.parseInt(dataObject.get(index));
		}
		
		public double getDouble(int index) {
			
			return Double.parseDouble(dataObject.get(index));
		}
	}
}
