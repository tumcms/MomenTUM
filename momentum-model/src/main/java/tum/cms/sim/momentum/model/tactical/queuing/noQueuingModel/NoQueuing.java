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

package tum.cms.sim.momentum.model.tactical.queuing.noQueuingModel;

import java.util.Collection;
import java.util.List;

import tum.cms.sim.momentum.data.agent.pedestrian.state.tactical.QueuingState;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrian;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrianExtension;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;
import tum.cms.sim.momentum.data.agent.pedestrian.types.ITacticalPedestrian;
import tum.cms.sim.momentum.data.layout.area.Area;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.tactical.queuing.QueuingModel;
import tum.cms.sim.momentum.utility.geometry.Vector2D;
import tum.cms.sim.momentum.utility.geometry.operation.GeometryAdditionals;

public class NoQueuing extends QueuingModel {

	@Override
	public IPedestrianExtension onPedestrianGeneration(IRichPedestrian pedestrian) {
		
		return null; // Nothing to do
	}

	@Override
	public void onPedestrianRemoval(IRichPedestrian pedestrian) {
		
		// Nothing to do
	}
	
	@Override
	public void callPreProcessing(SimulationState simulationState) {
		
		// Nothing to do
	}

	@Override
	public void callPostProcessing(SimulationState simulationState) {

		// Nothing to do
	}
	
	@Override
	public void callBeforeBehavior(SimulationState simulationState, Collection<IRichPedestrian> pedestrians) {
		
		// Nothing to do
	}
	
	@Override
	public void callPedestrianBehavior(ITacticalPedestrian pedestrian, SimulationState simulationState) {
		
		// This model just finds a random position in the goal
		if(pedestrian.getQueuingState() == null) {
			
			Area targetArea = pedestrian.getNextNavigationTarget();
			
			Vector2D position = null;
			
			List<IPedestrian> sameTargetPedestrians = this.query.findPedestrianSameTarget(pedestrian,
					this.perception, 
					targetArea, 
					false,
					0.0);
			
			int gambleIterator = 10;
			
			while(position == null && gambleIterator > 0) {
				
				position = GeometryAdditionals.findRandomPositionInPolygon(targetArea.getGeometry());
				
				if(position == null ||
				   this.query.isToCloseToAreaBorder(pedestrian.getBodyRadius(), 0.1, position, targetArea) ||
				   this.query.isCollisionWithPedestrian(pedestrian.getBodyRadius(), 0.1, position, sameTargetPedestrians)) {
					
					gambleIterator--;
				}
				else {
					
					break;
				}
			}
			
			Vector2D heading = position.subtract(pedestrian.getPosition()).getNormalized();
			QueuingState queuingState = new QueuingState(position, heading, pedestrian.getLastWalkingTarget());
			
			pedestrian.setQueuingState(queuingState);
		}
	}
	
	@Override
	public void callAfterBehavior(SimulationState simulationState, Collection<IRichPedestrian> pedestrians) {
		
		// Nothing to do
	}
}
