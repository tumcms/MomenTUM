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

import tum.cms.sim.momentum.utility.graph.Vertex;

public class RoutingState {

	private Vertex lastVisit = null;

	public Vertex getLastVisit() {
		return lastVisit;
	}

	private Vertex nextVisit = null;

	public Vertex getNextVisit() {
		return nextVisit;
	}

	private Vertex nextToCurrentVisit = null;
	
	public Vertex getNextToCurrentVisit() {
		return nextToCurrentVisit;
	}
	
	public void setNextToCurrentVisit(Vertex nextToCurrent) {
		
		this.nextToCurrentVisit = nextToCurrent;
	}

	private Vertex nextToLastVisit = null;
	
	public Vertex getNextToLastVisit() {
		return nextToLastVisit;
	}

	private Set<Vertex> visited = new HashSet<Vertex>();

	public Set<Vertex> getVisited() {
		return visited;
	}

	public RoutingState(Set<Vertex> visitedVertices,
			Vertex nextToLastVist,
			Vertex lastVisit,
			Vertex nextVisit) {
		
		this.lastVisit = lastVisit;
		this.nextToLastVisit = nextToLastVist;
		this.nextVisit = nextVisit;
		this.visited = visitedVertices;
	}

	public void print() {
		
		String nextToLast = this.nextToLastVisit == null ?  "- " : String.valueOf(this.nextToLastVisit.getId()) + " "; 
		String last = this.lastVisit == null ?  "- " : String.valueOf(this.lastVisit.getId()) + " "; 
		String next = this.nextVisit == null ?  "- " : String.valueOf(this.nextVisit.getId()) + " "; 
		
		System.out.println(
				"NextToLast: " + nextToLast +
				"Last: " + last +
				"Next: " + next);
	}
}
