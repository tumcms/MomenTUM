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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import tum.cms.sim.momentum.configuration.model.output.WriterSourceConfiguration.OutputType;
import tum.cms.sim.momentum.data.analysis.AnalysisElement;
import tum.cms.sim.momentum.data.analysis.AnalysisElementSet;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.infrastructure.execute.callable.IGenericCallable;
import tum.cms.sim.momentum.infrastructure.execute.callable.IPrePostProcessing;
import tum.cms.sim.momentum.infrastructure.logging.LoggingManager;
import tum.cms.sim.momentum.model.MessageStrings;
import tum.cms.sim.momentum.model.analysis.measure.Measure;
import tum.cms.sim.momentum.model.output.writerSources.WriterSource;
import tum.cms.sim.momentum.utility.generic.IHasProperties;
import tum.cms.sim.momentum.utility.generic.PropertyBackPack;
import tum.cms.sim.momentum.utility.generic.Unique;

/**
 * This is the class for all analysis methods.
 * In general, the AnalysisModel gets a Measure and an WriterSource.
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
 * Furthermore, the class provides the startAtCount property:
 * <property name="startAtPedestrian" type="Integer" value="0"/>
 * This indicates the minimal number of simulation agent at which the measurement should start.
 * 	
 * In addition to the startAtCount property the fromPedestrianUntil property:
 * <property name="fromPedestrianUntil" type="Double" value="300"/>
 * This provides the time beginning from startAtCount until the measurement should stop.
 *  
 * @author Peter M. Kielar
 *
 */
public class AnalysisModel extends Unique implements IPrePostProcessing, IGenericCallable, IHasProperties  {

	private final static String analysisStartStepName = "analysisStartStep";
	private final static String analysisEndStepName = "analysisEndStep";
	private final static String startAtCountName = "startAtCount";
	private final static String fromPedestrianUntilName = "fromPedestrianUntil";
	
	private final static String callName = "call";

	protected PropertyBackPack properties = null;
	
	private long analysisStartStep = 0L;
	private long analysisEndStep = Long.MAX_VALUE;
	private int startAtCount = 0;
	private double fromPedestrianUntil = Double.MAX_VALUE;
	private int call = Integer.MAX_VALUE;

	protected Measure analysisMeasure = null;
	protected WriterSource writerSource = null;

	/**
	 * Input Type is key as string
	 */
	protected HashMap<String, AnalysisElementSet> inputDataMap = new HashMap<>();
	
	/**
	 * Output Type is key as string
	 */
	protected HashMap<String, AnalysisElementSet> outputDataMap = new HashMap<>();

	@Override
	public PropertyBackPack getPropertyBackPack() {
		return properties;
	}
	
	@Override
	public void setPropertyBackPack(PropertyBackPack propertyContainer) {

		this.properties = propertyContainer; 
	}

	public void setWriterSource(WriterSource writerSource) {
		
		this.writerSource = writerSource;
	}

	public void setMeasure(Measure analysisMeasure) {
	
		this.analysisMeasure = analysisMeasure;
	}
		
	public HashMap<String, AnalysisElementSet> getOutputDataMap() {
		return outputDataMap;
	}  

	public List<String> getOutputTypes() {
		
		return this.analysisMeasure.getOutputTypes();
	}
	
	@Override
	public boolean isMultiThreading() {

		return false;
	}
	
	@Override
	public void callPreProcessing(SimulationState simulationState) {
		
 		this.analysisMeasure.initialize();
		
		if(this.properties.getIntegerProperty(analysisStartStepName) != null) {
			
			this.analysisStartStep = this.properties.getIntegerProperty(analysisStartStepName);
		}
	
		if(this.properties.getIntegerProperty(analysisEndStepName) != null) {
			
			this.analysisEndStep = this.properties.getIntegerProperty(analysisEndStepName);
		}
		
		if(this.properties.getIntegerProperty(startAtCountName) != null) {
			
			this.startAtCount = this.properties.getIntegerProperty(startAtCountName);
		}

		if(this.properties.getDoubleProperty(fromPedestrianUntilName) != null) {
			
			this.fromPedestrianUntil = this.properties.getDoubleProperty(fromPedestrianUntilName);
		}

		Integer call = this.properties.getIntegerProperty(callName);
		
		if(call != null) {
			
			this.call = call;
		}
		
		LoggingManager.logDebug(MessageStrings.propertySetTo,
				callName,
				String.valueOf(call),
				this.getClass().getSimpleName());
		
		LoggingManager.logDebug(MessageStrings.propertySetTo,
				analysisStartStepName,
				String.valueOf(this.analysisStartStep),
				this.getClass().getSimpleName());
		
		LoggingManager.logDebug(MessageStrings.propertySetTo,
				analysisEndStepName,
				String.valueOf(this.analysisEndStep),
				this.getClass().getSimpleName());
		
		LoggingManager.logDebug(MessageStrings.propertySetTo,
				startAtCountName,
				String.valueOf(this.startAtCount),
				this.getClass().getSimpleName());
		
		LoggingManager.logDebug(MessageStrings.propertySetTo,
				fromPedestrianUntilName,
				String.valueOf(this.fromPedestrianUntil),
				this.getClass().getSimpleName());
												
		if(this.call == 0) {
	
			LoggingManager.logDebug(this, "executes a pre-processing analysis operation");
			this.computeMeasuring(simulationState.getCurrentTimeStep());
		}
	}
	
	@Override
	public void execute(Collection<? extends Void> splittTask, SimulationState simulationState) {

		if(0 < this.call && this.call < Integer.MAX_VALUE && simulationState.getNumberOfAgents() >= startAtCount) {
			
			long currentTimeStep = simulationState.getCurrentTimeStep();
			
			if(currentTimeStep % this.call == 0) {
			
				if(this.analysisStartStep <= currentTimeStep && currentTimeStep<= this.analysisEndStep) {

					this.computeMeasuring(currentTimeStep);
				}
			}
		}
	}
	
	@Override
	public void callPostProcessing(SimulationState simulationState) {

		if(this.call == Integer.MAX_VALUE) {
			
			LoggingManager.logDebug(this, "executes a post-processing analysis operation");
			this.computeMeasuring(simulationState.getCurrentTimeStep());
		}
	}
	
	private void computeMeasuring(long currentTimeStep) {
		
		// clear, "its used up" each call
		this.outputDataMap.clear();
		
		// fill input and output data map for each the measuring
		this.inputDataMap = this.loadInputData(currentTimeStep);
		this.outputDataMap = this.arrangeOutputData(currentTimeStep);
		
		if(!this.inputDataMap.isEmpty()) {

			this.analysisMeasure.measure(currentTimeStep, this.inputDataMap, this.outputDataMap);
		}
	}
	
	private HashMap<String, AnalysisElementSet> loadInputData(long currentTimeStep) {
		
		HashMap<String, AnalysisElementSet> inputDataMap = new HashMap<>();
		
		for(String inputDataType : this.analysisMeasure.getInputTypes()) {
			
			AnalysisElementSet dataElementSet = new AnalysisElementSet();
			inputDataMap.put(inputDataType, dataElementSet);
		}
		
		while(this.writerSource.hasNextSet()) {
			
			this.writerSource.loadSet();
			
			while(writerSource.hasNextSetItem()) {
				
				this.writerSource.loadSetItem();
				
				String id = this.writerSource.readSingleValue(OutputType.id.name());
				
				for(String inputDataType : this.analysisMeasure.getInputTypes()) {
					
					AnalysisElement analysisElement = null;
					
					if(AnalysisType.analysisDoubleTypes.contains(inputDataType)) {
						
						analysisElement = new AnalysisElement(id, 
										Double.parseDouble(this.writerSource.readSingleValue(inputDataType)), 
										currentTimeStep);
					}
					else {
						
						analysisElement = new AnalysisElement(id, 
								Integer.parseInt(this.writerSource.readSingleValue(inputDataType)), 
								currentTimeStep);
					}

					inputDataMap.get(inputDataType).addElement(analysisElement);
				}
			}
	
		}
		 
		return inputDataMap;
	}
	
	private HashMap<String, AnalysisElementSet> arrangeOutputData(long currentTimeStep) {
	
		HashMap<String, AnalysisElementSet> outputDataMap = new HashMap<>();
		
		for(String inputDataType : this.analysisMeasure.getOutputTypes()) {
			
			AnalysisElementSet dataElementSet = new AnalysisElementSet();
			outputDataMap.put(inputDataType, dataElementSet);
		}
		
		return outputDataMap;
	}
}
