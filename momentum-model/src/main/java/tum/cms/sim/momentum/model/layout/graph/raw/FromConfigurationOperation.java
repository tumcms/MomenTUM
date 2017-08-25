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

package tum.cms.sim.momentum.model.layout.graph.raw;

import java.util.ArrayList;
import java.util.HashSet;

import org.apache.commons.math3.util.FastMath;
import tum.cms.sim.momentum.configuration.scenario.EdgeConfiguration;
import tum.cms.sim.momentum.configuration.scenario.GraphScenarioConfiguration;
import tum.cms.sim.momentum.configuration.scenario.ScenarioConfiguration;
import tum.cms.sim.momentum.configuration.scenario.VertexConfiguration;
import tum.cms.sim.momentum.data.layout.area.Area;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.layout.graph.GraphOperation;
import tum.cms.sim.momentum.utility.geometry.GeometryFactory;
import tum.cms.sim.momentum.utility.geometry.Vector2D;
import tum.cms.sim.momentum.utility.graph.Edge;
import tum.cms.sim.momentum.utility.graph.Graph;
import tum.cms.sim.momentum.utility.graph.GraphTheoryFactory;
import tum.cms.sim.momentum.utility.graph.Vertex;

public class FromConfigurationOperation extends GraphOperation {

	private static String graphIdName = "graphId";
	private static String precisionSeedName = "precisionSeed";
	
	private ArrayList<ScenarioConfiguration> configurations = null;
	
	public FromConfigurationOperation(ArrayList<ScenarioConfiguration> scenarioConfigurations) {
		
		this.configurations = scenarioConfigurations;
	}

	@Override
	public void callPreProcessing(SimulationState simulationState) {
		
		Integer graphId = this.properties.getIntegerProperty(graphIdName);
		Double precisionSeed = this.properties.getDoubleProperty(precisionSeedName);
		GraphScenarioConfiguration graphConfiguration = null;
		
		for(GraphScenarioConfiguration graphScenarioConfiguration : configurations.get(0).getGraphs()) {
			
			if(graphScenarioConfiguration.getId().intValue() == graphId) {
			
				graphConfiguration = graphScenarioConfiguration;
			}
		}
		
		HashSet<Area> alreadySeedAreas = new HashSet<>();
		
		Graph graph = GraphTheoryFactory.createGraph(graphConfiguration.getName());
		graph.setId(graphConfiguration.getId());
		this.scenarioManager.getGraphs().add(graph);

		for(VertexConfiguration vertexConfiguration : graphConfiguration.getVertices()) {
			
			Vertex newVertex = null;
			
			Vector2D center = GeometryFactory.createVector(vertexConfiguration.getPoint().getX(),
					vertexConfiguration.getPoint().getY());
			
			boolean isSeed = this.scenarioManager.getAreas()
					.stream()
					.filter(area -> FastMath.abs(area.getPointOfInterest().distance(center)) < precisionSeed)
					.count() > 0;	
					
			if(isSeed) {
				
				Area area = this.scenarioManager.getAreas()
						.stream()
						.filter(existingArea -> FastMath.abs(existingArea.getPointOfInterest().distance(center)) < precisionSeed &&
								!alreadySeedAreas.contains(existingArea))
						.findFirst()
						.get();
				
				alreadySeedAreas.add(area);
				
				// select one that was not taken already
				// put into graph factory
				
				newVertex = GraphTheoryFactory.createVertex(area.getGeometry(), vertexConfiguration.getId());
			}
			else {
				
				newVertex = GraphTheoryFactory.createVertexCyleBased(center, vertexConfiguration.getId());
			}
			
			graph.addVertex(newVertex);
		}
		
		for(EdgeConfiguration edgeConfiguration : graphConfiguration.getEdges()) {
			
			Vertex left = graph.getVertex(edgeConfiguration.getIdLeft());
			Vertex right = graph.getVertex(edgeConfiguration.getIdRight());
			
			Edge edge = GraphTheoryFactory.createEdge(left, right);
			graph.conncetVertices(left, right, edge);
		}
	}

	@Override
	public void callPostProcessing(SimulationState simulationState) {
		
		// nothing to do
	}
}
