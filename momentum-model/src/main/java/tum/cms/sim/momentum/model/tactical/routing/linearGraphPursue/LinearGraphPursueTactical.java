package tum.cms.sim.momentum.model.tactical.routing.linearGraphPursue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;

import org.apache.commons.lang3.tuple.Pair;

import tum.cms.sim.momentum.data.agent.pedestrian.state.tactical.RoutingState;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrianExtension;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;
import tum.cms.sim.momentum.data.agent.pedestrian.types.ITacticalPedestrian;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.tactical.routing.RoutingModel;
import tum.cms.sim.momentum.utility.geometry.Vector2D;
import tum.cms.sim.momentum.utility.geometry.operation.GeometryAdditionals;
import tum.cms.sim.momentum.utility.graph.Vertex;

/**
 * This simple routing model will find the next vertex which is in
 * heading direction
 * far away
 * adjacent to the current vertex if given
 * 
 * @author Peter Kielar
 *
 */
public class LinearGraphPursueTactical extends RoutingModel {

	@Override
	public IPedestrianExtension onPedestrianGeneration(IRichPedestrian pedestrian) {
		// nothing to do
		return null;
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
	public void callPedestrianBehavior(ITacticalPedestrian pedestrian, SimulationState simulationState) {
		
		Vector2D heading = pedestrian.getHeading();
		Vector2D position = pedestrian.getPosition();
		
		Collection<Vertex> potentialNextGoals = null;
		Vertex lastVertex = null;
		
		if(pedestrian.getRoutingState() != null && pedestrian.getRoutingState().getNextVisit() != null) {
			
			potentialNextGoals = this.scenarioManager.getGraph().getSuccessorVertices(
					pedestrian.getRoutingState().getNextVisit());
			
			lastVertex = pedestrian.getRoutingState().getNextVisit();
		}
		else {
			
			potentialNextGoals = this.scenarioManager.getGraph().getVertices();
		}
		
		ArrayList<Pair<Vertex,Double>> visibleVertices = new ArrayList<>();
		
		for(Vertex vertex : potentialNextGoals) {
			
			if(this.perception.isVisible(pedestrian, vertex)) {
			
				visibleVertices.add(Pair.of(vertex,
						GeometryAdditionals.angleBetween0And180(vertex.getGeometry().getCenter(), position, heading)
					));
			}
		}
		
		if(visibleVertices.size() > 0) {
			
			Vertex next = visibleVertices.stream().sorted(Comparator.comparing(Pair::getRight)).findFirst().get().getLeft();
		
			RoutingState routingState = new RoutingState(new HashSet<Vertex>(), null, lastVertex, next);
			pedestrian.setRoutingState(routingState);
		}
		else {
			
			pedestrian.setRoutingState(null);
		}
	}

	@Override
	public void callPreProcessing(SimulationState simulationState) {
		// nothing to do
	}

	@Override
	public void callPostProcessing(SimulationState simulationState) {
		// nothing to do
	}

}
