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

package tum.cms.sim.momentum.model.strategical.interestModel;

import java.util.ArrayList;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.util.FastMath;

import tum.cms.sim.momentum.data.agent.pedestrian.types.IStrategicPedestrian;
import tum.cms.sim.momentum.data.layout.area.Area;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.PedestrianBehaviorModel;
import tum.cms.sim.momentum.model.strategical.interestModel.InterestConstant.InterestState;
import tum.cms.sim.momentum.utility.probability.ProbabilitySet;

public class InterestCalculator {

	private InterestConfiguration configuration = null;
	
	private Integer locationId = null;
	
	private Area location = null;

	public Area getLocation() {
		return location;
	}

	private Double interestMagnitude = null;
	
	public Double getInterestMagnitude() {
		return interestMagnitude;
	}

	private Double interestPointer = null;
	private Double fulfillmentDuration = null;
	
	private Pair<Double, Double> currentStartEndPhase = null;
	
	public Double getRemainingRaisingTime() {
		
		return (currentStartEndPhase.getRight() - currentStartEndPhase.getLeft()) - interestPointer; 
	}
	
	private InterestState interestState = null;

	public InterestCalculator(InterestStrategical interestStrategical, 
			Area location, 
			InterestConfiguration configuration) {
		
		this.location = location;
		this.locationId = location.getId();
		this.configuration = configuration;
	}
	
	public InterestState calculateState(SimulationState simulationState, IStrategicPedestrian currentPedestrian) {
		
		if(this.interestState == null) {
			
			double relaxetionTime = this.calculateRelaxtionTime();
			this.currentStartEndPhase = this.findStartEndPhase(simulationState, relaxetionTime);
			this.interestMagnitude = this.calculateSigmoidMagnitude(simulationState, this.currentStartEndPhase);
			this.interestState = InterestState.Raising;
		}
		else if(this.interestState == InterestState.Dropped) {
			
			this.currentStartEndPhase = this.findStartEndPhase(simulationState);
			this.interestMagnitude = this.calculateSigmoidMagnitude(simulationState, this.currentStartEndPhase);
			this.interestState = InterestState.Raising;
		}
		else if(this.interestState == InterestState.Raising) {
		
			this.interestMagnitude = this.calculateSigmoidMagnitude(simulationState, this.currentStartEndPhase);
			
			if(this.interestMagnitude >= InterestConstant.MaximalThreshold || 
			   currentPedestrian.getStrategicalState().getNextTargetArea().equals(this.location)) {
			
				this.interestMagnitude = 1.0;
				this.interestState = InterestState.Maximal;
			}
		}
		else if(this.interestState == InterestState.Maximal && 
				(currentPedestrian.getStrategicalState() == null || 
				 currentPedestrian.getStrategicalState().getNextTargetArea().equals(this.location))) {
			
			if(this.calculateReached(currentPedestrian)) {
				
				this.interestState = InterestState.Reached;				
			}
		}
		else if(this.interestState == InterestState.Reached) {
			
			if(this.calculateServiceTime(simulationState, currentPedestrian)) {
				
				this.interestMagnitude = InterestConstant.MinimalThreshold;
				this.interestState = InterestState.Dropped;
				this.interestPointer = Double.NaN;
			}
		}
		
		return interestState;
	}
	
	private Double calculateSigmoidMagnitude(SimulationState simulationState, Pair<Double, Double> currentStartEndPhase) {

		double raisingDuration = currentStartEndPhase.getRight() - currentStartEndPhase.getLeft();
		
		if(interestPointer == null) { // first Time 
	
			interestPointer = -raisingDuration/2.0 + -currentStartEndPhase.getLeft(); // start
		}
		else if(this.interestPointer.isNaN()) {
			
			this.interestPointer = -raisingDuration/2.0;
		}
		else {
			
			interestPointer += simulationState.getTimeStepDuration();
		}
		
		double raisingConstant = (raisingDuration / 2.0) / (-1 * FastMath.log((1.0 / InterestConstant.MaximalThreshold - 1.0)));
		double interestMagnitude = 1.0 / (1.0 + FastMath.exp((-1.0 * interestPointer) / raisingConstant));
		
		return interestMagnitude;
	}

	private Pair<Double, Double> findStartEndPhase(SimulationState simulationState) {
	
		double startTime = simulationState.getCurrentTime();
		double endTime = startTime + this.calculateRaisingTime(simulationState);
		
		while(endTime < simulationState.getCurrentTime()) {
			
			startTime = endTime;
			endTime = startTime + this.calculateRaisingTime(simulationState);
		}

		return new MutablePair<Double, Double>(startTime, endTime);
	}

	// for initialization only!
	private Pair<Double, Double> findStartEndPhase(SimulationState simulationState, double relaxetionTime) {
		
		double startTime = simulationState.getCurrentTime() - relaxetionTime;
		
		double serviceTime = this.drawFromTimeSlot(simulationState, 
				this.configuration.getServiceTimeSlots(locationId),
				this.configuration.getServiceTimeDistributions(locationId));
		
		double endTime = startTime + PedestrianBehaviorModel.getRandom().nextDouble() * 
				(this.calculateRaisingTime(simulationState) + serviceTime);
		
		while(endTime < simulationState.getCurrentTime()) {
			
			serviceTime = this.drawFromTimeSlot(simulationState, 
					this.configuration.getServiceTimeSlots(locationId),
					this.configuration.getServiceTimeDistributions(locationId));
			
			startTime = endTime;
			endTime = startTime + this.calculateRaisingTime(simulationState) + serviceTime;
		}

		return new MutablePair<Double, Double>(startTime, endTime);
	}
	
	private double calculateRaisingTime(SimulationState simulationState) {

		double interarrivalDraw = this.drawFromTimeSlot(simulationState, 
				this.configuration.getInterarrivalSlots(locationId),
				this.configuration.getInterarrivalDistributions(locationId));
		
		double existingPedestrian = this.<Integer>selectFromTimeSlot(simulationState, 
				this.configuration.getPedestrianNumbersSlots(),
				this.configuration.getPedestrianInFlowNumbers());

		double pedestrianCountLocation = this.<Integer>selectFromTimeSlot(simulationState,
				this.configuration.getPedestrianNumbersSlots(),
				this.configuration.getPedestrianCountLocation().get(this.locationId));

		double a = (0.0 - InterestConstant.GrowhtFactorAlpha) /
				(1.0/FastMath.pow(existingPedestrian, 1.5) - 1.0/FastMath.pow(existingPedestrian * InterestConstant.GrowthFactorBeta, 1.5)); 
	    double b = 0.0 - a * 1.0/FastMath.pow(existingPedestrian, 1.5);

		double sprintFactor = a * 1.0/FastMath.pow(pedestrianCountLocation, 1.5) + b;

		return sprintFactor * existingPedestrian * interarrivalDraw;
	}

	private double calculateRelaxtionTime() {
		
		Integer overallMaximalPedestrian = this.configuration.getPedestrianInFlowNumbers()
				.stream()
				.mapToInt(Integer::intValue)
				.sum();
		
		Double overallMaximalInterarrivalTime = this.configuration.getInterarrivalDistributions(locationId)
				.stream()
				.mapToDouble(ProbabilitySet::getMax)
				.max()
				.getAsDouble();
		
		ArrayList<ProbabilitySet<Double>> serviceTimes = this.configuration.getServiceTimeDistributions(locationId);
		Double overallMaximalServiceTime = null;
		
		if(serviceTimes == null) {
			
			overallMaximalServiceTime = 0.0;
		}
		else {
			
			overallMaximalServiceTime = serviceTimes
					.stream()
					.mapToDouble(ProbabilitySet::getMax)
					.max()
					.getAsDouble();	
		}
		
		return overallMaximalPedestrian * overallMaximalInterarrivalTime + overallMaximalServiceTime;
	}
	
	private boolean calculateReached(IStrategicPedestrian currentPedestrian) {
		
		if(currentPedestrian.getStrategicalState().getNextTargetArea() == this.location) {
			
			return currentPedestrian.getNextNavigationTarget().getGeometry().contains(currentPedestrian.getPosition());
		}
		
		return false;
	}
	
	private boolean calculateServiceTime(SimulationState simulationState, IStrategicPedestrian currentPedestrian) {
		
		if(this.fulfillmentDuration == null) {

			this.fulfillmentDuration = this.drawFromTimeSlot(simulationState, 
					this.configuration.getServiceTimeSlots(locationId),
					this.configuration.getServiceTimeDistributions(locationId));

			if(this.fulfillmentDuration == 0.0) {
				
				this.fulfillmentDuration = Double.POSITIVE_INFINITY;
			}
		}
		else {
			
			this.fulfillmentDuration -= simulationState.getTimeStepDuration();
		}
		
		if(this.fulfillmentDuration <= 0.0) {
			
			this.fulfillmentDuration = null;
		}
		
		return this.fulfillmentDuration == null;
	}
	
	private Double drawFromTimeSlot(SimulationState simulationState, 
			ArrayList<Pair<Double,Double>> distributionSlots,
			ArrayList<ProbabilitySet<Double>> distributions) {
			
		if(distributionSlots == null || distributions == null) {
			return 0.0;
		}
		
		Double drawn = null;
		int iter = 0;
		
		for(Pair<Double,Double> distributionFrame : distributionSlots) {
		
			if(distributionFrame.getLeft() <= simulationState.getCurrentTime() &&
			   simulationState.getCurrentTime() <= distributionFrame.getRight()  ) {
				
//				double wSum = 0.0;
//				
//				for(Pair<Double,Double> item : distributions.get(iter).getSet()) {
//					wSum = wSum + item.getValue0() * item.getValue1();
//				}
				drawn = distributions.get(iter).getItemEquallyDistributed();
				break;
			}
			
			iter++;
		}
		
		return drawn;
	}

	private <T> T selectFromTimeSlot(SimulationState simulationState, 
			ArrayList<Pair<Double,Double>> distributionSlots,
			ArrayList<T> selectList) {
		
		T drawn = null;
		int iter = 0;
		
		for(Pair<Double,Double> distributionFrame : distributionSlots) {
		
			if(distributionFrame.getLeft() <= simulationState.getCurrentTime() &&
			   simulationState.getCurrentTime() <= distributionFrame.getRight()) {
				
				drawn = selectList.get(iter);
				break;
			}
			
			iter++;
		}
		
		return drawn;
	}
}
