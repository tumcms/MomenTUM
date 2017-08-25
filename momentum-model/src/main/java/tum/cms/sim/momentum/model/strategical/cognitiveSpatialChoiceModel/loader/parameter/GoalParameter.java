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

package tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.loader.parameter;

import java.util.List;

import tum.cms.sim.momentum.data.layout.area.Area;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.CognitiveConstant.OccupancyType;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.memory.OpeningHourChunk;

public class GoalParameter {

	private Boolean singlePlace = false;
	
	public Boolean getSinglePlace() {
		return singlePlace;
	}

	public void setSinglePlace(Boolean singlePlace) {
		
		if(singlePlace != null) {
			
			this.singlePlace = singlePlace;
		}
	}

	private List<Integer> predecessorsPreferences = null;

	public List<Integer> getPredecessorsPreferences() {
		return predecessorsPreferences;
	}

	public void setPredecessorsPreferences(List<Integer> predecessorsPreferences) {
		this.predecessorsPreferences = predecessorsPreferences;
	}

	private List<OpeningHourChunk> openingHours = null;
	
	public List<OpeningHourChunk> getOpeningHours() {
		return openingHours;
	}

	public void setOpeningHours(List<OpeningHourChunk> openingHours) {
		this.openingHours = openingHours;
	}

	private Integer preferenceId = null;

	public Integer getPreferenceId() {
		return preferenceId;
	}

	public void setPreferenceId(Integer preferenceId) {
		this.preferenceId = preferenceId;
	}
	
	private Area goalArea = null;

	public Area getGoalArea() {
		return goalArea;
	}

	public void setGoalArea(Area goalArea) {
		this.goalArea = goalArea;
	}

	private OccupancyType occupancyType = null;
	
	public OccupancyType getOccupancyType() {
		return occupancyType;
	}

	public void setOccupancyType(OccupancyType occupancyType) {
		this.occupancyType = occupancyType;
	}
}
