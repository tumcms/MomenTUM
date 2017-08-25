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

public class ListOutputWriter extends OutputWriter {
	
//	private final static String fileNameString = "file";
//	private final static String listHeaderName = "listHeader";
//	
//	private WriterSource<String> writerSource = null;
//	private File file = null;
//	PrintStream outWriter = null;
//	private String listHeader = null;
//	
//	@SuppressWarnings("unchecked")
//	@Override
//	public void setWriterSource(WriterSource<?> writerSource) {
//		
//		this.writerSource = (WriterSource<String>) writerSource;  
//	}
//
//	@Override
//	public void initialize() {
//
//		this.file = this.properties.getFileProperty(fileNameString);
//		this.file.delete();
//		
//		this.listHeader = this.properties.getStringProperty(listHeaderName);
//		
//		try {
//			
//			outWriter = new PrintStream(new FileOutputStream(file, false));
//		}
//		catch (FileNotFoundException e) {
//
//			e.printStackTrace();
//		}
//	}
//
//	@Override
//	public void write() {
//		
//		int numberOfSetItems = writerSource.loadSet();
//		
//		while(numberOfSetItems-- > 0) {
//			
//			writerSource.loadSetItem();
//		}
//		
//		outWriter.print(this.listHeader);
//		outWriter.print(writerSource.readSingleValue(this.listHeader));
//	}
//
//	@Override
//	public void flush() {
//		
//		outWriter.flush();
//		outWriter.close();
//	}
}
