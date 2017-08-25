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

package tum.cms.sim.momentum.model.layout.graph.vertex.vertexPruneModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import tum.cms.sim.momentum.data.layout.obstacle.Obstacle;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.layout.graph.GraphOperation;
import tum.cms.sim.momentum.utility.geometry.Geometry2D;
import tum.cms.sim.momentum.utility.geometry.GeometryFactory;
import tum.cms.sim.momentum.utility.geometry.Vector2D;
import tum.cms.sim.momentum.utility.geometry.operation.GeometryAdditionals;
import tum.cms.sim.momentum.utility.graph.Graph;
import tum.cms.sim.momentum.utility.graph.Vertex;

/**
 * Instantiated by GraphType <code>VertexRemoveVisibilityBased</code>
 * @param mergeDistance vertices with a distance less than mergeDistance will be merged
 *
 */
public class VertexVisibilityPruneModel extends GraphOperation {
	
	private static String mergeDistanceName = "mergeDistance";
	private static String visibilityToleranceName = "visibilityTolerance";

	@Override
	public void callPreProcessing(SimulationState simulationState) {

		double mergeDistance = this.properties.getDoubleProperty(mergeDistanceName);
		double visibilityTolerance = this.properties.getDoubleProperty(visibilityToleranceName);
		ArrayList<Vertex> mergeVertices = new ArrayList<Vertex>(this.scenarioManager.getGraph().getVertices());
		Graph graph = this.scenarioManager.getGraph();
		Collection<Geometry2D> blockingGeometries = this.scenarioManager.getObstacles().stream().map(Obstacle::getGeometry).collect(Collectors.toList());	
		
		HashSet<Vertex> untouchableVertices = new HashSet<Vertex>();
		
		this.scenarioManager.getAreas().stream().forEach(area -> {
			
			untouchableVertices.add(this.scenarioManager.getGraph().getGeometryVertex(area.getGeometry()));
		
		});
		
		for(Vertex seed : untouchableVertices) {
			
			for(int iter = 0; iter < mergeVertices.size(); iter++) {
				
				if(mergeVertices.get(iter).euklidDistanceBetweenVertex(seed) < mergeDistance) {
					
					mergeVertices.remove(iter--);
				}
			}
		}
		
		HashSet<Vertex> neighborsToMerge = new HashSet<Vertex>();
		HashSet<Vertex> verticesToDelete = new HashSet<Vertex>();
        TreeSet<Vertex> sortedVertices = null;
   
		for (Vertex mergeCore : mergeVertices) {
			
			if (neighborsToMerge.contains(mergeCore)
			    || untouchableVertices.contains(mergeCore)) {
	       
				continue;
	        }
			
			//three-way vertex sort
	        sortedVertices = new TreeSet<Vertex>(new Comparator<Vertex>() {
	        	@Override
	        	public int compare(Vertex o1, Vertex o2) {

	        		if (mergeCore.getGeometry().getCenter().distance(o1.getGeometry().getCenter()) < 
	        			mergeCore.getGeometry().getCenter().distance(o2.getGeometry().getCenter())) {
	        			
	        			return -1;
	        		}
	        		else if (mergeCore.getGeometry().getCenter().distance(o1.getGeometry().getCenter()) > 
	        			mergeCore.getGeometry().getCenter().distance(o2.getGeometry().getCenter())) {
	        			
	        			return 1;
	        		}
	        		else {
	        			
	        			return 0;
	        		}
	        	}
	        });
	        sortedVertices.addAll(mergeVertices);
	        
            // check which neighbors can be deleted
            neighborsToMerge.addAll(this.checkNeigbhorsForDeletion(graph, 
            		blockingGeometries,
            		untouchableVertices,
            		mergeCore,
            		mergeDistance,
            		verticesToDelete,
            		visibilityTolerance)); 
        	}

        // remove the vertices itself
		verticesToDelete.forEach(toRemove -> graph.removeVertex(toRemove));
		
	}

	@Override
	public void callPostProcessing(SimulationState simulationState) {
		// nothing to do
		
	}
	
	private Set<Vertex> checkNeigbhorsForDeletion(Graph graph, 
    		Collection<Geometry2D> blockingGeometries, 
    		HashSet<Vertex> untouchableVertices,
    		Vertex mergeCore,
    		double mergeDistance,
    		HashSet<Vertex> verticesToDelete,
    		double visibilityTolerance) {

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
	            		visibilityTolerance);
    	 	 
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
