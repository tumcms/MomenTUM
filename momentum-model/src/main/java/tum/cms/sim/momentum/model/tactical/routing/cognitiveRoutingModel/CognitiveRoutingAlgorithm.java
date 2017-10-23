package tum.cms.sim.momentum.model.tactical.routing.cognitiveRoutingModel;

import tum.cms.sim.momentum.utility.graph.Graph;
import tum.cms.sim.momentum.utility.graph.Vertex;
import tum.cms.sim.momentum.utility.graph.pathAlgorithm.IterativePathAlgorithm;
import tum.cms.sim.momentum.utility.graph.pathAlgorithm.ShortestPathAlgorithm;
import tum.cms.sim.momentum.utility.graph.pathAlgorithm.selectorOperation.VertexSelector;
import tum.cms.sim.momentum.utility.graph.pathAlgorithm.weightOperation.AStarEuklidWeightCalculator;
import tum.cms.sim.momentum.utility.graph.pathAlgorithm.weightOperation.DirectWeightCalculatur;
import tum.cms.sim.momentum.utility.graph.pathAlgorithm.weightOperation.IterativeWeightCalculator;

public class CognitiveRoutingAlgorithm extends IterativePathAlgorithm {

	public CognitiveRoutingAlgorithm(IterativeWeightCalculator weightCalculator, VertexSelector selector) {
		super(weightCalculator, selector);
		// TODO Auto-generated constructor stub
	}

	
	
	//public void compute
	
//	private  iterativAlgorithm = null;
//	private AStarEuklidWeightCalculator directWeightCalculator = null;
//	private ShortestPathAlgorithm beelineComputation = null;
//	
//	public CognitiveRoutingAlgorithm(DirectWeightCalculatur calculator) {
//
//		this.beelineComputation = new ShortestPathAlgorithm(calculator);
//	}

	
}
