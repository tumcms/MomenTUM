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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import tum.cms.sim.momentum.data.agent.pedestrian.state.strategic.StrategicalState;
import tum.cms.sim.momentum.data.agent.pedestrian.state.tactical.TacticalState.Behavior;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrianExtension;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IStrategicPedestrian;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.strategical.DestinationChoiceModel;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.loader.ChunkLoader;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.loader.CognitiveParameterLoader;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.loader.ProcessLoader;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.memory.GoalChunk;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.memory.PreferenceChunk;
import tum.cms.sim.momentum.utility.generic.PropertyBackPack;

public class CognitiveSpatialChoiceStrategical extends DestinationChoiceModel {

	private static String groupOutputTargetIdName = "groupOutputTargetId";
	
	private Integer groupOutputTargetId = null;
	private CognitiveParameterLoader cognitiveParameter = new CognitiveParameterLoader();
	private ChunkLoader chunkLoader = new ChunkLoader();
	private ProcessLoader processLoader = new ProcessLoader();
	
	private HashMap<Integer, CognitiveSpatialChoiceStrategicExtension> groupMindSet = new HashMap<>();

	@Override
	public IPedestrianExtension onPedestrianGeneration(IRichPedestrian pedestrian) {
		
		CognitiveSpatialChoiceStrategicExtension extension = null;
		
		if(!groupMindSet.containsKey(pedestrian.getGroupId())) {			
			
			HashMap<Integer, PreferenceChunk> preferenceChunks = new HashMap<>();
			HashMap<Integer, GoalChunk> goalChunks = new HashMap<>();
			
			chunkLoader.loadPreferenceChunks(preferenceChunks, cognitiveParameter);
			chunkLoader.loadGoalChunk(goalChunks, preferenceChunks, cognitiveParameter);
			
			extension = new CognitiveSpatialChoiceStrategicExtension(pedestrian.getId(),
					pedestrian.getGroupId(),
					pedestrian.getStartLocationId(),
					cognitiveParameter,
					goalChunks,
					preferenceChunks.values(),
					groupOutputTargetId);
			
			extension.setProcesses(processLoader.loadPerceptionProcess(cognitiveParameter.getProcessParameter()),
					processLoader.loadDeductionProcess(cognitiveParameter.getProcessParameter()),
					processLoader.loadOperationProcess(cognitiveParameter.getProcessParameter()),
					processLoader.loadPreferenceProcess(preferenceChunks.values(), cognitiveParameter.getProcessParameter()),
					processLoader.loadScheduleProcess(cognitiveParameter.getProcessParameter()),
					processLoader.loadRescheduleProcess(cognitiveParameter.getProcessParameter()),
					processLoader.loadValuationProcess(cognitiveParameter.getProcessParameter()));
			
			pedestrian.setLeader(true);	
			groupMindSet.put(pedestrian.getGroupId(), extension);
			
			extension.addGroupMember(pedestrian.getId());
		}
		else {
			
			pedestrian.setLeader(false);
			extension = this.groupMindSet.get(pedestrian.getGroupId());
			extension.addGroupMember(pedestrian.getId());
		}
		
		return extension;
	}
	
	@Override
	public void onPedestrianRemoval(IRichPedestrian pedestrian) {
		
		CognitiveSpatialChoiceStrategicExtension extension = (CognitiveSpatialChoiceStrategicExtension)pedestrian.getExtensionState(this);		
		extension.removeGroupMember(pedestrian);
	}

	@Override
	public void callPedestrianBehavior(IStrategicPedestrian pedestrian, SimulationState simulationState) {
			
		CognitiveSpatialChoiceStrategicExtension extension = (CognitiveSpatialChoiceStrategicExtension) pedestrian.getExtensionState(this);

		if(extension.cogntiveCycleActive(simulationState)) {
			
			if(extension.canPerformControl(pedestrian)) {
				
				extension.executeCognitiveProcessing(this.perception,
						this.query,
						simulationState,
						pedestrian,
						this.scenarioManager);
			}	
		}
	}

	@Override
	public void callPreProcessing(SimulationState simulationState) {
		
		Collection<PropertyBackPack> goalBackPacks = this.properties.getChildPropertyBackPacks();
		
		cognitiveParameter.createBasics(this.properties);
		cognitiveParameter.createProcessParameter(this.properties, this.scenarioManager);
		
		this.groupOutputTargetId = this.properties.getIntegerProperty(CognitiveSpatialChoiceStrategical.groupOutputTargetIdName);
		
		for(PropertyBackPack goalBackPack : goalBackPacks) {
					
			List<Integer> goalsAreaIds = cognitiveParameter.createAreaIds(goalBackPack);
			
			for(Integer areaId : goalsAreaIds) {
			
				if(areaId == null) {
				
					continue;
				}
				
				cognitiveParameter.addGoalParameter(areaId, goalBackPack, this.scenarioManager, simulationState);
				cognitiveParameter.addPreferenceParameter(goalBackPack, goalsAreaIds.size(), simulationState);
			}
		}		

		this.scenarioManager.getGraph().computeFloydWarshall();
	}

	@Override
	public void callBeforeBehavior(SimulationState simulationState, Collection<IRichPedestrian> pedestrians) { 
		
		pedestrians.stream()
			.forEach(pedestrian -> {
				
				CognitiveSpatialChoiceStrategicExtension extension = (CognitiveSpatialChoiceStrategicExtension)pedestrian.getExtensionState(this);
	
				extension.updateGroupLeader(pedestrian);
				
				if(pedestrian.isLeader()) {
					
					List<IRichPedestrian> others = pedestrians.stream().filter(other -> other.getGroupId() == pedestrian.getGroupId()).collect(Collectors.toList());
					extension.updateGroupMembers(others);
				}
			});
	}

	@Override
	public void callAfterBehavior(SimulationState simulationState, Collection<IRichPedestrian> pedestrians) { 

		pedestrians.stream()
			.forEach(pedestrian -> {
				
				CognitiveSpatialChoiceStrategicExtension extension = (CognitiveSpatialChoiceStrategicExtension)pedestrian.getExtensionState(this);			

				if(extension.getGoal() != null) {

					pedestrian.setStrategicalState(new StrategicalState(extension.getGoal(), extension.getBehavior()));
				}
				else {
					
					//Area lastArea = pedestrian.getStrategicalState().getNextTargetArea();
					pedestrian.setStrategicalState(new StrategicalState(null, Behavior.None));
				}
			});
	}

	@Override
	public void callPostProcessing(SimulationState simulationState) { /* nothing to do */ }
}
