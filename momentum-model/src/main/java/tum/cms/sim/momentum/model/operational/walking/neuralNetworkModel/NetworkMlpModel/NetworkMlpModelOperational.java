package tum.cms.sim.momentum.model.operational.walking.neuralNetworkModel.NetworkMlpModel;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import tum.cms.sim.momentum.data.agent.pedestrian.state.operational.WalkingState;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IOperationalPedestrian;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrianExtension;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.operational.walking.WalkingModel;
import tum.cms.sim.momentum.model.operational.walking.csvPlackback.CsvPlaybackPedestrianExtensions;
import tum.cms.sim.momentum.model.operational.walking.csvPlackback.CsvPlaybackPedestrianExtensions.CsvPlaybackDataItem;
import tum.cms.sim.momentum.model.operational.walking.csvPlackback.CsvPlaybackPedestrianExtensions.CsvPlaybackPerceptionItem;
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
	private static String trainedTimeStepName = "trainedTimeStep";
	private static String angleScalingName = "angleScaling";
	private static String keepProbabilityName = "keepProbability";
	private static String distancePerceiveScalingName = "distancePerceiveScaling";
	private static String anglePerceiveScalingName = "anglePerceiveScaling";
	private static String distanceGoalScalingName = "distanceGoalScaling";
	private static String angleGoalScalingName = "angleGoalScaling";
	private static String lastVelocityScalingName = "lastVelocityScaling";
	private static String lastAngleScalingName = "lastAngleScaling";
	private static String ignoreClassesListName = "ignoreClassesPairs";
	
	/**
	 * This contains a set of items with index that define missing training classes.
	 * This is done to reduce the output tensor size.
	 */
	private int[] ignoreClasses = null;
	
	/**
	 * The keep probability value controls the dropout. Typically dropout is not used
	 * in a productive environment. However, in pedestrian context some randomness
	 * seems to improve the variability of movement. A keep probability value of 0.8
	 * will give the change of 20% dropout.
	 */
	private double keepProbability = 1.0;
	
	/**
	 * These scaling factors are used to scale in input values for the neural network
	 * to be in the same dimensions as the network itself. This values are the maximum
	 * of the training data for each training data dimension.
	 */
	private double distancePerceiveScaling = 1.0;
	private double anglePerceiveScaling = 1.0;
	private double distanceGoalScaling = 1.0;
	private double angleGoalScaling = 1.0;
	private double lastVelocityScaling = 1.0;
	private double lastAngleScaling = 1.0;
	
	/**
	 * Used to further reduce the rotation of an agent by configuration
	 */
	private double angleScaling = 1.0;
	/**
	 * Defines the time step duration in the training phase.
	 * If none is given, we use the current time step.
	 * This scenario is typical.
	 */
	private double trainedTimeStep = 0.0;
	
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
	
	/**
	 * This scale helps to change the input and output of the network
	 * in case a timestep is used for which the network was not trained for.
	 */
	double multiplicator = 1.0;
	
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
	
	private int[] createIgnores(ArrayList<Pair<Integer, Integer>> ignoredRangeofClasses) {
		
		ArrayList<Integer> misses = new ArrayList<>();
	
		for(Pair<Integer,Integer> ignoreClassRange : ignoredRangeofClasses) {
			for(int iter = ignoreClassRange.getLeft(); iter <= ignoreClassRange.getRight(); iter++) {
				misses.add(iter);
			}
		}
	
		int[] missingClasses = new int[misses.size()];
		for(int iter = 0; iter < missingClasses.length; iter++) {
			missingClasses[iter] = misses.get(iter);
		}
		
		return missingClasses;
	}
	
	@Override
	public void callPedestrianBehavior(IOperationalPedestrian pedestrian, SimulationState simulationState) {

		if(trainedTimeStep != simulationState.getTimeStepDuration()) {
			
			 multiplicator =  trainedTimeStep / simulationState.getTimeStepDuration();
		}
		
		NetworkMlpPedestrianExtension extension = (NetworkMlpPedestrianExtension) pedestrian.getExtensionState(this);
		
		List<Double> inTensorData = this.assembleInTensor(pedestrian, perception, simulationState, extension);
		
		int outClasses = velocityClasses * angleClasses - this.ignoreClasses.length;
	
		double[] predictedClasses = this.estimate(inTensorData, simulationState, outClasses);
		
		int predictedClass = extension.findClassIdByMaxValue(predictedClasses);

		if(this.ignoreClasses.length > 0) {

			for(int iter = 0; iter < this.ignoreClasses.length; iter++) {
				
				if(predictedClass >= this.ignoreClasses[iter]) {
					
					predictedClass++;
				}
			}
		}
		
		// This is how the joint class is computed in the Tensorflow code
		// categorieBoth = veloCategory * catAngle - (catAngle - angleCategory)
        // classAngle = ((categorieBoth - 1) % catAngle) + 1
        // classVelo = int((categorieBoth - 1) / catAngle) + 1
		
		int predictedAngleClass = (predictedClass % this.angleClasses) + 1;
		int predictedVelocityClass = (int)(predictedClass / this.angleClasses) + 1;
		
		double predictedVelocityNorm = extension.getNormForClassId(predictedVelocityClass, this.velocityClasses);
		predictedVelocityNorm = new BigDecimal(predictedVelocityNorm).round(new MathContext(2)).doubleValue();
		double predictedVelocity = extension.denormVelo(predictedVelocityNorm,
				pedestrian.getMaximalVelocity(),
				this.trainedTimeStep) 
				* 1/multiplicator;
		
		double predictedAngleNorm = extension.getNormForClassId(predictedAngleClass, this.angleClasses);	
		predictedAngleNorm = new BigDecimal(predictedAngleNorm).round(new MathContext(2)).doubleValue();
		double predictedAngle = extension.denormAngle(predictedAngleNorm) * 1/multiplicator * angleScaling;
	
//		System.out.println("ped: " +pedestrian.getId()
//				+ ", class: " + predictedClass 
//				+ ", veloClass: " + String.valueOf(predictedVelocityClass) + " velo:" + String.valueOf(predictedVelocity)
//				+ ", angleClass: " + String.valueOf(predictedAngleClass) + " angle:" + String.valueOf(predictedAngle));

		Vector2D newVelocity = null;
		
		newVelocity = pedestrian.getHeading().copy().setMagnitude(predictedVelocity).rotate(predictedAngle);
	
		double xNext = pedestrian.getPosition().getXComponent() + newVelocity.getXComponent();
		double yNext = pedestrian.getPosition().getYComponent() + newVelocity.getYComponent();
		
		WalkingState newWalkingState = new WalkingState(
				GeometryFactory.createVector(xNext, yNext),
				newVelocity,
				newVelocity.copy().getNormalized());
		
		extension.computeMovementTeaching(pedestrian,
				newWalkingState,
				simulationState,
				predictedVelocityClass,
				predictedAngleClass,
				1.0, 1.0);
		
		pedestrian.setWalkingState(newWalkingState);
	}

	private double[] estimate(List<Double> inTensorData , SimulationState simulationState, int outClasses) {

		NeuralNetwork network = this.getNetworkForThread(simulationState.getCalledOnThread());
		
		NeuralTensor inTensor = NeuralNetworkFactory.createNeuralTensor(this.inputTensor, new long[] {1, inTensorData.size()});
		NeuralTensor dropoutTensor = NeuralNetworkFactory.createNeuralTensor(this.dropoutTensor, new long[] {1});
		

		double[] data = new double[inTensorData.size()];
		
		for(int iter = 0; iter < inTensorData.size(); iter++) {
			
			data[iter] = inTensorData.get(iter);
		}
		
		dropoutTensor.fill(new double[] { this.keepProbability });
  		inTensor.fill(data);

 		List<NeuralTensor> inTensors = new ArrayList<NeuralTensor>();
 		// {1, size} tensor
 		inTensors.add(inTensor);
 		// {1, 1} tensor
 		inTensors.add(dropoutTensor);
 		NeuralTensor outTensor = null;
		
		// {1,class} is prediction
 		outTensor = NeuralNetworkFactory.createNeuralTensor(this.outputTensor, new long[] {1, outClasses});
		
		double[] predictedClasses = null;

		try {
			
			network.executeNetwork(inTensors, outTensor);
			predictedClasses = outTensor.getDoubleData();
			
		} 
		catch (Exception e) {
			
			e.printStackTrace();
		}
		
		inTensor.close();
		dropoutTensor.close();
		outTensor.close();
		
		return predictedClasses;
	}

	@Override
	public void callPreProcessing(SimulationState simulationState) {
	
		this.pathToClassificationNetwork = this.properties.getStringProperty(pathToClassificationNetworkName);
		this.inputTensor = this.properties.getStringProperty(inputTensorName);
		this.dropoutTensor = this.properties.getStringProperty(dropoutTensorName);
		this.outputTensor = this.properties.getStringProperty(outputTensorName);
		this.velocityClasses = this.properties.getIntegerProperty(velocityClassesName);
		this.angleClasses = this.properties.getIntegerProperty(angleClassesName);
		
		if(this.properties.getDoubleProperty(angleScalingName) != null) {
			
			this.angleScaling = this.properties.getDoubleProperty(angleScalingName);
		}
				
		if(this.properties.getDoubleProperty(distancePerceiveScalingName) != null) {
					
			this.distancePerceiveScaling = this.properties.getDoubleProperty(distancePerceiveScalingName);
		}
		
		if(this.properties.getDoubleProperty(anglePerceiveScalingName) != null) {
			
			this.anglePerceiveScaling = this.properties.getDoubleProperty(anglePerceiveScalingName);
		}
		
		if(this.properties.getDoubleProperty(distanceGoalScalingName) != null) {
			
			this.distanceGoalScaling = this.properties.getDoubleProperty(distanceGoalScalingName);
		}
		
		if(this.properties.getDoubleProperty(angleGoalScalingName) != null) {
			
			this.angleGoalScaling = this.properties.getDoubleProperty(angleGoalScalingName);
		}
		
		if(this.properties.getDoubleProperty(lastVelocityScalingName) != null) {
			
			this.lastVelocityScaling = this.properties.getDoubleProperty(lastVelocityScalingName);
		}
		
		if(this.properties.getDoubleProperty(lastAngleScalingName) != null) {
			
			this.lastAngleScaling = this.properties.getDoubleProperty(lastAngleScalingName);
		}
		
		if(this.properties.getDoubleProperty(keepProbabilityName) != null) {
			
			this.keepProbability = this.properties.getDoubleProperty(keepProbabilityName);
		}
		
		
		if(this.properties.getListProperty(ignoreClassesListName) != null) {
			
			ArrayList<Integer> ignoreClassesProperty = this.properties.getListProperty(ignoreClassesListName);
			ArrayList<Pair<Integer,Integer>> ignoredRangeofClasses = new ArrayList<Pair<Integer,Integer>>();
			for(int iter = 0; iter < ignoreClassesProperty.size(); iter += 2) {
				
				ignoredRangeofClasses.add(new ImmutablePair<Integer, Integer>(ignoreClassesProperty.get(iter),
						ignoreClassesProperty.get(iter+1)));
			}
			
			this.ignoreClasses = this.createIgnores(ignoredRangeofClasses);
		}
		
		CsvPlaybackPedestrianExtensions.setCountItems(this.properties.getIntegerProperty("perceptionCount"));
		
		if(this.properties.getDoubleProperty(trainedTimeStepName) != null) {
			
			this.trainedTimeStep = this.properties.getDoubleProperty(trainedTimeStepName);
		}
		else {
			
			this.trainedTimeStep = simulationState.getTimeStepDuration();
		}
	}

	@Override
	public void callPostProcessing(SimulationState simulationState) {
		
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
	
	private List<Double> assembleInTensor(IOperationalPedestrian pedestrian,
			PerceptionalModel perception,
			SimulationState simulationState,
			NetworkMlpPedestrianExtension extension) {

		if(!extension.isMemoryReady()) {
			
			for(int iter = 0; iter < CsvPlaybackPedestrianExtensions.getMemorySize(); iter++) {
				extension.addDefaultMemoryItem(pedestrian, perception, simulationState, velocityClasses, angleClasses);
			}
		}
		else {
			
			extension.updateTrainingData(pedestrian, perception, simulationState);
		}
		
		List<Double> inTensorData = new ArrayList<Double>();

		CsvPlaybackDataItem currentItem = null;
		for(int iter = 0; iter < 2; iter++) {
			
			if(currentItem == null) {
				currentItem = extension.getCurrent();
			}
			else {
				currentItem = extension.getLast();
			}
				
			for(CsvPlaybackPerceptionItem perceptionItem : currentItem.getPerceptionItems()) {
				
				inTensorData.add(perceptionItem.getDistanceToPercept() / this.distancePerceiveScaling);
				inTensorData.add(perceptionItem.getAngleToPercept() / this.anglePerceiveScaling);
			}
		
//				inTensorData.add(item.getVelocityMagnitudeOfPercept());
//				inTensorData.add(item.getVelocityAngleDifferenceToPercept());
//				inTensorData.add(item.getTypeOfPercept());
			inTensorData.add(currentItem.getGoalItem().getAngleToGoal() / this.angleGoalScaling);		
			inTensorData.add(currentItem.getGoalItem().getDistanceToGoal() / this.distanceGoalScaling);
			inTensorData.add(currentItem.getMovementItem().getVelocityNormValue() / this.lastVelocityScaling);
			inTensorData.add(currentItem.getMovementItem().getAngleNormValue() / this.lastAngleScaling);
		}
	
		return inTensorData;
	}
}
