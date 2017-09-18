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

package tum.cms.sim.momentum.model.operational;

import java.util.Collection;

import tum.cms.sim.momentum.configuration.ModelTypConstants.ModelType;
import tum.cms.sim.momentum.data.agent.pedestrian.state.operational.StandingState;
import tum.cms.sim.momentum.data.agent.pedestrian.state.operational.WalkingState;
import tum.cms.sim.momentum.data.agent.pedestrian.state.tactical.TacticalState.Motoric;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrianExtension;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.PedestrianBehaviorModel;
import tum.cms.sim.momentum.model.operational.standing.StandingModel;
import tum.cms.sim.momentum.model.operational.walking.WalkingModel;
import tum.cms.sim.momentum.utility.geometry.Vector2D;

public class OperationalModel extends PedestrianBehaviorModel {

	private WalkingModel walkingModel = null;

	public WalkingModel getWalkingModel() {
		return walkingModel;
	}

	public void setWalkingModel(WalkingModel walkingModel) {
		this.walkingModel = walkingModel;
	}
	
	private StandingModel standingModel = null;

	public StandingModel getStandingModel() {
		return standingModel;
	}

	public void setStandingModel(StandingModel standingModel) {
		this.standingModel = standingModel;
	}
	
	@Override
	public ModelType getModelType() {
		
		return ModelType.Operational;
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
		
		this.walkingModel.callBeforeBehavior(simulationState, pedestrians);
	
		if(this.standingModel != null) {
			
			this.standingModel.callBeforeBehavior(simulationState, pedestrians);
		}
	}

	@Override
	public void callPedestrianBehavior(IRichPedestrian pedestrian, SimulationState simulationState) {

		if(this.standingModel != null) {
			
			switch(pedestrian.getMotoricTask()) {
			
			case Standing:
	
				this.callOperationalStandingBehavior(pedestrian, simulationState);
				break;			
				
			case Walking:
			default:
				
				Vector2D currentPosition = pedestrian.getPosition();
				this.callOperationalWalkingBehavior(pedestrian, simulationState);
				
				if(currentPosition.equals(pedestrian.getPosition())) {
					
					 currentPosition = pedestrian.getPosition();
				}
				
				break;
			}
		}
		else {

			switch(pedestrian.getMotoricTask()) {
			
			case Standing:
	
				this.callOperationalWalkingBehavior(pedestrian, simulationState);
				
				WalkingState state = pedestrian.getWalkingState();
				pedestrian.setStandingState(new StandingState(state.getWalkingPosition(),
						state.getWalkingVelocity(),
						state.getWalkingHeading()));
				pedestrian.setWalkingState(null);
				
				break;			
				
			case Walking:
			default:
				
				this.callOperationalWalkingBehavior(pedestrian, simulationState);

				
				break;
			}
		}
	}	
	
	@Override
	public void callAfterBehavior(SimulationState simulationState, Collection<IRichPedestrian> pedestrians) {

		if(this.standingModel != null) {
			
			this.standingModel.callAfterBehavior(simulationState, pedestrians);
		}
		
		this.walkingModel.callAfterBehavior(simulationState, pedestrians);	
		
		pedestrians.parallelStream().forEach(pedestrian -> {
			
			if(!pedestrian.getMotoricTask().equals(Motoric.Standing) && pedestrian.getWalkingState() != null) {
				
				pedestrian.setStandingState(null);
			}
			
			if(!pedestrian.getMotoricTask().equals(Motoric.Walking)) {
				
				pedestrian.setWalkingState(null);
			}
		});
	}

	@Override
	public IPedestrianExtension onPedestrianGeneration(IRichPedestrian pedestrian) {
	
		return null; // Nothing to do
	}

	@Override
	public void onPedestrianRemoval(IRichPedestrian pedestrian) {
		
		// Nothing to do	
	}
	
	private void callOperationalWalkingBehavior(IRichPedestrian pedestrian, SimulationState simulationState) {
		
		this.walkingModel.callPedestrianBehavior(pedestrian, simulationState);
	}
		
	private void callOperationalStandingBehavior(IRichPedestrian pedestrian, SimulationState simulationState) {
		
		this.standingModel.callPedestrianBehavior(pedestrian, simulationState);
	}
}
