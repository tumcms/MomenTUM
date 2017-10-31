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

package tum.cms.sim.momentum.data.agent.pedestrian.types;

import tum.cms.sim.momentum.data.agent.pedestrian.IExtendsPedestrian;
import tum.cms.sim.momentum.data.agent.pedestrian.state.other.MessageState;
import tum.cms.sim.momentum.data.agent.pedestrian.state.other.MetaState;
import tum.cms.sim.momentum.data.agent.pedestrian.state.tactical.TacticalState.Behavior;
import tum.cms.sim.momentum.data.agent.pedestrian.state.tactical.TacticalState.Motoric;
import tum.cms.sim.momentum.data.layout.area.Area;
import tum.cms.sim.momentum.utility.generic.IUnique;
import tum.cms.sim.momentum.utility.geometry.Vector2D;
import tum.cms.sim.momentum.utility.graph.Vertex;

public interface IPedestrian extends IUnique {
	
	public MetaState getMetaState();

	public boolean isLeader();
	
	public double getBodyRadius();
	
	public double getMass();
	
	public int getGroupId();
	
	public int getGroupSize();
	
	public int getStartLocationId();
	
	public int getSeedId();
	
	
	public double getDesiredVelocity();
	
	public double getMaximalVelocity();

	public Behavior getBehavior();
	
	public Vector2D getPosition();
	
	public Vector2D getHeading();
	
	public Vector2D getVelocity();
	
	
	public Motoric getMotoricTask();
	
	public Vector2D getNextHeading();
	
	public Vertex getLastWalkingTarget();
	
	public Vector2D getNextWalkingTarget();
	
	
	public Behavior getBehaviorTask();

	public Area getNextNavigationTarget();
	
	public IPedestrianExtension getExtensionState(IExtendsPedestrian modelReference);

	public MessageState getMessageState();

}
