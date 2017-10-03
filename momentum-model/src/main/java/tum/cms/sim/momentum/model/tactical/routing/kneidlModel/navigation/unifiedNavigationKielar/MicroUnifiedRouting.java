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

package tum.cms.sim.momentum.model.tactical.routing.kneidlModel.navigation.unifiedNavigationKielar;

import java.util.Set;

import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;
import tum.cms.sim.momentum.model.tactical.routing.kneidlModel.navigation.MicroRoutingAlgorithm;
import tum.cms.sim.momentum.model.tactical.routing.kneidlModel.navigation.fastestNavigation.MicroFastestPathDijkstra;
import tum.cms.sim.momentum.model.tactical.routing.kneidlModel.navigation.heuristicNavigation.MicroBeelineHeuristic;
import tum.cms.sim.momentum.utility.graph.Graph;
import tum.cms.sim.momentum.utility.graph.Path;
import tum.cms.sim.momentum.utility.graph.Vertex;
import tum.cms.sim.momentum.utility.graph.pathAlgorithm.IterativeShortestPathAlgorithm;
import tum.cms.sim.momentum.utility.graph.pathAlgorithm.selectorOperation.VertexSelector;

public class MicroUnifiedRouting extends MicroRoutingAlgorithm {

	private IterativeShortestPathAlgorithm algorithm = null;
	private MicroBeelineHeuristic beelineAglorithm = null;
	private MicroFastestPathDijkstra fastestPathAlgorithm = null;
	private UnifiedIterativeRoutingCalculator weightCalculator = null;
	
	public MicroUnifiedRouting(MicroBeelineHeuristic beelineAglorithm,
			MicroFastestPathDijkstra fastestPathAlgorithm,
			UnifiedIterativeRoutingCalculator weightCalculator, 
			VertexSelector vertexSelector,
			IRichPedestrian currentPedestrian) {
		
		super(currentPedestrian);
		
		this.beelineAglorithm = beelineAglorithm;
		this.fastestPathAlgorithm = fastestPathAlgorithm;
		this.algorithm = new IterativeShortestPathAlgorithm(weightCalculator, vertexSelector);

		this.weightCalculator = weightCalculator;
		this.weightCalculator.updateCurrentPedestrian(this.currentPedestrian);
	}

	@Override
	public Path route(Graph graph, 
			Set<Vertex> pedestrainVisitiedVertices, 
			Vertex previousVertex, 
			Vertex pedestrianPosition,
			Vertex destination) {
		
		this.weightCalculator.updateCurrentPerception(this.currentPerception);
	
		// first calculate weights for fastest path, 
		Path fastestPath = this.fastestPathAlgorithm.route(graph, 
				pedestrainVisitiedVertices,
				previousVertex,
				pedestrianPosition, 
				destination);
	
		this.weightCalculator.updateCurrentFastestPath(fastestPath);
		
		// second calculate weights for heuristic beeline,
		Path beelinePath = this.beelineAglorithm.route(graph,
				pedestrainVisitiedVertices,
				previousVertex, 
				pedestrianPosition, 
				destination);
		
		this.weightCalculator.updateCurrentBeelinePath(beelinePath);
		
		// finally calculate by iterative routing including sall, greedy beeline and ant optimiziation
		return this.algorithm.calculateNextPath(graph, pedestrainVisitiedVertices, previousVertex, pedestrianPosition, destination);
	}
}
