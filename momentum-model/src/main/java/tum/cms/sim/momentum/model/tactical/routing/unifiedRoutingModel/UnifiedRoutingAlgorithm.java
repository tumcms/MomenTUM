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

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;
import tum.cms.sim.momentum.data.agent.pedestrian.types.ITacticalPedestrian;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.support.perceptional.PerceptionalModel;
import tum.cms.sim.momentum.model.tactical.routing.unifiedRoutingModel.underylingModels.AvoidanceCalculator;
import tum.cms.sim.momentum.model.tactical.routing.unifiedRoutingModel.underylingModels.BeelineHeuristicsCalculator;
import tum.cms.sim.momentum.model.tactical.routing.unifiedRoutingModel.underylingModels.DecisionDurationCalculator;
import tum.cms.sim.momentum.model.tactical.routing.unifiedRoutingModel.underylingModels.FastestPathCalculator;
import tum.cms.sim.momentum.model.tactical.routing.unifiedRoutingModel.underylingModels.GreedyBeelineHeuristicsCalculator;
import tum.cms.sim.momentum.model.tactical.routing.unifiedRoutingModel.underylingModels.LeaderPathCalculator;
import tum.cms.sim.momentum.model.tactical.routing.unifiedRoutingModel.underylingModels.RightHandSideCalculator;
import tum.cms.sim.momentum.model.tactical.routing.unifiedRoutingModel.underylingModels.ShortestPathCalculator;
import tum.cms.sim.momentum.model.tactical.routing.unifiedRoutingModel.underylingModels.StraightAndLongLegsCalculator;
import tum.cms.sim.momentum.utility.geometry.Vector2D;
import tum.cms.sim.momentum.utility.graph.Graph;
import tum.cms.sim.momentum.utility.graph.Path;
import tum.cms.sim.momentum.utility.graph.Vertex;
import tum.cms.sim.momentum.utility.graph.pathAlgorithm.IterativeShortestPathAlgorithm;
import tum.cms.sim.momentum.utility.graph.pathAlgorithm.ShortestPathAlgorithm;
import tum.cms.sim.momentum.utility.graph.pathAlgorithm.selectorOperation.SmallestVertexSelector;
import tum.cms.sim.momentum.utility.graph.pathAlgorithm.weightOperation.IterativeWeightCalculator;

public class UnifiedRoutingAlgorithm extends IterativeWeightCalculator {

	private DecisionDurationCalculator decisionDurationCalculator = null;
	private IterativeShortestPathAlgorithm iterativAlgorithm = null;
	
	private ShortestPathAlgorithm bhDirectAlgorithm = null;
	private BeelineHeuristicsCalculator beelineHeuristicCalculator = new BeelineHeuristicsCalculator(UnifiedRoutingConstant.BeelineVertexWeightNameSeed);
	private Path currentBhPath = null;
	
	private ShortestPathAlgorithm spDirectAlgorithm = null;
	private ShortestPathCalculator shortestPathCalculator = new ShortestPathCalculator(UnifiedRoutingConstant.ShortestVertexWeightNameSeed);
	private Path currentSpPath = null;
	
	private StraightAndLongLegsCalculator straightAndLongLegsCalculator = new StraightAndLongLegsCalculator();
	private HashMap<Vertex, Double> currentSallValues = null;
	
	private GreedyBeelineHeuristicsCalculator greedyBeelineHeuristicsCalculator = new GreedyBeelineHeuristicsCalculator();
	private HashMap<Vertex, Double> currentGbHValues = null;

	private FastestPathCalculator fastesPathCalculator = new FastestPathCalculator();
	private HashMap<Vertex, Double> currentFpValues = null;
	
	private LeaderPathCalculator leaderPathCalculator = new LeaderPathCalculator();
	private HashMap<Vertex, Double> currentLpValues = null;
	
	private RightHandSideCalculator rightHandSideCalculator = new RightHandSideCalculator();
	private HashMap<Vertex, Double> currentRhSValues = null;

	private AvoidanceCalculator avoidanceCalculator = new AvoidanceCalculator();
	private HashMap<Vertex, Double> currentAvValues = null;
	
	private UnifiedRoutingPedestrianExtension currentExtension = null;

	public UnifiedRoutingAlgorithm() {
		
		this.iterativAlgorithm = new IterativeShortestPathAlgorithm(this, new SmallestVertexSelector());
		this.bhDirectAlgorithm = new ShortestPathAlgorithm(beelineHeuristicCalculator);
		this.spDirectAlgorithm = new ShortestPathAlgorithm(shortestPathCalculator);
	}
	
	public boolean isInDecisionWaiting(SimulationState simulationState, Graph graph, Vertex current) {
		
		boolean isWaiting = true;
		
		if(this.decisionDurationCalculator.isDeciding()) {
			
			if(this.decisionDurationCalculator.isFinished(simulationState)) {
				
				isWaiting = false;
			}
		}
		else {
			
			//int succesorEdges = graph.getSuccessorEdges(current).size();
			try {
				
			
			Vector2D curPoint = current.getGeometry().getCenter();
			
			int number = (int) graph.getVertices().stream()
					.filter(ver -> ver.getGeometry().getCenter().distance(curPoint) < 20)
					.count();
			
			isWaiting = this.decisionDurationCalculator.computeDecisionDuration(simulationState, number);
			}
			catch(Exception ex) {
				ex = null;
			}
		}
		
		return isWaiting;
	}
	
	public void setCurrentPerception(PerceptionalModel currentPerception) {		
		
		this.fastesPathCalculator.setCurrentPerception(currentPerception);
	}
	
	public void setCurrentPedestrian(ITacticalPedestrian currentPedestrian) {
	
		this.fastesPathCalculator.setCurrentPedestrian(currentPedestrian);
	}
	
	public void updateWeightName(int threadNumber) {
	
		this.beelineHeuristicCalculator.setCalculationWeight(Integer.toString(threadNumber));
		this.shortestPathCalculator.setCalculationWeight(Integer.toString(threadNumber));
	}

	public void setExtension(UnifiedRoutingPedestrianExtension extension) {
		
		this.currentExtension = extension;
		
		if(decisionDurationCalculator == null) {
			
			this.decisionDurationCalculator = new DecisionDurationCalculator(extension);
		}
		
		this.straightAndLongLegsCalculator.setSallAngle(extension.getSallAngle());
	}	

	public void initializePedestrianWeightsForTarget(Graph graph, Vertex target) {
		
		if(this.currentExtension.getShortestWeightProportion() > 0.0) {
			
			this.shortestPathCalculator.initalizeWeights(graph);
		}
		
		if(this.currentExtension.getBeelineWeightProportion() > 0.0) {
			
			this.beelineHeuristicCalculator.initalizeWeights(graph);
		}
	}
	
	public Path route(Graph graph, Vertex start, Vertex target, Vertex previousVisit, Set<Vertex> visited) {
		
		if(this.currentExtension.getShortestWeightProportion() > 0.0) {
			
			this.currentSpPath = this.spDirectAlgorithm.calculateShortestPath(graph, start, target);
		}
		else {
			
			this.currentSpPath = null;
		}
	
		if(this.currentExtension.getBeelineWeightProportion() > 0.0) {
				
			this.currentBhPath = this.bhDirectAlgorithm.calculateShortestPath(graph, start, target);
		}
		else {
			
			this.currentBhPath = null;
		}
	
		return this.iterativAlgorithm.calculateNextPath(graph, visited, previousVisit, start, target);
	}
	
	public void updateGlobalWeights(Graph graph, Collection<IRichPedestrian> pedestrians, SimulationState simulationState) {
		
		fastesPathCalculator.globalUpdateWeight(graph, pedestrians, simulationState);
		leaderPathCalculator.globalUpdateWeight(graph, pedestrians, simulationState);
	}

	@Override
	public void preCalculateWeight(Graph graph, Vertex previousVisit, Vertex current, Vertex target) {
		
		this.currentSallValues = new HashMap<Vertex, Double>();

		for(Vertex successor : graph.getSuccessorVertices(current)) {
			
			currentSallValues.put(successor, this.straightAndLongLegsCalculator.calculateWeight(graph, previousVisit, target, current, successor));
		}
		
		this.currentGbHValues = new HashMap<Vertex, Double>();

		for(Vertex successor : graph.getSuccessorVertices(current)) {
			
			currentGbHValues.put(successor, this.greedyBeelineHeuristicsCalculator.calculateWeight(graph, previousVisit, target, current, successor));
		}		
		
		this.currentFpValues = new HashMap<Vertex, Double>();
		
		for(Vertex successor : graph.getSuccessorVertices(current)) {
			
			currentFpValues.put(successor, this.fastesPathCalculator.calculateWeight(graph, previousVisit, target, current, successor));
		}	
		
		this.currentLpValues = new HashMap<Vertex, Double>();
		
		for(Vertex successor : graph.getSuccessorVertices(current)) {
			
			currentLpValues.put(successor, this.leaderPathCalculator.calculateWeight(graph, previousVisit, target, current, successor));
		}	
		
		this.currentRhSValues = new HashMap<>();
		
		for(Vertex successor : graph.getSuccessorVertices(current)) {
			
			currentRhSValues.put(successor, this.rightHandSideCalculator.calculateWeight(graph, previousVisit, target, current, successor));
		}	
		
		this.currentAvValues = new HashMap<>();
		
		for(Vertex successor : graph.getSuccessorVertices(current)) {
			
			currentAvValues.put(successor, this.avoidanceCalculator.calculateWeight(graph, previousVisit, target, current, successor));
		}	
	}

	@Override
	public double calculateWeight(Graph graph, Vertex previousVisit, Vertex target, Vertex current, Vertex successor) {
		
		if(successor == target) {
			
			return 0.0;
		}
		
		// Spatial Weights
	
		Double sallWeight = (this.currentSallValues.get(successor)) / 
				(currentSallValues.values().stream().mapToDouble(value -> value.doubleValue()).max().getAsDouble());
		sallWeight *= this.currentExtension.getSallWeightProportion(); 
		
		Double gbhWeight = (this.currentGbHValues.get(successor)) / 
				(currentGbHValues.values().stream().mapToDouble(value -> value.doubleValue()).max().getAsDouble());
		gbhWeight *= this.currentExtension.getGreedyBeelineWeightProportion();  

		Double spWeight = 0.0;
		if(this.currentSpPath != null) {
			
			spWeight = 1.0 - (this.currentSpPath.getCurrentVertex().getId() == successor.getId() ? 1.0 : 0.0);
		}
		
		spWeight *= this.currentExtension.getShortestWeightProportion(); 
		
		Double blWeight = 1.0;
		
		if(this.currentBhPath != null) {
			
			blWeight = 1.0 - (this.currentBhPath.getCurrentVertex().getId() == successor.getId() ? 1.0 : 0.0);
		}
		
		blWeight *= this.currentExtension.getBeelineWeightProportion(); 
		
		Double rhsWeight = (this.currentRhSValues.get(successor))/
				(currentRhSValues.values().stream().mapToDouble(value -> value.doubleValue()).max().getAsDouble());
		
		if(Double.isNaN(rhsWeight)) {
			
			rhsWeight = 0.0;
		}

		rhsWeight *= this.currentExtension.getRightHandWeightProportion();
		
		Double spatialWeight = sallWeight + gbhWeight + spWeight + blWeight;// + rhsWeight;
		
		// Herding Weights
		
		//the smaller the better
		Double fpWeight = (this.currentFpValues.get(successor))  / 
				(currentFpValues.values().stream().mapToDouble(value -> value.doubleValue()).max().getAsDouble()) ;
		fpWeight *= this.currentExtension.getFastestWeightProportion();
	
		if(Double.isNaN(fpWeight)) {
			
			fpWeight = 0.0;
		}

		// The bigger the better
		Double lpWeight = 1.0 - (this.currentLpValues.get(successor)) / (UnifiedRoutingConstant.LeaderBoundary);
		
		lpWeight *= this.currentExtension.getLeaderWeightProportion();
		
		if(Double.isNaN(lpWeight)) {
			
			lpWeight = 1.0;
		}

		Double socialWeight = fpWeight + lpWeight;

		// Emotional weights
		
		Double avWeight = (this.currentAvValues.get(successor));// /
				//(currentAvValues.values().stream().mapToDouble(value -> value.doubleValue()).max().getAsDouble());
		avWeight *= currentExtension.getAvoidanceWeightProportion();
		
		if(Double.isNaN(avWeight)) {
			
			avWeight = 1.0;
		}
		
		Double emotionalWeight = avWeight;
	
		if(Double.isNaN(emotionalWeight)) {
			
			emotionalWeight = 1.0;
		}
	
//		if(emotionalWeight <= 0.1) {
//			
//			emotionalWeight = 1.0;
//		}

		// in case of a depth search, follow the direct path algorithms to the next node,
		// this is reseted for each routing call
		
		// finalize 
		return spatialWeight * (1.0 - this.currentExtension.getHerdingProportion()) +
		   socialWeight * this.currentExtension.getHerdingProportion() +
			   emotionalWeight;
	}

	@Override
	public void updateWeight(Graph graph, double calculatedWeight, Vertex previousVisit, Vertex target, Vertex current, Vertex successor) {
		// Nothing
	}
}
