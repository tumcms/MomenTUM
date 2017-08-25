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

package tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.loader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import tum.cms.sim.momentum.data.layout.ScenarioManager;
import tum.cms.sim.momentum.data.layout.area.Area;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.CognitiveConstant;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.CognitiveConstant.OccupancyType;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.loader.parameter.GoalParameter;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.loader.parameter.PreferenceParameter;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.loader.parameter.ProcessParameter;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.memory.OpeningHourChunk;
import tum.cms.sim.momentum.utility.generic.PropertyBackPack;
import tum.cms.sim.momentum.utility.probability.distrubution.Discret;
import tum.cms.sim.momentum.utility.probability.distrubution.DistributionFactory;
import tum.cms.sim.momentum.utility.probability.distrubution.IDistribution;

public class CognitiveParameterLoader {

	private String areaIdName = "areaIds";

	private String cognitiveClockName = "cognitiveClock";

//	private String decisionThresholdFactorName = "decisionThresholdFactor";
//	private String eliminateThresholdName = "eliminateThreshold";
	private String leakyIntegrationAlphaName = "leakyIntegrationAlpha";
	private String leakyIntegrationKName = "leakyIntegrationK";
	private String distanceScaleName = "distanceScale";
	private String minimalServiceTimeName = "minimalServiceTime";
	private String nearestNeibhborThresholdName = "nearestNeibhborThreshold";
	private String waitingPenaltyName = "waitingPenalty";
	private String participatingPenaltyName = "participatingPenalty";
	private String proximityDistanceName = "proximityDistance";
	
//	private String scheduleSizeMeanName = "scheduleSizeMean";
//	private String scheduleSizeVarianzName = "scheduleSizeVarianz";
//	private String scheduleTimeMeanName = "scheduleTimeMean";
//	private String scheduleTimeVarianzName = "scheduleTimeVarianz";
	
	private String maximalPedestrians = "maximalPedestrians";
	private String rescheduleThresholdName = "rescheduleThreshold";
	
	private String interestShareName = "interestShare";
	private String intensityChangeName = "intensityChange";

	private String perceptionFunctionName = "perceptionFunction";
	private String occupancyFunctionName = "occupancyFunction";
//	private String rescheduleFunctionName = "rescheduleFunction";
	private String scheduleFunctionName = "scheduleFunction";
	private String preferenceFunctionName = "preferenceFunction";
	private String valuationFunctionName = "valuationFunction";

	private String seedIdName = "seedId";
	private String preferenceIdName = "preferenceId";
	private String oneTimeName = "oneTime";
	private String singlePlaceName = "singlePlace";
	private String serviceTimeGammaKName = "serviceTimeGammaK";
	private String serviceTimeGammaThetaName = "serviceTimeGammaTheta";
	private String interarrivalTimeLambdaName = "interarrivalTimeLambda";
	private String groupDistributionName = "groupDistribution";
	private String occupancyTypeName = "occupancyType";
	private String predecessorPreferencesName = "predecessorPreferences";
	private String openingHoursName = "openingHours";

	private String openingMalusName = "openingMalus";
	private Double openingMalus = 0.0;
	
	public Double getOpeningMalus() {
		return openingMalus;
	}

	protected Double cognitiveClock = null;

	public Double getCognitiveClock() {
		return cognitiveClock;
	}

	public void setCognitiveClock(Double cognitiveClock) {
		this.cognitiveClock = cognitiveClock;
	}

	private ProcessParameter processParameter = null;

	public ProcessParameter getProcessParameter() {
		return processParameter;
	}

	private HashMap<Integer, PreferenceParameter> preferenceParameters = new HashMap<>();

	public PreferenceParameter getPreferenceParameter(Integer preferenceId) {
		return preferenceParameters.get(preferenceId);
	}

	public Collection<PreferenceParameter> getPreferenceParameters() {

		return this.preferenceParameters.values();
	}

	private HashMap<Integer, GoalParameter> goalParameters = new HashMap<>();

	public GoalParameter getGoalParameter(Integer areaId) {
		return goalParameters.get(areaId);
	}

	public Collection<GoalParameter> getGoalParameters() {

		return this.goalParameters.values();
	}

	public void createBasics(PropertyBackPack modelBackPack) {

		this.cognitiveClock = modelBackPack.getDoubleProperty(cognitiveClockName);
		this.openingMalus = modelBackPack.getDoubleProperty(openingMalusName);
	}

	public ArrayList<Integer> createAreaIds(PropertyBackPack goalBackPack) {

		return goalBackPack.<Integer> getListProperty(areaIdName);
	}

	public void createProcessParameter(PropertyBackPack modelBackPack, ScenarioManager scenarioManager) {

		this.processParameter = new ProcessParameter();

		// processParameter.setMinimalInterarrival(modelBackPack.getDoubleProperty(minimalInterarrivalName));
		processParameter.setMinimalServiceTime(modelBackPack.getDoubleProperty(minimalServiceTimeName));

		processParameter.setMaximalPedestrians(modelBackPack.getIntegerProperty(maximalPedestrians));
		// perception
		processParameter.setDistancePerceptionType(CognitiveConstant.DistancePerceptionType
				.valueOf(modelBackPack.getStringProperty(perceptionFunctionName)));

		processParameter.setLeakyIntegrationAlpha(modelBackPack.getDoubleProperty(leakyIntegrationAlphaName));
		processParameter.setLeakyIntegrationK(modelBackPack.getDoubleProperty(leakyIntegrationKName));

		processParameter.setDistanceScale(modelBackPack.getDoubleProperty(distanceScaleName));

		processParameter.setOccupancyPerceptionType(CognitiveConstant.OccupancyPerceptionType
				.valueOf(modelBackPack.getStringProperty(occupancyFunctionName)));

		processParameter.setParticipatingPenalty(modelBackPack.getDoubleProperty(participatingPenaltyName));
		processParameter.setWaitingPenalty(modelBackPack.getDoubleProperty(waitingPenaltyName));

		// Deduction
		processParameter.setProximityDistance(modelBackPack.getDoubleProperty(proximityDistanceName));
		
		// preference
		processParameter.setPreferenceType(
				CognitiveConstant.PreferenceType.valueOf(modelBackPack.getStringProperty(preferenceFunctionName)));

		Discret groupDistribution = null;

		if (modelBackPack.<Double> getListProperty(groupDistributionName) != null) {

			ArrayList<Pair<Double,Double>> probabilityList = new ArrayList<Pair<Double,Double>>();
			ArrayList<Double> groupSizeProbability = modelBackPack.<Double> getListProperty(groupDistributionName);
			
			for(int iter = 0; iter < groupSizeProbability.size(); iter++) {
				
				probabilityList.add(new ImmutablePair<Double, Double>(iter + 1.0, groupSizeProbability.get(iter)));
			}
			
			groupDistribution = DistributionFactory.createDiscretDistrubtion(probabilityList);
		}
		else {
			
			ArrayList<Pair<Double,Double>> probabilityList = new ArrayList<Pair<Double,Double>>();
			probabilityList.add(new ImmutablePair<Double, Double>(1.0, 1.0));
			groupDistribution = DistributionFactory.createDiscretDistrubtion(probabilityList);
		}

		processParameter.setGroupDistribution(groupDistribution);

		// schedule
		processParameter.setScheduleType(
				CognitiveConstant.ScheduleType.valueOf(modelBackPack.getStringProperty(scheduleFunctionName)));

		processParameter.setNearestNeighborThreshold(modelBackPack.getDoubleProperty(nearestNeibhborThresholdName));

//		Double scheduleSizeMean = modelBackPack.getDoubleProperty(scheduleSizeMeanName);
//		Double scheduleSizeVarianz = modelBackPack.getDoubleProperty(scheduleSizeVarianzName);
		IDistribution scheduleSizeDistribution = DistributionFactory.createGammaDistribution(4,1);
		
		processParameter.setScheduleSizeDistribution(scheduleSizeDistribution);

//		Double scheduleTimeMean = modelBackPack.getDoubleProperty(scheduleTimeMeanName);
//		Double scheduleTimeVarianz = modelBackPack.getDoubleProperty(scheduleTimeVarianzName);
		IDistribution scheduleTimeDistribution = null;//DistributionFactory.createNormalDistribution(scheduleTimeMean,
				//scheduleTimeVarianz);
		processParameter.setScheduleTimeDistribution(scheduleTimeDistribution);

		// valuation
		processParameter.setValuationType(
				CognitiveConstant.ValuationType.valueOf(modelBackPack.getStringProperty(valuationFunctionName)));

		processParameter.setInterestShare(modelBackPack.getDoubleProperty(interestShareName));
		processParameter.setIntensityChange(modelBackPack.getDoubleProperty(intensityChangeName));
		
		// reschedule
		processParameter.setRescheduleThreshold(modelBackPack.getDoubleProperty(rescheduleThresholdName));
	}

	public void addGoalParameter(Integer areaId, PropertyBackPack goalsBackPack, ScenarioManager scenarioManager,
			SimulationState simulationState) {

		HashMap<Integer, Area> interestLocationsMap = new HashMap<Integer, Area>();
		scenarioManager.getDestinations()
				.forEach(destination -> interestLocationsMap.put(destination.getId(), destination));
		scenarioManager.getIntermediates()
				.forEach(intermediate -> interestLocationsMap.put(intermediate.getId(), intermediate));

		GoalParameter goalParameter = new GoalParameter();

		Area goalArea = scenarioManager.getAreas().stream().filter(area -> area.getId().equals(areaId)).findFirst()
				.get();
		goalParameter.setGoalArea(goalArea);

		Boolean singlePlace = goalsBackPack.getBooleanProperty(singlePlaceName);
		goalParameter.setSinglePlace(singlePlace);
		
		String occupancyType = goalsBackPack.getStringProperty(occupancyTypeName);
		goalParameter.setOccupancyType(OccupancyType.valueOf(occupancyType));

		ArrayList<OpeningHourChunk> openingHours = this.createOpeningHours(goalsBackPack, simulationState);
		goalParameter.setOpeningHours(openingHours);

		ArrayList<Integer> predecessorPreferencesIds = goalsBackPack.getListProperty(predecessorPreferencesName);
		
		if (predecessorPreferencesIds != null && predecessorPreferencesIds.size() > 0) {

			goalParameter.setPredecessorsPreferences(predecessorPreferencesIds);
		} 
		else {

			goalParameter.setPredecessorsPreferences(new ArrayList<Integer>());
		}

		Integer preferenceId = goalsBackPack.getIntegerProperty(preferenceIdName);
		goalParameter.setPreferenceId(preferenceId);

		this.goalParameters.put(areaId, goalParameter);
	}

	public void addPreferenceParameter(PropertyBackPack goalBackPack, 
			double numberOfGoalsForPreference,
			SimulationState simulationState) {

		int preferenceId = goalBackPack.getIntegerProperty(preferenceIdName);

		if (this.preferenceParameters.containsKey(preferenceId)) {

			return;
		}

		PreferenceParameter preferenceParameter = new PreferenceParameter();

		preferenceParameter.setPreferenceId(preferenceId);

		Double lambda = null;
		if(goalBackPack.getDoubleProperty(interarrivalTimeLambdaName) != null) {
			
			lambda = goalBackPack.getDoubleProperty(interarrivalTimeLambdaName);
		}
		
		if(lambda == null) {

			preferenceParameter.setInterarrivalMeasurments(null);
			preferenceParameter.setInterarrivalDistribution(null);
		}
		else {
			
			//1980.0; ikom
			Double openingTime = 1980.0;//12.0*60.0*60.0;//this.createOpeningHours(goalBackPack, simulationState).stream()
			//.mapToDouble(OpeningHourChunk::getDuration).sum() - openingMalus;
		
			preferenceParameter.setInterarrivalMeasurments((openingTime / lambda) * numberOfGoalsForPreference);
			
			IDistribution interarrivalDistribution = DistributionFactory
					.createPoissonDistribution(lambda * numberOfGoalsForPreference);
			preferenceParameter.setInterarrivalDistribution(interarrivalDistribution);
		
		}

		Integer seedId = goalBackPack.getIntegerProperty(seedIdName);
		preferenceParameter.setSeedId(seedId);
		
		Boolean oneTimeGoal = goalBackPack.getBooleanProperty(oneTimeName);
		preferenceParameter.setOneTimePreference(oneTimeGoal);

		Double gammaK = goalBackPack.getDoubleProperty(serviceTimeGammaKName);
		Double gammaTheta = goalBackPack.getDoubleProperty(serviceTimeGammaThetaName);

		if (gammaK != null && gammaTheta != null) {

			IDistribution gammaDistribution = DistributionFactory.createGammaDistribution(gammaK, gammaTheta);
			preferenceParameter.setServiceTimeDistribution(gammaDistribution);
		}

		this.preferenceParameters.put(preferenceId, preferenceParameter);
	}

	private ArrayList<OpeningHourChunk> createOpeningHours(PropertyBackPack goalBackPack,
			SimulationState simulationState) {

		HashMap<Integer, ArrayList<Integer>> openingHoursConfiguration = goalBackPack
				.<Integer> getMatrixProperty(openingHoursName);
		ArrayList<OpeningHourChunk> openingHours = new ArrayList<OpeningHourChunk>();

		if (openingHoursConfiguration != null && openingHoursConfiguration.size() > 0) {

			ArrayList<Integer> startingHoursConfiguration = openingHoursConfiguration.get(0);
			ArrayList<Integer> endingHoursConfiguration = openingHoursConfiguration.get(1);

			for (int iter = 0; iter < startingHoursConfiguration.size(); iter++) {

				openingHours.add(new OpeningHourChunk(startingHoursConfiguration.get(iter) * 60,
						endingHoursConfiguration.get(iter) * 60));
			}
		} else {

			openingHours.add(new OpeningHourChunk(0, simulationState.getSimulationEndTime().intValue()));
		}

		return openingHours;
	}
}
