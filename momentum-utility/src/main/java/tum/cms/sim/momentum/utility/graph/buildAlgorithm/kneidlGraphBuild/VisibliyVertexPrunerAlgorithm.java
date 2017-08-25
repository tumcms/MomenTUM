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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import tum.cms.sim.momentum.utility.geometry.Geometry2D;
import tum.cms.sim.momentum.utility.geometry.GeometryFactory;
import tum.cms.sim.momentum.utility.geometry.Vector2D;
import tum.cms.sim.momentum.utility.geometry.operation.GeometryAdditionals;
import tum.cms.sim.momentum.utility.graph.Graph;
import tum.cms.sim.momentum.utility.graph.Vertex;

public class VisibliyVertexPrunerAlgorithm {

	public void pruneVertices(Graph graph, 
			ArrayList<Vertex> mergeVertices,
			HashSet<Vertex> untouchableVertices,
			Collection<Geometry2D> blockingGeometries,
			double mergeDistance) {

		HashSet<Vertex> neighborsToMerge = new HashSet<Vertex>();
		HashSet<Vertex> verticesToDelete = new HashSet<Vertex>();
        TreeSet<Vertex> sortedVertices = null;

        VertexThreeWayDistanceComparer vertexDistanceComparator = new VertexThreeWayDistanceComparer();
   
		for (Vertex mergeCore : mergeVertices) {
			
			if (neighborsToMerge.contains(mergeCore)
			    || untouchableVertices.contains(mergeCore)) {
	       
				continue;
	        }
			
		    vertexDistanceComparator.setVertex(mergeCore);
            sortedVertices = new TreeSet<Vertex>(vertexDistanceComparator);
	        sortedVertices.addAll(mergeVertices); // sort 
	        
            // check which neighbors can be deleted
            neighborsToMerge.addAll(this.checkNeigbhorsForDeletion(graph, 
            		blockingGeometries,
            		untouchableVertices,
            		mergeCore,
            		mergeDistance,
            		verticesToDelete)); 
        	}

        // remove the vertices itself
		verticesToDelete.forEach(toRemove -> graph.removeVertex(toRemove));
    }

    private Set<Vertex> checkNeigbhorsForDeletion(Graph graph, 
    		Collection<Geometry2D> blockingGeometries, 
    		HashSet<Vertex> untouchableVertices,
    		Vertex mergeCore,
    		double mergeDistance,
    		HashSet<Vertex> verticesToDelete) {

        HashSet<Vertex> neighborsToMerge = new HashSet<Vertex>();
        neighborsToMerge.add(mergeCore);
       

        Collection<Vertex> neighbors = graph.getSuccessorVertices(mergeCore);

        if (neighbors.isEmpty()) {
        	
            return Collections.emptySet();
        }

        // iterate over all possible neighbor nodes and add them to the merge list if possible
        // if the neighbor is in range of the merging distance
        
        for(Vertex neighborVertex : neighbors) {
        	
        	if(neighborVertex.euklidDistanceBetweenVertex(mergeCore) > mergeDistance
        	   || verticesToDelete.contains(neighborVertex) // the merge vertices are never already removed
        	   || untouchableVertices.contains(neighborVertex)) { // the merge vertices are never known seeds
        		continue;
        	}
        	
        	neighborsToMerge.add(neighborVertex);
        }
        
        HashSet<Vertex> removedNeighborsToMerge = new HashSet<Vertex>();
        Vector2D mergeCenter = null;
        
        // Check if all of the neighbors neighbors and are visible from the new core position
        // if a neighbors neighbor is not visible, take the neighbor out of the merge list and try again
    	for(Vertex neighbor : neighborsToMerge) {
    		
    		// build the new center based on the current neighbor list
    		mergeCenter = this.createMergeCenter(neighborsToMerge.stream()
    				.filter(testNeighbor -> !removedNeighborsToMerge.contains(testNeighbor))
    				.collect(Collectors.toList()),
    			mergeCore);
    
    		for(Vertex neighborsNeighbor : graph.getSuccessorVertices(neighbor)) {
    			
    			boolean inSight = GeometryAdditionals.calculateIntersection(blockingGeometries,
    					mergeCenter,
	            		neighborsNeighbor.getGeometry().getCenter(),
	            		0.0);
    	 	 
				if(!inSight) {
					
					removedNeighborsToMerge.add(neighbor);
					break;
				}
    		}
    	}
    	
    	// update the core vertex
    	if(neighborsToMerge.size() > 0
    	   && neighborsToMerge.size() > removedNeighborsToMerge.size()) {

    		verticesToDelete.addAll(neighborsToMerge.stream()
    			.filter(testNeighbor -> !removedNeighborsToMerge.contains(testNeighbor))
				.collect(Collectors.toList()));
    		
            // create new core center
        	mergeCore.getGeometry().getCenter().set(mergeCenter);  
    	}
    	
        return neighborsToMerge;
    }

    private Vector2D createMergeCenter(Collection<Vertex> vertices, Vertex mergeCore) {

    	Vector2D center = GeometryFactory.createVector(0.0, 0.0);

    	for(Vertex vertex : vertices) {
    		
    		center = vertex.getGeometry().getCenter().sum(center);
    	}

    	return center.multiply(1.0 / vertices.size());
    }
}
