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

package tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.cognitiveFunction.valuation;
import java.util.Collection;

import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.memory.GoalChunk;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.memory.PhysicalChunk;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.memory.PlanChunk;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.memory.PreferenceChunk;

public class WeightedValuation implements IValuation {

	private Double interestShare = null;
	
	public Double getInterestShare() {
		return interestShare;
	}

	public void setInterestShare(Double interestShare) {
		this.interestShare = interestShare;
	}

	@Override
	public void valuation(PlanChunk plan,
			Collection<GoalChunk> goals,
			Collection<PreferenceChunk> preferences,
			PhysicalChunk physicalChunk) {

//		Double sumDistance = goals.stream().mapToDouble(GoalChunk::getDistance).sum();
//		Double sumInterest = preferences.stream().mapToDouble(PreferenceChunk::getInterest).sum();
//		Double sumOccupancy = goals.stream().mapToDouble(GoalChunk::getOccupancy).sum();

		//Double sum = sumInterest + sumOccupancy + sumDistance;
		
		// calculate attribute attention share		
//		plan.setAttentionShareDistance(sumDistance/sum); 
//		plan.setAttentionShareOccupancy(sumOccupancy/sum); 
//		plan.setAttentionShareInterest(sumInterest/sum); 
//		
//		for(GoalChunk goal : goals) {
//
//			// Calculate valences, scale from 0.0 to 1.0 (extreme values) 0 is optimal
//			Double distanceValence = plan.getAttentionShareDistance() * goal.getDistance();
//			Double occupancyValence = plan.getAttentionShareOccupancy() * goal.getOccupancy();
//			Double interestValence = plan.getAttentionShareInterest() *  (1.0 - goal.getInterest());
//			
//			Double subjectiveValuation = distanceValence + occupancyValence + interestValence;
//
//			goal.setValenceStrength(subjectiveValuation);
//		}
	}
	
//	private Pair<Double, Double> calculateAttentionShare(Collection<GoalChunk> goals) {
//		
//		Double interestDistanceInfluence = goals.stream().mapToDouble(GoalChunk::getDistance).max().getAsDouble() - 
//				goals.stream().mapToDouble(GoalChunk::getDistance).min().getAsDouble();
//		Double interestOccupancyInfluence = goals.stream().mapToDouble(GoalChunk::getOccupancy).max().getAsDouble() - 
//				goals.stream().mapToDouble(GoalChunk::getDistance).min().getAsDouble();
//		
//		Pair<Double, Double> distanceOccupancyAttentionShare = this.calculateDistanceOccupancyTradeoff(
//				interestDistanceInfluence, 
//				interestOccupancyInfluence);
//
//		Double sum = (distanceOccupancyAttentionShare.getValue0() + distanceOccupancyAttentionShare.getValue1())
//				* (1.0 - this.interestShare);
//		Double distanceShare = distanceOccupancyAttentionShare.getValue0() * sum;
//		Double occupanceShare = distanceOccupancyAttentionShare.getValue1() * sum;
//				
//		return new Pair<Double, Double>(distanceShare, occupanceShare);
//	}
//	
//	/**
//	 * This method is based on the empirical data of 
//	 * 1995_G�rling_Tradeoffs of priorities against spatiotemporal constraints in sequencing activities in environments
//	 * 
//	 * x = distance Influence 
//	 * y = occupancy Influence
//	 * 
//	 * For an emphasis on priority with high occupancy
//	 * @(x,y)(1 - exp(0.3273*x)) + (1 - exp( -4.729*y))
//	 * if x - y < 0
//	 * 
//	 * For equal priority the attention share for distance is
//	 * @(x,y)(1 - exp(0.4524*x)) + (1 - exp(-4.65*y)) 
//	 * if x = y
//	 * 
//	 * For an emphasis on priority with high distance
//	 * @(x,y)(1 - exp(0.5616*x)) + (1 - exp(-6.0*y))
//	 * if x - y > 0
//	 * 
//	 * Finally interpolate between these levels linearly 
//	 * if the x - y value do not result in -1, 0, or 1
//	 * 
//	 * within the 3D space most top is the occupancy emphasis surface
//	 * in between equal emphasis surface
//	 * then the distance emphasis surface
//	 * 
//	 * @param interestDistanceInfluence
//	 * @param interestOccupancyInfluence
//	 * @return Pair<DistanceAttentionShare, OccupancyAttentionShare>
//	 */
//	private Pair<Double, Double> calculateDistanceOccupancyTradeoff(
//			Double interestDistanceInfluence,
//			Double interestOccupancyInfluence) {
//		
//		Double attentionDifference = interestDistanceInfluence - interestOccupancyInfluence;
//	
//		Double distanceAttentionShare = 0.0;
//		Double occupancyAttentionShare = 0.0;
//		
//		double equalShareParamAlpha = 0.4524;
//		double equalShareParamBeta = -4.65;
//		
//		double distanceShareParamAlpha = 0.5616;
//		double distanceShareParamBeta = -6.0;
//		
//		double occupancyShareParamAlpha = 0.3273;
//		double occupancyShareParamBeta = -4.729;
//		
//		// fEP = @(x,y)(1 - exp(0.4524*x)) + (1 - exp(-4.65*y))
//		// fHF = @(x,y)(1 - exp(0.5616*x)) + (1 - exp(-6.0*y))
//		// fHC = @(x,y)(1 - exp(0.3273*x)) + (1 - exp( -4.729*y))
//		
//		if(FastMath.abs(attentionDifference) == 0.0) { // attention is without emphasis
//			
//			distanceAttentionShare = (1.0 - FastMath.exp(equalShareParamAlpha * interestDistanceInfluence)) +
//					(1.0 - FastMath.exp(equalShareParamBeta * interestOccupancyInfluence));
//		}
//		else if(interestOccupancyInfluence == 0.0) { // attention only on distance 
//			
//			distanceAttentionShare = 1.0;
//		}
//		else if(interestDistanceInfluence == 0.0) { // attention only on occupancy 
//			
//			distanceAttentionShare = 0.0;
//		}
//		else { // interpolate between surfaces
//			
//			Double equalShare = (1.0 - FastMath.exp(equalShareParamAlpha * interestDistanceInfluence)) +
//					(1.0 - FastMath.exp(equalShareParamBeta * interestOccupancyInfluence));
//		
//			Double distanceShare = 0.0;
//		
//			if(attentionDifference > 0.0) { // attention is between equal and distance
//		
//				distanceShare = (1.0 - FastMath.exp(distanceShareParamAlpha * interestDistanceInfluence)) +
//						(1.0 - FastMath.exp(distanceShareParamBeta * interestOccupancyInfluence));		
//			}
//			else { /// attention is between equal and occupancy
//				
//				distanceShare = (1.0 - FastMath.exp(occupancyShareParamAlpha * interestDistanceInfluence)) +
//						(1.0 - FastMath.exp(occupancyShareParamBeta * interestOccupancyInfluence));
//			}
//			
//			// interpolate
//			Double slope = equalShare - distanceShare;
//			distanceAttentionShare = slope * attentionDifference + distanceShare;
//		}
//		
//		occupancyAttentionShare = 1.0 - distanceAttentionShare;
//		
//		return new Pair<Double, Double>(distanceAttentionShare, occupancyAttentionShare);
//	}
}
