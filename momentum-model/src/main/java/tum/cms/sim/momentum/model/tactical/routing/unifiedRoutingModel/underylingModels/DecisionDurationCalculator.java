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

package tum.cms.sim.momentum.model.tactical.routing.unifiedRoutingModel.underylingModels;

import org.apache.commons.math3.util.FastMath;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.tactical.routing.unifiedRoutingModel.UnifiedRoutingPedestrianExtension;
import tum.cms.sim.momentum.model.tactical.routing.unifiedRoutingModel.UnifiedRoutingConstant.DecisionDuration;

public class DecisionDurationCalculator {

	private Double decisionStart = null;
	private Double decisionDuration = null;
	private DecisionDuration durationType = null;
	
	public DecisionDurationCalculator(UnifiedRoutingPedestrianExtension pedestrianExtension) {
		
		this.durationType = pedestrianExtension.getDecisionDuration();
	}
	
	public Boolean isDeciding() {
		
		return decisionStart != null;
	}
	
	public Boolean isFinished(SimulationState simulationState) {
		
		Boolean result = true;
		
		if(decisionStart != null && this.durationType != DecisionDuration.None) {
			
			result = decisionStart + decisionDuration <= simulationState.getCurrentTime();
			
			if(result) {
				
				decisionStart = null;
				decisionDuration = null;
			}
		}
		
		return result;
	}
	
	public boolean computeDecisionDuration(SimulationState simulationState, int numberOfRoutes) {
		
		switch(this.durationType) {
		
		case City:
			
			decisionStart = simulationState.getCurrentTime();
			decisionDuration = 0.1 * 1.0/3.0 + 0.1 * FastMath.log(((double)numberOfRoutes));
			
			break;
			
		case None:
			break;
			
		case Open:
			break;
		default:
			break;
		
		}
		
		return decisionDuration == null ? false : true;
	}
}
