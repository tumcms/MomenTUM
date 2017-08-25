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

package tum.cms.sim.momentum.utility.csvData.reader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import tum.cms.sim.momentum.configuration.model.output.WriterSourceConfiguration.OutputType;

public class CsvReader {

	private File inputfile = null;
	
	public double getInputFileSizeInMegaByte() {
		return inputfile.length() / 1000000.0;
	}

	public String getInputFilePath() {
		return inputfile.getAbsolutePath();
	}
	
	private CSVFormat format = null;

	private RandomAccesLineNumberReader lineReader = null;

	private Double timeStepDifference = -1d;

	public double getTimeStepDifference() {
		return timeStepDifference;
	}

	private ArrayList<OutputType> headerTypes = null;
	private ArrayList<String> headerStrings = null;
	
	public ArrayList<OutputType> getHeaderTypes() {	
		return headerTypes;
	}

	public void putLineIndex(Long timeIndex, Long lineStart, Long fileIndex) {

		this.lineReader.putLineIndexer(timeIndex, lineStart, fileIndex);
	}
	
	public CsvReader(String simulationOutputFileName, String dataClusterHeader, String delimiter) throws Exception {
	
		this.inputfile = new File(simulationOutputFileName);
		this.format = CSVFormat.newFormat(delimiter.charAt(0)).withRecordSeparator(System.lineSeparator());
		this.lineReader = new RandomAccesLineNumberReader(this.inputfile);
		
		this.headerStrings = this.createHeader(this.inputfile, this.format);
		String[] headerArray = new String[headerStrings.size()];
		
		this.format = this.format.withHeader(headerStrings.toArray(headerArray));
		this.createTimeStepDelta(this.inputfile, this.format, dataClusterHeader);
		this.lineReader.setCurrentLineIndex(1L);
	}

	public void close() throws Exception {
		
		this.lineReader.close();
	}
	
	private CSVRecord readRecord(String contentLine, CSVFormat format) throws IOException {
		
		return CSVParser.parse(contentLine, format).getRecords().get(0);
	}
	
	private ArrayList<String> createHeader(File inputfile, CSVFormat nonHeaderFormat) throws Exception {
		
		ArrayList<String> headerStrings = new ArrayList<String>();
		
		CSVRecord headerRecord = this.readRecord(lineReader.readLine(), nonHeaderFormat);
		Iterator<String> entryIterator = headerRecord.iterator();
		String entry = null;
		
		while(entryIterator.hasNext()) {
			
			entry = entryIterator.next(); 
			headerStrings.add(entry);
		}
		
		return headerStrings;
	}

	private void createTimeStepDelta(File inputfile, CSVFormat format, String dataClusterHeader) throws Exception {
				
		CSVRecord currentRecord = null; 
		String lineContent = null;
		String firstCluster = null;
		
		while((lineContent = lineReader.readLine()) != null) { 
			
			currentRecord = this.readRecord(lineContent, format);
			
			if(firstCluster == null) {
				
				firstCluster = currentRecord.get(dataClusterHeader);
			}	
			
			if(!currentRecord.get(dataClusterHeader).equals(firstCluster)) {
				
				timeStepDifference = Double.parseDouble(currentRecord.get(dataClusterHeader)) 
						- Double.parseDouble(firstCluster);
				break;
			}
		}	
	}

	public SimulationOutputCluster readRecord(String identificationHeader,
			String dataClusterHeader,
			Long clusterStartLineIndex) throws Exception {

		Long currentLine = lineReader.getCurrentLineIndex();
	
		// go to target line
		if(clusterStartLineIndex != currentLine) {
			
			lineReader.setCurrentLineIndex(clusterStartLineIndex);
		}
	
//		if(clusterStartLineIndex == 0) {
//			
//			// waste header
//			clusterStartLineIndex++;
//			String header = lineReader.readLine();
//		}
		
		String currentLineContent = null;
		CSVRecord currentRecord = null;
		String currentIndex = null;
	
		ArrayList<CSVRecord> dataStepList = new ArrayList<CSVRecord>();

		while(true) {

			currentLineContent = lineReader.readLine();

			if(currentLineContent == null || currentLineContent.isEmpty()) { // end of file, stop
				
				return null;
			}

			currentRecord = this.readRecord(currentLineContent, this.format);

			if(currentIndex == null) { // remember cluster index
			
				currentIndex = currentRecord.get(dataClusterHeader);
			}
			
			if(!currentRecord.get(dataClusterHeader).equals(currentIndex)) { // header index changed
			
				// go one line back, reading has to stop due to cluster identification change
				lineReader.setCurrentLineIndex(lineReader.getCurrentLineIndex() - 1);
				break; // and stop
			}
		//	System.out.println(currentRecord);
			dataStepList.add(currentRecord);
		}
		
		SimulationOutputCluster newCluster = new SimulationOutputCluster(identificationHeader,
				clusterStartLineIndex,
				Double.parseDouble(currentIndex),
				dataStepList,
				lineReader.getPointer(),
				headerStrings);

		return newCluster;
	}

	public Long seekLineForCluster(String clusterSeparator,  
			double targetIndex, 
			HashMap<Double, SimulationOutputCluster> dataSetBuffer) throws Exception {

		Long lineNumber = lineReader.getLineNumber((long)targetIndex); // found or 0
	
		if(lineNumber == null) {
			
			//return 0L;
			return -1L;
		}
		
		if(!lineReader.hasReadLine(lineNumber)) {
		
			lineReader.setCurrentLineIndex(0L);

			String currentLineContent = null;
			String currentIndex = null;
			
			CSVRecord currentRecord = null;
			Long items = 0L;
			
			while(true) {
				
				currentLineContent = lineReader.readLine();
				
				if(currentLineContent == null || currentLineContent.isEmpty()) { // end of file, stop
					
					return -1L;
				}
				
				currentRecord = this.readRecord(currentLineContent, this.format);
				items++;
				
				if(currentIndex == null) { // remember cluster index
				
					currentIndex = currentRecord.get(clusterSeparator);
				}
				
				if(!currentRecord.get(clusterSeparator).equals(currentIndex)) { // header index changed
				
					currentIndex = currentRecord.get(clusterSeparator);
					lineNumber = lineReader.getCurrentLineIndex() - 1;
					double currentIndexValue = Double.parseDouble(currentIndex);
					
					dataSetBuffer.put(currentIndexValue, new SimulationOutputCluster(currentIndexValue,
							lineNumber,
							items,
							lineReader.getPointer()));			
		
					if(currentIndexValue >= targetIndex) {
							
						lineReader.setCurrentLineIndex(lineReader.getCurrentLineIndex() - 1);
						break;
					}
					
					items = 0L;
				}
			}
		}
		else {
			
			lineReader.setCurrentLineIndex(lineNumber);
		}
		
 		return lineNumber;
	}
}
