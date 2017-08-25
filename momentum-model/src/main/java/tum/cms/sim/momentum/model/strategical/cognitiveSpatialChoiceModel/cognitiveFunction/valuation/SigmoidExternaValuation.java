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
import org.apache.commons.math3.util.FastMath;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.memory.GoalChunk;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.memory.PhysicalChunk;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.memory.PlanChunk;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.memory.PreferenceChunk;

public class SigmoidExternaValuation implements IValuation {

	/**
	 * The smaller the stronger the jump
	 */
	private double intensityChange = 0.01;

	public double getIntensityChange() {
		return intensityChange;
	}

	public void setIntensityChange(double intensityChange) {
		this.intensityChange = intensityChange;
	}

	@Override
	public void valuation(PlanChunk plan,
			Collection<GoalChunk> goals,
			Collection<PreferenceChunk> preferences,
			PhysicalChunk physicalChunk) {

		Double k = 1.0 / (-1.0 * FastMath.log((1.0 / (1.0 - this.intensityChange)) - 1.0));
		
		Double sumInterest = 0.0;
		
		for(PreferenceChunk preference : preferences) {
			
			double scaledPreference = preference.getInterest() * 2.0 - 1.0;// from 0 till 1 to -1 till 1
			Double preferenceValence = 1.0 / (1.0 + FastMath.exp((-1.0 * scaledPreference) / k));
			sumInterest += preferenceValence;
		}
		
		Double sumDistance = 0.0; 
		Double sumOccupancy = 0.0; 
		
		for(GoalChunk goal : goals) {
			
			double scaledDistance = goal.getDistance() * 2.0 - 1.0;// from 0 till 1 to -1 till 1
			Double distanceValence = 1.0 / (1.0 + FastMath.exp((-1.0 * scaledDistance) / k));
			sumDistance += distanceValence;
			
			double scaledOccupancy = goal.getOccupancy() * 2.0 - 1.0;// from 0 till 1 to -1 till 1
			Double occupancyValence = 1.0 / (1.0 + FastMath.exp((-1.0 * scaledOccupancy) / k));
			sumOccupancy += occupancyValence;
		}
		
		//Double sum = sumInterest + sumOccupancy + sumDistance;
		
		// calculate attribute attention share
//		plan.setAttentionShareDistance(sumDistance/sum); 
//		plan.setAttentionShareOccupancy(sumOccupancy/sum); 
//		plan.setAttentionShareInterest(sumInterest/sum); 
//
//		goals.stream().forEach(goal -> {
//			
//			// Calculate valences, scale from 0.0 to 1.0 (extreme values) 0 is optimal
//			Double distanceValence = plan.getAttentionShareDistance() * goal.getDistance();
//			Double occupancyValence = plan.getAttentionShareOccupancy() * goal.getOccupancy();
//			Double interestValence = plan.getAttentionShareInterest() *  (1.0 - goal.getInterest());
//			
//			Double subjectiveValuation = distanceValence + occupancyValence + interestValence;
//
//			goal.setValenceStrength(subjectiveValuation);
//		});
	}
}
