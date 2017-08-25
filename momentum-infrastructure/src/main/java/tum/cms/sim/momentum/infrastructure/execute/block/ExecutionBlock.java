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

package tum.cms.sim.momentum.infrastructure.execute.block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import tum.cms.sim.momentum.infrastructure.execute.CallController;
import tum.cms.sim.momentum.infrastructure.execute.IUpdateState;
import tum.cms.sim.momentum.infrastructure.execute.callable.IExecutionCallable;
import tum.cms.sim.momentum.infrastructure.execute.threading.ThreadingManager.ThreadingType;
import tum.cms.sim.momentum.infrastructure.time.TimeManager;

public class ExecutionBlock<T> {

	private int blockManipulator = Integer.MIN_VALUE;
	private HashMap<Integer, Integer> exeuctionModelManipulator = new HashMap<>();
	
	private ArrayList<IExecutionCallable<T>> models = new ArrayList<IExecutionCallable<T>>();
	private IUpdateState stateUpdater = null;
	
	public void setStateUpdater(IUpdateState stateUpdater) {
		this.stateUpdater = stateUpdater;
	}

	public int getBlockManipulator() {
		return blockManipulator;
	}

	public void setBlockManipulator(int blockManipulatorId) {
		this.blockManipulator = blockManipulatorId;
	}

	public void addModel(IExecutionCallable<T> model) {
		
		this.models.add(model);
	}
	
	/**
	 * Executes all models of a block.
	 * This method checks if the multiplicator allow to compute the model operations.
	 * The models provide their type and their threading behavior
	 * 
	 * @param callController, for calling the models
	 * @param timeManager
	 * @throws TimeoutException
	 */
	public void callOrderdModels(CallController<T> callController, TimeManager timeManager) throws TimeoutException {
	
		// Check if the block can be executed based on the time step
		if(callController.isCallable(timeManager.getTimeState(), this.getBlockManipulator())) {
			
			// Execute all models in given order
			for(IExecutionCallable<T> model : models) {
				
				// Check if the model can be called based on the time step
				if(exeuctionModelManipulator.containsKey(model.getId()) &&
				   !callController.isCallable(timeManager.getTimeState(), exeuctionModelManipulator.get(model.getId()))) {
					
					continue;
				}
				
				callController.callModelBeforeBehavior(this.getBlockManipulator(), model);
				
				callController.callModelBehavior(model,
						this.getBlockManipulator(),
						ThreadingType.getThreadingType(model.isMultiThreading()));
				
				callController.callModelAfterBehavior(this.getBlockManipulator(), model);
				
				// Update corresponding agent states
				stateUpdater.updateState(model.getModelType());
			}
		}
	}
}
