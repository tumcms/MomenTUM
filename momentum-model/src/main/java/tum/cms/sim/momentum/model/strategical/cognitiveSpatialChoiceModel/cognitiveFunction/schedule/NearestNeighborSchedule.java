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

package tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.cognitiveFunction.schedule;

import java.util.ArrayList;
import org.apache.commons.math3.util.FastMath;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.memory.GoalChunk;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.memory.PhysicalChunk;
import tum.cms.sim.momentum.utility.geometry.Vector2D;
import tum.cms.sim.momentum.utility.geometry.operation.GeometryAdditionals;

public class NearestNeighborSchedule implements ISchedule {

	@Override
	public ArrayList<GoalChunk> scheduling(ArrayList<GoalChunk> scheduleGoals, 
			PhysicalChunk physical, 
			int scheduleSize) {
		
		// add valence to the to schedule lists
		ArrayList<Vector2D> valenceCenters = new ArrayList<>();
		
		// mean distance to the goals, equals one unit of psychological distance
		Double toGoalUnit = scheduleGoals.stream()
			.mapToDouble(goal -> physical.getThisPedestrian().getPosition().distance(goal.getGoalArea().getGeometry().getCenter()))
			.average()
			.getAsDouble();
		
		for(GoalChunk goal : scheduleGoals) {
			
			Vector2D pedestrianPosition = physical.getThisPedestrian().getPosition();
			Vector2D toGoalVector = goal.getGoalArea().getGeometry().getCenter().subtract(pedestrianPosition);
			Vector2D toGoalDirection = toGoalVector.getNormalized().multiply(toGoalUnit);
			double strength = FastMath.exp(3.0 * goal.getValenceStrength()) - 1.0; 
	
			Vector2D valenceVector = pedestrianPosition.sum(toGoalVector.sum(toGoalDirection.multiply(strength)));
			valenceCenters.add(valenceVector);
		}
		
		// create empty plan		
		ArrayList<GoalChunk> schedule = new ArrayList<>();
				
		// find starting point for tour
		Vector2D current = GeometryAdditionals.findClosestSet(physical.getThisPedestrian().getPosition(), valenceCenters).get(0);
				
		// nearest neighbor interior planing procedure loop
		while(valenceCenters.size() > 0) {
			
			// add current goal to plan and
			// remove first schedule goal from goal list
			for(int iter = 0; iter < valenceCenters.size(); iter++) {
				
				if(valenceCenters.get(iter).equals(current)) {
					
					schedule.add(scheduleGoals.get(iter));
					scheduleGoals.remove(iter);
					scheduleSize--;
					valenceCenters.remove(iter);
					break;
				}
			}
			
			if(scheduleSize == 0 || valenceCenters.size() == 0) {
				
				break;
			}
			
			current = GeometryAdditionals.findClosestSet(current, valenceCenters).get(0);
		}
		
		return schedule;
	}
}
