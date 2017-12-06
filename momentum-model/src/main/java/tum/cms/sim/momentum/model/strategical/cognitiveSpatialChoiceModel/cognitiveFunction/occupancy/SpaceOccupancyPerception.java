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

package tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.cognitiveFunction.occupancy;

import java.util.List;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.util.FastMath;

import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrian;
import tum.cms.sim.momentum.data.layout.area.IntermediateArea;
import tum.cms.sim.momentum.model.perceptional.PerceptionalModel;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.CognitiveConstant;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.CognitiveConstant.OccupancyType;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.memory.GoalChunk;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.memory.OperationChunk;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.memory.PhysicalChunk;
import tum.cms.sim.momentum.utility.geometry.Polygon2D;

public class SpaceOccupancyPerception implements IOccupancyPerception {

	private Double participatingPenalty = null;
	
	
	public Double getParticipatingPenalty() {
		return participatingPenalty;
	}

	public void setParticipatingPenalty(Double participatingPenalty) {
		this.participatingPenalty = participatingPenalty;
	}

	private Double waitingPenalty = null;
	
	public Double getWaitingPenalty() {
		return waitingPenalty;
	}

	public void setWaitingPenalty(Double waitingPenalty) {
		this.waitingPenalty = waitingPenalty;
	}

	@Override
	public void perceptOccupancy(PerceptionalModel perceptionModel, 
			GoalChunk goal, 
			PhysicalChunk physical,
			OperationChunk operation) {
		
		if(!(goal.getGoalArea() instanceof IntermediateArea) || goal.isDoor()) {
			
			goal.setOccupancy(0.0);
			return;
		}
		
		Double familiarity = CognitiveConstant.fromFamilirty(goal.getFamiliarity());
		Polygon2D areaPolygon = goal.getGoalArea().getGeometry();
		
		List<IPedestrian> pedestrianInteractingAtGoal = perceptionModel.findPedestrianSameTarget(
				physical.getThisPedestrian(),
				goal.getGoalArea(), 
				true,
				null);
		
		if(goal.getVisible()) {
			
			familiarity = 1.0;
		}
	
		Double occupancy = null;
		Pair<Double, Double> lengthUsedSpace = null;
		
		if(goal.getOccupancyType() == OccupancyType.Waiting && pedestrianInteractingAtGoal.size() > 0) {

//			if(CognitiveConstant.checkInBehavior(operation) && // freeze occupancy if in queue
//			   operation.getCurrentTask().getArea().getId() == goal.getGoalId()) {
//				
//				occupancy = goal.getOccupancy();
//			}
//			else {
//				
				lengthUsedSpace = this.usedTimeForWaiting(pedestrianInteractingAtGoal, goal);

				if(goal.getSinglePlace() && lengthUsedSpace.getRight() > 0.0) {
					
					occupancy = 1.0;
				}
//			}
		}
		else { // Engage area
			
			lengthUsedSpace = this.usedSpaceForEngage(pedestrianInteractingAtGoal, areaPolygon);
		}
		
		if(occupancy == null) { // if  null, update it
			
			// occupancy model, low familiarity will increase underestimation
			// value1 is used space value0 is existing space
			occupancy = 1.0 - FastMath.exp(-1.0 * familiarity * lengthUsedSpace.getRight() / lengthUsedSpace.getLeft());
		}


		goal.setOccupancy(occupancy);
	}
		
	private Pair<Double, Double> usedSpaceForEngage(List<IPedestrian> pedestrianInteractingAtGoal,
			Polygon2D areaPolygon) {
		
		Double length = areaPolygon.area();
		Double usedSpace = 0.0;

		double penalty = this.participatingPenalty;
		for(IPedestrian other : pedestrianInteractingAtGoal) {
			
			usedSpace += FastMath.pow(2.0 * other.getBodyRadius() + penalty, 2.0);
		}
	
		return new MutablePair<Double, Double>(length, usedSpace);
	}

	private Pair<Double, Double> usedTimeForWaiting(List<IPedestrian> pedestrianInteractingAtGoal, GoalChunk goal) {
		
		Double meanWaiting = goal.getPreference().getServiceTimeDistributions().getMean();
		Double stdWaiting = goal.getPreference().getServiceTimeDistributions().getStandardDeviation();
		Double length = meanWaiting + 2.0 * stdWaiting;
		Double usedSpace = 0.0;
		
		for(int iter = 0; iter < pedestrianInteractingAtGoal.size(); iter++) {
			
			usedSpace += meanWaiting * waitingPenalty;// * stdWaiting;
			//meanSpace += std;
		}

		return new MutablePair<Double, Double>(length, usedSpace);
	}
}
