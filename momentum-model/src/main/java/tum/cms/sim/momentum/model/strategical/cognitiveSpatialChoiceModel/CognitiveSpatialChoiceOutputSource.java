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

import java.util.HashMap;
import java.util.Iterator;

import tum.cms.sim.momentum.model.output.writerSources.genericWriterSources.ModelPedestrianWriterSource;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.memory.GoalChunk;

public class CognitiveSpatialChoiceOutputSource 
	extends ModelPedestrianWriterSource<CognitiveSpatialChoiceStrategical, CognitiveSpatialChoiceStrategicExtension> {

	private static final String GoalsNameName = "GoalName";

	private static final String DistanceName = "Distance";
	private static final String OccupancyName = "Occupancy";
	private static final String PreferenceName = "Preference";
	private static final String ActualizationName = "Actualization";
	private static final String AvailabilityName = "Availability";
	private static final String ImplementationName = "Implementation";
	private static final String ScheduleName = "Schedule";
	private static final String StartLocationName = "StartLocation";
	private static final String GroupIdName = "GroupId";
	
	private HashMap<Integer, Integer> dynamicGoalMap = null;
	private int dynamicOutputSize = 0;

	@Override
	protected boolean canWrite(CognitiveSpatialChoiceStrategicExtension currentPedestrianExtension) {
		
		if(currentPedestrianExtension == null || !currentPedestrianExtension.canWriteOutput()) {
			
			return false;
		}
		
		return true;
	}
	
	@Override
	protected String getPedestrianData(
			CognitiveSpatialChoiceStrategicExtension currentPedestrianExtension,
			String format,
			String dataElement) {
		
		if(dynamicGoalMap == null) {
			
			dynamicGoalMap = new HashMap<Integer, Integer>();
			int size = currentPedestrianExtension.getGoalChunksMap().size();
			Iterator<GoalChunk> goalIterator = currentPedestrianExtension.getGoalChunksMap().values().iterator();
			
			for(int iter = 0; iter < size; iter++) {
				
				dynamicGoalMap.put(iter, goalIterator.next().getGoalId());
			}		
		}

		if(dynamicOutputSize > dynamicGoalMap.size() - 1) {
			
			dynamicOutputSize = 0;
		}
		
		String result = null;

		if(dataElement.equals(GoalsNameName + String.valueOf(dynamicOutputSize))) {

			String goalName = currentPedestrianExtension.getGoalChunksMap()
					.get(dynamicGoalMap.get(dynamicOutputSize))
					.getGoalArea()
					.getName();
			
			result = String.format(format, goalName);		
		}
		else if(dataElement.equals(DistanceName + String.valueOf(dynamicOutputSize))) {
			
			Double distance = currentPedestrianExtension.getGoalChunksMap()
					.get(dynamicGoalMap.get(dynamicOutputSize))
					.getDistance();
			
			result = String.format(format, distance);
		}
		else if(dataElement.equals(OccupancyName + String.valueOf(dynamicOutputSize))) {
			
			Double occupancy = currentPedestrianExtension.getGoalChunksMap()
					.get(dynamicGoalMap.get(dynamicOutputSize))
					.getOccupancy();
			
			result = String.format(format, occupancy);
		}
		else if(dataElement.equals(PreferenceName + String.valueOf(dynamicOutputSize))) {
			
			Double interest = currentPedestrianExtension.getGoalChunksMap()
					.get(dynamicGoalMap.get(dynamicOutputSize))
					.getInterest();
			
			result = String.format(format, interest);
		}
		else if(dataElement.equals(ActualizationName + String.valueOf(dynamicOutputSize))) {
			
			String actualization = currentPedestrianExtension.getGoalChunksMap()
					.get(dynamicGoalMap.get(dynamicOutputSize))
					.getPreference()
					.getActualization()
					.name();
			
			result = String.format(format, actualization);
		}
		else if(dataElement.equals(AvailabilityName + String.valueOf(dynamicOutputSize))) {
			
			String availability = currentPedestrianExtension.getGoalChunksMap()
					.get(dynamicGoalMap.get(dynamicOutputSize))
					.getAvailability()
					.name();
			
			result = String.format(format, availability);
		}
		else if(dataElement.equals(ImplementationName)) {
			
			String implementation = currentPedestrianExtension.getBehavior().name();
			
			result = String.format(format, implementation);	
		}
		//else if(dataElement.equals(ScheduleCategoryName))
		else if(dataElement.equals(ScheduleName)) {
			
			this.dynamicOutputSize = -1;
			
			String scheduleList = "-1";
			
			if(!currentPedestrianExtension.getPlanChunk().getSchedule().isEmpty()) {
			
				StringBuilder builder = new StringBuilder();
				for(GoalChunk goalInSchedule : currentPedestrianExtension.getPlanChunk().getSchedule()) {

					builder.append(goalInSchedule.getGoalId());
					builder.append("_");
				}
				
				builder.append("-1");
				scheduleList = builder.toString();
			}

			result = String.format(format, scheduleList);
		}
		else if(dataElement.equals(StartLocationName)) {
			
			int startLocationId = currentPedestrianExtension.getStartLocation();
			
			result = String.format(format, startLocationId);
		}
		else if(dataElement.equals(GroupIdName)) {
			
			int groupId = currentPedestrianExtension.getGroupId();
			
			result = String.format(format, groupId);
		}
		
		this.dynamicOutputSize++;
		
		return result;
	}
}
