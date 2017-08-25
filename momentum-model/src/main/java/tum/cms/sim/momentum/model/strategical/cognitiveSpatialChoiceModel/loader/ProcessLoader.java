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

import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.cognitiveFunction.distance.LeakyDistancePerception;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.cognitiveFunction.distance.NoneDistancePerception;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.cognitiveFunction.distance.RealDistancePerception;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.cognitiveFunction.occupancy.NoneOccupancyPerception;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.cognitiveFunction.occupancy.SpaceOccupancyPerception;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.cognitiveFunction.preference.InterestPreference;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.cognitiveFunction.preference.NonePreference;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.cognitiveFunction.reschedule.DecisionFieldTheoryReschedule;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.cognitiveFunction.reschedule.NoneReschedule;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.cognitiveFunction.schedule.ConvexHullSchedule;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.cognitiveFunction.schedule.NearestNeighborSchedule;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.cognitiveFunction.schedule.Rank;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.cognitiveFunction.valuation.SigmoidIntraValuation;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.cognitiveFunction.valuation.WeightedValuation;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.loader.parameter.ProcessParameter;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.memory.PreferenceChunk;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.process.DeductionProcess;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.process.OperationProcess;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.process.PerceptionProcess;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.process.PreferenceProcess;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.process.RescheduleProcess;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.process.ScheduleProcess;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.process.ValuationProcess;

public class ProcessLoader {

	public DeductionProcess loadDeductionProcess(ProcessParameter processParameter) {
		
		DeductionProcess process = new DeductionProcess();
		process.setMinimalServiceTime(processParameter.getMinimalServiceTime());

		return process;
	}
	
	public OperationProcess loadOperationProcess(ProcessParameter processParameter) {
		
		OperationProcess process = new OperationProcess();
		
		return process;
	}
	
	public PerceptionProcess loadPerceptionProcess(ProcessParameter processParameter) {
		
		PerceptionProcess process = new PerceptionProcess();
		
		process.setProximityDistance(processParameter.getProximityDistance());
		
		switch(processParameter.getDistancePerceptionType()) {
		
		case Real:
			
			RealDistancePerception realDistancePerception = new RealDistancePerception();
			realDistancePerception.setDistanceScale(processParameter.getDistanceScale());
			process.setDistancePerception(realDistancePerception);
			
			break;
			
		case Leaky:
	
			LeakyDistancePerception leakyDistancePerception = new LeakyDistancePerception();
			leakyDistancePerception.setLeakyIntegrationAlpha(processParameter.getLeakyIntegrationAlpha());
			leakyDistancePerception.setLeakyIntegrationK(processParameter.getLeakyIntegrationK());
			leakyDistancePerception.setDistanceScale(processParameter.getDistanceScale());
			process.setDistancePerception(leakyDistancePerception);
		
			break;
			
		case None:
		default:
			
			NoneDistancePerception noneDistancePerception = new NoneDistancePerception();
			process.setDistancePerception(noneDistancePerception);
			
			break;
		}
	
		switch(processParameter.getOccupancyPerceptionType()) {

		case Space:
			
			SpaceOccupancyPerception oneDimensionOccupancyPerception = new SpaceOccupancyPerception();
			oneDimensionOccupancyPerception.setParticipatingPenalty(processParameter.getParticipatingPenalty());
			//oneDimensionOccupancyPerception.setProximity(processParameter.getProximityDistance());
			oneDimensionOccupancyPerception.setWaitingPenalty(processParameter.getWaitingPenalty());
			process.setOccupancyPerception(oneDimensionOccupancyPerception);
			
			break;

		case None:				
		default:
			
			NoneOccupancyPerception noneOccupancyPerception = new NoneOccupancyPerception();
			process.setOccupancyPerception(noneOccupancyPerception);
			
			break;
		}
		
		return process;
	}
	
	public ArrayList<PreferenceProcess> loadPreferenceProcess(Collection<PreferenceChunk> preferences, 
			ProcessParameter processParameter) {
		
		ArrayList<PreferenceProcess> preferenceProcesses = new ArrayList<>();
		
		for(PreferenceChunk preference : preferences) {
			
			PreferenceProcess process = new PreferenceProcess();
			
			switch(processParameter.getPreferenceType()) {
		
			case Interest:
				
				InterestPreference interestPreference = new InterestPreference();
//				interestPreference.setMinimalInterarival(processParameter.getMinimalInterarrival());
				interestPreference.setMinimalServiceTime(processParameter.getMinimalServiceTime());
				interestPreference.setGroupDistribution(processParameter.getGroupDistribution());
				interestPreference.setMaximalPedestrian(processParameter.getMaximalPedestrians());
				process.setPreferring(interestPreference);
				
				break;
				
			case None:
			default:
				
				NonePreference nonePreference = new NonePreference();
				process.setPreferring(nonePreference);
				
				break;
			
			}

			process.setPreferenceId(preference.getPreferenceId());
				
			preferenceProcesses.add(process);
		}
	
		return preferenceProcesses;
	}
	
	public RescheduleProcess loadRescheduleProcess(ProcessParameter processParameter) {
		
		RescheduleProcess process = new RescheduleProcess();
		
		switch(processParameter.getRescheduleType()) {
		
		case DecisionFieldTheory:
			
			DecisionFieldTheoryReschedule decisionFieldTheoryReschedule = new DecisionFieldTheoryReschedule();
			decisionFieldTheoryReschedule.setDecisionThreshold(processParameter.getDecisionThreshold());
			decisionFieldTheoryReschedule.setEliminateThreshold(processParameter.getEliminateThreshold());
			process.setReschedule(decisionFieldTheoryReschedule);
			
			break;
			
		case None:
		default:
			
			NoneReschedule noneReschedule = new NoneReschedule();
			process.setReschedule(noneReschedule);
			
			break;
		
		}
		
		process.setValenceThreshold(processParameter.getRescheduleThreshold());
		
		
		return process;
	}
	
	public ScheduleProcess loadScheduleProcess(ProcessParameter processParameter) {
		
		ScheduleProcess process = new ScheduleProcess();
		
		switch(processParameter.getScheduleType()) {
		
		case Rank:
			
			Rank intraAlternative = new Rank();
			process.setScheduler(intraAlternative);
			
			break;
			
		case ConvexHull:
			
			ConvexHullSchedule convexHullSchedule = new ConvexHullSchedule();
			convexHullSchedule.setNearestNeighborThreshold(processParameter.getNearestNeighborThreshold());
			process.setScheduler(convexHullSchedule);
			
			break;
		
		case NearestNeighbor:
		default:
			
			NearestNeighborSchedule nearestNeighborSchedule = new NearestNeighborSchedule();
			process.setScheduler(nearestNeighborSchedule);
			
			break;
		
		}
		
		process.setScheduleSizeDistribution(processParameter.getScheduleSizeDistribution());
		process.setScheduleTimeDistribution(processParameter.getScheduleTimeDistribution());

		return process;
	}
	
	public ValuationProcess loadValuationProcess(ProcessParameter processParameter) {
		
		ValuationProcess process = new ValuationProcess();
		
		switch(processParameter.getValuationType()) {
		
		case IntraAttribute:
			
			SigmoidIntraValuation sigmoidValuation = new SigmoidIntraValuation();
			sigmoidValuation.setIntensityChange(processParameter.getIntensityChange());
			process.setValuation(sigmoidValuation);
			
			break;
			
		case Weighted:
		default:
			
			WeightedValuation weightedValuation = new WeightedValuation();
			weightedValuation.setInterestShare(processParameter.getInterestShare());
			process.setValuation(weightedValuation);
			
			break;
			
		}

		return process;
	}
}
