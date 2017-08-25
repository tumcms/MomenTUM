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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.tuple.Pair;

import tum.cms.sim.momentum.configuration.execution.ExecutionOrderConfiguration;
import tum.cms.sim.momentum.infrastructure.execute.block.ExecutionBlock;
import tum.cms.sim.momentum.infrastructure.execute.block.ExecutionOrderController;
import tum.cms.sim.momentum.infrastructure.execute.callable.IExecutionCallable;
import tum.cms.sim.momentum.infrastructure.execute.callable.IGenericCallable;
import tum.cms.sim.momentum.infrastructure.execute.callable.IPrePostProcessing;
import tum.cms.sim.momentum.infrastructure.execute.threading.IThreadingTaskSplitter;
import tum.cms.sim.momentum.infrastructure.execute.threading.ThreadingManager;
import tum.cms.sim.momentum.infrastructure.execute.threading.ThreadingStringConstants;
import tum.cms.sim.momentum.infrastructure.execute.threading.ThreadingManager.ThreadingType;
import tum.cms.sim.momentum.infrastructure.logging.LoggingManager;
import tum.cms.sim.momentum.infrastructure.network.NetworkManager;
import tum.cms.sim.momentum.infrastructure.time.TimeManager;
import tum.cms.sim.momentum.infrastructure.time.TimeState;

public class CallController<T> {

	private final static int classicalCallMultiplicator = 1;
	private boolean failed = false;
	
	public boolean hasFailed() {
		return failed;
	}

	private boolean isDebug = true;
	private TimeManager timeManager = null;
	private NetworkManager networkManager = null;
	private IThreadingTaskSplitter<T> taskSplitter = null;
	private ThreadingManager<T, SimulationState> threadingManager = null; 
	private ExecutionOrderController<T> executionOrderController = null;

	public void setTimeManager(TimeManager timeManager) {
		this.timeManager = timeManager;
	}
	
	public void setNetworkManager(NetworkManager networkManager) {
		this.networkManager = networkManager;
	}
	
	public void setThreadingManager(ThreadingManager<T, SimulationState> threadingManager) {
		this.threadingManager = threadingManager;
	}

	public void setTaskSplitter(IThreadingTaskSplitter<T> taskSplitter) {
		this.taskSplitter = taskSplitter;
	}
	
	public boolean isClassical() {
		
		return executionOrderController == null;
	}

	public void createExecutionOrder(ExecutionOrderConfiguration executionOrderConfiguration,
			Map<Integer, ? extends IExecutionCallable<T>> executionModels,
			IUpdateState stateUpdater)  {
		
		if(executionOrderConfiguration != null) {
			
			executionOrderController = new ExecutionOrderController<T>(executionOrderConfiguration,
					executionModels,
					stateUpdater);
		}
	}
	
	public void callPreProcessing(Collection<? extends IPrePostProcessing> callables) {
		
		for(IPrePostProcessing callable : callables) {
			
			LoggingManager.logDebug(callable, "pre processing");
			timeManager.startExecutionTimeModel(callable);
			
			callable.callPreProcessing(this.createSimulationState());
			
			timeManager.updateExecutionTimeModel(callable);
		}
	}

	public void callGenericCallable(Collection<? extends IGenericCallable> callables) {
		
		for(IGenericCallable callable : callables) {
			
			timeManager.startExecutionTimeModel(callable);
			
			callable.execute(null, this.createSimulationState());
			
			timeManager.updateExecutionTimeModel(callable);
		}
	}
	
	public void callExecutionCallable(Collection<? extends IExecutionCallable<T>> callables, IUpdateState updateState)  throws TimeoutException {
		
		for(IExecutionCallable<T> callable : callables) {

			timeManager.startExecutionTimeModel(callable);
			
			callable.executeBeforeExecute(this.createSimulationState(),	taskSplitter.getWork());
			
			timeManager.updateExecutionTimeModel(callable);
		}
		
		for(IExecutionCallable<T> callable : callables) {
			
			timeManager.startExecutionTimeModel(callable);

			this.callBehavior(callable, classicalCallMultiplicator, ThreadingType.MultiThread);
			
			timeManager.updateExecutionTimeModel(callable);
		}
		
		for(IExecutionCallable<T> callable : callables) {

			timeManager.startExecutionTimeModel(callable);
			
			callable.executeAfterExecute(this.createSimulationState(),taskSplitter.getWork());
			
			timeManager.updateExecutionTimeModel(callable);
		}
		
		for(IExecutionCallable<T> callable : callables) {

			updateState.updateState(callable.getModelType());
		}
	}
	
	public void callPostProcessing(Collection<? extends IPrePostProcessing> callables) {
		
		for(IPrePostProcessing callable : callables) {
			
			LoggingManager.logDebug(callable, "post processing");
			timeManager.startExecutionTimeModel(callable);
			
			callable.callPostProcessing(this.createSimulationState());
			
			timeManager.updateExecutionTimeModel(callable);
		}
	}
	
	public void callModelBeforeBehavior(int timeStepMultiplicator,
			IExecutionCallable<T> callable) {
		
		SimulationState simulationState = this.createSimulationState();
		
		simulationState.setTimeStepMulticator(timeStepMultiplicator);
		
		timeManager.startExecutionTimeModel(callable);

		callable.executeBeforeExecute(simulationState, taskSplitter.getWork());
		
		timeManager.updateExecutionTimeModel(callable);
	}
	
	public void callModelBehavior(IExecutionCallable<T> callable,
			Integer timeStepMultiplicator,
			ThreadingType type) throws TimeoutException {

		SimulationState simulationState = this.createSimulationState();
		
		simulationState.setTimeStepMulticator(timeStepMultiplicator);
		
		timeManager.startExecutionTimeModel(callable);
		
		this.callBehavior(callable, timeStepMultiplicator, type);
		
		timeManager.updateExecutionTimeModel(callable);
	}
	
	public void callModelAfterBehavior(int timeStepMultiplicator,
			IExecutionCallable<T> callable) {
		
		SimulationState simulationState = this.createSimulationState();
		
		simulationState.setTimeStepMulticator(timeStepMultiplicator);
		
		timeManager.startExecutionTimeModel(callable);
		
		callable.executeAfterExecute(simulationState,  taskSplitter.getWork());

		timeManager.updateExecutionTimeModel(callable);
	}
	
	private void callBehavior(IExecutionCallable<T> callable,
			int timeStepMultiplicator,
			ThreadingType type) throws TimeoutException {
		
		switch(type) {
		
		case MultiThread:
			
			ArrayList<Pair<Integer, Integer>> workSplitter = threadingManager.calculateWorkLoad(taskSplitter.getWorkSize());
			boolean wasStarted = false;

			if(workSplitter.size() > 0) {

				if(!wasStarted) {
					
					threadingManager.prepareThreading();
					wasStarted = true;
				}
				
				int threadNumber = 1;
				Pair<Integer, Integer> fromStartToEndSplitter = null;
				
				for(int iter = 0; iter < workSplitter.size(); iter++) {
							
					fromStartToEndSplitter = workSplitter.get(iter);
					
					SimulationState simulationState = this.createSimulationState(threadNumber);
					
					simulationState.setTimeStepMulticator(timeStepMultiplicator);
					
					this.failed |= threadingManager.runWorker(callable, 
							simulationState,
							fromStartToEndSplitter.getLeft(), 
							fromStartToEndSplitter.getRight());
					
					threadNumber++;
				}
				
				if(wasStarted) {
					
					threadingManager.waitForWorkdone(this.isDebug);
					wasStarted = false;
				}
			}
			break;
			
		case SingleThread:

			SimulationState simulationState = this.createSimulationState();
			
			simulationState.setTimeStepMulticator(timeStepMultiplicator);
			
			callable.execute(taskSplitter.getWork(), simulationState);
			break;
		}
		
		if(this.failed) {

			LoggingManager.logUser(ThreadingStringConstants.logStopFailed);
		}
	}
	
	public boolean isCallable(TimeState timeState, int timeStepMultiplicator) {
		
		return (timeState.getCurrentTimeStep() + 1) % timeStepMultiplicator == 0;
	}

	public void callBlockExecution(TimeManager simulationtimeManager) throws TimeoutException {
		
		ExecutionBlock<T> executionBlock = this.executionOrderController.nextBlock();
		
		while(executionBlock != null) {
			
			// execute block, if this possible, if not, nothing is done
			// a block can be executed based on timeStep (int) % multiplicator
			// The models are called in order based on the block list configuration
			// The system will automatically update data states
			executionBlock.callOrderdModels(this, simulationtimeManager);	
			
			// get next block based on configuration order			
			executionBlock = this.executionOrderController.nextBlock();	
		}
	}

	public void cleanUp() throws TimeoutException {

		this.threadingManager.cleanUp();
	}
	
	private SimulationState createSimulationState() {
		
		return new SimulationState(timeManager.getTimeState(),
				threadingManager.getThreadingState(null),
				this.taskSplitter.getWorkSize(),
				networkManager.getNetworkState());
	}
	
	private SimulationState createSimulationState(int threadNumber) {
		
		return new SimulationState(timeManager.getTimeState(),
				threadingManager.getThreadingState(threadNumber),
				this.taskSplitter.getWorkSize(),
				networkManager.getNetworkState());
	}
}
