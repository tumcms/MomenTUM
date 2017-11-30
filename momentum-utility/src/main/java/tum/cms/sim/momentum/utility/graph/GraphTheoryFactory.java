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

package tum.cms.sim.momentum.utility.graph;

import tum.cms.sim.momentum.utility.geometry.Cycle2D;
import tum.cms.sim.momentum.utility.geometry.Geometry2D;
import tum.cms.sim.momentum.utility.geometry.GeometryFactory;
import tum.cms.sim.momentum.utility.geometry.Segment2D;
import tum.cms.sim.momentum.utility.geometry.Vector2D;

public class GraphTheoryFactory {
	
	private static int edgeSeed = -1;
	private static double radius = 0.5;
	
	public static synchronized void updateIds(Integer id) {
		
		edgeSeed = edgeSeed <= id ? id + 1 : edgeSeed;
	}
	
	public static synchronized int getNewEdgeId() {
			
		int result = ++GraphTheoryFactory.edgeSeed;
		
		return result;
	}
	
	private static int vertexSeed = -1;

	public static synchronized int getNewVertexId() {
			
		int result = ++GraphTheoryFactory.vertexSeed;
		
		return result;
	}
	
	private static int graphSeed = -1;

	public static synchronized int getNewGraphId() {
			
		int result = ++GraphTheoryFactory.graphSeed;
		
		return result;
	}
	
	private GraphTheoryFactory() { }
	
	public static Edge createEdge(Vertex start, Vertex end) {
		
		Edge edge = new Edge(start, end);
		edge.setName(start.getId().toString() + "_" + end.getId().toString());
		edge.setId(GraphTheoryFactory.getNewEdgeId());
		return edge;
	}
	public static Vertex createVertex(Geometry2D geometry, boolean isSeed, Integer id) {
		
		Vertex vertex = new Vertex(geometry, isSeed);
		vertex.setId(id);
		GraphTheoryFactory.updateIds(id);
		vertex.setName(vertex.getId().toString());
		return vertex;
	}
	
	public static Vertex createVertex(Geometry2D geometry, boolean isSeed) {
		
		Vertex vertex = new Vertex(geometry, isSeed);
		vertex.setId(GraphTheoryFactory.getNewVertexId());
		vertex.setName(vertex.getId().toString());
		return vertex;
	}
	
	public static Vertex createVertex(Geometry2D geometry) {
		
		Vertex vertex = new Vertex(geometry);
		vertex.setId(GraphTheoryFactory.getNewVertexId());
		vertex.setName(vertex.getId().toString());
		return vertex;
	}
	
	public static Vertex createVertex(Geometry2D geometry, Integer id) {
		
		Vertex vertex = new Vertex(geometry);
		vertex.setId(id);
		GraphTheoryFactory.updateIds(id);
		vertex.setName(vertex.getId().toString());
		return vertex;
	}
	
	public static Vertex createVertexCyleBased(Vector2D center,  Integer id) {
		
		Cycle2D vertexCycle = GeometryFactory.createCycle(center, radius);
		Vertex vertex = new Vertex(vertexCycle);
		vertex.setId(id);
		GraphTheoryFactory.updateIds(id);

		vertex.setName(vertex.getId().toString());
		return vertex;
	}

	public static Vertex createVertexCyleBased(Vector2D center) {
		
		Cycle2D vertexCycle = GeometryFactory.createCycle(center, radius);
		Vertex vertex = new Vertex(vertexCycle);
		vertex.setId(GraphTheoryFactory.getNewVertexId());
		vertex.setName(vertex.getId().toString());
		return vertex;
	}

	public static Graph createGraph(String name) {
		
		Graph graph = new Graph();
		graph.setName(name);
		graph.setId(GraphTheoryFactory.getNewVertexId());
		return graph;
	}
	
	
	public static Segment2D createEdgeSegment(Vertex left, Vertex right) {

		Vector2D rightCenter = right.getGeometry().getCenter();
		Vector2D leftCenter = left.getGeometry().getCenter();
	
		return GeometryFactory.createSegment(leftCenter, rightCenter);
	}

	public static Edge createEdge(Integer id, Integer id2, Vertex left, Vertex right) {

		return GraphTheoryFactory.createEdge(left, right);
	}
}
