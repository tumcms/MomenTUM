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

package tum.cms.sim.momentum.model.layout.graph.vertex.vertexPruneModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.math3.util.FastMath;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.layout.graph.GraphOperation;
import tum.cms.sim.momentum.utility.geometry.operation.GeometryAdditionals;
import tum.cms.sim.momentum.utility.graph.GraphTheoryFactory;
import tum.cms.sim.momentum.utility.graph.Vertex;

/**
 * Instantiated by GraphType <code>VertexRemoveSimple</code>
 * @param precision the minimum distance between two vertices
 * 
 */
public class VertexSimplePruneModel extends GraphOperation {
	
	private static String mergeDistanceName = "mergeDistance";

	@Override
	public void callPreProcessing(SimulationState simulationState) {

		double mergeDistance = this.properties.getDoubleProperty(mergeDistanceName);

		List<Vertex> vertexList = this.scenarioManager.getGraph().getVertices()
				.stream()
				.filter(vertex -> !vertex.isSeed())
				.collect(Collectors.toList());
		
		List<Vertex> seedVertices = this.scenarioManager.getGraph().getVertices()
				.stream()
				.filter(vertex -> vertex.isSeed())
				.collect(Collectors.toList());
		
		List<Vertex> prunedVertexList = new ArrayList<Vertex>();
		boolean prune = true;		
		
		for(Vertex seed : seedVertices) {
			
			for(int iter = 0; iter < vertexList.size(); iter++) {
				
				if(vertexList.get(iter).euklidDistanceBetweenVertex(seed) < mergeDistance) {
					
					vertexList.remove(iter--);
				}
			}
		}
		
		while(prune) {
			
			prune = this.vertexPruner(vertexList, prunedVertexList, mergeDistance);
		}
		
		this.scenarioManager.getGraph().clearGraph();
		prunedVertexList.stream().forEach(vertex -> this.scenarioManager.getGraph().addVertex(vertex));
		seedVertices.stream().forEach(vertex -> this.scenarioManager.getGraph().addVertex(vertex));
	}

	@Override
	public void callPostProcessing(SimulationState simulationState) {
		
		// nothing to do
	}

	/**
	 * Processes a list of vertices to be pruned. Returns true if vertices were pruned, false if there
	 * are no more vertices to be pruned
	 * @param vertexList
	 * @param prunedVertexList
	 * @param precision
	 * @return
	 */
	private boolean vertexPruner(Collection<Vertex> vertexList, List<Vertex> prunedVertexList, double precision) {
		List<Vertex> verticesToMerge = new ArrayList<Vertex>();
		double dist = FastMath.sqrt(2*FastMath.pow(precision, 2.));
		
		for(Vertex current : vertexList) {
			
			vertexList.stream().forEach(cand -> {
				
				if(current.euklidDistanceBetweenVertex(cand) <= dist) {
			
					verticesToMerge.add(cand);
				}
			});
			
			if(verticesToMerge.size() > 1) {
				
				prunedVertexList.add(
					GraphTheoryFactory.createVertexCyleBased(
						GeometryAdditionals.calculateVertexCenter(verticesToMerge)));
			}
			else {
				
				prunedVertexList.add(current);
			}
				
			vertexList.removeAll(verticesToMerge);
			
			return true;
			
		}
		
		return false;
	}
}
