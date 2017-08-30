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

import tum.cms.sim.momentum.data.analysis.AnalysisElement;
import tum.cms.sim.momentum.data.analysis.AnalysisElementSet;
import tum.cms.sim.momentum.model.analysis.AnalysisType;

/**
 * This measure class computes how long a pedestrian existed.
 * Every time a pedestrian leaves the system, a output data point is writen.
 * 
 * @author Peter M. Kielar
 *
 */
public class MeasureAgentExistens extends Measure {

	private HashMap<String, Long> startExistsMap = new HashMap<>();
	
	@Override
	public void initialize() {
		
		this.inputTypes.add(AnalysisType.timeStep);
		
		this.outputTypes.add(AnalysisType.id);
		this.outputTypes.add(AnalysisType.existensType);
	}
	
	@Override
	public void measure(long timeStep,
			HashMap<String, AnalysisElementSet> inputMap, 
			HashMap<String, AnalysisElementSet> outputMap) {
	
		Collection<AnalysisElement> currenTimeStepData = inputMap.get(AnalysisType.timeStep).getObjectOrderedData();
	
		HashMap<String, Long> currentExistsMap = new HashMap<>();
		
		currenTimeStepData.forEach(pedestrianData -> {
			
			// started existing?
			if(!startExistsMap.containsKey(pedestrianData.getId())) {
				
				startExistsMap.put(pedestrianData.getId(), timeStep);
			}
			
			// exists at the moment
			currentExistsMap.put(pedestrianData.getId(), timeStep);
		});
		
		ArrayList<String> stoppedExisting = new ArrayList<>();
		startExistsMap.forEach((pedestrianId, startExistingTimeStep) -> {
			
			// stopped existing?
			if(!currentExistsMap.containsKey(pedestrianId)) {
				
				// create data point with existing time
				AnalysisElement analysisElement = new AnalysisElement(pedestrianId,
						timeStep - startExistsMap.get(pedestrianId),
						timeStep);
				
				// store data point
				outputMap.get(AnalysisType.existensType).addElement(analysisElement);
				
				// add pedestrian to remove list
				stoppedExisting.add(pedestrianId);
			}
		});
		
		stoppedExisting.forEach(removePedestrianId -> startExistsMap.remove(removePedestrianId));
	}
}
