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

package tum.cms.sim.momentum.model.generator.seed;

import tum.cms.sim.momentum.configuration.generator.PedestrianSeedConfiguration;
import tum.cms.sim.momentum.data.agent.pedestrian.PedestrianManager;
import tum.cms.sim.momentum.data.agent.pedestrian.state.other.StaticState;

import org.apache.commons.math3.util.FastMath;

public class NoDistributionPedestrianSeed extends PedestrianSeed {
 
	private static final String desiredVelocityString = "desiredVelocity";
	private static final String maximalVelocityString = "maximalVelocity";
	protected static final String groupSizeString = "groupSize";
	
	private double maximalVelocity = Double.NEGATIVE_INFINITY;
	private double desiredVelocity = Double.NEGATIVE_INFINITY;
	
	private int fixedGroupSize = Integer.MIN_VALUE;
	
	@Override
	public StaticState generateStaticState(int startLocationId, int currentScenarioId) {
		
		if(this.groupSizeForGenerator <= 0) {
			
			this.currentGroupId = PedestrianManager.getGroupNewId();	
			this.groupSizeForGenerator = this.fixedGroupSize;
		}
	
		Double radius = radiusMeter;
		double mass = 1.0;
		if(radius == null) {
			
			mass = 60.0 + 40.0 * FastMath.random();
			radius = mass / 320.0;
		}
		
		StaticState newStaticState = new StaticState(maximalVelocity, 
				desiredVelocity,
				radius,
				mass,
				currentScenarioId,
				startLocationId,
				this.getId(),
				this.currentGroupId,
				this.fixedGroupSize);
		
		newStaticState.setId(PedestrianManager.getPedestrianNewId());
		this.groupSizeForGenerator--;
		
		return newStaticState;
	}

	@Override
	public void loadConfiguration(PedestrianSeedConfiguration configuration) {
		
		if(properties.getDoubleProperty(radiusMeterString) != null) {
			
			radiusMeter = properties.getDoubleProperty(radiusMeterString);
		}
		
		maximalVelocity = properties.getDoubleProperty(maximalVelocityString);
		desiredVelocity = properties.getDoubleProperty(desiredVelocityString);
		fixedGroupSize = properties.getIntegerProperty(groupSizeString);
	}

	@Override
	public Double getMaximalRadius() {
		
		Double radius = radiusMeter;
		
		if(radius == null) {
			
			radius = 100.0 / 320.0;
		}
		
		return radius;
	}
}
