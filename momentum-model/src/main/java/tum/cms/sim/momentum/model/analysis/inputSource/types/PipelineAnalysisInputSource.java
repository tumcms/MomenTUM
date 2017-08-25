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

/**
 * This class handles the pipelining of analysis models.
 * 
 * ATM not implemented but will be if analysis pipelines are needed
 * @author Kielar
 *
 */
public class PipelineAnalysisInputSource {
//extends AnalysisInputSource {
//
//	private List<String> analysisDoubleTypes = null;
//	private List<String> analysisIntegerTypes = null;
//			
//	public PipelineAnalysisInputSource(List<String> analysisDoubleTypes,
//			List<String> analysisIntegerTypes) {
//		
//		for(String outputType : analysisDoubleTypes) {
//			
//			dataHandler.getDataMap().put(outputType, new DataElementSet<Number>());
//		}
//		
//		for(String outputType : analysisIntegerTypes) {
//			
//			dataHandler.getDataMap().put(outputType, new DataElementSet<Number>());
//		}
//		
//		this.analysisDoubleTypes = analysisDoubleTypes;
//		this.analysisIntegerTypes = analysisIntegerTypes;	
//	}
//	
//	@Override
//	public long readNextDataSet() {
//		
//		long timeStep = this.
//		
//		// if the source is a other pipeline, readNextDataSet have to access 
////		if(this.analysisSource instanceof PipelineAnalysisInputSource) {
////			
////			
////		}
////		else { // the underlying an
////			
////			this.analysisSource.readNextDataSet();
////			
////			for(String doubleType : this.analysisDoubleTypes) {
////				
////				Map<String, Double> doubleDataMap = this.analysisSource.getDoubleData(doubleType);
////				
////				for(Entry<String, Double> data : doubleDataMap.entrySet()) {
////					
////					this.resultDataMap.get(data.getKey()).addResultElement(
////							new DataElement(data.getKey(),
////								data.getValue(), 
////								timeStep));
////				}
////			}
////			
////			for(String integerType : this.analysisIntegerTypes) {
////				
////				Map<String, Integer> integerTypeDataMap = this.analysisSource.getIntegerData(integerType);
////				
////				for(Entry<String, Integer> data : integerTypeDataMap.entrySet()) {
////					
////					this.resultDataMap.get(data.getKey()).addResultElement(
////							new DataElement(data.getKey(),
////								data.getValue(), 
////								timeStep));
////				}
////			}
////		}
//		
//		return timeStep;
//	}
//
//	@Override
//	public Map<String, Double> getDoubleData(String dataType) {
//		
//		DataElementSet<Number> elementSet = resultDataMap.get(dataType);
//		
//		if(!this.resultAcessIterator.containsKey(dataType)) {
//			
//			this.resultAcessIterator.put(dataType, elementSet.getTimeOrderedResultIterator());
//		}
//
//		HashMap<String, Double> dataMap = new HashMap<>();
//		
//		this.resultAcessIterator.get(dataType)
//			.next()
//			.forEach((dataElement) -> dataMap.put(dataElement.getPedestrianId(), dataElement.getData().doubleValue()));
//		
//		return dataMap;
//	}
//
//	@Override
//	public Map<String, Integer> getIntegerData(String dataType) {
//		
//		DataElementSet<Number> elementSet = resultDataMap.get(dataType);
//		
//		if(!this.resultAcessIterator.containsKey(dataType)) {
//			
//			this.resultAcessIterator.put(dataType, elementSet.getTimeOrderedResultIterator());
//		}
//
//		HashMap<String, Integer> dataMap = new HashMap<>();
//		
//		this.resultAcessIterator.get(dataType)
//			.next()
//			.forEach((dataElement) -> dataMap.put(dataElement.getPedestrianId(), dataElement.getData().intValue()));
//		
//		return dataMap;
//	}
//
//	@Override
//	public Set<String> getPedestrianIds() {
//		
//		// all sub DataElementSet objects comprise the same pedestrians
//		return this.resultDataMap.values().stream().findFirst().get().getPedestrianIds();
//	}
}
