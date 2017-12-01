package tum.cms.sim.momentum.model.operational.walking.neuralNetworkModel.NetworkMlpModel;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tum.cms.sim.momentum.data.agent.pedestrian.types.IOperationalPedestrian;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrianExtension;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.operational.walking.WalkingModel;
import tum.cms.sim.momentum.utility.neuralNetwork.NeuralNetwork;
import tum.cms.sim.momentum.utility.neuralNetwork.NeuralNetworkFactory;
import tum.cms.sim.momentum.utility.neuralNetwork.NeuralTensor;

public class NetworkMlpModelOperational extends WalkingModel {

	private static String inputTensorName = "inputTensorName";
	private static String outputTensorName = "outputTensorName";
	private static String pathToModelName = "pathToModel";
	
	/**
	 * For each thread there is a network to enable parallel computations.
	 */
	private Map<Integer, NeuralNetwork> network = new HashMap<>();
	
	/**
	 * The path to folder of the stored tensorflow network.
	 */
	private String path;
	
	/**
	 * Name of the input tensor of the model.
	 * Thus, the name of the input placeholder.
	 */
	private String inputTensor;
	
	/**
	 * Name of the output tensor of the model.
	 * Thus, the name of the prediction.
	 */
	private String outputTensor;
	
	
	@Override
	public IPedestrianExtension onPedestrianGeneration(IRichPedestrian pedestrian) {
		
		return null; // nothing to do
	}

	@Override
	public void onPedestrianRemoval(IRichPedestrian pedestrian) {
		// nothing to do
	}

	@Override
	public void callBeforeBehavior(SimulationState simulationState, Collection<IRichPedestrian> pedestrians) {
		// nothing to do
	}

	@Override
	public void callAfterBehavior(SimulationState simulationState, Collection<IRichPedestrian> pedestrians) {
		// nothing to do
	}

	@Override
	public void callPedestrianBehavior(IOperationalPedestrian pedestrian, SimulationState simulationState) {
		
		NeuralNetwork network = this.getNetworkForThread(simulationState.getCalledOnThread());
		
	}

	@Override
	public void callPreProcessing(SimulationState simulationState) {
	
		this.path = this.properties.getStringProperty(pathToModelName);
		this.inputTensor = this.properties.getStringProperty(inputTensorName);
		this.outputTensor = this.properties.getStringProperty(outputTensorName);
		
//		String filePath = "C:\\Programmierung\\MomenTUM\\momentum-users\\PhDThesis\\Kielar\\NN_2017\\2017_NN_Operational_testing";
//		String inputTensorName = "Placeholder";
//		String outputTensorName = "Identity";
		
		for(int iter = 0; iter < simulationState.getNumberOfThreads(); iter++) {
			
		}
		String filePath 
		
		 NeuralNetworkFactory.createNeuralNetwork(filePath);
		
		NeuralTensor inTensor = NeuralNetworkFactory.createNeuralTensor(inputTensorName, new long[] {1,210});
		float[] data = new float[210];
		for(int iter = 0; iter < 210; iter++) {
			
			data[iter] = (float)(iter/10.0);
		}
		
		inTensor.fill(data);
		
		NeuralTensor outTensor = NeuralNetworkFactory.createNeuralTensor("Identity", new long[] {1,2});
		
		float[] outData = null;
		
		try {
			
			network.executeNetwork(inTensor, outTensor);
			
			outData = outTensor.getFloatData();
			
		} 
		catch (Exception e) {
			
			e.printStackTrace();
		}
		
		outData = null;
	}

	@Override
	public void callPostProcessing(SimulationState simulationState) {
		// nothing to do
	}

	/**
	 * Add or get a neural network based on the path given in the properties.
	 * This will create a new network for each thread id
	 * @param threadId, id of the current thread
	 * @return {@link NeuralNetwork}
	 */
	private NeuralNetwork getNetworkForThread(int threadId) {
		
		this.network.putIfAbsent(threadId, NeuralNetworkFactory.createNeuralNetwork(this.path));
		
		return this.network.get(threadId);
	}
}
