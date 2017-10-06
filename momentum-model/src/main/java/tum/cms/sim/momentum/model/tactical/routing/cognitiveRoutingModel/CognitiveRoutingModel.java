package tum.cms.sim.momentum.model.tactical.routing.cognitiveRoutingModel;

//import java.util.Collection;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrianExtansion;
//import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;
//import tum.cms.sim.momentum.data.agent.pedestrian.types.ITacticalPedestrian;
//import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
//import tum.cms.sim.momentum.model.tactical.routing.RoutingModel;
//import tum.cms.sim.momentum.model.tactical.routing.cognitiveRoutingModel.underlyingModels.CognitiveRoutingDirectCalculator;
//import tum.cms.sim.momentum.model.tactical.routing.cognitiveRoutingModel.underlyingModels.CognitiveRoutingLearning;
//import tum.cms.sim.momentum.model.tactical.routing.unifiedRoutingModel.UnifiedRoutingAlgorithm;
//import tum.cms.sim.momentum.utility.graph.Vertex;

public class CognitiveRoutingModel { //extends RoutingModel {

//	private CognitiveRoutingLearning cognitiveRoutingLearning = null;
//	private CognitiveRoutingParameter cognitiveRoutingParameter = null;
//	private HashMap<Integer, CognitiveRoutingAlgorithm> cognitiveRoutingAlgorithms = null;
//	
//	@Override
//	public IPedestrianExtansion onPedestrianGeneration(IRichPedestrian pedestrian) {
//		
//		CognitiveRoutingExtension pedestrianExtension = new CognitiveRoutingExtension();
//		
//		HashSet<Vertex> knownVertices = this.cognitiveRoutingLearning.calculateInitialMemory(
//				this.scenarioManager,
//				this.cognitiveRoutingParameter.getInitialKnowledgDistribution());
//		
//		this.cognitiveRoutingLearning.updatePedestrianVertexWeights(
//				this.scenarioManager, 
//				pedestrian.getId(),
//				knownVertices);
//		
//		pedestrianExtension.addKnownVertexIds(
//				knownVertices,
//				this.scenarioManager.getGraph().getVertexCount());
//		
//		// TODO set initial level of the parameters of the agent
//		
//		return pedestrianExtension;
//	}
//
//	@Override
//	public void onPedestrianRemoval(IRichPedestrian pedestrian) {
//		
//		this.cognitiveRoutingLearning.clearMemory(this.scenarioManager, pedestrian.getId());
//	}
//
//	@Override
//	public void callBeforeBehavior(SimulationState simulationState, Collection<IRichPedestrian> pedestrians) {
//		// Nothing to do
//	}
//
//	@Override
//	public void callPedestrianBehavior(ITacticalPedestrian pedestrian, SimulationState simulationState) {
//		
//		// start /end
//		// route
//		
//		// 
//	}
//
//	@Override
//	public void callAfterBehavior(SimulationState simulationState, Collection<IRichPedestrian> pedestrians) {
//		
// 		pedestrians.parallelStream().forEach(pedestrian -> {
//			
//			if(pedestrian.getRoutingState() != null) {
//				
//				CognitiveRoutingExtension pedestrianExtension = (CognitiveRoutingExtension)pedestrian.getExtensionState(this);
//				
//				Set<Vertex> newlyLearnedVertices = this.cognitiveRoutingLearning.findNewlyLearnedVertices(
//						pedestrian,
//						pedestrianExtension,
//						this.perception,
//						this.scenarioManager);
//
//				this.cognitiveRoutingLearning.updatePedestrianVertexWeights(
//						this.scenarioManager, 
//						pedestrian.getId(),
//						newlyLearnedVertices);
//				
//				pedestrianExtension.addKnownVertexIds(
//						newlyLearnedVertices,
//						this.scenarioManager.getGraph().getVertexCount());
//				
//				// TODO update initial level of the parameters of the agent
//			}
//		});
//	}
//
//	@Override
//	public void callPreProcessing(SimulationState simulationState) {
//
//		cognitiveRoutingAlgorithms = new HashMap<>();
//		cognitiveRoutingLearning = new CognitiveRoutingLearning();
//		cognitiveRoutingLearning.computeDepthMap(this.scenarioManager);
//		
//		cognitiveRoutingParameter = new CognitiveRoutingParameter();
//		
//		cognitiveRoutingParameter.setInitialKnowledgDistribution(this.properties);
//		cognitiveRoutingParameter.setLearingDistanceDistribution(this.properties);
//		cognitiveRoutingParameter.setLookAheadDistribution(this.properties);
//		cognitiveRoutingParameter.setNodeReachedDistribution(this.properties);
//		cognitiveRoutingParameter.setPointingAccuracyDistribution(this.properties);
//	}
//
//	@Override
//	public void callPostProcessing(SimulationState simulationState) {
//		// Nothing to do
//	}
//	
//	private synchronized CognitiveRoutingAlgorithm getAlgorithm(SimulationState simulationState) {
//		
//		int threadNumber = simulationState.getCalledOnThread();
//		
//		if(!this.cognitiveRoutingAlgorithms.containsKey(threadNumber)) {
//			
////			CognitiveRoutingDirectCalculator directCalculator = new CognitiveRoutingDirectCalculator();
////			this.cognitiveRoutingAlgorithms.put(threadNumber, new CognitiveRoutingAlgorithm());
//		}
//		
//		return this.cognitiveRoutingAlgorithms.get(threadNumber);
//	}
}
