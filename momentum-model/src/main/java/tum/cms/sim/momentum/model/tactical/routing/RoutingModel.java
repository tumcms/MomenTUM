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

package tum.cms.sim.momentum.model.tactical.routing;

import java.util.HashSet;
import java.util.Set;

import tum.cms.sim.momentum.data.agent.pedestrian.state.tactical.RoutingState;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;
import tum.cms.sim.momentum.data.agent.pedestrian.types.ITacticalPedestrian;
import tum.cms.sim.momentum.data.layout.ScenarioManager;
import tum.cms.sim.momentum.model.support.perceptional.PerceptionalModel;
import tum.cms.sim.momentum.model.tactical.SubTacticalModel;
import tum.cms.sim.momentum.utility.graph.GraphTheoryFactory;
import tum.cms.sim.momentum.utility.graph.Path;
import tum.cms.sim.momentum.utility.graph.Vertex;

public abstract class RoutingModel extends SubTacticalModel {

	/**
	 * Override this method in a routing model in order to 
	 * provide specific decision support regarding:
	 * Is the current vertex visited?
	 * The default implementation uses an  on sight approach.
	 * 
	 * @return true if the current vertex is reached.
	 */
	public boolean checkIsVertexVisited() {
	
		return true; // default check in rerouting Necessary
	}
	
	public RoutingState updateRouteState(PerceptionalModel peception,
			ITacticalPedestrian pedestrian,
			Path newRoute) {
		
		Vertex nextToLastVertex = null;
		Vertex lastVertex = null;
		Set<Vertex> visited = null;
		
		if(pedestrian.getRoutingState() != null) {
			
			nextToLastVertex = pedestrian.getRoutingState().getLastVisit();
			lastVertex = pedestrian.getRoutingState().getNextVisit();
			visited = pedestrian.getRoutingState().getVisited();
			visited.add(lastVertex);
		}
		else {
			
			lastVertex = newRoute.getFirstVertex();

			if(visited == null) {
				
				visited = new HashSet<Vertex>();
			}
			
			visited.add(lastVertex);
		}
		
		if(newRoute != null) {

			return new RoutingState(visited,
					nextToLastVertex,
					lastVertex,
					newRoute.getCurrentVertex());
		}
		
		return null;
	}

	/**
	 * Check if a pedestrian needs to reroute.
	 * Only if current vertex is not visible
	 * or if the next vertex is visible
	 * @param pedestrian
	 * @param tacticalControlActive, activates next vertex visible check
	 * @return if true, reroute
	 */
	public boolean reRoutingNecessary(IRichPedestrian pedestrian, boolean tacticalControlActive) {
		
		if(pedestrian.getRoutingState() == null) {
			
			return true;
		}	

		boolean currentWalkingTargetVisible = perception.isVisible(pedestrian.getPosition(), pedestrian.getRoutingState().getNextVisit());
		
		// is the current walking target not visible?! reroute
		if(!currentWalkingTargetVisible) {
			
			pedestrian.setRoutingState(new RoutingState(pedestrian.getRoutingState().getVisited(),
						pedestrian.getRoutingState().getNextToLastVisit(),
						pedestrian.getRoutingState().getLastVisit(),
						null));
			
			return true;
		}
		
		if(tacticalControlActive && pedestrian.getRoutingState().getNextToCurrentVisit() != null) {
			
			boolean nextWalkingTargetVisible = perception.isVisible(pedestrian.getPosition(), pedestrian.getRoutingState().getNextToCurrentVisit());
			
			// is the next walking target not visible?! reroute
			if(nextWalkingTargetVisible && this.checkIsVertexVisited()) {
				
				return true;
			}
		}
	
		return false;
	}
	
	/**
	 * Exchange the last vertex of the route with another vertex based on the point of interest
	 * this will improve the routing behavior. Sometimes walking to the center of the goal is
	 * not the ideal strategy! E.g. queuing behavior.
	 * 
	 * Also, if the strategic goal is visible the next vertex will automatically set to the goal!
	 * This will reduce unnecessary zig-zak routing.
	 */
	public boolean shortCutRoute(PerceptionalModel perception, ITacticalPedestrian pedestrian) {
		
		boolean shortCutSuccessful = false;
		
		if(pedestrian.getRoutingState() != null && pedestrian.getRoutingState().getNextVisit() != null) {
			
			boolean goalAreaEqualsPointOfInterest = pedestrian.getNextNavigationTarget().getGeometry().getCenter().equals(
					pedestrian.getNextNavigationTarget().getPointOfInterest());
			
			Vertex nextVisit = pedestrian.getRoutingState().getNextVisit();

			if(goalAreaEqualsPointOfInterest) { 
				
				if(perception.isVisible(pedestrian.getPosition(), pedestrian.getNextNavigationTarget().getPointOfInterest())) {
					
					// the point of interest is the goal area and the goal area is visible
					// reset to correct goal location target
					if(this.scenarioManager.getGraphs().size() > 0) {
						
						nextVisit = this.scenarioManager.getGraph().getGeometryVertex(pedestrian.getNextNavigationTarget().getGeometry());	
					}
					else {
						
						nextVisit = GraphTheoryFactory.createVertex(pedestrian.getNextNavigationTarget().getGeometry());		
					}
					
					shortCutSuccessful = true;
					
					pedestrian.setRoutingState(new RoutingState(pedestrian.getRoutingState().getVisited(),
							pedestrian.getRoutingState().getNextToLastVisit(),
							pedestrian.getRoutingState().getLastVisit(),
							nextVisit));
				}
			}
			else {
				
				if(perception.isVisible(pedestrian.getPosition(), pedestrian.getNextNavigationTarget().getPointOfInterest()) ) { 
					
					// the point of interest is visible and it is not the goal area center and not some normal 
					// navigation point, select the point of interest as dynamic walking target
					nextVisit = GraphTheoryFactory.createVertexCyleBased(pedestrian.getNextNavigationTarget().getPointOfInterest(), -1);
					shortCutSuccessful = true;

					pedestrian.setRoutingState(new RoutingState(pedestrian.getRoutingState().getVisited(),
							pedestrian.getRoutingState().getNextToLastVisit(),
							pedestrian.getRoutingState().getLastVisit(),
							nextVisit));
				}
			}
		}
		
		return shortCutSuccessful;
	}

	/**
	 * Finds the next vertex close to the current position
	 */
	public Vertex findNavigationStartPoint(ITacticalPedestrian pedestrian,
			PerceptionalModel perception,
			ScenarioManager scenarioManager) {
		
		Vertex startVertex = null;
		Vertex end = this.scenarioManager.getGraph().getGeometryVertex(pedestrian.getNextNavigationTarget().getGeometry());
		
		if(pedestrian.getRoutingState() == null || // no routing behavior present, find new start
		   pedestrian.getRoutingState().getNextVisit() == null || // re-routing because next visit is not visible
		   pedestrian.getRoutingState().getNextVisit().getId() == end.getId() || // at the end of routing (shortcut)
		   pedestrian.getRoutingState().getNextVisit().isTemporary()) { // old vertex was temporary

			int maximalInteration = 50;
			Vertex startVertexTemp = null;
			HashSet<Vertex> toIgnore = new HashSet<Vertex>();

			while(maximalInteration > 0) {
				
				startVertexTemp = this.scenarioManager.getGraph().findVertexClosestToPosition(
						pedestrian.getPosition(),
						toIgnore);

				if(startVertexTemp != null) {
					
					if(perception.isVisible(pedestrian.getPosition(), startVertexTemp)) {
						
						startVertex = startVertexTemp;
						break;
					}
					else {
						
						toIgnore.add(startVertexTemp);
					}

					
					maximalInteration--;
				}
				else {
					
					maximalInteration = 0;
				}
			}
			
			if(startVertex == null) {
				
				startVertex = this.scenarioManager.getGraph().findVertexClosestToPosition(
						pedestrian.getPosition(),
						null);
			}
		}
		else {
			
			startVertex = pedestrian.getRoutingState().getNextVisit();
		}
			

		return startVertex;
	}
}
