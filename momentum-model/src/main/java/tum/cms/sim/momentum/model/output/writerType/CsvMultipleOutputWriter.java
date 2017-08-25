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

package tum.cms.sim.momentum.model.output.writerType;

import tum.cms.sim.momentum.model.output.OutputWriter;

public class CsvMultipleOutputWriter extends OutputWriter {
	
//	private final static String fileNameString = "file";
//	private final static String orderListString = "order";
//	private final static String delimiterString = "delimiter";
//	private final static String bufferString = "buffer";
//	private final static String appendString = "append";
//
//	private File file = null;
//	private File indexFile = null;
//	private CSVPrinter csvPrinter = null;
//	private CSVFormat csvFormat = null;
//	private String delimiter =  null;
//	
//	private PrintStream indexWriter = null;
//	private ArrayList<Long> currentLine = new ArrayList<Long>();
//	private ArrayList<Long> itemLineSize = new ArrayList<Long>();
//	private ArrayList<Long> currentLinePointer = new ArrayList<Long>();
//	private ArrayList<String> indexFromData = new ArrayList<String>();
//	private int headerSize = 0;
//	
//	private Boolean append = false;
//	private ArrayList<String> headerList = null;
//	private int buffer = 1;
//	private ArrayList<ArrayList<String>> bufferList = new ArrayList<ArrayList<String>>();
//	private boolean shouldWriteIndex = false;
//	
//	private WriterSource<String> writerSource = null;
//
//	@SuppressWarnings("unchecked")
//	@Override
//	public void setWriterSource(WriterSource<?> writerSource) {
//		
//		this.writerSource = (WriterSource<String>) writerSource;  
//	}
//	
//	public void setWriteIndex(boolean shouldWriteIndex) {
//		
//		this.shouldWriteIndex = shouldWriteIndex;
//	}
//
//	
//	@Override
//	public void initialize() {
//		
//		this.append = this.properties.getBooleanProperty(appendString);
//		
//		if(this.append == null) {
//			
//			this.append = false;
//		}
//		
//		this.file = this.properties.getFileProperty(fileNameString);
//		this.indexFile = new File(this.file.getAbsolutePath() + WriterSourceConfiguration.indexString);
//		headerList = this.properties.<String>getListProperty(orderListString);
//		
//		buffer = this.properties.getIntegerProperty(bufferString);
//		delimiter = this.properties.getStringProperty(delimiterString);
//		csvFormat = CSVFormat.newFormat(delimiter.charAt(0)).withRecordSeparator(System.lineSeparator());
//		
//		if(!this.append || !this.file.exists()) {
//			
//			this.file.delete();
//			
//			// add header list to output
//			Collections.replaceAll(headerList, null, "null");
//			bufferList.add(headerList);	
//		}
//		
//		this.indexFile.delete(); // in append mode this is stupid, but just ignore
//
//		currentLine.add(0L);
//		currentLinePointer.add(0L);
//	}
//
//	@Override
//	public void flush() {
//
//		this.append(bufferList);
//		
//		try {
//			
//			csvPrinter.close();
//			
//			if(this.shouldWriteIndex) {
//				
//				indexWriter.close();
//			}
//		} 
//		catch (IOException e) {
//
//			e.printStackTrace();
//		}
//	}
//
//	@Override
//	public void write() {
//
//		long currentItemSetSize = 0L;
//		long currentItemSetLine = 0L;
//
//		String indexItem = null;
//		writerSource.hasNextSet();
//		int valueSetSize = writerSource.loadSet();
//		
//		while(valueSetSize > 0) {
//			
//			while(valueSetSize-- > 0) {
//				
//				writerSource.loadSetItem();
//	
//				Pair<ArrayList<String>,Long> data = this.readDataSource();
//				
//				currentItemSetLine++;
//				currentItemSetSize += data.getRight();
//				bufferList.add(data.getLeft());
//				indexItem = data.getLeft().get(0);				
//			}
//			
//			if(currentItemSetLine > 0) {
//				
//				indexFromData.add(indexItem);
//				itemLineSize.add(currentItemSetLine);		
//				currentLinePointer.add(currentLinePointer.get(currentLinePointer.size() - 1) + currentItemSetSize);
//				currentLine.add(currentLine.get(currentLine.size() - 1) + currentItemSetLine);
//			}				
//			
//			if(buffer - bufferList.size() < 0) {
//				
//				this.append(bufferList);
//			}
//			
//			valueSetSize = writerSource.loadSet();
//		}
//	}
//	
//	private void append(ArrayList<ArrayList<String>> valuesList) {
//	
//		try {
//			
//			if(csvPrinter == null) {
//				
//				csvPrinter = new CSVPrinter(new FileWriter(file, true), csvFormat);
//				
//				if(this.shouldWriteIndex) {
//					
//					indexFile.delete();
//					indexWriter = new PrintStream(new FileOutputStream(indexFile, false));
//					
//					headerList.stream().forEach(header -> headerSize += header.length());
//					headerSize += (headerList.size() - 1) + 2; // separators and newline
//				}
//			}
//			
//			for(ArrayList<String> entry : valuesList) {
//				
//				csvPrinter.printRecord(entry);
//			}
//			
//			if(this.shouldWriteIndex) {
//				
//				for(int iter = 0; iter < currentLine.size() - 1; iter++) {
//					
//					// first is line number, second is number of lines for block, 
//					// third is file pointer to start of first line
//					String indexContent = indexFromData.get(iter)
//							+ ";" 
//							+ String.valueOf(this.currentLine.get(iter)) 
//							+ ";" 
//							+ String.valueOf(this.itemLineSize.get(iter))
//							+ ";"
//							+ String.valueOf(this.currentLinePointer.get(iter) + headerSize);
//					
//					indexWriter.println(indexContent);
//				}
//			}
//			
//			csvPrinter.flush();
//			
//			if(this.shouldWriteIndex) {
//				
//				indexWriter.flush();
//			}
//		} 
//		catch (IOException e) {
//			
//			// TODO Error handling
//		}
//		
//		bufferList = new ArrayList<ArrayList<String>>();
//		
//		if(this.shouldWriteIndex) {
//		
//			Long temp = currentLine.get(currentLine.size() - 1);
//			currentLine = new ArrayList<Long>();
//			currentLine.add(temp);
//			
//			temp = currentLinePointer.get(currentLinePointer.size() - 1);
//			currentLinePointer = new ArrayList<Long>();
//			currentLinePointer.add(temp);
//			
//			itemLineSize = new ArrayList<Long>();
//			indexFromData = new ArrayList<String>();
//		}
//	}
//	
//	private Pair<ArrayList<String>,Long> readDataSource() {
//		
//		long currentItemSetSize = 0L;
//		
//		String item = null;
//		String itemContent = null;
//		
//		ArrayList<String> entries = new ArrayList<String>(headerList.size());
//
//		for(int iter = 0; iter < headerList.size(); iter++) {
//			
//			item = headerList.get(iter);
//			itemContent = this.writerSource.readSingleValue(item);
//			
//			if(itemContent != null) {
//		
//				currentItemSetSize += itemContent.length();
//				entries.add(itemContent);
//			}
//		}
//		
//		currentItemSetSize += entries.size() - 1; // add number of separators!
//		currentItemSetSize += 2; // add newline /r/n 
//		return new MutablePair<ArrayList<String>,Long>(entries, currentItemSetSize);
//	}
}
