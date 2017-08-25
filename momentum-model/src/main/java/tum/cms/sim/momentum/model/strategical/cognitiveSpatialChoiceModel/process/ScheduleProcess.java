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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.stream.Collectors;
import org.apache.commons.math3.util.FastMath;
import tum.cms.sim.momentum.data.layout.area.DestinationArea;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.CognitiveConstant.Actualization;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.CognitiveConstant.Availability;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.CognitiveConstant.Deciding;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.cognitiveFunction.schedule.ISchedule;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.memory.GoalChunk;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.memory.OperationChunk;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.memory.PhysicalChunk;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.memory.PlanChunk;
import tum.cms.sim.momentum.utility.probability.distrubution.IDistribution;

public class ScheduleProcess {

	private ISchedule scheduler = null;
	
	public ISchedule getScheduler() {
		return scheduler;
	}

	public void setScheduler(ISchedule scheduler) {
		this.scheduler = scheduler;
	}

	private Boolean isFirstPlan = true;

	private Double timeLeftUntilScheduled = null;
	
	private Integer scheduleSize = null;
	
	private IDistribution scheduleTimeDistribution = null;
	
	public IDistribution getScheduleTimeDistribution() {
		return scheduleTimeDistribution;
	}

	public void setScheduleTimeDistribution(IDistribution scheduleTimeDistribution) {
		this.scheduleTimeDistribution = scheduleTimeDistribution;
	}

	private IDistribution scheduleSizeDistribution = null;

	public IDistribution getScheduleSizeDistribution() {
		return scheduleSizeDistribution;
	}

	public void setScheduleSizeDistribution(IDistribution scheduleSizeDistribution) {
		this.scheduleSizeDistribution = scheduleSizeDistribution;
	}

	public void executeScheduling(PlanChunk plan,
			Collection<GoalChunk> goals,
			PhysicalChunk physical,
			OperationChunk operation,
			Double cognitiveClock) {
		
		if((plan.getIsPlanUpdate() && !plan.isScheduleEmpty()) && !(plan.getDeciding() == Deciding.Scheduling)) {
			
			return;
		}
	
		this.computeTime(plan, cognitiveClock);
	
		if(this.timeLeftUntilScheduled > 0.0 && !isFirstPlan) {
			
			return;
		}
		else {
			
			this.timeLeftUntilScheduled = 0.0;
			plan.setIsPlanUpdate(true);
		}
		
		if(plan.getSchedule().size() > 0 && plan.getSchedule().get(0).getPreference().getActualization() == Actualization.Ongoing) {
			
			//int i = 0;
		}
		// schedule time run up, hence do the scheduling
		plan.setDeciding(Deciding.Finished);
		this.timeLeftUntilScheduled = null;

		goals.forEach(goal -> goal.setScheduledValenceStrength(goal.getValenceStrength()));

		// ignore not usable goals
		// and the goals not for the agent (seed == or other ID)
		ArrayList<GoalChunk> schedule = this.initialSelectGoals(goals, physical);

		Collections.sort(schedule, new Comparator<GoalChunk>() {
			@Override
			public int compare(GoalChunk leftGoal, GoalChunk rightGoal) {			
				return -1 * leftGoal.getValenceStrength().compareTo(rightGoal.getValenceStrength());
			}}
		);
		
		schedule = this.sortOutPreferenceBased(schedule);
		schedule = this.sortDependencies(schedule);
		
		// first select region to go to, do this by identifying doors as regions and places as local region.
		// include ignoring the origin (from region) in the first plan
		schedule = this.filterRegions(schedule, physical);
		
		// remove all goals extending the schedule size
		schedule = this.filterToSize(schedule, scheduleSize);

		// update schedule
		plan.setSchedule(schedule);
		if(plan.isScheduleEmpty()) {
			//boolean i = false;
		}
	}
	
	private ArrayList<GoalChunk> filterToSize(ArrayList<GoalChunk> schedule, int scheduleSize) {

		ArrayList<GoalChunk> scheduleInSize = new ArrayList<>();
		
		for(int iter = 0; iter < scheduleSize && iter < schedule.size(); iter++) {
			
			scheduleInSize.add(schedule.get(iter));
		}
		
		return scheduleInSize;
	}
	
	/** 
	 * todo revert order 
	 * */
	private ArrayList<GoalChunk> sortDependencies(ArrayList<GoalChunk> schedule) {

		if(schedule.size() > 1) {
			
			for(int iter = 0; iter < schedule.size(); iter++) {
				
				int oldSuccessorPosition = -1;
				
				for(int successor = iter; successor < schedule.size(); successor++) {
					
					if(schedule.get(successor).isSuccesorOf(schedule.get(iter))) {
						
						oldSuccessorPosition = successor;
						break;
					}
				}
				
				if(oldSuccessorPosition != -1) {
					
					GoalChunk tempGoal = schedule.remove(oldSuccessorPosition);
					schedule.add(iter, tempGoal);
					iter--;
				}
				
			}
		}

		return schedule;
	}

	private ArrayList<GoalChunk> filterRegions(ArrayList<GoalChunk> schedule, PhysicalChunk physical) {
				ArrayList<GoalChunk> doorRegionGoals = new ArrayList<>();
		ArrayList<GoalChunk> localRegionGoals = new ArrayList<>();
		GoalChunk fromRegionDoor = null;
		
		for(GoalChunk potentialGoal : schedule) {
		
			if(potentialGoal.isDoor() || potentialGoal.getGoalArea() instanceof DestinationArea) {

				if(isFirstPlan) {
					
					int destinationOriginId = ((DestinationArea)potentialGoal.getGoalArea()).getOverlappingOrigin();
					
					if(destinationOriginId == physical.getThisPedestrian().getStartLocationId()) {
								
						fromRegionDoor = potentialGoal;
						continue;
					}
				}
				
				if(potentialGoal.isDoor()) {
					
					doorRegionGoals.add(potentialGoal);		
				}
				
			}
			else {
				
				localRegionGoals.add(potentialGoal);
			}
		}
		
		// hierarchical planning first step
		if(doorRegionGoals.size() > 0) {
				
			// next, select based on the motivation which is the most important region. 
			// for the local region places, use the max value.
			// GoalChunk bestLocal = Collections.max(localRegionGoals, Comparator.comparing(GoalChunk::getInterest));
			Collections.sort(localRegionGoals, new Comparator<GoalChunk>() {
				@Override
				public int compare(GoalChunk leftGoal, GoalChunk rightGoal) {			
					return -1 * leftGoal.getValenceStrength().compareTo(rightGoal.getValenceStrength());
				}}
			);
			
			double averageLocal = localRegionGoals.get((int)(localRegionGoals.size() / 2.0)).getValenceStrength();
			GoalChunk bestDoor = Collections.max(doorRegionGoals, Comparator.comparing(GoalChunk::getValenceStrength));
	
			if(averageLocal < bestDoor.getValenceStrength() && bestDoor.getAvailability() == Availability.Performable) {
				
				schedule.clear();
				schedule.add(bestDoor);
			}
			
			if(schedule.size() > 1 && fromRegionDoor != null) {
				
				if(schedule.get(0).getGoalId().intValue() == fromRegionDoor.getGoalId().intValue()) {
					
					schedule.remove(0);
					schedule.add(1, fromRegionDoor);
				}
			}
		}
		
		// remove all goals after a destination goal, because not part of the scenario
		boolean isDestinationGoalFound = false;
		
		for(int iter = 0; iter < schedule.size(); iter++) {
			
			if(isDestinationGoalFound) {
			
				schedule.remove(iter--);
			}
			else {
				
				if(schedule.get(iter).isDoor() || schedule.get(iter).getGoalArea() instanceof DestinationArea) {
					
					isDestinationGoalFound = true;
				}
			}
		}
		
		isFirstPlan = false;
		
		return schedule;
	}

	private ArrayList<GoalChunk> initialSelectGoals(Collection<GoalChunk> goals, PhysicalChunk physical) {
		
		return  new ArrayList<GoalChunk>(
				goals.stream()
				.filter(goal -> goal.getPreference().getActualization() == Actualization.Unfinished)
				.filter(goal -> goal.getPreference().getActualization() != Actualization.Achieved)
				.filter(goal -> goal.getPreference().getSeedId() == null ||
								goal.getPreference().getSeedId().intValue() == physical.getThisPedestrian().getSeedId())
				.collect(Collectors.toList())
		);
	}

	/**
	 * precondition, goals are sorted regarding some cogntiveFunction
	 * @param schedule
	 * @return
	 */
	private ArrayList<GoalChunk> sortOutPreferenceBased(ArrayList<GoalChunk> schedule) {

		// Remove goals with same interest type but lower valence (later in the list)
		HashSet<Integer> preferenceIds = new HashSet<Integer>();
		schedule.forEach(goal -> preferenceIds.add(goal.getPreference().getPreferenceId()));
		
		ArrayList<GoalChunk> scheduleGoals = new ArrayList<>();
		
		for(int iter = 0; iter < schedule.size(); iter++) {

			// and ignore goals which preference id is already in the the schedule
		   	if(preferenceIds.contains(schedule.get(iter).getPreference().getPreferenceId())) {
		   		
				preferenceIds.remove(schedule.get(iter).getPreference().getPreferenceId());
				scheduleGoals.add(schedule.get(iter));
		   	}
		}    
	
		return scheduleGoals;
	}

	private void computeTime(PlanChunk plan, Double cognitiveClock) {
		
		if(this.timeLeftUntilScheduled == null) {
			
			this.scheduleSize = (int)FastMath.round(this.scheduleSizeDistribution.getSample());
			
			if(this.scheduleSize < 1) {
				
				this.scheduleSize = 1;
			}
			
			this.timeLeftUntilScheduled = 8.403 * FastMath.pow(this.scheduleSize, 0.6038) - 8.403;
			plan.setDeciding(Deciding.Scheduling);
		}
		else {
			
			this.timeLeftUntilScheduled -= cognitiveClock;
		}
	}
}
