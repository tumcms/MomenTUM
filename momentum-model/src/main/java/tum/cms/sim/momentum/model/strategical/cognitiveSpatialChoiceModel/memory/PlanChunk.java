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

package tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.memory;

import java.util.ArrayList;
import java.util.List;

import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.CognitiveConstant.Deciding;

public class PlanChunk {

//	private Boolean firstGoalChanged = false;
//	
//	public Boolean getFirstGoalChanged() {
//		
//		boolean result = firstGoalChanged;
//		this.firstGoalChanged = false;
//		
//		return result;
//	}
//
//	public void consiumeFirstGoalChanged() {
//		
//		firstGoalChanged = false;
//	}

	private Boolean valenceChanged = false;
	
	public Boolean getValenceChanged() {
		return valenceChanged;
	}

	public void setValenceChanged(Boolean valenceChanged) {
		this.valenceChanged = valenceChanged;
	}

	private Deciding deciding = Deciding.Finished;
	
	public Deciding getDeciding() {
		return deciding;
	}

	public void setDeciding(Deciding deciding) {
		this.deciding = deciding;
	}
	
	public GoalChunk getFirstValidGoal() {
		
		return schedule.size() < 1 ? null : schedule.get(0);
	}

	private ArrayList<GoalChunk> schedule = new ArrayList<>();

	public boolean isScheduleEmpty() {
		
		return this.getFirstValidGoal() == null;
	}
	
	public void removeGoal(GoalChunk goal) {
		
		for(int iter = 0; iter < schedule.size(); iter++) {
			
			if(schedule.get(iter).getGoalId() == goal.getGoalId()) {
				
				//firstGoalChanged = true;
				schedule.remove(iter);
				break;
			}
		}
	}
	
	public ArrayList<GoalChunk> getSchedule() {
		return schedule;
	}

	public void setSchedule(ArrayList<GoalChunk> schedule) {
		this.schedule = schedule;
	}
	
	private Boolean isPlanUpdate = true;
	
	public Boolean getIsPlanUpdate() {
		return isPlanUpdate;
	}

	public void setIsPlanUpdate(Boolean isPlanUpdate) {
		this.isPlanUpdate = isPlanUpdate;
	}

	/**
	 * The rescheduled goals have to be in the schedule but can be less.
	 * The order in the rescheduled goal list will be changed in the schedule.
	 * All other goals will be untouched. Thus, only the order of goals schedule
	 * are changed regarding the reschedule list.
	 * @param rescheduledGoals
	 */
	public void updateSchedule(List<GoalChunk> rescheduledGoals) {

		for(int iter = 0; iter < schedule.size(); iter++) {
			
			boolean swap = false;
			
			for(int check = 0; check < rescheduledGoals.size(); check++) {
				
				if(schedule.get(iter).getGoalId().intValue() == rescheduledGoals.get(check).getGoalId().intValue()) {
					
					swap = true;
					break;
				}
			}
			
			if(swap) {
				
				//firstGoalChanged = true;
				schedule.set(iter, rescheduledGoals.remove(0));
			}
		}
	}
}
