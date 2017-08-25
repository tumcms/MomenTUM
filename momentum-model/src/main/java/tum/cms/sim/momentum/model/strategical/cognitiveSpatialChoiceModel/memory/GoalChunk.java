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

package tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.memory;

import java.util.List;

import tum.cms.sim.momentum.data.layout.area.Area;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.CognitiveConstant.Availability;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.CognitiveConstant.Familiarity;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.CognitiveConstant.OccupancyType;
import tum.cms.sim.momentum.utility.geometry.Vector2D;

public class GoalChunk {

	private Boolean singlePlace = false;
	
	public Boolean getSinglePlace() {
		return singlePlace;
	}

	public void setSinglePlace(Boolean singlePlace) {
		this.singlePlace = singlePlace;
	}

	private List<PreferenceChunk> predecessorsPreferences = null;
	
	public List<PreferenceChunk> getPredecessorsPreferences() {
		return predecessorsPreferences;
	}

	public void setPredecessorsPreferences(List<PreferenceChunk> predecessorsPreferences) {
		this.predecessorsPreferences = predecessorsPreferences;
	}

	private Double openingInSeconds = null;
	
	public Double getOpeningInSeconds() {
		return openingInSeconds;
	}

	public void setOpeningInSeconds(Double openingInSeconds) {
		this.openingInSeconds = openingInSeconds;
	}

	private List<OpeningHourChunk> openingHours = null;
	
	public List<OpeningHourChunk> getOpeningHours() {
		return openingHours;
	}

	public void setOpeningHours(List<OpeningHourChunk> openingHours, Double openingMalus) {
		this.openingHours = openingHours;
		this.openingInSeconds = openingHours.stream().mapToDouble(openHour -> openHour.getDuration()).sum() - openingMalus;
	}

	private PreferenceChunk preference = null;

	public PreferenceChunk getPreference() {
		return preference;
	}

	public void setPreference(PreferenceChunk preference) {
		this.preference = preference;
	}

	public Integer getGoalId() {
		return goalArea.getId();
	}

	private Area goalArea = null;

	public Area getGoalArea() {
		return goalArea;
	}

	public void setGoalArea(Area goalArea) {
		this.goalArea = goalArea;
	}

	public Vector2D getPointOfInterest() {
		return this.goalArea.getPointOfInterest();
	}

	private Familiarity familiarity = null;
	
	public Familiarity getFamiliarity() {
		return familiarity;
	}

	public void setFamiliarity(Familiarity familiarity) {
		this.familiarity = familiarity;
	}

//	private Boolean positionKnown = true;
//	
//	public Boolean getPositionKnown() {
//		return positionKnown;
//	}
//
//	public void setPositionKnown(Boolean positionKnown) {
//		this.positionKnown = positionKnown;
//	}

	private Boolean visible = null;
	
	public Boolean getVisible() {
		return visible;
	}

	public void setVisible(Boolean visible) {
		this.visible = visible;
	}

	private Double distance = null;

	public Double getDistance() {
		return distance;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}

	private Double occupancy = null;
	
	public Double getOccupancy() {
		return occupancy;
	}

	public void setOccupancy(Double occupancy) {
		this.occupancy = occupancy;
	}

	private Boolean proximity = null;
	
	public Boolean getProximity() {
		return proximity;
	}

	public void setProximity(Boolean proximity) {
		this.proximity = proximity;
	}

	public Double getInterest() {
		return this.preference.getInterest();
	}

	private Availability availability = null;

	public Availability getAvailability() {
		return availability;
	}

	public void setAvailability(Availability availability) {
		this.availability = availability;
	}
	
	private Double scheduledValenceStrength = null;

	public Double getScheduledValenceStrength() {
		return scheduledValenceStrength;
	}

	public void setScheduledValenceStrength(Double scheduledValenceStrength) {
		this.scheduledValenceStrength = scheduledValenceStrength;
	}

	private Double valenceStrength = null;
	
	public Double getValenceStrength() {
		return valenceStrength;
	}

	public void setValenceStrength(Double valenceStrength) {
		this.valenceStrength = valenceStrength;
	}


	private OccupancyType occupancyType = null;
	
	public OccupancyType getOccupancyType() {
		return occupancyType;
	}

	public void setOccupancyType(OccupancyType occupancyType) {
		this.occupancyType = occupancyType;
	}

	public boolean isDoor() {
		
		return this.occupancyType == OccupancyType.None;
	}

	public boolean isFull() {
	
		return this.singlePlace && this.getOccupancy() == 1.0;
	}
	
	private boolean availablityChanged = false;

	public boolean getAvailablityChanged() {
		return availablityChanged;
	}

	public void setAvailablityChanged(boolean availablityChanged) {
		this.availablityChanged = availablityChanged;
	}
	
	
	public boolean isSuccesorOf(GoalChunk anotherGoal) {
	
		boolean isPredecessorsOf = false;
		
		if(anotherGoal.getPredecessorsPreferences() != null && !anotherGoal.getPredecessorsPreferences().isEmpty()) {

			isPredecessorsOf = anotherGoal.getPredecessorsPreferences().contains(this.getPreference());
			
			if(!isPredecessorsOf) {
				
				for(PreferenceChunk prePredecessorPreference : anotherGoal.getPredecessorsPreferences()) {
					
					for(GoalChunk prePredecessorGoal : prePredecessorPreference.getAssociatedGoals()) {
						
						isPredecessorsOf = this.isSuccesorOf(prePredecessorGoal);
						
						if(isPredecessorsOf) {
							break;
						}
					}
					
					if(isPredecessorsOf) {
						break;
					}
				}
			}
		}
		
		return isPredecessorsOf;
	}
}
