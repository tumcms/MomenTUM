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

package tum.cms.sim.momentum.infrastructure.time;

import java.util.HashMap;

import tum.cms.sim.momentum.configuration.execution.TimeStateConfiguration;
import tum.cms.sim.momentum.infrastructure.logging.LoggingManager;
import tum.cms.sim.momentum.utility.generic.IUnique;

public class TimeManager {
	
	public static final double toSeconds = 1000.0;
	public static final double toMinutes = toSeconds * 60;
	public static final double toHours = toSeconds * 60 * 60;
	
	private double meanTimeStepComputationDuration = -1.0; 
	protected TimeState timeState = null;
	
	private HashMap<IUnique, Long> timeMeasurments = new HashMap<>();
	
	private double timeSimulationMeasurmentStart = 0.0;
	private double timeMeasurmentStart = 0.0;
	private double executionExecutionTime = 0.0;
	private double executionTimePreProcessing = 0.0;
	private double executionTimePostProcessing = 0.0;
	private double runTimePercentageTracker = 0.0;

	public TimeState getTimeState() {
		return timeState.clone();
	}

	public TimeManager(TimeStateConfiguration timeStateConfiguration) {
	
		this.timeState = new TimeState();
		this.timeState.setCurrentTime(0.0);
		this.timeState.setCurrentTimeStep(0L);
		this.timeState.setLastTimeStepTime(null);
		
		if(timeStateConfiguration != null) {
			this.timeState.setTimeStepDuration(timeStateConfiguration.getTimeStepDuration());
			this.timeState.setSimulationEndTime(timeStateConfiguration.getSimulationEndTime());
		}
	}

	public double getMeanTimeStepComputationDuration() {
		return meanTimeStepComputationDuration;
	}

	public Double getTimeStepDuration() {
		return timeState.getTimeStepDuration();
	}

	public Double getCurrentTime() {
		return timeState.getCurrentTime();
	}
	
	public Long getCurrentTimeStep() {
		return timeState.getCurrentTimeStep();
	}

	public Double getSimulationEndTime() {
		return timeState.getSimulationEndTime();
	}

	public boolean isFinished() {
		
		return this.timeState.remainingTime() < 0.0 ? true : false;
	}

	public void nextTimeStep() {
		
		this.computeMeanTimeStepComputationDuration();
		
		this.timeState.incrementTimeStep();
		this.timeState.updateCurrentTime();

		double perentage = ((this.getCurrentTime() * 100) / this.timeState.getSimulationEndTime());
		
		if((int)(perentage) > runTimePercentageTracker && (int)(perentage) % 10 == 0) {
			
			runTimePercentageTracker += 10.0;
			LoggingManager.logUser("Simulation %d%% done", (int)((this.getCurrentTime() / this.timeState.getSimulationEndTime()) * 100));
		}
		
		this.timeState.updateLastTimeStepTime();
	}
	
	private void computeMeanTimeStepComputationDuration() {
		
		// info
		// first time step is number 0
		// thus for sliding mean add 1 to the old current time step and deviate by current time step + 2
		
		if(this.meanTimeStepComputationDuration == -1.0) { 
			
			meanTimeStepComputationDuration = System.currentTimeMillis() - timeSimulationMeasurmentStart;
		}
		else {
			
			meanTimeStepComputationDuration = (
						meanTimeStepComputationDuration * (this.timeState.getCurrentTimeStep()  + 1) 
						+ System.currentTimeMillis() - this.timeState.getLastTimeStepTime())
					/ (this.timeState.getCurrentTimeStep() + 2);
		}
	}

	public void startSimulationTimeMeasurment() {
		
		this.timeSimulationMeasurmentStart = System.currentTimeMillis();
	}
	
	public double stopSimulationTimeMeasurment() {
		
		double result = System.currentTimeMillis() - this.timeSimulationMeasurmentStart;
		this.timeSimulationMeasurmentStart = 0.0;
		
		return result;
	}

	public void startGenericTimeMeasurment() {
		
		this.timeMeasurmentStart = System.currentTimeMillis();
	}
	
	public void startExecutionTimeModel(IUnique unique) {
		
		if(!timeMeasurments.containsKey(unique)) {
			
			timeMeasurments.put(unique, System.currentTimeMillis());
		}
	}
	
	public double stopGenericTimeMeasurment() {
		
		double result = System.currentTimeMillis() - this.timeMeasurmentStart;
		this.timeMeasurmentStart = 0.0;
		
		return result;
	}
	
	public double getExeuctionTimeModel(String modelName) {
		
		return this.timeMeasurments.get(modelName);
	}
	
	public double getExecutionTimeOverhead() {
		
		double nonOverhead = this.timeMeasurments.values()
				.stream()
				.mapToDouble(time -> time.doubleValue())
				.sum();
		
		return (this.executionExecutionTime) - nonOverhead;
	}
	
	public double getExecutionTimeProcessing() {
		
		return this.executionExecutionTime;
	}
	
	public void updateSimulationExecutionTime() {
		this.executionExecutionTime += this.stopSimulationTimeMeasurment();
	}
	
	public double getPostProcessingTime() {
		
		return this.executionTimePostProcessing;
	}
	
	public void updateExecutionTimeModel(IUnique unique) {
		
		if(!timeMeasurments.containsKey(unique)) {
			
			timeMeasurments.put(unique, 0l);
		}
		
		timeMeasurments.put(unique, System.currentTimeMillis() - timeMeasurments.get(unique));
	}
	
	public void updatePostProcessingTimeMeasurment(double postProcessingStopTimeMeasurment) {

		this.executionTimePostProcessing += postProcessingStopTimeMeasurment;
	}

	public double getPreProcessingTime() {
		
		return this.executionTimePreProcessing;
	}
	
	public void updatePreProcessingTimeMeasurment(double preProcessingStopTimeMeasurment) {

		this.executionTimePreProcessing += preProcessingStopTimeMeasurment;
	}
}
