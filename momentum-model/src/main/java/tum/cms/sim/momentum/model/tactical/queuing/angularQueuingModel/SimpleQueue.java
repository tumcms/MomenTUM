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

import java.util.HashMap;
import java.util.HashSet;

import tum.cms.sim.momentum.data.layout.area.Area;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.utility.geometry.Vector2D;

public class SimpleQueue {

	private static int groupMovedThisCycle = 2; 
	private static int groupMovedLastCycle = 1; 
	private static int removeGroupMoved = 0;
	
	/**
	 * Pedestrian id as end of queue tail
	 */
	private Integer queueTail = null;

	/**
	 * group Id -> pedestrian Id of the first in the group
	 */
	private HashMap<Integer, Integer> groupToPedestrian = new HashMap<Integer, Integer>();
	
	/**
	 * pedestrian Id -> group Id of the first in the group
	 */
	private HashMap<Integer, Integer> pedestrianToGroup = new HashMap<Integer, Integer>();
	
	/**
	 * group Id -> pedestrian of group non leader Ids
	 */
	private HashMap<Integer, HashSet<Integer>> groupMembersNoneLeader = new HashMap<Integer,  HashSet<Integer>>();
	
	/**
	 * pedestrian id -> left the queue, for previous pedestrian to check if have to move
	 */
	private HashSet<Integer> nextRemovedPedestrianTemporary = new HashSet<Integer>();
	
	/**
	 * pedestrian id -> next pedestrian id
	 */
	private HashMap<Integer, Integer> pedestriansNextPedestrianId = new HashMap<Integer, Integer>();
	
	/**
	 * pedestrian id -> previous pedestrian id
	 */
	private HashMap<Integer, Integer> pedestrianPreviousPedestrianId = new HashMap<>();
	
	/**
	 * pedestrian id -> position of the waiting position
	 */
	private HashMap<Integer, Vector2D> pedestriansPosition = new HashMap<Integer, Vector2D>();

	/** 
	 * Stores if a pedestrian is in a the queue
	 */
	private HashSet<Integer> pedestrianInQueue = new HashSet<Integer>();
	
	/**
	 * Stores if the group moved, thus the group members will follow in the next cycle
	 */
	private HashMap<Integer, Integer> groupMoved = new HashMap<Integer, Integer>();
	
	/**
	 * Stores the time lag until the pedestrian can move to the next position
	 */
	private HashMap<Integer, Double> moveLag = new HashMap<Integer, Double>();
	
	private double queuingLag = 0.5;

	public void updateGroupMove(int groupId) {
		
		Integer moved = groupMoved.get(groupId);
		
		if(moved != null) {
			
			moved--;
			
			if(moved.intValue() == removeGroupMoved)  {
				
				groupMoved.remove(groupId);
			}
			else {
				
				groupMoved.put(groupId, moved.intValue());	
			}
		}
	}
	
	public boolean didGroupMoved(int groupId) {

		if(groupMoved.containsKey(groupId)) {
			return groupMoved.get(groupId).intValue() == groupMovedLastCycle;
		}
		
		return false;
	}

	public int getFirstOfGroup(int groupId) {
		
		return groupToPedestrian.get(groupId);
	}

	public boolean isFirstOfGroup(int pededstrianId, int groupId) {
		
		return groupToPedestrian.get(groupId) == null ||
				groupToPedestrian.get(groupId).intValue() == pededstrianId;
	}
	
	public SimpleQueue(double precision, double queuingLag) {
		
		this.queuingLag = queuingLag;
	}

	public boolean isQueueHead(int pedestrianId) {
		
		return pedestriansNextPedestrianId.containsKey(pedestrianId) &&
				pedestriansNextPedestrianId.get(pedestrianId) == null;
	}

	public boolean isEmtpy() {
	
		return pedestrianInQueue.size() == 0;
	}

	public boolean isInQueue(int pedestrianId, int groupId) {
		
		return pedestrianInQueue.contains(pedestrianId) || 
				(groupMembersNoneLeader.containsKey(groupId) && groupMembersNoneLeader.get(groupId).contains(pedestrianId));
	}
	
	public boolean isGroupInQueue(int groupId) {
		
		return groupToPedestrian.containsKey(groupId);
	}

	public Vector2D getGroupsPosition(int groupId) {

		int firstPedestrianOfGroupId = groupToPedestrian.get(groupId);
		
		return pedestriansPosition.get(firstPedestrianOfGroupId);
	}
	
	public boolean isTailEqualsHead() {
		
		return this.queueTail != null && this.pedestriansNextPedestrianId.get(this.queueTail) == null;
	}

	public boolean isNextHead(Integer pedestrianId) {
		
		Integer nextId = this.pedestriansNextPedestrianId.get(pedestrianId);
		
		return nextId != null && this.pedestriansNextPedestrianId.get(nextId) == null;
	}
	
	public Vector2D getQueueEnd() {
		
		return this.pedestriansPosition.get(this.queueTail);
	}
	
	public boolean isPositionChanged(int pedestrianId, 
			int groupId, 
			SimulationState simulationState,
			Area targetArea) {

		//Vector2D queueHeadPosition = targetArea.getGeometry().getCenter();
		
		Integer nextPedestrianId = this.pedestriansNextPedestrianId.get(pedestrianId);

		// is queue head
		if(nextPedestrianId == null) { 
			return false;
		}
		
		// next pedestrian was head of the queue or  next pedestrian left the queue?
		if(!pedestrianInQueue.contains(nextPedestrianId.intValue()) ||
				this.nextRemovedPedestrianTemporary.contains(pedestrianId)) { // never?
			
			if(!moveLag.containsKey(groupId)) {
				
				moveLag.put(groupId, queuingLag);
			}
			else {
				
				moveLag.put(groupId, moveLag.get(groupId) - simulationState.getTimeStepDuration());
			}
			
//			if(!pedestrianInQueue.contains(nextPedestrianId)) {
//				
//				for(IPedestrian otherPedestrian : otherPedestrians) {
//					
//					if(targetArea.getGeometry().getCenter().distance(otherPedestrian.getPosition()) < otherPedestrian.getBodyRadius()) {
//						
//						return false;
//					}
//				}
//			}
			
			if(moveLag.get(groupId) <= 0.0) {
				
				if(this.nextRemovedPedestrianTemporary.contains(pedestrianId)) {
				
					this.nextRemovedPedestrianTemporary.remove(pedestrianId);
				}
				
				moveLag.remove(groupId);
				
				return true;
			}
		
			return false;
		}
		else { // move update from next found next
			
			int nextGroupId = this.pedestrianToGroup.get(nextPedestrianId).intValue();
			
			if(groupMoved.get(nextGroupId) != null || moveLag.get(groupId) != null) {	
				
				if(!moveLag.containsKey(groupId)) {
					
					moveLag.put(groupId, queuingLag);
				}
				else {
					
					moveLag.put(groupId, moveLag.get(groupId) - simulationState.getTimeStepDuration());
				}
		
				if(moveLag.get(groupId) <= 0.0) {

					moveLag.remove(groupId);
					
					return true;
				}
		
				return false;
			}

			return false;
		}	
	}
	
	public void addNewQueue(Vector2D queuingPosition, int pedestrianId, int groupId) {
		
		pedestrianInQueue.add(pedestrianId);
		pedestriansPosition.put(pedestrianId, queuingPosition);
		
		pedestriansNextPedestrianId.put(pedestrianId, null);
		pedestrianPreviousPedestrianId.put(pedestrianId, null);
		
		queueTail = pedestrianId;
		
		pedestrianToGroup.put(pedestrianId, groupId);
		groupToPedestrian.put(groupId, pedestrianId);
	}
	
	public void addToQueueEnd(Vector2D queueEnd, Vector2D queuingPosition, int pedestrianId, int groupId) {
		
		pedestrianInQueue.add(pedestrianId);
		pedestriansPosition.put(pedestrianId, queuingPosition);
		
		pedestriansNextPedestrianId.put(pedestrianId, queueTail);
		
		pedestrianPreviousPedestrianId.put(queueTail, pedestrianId);
		pedestrianPreviousPedestrianId.put(pedestrianId, null);
		
		queueTail = pedestrianId;
		
		if(!groupToPedestrian.containsKey(groupId)) {
			
			pedestrianToGroup.put(pedestrianId, groupId);
			groupToPedestrian.put(groupId, pedestrianId);
			//moveLag.put(groupId, this.queuingLag);
		}
	}

	public Vector2D getNextPosition(int pedestrianId) {
		
		Integer next = this.pedestriansNextPedestrianId.get(pedestrianId);
		
		Vector2D nextPosition = null;
		
		if(next != null) {
			
			nextPosition = this.pedestriansPosition.get(next.intValue());
		}
		
		return nextPosition;
	}
	
	public Vector2D getNextNextPosition(int pedestrianId) {
		
		Integer next = this.pedestriansNextPedestrianId.get(pedestrianId);
		
		Vector2D nextNextPosition = null;
		
		if(next != null) {
			
			next = this.pedestriansNextPedestrianId.get(next.intValue());
			
			if(next != null) {
					
				nextNextPosition = this.pedestriansPosition.get(next.intValue());
			}
		}
		
		return nextNextPosition;
	}

	public void updatePositionOnly(int pedestrianId, Vector2D queuingPosition) {
		
		pedestriansPosition.put(pedestrianId, queuingPosition);
	}
	
	/**
	 * returns the position of the next pedestrian if the current pedestrian should move.
	 * if null is returned the pedestrian is the new head of the queue.
	 */
	public Vector2D doMoveToNextPosition(int pedestrianId, int groupId) {
		
		int nextPedestrianId = this.pedestriansNextPedestrianId.get(pedestrianId).intValue();	
		Vector2D walkTowards = null;
		
		if(!pedestrianInQueue.contains(nextPedestrianId)) {
			
			pedestriansNextPedestrianId.put(pedestrianId, null);	
		}
		else {
			
			walkTowards = this.pedestriansPosition.get(nextPedestrianId);
		}
		
		groupMoved.put(groupId, groupMovedThisCycle);
		
		return walkTowards;
	}

	public void addGroupMemberToQueue(int groupId, int pedestrianId) {
	
		if(!this.groupMembersNoneLeader.containsKey(groupId)) {
			
			this.groupMembersNoneLeader.put(groupId, new HashSet<Integer>());
		}
		
		this.groupMembersNoneLeader.get(groupId).add(pedestrianId);
	}
	
	public void removeFromQueue(int pedestrianId, int groupId) {
		
		// might already be removed 
		if(pedestrianInQueue.contains(pedestrianId)) { 

			pedestrianInQueue.remove(pedestrianId);
			
			if(groupToPedestrian.get(groupId).intValue() == pedestrianId) {
				
				pedestrianToGroup.remove(pedestrianId);
				groupToPedestrian.remove(groupId);
				groupMoved.remove(groupId);
			}

			Integer nextPedestrianId = this.pedestriansNextPedestrianId.remove(pedestrianId);
			Integer previousPedestrianId = this.pedestrianPreviousPedestrianId.remove(pedestrianId);
			
			if(previousPedestrianId != null) { // this pedestrian is not tail
				
				if(nextPedestrianId != null) {

					this.pedestriansNextPedestrianId.put(previousPedestrianId, nextPedestrianId);
				}
			}
			else { // this pedestrian is tail
				
				queueTail = nextPedestrianId;
			}
			
			if(nextPedestrianId != null) { // this pedestrian not head
				
				if(previousPedestrianId != null) { // add to the previous one the information that the next has left
				
					this.nextRemovedPedestrianTemporary.add(previousPedestrianId);
				}
				
				this.pedestrianPreviousPedestrianId.put(nextPedestrianId, previousPedestrianId);
			}
		}
		else if(this.groupMembersNoneLeader.containsKey(groupId) &&
				this.groupMembersNoneLeader.get(groupId).contains(pedestrianId)) {
			
			this.groupMembersNoneLeader.get(groupId).remove(pedestrianId);
			
			if(this.groupMembersNoneLeader.isEmpty()) {
				
				this.groupMembersNoneLeader.remove(groupId);
			}
		}
	}
}
