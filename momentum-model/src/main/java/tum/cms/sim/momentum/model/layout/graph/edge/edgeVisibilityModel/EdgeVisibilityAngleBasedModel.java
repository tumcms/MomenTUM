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

package tum.cms.sim.momentum.model.layout.graph.edge.edgeVisibilityModel;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

import tum.cms.sim.momentum.data.layout.obstacle.Obstacle;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.layout.graph.GraphOperation;
import tum.cms.sim.momentum.utility.geometry.Geometry2D;
import tum.cms.sim.momentum.utility.graph.Graph;
import tum.cms.sim.momentum.utility.graph.Vertex;

/**
 * Instantiated by GraphType <code>EdgeCreateVisibilityAngleBased</code>
 * @param alpha new edges are generated only if they lie in an angle of more than alpha towards the 
 * other edges outgoing from the vertex
 *
 */
public class EdgeVisibilityAngleBasedModel extends GraphOperation {
	
	private static String alphaName = "alpha";
	private static String visibilityToleranceName = "visibilityTolerance";
	
	@Override
	public void callPreProcessing(SimulationState simulationState) {
		
		double visibilityTolerance = this.properties.getDoubleProperty(visibilityToleranceName);
		
		Collection<Geometry2D> blockingGeometries = this.scenarioManager.getObstacles().stream()
				.map(Obstacle::getGeometry).collect(Collectors.toList());	
		
		Graph graph = this.scenarioManager.getGraph();
		double alpha = this.properties.getDoubleProperty(alphaName) / 2.0;
		
		HashSet<Vertex> knownSeedVertices = new HashSet<Vertex>();
		this.scenarioManager.getAreas().stream().forEach(area -> {
			knownSeedVertices.add(this.scenarioManager.getGraph().getGeometryVertex(area.getGeometry()));
		});
		
		VisibilityEdgeConnectAlgorithm edgeConnector = new VisibilityEdgeConnectAlgorithm();
		
		try {
			
			edgeConnector.connectVertices(blockingGeometries, graph, knownSeedVertices, alpha, visibilityTolerance);
		}
		catch (Exception e) {

			e.printStackTrace();
		}
	}

	@Override
	public void callPostProcessing(SimulationState simulationState) {
		// nothing to do
		
	}

}
