package tum.cms.sim.momentum.model.tactical.routing.cognitiveRoutingModel;

import tum.cms.sim.momentum.utility.graph.Graph;
import tum.cms.sim.momentum.utility.graph.Vertex;
import tum.cms.sim.momentum.utility.graph.pathAlgorithm.weightOperation.IterativeWeightCalculator;

public class CognitiveRoutingWeightCalculator extends IterativeWeightCalculator {

	@Override
	public void preCalculateWeight(Graph graph, Vertex previousVertex, Vertex current, Vertex target) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double calculateWeight(Graph graph, Vertex previousVertex, Vertex target, Vertex current, Vertex successor) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void updateWeight(Graph graph, double calculatedWeight, Vertex previousVertex, Vertex target, Vertex current,
			Vertex successor) {
		// TODO Auto-generated method stub
		
	}

}
