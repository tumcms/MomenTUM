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

package tum.cms.sim.momentum.model.strategical.strictOrderModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import tum.cms.sim.momentum.data.agent.pedestrian.state.strategic.StrategicalState;
import tum.cms.sim.momentum.data.agent.pedestrian.state.tactical.TacticalState.Behavior;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrianExtension;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IStrategicPedestrian;
import tum.cms.sim.momentum.data.layout.area.Area;
import tum.cms.sim.momentum.data.layout.area.DestinationArea;
import tum.cms.sim.momentum.data.layout.area.IntermediateArea;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.strategical.DestinationChoiceModel;

public class StrictOrderStrategical extends DestinationChoiceModel {
	
	private static String areaOrderName = "areaOrder";
	private static String areaTaskName = "areaTask";
	private static String areaDurationName = "areaDuration";
	
	private ArrayList<Area> order = null;
	private ArrayList<Behavior> task = null;
	private ArrayList<Double> duration = null;
	
	@Override
	public IPedestrianExtension onPedestrianGeneration(IRichPedestrian pedestrian) {
		
		return new StrictOrderExtension();
	}

	@Override
	public void onPedestrianRemoval(IRichPedestrian pedestrian) { 	/* nothing to do */ }

	@Override
	public void callBeforeBehavior(SimulationState simulationState, Collection<IRichPedestrian> pedestrians) { /* nothing to do */ }

	@Override
	public void callPedestrianBehavior(IStrategicPedestrian pedestrian, SimulationState simulationState) {
		
		StrictOrderExtension extension = (StrictOrderExtension) pedestrian.getExtensionState(this);
		Area next = null;
		Behavior command = null;
		boolean initalize = false;
		
		if(pedestrian.getStrategicalState() == null ||
				   pedestrian.getStrategicalState().getTacticalBehavior() == Behavior.None) {
			
			next = this.order.get(0);
			extension.updateToNextTarget();
			command = Behavior.Routing;
			pedestrian.setStrategicalState(new StrategicalState(next, command));
			initalize = true;
		}
		else {
			
			command = pedestrian.getBehaviorTask();
			next = this.order.get(extension.getCurrentTargetArea());
		}
		
		boolean isTargetVisible = perception.isVisible(pedestrian,
				pedestrian.getNextNavigationTarget().getPointOfInterest())
				|| this.perception.isVisible(pedestrian, next.getPointOfInterest());
		
		if(initalize && !isTargetVisible) {
			
			command = Behavior.Routing;
		}
		else if(initalize && isTargetVisible) {
			
			command = this.task.get(extension.getCurrentTargetArea());
		}
		else if(isTargetVisible) {

			command = this.task.get(extension.getCurrentTargetArea());

			boolean isInTarget = pedestrian.getNextNavigationTarget().getGeometry().contains(pedestrian.getPosition());
			
			if(extension.getCurrentWaitingTime() == null && isInTarget) { // now reached!
				
				Double waitingTime = this.duration.get(extension.getCurrentTargetArea());
				extension.setCurrentWaitingTime(waitingTime);
				
				command = this.task.get(extension.getCurrentTargetArea());
			}
			else if (extension.getCurrentWaitingTime() != null &&
					extension.getCurrentWaitingTime() <= 0.0) { // at the target finished
				
				extension.setCurrentWaitingTime(null);
			
				if(extension.getCurrentTargetArea() + 1 < this.order.size()) {
					
					next = this.order.get(extension.getCurrentTargetArea() + 1);
					extension.updateToNextTarget();

					command = Behavior.Routing;
				}
				else {
					
					command = Behavior.None;
				}
			}
			else if(extension.getCurrentWaitingTime() != null){ // at the target waiting
				
				Double waitingTime = extension.getCurrentWaitingTime();
				waitingTime -= simulationState.getTimeStepDuration();
				extension.setCurrentWaitingTime(waitingTime);
			}
			else {
				
				command = this.task.get(extension.getCurrentTargetArea());
			}
		}

		if(next != null) {
			
			pedestrian.setStrategicalState(new StrategicalState(next, command));
		}
		else {
			
			next = pedestrian.getStrategicalState().getNextTargetArea();
			pedestrian.setStrategicalState(new StrategicalState(next, command));
		}
	}

	@Override
	public void callPreProcessing(SimulationState simulationState) { 
		
		List<DestinationArea> destinations = this.scenarioManager.getDestinations();
		List<IntermediateArea> intermediates = this.scenarioManager.getIntermediates();
		
		ArrayList<Area> areas = new ArrayList<Area>(destinations);
		areas.addAll(intermediates);
	
		List<Integer> areaOrder = this.properties.<Integer>getListProperty(areaOrderName);
		List<String> areaTasks = this.properties.<String>getListProperty(areaTaskName);
		this.duration = this.properties.<Double>getListProperty(areaDurationName);
		
		this.order = new ArrayList<Area>();
		
		for(int iter = 0; iter < areaOrder.size(); iter++) {
			
			for(Area area : areas) {
				
				if(area.getId() == areaOrder.get(iter)) {
					
					this.order.add(area);					
				}
			}
		}

		this.task = new ArrayList<Behavior>();
		areaTasks.forEach(areaTask -> this.task.add(areaTask != null ? Behavior.valueOf(areaTask) : null));
	}

	@Override
	public void callPostProcessing(SimulationState simulationState) { 	/* nothing to do */ }

	@Override
	public void callAfterBehavior(SimulationState simulationState, Collection<IRichPedestrian> pedestrians) {
		
		// Nothing to do
	}
}
