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

package tum.cms.sim.momentum.model.tactical.participating;

import tum.cms.sim.momentum.data.layout.area.Area;
import tum.cms.sim.momentum.model.tactical.SubTacticalModel;
import tum.cms.sim.momentum.utility.geometry.Segment2D;
import tum.cms.sim.momentum.utility.geometry.Vector2D;
import org.apache.commons.math3.util.FastMath;

public abstract class StayingModel extends SubTacticalModel {

	protected double calculateMinimalAttractorDistance(Area targetArea) {
		
		double distance = Double.MAX_VALUE;
		
//		if(targetArea.getGatheringSegment() == null) {
			
			Vector2D attractor = targetArea.getPointOfInterest();
			
			for(Segment2D border : targetArea.getGeometry().getSegments()) {
				
				distance = FastMath.min(distance, attractor.distance(border.getPointOnSegmentClosestToVector(attractor)));
			}
//		}
//		else {
//	
//			for(Vector2D corner : targetArea.getGeometry().getSegments()) {
//				
//				distance = FastMath.min(distance, attractor.distance(border.getPointOnSegmentClosestToVector(attractor)));
//			}
//			
//			distance = targetArea.getGatheringSegment().getLenghtDistance();
//		}
//		
		return distance;
	}
	
	protected double calculateMaximalAttractorDistance(Area targetArea) {
		
		double distance = Double.MIN_VALUE;
		
//		if(targetArea.getGatheringSegment() == null) {
//			
			Vector2D attractor = targetArea.getPointOfInterest();
			
			for(Vector2D corner : targetArea.getGeometry().getVertices()) {
				
				distance = FastMath.max(distance, attractor.distance(corner));
			}
//		}
//		else {
//	
//			distance = targetArea.getGatheringSegment().getLenghtDistance();
//		}
		
		return distance;
	}
	protected double calculateCurrentAttractorDistance(Area targetArea, Vector2D participationPosition) {
		
		Vector2D attractor = null;
		
		if(targetArea.getGatheringSegment() == null) {
			
			attractor = targetArea.getPointOfInterest();
		}
		else {
			
			attractor = targetArea.getGatheringSegment().getPointOnSegmentClosestToVector(participationPosition);
		}
		
		return attractor.distance(participationPosition);
	}
	
//	protected Vector2D findMassGroupGatheringPoint(List<IPedestrian> groupMembers, ITacticalPedestrian currentPedestrian) { 
//		
//		Vector2D massGatheringPoint = null;
//		
//		if(groupMembers.size() > 0) {
//			
//			for(IPedestrian groupMember : groupMembers) {
//				
//				if(groupMember.getNextNavigationTarget().getId() == 
//				   currentPedestrian.getNextNavigationTarget().getId() &&
//				   groupMember.getBehaviorTask() == Behavior.Participating) {
//				
//					if(massGatheringPoint == null) {
//						
//						massGatheringPoint = groupMember.getNextWalkingTarget();
//					}
//					else {
//						
//						massGatheringPoint.sum(groupMember.getNextWalkingTarget());
//					}
//				}
//			}
//			
//			if(massGatheringPoint != null) {
//				
//				massGatheringPoint = massGatheringPoint.multiply(1.0/((double)groupMembers.size()));
//			}
//		}
//		
//		return massGatheringPoint;
//	}
}
