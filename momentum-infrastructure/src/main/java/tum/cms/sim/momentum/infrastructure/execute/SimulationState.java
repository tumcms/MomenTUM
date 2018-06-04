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

package tum.cms.sim.momentum.infrastructure.execute;

import tum.cms.sim.momentum.infrastructure.execute.threading.ThreadingState;
import tum.cms.sim.momentum.infrastructure.network.NetworkState;
import tum.cms.sim.momentum.infrastructure.time.TimeState;

public class SimulationState {

	private int timeStepMulticator = 1;

	private int numberOfAgents = 0;


	private TimeState timeState = null;
	private ThreadingState threadingState = null;
	private NetworkState networkState = null;
	
	public SimulationState(TimeState timeState, ThreadingState threadingState, int numberOfAgents, NetworkState networkState) {
		
		this.timeState = timeState;
		this.threadingState = threadingState;
		this.numberOfAgents = numberOfAgents;
		this.networkState = networkState;
	}
	
	/**
	 * Provides the number of simulation agents, e.g. pedestrians
	 */
	public int getNumberOfAgents() {
		return numberOfAgents;
	}
	
	/**
	 * from 1 to X. If -1, only one thread exists
	 */
	public int getCalledOnThread() {
		
		return threadingState.getCalledOnThread();
	}
	
	/**
	 * minimal 1, maximal X
	 */
	public int getNumberOfThreads() {
		return threadingState.getThreads();
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
	
	public int getTimeStepMulticator() {
		return timeStepMulticator;
	}

	public void setTimeStepMulticator(int timeStepMulticator) {
		this.timeStepMulticator = timeStepMulticator;
	}
	
	
	public String getNetworkHostAddress() {
		return networkState.getHostAddress();
	}
	
	public String getNetworkControlTopicName() {
		return networkState.getControlTopicName();
	}
	
	public String getNetworkDataTopicName() {
		return networkState.getDataTopicName();
	}

	/**
	 * Translates a data time step to simulation time step.
	 * Basically you ask here: I have a time step X (int) and I known
	 * the duration (seconds) for X. What is this in simulation time?
	 * 
	 * E.g. 
	 * timeStepMapping is 0.04 -> means 1 dataTimeStep is 0.04 seconds
	 * getTimeStepDuration is 0.1 ->  means 1 simulationTimeStep is 0.1 seconds
	 * Calculation with 132 dataTimeStep each with 0.04 length
	 * (int)(131 * 0.04 seconds / 0.1 seconds) + 0.5 = (int)(5.24 seconds / 0.1 seconds) 
	 * = (int)(52.4) = 52 simulation time step
	 */
	public long getScaledTimeStep(long timeStepToScale, double timeStepToScaleDuration) {
		
		return (long)(((timeStepToScaleDuration * timeStepToScale) / this.getTimeStepDuration().doubleValue()) + 0.000001);
	}
}
