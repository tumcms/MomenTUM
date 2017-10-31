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

package tum.cms.sim.momentum.model.tactical.routing.kneidlModel.navigation.unifiedNavigationKielar;

import java.util.HashMap;

import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;
import tum.cms.sim.momentum.model.perceptional.PerceptionalModel;
import tum.cms.sim.momentum.model.tactical.routing.kneidlModel.navigation.KneidlIterativeWeightCalculator;
import tum.cms.sim.momentum.model.tactical.routing.kneidlModel.navigation.antGbSallNavigation.AntGbSallWeightCalculator;
import tum.cms.sim.momentum.model.tactical.routing.kneidlModel.navigation.fastestNavigation.FastestWeightCalculator;
import tum.cms.sim.momentum.model.tactical.routing.kneidlModel.navigation.greedHeuristic.GreedyBeelineWeightCalculator;
import tum.cms.sim.momentum.model.tactical.routing.kneidlModel.navigation.heuristicNavigation.BeelineWeightCalculator;
import tum.cms.sim.momentum.model.tactical.routing.kneidlModel.navigation.sallNavigation.StraigthAndLongLegsCalculator;
import tum.cms.sim.momentum.utility.graph.Graph;
import tum.cms.sim.momentum.utility.graph.Path;
import tum.cms.sim.momentum.utility.graph.Vertex;

public class UnifiedIterativeRoutingCalculator extends KneidlIterativeWeightCalculator {

	protected AntGbSallWeightCalculator antCalculator = null;
	protected BeelineWeightCalculator beelineCalculator = null;
	protected FastestWeightCalculator fastestPathCalculator;
	protected Path fastesPath = null;	
	protected Path beelinePath = null;
	
	private HashMap<Vertex, Double> antGbSallMap = null;
	
	private double currentMaximalAnt = 0.0;
	
	public UnifiedIterativeRoutingCalculator(StraigthAndLongLegsCalculator sallCalculator,
			GreedyBeelineWeightCalculator greedyBeelineCalculator,
			AntGbSallWeightCalculator antCalculator,
			BeelineWeightCalculator beelineCalculator,
			FastestWeightCalculator fastestPathCalculator) {
	
		this.antCalculator = antCalculator;
		this.beelineCalculator = beelineCalculator;
		this.fastestPathCalculator = fastestPathCalculator;
	}

	public void updateCurrentFastestPath(Path fastestPath) {
		
		this.fastesPath = fastestPath;
	}
	
	public void updateCurrentBeelinePath(Path beelinePath) {

		this.beelinePath = beelinePath;
	}
	
	public void updateCurrentPerception(PerceptionalModel currentPerception) {
		
		this.setCurrentPerception(currentPerception);
		
		this.antCalculator.setCurrentPerception(currentPerception);
		this.beelineCalculator.setCurrentPerception(currentPerception);
		this.fastestPathCalculator.setCurrentPerception(currentPerception);
	}
	
	public void updateCurrentPedestrian(IRichPedestrian currentPedestrian) {

		this.setCurrentPedestrian(this.currentPedestrian);
		
		this.antCalculator.setCurrentPedestrian(currentPedestrian);
		this.beelineCalculator.setCurrentPedestrian(currentPedestrian);
		this.fastestPathCalculator.setCurrentPedestrian(currentPedestrian);
	}
	
	@Override
	public void preCalculateWeight(Graph graph, Vertex previousVertex, Vertex current, Vertex target) {

		this.calculateLocalAnt(graph, previousVertex, current, target);
	}

	/**
	 * Unified selects smallest from 0 till 1, the smaller the better
	 * 
	 * Sall selects smallest meter (longest leg), the smaller the better, local optimal
	 * Ant selects smallest in pheromone, the bigger the better, local optimal
	 * Greedy Beeline selects smallest in meter, the smaller the better, local optimal
	 * 
	 * Beeline is direct routing and selects smallest in meter, the smaller the better, global optimal
	 * Fastest is direct routing and selects smallest in seconds, the smaller the better, global optimal
	 */
	@Override
	public double calculateWeight(Graph graph, Vertex previousVertex, Vertex target, Vertex current, Vertex successor) {
		
		double antWeight = this.antGbSallMap.get(successor) / this.currentMaximalAnt; 
	
		if(Double.isNaN(antWeight)) {
			antWeight = 0.0;
		}

		double beelineWeight =  1.0 - (this.beelinePath.getCurrentVertex().getId() == successor.getId() ? 1.0 : 0); // getIntermediate()
		beelineWeight *= beelineWeightProportion;
		
		double fastestWeight = 1.0 - (this.fastesPath.getCurrentVertex().getId() == successor.getId() ? 1.0 : 0); // getIntermediate()
		fastestWeight *= fastestWeightProportion;

		double iterativeWeight = antWeight * iterativeWeightProportion;
		double directWeight = (beelineWeight + fastestWeight) * directWeightProportion;
		
		return directWeight + iterativeWeight;
	}
	
	private double iterativeWeightProportion = -1;
	private double directWeightProportion = -1;
	private double fastestWeightProportion = -1;	
	private double beelineWeightProportion = -1;
	
	public void setIterativeWeightProportion(double iterativeWeightProportion) {
		this.iterativeWeightProportion = iterativeWeightProportion;
	}

	public void setFastestWeightProportion(double fastestWeightProportion) {
		this.fastestWeightProportion = fastestWeightProportion;
	}

	public void setBeelineWeightProportion(double beelineWeightProportion) {
		this.beelineWeightProportion = beelineWeightProportion;
	}
	
	public void setDirectWeightProportion(double directWeightProportion) {
		this.directWeightProportion = directWeightProportion;
	}

	
	@Override
	public void updateWeight(Graph graph, double calculatedWeight, Vertex previousVertex, Vertex target, Vertex current, Vertex successor) {
		
		// nothing to do
	}
	
	private void calculateLocalAnt(Graph graph, Vertex previousVertex, Vertex current, Vertex target) {

		this.antGbSallMap = new HashMap<Vertex, Double>();

		for(Vertex successor : graph.getSuccessorVertices(current)) {
			
			antGbSallMap.put(successor, this.antCalculator.calculateWeight(graph, previousVertex, target, current, successor));
		}
		
		this.currentMaximalAnt = antGbSallMap.values().stream().mapToDouble(value -> value.doubleValue()).max().getAsDouble();
	}

}
