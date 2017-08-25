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

package tum.cms.sim.momentum.data.agent.pedestrian.state.other;

import java.util.HashSet;

/**
 * In order to save memory a null value for a callableModelNames 
 * member equals "call on all models".
 * The same is valid for callNotOnModelNames
 * @author ga37sib
 *
 */
public class MetaState {

	private Double generationTime = null;
	
	public Double getGenerationTime() {
		return generationTime;
	}

	public void setGenerationTime(Double generationTime) {
		this.generationTime = generationTime;
	}
	
	private HashSet<Integer> callableModelIds = null;
	private HashSet<Integer> callNotOnModelIds = null;
	private HashSet<Integer> visibleModelIds = null;
	
	public boolean isAllowedToCall(Integer modelId) {
		
		boolean result = true;
		
		if(callableModelIds != null) {
			
			result = this.callableModelIds.contains(modelId);
		}

		return result;
	}
	
	public boolean isNotAllowedToCall(Integer modelId) {
		
		boolean result = false;
		
		if(callNotOnModelIds != null) {
		
			result = this.callNotOnModelIds.contains(modelId);
		}
		
		return result;	
	}
	
	public boolean isAllowedToBeVisible(Integer modelId) {
		
		boolean result = false;
		
		if(visibleModelIds != null) {
			
				result = this.visibleModelIds.contains(modelId);
		}
		
		return result;
	}
	
	public HashSet<Integer> getCallableModelNames() {
		return callableModelIds;
	}
	
	public HashSet<Integer> getVisibleModelNames() {
		return visibleModelIds;
	}
	
	public boolean areModelIdsVisible(HashSet<Integer> givenModelIds) {
		
		return visibleModelIds.containsAll(givenModelIds);
	}

	/**
	 * If first parameter is zero, all models will be called; if second parameter is zero no model will not be called
	 * First parameter: all models in the map can be applied on that pedestrian
	 * Second parameter: all models in that map will not be applied to that pedestrian
	 * Third parameter: these models will not be called, but will be considered as visible for this pedestrians, so these models
	 * are not simulated by the pedestrian, but pedestrians which use these models influence the pedestrian
	 * @param callableModelIds
	 * @param callNotOnModelIds
	 * @param visibileModelIds
	 */
//	public MetaState(HashSet<Integer> callableModelIds, HashSet<Integer> callNotOnModelIds) {
//		
//		this.callableModelIds = callableModelIds;
//		this.callNotOnModelIds = callNotOnModelIds;
//	}
	
	public MetaState(HashSet<Integer> callableModelIds, HashSet<Integer> callNotOnModelIds, HashSet<Integer> visibleModelIds) {
		
		this.callableModelIds = callableModelIds;
		this.callNotOnModelIds = callNotOnModelIds;
		this.visibleModelIds = visibleModelIds;
	}


}
