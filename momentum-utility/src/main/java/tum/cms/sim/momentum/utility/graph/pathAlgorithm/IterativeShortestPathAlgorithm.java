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

import java.util.HashMap;
import java.util.Random;
import java.util.Set;

import tum.cms.sim.momentum.utility.graph.Graph;
import tum.cms.sim.momentum.utility.graph.Path;
import tum.cms.sim.momentum.utility.graph.Vertex;
import tum.cms.sim.momentum.utility.graph.pathAlgorithm.selectorOperation.VertexSelector;
import tum.cms.sim.momentum.utility.graph.pathAlgorithm.weightOperation.IterativeWeightCalculator;

public class IterativeShortestPathAlgorithm {

	private IterativeWeightCalculator weightCalculator = null;
    private VertexSelector selector = null;

	public IterativeShortestPathAlgorithm(IterativeWeightCalculator weightCalculator, VertexSelector selector) {
    	
    	this.weightCalculator = weightCalculator;
    	this.selector = selector;
    }
    
	public Path calculateNextPath(Graph graph, Set<Vertex> visited, Vertex previousVertex, Vertex start, Vertex target, int depth) {
		
  		Path path = null;
  		
		if(previousVertex != null) {
		
			path = new Path(previousVertex, start);
		}
		else {
			
			path = new Path(start, target);
		}
		
		if(target.equals(start)) {
			
		    return new Path(start, target);
		}
		
		Vertex next = start;
		path.appendVertex(start);
		//int sections = 0;
		
		//while(sections++ < depth) {
			
			next = selectBestSuccessor(graph, visited, previousVertex, start, target);
	    	
	        if(next == null) { // dead alley
	        	
	        	Object[] nextVertices = graph.getSuccessorVertices(start).toArray();	
	        	next = (Vertex)nextVertices[new Random().nextInt(nextVertices.length)];
	        	depth = 0;
	        }
	        
	        previousVertex = start;
	        path.appendVertex(next);
	        //start = next;
	        
	       // if(sections == 1) {
	        	
	         path.setCurrentVertex(next);
	        //}
		//}

		return path;
	}
	
	private Vertex selectBestSuccessor(Graph graph, Set<Vertex> visited, Vertex previousVertex, Vertex current, Vertex target) {
		
        HashMap<Vertex, Double> selectionMap = new HashMap<Vertex, Double>();
        
        weightCalculator.preCalculateWeight(graph, previousVertex, current, target);
        
        for(Vertex successor : graph.getSuccessorVertices(current)) {
        	
        	if(visited == null || !visited.contains(successor)) {
        		
        		selectionMap.put(successor, weightCalculator.calculateWeight(graph, previousVertex, target, current, successor));
        	}
        }
        
        Vertex nextBestVertex = selector.selectNextVertex(selectionMap);

        return nextBestVertex;
	}
}
