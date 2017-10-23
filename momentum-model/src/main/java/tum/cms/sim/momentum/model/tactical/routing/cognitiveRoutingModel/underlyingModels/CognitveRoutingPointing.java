package tum.cms.sim.momentum.model.tactical.routing.cognitiveRoutingModel.underlyingModels;

import java.util.ArrayList;

import tum.cms.sim.momentum.data.agent.pedestrian.types.ITacticalPedestrian;
import tum.cms.sim.momentum.data.layout.ScenarioManager;
import tum.cms.sim.momentum.model.tactical.routing.cognitiveRoutingModel.CognitiveRoutingExtension;
import tum.cms.sim.momentum.utility.graph.Vertex;

public class CognitveRoutingPointing {

	public ArrayList<Vertex> targetVerticesInPointingRange(ScenarioManager scenarioManager,
			ITacticalPedestrian pedestrian,
			CognitiveRoutingExtension extension) {
		
		ArrayList<Vertex> targetVertices = new ArrayList<>();
		
		// compute angle range (pointing distribution, graph, position)
		
		// compute cone depth (look ahead distribution, graph, position) 
		
		return targetVertices;
	}
}
