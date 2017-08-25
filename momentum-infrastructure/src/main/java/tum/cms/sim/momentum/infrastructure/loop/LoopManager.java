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

package tum.cms.sim.momentum.infrastructure.loop;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import tum.cms.sim.momentum.configuration.execution.LoopConfiguration;
import tum.cms.sim.momentum.configuration.execution.LoopVariableConfiguration;
import tum.cms.sim.momentum.configuration.execution.LoopConfiguration.LoopUpdateType;

public class LoopManager {

	private boolean endLooping = false;
	private LoopUpdateType updateType = null;
	private int digits = 3;	
	private ArrayList<LoopVariable> loopVariables = new ArrayList<>();
	
	public LoopManager(LoopConfiguration loopConfiguration) {

		this.updateType = loopConfiguration.getUpdateType();
		this.digits = loopConfiguration.getAccuracyDigits() != null ? loopConfiguration.getAccuracyDigits() : this.digits;
			
		for(LoopVariableConfiguration loopVariableConfiguration : loopConfiguration.getLoopVariables()) {

			LoopVariable loopVariable = new LoopVariable(loopVariableConfiguration.getInitial(),
					loopVariableConfiguration.getStart(),
					loopVariableConfiguration.getChange(),
					loopVariableConfiguration.getSteps(),
					loopVariableConfiguration.getName());
			
			this.loopVariables.add(loopVariable);
		}
		
		if(this.loopVariables.isEmpty()) {
			
			this.endLooping = true;
		}
	}
	/**
	 * Check if the simulation should be stopped
	 * @return true means stop simulate re-run.
	 */
	public boolean stopLooping() {

		return endLooping;
	}

	/**
	 * Gives all names of the loop variables; thus, the substitute names.
	 * Index corresponds to getLoopVariableUpdates
	 * @return List of loop variable names
	 */
	public List<String> getLoopSubtitutes() {
	
		return this.loopVariables.stream().map(LoopVariable::getSubstitute).collect(Collectors.toList());
	}
	
	/**
	 * Gives all loop variable updated values in string format.
	  * Index corresponds to getLoopSubtitutes
	 * @return List of loop variable updates in string format
	 */
	public List<String> getLoopVariableUpdates() {
		
		return this.loopVariables.stream()
				.map(LoopVariable::getCurrentValue)
				.map(value -> String.valueOf(value.doubleValue()))
				.collect(Collectors.toList());
	}
	
	/**
	 * Updates the loop variable values based on the looping strategy.
	 */
	public void updateLoopVariables() {
		
		switch(updateType) {
		
		case Permutation: // change the variable from "left" to "right", and go to the next variable if its left has an overflow
		default:
			
			boolean overflow = false;
			
			for(int index = 0; index < this.loopVariables.size(); index++) {
				
				//increment the variable and check for an overflow
				overflow = this.loopVariables.get(index).incrementStep(this.digits);
				
				// in case of no overflow stop the loop
				if(!overflow) {

					break;
				}
				// in case of an overflow, go to the next variable (to the right) and increment the variable.
			}
				
			// If overflow == true, the code permuted the last variable (index == loopVariable.size()). 
			// Therefore, stop looping the simulation.
			this.endLooping = overflow;
			
			break;
			
		case Stepwise: // changes all variables stepwise, minimal loop count is the minimal step of all variables

			for(LoopVariable loopVariable : this.loopVariables) {
			
				// if a single variable overflows, stop looping the simulation
				if(loopVariable.incrementStep(this.digits)) {
					
					this.endLooping = true;
				}
			}

			break;
		}
	}
}
