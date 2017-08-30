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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

import tum.cms.sim.momentum.data.analysis.AnalysisElement;
import tum.cms.sim.momentum.data.analysis.AnalysisElementSet;
import tum.cms.sim.momentum.model.analysis.AnalysisType;
import tum.cms.sim.momentum.utility.geometry.GeometryFactory;
import tum.cms.sim.momentum.utility.geometry.Segment2D;
import tum.cms.sim.momentum.utility.geometry.Vector2D;

public class MeasureLineCrossing extends Measure {

	private final static String leftXName = "leftX";
	private final static String leftYName = "leftY";
	private final static String rightXName = "rightX";
	private final static String rightYName = "rightY";
	
	private ArrayList<Segment2D> measuringLineList = new ArrayList<>();	

	private HashMap<String, AnalysisElement> lastXPositionData = new HashMap<>();
	private HashMap<String, AnalysisElement> lastYPositionData = new HashMap<>();
	private HashMap<Segment2D, HashSet<String>> linesCrossed = new HashMap<>();
	private HashSet<String> ignorePedestrianSet = new HashSet<>();
	
	@Override
	public void initialize() {
		
		ArrayList<Double> leftXList = this.properties.<Double>getListProperty(leftXName);
		ArrayList<Double> leftYList = this.properties.<Double>getListProperty(leftYName);
		ArrayList<Double> rightXList = this.properties.<Double>getListProperty(rightXName);
		ArrayList<Double> rightYList = this.properties.<Double>getListProperty(rightYName);
		
		for(int iter = 0; iter < leftXList.size(); iter++) {
			
			this.measuringLineList.add(GeometryFactory.createSegment(
					GeometryFactory.createVector(leftXList.get(iter), leftYList.get(iter)),	
					GeometryFactory.createVector(rightXList.get(iter), rightYList.get(iter))));
		}

		this.measuringLineList.forEach(segment -> linesCrossed.put(segment, new HashSet<>()));
	
		this.inputTypes.add(AnalysisType.xPositionType);
		this.inputTypes.add(AnalysisType.yPositionType);

		this.outputTypes.add(AnalysisType.timeStep);
		this.outputTypes.add(AnalysisType.crossedLineType);
	}

	@Override
	public void measure(long timeStep,
			HashMap<String, AnalysisElementSet> inputMap, 
			HashMap<String, AnalysisElementSet> outputMap) {
		
		Collection<AnalysisElement> currenXPositionData = inputMap.get(AnalysisType.xPositionType).getObjectOrderedData();
		Collection<AnalysisElement> currenYPositionData = inputMap.get(AnalysisType.yPositionType).getObjectOrderedData();
			
		Iterator<AnalysisElement> yPositionIterator = currenYPositionData.iterator();
		
		currenXPositionData.forEach(xPositionData -> {

			// last x position given?
			// Check for line crossings, if the pedestrian did not crossed all already
			if(lastXPositionData.containsKey(xPositionData.getId()) &&
			   !ignorePedestrianSet.contains(xPositionData.getId())) {
				
				this.measureLineCrossing(outputMap,
						lastXPositionData.get(xPositionData.getId()),
						lastYPositionData.get(xPositionData.getId()),
						xPositionData,
						yPositionIterator.next());
			}
		});
		
		currenXPositionData.forEach(xPositionData -> {
			
			// store last position x
			if(!lastXPositionData.containsKey(xPositionData.getId())) {
				
				lastXPositionData.put(xPositionData.getId(), xPositionData);
			}
		});
		
		currenYPositionData.forEach(yPositionData -> {
			
			// store last position y
			if(!lastYPositionData.containsKey(yPositionData.getId())) {
				
				lastYPositionData.put(yPositionData.getId(), yPositionData);
			}
		});
	}
	
	private void measureLineCrossing(HashMap<String, AnalysisElementSet> outputMap,
			AnalysisElement xPositionLast,
			AnalysisElement yPositionLast,
			AnalysisElement xPositionCurrent,
			AnalysisElement yPositionCurrent) {
		
		Vector2D lastPosition =  GeometryFactory.createVector(
				xPositionLast.getData().doubleValue(), 
				yPositionLast.getData().doubleValue());
		
		Vector2D currentPosition = 	GeometryFactory.createVector(
				xPositionCurrent.getData().doubleValue(), 
				yPositionCurrent.getData().doubleValue());

		if(!lastPosition.equals(currentPosition)) {
			
			Segment2D workingSegment = GeometryFactory.createSegment(lastPosition, currentPosition);
	
			// check if a line was crossed
			this.measuringLineList.forEach(segment -> {
	
				if(segment.getIntersection(workingSegment).size() > 0) {
					
					HashSet<String> crossedForSegment = linesCrossed.get(segment);
					
					if(!crossedForSegment.contains(xPositionLast.getId())) {
					
						crossedForSegment.add(xPositionLast.getId());
					}
				}
			});
		}
		
		int crossed = 0;
		
		// Check the number of crossed lines
		for(Entry<Segment2D, HashSet<String>> lineCrossings : linesCrossed.entrySet()) {
	
			if(lineCrossings.getValue().contains(xPositionLast.getId())) {
				
				crossed++;
			}
		}

		if(crossed == measuringLineList.size()) {
			
			// pedestrian crossed all lines, ignore it 
			ignorePedestrianSet.add(xPositionLast.getId());
			
			// store data point
			AnalysisElement analysisElement = new AnalysisElement(xPositionLast.getId(), 
					crossed,
					xPositionCurrent.getTimeStep());
			
			outputMap.get(AnalysisType.crossedLineType).addElement(analysisElement);
		}
	}
}
