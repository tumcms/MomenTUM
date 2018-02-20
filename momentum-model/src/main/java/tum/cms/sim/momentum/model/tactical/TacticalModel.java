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

package tum.cms.sim.momentum.model.tactical;

import java.util.Collection;

import tum.cms.sim.momentum.configuration.ModelTypConstants.ModelType;
import tum.cms.sim.momentum.data.agent.pedestrian.state.tactical.RoutingState;
import tum.cms.sim.momentum.data.agent.pedestrian.state.tactical.TacticalState;
import tum.cms.sim.momentum.data.agent.pedestrian.state.tactical.TacticalState.Behavior;
import tum.cms.sim.momentum.data.agent.pedestrian.state.tactical.TacticalState.Motoric;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrianExtension;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.infrastructure.logging.LoggingManager;
import tum.cms.sim.momentum.model.MessageStrings;
import tum.cms.sim.momentum.model.PedestrianBehaviorModel;
import tum.cms.sim.momentum.model.tactical.participating.StayingModel;
import tum.cms.sim.momentum.model.tactical.queuing.QueuingModel;
import tum.cms.sim.momentum.model.tactical.routing.RoutingModel;
import tum.cms.sim.momentum.model.tactical.searching.SearchingModel;
import tum.cms.sim.momentum.utility.geometry.Vector2D;

public class TacticalModel extends PedestrianBehaviorModel {
	
	protected final static String strategicCommandName = "strategicCommand";
	protected final static String goalDistanceRadiusName = "goalDistanceRadius";
	protected final static String navigationDistanceRadiusName = "navigationDistanceRadius";
	protected final static String tacticalControlName = "tacticalControl";
	protected final static String deepNodeSelectionName = "deepNodeSelection";
	protected final static String routeMemoryName = "routeMemory";
	
	protected Behavior strategicFixedCommand = null;
	protected double goalDistanceRadius = 0.15;
	protected double navigationDistanceRadius = 0.15;
	protected boolean tacticalControl = true;
	protected boolean routeMemory = true;
	protected int deepNodeSelection = 0;

	private RoutingModel routingModel = null;
	
	public RoutingModel getRoutingModel() {
		return routingModel;
	}

	public void setRoutingModel(RoutingModel routingModel) {
		this.routingModel = routingModel;
	}

	private SearchingModel searchingModel = null;
	
	public SearchingModel getSearchingModel() {
		return searchingModel;
	}

	public void setSearchingModel(SearchingModel searchingModel) {
		this.searchingModel = searchingModel;
	}
	
	private QueuingModel queuingModel = null;
	
	public QueuingModel getQueuingModel() {
		return queuingModel;
	}

	public void setQueuingModel(QueuingModel queuingModel) {
		this.queuingModel = queuingModel;
	}
	
	private StayingModel stayingModel = null;

	public StayingModel getStayingModel() {
		return stayingModel;
	}

	public void setParticipatingModel(StayingModel stayingModel) {
		this.stayingModel = stayingModel;
	}
	
	@Override
	public ModelType getModelType() {
		
		return ModelType.Tactical;
	}
	
	@Override
	public IPedestrianExtension onPedestrianGeneration(IRichPedestrian pedestrian) {
		
		return null; 
	}

	@Override
	public void onPedestrianRemoval(IRichPedestrian pedestrian) {
		
		// Nothing to do
	}

	@Override
	public void callPreProcessing(SimulationState simulationState) {
		
		if(this.properties.getStringProperty(strategicCommandName) != null) {
			
			strategicFixedCommand = Behavior.valueOf(this.properties.getStringProperty(strategicCommandName));
		}

		if(this.properties.getDoubleProperty(goalDistanceRadiusName) != null) {
			
			goalDistanceRadius = this.properties.getDoubleProperty(goalDistanceRadiusName);
		}
		
		if(this.properties.getDoubleProperty(navigationDistanceRadiusName) != null) {
			
			navigationDistanceRadius = this.properties.getDoubleProperty(navigationDistanceRadiusName);
		}
		
		if(this.properties.getBooleanProperty(tacticalControlName) != null) {
			
			tacticalControl = this.properties.getBooleanProperty(tacticalControlName);
		}
		
		if(this.properties.getIntegerProperty(deepNodeSelectionName) != null) {
			
			deepNodeSelection = this.properties.getIntegerProperty(deepNodeSelectionName);
		}
		
		if(this.properties.getBooleanProperty(routeMemoryName) != null) {
			
			routeMemory = this.properties.getBooleanProperty(routeMemoryName);
		}
		
		LoggingManager.logDebug(MessageStrings.propertySetTo,
				navigationDistanceRadiusName,
				String.valueOf(navigationDistanceRadius),
				this.getClass().getSimpleName());
		
		LoggingManager.logDebug(MessageStrings.propertySetTo,
				goalDistanceRadiusName,
				String.valueOf(goalDistanceRadius),
				this.getClass().getSimpleName());
		
		LoggingManager.logDebug(MessageStrings.propertySetTo,
				tacticalControlName,
				String.valueOf(tacticalControl),
				this.getClass().getSimpleName());
		
		LoggingManager.logDebug(MessageStrings.propertySetTo,
				deepNodeSelectionName,
				String.valueOf(deepNodeSelection),
				this.getClass().getSimpleName());
		
		LoggingManager.logDebug(MessageStrings.propertySetTo,
				routeMemoryName,
				String.valueOf(routeMemory),
				this.getClass().getSimpleName());
		
		LoggingManager.logDebug(MessageStrings.propertySetTo,
				strategicFixedCommand,
				String.valueOf(strategicFixedCommand),
				this.getClass().getSimpleName());
	}

	@Override
	public void callPostProcessing(SimulationState simulationState) {
		
		// Nothing to do
	}

	@Override
	public void callBeforeBehavior(SimulationState simulationState, Collection<IRichPedestrian> pedestrians) {
		
		pedestrians.parallelStream().forEach(pedestrian -> {		
			
			boolean tacticalControlRouting = false;
			
			// Initial check: is goal visible and does the pedestrian has active behavior?
			// If not, due to some circumstances, start routing to the goal until it can be seen.
			if(this.tacticalControl && this.checkCommandExecutable(pedestrian)) {
				
				// The strategic command cannot be executed -> route until the the walking goal is visible
				// This describes a bottom up control "fighting" the strategic command.
				tacticalControlRouting = true;
				pedestrian.getTacticalState().setOverrideBehaviorTask(Behavior.Routing);
			}

			if(tacticalControlRouting || !this.getBehavior(pedestrian).equals(Behavior.Staying)) {
				
				pedestrian.setStayingState(null);
			}

			if(tacticalControlRouting || !this.getBehavior(pedestrian).equals(Behavior.Queuing)) {
				
				pedestrian.setQueuingState(null);
			}
			
			if(tacticalControlRouting || !this.getBehavior(pedestrian).equals(Behavior.Searching)) {
							
				pedestrian.setSearchingState(null);
			}
			
			if(!tacticalControlRouting && !this.getBehavior(pedestrian).equals(Behavior.Routing)) {

				pedestrian.setRoutingState(null);
			}
		});
		
		if(this.stayingModel != null) {
		
			this.stayingModel.callBeforeBehavior(simulationState, pedestrians);
		}
		
		if(this.queuingModel != null) {
			
			this.queuingModel.callBeforeBehavior(simulationState, pedestrians);
		}
		
		if(this.routingModel != null) {
			
			this.routingModel.callBeforeBehavior(simulationState, pedestrians);
		}
		
		if(this.searchingModel != null) {
			
			this.searchingModel.callBeforeBehavior(simulationState, pedestrians);
		}
	}
	
	private boolean checkCommandExecutable(IRichPedestrian pedestrian) { 

		if(this.getBehavior(pedestrian) == null) {
			
			return false;
		}
		
		boolean noCommandGiven = this.getBehavior(pedestrian).equals(Behavior.None);
		
		if(noCommandGiven) {
			
			return false;
		}
		
		boolean routingCommandGiven = this.getBehavior(pedestrian).equals(Behavior.Routing);
		
		if(routingCommandGiven) {
			
			return false;
		}
		
		boolean queuingActive = pedestrian.getQueuingState() != null;
		boolean stayingActive = pedestrian.getStandingState() != null;
		
		if(queuingActive || stayingActive) {
			
			return false;
		}
		
		boolean goalIsVisible = this.isGoalTargetVisible(pedestrian);
		
		return !goalIsVisible; // if not visible the check failed!
	}

	private Behavior getBehavior(IRichPedestrian pedestrian) {
		
		Behavior behavior = Behavior.None;
		
		if(this.strategicFixedCommand != null) {
			
			behavior = this.strategicFixedCommand;
		}
		else {
			
			behavior = pedestrian.getBehaviorTask();
		}
		
		return behavior;
	}
	
	@Override
	public void callPedestrianBehavior(IRichPedestrian pedestrian, SimulationState simulationState) {

		Behavior command = this.getBehavior(pedestrian);
		
		// In case the strategic command cannot be executed, it is overridden with Routing
		// If this is the case, and tacticalControl is activated, execute routing behavior.
		if(this.tacticalControl && 
		   pedestrian.getTacticalState() != null && 
		   pedestrian.getTacticalState().getOverrideBehaviorTask() != null) {
				
			command = pedestrian.getTacticalState().getOverrideBehaviorTask();
		}
	
  		switch(command) {
  		
		case Staying:
			
			this.callTacticStayingBehavior(pedestrian, simulationState);
			break;
			
		case Queuing:
			
			this.callTactiQueuingBehavior(pedestrian, simulationState);
			break;
			
		case Searching:
			
			this.callTacticSearchBehavior(pedestrian, simulationState);
			break;
			
		case Routing:
			
			this.callTacticRouteBehavior(pedestrian, simulationState);
			break;
			
		case None:
		default:
			
			break;
  		}
	}
	
	@Override
	public void callAfterBehavior(SimulationState simulationState, Collection<IRichPedestrian> pedestrians) {
	
		if(this.stayingModel != null) {
			
			this.stayingModel.callAfterBehavior(simulationState, pedestrians);
		}
		
		if(this.queuingModel != null) {
			
			this.queuingModel.callAfterBehavior(simulationState, pedestrians);
		}
		
		if(this.routingModel != null) {
			
			this.routingModel.callAfterBehavior(simulationState, pedestrians);
		}
		
		if(this.searchingModel != null) {
			
			this.searchingModel.callAfterBehavior(simulationState, pedestrians);
		}

		pedestrians.parallelStream().forEach(pedestrian -> {			
	
			boolean isSmallScaleBehavior = false;
			Vector2D nextWalkingTarget = pedestrian.getNextWalkingTarget();
			
			if(pedestrian.getStayingState() != null ||
			   pedestrian.getQueuingState() != null) {
				
				isSmallScaleBehavior = true;
			}
			else if(pedestrian.getRoutingState() != null ||
					pedestrian.getSearchingState() != null) {
			
				isSmallScaleBehavior = false;
			}
			else { // no target, nothing to do

				isSmallScaleBehavior = true;
			}
			
			Motoric motoricTask = null;

			boolean isReadyToStand = isSmallScaleBehavior && 
					(pedestrian.getStandingState() != null || this.isGoalPositionReached(nextWalkingTarget, pedestrian.getPosition()));
			
			if(isReadyToStand) {

				motoricTask = Motoric.Standing;		
			}
			else {
				
				motoricTask = Motoric.Walking;
			}
			
			pedestrian.setTacticalState(new TacticalState(motoricTask));
		});
	}

	/**
	 * Checks if the pedestrian can see the goal area or the point of interest.
	 * @param pedestrian which for checking
	 * @return true if visible, otherwise false
	 */
	private boolean isGoalTargetVisible(IRichPedestrian pedestrian) {
		
		return perception.isVisible(pedestrian, pedestrian.getNextNavigationTarget().getPointOfInterest());
	}
	
	/**
	 * Calculates if the pedestrian is close to the given position
	 * @param pedestrian which for checking
	 * @return true if visible, otherwise false
	 */	
	private boolean isGoalPositionReached(Vector2D pedestrianPosition, Vector2D targetPosition) {
		
		return targetPosition != null && targetPosition.distance(pedestrianPosition) <= this.goalDistanceRadius;
	}
	
	private void callTacticRouteBehavior(IRichPedestrian pedestrian, SimulationState simulationState) {
				
		// identify if the agent is close to its next navigation vertex
		boolean isCloseToVertex = pedestrian.getRoutingState() == null || 
			pedestrian.getRoutingState().getNextVisit() == null ||
			pedestrian.getPosition().distance(pedestrian.getRoutingState().getNextVisit().getGeometry().getCenter()) < navigationDistanceRadius;
		
		if(isCloseToVertex && !tacticalControl) {
			
			this.routingModel.callPedestrianBehavior(pedestrian, simulationState);	
		}
		else if(tacticalControl) {
			
			this.callTacticalControlRouting(pedestrian, simulationState, isCloseToVertex);
		}
		
		// Is the route memory activated, if not delete it
		if(!routeMemory && pedestrian.getRoutingState() != null) {
			
			pedestrian.getRoutingState().getVisited().clear();
		}
	}
	
	private void callTacticalControlRouting(IRichPedestrian pedestrian, SimulationState simulationState, boolean isCloseToVertex) {
		
		// correct goal / point of interest is visible, just go there!
		boolean normalRouting  = !this.routingModel.shortCutRoute(this.perception, pedestrian);
	
		boolean routingStateIsEmpty = this.routingModel.isRouteStateEmpty(pedestrian);
		boolean notVisible = false;
		boolean nextNextVisible = false;
		
		if(!routingStateIsEmpty) {
			
			notVisible = this.routingModel.isNextRouteNotVisible(pedestrian);
			nextNextVisible = this.routingModel.isNextNextRouteVisible(pedestrian, this.deepNodeSelection > 0);
		}
				
		if(normalRouting && (isCloseToVertex || routingStateIsEmpty || notVisible || nextNextVisible)) {
			
			if(notVisible && !routingStateIsEmpty) {
			
				pedestrian.getRoutingState().setNextVisit(null);
			}
			
			this.routingModel.callPedestrianBehavior(pedestrian, simulationState);

			if(this.deepNodeSelection > 0 && (nextNextVisible || isCloseToVertex)) {
			
				RoutingState deepRoutingResult = this.routingModel.deepRouting(pedestrian, simulationState, this.deepNodeSelection);
				pedestrian.setRoutingState(deepRoutingResult);
			}
		}			
	}

	private void callTacticSearchBehavior(IRichPedestrian pedestrian, SimulationState simulationState) {
		
		this.searchingModel.callPedestrianBehavior(pedestrian, simulationState);
	}
	
	private void callTacticStayingBehavior(IRichPedestrian pedestrian, SimulationState simulationState) {
		
		this.stayingModel.callPedestrianBehavior(pedestrian, simulationState);
	}
	
	private void callTactiQueuingBehavior(IRichPedestrian pedestrian, SimulationState simulationState) {
		
		this.queuingModel.callPedestrianBehavior(pedestrian, simulationState);
	}	
}
