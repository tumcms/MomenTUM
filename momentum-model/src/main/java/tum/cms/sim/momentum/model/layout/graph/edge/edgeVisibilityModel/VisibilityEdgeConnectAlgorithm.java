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

package tum.cms.sim.momentum.model.layout.graph.edge.edgeVisibilityModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.util.FastMath;

import tum.cms.sim.momentum.utility.geometry.AngleInterval2D;
import tum.cms.sim.momentum.utility.geometry.Geometry2D;
import tum.cms.sim.momentum.utility.geometry.GeometryFactory;
import tum.cms.sim.momentum.utility.geometry.Vector2D;
import tum.cms.sim.momentum.utility.geometry.operation.GeometryAdditionals;
import tum.cms.sim.momentum.utility.graph.Graph;
import tum.cms.sim.momentum.utility.graph.Vertex;

public class VisibilityEdgeConnectAlgorithm {

	public void connectVertices(Collection<Geometry2D> blockingGeometries,
    		Graph graph, 
    		Collection<Vertex> knownSeedVertices, 
    		double alpha,
    		double connectObstacleDistance) throws Exception {
		
    	int numberOfAngleSegments = Integer.MAX_VALUE; 
    	alpha = GeometryAdditionals.translateToRadiant(alpha);
    	 
    	if(alpha > 0.0) {
    		
    		numberOfAngleSegments = (int)(((FastMath.PI * 2.0) / alpha));
    	}
    		
        HashMap<Vertex, ArrayList<AngleInterval2D>> vertexAngleIntervals = null;
        
        vertexAngleIntervals = new HashMap<Vertex, ArrayList<AngleInterval2D>>();
        
        for (Vertex vertex : graph.getVertices()) {
        	
        	vertexAngleIntervals.put(vertex, new ArrayList<>());
        }

        ArrayList<Pair<Vertex,Vertex>> sortedPairsList = new ArrayList<>();
        
        for (Vertex vertex : graph.getVertices()) {

        	 for (Vertex vertexOther : graph.getVertices()) {
        		 
        		 if (vertex.equals(vertexOther)) {
        			 continue;
        		 }
        		 
        		 sortedPairsList.add(Pair.of(vertex, vertexOther));
        	 }
        }

        sortedPairsList.sort(new Comparator<Pair<Vertex,Vertex>>() {

			@Override
			public int compare(Pair<Vertex, Vertex> first, Pair<Vertex, Vertex> second) {
				
				return 	Double.compare(first.getLeft().euklidDistanceBetweenVertex(first.getRight()),
						second.getLeft().euklidDistanceBetweenVertex(second.getRight()));
			}
		});
       
        for(int iter = 0; iter < sortedPairsList.size(); iter++) {
        	
        	// area get next valid vertex to use
            Vertex vertex = sortedPairsList.get(iter).getLeft();
            Vertex neighborVertex =sortedPairsList.get(iter).getRight();
            
            if(graph.getSuccessorVertices(vertex).contains(neighborVertex) ||
               graph.getSuccessorVertices(neighborVertex).contains(vertex) ||
               vertexAngleIntervals.get(vertex).size() >= numberOfAngleSegments ||
               (vertexAngleIntervals.get(neighborVertex).size() >= numberOfAngleSegments && !vertex.isSeed())) {
            	
            	continue;
            }
  	
        	Double leftToRight = this.computeAngleOrIntersection(vertex, neighborVertex, vertexAngleIntervals.get(vertex), alpha);
        	Double rightToLeft = this.computeAngleOrIntersection(neighborVertex, vertex, vertexAngleIntervals.get(neighborVertex), alpha);
        	
        	if (leftToRight != null && (rightToLeft != null || vertex.isSeed())) {
        		
        		 boolean inSight = GeometryAdditionals.calculateIntersection(blockingGeometries,
	            		vertex.getGeometry().getCenter(),
	            		neighborVertex.getGeometry().getCenter(),
	            		connectObstacleDistance);	            
	            
	            if(inSight) {

	            	vertexAngleIntervals.get(vertex).add(GeometryFactory.createAngleInterval(leftToRight + alpha, 2.0 * alpha));
	            	
	            	if(rightToLeft != null) {
	 
	            		vertexAngleIntervals.get(neighborVertex).add(GeometryFactory.createAngleInterval(rightToLeft + alpha, 2.0 * alpha));
	            	}
	            	
            		graph.doublyConnectVertices(vertex, neighborVertex);
            	}
            }
        }
    }

    private Double computeAngleOrIntersection(Vertex vertex, Vertex neighborVertex, List<AngleInterval2D> angleIntervals, double alpha) {
		       
    	Vector2D referencePointShifted = vertex.getGeometry().getCenter().sum(10, 0);
    	// 10 could be any other value just for line horizontal to baseline 
    	
        Double angle = GeometryAdditionals.angleBetween0And360CCW(
        		referencePointShifted,
        		vertex.getGeometry().getCenter(),
        		neighborVertex.getGeometry().getCenter());
        
        boolean intersects = false;

        AngleInterval2D newInterval = GeometryFactory.createAngleInterval(angle + alpha, 2 * alpha);
        
		for (AngleInterval2D angleInterval : angleIntervals) {
			 
			if(angleInterval.intersects(newInterval)) {// Interval towards baseline! clockwise! Internal is also clockwise!

	            intersects = true;
                break;
            }	
    	}

    	return (!intersects ? angle : null);
    } 	
}
