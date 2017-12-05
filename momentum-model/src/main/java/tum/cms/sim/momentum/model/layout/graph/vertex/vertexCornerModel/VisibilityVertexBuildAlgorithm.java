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

package tum.cms.sim.momentum.model.layout.graph.vertex.vertexCornerModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.util.FastMath;
import tum.cms.sim.momentum.utility.geometry.Geometry2D;
import tum.cms.sim.momentum.utility.geometry.GeometryFactory;
import tum.cms.sim.momentum.utility.geometry.Polygon2D;
import tum.cms.sim.momentum.utility.geometry.Segment2D;
import tum.cms.sim.momentum.utility.geometry.Vector2D;
import tum.cms.sim.momentum.utility.geometry.operation.GeometryAdditionals;
import tum.cms.sim.momentum.utility.graph.GraphTheoryFactory;
import tum.cms.sim.momentum.utility.graph.Vertex;

public class VisibilityVertexBuildAlgorithm {

	private static double precisison = 0.1;
	private static double richedAngle = 90.0;
	private static double normalAngle = 45.0;
	
	private class VertexErrorPosition {
		
        // This saves the information which geometries block a direct line between vertex and corner (true blocks)
        ArrayList<ArrayList<Vector2D>> blocksLineGeometries = new ArrayList<ArrayList<Vector2D>>();

        public ArrayList<ArrayList<Vector2D>> getBlocksLineGeometries() {
			return blocksLineGeometries;
		}

        public VertexErrorPosition() {
        	
        	this.blocksLineGeometries = new ArrayList<ArrayList<Vector2D>>();
        }

		public boolean isEmpty() {
	
			return !blocksLineGeometries.stream().anyMatch(intersections -> intersections.size() > 0);
		}
	}

	public ArrayList<Vertex> createGraphEnrichedSeeds(Collection<Geometry2D> blockingGeometries, 
			double cornerGenerateDistance,
			double subsegmentGenerateDistance) {
	
		ArrayList<Vertex> vertices = new ArrayList<Vertex>();
        
        for (Geometry2D blockingGeometry : blockingGeometries) {

        	List<Geometry2D> notSelfBlockingGeometries = blockingGeometries.stream()
        			.filter(geometry -> geometry != null && 
        				    geometry != blockingGeometry && 
        				    !geometry.equals(blockingGeometry))
        			.collect(Collectors.toList());
        	
            if (blockingGeometry instanceof Polygon2D) {
            	
                createVertexLinePoints(notSelfBlockingGeometries,
                		vertices, 
                		((Polygon2D)blockingGeometry).getVertices(),
                		cornerGenerateDistance);
                
                for(Segment2D segment : ((Polygon2D)blockingGeometry).getSegments()) {
                	
                	for(Segment2D subSegment : segment.getLineSegmentsSplit(subsegmentGenerateDistance)) {
                		
                        List<Vector2D> segmentPoints = subSegment.getVertices();

            
                        
//                        if(segmentPoints.size() > 2) {
//                        	
//        	                createVertexLinePoints(notSelfBlockingGeometries,
//        	                		vertices, 
//        	                		segmentPoints.subList(1, segmentPoints.size() - 2),
//        	                		cornerGenerateDistance);
//                        }
//                        else {
//                        	
                            createVertexFreePoint(notSelfBlockingGeometries,
                            		vertices, 
                            		segmentPoints.get(0), 
                            		segmentPoints.get(1),
                            		richedAngle,
                            		cornerGenerateDistance);
                            
                            createVertexFreePoint(notSelfBlockingGeometries, 
                            		vertices, 
                            		segmentPoints.get(segmentPoints.size() - 1),
                            		segmentPoints.get(segmentPoints.size() - 2),
                            		richedAngle,
                            		cornerGenerateDistance);
//                        }
                	}
                }
            }
            else if (blockingGeometry instanceof Segment2D) {
            	
            	for(Segment2D subSegment : ((Segment2D)blockingGeometry).getLineSegmentsSplit(subsegmentGenerateDistance)) {
            		
	                List<Vector2D> segmentPoints = subSegment.getVertices();
	
	                createVertexFreePoint(notSelfBlockingGeometries,
	                		vertices, 
	                		segmentPoints.get(0), 
	                		segmentPoints.get(1),
	                		richedAngle,
	                		cornerGenerateDistance);
	                
	                createVertexFreePoint(notSelfBlockingGeometries, 
	                		vertices, 
	                		segmentPoints.get(segmentPoints.size() - 1),
	                		segmentPoints.get(segmentPoints.size() - 2),
	                		richedAngle,
	                		cornerGenerateDistance);
	                
	                if(segmentPoints.size() > 2) {
	                	
		                createVertexLinePoints(notSelfBlockingGeometries,
		                		vertices, 
		                		segmentPoints.subList(1, segmentPoints.size() - 2),
		                		cornerGenerateDistance);
	                }
            	}
            }
            else {
            	
            	// is cycle!
            }
        }
        
        return vertices;	
	}
 			
	public ArrayList<Vertex> createGraphSeeds(Collection<Geometry2D> blockingGeometries, double cornerGenerateDistance) {
    	
		ArrayList<Vertex> vertices = new ArrayList<Vertex>();
        
        for (Geometry2D blockingGeometry : blockingGeometries) {

        	List<Geometry2D> notSelfBlockingGeometries = blockingGeometries.stream()
        			.filter(geometry -> geometry != null && !(geometry == blockingGeometry))
        			.collect(Collectors.toList());
        	
            if (blockingGeometry instanceof Polygon2D) {
            	
                createVertexLinePoints(notSelfBlockingGeometries,
                		vertices, 
                		((Polygon2D)blockingGeometry).getVertices(),
                		cornerGenerateDistance);
            }
            else if (blockingGeometry instanceof Segment2D) {
            	
                List<Vector2D> segmentPoints = ((Segment2D)blockingGeometry).getVertices();

                createVertexFreePoint(notSelfBlockingGeometries,
                		vertices, 
                		segmentPoints.get(0), 
                		segmentPoints.get(1),
                		normalAngle,
                		cornerGenerateDistance);
                
                createVertexFreePoint(notSelfBlockingGeometries, 
                		vertices, 
                		segmentPoints.get(segmentPoints.size() - 1),
                		segmentPoints.get(segmentPoints.size() - 2),
                		normalAngle,
                		cornerGenerateDistance);
                
                if(segmentPoints.size() > 2) {
                	
	                createVertexLinePoints(notSelfBlockingGeometries,
	                		vertices, 
	                		segmentPoints.subList(1, segmentPoints.size() - 2),
	                		cornerGenerateDistance);
                }
            }
            else {
            	
            	// is cycle!
            }
        }
        
        return vertices;
    }

    /**
     * 
     * @param blockingGeometries
     * @param vertices
     * @param points
     */
    private void createVertexLinePoints(List<Geometry2D> blockingGeometries, 
    		ArrayList<Vertex> vertices,
    		List<Vector2D> points,
    		double cornerGenerateDistance) {

        Vector2D firstPoint = null;
        Vector2D cornerPoint = null;
        Vector2D secondPoint = null;
        Vector2D vertex = null;
        
        double outerAngle = 0.0;
        
        for (int iter = 0; iter < points.size(); iter++) {
        	
            firstPoint = points.get(iter); // current
            cornerPoint = points.get((iter + 1) % points.size()); // next, or first
            secondPoint = points.get((iter + 2) % points.size()); // next next, or second
            
            // do this only for convex
            outerAngle = GeometryAdditionals.angleBetween0And360CCW(firstPoint, cornerPoint, secondPoint);
            
            if(outerAngle < FastMath.PI) {
            	
            	continue;
            }
            
            vertex = GeometryAdditionals.createBisectorPoint(firstPoint, cornerPoint, secondPoint, cornerGenerateDistance);
        	
        	VertexErrorPosition errorPosition = this.calculateVertexErrorPosition(blockingGeometries,
        			cornerPoint, 
        			vertex);
        	
        	if(!errorPosition.isEmpty()) {
        	
        		vertex = adjustVertexPosition(blockingGeometries,
        				cornerPoint,
        				FastMath.PI * 2 - outerAngle,
        				vertex, 
        				errorPosition,
        				cornerGenerateDistance);
        	}
        	
        	if(vertex != null) {
	    		
        		vertex = this.proximityError(blockingGeometries, vertex, cornerGenerateDistance);
    	    }
        	
        	if(vertex != null) {
        		
            	this.addCycleVertex(vertices, vertex);
        	}
        }
    }
    
    private void addCycleVertex(ArrayList<Vertex> verices, Vector2D vertex) {
    	
    	verices.add(GraphTheoryFactory.createVertexCyleBased(vertex)); 	
    }

    private void createVertexFreePoint(List<Geometry2D> blockingGeometries, 
    		ArrayList<Vertex> verices,
    		Vector2D freePoint, 
    		Vector2D segementPoint,
    		double bisectorAngle,
    		double cornerGenerateDistance) {

        Pair<Vector2D, Vector2D> antennaPoints = GeometryAdditionals.createAntennaBisectorPoints(freePoint, 
        		segementPoint,
        		cornerGenerateDistance,
        		bisectorAngle);
        
        Vector2D adjustedAntenna = antennaPoints.getLeft();
        
        // check for touching obstacles antenna 0     
        VertexErrorPosition errorPosition = this.calculateVertexErrorPosition(blockingGeometries, freePoint, adjustedAntenna);
        
		if(!errorPosition.isEmpty()) {
			
			adjustedAntenna = adjustVertexPosition(blockingGeometries, 
					freePoint,
					FastMath.PI * 2, 
					antennaPoints.getLeft(),
					errorPosition,
					cornerGenerateDistance);
		}

		if(adjustedAntenna != null) {
	    		
	        adjustedAntenna = this.proximityError(blockingGeometries, adjustedAntenna, cornerGenerateDistance);
	    }
		  
		if(adjustedAntenna != null) {
    		
			this.addCycleVertex(verices, adjustedAntenna);
    	}
			
	    adjustedAntenna = antennaPoints.getRight();
        
	    // check for touching obstacles antenna 1     
        errorPosition = this.calculateVertexErrorPosition(blockingGeometries, freePoint, antennaPoints.getRight());
        
        if(!errorPosition.isEmpty()) {
			
			adjustedAntenna = adjustVertexPosition(blockingGeometries,
					freePoint, 
					FastMath.PI * 2,
					antennaPoints.getRight(),
					errorPosition,
					cornerGenerateDistance);
		}
        
        if(adjustedAntenna != null) {
    		
        	adjustedAntenna = this.proximityError(blockingGeometries, adjustedAntenna, cornerGenerateDistance);
        }
        
		if(adjustedAntenna != null) {
    		
			this.addCycleVertex(verices, adjustedAntenna);
    	}
    }
    
    /**
     * Due to "weak" geometry configurations some generated vertices are too close to polygons / segments.
     * This method will remove these vertices.
     * @return
     */
    private Vector2D proximityError(List<Geometry2D> blockingGeometries, Vector2D vertex, double cornerDistance) {
    	
    	for(Geometry2D blockingGeometry : blockingGeometries) {
    		
    		if(blockingGeometry.distanceBetween(vertex) < cornerDistance * 0.5) {
    			
    			vertex = null;
    			break;
    		}
    		
    		if(blockingGeometry.contains(vertex)) {
    			
    			vertex = null;
    			break;	
    		}
    	}
    	
    	return vertex;
    }

    /**
     * Find bad placed points regarding other geometry, if
     * 
     * 1.
     * If the corner point touches another obstacle the vertex has to
     * be placed in another angle regarding that touching obstacle
     * 
     * 2.
     * Check if the line created by the corner and the vertex intersects with a
     * geometry which is not the corner itself. 
     * 
     * 3.
     * If a vertex is within a polygon it is not valid.
     * 
     * @param blockingGeometries
     * @param cornerPoint
     * @param vertex
     * @return
     */
    private VertexErrorPosition calculateVertexErrorPosition(List<Geometry2D> blockingGeometries, 
    		Vector2D cornerPoint, 
    		Vector2D vertex) {

    	VertexErrorPosition errorPosition = new VertexErrorPosition();	
        Segment2D testSegment = GeometryFactory.createSegment(cornerPoint, vertex);
        ArrayList<Vector2D> positiveTests = null;

        for (Geometry2D blockingGeometry : blockingGeometries) {

        	positiveTests = blockingGeometry.getIntersection(testSegment);
        	
        	if(blockingGeometry.isOnLines(vertex, VisibilityVertexBuildAlgorithm.precisison)) {
        		
        		positiveTests.add(cornerPoint);
        	}
        	else if(blockingGeometry.contains(vertex)) {
        		
        		positiveTests.add(cornerPoint);
        	}
        
        	if(positiveTests.size() > 0) {
        		
        		errorPosition.getBlocksLineGeometries().add(positiveTests);
        	}     	
        }
        
        return errorPosition;
    }

    /**
     * Try to find a points which are not appropriate and remove them if necessary
     * In the original code another point was "selected" but this may never work correct
     * the way it was done. Only if a pedestrian can pass by not touching blocking geometry
     * Therefore only valid points are returned.
     * @param blockingGeometries
     * @param cornerPoint
     * @param innerAngleSourceGeometry
     * @param vertex
     * @param vertexErrorPosition
     * @return
     */
    private Vector2D adjustVertexPosition(List<Geometry2D> blockingGeometries, 
    		Vector2D cornerPoint, 
    		double innerAngleSourceGeometry,
    		Vector2D vertex,
    		VertexErrorPosition vertexErrorPosition,
    		double cornerDistance) {
    	
    	Vector2D probePoint = vertex;
    	
        for (ArrayList<Vector2D> intersectionPoints : vertexErrorPosition.getBlocksLineGeometries()) {
       
        	if(intersectionPoints.size() == 0) {
        	
        		continue;
        	}
        		
    		for (Vector2D intersectionPoint : intersectionPoints) {
    			 		   			
    			if(cornerPoint.distance(probePoint) <= cornerPoint.distance(intersectionPoint)) {
    				
    				continue;
    			}
    			
				if(!cornerPoint.equals(intersectionPoint)) { 
				
					probePoint = intersectionPoint;
					
					// Check if a pedestrian can pass 
			        if(cornerPoint.distance(probePoint) > cornerDistance) {
			        	
			        	// change the position
			        	vertex = cornerPoint.sum(probePoint.getXComponent() / 2.0, 
			        			probePoint.getYComponent() / 2.0);
			        }
			        else {
			        
			        	return null;
			        }
				}        	
				else { // Corner is the nearest intersection point, thus we have to check if this is another geometries corner
					
					for(Geometry2D blockingGeometry : blockingGeometries) {
							
						// If is on the polygon (not corner), the position is not correct
						// It the nearest point is on a corner of the polygon, than
						// it is only ok if the original angle radiant is < PI/2
						if(blockingGeometry.isOnLines(cornerPoint, precisison) ||
						   (!blockingGeometry.isOnCorners(cornerPoint, precisison) && innerAngleSourceGeometry >= FastMath.PI/2.0)) {
							
							return null;
						}		
					}
				}
        	}
        }
        
        return vertex;
    }
}
