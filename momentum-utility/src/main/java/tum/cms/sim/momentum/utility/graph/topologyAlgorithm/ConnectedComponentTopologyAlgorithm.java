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

package tum.cms.sim.momentum.utility.graph.topologyAlgorithm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import tum.cms.sim.momentum.utility.graph.Graph;
import tum.cms.sim.momentum.utility.graph.Vertex;

public class ConnectedComponentTopologyAlgorithm {
	
    public HashMap<Vertex, Integer> calculateConnectedComponents(Graph graph) {
    	 
    	HashMap<Vertex, Integer> marks = new HashMap<Vertex, Integer>();
    	int connectedComponentNumber = 0;
    	
        // iterate through all vertices, search if not already marked
        for (Vertex vertex : graph.getVertices()) {
        	
            if (this.notVisited(vertex, marks)) {
            	
            	connectedComponentNumber++; // new connected component
                this.depthFirstSearch(graph, vertex, marks, connectedComponentNumber);
            }
        }
        
        return marks;
    }
    
    /**
     * Returns a list of unreachable vertices
     * @author qa
     * @param graph
     * @param knownSeedVertices
     */
    public Collection<Vertex> getUnreachableVertices(Graph graph, Collection<Vertex> knownSeedVertices) {
    	
    	ArrayList<Vertex> unreachable = new ArrayList<Vertex>();
		HashMap<Vertex, Integer> marks = new HashMap<Vertex, Integer>();
    	int connectedComponentNumber = 0;

    	knownSeedVertices.stream().forEach(seed -> this.depthFirstSearch(graph, seed, marks, connectedComponentNumber));
    	
    	graph.getVertices().stream()
    		.forEach(vertex -> {
    			
	    		if(this.notVisited(vertex, marks)) {
	    			unreachable.add(vertex);
	    		}
	    	});
		
		return unreachable;
	}
    
    private void depthFirstSearch(Graph graph, Vertex vertex, HashMap<Vertex, Integer> marks, Integer connectedComponentNumber) {
    	
        // mark this vertex with the connected component id
    	marks.put(vertex, connectedComponentNumber);

        // check for all neighbors
        Vertex currentNeighbor = null;
        Collection<Vertex> neigbhors = graph.getSuccessorVertices(vertex);
        Iterator<Vertex> neighborIt = neigbhors.iterator();

        while (neighborIt.hasNext()) {

			currentNeighbor = neighborIt.next();
			
			if (this.notVisited(currentNeighbor, marks)) {
				
				depthFirstSearch(graph, currentNeighbor, marks, connectedComponentNumber);
			}
        }
    }

    // true, if vertex has not been visited yet
    private boolean notVisited(Vertex visitCheckVertex, HashMap<Vertex, Integer> marks) {
    	
        return !marks.containsKey(visitCheckVertex) || marks.get(visitCheckVertex) == null;
    }
}
