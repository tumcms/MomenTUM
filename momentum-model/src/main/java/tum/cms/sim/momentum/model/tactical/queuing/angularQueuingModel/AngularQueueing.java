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

package tum.cms.sim.momentum.model.tactical.queuing.angularQueuingModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.math3.util.FastMath;
import tum.cms.sim.momentum.configuration.model.lattice.LatticeModelConfiguration.LatticeType;
import tum.cms.sim.momentum.configuration.model.lattice.LatticeModelConfiguration.NeighbourhoodType;
import tum.cms.sim.momentum.data.agent.pedestrian.state.tactical.QueuingState;
import tum.cms.sim.momentum.data.agent.pedestrian.state.tactical.TacticalState.Behavior;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrian;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrianExtansion;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;
import tum.cms.sim.momentum.data.agent.pedestrian.types.ITacticalPedestrian;
import tum.cms.sim.momentum.data.layout.area.Area;
import tum.cms.sim.momentum.data.layout.area.OriginArea;
import tum.cms.sim.momentum.data.layout.obstacle.Obstacle;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.PedestrianBehaviorModel;
import tum.cms.sim.momentum.model.layout.lattice.LatticeModel;
import tum.cms.sim.momentum.model.tactical.queuing.QueuingModel;
import tum.cms.sim.momentum.utility.geometry.Geometry2D;
import tum.cms.sim.momentum.utility.geometry.GeometryFactory;
import tum.cms.sim.momentum.utility.geometry.Polygon2D;
import tum.cms.sim.momentum.utility.geometry.Vector2D;
import tum.cms.sim.momentum.utility.geometry.operation.GeometryAdditionals;
import tum.cms.sim.momentum.utility.lattice.CellIndex;
import tum.cms.sim.momentum.utility.lattice.ILattice;
import tum.cms.sim.momentum.utility.lattice.LatticeTheoryFactory;

public class AngularQueueing extends QueuingModel {

	private static String safetyDistanceName = "safetyDistance";
	private static String initalDistanceName = "initalDistance";
	private static String useIntialName = "useIntial";
	private static String closeToSelfName = "closeToSelf";
	private static String queueArcName = "queueArc";
	private static String queueDistanceName = "queueDistance";
	private static String proximityDistanceName = "proximityDistance";
	private static String queuingLagName = "queuingLag";
	
	protected double precision = 0.01;
	protected double safetyDistance = 0.1;	
	
	private ILattice queueLattice = null;
	
	protected HashSet<String> useInital = new HashSet<>();
	protected HashSet<String> closeToSelf = new HashSet<>();
	protected double queueArc = FastMath.PI / 2.0;
	protected double queueDistance = 1.0; 
	protected double proximityDistance = 2.0;
	protected double queuingLag = 0.5;
	protected Double initalDistance = 0.0;

	private HashMap<Area, SimpleQueue> queues = new HashMap<Area, SimpleQueue>();
	
	private synchronized SimpleQueue generateQueue(Area queueArea) {
		
		if(queueArea == null) {
			
			return null;
		}
		
		if(!this.queues.containsKey(queueArea)) {
		
			this.queues.put(queueArea, new SimpleQueue(this.precision, this.queuingLag));
		}
		
		return this.queues.get(queueArea);
	}
	
	@Override
	public IPedestrianExtansion onPedestrianGeneration(IRichPedestrian pedestrian) {
	
		return null; // Nothing to do
	}

	@Override
	public void onPedestrianRemoval(IRichPedestrian pedestrian) {

		// Nothing to do
	}

	@Override
	public void callPreProcessing(SimulationState simulationState) {

		this.queueArc = this.properties.getDoubleProperty(queueArcName);
		this.queueDistance = this.properties.getDoubleProperty(queueDistanceName);
		
		this.safetyDistance = this.properties.getDoubleProperty(safetyDistanceName);
		
		if(this.properties.getDoubleProperty(proximityDistanceName) == null) {
			
			this.proximityDistance = 2.0 * this.queueDistance;
		}
		else {
			
			this.proximityDistance = this.properties.getDoubleProperty(proximityDistanceName);
		}
		
		this.initalDistance = this.properties.getDoubleProperty(initalDistanceName);
		
		this.queuingLag = this.properties.getDoubleProperty(queuingLagName);
		
		ArrayList<String> intialUsageList = this.properties.<String>getListProperty(useIntialName);
		
		if(intialUsageList != null && intialUsageList.size() > 0) {
			
			intialUsageList.stream().forEach(category -> this.useInital.add(category));
		}
		
		ArrayList<String> closeToSelfList = this.properties.<String>getListProperty(closeToSelfName);
		
		if(closeToSelfList != null && closeToSelfList.size() > 0) {
				
			closeToSelfList.stream().forEach(category -> this.closeToSelf.add(category));
		}
	
		double latticeSize = this.scenarioManager.getLattices().get(0).getCellEdgeSize();
		this.queueLattice = LatticeTheoryFactory.createLattice("queueLattice",
				LatticeType.Quadratic, 
				NeighbourhoodType.Touching,
				latticeSize,
				this.scenarioManager.getScenarios().getMaxX(),
				this.scenarioManager.getScenarios().getMinX(),
				this.scenarioManager.getScenarios().getMaxY(),
				this.scenarioManager.getScenarios().getMinY());
		
		LatticeModel.fillLatticeForObstacles(this.queueLattice, this.scenarioManager.getScenarios());
		
		List<CellIndex> originCenterCells = this.scenarioManager.getOrigins()
				.stream()
				.map(OriginArea::getGeometry)
				.map(Geometry2D::getCenter)
				.map(center -> this.queueLattice.getCellIndexFromPosition(center))
				.collect(Collectors.toList());
		
		this.queueLattice.flood(originCenterCells);
	}
	
	@Override
	public void callPostProcessing(SimulationState simulationState) {

		// Nothing to do
	}	
	
	@Override
	public void callBeforeBehavior(SimulationState simulationState, Collection<IRichPedestrian> pedestrians) {
		
		pedestrians.forEach(pedestrian -> {
			
			Area targetArea = pedestrian.getNextNavigationTarget();
			
			queues.entrySet().forEach(areaToQueue -> {

				if(targetArea == null || areaToQueue.getKey().getId() != targetArea.getId() ||
				   !pedestrian.getStrategicalState().getTacticalBehavior().equals(Behavior.Queuing) ||
						   pedestrian.getQueuingState() == null) {
					
					areaToQueue.getValue().removeFromQueue(pedestrian.getId(), pedestrian.getGroupId());
				}
				
				if(areaToQueue.getValue().isGroupInQueue(pedestrian.getGroupId()) && 
						areaToQueue.getValue().getFirstOfGroup(pedestrian.getGroupId()) == pedestrian.getId()) {
					
					areaToQueue.getValue().updateGroupMove(pedestrian.getGroupId());
				}	
			});
		});
		
		queues.entrySet().forEach(areaToQueue -> 
			areaToQueue.getKey().updatePointOfInterest(areaToQueue.getValue().getQueueEnd()));
	}

	@Override
	public void callAfterBehavior(SimulationState simulationState, Collection<IRichPedestrian> pedestrians) {
		
	}

	@Override
	public void callPedestrianBehavior(ITacticalPedestrian pedestrian, SimulationState simulationState) {
				
		Area targetArea = pedestrian.getNextNavigationTarget();
		
		SimpleQueue queue = generateQueue(targetArea);

		QueuingState queuingState = null;
		
		synchronized (this) {
			
			if(!queue.isInQueue(pedestrian.getId(), pedestrian.getGroupId())) { // queue up 
				
				queuingState = this.addToQueue(pedestrian, queue, targetArea);
			}
			else { // update the position in the queue continuously

				queuingState = this.updateInQueue(pedestrian, queue, targetArea, simulationState);
			}
		
			pedestrian.setQueuingState(queuingState);
			targetArea.updatePointOfInterest(queue.getQueueEnd());
		}
	}
	
	private QueuingState addToQueue(ITacticalPedestrian pedestrian, SimpleQueue queue, Area targetArea) {
		
		QueuingState queuingState = null;

		if(queue.isEmtpy()) { // first of the group and first at queue
			
			Vector2D queuingPosition = targetArea.getGeometry().getCenter();
			Vector2D queuingHeading = this.computeDirectionHeading(targetArea,
					queue, 
					queuingPosition,
					pedestrian.getId());
			
			if(queuingHeading == null) {
				queuingHeading = pedestrian.getHeading();
			}
			
			if(pedestrian.getPosition().distance(queuingPosition) < proximityDistance &&
					this.perception.isVisible(pedestrian.getPosition(), targetArea.getPointOfInterest())) {
				
				queue.addNewQueue(queuingPosition, pedestrian.getId(), pedestrian.getGroupId());
			}

			queuingState = new QueuingState(queuingPosition, queuingHeading, pedestrian.getLastWalkingTarget());
		}
		else {
			
			// not empty queue and pedestrians group is not in queue
			if(!queue.isGroupInQueue(pedestrian.getGroupId())) { 
			
				Vector2D queueEnd = queue.getQueueEnd();
				Vector2D queuingHeading = this.computeDirectionHeading(targetArea,
						queue, 
						queueEnd,
						pedestrian.getId());
				
				if(queuingHeading == null) {
					queuingHeading = pedestrian.getHeading();
				}
				
				if(pedestrian.getPosition().distance(targetArea.getPointOfInterest()) < proximityDistance) {
						//this.perception.isVisible(pedestrian.getPosition(), targetArea.getPointOfInterest())) {
						
					queue.addToQueueEnd(queueEnd, 
							queueEnd, 
							pedestrian.getId(), 
							pedestrian.getGroupId());
					
					queuingHeading = this.computeDirectionHeading(targetArea,
							queue, 
							queueEnd,
							pedestrian.getId());
					
					if(queuingHeading == null) {
						queuingHeading = pedestrian.getHeading();
					}
					
					boolean isNextToHead = queue.isTailEqualsHead() && 
							targetArea.getCategories() != null && 
							targetArea.isInCategories(useInital);
					
					queueEnd = this.findQueueEndPosition(pedestrian,
							//QueueAdaptation.InitialQueueing, 
							targetArea,
							queuingHeading, 
							queueEnd,
							isNextToHead,
							targetArea.isInCategories(closeToSelf));
					
					queue.updatePositionOnly(pedestrian.getId(), queueEnd);
				}
				
				queuingState = new QueuingState(queueEnd, queuingHeading, pedestrian.getLastWalkingTarget());
			}
			// group is in queue and pedestrian not first of group
			else if(!queue.isFirstOfGroup(pedestrian.getId(), pedestrian.getGroupId())) { 
				
				Vector2D queueEnd = queue.getGroupsPosition(pedestrian.getGroupId());
				Vector2D groupHeading = this.computeDirectionHeading(targetArea,
						queue, 
						queueEnd,
						queue.getFirstOfGroup(pedestrian.getGroupId()));
				
				if(groupHeading == null) {
					groupHeading = pedestrian.getHeading();
				}
				
				if(pedestrian.getQueuingState() != null && pedestrian.getPosition().distance(queueEnd) < proximityDistance) {
						
					queue.addGroupMemberToQueue(pedestrian.getGroupId(), pedestrian.getId());
					
					// if the goal is a single location goal, do not follow the buddy if he is the head of the queue
					if(queue.isQueueHead(queue.getFirstOfGroup(pedestrian.getGroupId())) && 
							targetArea.isInCategories(useInital)) {
		
						queueEnd = groupHeading.rotate(FastMath.PI).scale(proximityDistance + this.initalDistance).sum(queueEnd);
					}
				}
				else { // keep some distance to the queuing Position so that the leader can place himself there

				
					queueEnd = groupHeading.rotate(FastMath.PI).scale(proximityDistance).sum(queueEnd);
				}

				queuingState = new QueuingState(queueEnd, groupHeading, pedestrian.getLastWalkingTarget());
			}
		}
		
		// buddies in queue
//		if(queue.isGroupInQueue(pedestrian.getGroupId()) && !queue.isFirstOfGroup(pedestrian.getId(), pedestrian.getGroupId())) { 
//			
//			Vector2D queueEnd = queue.getGroupsPosition(pedestrian.getGroupId());
//			Vector2D groupHeading = this.computeDirectionHeading(targetArea,
//					queue, 
//					queueEnd,
//					queue.getFirstOfGroup(pedestrian.getGroupId()));
//			
//			if(pedestrian.getQueuingState() != null && pedestrian.getPosition().distance(queueEnd) < proximityDistance) {
//				
//				queue.addGroupMemberToQueue(pedestrian.getGroupId(), pedestrian.getId());
//				
//				// if the goal is a single location goal, do not follow the buddy if he is the head of the queue
//				if(queue.isQueueHead(queue.getFirstOfGroup(pedestrian.getGroupId())) && 
//						this.useInital.contains(targetArea.getCategory())) {
//	
//					queueEnd = groupHeading.rotate(FastMath.PI).scale(proximityDistance + this.initalDistance).sum(queueEnd);
//				}
//			}
//			else { // keep some distance to the queuing Position so that the leader can place himself there
//
//				queueEnd = groupHeading.rotate(FastMath.PI).scale(proximityDistance).sum(queueEnd);
//			}
//
//			queuingState = new QueuingState(queueEnd, groupHeading, pedestrian.getLastWalkingTarget());
//		}
//		else if(queue.isEmtpy() && queue.isFirstOfGroup(pedestrian.getId(), pedestrian.getGroupId())) {	
//							
//			Vector2D queuingPosition = targetArea.getGeometry().getCenter();
//			Vector2D queuingHeading = this.computeDirectionHeading(targetArea,
//					queue, 
//					queuingPosition,
//					pedestrian.getId());
//			
//			if(pedestrian.getPosition().distance(queuingPosition) < proximityDistance &&
//					this.perception.isVisible(pedestrian.getPosition(), targetArea.getPointOfInterest())) {
//				
//				queue.addNewQueue(queuingPosition, pedestrian.getId(), pedestrian.getGroupId());
//			}
//			
//			queuingState = new QueuingState(queuingPosition, queuingHeading, pedestrian.getLastWalkingTarget());
//			
//		}
//		else if(queue.isFirstOfGroup(pedestrian.getId(), pedestrian.getGroupId())) { // queue up at a queue
//			
//			Vector2D queueEnd = queue.getQueueEnd();
//			Vector2D queuingHeading = this.computeDirectionHeading(targetArea,
//					queue, 
//					queueEnd,
//					pedestrian.getId());
//
//			if(pedestrian.getPosition().distance(targetArea.getPointOfInterest()) < proximityDistance) {
//					//this.perception.isVisible(pedestrian.getPosition(), targetArea.getPointOfInterest())) {
//					
//				queue.addToQueueEnd(queueEnd, 
//						queueEnd, 
//						pedestrian.getId(), 
//						pedestrian.getGroupId());
//				
//				queuingHeading = this.computeDirectionHeading(targetArea,
//						queue, 
//						queueEnd,
//						pedestrian.getId());
//				
//				boolean isNextToHead = queue.isTailEqualsHead() && 
//						targetArea.getCategory() != null && 
//						this.useInital.contains(targetArea.getCategory());
//				
//				queueEnd = this.findQueueEndPosition(pedestrian,
//						//QueueAdaptation.InitialQueueing, 
//						targetArea,
//						queuingHeading, 
//						queueEnd,
//						isNextToHead,
//						this.closeToSelf.contains(targetArea.getCategory()));
//				
//				queue.updatePositionOnly(pedestrian.getId(), queueEnd);
//			}
//		
//			queuingState = new QueuingState(queueEnd, queuingHeading, pedestrian.getLastWalkingTarget());
//		}
//		else if(!queue.isFirstOfGroup(pedestrian.getId(), pedestrian.getGroupId())) {
//			
//			Vector2D queuingPosition = queue.getQueueEnd();
//			Vector2D queuingHeading = pedestrian.getHeading();
//
//			// wait for leader to queue
//			if(queuingPosition == null || pedestrian.getPosition().distance(queuingPosition) < 2 * proximityDistance) {
//			
//				queuingPosition = pedestrian.getPosition();
//				queuingState = new QueuingState(queuingPosition, queuingHeading, pedestrian.getLastWalkingTarget());
//			}
//		}
		
		return queuingState;
	}

	private QueuingState updateInQueue(ITacticalPedestrian pedestrian, 
			SimpleQueue queue,
			Area targetArea, 
			SimulationState simulationState) {
		
		QueuingState queuingState = null;

		if(queue.isFirstOfGroup(pedestrian.getId(), pedestrian.getGroupId())) { // normal update
			
			boolean isPositionChanged = queue.isPositionChanged(pedestrian.getId(),
					pedestrian.getGroupId(),
					simulationState,
					targetArea);
			
			// Not the first pedestrian and next pedestrian moved
			if(isPositionChanged) { 
				
				Vector2D nextPosition = queue.doMoveToNextPosition(pedestrian.getId(), 
						pedestrian.getGroupId());
				
				if(!queue.isQueueHead(pedestrian.getId())) {
					
					Vector2D queuingNextHeading = this.computeDirectionHeading(targetArea,
							queue, 
							nextPosition,
							pedestrian.getId());
					
					if(queuingNextHeading == null) {
						queuingNextHeading = pedestrian.getHeading();
					}
					
					boolean isNextToHead = queue.isNextHead(pedestrian.getId()) && 
							targetArea.getCategories() != null && 
							targetArea.isInCategories(useInital);
					
					nextPosition = this.findQueueEndPosition(pedestrian, 
							targetArea,
							queuingNextHeading,
							nextPosition,
							isNextToHead,
							targetArea.isInCategories(closeToSelf));
				}
				else {
					
					nextPosition = targetArea.getGeometry().getCenter();
				}
				
				queue.updatePositionOnly(pedestrian.getId(),  nextPosition);
				
				Vector2D queuingHeading = this.computeDirectionHeading(targetArea,
						queue, 
						nextPosition,
						pedestrian.getId());
				
				if(queuingHeading == null) {
					queuingHeading = pedestrian.getHeading();
				}
				
				queuingState = new QueuingState(nextPosition, queuingHeading, pedestrian.getLastWalkingTarget());
			}
			else { // keep waiting

				queuingState = new QueuingState(pedestrian.getQueuingState());
			}
		}
		else if(queue.isGroupInQueue(pedestrian.getGroupId())){ // update as follower buddy if leader is still in queue

			// does the first of the group moved?
			if(queue.didGroupMoved(pedestrian.getGroupId())) {
				
				Vector2D nextPosition = queue.getGroupsPosition(pedestrian.getGroupId());
				Vector2D nextHeading = this.computeDirectionHeading(targetArea,
						queue, 
						nextPosition,
						queue.getFirstOfGroup(pedestrian.getGroupId()));
				
				Vector2D queuingPosition = null;
				
				if(nextHeading == null) {
					nextHeading = pedestrian.getHeading();
				}
				// if the goal is a single location goal, do not follow the buddy if he is the head of the queue
				if(queue.isQueueHead(queue.getFirstOfGroup(pedestrian.getGroupId())) && 
						targetArea.isInCategories(useInital)) {		

					queuingPosition = this.findQueueEndPosition(pedestrian, 
							targetArea,
							nextHeading,
							nextPosition,
							true,
							targetArea.isInCategories(closeToSelf));
				}
				else { // follow the buddy
					
					queuingPosition = nextPosition;//this.findQueueEndPosition(pedestrian, nextHeading, nextPosition, false);
				}
						
				queuingState = new QueuingState(queuingPosition, nextHeading, pedestrian.getLastWalkingTarget());
			}
			else { // keep waiting
				
				if(pedestrian.getQueuingState() != null &&
				   pedestrian.getQueuingState().getQueuingPosition().distance(
						   queue.getGroupsPosition(pedestrian.getGroupId())) >= proximityDistance) {
					
					Vector2D nextPosition = queue.getGroupsPosition(pedestrian.getGroupId());
					Vector2D nextHeading = this.computeDirectionHeading(targetArea,
							queue, 
							nextPosition,
							queue.getFirstOfGroup(pedestrian.getGroupId()));
					
					// follow the buddy
					Vector2D queuingPosition = nextPosition;//this.findQueueEndPosition(pedestrian, nextHeading, nextPosition, false);
					queuingState = new QueuingState(queuingPosition, 
								nextHeading, 
								pedestrian.getLastWalkingTarget());

				}
				else {
					
					if(pedestrian.getQueuingState() != null) {
						
						queuingState = new QueuingState(pedestrian.getQueuingState());
					}
				}
			
			}
		} 
		else { // leader left queue
			
			queue.removeFromQueue(pedestrian.getId(), pedestrian.getGroupId());
		}

//		if(targetArea.getGeometry().contains(queuingState.getQueuingPosition()) &&
//				!queue.isQueueHead(pedestrian.getId())) {
//			
//			int i = 0;
//		}
//		
		return queuingState;
	}

	private Vector2D computeDirectionHeading(Area targetArea,
			SimpleQueue queue,
			Vector2D queuingPosition,
			int pedestrianId) {
		
		Vector2D lookAt = queue.getNextNextPosition(pedestrianId);
		Vector2D lookFrom = queuingPosition;
		Vector2D newHeading = null;
		
//		try {
//
//			lookAt = targetArea.getGatheringSegment().getPointOnSegmentClosestToVector(queuingPosition);
//
//		newHeading = lookAt.subtract(lookFrom).getNormalized();
//		}
//		catch(Exception ex) {
//			
//			ex = null;
//		}
		try {
				
			if(lookAt == null || lookAt.subtract(lookFrom).getNormalized().equals(GeometryFactory.createVector(0.0, 0.0))) {
				
				lookAt = queue.getNextPosition(pedestrianId);
	
				if(lookAt == null || 
						lookAt.subtract(lookFrom).getNormalized().equals(GeometryFactory.createVector(0.0, 0.0))) {
				
					lookAt = targetArea.getGatheringSegment().getPointOnSegmentClosestToVector(queuingPosition);
				}
			}

			newHeading = lookAt.subtract(lookFrom).getNormalized();
		}
		catch(Exception ex) {
			
			ex = null;
		}
		
		return newHeading;
	}
	
//	private Vector2D findBuddyQueueEnd(ITacticalPedestrian pedestrian,
//			Vector2D leadersQueueingPosition) {
//		
//		return leadersQueueingPosition;
//	}
		
//		return leadersQueueingPosition;
//		Vector2D buddiesQueueEnd = null;
//		
//		double radius = pedestrian.getBodyRadius();
//		
//		List<IPedestrian> otherPedestrians = this.perception.getAllPedestrians(pedestrian);
//		List<Geometry2D> obstacleGeometires = this.scenarioManager.getObstacles()
//				.stream()
//				.map(Obstacle::getGeometry)
//				.collect(Collectors.toList());
//		
//		List<Vector2D> closeToBuddiesPosition = this.queueLattice.getAllNeighborIndices(leadersQueueingPosition)
//			.stream()
//			.filter(cell -> this.queueLattice.isCellFree(cell))
//			.map(cell -> this.queueLattice.getCenterPositionFromCellIndex(cell))
//			.collect(Collectors.toList());
//
//		int extensions = 3;
//		
//		if(closeToBuddiesPosition.size() > 0) {
//			
//			while(extensions > 0) {
//				
//				List<Vector2D> freePosition = new ArrayList<>();
//				
//				CloseToPositionComparer comparer = new CloseToPositionComparer();
//				comparer.setPosition(leadersQueueingPosition);
//				//comparer.setNextPosition(pedestrian.getPosition());
//				closeToBuddiesPosition.sort(comparer);
//				
//				freePosition.addAll(closeToBuddiesPosition);
//				
//				while(freePosition.size() > 0) {
//					
//					buddiesQueueEnd = freePosition.remove(0);
//				
//					if(!this.query.isCollisionWithPedestrian(radius, safetyDistance, buddiesQueueEnd, otherPedestrians) &&
//						this.perception.isVisible(buddiesQueueEnd, leadersQueueingPosition) &&
//						this.perception.isVisible(buddiesQueueEnd, pedestrian.getPosition())) {
//						
//						boolean obstacleBreak = false;
//						
//						for(Geometry2D obstacleGeomety : obstacleGeometires) {
//	
//							if(obstacleGeomety.distanceBetween(buddiesQueueEnd) < safetyDistance) {
//	
//								obstacleBreak = true;
//								break;
//							}
//						}
//						
//						if(!obstacleBreak) {
//							
//							break;
//						}
//						else {
//							
//							buddiesQueueEnd = null;
//						}
//					}
//					else {
//						
//						buddiesQueueEnd = null;
//					}
//				}
//				
//				if(buddiesQueueEnd == null) {
//					
//					extensions--;
//					List<Vector2D> extendedPositions = new ArrayList<>();
//					
//					closeToBuddiesPosition.forEach(closePosition -> 
//						
//						extendedPositions.addAll(this.queueLattice.getAllNeighborIndices(closePosition)
//							.stream()
//							.filter(cell -> this.queueLattice.isCellFree(cell))
//							.map(cell -> this.queueLattice.getCenterPositionFromCellIndex(cell))
//							.collect(Collectors.toList()))
//							);
//					closeToBuddiesPosition.clear();
//					closeToBuddiesPosition.addAll(extendedPositions);
//				}
//				else {
//					break;
//				}
//			}
//		}
//		
//		if(buddiesQueueEnd == null) {
//		
//			buddiesQueueEnd = leadersQueueingPosition;
//		}
//		
//		return buddiesQueueEnd;
//	}
	
	private Vector2D findQueueEndPosition(ITacticalPedestrian pedestrian,
			Area target,
			Vector2D endHeading,
			Vector2D queueEnd,
			boolean useInitialDistance,
			boolean closeToSelf) {
		
		Vector2D newQueueEnd = null;

		List<IPedestrian> otherPedestrians = this.perception.findPedestrianSameTarget(pedestrian, target, true, null);
		
		List<Geometry2D> obstacleGeometires = this.scenarioManager.getObstacles()
				.stream()
				.map(Obstacle::getGeometry)
				.collect(Collectors.toList());
		
		double radius = pedestrian.getBodyRadius();
		double arc = 0.0;
		double distance = queueDistance;
		
		if(useInitialDistance) {
			
			distance += this.initalDistance;
		}
		
		Double rotationChange = null;
		boolean validPosition = false;
		int rotationNumber = 360 / (int)queueArc;
		double rand = 0.0;
		double spaceMode = 1.0;
		int numberOfZeroSpace = 0;
		
		int checks = 3;
		
		while(newQueueEnd == null && checks > 0) {
			
			while(!validPosition && rotationNumber > 0) {
				
				rand = 0.25 * PedestrianBehaviorModel.getRandom().nextDouble();
				distance = queueDistance * spaceMode;
				Vector2D alphaCorner = null;
	
				alphaCorner = queueEnd.sum(
						endHeading.scale(safetyDistance + rand).rotate(GeometryAdditionals.translateToRadiant(180.0 + arc))
						);


				Vector2D betaCorner = queueEnd.sum( // right hand sight corner
						endHeading.scale(distance + radius * 2 + rand).rotate(GeometryAdditionals.translateToRadiant((-180.0 + (queueArc + arc))))
						   );
				
				Vector2D gammaCorner = queueEnd.sum( // left hand sight corner
						endHeading.scale(distance + radius * 2 + rand).rotate(GeometryAdditionals.translateToRadiant((180.0 - (queueArc - arc))))
						   );
				
				List<Vector2D> corners = new ArrayList<>();
				corners.add(alphaCorner);
				corners.add(gammaCorner);
				corners.add(betaCorner);
				
				if(!GeometryAdditionals.polygonHasCounterClockwiseWielding(corners)) {
					
					corners = GeometryAdditionals.switchOrderOfVertices(corners);
				}
				
				Polygon2D queueArea = GeometryFactory.createPolygon(corners);
				
				List<Vector2D> freeCells = this.queueLattice.getAllPolygonCells(queueArea)
						.stream()
						.filter(cell -> this.queueLattice.isCellFree(cell))
						.map(cell -> this.queueLattice.getCenterPosition(cell))
						.collect(Collectors.toList());
				
				if(freeCells.size() > 0) {
					
					CloseToPositionComparer comparer = new CloseToPositionComparer();
					
					if(closeToSelf) {

						comparer.setPosition(pedestrian.getPosition());
					}
					else {

						comparer.setPosition(queueEnd);
					}
					
					freeCells.sort(comparer);
					
					while(freeCells.size() > 0) {
						
						newQueueEnd = freeCells.remove(0);
					
						if(!this.perception.isCollisionWithPedestrian(pedestrian, radius, safetyDistance, newQueueEnd, otherPedestrians) &&
							this.perception.isVisible(newQueueEnd, queueEnd) &&
							this.perception.isVisible(newQueueEnd, pedestrian.getPosition())) {
							
							boolean obstacleBreak = false;
							
							for(Geometry2D obstacleGeomety : obstacleGeometires) {

								if(obstacleGeomety.distanceBetween(newQueueEnd) < safetyDistance) {

									obstacleBreak = true;
									break;
								}
							}
							
							if(!obstacleBreak) {
								
								validPosition = true;
								break;	
							}
							else {
								
								newQueueEnd = null;
							}
						}
						else {
							
							newQueueEnd = null;
						}
					}
				}

				if(newQueueEnd == null) {
					
					numberOfZeroSpace++;
				}
				
				if(!validPosition) {
					
					if(rotationChange == null) {
						
						if(betaCorner.distance(pedestrian.getPosition()) < 
						   gammaCorner.distance(pedestrian.getPosition())) {
							
							rotationChange = 1.0;
						}
						else {
							
							rotationChange = -1.0;
						}
					}
					
					arc += rotationChange * (int)queueArc;
					rotationNumber--;
				}
			}
			
			if(newQueueEnd == null) {
				
				checks--;
				newQueueEnd = null;
				arc = 0.0;
				rotationNumber = 360 / (int)queueArc;
				
				if(rotationNumber - 1 <= numberOfZeroSpace) {
				
					spaceMode += 1.0;
				}
				numberOfZeroSpace = 0;
			}
		}
		
		if(newQueueEnd == null) {
			newQueueEnd = pedestrian.getPosition().sum(
					pedestrian.getPosition().subtract(queueEnd).scale(queueDistance));
//			newQueueEnd = queueEnd;
		}
		
		return newQueueEnd;
	}
	
	private class CloseToPositionComparer implements Comparator<Vector2D> {

		private Vector2D position = null;
		
		public void setPosition(Vector2D position) {
			this.position = position;
		}

		@Override
		public int compare(Vector2D first, Vector2D second) {

			double distanceToFirst = position.distance(first); 
			double distanceToSecond = position.distance(second);
			
			if(distanceToFirst < distanceToSecond)  {
			
				return -1;
			}
			else if(distanceToFirst > distanceToSecond) {
				
				return 1;
			}
			
			return 0;
		}
	}
}
