package tum.cms.sim.momentum.model.operational.walking.neuralNetworkModel.NetworkMlpModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tum.cms.sim.momentum.data.agent.pedestrian.state.operational.WalkingState;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IOperationalPedestrian;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrian;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrianExtension;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.operational.walking.WalkingModel;
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
	
		List<Float> inTensorData = this.assembleInTensor(pedestrian, perception, simulationState);
		
		NeuralNetwork network = this.getNetworkForThread(simulationState.getCalledOnThread());
		NeuralTensor inTensor = NeuralNetworkFactory.createNeuralTensor(this.inputTensor, new long[] {1,inTensorData.size()});

		float[] data = new float[inTensorData.size()];
		
		for(int iter = 0; iter < inTensorData.size(); iter++) {
			
			data[iter] = inTensorData.get(iter);
		}
		
		inTensor.fill(data);
		
		NeuralTensor outTensor = NeuralNetworkFactory.createNeuralTensor(this.outputTensor, new long[] {1,2});
		
		Vector2D predictVelocity = GeometryFactory.createVector(0.0, 0.0);
		
		try {
			
			network.executeNetwork(inTensor, outTensor);
			float[] outData = outTensor.getFloatData();
			double scaleTime = 1.0/simulationState.getTimeStepDuration();
			predictVelocity.set(outData[0] / scaleTime, outData[1] / scaleTime);
		} 
		catch (Exception e) {
			
			e.printStackTrace();
		}
		
		inTensor.close();
		outTensor.close();

		double xNext = pedestrian.getPosition().getXComponent() + predictVelocity.getXComponent();
		double yNext = pedestrian.getPosition().getYComponent() + predictVelocity.getYComponent();

		double headingXNext = (xNext - pedestrian.getPosition().getXComponent());
		double headingYNext = (yNext - pedestrian.getPosition().getYComponent());
		
		WalkingState newWalkingState = new WalkingState(
				GeometryFactory.createVector(xNext, yNext),
				predictVelocity,
				GeometryFactory.createVector(headingXNext, headingYNext).getNormalized());
		
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
	
	private static double freeCode = -0.25;
	private static double groupCode = 0.0;
	private static double pedestrianCode = 0.25;
	private static double obstacleCode = 0.5;
	
	/**
	 * Assembly order: (count 210)
	 * perceptionDistance (list) 51
	 * perceptionVelocityX (list) 102
	 * perceptionVelocityY (list) 153
	 * perceptionType (list) 204
	 * pedestrianVelocityXLast (scalar) 205
	 * pedestrianVelocityYLast (scalar) 206
	 * pedestrianVelocityXLastSec (scalar) 207
	 * pedestrianVelocityYLastSec (scalar) 208
	 * pedestrianWalkingGoalX (scalar) 209
	 * pedestrianWalkingGoalY (scalar) 210
	 * 
	 * @param pedestrian
	 * @param perception
	 * @param simulationState
	 */
	private List<Float> assembleInTensor(IOperationalPedestrian pedestrian, PerceptionalModel perception, SimulationState simulationState) {
		
		List<Float> perceptionDistanceSpace = new ArrayList<Float>();
		List<Float> perceptionVelocityXSpace = new ArrayList<Float>();
		List<Float> perceptionVelocityYSpace = new ArrayList<Float>();
		List<Float> perceptionTypeSpace = new ArrayList<Float>();
		
		Vector2D pedestrianPosition = pedestrian.getPosition();
		
		// this collection is of constant size, null if no obstacle was found
		List<Vector2D> obstaclePositions = perception.getPerceptedObstaclePositions(pedestrian, simulationState);
	
		// this collection is of constant size, null if no pedestrian was found
		List<IPedestrian> pedestrianPositions = perception.getPerceptedPedestrianPositions(pedestrian, simulationState);
		
		// gives the walked distance between to time steps thus it is already in distance/timeStepDuration
		// e.g. 5cm (/0.05sec), now we scale it to seconds = 5*20cm/1s = 1m/1s
		// time scale
		double scaleTime = 1.0/simulationState.getTimeStepDuration();
		double scaleDistance = 1.0/perception.getPerceptionDistance();
				
		for(int iter = 0; iter < obstaclePositions.size(); iter++) {
			
			if(obstaclePositions.get(iter) != null) {
				
				perceptionDistanceSpace.add((float) ((pedestrianPosition.distance(obstaclePositions.get(iter)) 
						- pedestrian.getBodyRadius()) * scaleDistance));
				perceptionVelocityXSpace.add((float) (-pedestrian.getVelocity().getXComponent() * scaleTime));
				perceptionVelocityYSpace.add((float) (-pedestrian.getVelocity().getXComponent() * scaleTime));
				perceptionTypeSpace.add((float) obstacleCode); 
			}
			else if(pedestrianPositions.get(iter) != null) {
				
				IPedestrian other = pedestrianPositions.get(iter);
				perceptionDistanceSpace.add((float) ((pedestrianPosition.distance(pedestrianPositions.get(iter).getPosition()) 
						- pedestrian.getBodyRadius()) * scaleDistance));
				perceptionVelocityXSpace.add((float) ((other.getVelocity().getXComponent() - pedestrian.getVelocity().getXComponent()) * scaleTime));  
				perceptionVelocityYSpace.add((float) ((other.getVelocity().getYComponent() - pedestrian.getVelocity().getYComponent()) * scaleTime));
				perceptionTypeSpace.add((float) (other.getGroupId() == pedestrian.getGroupId() ? groupCode : pedestrianCode));
			}
			else {
			
				perceptionDistanceSpace.add((float) (perception.getPerceptionDistance() * scaleDistance));
				perceptionVelocityXSpace.add((float) 0.0);
				perceptionVelocityYSpace.add((float) 0.0);
				perceptionTypeSpace.add((float) freeCode); 
			}
		}
		
		NetworkMlpPedestrianExtension extension = (NetworkMlpPedestrianExtension) pedestrian.getExtensionState(this);
		extension.setSecondToLastVelocity(extension.getLastVelocity());
		extension.setLastVelocity(pedestrian.getVelocity());
		
		float pedestrianVelocityXLastSec = (float) (extension.getSecondToLastVelocity().getXComponent() * scaleTime);
		float pedestrianVelocityYLastSec = (float) (extension.getSecondToLastVelocity().getYComponent() * scaleTime);
		float pedestrianVelocityXLast = (float) (extension.getLastVelocity().getXComponent() * scaleTime);
		float pedestrianVelocityYLast = (float) (extension.getLastVelocity().getYComponent() * scaleTime);
		
		float towardsGoalX = 0.0f;
		float towardsGoalY = 0.0f;
		
		if(pedestrian.getNextWalkingTarget() != null) {
			
			Vector2D towardsGoal = pedestrian.getNextWalkingTarget().subtract(pedestrian.getPosition()).getNormalized();
			towardsGoalX = (float) towardsGoal.getXComponent();
			towardsGoalY = (float) towardsGoal.getYComponent();
		}
		
		List<Float> inTensorData = new ArrayList<Float>();
		inTensorData.addAll(perceptionDistanceSpace);
		inTensorData.addAll(perceptionVelocityXSpace);
		inTensorData.addAll(perceptionVelocityYSpace);
		inTensorData.addAll(perceptionTypeSpace);
		inTensorData.add(pedestrianVelocityXLast);
		inTensorData.add(pedestrianVelocityYLast);
		inTensorData.add(pedestrianVelocityXLastSec);
		inTensorData.add(pedestrianVelocityYLastSec);
		inTensorData.add(towardsGoalX);
		inTensorData.add(towardsGoalY);
		
		return inTensorData;
	}
}
