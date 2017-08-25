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

package tum.cms.sim.momentum.model.tactical.routing.kneidlModel.navigation.greedHeuristic;

import java.util.Random;

import tum.cms.sim.momentum.model.tactical.routing.kneidlModel.navigation.KneidlIterativeWeightCalculator;
import tum.cms.sim.momentum.utility.graph.Graph;
import tum.cms.sim.momentum.utility.graph.Vertex;
import tum.cms.sim.momentum.utility.probability.HighQualityRandom;

public class GreedyBeelineWeightCalculator 
	extends KneidlIterativeWeightCalculator {
	
	protected Random random = null;
	
	public GreedyBeelineWeightCalculator() {
		random = new HighQualityRandom();
	}

	/**
	 * Successor distance to target / current distance to target 
	 * is 1 if equal (on same circular distance)
	 * is 0-1 if successor is closer then current
	 * is > 1 if successor is farer away then current 
	 */
	@Override
	public double calculateWeight(Graph graph, Vertex previousVertex, Vertex target, Vertex current, Vertex successor)  {

        // Kielar; do not add euklid distance to local decision making
        // will create zick-zack routes in local optimal

        return successor.euklidDistanceBetweenVertex(target);
	}

	@Override
	public void preCalculateWeight(Graph graph, Vertex previousVertex, Vertex current, Vertex target) {
		
		// nothing to do
	}
	
	@Override
	public void updateWeight(Graph graph, double calculatedWeight, Vertex previousVertex, Vertex target, Vertex current, Vertex successor) {
		
		// nothing to do
	}
}
