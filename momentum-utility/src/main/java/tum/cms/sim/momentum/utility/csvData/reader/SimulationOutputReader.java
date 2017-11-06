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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import tum.cms.sim.momentum.configuration.model.output.WriterSourceConfiguration.OutputType;
import tum.cms.sim.momentum.utility.csvData.reader.BufferingStrategy;
import tum.cms.sim.momentum.utility.csvData.CsvType;

public class SimulationOutputReader {
	
	private HashMap<Double, SimulationOutputCluster> dataSetBuffer = null;
	private CsvReader dataSetReader = null;
	private int currentBufferSize = -1;
	private boolean clearBuffer = false;

	private double currentAsyncIndex = 0L;
	private Double bufferedSetIndexLeft = null;
	private Double bufferedSetIndexRight = null;
	private ThreadPoolExecutor workerPool = null;
	private AsyncDataSetReader worker = null;

	private double timeStepDuration = 0;
	private double endCluster = 0;
	private double endTime = 0;
	private String innerClusterSeparator = null; // OutputType.id.name();

	private String clusterSeparator = OutputType.timeStep.name();
	private CsvType csvType;
	private String filePathHash;

	public CsvReader getDataSetReader() {
		return dataSetReader;
	}

	public double getTimeStepDuration() {
		return timeStepDuration;
	}

	public double getEndTime() {
		return this.endTime;
	}

	public double getEndCluster() {
		return this.endCluster;
	}

	public void setInnerClusterSeparator(String innerSeparatr) {
		this.innerClusterSeparator = innerSeparatr;
	}

	public void setClusterSeparator(String clusterSeparator) {
		this.clusterSeparator = clusterSeparator;
	}

	public double getTimeStepDifference() {

		return this.dataSetReader.getTimeStepDifference();
	}

	public void cleanBuffer() {

		dataSetBuffer.forEach((cluster, data) -> dataSetBuffer.put(cluster, null));
	}

	public CsvType getCsvType() {
		return csvType;
	}
	
	public boolean isDataWithContent(Double index) {
		
		boolean isDataExistent = true;
		
		if (dataSetBuffer.get(index) == null || dataSetBuffer.get(index).isEmpty()) {
		
			isDataExistent = false;
		}

		return isDataExistent;
	}

	public String getFilePathHash() {
		return filePathHash;
	}

	private void setIndexContent(Long timeIndex, Long lineStart, Long pointer) {

		this.dataSetReader.putLineIndex(timeIndex, lineStart, pointer);
	}

	private void setBufferStrategie(BufferingStrategy bufferStrategie) {

		switch (bufferStrategie) {

		case AllBuffer:
			currentBufferSize = Integer.MAX_VALUE;
			break;
		case NoBuffer:
			currentBufferSize = 1;
			break;
		case FileBuffer:
			currentBufferSize = 200;
			clearBuffer = true;
			break;
		default:
			break;
		}
	}

	public SimulationOutputReader(CsvReader dataSetReader, String clusterSeparator, double endTime,
			double timeStepDuration, CsvType csvType) {

		this.clusterSeparator = clusterSeparator;
		this.dataSetReader = dataSetReader;
		this.endTime = endTime;
		this.timeStepDuration = timeStepDuration;
		this.csvType = csvType;
		this.filePathHash = generateFilePathHash();

		this.setBufferStrategie(csvType.getBufferingStrategy());
		this.initializeClusterMap(endTime, timeStepDuration);
	}

	private String generateFilePathHash() {
		SecureRandom random = new SecureRandom();
		String hash = new BigInteger(130, random).toString();
		try {
			byte[] bytesOfMessage  = getDataSetReader().getInputFilePath().getBytes("UTF-8");
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] theDigest = digest.digest(bytesOfMessage);
			hash = theDigest.toString();
		} catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		} 
		return "."+hash.substring(hash.length()-6, hash.length());
	}

	public boolean makeReadyForIndex(double index) throws Exception {

		boolean indexExists = false;
		
		this.asyncReadDataSet(index); // initialize loading the data
		
		// if the modulo index is not zero, the reader is not
		// compatible with the current timeStep
		if(index % this.dataSetReader.getTimeStepDifference() != 0.0) {
			
			// However, this means the reader is ready, because it has nothing to do
			// still we dislike this and interpolate to the closest (last index)
			// double interpolatedTimeStep = index - (index % this.dataSetReader.getTimeStepDifference());
			// still.. this is not a good solutions, actually:
			// Create more dataSetBuffer indices for the non existing time steps and link
			// the content of the neighboring data sets to the empty indices.
			indexExists = true;
		}
		else  { // here the reader is compatible with the index
			
			// check if the data is ready
			if(dataSetBuffer.get(index) == null || !dataSetBuffer.get(index).isReady() ) {
				
				// if not ready, data is being red, if null data does not exists
				indexExists = false;
			}
			else {
				
				indexExists = true;
			}
		}
		
		return indexExists;
	}

	public void clearBuffer(double index) {

		if (this.clearBuffer && index > -1L && bufferedSetIndexLeft != null && bufferedSetIndexRight != null) {

			double fromIndex = 0;
			double toIndex = this.getEndCluster();

			if (bufferedSetIndexLeft < index - (int) (currentBufferSize / 2) * getTimeStepDifference()) {

				fromIndex = bufferedSetIndexLeft;
				toIndex = index - ((int) (currentBufferSize / 2) - 1) * getTimeStepDifference();
				double iter = fromIndex;

				while (iter <= toIndex) {

					if (this.dataSetBuffer.get(iter) != null) {

						this.dataSetBuffer.get(iter).clearBuffer();
					}
					iter += getTimeStepDifference();
				}
			}

			if (bufferedSetIndexRight > index + currentBufferSize * getTimeStepDifference()) {

				fromIndex = index + (currentBufferSize + 1) * getTimeStepDifference();
				toIndex = bufferedSetIndexRight;
				double iter = fromIndex;

				while (iter <= toIndex) {

					SimulationOutputCluster current = this.dataSetBuffer.get(iter);

					if (current != null) {

						current.clearBuffer();
					}

					iter += getTimeStepDifference();
				}
			}
		}
	}

	public ArrayList<OutputType> getHeaderTypes() {

		return this.dataSetReader.getHeaderTypes();
	}

	public void readIndex(String indexAppendix) throws FileNotFoundException, IOException {

		if (new File(this.dataSetReader.getInputFilePath() + indexAppendix).exists()) {

			try (BufferedReader in = new BufferedReader(
					new FileReader(this.dataSetReader.getInputFilePath() + indexAppendix))) {

				String line = null;

				while ((line = in.readLine()) != null) {

					String[] content = line.split(";");
					
					if(content.length == 3) { // backward  compatibility
						
						this.setIndexContent(Long.parseLong(content[0]), Long.parseLong(content[1]),
								Long.parseLong(content[2]));
					}
					else {
						
						this.setIndexContent(Long.parseLong(content[0]), Long.parseLong(content[1]),
								Long.parseLong(content[3]));
					}
				}
			}
		}
		else {

			this.readStandardIndex();
		}
	}


	public SimulationOutputCluster asyncReadDataSet(double index) throws Exception {

		SimulationOutputCluster data = this.readDataSet(index);
		currentAsyncIndex = (long) index;
		return data;
	}

	public void startReadDataSetAsync() {

		this.bufferedSetIndexRight = 0d;
		this.bufferedSetIndexLeft = 0d;

		workerPool = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(1));

		worker = new AsyncDataSetReader();
		workerPool.execute(worker);
	}

	public void endReadDataSetAsync() throws Exception {

		if (workerPool != null) {

			worker.shutdown();
			workerPool.shutdown();
			workerPool = null;
			dataSetReader.close();
		}

		worker = null;
		bufferedSetIndexLeft = null;
		bufferedSetIndexRight = null;
	}

	public SimulationOutputCluster readDataSet(double index) throws Exception {

		if (dataSetBuffer.get(index) == null || !dataSetBuffer.get(index).isReady()) {

			Long lineIndex = this.dataSetReader.seekLineForCluster(clusterSeparator, index, this.dataSetBuffer);

			if (lineIndex >= 0) {

				this.loadRecords(index, lineIndex, 1);
			}
			else {

				dataSetBuffer.put(index, new SimulationOutputCluster(index));
			}
		}

		return dataSetBuffer.get(index);
	}

	private void asyncWorkerReadDataSet(double fromIndex, double toIndex) throws Exception {

		double iter = fromIndex;

		while (iter <= toIndex) {

			this.readDataSet(iter);
			iter += this.getTimeStepDifference();
		}
	}

	private void readStandardIndex() throws FileNotFoundException, IOException {

		Integer lineCounter = 0;
		Long timeStepOfLastLine = null;
		Long timeStepOfCurrentLine = null;

		if (new File(this.getDataSetReader().getInputFilePath()).exists()) {

			try (BufferedReader in = new BufferedReader(new FileReader(this.getDataSetReader().getInputFilePath()))) {

				String line = in.readLine(); // header

				long sizeOfTimeStepLines = line.length() + 2;
				
				while ((line = in.readLine()) != null) {

					String[] content = line.split(";");

					timeStepOfCurrentLine = Long.parseLong(content[0]);

					if (timeStepOfLastLine == null) {
						
						this.setIndexContent(Long.parseLong(content[0]), Long.parseLong(lineCounter.toString()),
								 sizeOfTimeStepLines);
					}

					if (timeStepOfLastLine != null && timeStepOfCurrentLine.compareTo(timeStepOfLastLine) != 0) {
						
						this.setIndexContent(Long.parseLong(content[0]), Long.parseLong(lineCounter.toString()),
								 sizeOfTimeStepLines);
					}

					sizeOfTimeStepLines += line.length() + 2;
					lineCounter++;
					timeStepOfLastLine = timeStepOfCurrentLine;
				}
			}
		}
	}
	
	private void loadRecords(double index, Long lineIndex, double buffer) throws Exception {

		SimulationOutputCluster dataSet = null;
		double currentIndex = -1;

		for (int iter = 0; iter < buffer; iter++) {

			dataSet = dataSetReader.readRecord(this.innerClusterSeparator, this.clusterSeparator, lineIndex);

			if (dataSet == null) { // end of file, dataSet not present

				if (currentIndex == -1) {
					currentIndex = index;
				}
				// Fill empty till end Steps
				while (currentIndex < this.getEndCluster()) {

					dataSetBuffer.put(currentIndex, new SimulationOutputCluster(currentIndex));
					currentIndex = currentIndex + this.getTimeStepDifference();
				}

				break;
			}

			currentIndex = dataSet.getIndex();

			if (index < currentIndex) {

				// Fill empty in between steps and before
				while (index < currentIndex) {

					if (!dataSetBuffer.containsKey(index) || dataSetBuffer.get(index) == null
							|| !dataSetBuffer.get(index).isReady()) {

						dataSetBuffer.put(index, new SimulationOutputCluster(index));
					}
					index = index + this.getTimeStepDifference();
				}
			}

			dataSetBuffer.put(currentIndex, dataSet);
			
			currentIndex = currentIndex + this.getTimeStepDifference();
			index = currentIndex;
			lineIndex = dataSet.getClusterStartLine() + dataSet.getClusterLines();
		}
	}

	private void initializeClusterMap(double endTime, double timeStepDuration) {

		double delta = this.dataSetReader.getTimeStepDifference();
		endCluster = endTime / timeStepDuration;
		double clusters = (int) (endCluster / delta);

		Double index = -1d;
		dataSetBuffer = new HashMap<Double, SimulationOutputCluster>();

		while (clusters > -1 * timeStepDuration) {

			index = delta * clusters;
			dataSetBuffer.put(index, null);
			clusters -= 1.0;
		}
	}

	private class AsyncDataSetReader implements Runnable {

		private double lastReadIndex = -1L;

		private boolean shutDownDemanded = false;

		public void shutdown() {

			this.shutDownDemanded = true;
		}

		@Override
		public void run() {

			while (!this.shutDownDemanded) {

				double fromIndex = 0;
				double toIndex = SimulationOutputReader.this.getEndCluster();

				if (this.lastReadIndex != SimulationOutputReader.this.currentAsyncIndex) {

					SimulationOutputReader.this.clearBuffer(this.lastReadIndex);

					this.lastReadIndex = SimulationOutputReader.this.currentAsyncIndex;

					if (this.lastReadIndex
							- (int) (currentBufferSize / 2) < SimulationOutputReader.this.bufferedSetIndexLeft) {

						// load from buffer fill/2 to last filled index
						if (this.lastReadIndex - (int) (currentBufferSize / 2) > 0) {

							fromIndex = this.lastReadIndex - (int) (currentBufferSize / 2)
									* SimulationOutputReader.this.getTimeStepDifference();
						}

						toIndex = SimulationOutputReader.this.bufferedSetIndexLeft
								- SimulationOutputReader.this.getTimeStepDifference();

						try {

							if (toIndex > 0 && fromIndex > 0) {

								SimulationOutputReader.this.asyncWorkerReadDataSet(fromIndex, toIndex);
								bufferedSetIndexLeft = fromIndex;
							}
						} catch (Exception e) {

							e.printStackTrace();
						}
					}

					if (this.lastReadIndex + currentBufferSize > SimulationOutputReader.this.bufferedSetIndexRight) {

						fromIndex = SimulationOutputReader.this.bufferedSetIndexRight
								+ SimulationOutputReader.this.getTimeStepDifference();
						toIndex = this.lastReadIndex
								+ currentBufferSize * SimulationOutputReader.this.getTimeStepDifference();

						if (toIndex > SimulationOutputReader.this.getEndCluster()) {

							toIndex = SimulationOutputReader.this.getEndCluster();
						}

						try {

							SimulationOutputReader.this.asyncWorkerReadDataSet(fromIndex, toIndex);
							bufferedSetIndexRight = toIndex;
						} catch (Exception e) {

							e.printStackTrace();
						}
					}
				}

				try {

					Thread.sleep(50L);
				} catch (InterruptedException e) {

					e.printStackTrace();
				}
			}
		}
	}

}