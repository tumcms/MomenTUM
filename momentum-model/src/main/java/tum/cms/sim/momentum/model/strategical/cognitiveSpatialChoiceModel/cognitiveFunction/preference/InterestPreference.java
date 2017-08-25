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

package tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.cognitiveFunction.preference;

import java.util.List;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.util.FastMath;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.PedestrianBehaviorModel;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.CognitiveConstant.Actualization;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.memory.GoalChunk;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.memory.PhysicalChunk;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.memory.PreferenceChunk;
import tum.cms.sim.momentum.utility.probability.distrubution.Discret;

public class InterestPreference implements IPreference {
	
	public static double MinimalThreshold = 0.001;	
	public static double MaximalThreshold = 0.999;

	private Double minimalServiceTime = 1.0;
	
	public Double getMinimalServiceTime() {
		return minimalServiceTime;
	}

	public void setMinimalServiceTime(Double minimalServiceTime) {
		this.minimalServiceTime = minimalServiceTime;
	}
	
	private Discret groupDistribution = null;
	
	public Discret getGroupDistribution() {
		return groupDistribution;
	}

	public void setGroupDistribution(Discret groupDistribution) {
		this.groupDistribution = groupDistribution;
	}

	private int maximalPedestrians = 0;
	
	public int getMaximalPedestrians() {
		return maximalPedestrians;
	}

	public void setMaximalPedestrian(int maximalPedestrians) {
		this.maximalPedestrians = maximalPedestrians;
	}

	//private static HashMap<String, ArrayList<Double>> temp = new HashMap<>();
	
	private Pair<Double, Double> currentStartEndPhase = null;
	private Double percentageInterest = null;
	private Double timeTracker = null;
	private Boolean firstTime = true;
	
	@Override
	public Double preferring(List<GoalChunk> goals,
			PreferenceChunk preference,
			SimulationState simulationState,
			PhysicalChunk physical,
			Double cognitiveClock) {
		
		if(firstTime) {
			
			if(preference.getInterarrivalDistribution() != null) {
				
				double relaxationTime = this.calculateRelaxtionTime(preference);
				this.currentStartEndPhase = this.findStartEndPhase(preference, simulationState, physical, relaxationTime);
						
				double accumulateInterst = preference.getMeanInterarrivalTimes() * this.maximalPedestrians;//1894.0;//* 5877;
				this.percentageInterest = this.calculateSigmoidMagnitude(cognitiveClock);
				while(accumulateInterst-- > 0) {
				
					this.percentageInterest = this.calculateSigmoidMagnitude(cognitiveClock);
				}
				
				this.firstTime = false;
			}
			else {
				
				this.percentageInterest = MaximalThreshold;
			}
		}
		else {
			
			boolean isArchieved = false;
			
			for(GoalChunk goal : goals) {
				
				if(goal.getPreference().getActualization() == Actualization.Achieved) {
				
					isArchieved = true;
					break;
				}
			}
			
			if(isArchieved) {
				
				if(!preference.getOneTimePreference()) {
					
					this.currentStartEndPhase = this.findStartEndPhase(preference, simulationState, physical);
						
					percentageInterest = Double.NaN; // restart Sigmoid at start time
					percentageInterest = this.calculateSigmoidMagnitude(cognitiveClock);
				}
				else {
					
					percentageInterest = 0.0; // declare None as interest
				}
			}
			else {
				
				if(preference.getInterarrivalDistribution() != null) {
					
					percentageInterest = this.calculateSigmoidMagnitude(cognitiveClock);
				}
				else {
						
					this.percentageInterest = MaximalThreshold;
				}
				
			}
		}

		return percentageInterest;
	}

	private Double calculateSigmoidMagnitude(Double cognitiveClock) {

		double raisingDuration = currentStartEndPhase.getRight() - currentStartEndPhase.getLeft();
		
		if(percentageInterest == null) { // first Time 
	
			this.timeTracker = -raisingDuration / 2.0 - currentStartEndPhase.getLeft(); // start
		}
		else if(this.percentageInterest.isNaN()) {
			
			this.timeTracker = -raisingDuration / 2.0;
		}
		else {
			
			this.timeTracker += cognitiveClock;
		}
		
		double raisingConstant = (raisingDuration / 2.0) / (-1 * FastMath.log((1.0 / MaximalThreshold - 1.0)));
		double interestMagnitude = 1.0 / (1.0 + FastMath.exp((-1.0 * this.timeTracker) / raisingConstant));
		
		return interestMagnitude;
	}
	
	private Double calculateRelaxtionTime(PreferenceChunk preference) {

		// three times standard deviation is max
		Double maximalInterarrivalTime = preference.getMaximalInterarrivalDistribution().getStandardDeviation() * 3.0 +
				preference.getMaximalInterarrivalDistribution().getMean();
		
		Double relaxationTime = preference.getInterarrivalMeasurments() * 		
				 this.groupDistribution.getMean() *
				 maximalInterarrivalTime; 
		
		return relaxationTime * PedestrianBehaviorModel.getRandom().nextDouble(); // initial relaxation is randomized
	}
	
	private Pair<Double, Double> findStartEndPhase(PreferenceChunk preference, 
			SimulationState simulationState,
			PhysicalChunk physical) {
		
		double startTime = simulationState.getCurrentTime();
		double endTime = startTime + this.calculateRaisingTime(preference ,simulationState, physical);
		
		while(endTime < simulationState.getCurrentTime()) {
			
			startTime = endTime;
			endTime = startTime + this.calculateRaisingTime(preference, simulationState, physical);
		}

		return new MutablePair<Double, Double>(startTime, endTime);
	}
	
	private Pair<Double, Double> findStartEndPhase(PreferenceChunk preference, 
			SimulationState simulationState,
			PhysicalChunk physical,
			double relaxetionTime) {
		
		double startTime = simulationState.getCurrentTime() - relaxetionTime;
		
		double serviceTimeDraw = this.minimalServiceTime;
		
		if(preference.getServiceTimeDistributions() != null) {
			
			serviceTimeDraw = preference.getServiceTimeDistributions().getSample();
		}
		
		if(serviceTimeDraw <= this.minimalServiceTime) {
			
			serviceTimeDraw = this.minimalServiceTime;
		}
		
		double endTime = startTime + 
				this.calculateRaisingTime(preference, simulationState, physical) + 
				serviceTimeDraw;
	
		double raiseTime = 0.0;
		
		while(endTime < simulationState.getCurrentTime()) {
			
			startTime = endTime;
			raiseTime = this.calculateRaisingTime(preference, simulationState, physical);
			endTime = startTime + raiseTime + serviceTimeDraw;
		}

		return new MutablePair<Double, Double>(startTime, endTime);
	}
	
	private double calculateRaisingTime(PreferenceChunk preference, SimulationState simulationState, PhysicalChunk physical) {
		
		try {
			
		double interarrivalDraw = preference.getInterarrivalDistribution().getSample();
		double existingPedestrian = this.maximalPedestrians;//1894.0;// ikom 1894.0; bttw 5877//preference.getInterarrivalMeasurments();
	    double groupSizeMean = this.groupDistribution.getMean();
		
		return existingPedestrian * groupSizeMean * interarrivalDraw;
		} catch (Exception x){
			x =null;
		}
		return 0;
	}
}
