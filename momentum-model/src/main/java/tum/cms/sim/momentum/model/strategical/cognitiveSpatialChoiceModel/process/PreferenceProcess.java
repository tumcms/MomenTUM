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

import java.util.List;

import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.cognitiveFunction.preference.IPreference;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.memory.GoalChunk;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.memory.PhysicalChunk;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.memory.PreferenceChunk;

public class PreferenceProcess {

	private IPreference preferring = null;

	public IPreference getPreferring() {
		return preferring;
	}

	public void setPreferring(IPreference preferring) {
		this.preferring = preferring;
	}

	private Integer preferenceId = null;

	public Integer getPreferenceId() {
		return preferenceId;
	}

	public void setPreferenceId(Integer preferenceId) {
		this.preferenceId = preferenceId;
	}
	
	public Double executePreference(List<GoalChunk> goals,
			PreferenceChunk preference, 
			SimulationState simulationState,
			PhysicalChunk physical,
			Double cognitiveClock) {
		
		return preferring.preferring(goals, preference, simulationState, physical, cognitiveClock).doubleValue();
	}
	
//	public static void normalizeInterest(Collection<GoalChunk> goals) {
//		
//		double maximalInterest = 0.0;
//		double minimalInterest= Double.MAX_VALUE;
//		
//		for(GoalChunk goal : goals) {
//			
//			if(goal.getInterest().doubleValue() > maximalInterest) {
//				
//				maximalInterest = goal.getInterest();
//			}
//			
//			if(minimalInterest > goal.getInterest().doubleValue()) {
//				
//				minimalInterest = goal.getInterest();
//			}
//		}
//		
//		for(GoalChunk goal : goals) {
//			
//			double currentInterest = goal.getInterest() - minimalInterest;
//	
//			goal.setInterest(currentInterest / (maximalInterest - minimalInterest));
//		}
//	}
}
