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

package tum.cms.sim.momentum.model.output.writerFormats;

import tum.cms.sim.momentum.data.output.WriterData;
import tum.cms.sim.momentum.model.output.writerSources.WriterSource;

/**
 * This class formats data into csv including header and if needed
 * an index format including a index data, a pointer and a line number for each data set. 
 * 
 * This class provides a delimiter property:
 * <property name="delimiter" type="String" value=";"/>
 * This is the separating string between data elements in the csv.
 *   
 * To write an index property:
 * <property name="index" type="boolean" value="True"/> 
 * This defines if index information should be created alongside the data.
 * This is mostly used in combination with the FileWriter index property.
 * 
 * @author Peter M. Kielar
 *
 */
public class CsvWriterFormat extends WriterFormat {

	private static final String delimiterName = "delimiter";
	private static final String indexName = "index";
	
	/**
	 * CSV data delimiter from configuration.
	 */
	private String delimiter = null;
	
	/**
	 * Indicates if a index should be generated.
	 */
	private boolean writeIndex = false;
	
	/**
	 * Stores the index string.
	 * 
	 * Data for index mechanism, stores the last read index information.
	 * Long timeIndex, Long lineStart, Long pointer
	 */
	private StringBuilder indexBuilder = null; 
	
	/**
	 * Identification of a data cluster
	 */
	private String index = null; 
	
	/**
	 * Line in result string of cluster start, because of the header it starts with 1.
	 */
	private Long line = 1l; 
	
	/**
	 * Char in result string of cluster start
	 */
	private Long setPointer = 0l ;
	
	/**
	 * The first time data is formated, append the header!
	 */
	private boolean headerFormated = false;
	
	private static String timeStepIndicatorName = "timeStep";
	
	private String timeStepIndicator = "timeStep";
	
	@Override
	public void initialize() {

		this.delimiter = this.properties.getStringProperty(delimiterName);
		
		if(this.properties.getBooleanProperty(indexName) != null) {
		
			this.writeIndex = this.properties.getBooleanProperty(indexName);
		}
		
		if(this.properties.getBooleanProperty(timeStepIndicatorName) != null) {
			
			this.timeStepIndicator = this.properties.getStringProperty(timeStepIndicatorName);
		}
	}

	@Override
	public WriterData formatData(WriterSource writerSource) {
		
		WriterData writerData = new WriterData();
		StringBuilder csvDataBuilder = new StringBuilder();
		
		if(!headerFormated) {
			
			headerFormated = true;
			this.appendHeaderData(writerSource, csvDataBuilder);
		}
		
		indexBuilder = new StringBuilder();
		
		while(writerSource.hasNextSet()) {
			
			writerSource.loadSet();
			
			String indexItem = null;
			long currentSetSize = 0L;
			long currentSetLines = 0L;
			
			while(writerSource.hasNextSetItem()) {
				
				writerSource.loadSetItem();
				
				for(int iter = 0; iter < writerSource.getDataItemNames().size(); iter++) {
					
					String itemType = writerSource.getDataItemNames().get(iter);
					String itemContent = writerSource.readSingleValue(itemType);
					
					if(itemType != null && itemContent != null) {
				
						currentSetSize += itemContent.length();
						csvDataBuilder.append(itemContent);
						
						// get "timeStep" element identifies a cluster
						if(itemType.equals(this.timeStepIndicator)) {
							
							indexItem = itemContent;
						}
						
						// after each element (except the last) add delimiter
						if(iter + 1 < writerSource.getDataItemNames().size()) {
							
							csvDataBuilder.append(this.delimiter);
							currentSetSize += delimiter.length();		
						}
						else { // After the last element of the line add a newline
							
							csvDataBuilder.append(System.lineSeparator());
							
							 // Length of newline
							currentSetSize += System.lineSeparator().length();
							currentSetLines++; // increase number of csv lines
						}
					}
				}	
			}
			
			if(this.writeIndex && indexItem != null) { // Generate index
				
				this.index = indexItem; // The index for the given cluster index
				
				// write the last index content to the indexBuilder
				this.indexBuilder.append(String.valueOf(this.index));
				this.indexBuilder.append(";");
				this.indexBuilder.append(String.valueOf(this.line));
				this.indexBuilder.append(";");
				this.indexBuilder.append(String.valueOf(this.setPointer));
				this.indexBuilder.append(System.lineSeparator());
				
				// Update index for next round
				this.line += currentSetLines; // Line where the next set starts
				this.setPointer += currentSetSize; // Char where the next set starts
			}	
		}
		
		if(csvDataBuilder.length() > 0) {
		
			writerData.setData(csvDataBuilder.toString());
		}
		
		if(this.writeIndex && this.indexBuilder.length() > 0) {

			writerData.setIndex(this.indexBuilder.toString());	
		}
		
		return writerData;
	}

	private void appendHeaderData(WriterSource writerSource, StringBuilder csvDataBuilder) {

		// Initialize the setPointer start with header size
		writerSource.getDataItemNames().stream().forEach(header -> setPointer += header.length());
		setPointer += (writerSource.getDataItemNames().size() - 1) * delimiter.length();
		setPointer += System.lineSeparator().length();
		this.line++;
		
		// Save header data
		for(int iter = 0; iter < writerSource.getDataItemNames().size(); iter++) {
			
			String itemType = writerSource.getDataItemNames().get(iter);
			
			if(itemType != null) {
		
				csvDataBuilder.append(itemType);
				
				// after each element (except the last) add delimiter
				if(iter + 1 < writerSource.getDataItemNames().size()) {
					
					csvDataBuilder.append(this.delimiter);
				}
				else { // After the last element of the line add a newline
					
					csvDataBuilder.append(System.lineSeparator());
				}
			}
		}
	}
}
