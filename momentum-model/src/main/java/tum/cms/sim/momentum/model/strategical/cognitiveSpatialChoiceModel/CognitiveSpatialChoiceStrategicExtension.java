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

package tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import tum.cms.sim.momentum.data.agent.pedestrian.state.tactical.TacticalState.Behavior;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrianExtansion;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IStrategicPedestrian;
import tum.cms.sim.momentum.data.layout.ScenarioManager;
import tum.cms.sim.momentum.data.layout.area.Area;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.perceptional.PerceptionalModel;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.loader.CognitiveParameterLoader;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.memory.GoalChunk;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.memory.OperationChunk;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.memory.PhysicalChunk;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.memory.PlanChunk;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.memory.PreferenceChunk;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.process.DeductionProcess;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.process.OperationProcess;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.process.PerceptionProcess;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.process.PreferenceProcess;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.process.RescheduleProcess;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.process.ScheduleProcess;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.process.ValuationProcess;

public class CognitiveSpatialChoiceStrategicExtension implements IPedestrianExtansion {

	private CognitiveParameterLoader cognitiveBoundary = null;
	
	private PhysicalChunk physicalChunk = null;
	private PlanChunk planChunk = null;
	
	public PlanChunk getPlanChunk() {
		return planChunk;
	}

	private ArrayList<PreferenceChunk> preferenceChunks = null;

	private ArrayList<GoalChunk> goalChunks = null;
	private HashMap<Integer, GoalChunk> goalChunkMap = null;
	
	public HashMap<Integer, GoalChunk> getGoalChunksMap() {
		return goalChunkMap;
	}

	private OperationChunk operationChunk = null;
	
	private PerceptionProcess perceptionProcess = null;
	private DeductionProcess deductionProcess = null;
	private OperationProcess operationProcess = null;
	private ArrayList<PreferenceProcess> preferenceProcesses = null;
	private ScheduleProcess scheduleProcess = null;
	private RescheduleProcess rescheduleProcess = null;
	private ValuationProcess valuationProcess = null;

	private int startLocation = -1;
	
	public int getStartLocation() {
		return startLocation;
	}

	public CognitiveSpatialChoiceStrategicExtension(int leaderId,
			int groupId,
			int startLocation,
			CognitiveParameterLoader cognitiveClock, 
			HashMap<Integer, GoalChunk> goals,
			Collection<PreferenceChunk> preference,
			Integer groupOutputTargetId) {
		
		this.groupId = groupId;
		this.startLocation = startLocation;
		this.leaderId = leaderId;
		this.cognitiveBoundary = cognitiveClock;
		this.planChunk = new PlanChunk();
		this.physicalChunk = new PhysicalChunk();
		this.goalChunks = new ArrayList<>(goals.values());
		this.goalChunkMap = goals;
		this.preferenceChunks = new ArrayList<>(preference);
		this.operationChunk = new OperationChunk();
		this.groupOutputTargetId = groupOutputTargetId;
	}
	
	public void setProcesses(
			PerceptionProcess preceptionProcess,
			DeductionProcess deductionProcess,
			OperationProcess operationProcess,
			ArrayList<PreferenceProcess> preferenceProcesses,
			ScheduleProcess scheduleProcess,
			RescheduleProcess rescheduleProcess,
			ValuationProcess valuationProcess) {
		
		this.perceptionProcess = preceptionProcess;
		this.deductionProcess = deductionProcess;
		this.operationProcess = operationProcess;
		this.preferenceProcesses = preferenceProcesses;
		this.scheduleProcess = scheduleProcess;
		this.rescheduleProcess = rescheduleProcess;
		this.valuationProcess = valuationProcess;
	}

	public void executeCognitiveProcessing(
			PerceptionalModel perceptionModel,
			SimulationState simulationState,
			IStrategicPedestrian pedestrian,
			ScenarioManager scenario) {
		
		// update position
		this.physicalChunk.setThisPedestrian(pedestrian);
		this.physicalChunk.setScenario(scenario);

		// update goal related information
		for(GoalChunk goal : this.goalChunks) {
			
			this.perceptionProcess.executeVisible(perceptionModel, goal, this.physicalChunk);
			this.perceptionProcess.executeProximity(goal, this.physicalChunk, this.operationChunk);
			this.perceptionProcess.executeDistance(goal, this.physicalChunk, simulationState);
			this.perceptionProcess.executeOccupancy(perceptionModel, goal, this.physicalChunk, this.operationChunk);
		}
		
		// update deduction all goals
		for(GoalChunk goal : this.goalChunks) {

			this.deductionProcess.executeAvailability(goal, simulationState);
		}
		
		// update deduction current goal 
		this.deductionProcess.executeActualization(this.planChunk,
				this.goalChunks,
				this.preferenceChunks,
				this.physicalChunk,
				this.cognitiveBoundary.getCognitiveClock());

		// update preference related information
		for(PreferenceChunk preference : this.preferenceChunks) {
			
			List<GoalChunk> preferencesRelatedGoals = this.goalChunks.stream()
					.filter(goal -> goal.getPreference().getPreferenceId() == preference.getPreferenceId())
					.collect(Collectors.toList());
			
			PreferenceProcess preferenceProcess = this.preferenceProcesses.stream()
					.filter(process -> process.getPreferenceId() == preference.getPreferenceId())
					.findFirst()
					.get();
				
			Double interest = preferenceProcess.executePreference(preferencesRelatedGoals,
					preference,
					simulationState,
					this.physicalChunk,
					this.cognitiveBoundary.getCognitiveClock());
			
			preference.setInterest(interest);
		}
		
		// update valences
		this.valuationProcess.executeValuation(this.planChunk, this.goalChunks, this.preferenceChunks, this.physicalChunk);
		this.rescheduleProcess.executeRescheduling(this.planChunk, this.operationChunk, this.physicalChunk);

		// reschedule visible goals if necessary
		//this.rescheduleProcess.executeRescheduling(this.planChunk,  this.operationChunk, this.cognitiveBoundary.getCognitiveClock());
		
		 // schedule goals or re-plan if goal is achieved and valence changed
		this.scheduleProcess.executeScheduling(this.planChunk, 
				this.goalChunks, 
				this.physicalChunk,
				this.operationChunk,
				this.cognitiveBoundary.getCognitiveClock());
		
		// update operation
		this.operationProcess.execute(this.planChunk, this.operationChunk);
	}

	public Behavior getBehavior() {
		
		return this.operationChunk.getCurrentTask();
	}
	
	public Area getGoal() {
		
		return this.operationChunk.getGoal();
	}
	
	
	// internal logic start

	private Integer groupOutputTargetId = null;
	private Integer groupId = null;

	public Integer getGroupId() {
		return groupId;
	}

	public boolean canWriteOutput() {

		return this.groupOutputTargetId == null || this.groupOutputTargetId == this.groupId;
	}

	private Integer timeStepForCognitiveCycle = null;
	
	public boolean cogntiveCycleActive(SimulationState simulationState) {

		if(this.timeStepForCognitiveCycle == null) {
		
			this.timeStepForCognitiveCycle  = (int)(this.cognitiveBoundary.getCognitiveClock() / simulationState.getTimeStepDuration());
		}

		return simulationState.getCurrentTimeStep() % timeStepForCognitiveCycle == 0;
	}
	
	private HashSet<Integer> groupMembers = new HashSet<Integer>();
	private int leaderId = -1;
	
	public boolean canPerformControl(IStrategicPedestrian pedestrian) {

		return pedestrian.getId().intValue() == this.leaderId;
	}

	public void addGroupMember(Integer groupMemberId) {
	
		this.groupMembers.add(groupMemberId);
	}

	public void updateGroupLeader(IRichPedestrian groupMember) {
		
		if(!groupMember.isLeader() && this.leaderId == groupMember.getId()) {
			
			groupMember.setLeader(true);
		}
	}
	
	public void removeGroupMember(IRichPedestrian groupMember) {

		if(groupMember.getId() == this.leaderId && this.groupMembers.size() > 1) {
			
			this.groupMembers.remove(this.leaderId);
			int newLeader = groupMembers.stream().findFirst().get();
			this.leaderId = newLeader;
			this.operationChunk.setCurrentTask(Behavior.Routing);
		}
		else {
			
			this.groupMembers.remove(groupMember.getId());
		}
	}

	public void updateGroupMembers(List<IRichPedestrian> others) {

		this.physicalChunk.setPositionOfGroupMembers(others.stream().map(IRichPedestrian::getPosition).collect(Collectors.toList()));
	}
	
	// internal logic end
}
