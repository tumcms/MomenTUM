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

package tum.cms.sim.momentum.model.tactical.routing.cognitiveRoutingModel.underlyingModels;

import java.util.ArrayList;
import java.util.Comparator;

import tum.cms.sim.momentum.utility.graph.Graph;
import tum.cms.sim.momentum.utility.graph.Vertex;
import tum.cms.sim.momentum.utility.graph.pathAlgorithm.weightOperation.DirectWeightCalculatur;

public class CognitiveRoutingDirectCalculator extends DirectWeightCalculatur {
	
	@Override
	public double calculateWeight(Graph graph, Vertex previousVertex, Vertex target, Vertex current, Vertex successor) {
	
		double euklideanDistance = current.euklidDistanceBetweenVertex(successor);
		double currentVertexWeight = current.getWeight(this.vertexWeightName);
		
		return currentVertexWeight + euklideanDistance;
	}

	protected Comparator<Vertex> weightComparator = new Comparator<Vertex>() {

		@Override
		public int compare(Vertex o1, Vertex o2) {
			
			Double v1 = o1.getWeight(CognitiveRoutingDirectCalculator.this.vertexWeightName);
			Double v2 = o2.getWeight(CognitiveRoutingDirectCalculator.this.vertexWeightName);
			return Double.compare(v2, v1);
		}
	};
	
	protected String vertexWeightName = null;	
	protected String startVertexWeightName = null;
	
	public void setCalculationWeight(String weightExtension) {
		
		this.vertexWeightName = startVertexWeightName + weightExtension;
	}

	public CognitiveRoutingDirectCalculator(String vertexWeightName) {
		
		this.startVertexWeightName = vertexWeightName;
		this.vertexWeightName = startVertexWeightName;
	}
	
	@Override
	public void initializeWeightsForStart(Graph graph, Vertex startVertex) {

    	graph.getVertex(startVertex.getId()).setWeight(this.vertexWeightName, 0.0);
	}
	
	@Override
	public void updateWeight(Graph graph, double calculatedWeight, Vertex previousVisit, Vertex target, Vertex current, Vertex successor) {
		
		successor.setWeight(this.vertexWeightName, calculatedWeight);
	}
	
	@Override
	public boolean compareWeight(Vertex successor, double weight) {
	
		return successor.getWeight(this.vertexWeightName) > weight;
	}

	@Override
	public ArrayList<Vertex> sort(ArrayList<Vertex> candidates) {
		
		candidates.sort(weightComparator);
		return candidates;
	}
	
	@Override
	public void initalizeWeights(Graph graph) {
		
		graph.getVertices().forEach(vertex -> vertex.setWeight(this.vertexWeightName, Double.MAX_VALUE));
	}
	
	@Override
	public void removeWeights(Graph graph) {
		graph.getVertices().forEach(vertex -> vertex.removeWeight(this.vertexWeightName));
	}
}
