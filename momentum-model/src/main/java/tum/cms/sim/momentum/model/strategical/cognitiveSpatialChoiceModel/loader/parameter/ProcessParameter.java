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

import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.CognitiveConstant.DistancePerceptionType;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.CognitiveConstant.OccupancyPerceptionType;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.CognitiveConstant.PreferenceType;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.CognitiveConstant.RescheduleType;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.CognitiveConstant.ScheduleType;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.CognitiveConstant.ValuationType;
import tum.cms.sim.momentum.utility.probability.distrubution.Discret;
import tum.cms.sim.momentum.utility.probability.distrubution.IDistribution;

public class ProcessParameter {

	// Model Types
	
	private DistancePerceptionType distancePerceptionType = DistancePerceptionType.None;
	
	public DistancePerceptionType getDistancePerceptionType() {
		return distancePerceptionType;
	}

	public void setDistancePerceptionType(DistancePerceptionType distancePerceptionType) {
		this.distancePerceptionType = distancePerceptionType;
	}
	
	private OccupancyPerceptionType occupancyPerceptionType = OccupancyPerceptionType.None;

	public OccupancyPerceptionType getOccupancyPerceptionType() {
		return occupancyPerceptionType;
	}

	public void setOccupancyPerceptionType(OccupancyPerceptionType occupancyPerceptionType) {
		this.occupancyPerceptionType = occupancyPerceptionType;
	}

	private RescheduleType rescheduleType = RescheduleType.None;
	
	public RescheduleType getRescheduleType() {
		return rescheduleType;
	}

	public void setRescheduleType(RescheduleType rescheduleType) {
		this.rescheduleType = rescheduleType;
	}
	
	private ScheduleType scheduleType = ScheduleType.NearestNeighbor;

	public ScheduleType getScheduleType() {
		return scheduleType;
	}

	public void setScheduleType(ScheduleType scheduleType) {
		this.scheduleType = scheduleType;
	}
	
	private PreferenceType preferenceType = PreferenceType.None;

	public PreferenceType getPreferenceType() {
		return preferenceType;
	}

	public void setPreferenceType(PreferenceType preferenceType) {
		this.preferenceType = preferenceType;
	}
	
	private ValuationType valuationType = ValuationType.Weighted;

	public ValuationType getValuationType() {
		return valuationType;
	}

	public void setValuationType(ValuationType valuationType) {
		this.valuationType = valuationType;
	}

	// Perception Process
	private Double waitingPenalty = null;
	
	public Double getWaitingPenalty() {
		return waitingPenalty;
	}

	public void setWaitingPenalty(Double waitingPenalty) {
		this.waitingPenalty = waitingPenalty;
	}

	private Double participatingPenalty = null;

	public Double getParticipatingPenalty() {
		return participatingPenalty;
	}

	public void setParticipatingPenalty(Double participatingPenalty) {
		this.participatingPenalty = participatingPenalty;
	}

	private Double leakyIntegrationK = null;
	
	public Double getLeakyIntegrationK() {
		return leakyIntegrationK;
	}

	public void setLeakyIntegrationK(Double leakyIntegrationK) {
		this.leakyIntegrationK = leakyIntegrationK;
	}

	private Double leakyIntegrationAlpha = null;
	
	public Double getLeakyIntegrationAlpha() {
		return leakyIntegrationAlpha;
	}

	public void setLeakyIntegrationAlpha(Double leakyIntegrationAlpha) {
		this.leakyIntegrationAlpha = leakyIntegrationAlpha;
	}

	// Deduction process
	private Double proximityDistance = null;
	
	public Double getProximityDistance() {
		return proximityDistance;
	}

	public void setProximityDistance(Double proximityDistance) {
		this.proximityDistance = proximityDistance;
	}

	// Preference process
	private Double distanceScale = null;
	
	public Double getDistanceScale() {
		return distanceScale;
	}

	public void setDistanceScale(Double distanceScale) {
		this.distanceScale = distanceScale;
	}

	private Double minimalServiceTime = 1.0;
	
	public Double getMinimalServiceTime() {
		return minimalServiceTime;
	}

	public void setMinimalServiceTime(Double minimalServiceTime) {
		this.minimalServiceTime = minimalServiceTime;
	}
	
	public Integer maximalPedestrians = 0;
	
	public Integer getMaximalPedestrians() {
		return maximalPedestrians;
	}

	public void setMaximalPedestrians(Integer maximalPedestrians) {
		this.maximalPedestrians = maximalPedestrians;
	}

	private Discret groupDistribution = null;
	
	public Discret getGroupDistribution() {
		return groupDistribution;
	}

	public void setGroupDistribution(Discret groupDistribution) {
		this.groupDistribution = groupDistribution;
	}

	// Reschedule process
	private Double eliminateThreshold = -1.0;
	
	public Double getEliminateThreshold() {
		return eliminateThreshold;
	}

	public void setEliminateThreshold(Double eliminateThreshold) {
		this.eliminateThreshold = eliminateThreshold;
	}

	private Double decisionThreshold = 1.0;

	public Double getDecisionThreshold() {
		return decisionThreshold;
	}

	public void setDecisionThreshold(Double decisionThreshold) {
		this.decisionThreshold = decisionThreshold;
	}
	
	// Schedule process
	private Double nearestNeighborThreshold = null;
	
	public Double getNearestNeighborThreshold() {
		return nearestNeighborThreshold;
	}

	public void setNearestNeighborThreshold(Double nearestNeighborThreshold) {
		this.nearestNeighborThreshold = nearestNeighborThreshold;
	}

	private IDistribution scheduleTimeDistribution = null;
	
	public IDistribution getScheduleTimeDistribution() {
		return scheduleTimeDistribution;
	}

	public void setScheduleTimeDistribution(IDistribution scheduleTimeDistribution) {
		this.scheduleTimeDistribution = scheduleTimeDistribution;
	}

	private IDistribution scheduleSizeDistribution = null;

	public IDistribution getScheduleSizeDistribution() {
		return scheduleSizeDistribution;
	}

	public void setScheduleSizeDistribution(IDistribution scheduleSizeDistribution) {
		this.scheduleSizeDistribution = scheduleSizeDistribution;
	}
	
	// Reschedule process
	private Double rescheduleThreshold = 5.0;

	public Double getRescheduleThreshold() {
		return rescheduleThreshold;
	}

	public void setRescheduleThreshold(Double rescheduleThreshold) {
		this.rescheduleThreshold = rescheduleThreshold;
	}

	private Double interestShare = 0.5;

	public Double getInterestShare() {
		return interestShare;
	}

	public void setInterestShare(Double interestShare) {
		this.interestShare = interestShare;
	}
	
	private double intensityChange = 0.01;

	public double getIntensityChange() {
		return intensityChange;
	}

	public void setIntensityChange(double intensityChange) {
		this.intensityChange = intensityChange;
	}

}
