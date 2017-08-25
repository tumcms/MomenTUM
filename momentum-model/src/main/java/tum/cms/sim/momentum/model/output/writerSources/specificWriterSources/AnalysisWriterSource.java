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

package tum.cms.sim.momentum.model.output.writerSources.specificWriterSources;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import tum.cms.sim.momentum.configuration.generic.FormatString;
import tum.cms.sim.momentum.configuration.model.output.WriterSourceConfiguration.OutputType;
import tum.cms.sim.momentum.data.analysis.AnalysisElement;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.analysis.AnalysisModel;
import tum.cms.sim.momentum.model.output.writerSources.genericWriterSources.SingleSetWriterSource;

public class AnalysisWriterSource extends SingleSetWriterSource {
	
	protected AnalysisModel analysisModel = null;

	public void setAnalysisModel(AnalysisModel analysisModel)  {
		
		this.analysisModel = analysisModel;
	}
	
//	/**
//	 * elementIterators.getKeys() - Data Types
//	 * elementIterators.getValues() - Iterators for time sorted data
//	 * 
//	 * Iterator - point to a list of AnalysisElement (each associated to a pedestrian and a data)
//	 * AnalysisElement - contains data for Data Type for a pedestrian
//	 */
//	private HashMap<String, Iterator<List<AnalysisElement>>> setDataMap =  new HashMap<>();
	
	/**
	 * Each HashMap refers to a data set of the current time step
	 * Map string is output type and data as number for each element in the set
	 */
	private Iterator<HashMap<String, Number>> currentSetIterator = null;
	
	/**
	 * First is the type and number is the data
	 * The currentItem changes for each loadSetItem
	 */
	private HashMap<String, Number> currentItem = null;
	
	@Override
	public void initialize(SimulationState simulationState) {
		
		this.dataItemNames.addAll(this.analysisModel.getOutputTypes());
	}
	
	@Override
	public String readSingleValue(String resultDataTypeName) {

		FormatString formatter = this.properties.getFormatProperty(resultDataTypeName);
		String format = formatter.getFormat();	
		Number item = this.currentItem.get(resultDataTypeName);

		return String.format(format, item);
	}

	@Override
	public void loadSet() {
		
		LinkedHashMap<String, HashMap<String, Number>> currentSet = new LinkedHashMap<>();
		
		// initialize all references to the sets firsts
		
		// The output may have multiple similar structured output elements
		// E.g. similar to pedestrian's data.
		// The for each provides a list of data elements.
	
		this.analysisModel.getOutputDataMap().forEach((outputType, outputData) -> {

			// outputData comprises all data of a single type for each identification (e.g. pedestrian)
			
			// dataElementsIterator is the data of a single identification for a type (e.g. pedestrian.xPosition)
			Iterator<AnalysisElement> dataElementsIterator = outputData.getObjectOrderedData().iterator();

			while(dataElementsIterator.hasNext()) {
				
				AnalysisElement analysisElement = dataElementsIterator.next();
				
				if(!currentSet.containsKey(analysisElement.getId())) {
					
					currentSet.put(analysisElement.getId(), new HashMap<>());
					
					if(this.analysisModel.getOutputDataMap().containsKey(OutputType.timeStep.name())) {
						
						currentSet.get(analysisElement.getId()).put(OutputType.timeStep.name(), analysisElement.getTimeStep());
					}
					
					if(this.analysisModel.getOutputDataMap().containsKey(OutputType.id.name())) {
						
						currentSet.get(analysisElement.getId()).put(OutputType.id.name(), Integer.parseInt(analysisElement.getId()));
					}
				}
				
				currentSet.get(analysisElement.getId()).put(outputType, analysisElement.getData());			
			}
		});
		
		// get the iterator for the output elements
		this.currentSetIterator = currentSet.values().iterator();
	}

	@Override
	public void loadSetItem() {

		this.currentItem = this.currentSetIterator.next();
	}
	
//	/**
//	 * For each time-step there is a set. After reading the set it will be empty for the current time-step.
//	 */
//	@Override
//	public boolean hasNextSet() {
//					
//	//		.forEach((outputType, outputData) -> 				
//	//			this.setDataMap.put(outputType, ));
//	//	
//	//		// each iterator points to a list order by time, each list is of the same size
//	//		// hence ask the first iterator if another set exists (another time step)
//	//		return this.setDataMap.size() > 0 ? this.setDataMap.values().stream().findFirst().get().hasNext() : false;
//			
//		if(currentTimeStep == this.timeStepController.getCurrentTimeStep()) {
//			
//			return false;
//		}
//		
//		currentTimeStep = this.timeStepController.getCurrentTimeStep();
//			
//		return true;
//	}

	@Override
	public boolean hasNextSetItem() {
		
		return this.currentSetIterator.hasNext();
	}
}
