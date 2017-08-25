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
import java.util.HashMap;
import org.apache.commons.math3.util.FastMath;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.memory.GoalChunk;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.memory.PhysicalChunk;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.memory.PlanChunk;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.memory.PreferenceChunk;

public class SigmoidIntraValuation implements IValuation {

	/**
	 * The bigger the stronger the jump (rectangular)
	 */
	private double intensityChange = 1;

	public double getIntensityChange() {
		return intensityChange;
	}

	public void setIntensityChange(double intensityChange) {
		this.intensityChange = 1.0 / FastMath.pow(10.0, intensityChange);
	}

	@Override
	public void valuation(PlanChunk plan,
			Collection<GoalChunk> goals,
			Collection<PreferenceChunk> preferences,
			PhysicalChunk physicalChunk) {

		double k = 1.0 / (-1.0 * FastMath.log((1.0 / (1.0 - this.intensityChange)) - 1.0));

		HashMap<GoalChunk, Double> distanceValences = new HashMap<>();
		HashMap<GoalChunk, Double> occupancyValences = new HashMap<>();
		HashMap<GoalChunk, Double> perferenceValences = new HashMap<>();
		
		double minDistance = Double.MAX_VALUE;
		double maxDistance = 0.0;
		double minOccupancy = Double.MAX_VALUE;
		double maxOccupancy = 0.0;
		double minPreference = Double.MAX_VALUE;
		double maxPreference = 0.0;
		
		for(GoalChunk goal : goals) {
			
			if(!goal.isDoor()) { // is not door!
				
				// distance 1 is far, zero is near
				// from 0 till 1 to -1 till 1
				double scaledDistance = 1.0 - 2 * goal.getDistance();
				double distanceValence = 1.0 / (1.0 + FastMath.exp((-1.0 * scaledDistance) / k));
				minDistance = FastMath.min(minDistance, goal.getDistance());
				maxDistance = FastMath.max(maxDistance, goal.getDistance());
				distanceValences.put(goal, distanceValence);
				
				// occupancy 1 is full, zero is empty
				// from 0 till 1 to -1 till 1			
				double scaledOccupancy = 1.0 - 2 * goal.getOccupancy();
				double occupancyValence = 1.0 / (1.0 + FastMath.exp((-1.0 * scaledOccupancy) / k));
				minOccupancy = FastMath.min(minOccupancy, goal.getOccupancy());
				maxOccupancy = FastMath.max(maxOccupancy, goal.getOccupancy());
				occupancyValences.put(goal, occupancyValence);
				
				// preference 1 is important, zero is not interested
				// from 0 till 1 to -1 till 1
				double scaledPreference = goal.getPreference().getInterest() * 2.0 - 1.0;			
				Double preferenceValence = 1.0 / (1.0 + FastMath.exp((-1.0 * scaledPreference) / k));
				minPreference = FastMath.min(minPreference, goal.getPreference().getInterest());
				maxPreference = FastMath.max(maxPreference, goal.getPreference().getInterest());
				perferenceValences.put(goal, preferenceValence);
			}
		}

		double sum = ((maxDistance - minDistance) + (maxOccupancy - minOccupancy) + (maxPreference - minPreference));
		Double distanceImpact = (maxDistance - minDistance) / sum; //distanceStdDev / sum;
		Double occupancyImpact =  (maxOccupancy - minOccupancy) / sum; //occupancyStdDev / sum;
		Double preferenceImpact = (maxPreference - minPreference) / sum; //preferenceStdDev / sum;

		for(GoalChunk goal : goals) {
			
			Double subjectiveValuation = 0.0;
			
			if(!goal.isDoor()) { // is not door!

				subjectiveValuation = distanceValences.get(goal) * distanceImpact +
						occupancyValences.get(goal) * occupancyImpact +
						perferenceValences.get(goal) * preferenceImpact;
			}
			else {
				// get preference directly because it is already 0 to 1 scaled, only for doors
				// here the preference is an accumulation of the interest (sum off all)
				subjectiveValuation = goal.getPreference().getInterest();
			}
						 
			goal.setValenceStrength(subjectiveValuation);
		}
	}
}
