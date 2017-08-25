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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

import tum.cms.sim.momentum.data.agent.pedestrian.state.tactical.TacticalState.Behavior;
import tum.cms.sim.momentum.data.analysis.AnalysisElement;
import tum.cms.sim.momentum.data.analysis.AnalysisElementSet;
import tum.cms.sim.momentum.model.analysis.AnalysisType;
import tum.cms.sim.momentum.utility.geometry.Geometry2D;
import tum.cms.sim.momentum.utility.geometry.GeometryFactory;

public class MeasureAreaOccupancy extends Measure {

	private HashSet<Integer> originIds = new HashSet<>();
	private HashMap<Integer, Geometry2D> targetAreas = new HashMap<>();

	@Override
	public void initialize() {

		this.inputTypes.add(AnalysisType.behaviorType);
		this.inputTypes.add(AnalysisType.strategicTargetType);
		this.inputTypes.add(AnalysisType.xPositionType);
		this.inputTypes.add(AnalysisType.yPositionType);
		
		this.outputTypes.add(AnalysisType.occupancyType);
		
		this.scenarioManager.getOrigins().forEach(origin -> originIds.add(origin.getId()));
		
		this.scenarioManager.getDestinations().forEach(destination -> targetAreas.put(destination.getId(), destination.getGeometry()));
		this.scenarioManager.getIntermediates().forEach(intermediate -> targetAreas.put(intermediate.getId(), intermediate.getGeometry()));
	}
	
	@Override
	public void measure(
			long timeStep,
			HashMap<String, AnalysisElementSet> inputMap,
			HashMap<String, AnalysisElementSet> outputMap) {
		
		HashMap<Integer, Integer> targetOccupancy = new HashMap<>();
		targetAreas.keySet().forEach(targetId -> targetOccupancy.put(targetId, 0));
		   
		Iterator<AnalysisElement> currentStrategicTargets = inputMap.get(AnalysisType.strategicTargetType)
				.getObjectOrderedData().iterator();
		
		Iterator<AnalysisElement> currentPerformedBehaviors = inputMap.get(AnalysisType.behaviorType)
				.getObjectOrderedData().iterator();

		Iterator<AnalysisElement> currentXPositions = inputMap.get(AnalysisType.xPositionType)
				.getObjectOrderedData().iterator();
		
		Iterator<AnalysisElement> currentYPositions = inputMap.get(AnalysisType.yPositionType)
				.getObjectOrderedData().iterator();
		
		
		while(currentStrategicTargets.hasNext()) {
			
			// Identify if an agent is in a target area
			Integer targetId = this.measureOccupancy(currentStrategicTargets.next(),
					currentPerformedBehaviors.next(),
					currentXPositions.next(),
					currentYPositions.next());
			
			// sum up information
			if(targetId != null) {
				
				targetOccupancy.put(targetId, targetOccupancy.get(targetId) + 1);
			}
		}
		
		for(Entry<Integer, Integer> occupancyPerTarget : targetOccupancy.entrySet()) {
			
			// create data point with existing time
			AnalysisElement analysisElement = new AnalysisElement(String.valueOf(occupancyPerTarget.getKey()), 
					occupancyPerTarget.getValue(),
					timeStep);
			
			// store data point
			outputMap.get(AnalysisType.occupancyType).addElement(analysisElement);
		}
	}
	
	private Integer measureOccupancy(AnalysisElement strategicTarget,
			AnalysisElement behaviorElement,
			AnalysisElement xPositionElement,
			AnalysisElement yPositionElement) {

		Integer targetId = null;
		Behavior behavior = null;
		
		if(strategicTarget.getData() != null && behaviorElement.getData() != null) {
			
			behavior = Behavior.values()[behaviorElement.getData().intValue()];	
			targetId = strategicTarget.getData().intValue();	
			
			if(targetId == -1 || this.originIds.contains(targetId) ||
					behavior == Behavior.Routing || behavior == Behavior.Searching ||
					(behavior == Behavior.Staying &&
					!targetAreas.get(targetId).contains(GeometryFactory.createVector(xPositionElement.getData().doubleValue(), yPositionElement.getData().doubleValue())))) {

				targetId = null;
			}
		}
		
		return targetId;
	}
}
