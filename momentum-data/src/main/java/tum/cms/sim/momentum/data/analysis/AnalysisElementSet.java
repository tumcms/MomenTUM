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

package tum.cms.sim.momentum.data.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

public class AnalysisElementSet {
	
	private LinkedHashMap<String, AnalysisElement> objectHashedElements = new LinkedHashMap<String, AnalysisElement>();
	private LinkedHashMap<Long, AnalysisElement> timeStepHashedElements = new LinkedHashMap<Long, AnalysisElement>();
	private ArrayList<AnalysisElement> unorderedElements = new ArrayList<>();
	
	public List<AnalysisElement> getUnorderedElements() {
		return this.unorderedElements;
	}

	/**
	 * Loaded in order of the simulation, long is the timestep, T the data
	 */
	public Collection<AnalysisElement> getObjectOrderedData() {
	
		return this.objectHashedElements.values();
	}
	
	/**
	 * Loaded in order of the simulation, string is the pedestrian id, T the data
	 */
	public Collection<AnalysisElement> getTimeOrderedData() {
		
		return this.timeStepHashedElements.values();
	}
	
	public void addElement(AnalysisElement resultElement) {
		
		this.unorderedElements.add(resultElement);
		
		this.timeStepHashedElements.put(resultElement.getTimeStep(), resultElement);
		this.objectHashedElements.put(resultElement.getId(), resultElement);
	}
}
