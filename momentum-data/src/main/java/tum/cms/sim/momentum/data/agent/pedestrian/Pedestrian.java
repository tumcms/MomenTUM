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

package tum.cms.sim.momentum.data.agent.pedestrian;

import java.util.HashMap;

import tum.cms.sim.momentum.data.agent.pedestrian.state.operational.*;
import tum.cms.sim.momentum.data.agent.pedestrian.state.other.MetaState;
import tum.cms.sim.momentum.data.agent.pedestrian.state.other.StaticState;
import tum.cms.sim.momentum.data.agent.pedestrian.state.strategic.StrategicalState;
import tum.cms.sim.momentum.data.agent.pedestrian.state.tactical.*;
import tum.cms.sim.momentum.data.agent.pedestrian.state.tactical.TacticalState.Behavior;
import tum.cms.sim.momentum.data.agent.pedestrian.state.tactical.TacticalState.Motoric;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrianExtansion;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;
import tum.cms.sim.momentum.data.layout.area.Area;
import tum.cms.sim.momentum.utility.generic.Unique;
import tum.cms.sim.momentum.utility.geometry.Vector2D;
import tum.cms.sim.momentum.utility.graph.Vertex;

public class Pedestrian extends Unique implements IRichPedestrian {
	
	protected PedestrianState state = new PedestrianState();
	
	public Pedestrian(StaticState staticState) {
		
		state.staticState = staticState;
	}
	
	public void updateStrategicalState(Pedestrian originalPedestrian) {
		
		state.strategicalState = originalPedestrian.getStrategicalState();
	}
	
	public void updateTacticalState(Pedestrian originalPedestrian) {
		
		state.stayingState = originalPedestrian.getStayingState();
		state.queuingState = originalPedestrian.getQueuingState();
		state.routingState = originalPedestrian.getRoutingState();
		state.searchingState = originalPedestrian.getSearchingState();
	}
	
	public void updateOperationalState(Pedestrian originalPedestrian) {

		state.standingState = originalPedestrian.getStandingState();
		state.walkingState = originalPedestrian.getWalkingState();
	}
	
	public void updateExtension(IExtendsPedestrian modelReference, Pedestrian originalPedestrian) {
		
		state.extensionContainer.put(modelReference, originalPedestrian.getExtensionState(modelReference));
	}
	
	// IPedestrian
	
	@Override
	public boolean isLeader() {
		
		return state.staticState.isLeader();
	}
	
	@Override
	public double getBodyRadius() {
		
		return state.staticState.getBodyRadius();
	}
	
	@Override
	public double getMass() {
		
		return state.staticState.getMass();
	}
	
	@Override
	public int getGroupId() {
		
		return state.staticState.getGroupId();
	}
	
	@Override
	public int getStartLocationId() {
		
		return state.staticState.getStartLocationId();
	}
	
	@Override
	public int getSeedId() {
		
		return state.staticState.getSeedId();
	}
	
	@Override
	public int getGroupSize() {
		
		return state.staticState.getGroupSize();
	}
	
	@Override
	public double getDesiredVelocity() {
		
		return state.staticState.getDesiredVelocity();
	}
	
	@Override
	public double getMaximalVelocity() {
		
		return state.staticState.getMaximalVelocity();
	}

	@Override
	public Motoric getMotoricTask() {

		return state.tacticalState == null ? Motoric.Standing : state.tacticalState.getMotoricTask();
	}

	@Override
	public Vector2D getPosition() {
		
		if(state.standingState != null) {
			
			return state.standingState.getStandingPosition();
		}
		
		return state.walkingState.getWalkingPosition();
	}
	
	@Override
	public Vector2D getHeading() {
		
		if(state.standingState != null) {
			
			return state.standingState.getStandingHeading();
		}
		
		return state.walkingState.getWalkingHeading();
	}
	
	@Override
	public Vector2D getVelocity() {
		
		if(state.standingState != null) {
			
			return state.standingState.getStandingVelocity();
		}

		return state.walkingState.getWalkingVelocity();
	}
	
	@Override
	public Vertex getLastWalkingTarget() {
		
		if(state.stayingState != null) {
			
			return state.stayingState.getLastVisit();
		}
		else if(state.queuingState != null) {
			
			return state.queuingState.getLastVisit();
		}
		else if(state.routingState != null) {
			
			return state.routingState.getLastVisit();
		}
		else if(state.searchingState != null) {
			
			return state.searchingState.getLastSearchVisit();
		}
		
		return null;
	}
	
	@Override
	public Vector2D getNextHeading() {
		
		if(state.stayingState != null && state.stayingState.getStayingHeading() != null) {
			
			return state.stayingState.getStayingHeading();
		}
		else if(state.queuingState != null && state.queuingState.getQueuingHeading() != null) {
			
			return state.queuingState.getQueuingHeading();
		}
		else if(state.standingState != null && state.standingState.getStandingHeading() != null) {
			
			return state.standingState.getStandingHeading();
		}
		else if(state.walkingState != null && state.walkingState.getWalkingHeading() != null) {
			
			return state.walkingState.getWalkingHeading();
		}
		
		return null;
	}
	
	@Override
	public Vector2D getNextWalkingTarget() {

		if(state.stayingState != null && state.stayingState.getStayingPosition() != null) {
			
			return state.stayingState.getStayingPosition();
		}
		else if(state.queuingState != null && state.queuingState.getQueuingPosition() != null) {
			
			return state.queuingState.getQueuingPosition();
		}
		else if(state.routingState != null && state.routingState.getNextVisit() != null) {
			
			return state.routingState.getNextVisit().getGeometry().getCenter();
		}
		else if(state.searchingState != null && state.searchingState.getNextSearchVisit() != null) {
			
			return state.searchingState.getNextSearchVisit().getGeometry().getCenter();
		}
		else if(state.standingState != null && state.standingState.getStandingPosition() != null) {
			
			return state.standingState.getStandingPosition();
		}
		else if(state.walkingState != null && state.walkingState.getWalkingPosition() != null) {
			
			return state.walkingState.getWalkingPosition();
		}
		
		return null;
	}

	/* Strategic */
	
	@Override
	public Behavior getBehaviorTask() {
		
		return this.state.strategicalState.getTacticalBehavior();
	}

	@Override
	public Area getNextNavigationTarget() {

		return state.strategicalState.getNextTargetArea();
	}
	
	/* Extension */
	
	public IPedestrianExtansion getExtensionState(IExtendsPedestrian modelReference) {
		
		return state.extensionContainer.get(modelReference);
	}


	// IPedestrian

	@Override
	public void setLeader(Boolean isLeader) {

		state.staticState.setLeader(isLeader);
	}

	@Override
	public WalkingState getWalkingState() {
		
		return state.walkingState;
	}

	@Override
	public StandingState getStandingState() {
	
		return state.standingState;
	}

	@Override
	public Behavior getBehavior() {
	
		if(state.stayingState != null) {
			
			return Behavior.Staying;
		}
		else if(state.queuingState != null) {
			
			return Behavior.Queuing;
		}
		else if(state.routingState != null) {
			
			return Behavior.Routing;
		}
		else if(state.searchingState != null) {
			
			return Behavior.Searching;
		}
		
		return Behavior.None;
	}

	@Override
	public TacticalState getTacticalState() {
		
		return state.tacticalState;
	}
	
	@Override
	public StayingState getStayingState() {

		return state.stayingState;
	}

	@Override
	public QueuingState getQueuingState() {

		return state.queuingState;
	}

	@Override
	public RoutingState getRoutingState() {

		return state.routingState;
	}
	
	@Override
	public SearchingState getSearchingState() {

		return state.searchingState;
	}

	@Override
	public StrategicalState getStrategicalState() {

		return state.strategicalState;
	}
	
	@Override
	public MetaState getMetaState() {
		return state.metaState;
	}

	@Override
	public void setTacticalState(TacticalState tacticalState) {
	
		state.tacticalState = tacticalState;
	}
	
	@Override
	public void setExtensionState(IPedestrianExtansion extension, IExtendsPedestrian modelReference) {
		
		state.extensionContainer.put(modelReference, extension);
	}
	
	@Override
	public void setWalkingState(WalkingState otherState) {
	
		state.walkingState = otherState;
	}

	@Override
	public void setStandingState(StandingState otherState) {
		
		state.standingState = otherState;
	}

	@Override
	public void setStayingState(StayingState otherState) {
	
		state.stayingState = otherState;
	}
	
	@Override
	public void setQueuingState(QueuingState otherState) {

		state.queuingState = otherState;
	}

	@Override
	public void setRoutingState(RoutingState otherState) {

		state.routingState = otherState;
	}

	@Override
	public void setSearchingState(SearchingState otherState) {

		state.searchingState = otherState;
	}
	
	@Override
	public void setStrategicalState(StrategicalState otherState) {

		state.strategicalState = otherState;
	}
	
	@Override
	public void setMetaState(MetaState otherState) {
	
		state.metaState = otherState;
	}

	protected class PedestrianState {
		
		StaticState staticState = null;
	
		WalkingState walkingState = null;

		StandingState standingState = null;
		
		TacticalState tacticalState = null;
		
		StayingState stayingState = null;
		
		QueuingState queuingState = null;
		
		RoutingState routingState = null;
		
		SearchingState searchingState = null;
		
		StrategicalState strategicalState = null;
		
		MetaState metaState = null;

		HashMap<IExtendsPedestrian, IPedestrianExtansion> extensionContainer = new HashMap<IExtendsPedestrian, IPedestrianExtansion>();
	}
}
