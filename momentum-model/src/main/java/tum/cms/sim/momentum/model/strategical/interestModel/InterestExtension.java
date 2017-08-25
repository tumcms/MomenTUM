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

package tum.cms.sim.momentum.model.strategical.interestModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import tum.cms.sim.momentum.data.agent.pedestrian.state.strategic.StrategicalState;
import tum.cms.sim.momentum.data.agent.pedestrian.state.tactical.TacticalState.Behavior;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrianExtansion;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IStrategicPedestrian;
import tum.cms.sim.momentum.data.layout.area.Area;
import tum.cms.sim.momentum.data.layout.area.DestinationArea;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.strategical.interestModel.InterestConstant.InterestState;
import tum.cms.sim.momentum.model.support.perceptional.PerceptionalModel;

public class InterestExtension implements IPedestrianExtansion {

	private ArrayList<InterestCalculator> interestModels = new ArrayList<InterestCalculator>();
	private LinkedList<Area> goalQueue = new LinkedList<Area>();
	private HashSet<Area> isQueuedSet = new HashSet<Area>();
	private PerceptionalModel perception = null;
	
	public InterestExtension(InterestStrategical interestStrategical,
			List<Area> interestLocation,
			InterestConfiguration configuration) {
	
		this.perception = interestStrategical.getPerception();
		interestLocation.forEach(location -> interestModels.add(new InterestCalculator(interestStrategical, location, configuration)));
	}

	public void updateInterests(SimulationState simulationState, IStrategicPedestrian currentPedestrian) {
		
		ArrayList<QueueState> queueStates = new ArrayList<QueueState>();
		
		for(InterestCalculator model : this.interestModels) {
	
			this.calculateQueueStates(simulationState, currentPedestrian, queueStates, model);
		}
		
		for(QueueState queueState : queueStates) {
			
			this.updateQueueState(queueState);
		}
		
		if(goalQueue.peekFirst() == null) {
		
			this.handleEmptyQueue(queueStates, currentPedestrian.getStartLocationId());
		}

		if(!goalQueue.peekFirst().equals(currentPedestrian.getStrategicalState().getNextTargetArea())) {

			Behavior behavior = this.chooseBehavior(currentPedestrian, goalQueue.peekFirst(), simulationState);
			
			currentPedestrian.setStrategicalState(new StrategicalState(goalQueue.peekFirst(), behavior));
		}		
	}
	
	private Behavior chooseBehavior(IStrategicPedestrian pedestrian, Area target, SimulationState simulationState) {
		
		Behavior behavior = Behavior.Routing;
		
		if(pedestrian.getNextNavigationTarget().getGeometry().contains(pedestrian.getPosition()) || 
				(perception.isVisible(pedestrian.getPosition(),
						pedestrian.getNextNavigationTarget().getPointOfInterest()) && 
						pedestrian.getNextNavigationTarget().getGeometry().contains(pedestrian.getNextWalkingTarget()))) {
			
			behavior = Behavior.Staying;
		}
		
		return behavior;
	}
	
	private void calculateQueueStates(SimulationState simulationState,
			IStrategicPedestrian currentPedestrian,
			ArrayList<QueueState> queueStates,
			InterestCalculator model) {

		// interest update
		InterestState state = model.calculateState(simulationState, currentPedestrian);
		
		// store update
		queueStates.add(new QueueState(model.getLocation(), 
				state,
				model.getInterestMagnitude(),
				model.getRemainingRaisingTime()));
	}
	
	private void updateQueueState(QueueState queueState) {
		
		if(queueState.getState() == InterestState.Dropped) { // goal fulfilled!
			
			Area fulfilledGoal = goalQueue.pop();
			isQueuedSet.remove(fulfilledGoal);
		}
		else if(queueState.getState() == InterestState.Maximal) { // queue it, do it later
			
			if(!isQueuedSet.contains(queueState.getArea())) { // if not already queued
				
				goalQueue.add(queueState.getArea());	
				isQueuedSet.add(queueState.getArea());
			}
		}
	}
	
	boolean firstTime = true;
	private void handleEmptyQueue(ArrayList<QueueState> queueStates, int startLocationId) {
		
		QueueState bestInterest = new QueueState(null, null, 0.0, Double.MAX_VALUE);
		
		for(QueueState queueState : queueStates) {
			
			if(firstTime) { // ignore the origin for the first empty queue
			
				if(queueState.area instanceof DestinationArea) {
					
					DestinationArea destination = (DestinationArea)queueState.area;
					
					if(destination.getOverlappingOrigin() != null &&
							destination.getOverlappingOrigin() == startLocationId) {
						
						continue;
					}
					
				}
			}

			if(((1.0 - bestInterest.getMagnitude()) * bestInterest.getRemainingRaisingTime()) >
			   ((1.0 - queueState.getMagnitude()) * queueState.getRemainingRaisingTime())) {
				
				bestInterest = new QueueState(queueState);
			}
		}
		
		firstTime = false;
		Area targetArea = bestInterest.getArea();
		goalQueue.push(targetArea);
		isQueuedSet.add(targetArea);
	}
	
	private class QueueState {
		
		private Area area = null;
				
		public Area getArea() {
			return area;
		}

		private InterestState state = null;
		
		public InterestState getState() {
			return state;
		}
		
		private Double magnitude = null;
		
		public Double getMagnitude() {
			return magnitude;
		}
		
		private Double remainingRaisingTime = null;

		public Double getRemainingRaisingTime() {
			return remainingRaisingTime;
		}

		public QueueState(Area area, InterestState state, Double magnitude, Double remainingRaisingTime) {
			
			this.area = area;
			this.state = state;
			this.magnitude = magnitude;
			this.remainingRaisingTime = remainingRaisingTime;
		}

		public QueueState(QueueState maximalInterest) {

			this.area = maximalInterest.getArea();
			this.state = maximalInterest.getState();
			this.magnitude = maximalInterest.getMagnitude();
			this.remainingRaisingTime = maximalInterest.getRemainingRaisingTime();
		}
	}
}
