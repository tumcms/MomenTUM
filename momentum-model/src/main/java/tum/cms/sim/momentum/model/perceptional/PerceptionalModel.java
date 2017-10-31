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

package tum.cms.sim.momentum.model.perceptional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import tum.cms.sim.momentum.data.agent.pedestrian.state.other.MetaState;
import tum.cms.sim.momentum.data.agent.pedestrian.state.tactical.TacticalState.Behavior;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrian;
import tum.cms.sim.momentum.data.layout.area.Area;
import tum.cms.sim.momentum.data.layout.obstacle.Obstacle;
import tum.cms.sim.momentum.data.layout.obstacle.SolidObstacle;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.PedestrianSupportModel;
import tum.cms.sim.momentum.utility.geometry.Polygon2D;
import tum.cms.sim.momentum.utility.geometry.Segment2D;
import tum.cms.sim.momentum.utility.geometry.Vector2D;
import tum.cms.sim.momentum.utility.graph.Edge;
import tum.cms.sim.momentum.utility.graph.Vertex;

public abstract class PerceptionalModel extends PedestrianSupportModel {

//	public List<IPedestrian> getNearestPedestrians(IPedestrian pedestrian, double distance) {
//	
//		MetaState pedestrianMetaState = pedestrian.getMetaState();
//		
//		if (pedestrianMetaState.getCallableModelNames() == null) {
//				
//			return this.pedestrianManager.getNearestPedestriansImmutable(pedestrian, distance)
//			.stream()
//			.filter(other -> other.getId() != pedestrian.getId())
//			.collect(Collectors.toList());
//		}
//				
//		return this.pedestrianManager.getNearestPedestriansImmutable(pedestrian, distance)
//				.stream()
//				.filter(other -> pedestrianMetaState.areModelIdsVisible(other.getMetaState().getCallableModelNames()))
//				.filter(other -> other.getId() != pedestrian.getId())
//				.collect(Collectors.toList());
//	}
	
	public List<IPedestrian> getAllPedestrians(IPedestrian pedestrian) {
	
		MetaState pedestrianMetaState = pedestrian.getMetaState();
		
		if (pedestrianMetaState.getCallableModelNames() == null) {
				
			return this.pedestrianManager.getAllPedestriansImmutable()
			.stream()
			.filter(other -> other.getId() != pedestrian.getId())
			.collect(Collectors.toList());
		}
				
		return this.pedestrianManager.getAllPedestrians()
				.stream()
				.filter(other -> pedestrianMetaState.areModelIdsVisible(other.getMetaState().getCallableModelNames()))
				.filter(other -> other.getId() != pedestrian.getId())
				.collect(Collectors.toList());
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
			Area area,
			Boolean checkBehavior,
			Double proximity) {
		
		List<IPedestrian> sameGoal = new ArrayList<IPedestrian>();
		
		for(IPedestrian other : this.getAllPedestrians(pedestrian)) {
			
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
	}
	
	/**
	 * Could be called by all models
	 * If the pedestrian is in a social group, the method returns all pedestrian in the same group.
	 * The method may only return visible or group members within a certain radius, based on the implementation.
	 */
	public List<IPedestrian> groupMembers(IPedestrian pedestrian,
			SimulationState simulationState) {
	
		return this.getAllPedestrians(pedestrian).stream()
			.filter(other -> other.getGroupId() == pedestrian.getGroupId())
			.collect(Collectors.toList());
	}
	
	
	/**
	 * This method checks if a pedestrian overlaps with the position
	 * of other pedestrians if it would go to the point to check.
	 * 
	 * TODO highly inefficient
	 * 
	 */
	public boolean isCollisionWithPedestrian(IPedestrian pedestrian,
			double pedestrianBodyRadius,
			double safetyDistance,
			Vector2D pointToCheck,
			List<IPedestrian> otherPedestrinas) {
		
		for(IPedestrian other : otherPedestrinas) {
			
			double distance = other.getNextWalkingTarget().subtract(pointToCheck).getMagnitude();

			if(distance < other.getBodyRadius()
					+ safetyDistance
					+ pedestrianBodyRadius) {
				
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isCollisionWithPedestrian(IPedestrian pedestrian,
			double pedestrianBodyRadius,
			double safetyDistance,
			Vector2D pointToCheck) {
		
		return this.isCollisionWithPedestrian(pedestrian, pedestrianBodyRadius, safetyDistance, 
				pointToCheck, this.getAllPedestrians(pedestrian));
	}
		
	/**
	 * @param pedestrian
	 * @param perception
	 * @param simulationState
	 * @return
	 */
	public List<IPedestrian> findLeaders(IPedestrian pedestrian,
			SimulationState simulationState) {
	
		return this.getAllPedestrians(pedestrian).stream()
			.filter(other -> other.getGroupId() == pedestrian.getGroupId())
			.filter(other -> other.isLeader())
			.collect(Collectors.toList());
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

	public boolean isVisible(IPedestrian pedestrian, List<Vector2D> positionList) {
		for (Vector2D currentPosition : positionList) {
			if(isVisible(pedestrian, currentPosition))
				return true;
		}
		return false;
	}
	
	public abstract Collection<IPedestrian> getPerceptedPedestrians(IPedestrian pedestrian, SimulationState simulationState);
	
	public abstract boolean isVisible(IPedestrian pedestrian, IPedestrian otherPedestrian);
	
	public abstract boolean isVisible(IPedestrian pedestrian, Vector2D position);

	public abstract boolean isVisible(IPedestrian pedestrian, Area area);
	
	public abstract boolean isVisible(IPedestrian pedestrian, Vertex vertex);

	public abstract boolean isVisible(IPedestrian pedestrian, Edge edge);	
	
	public abstract boolean isVisible(Vector2D viewPort, Vector2D position);
	
	public abstract double getPerceptionDistance();
}
