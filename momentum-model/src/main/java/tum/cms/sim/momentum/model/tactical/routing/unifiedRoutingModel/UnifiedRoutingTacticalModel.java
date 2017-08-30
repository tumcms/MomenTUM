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

package tum.cms.sim.momentum.model.tactical.routing.unifiedRoutingModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.math3.util.FastMath;

import tum.cms.sim.momentum.data.agent.pedestrian.state.tactical.RoutingState;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrianExtansion;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;
import tum.cms.sim.momentum.data.agent.pedestrian.types.ITacticalPedestrian;
import tum.cms.sim.momentum.data.layout.area.AvoidanceArea;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.support.perceptional.PerceptionalModel;
import tum.cms.sim.momentum.model.tactical.routing.RoutingModel;
import tum.cms.sim.momentum.model.tactical.routing.unifiedRoutingModel.UnifiedRoutingConstant.DecisionDuration;
import tum.cms.sim.momentum.utility.graph.Edge;
import tum.cms.sim.momentum.utility.graph.Graph;
import tum.cms.sim.momentum.utility.graph.Path;
import tum.cms.sim.momentum.utility.graph.Vertex;

public class UnifiedRoutingTacticalModel extends RoutingModel {

	private static final String herdingName = "herding";
	private static final String decisionDurationName = "decisionDuration";
	private static final String herdingBoundaryName ="herdingBoundary";
	private static final String lostPerSecondName = "lostPerSecond";
	private static final String calibrationModeName = "calibrationMode";
	private static final String resultModeRandomName = "resultMode";
	private static final String randomModeName = "randomMode";
	private static final String probabiliyModeName = "probabiliyMode";
	private static final String leaderPathName = "leaderPath";
	private static final String fastestPathName = "fastestPath";
	private static final String spatialBoundaryName = "spatialBoundary";

	private HashMap<Integer, UnifiedRoutingAlgorithm> unfiedAlgorithms = new HashMap<Integer, UnifiedRoutingAlgorithm>();
	private UnifiedRoutingAlgorithm globalAlgorithm = new UnifiedRoutingAlgorithm(); 
	
	private UnifiedTypeExtractor typeExtractor = new UnifiedTypeExtractor();

	private Boolean herding = false;
	private Boolean rightHandSide = false;
	private DecisionDuration decisionDuration = DecisionDuration.None;
	
	@Override
	public void callPreProcessing(SimulationState simulationState) {
		
		Integer calibrationMode = this.properties.getIntegerProperty(calibrationModeName);
		HashMap<Integer, ArrayList<Double>> resultMode = this.properties.getMatrixProperty(resultModeRandomName);
		
		Boolean randomMode = this.properties.getBooleanProperty(randomModeName);
		ArrayList<Double> probabilityMode = this.properties.getListProperty(probabiliyModeName) ;
				
		if(calibrationMode != null) {
			
			this.typeExtractor.setCalibrationMode(calibrationMode);
		}
		else { // other modes, no real result, no calibration
			
			this.typeExtractor.setResultMode(resultMode);
		}
		
		if(probabilityMode != null) {
			
			this.typeExtractor.setProbabilityMode(probabilityMode);
		}
		else if(randomMode != null) {
			
			this.typeExtractor.setRandomMode();
		}
		else {
			
			this.typeExtractor.setStockMode();
		}
		
		Double sallAngle = this.properties.getDoubleProperty("sallAngle");
		
		if(sallAngle != null) {
			
			UnifiedRoutingConstant.SallLegAngleThreshold = sallAngle * ((2.0 * FastMath.PI) / 360.0);
		}
		
		Double sallInfluance = this.properties.getDoubleProperty("sallInfluance");
	
		if(sallInfluance != null) {
			
			UnifiedRoutingConstant.SallCalculationAngleInfluence = sallInfluance;
		}
	
		Boolean herding = this.properties.getBooleanProperty(UnifiedRoutingTacticalModel.herdingName);
		
		if(herding != null && herding) {
			
			this.herding = true;
			Double leaderPath = this.properties.getDoubleProperty(leaderPathName);
			Double fastestPath = this.properties.getDoubleProperty(fastestPathName);
			UnifiedRoutingConstant.SpatialBoundary = this.properties.getDoubleProperty(spatialBoundaryName);
			
			if(leaderPath > 0.0) {	
			
				UnifiedRoutingConstant.LeaderBoundary = this.properties.getDoubleProperty(UnifiedRoutingTacticalModel.herdingBoundaryName);
				UnifiedRoutingConstant.LostPedsPerSecond = this.properties.getDoubleProperty(lostPerSecondName);
			}
			
			this.typeExtractor.setHerding(leaderPath, fastestPath);
		}
		
		Boolean rightHandSide = this.properties.getBooleanProperty("rightHandSide");
		
		if(rightHandSide != null) {
			
			this.rightHandSide = rightHandSide;
		}
		
		Double avoidance = this.properties.getDoubleProperty("avoidance");
		
		if(avoidance != null) {
			
			this.typeExtractor.setBaseAvoidance(avoidance);
		}
		
		String decisionDuration = this.properties.getStringProperty(decisionDurationName);
		
		if(decisionDuration != null) {
		
			this.decisionDuration = DecisionDuration.valueOf(decisionDuration);
		}
		
		this.initializeGenerically(this.scenarioManager.getGraph());
	}

	@Override
	public IPedestrianExtansion onPedestrianGeneration(IRichPedestrian pedestrian) {
		
		return new UnifiedRoutingPedestrianExtension(this.typeExtractor, 
				this.herding,
				this.rightHandSide,
				this.decisionDuration,
				UnifiedRoutingConstant.SallLegAngleThreshold);
	}

	@Override
	public void callBeforeBehavior(SimulationState simulationState, Collection<IRichPedestrian> pedestrians) {
		
		// Nothing to do
	}
	
	@Override
	public void callPedestrianBehavior(ITacticalPedestrian pedestrian, SimulationState simulationState) {
		
		Vertex start = this.findNavigationStartPoint(pedestrian, this.perception, this.scenarioManager);
		Vertex end = this.scenarioManager.getGraph().getGeometryVertex(pedestrian.getNextNavigationTarget().getGeometry());	
		
		UnifiedRoutingAlgorithm routingAlgorithm = this.getRoutingAlgorithm(simulationState);
		
		routingAlgorithm.setExtension((UnifiedRoutingPedestrianExtension)pedestrian.getExtensionState(this));
		routingAlgorithm.updateWeightName(simulationState.getCalledOnThread());
		routingAlgorithm.setCurrentPedestrian(pedestrian);
		routingAlgorithm.setCurrentPerception(perception);
		Path route = null;
		
		if(!routingAlgorithm.isInDecisionWaiting(simulationState, this.scenarioManager.getGraph(), start)) {
			
			route = this.navigate(this.perception,
					this.scenarioManager.getGraph(),
					start, 
					end,
					routingAlgorithm);		
		}
		else {
		
			route = new Path(start, start);
		}
		
		RoutingState routingState = this.updateRouteState(this.perception, pedestrian, route);
		pedestrian.setRoutingState(routingState);	
	}
	
	@Override
	public void callAfterBehavior(SimulationState simulationState, Collection<IRichPedestrian> pedestrians) {
		
		globalAlgorithm.updateGlobalWeights(this.scenarioManager.getGraph(),
				pedestrians, 
				simulationState);
	}

	@Override
	public void callPostProcessing(SimulationState simulationState) { /* nothing to do */ }

	@Override
	public void onPedestrianRemoval(IRichPedestrian pedestrian) { /* nothing to do */ }
	
	private Path navigate(PerceptionalModel perception, 
			Graph graph, 
			Vertex start,
			Vertex end,
			UnifiedRoutingAlgorithm routingAlgorithm) {

		routingAlgorithm.initializePedestrianWeightsForTarget(graph, end);
		
		Path path = routingAlgorithm.route(graph, 
			start, 
			end);
		
		return path;
	}
	
	private synchronized UnifiedRoutingAlgorithm getRoutingAlgorithm(SimulationState simulationState) {
		
		int threadNumber = simulationState.getCalledOnThread();
		
		if(!this.unfiedAlgorithms.containsKey(threadNumber)) {
			
			this.unfiedAlgorithms.put(threadNumber, new UnifiedRoutingAlgorithm());
		}
		
		return this.unfiedAlgorithms.get(threadNumber);
	}

	private void initializeGenerically(Graph graph) {

		List<AvoidanceArea> avoidanceAreas = this.scenarioManager.getAvoidances();

		for(Edge edge : graph.getAllEdges()) {
			
			edge.setWeight(UnifiedRoutingConstant.AvoidancePowerOnEdge, 0.0);
		}
		
		for(AvoidanceArea avoidanceArea : avoidanceAreas) {
			
			for(Edge edge : graph.getAllEdges()) {
				
//				if(this.perception.isVisible(edge.getStart().getGeometry().getCenter(), avoidanceArea) ||
//				   this.perception.isVisible(edge.getEnd().getGeometry().getCenter(), avoidanceArea)) {
					
					double distance = FastMath.min(avoidanceArea.getGeometry().distanceBetween(edge.getStart().getGeometry().getCenter()),
							avoidanceArea.getGeometry().distanceBetween(edge.getEnd().getGeometry().getCenter()));
					
					double currentAvoidance = edge.getWeight(UnifiedRoutingConstant.AvoidancePowerOnEdge);
					double changeInAvoidance = FastMath.exp(-0.01 * distance * distance);
					edge.setWeight(UnifiedRoutingConstant.AvoidancePowerOnEdge,
							currentAvoidance + changeInAvoidance);
//				}
			}
		}
		

		for(Edge edge : graph.getAllEdges()) {
			
			edge.setWeight(UnifiedRoutingConstant.FastestEdgeMeanSpeedWeightName, UnifiedRoutingConstant.FastestMeanSpeed);
			edge.setWeight(UnifiedRoutingConstant.ShortestVertexWeightNameSeed, Double.MAX_VALUE);
			edge.setWeight(UnifiedRoutingConstant.NumberOfPedestriansOnEdge, 0.0);
		}
	}
}
