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

package tum.cms.sim.momentum.model.layout.graph.vertex.vertexIntersectionModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.layout.graph.GraphOperation;
import tum.cms.sim.momentum.utility.geometry.GeometryFactory;
import tum.cms.sim.momentum.utility.geometry.Segment2D;
import tum.cms.sim.momentum.utility.geometry.Vector2D;
import tum.cms.sim.momentum.utility.graph.Edge;
import tum.cms.sim.momentum.utility.graph.GraphTheoryFactory;
import tum.cms.sim.momentum.utility.graph.Vertex;

/**
 * Instantiated by GraphType <code>VertexCreateAtIntersections</code>
 * @param precision the minimum distance between two vertices
 *
 */
public class VertexIntersectionModel extends GraphOperation {
	
	//private static String precisionName = "precision";
	
	@Override
	public void callPreProcessing(SimulationState simulationState) {
	
		//double precision = this.properties.getDoubleProperty(precisionName);
		List<Edge> edgeList = this.scenarioManager.getGraph().getAllEdges();
		
		ArrayList<Vertex> edgeToIntersections = new ArrayList<>();
		HashSet<String> checkedPair = new HashSet<String>(); 
		
		for(Edge currentEdge : edgeList) {
			
			edgeToIntersections.add(currentEdge.getStart());
			edgeToIntersections.add(currentEdge.getEnd());
		}
		
		for(Edge currentEdge : edgeList) {
			
			Segment2D currentSegment = GeometryFactory.createSegment(
					currentEdge.getStart().getGeometry().getCenter(), 
					currentEdge.getEnd().getGeometry().getCenter());

			for(Edge observedEdge : edgeList) {
				
				if(checkedPair.contains(observedEdge.getStart().getName() + "_" + observedEdge.getEnd().getName()) ||
				   checkedPair.contains(observedEdge.getEnd().getName() + "_" + observedEdge.getStart().getName())) {

					continue;
				}

				Segment2D observedSegment = GeometryFactory.createSegment(
						observedEdge.getStart().getGeometry().getCenter(), 
						observedEdge.getEnd().getGeometry().getCenter());
				
				ArrayList<Vector2D> intersections = currentSegment.getIntersection(observedSegment);
				
				if(intersections.size() > 0) {
					
					ArrayList<Vertex> intersectionsVertices = new ArrayList<>();

					intersections.forEach(intersection -> 
					intersectionsVertices.add(GraphTheoryFactory.createVertexCyleBased(intersection)));

					edgeToIntersections.addAll(intersectionsVertices);
				}
			}

			checkedPair.add(currentEdge.getStart().getName() + "_" + currentEdge.getEnd().getName());
			checkedPair.add(currentEdge.getEnd().getName() + "_" + currentEdge.getStart().getName());
		}
		
		for(int iter = 0, comp = 1; comp < edgeToIntersections.size() - 1; iter++, comp++) {

			if(edgeToIntersections.get(iter).getGeometry().getCenter().equals(edgeToIntersections.get(comp).getGeometry().getCenter())) {
				
				edgeToIntersections.remove(comp);
				iter--;
				comp--;
			}
		}
		
		this.scenarioManager.getGraph().clearGraph();
		edgeToIntersections.forEach(node -> this.scenarioManager.getGraph().addVertex(node));
	}
	
	@Override
	public void callPostProcessing(SimulationState simulationState) {
		
		// nothing to do	
	}
}
