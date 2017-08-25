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

package tum.cms.sim.momentum.model.analysis;

/**
 * This is the class for all analysis methods.
 * In general, the AnalysisModel gets a Measure and an AnalysisInputSource.
 * The types of these classes define the details of the analysis process.
 * This class organized the analysis processes within the simulation pipeline.
 * 
 * To control the analysis process use the call property:
 * <property name="call" type="Integer" value="32"/>
 * The value can be 0, a number or Integer.MAX_VALUE.
 * If the value is 0 data will be analyzed in the pre-processing phase.
 * If the value is Integer.MAX_VALUE data will be analyzed in the post-processing phase.
 * If the value is 0 < value < Integer.MAX_VALUE the data will be analyzed every time-step
 * in which value % time-step = 0
 *  
 * Also, the class provides a analysisStartStep property:
 * <property name="analysisStartStep" type="Integer" value="0"/>	
 * This indicates when to start the analysis if call is 0 < value < Integer.MAX_VALUE
 * 
 * Also, the class provides a analysisEndStep property:
 * <property name="analysisEndStep" type="Integer" value="Integer.MAX_VALUE"/>
 * This indicates when to stop the analysis if call is 0 < value < Integer.MAX_VALUE
 * 
 * @author Peter M. Kielar
 *
 */
public abstract class TimeAnalysisModel extends AnalysisModel {
	
//	private final static String analysisStartStepName = "analysisStartStep";
//	private final static String analysisEndStepName = "analysisEndStep";
//	private final static String callName = "call";
//
//	private long analysisStartStep = 0L;
//	private long analysisEndStep = Long.MAX_VALUE;
//	private int call = Integer.MAX_VALUE;
//
//	@Override
//	public void callPreProcessing(SimulationState simulationState) {
//		
//		this.analysisStartStep = this.properties.getIntegerProperty(analysisStartStepName);
//		this.analysisEndStep = this.properties.getIntegerProperty(analysisEndStepName);
//
//		Integer call = this.properties.getIntegerProperty(callName);
//		
//		if(call != null) {
//			
//			this.call = call;
//		}
//		
//		LoggingManager.logDebug(MessageStrings.propertySetTo,
//				callName,
//				String.valueOf(call),
//				this.getClass().getSimpleName());
//		
//		LoggingManager.logDebug(MessageStrings.propertySetTo,
//				analysisStartStepName,
//				String.valueOf(this.analysisStartStep),
//				this.getClass().getSimpleName());
//		
//		LoggingManager.logDebug(MessageStrings.propertySetTo,
//				analysisEndStepName,
//				String.valueOf(this.analysisEndStep),
//				this.getClass().getSimpleName());
//								
//		if(this.call == 0) {
//	
//			LoggingManager.logDebug(this, "executes a pre-processing analysis operation");
//		}
//	}
//	
//	@Override
//	public void execute(Collection<? extends Void> splittTask, SimulationState simulationState) {
//
//		if(0 < this.call && this.call < Integer.MAX_VALUE) {
//			
//			if(simulationState.getCurrentTimeStep() % this.call == 0) {
//			
//				
//			}
//		}
//	
//		// Load data set for the current time step
//		long timeStep = simulationState.getCurrentTimeStep();
//		
//		if(timeStep % this.analysisInputSource.getTimeStepDifference() == 0) {
//			
//			this.analysisInputSource.readNextDataSet();	
//		}
//	
//		if(timeStep % this.analysisInputSource.getCall() == 0) {
//			
//			if(this.analysisInputSource.getAnalysisStartStep() <= timeStep && 
//					   timeStep <= this.analysisInputSource.getAnalysisEndStep()) {
//			
//				// clear "its used up" each time lap
//				this.getOutputDataMap().clear();
//				
//				// use data set if in correct time span
//				if(this.analysisInputSource.getAnalysisStartStep() <= timeStep && 
//				   timeStep <= this.analysisInputSource.getAnalysisEndStep()) {
//					
//					// fill input data map with load data
//					for(String type : this.inputTypes) {
//						
//						AnalysisElementSet dataElementSet = this.analysisInputSource.getData(type, timeStep);
//						this.inputDataMap.put(type, dataElementSet);
//					}
//				}
//					
//				if(this.inputDataMap != null && !this.inputDataMap.isEmpty()) {
//		
//					this.measure(timeStep, this.inputDataMap, this.outputDataMap);
//				}
//			}
//		}
//	}
//	
//	@Override
//	public void callPostProcessing(SimulationState simulationState) {
//
//		if(this.call == Integer.MAX_VALUE) {
//			
//			LoggingManager.logDebug(this, "executes a post-processing analysis operation");
//		
//		}
//	}
}
