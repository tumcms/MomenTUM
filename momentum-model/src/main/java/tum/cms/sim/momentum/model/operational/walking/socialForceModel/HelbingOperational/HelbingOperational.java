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

package tum.cms.sim.momentum.model.operational.walking.socialForceModel.HelbingOperational;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import tum.cms.sim.momentum.data.agent.pedestrian.state.operational.WalkingState;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IOperationalPedestrian;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrian;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrianExtension;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;
import tum.cms.sim.momentum.data.layout.obstacle.Obstacle;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.operational.walking.WalkingModel;
import tum.cms.sim.momentum.model.operational.walking.socialForceModel.SocialForce;
import tum.cms.sim.momentum.utility.geometry.Vector2D;

public class HelbingOperational extends WalkingModel {
	
	private SocialForce socialForce;
	
	@Override
	public void callPreProcessing(SimulationState simulationState) {
		
		socialForce = new SocialForce(this);
	}

	@Override
	public void callBeforeBehavior(SimulationState simulationState, Collection<IRichPedestrian> pedestrians) {
		// nothing to do
	}
	
	@Override
	public void callPostProcessing(SimulationState simulationState) {
		// nothing to do
	}
	
	@Override
	public void callPedestrianBehavior(IOperationalPedestrian pedestrian, SimulationState simulationState) {
			
		List<IPedestrian> otherPedestrians =  perception.getAllPedestrians(pedestrian); // perception.getNearestPedestrians(pedestrian, 3.0);
			
		List<Obstacle> obstacles = this.scenarioManager.getObstacles()
				.stream()
				.filter(obstacle -> obstacle.getGeometry().distanceBetween(pedestrian.getPosition()) < 5.0)
				.collect(Collectors.toList());
		
		Vector2D acceleration = socialForce.computeNewAcceleration(pedestrian, 
																	otherPedestrians,
																	obstacles);
		
		Vector2D deltaVelocity = acceleration.multiply(simulationState.getTimeStepDuration());
		Vector2D velocity = pedestrian.getVelocity().sum(deltaVelocity);

		if(velocity.getMagnitude() > pedestrian.getMaximalVelocity() ) {
		
			velocity = velocity.getNormalized()
					.multiply(pedestrian.getMaximalVelocity());
		}
		
		Vector2D deltaPosition = velocity.multiply(simulationState.getTimeStepDuration());
		Vector2D position = pedestrian.getPosition().sum(deltaPosition);
		
		Vector2D heading = this.computeHeading(pedestrian, pedestrian.getNextWalkingTarget());
		WalkingState novelState = new WalkingState(position, velocity, heading);
		
		pedestrian.setWalkingState(novelState);
	}	
	
	@Override
	public IPedestrianExtension onPedestrianGeneration(IRichPedestrian pedestrian) {
		
		return null;
	}
	
	@Override
	public void onPedestrianRemoval(IRichPedestrian pedestrian) {

		// nothing to do
	}

	//------------------------------------------------------------------------------------------------------------------
	
	private Vector2D computeHeading(IOperationalPedestrian me, Vector2D target) {
	
		return target.subtract(me.getPosition()).getNormalized();
	}
	
	
	@Override
	public void callAfterBehavior(SimulationState simulationState, Collection<IRichPedestrian> pedestrians) {
		
		// nothing to do, no overall pedestrian behavior
	}
}
