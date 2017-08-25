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

package tum.cms.sim.momentum.utility.graph.pathAlgorithm;

import java.util.ArrayList;
import java.util.HashMap;

import tum.cms.sim.momentum.utility.graph.Graph;
import tum.cms.sim.momentum.utility.graph.Path;
import tum.cms.sim.momentum.utility.graph.Vertex;
import tum.cms.sim.momentum.utility.graph.pathAlgorithm.pathOperation.GenericPathReconstructor;
import tum.cms.sim.momentum.utility.graph.pathAlgorithm.weightOperation.DirectWeightCalculatur;

/**
 * Generic Dijkstra version
 * @author PK
 *
 */
public class ShortestPathAlgorithm {

    private ArrayList<Vertex> candidates = null;
    private HashMap<Vertex,Vertex> predecessorMap = null;

    public HashMap<Vertex, Vertex> getPredecessorMap() {
		return predecessorMap;
	}

	private DirectWeightCalculatur weightCalculator = null;
    
    public ShortestPathAlgorithm(DirectWeightCalculatur weightCalculator) {
    	
    	this.weightCalculator = weightCalculator;
    }
    
    public Path calculateShortestPath(Graph graph, Vertex start, Vertex target) {
    	
    	if(target == null || start == null || graph == null) {
    			
    		return null;
    	}
    	
    	// init candidate list
    	candidates = new ArrayList<Vertex>();
    	candidates.addAll(graph.getVertices());
    	 	 	
    	predecessorMap = new HashMap<Vertex,Vertex>(); // map of predecessor for each vertex
    	graph.getVertices().stream().forEach(vertex -> predecessorMap.put(vertex, null)); // predecessor is null	
    	weightCalculator.initializeWeightsForStart(graph, start);
    	
    	Path shortestPath = null;
    	
       	if(!start.getId().equals(target.getId())) {

       		shortestPath = calculate(graph, start, target);    		
		}
       	else {   	
       		
       		shortestPath = new Path(start, target);  
       	}
       	
		return shortestPath;
    }

    private Path calculate(Graph graph, Vertex start, Vertex target) {
    	
        Vertex current = start;
        double weight = Double.POSITIVE_INFINITY;
        Path shortestPath = null;

        while (!candidates.isEmpty() && start.getId() != target.getId()) {

        	if(candidates.size() > 1) {
        		
            	candidates = weightCalculator.sort(candidates); // sort candidate list according to lowest vertex weight
        	}
    
        	current = candidates.remove(candidates.size() - 1); // the smallest is at the end

            if(current.getId().equals(target.getId()) && predecessorMap.get(current) != null) {

            	shortestPath = GenericPathReconstructor.reconstructPath(start, target, predecessorMap);

            	break;
            }

            // check neighbors for weight update
            for(Vertex successor : graph.getSuccessorVertices(current)) {
            	 	
            	weight = weightCalculator.calculateWeight(graph, null, target, current, successor);
  
				if(weightCalculator.compareWeight(successor, weight)) {
	
					weightCalculator.updateWeight(graph, weight, null, target, current, successor);
					predecessorMap.put(successor, current);
				}
            }
        }

        return shortestPath;
    }
}
