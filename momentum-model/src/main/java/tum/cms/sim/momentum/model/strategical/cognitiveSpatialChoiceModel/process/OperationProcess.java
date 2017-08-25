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

import tum.cms.sim.momentum.data.agent.pedestrian.state.tactical.TacticalState.Behavior;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.CognitiveConstant.Deciding;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.memory.GoalChunk;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.memory.OperationChunk;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.memory.PlanChunk;

public class OperationProcess {
	
	public void execute(PlanChunk planChunk, OperationChunk operation) {
		

		// if nothing to do, keep old behavior
		if(planChunk.isScheduleEmpty() || planChunk.getDeciding() == Deciding.Scheduling) {

			operation.setCurrentTask(Behavior.None);
			operation.setGoal(null);
			
			return;
		}
		
		Behavior behavior = Behavior.Routing;
		GoalChunk topGoal = planChunk.getFirstValidGoal();
		
		if(operation.getGoal() != null && topGoal.getGoalArea().getId().equals(operation.getGoal().getId())) {
			
			if(topGoal.getVisible() && topGoal.getProximity()) {
				
				switch(topGoal.getOccupancyType()) {
				
				case Engage:
				case None:
					behavior = Behavior.Staying;
					break;

				case Waiting:
					behavior = Behavior.Queuing;
					break;
				}
			}			
			else if(operation.getCurrentTask() != null &&
					operation.getCurrentTask() != Behavior.Staying &&
				    operation.getCurrentTask() != Behavior.Routing) {  // keep old behavior
					
				switch(topGoal.getOccupancyType()) {
				
				case Engage:
				case None:
					behavior = Behavior.Staying;
					break;
	
				case Waiting:
					behavior = Behavior.Queuing;
					break;
				}
			}	
		}


		operation.setCurrentTask(behavior);
		operation.setGoal(topGoal.getGoalArea());
	}
}
