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

package tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.cognitiveFunction.distance;

import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.CognitiveConstant;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.memory.GoalChunk;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.memory.PhysicalChunk;
import tum.cms.sim.momentum.utility.geometry.Vector2D;
import tum.cms.sim.momentum.utility.graph.Graph;
import tum.cms.sim.momentum.utility.graph.Path;
import tum.cms.sim.momentum.utility.graph.Vertex;
import tum.cms.sim.momentum.utility.graph.pathAlgorithm.ShortestPathAlgorithm;
import tum.cms.sim.momentum.utility.graph.pathAlgorithm.weightOperation.AStarEuklidWeightCalculator;

public class RealDistancePerception implements IDistancePerception {

private Double distanceScale = null;
	
	public Double getDistanceScale() {
		return distanceScale;
	}

	public void setDistanceScale(Double distanceScale) {
		this.distanceScale = distanceScale;
	}
	
	@Override
	public void perceptDistance(GoalChunk goal, PhysicalChunk physical, SimulationState simulationState) {
		
		
		Vector2D position = physical.getThisPedestrian().getPosition();
		Double distance = 0.0;
		
		if(goal.getVisible()) {
			
			distance = position.distance(goal.getPointOfInterest());
		}
		else {
			
			Graph graph = physical.getScenario().getGraph();
			Vertex start = graph.findVertexClosestToPosition(
					physical.getThisPedestrian().getPosition(),
					null);
			
			Vertex end = graph.getGeometryVertex(goal.getGoalArea().getGeometry());
			
			distance = graph.getDistance(start, end);
			
			if(distance == null) {
				
				AStarEuklidWeightCalculator calculator = new AStarEuklidWeightCalculator(CognitiveConstant.DistancePerceptionWeight,
						String.valueOf(simulationState.getCalledOnThread()));
						
				ShortestPathAlgorithm dijkstraAlgorithm = new ShortestPathAlgorithm(calculator);
						
				calculator.initalizeWeights(graph);
				Path path = dijkstraAlgorithm.calculateShortestPath(graph, start, end);
				graph.setDistance(path);
				distance = path.distance();
			}
		}
		
		goal.setDistance(distance / this.distanceScale);
	}
}
