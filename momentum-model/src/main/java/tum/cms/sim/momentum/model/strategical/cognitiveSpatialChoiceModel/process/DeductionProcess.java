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

package tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.process;

import java.util.Collection;

import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.CognitiveConstant.Actualization;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.CognitiveConstant.Availability;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.CognitiveConstant.Deciding;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.memory.GoalChunk;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.memory.OpeningHourChunk;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.memory.PhysicalChunk;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.memory.PlanChunk;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.memory.PreferenceChunk;
import tum.cms.sim.momentum.utility.geometry.Vector2D;

public class DeductionProcess {

	private int lastCurrentGoal = -1;

	private Double minimalServiceTime = 1.0;
	
	public Double getMinimalServiceTime() {
		return minimalServiceTime;
	}

	public void setMinimalServiceTime(Double minimalServiceTime) {
		this.minimalServiceTime = minimalServiceTime;
	}

	private Double doneFulfillmentDuration = 0.0;
	private Double fulfillmentDuration = 0.0;

	public void executeAvailability(GoalChunk goal, SimulationState simulationState) {
		
		Availability availability = Availability.Impossible;
	
		
		Availability predecessorAvailability = this.checkPredecessors(goal);
		Availability openingHourAvailability = this.checkOpeningHours(goal, simulationState);

		if(predecessorAvailability == Availability.Performable &&
		   openingHourAvailability == Availability.Performable) {
			
			availability = Availability.Performable;
		}
		
		if(availability != goal.getAvailability()) {
			
			goal.setAvailablityChanged(true);
			goal.setAvailability(availability);
		}
		else {
			goal.setAvailablityChanged(false);
		}
	}
	
	public void executeActualization(
			PlanChunk plan, 
			Collection<GoalChunk> goals, 
			Collection<PreferenceChunk> preferences, 
			PhysicalChunk physical,
			Double cognitiveClock) {
		
		for(GoalChunk goal : goals) {
			
			if(goal.getPreference().getActualization() == Actualization.Achieved && !goal.getPreference().getOneTimePreference()) {
				
				goal.getPreference().setActualization(Actualization.Unfinished);
			}
		}
		
		if(plan.getDeciding() != Deciding.Scheduling && 
		   !plan.isScheduleEmpty()) {
			
			GoalChunk current = plan.getFirstValidGoal();
			
			if(current.getGoalId() != this.lastCurrentGoal) {
				
				this.fulfillmentDuration = 0.0;
				this.doneFulfillmentDuration = 0.0;
				this.lastCurrentGoal = current.getGoalId();
			}
			
			Boolean isAtLocation = false;
			
			for(Vector2D positionsToCheck : physical.getPositionOfGroupMembers()) {
				
				isAtLocation = current.getGoalArea().getGeometry().contains(positionsToCheck);
				
				if(isAtLocation) {
					break;
				}
			}

			switch(current.getPreference().getActualization()) {
			
			case Ongoing:

				this.doneFulfillmentDuration += cognitiveClock;

				if(this.fulfillmentDuration - this.doneFulfillmentDuration <= 0.0) {
					
					this.fulfillmentDuration = 0.0;
					this.doneFulfillmentDuration = 0.0;
					current.getPreference().setActualization(Actualization.Achieved);
					plan.removeGoal(current);
					
					this.updateSamePreference(goals, current, current.getPreference().getActualization());
				}
				else {
					
					current.getPreference().setActualization(Actualization.Ongoing);
					this.updateSamePreference(goals, current, current.getPreference().getActualization());
				}
				
				break;
				
			case Unfinished:

				if(isAtLocation) {
								
					this.fulfillmentDuration = Double.MAX_VALUE;
					
					if(current.getPreference().getServiceTimeDistributions() != null) {
						
						this.fulfillmentDuration = current.getPreference().getServiceTimeDistributions().getSample();
					}
					else {
						
						this.fulfillmentDuration = 0.0;
					}
					
					if(this.fulfillmentDuration < this.minimalServiceTime) {
						
						this.fulfillmentDuration = this.minimalServiceTime;
					}
					
					this.doneFulfillmentDuration = 0.0;
					current.getPreference().setActualization(Actualization.Ongoing);
					this.updateSamePreference(goals, current, current.getPreference().getActualization());
				}

				break;
				
			default:
				break;		
			}		
		}
	}

	private void updateSamePreference(Collection<GoalChunk> goals, 
			GoalChunk currentGoal,
			Actualization actualization) {
		
		for(GoalChunk goal : goals) {
			
			if(goal.getPreference().getPreferenceId() == currentGoal.getPreference().getPreferenceId()) {
				
				goal.getPreference().setActualization(actualization);
			}
		}	
	}

	private Availability checkPredecessors(GoalChunk goal) {
		
		Availability availability = Availability.Performable;
		
		for(PreferenceChunk preferenceChunk : goal.getPredecessorsPreferences()) {
			
			if(preferenceChunk.getActualization() != Actualization.Achieved) {
				
				availability = Availability.Impossible;
				break;
			}
		}

		return availability;
	}
	
	private Availability checkOpeningHours(GoalChunk goal, SimulationState simulationState) {
		
		Availability resultSchedule = Availability.Impossible;
		
		for(OpeningHourChunk openingHour : goal.getOpeningHours()) {
			
			if(openingHour.getStartInSeconds() <= simulationState.getCurrentTime() &&
			   simulationState.getCurrentTime() <= openingHour.getEndInSeconds()) {
				
				resultSchedule = Availability.Performable;
				break;
			}
		}
		
		return resultSchedule;
	}
}
