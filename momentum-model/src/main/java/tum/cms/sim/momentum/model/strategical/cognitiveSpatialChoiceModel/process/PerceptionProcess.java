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
import tum.cms.sim.momentum.data.agent.pedestrian.state.tactical.TacticalState.Motoric;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.perceptional.PerceptionalModel;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.CognitiveConstant.OccupancyType;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.cognitiveFunction.distance.IDistancePerception;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.cognitiveFunction.occupancy.IOccupancyPerception;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.memory.GoalChunk;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.memory.OperationChunk;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.memory.PhysicalChunk;
import tum.cms.sim.momentum.utility.geometry.Vector2D;

public class PerceptionProcess {
	
	private IDistancePerception distancePerception = null;
	
	public IDistancePerception getDistancePerception() {
		return distancePerception;
	}

	public void setDistancePerception(IDistancePerception distancePerception) {
		this.distancePerception = distancePerception;
	}
	
	private IOccupancyPerception occupancyPerception = null;

	public IOccupancyPerception getOccupancyPerception() {
		return occupancyPerception;
	}

	public void setOccupancyPerception(IOccupancyPerception occupancyPerception) {
		this.occupancyPerception = occupancyPerception;
	}

	private Double proximityDistance = null;
	
	public Double getProximityDistance() {
		return proximityDistance;
	}

	public void setProximityDistance(Double proximityDistance) {
		this.proximityDistance = proximityDistance;
	}
	
//	public void normalizeOccupancy(Collection<GoalChunk> goals) {
//		
//		double maximalOccupancy = 0.0;
//		double minimalOccupancy = Double.MAX_VALUE;
//		
//		for(GoalChunk goal : goals) {
//			
//			if(goal.getOccupancy().doubleValue() > maximalOccupancy) {
//				
//				maximalOccupancy = goal.getOccupancy();
//			}
//			
//			if(minimalOccupancy > goal.getOccupancy().doubleValue()) {
//				
//				minimalOccupancy = goal.getOccupancy();
//			}
//		}
//		
//		for(GoalChunk goal : goals) {
//			
//			double currentOccupancy = goal.getOccupancy() - minimalOccupancy;
//	
//			if(maximalOccupancy == 0.0) {
//				
//				goal.setOccupancy(0.0);
//			}
//			else {
//				
//				goal.setOccupancy(currentOccupancy / (maximalOccupancy - minimalOccupancy));
//			}
//		}
//	}
//	
//	public void normalizeDistance(Collection<GoalChunk> goals) {
//	
////		double minimalDistanz = 0.0;//Double.MAX_VALUE;
//		
////		for(GoalChunk goal : goals) {
////			
////			if(minimalDistanz > goal.getDistance().doubleValue()) {
////				
////				minimalDistanz = goal.getDistance();
////			}
////		}
//		
//		for(GoalChunk goal : goals) {
//			
//			double currentDistance = goal.getDistance();// - minimalDistanz;
//			
//			goal.setDistance(currentDistance / (this.distanceScale));// - minimalDistanz));
//		}
//	}
	
	public void executeVisible(PerceptionalModel perceptionModel, GoalChunk goal, PhysicalChunk physical) {
	
		Boolean visible = null;

		visible = perceptionModel.isVisible(physical.getThisPedestrian(), goal.getGoalArea().getPointOfInterest());
		
		if(!visible) {
			
			visible = perceptionModel.isVisible(physical.getThisPedestrian(), 
					goal.getGoalArea().getPointOfInterest());
		}

		goal.setVisible(visible);
	}

	public void executeProximity(GoalChunk goal, PhysicalChunk physical, OperationChunk operation) {
		
		Boolean proximity = false;

		if(goal.getVisible() && 
		   operation.getGoal() != null &&
		   operation.getGoal().getId() == goal.getGoalId()) {
						
			if(operation.getCurrentTask() != null && 
			   operation.getCurrentTask() != Behavior.None && 
			   operation.getCurrentTask() != Behavior.Routing && 
			   physical.getThisPedestrian().getMotoricTask() == Motoric.Standing &&
			   goal.getProximity()) {

				proximity = true;
			}
			else {
				
				if(goal.getOccupancyType() == OccupancyType.Waiting) {
					
					proximity = goal.getGoalArea().getPointOfInterest().distance(physical.getThisPedestrian().getPosition()) 
							<= this.proximityDistance;
				}
				else {
					
					for(Vector2D positionsToCheck : physical.getPositionOfGroupMembers()) {
					
						if(goal.getGoalArea().getGeometry().contains(positionsToCheck)) {
							
							proximity = true;
						}
						else {
							
							Vector2D nearestPointToTarget = goal.getGoalArea().getGeometry()
									.getPointOnPolygonClosestToVector(positionsToCheck);
							
							proximity = nearestPointToTarget.distance(positionsToCheck) <= this.proximityDistance ||
									goal.getGoalArea().getGeometry().contains(positionsToCheck);
						}
					}
				}
			}
		}

		goal.setProximity(proximity);
	}
	
	public void executeDistance(GoalChunk goal, PhysicalChunk physical, SimulationState simulationState) {
		
		this.distancePerception.perceptDistance(goal, physical, simulationState);
	}
	
	public void executeOccupancy(PerceptionalModel perceptionModel, 
			GoalChunk goal, 
			PhysicalChunk physical,
			OperationChunk operation) {
		
		this.occupancyPerception.perceptOccupancy(perceptionModel, goal, physical, operation);
	}
}
