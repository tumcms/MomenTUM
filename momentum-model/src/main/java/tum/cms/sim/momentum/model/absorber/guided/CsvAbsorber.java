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

/**
 * This absorber model will get a csv data set and will remove all pedestrians
 * which exists in the simulation (based on id) and are not in the csv data set.
 * 
 * @author Peter Kielar
 *
 */
public class CsvAbsorber extends Absorber {

	private static String csvInputName = "csvInput";
	private static String csvMappingName = "csvMapping";
	private static String timeStepMappingName = "timeStepMapping";
	private static String containsHeaderName = "containsHeader";
	
	private double timeStepMapping = 0.0;
	private HashMap<Integer, ArrayList<Double>> csvMapping;
	private HashMap<Long, HashSet<Integer>> absorberSet = new HashMap<>();

	@Override
	public void callPreProcessing(SimulationState simulationState) {
		
		this.timeStepMapping = this.properties.getDoubleProperty(timeStepMappingName);
		List<String> csvInput = this.properties.<String>getListProperty(csvMappingName);
		int timeStepIndex = -1;
		int idIndex = -1;
		for(int iter = 0; iter < csvInput.size(); iter++) {
			
			if(OutputType.valueOf(csvInput.get(iter)) == OutputType.timeStep) {
				
				timeStepIndex = iter;
			}
			
			if(OutputType.valueOf(csvInput.get(iter)) == OutputType.id) {
				
				idIndex = iter;
			}
		}
		
		this.csvMapping = this.properties.<Double>getMatrixProperty(csvInputName);
		
		if(this.properties.getBooleanProperty(containsHeaderName)) {
			this.csvMapping.remove(0);
		}
		
		// ignore the first time step because it is never used
		// the absorber looks ahead a single time step and checks
		// if pedestrian data is given.
		long scipTimeStep = csvMapping.values().stream().findFirst().get().get(timeStepIndex).longValue();

		// compute generation map
		for(ArrayList<Double> data : csvMapping.values()) {

			long dataTimeStep = data.get(timeStepIndex).longValue();
			long simulationTimeStep = simulationState.getScaledTimeStep(dataTimeStep, this.timeStepMapping);
			
			if(scipTimeStep != simulationTimeStep) {
				
				int id = data.get(idIndex).intValue();
				this.absorberSet.putIfAbsent(simulationTimeStep, new HashSet<>());
				this.absorberSet.get(simulationTimeStep).add(id);
			}
		}
	}

	@Override
	public void execute(Collection<? extends Void> splittTask, SimulationState simulationState) {
		
		if(this.absorberSet.isEmpty()) {
			
			this.pedestrianManager.removeAllPedestrians();
		}
		else if(this.absorberSet.containsKey(simulationState.getCurrentTimeStep() + 1)) {
			
			ArrayList<Integer> pedestriansToDelete = new ArrayList<>();
			boolean exists = false;
			for(IPedestrian existingPedestrian : this.pedestrianManager.getAllPedestrians()) {
				
				for(Integer pedestrianID : this.absorberSet.get(simulationState.getCurrentTimeStep() + 1)) {
					
					if(pedestrianID.intValue() == existingPedestrian.getId().intValue()) {
						
						exists = true;
						break;
					}
				}
				
				if(!exists) {
					
					pedestriansToDelete.add(existingPedestrian.getId());
				}
				
				exists = false;
			}
			
			if(pedestriansToDelete.size() > 0) {
				
				this.pedestrianManager.removePedestrians(pedestriansToDelete);
			}
			
			this.absorberSet.remove(simulationState.getCurrentTimeStep() + 1);
		}	
	}

	@Override
	public void callPostProcessing(SimulationState simulationState) {
		// Nothing to do
	}
}
