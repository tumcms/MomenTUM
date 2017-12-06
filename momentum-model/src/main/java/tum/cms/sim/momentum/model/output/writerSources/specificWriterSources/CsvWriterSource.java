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

import java.util.Iterator;

import tum.cms.sim.momentum.configuration.model.output.WriterSourceConfiguration;
import tum.cms.sim.momentum.configuration.model.output.WriterSourceConfiguration.OutputType;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.infrastructure.logging.LoggingManager;
import tum.cms.sim.momentum.model.output.writerSources.genericWriterSources.SingleSetWriterSource;
import tum.cms.sim.momentum.utility.csvData.CsvType;
import tum.cms.sim.momentum.utility.csvData.reader.CsvReader;
import tum.cms.sim.momentum.utility.csvData.reader.SimulationOutputCluster;
import tum.cms.sim.momentum.utility.csvData.reader.SimulationOutputReader;

/**
 * This writer source provides csv based data from a file.
 * Such data is mostly used for analyzing data of a simulation,
 * which was written to a file target by a csv format output writer.
 * 
 * @author Peter M. Kielar
 *
 */
public class CsvWriterSource extends SingleSetWriterSource {

	private final static String csvFileName = "csvFile";
	private final static String delimiterName = "delimiter";
	
	private SimulationOutputReader outputReader = null;
	private SimulationOutputCluster currentCluster = null;
	private Iterator<String> identificationIterator = null;
	private String currentIdentification = null;
	
	@Override
	public void initialize(SimulationState simulationState) {
		
		String csvFile = this.properties.getStringProperty(csvFileName);
		String delimiter = this.properties.getStringProperty(delimiterName);
		
		try {
			
			CsvReader csvReader = new CsvReader(csvFile, OutputType.timeStep.name(), delimiter);
			this.outputReader = new SimulationOutputReader(csvReader,
					OutputType.timeStep.name(), 
					simulationState.getSimulationEndTime(),
					simulationState.getTimeStepDuration(),
					CsvType.Pedestrian);
			
			this.outputReader.setInnerClusterSeparator("id");
			this.outputReader.readIndex(WriterSourceConfiguration.indexString);
		} 
		catch (Exception exception) {
	
			LoggingManager.logUser(this, exception);
		}
		
		// set dataItemNames
		this.outputReader.getHeaderTypes().forEach(header -> this.dataItemNames.add(header.name()));
	}
	
	@Override
	public String readSingleValue(String outputTypeName) {
		
		return this.currentCluster.getStringData(this.currentIdentification, outputTypeName);
	}

	@Override
	public void loadSetItem() {

		this.currentIdentification = this.identificationIterator.next();
	}

	@Override
	public boolean hasNextSetItem() {
		
		if(this.currentCluster != null && this.identificationIterator.hasNext()) {

			return true;
		}
		
		return false;
	}

	@Override
	public void loadSet() {
		
		long currentTimeStep = this.timeManager.getCurrentTimeStep();
		
		// delete last data set to keep the memory clean
		this.outputReader.cleanBuffer();

		try {
			
			this.currentCluster = this.outputReader.readDataSet(currentTimeStep);
			this.identificationIterator = currentCluster.getIdentifications().iterator();
			this.currentIdentification = null;
		}
		catch (Exception exception) {
			
			LoggingManager.logUser(this, exception);
			this.currentCluster = null;
		}
	}
}
