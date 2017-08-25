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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.csv.CSVRecord;

public class SimulationOutputCluster {

	private HashSet<String> header = new HashSet<String>();
	private LinkedHashMap<String, Map<String, String>> dataStep = null;

	private Long clusterStartLine  = 0L;
	private Long clusterLines = 0L;
	private double index = 0;
	private boolean ready = false;
	private Long fileIndex = -1L;
	
	
	public LinkedHashMap<String, Map<String, String>> getDataStep() {
		return dataStep;
	}
	
	public long getFileIndex() {
		return fileIndex;
	}

	public boolean isReady() {
		return ready;
	}

	public Long getClusterStartLine() {
		return clusterStartLine;
	}

	public Long getClusterLines() {
		return clusterLines;
	}
	
	public double getIndex() {
		return index;
	}
	
	public SimulationOutputCluster(double index) {
		
		this.ready = true;
		this.index = index;
		this.clusterLines = -1L;
		this.clusterStartLine = -1L;
	}
	
	public SimulationOutputCluster(double index, Long clusterStartLine, Long clusterLineSize, Long fileIndex) {
	
		this.fileIndex = fileIndex;
		this.ready = false;
		this.index = index;
		this.clusterStartLine = clusterStartLine;
		this.clusterLines  = clusterLineSize;
	}
	
	public SimulationOutputCluster(String identificationHeaderName,
			Long clusterStartLine,
			double index,
			ArrayList<CSVRecord> clusteredRecords,
			Long fileIndex,
			ArrayList<String> header) {

		this.fileIndex = fileIndex;
		this.ready = true;
		this.index = index;
		this.clusterStartLine = clusterStartLine;
		this.clusterLines = new Long(clusteredRecords.size());
		dataStep = new LinkedHashMap<String, Map<String, String>>();
		this.header.addAll(header);
		clusteredRecords.forEach(record -> dataStep.put(record.get(identificationHeaderName), record.toMap()));
	}
	
	public void setRecords(String identificationHeaderName, ArrayList<CSVRecord> clusteredRecords) {
		
		this.ready = true;
		clusteredRecords.forEach(record -> dataStep.put(record.get(identificationHeaderName), record.toMap()));
	}
	
	public void clearBuffer() {
		
		this.ready = false;
		dataStep = null;
	}
	
	public boolean isEmpty() {
		return dataStep == null ? true : false;
	}
	
	public boolean containsIdentification(String id) {
		return this.dataStep.containsKey(id);
	}
	
	public Set<String> getIdentifications() {
		
		return this.dataStep.keySet();
	}

	public Double getDoubleData(String innerClusterIdentifcation, String item) {
		
		if(this.dataStep == null || !this.header.contains(item)) {
			return null;
		}
		
		return Double.parseDouble(this.dataStep.get(innerClusterIdentifcation).get(item));
	}
	
	public Integer getIntegerData(String innerClusterIdentifcation, String item) {
		
		if(this.dataStep == null || !this.header.contains(item)) {
			
			return null;
		}
		
		return Integer.parseInt(this.dataStep.get(innerClusterIdentifcation).get(item));
	}

	public Boolean getBooleanData(String innerClusterIdentifcation, String item) {
	
		if(this.dataStep == null || !this.header.contains(item)) {
			
			return null;
		}
		
		return Boolean.parseBoolean(this.dataStep.get(innerClusterIdentifcation).get(item));
	}

	public String getStringData(String innerClusterIdentifcation, String item) {
		
		if(this.dataStep == null || !this.header.contains(item)) {
			
			return null;
		}
		
		return this.dataStep.get(innerClusterIdentifcation).get(item);
	}
}
