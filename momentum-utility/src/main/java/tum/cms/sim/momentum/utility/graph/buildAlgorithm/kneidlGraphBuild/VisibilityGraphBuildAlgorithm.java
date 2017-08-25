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

package tum.cms.sim.momentum.utility.graph.buildAlgorithm.kneidlGraphBuild;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.math3.util.FastMath;
import tum.cms.sim.momentum.utility.geometry.AngleInterval2D;
import tum.cms.sim.momentum.utility.geometry.Geometry2D;
import tum.cms.sim.momentum.utility.geometry.GeometryFactory;
import tum.cms.sim.momentum.utility.geometry.Vector2D;
import tum.cms.sim.momentum.utility.geometry.operation.GeometryAdditionals;
import tum.cms.sim.momentum.utility.graph.Edge;
import tum.cms.sim.momentum.utility.graph.Graph;
import tum.cms.sim.momentum.utility.graph.GraphTheoryFactory;
import tum.cms.sim.momentum.utility.graph.Vertex;
import tum.cms.sim.momentum.utility.graph.topologyAlgorithm.ConnectedComponentTopologyAlgorithm;

public class VisibilityGraphBuildAlgorithm {

	private final static String cornerDistanceName = "cornerDistance";
	private final static String segmentSegregationName = "segmentSegregation";
	private final static String reductionStrengthName = "reductionStrength";
	
	private double maxAlpha = 20.0;
	private double baseAlpha = 0.0;
	private int maxIteration = 1;
   
	VisibilityVertexBuildAlgorithm vertexBuilder = new VisibilityVertexBuildAlgorithm();

	public VisibilityGraphBuildAlgorithm() {
		
	}
	
    public Graph buildNormal(Graph graph, 
    		double scenarioSize,
    		Collection<Geometry2D> knownSeeds, 
    		Collection<Geometry2D> blockingGeometries) {

		double cornerDistance = graph.getPropertyBackPack().getDoubleProperty(cornerDistanceName);
    	ArrayList<Vertex> knownVertexSeeds = this.addKnownVertexSeeds(graph, knownSeeds);
     	
     	ArrayList<Vertex> cornerVerticesSeeds = vertexBuilder.createGraphSeeds(blockingGeometries, cornerDistance);
    	cornerVerticesSeeds.forEach(cornerSeed -> graph.addVertex(cornerSeed));
  	
    	HashSet<Vertex> untouchableVertices = new HashSet<Vertex>(knownVertexSeeds);

        VisibliyVertexPrunerAlgorithm vertexPruner = new VisibliyVertexPrunerAlgorithm();
        vertexPruner.pruneVertices(graph, 
        		cornerVerticesSeeds, 
        		untouchableVertices, 
        		blockingGeometries,
        		cornerDistance);
        
        this.connectVertices(blockingGeometries, graph, knownVertexSeeds, baseAlpha, scenarioSize); 
        this.removeDispensibleConnectedComponents(graph, knownVertexSeeds);

        return graph;
    }
    
    public Graph buildReduced(Graph graph, 
    		double scenarioSize,
    		Collection<Geometry2D> knownSeeds, 
    		Collection<Geometry2D> blockingGeometries) throws Exception {

    	double cornerDistance = graph.getPropertyBackPack().getDoubleProperty(cornerDistanceName);
    	this.maxAlpha = graph.getPropertyBackPack().getDoubleProperty(reductionStrengthName);
    	
    	ArrayList<Vertex> knownVertexSeeds = this.addKnownVertexSeeds(graph, knownSeeds);

       	ArrayList<Vertex> cornerVerticesSeeds = vertexBuilder.createGraphSeeds(blockingGeometries, cornerDistance);
    	cornerVerticesSeeds.forEach(cornerSeed -> graph.addVertex(cornerSeed));
    	HashSet<Vertex> untouchableVertices = new HashSet<Vertex>(knownVertexSeeds);

        VisibliyVertexPrunerAlgorithm vertexPruner = new VisibliyVertexPrunerAlgorithm();
        vertexPruner.pruneVertices(graph, 
        		cornerVerticesSeeds, 
        		untouchableVertices, 
        		blockingGeometries,
        		cornerDistance);
        
    	this.kneidlEdgeReduction(graph, scenarioSize, blockingGeometries, knownVertexSeeds);
        this.removeDispensibleConnectedComponents(graph, knownVertexSeeds);

        return graph;
    }
   
    public Graph buildReducedEnriched(Graph graph, 
    		double scenarioSize,
    		Collection<Geometry2D> knownSeeds, 
    		Collection<Geometry2D> blockingGeometries) throws Exception {

    	double cornerDistance = graph.getPropertyBackPack().getDoubleProperty(cornerDistanceName);
    	double segmentSegregation = graph.getPropertyBackPack().getDoubleProperty(segmentSegregationName);
    	this.maxAlpha = graph.getPropertyBackPack().getDoubleProperty(reductionStrengthName);
    	
    	ArrayList<Vertex> knownVertexSeeds = this.addKnownVertexSeeds(graph, knownSeeds);
    	
       	ArrayList<Vertex> cornerVerticesSeeds = vertexBuilder.createGraphEnrichedSeeds(blockingGeometries,
       			cornerDistance,
       			segmentSegregation);
       	
    	cornerVerticesSeeds.forEach(cornerSeed -> graph.addVertex(cornerSeed));
    	HashSet<Vertex> untouchableVertices = new HashSet<Vertex>(knownVertexSeeds);

        VisibliyVertexPrunerAlgorithm vertexPruner = new VisibliyVertexPrunerAlgorithm();
        vertexPruner.pruneVertices(graph, 
        		cornerVerticesSeeds, 
        		untouchableVertices, 
        		blockingGeometries,
        		cornerDistance);
        
    	this.kneidlEdgeReduction(graph, scenarioSize, blockingGeometries, knownVertexSeeds);
        this.removeDispensibleConnectedComponents(graph, knownVertexSeeds);

        return graph;
    }
    
    private ArrayList<Vertex> addKnownVertexSeeds(Graph graph, Collection<Geometry2D> knownSeeds) {
	    
     	ArrayList<Vertex> knownVertexSeeds = new ArrayList<Vertex>();
    	 
    	knownSeeds.forEach(knownSeed -> knownVertexSeeds.add(GraphTheoryFactory.createVertex(knownSeed)));
    	knownVertexSeeds.forEach(seedVertex -> graph.addVertex(seedVertex));
	    
    	return knownVertexSeeds;
    }

    private void kneidlEdgeReduction(Graph graph, 
    		double scenarioSize,
    		Collection<Geometry2D> blockingGeometries,
    		Collection<Vertex> knownSeedVertices) throws Exception {
    	  
        // init alphas, radiant measure,
        //double upperAlpha = (FastMath.PI / 180) * maxAlpha;
        //double lowerAlpha = 0.0;
        double alpha = GeometryAdditionals.translateToRadiant(maxAlpha);
        boolean isConnected = false;
        maxIteration = 0;
        do { // try largest alpha possible, the larger the less edges
        	
        	graph.disconnectAllVertices();
        	this.connectVertices(blockingGeometries, graph, knownSeedVertices, alpha, scenarioSize);
	        isConnected = this.checkVertexSeedConnectivity(graph, knownSeedVertices);
            alpha = alpha / 2.0; 
            maxIteration--;

        } while (maxIteration > 0 && !isConnected);

       if(!isConnected) {
//        	
    	   isConnected = true;//throw new Exception("Generated graph is not connected!");
        }
    }	  

    private boolean checkVertexSeedConnectivity(Graph graph, Collection<Vertex> knownSeedVertices) {
    	
    	// find components 
    	ConnectedComponentTopologyAlgorithm connectivityAlgorithm = new ConnectedComponentTopologyAlgorithm();
    	HashMap<Vertex, Integer> connectivityMap = connectivityAlgorithm.calculateConnectedComponents(graph);
    	int connectivityComponent = Integer.MIN_VALUE;
    	int lastConnectivityComponent = Integer.MIN_VALUE;
    	boolean isConnected = true;
    	
    	// check if all known seeds are in the components
    	for(Vertex seedVertex : knownSeedVertices) {
    		
    		if(lastConnectivityComponent == Integer.MIN_VALUE) {
    			
    			lastConnectivityComponent = connectivityMap.get(seedVertex);
    		}
    		
    		connectivityComponent = connectivityMap.get(seedVertex);
    		
    		// seeds are not connected! This should never happen
    		// thus alpha is to huge or the layout is poorly modeled
    		if(lastConnectivityComponent != connectivityComponent) {
    			
    			isConnected = false;
    			break;
    		}
    	}

        return isConnected;
    }

	private void removeDispensibleConnectedComponents(Graph graph, ArrayList<Vertex> knownVertexSeeds) {
	    
		ArrayList<Vertex> toRemoveVertices = new ArrayList<Vertex>();
		ConnectedComponentTopologyAlgorithm connectivityAlgorithm = new ConnectedComponentTopologyAlgorithm();
		HashMap<Vertex, Integer> connectivityMap = connectivityAlgorithm.calculateConnectedComponents(graph);
		
		HashSet<Integer> seedComponents = new HashSet<Integer>();		
		knownVertexSeeds.stream().forEach(seed -> seedComponents.add(connectivityMap.get(seed)));
		
		// remove vertices not in the seed component
		// that is valid because seed vertices have to be in the same component
		for(Vertex judgedVertex : graph.getVertices()) {
			
			if(knownVertexSeeds.contains(judgedVertex)) { // ignore seeds, they are mandatory
				
				continue;
			}
			
			if(!seedComponents.contains(connectivityMap.get(judgedVertex))) {
				
				toRemoveVertices.add(judgedVertex);
			}
		}
		
		toRemoveVertices.stream().forEach(toDeleteVertex -> graph.removeVertex(toDeleteVertex));
	}

    private void connectVertices(Collection<Geometry2D> blockingGeometries,
    		Graph graph, 
    		Collection<Vertex> knownSeedVertices, 
    		double alpha,
    		double scenarioSize) {

    	int numberOfAngleSegments = Integer.MAX_VALUE; 
    	
    	if(alpha > 0.0) {
    		
    		numberOfAngleSegments = (int)(((FastMath.PI * 2.0) / alpha));
    	}
    			
        VertexThreeWayDistanceComparer vertexDistanceComparator = new VertexThreeWayDistanceComparer();

        HashMap<Vertex, ArrayList<AngleInterval2D>> vertexAngleIntervals = null;
        

        vertexAngleIntervals = new HashMap<Vertex, ArrayList<AngleInterval2D>>();
        
        for (Vertex vertex : graph.getVertices()) {
        	
        	vertexAngleIntervals.put(vertex, new ArrayList<>());
        }

        LinkedHashMap<Vertex, LinkedList<Vertex>> verticesSorted = new LinkedHashMap<>();
        
        for (Vertex vertex : graph.getVertices()) {
        	
        	verticesSorted.put(vertex, new LinkedList<>());
        	verticesSorted.get(vertex).addAll(graph.getVertices());
        	
        	vertexDistanceComparator.setVertex(vertex);
        	verticesSorted.get(vertex).sort(vertexDistanceComparator);
        }

        Vertex vertex = null;
        Vertex neighborVertex = null;
        
        while(!verticesSorted.isEmpty()) {
        	
    		vertex = neighborVertex;
    		
    		while(vertex != null && verticesSorted.get(vertex).isEmpty()) {

    			verticesSorted.remove(vertex);
    			
    			if(verticesSorted.isEmpty()) {
    				break;
    			}
    			vertex = verticesSorted.keySet().stream().findFirst().get();
    		}
    		
    		if(verticesSorted.isEmpty()) {
				break;
			}
    		
    		if(vertex == null ||  vertexAngleIntervals.get(vertex).size() >= numberOfAngleSegments) {
    			
    			vertex = verticesSorted.keySet().stream().findFirst().get();
    		}
        	
        	neighborVertex = verticesSorted.get(vertex).poll();
        	       
        	if (vertex.getId().equals(neighborVertex.getId()) ||
        		graph.getSuccessorVertices(neighborVertex).contains(vertex)) {
             
        		continue;
        	}      	

        	Double angle = null;
        	
        	if(alpha > 0.0) {
        		
        		angle = this.calculateInteresctionAngle(vertex, neighborVertex, vertexAngleIntervals.get(vertex), alpha);
        	}
        	else {
        		
        		angle = 0.0;
        	}

        	if (angle != null) {
        		
        		 boolean inSight = GeometryAdditionals.calculateIntersection(blockingGeometries,
	            		vertex.getGeometry().getCenter(),
	            		neighborVertex.getGeometry().getCenter(),
	            		0.0);	            
	            
	            if(inSight) {
            	
	            	if(angle > 0.0) {
	            		
	            		vertexAngleIntervals.get(vertex).add(
	            				GeometryFactory.createAngleInterval(angle + alpha, 2.0 * alpha));
	            	}
	            	
            		this.generatNeighborConnection(graph, vertex, neighborVertex);
            	}
            }
        }
        
//        for (Vertex vertex : vertices) {
//            
//	        vertexDistanceComparator.setVertex(vertex);
//	        
//	        //Most close Vertex first!
//	        sortedVertices = new ArrayList<Vertex>();        
//	        sortedVertices.addAll(graph.getVertices()); 
//	        sortedVertices.sort(vertexDistanceComparator);
//
//	        // iterate through all found neighbors
//	        for (Vertex neighborVertex : sortedVertices) { // for all, sorted proximity towards reference vertex
//	   
//	        }
//        }
    }
    
    /**
     * A visibility graph is not directed, thus create left-right and right-left association with
     * the same edge.
     */
    private void generatNeighborConnection(Graph graph, Vertex left, Vertex right) {
    	
		Edge edge = GraphTheoryFactory.createEdge(left, right);
		graph.conncetVertices(left, right, edge);
		
		edge = GraphTheoryFactory.createEdge(right, left);
		graph.conncetVertices(right, left, edge);
    }

    private Double calculateInteresctionAngle(Vertex vertex, Vertex neighborVertex, List<AngleInterval2D> angleIntervals, double alpha) {
		       
    	Vector2D referencePointShifted = vertex.getGeometry().getCenter().sum(10, 0);
    	// 10 could be any other value just for line horizontal to baseline 
    	
        Double angle = GeometryAdditionals.angleBetween0And360CCW(
        		neighborVertex.getGeometry().getCenter(),
        		vertex.getGeometry().getCenter(),
        		referencePointShifted);
        
        boolean intersects = false;
        AngleInterval2D newInterval = GeometryFactory.createAngleInterval(angle + alpha, 2 * alpha);
    	if(newInterval.getLeft() < newInterval.getRight()) {
    		
    		intersects = false;
    	}
        for (AngleInterval2D angleInterval : angleIntervals) {
        	
        	if(angleInterval.getLeft() < angleInterval.getRight()) {
        		
        		intersects = false;
        	}
        	if(angleInterval.intersects(newInterval)) {// Interval towards baseline! clockwise! Internal is also clockwise!

	            intersects = true;
                break;
            }
        }

    	return (!intersects ? angle : null);
    } 
}
