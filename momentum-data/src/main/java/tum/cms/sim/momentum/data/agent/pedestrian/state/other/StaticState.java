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

package tum.cms.sim.momentum.data.agent.pedestrian.state.other;

import tum.cms.sim.momentum.utility.generic.Unique;

public class StaticState extends Unique {

	private double maximalVelocity = Double.NEGATIVE_INFINITY;
	
	public double getMaximalVelocity() {
		return maximalVelocity;
	}
	
	private double desiredlVelocity = Double.NEGATIVE_INFINITY;

	public double getDesiredVelocity() {
		return desiredlVelocity;
	}

	private double bodyRadius = 0.0;

	public double getBodyRadius(){
		return this.bodyRadius;
	}
	
	private int currentScenarioId = 0;
	
	public int getCurrentScenarioId() {
		return currentScenarioId;
	}

	public void setCurrentScenarioId(int currentScenarioId) {
		this.currentScenarioId = currentScenarioId;
	}

	private int groupId = 0;
	
	public int getGroupId() {
		return this.groupId;
	}
	
	public void setGroupId(int id) {
		this.groupId = id;
	}
	
	private int groupSize = 0;
	
	public int getGroupSize() {
		return groupSize;
	}

	public void setGroupSize(int groupSize) {
		this.groupSize = groupSize;
	}

	private boolean isLeader = true;
	
	public boolean isLeader() {
		return isLeader;
	}

	public void setLeader(boolean isLeader) {
		this.isLeader = isLeader;
	}

	private int startLocationId = -1;

	public int getStartLocationId() {
		return startLocationId;
	}	
	
	private int seedId = -1;
	
	public int getSeedId() {
		return seedId;
	}
	
	private double mass = 0.0;
	
	public double getMass() {
		return mass;
	}

	public StaticState(double maximalVelocity, 
			double desiredlVelocity, 
			double bodyRadius, 
			double mass,
			int currentScenarioId,
			int startLocationId,
			int seedId,
			int groupId,
			int groupSize) {
		
		this.bodyRadius = bodyRadius;
		this.mass = mass;
		this.maximalVelocity = maximalVelocity;
		this.desiredlVelocity = desiredlVelocity;
		this.startLocationId = startLocationId;
		this.groupId = groupId;
		this.groupSize = groupSize;
		this.seedId = seedId;
	}
}
