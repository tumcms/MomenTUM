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
import java.util.LinkedHashSet;
import java.util.Set;

import tum.cms.sim.momentum.data.agent.pedestrian.state.tactical.RoutingState;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;
import tum.cms.sim.momentum.data.agent.pedestrian.types.ITacticalPedestrian;
import tum.cms.sim.momentum.data.layout.ScenarioManager;
import tum.cms.sim.momentum.infrastructure.exception.NoRouteFoundException;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.infrastructure.logging.LoggingManager;
import tum.cms.sim.momentum.model.perceptional.PerceptionalModel;
import tum.cms.sim.momentum.model.tactical.SubTacticalModel;
import tum.cms.sim.momentum.utility.graph.GraphTheoryFactory;
import tum.cms.sim.momentum.utility.graph.Path;
import tum.cms.sim.momentum.utility.graph.Vertex;

public abstract class RoutingModel extends SubTacticalModel {

	/**
	 * Override this method in a routing model in order to 
	 * provide specific decision support regarding:
	 * Is the current vertex visited?
	 * This is additional to the is visible approach.
	 * And the navigation distance check will still be active.
	 */
	public boolean checkIsVertexVisited() {
	
		return true; // default check 
	}
	
	public RoutingState deepRouting(IRichPedestrian pedestrian, SimulationState simulationState, int deepNodeSelection) {
	
		Vertex nextToLast = pedestrian.getRoutingState().getNextToLastVisit();
		Vertex last = pedestrian.getRoutingState().getLastVisit();
		Vertex start = last;
		Vertex next = pedestrian.getRoutingState().getNextVisit();
		Vertex nextToNext = pedestrian.getRoutingState().getNextToCurrentVisit();
		
		Set<Vertex> visited = pedestrian.getRoutingState().getVisited();
		Vertex end = null;
		
		if(pedestrian.getNextNavigationTarget() != null) {
			end = scenarioManager.getGraph().getGeometryVertex(pedestrian.getNextNavigationTarget().getGeometry());
		}
		
		if(end == null || next == null || !end.equals(next)) {
			
			while(deepNodeSelection > 0) {
				
				this.callPedestrianBehavior(pedestrian, simulationState);
			
				RoutingState newRoutingState = pedestrian.getRoutingState();
				deepNodeSelection--;
				
				boolean isLoopedPath = (end != null && newRoutingState.getNextVisit().equals(end)) ||
						   (start != null && end != null && start.equals(end));
				boolean isNotVisible = !perception.isVisible(pedestrian, newRoutingState.getNextVisit());
				boolean isDepthStop = deepNodeSelection == 0;
				
				if(isLoopedPath || isNotVisible || isDepthStop) {
					
					nextToNext = newRoutingState.getNextVisit();
					visited.remove(next);
					break;
				}

				nextToLast = last;
				visited.add(next);
				
				last = newRoutingState.getLastVisit();
				next = newRoutingState.getNextVisit();
			}
		}
				
		RoutingState finalRoutingState = new RoutingState(visited, nextToLast, last, next);
		finalRoutingState.setNextToCurrentVisit(nextToNext);

		return finalRoutingState;
	}
	
	/**
	 * Updates the routing state by using the correct next, last nextToNext etc.
	 * vertices for the routing state. This is used to define a standard interface
	 * to fill the routing state. This will avoid errors due to implementations in routing models
	 * that do not know how to fill the routing state correctly.
	 * The nextToNextVertex is set only in case deep routing is used. This is done elsewhere.
	 * @throws NoRouteFoundException 
	 */
	public RoutingState updateRouteState(ITacticalPedestrian pedestrian, Path newRoute) {
		
		if(newRoute == null) {
			
			LoggingManager.logUser(new NoRouteFoundException());
		}
		
		Vertex nextToLastVertex = null;
		Vertex lastVertex = null;
		Set<Vertex> visited = null;
		Vertex nextVertex = newRoute.getCurrentVertex();
		
		if(pedestrian.getRoutingState() != null) {
			
			visited = pedestrian.getRoutingState().getVisited();
			
			// if nextVertex is null it cannot be found (not Visible etc.)
			// and we have to reset the behavior.
			// Thus, the agent was not able to visit the target next vertex
			if(pedestrian.getRoutingState().getNextVisit() == null) {
			
				nextToLastVertex = pedestrian.getRoutingState().getNextToLastVisit();
				lastVertex = pedestrian.getRoutingState().getLastVisit();
			}
			else { // the next visit was successful, shift the visit order
				
				if(pedestrian.getRoutingState().getLastVisit() != null) {
					
					nextToLastVertex = pedestrian.getRoutingState().getLastVisit();
				}
			
				if(pedestrian.getRoutingState().getNextVisit() != null) {
					
					lastVertex = pedestrian.getRoutingState().getNextVisit();
				}
				
				if(lastVertex != null) {
					
					visited.add(lastVertex);	
				}
			}
		}
		else { // first routing
				
			nextToLastVertex = newRoute.getPreviousVertex();
			lastVertex = newRoute.getFirstVertex();
			visited = new LinkedHashSet<Vertex>();
		}
		
		return new RoutingState(visited,
				nextToLastVertex,
				lastVertex,
				nextVertex);
	}

	public boolean isRouteStateEmpty(IRichPedestrian pedestrian) {
		
		if(pedestrian.getRoutingState() == null) {
			
			return true;
		}
		
		return false;
	}
	
	public boolean isNextRouteNotVisible(IRichPedestrian pedestrian) {

		if(pedestrian.getRoutingState().getNextVisit() == null) {
			
			return false;
		}
		
		double distanceToNextVisit = pedestrian.getRoutingState().getNextVisit().euklidDistanceBetweenVertex(pedestrian.getPosition());
		
		// is the current walking target not visible?! reroute
		// however, if it is not in perception range, keep routing. Thus, reroute if close and not visible
		if(distanceToNextVisit < perception.getPerceptionDistance() &&
		   !perception.isVisible(pedestrian, pedestrian.getRoutingState().getNextVisit())) {
			
			return true;
		}
		
		return false;
	}

	public boolean isNextNextRouteVisible(IRichPedestrian pedestrian, boolean isDeepSelect) {
		
		
		// Enables a more smooth routing because it addresses the vertex following the current one
		// is only done in case deep note selection is at least 1
		if(isDeepSelect && pedestrian.getRoutingState().getNextToCurrentVisit() != null) {
			
			double distanceToNextToCurrentVisit = pedestrian.getRoutingState().getNextToCurrentVisit().euklidDistanceBetweenVertex(pedestrian.getPosition());
			
			// Is the next next walking target visible?
			// If yes reroute to have a continuously not visible next to current vertex
			// Thus, the agent will have a more steady navigation flow.
			// However, if it is not in perception range, keep routing.
			if(this.checkIsVertexVisited() &&
			   distanceToNextToCurrentVisit < perception.getPerceptionDistance() &&
			   perception.isVisible(pedestrian, pedestrian.getRoutingState().getNextToCurrentVisit())) {
				
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
		
		if(pedestrian.getRoutingState() != null &&
		   pedestrian.getRoutingState().getNextVisit() != null &&
		   pedestrian.getNextNavigationTarget() != null) {
			
			boolean goalAreaEqualsPointOfInterest = pedestrian.getNextNavigationTarget().getGeometry().getCenter().equals(
					pedestrian.getNextNavigationTarget().getPointOfInterest());
			
			Vertex nextVisit = pedestrian.getRoutingState().getNextVisit();

			if(goalAreaEqualsPointOfInterest) { 
				
				if(perception.isVisible(pedestrian, pedestrian.getNextNavigationTarget().getPointOfInterest())) {
					
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
				
				if(perception.isVisible(pedestrian, pedestrian.getNextNavigationTarget().getPointOfInterest()) ) { 
					
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
					
					if(perception.isVisible(pedestrian, startVertexTemp) &&
					   this.scenarioManager.getGraph().getSuccessorEdges(startVertexTemp).size() > 0) {
						
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
