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

package tum.cms.sim.momentum.model.tactical.routing.kneidlModel;

import java.util.Collection;
import java.util.HashMap;

import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrianExtansion;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;
import tum.cms.sim.momentum.model.perceptional.PerceptionalModel;
import tum.cms.sim.momentum.model.tactical.routing.kneidlModel.KneidlConstant.KneidlNavigationType;
import tum.cms.sim.momentum.model.tactical.routing.kneidlModel.navigation.MicroRoutingAlgorithm;
import tum.cms.sim.momentum.model.tactical.routing.kneidlModel.navigation.fastestNavigation.FastestWeightCalculator;
import tum.cms.sim.momentum.model.tactical.routing.kneidlModel.navigation.fastestNavigation.MicroFastestPathDijkstra;
import tum.cms.sim.momentum.model.tactical.routing.kneidlModel.navigation.greedHeuristic.GreedyBeelineHeuristics;
import tum.cms.sim.momentum.model.tactical.routing.kneidlModel.navigation.greedHeuristic.GreedyBeelineWeightCalculator;
import tum.cms.sim.momentum.model.tactical.routing.kneidlModel.navigation.heuristicNavigation.BeelineWeightCalculator;
import tum.cms.sim.momentum.model.tactical.routing.kneidlModel.navigation.heuristicNavigation.MicroBeelineHeuristic;
import tum.cms.sim.momentum.model.tactical.routing.kneidlModel.navigation.sallNavigation.MicroStraigthAndLongLegs;
import tum.cms.sim.momentum.model.tactical.routing.kneidlModel.navigation.sallNavigation.StraigthAndLongLegsCalculator;

public class KneidlPedestrianExtension implements IPedestrianExtansion {
	
	private HashMap<KneidlNavigationType, MicroRoutingAlgorithm> routingAlgorithms =
			new HashMap<KneidlNavigationType, MicroRoutingAlgorithm>();

	private FastestWeightCalculator fastestCalculator = null;
	private BeelineWeightCalculator beelineCalculator = null;

	public MicroRoutingAlgorithm getRoutingAlgorithm() {
		
		return routingAlgorithms.get(navigationType);
	}
	
	public Collection<MicroRoutingAlgorithm> getAllRoutignAlgorithm() {
		
		return routingAlgorithms.values();
	}
	

	public void setCurrentPerception(PerceptionalModel currentPerception) {

		routingAlgorithms.values().forEach(algorithm -> algorithm.setCurrentPerception(currentPerception));
	}

	private KneidlNavigationType navigationType = null;
	
	public void selectNavigationBehavior(KneidlNavigationType navigationType) {

		this.navigationType = navigationType;
	}
	
	public KneidlNavigationType getNavigationType() {
		return navigationType;
	}


	public KneidlPedestrianExtension(String pedestrianWeightNameExtension, IRichPedestrian currentPedestrian) {
		
		fastestCalculator = new FastestWeightCalculator();
		MicroFastestPathDijkstra fastestPath = new MicroFastestPathDijkstra(fastestCalculator, currentPedestrian);
		routingAlgorithms.put(KneidlNavigationType.FastestEuklid, fastestPath);
		
		beelineCalculator = new BeelineWeightCalculator();
		MicroBeelineHeuristic beeline = new MicroBeelineHeuristic(beelineCalculator, currentPedestrian);
		routingAlgorithms.put(KneidlNavigationType.BeelineHeuristic, beeline);


		GreedyBeelineWeightCalculator greedyBeelineCalculator = new GreedyBeelineWeightCalculator();
		routingAlgorithms.put(KneidlNavigationType.GreedyBeelineHeuristic, new GreedyBeelineHeuristics(greedyBeelineCalculator, currentPedestrian));
		
		StraigthAndLongLegsCalculator sallCalculator = new StraigthAndLongLegsCalculator();
		routingAlgorithms.put(KneidlNavigationType.StraightAndLongLegs, new MicroStraigthAndLongLegs(sallCalculator, currentPedestrian));
	}

	public void updateWeightName(int threadNumber) {
	
		this.beelineCalculator.setCalculationWeight(Integer.toString(threadNumber));
		this.fastestCalculator.setCalculationWeight(Integer.toString(threadNumber));
	}
}
