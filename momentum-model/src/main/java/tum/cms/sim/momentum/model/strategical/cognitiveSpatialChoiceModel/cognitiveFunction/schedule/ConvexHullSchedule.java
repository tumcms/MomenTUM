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
import java.util.List;
import org.apache.commons.math3.util.FastMath;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.memory.GoalChunk;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.memory.PhysicalChunk;
import tum.cms.sim.momentum.utility.geometry.GeometryFactory;
import tum.cms.sim.momentum.utility.geometry.Vector2D;
import tum.cms.sim.momentum.utility.geometry.operation.GeometryAdditionals;

public class ConvexHullSchedule implements ISchedule {

	
	private Double nearestNeighborThreshold = null;
	
	public Double getNearestNeighborThreshold() {
		return nearestNeighborThreshold;
	}

	public void setNearestNeighborThreshold(Double nearestNeighborThreshold) {
		this.nearestNeighborThreshold = nearestNeighborThreshold;
	}

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

		// Calculate convex hull
		List<Vector2D> convexHull = null;
		if(valenceCenters.size() > 3) {
			
			convexHull = GeometryFactory.calculateConvexHull(valenceCenters);
		}
		else {
			
			convexHull = valenceCenters;
		}

		
		// find starting point for tour
		Vector2D current = GeometryAdditionals.findClosestSet(physical.getThisPedestrian().getPosition(), valenceCenters).get(0);
		
		Boolean currentIsHull = false;
		
		for(int iter = 0; iter < convexHull.size(); iter++) {
			
			if(convexHull.get(iter).equals(current)) {
				
				currentIsHull = true;
				break;
			}
		}
	
		// convex hull & nearest interior planing procedure loop
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
			
			for(int iter = 0; iter < convexHull.size(); iter++) {
				
				if(convexHull.get(iter).equals(current)) {
					
					convexHull.remove(iter);

					break;
				}
			}	

			if(scheduleSize == 0 || valenceCenters.size() == 0) {
				
				break;
			}
			
			ArrayList<Vector2D> closestOrdered = GeometryAdditionals.findClosestSet(current, valenceCenters);

			// 1. if the current is of hull find next closest interior
			if(currentIsHull || closestOrdered.size() == 1) {
				
				boolean found = false;
				for(int iter = 0; iter < closestOrdered.size(); iter++) {
					
					boolean notInHull = true;
					
					for(int check = 0; check < convexHull.size(); check++) {
						
						if(convexHull.get(check).equals(closestOrdered.get(iter))) {
							
							notInHull = false;
							break;
						}				
					}
					
					if(notInHull) {
						
						current = closestOrdered.get(iter);
						currentIsHull = false;
						found = true;
						break;
					}
				}
				
				// 1.1 if no more non interior exist, find the next hull
				if(!found) {
					
					current = GeometryAdditionals.findClosestSet(current, convexHull).get(0);
					currentIsHull = true;
				}
				
			}
			// 2. if current is interior
			else {
				
				// 2.1 find the next two closest points
				Vector2D closest = closestOrdered.get(0);
				Vector2D secondClosest = closestOrdered.get(1);
				
				// 2.2 check if the closest are part of hull
				Boolean closesetIsHull = convexHull.stream().anyMatch(hullVector -> hullVector.equals(closest));
				Boolean secondClosesetIsHull = convexHull.stream().anyMatch(hullVector -> hullVector.equals(secondClosest));
				
				// 2.3 select closest if in hull
				
				if(closesetIsHull) {
					
					current = closest;
					currentIsHull = closesetIsHull;
				}
				// 2.4 if both are not in hull select the closest
				else if(!closesetIsHull && !closesetIsHull) {
					
					current = closest;
					currentIsHull = closesetIsHull;
				}
				// 2.5 if the closest is not in hull and the second closest is in hull apply heuristsc
				else {
					
					if(current.distance(secondClosest) * this.nearestNeighborThreshold < current.distance(closest)) {
					
						current = secondClosest;
						currentIsHull = secondClosesetIsHull;
					}
					else {
						
						current = closest;
						currentIsHull = closesetIsHull;
					}
				}
			}
		}
		
		return schedule;
	}
}
