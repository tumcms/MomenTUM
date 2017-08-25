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

package tum.cms.sim.momentum.infrastructure.network;

import tum.cms.sim.momentum.configuration.network.NetworkConfiguration.NetworkMode;

import java.util.ArrayDeque;

public class SynchronizationManager {
	
	/**
	 * Time period waiting each cycle [ns].
	 */
	public long SleepingPeriod = 100;
	private NetworkMode networkMode = NetworkMode.ClockIndependent;

	private long TimeSimulationStart = 0;		// Point in time, when simulation was started
	private long TimeSimulationStartOffset = 0; // Time offset to compensate for time divergence across distributed simulation
	
	private long ComputationDurationMeasurementStart = 0;	// Point in time, when measurement is started for one computational cycle
//	private long NetworkLatence = 0; // measured network latency
	
	private int maxNumberMeasurementSamples = 5; // Number of samples to calculate mean computational time
	private int currentNumberMeasurementSamples = 0;
	private ArrayDeque<Long> ComputationDurationMeasurementSamples = new ArrayDeque<Long>(maxNumberMeasurementSamples); // List of samples

	public void setNetworkMode(NetworkMode networkMode) {
		this.networkMode = networkMode;
	}
	
	public NetworkMode getNetworkMode() {
		return this.networkMode;
	}
	
	/**
	 * Trigger, when simulation starts
	 */
	public void startClock() {
		TimeSimulationStart = System.nanoTime();
	}
	
	/**
	 * Returns the time since the start of the simulation.
	 * @return Time since start of simulation [ns].
	 */
	public long getSimulationTime() {
		return System.nanoTime() - TimeSimulationStart + TimeSimulationStartOffset;
	}
	
	/**
	 * Updates the local time.
	 * @param currentTime New time since start of simulation [ns].
	 */
	public void updateSimulationTime(long currentTime) {
		// only update the time, if MomenTUMv2's clock is in slave mode
		if (this.networkMode == NetworkMode.ClockSlave) {
			TimeSimulationStartOffset = currentTime - (System.nanoTime() - TimeSimulationStart);
		}
		
	}
	
//	public static double convertNanoSecondsToSeconds(long nanoSeconds) {
//		return (double) (nanoSeconds / 1000000000.0);
//	}
	
	public static long convertSecondsToNanoSeconds(double seconds) {
		return (long) (seconds * 1000000000.0);
	}
	
	/**
	 * Saves current time for evaluating the computational duration of one step.
	 */
	public void startComputationDurationMeasurement() {
		ComputationDurationMeasurementStart = System.nanoTime();
	}
	
	/**
	 * Stop the time, after loop cycle finished.
	 */
	public void stopComputationDurationMeasurement() {
		long lastComputationDuration = System.nanoTime() - ComputationDurationMeasurementStart;
		if(!ComputationDurationMeasurementSamples.isEmpty() && currentNumberMeasurementSamples >= maxNumberMeasurementSamples) {
			ComputationDurationMeasurementSamples.removeLast();
		} else {
			currentNumberMeasurementSamples++;
		}

		ComputationDurationMeasurementSamples.push(lastComputationDuration);
	}
	
	/**
	 * Mean computational time for executing one loop cycle.
	 * @return in [ns].
	 */
	public double getMeanComputationDuration() {
		long durationSum = 0;
	    for (Long number : ComputationDurationMeasurementSamples) {
	    	durationSum = durationSum + number;
	    }
	    //System.out.println("Number of elements: " + currentNumberMeasurementSamples);
	    return ((double)durationSum/(double)currentNumberMeasurementSamples);
	}
	
	/**
	 * Set the network latence.
	 * @param latence in [ns]
	 */
//	public void setLatence(long latence) {
//		NetworkLatence = latence;
//	}
//	
//	/**
//	 * Returns the network latence.
//	 * @return in [ns].
//	 */
//	public long getLatence() {
//		return NetworkLatence;
//	}
	
	/**
	 * Wait so long, that the next simulation step synchronized with master clock or just ran, when network mode is independent.
	 * @param nextCycleTime
	 */
	public void loopWait(long nextCycleTime) {
		
		if (this.networkMode == NetworkMode.ClockIndependent) {
			return;
		} else if (this.networkMode == NetworkMode.ClockMaster || this.networkMode == NetworkMode.ClockSlave) {
			
			java.util.concurrent.locks.LockSupport.parkNanos(nextCycleTime -
					this.getSimulationTime() -
					(long) this.getMeanComputationDuration());
					//- this.synchronizationManager.GetLatence());
		}
		

	}
	
}
