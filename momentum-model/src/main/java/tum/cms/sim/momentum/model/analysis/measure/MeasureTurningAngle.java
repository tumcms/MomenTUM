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
//import java.util.Iterator;
//import java.util.List;
//
//import tum.cms.sim.momentum.data.analysis.AnalysisElement;
import tum.cms.sim.momentum.data.analysis.AnalysisElementSet;
//import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
//import tum.cms.sim.momentum.model.analysis.AnalysisType;
//import tum.cms.sim.momentum.model.analysis.PostAnalysisModel;
//import tum.cms.sim.momentum.utility.geometry2D.Vector2D;
//import tum.cms.sim.momentum.utility.geometry2D.operation.GeometryAdditionals;
//import tum.cms.sim.momentum.utility.graphTheory.Vertex;

public class MeasureTurningAngle extends Measure {

//	private HashMap<String, Vector2D> secondLastTarget = new HashMap<>();
//	private HashMap<String, Vector2D> lastTarget = new HashMap<>();
	
	@Override
	public void initialize() {
//		
//		this.inputTypes.add(AnalysisType.tacticalTargetType);
//
//		this.outputTypes.add(AnalysisType.turingAngleType);
//		this.outputTypes.add(AnalysisType.navigationIdType);
	}
	
	@Override
	public void measure(long timeStep,
			HashMap<String, AnalysisElementSet> inputMap, 
			HashMap<String, AnalysisElementSet> outputMap) {

//		Iterator<AnalysisElement> tacticalTargetsAll = inputMap.get(AnalysisType.tacticalTargetType)
//				.getObjectOrderedData().iterator();
//	
//		while(tacticalTargetsAll.hasNext()) {
//			
//			this.measurePathDistance(outputMap, tacticalTargetsAll.next().iterator());
//		}
	}

	//private void measurePathDistance(HashMap<String, AnalysisElementSet> outputMap,
	//		Iterator<AnalysisElement> tacticalTargets) {
	
//		AnalysisElement lastTacticalTarget = null;	
//		AnalysisElement tacticalTarget = null;	
//		
//		Vertex secondLastTarget = null;
//		Vertex lastTarget = null;
//		Vertex currentTarget = null;
//		
//		Vertex tempLastTarget = null;
//		Vertex tempCurrentTarget = null;
//		boolean firstTime = false;
//				
//		while(tacticalTargets.hasNext()) {
//					   
//			if(currentTarget != null && lastTarget != null && secondLastTarget != null &&
//			   !currentTarget.getId().equals(lastTarget.getId()) &&
//			   !lastTarget.getId().equals(secondLastTarget.getId())) {
//			
//				Vector2D straightDirection = lastTarget.getGeometry().getCenter().sum(
//						lastTarget.getGeometry().getCenter().subtract(
//						secondLastTarget.getGeometry().getCenter())
//						.getNormalized());
//				
//				double radiant = GeometryAdditionals.angleBetweenPlusMinus180(straightDirection, 
//						lastTarget.getGeometry().getCenter(), 
//						currentTarget.getGeometry().getCenter());
//						
//				outputMap.get(AnalysisType.turingAngleType).addElement(
//					new AnalysisElement(lastTacticalTarget.getIdentificationId(), 
//							radiant,
//							lastTacticalTarget.getTimeStep()));
//				
//				outputMap.get(AnalysisType.navigationIdType).addElement(
//						new AnalysisElement(lastTacticalTarget.getIdentificationId(), 
//								lastTarget.getId(),
//								lastTacticalTarget.getTimeStep()));
//				
//				secondLastTarget = null;
//			}
//			
//			if(currentTarget == null) {
//				
//				currentTarget = this.scenarioManager.getGraph().getGeometryVertex(
//						this.scenarioManager.getOrigins().stream().findFirst().get().getGeometry());
//			}
//			
//			if(!firstTime && lastTacticalTarget != null) {
//				
//				outputMap.get(AnalysisType.turingAngleType).addElement(
//						new AnalysisElement(lastTacticalTarget.getIdentificationId(), 
//								0.0,
//								lastTacticalTarget.getTimeStep()));
//				
//				outputMap.get(AnalysisType.navigationIdType).addElement(
//						new AnalysisElement(lastTacticalTarget.getIdentificationId(), 
//								lastTarget.getId(),
//								lastTacticalTarget.getTimeStep()));
//				
//				firstTime = true;
//			}
//			
//			tempLastTarget = lastTarget;
//			tempCurrentTarget = currentTarget;
//			
//			tacticalTarget = tacticalTargets.next();
//			currentTarget = this.scenarioManager.getGraph().getVertex(tacticalTarget.getData().intValue());
//
//			lastTacticalTarget = tacticalTarget;
//			
//			if(tempCurrentTarget != null && !tempCurrentTarget.equals(currentTarget)) {
//				
//				lastTarget = tempCurrentTarget;	
//				tempCurrentTarget = null;
//			}
//			
//			if(tempLastTarget != null && !tempLastTarget.equals(lastTarget)) {
//		
//				secondLastTarget = tempLastTarget;
//				tempLastTarget = null;
//			}		
//		}
//		
//		outputMap.get(AnalysisType.turingAngleType).addElement(
//				new AnalysisElement(lastTacticalTarget.getIdentificationId(), 
//						0.0,
//						lastTacticalTarget.getTimeStep()));
//			
//			outputMap.get(AnalysisType.navigationIdType).addElement(
//					new AnalysisElement(tacticalTarget.getIdentificationId(), 
//							currentTarget.getId(),
//							tacticalTarget.getTimeStep()));
	//}
}
