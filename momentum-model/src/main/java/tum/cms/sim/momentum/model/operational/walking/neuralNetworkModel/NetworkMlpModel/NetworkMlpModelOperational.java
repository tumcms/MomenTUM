package tum.cms.sim.momentum.model.operational.walking.neuralNetworkModel.NetworkMlpModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private static String dropoutTensorName = "dropoutTensorName";
	private static String outputTensorName = "outputTensorName";
	private static String pathToClassificationNetworkName = "pathToClassificationNetwork";
	private static String velocityClassesName = "velocityClasses";
	private static String angleClassesName = "angleClasses";
	private static String numberOfLastCategoriesName = "numberOfLastCategories";
	private static String numberForMeanName = "numberForMean";
	
	private int numberForMean = 5;
	private int numberOfLastCategories = 2;
	
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
	private  Map<Integer, NeuralNetwork> networksForThread = new HashMap<>();
	
	/**
	 * For each thread there is an in tensor to enable parallel computations.
	 */
	private  Map<Integer, NeuralTensor> inTensorsForThread = new HashMap<>();
	
	/**
	 * For each thread there is an out tensor to enable parallel computations.
	 */
	private Map<Integer, NeuralTensor> outTensorsForThread = new HashMap<>();
	
	/**
	 * For each thread there is an dropout tensor to enable parallel computations.
	 */
	private Map<Integer, NeuralTensor> dropoutTensorsForThread = new HashMap<>();
	
	/**
	 * The path to folder of the stored tensorflow network.
	 */
	private String pathToClassificationNetwork;

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
	
	/**
	 * Name of the dropout tensor of the model.
	 * Thus, the name of the operation to deactivate neurons.
	 */
	private String dropoutTensor;
	
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

		NetworkMlpPedestrianExtension extension = (NetworkMlpPedestrianExtension) pedestrian.getExtensionState(this);
		
		List<Double> inTensorData = this.assembleInTensor(pedestrian, perception, simulationState, extension);
		
		double[] predictedClasses = this.estimate(inTensorData, simulationState);
		
		int predictedClass = extension.findClassIdByMaxValue(predictedClasses);

		// In Tensorflow we use this to compute the class:
		// categorieBoth = (catVelo - 1) * veloCategory + (angleCategory - 1)
		
		// Thus, the first dimension is velocity and can be computed via / and ceil
		// E.g.a) joint class is 45, velocity classes are 12 -> 3.75 -> 4.0 velocity class
		// E.g.b) joint class is 5, velocity classes are 12 -> 0.42 -> 1.0 velocity class
		int predictedVelocityClass = (int) Math.ceil((double)predictedClass / (double)velocityClasses);
		
		// E.g.a) 12 * floor(3.75) = 36 and 12 * ceil(3.75) = 48 
		// E.g.b) 12 * floor(0.42) = 0 and 12 * ceil(1.0) = 12 
		// This means that the range for the angle class is
		// a) 36 - 48, that gives (4 velocity class) * 12 velocity classes - 36 joint class = 9 angle class
		// b) 0 - 12, that gives angle class (1 velocity class ) * 12 velocity classes - 5 joint class = 5 angle class
		int predictedAngleClass = (predictedVelocityClass) * velocityClasses - (int)predictedClass;
				
		double predictedVelocity = extension.getVelocityForClassification(predictedVelocityClass,
				this.velocityClasses,
				pedestrian,
				simulationState);
		
		//predictedVelocity *= 0.25;
		
		double  predictedAngle = extension.getAngleForClassification(predictedAngleClass,
				this.angleClasses);		
		
		//predictedAngle *= 0.25;
		
		Vector2D predictVelocity = pedestrian.getVelocity()
				.setMagnitude(predictedVelocity)
				.rotate(predictedAngle);
		
		double xNext = pedestrian.getPosition().getXComponent() + predictVelocity.getXComponent();
		double yNext = pedestrian.getPosition().getYComponent() + predictVelocity.getYComponent();

		double headingXNext = (xNext - pedestrian.getPosition().getXComponent());
		double headingYNext = (yNext - pedestrian.getPosition().getYComponent());
		
		Vector2D heading = extension.updateHeadings(GeometryFactory.createVector(headingXNext, headingYNext).getNormalized(),
				numberOfLastCategories);
		
		WalkingState newWalkingState = new WalkingState(
				GeometryFactory.createVector(xNext, yNext),
				predictVelocity,
				heading);
		
		extension.updatePedestrianTeach(pedestrian, newWalkingState, simulationState, velocityClasses, angleClasses, numberOfLastCategories);
		
		pedestrian.setWalkingState(newWalkingState);
	}

	private double[] estimate(List<Double> inTensorData , SimulationState simulationState) {

		NeuralNetwork network = this.getNetworkForThread(simulationState.getCalledOnThread());
		NeuralTensor inTensor = this.getInTensorForThread(simulationState.getCalledOnThread(), inTensorData.size());
		NeuralTensor dropoutTensor = this.getDropoutTensorForThread(simulationState.getCalledOnThread());

		double[] data = new double[inTensorData.size()];
		
		for(int iter = 0; iter < inTensorData.size(); iter++) {
			
			data[iter] = inTensorData.get(iter);
		}
		
  		inTensor.fill(data);

 		List<NeuralTensor> inTensors = new ArrayList<NeuralTensor>();
 		// {1, size} tensor
 		inTensors.add(inTensor);
 		// {1, 1} tensor
 		inTensors.add(dropoutTensor);
 		
		// {1,class} is prediction
		NeuralTensor outTensor = //NeuralNetworkFactory.createNeuralTensor(this.outputTensor, new long[] {1, angleClasses * velocityClasses});
			this.getOutTensorForThread(simulationState.getCalledOnThread());
		
		double[] predictedClasses = null;

		try {
			
			network.executeNetwork(inTensors, outTensor);
			predictedClasses = outTensor.getDoubleData();
		} 
		catch (Exception e) {
			
			e.printStackTrace();
		}
		
		//inTensor.close();
		//dropoutTensor.close();
		//outTensor.close();
		
		return predictedClasses;
	}

	private void plotForMatlab(double[] predictedClasses) {
		
		int iter = 0;
		
		System.out.print("z = [");
		for(int veloClass = 0; veloClass < velocityClasses; veloClass++) {
			
			for(int angleClass = 0; angleClass < angleClasses; angleClass++) {
				
				System.out.print(predictedClasses[iter]);
				iter++;
				
				if(angleClass + 1 < angleClasses) {
					
					System.out.print(",");
				}
			}
			
			if(veloClass + 1 < velocityClasses) {
			
				System.out.print(";");
			}
		}
		System.out.print("];");
		
		System.out.println();
		System.out.print("y = [");
		for(int veloClass = 0; veloClass < velocityClasses; veloClass++) {
			
			for(int angleClass = 0; angleClass < angleClasses; angleClass++) {
				
				System.out.print(1 + veloClass);
				iter++;
				
				if(angleClass + 1 < angleClasses) {
					
					System.out.print(",");
				}
			}
			
			if(veloClass + 1 < velocityClasses) {
			
				System.out.print(";");
			}
		}
		System.out.print("];");
		System.out.println();
		System.out.print("x = [");
		for(int veloClass = 0; veloClass < velocityClasses; veloClass++) {
			
			for(int angleClass = 0; angleClass < angleClasses; angleClass++) {
				
				System.out.print(1 + angleClass);
				iter++;
				
				if(angleClass + 1 < angleClasses) {
					
					System.out.print(",");
				}
			}
			
			if(veloClass + 1 < velocityClasses) {
			
				System.out.print(";");
			}
		}
		System.out.print("];");
	}
	@Override
	public void callPreProcessing(SimulationState simulationState) {
	
		this.pathToClassificationNetwork = this.properties.getStringProperty(pathToClassificationNetworkName);
		this.inputTensor = this.properties.getStringProperty(inputTensorName);
		this.dropoutTensor = this.properties.getStringProperty(dropoutTensorName);
		this.outputTensor = this.properties.getStringProperty(outputTensorName);
		this.velocityClasses = this.properties.getIntegerProperty(velocityClassesName);
		this.angleClasses = this.properties.getIntegerProperty(angleClassesName);
		this.numberOfLastCategories = this.properties.getIntegerProperty(numberOfLastCategoriesName);
		this.numberForMean = this.properties.getIntegerProperty(numberForMeanName);
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

			this.networksForThread.put(threadId, NeuralNetworkFactory.createNeuralNetwork(this.pathToClassificationNetwork));
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
	 * @return {@link NeuralTensor}
	 */
	private NeuralTensor getOutTensorForThread(int threadId) {

		if(!this.outTensorsForThread.containsKey(threadId)) {
			
			this.outTensorsForThread.put(threadId,
					NeuralNetworkFactory.createNeuralTensor(this.outputTensor, new long[] {1, angleClasses * velocityClasses}));
		}
		
		return this.outTensorsForThread.get(threadId);
	}
	
	/**
	 * Add or get a dropout tensor based on the target network profile.
	 * This will create a new tensor for each thread id
	 * @param threadId, id of the current thread
	 * @return {@link NeuralTensor}
	 */
	private NeuralTensor getDropoutTensorForThread(int threadId) {

		if(!this.dropoutTensorsForThread.containsKey(threadId)) {
			
			this.dropoutTensorsForThread.put(threadId,
					NeuralNetworkFactory.createNeuralTensor(this.dropoutTensor, new long[] {1, 1}));
			
			this.dropoutTensorsForThread.get(threadId).fill(new double[] { 1.0 });
		}
		
		return this.dropoutTensorsForThread.get(threadId);
	}
	
	private List<Double> assembleInTensor(IOperationalPedestrian pedestrian,
			PerceptionalModel perception,
			SimulationState simulationState,
			NetworkMlpPedestrianExtension extension) {

		extension.initializeLastTeach(pedestrian,
				simulationState,
				pedestrian.getVelocity().getMagnitude(),
				0.0,
				velocityClasses,
				angleClasses,
				numberOfLastCategories);
		
		extension.updatePedestrianSpace(pedestrian,simulationState, velocityClasses, angleClasses, numberOfLastCategories);
		extension.updatePerceptionSpace(pedestrian, this.perception, simulationState);
		
		List<Double> inTensorData = new ArrayList<Double>();
		
		for(CsvPlaybackPerceptionWriterItem item : extension.getPerceptItems()) {
			
			inTensorData.add(item.getDistanceToPercept());
		}
			
		for(CsvPlaybackPerceptionWriterItem item : extension.getPerceptItems()) {
				
			inTensorData.add(item.getAngleToPercept());
		}
		
		for(CsvPlaybackPerceptionWriterItem item : extension.getPerceptItems()) {
			
			inTensorData.add(item.getVelocityMagnitudeOfPercept());
		}
		
		for(CsvPlaybackPerceptionWriterItem item : extension.getPerceptItems()) {
			
			inTensorData.add(item.getVelocityAngleDifferenceToPercept());
		}
		
//		for(CsvPlaybackPerceptionWriterItem item : extension.getPerceptItems()) {
//			
//			inTensorData.add((float) item.getTypeOfPercept() * scale);
//		}
		
		inTensorData.add(extension.getAngleToGoal());
		
		for(int last = 1; last >= 0; last--) {
			
			inTensorData.add(extension.getLastVelocityMagnitudeCategories().get(last) - 1.0);
			inTensorData.add(extension.getLastVelocityAngleCategories().get(last) - 1.0);
		}

		return inTensorData;
	}
}
