/*******************************************************************************
 * Welcome to the pedestrian simulation framework MomenTUM. 
 * This file belongs to the MomenTUM version 2.0.2.
 * 
 * This software was developed under the lead of Dr. Peter M. Kielar at the
 * Chair of Computational Modeling and Simulation at the Technical University Munich.
 * 
 * All rights reserved. Copyright (C) 2017.
 * 
 * Contact: peter.kielar@tum.de, https://www.cms.bgu.tum.de/en/
 * 
 * Permission is hereby granted, free of charge, to use and/or copy this software
 * for non-commercial research and education purposes if the authors of this
 * software and their research papers are properly cited.
 * For citation information visit:
 * https://www.cms.bgu.tum.de/en/31-forschung/projekte/456-momentum
 * 
 * However, further rights are not granted.
 * If you need another license or specific rights, contact us!
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/

package tum.cms.sim.momentum.model.tactical.routing.kneidlModel;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrianExtension;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;
import tum.cms.sim.momentum.data.agent.pedestrian.types.ITacticalPedestrian;
import tum.cms.sim.momentum.data.layout.area.Area;
import tum.cms.sim.momentum.data.layout.obstacle.Obstacle;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.tactical.routing.RoutingModel;
import tum.cms.sim.momentum.model.tactical.routing.kneidlModel.KneidlConstant.KneidlNavigationType;
import tum.cms.sim.momentum.model.tactical.routing.kneidlModel.navigation.MicroRoutingAlgorithm;
import tum.cms.sim.momentum.model.tactical.routing.kneidlModel.navigation.fastestNavigation.*;
import tum.cms.sim.momentum.model.tactical.routing.unifiedRoutingModel.landmarks.LandmarkGraphManipulation;
import tum.cms.sim.momentum.utility.geometry.Geometry2D;
import tum.cms.sim.momentum.utility.graph.Graph;
import tum.cms.sim.momentum.utility.graph.Path;
import tum.cms.sim.momentum.utility.graph.Vertex;
import tum.cms.sim.momentum.utility.probability.ProbabilitySet;

public class KneidTacticalModel extends RoutingModel {

	private static final String kneidlNavigationTypeName = "navigation";

	private MacroFastestPathDijkstra macroFastPathAlgorithm = null;
	//private MacroAntGbSall macroAntGbSallAlgorithm = null;
	
	private ProbabilitySet<KneidlNavigationType> navigationTypeProbabilitySpace = new ProbabilitySet<KneidlNavigationType>();
	private Graph visibilityGraph = null;
	
	private boolean landmarkRouting = false;

	@Override
	public void callPreProcessing(SimulationState simulationState) {
		
		// routing algorithm create (for generic purposes not for pedestrians)
		macroFastPathAlgorithm = new MacroFastestPathDijkstra();
		//macroAntGbSallAlgorithm = new MacroAntGbSall();
		
		// routing probability init
		
		this.appendRoutingProbability(KneidlNavigationType.FastestEuklid, navigationTypeProbabilitySpace);
		this.appendRoutingProbability(KneidlNavigationType.BeelineHeuristic, navigationTypeProbabilitySpace);
		this.appendRoutingProbability(KneidlNavigationType.GreedyBeelineHeuristic, navigationTypeProbabilitySpace);
		this.appendRoutingProbability(KneidlNavigationType.StraightAndLongLegs, navigationTypeProbabilitySpace);
		
		// graph init	
		this.visibilityGraph = this.scenarioManager.getGraph();
	
		// Graph generic weights init 
		this.initializeGenerically(this.visibilityGraph);
	}

	@Override
	public void callPedestrianBehavior(ITacticalPedestrian pedestrian, SimulationState simulationState) {
	
		KneidlPedestrianExtension extension = (KneidlPedestrianExtension)pedestrian.getExtensionState(this);
		extension.setCurrentPerception(perception);
		
		Graph graph = this.scenarioManager.getGraph();
		Vertex start = this.findNavigationStartPoint(pedestrian, this.perception, this.scenarioManager);
		Vertex end = this.scenarioManager.getGraph().getGeometryVertex(pedestrian.getNextNavigationTarget().getGeometry());		
		
		Path route = this.navigate(simulationState, graph, pedestrian, start, end, extension);
		
		pedestrian.setRoutingState(this.updateRouteState(pedestrian, route));
	}

	private Path navigate(SimulationState simulationState, 
			Graph graph, 
			ITacticalPedestrian pedestrian, 
			Vertex start,
			Vertex end, 
			KneidlPedestrianExtension extension) {

		// use the algorithm of the ped for routing, this is threadsave
		MicroRoutingAlgorithm routingAlgorithm = extension.getRoutingAlgorithm();

		//this.removeWeightsForPedestrian(simulationState, graph, pedestrian, extension);
		this.initializeWeightsForPedestrian(simulationState, graph, pedestrian, extension);
		
		Vertex previousVisit = null;
		Set<Vertex> visited = null;
		
		if(pedestrian.getRoutingState() != null) {
		
			previousVisit = pedestrian.getRoutingState().getLastVisit();
			visited = pedestrian.getRoutingState().getVisited();
		}
		
		Path path = routingAlgorithm.route(graph, 
				visited, 
				previousVisit, 
				start, 
				end);

		return path;
	}

	@Override
	public void callBeforeBehavior(SimulationState simulationState, Collection<IRichPedestrian> pedestrians) {

		// Update Travel Times
		 macroFastPathAlgorithm.updateAllTravelTimes(this.visibilityGraph, pedestrians);
	}
	
	@Override
	public void callAfterBehavior(SimulationState simulationState, Collection<IRichPedestrian> pedestrians) {
		
		// nothing to do
	}

	@Override
	public void callPostProcessing(SimulationState simulationState) { /* nothing to do */ }
		
	@Override
	public IPedestrianExtension onPedestrianGeneration(IRichPedestrian pedestrian) {
		
		KneidlNavigationType navigationType = navigationTypeProbabilitySpace.getItemEquallyDistributed();
	
		KneidlPedestrianExtension extension = new KneidlPedestrianExtension(pedestrian.getId().toString(), pedestrian);

		extension.selectNavigationBehavior(navigationType); 
		
		return extension;
	}

	@Override
	public void onPedestrianRemoval(IRichPedestrian pedestrian) { /* nothing to do */ }
	
	private void appendRoutingProbability(KneidlNavigationType routingAlgorithm, ProbabilitySet<KneidlNavigationType> probabilitySet) {
		
		double probability = this.properties.getDoubleProperty(KneidTacticalModel.kneidlNavigationTypeName + routingAlgorithm.name());
		probabilitySet.append(routingAlgorithm, probability);
	}
	
	private void initializeGenerically(Graph graph) {

		macroFastPathAlgorithm.initializeWeightsForGraph(graph);
		//macroAntGbSallAlgorithm.initializeWeightsForGraph(graph);
		
		if(this.landmarkRouting) {
		
			List<Geometry2D> blockingGeometries = this.scenarioManager.getObstacles()
					.stream()
					.map(Obstacle::getGeometry)
					.collect(Collectors.toList());
			
			List<Area> areas = this.scenarioManager.getAreas();
			
			LandmarkGraphManipulation landmarkBasedRouting = new LandmarkGraphManipulation();
			landmarkBasedRouting.landmarkManipulation(graph, blockingGeometries, areas);
		}
	}

	private void initializeWeightsForPedestrian(SimulationState simulationState, Graph graph, ITacticalPedestrian pedestrian, KneidlPedestrianExtension extension) {

		extension.updateWeightName(simulationState.getCalledOnThread());
		
		Vertex currentTarget = graph.getGeometryVertex(pedestrian.getNextNavigationTarget().getGeometry());
		extension.getAllRoutignAlgorithm().forEach(routingAlgorithm -> 
			routingAlgorithm.initializePedestrianWeightsForTarget(graph, currentTarget));
	}
}
