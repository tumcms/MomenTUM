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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import tum.cms.sim.momentum.utility.generic.IHasProperties;
import tum.cms.sim.momentum.utility.generic.PropertyBackPack;
import tum.cms.sim.momentum.utility.generic.Unique;
import tum.cms.sim.momentum.utility.geometry.Geometry2D;
import tum.cms.sim.momentum.utility.geometry.Vector2D;
import tum.cms.sim.momentum.utility.geometry.operation.GeometryAdditionals;

import java.util.Optional;
import java.util.Set;

public class Graph extends Unique implements IHasProperties {
	
	protected PropertyBackPack properties = null;
	
	@Override
	public PropertyBackPack getPropertyBackPack() {
		return properties;
	}
	
	@Override
	public void setPropertyBackPack(PropertyBackPack propertyContainer) {

		this.properties = propertyContainer; 
	}
	
	private HashMap<String, Double> distanceMap = new HashMap<>();
    private HashMap<Integer, Vertex> vertexMap = new HashMap<Integer, Vertex>();   
    private HashMap<Geometry2D, Vertex> geometryMap = new HashMap<Geometry2D, Vertex>(); 
    private HashMap<Vertex, HashSet<Edge>> successorEdgeMap = new HashMap<Vertex, HashSet<Edge>>();
    private HashMap<Vertex, HashSet<Vertex>> successorVertexMap = new HashMap<Vertex, HashSet<Vertex>>();    
    private Double maximalDistance = null;
	
	private boolean floydWarshalComputed = false;
	
    Graph() { }

    public Collection<Vertex> getVertices() {
    	
    	return vertexMap.values();
    }
    
    public int getVertexCount() {
    	
    	return vertexMap.size();
    }

    public int getEdgeCount() {
    	
    	int countEdges = 0;
    	
    	for(HashSet<Edge> edgeMap : successorEdgeMap.values()) {
    		
    		 countEdges += edgeMap.size();
    	}
    	
    	return countEdges;
    }
    
    public Collection<Edge> getSuccessorEdges(Vertex current) {
    	
    	return successorEdgeMap.get(current);
    }

    public Collection<Vertex> getSuccessorVertices(Vertex current) {
    	
    	return successorVertexMap.get(current);
    }
    
    public Vertex getVertex(int vertexId) {
    	
    	return vertexMap.get(vertexId);
    }
    
	public boolean containsVertex(Integer id) {

		return vertexMap.containsKey(id);
	}
	
	public void setDistance(Path fromToPath) {
		
		if(fromToPath.getFirstVertex().getId() != -1 && fromToPath.getFinalVertex().getId() != -1) {
			
			String key = String.valueOf(fromToPath.getFirstVertex().getId()) + "_" + String.valueOf(fromToPath.getFinalVertex().getId());
			this.distanceMap.put(key, fromToPath.distance());	
		}
	}
	
	public void setDistance(Vertex start, Vertex end, Double distance) {
		
		if(start.getId() != -1 && end.getId() != -1) {
			
			String key = String.valueOf(start.getId()) + "_" + String.valueOf(end.getId());
			this.distanceMap.put(key, distance);	
		}
	}
    
	public Double getDistance(Vertex start, Vertex end) {
		
		if(start != null && end != null && start.getId() != -1 && end.getId() != -1) {
			
			String key = String.valueOf(start.getId()) + "_" + String.valueOf( end.getId());
			
			if(this.distanceMap.containsKey(key)) {
				
				return this.distanceMap.get(key);
			}
		}
		
		return null;
	}
//    public Edge getEdge(int edgeId) {
//    	
//    	return edgeMap.get(edgeId);
//    }
    
    public Edge getEdge(Vertex startVertex, Vertex endVertex) {
    	
    	if(startVertex == endVertex) {
    		
    		return null;
    	}
    		
    	Optional<Edge> result = successorEdgeMap.get(vertexMap.get(startVertex.getId()))
			.stream()
			.filter(edge -> edge.getEnd().getId() == endVertex.getId())
			.findFirst();
		
    	return result.isPresent() ? result.get() : null;
    }
    
    public HashSet<Edge> getAdjacentEdges(Vertex startVertex) {
    	
    	return successorEdgeMap.get(vertexMap.get(startVertex.getId()));
    }
    
    /**
     * Returns a list of all edges of the graph
     * @author qa
     * @return
     */
    public List<Edge> getAllEdges() {
    	List<Edge> edgeList = new ArrayList<Edge>();
    	for(Vertex current : this.getVertices()) {  	 
    		for(Vertex successor : this.getSuccessorVertices(current)) {
    			if(current == successor) 
    				continue;
    			edgeList.add(this.getEdge(current, successor));
    		}
       	}
    	return edgeList;
    }

    public void addVertex(Vertex vertex) {

    	if(vertexMap.containsKey(vertex.getId())) {
    		return;
    	}
    	
     	vertexMap.put(vertex.getId(), vertex);
    	
    	successorEdgeMap.put(vertex, new HashSet<Edge>());
       	successorVertexMap.put(vertex, new HashSet<Vertex>());
       	geometryMap.put(vertex.getGeometry(), vertex);
    }
    
    public void removeVertex(Vertex deleted) {
 	
    	vertexMap.remove(deleted.getId()); // delete ref
    	successorVertexMap.remove(deleted); // delete as start vertex
    	
    	// delete as successor vertex
    	successorVertexMap.values()
    		.forEach(successorVertices -> successorVertices.removeIf(end -> end == deleted));
    	
    	// delete all edges if is start vertex
    	successorEdgeMap.remove(deleted); 
    	
    	// delete all edges if is successor vertex
    	successorEdgeMap.values()
			.forEach(successorEdges -> successorEdges.removeIf(edge -> edge.getEnd() == deleted));
    	
    	// delete geomtry ref
    	geometryMap.remove(deleted.getGeometry());
    }
    
    public void conncetVertices(Vertex vertexStart, Vertex vertexEnd, Edge connector) {
    	
    	if(!vertexMap.containsKey(vertexStart.getId())) {
    		this.addVertex(vertexStart);
    	}
    	
    	if(!vertexMap.containsKey(vertexEnd.getId())) {
    		this.addVertex(vertexEnd);
    	}

    	successorEdgeMap.get(vertexStart).add(connector);
    	successorVertexMap.get(vertexStart).add(vertexEnd);
    }
    
    /**
     * Connects two vertices A and B in both ways.
     * @author qa
     * @param vertexA
     * @param vertexB
     */
    public void doublyConnectVertices(Vertex vertexA, Vertex vertexB) {
    	
    	Edge connector = GraphTheoryFactory.createEdge(vertexA, vertexB);
    	this.conncetVertices(vertexA, vertexB, connector);
		
    	connector = GraphTheoryFactory.createEdge(vertexB, vertexA);
		this.conncetVertices(vertexB, vertexA, connector);
    }
    
	/**
	 * Connects all vertices of the graph if they are visible to each other.
	 * @author qa
	 * @param graph
	 * @param blockingGeometries
	 */
	public void connectAllVisibleVertices(Collection<Geometry2D> blockingGeometries, double connectObstacleDistance) {
		
		boolean inSight = false;
		for(Vertex vertex : this.getVertices()) {
			
			for(Vertex vertexToSee : this.getVertices()) {
				
				if(vertexToSee.equals(vertex)) {
					
					continue;
				}
				
				inSight = GeometryAdditionals.calculateIntersection(blockingGeometries, 
						vertex.getGeometry().getCenter(), 
						vertexToSee.getGeometry().getCenter(),
						connectObstacleDistance);
				
				if(inSight) {	
					
					this.doublyConnectVertices(vertex, vertexToSee);
				}
			}
		}
	}
    
	public void disconnectDirectedVertices(Vertex vertexStart, Vertex vertexEnd) {
		
    	if(successorVertexMap.containsKey(vertexStart)) {
    		
    		successorVertexMap.get(vertexStart).removeIf(end -> end.getId().intValue() == vertexEnd.getId());
    	}
    	 
    	// remove successor edge  if connection exists	
    	if(successorEdgeMap.containsKey(vertexStart)) {
    		
    		successorEdgeMap.get(vertexStart).removeIf(edge -> edge.getEnd().getId().intValue() == vertexEnd.getId());
    	}
	}
	
    public void disconnectVertices(Vertex vertexStart, Vertex vertexEnd) {
    	
    	// remove edge
    	// edgeMap.removeIf(edge -> edge.getStart().getId() == vertexStart.getId() && edge.getEnd().getId() == vertexEnd.getId());
      	
      	// remove successor vertex if connection exists
    	if(successorVertexMap.containsKey(vertexStart)) {
    		
    		successorVertexMap.get(vertexStart).removeIf(end -> end.getId().intValue() == vertexEnd.getId());
    	}
    	
    	if(successorVertexMap.containsKey(vertexEnd)) {
    		
    		successorVertexMap.get(vertexEnd).removeIf(start -> start.getId().intValue() == vertexStart.getId());
    	}
    	 
    	// remove successor edge  if connection exists	
    	if(successorEdgeMap.containsKey(vertexStart)) {
    		
    		successorEdgeMap.get(vertexStart).removeIf(edge -> edge.getEnd().getId().intValue() == vertexEnd.getId());
    	}
    	
    	if(successorEdgeMap.containsKey(vertexEnd)) {
    		
    		successorEdgeMap.get(vertexEnd).removeIf(edge -> edge.getStart().getId().intValue() == vertexStart.getId());
    	}
    }
    
    public void removeEdge(Edge edge) {
    	
    	this.disconnectVertices(edge.getStart(), edge.getEnd());
    }
    
    public void disconnectAllVertices() {
    	
    	//edgeMap = new HashMap<Integer, Edge>();
    	successorEdgeMap.forEach((vertex, edge) -> successorEdgeMap.put(vertex, new HashSet<Edge>()));
    	successorVertexMap.forEach((vertex, successors) -> successorVertexMap.put(vertex, new HashSet<Vertex>()));
    }
    
    public void clearGraph() {
    	
    	disconnectAllVertices();
    	vertexMap = new HashMap<>();
    	geometryMap = new HashMap<>();
    	successorEdgeMap.clear();
    	successorVertexMap.clear();
    	maximalDistance = null;
    	floydWarshalComputed = false;
    }
    
    public Vertex getGeometryVertex(Geometry2D geometry) {
    	
    	return this.geometryMap.get(geometry);
    }
    
    public Vertex findVertexClosestToPosition(Vector2D position, Set<Vertex> toIgnore) {

    	double distance = Double.MAX_VALUE;
    	double currentDistance = 0.0;
    	Vertex bestVertex = null;
    	
    	for(Vertex vertex : this.vertexMap.values()) {
    	
    		if(toIgnore != null && toIgnore.contains(vertex)) {
    			continue;
    		}
    		currentDistance = vertex.euklidDistanceBetweenVertex(position);
    		
    		if(currentDistance < distance) {
    		
    			bestVertex = vertex;
    			distance = currentDistance;
    		}
    	}
 
    	return bestVertex;
    }
    
    public Vertex findVertexClosestToPositionAndTarget(Vector2D position, Vector2D target, Set<Vertex> toIgnore) {

    	double distance = Double.MAX_VALUE;
    	double currentDistance = 0.0;
    	Vertex bestVertex = null;
    	
    	for(Vertex vertex : this.vertexMap.values()) {
    	
    		if(toIgnore != null && toIgnore.contains(vertex)) {
    			continue;
    		}
    		
    		currentDistance = vertex.euklidDistanceBetweenVertex(position) +
    				 vertex.euklidDistanceBetweenVertex(target);
    		
    		if(currentDistance < distance) {
    		
    			bestVertex = vertex;
    			distance = currentDistance;
    		}
    	}
 
    	return bestVertex;
    }
    
    public synchronized double findMaximalVertexDistance() {
    	
    	if(this.maximalDistance == null) {
    		
    		this.maximalDistance = Double.MIN_VALUE;
    		double currentDistance = 0;
    		
	    	for(Vertex compare : this.vertexMap.values()) {
	    		
	    		for(Vertex against : this.vertexMap.values()) {
		    		
	    			currentDistance = compare.euklidDistanceBetweenVertex(against);

	    			if(currentDistance > this.maximalDistance) {
	    				
	    				this.maximalDistance = currentDistance;
	    			}
		    	}
	    	}
    	}
    	
    	return this.maximalDistance;
    }
    
    public String getNullVertexIndices() {
    	
    	StringBuilder builder = new StringBuilder();
    	
    	for(Entry<Integer, Vertex> keyValue : vertexMap.entrySet()) {
    		
    		if(keyValue.getValue() == null) {
    			
    			builder.append(String.valueOf(keyValue.getKey()) + "_");
    		}
    	}
    	
    	builder.append(System.lineSeparator());
    	
    	for(Vertex key : successorVertexMap.keySet()) {
    	
    		for(Vertex value : successorVertexMap.get(key)) {
    			
    			if(value == null) {
    				
    				builder.append(String.valueOf(key.getId()) + "_");
    			}
    		}
    	}
    	
    	builder.append(System.lineSeparator());
    			
    	for(Vertex key : successorEdgeMap.keySet()) {
        	
    		for(Edge edge : successorEdgeMap.get(key)) {
    			
    			if(edge == null) {
    				
    				builder.append(String.valueOf(key.getId()) + "_");
    			}
    		}
    	}
    	builder.append(System.lineSeparator());
    	
        return builder.toString();
    }
    
    /**
	 * Based on visibilityGraphBuildAlgorithm.addKnownVertexSeeds. Adds vertices based on input geometry.
	 * @author qa
	 * @param knownSeeds
	 * @return
	 */
	public void addGeometryAsVertex(Collection<Geometry2D> knownSeeds) {
    	
    	knownSeeds.forEach(knownSeed -> this.addVertex(GraphTheoryFactory.createVertex(knownSeed)));

    }

	/**
	 * Based on https://en.wikipedia.org/wiki/Floyd%E2%80%93Warshall_algorithm
	 * 
	 * @param weightName
	 * @author pk
	 */
	public void computeFloydWarshall() {
		
		if(floydWarshalComputed) {
			return;
		}
		
		floydWarshalComputed = true;
		int size = this.getVertexCount();
		double[][] distance = new double[size][size];
		
		HashMap<Integer, Vertex> vertexMap = new HashMap<>();
		int number = 0;
		
		for(Vertex vertex : this.vertexMap.values()) {
			
			vertexMap.put(number, vertex);
			number++;
		}
		
		for(int row = 0; row < size; row++) {
			
			for(int col = 0; col < size; col++) {
				
				if(this.successorVertexMap.get(vertexMap.get(row)).contains(vertexMap.get(col))) {
					
					distance[row][col] = vertexMap.get(row).euklidDistanceBetweenVertex(vertexMap.get(col));
					distance[col][row] = distance[row][col];
				}
				else if(row == col) {
					
					distance[row][col] = 0.0;
				}
				else {
					
					distance[row][col] = Double.MAX_VALUE;
					distance[col][row] = Double.MAX_VALUE;
				}
				
			}
		}	
		
		for(number = 0; number < size; number++) {
			
			for(int row = 0; row < size; row++) {
				
				for(int col = 0; col < size; col++) {

					if(distance[row][col] > distance[row][number] + distance[number][col]) {
						
						distance[row][col] = distance[row][number] + distance[number][col];
					}	
				}
			}
		}
		
		for(int row = 0; row < size; row++) {
			
			for(int col = 0; col < size; col++) {
				
				this.setDistance(vertexMap.get(row), vertexMap.get(col), distance[row][col]);
			}
		}
	}
	
	/**
     * Uses Kruskal's algorithm to convert the graph to a minimum spanning tree (MST)
     * according to <a href="https://en.wikipedia.org/wiki/Kruskal's_algorithm">
     * https://en.wikipedia.org/wiki/Kruskal's_algorithm</a>.
     * The graph must be weighted with the given <code>weightName</code>.
     * @author qa
     */
    public void convertToMST(String weightName) {
    	
    	UnionFind<Vertex> vertexForest = new UnionFind<Vertex>(this.getVertices());
    	List<Edge> edgeList = new ArrayList<Edge>();

    	edgeList = this.getAllEdges();
    	
    	//sort the edges according to their weight
    	Collections.sort(edgeList, new Comparator<Edge>() {
    		@Override
    		public int compare(Edge e1, Edge e2) {
    			if(e1.getWeight(weightName) < e2.getWeight(weightName)) {
    				return -1;
    			}
    			else if(e2.getWeight(weightName) < e1.getWeight(weightName)) {
    				return 1;
    			}
    			else 
    				return 0;
    		}
		});
    	
    	//remove all edges from the graph. The needed information is now stored in edgeList
    	this.disconnectAllVertices();
    	
    	//run Kruskal's algorithm
    	for(Edge e : edgeList) {
    		Vertex start = e.getStart();
    		Vertex end = e.getEnd();
    		if(vertexForest.find(start).equals(vertexForest.find(end)))
    			continue;
    		
    		vertexForest.union(start, end);
    		this.doublyConnectVertices(start, end);
    	}
    	
    }
    
    /**
	 * Computes the Euklidean distance between all vertices of a graph and stores the result
	 * as the edge's weight.
	 * @param graph
	 * @param weightName
	 */
	public void computeEuklideanEdgeWeights(String weightName) {
    	for(Vertex current : this.getVertices()) {
    		this.getSuccessorEdges(current).stream().forEach(successor -> {
    			successor.setWeight(weightName, current.euklidDistanceBetweenVertex(successor.getEnd()));
    		});
    	}
	}
}
