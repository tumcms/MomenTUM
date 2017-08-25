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

public class SimulationAnalysisInputSource extends AnalysisInputSource {
	
//	private int setSize = 0;
//	@Override
//	public long readNextDataSet() {
//
//		return Integer.parseInt(this.writerSource.readSingleValue(OutputType.timeStep.name()));
//	}
//
//	@Override
//	public AnalysisElementSet getData(String dataType, long timeStep) {
//		
//		AnalysisElementSet dataElementSet = new AnalysisElementSet();
//		
//		String pedestrianId = null;
//		
//		// nothing to do, the writer source is controlled by the running simulation
//		// Hence, the next data set is present if the next simulation step is ready
//		this.setSize = this.writerSource.loadSet();
//		
//		if(this.setSize > 0 ) {
//			
//			while(this.setSize-- > 0) {
//				
//				this.writerSource.loadSetItem();
//							
//				pedestrianId = this.writerSource.readSingleValue(OutputType.id.name());
//				
//				if(AnalysisType.analysisDoubleTypes.contains(dataType)) {
//	
//					dataElementSet.addElement(
//							new AnalysisElement(pedestrianId, 
//									Double.parseDouble(this.writerSource.readSingleValue(dataType)), 
//									timeStep));
//	
//				}
//				else {
//					
//					dataElementSet.addElement(
//							new AnalysisElement(pedestrianId, 
//									Integer.parseInt(this.writerSource.readSingleValue(dataType)), 
//									timeStep));
//				}
//			}
//	
//		}
//		else {
//			
//			dataElementSet = null;
//		}
//		
//		return dataElementSet;
//	}
//
//	/**
//	 * Always true because the simulation will stop the analysis automatically.
//	 */
//	@Override
//	public boolean hasNextDataSet() {
//		
//		return true;
//	}
//	
//	@Override
//	protected Set<String> getPedestrianIds() {
//		
//		Set<String> result = new HashSet<String>();
//		int setSize = this.writerSource.loadSet();
//		
//		if(setSize-- > 0 ) {
//			
//			while(setSize > 0) {
//				
//				result.add(this.writerSource.readSingleValue(OutputType.id.name()));
//			}	
//		}
//		else {
//			
//			result = null;
//		}
//	
//		return result;
//	}
}
