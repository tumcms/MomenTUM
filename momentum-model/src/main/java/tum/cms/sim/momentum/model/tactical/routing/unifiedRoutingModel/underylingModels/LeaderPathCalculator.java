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

package tum.cms.sim.momentum.model.tactical.routing.unifiedRoutingModel.underylingModels;

import java.util.Collection;
import java.util.HashMap;

import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.tactical.routing.unifiedRoutingModel.UnifiedRoutingConstant;
import tum.cms.sim.momentum.utility.graph.Edge;
import tum.cms.sim.momentum.utility.graph.Graph;
import tum.cms.sim.momentum.utility.graph.Vertex;

public class LeaderPathCalculator extends UnifiedSocialCalculator {

	@Override
	public void globalUpdateWeight(Graph graph, Collection<IRichPedestrian> pedestrians, SimulationState simulationState) {
		
		Edge edge = null;
		Double numberOfPeds = 0.0;
		
		HashMap<Edge, Double> sumPedestriansOnEdge = new HashMap<Edge, Double>(); 

		if(pedestrians.size() > 0) {
			
			for(IRichPedestrian pedestrian : pedestrians) {
				
				// new pedestrian could not find a previous node,
				if(pedestrian.getRoutingState() == null || pedestrian.getRoutingState().getLastVisit() == null) {
					
					continue;
				}
				
				if(pedestrian.getRoutingState().getLastVisit().getId() == -1) { // pseudo state
					
					continue;
				}
				
				// on the route from previous to current intermediate vertex
				edge = graph.getEdge(pedestrian.getRoutingState().getLastVisit(),
						pedestrian.getRoutingState().getNextVisit());
			
				if(edge == null) { // no edge found? 
					
					continue;
				}
				
				numberOfPeds = sumPedestriansOnEdge.get(edge); 
				numberOfPeds = numberOfPeds == null ? 0.0 : numberOfPeds;
				
				sumPedestriansOnEdge.put(edge, numberOfPeds + 1.0);
			}
			
			for(Edge updateEdge : graph.getAllEdges()) {
				
				numberOfPeds = sumPedestriansOnEdge.get(updateEdge);
				numberOfPeds = numberOfPeds == null ? 0.0 : numberOfPeds;

				double oldNumber = updateEdge.getWeight(UnifiedRoutingConstant.NumberOfPedestriansOnEdge);
				
				if(numberOfPeds < oldNumber) {
					
					numberOfPeds = oldNumber;
				}
				// damit ist p(t) = (t* -0.00167(je Sekunde) + 1 der Abfall
				
				if(UnifiedRoutingConstant.LeaderBoundary > numberOfPeds) {
					
					// for each second 
					// 10 += 0.05 * (-0.0417)
					numberOfPeds += simulationState.getTimeStepDuration() * UnifiedRoutingConstant.LostPedsPerSecond;
					
					if(numberOfPeds < 0.0) {
						
						numberOfPeds = 0.0;
					}
					
					updateEdge.setWeight(UnifiedRoutingConstant.NumberOfPedestriansOnEdge, numberOfPeds);
				}
				else {
					
					updateEdge.setWeight(UnifiedRoutingConstant.NumberOfPedestriansOnEdge, UnifiedRoutingConstant.LeaderBoundary);
					
//						for(Edge radiationEdge : graph.getSuccessorEdges(updateEdge.getStart())) {
//							
//							if(radiationEdge.equals(updateEdge)) {
//								continue;
//							}
//							
//							radiationEdge.setWeight(KielarRoutingConstant.NumberOfPedestriansOnEdge, 
//									numberOfPeds / KielarRoutingConstant.PedestrianRadiationDrop);
//						}
				}
			}
		}
	}

	@Override
	public double calculateWeight(Graph graph, Vertex previousVisit, Vertex target, Vertex current, Vertex successor) {
	
		Edge edge = graph.getEdge(current, successor);
		double pedestrianOnEdge = edge.getWeight(UnifiedRoutingConstant.NumberOfPedestriansOnEdge);
		
		double herdingWeight = 0.0;
		
		if(pedestrianOnEdge > 0.0) {
			
			//double edgeLength = current.euklidDistanceBetweenVertex(successor);
			herdingWeight = pedestrianOnEdge;//edgeLength / pedestrianOnEdge; 
		}
		
		return herdingWeight;
	}
}
