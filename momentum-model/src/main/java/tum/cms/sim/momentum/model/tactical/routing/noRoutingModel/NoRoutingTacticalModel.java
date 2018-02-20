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

package tum.cms.sim.momentum.model.tactical.routing.noRoutingModel;

import java.util.Collection;
import java.util.LinkedHashSet;

import tum.cms.sim.momentum.data.agent.pedestrian.state.tactical.RoutingState;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrianExtension;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;
import tum.cms.sim.momentum.data.agent.pedestrian.types.ITacticalPedestrian;
import tum.cms.sim.momentum.data.layout.area.Area;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.tactical.routing.RoutingModel;
import tum.cms.sim.momentum.utility.graph.GraphTheoryFactory;
import tum.cms.sim.momentum.utility.graph.Vertex;

public class NoRoutingTacticalModel extends RoutingModel {

	@Override
	public IPedestrianExtension onPedestrianGeneration(IRichPedestrian pedestrian) {

		return null;
	}
	
	@Override
	public void callAfterBehavior(SimulationState simulationState, Collection<IRichPedestrian> pedestrians) {
	
		// nothing to do
	}
	
	@Override
	public void callBeforeBehavior(SimulationState simulationState, Collection<IRichPedestrian> pedestrians) {
		
		// Nothing to do
	}
	
	@Override
	public void callPedestrianBehavior(ITacticalPedestrian pedestrian, SimulationState simulationState) {
		
		// this simple model does create a route only once
		if(pedestrian.getRoutingState() == null) {
			
			int startLocation = pedestrian.getStartLocationId();
			
			Area originArea = this.scenarioManager.getOrigins().stream()
					.filter(origin -> origin.getId() == startLocation)
					.findFirst()
					.get();
			
			Vertex originVertex = GraphTheoryFactory.createVertex(originArea.getGeometry());
			Vertex targetVertex = GraphTheoryFactory.createVertex(pedestrian.getNextNavigationTarget().getGeometry());

			RoutingState routingState = new RoutingState(new LinkedHashSet<Vertex>(), null, originVertex, targetVertex);	
			pedestrian.setRoutingState(routingState);
		}
	}

	@Override
	public void callPreProcessing(SimulationState simulationState) {

		// nothing to do		
	}

	@Override
	public void callPostProcessing(SimulationState simulationState) {
	
		// nothing to do
	}

	@Override
	public void onPedestrianRemoval(IRichPedestrian pedestrian) {

		// nothing to do
	}
}
