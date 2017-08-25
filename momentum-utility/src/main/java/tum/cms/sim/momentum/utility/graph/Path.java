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
import java.util.List;

import tum.cms.sim.momentum.utility.graph.types.IimmutablePath;

public class Path implements IimmutablePath {
	
	private Vertex firstVertex = null;
	
	@Override
	public Vertex getFirstVertex() {
		
		return firstVertex;
	}
	public void setFirstVertex(Vertex firstVertex) {

		this.firstVertex = firstVertex;
	}
	
	private Vertex finalVertex = null;
	
	@Override
    public Vertex getFinalVertex() {
    	
		return finalVertex;
	}
    
	public void setLastVertex(Vertex lastVertex) {
		
		this.finalVertex = lastVertex;
	}

	private ArrayList<Vertex> vertexPath = new ArrayList<Vertex>();
    
    private int currentVertexIndex = -1;
	private int previousVertexIndex = -2;

	public List<Vertex> getVertexPath() {
		
		return this.vertexPath;
	}
	
	@Override
	public double distance() {
		
		double distance = 0.0;
		
		for(int iter = 0; iter < this.vertexPath.size() - 1; iter++) {
			
			distance += this.vertexPath.get(iter).euklidDistanceBetweenVertex(this.vertexPath.get(iter + 1));
		}

		return distance;
	}
	
	@Override
	public Vertex getPreviousVertex() {

		if(previousVertexIndex == -2) {
			
			return null;
		}
		else if(previousVertexIndex == -1) {
			
			return this.firstVertex;
		}
		
		return vertexPath.get(previousVertexIndex);
	}
	
	@Override
	public Vertex getCurrentVertex() {
		
		if(currentVertexIndex == -1) {
			
			return this.getFirstVertex();
		}
		else if(currentVertexIndex == Integer.MAX_VALUE) {
			
			return this.getFinalVertex();
		}

		return vertexPath.get(currentVertexIndex);
	}

	public Vertex getNextVertex() {
		
		if(vertexPath.size() == 0 || vertexPath.size() < currentVertexIndex + 2) {

			return null;
		}
		else {
			
			return vertexPath.get(currentVertexIndex + 1);
		}
	}
	
	public synchronized void setCurrentVertex(Vertex currentVertex) {
	  	
		for(int iter = 0; iter < this.getVertexPath().size(); iter++) {
    		
    		if(this.getVertexPath().get(iter).getId() == currentVertex.getId()) {
    			
    		   	currentVertexIndex = iter;
    		 	previousVertexIndex = iter - 1;
    		}	
    	} 	
    }
	
    public Path(IimmutablePath path, Vertex currentVertex) {

    	this.firstVertex = path.getFirstVertex();
    	this.finalVertex = path.getFinalVertex();
    	this.vertexPath = new ArrayList<Vertex>();

    	if(path != null) {

    		if(path.getVertexPath().size() == 0) {
    			
    			currentVertexIndex = Integer.MAX_VALUE;
    		}
    		else {
    			
		    	for(int iter = 0; iter < path.getVertexPath().size(); iter++) {
		    		
		    		if(path.getVertexPath().get(iter).getId() == currentVertex.getId()) {
		    			
		    		   	currentVertexIndex = iter;
		    		 	previousVertexIndex = iter - 1;
		    		}	
	
		    		this.vertexPath.add(path.getVertexPath().get(iter));
		    	} 
    		}
    	}
	}
    
	public Path(Vertex firstVertex, Vertex lastVertex) { 
		
	 	this.firstVertex = firstVertex;	 	
		this.vertexPath = new ArrayList<Vertex>();     	
		this.currentVertexIndex = Integer.MAX_VALUE;
    	this.finalVertex = lastVertex;
	}

	public synchronized void appendVertex(Vertex vertex) {
  	
    	vertexPath.add(vertex); 
    }
   
    public synchronized void prependVertex(Vertex vertex) {
      	
    	vertexPath.add(0, vertex);
    }


//	public synchronized Vertex removeCurrentIntermediate() {
//		
//		Vertex removed = null;
//		
//		if(vertexPath.size() > 0) {
//			
//			if(this.currentVertexIndex == vertexPath.size() - 1) {
//				
//				this.currentVertexIndex = vertexPath.size() - 2;
//				this.previousVertexIndex = vertexPath.size() - 3;
//			}
//			
//			removed = vertexPath.remove(vertexPath.size() - 1);
//		}
//		
//		return removed;
//	}
}
