package tum.cms.sim.momentum.model.tactical.routing.linearGraphPursue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import tum.cms.sim.momentum.data.agent.pedestrian.state.tactical.RoutingState;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrianExtension;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;
import tum.cms.sim.momentum.data.agent.pedestrian.types.ITacticalPedestrian;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.tactical.routing.RoutingModel;
import tum.cms.sim.momentum.utility.geometry.Vector2D;
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
		
		//Vector2D heading = pedestrian.getHeading();
		Vector2D position = pedestrian.getPosition();
		
		ArrayList<Vertex> potentialNextGoals = new ArrayList<>(this.scenarioManager.getGraph().getVertices());
		Vertex lastVertex = null;
		Vertex nextToLastVertex = null;
		Set<Vertex> visited = new HashSet<Vertex>();
		
		if(pedestrian.getRoutingState() != null && pedestrian.getRoutingState().getNextVisit() != null) {
			
			lastVertex = pedestrian.getRoutingState().getNextVisit();
			nextToLastVertex = pedestrian.getRoutingState().getLastVisit();
			visited.addAll(pedestrian.getRoutingState().getVisited());
			potentialNextGoals.removeAll(pedestrian.getRoutingState().getVisited());
			potentialNextGoals.remove(pedestrian.getRoutingState().getNextVisit());
		}
		
		ArrayList<Pair<Vertex,Double>> toCheckVertices = new ArrayList<>();
		
 		for(Vertex vertex : potentialNextGoals) {
			
			if(this.perception.isVisible(pedestrian, vertex)) {
			
				toCheckVertices.add(Pair.of(vertex, vertex.getGeometry().getCenter().distance(position)));
			}
		}

		RoutingState newRoutingState = null;
		
		if(pedestrian.getRoutingState() != null &&
		   toCheckVertices.size() == 0 &&
		   pedestrian.getRoutingState().getNextVisit() != null) {
			
			// in case nothing is visible we seek for the next vertex from the current
			Vertex nextVisit = pedestrian.getRoutingState().getNextVisit();
			
			try {
				for(Vertex neighbor : scenarioManager.getGraph().getSuccessorVertices(nextVisit)) {
					
					toCheckVertices.add(Pair.of(neighbor, neighbor.getGeometry().getCenter().distance(position)));
				}
			}
			catch(Exception ex) {
				ex = null;
			}
			
			if(toCheckVertices.size() > 0 && pedestrian.getRoutingState() != null){ 
				
				List<Pair<Vertex, Double>> nextSorted = toCheckVertices.stream()
						.sorted(Comparator.comparing(Pair::getRight))
						.collect(Collectors.toList());
				
				Collections.reverse(nextSorted);
				Vertex next = nextSorted.get(0).getLeft();
<<<<<<< HEAD
				newRoutingState = new RoutingState(visited, nextToLastVertex , lastVertex, next);
=======
				newRoutingState = new RoutingState(new LinkedHashSet<Vertex>(), nextToLastVertex , lastVertex, next);
>>>>>>> master
			}
		}
		else {

			if(toCheckVertices.size() == 0) {
				
  				for(Vertex vertex : potentialNextGoals) {
					
					toCheckVertices.add(Pair.of(vertex, vertex.getGeometry().getCenter().distance(position)));
				}
			}
			
			List<Pair<Vertex, Double>> nextSorted = toCheckVertices.stream()
					.sorted(Comparator.comparing(Pair::getRight))
					.collect(Collectors.toList());

			Vertex next = nextSorted.get(0).getLeft();
		
			newRoutingState = new RoutingState(visited, nextToLastVertex , lastVertex, next);
		}

		pedestrian.setRoutingState(newRoutingState);
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
