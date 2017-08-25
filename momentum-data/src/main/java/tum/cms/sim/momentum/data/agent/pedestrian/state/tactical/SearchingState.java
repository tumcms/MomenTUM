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

package tum.cms.sim.momentum.data.agent.pedestrian.state.tactical;

import java.util.HashSet;
import java.util.Set;

import tum.cms.sim.momentum.utility.geometry.Polygon2D;
import tum.cms.sim.momentum.utility.graph.Vertex;

public class SearchingState {
	
	private Vertex nextVisit = null;
	
	public Vertex getNextVisit() {
		return nextVisit;
	}

	public void setNextVisit(Vertex nextVisit) {
		this.nextVisit = nextVisit;
	}

	private Vertex lastSearchVisit = null;
	
	public Vertex getLastSearchVisit() {
		return lastSearchVisit;
	}

	private Vertex nextSearchVisit = null;
	
	public Vertex getNextSearchVisit() {
		return nextSearchVisit;
	}

	private Polygon2D searchSpace = null;

	public Polygon2D getSearchSpace() {
		return searchSpace;
	}

	private Set<Vertex> visited = new HashSet<Vertex>();

	public Set<Vertex> getVisited() {
		return visited;
	}

	public SearchingState(Vertex nextVisit, 
			Vertex lastSearchVisit, 
			Vertex nextSearchVisit,
			Polygon2D searchSpace,
			Set<Vertex> visitedVertices) {
		
		this.nextVisit = nextVisit;
		this.lastSearchVisit = lastSearchVisit;
		this.nextSearchVisit = nextSearchVisit;
		this.searchSpace = searchSpace;
		this.visited = visitedVertices;
	}
	
	public SearchingState(SearchingState otherState) {
		
		this.nextVisit = otherState.getNextVisit();
		this.lastSearchVisit = otherState.getLastSearchVisit();
		this.nextSearchVisit = otherState.getNextSearchVisit();
		this.searchSpace = otherState.getSearchSpace();
		this.visited = otherState.getVisited();
	}
}
