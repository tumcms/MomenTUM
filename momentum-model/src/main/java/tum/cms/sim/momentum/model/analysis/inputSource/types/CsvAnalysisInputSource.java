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

package tum.cms.sim.momentum.model.analysis.inputSource.types;

import tum.cms.sim.momentum.model.analysis.inputSource.AnalysisInputSource;

public class CsvAnalysisInputSource extends AnalysisInputSource {
	
//	private final static String csvFileName = "csvFileName";
//	private final static String delimiterName = "delimiter";
//	private final static String bufferSizeName = "bufferSize";
//	
//	private int bufferSize = 100;
//	private int readItems = 0;
//	
//	private SimulationOutputReader outputReader = null;
//	private SimulationOutputCluster currentCluster = null;

//	@Override
//	public void loadConfiguration(AnalysisInputSourceConfiguration configuration) {
//		
//		super.loadConfiguration(configuration);
//	}	
	
//	private void loadCvs() {
//		
//		String csvFile = this.properties.getStringProperty(csvFileName);
//		String delimiter = this.properties.getStringProperty(delimiterName);
//		
//		if(this.properties.getIntegerProperty(bufferSizeName) != null) {
//			
//			bufferSize = this.properties.getIntegerProperty(bufferSizeName);
//		}
//		
//		try {
//			
//			CsvReader csvReader = new CsvReader(csvFile, OutputType.timeStep.name(), delimiter);
//			this.outputReader = new SimulationOutputReader(csvReader,
//					OutputType.timeStep.name(), 
//					this.simulationEndTime,
//					this.timeStepDuration,
//					BufferingStratgie.NoBuffer);
//			
//			this.outputReader.setInnerClusterSeparator("id");
//			this.outputReader.readIndex(WriterSourceConfiguration.indexString);
//		} 
//		catch (Exception exception) {
//	
//			LoggingManager.logUser(this, exception);
//		}
//	}
	
//	@Override
//	public long readNextDataSet() {
//		
//		long currentTimeStep = this.currentTimeStep;
//		
//		if(this.outputReader == null) {
//			
//			this.loadCvs();
//		}
//	
//		if(readItems++ >= bufferSize){
//			
//			readItems = 0 ;
//			this.outputReader.cleanBuffer();
//		}
//		
//		try {
//			
//			currentCluster = this.outputReader.readDataSet(currentTimeStep);
//		}
//		catch (Exception e) {
//			
//			e.printStackTrace();
//			currentCluster = null;
//		}
//		
//		if(readItems++ >= bufferSize){
//			
//			readItems = 0 ;
//			this.outputReader.cleanBuffer();
//		}
//		
//		this.currentTimeStep += this.outputReader.getTimeStepDifference();
//		
//		return currentTimeStep;
//	}

//	@Override
//	public AnalysisElementSet getData(String dataType, long timeStep) {
//		
//		AnalysisElementSet dataElementSet = new AnalysisElementSet();
//		
//		for(String pedestrianId : this.getPedestrianIds()) {
//			
//			if(!this.currentCluster.isEmpty()) {
//				
//				if(AnalysisType.analysisDoubleTypes.contains(dataType)) {
//				
//					dataElementSet.addElement(
//							new AnalysisElement(pedestrianId, 
//									this.currentCluster.getDoubleData(pedestrianId, dataType), 
//									timeStep));
//	
//				}
//				else {
//					
//					dataElementSet.addElement(
//							new AnalysisElement(pedestrianId, 
//									this.currentCluster.getIntegerData(pedestrianId, dataType), 
//									timeStep));
//				}
//			}
//			else {
//				
//				dataElementSet = null;
//			}
//		}
//		
//		return dataElementSet;
//	}

//	@Override
//	public boolean hasNextDataSet() {
//		
//		if(this.outputReader == null) {
//			
//			this.loadCvs();
//		}
//		
//		return this.currentTimeStep * this.outputReader.getTimeStepDuration() < this.outputReader.getEndTime();
//	}
	
//	@Override
//	protected Set<String> getPedestrianIds() {
//		
//		Set<String> result = null;
//	
//		if(this.currentCluster != null && !this.currentCluster.isEmpty()) {
//			
//			result = this.currentCluster.getIdentifications();
//		}
//		else {
//			
//			result = Collections.emptySet();
//		}
//		
//		return result;
//	}
}
