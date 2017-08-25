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

import java.util.ArrayList;

import tum.cms.sim.momentum.data.agent.pedestrian.state.tactical.TacticalState.Behavior;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.CognitiveConstant.Actualization;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.CognitiveConstant.Availability;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.CognitiveConstant.Deciding;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.cognitiveFunction.reschedule.IReschedule;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.memory.GoalChunk;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.memory.OperationChunk;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.memory.PhysicalChunk;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.memory.PlanChunk;

public class RescheduleProcess {
	
	private IReschedule reschedule = null;
	
	public IReschedule getReschedule() {
		return reschedule;
	}

	public void setReschedule(IReschedule reschedule) {
		this.reschedule = reschedule;
	}

	private Double rescheduleThreshold = null;
	
	public Double getValenceThreshold() {
		return rescheduleThreshold;
	}

	public void setValenceThreshold(Double valenceThreshold) {
		this.rescheduleThreshold = valenceThreshold;
	}

	public void executeRescheduling(PlanChunk plan, OperationChunk operation, PhysicalChunk physicalChunk) {

		if(plan.isScheduleEmpty()) {
			
			plan.setIsPlanUpdate(false);
			return;
		}
		
		boolean isPlaning = plan.getDeciding() == Deciding.Scheduling ;
		
		boolean isRealizingGoal = plan.getFirstValidGoal() != null && 
				plan.getFirstValidGoal().getPreference().getActualization() == Actualization.Ongoing;// ||
				 //operation.getCurrentTask().equals(Behavior.Participating));

		if((isPlaning || isRealizingGoal)) {

			return;
		}

		GoalChunk topGoal = plan.getFirstValidGoal();
		
		boolean isSingleFull = plan.getFirstValidGoal() != null && 
				operation.getCurrentTask() == Behavior.Queuing &&
				plan.getFirstValidGoal().isFull();
		
		if(topGoal.getAvailablityChanged() || 
		   topGoal.getAvailability() == Availability.Impossible ||
		   isSingleFull) {
			
			plan.setIsPlanUpdate(false);
			return;
		}	

		ArrayList<GoalChunk> visibleGoals = new ArrayList<>();
	
		for(GoalChunk scheduleGoal : plan.getSchedule()) {
			
			if(scheduleGoal.getVisible() || scheduleGoal.equals(plan.getFirstValidGoal())) { 
				
				visibleGoals.add(scheduleGoal);
			}
		}

		for(int high = 0, low = 1; low < visibleGoals.size(); high++, low++) {
			
			double planedDifference = visibleGoals.get(high).getScheduledValenceStrength() -
					visibleGoals.get(low).getScheduledValenceStrength();
			
			double realDifference = visibleGoals.get(high).getValenceStrength() -
					visibleGoals.get(low).getValenceStrength();
			
			if(visibleGoals.get(low).getAvailability() == Availability.Performable &&
			   planedDifference - realDifference >= this.rescheduleThreshold * low) {
				
				//System.out.println(String.valueOf(planedDifference - realDifference) + " " + String.valueOf(low));
				plan.setIsPlanUpdate(false);
				return;
			}
		}
	}
}
