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

package tum.cms.sim.momentum.model.generator.interval;

import tum.cms.sim.momentum.infrastructure.execute.SimulationState;

public class PlanGeneratorInterval extends GeneratorInterval {

	protected double intervalStartTime = Double.POSITIVE_INFINITY;
	protected double intervalEndTime = Double.NEGATIVE_INFINITY;
	
	protected double percentagePedstrian = Double.NEGATIVE_INFINITY;
	
	protected double pedestrianReminder = 0.0;
	//protected double pedestriansAllowedToGenerate = 0;
	
	public PlanGeneratorInterval(double generatorStartTime, double generatorEndTime, int maximalPedestrians) {

		super(generatorStartTime, generatorEndTime, maximalPedestrians);
	}
		
	public void loadConfiguration(double intervalStartTime, double intervalEndTime, double percentagePedestrian) {
		
		this.intervalStartTime = intervalStartTime;
		this.intervalEndTime = intervalEndTime;
		this.percentagePedstrian = percentagePedestrian;
	}
	
	@Override
	public double allowPedestrianGeneration(SimulationState simulationState, int generatedPedestrians, int maximalPedestrians) {
		
		if(!Double.isFinite(this.intervalEndTime)) { // if the end time was calculated with infinite generator end time this intervenes
			
			this.intervalEndTime = simulationState.getSimulationEndTime();
		}

		Boolean isAfterStartTime  = this.generatorStartTime <= simulationState.getCurrentTime() &&
				   				   this.intervalStartTime <= simulationState.getCurrentTime();
		
		Boolean isBeforeEndTime = this.generatorEndTime >= simulationState.getCurrentTime() &&
				   				  this.intervalEndTime >= simulationState.getCurrentTime();
				   				  
		if(isAfterStartTime && isBeforeEndTime) {

			double duration = this.intervalEndTime - this.intervalStartTime;
			double pedestriansPerInterval = maximalPedestrians * this.percentagePedstrian;
			double timeStepsInInterval = duration / simulationState.getTimeStepDuration();
			double pedestriansPerTimeStep = pedestriansPerInterval / timeStepsInInterval;
			
			this.pedestrianReminder += pedestriansPerTimeStep;
		}

		double toGenerate = (int)pedestrianReminder;
		
		if(toGenerate > 0) {
			
			this.pedestrianReminder -= toGenerate;
		}
		
		return toGenerate;
	}
}
