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

package tum.cms.sim.momentum.infrastructure.execute.threading;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import tum.cms.sim.momentum.configuration.execution.ThreadingStateConfiguration;

public class ThreadingManager<T, K> {

	public enum ThreadingType {
		
		MultiThread,
		SingleThread;
		
		public static ThreadingType getThreadingType(boolean isMultiThreading) {

			if(isMultiThreading) {
				
				return MultiThread;
			}
			
			return SingleThread;
	    }
	}
	
	private static final String timeoutExceptionString = "A parallel execuction took longer than 30 seconds!";
	
	private CountDownLatch syncHelper = null;
	private int timeoutMiliseconds = 1000 * 30;
	private ThreadingState threadingState = null;
	private ThreadPoolExecutor workerPool = null;
	private IThreadingTaskSplitter<? extends T> taskSplitter = null;
	
	public ThreadingState getThreadingState(Integer callOnThread) {
		
		ThreadingState state = threadingState.clone();
		state.setCalledOnThread(callOnThread);
		
		return state;
	}


	public void setTaskSplitter(IThreadingTaskSplitter<? extends T> taskSplitter) {

		this.taskSplitter = taskSplitter;
	}

	public ThreadingManager(ThreadingStateConfiguration threadingStateConfiguration) {
		
		threadingState = new ThreadingState();
		
		if(threadingStateConfiguration != null) {
			
			threadingState.setThreads(threadingStateConfiguration.getThreads());
		}

		workerPool = new ThreadPoolExecutor(threadingState.getThreads(),
				threadingState.getThreads(),
				0,
				TimeUnit.MILLISECONDS, 
				new ArrayBlockingQueue<Runnable>(threadingState.getThreads()));
	}
	
	public void cleanUp() throws TimeoutException {
		
		workerPool.shutdown();
		
		try {
			
			workerPool.awaitTermination(this.timeoutMiliseconds, TimeUnit.MILLISECONDS);
		} 
		catch (InterruptedException e) {
	
			throw new TimeoutException(ThreadingManager.timeoutExceptionString);
		}
	}
	
	public void prepareThreading() {

		syncHelper = new CountDownLatch(threadingState.getThreads());
	}
	
	/**
	 * 
	 * @return true if failed!
	 */
	public boolean runWorker(IThreadTask<T,K> taskRunner, 
			K additionalData,
			int startIndex, 
			int endIndex) {
		
		Collection<? extends T> splittTask = (Collection<? extends T>) ThreadingManager.this
				.taskSplitter
				.getWorkForThread(startIndex, endIndex);
		
		TaskRunnable<T,K> worker = new TaskRunnable<T,K>(taskRunner, 
				additionalData, 
				this.syncHelper, 
				splittTask);
		
		workerPool.execute(worker);
		
		return worker.hasFailed();
	}
	
	public void waitForWorkdone(boolean isDebug) throws TimeoutException {

		try {
			
			if(isDebug) {
		
				syncHelper.await();
			}
			else {
				
				syncHelper.await(this.timeoutMiliseconds, TimeUnit.MILLISECONDS);
			}
		} 
		catch (InterruptedException e) {

			throw new TimeoutException(ThreadingManager.timeoutExceptionString);
		}
	}

	public ArrayList<Pair<Integer, Integer>> calculateWorkLoad(int numberOfItems) {
		
		ArrayList<Pair<Integer, Integer>> workLoad = new ArrayList<Pair<Integer, Integer>>();
		
		if(numberOfItems <= 0) {
			
			return workLoad;
		}
		
		int pedsToHandleEach = (int)(numberOfItems / threadingState.getThreads());
		int reminder = numberOfItems % threadingState.getThreads();
		
		int splitterIndex = 0;
		Pair<Integer, Integer> startEndIndex = null;
		
		for(int iter = 0; iter < threadingState.getThreads(); iter++) {
			
			if(iter == 0) {
				
				// the first thread gets the reminder work
				startEndIndex = new MutablePair<Integer, Integer>(splitterIndex, splitterIndex + reminder + pedsToHandleEach);		
				workLoad.add(startEndIndex);
				splitterIndex = splitterIndex + reminder + pedsToHandleEach;
			}
			else {
				
				startEndIndex = new MutablePair<Integer, Integer>(splitterIndex, splitterIndex + pedsToHandleEach);		
				workLoad.add(startEndIndex);
				splitterIndex = splitterIndex + pedsToHandleEach;
			}
		}
		
		return workLoad;
	}
}
