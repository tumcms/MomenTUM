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

package tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.cognitiveFunction.reschedule;

import java.util.ArrayList;
import java.util.List;

import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.CognitiveConstant.Deciding;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.CognitiveConstant.RescheduleThreshold;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.memory.GoalChunk;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.memory.OperationChunk;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.memory.PlanChunk;
import tum.cms.sim.momentum.utility.matrixCalculus.Matrix2D;
import tum.cms.sim.momentum.utility.matrixCalculus.Matrix2DOperation;

public class DecisionFieldTheoryReschedule implements IReschedule {

	private Double rescheduleTime = 0.0;

	public Double getRescheduleTime() {
		return rescheduleTime;
	}

	private RescheduleState rescheduleState = null;

	private Double eliminateThreshold = -1.0;
	
	public Double getEliminateThreshold() {
		return eliminateThreshold;
	}

	public void setEliminateThreshold(Double eliminateThreshold) {
		this.eliminateThreshold = eliminateThreshold;
	}

	private Double decisionThreshold = 1.0;

	public Double getDecisionThreshold() {
		return decisionThreshold;
	}

	public void setDecisionThreshold(Double decisionThreshold) {
		this.decisionThreshold = decisionThreshold;
	}
	
	private ArrayList<GoalChunk> rescheduleGoals = null;
	
	public ArrayList<GoalChunk> getRescheduleGoals() {
		return rescheduleGoals;
	}

	public void setRescheduleGoals(ArrayList<GoalChunk> rescheduleGoals) {
		this.rescheduleGoals = rescheduleGoals;
	}
	
	@Override
	public void rescheduling(PlanChunk plan, OperationChunk operation, Double decisionClock) {
		
		if(!(plan.getDeciding() == Deciding.Rescheduling)) {
			
//			if(!plan.getValenceChanged() || plan.getDeciding() == Deciding.Scheduling || CognitiveConstant.checkInBehavior(operation)) {
//				
//				return;
//			}
		}
			
		if(rescheduleState == null) {
			
			this.rescheduleGoals = new ArrayList<>();
			
			plan.getSchedule().stream()
				.filter(goalChunk -> goalChunk.getVisible())
				.forEach(visibleGoal -> rescheduleGoals.add(visibleGoal));
			
			plan.setDeciding(Deciding.Rescheduling);
			rescheduleState = new RescheduleState();
			rescheduleState.setRescheduleGoals(this.rescheduleGoals);
			
			rescheduleTime = 0.0;
		}
			
//		Matrix2D attentionWeights = this.calculateAttentionWeights(plan.getAttentionShareDistance(),
//				plan.getAttentionShareOccupancy());		
		
		Matrix2D attributeEvaluation = this.calculateSubjectiveAttributeEvaluation(rescheduleGoals);
		
		// valence = contrasting * attributes * attention share + stochastic error
		Matrix2D attributeAttention = Matrix2DOperation.multiplicate(attributeEvaluation, null);// attentionWeights);
		
		// error term
		//Matrix2D errorAddedAttribution = Matrix2DOperation.elementAddition(attributeAttention, errorMatrix(N,1));
		
		Matrix2D contrastings = this.calculateContrastings(rescheduleGoals.size());
			
		Matrix2D valence = Matrix2DOperation.multiplicate(contrastings, attributeAttention);
		
		// goalsPreference = substitution * lastGoalsPreference + valence * deltaTime
		Matrix2D feedBack = calculateFeedBack(rescheduleGoals.size());
		
		Matrix2D substitutionLastPreference = Matrix2DOperation.multiplicate(feedBack, rescheduleState.getRescheduleMatrix());
		
		Matrix2D valenceDeltaTime = Matrix2DOperation.multiplicate(valence, decisionClock);
		
		Matrix2D preferenceStates = Matrix2DOperation.elementAddition(substitutionLastPreference, valenceDeltaTime);
		
		rescheduleState.setRescheduleMatrix(preferenceStates);
		rescheduleTime += decisionClock;
		
		if(this.checkFinished(rescheduleState)) {
		
			List<GoalChunk> rescheduledGoals = null; // rescheduleState.getRescheduledGoals();
	
			plan.setDeciding(Deciding.Finished);
			plan.updateSchedule(rescheduledGoals);
			
			rescheduleState = null;
			rescheduledGoals = null;
		}
	}

	
	private boolean checkFinished(RescheduleState rescheduleState) {
		
		Matrix2D preferenceMatrix = rescheduleState.getRescheduleMatrix();
		int numberOfElimnates = 0;
		boolean exceededThreshold = false;
				
		for(int iter = 0; iter < preferenceMatrix.getRowSize(); iter++) {
			
			if(preferenceMatrix.get(iter, 0) < eliminateThreshold) {
				
				numberOfElimnates++;
			}
			
			if(preferenceMatrix.get(iter, 0) > decisionThreshold) {
				
				exceededThreshold = true;
				break;
			}
		}
		
		if(numberOfElimnates >= rescheduleState.getRescheduleMatrix().getRowSize() - 1 || 
		   exceededThreshold) {
			
			return true;
		}
		
		return false;
	}

	private Matrix2D calculateContrastings(Integer numberOfGoals) {
		
		Matrix2D contrastings = new Matrix2D(numberOfGoals, numberOfGoals);
		double diagonalContrast = 1;
		double otherContrast = -1.0/(numberOfGoals - 1.0);
		
		for(int rowIndex = 0; rowIndex < numberOfGoals; rowIndex++) {

			for(int columnIndex = 0; columnIndex < numberOfGoals; columnIndex++) {
				
				if(rowIndex == columnIndex) {
					
					contrastings.set(rowIndex, columnIndex, diagonalContrast);
				}
				else {
					
					contrastings.set(rowIndex, columnIndex, otherContrast);
				}
			}	
		}
		
		return contrastings;
	}
	
	private Matrix2D calculateFeedBack(Integer numerOfActiveGoals) {
		
		Matrix2D feedBack = new Matrix2D(numerOfActiveGoals, numerOfActiveGoals);
		
		for(int columnIndex = 0; columnIndex < feedBack.getColumnSize(); columnIndex++) {
			
			for(int rowIndex = 0; rowIndex < feedBack.getRowSize(); rowIndex++) {
				
				if(rowIndex == columnIndex) {
					
					feedBack.set(rowIndex, columnIndex, RescheduleThreshold.EqualitySubstitution);
				}
				else {
					
					feedBack.set(rowIndex, columnIndex, 0.0);	
				}
			}
		}
		
		return feedBack;
	}
	
	private Matrix2D calculateSubjectiveAttributeEvaluation(List<GoalChunk> rescheduleGoals) {
	
		Matrix2D attributeEvaluation = new Matrix2D(rescheduleGoals.size(), RescheduleThreshold.AttributeNumber);
		
		for(int iter = 0; iter < rescheduleGoals.size(); iter++) {

			attributeEvaluation.set(iter, 0, rescheduleGoals.get(iter).getInterest());
			attributeEvaluation.set(iter, 1, 1.0 - rescheduleGoals.get(iter).getOccupancy());
			attributeEvaluation.set(iter, 2, 1.0 - rescheduleGoals.get(iter).getDistance());
		}
		
		return attributeEvaluation;
	}
	
//	private Matrix2D calculateAttentionWeights(double distanceShare, double occupancyShare) {
//		
//		Matrix2D attentionWeights = new Matrix2D(RescheduleThreshold.AttributeNumber, 1);
//
//		Double attention = PedestrianBehaviorModel.getRandom().nextDouble();
//
//		if(attention < distanceShare) {
//			
//			attentionWeights.set(0, 0, 1.0);
//		}
//		else {
//			
//			attentionWeights.set(0, 0, 0.0);	
//		}
//		
//		if(distanceShare < attention && attention < distanceShare + occupancyShare) {
//			
//			attentionWeights.set(1, 0, 1.0);
//		}
//		else {
//			
//			attentionWeights.set(1, 0, 0.0);	
//		}
//		
//		if(attention >= distanceShare + occupancyShare) {
//			
//			attentionWeights.set(2, 0, 1.0);
//		}
//		else {
//			
//			attentionWeights.set(2, 0, 0.0);	
//		}
//		
//		return attentionWeights;
//	}
	
	private class RescheduleState {

		//private List<GoalChunk> goals = null;
		private Matrix2D rescheduleMatrix = null;
		
		public void setRescheduleMatrix(Matrix2D rescheduleMatrix) {
			this.rescheduleMatrix = rescheduleMatrix;
		}

		public Matrix2D getRescheduleMatrix() {
			
			return rescheduleMatrix;
		}

		public void setRescheduleGoals(List<GoalChunk> goals) {
			
			rescheduleMatrix = new Matrix2D(goals.size(), 1);

			for(int iter = 0; iter < goals.size(); iter++) {
				
				rescheduleMatrix.set(iter, 0, 0.0);
			}
			
			//this.goals = goals; 
		}
		
//		public List<GoalChunk> getRescheduledGoals() {
//			
//			ArrayList<GoalChunk> goals = new ArrayList<>();
//			ArrayList<Pair<Integer, Double>> goalRescheduled = new ArrayList<>();
//			
//			for(int iter = 0; iter < rescheduleMatrix.getRowSize(); iter++) {
//				
//				goalRescheduled.add(new Pair<Integer, Double>(iter, rescheduleMatrix.get(iter, 0)));
//			}
//			
//			goalRescheduled.sort(goalReorganizer);
//			
//			for(int iter = 0; iter < goalRescheduled.size(); iter++) {
//				
//				// this.goals is in original order, and rescheduleMatrix preserves this order
//				// the goalRescheduled is correctly sorted regarding new preference and 
//				// stores in value0 the original index of this.goals
//				goals.add(this.goals.get(goalRescheduled.get(iter).getValue0()));
//			}
//			
//			return goals;
//		}
//
//		private Comparator<Pair<Integer, Double>> goalReorganizer = new Comparator<Pair<Integer, Double>>() {
//
//			@Override
//			public int compare(Pair<Integer, Double> first, Pair<Integer, Double> second) {
//				
//				return first.getValue1().compareTo(second.getValue1());
//			}
//
//		};
	}
}
