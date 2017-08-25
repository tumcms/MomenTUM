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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class RandomAccesLineNumberReader implements AutoCloseable {

	private static String errorIndexFile = "Missing Index File: FileName.cvs.idx";
	private RandomAccessFile randomAccessFile = null;
	private HashMap<Long, Long> lineSizeIndexer = new HashMap<Long, Long>();
	private LinkedHashMap<Long, Long> timeToLine = new LinkedHashMap<Long, Long>();
	
	private Long currentLineIndex = 0L;
	
	public Long getCurrentLineIndex() {
		return currentLineIndex;
	}

	public RandomAccesLineNumberReader(File file) throws FileNotFoundException {
		this.randomAccessFile = new RandomAccessFile(file, "r");
	}
	
	public synchronized void putLineIndexer(Long timeIndex, Long lineIndex, Long pointer) {

		this.lineSizeIndexer.put(lineIndex, pointer);
		this.timeToLine.put(timeIndex, lineIndex);
	}
	
	public synchronized void setCurrentLineIndex(Long lineIndex) throws Exception {
		
		if(this.randomAccessFile == null) {
			
			throw(new Exception(errorIndexFile));
		}
		
		if(lineSizeIndexer.get(lineIndex) != null) {
			
			this.randomAccessFile.seek(lineSizeIndexer.get(lineIndex));
		}
	
		currentLineIndex = lineIndex;
	}
	
	public long getNumberOfChars() throws IOException {
		
		return this.randomAccessFile.length();
	}
	
	public long getPointer() throws IOException {
		
		return this.randomAccessFile.getFilePointer();
	}	
	
	public synchronized String readLine() throws IOException {
		
		String readLine = this.randomAccessFile.readLine();
		
		if(readLine != null && !readLine.isEmpty()) {
			
			currentLineIndex++;
			
			if(!lineSizeIndexer.containsKey(currentLineIndex)) {
	
				Long completeSize = this.randomAccessFile.getFilePointer();
				lineSizeIndexer.put(currentLineIndex, completeSize);
			}
		}
		
		return readLine;
	}

	@Override
	public void close() throws Exception {
		
		this.randomAccessFile.close();
	}

	public boolean hasReadLine(Long lineIndex) {

		return this.lineSizeIndexer.containsKey(lineIndex);
	}

	public Long getLineNumber(Long index) {
			
		return this.timeToLine.get(index);
	}
}
