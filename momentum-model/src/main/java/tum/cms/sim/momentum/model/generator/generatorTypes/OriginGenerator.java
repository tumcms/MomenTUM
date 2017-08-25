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

package tum.cms.sim.momentum.model.generator.generatorTypes;

import java.util.ArrayList;

import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.generator.Generator;
import tum.cms.sim.momentum.model.generator.geometry.GeneratorGeometry;
import tum.cms.sim.momentum.model.generator.interval.GeneratorIntervalSet;

public abstract class OriginGenerator extends Generator  {
	
	private final static String maximalPedestriansName = "maximalPedestrians";
	private final static String pedestrianVarianceName = "pedestrianVariance";
	private final static String basicHeadingName = "basicHeading";	
	private final static String startTimeName = "startTime";	
	private final static String endTimeName = "endTime";	
	private final static String safetyDistanceName = "safetyDistance";
	private final static String scenarioLatticeIdName = "scenarioLatticeId";
	
	protected double generatorStartTime = Double.NEGATIVE_INFINITY;	
	protected double generatorEndTime = Double.NEGATIVE_INFINITY;
	protected double safetyDistance = Double.NEGATIVE_INFINITY;	
	protected double basicHeading = Double.MIN_VALUE;
	protected int maximalPedestrians = Integer.MIN_VALUE;
	protected Integer pedestrianVariance = null;
	protected int scenarioLatticeId = Integer.MIN_VALUE;

	protected ArrayList<Double> intervalDurations = null;
	protected ArrayList<Double> intervalPercentages = null;
	
	protected int generatedPedestrians = 0;
	protected double remainingPedestrians = 0;

	protected GeneratorIntervalSet intervalSet = null;

	public void setIntervalSet(GeneratorIntervalSet intervalSet) {
		this.intervalSet = intervalSet;
	}
	
	protected GeneratorGeometry generatorGeometry = null;
	
	public void setGeneratorGeometry(GeneratorGeometry generatorGeometry) {
		this.generatorGeometry = generatorGeometry;
	}
	
	@Override
	public void callPreProcessing(SimulationState simulationState) {
		
		basicHeading = this.properties.getDoubleProperty(basicHeadingName);
		maximalPedestrians = this.properties.getIntegerProperty(maximalPedestriansName);
		pedestrianVariance = this.properties.getIntegerProperty(pedestrianVarianceName);
		generatorStartTime = this.properties.getDoubleProperty(startTimeName);
		generatorEndTime = this.properties.getDoubleProperty(endTimeName);
		safetyDistance = this.properties.getDoubleProperty(safetyDistanceName);
		
		if(this.properties.getIntegerProperty(scenarioLatticeIdName) != null) {
			
			scenarioLatticeId = this.properties.getIntegerProperty(scenarioLatticeIdName);
		}
	}
}
