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

package tum.cms.sim.momentum.model.operational.walking.socialForceModel.ParallelHelbingOperational;

import tum.cms.sim.momentum.data.agent.pedestrian.IExtendsPedestrian;
import tum.cms.sim.momentum.data.agent.pedestrian.state.operational.StandingState;
import tum.cms.sim.momentum.data.agent.pedestrian.state.operational.WalkingState;
import tum.cms.sim.momentum.data.agent.pedestrian.state.other.MessageState;
import tum.cms.sim.momentum.data.agent.pedestrian.state.other.MetaState;
import tum.cms.sim.momentum.data.agent.pedestrian.state.tactical.TacticalState.Behavior;
import tum.cms.sim.momentum.data.agent.pedestrian.state.tactical.TacticalState.Motoric;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IOperationalPedestrian;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrianExtension;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;
import tum.cms.sim.momentum.data.layout.area.Area;
import tum.cms.sim.momentum.utility.geometry.GeometryFactory;
import tum.cms.sim.momentum.utility.geometry.Vector2D;
import tum.cms.sim.momentum.utility.graph.Vertex;
import tum.cms.sim.momentum.utility.spaceTree.IQuadTreeBody;

public class NodeData implements IOperationalPedestrian, IQuadTreeBody {

    private double bodyRadius;
    private double mass;
    private Vector2D centerOfMass = GeometryFactory.createVector(0, 0);
    private Vector2D velocity = GeometryFactory.createVector(0, 0);
    private Vector2D heading = GeometryFactory.createVector(0, 0);
    private Motoric motoricTask;
    
    public static NodeData createFromPedestrian(IRichPedestrian pedestrian) {
    	NodeData body = new NodeData();
        
    	body.setBodyRadius(pedestrian.getBodyRadius());
        body.setMass(pedestrian.getMass());
        body.setCenterOfMass(pedestrian.getPosition().copy());
    	body.setVelocity(pedestrian.getVelocity());
        body.setHeading(pedestrian.getHeading().copy());
        body.setMotoricTask(pedestrian.getMotoricTask());
        
        return body;
    }

    public NodeData() {
    }

    public void setBodyRadius(double value) {
        this.bodyRadius = value;
    }

    public void setMotoricTask(Motoric motoricTask) {
        this.motoricTask = motoricTask;
    }

    public void setVelocity(Vector2D velocity) {
        this.velocity = velocity;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public Vector2D getCenterOfMass()
    {
        return centerOfMass;
    }

    public void setCenterOfMass(Vector2D value)
    {
        this.centerOfMass = value;
    }

	public void setHeading(Vector2D heading) {
		this.heading = heading;		
	} 

    @Override
    public Vector2D getPosition()
    {
        return centerOfMass;
    }

    @Override
    public double getBodyRadius() {
        return bodyRadius;
    }

    @Override
    public Motoric getMotoricTask() {
        return motoricTask;
    }

    @Override
    public Vector2D getVelocity() {
        return velocity;
    }

    @Override
    public double getMass() {
        return mass;
    }
	
	@Override
	public Vector2D getHeading() {
		return heading;
	}
	
	/*
	 * The following method are not needed
	 */	
	

	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setId(Integer id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public MetaState getMetaState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isLeader() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getGroupId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getGroupSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getStartLocationId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getSeedId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getDesiredVelocity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getMaximalVelocity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Behavior getBehavior() {
		// TODO Auto-generated method stub
		return null;
	}
	

	@Override
	public Vector2D getNextHeading() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vertex getLastWalkingTarget() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vector2D getNextWalkingTarget() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Behavior getBehaviorTask() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Area getNextNavigationTarget() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPedestrianExtension getExtensionState(IExtendsPedestrian modelReference) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MessageState getMessageState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WalkingState getWalkingState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StandingState getStandingState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setWalkingState(WalkingState state) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setStandingState(StandingState state) {
		// TODO Auto-generated method stub
		
	}
}
