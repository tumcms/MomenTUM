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

package tum.cms.sim.momentum.model.tactical.routing.kneidlModel.navigation.fastestNavigation;

import tum.cms.sim.momentum.model.tactical.routing.kneidlModel.KneidlConstant;
import tum.cms.sim.momentum.model.tactical.routing.kneidlModel.navigation.KneidlDirectWeightCalculator;
import tum.cms.sim.momentum.utility.graph.Edge;
import tum.cms.sim.momentum.utility.graph.Graph;
import tum.cms.sim.momentum.utility.graph.Vertex;

public class FastestWeightCalculator 
	extends KneidlDirectWeightCalculator  {

	public FastestWeightCalculator() {
		super(KneidlConstant.FastestVertexWeightNameSeed);
	}
	
	public double readWeight(Vertex vertex) {
		return vertex.getWeight(this.vertexWeightName);
	}
	
	@Override
	public double calculateWeight(Graph graph, Vertex previousVertex, Vertex target, Vertex current, Vertex successor) {
		
		Edge edge = graph.getEdge(current, successor);
		
		double isInSight = 1.0;
		
		this.currentPerception.isVisible(this.currentPedestrian, edge);

		double meanVelocity = KneidlConstant.FastestMeanSpeed;
		
		if(isInSight == 1.0) {
			
			meanVelocity = edge.getWeight(KneidlConstant.FastestEdgeMeanSpeedWeightName);
		}
		
		double euklideanDistance = current.euklidDistanceBetweenVertex(successor);
		double currentVertexWeight = current.getWeight(this.vertexWeightName);
		
		return currentVertexWeight + euklideanDistance * (1.0 / meanVelocity) ;
	}
}
