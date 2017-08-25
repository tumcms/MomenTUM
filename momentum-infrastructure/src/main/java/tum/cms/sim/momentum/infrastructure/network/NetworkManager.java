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

import tum.cms.sim.momentum.infrastructure.logging.LoggingManager;

import tum.cms.sim.momentum.infrastructure.network.BrokerManager;
import tum.cms.sim.momentum.infrastructure.network.NetworkStrings;
import tum.cms.sim.momentum.infrastructure.network.ChannelManager;
import tum.cms.sim.momentum.infrastructure.network.SynchronizationManager;
import tum.cms.sim.momentum.infrastructure.network.Parser;

import tum.cms.sim.momentum.configuration.network.NetworkConfiguration;
import tum.cms.sim.momentum.configuration.network.NetworkConfiguration.NetworkMode;
import tum.cms.sim.momentum.infrastructure.time.TimeManager;

public class NetworkManager implements INetworkInformation {
	
	protected NetworkState networkState = new NetworkState();
		
	private BrokerManager brokerManager = new BrokerManager();
	private ChannelManager controlMessageChannel = new ChannelManager();
	private Parser parser = new Parser();
	private SynchronizationManager synchronizationManager = new SynchronizationManager();
	
	public NetworkState getNetworkState() {
		return networkState.clone();
	}
	
	public NetworkMode getNetworkMode() {
		return this.synchronizationManager.getNetworkMode();
	}

	public NetworkManager(NetworkConfiguration networkConfiguration) {
		
		if (networkConfiguration == null ) {
			this.networkState.setState(NetworkState.State.deactivated);
		} else {
			this.networkState.setState(NetworkState.State.offline);
			this.networkState.setHostAddress(networkConfiguration.getHostAddress());
			this.networkState.setControlTopicName(networkConfiguration.getControlTopicName());
			this.networkState.setDataTopicName(networkConfiguration.getDataTopicName());
			
			this.synchronizationManager.setNetworkMode(networkConfiguration.getNetworkMode());
		}
	}
	
	public void initializeNetwork() {
		if(this.networkState.getState() == NetworkState.State.deactivated) {
			return;
		}
		
		this.brokerManager.start( this.networkState.getHostAddress() );
		LoggingManager.logUser(NetworkStrings.BrokerStarted);
		
		controlMessageChannel.start(networkState.getHostAddress(), 
				this.networkState.getControlTopicName());
		
		this.networkState.setState(NetworkState.State.initialized);
	}

	public void startSimulationServer() {
		if(this.networkState.getState() != NetworkState.State.initialized) {
			return;
		}
	
		LoggingManager.logUser(NetworkStrings.WaitingForStart);
		
		waitForEvent(Parser.ControlMessage.MessageCommand.simulate);
		
		this.synchronizationManager.startClock();
		LoggingManager.logUser(NetworkStrings.StartingSimulation);
		
		this.networkState.setState(NetworkState.State.started);
	}
	
	public void pauseSimulationServer() {
		if(this.networkState.getState() != NetworkState.State.started) {
			return;
		}

		this.networkState.setState(NetworkState.State.stopped);
	}
	
	public void endNetwork() {
		if(this.networkState.getState() == NetworkState.State.started) {
			this.pauseSimulationServer();
		}
		
		if(this.networkState.getState() != NetworkState.State.stopped) {
			return;
		}

		this.controlMessageChannel.close();
		this.brokerManager.close();
		
		this.networkState.setState(NetworkState.State.offline);
	}
	
	public void handleNetwork(TimeManager timeController) {
		if(this.networkState.getState() != NetworkState.State.started) {
			return;
		}
		synchronizationManager.stopComputationDurationMeasurement();
		
		String messageText = controlMessageChannel.pullMessage();
		Parser.ControlMessage receivedControlMessage = parser.parseTextMessage(messageText);
		
		switch (receivedControlMessage.getMessageCommand()) {
		case simulate:
			break;
			
		case pause:
			LoggingManager.logUser(NetworkStrings.EventReceivedPause);
			waitForEvent(Parser.ControlMessage.MessageCommand.simulate);
			break;
			
		case finish:
			LoggingManager.logUser(NetworkStrings.EventReceivedFinish);
			this.networkState.setState(NetworkState.State.stopped);
			break;
			
		case synchronize:
			// LoggingManager.logUser(NetworkStrings.EventReceivedSynchronize);
			
			double systemTimeDouble = receivedControlMessage.getSystemTime();
			long systemTimeLong = SynchronizationManager.convertSecondsToNanoSeconds(systemTimeDouble);
			
			
			//double latenceDouble = receivedControlMessage.getLatence();
			//long latenceLong = SynchronizationManager.convertSecondsToNanoSeconds(latenceDouble);
//			this.synchronizationManager.setLatence(latenceLong);
		
			//long mytime = this.synchronizationManager.getSimulationTime();
			//System.out.printf("ReceivedTime=%.2fs MyTime=%.2fs Diff=%.2fms Latence=%.2fms\n", systemTimeLong / 1000000000.0, mytime / 1000000000.0, (mytime-systemTimeLong) / 1000000.0, latenceLong / 1000000.0 );

//			double computationTime = this.synchronizationManager.getMeanComputationDuration();
			//System.out.println("MeanTimeStepComputationDuration=" + simulationState.getMeanTimeStepComputationDuration() + "ms myTimeDiff=" + computationTime / 1000000.0 + "ms");

			this.synchronizationManager.updateSimulationTime(systemTimeLong);
			break;
			
		default:
			break;
		}

		
		double nextCycleTimeSecs = timeController.getCurrentTime() + timeController.getTimeStepDuration();
		long nextCycleTimeNs = (long) (nextCycleTimeSecs * 1000000000.0);
		
		synchronizationManager.loopWait(nextCycleTimeNs);
		
		synchronizationManager.startComputationDurationMeasurement();
	}
	
	
	private void waitForEvent(Parser.ControlMessage.MessageCommand event) {
		
		while(!(this.parser.parseTextMessage(this.controlMessageChannel.pullMessage()).getMessageCommand() == event)) {
			try {
				Thread.sleep(synchronizationManager.SleepingPeriod);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
