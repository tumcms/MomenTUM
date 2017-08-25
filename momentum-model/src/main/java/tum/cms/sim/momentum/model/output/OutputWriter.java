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

package tum.cms.sim.momentum.model.output;

import java.util.ArrayList;
import java.util.Collection;

import tum.cms.sim.momentum.data.output.WriterData;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.infrastructure.execute.callable.Callable;
import tum.cms.sim.momentum.infrastructure.execute.callable.IGenericCallable;
import tum.cms.sim.momentum.infrastructure.logging.LoggingManager;
import tum.cms.sim.momentum.model.MessageStrings;
import tum.cms.sim.momentum.model.output.writerFormats.WriterFormat;
import tum.cms.sim.momentum.model.output.writerSources.WriterSource;
import tum.cms.sim.momentum.model.output.writerTargets.WriterTarget;
import tum.cms.sim.momentum.utility.generic.IHasProperties;
import tum.cms.sim.momentum.utility.generic.PropertyBackPack;

/**
 * This is the class that writes formated data from a source into a target.
 * In general, the OutputWriter gets a WriterTarget, a WriterFormat and a WriterSource.
 * The types of these classes define the details of the writing process.
 * This class organized the writing processes within the simulation pipeline.
 * 
 * To control the writing process use the call property:
 * <property name="call" type="Integer" value="32"/>
 * The value can be 0, a number or Integer.MAX_VALUE.
 * If the value is 0 data will be written in the pre-processing phase.
 * If the value is Integer.MAX_VALUE data will be written in the post-processing phase.
 * If the value is 0 < value < Integer.MAX_VALUE the data will be written every time-step
 * in which value % time-step = 0
 *  
 * Also, the class provides a buffer property:
 * <property name="buffer" type="Integer" value="500"/>
 * The buffer provides the number of read operation the system will collected
 * until sending the data to the output target. However, for call = 0 or Integer.MAX_VALUE
 * the buffer is always 1.
 * 
 * @author Peter M. Kielar
 *
 */
public class OutputWriter extends Callable implements IHasProperties, IGenericCallable {
	
	private final static String callName = "call";
	private final static String bufferName = "buffer";
	
	private int call = -1;
	private int buffer = 1;	
	private ArrayList<WriterData> writerContents = new ArrayList<>();
	
	protected PropertyBackPack properties = null;
	
	@Override
	public void setPropertyBackPack(PropertyBackPack propertyContainer) {

		this.properties = propertyContainer; 
	}
	
	@Override
	public PropertyBackPack getPropertyBackPack() {
		return properties;
	}
	
	protected WriterTarget writerTarget = null;
	
	public void setWriterTarget(WriterTarget writerTarget) {
		this.writerTarget = writerTarget;
	}
	
	protected WriterFormat writerFormat = null;

	public void setWriterFormat(WriterFormat writerFormat) {
		this.writerFormat = writerFormat;
	}

	protected WriterSource writerSource = null;
	
	public void setWriterSource(WriterSource writerSource) {
		this.writerSource = writerSource;
	}
	
	@Override
	public boolean isMultiThreading() {

		return false;
	}

	@Override
	public void callPreProcessing(SimulationState simulationState) {
		
		this.writerSource.initialize(simulationState);
		this.writerTarget.initialize(simulationState);
		this.writerFormat.initialize();
		
		this.call = this.properties.getIntegerProperty(callName);

		if(0 < this.call && this.call < Integer.MAX_VALUE) {
			
			if(this.properties.getIntegerProperty(bufferName) != null) {
				
				buffer = this.properties.getIntegerProperty(bufferName);
			}
		}
		
		LoggingManager.logDebug(MessageStrings.propertySetTo,
				callName,
				String.valueOf(call),
				this.getClass().getSimpleName());
		
		LoggingManager.logDebug(MessageStrings.propertySetTo,
				bufferName,
				String.valueOf(buffer),
				this.getClass().getSimpleName());
				
		if(this.call == 0) {
	
			LoggingManager.logDebug(this, "executes a pre-processing write operation");
			this.executeWrite();
			this.writerTarget.close();
		}
	}

	@Override
	public void execute(Collection<? extends Void> splittTask, SimulationState simulationState) {

		if(0 < this.call && this.call < Integer.MAX_VALUE) {
			
			if(simulationState.getCurrentTimeStep() % this.call == 0) {
			
				this.executeWrite();
			}
		}
	}

	@Override
	public void callPostProcessing(SimulationState simulationState) {
		
		if(this.call == Integer.MAX_VALUE) {
			
			LoggingManager.logDebug(this, "executes a post-processing write operation");
			this.executeWrite();
			this.writerTarget.close();
		}
		
		if(0 < this.call && this.call < Integer.MAX_VALUE) {
		
			WriterData resultsData = new WriterData();
			this.writerContents.forEach(dataItem -> resultsData.setData(dataItem.getData()));
			this.writerContents.clear();
			
			this.writerTarget.writeData(resultsData);
			this.writerTarget.close();
		}
	}
	
	private void executeWrite() {
		
		WriterData writerData = writerFormat.formatData(writerSource);	
		
		if(!writerData.isEmpty()) {
		
			this.writerContents.add(writerData);
		}
	
		if(writerContents.size() == this.buffer) {
			
			WriterData resultsData = new WriterData();
			writerContents.forEach(dataItem -> resultsData.setWriterData(dataItem));
			writerContents.clear();
			
			writerTarget.writeData(resultsData);
		}
	}
}
