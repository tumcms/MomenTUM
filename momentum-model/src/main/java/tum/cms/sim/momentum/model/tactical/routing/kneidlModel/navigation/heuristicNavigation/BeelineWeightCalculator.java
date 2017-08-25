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

package tum.cms.sim.momentum.model.tactical.routing.kneidlModel.navigation.heuristicNavigation;

import java.util.Random;
import org.apache.commons.math3.util.FastMath;
import tum.cms.sim.momentum.model.tactical.routing.kneidlModel.KneidlConstant;
import tum.cms.sim.momentum.model.tactical.routing.kneidlModel.navigation.KneidlDirectWeightCalculator;
import tum.cms.sim.momentum.utility.graph.Graph;
import tum.cms.sim.momentum.utility.graph.Vertex;
import tum.cms.sim.momentum.utility.probability.HighQualityRandom;

public class BeelineWeightCalculator 
	extends KneidlDirectWeightCalculator {

	protected Random random = null;

	public BeelineWeightCalculator() {
		super(KneidlConstant.BeelineVertexWeightNameSeed);
		
		random = new HighQualityRandom();
	}
	
	public double readWeight(Vertex vertex) {
		return vertex.getWeight(this.vertexWeightName);
	}
	
	@Override
	public double calculateWeight(Graph graph, Vertex previousVertex, Vertex target, Vertex current, Vertex successor) {

		double euklidianDistance = current.euklidDistanceBetweenVertex(successor);
	    double beelineDistance = successor.euklidDistanceBetweenVertex(target);
	    double error = 1.0;
	    		
	    if(KneidlConstant.BeelineRandomError > 0.0) {
	   	 
	    	double randomValue = ((FastMath.abs(random.nextInt()) % 21.0) / 100.0);
	    	error = randomValue % KneidlConstant.BeelineRandomError + (1 - (KneidlConstant.BeelineRandomError/2.0));
	    }

	    double currentWeight = current.getWeight(this.vertexWeightName);
	   
	    return currentWeight + euklidianDistance + beelineDistance * KneidlConstant.BeelineAlpha * error;
    }
	
	@Override
	public void updateWeight(Graph graph, double calculatedWeight, Vertex previousVertex, Vertex target, Vertex current, Vertex successor) {
		
		double euklidianDistance = current.euklidDistanceBetweenVertex(successor);
	    double currentWeight = current.getWeight(this.vertexWeightName);
		successor.setWeight(this.vertexWeightName, euklidianDistance + currentWeight);
	}
}
