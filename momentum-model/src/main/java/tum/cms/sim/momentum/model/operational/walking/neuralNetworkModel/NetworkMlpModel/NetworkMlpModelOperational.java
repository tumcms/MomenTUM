package tum.cms.sim.momentum.model.operational.walking.neuralNetworkModel.NetworkMlpModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.util.FastMath;

import tum.cms.sim.momentum.data.agent.pedestrian.state.operational.WalkingState;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IOperationalPedestrian;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrianExtension;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.operational.walking.WalkingModel;
import tum.cms.sim.momentum.model.operational.walking.csvPlackback.CsvPlaybackPedestrianExtensions.CsvPlaybackPerceptionWriterItem;
import tum.cms.sim.momentum.model.perceptional.PerceptionalModel;
import tum.cms.sim.momentum.utility.geometry.GeometryFactory;
import tum.cms.sim.momentum.utility.geometry.Vector2D;
import tum.cms.sim.momentum.utility.neuralNetwork.NeuralNetwork;
import tum.cms.sim.momentum.utility.neuralNetwork.NeuralNetworkFactory;
import tum.cms.sim.momentum.utility.neuralNetwork.NeuralTensor;

public class NetworkMlpModelOperational extends WalkingModel {

	private static String inputTensorName = "inputTensorName";
	private static String outputTensorName = "outputTensorName";
	private static String pathToModelName = "pathToModel";
	private static String velocityClassesName = "velocityClasses";
	private static String angleClassesName = "angleClasses";
	
	/**
	 * The number of velocity classes for classification
	 */
	private int velocityClasses = 1;
	
	/**
	 * The number of angles (rotate body) classes for classification
	 */
	private int angleClasses = 1;
	
	/**
	 * For each thread there is a network to enable parallel computations.
	 */
	private Map<Integer, NeuralNetwork> networksForThread = new HashMap<>();
	
	/**
	 * For each thread there is an in tensor to enable parallel computations.
	 */
	private Map<Integer, NeuralTensor> inTensorsForThread = new HashMap<>();
	
	/**
	 * For each thread there is an out tensor to enable parallel computations.
	 */
	private Map<Integer, NeuralTensor> outTensorsForThread = new HashMap<>();
	

	NeuralTensor outTensor;
	
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
		
		return new NetworkMlpPedestrianExtension(); // nothing to do
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
	
		float scale = 1.0f; // scale 
		
		NetworkMlpPedestrianExtension extension = (NetworkMlpPedestrianExtension) pedestrian.getExtensionState(this);
		List<Float> inTensorData = this.assembleInTensor(pedestrian, perception, simulationState, extension, scale);
		
//		for(int iter = 0; iter < inTensorData.size(); iter++) {
//			
//			inTensorData.set(iter, 0.5f);
//		}
		
		NeuralNetwork network = this.getNetworkForThread(simulationState.getCalledOnThread());
		NeuralTensor inTensor = this.getInTensorForThread(simulationState.getCalledOnThread(), inTensorData.size());

		float[] data = new float[inTensorData.size()];
		
		for(int iter = 0; iter < inTensorData.size(); iter++) {
			
			data[iter] = inTensorData.get(iter);
		}
		
 		inTensor.fill(data);

		// {1,1} is velocityMagnitude
		// {1,2} is velocityAngleChange
		NeuralTensor outTensor = this.getOutTensorForThread(simulationState.getCalledOnThread(), 2);
		
		double velocityMagnitude = 0.0;
		double velocityAngleChange = 0.0;
		float[] outData = null;

		try {
			
			network.executeNetwork(inTensor, outTensor);
			outData = outTensor.getFloatData();
			
			velocityMagnitude = outData[0]; // (10.0);
			//velocityAngleChange =  (outData[1]/scale) * 2.0*FastMath.PI - FastMath.PI;
			velocityAngleChange = outData[1];//  (FastMath.PI - outData[1]);// * 2.0 * FastMath.PI;
			//velocityAngleChange = velocityAngleChange > FastMath.PI/2.0 || velocityAngleChange < -FastMath.PI/2.0 
			//		? 0.0 :
			//		velocityAngleChange;
		} 
		catch (Exception e) {
			
			e.printStackTrace();
		}
		
		Vector2D predictVelocity = pedestrian.getVelocity()
				.setMagnitude(velocityMagnitude)
				//.scale(velocityMagnitude);
				.rotate(velocityAngleChange);
		
		Vector2D predictVelocityX = pedestrian.getVelocity()
				//.setMagnitude(velocityMagnitude)
				.scale(velocityMagnitude)
				.rotate(velocityAngleChange);
		
		double xNext = pedestrian.getPosition().getXComponent() + predictVelocity.getXComponent();
		double yNext = pedestrian.getPosition().getYComponent() + predictVelocity.getYComponent();

		double headingXNext = (xNext - pedestrian.getPosition().getXComponent());
		double headingYNext = (yNext - pedestrian.getPosition().getYComponent());
		
		Vector2D heading = extension.updateHeadings(GeometryFactory.createVector(headingXNext, headingYNext).getNormalized(),
				20);
		
		WalkingState newWalkingState = new WalkingState(
				GeometryFactory.createVector(xNext, yNext),
				predictVelocity,
				heading);
		
		//extension.updatePedestrianTeach(pedestrian, newWalkingState, simulationState);
		
		pedestrian.setWalkingState(newWalkingState);
	}

	@Override
	public void callPreProcessing(SimulationState simulationState) {
	
		this.path = this.properties.getStringProperty(pathToModelName);
		this.inputTensor = this.properties.getStringProperty(inputTensorName);
		this.outputTensor = this.properties.getStringProperty(outputTensorName);
	}

	@Override
	public void callPostProcessing(SimulationState simulationState) {
		
		this.inTensorsForThread.forEach((id, tensor) -> tensor.close());
		this.inTensorsForThread.clear();
		this.outTensorsForThread.forEach((id, tensor) -> tensor.close());
		this.outTensorsForThread.clear();
		this.networksForThread.forEach((id, network) -> network.close());
		this.networksForThread.clear();
	}

	/**
	 * Add or get a neural network based on the path given in the properties.
	 * This will create a new network for each thread id
	 * @param threadId, id of the current thread
	 * @return {@link NeuralNetwork}
	 */
	private NeuralNetwork getNetworkForThread(int threadId) {
		
		if(!this.networksForThread.containsKey(threadId)) {
			
			this.networksForThread.put(threadId, NeuralNetworkFactory.createNeuralNetwork(this.path));
		}
		
		return this.networksForThread.get(threadId);
	}
	
	/**
	 * Add or get a in tensor based on the target network profile.
	 * This will create a new tensor for each thread id
	 * @param threadId, id of the current thread
	 * @param size, the size of second dimension the tensor 
	 * @return {@link NeuralTensor}
	 */
	private NeuralTensor getInTensorForThread(int threadId, int size) {
		
		if(!this.inTensorsForThread.containsKey(threadId)) {
			
			this.inTensorsForThread.put(threadId, NeuralNetworkFactory.createNeuralTensor(this.inputTensor, new long[] {1, size}));
		}
		
		return this.inTensorsForThread.get(threadId);
	}
	
	/**
	 * Add or get a out tensor based on the target network profile.
	 * This will create a new tensor for each thread id
	 * @param threadId, id of the current thread
	 * @param size, the size of second dimension the tensor 
	 * @return {@link NeuralTensor}
	 */
	private NeuralTensor getOutTensorForThread(int threadId, int size) {
		
		if(!this.outTensorsForThread.containsKey(threadId)) {
			
			this.outTensorsForThread.put(threadId, NeuralNetworkFactory.createNeuralTensor(this.outputTensor, new long[] {1, size}));
		}
		
		return this.outTensorsForThread.get(threadId);
	}

	private List<Float> assembleInTensor(IOperationalPedestrian pedestrian,
			PerceptionalModel perception,
			SimulationState simulationState,
			NetworkMlpPedestrianExtension extension,
			float scale) {
		
		extension.updatePerceptionSpace(pedestrian, this.perception, simulationState);
		extension.updatePedestrianSpace(pedestrian);
		
		List<Float> inTensorData = new ArrayList<Float>();
		
		for(CsvPlaybackPerceptionWriterItem item : extension.getPerceptItems()) {
			
			inTensorData.add((float) item.getDistanceToPercept() * scale);
		}
			
		for(CsvPlaybackPerceptionWriterItem item : extension.getPerceptItems()) {
				
			inTensorData.add((float) item.getAngleToPercept() * scale);
		}
		
		for(CsvPlaybackPerceptionWriterItem item : extension.getPerceptItems()) {
			
			inTensorData.add((float) item.getVelocityMagnitudeOfPercept() * scale);
		}
		
		for(CsvPlaybackPerceptionWriterItem item : extension.getPerceptItems()) {
			
			inTensorData.add((float) item.getVelocityAngleDifferenceToPercept() * scale);
		}
		
//		for(CsvPlaybackPerceptionWriterItem item : extension.getPerceptItems()) {
//			
//			inTensorData.add((float) item.getTypeOfPercept() * scale);
//		}
		
		inTensorData.add(extension.getAngleToGoal().floatValue() * scale);
		inTensorData.add(extension.getLastVelocityMagnitude().floatValue() * scale);
		inTensorData.add(extension.getLastVelocityAngleChange().floatValue() * scale);
//		inTensorData.add(extension.getLastLastVelocityMagnitude().floatValue() * scale);
//		inTensorData.add(extension.getLastLastVelocityAngleChange().floatValue() * scale);
		
		return inTensorData;
	}
}
