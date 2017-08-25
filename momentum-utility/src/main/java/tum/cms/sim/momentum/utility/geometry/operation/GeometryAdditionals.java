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

package tum.cms.sim.momentum.utility.geometry.operation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Stack;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.util.FastMath;

import tum.cms.sim.momentum.utility.geometry.Cycle2D;
import tum.cms.sim.momentum.utility.geometry.Geometry2D;
import tum.cms.sim.momentum.utility.geometry.GeometryFactory;
import tum.cms.sim.momentum.utility.geometry.Line2D;
import tum.cms.sim.momentum.utility.geometry.Polygon2D;
import tum.cms.sim.momentum.utility.geometry.Segment2D;
import tum.cms.sim.momentum.utility.geometry.Vector2D;
import tum.cms.sim.momentum.utility.graph.Edge;
import tum.cms.sim.momentum.utility.graph.Graph;
import tum.cms.sim.momentum.utility.graph.Vertex;
import tum.cms.sim.momentum.utility.probability.HighQualityRandom;

public class GeometryAdditionals {

    private static double precision = 0.0001;

    private static Random random = new HighQualityRandom();
    
    public static double translateToRadiant(double angle) {
    	
    	return FastMath.PI * (angle / 180.0);
    }
    
	public static double translateToDegree(double radiant) {
	
		return (180.0 / FastMath.PI) * radiant;
	}
	
    public static Vector2D createUnitVectorForAngle(double angle) {
    	
    	double randiant = GeometryAdditionals.translateToRadiant(angle);
    	return GeometryFactory.createVector(FastMath.cos(randiant), FastMath.sin(randiant));
    }

    public static double angleBetweenPlusMinus180(double l1_dir_x, double l1_dir_y, double l2_dir_x, double l2_dir_y) {
    	
    	Vector2D left = GeometryFactory.createVector(l1_dir_x, l1_dir_y);
    	Vector2D center = GeometryFactory.createVector(0, 0);
    	Vector2D right = GeometryFactory.createVector(l2_dir_x, l2_dir_y);
    			
        return angleBetweenPlusMinus180(left, center, right);
    }
    
    public static boolean isOnLine(Vector2D firstOfLine, Vector2D secondOfLine, Vector2D toCheck) {
    	
    	double checkA = (secondOfLine.getXComponent() - firstOfLine.getXComponent())*(toCheck.getYComponent() - secondOfLine.getYComponent());
    	double checkB = (secondOfLine.getYComponent() - firstOfLine.getYComponent())*(toCheck.getXComponent() - secondOfLine.getXComponent());
  
    	return FastMath.abs(checkA - checkB) < precision;
    }
    
    public static boolean isLeftOf(Vector2D firstOfLine, Vector2D secondOfLine, Vector2D toCheck) {
    	
    	double checkA = (secondOfLine.getXComponent() - firstOfLine.getXComponent())*(toCheck.getYComponent() - secondOfLine.getYComponent());
    	double checkB = (secondOfLine.getYComponent() - firstOfLine.getYComponent())*(toCheck.getXComponent() - secondOfLine.getXComponent());
  
    	return checkA - checkB > 0;
    }
    
 
    public static double angleBetweenPlusMinus180(Vector2D left, Vector2D center, Vector2D right) {

        double dx1 = left.getXComponent() - center.getXComponent();
        double dy1 = left.getYComponent() - center.getYComponent();
        double dx2 = right.getXComponent() - center.getXComponent();
        double dy2 = right.getYComponent() - center.getYComponent();
        Vector2D d1 = GeometryFactory.createVector(dx1, dy1).getNormalized();
		Vector2D d2 = GeometryFactory.createVector(dx2, dy2).getNormalized();
		double angle = d1.getAngleBetween(d2);
        return angle; 	
    }
    
    public static double angleBetween0And360CCW(double l1_dir_x, double l1_dir_y, double l2_dir_x, double l2_dir_y) {
    	
	    Vector2D left = GeometryFactory.createVector(l1_dir_x, l1_dir_y);
	 	Vector2D center = GeometryFactory.createVector(0, 0);
	 	Vector2D right = GeometryFactory.createVector(l2_dir_x, l2_dir_y);
	 	
	 	return angleBetween0And360CCW(left, center, right);
	}
    
    public static double angleBetween0And360CCW(Vector2D left, Vector2D center, Vector2D right) {

		double angle = angleBetweenPlusMinus180(left, center, right);
		
		if(angle < 0) { // is clockwise
			
			angle = FastMath.PI + (FastMath.PI + angle);
		}

        return angle; 	
    }

    
    public static double angleBetween0And360CW(Vector2D left, Vector2D center, Vector2D right) {

         return 2 * FastMath.PI - angleBetween0And360CCW(left, center, right); 	
    }
    
    public static double angleBetween0And180(Vector2D left, Vector2D center, Vector2D right) {

        return FastMath.abs(angleBetweenPlusMinus180(left, center, right));
    }

    /**
     * This method returns two points which are 45 degrees at the distance dist
     * to the endPoint of the line which goes from startPoint to endPoint.
     * 
     * @param endPoint
     *            end point of the line for which two points shall be generated
     * @param startPoint
     *            start point of the line
     * @param dist
     *            distance of the points to be generated
     * @return two generated points
     */
    public static Pair<Vector2D, Vector2D> createAntennaBisectorPoints(Vector2D endPoint, 
    		Vector2D startPoint, 
    		double dist, 
    		double bisectorAngle) {
    	
    	Vector2D leftAntenna = startPoint.rotate(GeometryAdditionals.translateToRadiant(180 - bisectorAngle), endPoint)
    			.subtract(endPoint)
    			.getNormalized()
    			.scale(dist)
    			.sum(endPoint);
    	 
    	Vector2D rightAntenna = startPoint.rotate(GeometryAdditionals.translateToRadiant(180 + bisectorAngle), endPoint) // FastMath.PI * 1.25 oben gegenteil
    			.subtract(endPoint)
    			.getNormalized()
    			.scale(dist)
    			.sum(endPoint);
 
    	return new MutablePair<Vector2D, Vector2D>(leftAntenna, rightAntenna);
    }

    /**
     * This method derives a point on the bisector of a corner which is
     * described by the firstPoint, cornerPoint, secondPoint at some distance.
     * 
     * @param left
     * @param center
     * @param right
     * @return
     */
    public static Vector2D createBisectorPoint(Vector2D left, Vector2D center, Vector2D right, double dist) {

        double leftX = left.getXComponent();
        double leftY = left.getYComponent();
        
        double centerX = center.getXComponent();
        double centerY = center.getYComponent();
        
        double rightX = right.getXComponent();
        double rightY = right.getYComponent();
        
        double leftDistance = left.distance(center);
        double rightDistance = center.distance(right);

        // point (xb,yb) is on the angle bisector for angle at corner point
        double bisectorX = centerX + (leftX - centerX) / leftDistance + (rightX - centerX) / rightDistance;
        double bisectorY = centerY + (leftY - centerY) / leftDistance + (rightY - centerY) / rightDistance;
        double bisectorDist = FastMath.sqrt((centerX - bisectorX) * (centerX - bisectorX) + (centerY - bisectorY)
        						* (centerY - bisectorY));
        double normBisX = (bisectorX - centerX) / bisectorDist;
        double normBisY = (bisectorY - centerY) / bisectorDist;

        Vector2D[] insideTester = (Vector2D[])Arrays.asList(left, center, right).toArray();
        Vector2D bisectorPoint =  GeometryFactory.createVector(centerX + precision * normBisX, centerY + precision * normBisY);
        
        if(GeometryFactory.createPolygon(insideTester).contains(bisectorPoint))  {
        	
        	bisectorPoint = GeometryFactory.createVector(centerX + -1 * dist * normBisX, centerY + -1 * dist * normBisY);
        }
        
        return bisectorPoint;
    } 

//    public static boolean isCycleCoveredByAngles(List<AngleInterval2D> angleIntervals, int numberOfAngleSegments) {
//
//        boolean cycleIsCovered = false;
//   
//        if (angleIntervals.size() > 1) {
//        
//        	if(angleIntervals.size() < numberOfAngleSegments ) {
//        		
//             	double sum = 0.0;
//            	
//            	for(AngleInterval2D interval : angleIntervals) {
//            		
//            		sum += interval.getLeft() - interval.getRight();
//            	}
//            	
//            	if(sum >= FastMath.PI * 2.0) {
//            		
//            		cycleIsCovered = true;
//            	}
//            	
//        	}
//        	else {
//        		
//        		cycleIsCovered = true;
//        	}
//        }
//        
//        return cycleIsCovered;
//    }

    public static ArrayList<Vector2D> findClosestSet(Vector2D closestTo, List<Vector2D> others) {
    	
    	ClosestVectorComparer comparer = new ClosestVectorComparer();
    	comparer.setVector(closestTo);
    	
    	ArrayList<Vector2D> list = new ArrayList<>();
    	others.forEach(other -> list.add(other));
    	
    	list.sort(comparer);
    	
    	return list;
    }
    
    private static class ClosestVectorComparer implements Comparator<Vector2D> {

    	private Vector2D vector;    	
    	
        public void setVector(Vector2D vector) {
    		this.vector = vector;
    	}

    	@Override
    	public int compare(Vector2D o1, Vector2D o2) {

    		if (vector.distance(o1) < vector.distance(o2)) {
    			
    			return -1;
    		}
    		else if (vector.distance(o1) > vector.distance(o2)) {
    			
    			return 1;
    		}
    		else {
    			
    			return 0;
    		}
    	}
    }

	public static List<Boolean> calculateInSightIncludeTouch(List<Geometry2D> blockingGeometries, 
    		List<Vector2D> starts, 
    		Geometry2D objectToSee,
    		double discretisation,
    		double precision) {
    	
    	List<Boolean> inSights = new ArrayList<Boolean>();
    	
    	if(objectToSee instanceof Cycle2D) {

	    	for(int iter = 0; iter < starts.size(); iter++) {
	    		
		    	Vector2D center = objectToSee.getCenter();
		    	boolean inSight = false;

		    	inSight = calculateInSightIncludeTouch(blockingGeometries, starts.get(iter), center, precision);
		    	
		    	if(!inSight) {
		    	
		    		inSight = calculateInSightIncludeTouch(blockingGeometries, starts.get(iter), center, precision);
		    	}
		    	
		    	inSights.add(inSight);
	    	}
    	}
    	else {
    		
    		List<Segment2D> segmentsToSee = null;
    		
    		if(objectToSee instanceof Polygon2D) {
    			
	    		segmentsToSee = ((Polygon2D) objectToSee).polygonAsSegments().getLineSegments();
	    	}
	    	else {
	    		
	    		segmentsToSee = ((Segment2D) objectToSee).calculateLineSegmentsToEquallySplitted(discretisation, null);
	    	}

	    	for(int iter = 0; iter < starts.size(); iter++) {
	    		
	    		boolean intersection = false;
	    		
	    		if(objectToSee instanceof Polygon2D) {
		    		intersection = calculateInSightIncludeTouch(blockingGeometries,
		    				objectToSee.getCenter(), 
		    				starts.get(iter),
		    				precision);
	    		}
	    		
	    		if(!intersection) {
	    			
	    			for(Segment2D segment : segmentsToSee) {
	    			
	    				if(starts.get(iter).equals(segment.getLastPoint()) || 
	    				   starts.get(iter).equals(segment.getFirstPoint())) {
	    					
	    					intersection = true;
	    					break;
	    				}
	    				
	    				intersection = calculateInSightIncludeTouch(blockingGeometries, 
	    						starts.get(iter), 
	    						segment.getFirstPoint(),
	    						precision);
	    				
	    				if(intersection) {
	    					break;
	    				}
	    				
	    				intersection = calculateInSightIncludeTouch(blockingGeometries, 
	    						starts.get(iter), 
	    						segment.getLastPoint(),
	    						precision);	
	    				
	    				if(intersection) {
	    					break;
	    				}
	    			}
	    		}
	    		
		    	inSights.add(intersection);
	    	}
    	}
    	
    	return inSights;
    }
    
    public static boolean calculateIntersection(Collection<Geometry2D> blockingGeometries, Vector2D start, Vector2D pointToSee, double tolerance) {
    	
        boolean inSight = true;
        
        if(start.equals(pointToSee)) {
        	return inSight;
        }
        
        Segment2D lineOfSight = GeometryFactory.createSegment(start, pointToSee); //calculateInSightSegement(left, right, safetyDistance);

        for(Geometry2D geometry : blockingGeometries) {
        	
        	ArrayList<Vector2D> intersections = geometry.getIntersection(lineOfSight);
        	
        	double distance = lineOfSight.minimalDistanceBetween(geometry.getVertices());

        	if(intersections.size() > 0 || distance < tolerance) {
 
    			inSight = false;
    			break;
        	}
        }

        return inSight;
    }
    
    public static boolean calculateInSightIncludeTouch(Collection<Geometry2D> blockingGeometries, Vector2D start, Vector2D pointToSee, double precision) {
	    	
        boolean inSight = true;
        
        if(start.equals(pointToSee)) {
        	return inSight;
        }
        
        Segment2D lineOfSight = GeometryFactory.createSegment(start, pointToSee); //calculateInSightSegement(left, right, safetyDistance);

        for(Geometry2D geometry : blockingGeometries) {
        	
        	ArrayList<Vector2D> intersections = geometry.getIntersection(lineOfSight);
        	
        	if(intersections.size() > 0) {
        		
        		inSight = false;
    			break;
//        		int onLine = 0;
//        		
//        		for(Vector2D intersectionPoint : intersections) {
//        			
//        			if(!geometry.isOnCorners(intersectionPoint, precision)) {
//        				
//        				onLine++;
//        			}
//        		}
//        		
//        		if(onLine < intersections.size()) {
//        			inSight = false;
//        			break;
//        		}
        	}
        	
        	if(!inSight) {
        		break;
        	}
        }

        return inSight;
    }

    /**
     * Important, the segments in the sortedGeometry list have to comprise only a single line and
     * have to be sorted regarding distance to the viewport. 
     * The distance is computed based on the corner to the viewport. 
     * Important, the two corners of a single segment is part of the sortedGeometry set.
     * Result is a set of vector2D sets that are corners of all visible triangles regarding a
     * blocking segment for the the viewpoint.
     */
	public static Collection<ArrayList<Vector2D>> calculateSightCorners(ArrayList<Pair<Vector2D, Segment2D>> sortedGeometry, Vector2D viewPort, double accuracy) {

		HashMap<Segment2D, ArrayList<Vector2D>> tempTriangle = new HashMap<>();	
		sortedGeometry.forEach(blockingSegment -> tempTriangle.put(blockingSegment.getRight(),  new ArrayList<>()));

		for(int iter = 0; iter < sortedGeometry.size(); iter++) {
			
			Segment2D mySegment = sortedGeometry.get(iter).getRight();
			Vector2D myCorner = sortedGeometry.get(iter).getLeft();
			
			// cast ray in direction to the to check corner
			Line2D lineRay = GeometryFactory.createLine2D(viewPort, myCorner.subtract(viewPort).getNormalized());	

			Vector2D rayIntersection = null;
			
			for(int compareIter = 0; compareIter < iter; compareIter++) {
				
				Segment2D otherSegment = sortedGeometry.get(compareIter).getRight();
				Vector2D otherCorner = sortedGeometry.get(compareIter).getLeft();
				
				if(mySegment.equals(otherSegment)) { 	// ignore same segment
					continue;
				}
				
				rayIntersection = GeometryAdditionals.intersectionToRay(sortedGeometry.get(compareIter).getRight(), lineRay);
		
				if(rayIntersection != null && // hit a corner nearby, same "geometry cluster"
				   (rayIntersection.distance(myCorner)) < accuracy &&
				   (rayIntersection.distance(otherCorner)) < accuracy) {
				     		
					// check if the end vectors creating a segment is hit by the ray
					// if yes ignore the ray because its at the back of a geometry
					Vector2D starOfCheck = mySegment.getFirstPoint().equals(myCorner) ? 
							mySegment.getLastPoint() : mySegment.getFirstPoint();		
							
					Vector2D endOfCheck = otherSegment.getFirstPoint().equals(otherCorner) ? 
							otherSegment.getLastPoint() : otherSegment.getFirstPoint();
												
					// if my and other segment share the same last corner, it cannot hit
					if(!starOfCheck.equals(endOfCheck)) {
						
						Segment2D checkOriantation = GeometryFactory.createSegment(starOfCheck, endOfCheck);
						Vector2D checkRay = GeometryAdditionals.intersectionToRay(checkOriantation, lineRay);
						
						if(checkRay != null) {
							
							// ray hit an real geometry not covered
							// story the corner now not later because it cannot cast further
							
							lineRay = null;
							tempTriangle.get(mySegment).add(otherCorner); 
						}
						
			     		break;
					}
				}
				else if(rayIntersection != null && // hit object in front
				   (rayIntersection.distance(myCorner)) > accuracy &&  // and the hit is not close to its or the own corner
				   (rayIntersection.distance(otherCorner)) > accuracy && // and closer than my corner (huge geometry make fussy distance sort)
				   viewPort.distance(rayIntersection) < viewPort.distance(myCorner)) {  

					lineRay = null;
					break; 
		     	}
			}
			
			if(lineRay != null) { // nothing hit the ray, hence the corner is visible
				
				tempTriangle.get(mySegment).add(myCorner);
			}
		
			if(lineRay != null) { // check if the ray hits something behind
				
				// store multiple hits and select the closest later on, important for the casting
				// because its not clear which order is present for all variants
				LinkedHashMap<Segment2D, Vector2D> intersectStore = new LinkedHashMap<>();
				
				for(int compareIter = iter + 1; compareIter < sortedGeometry.size(); compareIter++) {
					
					Segment2D otherSegment = sortedGeometry.get(compareIter).getRight();
					Vector2D otherCorner = sortedGeometry.get(compareIter).getLeft();
					
					if(mySegment.equals(otherSegment)) { 	// ignore same segment
						continue;
					}
					
					rayIntersection = GeometryAdditionals.intersectionToRay(otherSegment, lineRay);
			    	
					if(rayIntersection != null && // hit a corner nearby, same "geometry cluster"
					   (rayIntersection.distance(otherCorner)) < accuracy) {
			     		
						// check if the end vectors creating a segment is hit by the ray
						// if yes ignore the ray because its at the back of a geometry
						Vector2D starOfCheck = mySegment.getFirstPoint().equals(myCorner) ? 
								mySegment.getLastPoint() : mySegment.getFirstPoint();		
								
						Vector2D endOfCheck = otherSegment.getFirstPoint().equals(otherCorner) ? 
								otherSegment.getLastPoint() : otherSegment.getFirstPoint();
								
						// if my and other segment share the same last corner, it cannot hit
						if(!starOfCheck.equals(endOfCheck)) {	
						
							Segment2D checkOriantation = GeometryFactory.createSegment(starOfCheck, endOfCheck);
							Vector2D checkRay = GeometryAdditionals.intersectionToRay(checkOriantation, lineRay);
							
							// ray hit a virtual segment generated by the two other endpoints of the first intersection
							if(checkRay != null) {
								
								// ray hit an real geometry that is covered 
								break;
							}
						}
			     	}
					else  if(rayIntersection != null && // hit a segment behind
							!(myCorner.equals(otherSegment.getFirstPoint())) &&  // hit is not close at the other corner
							!(myCorner.equals(otherSegment.getLastPoint()))) { // otherwise the other Corner would create the ray
							
						intersectStore.put(sortedGeometry.get(compareIter).getRight(), rayIntersection);
					}
				}
							
				// multiple hits further away as the mySegment, find the closest
				if(intersectStore.size() > 0) {
					
					double minDistance = Double.MAX_VALUE;
					Segment2D bestIntersection = null;

					for(Entry<Segment2D, Vector2D> intersect : intersectStore.entrySet()) {
						
						double distance = viewPort.distance(intersect.getValue());
						
						if(minDistance > distance) {
							
							bestIntersection = intersect.getKey();
							minDistance = distance;
						}
					}
					
					tempTriangle.get(bestIntersection).add(intersectStore.get(bestIntersection));
				}
	
			}
		}
	
		for(ArrayList<Vector2D> corners : tempTriangle.values()) {
			
			corners.add(viewPort);
			List<Vector2D> temp = corners.stream().distinct().collect(Collectors.toList());
			List<Vector2D> realDistinct = new ArrayList<>();
			List<Integer> ignoreList = new ArrayList<>();
			
			if(temp.size() > 3) {
				
				for(int iter = 0; iter < temp.size() - 1; iter++) {
				
					for(int compare = iter + 1; compare < temp.size(); compare++) {
						
						if(temp.get(iter).roundTo(100.0).equals(temp.get(compare).roundTo(100.0))) {
							
							ignoreList.add(compare);
							ignoreList.add(iter);
							break;
						}
					}
				}
				
				for(int iter = 0; iter < temp.size(); iter++) {
					
					if(!ignoreList.contains(iter)) {
						
						realDistinct.add(temp.get(iter));
					}
				}
			}
			else {
				
				realDistinct = temp;
			}
		
			corners.clear();
			
			if(temp.size() > 2) { // if two or less hits indicates not visible
				
				corners.addAll(realDistinct);
			}
		}
	
		return tempTriangle.values();
	}
    /**
     * Calculates the distance from a point to a line
     * 
     * @param point
     * @param line
     * @return double
     */
    public static double distanceFromPointToLine(Vector2D point, Line2D line) {
    	
    	Vector2D referencePoint = line.getReferencePoint();
    	Vector2D gradientVector = line.getGradientVector();
    	
    	Vector2D vectorPointToReferencePoint = point.subtract(referencePoint);
    	double zComponentOfCrossProduct = vectorPointToReferencePoint.cross(gradientVector);
    	double distance =  FastMath.abs(zComponentOfCrossProduct / gradientVector.getMagnitude());
    	
    	return distance;    	
    }
    
    /**
     * CastOn has to be a single segment made out of 2 vertices.
     */
    public static Vector2D intersectionToRay(Segment2D castOn, Line2D ray) {
    	
    	
    	Vector2D v1 = ray.getReferencePoint().subtract(castOn.getFirstPoint());
    	Vector2D v2 = castOn.getLastPoint().subtract(castOn.getFirstPoint());
    	
    	Vector2D v3 = GeometryFactory.createVector(-1.0 * ray.getGradientVector().getYComponent(),
        		 ray.getGradientVector().getXComponent());

    	double t1 = v2.cross(v1) / v2.dot(v3);
    	double t2 = v1.dot(v3) / v2.dot(v3);

        if (t1 >= 0.0 && (t2 >= 0.0 && t2 <= 1.0)) {
        	
        	return ray.getReferencePoint().sum(ray.getGradientVector().scale(t1));
        }
          
        return null;
    }	

		
    public static double calculateLongestLegLengthAlongEdge(Graph graph, Edge alongEdge, Vertex target, double angleThreashold) {
		Stack<Edge> stack = new Stack<Edge>();   
        Edge nextEdge = null;
        
        Vertex longestLegEnd = alongEdge.getEnd();  
        double distanceToDestination = longestLegEnd.euklidDistanceBetweenVertex(target);
        
        stack.push(alongEdge);

        int maximalEdges = 25;
        if(alongEdge.getStart().euklidDistanceBetweenVertex(target) > distanceToDestination) {
       	 
	         while (!stack.isEmpty()) {
	         	
	             nextEdge = stack.pop();
	             maximalEdges--;
	             
	             if(maximalEdges < 0) {
	            	 break;
	             }
	             
	             distanceToDestination = nextEdge.getEnd().euklidDistanceBetweenVertex(target);
	
	             for (Edge successorEdge : graph.getSuccessorEdges(nextEdge.getEnd())) {   
	
	            	 if(successorEdge.getEnd().euklidDistanceBetweenVertex(target) > distanceToDestination) {
	            		 continue;
	            	 }
	            	 
	            	 // Reference compare, no direct cycles
	                 if (successorEdge.getEnd() == nextEdge.getStart()) {
	                	 
	                     continue;       
	                 }
	                 
	                 // nextEdge(start;end)-vertex-succesorEdge(start;end)
	                 // calculate angle at vertex between both edges
	                 double angleBetween = nextEdge.getEnd().angleBetweenVertex(nextEdge.getStart(), successorEdge.getEnd());
	
	                 // if the next edge near to target and on a long leg, keep going this direction
	                 if ((FastMath.PI - angleBetween) <= angleThreashold) {
	                	 
	                     stack.push(successorEdge);
	                     
	                     if(longestLegEnd.euklidDistanceBetweenVertex(target) > successorEdge.getEnd().euklidDistanceBetweenVertex(target)) { 
	
	                    	 longestLegEnd = successorEdge.getEnd(); 
	                    	 distanceToDestination = longestLegEnd.euklidDistanceBetweenVertex(target);
	                     }
	                 }
	             }
	         }
        }
        
        return distanceToDestination;
    }
    
    public static boolean polygonIsConvex(List<Vector2D> polygonVertices) {
        
    	if (polygonVertices.size() < 4) {
            return true;
    	}
    	
        boolean sign = false;
        int n = polygonVertices.size();
        
        for(int i=0;i<n;i++) {
            
        	double dx1 = polygonVertices.get((i + 2) % n).getXComponent() - polygonVertices.get((i + 1) % n).getXComponent();
            double dy1 = polygonVertices.get((i + 2) % n).getYComponent() - polygonVertices.get((i + 1) % n).getYComponent();
            double dx2 = polygonVertices.get(i).getXComponent() - polygonVertices.get((i + 1) % n).getXComponent();
            double dy2 = polygonVertices.get(i).getYComponent() - polygonVertices.get((i + 1) % n).getYComponent();
            double zcrossproduct = dx1 * dy2 - dy1 * dx2;
            
            if (i == 0) {
                sign = zcrossproduct > 0;
            }
            else {
                
            	if (sign != (zcrossproduct > 0)) {
                    return false;
            	}
            }
        }
        
        return true;
    }
	
	public static boolean polygonHasCounterClockwiseWielding(List<Vector2D> currentPolygon) {

	   int i,j,k;
	   int count = 0;
	   int n = currentPolygon.size();
	   double z;
	   boolean counterClockwise = false;
	   
	   for (i = 0; i < n; i++) {
		   
	      j = (i + 1) % n;
	      k = (i + 2) % n;
	      z  = (currentPolygon.get(j).getXComponent() - currentPolygon.get(i).getXComponent()) * (currentPolygon.get(k).getYComponent() - currentPolygon.get(j).getYComponent());
	      z -= (currentPolygon.get(j).getYComponent() - currentPolygon.get(i).getYComponent()) * (currentPolygon.get(k).getXComponent() - currentPolygon.get(j).getXComponent());
	      
	      if (z < 0) {
	         count--;
	      }
	      else if (z > 0) {
	         count++;
	      }
	   }
	   
	   if (count > 0) {
	      counterClockwise = true;
	   }
	   else if (count < 0) {
	      counterClockwise = false;
	   }
	   
	   return counterClockwise;
	}
	
	public static List<Vector2D> switchOrderOfVertices(List<Vector2D> oldOrder) {
        
		List<Vector2D> newOrder = new ArrayList<Vector2D>();
        int j = 0;

        for (int i = oldOrder.size() - 1; i >= 0; i--) {

            newOrder.add(j, oldOrder.get(i));
            j++;
        }

        return newOrder;
    }
	
	/**
	 * http://stackoverflow.com/questions/240778/random-points-inside-a-polygon
	 */
	public static Vector2D findRandomPositionInPolygon(Polygon2D polygon) {
		
		List<Polygon2D> triangulation = null;
		
		if(polygon.getVertices().size() == 3) {
		
			triangulation = new ArrayList<>();
			triangulation.add(polygon);
		}
		else {
			
			triangulation = polygon.triangulate();
		}

		ArrayList<Double> triangeWeights = new ArrayList<Double>();
		
		Double surfaceSum = triangulation.stream().mapToDouble(Polygon2D::area).sum();
		double[] surfaces = triangulation.stream().mapToDouble(Polygon2D::area).toArray();
		
		for(int iter = 0; iter < triangulation.size(); iter++) {
			
			triangeWeights.add(surfaces[iter] / surfaceSum);
		}
		
		double randomIndex = random.nextDouble();
		double summedWeight = 0.0;
	
		List<Vector2D> trianglePoints = null;
		
		for(int iter = 0; iter < triangeWeights.size(); iter++) {
			
			if(randomIndex <= summedWeight + triangeWeights.get(iter)) {
			
				trianglePoints = triangulation.get(iter).getVertices();
				break;
			}
			
			summedWeight += triangeWeights.get(iter);
		}

		Vector2D randomPosition = trianglePoints.get(1).subtract(trianglePoints.get(0)).multiply(random.nextDouble())
								.sum(trianglePoints.get(2).subtract(trianglePoints.get(0)).multiply(random.nextDouble()));
		
		randomPosition = randomPosition.sum(trianglePoints.get(0));
		
		if(!polygon.contains(randomPosition)) {

			randomPosition =  trianglePoints.get(1).sum(randomPosition.subtract(trianglePoints.get(2)).rotate(FastMath.PI));
		}

		return randomPosition;
	}
	
    public static Vector2D calculateVectorCenter(List<Vector2D> neighboringCandidates) {

    	Vector2D center = GeometryFactory.createVector(0.0, 0.0);

    	for(Vector2D vector : neighboringCandidates) {
    		
    		center = vector.sum(center);
    	}

    	return center.multiply(1.0 / neighboringCandidates.size());
    }
    
    public static Vector2D calculateVertexCenter(Collection<Vertex> vertices) {

    	Vector2D center = GeometryFactory.createVector(0.0, 0.0);

    	for(Vertex vertex : vertices) {
    		
    		center = vertex.getGeometry().getCenter().sum(center);
    	}

    	return center.multiply(1.0 / vertices.size());
    }
    
    
    /**
	 * Checks, if a segment2D is completely contained by an obstacle
	 * @author qa
	 * @param obstacle
	 * @param segment
	 * @return <code>true</code> if the segment2D is contained by the obstacle and 
	 * <code>false</code> otherwise
	 */
	public static boolean contains(Geometry2D obstacle, Segment2D segment) { 		 
		if(obstacle.contains(segment.getFirstPoint()) && obstacle.contains(segment.getLastPoint()))
			return true;
		
		return false;
	}

}
