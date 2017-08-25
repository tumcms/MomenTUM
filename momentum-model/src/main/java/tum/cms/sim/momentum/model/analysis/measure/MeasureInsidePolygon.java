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

package tum.cms.sim.momentum.model.analysis.measure;

import java.util.HashMap;
import java.util.Iterator;

import tum.cms.sim.momentum.data.analysis.AnalysisElement;
import tum.cms.sim.momentum.data.analysis.AnalysisElementSet;
import tum.cms.sim.momentum.model.analysis.AnalysisType;
import tum.cms.sim.momentum.utility.geometry.Geometry2D;
import tum.cms.sim.momentum.utility.geometry.GeometryFactory;
import tum.cms.sim.momentum.utility.geometry.Vector2D;

public class MeasureInsidePolygon extends Measure {

	private final static String areaIdName = "areaId";

	private Geometry2D measuringPolygon = null;	

	@Override
	public void initialize() {

		Integer areaId = this.properties.getIntegerProperty(areaIdName);
		
		this.measuringPolygon = this.scenarioManager.getAreas().stream()
				.filter((area) -> area.getId().equals(areaId))
				.findFirst()
				.get()
				.getGeometry();
		
		this.inputTypes.add(AnalysisType.xPositionType);
		this.inputTypes.add(AnalysisType.yPositionType);
		
		this.outputTypes.add(AnalysisType.insidePolygonType);
	}
	
	@Override
	public void measure(long timeStep, 
			HashMap<String, AnalysisElementSet> inputMap, 
			HashMap<String, AnalysisElementSet> outputMap) {
	
		Iterator<AnalysisElement> xPositionsAll = inputMap.get(AnalysisType.xPositionType)
				.getObjectOrderedData().iterator();
		
		Iterator<AnalysisElement> yPositionsAll = inputMap.get(AnalysisType.yPositionType)
				.getObjectOrderedData().iterator();
		
		while(xPositionsAll.hasNext() && yPositionsAll.hasNext()) {
			
			AnalysisElement xPosition = xPositionsAll.next();
			Vector2D currentPosition = GeometryFactory.createVector(
					xPosition.getData().doubleValue(), 
					yPositionsAll.next().getData().doubleValue());
			
			int inside = measuringPolygon.contains(currentPosition) ? 1 : 0;
			
			// create data point with existing time
			AnalysisElement analysisElement = new AnalysisElement(xPosition.getId(), 
					inside,
					timeStep);
			
			// store data point
			outputMap.get(AnalysisType.insidePolygonType).addElement(analysisElement);
		}
	}
}
