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

package tum.cms.sim.momentum.model.tactical.routing.kneidlModel.navigation.antGbSallNavigation;

import org.apache.commons.math3.util.FastMath;
import tum.cms.sim.momentum.model.tactical.routing.kneidlModel.KneidlConstant;
import tum.cms.sim.momentum.model.tactical.routing.kneidlModel.navigation.KneidlIterativeWeightCalculator;
import tum.cms.sim.momentum.utility.geometry.operation.GeometryAdditionals;
import tum.cms.sim.momentum.utility.graph.Edge;
import tum.cms.sim.momentum.utility.graph.Graph;
import tum.cms.sim.momentum.utility.graph.Vertex;

public class AntGbSallWeightCalculator 
	extends KneidlIterativeWeightCalculator {
	
	@Override
	public void preCalculateWeight(Graph graph, Vertex previousVertex, Vertex current, Vertex target) {
		
		// nothing to do
	}
	
	/**
	 * Uses the number of pedestrians (pheromone) from the old edge (if exists) the new number of pedestrians 
	 * (pheromone) of the next edge, the direction (angle) to the target and the relative distance enhancement
	 * of the succesor edge towards the target.
	 * 
	 * newPheromone/oldPheromone * angle_between(current-successor-target)/PI * newDistance/oldDistance
	 * 
	 * angle: 
	 * 1 (towards goal) and 0 (wrong direction), 0.5 is PI/2 direction
	 * 
	 * pheromone: 1 is empty edge, 1 + n pedestrians if not empty
	 * is 1 if equal pedestrians on the next edge
	 * is 0-1 if less pedestrians on the next edge
	 * is > 1 if more pedestrian on the next edge
	 * 
	 * beeline: 
	 * Successor distance to target / current distance to target 
	 * is 1 if equal (on same circular distance)
	 * is 0-1 if successor is closer then current
	 * is > 1 if successor is farer away then current
	 * 
	 *  longest leg: like sall 
	 */
	@Override
	public double calculateWeight(Graph graph, Vertex previousVertex, Vertex target, Vertex current, Vertex successor) {
	
		if(successor == target) {
			
			return Double.MAX_VALUE;
		}
		
		Edge nextEdge = graph.getEdge(current, successor);

		double longestLegDistance = GeometryAdditionals.calculateLongestLegLengthAlongEdge(graph, nextEdge, target, KneidlConstant.SallLegAngleThreshold);
		double sallDistanceEnhancement = longestLegDistance / current.euklidDistanceBetweenVertex(target);
		double sallAngleRelative = 1.0;
		double angle = 0.0;
		
		if(successor != target && previousVertex != null) {
			
			angle = current.angleBetweenVertex(previousVertex, successor);	
			double angleInfluence = 0.50;
			sallAngleRelative = (1 - angle / FastMath.PI) * angleInfluence;
		}
		

		double gbDistanceEnhancement = successor.euklidDistanceBetweenVertex(target);

		double weightGB = gbDistanceEnhancement * this.beelineWeight;
		double weightSALL = sallDistanceEnhancement * sallAngleRelative * this.sallWeight;
		
	    return (weightGB + weightSALL) * 1;//weightPheromone;
	}
	
	private double beelineWeight = 0.0;
	private double sallWeight = 0.0;

	public void setBeelineWeight(double beelineWeight) {
		this.beelineWeight = beelineWeight;
	}

	public void setSallWeight(double sallWeight) {
		this.sallWeight = sallWeight;
	}

	@Override
	public void updateWeight(Graph graph, double calculatedWeight, Vertex previousVertex, Vertex target, Vertex current, Vertex successor) {
		
		// nothing to do
	}
}
