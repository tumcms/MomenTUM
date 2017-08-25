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

package tum.cms.sim.momentum.utility.geometry;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.util.FastMath;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Segment;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;

/**
 * All class and method descriptions have been adopted from the dyn4j library documentation 
 * http://docs.dyn4j.org/v3.1.10/.
 * 
 * Represents a set of line segments, thus a open polygon
 * 
 * @author berndtornede, pk
 *
 */
public class Segment2D extends Geometry2D {
	
	private ArrayList<Segment> segments = new ArrayList<Segment>();	
	
	private ArrayList<Vector2D> vector2DList = null;
	ArrayList<Segment2D> segment2DList = null; 
	
	public List<Segment2D> getLineSegments() {
		
		if(segment2DList == null) {
			
			segment2DList = new ArrayList<Segment2D>();

			for (int i = 0; i < this.segments.size(); i++) {
				
				segment2DList.add(GeometryFactory.createSegment(
							new Vector2D(segments.get(i).getPoint1()),
							new Vector2D(segments.get(i).getPoint2())));
			}			
		}
	
		return segment2DList;
	}

	/**
	 * Constructor for a simple one line segment.
	 * 
	 * @param point1
	 * @param point2
	 */
	Segment2D(Vector2D point1, Vector2D point2) {
		this.segments.add(new Segment(point1.getVector(), point2.getVector()));
	}
		
	/**
	 * Constructor for a complex line segment.
	 * The vertices are now allowed to intersect or pairwise equal only once
	 * 
	 * @param nodeArray
	 */
	Segment2D(List<Vector2D> nodeArray) {	
	
		for(int iter = 0; iter < nodeArray.size() - 1; iter++) {
			
			this.segments.add(new Segment(nodeArray.get(iter).getVector(), nodeArray.get(iter + 1).getVector()));
		}
	}
	
	Segment2D(Vector2D[] nodeArray) {
		
		for(int iter = 0; iter < nodeArray.length - 1; iter++) {
			
			this.segments.add(new Segment(nodeArray[iter].getVector(), nodeArray[iter + 1].getVector()));
		}
	}

	@Override
	public boolean equals(Object other) {
		
		boolean isEqual = false;
		Segment2D otherSegment = null;
		
		if(!(other instanceof Segment2D)) {
			
			return false;
		}
		
		otherSegment = (Segment2D)other;

		if(otherSegment == this) {
			
			isEqual = true;
		}
		else if(otherSegment.getFirstPoint().equals(this.getFirstPoint()) &&
				otherSegment.getLastPoint().equals(this.getLastPoint())) {
			
			isEqual = true;
		}
		else if(otherSegment.getFirstPoint().equals(this.getLastPoint()) &&
				otherSegment.getLastPoint().equals(this.getFirstPoint())) {
			
			isEqual = true;
		}
		else if(otherSegment.getFirstPoint().roundTo(4.0).equals(this.getFirstPoint().roundTo(4.0)) &&
				otherSegment.getLastPoint().roundTo(4.0).equals(this.getLastPoint().roundTo(4.0))) {
			
			isEqual = true;
		}
		else if(otherSegment.getFirstPoint().roundTo(4.0).equals(this.getLastPoint().roundTo(4.0)) &&
				otherSegment.getLastPoint().roundTo(4.0).equals(this.getFirstPoint().roundTo(4.0))) {
			
			isEqual = true;
		}
		
		return isEqual;
	}
	/**
	 * Returns the array of vertices this Segment2D contains in local coordinates.
	 * 
	 * @return Vector2D[]
	 */
	@Override
	public synchronized List<Vector2D> getVertices() { 				// former .getNodeArray()
		
		if(vector2DList == null || vector2DList.isEmpty()) {
			
			vector2DList = new ArrayList<Vector2D>();
			
			for (int i = 0; i < this.segments.size(); i++) {
				
				if(!this.segments.get(i).getVertices()[0].equals(this.segments.get(i).getVertices()[1])) {
					
					if(i > 0 && !this.segments.get(i - 1).getVertices()[0].equals(this.segments.get(i).getVertices()[0])) {
						
						vector2DList.add(new Vector2D(this.segments.get(i).getVertices()[0]));
					}
					else {
						
						vector2DList.add(new Vector2D(this.segments.get(i).getVertices()[0]));
					}
					
					if(i == this.segments.size() - 1 
							&& !this.segments.get(0).getVertices()[0].equals(this.segments.get(i).getVertices()[1])) {
						
						vector2DList.add(new Vector2D(this.segments.get(i).getVertices()[1]));
					}
				}	
			}
		}
		
		return vector2DList;
	}

	/**
	 * Returns the number of line segments in the segment 
	 * 
	 * @return double
	 */
	public double getLength() {
		return this.segments.size();
	}	
	
	public double getLenghtDistance() {
		
		double distance = 0.0;
		
		for(Segment segment : this.segments) {
			
			distance += segment.getLength();
		}
		
		return distance;
	}
	
	/**
	 * Returns the intersection of the given Segment2D and this Segment2D.
	 * If they touch, they do not intersect! Will return an empty array if no intersections.
	 * @param line
	 * @return Vector2D
	 */
	public ArrayList<Vector2D> getIntersection(Segment2D openPolygon) {
		
		ArrayList<Vector2D> intersections = new ArrayList<Vector2D>();
		Vector2 intersection = null;
		
		for(Segment otherSegment : openPolygon.segments) {

			for(Segment mySegment : this.segments) {
				
				intersection = mySegment.getSegmentIntersection(otherSegment);
				
				if(intersection != null) {
					
					intersections.add(new Vector2D(intersection.x, intersection.y));
				}
				
				intersection = null;
			}
		}
		
		return intersections;
	}

	@Override
	public ArrayList<Vector2D> getIntersection(Polygon2D polygon) {
		
		return this.getIntersection(polygon.polygonAsSegments());
	}

	@Override
	public ArrayList<Vector2D> getIntersection(Cycle2D cycle) {
	
		return cycle.getIntersection(this);
	}
	
	/**
	 * Returns point1 in local coordinates.
	 * 
	 * @return Vector2D
	 */
	public Vector2D getFirstPoint() {
		
		Vector2 point1 = this.segments.get(0).getPoint1();
	
		return new Vector2D(point1.x, point1.y);
	}	

	/**
	 * Returns point2 in local coordinates.
	 * 
	 * @return Vector2D
	 */
	public Vector2D getLastPoint() {
		
		Vector2 point2 = this.segments.get(this.segments.size() - 1).getPoint2();
		
		return new Vector2D(point2.x, point2.y);
	}
	
	@Override
	public boolean contains(Vector2D point) {
		
		boolean result = false;
		
		for(Segment mySegment : this.segments) {
			
			result =  mySegment.contains(point.getVector());
			
			if(result) {
				
				break;
			}
		}
		
		return result;
	}
	
	@Override
	public boolean contains(Vector2D point, Transform transform) {
		
		boolean result = false;
		
		for(Segment mySegment : this.segments) {
			
			result =  mySegment.contains(point.getVector(), transform);
			
			if(result) {
				
				break;
			}
		}
		
		return result;
	}
	
	/**
	 * Returns the point on this Segment2D closest to the given point.
	 * The returned vector2d is a point which could be anywhere on the segement2d, created
	 * by the projection to on all subsegments and selecting the projection which is
	 * the closest to the point. Vector is directed from Point to Segment.
	 * @param point
	 * @return Vector2D
	 */
	public Vector2D getPointOnSegmentClosestToVector(Vector2D point) {
		
		Vector2 closest = this.segments.get(0).getPointOnSegmentClosestToPoint(point.getVector());
		Vector2 current = closest;
		
		for(int iter = 1; iter < this.segments.size(); iter++) {
			
			current = this.segments.get(iter).getPointOnSegmentClosestToPoint(point.getVector());
			
			if(closest.difference(point.getVector()).getMagnitude() >
				current.difference(point.getVector()).getMagnitude()) {
				
				closest = current;
			}
		}

		return new Vector2D(closest.x, closest.y);
	}

	@Override
	public void rotate(double theta, double x, double y) {
		
		for(Segment mySegment : this.segments) {
			
			 mySegment.rotate(theta, x, y);
		}
	}

	@Override
	public void rotate(double theta) {
		
		for(Segment mySegment : this.segments) {
			
			 mySegment.rotate(theta);
		}
	}
	
	@Override
	public void rotate(double theta, Vector2D point) {
		
		for(Segment mySegment : this.segments) {
			
			 mySegment.rotate(theta, point.getVector());
		}
	}
	
	@Override
	public void translate(double x, double y) {
		
		for(Segment mySegment : this.segments) {
			
			 mySegment.translate(x, y);
		}
	}
	
	@Override
	public void translate(Vector2D vector) {
		
		for(Segment mySegment : this.segments) {
			
			 mySegment.translate(vector.getVector());
		}
	}
	
	@Override
	public double distanceBetween(Vector2D point) {
		
		return this.getPointOnSegmentClosestToVector(point).difference(point).getMagnitude();
	}
	
	@Override
	public double minimalDistanceBetween(List<Vector2D> points) {
	
		double minimalDistance = Double.MAX_VALUE;
		
		for(Vector2D point : points) {
			
			minimalDistance = FastMath.min(this.distanceBetween(point), minimalDistance);
		}
		
		return minimalDistance;
	}
	
	@Override
	public Vector2D vectorBetween(Vector2D point) {
		
		return this.getPointOnSegmentClosestToVector(point).difference(point);
	}

	@Override
	public Mass createMass(double density) {
		
		ArrayList<Mass> masses = new ArrayList<Mass>();
		
		for(Segment segment : this.segments) {
			
			masses.add(segment.createMass(density));
		}
		
		return Mass.create(masses);
	}

	@Override
	public List<Segment2D> getSegments() {
		
		return this.getLineSegments();
	}
	
	/**
	* Creates an AABB Axis Aligned BoundingBox from this Shape.
	*
	* @return AABB
	*/
	@Override
	AABB createAxisAlignedBoundingBox() {
		
		AABB boundingBox = null;
		
		for(Segment segement : segments) {
			
			if(boundingBox == null) {
				
				boundingBox = segement.createAABB();
			}
			else {
				boundingBox.union(segement.createAABB());
			}
		}
		
		return boundingBox;
	}
	
//	@Override
//	public Vector2D[] getAxes(Vector2D[] vector, Transform transform) {
//		
//		
//		ArrayList<Vector2> foci2 = new ArrayList<Vector2>();
//		
//		for (int i = 0; i <= foci.length; i++) {
//			
//			foci2.add(foci[i].vector);
//		}
//		
//		Vector2[] vector2List = this.segment.getAxes((Vector2[])foci2.toArray(), transform);
//		
//		Vector2D[] vector2DList = new Vector2D[vector2List.length];
//			
//		for (int i = 0; i <= vector2List.length; i++) {
//			
//			vector2DList[i] = new Vector2D(vector2List[i].x, vector2List[i].y);
//		}
//		
//		return vector2DList;
//		return null;
//	}

//	@Override
//	public Vector2D[] getFoci(Transform transform) {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	public double getRadius() {
		
		return 0.0;
	}

	@Override
	public double getRadius(Vector2D vector) {
		
		return 0.0;
	}

	@Override
	public Vector2D getCenter() {

		if(this.segments.size() == 1) {
			
			Segment segment = this.segments.get(0);
			Vector2 centerGobalCoordinates = segment.getCenter();
		
			return GeometryFactory.createVector(centerGobalCoordinates.x, centerGobalCoordinates.y);
		}
		
		return null;
	}
	
	@Override
	public Vector2D getPointClosestToVector(Vector2D toVector) {
		
		return this.getPointOnSegmentClosestToVector(toVector);
	}
	
	
//	@Override
//	public Interval project(Vector2D vector) {
//		
//		Interval interval = null;
//		
//		for(Segment segment : this.segments) {
//			
//			if(interval == null) {
//			
//				interval = segment.project(vector.getVector());
//			}
//			else {
//			
//				interval.union(segment.project(vector.getVector()));
//			}
//		}
//		
//		return interval;
//	}
//
//	@Override
//	public Interval project(Vector2D vector, Transform transform) {
//		
//		Interval interval = null;
//		
//		for(Segment segment : this.segments) {
//			
//			if(interval == null) {
//			
//				interval = segment.project(vector.getVector(), transform);
//			}
//			else {
//			
//				interval.union(segment.project(vector.getVector(), transform));
//			}
//		}
//		
//		return interval;
//	}

	/**
	 * Checks if a vector is on the segment, this will not check if the vector is
	 * a segment corner point.
	 * @param polygon
	 * @param vector
	 * @return
	 */
	@Override
	public boolean isOnLines(Vector2D vector, double precision) {
		
		if(!this.isOnCorners(vector, precision)) {
			
			if(this.getPointOnSegmentClosestToVector(vector).subtract(vector).getMagnitude() < precision) {
				
				return true;
			}
		}
		
		return false;
	}

	/**
	 * Checks if a vector is a segment corner point
	 * @param segment
	 * @param vector
	 * @return
	 */
	@Override
	public boolean isOnCorners(Vector2D vector, double precision) {
		
		for(Vector2D onSegmentPoint : this.getVertices()) {
			
			if(vector.distance(onSegmentPoint) < precision) {
				
				return true;
			}
		}

		return false;	
	}
	
	/**
	 * Returns the array of edge normals in local coordinates.
	 * 
	 * @return Vector2D
	 */
	public List<Vector2D> getNormals() {
		
		Vector2[] vector2Array = this.segments.get(0).getNormals();
		
		List<Vector2D> vector2DList = new ArrayList<Vector2D>();
		
		for(int i = 0; i < vector2Array.length; i++) {
			
			vector2DList.add(GeometryFactory.createVector(vector2Array[i].x, vector2Array[i].y));
		}
		
		return vector2DList;
	}
	
	/**
	 * Returns a List of (smaller) segments for the segment. If possible, the length of the new Segments
	 * is @param maxLength, the two segments at the ends could be smaller
	 * 
	 * @param maxLength
	 * @return List<Segment2D>
	 */
	public List<Segment2D> getLineSegmentsSplitted(double maxLength) {
		
		ArrayList<Segment2D> result = new ArrayList<Segment2D>();
			
		Vector2D firstPoint = this.getFirstPoint();
		Vector2D secPoint = this.getLastPoint();
		
		if(firstPoint.distance(secPoint) > maxLength) {
			
			Vector2D midPoint = firstPoint.sum(secPoint).multiply(0.5);
			Vector2D direction = secPoint.difference(firstPoint);
			Vector2D unitDir = direction.getNormalized();
			Vector2D midPoint1 = midPoint.subtract(unitDir.multiply(maxLength/2));
			Vector2D midPoint2 = midPoint.sum(unitDir.multiply(maxLength/2));
			
			//add Segment in the middle to List
			result.add(GeometryFactory.createSegment(midPoint1,midPoint2));
			
			int i = 0;
			
			//now proceed from the middle to the one and the other end
			//and add the new Segments to the list
			while (i < FastMath.abs(midPoint.distance(firstPoint))/(maxLength) - 1) {
				
				i++;
				
				result.add(GeometryFactory.createSegment(midPoint1.subtract(unitDir.multiply((i-1)*maxLength)),
						midPoint1.subtract(unitDir.multiply((i)*maxLength))));
				result.add(GeometryFactory.createSegment(midPoint2.sum(unitDir.multiply((i-1)*maxLength)),
						midPoint2.sum(unitDir.multiply((i)*maxLength))));
			}
			
			//the last segment on either end might be smaller than maxLength
			//these are added here
			if(!result.get(result.size()-1).getLastPoint().equals(secPoint)) {
				
				result.add(GeometryFactory.createSegment(midPoint1.subtract(unitDir.multiply((i)*maxLength)),
						firstPoint));
				result.add(GeometryFactory.createSegment(midPoint2.sum(unitDir.multiply((i)*maxLength)),
						secPoint));
			}
			
		}
		else { // if Segment is already small enough
			
			result.add(this);
		}
	
		return result;	
	}
	
	
	/**
	 * Returns a List of (smaller) segments for the segment. All new Segments have equal length, 
	 * which is equal to or smaller than @param maxLength
	 * The give segment which on this method is called have to have only 1 segement element
	 * @param maxLength
	 * @return List<Segment2D>
	 */
	public List<Segment2D> getLineSegmentsSplittedEqually(double maxLength, Double roundPrecision) {
		
		ArrayList<Segment2D> result = new ArrayList<Segment2D>();
	
		Vector2D start = this.getFirstPoint();
		Vector2D end = this.getLastPoint();
		
		double length = start.distance(end);
		int numberSeg = (int)(length/maxLength);
		
		if(length % maxLength != 0) {
			numberSeg++;
		}
		
		double lengthSeg = length / numberSeg;
		
		if(length > maxLength) {
			
			Vector2D direction = end.difference(start);
			Vector2D unitDir = direction.getNormalized();
			Vector2D current = start.roundTo(roundPrecision);
			
			for (int i = 0; i < numberSeg; i++) {
				
				Segment2D shortSeg = GeometryFactory.createSegment(current, 
						current.sum(unitDir.scale(lengthSeg)).roundTo(roundPrecision));
				
				current = current.sum(unitDir.scale(lengthSeg)).roundTo(roundPrecision);
						
				result.add(shortSeg);
			}
		}
		else {
			
			result.add(this);
		}
	
		return result;	
	}
	
	
	/**
	 * Changed the internal SegmentsList and Segments2DList to equally long segments, 
	 * which are equal to or smaller than @param maxLength
	 * 
	 * @param maxLength
	 */
	public ArrayList<Segment2D> calculateLineSegmentsToEquallySplitted (double maxLength, Double roundPrecision) {
		
		//ArrayList<Segment> segments = new ArrayList<Segment>();	
		ArrayList<Segment2D> segments2D = new ArrayList<Segment2D>();	
		
		// first create small Segment2Ds
		for	(Segment curSeg : this.segments) {
			
			Segment2D curSegCopy = GeometryFactory.createSegment(
					new Vector2D (curSeg.getPoint1()), 
					new Vector2D (curSeg.getPoint2()));
			
			segments2D.addAll(curSegCopy.getLineSegmentsSplittedEqually(maxLength, roundPrecision));
		}
		
		//than "copy" them to basic segments
//		for (Segment2D curSeg2D : segments2D) {
//			segments.addAll(curSeg2D.segments);
//		}
		
		//this.segments = segments;
		return segments2D;
	}

	public Double getSmallestVertixValueOfX() {

		return (Double) this.getVertices().stream()
				.mapToDouble(vertex -> vertex.getXComponent())
				.min()
				.getAsDouble();
	}
	
	public Double getSmallestVertixValueOfY() {

		return (Double) this.getVertices().stream()
				.mapToDouble(vertex -> vertex.getYComponent())
				.min()
				.getAsDouble();
	}
	
	public Double getLargestVertixValueOfX() {

		return (Double) this.getVertices().stream()
				.mapToDouble(vertex -> vertex.getXComponent())
				.max()
				.getAsDouble();
	}
	
	public Double getLargestVertixValueOfY() {

		return (Double) this.getVertices().stream()
				.mapToDouble(vertex -> vertex.getYComponent())
				.max()
				.getAsDouble();
	}

	private Double area = null;
	
	@Override
	public double area() {
		
		if(area == null) {
			
			double area = 0.0;
			ArrayList<Vector2D> points = new ArrayList<Vector2D>(this.getVertices());
			points.add(this.getFirstPoint());
			
			for(int iter = 0; iter < points.size() - 1; iter++) {
				
				area += (points.get(iter).getXComponent() * points.get(iter + 1).getYComponent() -
						 points.get(iter + 1).getXComponent() * points.get(iter).getYComponent());
			}

			this.area =  FastMath.abs(area) / 2.0;
		}
	
		return this.area;
	}
	
//	public List<Segment2D> getLineSegmentsMaxLength(double maxLength) {
//		
//		ArrayList<Segment2D> result = new ArrayList<Segment2D>();
//		
//		for (Segment currentSegment : this.segments) {
//			
//			Vector2 firstPoint = currentSegment.getPoint1();
//			Vector2 secPoint = currentSegment.getPoint2();
//			
//			if(currentSegment.getLength() > maxLength) {
//				
//				Vector2 midPoint = currentSegment.getCenter();
//				Vector2 direction = secPoint.difference(firstPoint);
//				Vector2 unitDir = direction.getNormalized();
//				Vector2 midPoint1 = midPoint.subtract(unitDir.multiply(maxLength/2));
//				Vector2 midPoint2 = midPoint.add(unitDir.multiply(maxLength/2));
//				
//				result.add(GeometryFactory.createSegment(new Vector2D(midPoint1),
//														 new Vector2D(midPoint2)));
//				
//				for (int i = 1; i < FastMath.abs(midPoint.distance(firstPoint))/(maxLength/2); i = i+2) {
//					
//					result.add(GeometryFactory.createSegment(new Vector2D(midPoint1.subtract(unitDir.multiply((i-1)*maxLength))),
//							new Vector2D(midPoint1.subtract(unitDir.multiply((i)*maxLength)))));
//					result.add(GeometryFactory.createSegment(new Vector2D(midPoint2.add(unitDir.multiply((i-1)*maxLength))),
//							new Vector2D(midPoint2.add(unitDir.multiply((i)*maxLength)))));
//				}
//				
//			}else {
//				
//				result.add(GeometryFactory.createSegment(new Vector2D(firstPoint),
//														 new Vector2D(secPoint)));
//			}
//		}
//	return result;	
//	}
		
	
//	public Vector2D getPointOnSegmentClosestToPoint(Vector2D point) {
//		
//		Vector2 closest = this.segments.get(0).getPointOnSegmentClosestToPoint(point.getVector());
//		Vector2 current = closest;
//		
//		for(int iter = 1; iter < this.segments.size(); iter++) {
//			
//			current = this.segments.get(iter).getPointOnSegmentClosestToPoint(point.getVector());
//			
//			if(closest.difference(point.getVector()).getMagnitudeSquared() >
//				current.difference(point.getVector()).getMagnitudeSquared()) {
//				
//				closest = current;
//			}
//		}
//		
//		return new Vector2D(closest.x, closest.y);
//	}	


	
//	@Override
//	public Interval project(Vector2D vector) {
//		return this.segments.project(vector.vector);
//	}
//	
//	@Override
//	public Interval project(Vector2D vector, Transform transform) {
//		return this.segment.project(vector.vector, transform);
//	}

	// Not used atm
	//	@Override
	//	public AABB createAABB(Transform transform) {
	//		if(this.polygon != null) {
	//			return this.polygon.createAABB(transform);
	//		}
	//		else {
	//			return this.segment.createAABB(transform);
	//		}
	//	}
	//
	//	@Override
	//	public AABB createAABB() {
	//		if(this.polygon != null) {
	//			return this.polygon.createAABB();
	//		}
	//		else {
	//			return this.segment.createAABB();
	//		}
	//	}
}
