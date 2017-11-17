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

package tum.cms.sim.momentum.model.output.writerTargets;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel.MapMode;

import tum.cms.sim.momentum.configuration.model.output.WriterSourceConfiguration;
import tum.cms.sim.momentum.data.output.WriterData;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.infrastructure.logging.LoggingManager;

/**
 * This class writes data to the file system. 
 * It uses random access files and memory mapped files
 * to write as fast as possible with java.
 * 
 * The class is configurable via properties.
 * 
 * To write an index file of the output to fast access later on:
 * <property name="index" type="boolean" value="True"/> 
 * 
 * Define the file path and name:
 * <property name="file" type="File" value="yourPathToTheFile"/>
 * 
 * @author Peter M. Kielar
 *
 */
public class FileWriterTarget extends WriterTarget {
	
	private final static String indexName = "index";
	private final static String fileName = "file";
	
	private RandomAccessFile indexFile = null;
	private RandomAccessFile outputFile = null;
	
	@Override
	public void initialize(SimulationState simulationState) {
		
		File rawOutputFile = this.properties.getFileProperty(fileName);
		File rawIndexFile = null;
		
		if(this.properties.getBooleanProperty(indexName) != null) {
			
			if(this.properties.getBooleanProperty(indexName)) {
				
				rawIndexFile = new File(rawOutputFile.getAbsolutePath() + WriterSourceConfiguration.indexString);
			}
		}
			
		try {
			
			rawOutputFile.delete();
			outputFile = new RandomAccessFile(rawOutputFile, "rw");
			
			if(rawIndexFile != null) {
				
				rawIndexFile.delete(); 
				indexFile = new RandomAccessFile(rawIndexFile, "rw");
			}
		} 
		catch (FileNotFoundException fileNotFoundException) {
	
			LoggingManager.logUser(this, fileNotFoundException);
		}
	}

	@Override
	public void writeData(WriterData data) {
	
		if(!data.isEmpty()) {
			
			try {
				
				// moves the file pointer to the end of the file (on linux getFilePointer() always returns 0)
				outputFile.skipBytes((int)outputFile.length());
				
				// get the current pointer position in the data file
				long currentDataFilePosition = outputFile.getFilePointer();
				
				// map a part of the file into memory (the size of the results)
				MappedByteBuffer dataBuffer = outputFile.getChannel().map(MapMode.READ_WRITE,
						currentDataFilePosition,
						data.getData().length());
				
				// write the results
				dataBuffer.put(data.getData().getBytes());
				
				// if an index content is given and is allowed 
				if(data.hasIndex() && this.indexFile != null) { 
					
					// moves the file pointer to the end of the file (on linux getFilePointer() always returns 0)
					indexFile.skipBytes((int)indexFile.length());
					
					// get the current pointer position in the index file
					long currentIndexFilePosition = indexFile.getFilePointer();
					
					// map a part of the file into memory (the size of the index content)
					MappedByteBuffer indexBuffer = indexFile.getChannel().map(MapMode.READ_WRITE,
							currentIndexFilePosition,
							data.getIndex().length());
					
					// write the index
					indexBuffer.put(data.getIndex().getBytes());
				}
			} 
			catch (IOException ioException) {
				
				LoggingManager.logUser(this, ioException);
			}
		}
	}
	
	@Override
	public void close() {

		try {
			
			outputFile.close();
			
			if(indexFile != null) {
				
				indexFile.close();	
			}
		} 
		catch (IOException ioException) {

			LoggingManager.logUser(this, ioException);
		}
	}
}
