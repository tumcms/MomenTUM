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

package tum.cms.sim.momentum.model.operational.walking.macroscopicModels.classicLWRmodel;

import java.util.Iterator;

import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.operational.walking.macroscopicModels.data.MacroscopicEdge;
import tum.cms.sim.momentum.model.output.writerSources.genericWriterSources.ModelWriterSource;

public class ClassicLWROutputSource extends ModelWriterSource<ClassicLWR> {
	
	private Iterator<MacroscopicEdge> macroscopicEdges = null;
	private MacroscopicEdge currentMacroscopicEdge = null;

	@Override
	public void initialize(SimulationState simulationState) {
		// nothing to do;
	}

	@Override
	public void loadSetItem() {

		currentMacroscopicEdge = macroscopicEdges.next();
	}

	@Override
	public void loadSet() {
		
		//int number = (int) callable.getMacroscopicEdges().stream().count();
		
		macroscopicEdges = callable.getMacroscopicEdges().stream().iterator();
	}
	
	/**
	 * A model data set comprise a single data item only.
	 * After reading the item, this output source item is empty.
	 * In the next time step, this data is available again.
	 */
	@Override
	public boolean hasNextSetItem() {
		
		return macroscopicEdges.hasNext();
	}

	@Override
	protected String getModelData(ClassicLWR model, String format, String dataElement) {
		
		String result = null;
		
		switch(dataElement){
		
		case "macroid":
			result = this.getId(format);
			break;			
		case "firstNodeX":
			result = this.getFirstNodeX(format);
			break;
			
		case "firstNodeY":
			result = this.getFirstNodeY(format);
			break;
			
		case "secondNodeX":
			result = this.getSecondNodeX(format);
			break;
			
		case "secondNodeY":
			result = this.getSecondNodeY(format);
			break;
		
		case "width":
			result = this.getWidth(format);
			break;
			
		case "maximalDensity":
			result = this.getMaximalDensity(format);
			break;
		
		case "currentDensity":
			result = this.getCurrentDensity(format);
			break;
		
		default:
			break;
		}
		
		return result;	
	}

	private String getId(String format) {

		return String.format(format, currentMacroscopicEdge.getId());
	}
	
	private String getFirstNodeX(String format) {

		return String.format(format, currentMacroscopicEdge.getFirstNode().getPosition().getXComponent());
	}
	
	private String getFirstNodeY(String format) {

		return String.format(format, currentMacroscopicEdge.getFirstNode().getPosition().getYComponent());
	}

	private String getSecondNodeX(String format) {

		return String.format(format, currentMacroscopicEdge.getSecondNode().getPosition().getXComponent());
	}
	
	private String getSecondNodeY(String format) {

		return String.format(format, currentMacroscopicEdge.getSecondNode().getPosition().getYComponent());
	}
	
	private String getWidth(String format) {

		return String.format(format, currentMacroscopicEdge.getWidth());
	}

	private String getMaximalDensity(String format) {

		return String.format(format, currentMacroscopicEdge.getMaximalDensity());
	}
	
	private String getCurrentDensity(String format) {

		return String.format(format, currentMacroscopicEdge.getCurrentDensity());
	}
}
