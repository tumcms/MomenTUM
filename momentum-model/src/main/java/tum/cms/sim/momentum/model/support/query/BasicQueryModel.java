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

package tum.cms.sim.momentum.model.support.query;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import tum.cms.sim.momentum.data.agent.pedestrian.state.tactical.TacticalState.Behavior;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrian;
import tum.cms.sim.momentum.data.layout.area.Area;
import tum.cms.sim.momentum.data.layout.obstacle.Obstacle;
import tum.cms.sim.momentum.data.layout.obstacle.SolidObstacle;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.support.PedestrianSupportModel;
import tum.cms.sim.momentum.model.support.perceptional.PerceptionalModel;
import tum.cms.sim.momentum.utility.geometry.Polygon2D;
import tum.cms.sim.momentum.utility.geometry.Segment2D;
import tum.cms.sim.momentum.utility.geometry.Vector2D;

public class BasicQueryModel extends PedestrianSupportModel {

	@Override
	public void callPreProcessing(SimulationState simulationState) {

		// nothing to do
	}

	@Override
	public void callPostProcessing(SimulationState simulationState) {
		// Nothing to do
	}

	/**
	 * Could be called by all models
	 * If the pedestrian is in a social group, the method returns all pedestrian in the same group.
	 * The method may only return visible or group members within a certain radius, based on the implementation.
	 */
	public List<IPedestrian> groupMembers(IPedestrian pedestrian,
			PerceptionalModel perception,
			SimulationState simulationState) {
	
		return perception.getAllPedestrians(pedestrian).stream()
			.filter(other -> other.getGroupId() == pedestrian.getGroupId())
			.collect(Collectors.toList());
	}
	
	/**
	 * @param pedestrian
	 * @param perception
	 * @param simulationState
	 * @return
	 */
	public List<IPedestrian> findLeaders(IPedestrian pedestrian,
			PerceptionalModel perception,
			SimulationState simulationState) {
	
		return perception.getAllPedestrians(pedestrian).stream()
			.filter(other -> other.getGroupId() == pedestrian.getGroupId())
			.filter(other -> other.isLeader())
			.collect(Collectors.toList());
	}
	
	/**
	 * This method checks if a pedestrian overlaps with the position
	 * of other pedestrians if it would go to the point to check.
	 * 
	 * TODO highly inefficient
	 * 
	 */
	public boolean isCollisionWithPedestrian(double pedestrianBodyRadius,
			double safetyDistance,
			Vector2D pointToCheck, 
			List<IPedestrian> relevantOtherPedestrians) {
		
		for(IPedestrian other : relevantOtherPedestrians) {
			
			double distance = other.getNextWalkingTarget().subtract(pointToCheck).getMagnitude();

			if(distance < other.getBodyRadius()
					+ safetyDistance
					+ pedestrianBodyRadius) {
				
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * This method checks if a pedestrian overlaps with the borders of a area
	 * if it would go to the point to check.
	 * 
	 * TODO highly inefficient
	 * 
	 */
	public boolean isToCloseToAreaBorder(double pedestrianBodyRadius,
			double safetyDistance,
			Vector2D pointToCheck, 
			Area area) {
		
		boolean isClose = false;
		Vector2D closePoint = null;
		
		for(Segment2D border : ((Polygon2D)area.getGeometry()).polygonAsSegments().getLineSegments()) {
					
			closePoint = border.getPointOnSegmentClosestToVector(pointToCheck);
			double closeDistance = closePoint.distance(pointToCheck);
			
			if(closeDistance < safetyDistance + pedestrianBodyRadius) {
				
				isClose = true;
				break;
			}
		}
		
		return isClose;
	}

	/**
	 * TODO highly inefficient
	 * 
	 * @param bodyRadius
	 * @param safetyDistance
	 * @param singleParticipationPosition
	 * @param obstacles
	 * @return
	 */
	public boolean isCollisionWithObstacles(double bodyRadius,
			double safetyDistance,
			Vector2D singleParticipationPosition,
			ArrayList<Obstacle> obstacles) {

		boolean isCollision = false;
		
		for(Obstacle obstacle : obstacles) {
			
			if(obstacle.getGeometry().distanceBetween(singleParticipationPosition) < safetyDistance + bodyRadius) {
				
				isCollision = true;
				break;
			}
			
			if(obstacle instanceof SolidObstacle) {
				
				if(obstacle.getGeometry().contains(singleParticipationPosition)) {
					
					isCollision = true;
					break;
				}
			}
		}
		
		return isCollision;
	}
	
	/**
	 * This method finds all other pedestrians that choose to go to the same target.
	 * The method will ignore pedestrians that which performed behavior is None
	 * or Route if the checkBehavior parameter is true
	 * 
	 * TODO highly inefficient
	 * 
	 * @param proximity 
	 */
	public List<IPedestrian> findPedestrianSameTarget(IPedestrian pedestrian,
			PerceptionalModel perceptionModel,
			Area area,
			Boolean checkBehavior,
			Double proximity) {
		
		List<IPedestrian> sameGoal = new ArrayList<IPedestrian>();
		
		for(IPedestrian other : perceptionModel.getAllPedestrians(pedestrian)) {
			
			boolean proximityCheck = proximity != null && proximity > 0.0 && 
		    		(area.getGeometry().distanceBetween(other.getPosition()) < proximity) ||
		      		 area.getGeometry().contains(other.getPosition());
			
			boolean goalCheck = other.getNextNavigationTarget() != null &&
					   other.getNextNavigationTarget().getId() == area.getId() &&
					   (!checkBehavior || (other.getBehavior() != Behavior.Routing && other.getBehavior() != Behavior.None));
		      		 
			if(goalCheck || proximityCheck) {
				
				sameGoal.add(other);
			}
		}
		
		return sameGoal;
		
//		return perceptionModel.getAllPedestrians(pedestrian)
//				.stream()
//				.filter(other -> other.getNextNavigationTarget() != null &&
//							pedestrian.getNextNavigationTarget().getId() == area.getId() && 
//							     (!checkBehavior || (other.getPerformedBehavior() != Behavior.Routing && other.getPerformedBehavior() != Behavior.None)))
//				.collect(Collectors.toList());
	}
	
//	/**
//	 * Calculates the space used up by pedestrians at the area.
//	 * Pedestrians are squares here.
//	 * @param safetyDistance 
//	 */
//	public Double calculateUsedSpace(PerceptionalModel perceptionModel, 
//			IPedestrian pedestrian,
//			Double safetyDistance, 
//			Area area) {
//		
//		Double usedAreaSize = 0.0;
//		
//		List<IPedestrian> allPedestrians = perceptionModel.getAllPedestrians(pedestrian);
//		
//		for(IPedestrian perceptedPedestrian : allPedestrians) {
//			
//			if(!perceptedPedestrian.getId().equals(pedestrian.getId())) {
//				
//				boolean inArea = area.getGeometry().contains(
//						perceptedPedestrian.getPosition());
//				
//				if(inArea) {
//					
//					double radius = perceptedPedestrian.getBodyRadius();
//					usedAreaSize += FastMath.pow(radius * 2.0 + safetyDistance, 2.0); // area used by pedestrian
//				}
//			}
//		}
//		
//		return usedAreaSize;
//	}

	
//	public boolean isToCloseToSameTarget(IPedestrian currentPedestrian, List<IPedestrian> sameTargetPedestrians, double safetyDistance) {
//		
//		boolean toClose = false;
//		
//		for(IPedestrian sameTargetPedestrian : sameTargetPedestrians) {
//			
//			if(safetyDistance < sameTargetPedestrian.getPosition().distance(currentPedestrian.getPosition()) +
//								currentPedestrian.getBodyRadius() +
//								sameTargetPedestrian.getBodyRadius()) {
//				
//				toClose = true;
//				break;
//			}
//		}
//		
//		return toClose;
//	}
	

//	/**
//	 * This method will check if the target is lost or the intermediate navigation point cannot
//	 * be seen anymore. If this occurs, a rerouting is the rule of choice.
//	 */
//	public boolean isReNavigationDemanded(IPedestrian pedestrian,
//			PerceptionalModel perception) {
//		
//		Vertex tacticalTarget = pedestrian.getTacticalState().isEmpty() ?
//				null : 
//				pedestrian.getTacticalState().getTargetVertex();
//
//		Vertex strategicTarget = pedestrian.getStrategicalState().isEmpty() ?
//				null : 
//				this.scenarioManager.getGraph().getGeometryVertex(pedestrian.getStrategicalState().getNextTargetArea().getGeometry());
//
//		if(tacticalTarget == null && strategicTarget != null) { // reroute
//			
//			return true;
//		}
//
//		if(!pedestrian.getStrategicalState().isEmpty() &&
//		   !pedestrian.getTacticalState().isEmpty() &&
//		   !perception.isVisible(pedestrian, pedestrian.getTacticalState().getCurrentVertex())) {
//	
//			return true;
//		}
//		
//		if(!tacticalTarget.equals(strategicTarget)) {
//			
//			return true;
//		}
//		
//		if(tacticalTarget != null && tacticalTarget.equals(strategicTarget)) { // no reroute, at target
//		
//			return false;
//		}
//
//		return false; 
//	}
	
//	/**
//	 * Finds pedestrian in a given area, if visible
//	 * 
//	 * @param currentPedestrian
//	 * @param area
//	 * @return pedestrian list
//	 */
//	public List<IImmutablePedestrian> findPedestriansInArea(IPedestrian currentPedestrian, 
//			PerceptionalModel perception,
//			Area area) {
//	
//		ArrayList<IImmutablePedestrian> foundPedestrians = new ArrayList<IImmutablePedestrian>();
//		
//		if(perception.isVisible(currentPedestrian, area)) {
//			
//			foundPedestrians.addAll(this.pedestrianManager.getAllPedestriansImmutable()
//						.stream()
//						.filter(pedestrian -> pedestrian.getId() != currentPedestrian.getId() &&
//							area.getGeometry().contains(pedestrian.getPerceptedOperationalState().getPosition()) &&
//							perception.isVisible(currentPedestrian, pedestrian))
//						.collect(Collectors.toList()));
//		}
//		
//		return foundPedestrians;
//	}
	
}
