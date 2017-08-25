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

package tum.cms.sim.momentum.model.layout.graph.edge.edgeCreateOvermarsModel;

import java.util.Collection;
import java.util.stream.Collectors;

import tum.cms.sim.momentum.data.layout.obstacle.Obstacle;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.layout.graph.GraphOperation;
import tum.cms.sim.momentum.utility.geometry.Geometry2D;
import tum.cms.sim.momentum.utility.geometry.operation.GeometryAdditionals;
import tum.cms.sim.momentum.utility.graph.Graph;
import tum.cms.sim.momentum.utility.graph.Path;
import tum.cms.sim.momentum.utility.graph.Vertex;
import tum.cms.sim.momentum.utility.graph.pathAlgorithm.ShortestPathAlgorithm;
import tum.cms.sim.momentum.utility.graph.pathAlgorithm.weightOperation.DijkstraWeightCalculator;

/**
 * Instantiated by GraphType <code>EdgeCreateOvermarsUseful</code><br>
 * @param detourFactor if the path between two vertices along the current graph is by <code>
 * detourFactor</code> longer than the direct connection, the direct connection is added as a 
 * new edge to the graph
 *
 */
public class EdgeCreateOvermarsModel extends GraphOperation {
	
	private static String visibilityToleranceName = "visibilityTolerance";
	private static String detourFactorName = "detourFactor";
	
	@Override
	public void callPreProcessing(SimulationState simulationState) {
		
		double visibilityTolerance = this.properties.getDoubleProperty(visibilityToleranceName);
		
		Graph graph = this.scenarioManager.getGraph();
		Collection<Geometry2D> obstacleGeometries = this.scenarioManager.getObstacles().stream().map(Obstacle::getGeometry).collect(Collectors.toList());
		double K = this.properties.getDoubleProperty(detourFactorName);
		
		boolean inSight = false;
		double directCost = 0;
		double graphCost = 0;
		
		
		ShortestPathAlgorithm dijkstraAlgorithm = new ShortestPathAlgorithm(new DijkstraWeightCalculator("OvermarsWeight", null));
		
		for(Vertex start : graph.getVertices()) {
			
			for(Vertex end : graph.getVertices()) {
				
				//continue, if start and end are the same
				if(start.equals(end)) {
					
					continue;
				}
				
				//continue, if there is no direct path between start and end 
				inSight = GeometryAdditionals.calculateIntersection(obstacleGeometries, 
						start.getGeometry().getCenter(), 
						end.getGeometry().getCenter(),
						visibilityTolerance);
				
				if(!inSight) {
					
					continue;
				}
				
				//check, if the new edge is useful
				graph.getVertices().forEach(vertex -> vertex.setWeight("OvermarsWeight", Double.MAX_VALUE));
				graph.getAllEdges().forEach(edge -> edge.setWeight("OvermarsWeight", edge.getStart().euklidDistanceBetweenVertex(edge.getEnd())));
				Path path = dijkstraAlgorithm.calculateShortestPath(graph, start, end);
				
				directCost = start.euklidDistanceBetweenVertex(end);
				graphCost = path.distance();
				
				if(K * directCost < graphCost) {
					
		    		graph.doublyConnectVertices(start, end);
				}
			}
		}
		
	}

	@Override
	public void callPostProcessing(SimulationState simulationState) {
		// nothing to do
		
	}

}
