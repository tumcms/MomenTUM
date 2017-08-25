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

package tum.cms.sim.momentum.model.strategical.odMatrixModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import tum.cms.sim.momentum.data.agent.pedestrian.state.strategic.StrategicalState;
import tum.cms.sim.momentum.data.agent.pedestrian.state.tactical.TacticalState.Behavior;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrian;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrianExtansion;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IStrategicPedestrian;
import tum.cms.sim.momentum.data.layout.area.Area;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.strategical.DestinationChoiceModel;
import tum.cms.sim.momentum.utility.geometry.Vector2D;
import tum.cms.sim.momentum.utility.probability.ProbabilitySet;

public class ODMatrixStrategical extends DestinationChoiceModel {

	private Double proximity = 2.0;
	
	private static String odMatrixName = "originDestination";
	private static String fulfilmentOverallDurationName = "fulfilmentOverallDuration";
	private static String fulfilmentDurationName = "fulfilmentDuration";
	private static String serviceTimeDistributionName = "serviceTimeDistribution";
	private static String behaviorTypeName = "behaviorType";
	
	protected Map<Area, Behavior> behaviorTypeMap = new LinkedHashMap<>();
	protected double fulfilmentOverallDuration = 0.0;
	protected Map<Area, Double> fulfilmentMap = null;
	protected Map<Integer, ProbabilitySet<Double>> serviceTimeDistributions = null;
	protected List<ODMatrixState> odMatrices = new ArrayList<>();
	
	@Override
	public void callPreProcessing(SimulationState simulationState) {

		if(this.properties.<String>getListProperty(behaviorTypeName) != null) {
			
			this.behaviorTypeMap = this.createMappingBehaviorTask(this.properties.<String>getListProperty(behaviorTypeName));
		}
				
		if(this.properties.<Double>getListProperty(fulfilmentDurationName) != null) {
			
			this.fulfilmentMap = this.createMappingFulfillment(this.properties.<Double>getListProperty(fulfilmentDurationName));
		}
		else if(this.properties.getMatrixProperty(serviceTimeDistributionName) != null) {
			
			HashMap<Integer, ArrayList<Double>> serviceTimeCsv = this.properties.getMatrixProperty(serviceTimeDistributionName);
			
			try {
				
				this.loadServiceFileCsv(serviceTimeCsv);
			} 
			catch (IOException e) {

				e.printStackTrace();
			}
		}
		else if(this.properties.getDoubleProperty(fulfilmentOverallDurationName) != null) {
			
			this.fulfilmentOverallDuration = this.properties.getDoubleProperty(fulfilmentOverallDurationName);
		}
		
		if(this.properties.getMatrixProperty(ODMatrixStrategical.odMatrixName) != null) {
			
			this.createMappingOd(this.properties.getMatrixProperty(ODMatrixStrategical.odMatrixName));
		}
	}

	@Override
	public void callPedestrianBehavior(IStrategicPedestrian pedestrian, SimulationState simulationState) {
	
		if(pedestrian.getStrategicalState() == null ||
		   pedestrian.getStrategicalState().getTacticalBehavior() == Behavior.None) {
			
			pedestrian.setStrategicalState(new StrategicalState(
					this.choosetNextTarget(pedestrian, simulationState),
					Behavior.Routing));
		}
		else {
			
			this.executeBehavior(pedestrian, simulationState);
		}
	}

	@Override
	public IPedestrianExtansion onPedestrianGeneration(IRichPedestrian pedestrian) {
		
		return new ODMatrixExtension();
	}
	
	@Override
	public void onPedestrianRemoval(IRichPedestrian pedestrian) { /* nothing to do */ }
	
	@Override
	public void callBeforeBehavior(SimulationState simulationState, Collection<IRichPedestrian> pedestrians) { /* nothing to do */ }

	@Override
	public void callPostProcessing(SimulationState simulationState) { 	/* nothing to do */ }

	@Override
	public void callAfterBehavior(SimulationState simulationState, Collection<IRichPedestrian> pedestrians)  { 	/* nothing to do */ }
	
	private void executeBehavior(IStrategicPedestrian pedestrian, SimulationState simulationState) {
		
		Behavior behavior = Behavior.Routing;
		Area nextTarget = pedestrian.getNextNavigationTarget();
		ODMatrixExtension extension = (ODMatrixExtension)pedestrian.getExtensionState(this);
		
		if((perception.isVisible(pedestrian.getPosition(),
				pedestrian.getNextNavigationTarget().getPointOfInterest()) && 
				this.isClose(nextTarget, pedestrian, behavior)) ||
				this.checkInBehavior(extension, pedestrian.getBehaviorTask())) {

			if(!this.behaviorTypeMap.isEmpty()) {

				behavior = this.behaviorTypeMap.get(pedestrian.getStrategicalState().getNextTargetArea());
			}
			
			Double waitingTime = null;
			
			if(nextTarget.getGeometry().contains(pedestrian.getPosition())) {
				
				if(extension.getCurrentWaitingTime() == null) { // now reached!
					
					waitingTime = this.selectNextWaitingTime(pedestrian);	
					extension.setCurrentWaitingTime(waitingTime);
				}
				else if (extension.getCurrentWaitingTime() <= 0.0) { // at the target finished
					
					extension.setCurrentWaitingTime(null);
					nextTarget = this.choosetNextTarget(pedestrian, simulationState);
					behavior = Behavior.Routing;
				}
				else { // at the target waiting
					
					waitingTime = extension.getCurrentWaitingTime();
					waitingTime -= simulationState.getTimeStepDuration();
					extension.setCurrentWaitingTime(waitingTime);
				}
			}		
		}
		else {
			
			behavior = Behavior.Routing;
		}
			
		pedestrian.setStrategicalState(new StrategicalState(
				nextTarget,
				behavior));
	}
	
	private Area choosetNextTarget(IStrategicPedestrian pedestrian, SimulationState simulationState) {
		
		Area targetArea = null;
		ArrayList<Area> startingAreas = new ArrayList<Area>();
		startingAreas.addAll(this.scenarioManager.getOrigins());
		startingAreas.addAll(this.scenarioManager.getIntermediates());
		
		for(Area startingArea : startingAreas) {
			
			if(startingArea.getGeometry().contains(pedestrian.getPosition())) {
				
				targetArea = this.selectState(simulationState)
						.getOdMatrix()
						.get(startingArea)
						.getItemEquallyDistributed();
				break;
			}				
		}
		
		if(targetArea == null) {
			
			Area foundStart = null;
			double nearestOriginDistance = Double.MAX_VALUE;
			
			for(Area startingArea : startingAreas) {
				
				double currentDistance = startingArea.getGeometry().distanceBetween(pedestrian.getPosition());
				
				if(currentDistance < nearestOriginDistance) {
					
					foundStart = startingArea;
					nearestOriginDistance = currentDistance;
				}
			}
			
			targetArea = this.selectState(simulationState)
					.getOdMatrix()
					.get(foundStart)
					.getItemEquallyDistributed();
		}
		
		return targetArea;
	}
	
	private ODMatrixState selectState(SimulationState simulationState) {
		
		if(odMatrices.size() > 1) {
			
			for(ODMatrixState matrixState : odMatrices) {
			
				if(matrixState.getStartTime() >= simulationState.getCurrentTime()) {
					
					return matrixState;
				}
			}
		}
		
		return odMatrices.get(0);
	}
	
	private Double selectNextWaitingTime(IStrategicPedestrian pedestrian) {
		
		Double waitingTime = 0.0;
		
		if(this.fulfilmentMap != null) {
			
			if(this.fulfilmentMap.size() != 0) {
			
				waitingTime = this.fulfilmentMap.get(pedestrian.getStrategicalState().getNextTargetArea());
			}
		}
		else if(serviceTimeDistributions != null && 
				serviceTimeDistributions.containsKey((pedestrian.getStrategicalState().getNextTargetArea().getId()))) {
			
			waitingTime = serviceTimeDistributions.get(pedestrian.getStrategicalState().getNextTargetArea().getId()).getItemEquallyDistributed();
		}
		else {
			
			waitingTime = this.fulfilmentOverallDuration;
		}
		
		return waitingTime;
	}
	
	private Boolean isClose(Area target, IPedestrian pedestrian, Behavior type) {
		
		Vector2D nearestPointToTarget = target.getGeometry()
					.getPointOnPolygonClosestToVector(pedestrian.getPosition());
			
		Boolean proximity = nearestPointToTarget.distance(pedestrian.getPosition()) <= this.proximity ||
				target.getPointOfInterest().distance(pedestrian.getPosition()) <= this.proximity || 
					target.getGeometry().contains(pedestrian.getPosition());

		return proximity;
	}
	
	private boolean checkInBehavior(ODMatrixExtension extension, Behavior behavior) {
		
		return extension.getCurrentWaitingTime() != null ||
				behavior == Behavior.Queuing ||
				behavior == Behavior.Staying;
	}
	
	private void createMappingOd(HashMap<Integer, ArrayList<Double>> odMatrixConfiguration) {

		ArrayList<Area> ends = new ArrayList<Area>();
		ends.addAll(this.scenarioManager.getDestinations());
		ends.addAll(this.scenarioManager.getIntermediates());
		
		ArrayList<Area> starts = new ArrayList<Area>();
		starts.addAll(this.scenarioManager.getOrigins());
		starts.addAll(this.scenarioManager.getIntermediates());
		
		this.odMatrices = new ArrayList<>();
		ArrayList<Area> matrixDestinations = new ArrayList<>();
		
		LinkedHashMap<Area, ProbabilitySet<Area>> odMatrix = null;
		ODMatrixState currentMatrix = null;
		
		// For each line of the matrix do
		int lineNumber = 0;
		for(ArrayList<Double> line : odMatrixConfiguration.values()) {
			
			if(lineNumber == 0) { // first line and first element with a matrix
				
				matrixDestinations.clear();
				odMatrix = new LinkedHashMap<Area, ProbabilitySet<Area>>();
				currentMatrix = new ODMatrixState();
				currentMatrix.setOdMatrix(odMatrix);
				this.odMatrices.add(currentMatrix);
				
				// stored start time
				currentMatrix.setStartTime(line.get(0)); 
				
				// create destination list
				for(int iter = 1; iter < line.size(); iter++) {
					
					Double destinationId = line.get(iter);
					
					Optional<Area> destination = ends
							.stream()
							.filter(area -> area.getId().equals(destinationId.intValue()))
							.findFirst();
					
					if(destination.isPresent()) {
						
						matrixDestinations.add(destination.get());
					}
				}
				lineNumber++;
			}
			else if(line.isEmpty()) { // OD matrix finished
				
				lineNumber = 0;
			}
			else {
				
				// get start area
				Optional<Area> start = starts
						.stream()
						.filter(area -> area.getId().equals(line.get(0).intValue()))
						.findFirst();
				
				if(start.isPresent()) { // get probabilities
					
					Area startArea = start.get();
					odMatrix.put(startArea, new ProbabilitySet<Area>());
					
					for(int iter = 1; iter < line.size(); iter++) {
						
						odMatrix.get(startArea).append(matrixDestinations.get(iter -1), line.get(iter));
					}
				}
				lineNumber++;
			}
			
		}
	}
	
	private HashMap<Area, Double> createMappingFulfillment(ArrayList<Double> fulfillmentTimes) {
		
		ArrayList<Area> targets = new ArrayList<Area>();
		targets.addAll(this.scenarioManager.getDestinations());
		targets.addAll(this.scenarioManager.getIntermediates());
		
		HashMap<Area, Double> fulfillmentMap = new HashMap<Area, Double>();
		
		for(int iter = 0; iter < fulfillmentTimes.size(); iter++) {
			
			for(Area area : targets) {
				
				if(area.getId() == iter) {
					
					double time = fulfillmentTimes.get(iter) == null ? 0.0 : fulfillmentTimes.get(iter);
					fulfillmentMap.put(area, time);
					break;
				}
			}
		}

		return fulfillmentMap;
	}
	
	private HashMap<Area, Behavior> createMappingBehaviorTask(ArrayList<String> behaviorTypes) {
		
		ArrayList<Area> targets = new ArrayList<Area>();
		targets.addAll(this.scenarioManager.getDestinations());
		targets.addAll(this.scenarioManager.getIntermediates());
		
		HashMap<Area, Behavior> behaviorMap = new HashMap<Area, Behavior>();
		
		for(int iter = 0; iter < behaviorTypes.size(); iter++) {
			
			for(Area area : targets) {
				
				if(area.getId() == iter) {
					
					String task = behaviorTypes.get(iter) == null ? "Staying" : behaviorTypes.get(iter);
					behaviorMap.put(area, Behavior.valueOf(task));
					break;
				}
			}
		}

		return behaviorMap;
	}
	
	private void loadServiceFileCsv(HashMap<Integer, ArrayList<Double>> serviceTimeCsv) throws IOException {
		
		serviceTimeDistributions = new HashMap<>();
		Integer locationId = null;
		
		Iterator<ArrayList<Double>> iterator = serviceTimeCsv.values().iterator();
		ArrayList<Double> line = null;
		
		while(iterator.hasNext()) {

			line = iterator.next();
			
			if(line.size() == 1) { // new location
				
				// read location id
				locationId = line.get(0).intValue();
				serviceTimeDistributions.put(locationId, new ProbabilitySet<Double>());
			}
			else if(line.size() == 0) { // new time slot for location
				
				continue;
			}
			else { // new current distribution entry

				serviceTimeDistributions.get(locationId).append(line.get(0), line.get(1));
			}
		}
	}
}
