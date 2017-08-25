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

package tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.loader;

import java.util.ArrayList;
import java.util.HashMap;

import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.CognitiveConstant.Familiarity;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.loader.parameter.GoalParameter;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.loader.parameter.PreferenceParameter;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.memory.GoalChunk;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.memory.PreferenceChunk;
import tum.cms.sim.momentum.utility.probability.distrubution.IDistribution;

public class ChunkLoader {


	public void loadPreferenceChunks(HashMap<Integer, PreferenceChunk> preferenceChunks,
			CognitiveParameterLoader cognitiveParameter) {

		IDistribution maximalInterarrival = null;
		double meanInterarrivalMaximal = 0.0;
		//double meanInterarrivalTimes = 0.0;
		
		for(PreferenceParameter preferenceParameter : cognitiveParameter.getPreferenceParameters()) {
			
			PreferenceChunk preferenceChunk = new PreferenceChunk();
			preferenceChunk.setPreferenceId(preferenceParameter.getPreferenceId());
			
			if(preferenceParameter.getInterarrivalDistribution() != null) {
				
				if(meanInterarrivalMaximal < preferenceParameter.getInterarrivalDistribution().getMean()) {
					
					maximalInterarrival = preferenceParameter.getInterarrivalDistribution();
				}
			
				
				//meanInterarrivalTimes += preferenceParameter.getInterarrivalDistribution().getMean();
		
				preferenceChunk.setMeanInterarrivalTimes(preferenceParameter.getInterarrivalDistribution().getMean());
			}
			
			preferenceChunk.setInterarrivalDistribution(preferenceParameter.getInterarrivalDistribution());
			preferenceChunk.setInterarrivalMeasurments(preferenceParameter.getInterarrivalMeasurments());
			
			preferenceChunk.setOneTimePreference(preferenceParameter.getOneTimePreference());
			preferenceChunk.setSeedId(preferenceParameter.getSeedId());
			preferenceChunk.setServiceTimeDistributions(preferenceParameter.getServiceTimeDistribution());
			preferenceChunks.put(preferenceChunk.getPreferenceId(), preferenceChunk);
		}
		
		//meanInterarrivalTimes = meanInterarrivalTimes / cognitiveParameter.getPreferenceParameters().size();
		
		for(PreferenceChunk preferenceChunk : preferenceChunks.values()) {
		
			preferenceChunk.setMaximalInterarrivalDistribution(maximalInterarrival);
			//preferenceChunk.setMeanInterarrivalTimes(meanInterarrivalTimes);
		}
	}
	
	public void loadGoalChunk(HashMap<Integer, GoalChunk> goalChunks, 
			HashMap<Integer, PreferenceChunk> preferenceChunks,
			CognitiveParameterLoader cognitiveParameter) {
		
		for(GoalParameter goalParameter : cognitiveParameter.getGoalParameters()) {
			
			GoalChunk goalChunk = new GoalChunk();
			goalChunk.setSinglePlace(goalParameter.getSinglePlace());
			goalChunk.setOpeningHours(goalParameter.getOpeningHours(), cognitiveParameter.getOpeningMalus());
			goalChunk.setGoalArea(goalParameter.getGoalArea());
			goalChunk.setOccupancyType(goalParameter.getOccupancyType());
			goalChunk.setPreference(preferenceChunks.get(goalParameter.getPreferenceId()));
			preferenceChunks.get(goalParameter.getPreferenceId()).addAssociatedGoal(goalChunk);
			
			// TODO temporarily everything is well known
			goalChunk.setFamiliarity(Familiarity.High);
	
			ArrayList<PreferenceChunk> predecessorPreferences = new ArrayList<>();
			
			for(Integer predecessorPreferenceId : goalParameter.getPredecessorsPreferences()) {
				
				predecessorPreferences.add(preferenceChunks.get(predecessorPreferenceId));
			}
			
			goalChunk.setPredecessorsPreferences(predecessorPreferences);
			
			goalChunks.put(goalChunk.getGoalId(), goalChunk);
		}
	}
}
