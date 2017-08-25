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

package tum.cms.sim.momentum.data.output;

public class WriterData {

	/**
	 * The data formated data content to write into a target
	 */
	private StringBuilder data = new StringBuilder();

	/**
	 * If existent the index data content hat provides pointer
	 * to the data content.
	 */
	private StringBuilder index = new StringBuilder();
	
	public String getData() {
		return data.toString();
	}

	public void setData(String data) {
		this.data.append(data);
	}
	
	public String getIndex() {
		return index.toString();
	}

	public void setIndex(String index) {
		this.index.append(index);
	}

	public boolean isEmpty() {
		
		return this.data.length() == 0;
	}
	
	public boolean hasIndex() {
		
		return this.index.length() > 0;
	}

	public void setWriterData(WriterData dataItem) {
	
		this.setData(dataItem.getData());
		this.setIndex(dataItem.getIndex());
	}
}
