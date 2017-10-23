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

import java.util.Stack;

import org.apache.commons.math3.util.FastMath;

import tum.cms.sim.momentum.data.agent.pedestrian.types.ITacticalPedestrian;
import tum.cms.sim.momentum.model.tactical.routing.unifiedRoutingModel.UnifiedRoutingConstant;
import tum.cms.sim.momentum.utility.geometry.Vector2D;
import tum.cms.sim.momentum.utility.geometry.operation.GeometryAdditionals;
import tum.cms.sim.momentum.utility.graph.Edge;
import tum.cms.sim.momentum.utility.graph.Graph;
import tum.cms.sim.momentum.utility.graph.GraphTheoryFactory;
import tum.cms.sim.momentum.utility.graph.Path;
import tum.cms.sim.momentum.utility.graph.Vertex;
import tum.cms.sim.momentum.utility.graph.pathAlgorithm.weightOperation.IterativeWeightCalculator;

public class StraigthPathWeightCalculator extends IterativeWeightCalculator {
	
	private ITacticalPedestrian tacticalPedestrian = null;
	
	public void setTacticalPedestrian(ITacticalPedestrian tacticalPedestrian) {
		this.tacticalPedestrian = tacticalPedestrian;
	}

	@Override
	public void preCalculateWeight(Graph graph, Vertex previousVisit, Vertex current, Vertex target) {
		// Nothing	
	}

	@Override
	public void updateWeight(Graph graph, double calculatedWeight, Vertex previousVisit, Vertex target, Vertex current, Vertex successor) {
		// Nothing		
	}
	
	private double sallAngle = 0.0;
	
	public void setSallAngle(double sallAngle) {
		
		this.sallAngle = sallAngle;
	}

	/**
	 * The smaller the better.
	 */
	@Override
	public double calculateWeight(Graph graph, Vertex previousVisit, Vertex target, Vertex current, Vertex successor) {

		double weight = 0.0;
	
		if(successor.getId().intValue() == target.getId().intValue()) {
		
			// next is goal, thus perfect weight
			weight = 0.0;
		}
		else { // next is not goal, compute weight
			
			Vector2D fromPosition = null;
			Vector2D currentPosition = current.getGeometry().getCenter();
			Vector2D toPosition = successor.getGeometry().getCenter();
			
			if(previousVisit == null) {
			
				// previous vertex is unknown, use heading direction
				fromPosition = this.tacticalPedestrian.getPosition().sum(this.tacticalPedestrian.getHeading().rotate(FastMath.PI));
			}
			else {
				
			}
			
			
		}
		
//		GeometryAdditionals.angleBetween0And180(
//				left.getGeometry().getCenter(),
//				this.getGeometry().getCenter(), 
//				right.getGeometry().getCenter());
		
		if(successor != target && previousVisit != null) {
			
		double angle = current.angleBetweenVertex(previousVisit, successor);
		
		Edge nextEdge = graph.getEdge(current, successor);
		double sallDistanceEnhancement = 0.0;
		double longestLegDistanceToDestination = GeometryAdditionals.calculateLongestLegLengthAlongEdge(graph, nextEdge, target, sallAngle);
		
		sallDistanceEnhancement = longestLegDistanceToDestination / current.euklidDistanceBetweenVertex(target);
		if(UnifiedRoutingConstant.SallCalculationAngleInfluence < 1.0) {
			
			
		}

		double sallAngleRelative = 0.0;
		double sallDistancePercent = 1.0;
		
		
				
			sallAngleRelative =  (1 - angle / FastMath.PI) * UnifiedRoutingConstant.SallCalculationAngleInfluence;
			sallDistancePercent = (1 - UnifiedRoutingConstant.SallCalculationAngleInfluence);
		}	

	    return 0.0;//sallDistanceEnhancement * sallDistancePercent + sallAngleRelative;
	}
	
	public Graph findLongPathTree(
			Graph navigationGraph,
			Vector2D pointingPosition,
			Vertex start,
			double angleThreashold,
			double distanceThreshold,
			String graphName) {
		
		// identify assumed target
		Vertex pointingTarget = navigationGraph.findVertexClosestToPosition(pointingPosition, null);
		
		Graph longPathTree = GraphTheoryFactory.createGraph(graphName);
		
		
		Stack<Vertex> stack = new Stack<Vertex>();   
		stack.push(start);
		
        Edge nextEdge = null;
        
//        Vertex longestLegEnd = alongEdge.getEnd();  
//        double distanceToDestination = longestLegEnd.euklidDistanceBetweenVertex(target);
//        
//        stack.push(alongEdge);
//
//        int maximalEdges = 25;
//        if(alongEdge.getStart().euklidDistanceBetweenVertex(target) > distanceToDestination) {
//       	 
//	         while (!stack.isEmpty()) {
//	         	
//	             nextEdge = stack.pop();
//	             maximalEdges--;
//	             
//	             if(maximalEdges < 0) {
//	            	 break;
//	             }
//	             
//	             distanceToDestination = nextEdge.getEnd().euklidDistanceBetweenVertex(target);
//	
//	             for (Edge successorEdge : graph.getSuccessorEdges(nextEdge.getEnd())) {   
//	
//	            	 if(successorEdge.getEnd().euklidDistanceBetweenVertex(target) > distanceToDestination) {
//	            		 continue;
//	            	 }
//	            	 
//	            	 // Reference compare, no direct cycles
//	                 if (successorEdge.getEnd() == nextEdge.getStart()) {
//	                	 
//	                     continue;       
//	                 }
//	                 
//	                 // nextEdge(start;end)-vertex-succesorEdge(start;end)
//	                 // calculate angle at vertex between both edges
//	                 double angleBetween = nextEdge.getEnd().angleBetweenVertex(nextEdge.getStart(), successorEdge.getEnd());
//	
//	                 // if the next edge near to target and on a long leg, keep going this direction
//	                 if ((FastMath.PI - angleBetween) <= angleThreashold) {
//	                	 
//	                     stack.push(successorEdge);
//	                     
//	                     if(longestLegEnd.euklidDistanceBetweenVertex(target) > successorEdge.getEnd().euklidDistanceBetweenVertex(target)) { 
//	
//	                    	 longestLegEnd = successorEdge.getEnd(); 
//	                    	 distanceToDestination = longestLegEnd.euklidDistanceBetweenVertex(target);
//	                     }
//	                 }
//	             }
//	         }
//        }
        
        return longPathTree;
    }
}
