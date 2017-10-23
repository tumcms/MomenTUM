package tum.cms.sim.momentum.model.tactical.routing.cognitiveRoutingModel.underlyingModels;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.math3.util.FastMath;

import tum.cms.sim.momentum.configuration.model.lattice.LatticeModelConfiguration.LatticeType;
import tum.cms.sim.momentum.configuration.model.lattice.LatticeModelConfiguration.NeighbourhoodType;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;
import tum.cms.sim.momentum.data.layout.ScenarioManager;
import tum.cms.sim.momentum.model.layout.lattice.LatticeModel;
import tum.cms.sim.momentum.model.support.perceptional.PerceptionalModel;
import tum.cms.sim.momentum.model.tactical.routing.cognitiveRoutingModel.CognitiveRoutingConstants;
import tum.cms.sim.momentum.model.tactical.routing.cognitiveRoutingModel.CognitiveRoutingExtension;
import tum.cms.sim.momentum.utility.graph.Vertex;
import tum.cms.sim.momentum.utility.lattice.CellIndex;
import tum.cms.sim.momentum.utility.lattice.ILattice;
import tum.cms.sim.momentum.utility.lattice.Lattice.Occupation;
import tum.cms.sim.momentum.utility.lattice.LatticeTheoryFactory;
import tum.cms.sim.momentum.utility.probability.distrubution.IDistribution;

public class CognitiveRoutingLearning {

	
	//private HashMap<Double>
	private double depthMapAccuray = 5.0;
	private ILattice depthMapLattice = null;

	/**
	 * Creates a normalized depth map of the environment for later use.
	 * @param scenarioManager
	 */
	public void computeDepthMap(ScenarioManager scenarioManager) {
		
		depthMapLattice = LatticeTheoryFactory.createLattice(
				"depthMapLattice",
				LatticeType.Quadratic,
				NeighbourhoodType.Edge,
				depthMapAccuray,
				scenarioManager.getScenarios().getMaxX(), 
				scenarioManager.getScenarios().getMinX(),
				scenarioManager.getScenarios().getMaxY(),
				scenarioManager.getScenarios().getMinY());
				
		LatticeModel.fillLatticeForObstacles(depthMapLattice, scenarioManager.getScenarios());
		
		List<CellIndex> connectedCells =  depthMapLattice.getAllCellsWith(Occupation.Empty);
		
		connectedCells.parallelStream()
			.forEach(start -> connectedCells.forEach(end -> {
				
				if (depthMapLattice.breshamLineCast(start, end, Integer.MAX_VALUE)) {
					depthMapLattice.increaseCellNumberValue(start, 1.0);
				}
			}));
		
		double max = 0.0;
		
		for(CellIndex cell : connectedCells) {
			
			max = FastMath.max(depthMapLattice.getCellNumberValue(cell), max);
		}
		
		for(CellIndex cell : connectedCells) {
			
			depthMapLattice.setCellNumberValue(cell, depthMapLattice.getCellNumberValue(cell) / max);
		}
	}
	
	public Set<Vertex> findNewlyLearnedVertices(IRichPedestrian pedestrian,
			CognitiveRoutingExtension pedestrianExtension,
			PerceptionalModel perception,
			ScenarioManager scenarioManager) {
		
		List<Vertex> closeVertices = scenarioManager.getGraph().findVertexInDistance(
				pedestrian.getPosition(), 
				pedestrianExtension.getCurrentLearningDistance(),
				new HashSet<Vertex>());
		
		Collection<Vertex> closeByVertex = scenarioManager.getGraph().getSuccessorVertices(
				pedestrian.getRoutingState().getLastVisit());
		
		HashSet<Vertex> learnedVertices = new HashSet<>();
		learnedVertices.addAll(closeByVertex);
		
		closeVertices.stream().forEach(closeVertex -> {
			
			if(perception.isVisible(pedestrian.getPosition(), closeVertex)) {
				
				learnedVertices.add(closeVertex);
			}
		});
		
		return learnedVertices;
	}
	
//	/** 
//	 * Here, t
//	 * @param scenarioManager
//	 * @param perception
//	 */
//	public void computeOriginPerception(ScenarioManager scenarioManager, PerceptionalModel perception) {
//		
//		scenarioManager.getOrigins().parallelStream().forEach(origin -> {
//			
//			scenarioManager.getGraph().getVertices().stream().forEach(vertex -> {
//				
//				if(perception.isVisible(origin.getGeometry().getCenter(), vertex)) {
//					
//					if(!originPerceptionMap.containsKey(origin.getId().intValue())) {
//						
//						originPerceptionMap.put(origin.getId().intValue(), new ArrayList<Vertex>());
//					}
//					
//					originPerceptionMap.get(origin.getId().intValue()).add(vertex);
//				}
//			});
//		});
//	}
	
	/**
	 * Uses the depthMap and computes the initially known vertices of an agent.
	 * Furthermore this function adds the weights for an agent for all known
	 * vertices.
	 * 
	 * @param scenarioManager
	 * @param pedestrianId
	 * @param pedestrianExtension
	 * @param initialKnowledgDistribution
	 * @return A HashSet that stores all Ids of the known vertices
	 */
	public HashSet<Vertex> calculateInitialMemory(ScenarioManager scenarioManager,
			IDistribution initialKnowledgDistribution) {
		
		// this value is 0 < x limit 1
		double initialKnolwedge = initialKnowledgDistribution.getSample();
		HashSet<Vertex> knownVertices = new HashSet<>();
		
		for(Vertex vertex : scenarioManager.getGraph().getVertices()) {
				
			CellIndex cell = depthMapLattice.getCellIndexFromPosition(vertex.getGeometry().getCenter());
			
			if(depthMapLattice.getCellNumberValue(cell) >= initialKnolwedge) {
				
				knownVertices.add(vertex);
			}
		}
		
		return knownVertices;
	}
	
	public void updatePedestrianVertexWeights(ScenarioManager scenarioManager,
			int pedestrianId,
			Collection<Vertex> knownVertices) {
		
		for(Vertex knownVertex : knownVertices) {
			
			knownVertex.setWeight(this.getKnowledgeWeight(pedestrianId), 1.0);
		}
	}
	
	/**
	 * Removes all weights of an agent in the graph.
	 * Use onPedestrianDelete
	 * 
	 * @param scenarioManager
	 * @param pedestrianId
	 */
	public void clearMemory(ScenarioManager scenarioManager, int pedestrianId) {
		
		scenarioManager.getGraph().getVertices().stream()
			.forEach(vertex ->
				vertex.removeWeight(this.getKnowledgeWeight(pedestrianId)));
	}
	
	/**
	 * Shortcut for getting the weight name for a pedestrian.
	 * 
	 * @param pedestrianId
	 * @return
	 */
	private String getKnowledgeWeight(int pedestrianId) {
		
		return CognitiveRoutingConstants.vertexKnownGraphWeight + "." + String.valueOf(pedestrianId);
	}
}
