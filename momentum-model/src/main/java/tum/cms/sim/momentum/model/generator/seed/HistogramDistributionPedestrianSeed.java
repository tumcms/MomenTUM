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
import org.apache.commons.math3.util.FastMath;
import tum.cms.sim.momentum.configuration.generator.PedestrianSeedConfiguration;
import tum.cms.sim.momentum.data.agent.pedestrian.PedestrianManager;
import tum.cms.sim.momentum.data.agent.pedestrian.state.other.StaticState;
import tum.cms.sim.momentum.utility.probability.Histogram1DSet;

public class HistogramDistributionPedestrianSeed extends PedestrianSeed {

	private final static String velocityBinListName = "velocityBins";
	private final static String velocityBinWidthName = "velocityBinWidth";
	private final static String velocityBinWidthStartName = "minimalVelocity";

	private final static String groupSizeBinListName = "groupSizeBins";
	private final static String groupSizeBinWidthName = "groupSizeBinWidth";
	private final static String groupSizeBinWidthStartName = "minimalGroupSize";
	
	private Histogram1DSet velocityHistogram = null; 
	private Histogram1DSet groupHistogram = null;
	
	private int groupSize = 0;
	private double maximalVelocityGroup = 0;
	private double desiredVelocityGroup = 0;
	
	@Override
	public void loadConfiguration(PedestrianSeedConfiguration configuration) {
	
		if(properties.getDoubleProperty(radiusMeterString) != null) {
			
			radiusMeter = properties.getDoubleProperty(radiusMeterString);
		}
		
		ArrayList<Double> velocityBinList = properties.<Double>getListProperty(velocityBinListName);
		
		double velocityBinWidthStart = properties.getDoubleProperty(velocityBinWidthStartName);
		double velocityBinWidth = properties.getDoubleProperty(velocityBinWidthName);
		
		ArrayList<Double> groupBinList = properties.<Double>getListProperty(groupSizeBinListName);
		
		double groupSizeBinWidthStart = properties.getDoubleProperty(groupSizeBinWidthStartName);
		double groupSizeBinWidth = properties.getDoubleProperty(groupSizeBinWidthName);
		
		velocityHistogram = new Histogram1DSet(velocityBinWidthStart, velocityBinWidth);
		velocityBinList.forEach(bin -> velocityHistogram.appendBinValue(bin));	
		
		groupHistogram = new Histogram1DSet(groupSizeBinWidthStart, groupSizeBinWidth);

		groupBinList.forEach(bin -> groupHistogram.appendBinValue(bin));
	}

	@Override
	public StaticState generateStaticState(int startLocationId, int currentScenarioId) {
	
		Double radius = radiusMeter;
		double mass = 1.0;
		if(radius == null) {
			
			mass = 60.0 + 40.0 * FastMath.random();
			radius = mass / 320.0;
		}
		
		StaticState newStaticState = new StaticState(this.maximalVelocityGroup, 
				this.desiredVelocityGroup,
				radius,
				mass,
				currentScenarioId,
				startLocationId, 
				this.getId(),
				this.currentGroupId,
				this.groupSize);
		
		newStaticState.setId(PedestrianManager.getPedestrianNewId());
		this.groupSizeForGenerator--;
	
		if(this.groupSizeForGenerator <= 0) {
			
			this.groupSizeForGenerator = (int)groupHistogram.getBinValueEquallyDistributed();
			this.groupSize = this.groupSizeForGenerator; 
			this.maximalVelocityGroup = velocityHistogram.getBinValue(velocityHistogram.getNumberOfBins());
			this.desiredVelocityGroup = velocityHistogram.getBinValueEquallyDistributed();	
			this.currentGroupId = PedestrianManager.getGroupNewId();	
		}
		
		return newStaticState;
	}
	
	@Override
	public int getNextGroupSize() {
		
		if(this.groupSize == 0) {
			
			this.groupSizeForGenerator = (int)groupHistogram.getBinValueEquallyDistributed();
			this.groupSize = groupSizeForGenerator;
			this.maximalVelocityGroup = velocityHistogram.getBinValue(velocityHistogram.getNumberOfBins());
			this.desiredVelocityGroup = velocityHistogram.getBinValueEquallyDistributed();	
			this.currentGroupId = PedestrianManager.getGroupNewId();	
		}
		
		return groupSize;
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
