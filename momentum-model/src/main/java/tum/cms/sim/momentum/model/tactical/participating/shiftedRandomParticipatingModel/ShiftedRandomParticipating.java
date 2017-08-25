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

package tum.cms.sim.momentum.model.tactical.participating.shiftedRandomParticipatingModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.math3.util.FastMath;

import tum.cms.sim.momentum.data.agent.pedestrian.state.tactical.StayingState;
import tum.cms.sim.momentum.data.agent.pedestrian.state.tactical.TacticalState.Behavior;
import tum.cms.sim.momentum.data.agent.pedestrian.state.tactical.TacticalState.Motoric;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrian;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrianExtansion;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;
import tum.cms.sim.momentum.data.agent.pedestrian.types.ITacticalPedestrian;
import tum.cms.sim.momentum.data.layout.area.Area;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.PedestrianBehaviorModel;
import tum.cms.sim.momentum.model.tactical.participating.StayingModel;
import tum.cms.sim.momentum.utility.geometry.Vector2D;
import tum.cms.sim.momentum.utility.geometry.operation.GeometryAdditionals;

public class ShiftedRandomParticipating extends StayingModel {

	protected enum CrowdingType {
		
		Close,
		Everywhere,
		Far,
		Center
	}

	private static String numberOfGamblesName = "numberOfGambles";
	private static String safetyDistanceName = "safetyDistance";
	private static String groupPositionRadiusName = "groupPositionRadius";
	private static String closeCrowdingName = "close";
	private static String farCrowdingName = "far";
	private static String centerCrowdingName = "center";
	private static String participateDistanceName = "participateDistance";
	
	protected int numberOfGambles = 10;
	protected double safetyDistance = 0.1;
	protected double gatherGroupRadius = 3.0; 
	protected double participateDistance = 4.0;
	
	protected HashMap<Integer, CrowdingType> areasCrowdingTypes = new HashMap<>();
	protected HashMap<Integer, Double> minimalAttractionDistances = new HashMap<>();
	protected HashMap<Integer, Double> maximalAttractionDistances = new HashMap<>();
	
	@Override
	public IPedestrianExtansion onPedestrianGeneration(IRichPedestrian pedestrian) {
		
		// Nothing to do
		return null;
	}
	
	@Override
	public void onPedestrianRemoval(IRichPedestrian pedestrian) {
		
		// Nothing to do
	}
	
	@Override
	public void callPreProcessing(SimulationState simulationState) {

		ArrayList<String> closeCrowdingCategories = this.properties.<String>getListProperty(closeCrowdingName);
		ArrayList<String> farCrowdingCategories = this.properties.<String>getListProperty(farCrowdingName);
		ArrayList<String> centerCrowdingCategories = this.properties.<String>getListProperty(centerCrowdingName);
		
		List<Area> areas = this.scenarioManager.getAreas();
		
		for(Area area : areas) {
			
			if(closeCrowdingCategories != null &&
			   closeCrowdingCategories.size() > 0 &&
			   area.isInCategories(closeCrowdingCategories)) {
			
				areasCrowdingTypes.put(area.getId(), CrowdingType.Close);
			}
			else if(farCrowdingCategories != null &&
			   farCrowdingCategories.size() > 0 &&
			   area.isInCategories(farCrowdingCategories)) {
			
				areasCrowdingTypes.put(area.getId(), CrowdingType.Far);
			}
			else if(centerCrowdingCategories != null &&
					centerCrowdingCategories.size() > 0 &&
					 area.isInCategories(centerCrowdingCategories)) {
					
				areasCrowdingTypes.put(area.getId(), CrowdingType.Center);
			} 
			else {
				
				areasCrowdingTypes.put(area.getId(), CrowdingType.Everywhere);
			}

			minimalAttractionDistances.put(area.getId(), this.calculateMinimalAttractorDistance(area));
			maximalAttractionDistances.put(area.getId(), this.calculateMaximalAttractorDistance(area));
		}
		
		this.safetyDistance = this.properties.getDoubleProperty(safetyDistanceName);
		this.gatherGroupRadius = this.properties.getDoubleProperty(groupPositionRadiusName);
		this.numberOfGambles = this.properties.getIntegerProperty(numberOfGamblesName);
		this.participateDistance = this.properties.getDoubleProperty(participateDistanceName);
	}
	
	@Override
	public void callPostProcessing(SimulationState simulationState) {
		
		// Nothing to do
	}
	
	@Override
	public void callBeforeBehavior(SimulationState simulationState, Collection<IRichPedestrian> pedestrians) {

		pedestrians.forEach(pedestrian -> {
			
			if(pedestrian.getBehaviorTask() != Behavior.Staying || pedestrian.getStayingState() == null) {
				
				leaderPosition.remove(pedestrian.getId());
			}
		});
	}
	
	@Override
	public void callAfterBehavior(SimulationState simulationState, Collection<IRichPedestrian> pedestrians) {
		
		// Nothing to do
	}
	
	private HashMap<Integer,Vector2D> leaderPosition = new HashMap<>();
	
	@Override
	public void callPedestrianBehavior(ITacticalPedestrian pedestrian, SimulationState simulationState) {
		
		synchronized (this) {
			
			if(areasCrowdingTypes.get(pedestrian.getNextNavigationTarget().getId()).equals(CrowdingType.Center)) {
				
				Vector2D center = pedestrian.getNextNavigationTarget().getGeometry().getCenter();
				pedestrian.setStayingState(new StayingState(
						center,
						center.subtract(pedestrian.getPosition()).getNormalized(),
						pedestrian.getLastWalkingTarget()));
			}	
			else {
				
				if(pedestrian.getStayingState() != null) {
					
					if(pedestrian.isLeader() &&
					   this.query.isCollisionWithPedestrian(pedestrian.getBodyRadius(),
							0.0, 
							pedestrian.getStayingState().getStayingPosition(),
							this.perception.getAllPedestrians(pedestrian))) {
								
						pedestrian.setStayingState(null);
					}
					else if(pedestrian.isLeader() &&
							this.query.isCollisionWithObstacles(pedestrian.getBodyRadius(),
									safetyDistance, 
									pedestrian.getStayingState().getStayingPosition(),
									scenarioManager.getObstacles())) {
						
						pedestrian.setStayingState(null);
					}
					else if(!pedestrian.isLeader() && 
							(!leaderPosition.containsKey(pedestrian.getId()) ||
							  leaderPosition.get(pedestrian.getId()) == null)) {
						
						pedestrian.setStayingState(null);
					}
					else if(!pedestrian.isLeader() && 
							leaderPosition.containsKey(pedestrian.getId()) &&
							leaderPosition.get(pedestrian.getId()) != null &&
							this.query.isCollisionWithObstacles(pedestrian.getBodyRadius(),
									safetyDistance, 
									pedestrian.getStayingState().getStayingPosition(),
									scenarioManager.getObstacles())) {
							
							pedestrian.setStayingState(null);
							leaderPosition.put(pedestrian.getId(), null);
					}
					else if(!pedestrian.isLeader() && 
							leaderPosition.containsKey(pedestrian.getId()) &&
							leaderPosition.get(pedestrian.getId()) != null &&
							this.query.isCollisionWithObstacles(pedestrian.getBodyRadius(),
									safetyDistance, 
									pedestrian.getStayingState().getStayingPosition(),
									scenarioManager.getObstacles())) {
							
							pedestrian.setStayingState(null);
							leaderPosition.put(pedestrian.getId(), null);
					}
				}
				
				if(pedestrian.getStayingState() == null) {
					
					if(pedestrian.isLeader()) {
						
						pedestrian.setStayingState(this.findPositionForLeader(pedestrian));
					}
					else {
						
						IPedestrian leader = this.query.findLeaders(pedestrian, 
							this.perception,
							simulationState)
							.get(0);
	
						if(leaderPosition.containsKey(pedestrian.getId())) {
							
							pedestrian.setStayingState(this.findPositionForGroupMember(pedestrian, simulationState));
							leaderPosition.put(pedestrian.getId(), leader.getNextWalkingTarget());
						}
						else {
							
							Vector2D position = leader.getPosition().sum(
									pedestrian.getPosition()
										.subtract(leader.getPosition())
										.getNormalized()
										.multiply(participateDistance * 0.5));
									
							pedestrian.setStayingState(new StayingState(position,
								leader.getPosition().subtract(pedestrian.getPosition()).getNormalized(),
								pedestrian.getLastWalkingTarget()));
						}
	
					}
				}
				
				if(pedestrian.isLeader() &&
				   pedestrian.getStayingState() != null &&
				   pedestrian.getMotoricTask() == Motoric.Standing &&
				   !leaderPosition.containsKey(pedestrian.getId())) {
				
					leaderPosition.put(pedestrian.getId(), pedestrian.getNextWalkingTarget());
					
					List<IPedestrian> groupMembers = this.query.groupMembers(pedestrian, perception, simulationState);
					groupMembers.forEach(member -> {
						
						if(member.getId() != pedestrian.getId()) {
							
							leaderPosition.put(member.getId(), null);
						}
					});
				}
			}
		}
	}
	
	private StayingState findPositionForLeader(ITacticalPedestrian pedestrian) {
		
		Vector2D participationPosition = null;
		Vector2D participationHeading = null;
		
		Area targetArea = pedestrian.getNextNavigationTarget();
		
		List<IPedestrian> sameTargetPedestrians = this.query.findPedestrianSameTarget(pedestrian,
				this.perception, 
				targetArea,
				false,
				0.0);
		
		participationPosition = this.findSinglePosition(targetArea, pedestrian, sameTargetPedestrians);
		participationHeading = this.computeHeading(targetArea, participationPosition);
		
		return new StayingState(participationPosition,
				participationHeading,
				pedestrian.getLastWalkingTarget());
	}
	
	private StayingState findPositionForGroupMember(ITacticalPedestrian pedestrian,
			SimulationState simulationState) {
	
		StayingState participatingState = null;
		Vector2D participationPosition = null;
		Vector2D participationHeading = null;
		
		List<IPedestrian> groupMembers = this.query.findLeaders(pedestrian, 
				this.perception,
				simulationState);
			
		Area targetArea = pedestrian.getNextNavigationTarget();
		
		List<IPedestrian> sameTargetPedestrians = this.query.findPedestrianSameTarget(pedestrian,
				this.perception, 
				targetArea,
				true,
				0.0);
		
		Vector2D jointParticipationPosition = groupMembers.get(0).getNextWalkingTarget();
		
		participationPosition = this.findJointPosition(jointParticipationPosition,
				gatherGroupRadius, 
				targetArea, 
				pedestrian,
				sameTargetPedestrians);
		
		participationHeading = this.computeHeading(targetArea, participationPosition);
		
		participatingState = new StayingState(participationPosition,
				participationHeading,
				pedestrian.getLastWalkingTarget());
		
		return participatingState;
	}
	
	private Vector2D computeHeading(Area targetArea, Vector2D participationPosition) {
		
		Vector2D attractor = null;
		
		if(targetArea.getGatheringSegment() == null) {
			
			attractor = targetArea.getPointOfInterest();
		}
		else {
			
			attractor = targetArea.getGatheringSegment().getPointOnSegmentClosestToVector(participationPosition);
		}
		
		return attractor.subtract(participationPosition).getNormalized();
	}
	
	private Vector2D findJointPosition(Vector2D jointParticipationPosition, 
			double gatheringGroupRadius,
			Area targetArea, 
			IPedestrian groupMember,
			List<IPedestrian> sameTargetPedestrians) {
	
		if(areasCrowdingTypes.get(targetArea.getId()).equals(CrowdingType.Center)) {
			
			return targetArea.getGeometry().getCenter();
		}
		
		Vector2D groupeParticipationPosition = null;
		double currentGatheringGroupRadius = gatheringGroupRadius;
		double bodyRadius = groupMember.getBodyRadius();
		int gambleIterator = numberOfGambles;
		
		while(gambleIterator > 0) {
			
			double xRandom = 2.0 * currentGatheringGroupRadius * PedestrianBehaviorModel.getRandom().nextDouble()
					- currentGatheringGroupRadius;
			double yRandom = 2.0 * currentGatheringGroupRadius * PedestrianBehaviorModel.getRandom().nextDouble() 
					- currentGatheringGroupRadius;
			
			groupeParticipationPosition = jointParticipationPosition.sum(xRandom, yRandom);
			
			if(!targetArea.getGeometry().contains(groupeParticipationPosition) ||
			   this.query.isCollisionWithObstacles(bodyRadius, safetyDistance, groupeParticipationPosition, scenarioManager.getObstacles()) ||
			   this.query.isToCloseToAreaBorder(bodyRadius, safetyDistance, groupeParticipationPosition, targetArea) ||
			   this.query.isCollisionWithPedestrian(bodyRadius, 0.0, groupeParticipationPosition, sameTargetPedestrians)) {
						
					gambleIterator--;
					groupeParticipationPosition = null;
					currentGatheringGroupRadius += 0.1;	
			}
			else {
				
				break;
			}
		}
		
		if(groupeParticipationPosition == null) {
			
			groupeParticipationPosition = this.findSinglePosition(targetArea, groupMember, sameTargetPedestrians);
		}
		
	
		return groupeParticipationPosition;
	}
	
	private Vector2D findSinglePosition(Area targetArea, IPedestrian pedestrian, List<IPedestrian> sameTargetPedestrians) {
		
		if(areasCrowdingTypes.get(targetArea.getId()).equals(CrowdingType.Center)) {
			
			return targetArea.getGeometry().getCenter();
		}
		
		Vector2D singleParticipationPosition = null;
		double bodyRadius = pedestrian.getBodyRadius();
		
		if(areasCrowdingTypes.containsKey(targetArea.getId())) {
			
			if(areasCrowdingTypes.get(targetArea.getId()) != CrowdingType.Everywhere) {
	
				double currentDistance = 0.0;
				double gamble = 0.0;
				double change = 3.0;
				
				int gambleIterator = numberOfGambles;
				
				while(gambleIterator > 0) {
					
					singleParticipationPosition = GeometryAdditionals.findRandomPositionInPolygon(targetArea.getGeometry());
					
					gamble = PedestrianBehaviorModel.getRandom().nextDouble();
					currentDistance = this.calculateCurrentAttractorDistance(targetArea, singleParticipationPosition);
					
					if(areasCrowdingTypes.get(targetArea.getId()) == CrowdingType.Far &&
					   (currentDistance / this.minimalAttractionDistances.get(targetArea.getId()) < FastMath.pow(gamble, 1.0 / change))) {
						
						gambleIterator--;
					}
					else if (areasCrowdingTypes.get(targetArea.getId()) == CrowdingType.Close && 
							(currentDistance / this.maximalAttractionDistances.get(targetArea.getId()) > FastMath.pow(gamble, change))) {
						
						gambleIterator--;
					}
					else if(!targetArea.getGeometry().contains(singleParticipationPosition) ||
					   this.query.isCollisionWithObstacles(bodyRadius, safetyDistance, singleParticipationPosition, scenarioManager.getObstacles()) ||
					   this.query.isToCloseToAreaBorder(bodyRadius, safetyDistance, singleParticipationPosition, targetArea) ||
					   this.query.isCollisionWithPedestrian(bodyRadius, safetyDistance, singleParticipationPosition, sameTargetPedestrians)) {
						
						gambleIterator--;
					}
					else {
						
						break;
					}
				}	
			}
		}

		if(singleParticipationPosition == null) {
			
			int gambleIterator = numberOfGambles;
			while(gambleIterator > 0) {
				
				singleParticipationPosition = GeometryAdditionals.findRandomPositionInPolygon(targetArea.getGeometry());
				
				 if(targetArea.getGeometry().contains(singleParticipationPosition) ||
				    !this.query.isCollisionWithObstacles(bodyRadius, safetyDistance, singleParticipationPosition, scenarioManager.getObstacles()) ||
				    !this.query.isToCloseToAreaBorder(bodyRadius, safetyDistance, singleParticipationPosition, targetArea) ||
					!this.query.isCollisionWithPedestrian(bodyRadius, safetyDistance, singleParticipationPosition, sameTargetPedestrians)) {
							
					break;
				}
				gambleIterator--;
			}
			
			if(singleParticipationPosition == null) {
				
				singleParticipationPosition = GeometryAdditionals.findRandomPositionInPolygon(targetArea.getGeometry());
			}
		}	
		
		return  singleParticipationPosition;
	}
}
