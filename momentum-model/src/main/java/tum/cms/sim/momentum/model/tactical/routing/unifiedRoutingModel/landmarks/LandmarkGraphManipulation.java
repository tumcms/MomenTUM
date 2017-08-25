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

package tum.cms.sim.momentum.model.tactical.routing.unifiedRoutingModel.landmarks;

import java.util.List;

import tum.cms.sim.momentum.data.layout.area.Area;
import tum.cms.sim.momentum.utility.geometry.Geometry2D;
import tum.cms.sim.momentum.utility.graph.Graph;

/**
 * not used
 * @author ga37sib
 *
 */
public class LandmarkGraphManipulation {

	public void landmarkManipulation(Graph graph, List<Geometry2D> blockingGeometries, List<Area> areas) {

//		double maximalDistanceInGraph = graph.findMaximalVertexDistance();
//		
//		Stream<Area> landmarks = areas.stream().filter(area -> area.getLandmark() != null);
//		int numberOfLandmarks = (int)landmarks.count();
//		landmarks.forEach(landmarkArea -> this.manipulateGraph(graph, blockingGeometries, landmarkArea, maximalDistanceInGraph));
//		landmarks.forEach(landmarkArea -> this.normalizeGraph(graph, numberOfLandmarks));
	}
	
//	private void normalizeGraph(Graph graph, int numberOfLandmarks) {
//
//		for(Vertex vertex : graph.getVertices()) {
//			
//			for(Edge edge: graph.getSuccessorEdges(vertex)) {
//				
//				edge.multiplicateWeight(KneidlConstant.LandmarkEdgeWeightName, 1.0 / numberOfLandmarks);
//			}
//		}
//	}
//
//	/**
//	 * If the current vertex can see the landmark, the distance to that landmark of a successor
//	 * determines the weight of the edge between vertex and successor
//	 * @param graph
//	 * @param blockingGeometries
//	 * @param landmarkArea
//	 * @param maximalDistanceInGraph
//	 */
//	private void manipulateGraph(Graph graph,
//			List<Geometry2D> blockingGeometries, 
//			Area landmarkArea,
//			double maximalDistanceInGraph) {
//		
//		for(Vertex vertex : graph.getVertices()) {
//			
//			for(Edge edge: graph.getSuccessorEdges(vertex)) {
//				
//				Vertex successor = edge.getEnd();
//				boolean inSight = true;
//				
//				
//				if(!landmarkArea.getLandmark().canBeSeen()) {
//					
//					inSight = GeoAdditionals.calculateIntersection(blockingGeometries, 
//							vertex.getGeometry().getCenter(), 
//							landmarkArea.getGeometry().getCenter());
//				}
//				
//				double direction = landmarkArea.getLandmark().getType() == LandmarkType.Approach ? 1.0 : 0.0;
//				
//				if(inSight) {
//					
//					double distance = successor.euklidDistanceBetweenVertex(landmarkArea.getGeometry().getCenter());
//					double landmarkWeight = direction - distance / maximalDistanceInGraph;
//					
//					edge.increaseWeight(KneidlConstant.LandmarkEdgeWeightName, landmarkWeight);
//				}
//				else {
//					
//					edge.increaseWeight(KneidlConstant.LandmarkEdgeWeightName, direction);
//				}
//			}
//		}
//	}
}
