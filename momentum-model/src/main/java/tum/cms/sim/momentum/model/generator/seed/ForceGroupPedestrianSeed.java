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

import java.util.ArrayList;

import tum.cms.sim.momentum.configuration.generator.PedestrianSeedConfiguration;
import tum.cms.sim.momentum.data.agent.pedestrian.PedestrianManager;
import tum.cms.sim.momentum.data.agent.pedestrian.state.other.StaticState;


public class ForceGroupPedestrianSeed extends PedestrianSeed {

	private static final String desiredVelocityString = "desiredVelocity";
	private static final String maximalVelocityString = "maximalVelocity";
	private static final String groupSizeName = "groupSize";
	
	private double maximalVelocity = Double.NEGATIVE_INFINITY;
	private double desiredVelocity = Double.NEGATIVE_INFINITY;
	
	private ArrayList<Integer> groupSizeList = new ArrayList<Integer>();
	private int groupIter = 0;
	private int groupSize = 1;
	
	@Override
	public void loadConfiguration(PedestrianSeedConfiguration configuration) {
	
		radiusMeter = properties.getDoubleProperty(radiusMeterString);
		maximalVelocity = properties.getDoubleProperty(maximalVelocityString);
		desiredVelocity = properties.getDoubleProperty(desiredVelocityString);
		groupSizeList = properties.<Integer>getListProperty(groupSizeName);
	}

	@Override
	public StaticState generateStaticState(int startLocationId, int currentScenarioId) {
		
		if(this.groupSizeForGenerator <= 0) {
			
			this.currentGroupId = PedestrianManager.getGroupNewId();	
			this.groupSizeForGenerator = this.groupSizeList.get(groupIter++);
			this.groupSize = this.groupSizeForGenerator; 
		}
	
		StaticState newStaticState = new StaticState(maximalVelocity, 
				desiredVelocity,
				radiusMeter,
				1.0,
				currentScenarioId,
				this.getId(),
				startLocationId,
				this.currentGroupId,
				this.groupSize);
		
		newStaticState.setId(PedestrianManager.getPedestrianNewId());
		this.groupSizeForGenerator--;
		
		return newStaticState;
	}

	@Override
	public Double getMaximalRadius() {

		return this.radiusMeter;
	}
	
}
