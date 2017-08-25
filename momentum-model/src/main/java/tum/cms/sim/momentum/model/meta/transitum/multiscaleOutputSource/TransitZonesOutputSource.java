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

package tum.cms.sim.momentum.model.meta.transitum.multiscaleOutputSource;

import java.util.Iterator;

import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.meta.transitum.TransiTumModel;
import tum.cms.sim.momentum.model.meta.transitum.data.MultiscaleArea;
import tum.cms.sim.momentum.model.meta.transitum.data.MultiscaleArea.AreaType;
import tum.cms.sim.momentum.model.output.writerSources.genericWriterSources.ModelWriterSource;

public class TransitZonesOutputSource extends ModelWriterSource<TransiTumModel> {
	
	private Iterator<MultiscaleArea> currentTransitionAreas = null;
	private MultiscaleArea currentTransitionArea = null;

	@Override
	public void initialize(SimulationState simulationState) {

		this.dataItemNames.addAll(this.properties.getFormatNames());
	}

	@Override
	public void loadSetItem() {

		currentTransitionArea = currentTransitionAreas.next();
	}

	@Override
	public void loadSet() {
		
//		int number = (int)callable.getMultiscaleAreas().stream()
//				.filter(transitArea -> transitArea.getAreaType() == AreaType.Microscopic)
//				.count();
		
		currentTransitionAreas = callable.getMultiscaleAreas().stream()
				.filter(transitArea -> transitArea.getAreaType() == AreaType.Microscopic)
				.iterator();
		
	}
	
	/**
	 * A model data set comprise a single data item only.
	 * After reading the item, this output source item is empty.
	 * In the next time step, this data is available again.
	 */
	@Override
	public boolean hasNextSetItem() {
		
		return currentTransitionAreas.hasNext();
	}
	
	@Override
	protected String getModelData(TransiTumModel model, String format, String dataElement) {
		
		String result = null;
		
		switch(dataElement){
		
		case "transitid":
			result = this.getId(format);
			break;
		case "transitx":
			result = this.getCurrentX(format);
			break;
		case "transity":
			result = this.getCurrentY(format);
			break;
			
		case "radiusIn":
			result = this.getRadiusIn(format);
			break;
		
		case "radiusOut":
			result = this.getRadiusOut(format, model);
			break;
		
		default:
			break;
		}
		
		return result;	
	}

	
	private String getId(String format) {

		return String.format(format, currentTransitionArea.getId());
	}

	private String getRadiusIn(String format) {

		return String.format(format, currentTransitionArea.getRadiusOfArea());
	}
	
	private String getRadiusOut(String format, TransiTumModel model) {
		
	
		return String.format(format, currentTransitionArea.getRadiusOfArea() + model.getTransitionRadiusMicroMeso());
	}

	private String getCurrentY(String format) {
		
		return String.format(format, currentTransitionArea.getCenterOfArea().getYComponent());
	}

	private String getCurrentX(String format) {
		
		return String.format(format, currentTransitionArea.getCenterOfArea().getXComponent());
	}
}
