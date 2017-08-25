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
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Transform;

/**
 * All class and method descriptions have been adopted from the dyn4j library documentation 
 * http://docs.dyn4j.org/v3.1.10/.
 * 
 * Represents a Circle.
 * 
 * A Cycle2D's radius must be larger than zero.
 * 
 * @author berndtornede, pk
 *
 */
public class Cycle2D extends Geometry2D {

	private Circle circle = null;
	
	private Vector2D center;
	
	/**
	 * Full constructor.
	 * 
	 * @param centerX
	 * @param centerY
	 * @param radius
	 */
	Cycle2D(double centerX, double centerY, double radius) {
		
		
		this.center = new Vector2D(centerX, centerY);		
		this.circle = new Circle(radius);		
		this.circle.getCenter().getDirection();
	}
	
	/**
	 * Full constructor.
	 * 
	 * @param center
	 * @param radius
	 */
	Cycle2D(Vector2D center, double radius) {
		
		this.center = center;	
		this.circle = new Circle(radius);	
	}
	
	/**
	 * Returns the center of this Cycle2D in local coordinates.
	 * 
	 * @return Vector2D
	 */
	public Vector2D getCenter() {
		return this.center;
	}

	@Override
	public boolean contains(Vector2D point, Transform transform) {
		return this.circle.contains(point.getVector(), transform);
	}
	
	@Override
	public boolean contains(Vector2D point) {
		return this.circle.contains(point.getVector());
	}
	
	@Override
	public double distanceBetween(Vector2D point) {
		return point.distance(this.center) - this.getRadius();
	}
	
	@Override
	public double minimalDistanceBetween(List<Vector2D> points) {
	
		double minimalDistance = Double.MAX_VALUE;
		
		for(Vector2D point : points) {
			
			minimalDistance = FastMath.min(this.distanceBetween(point), minimalDistance);
		}
		
		return minimalDistance;
	}
	
	public double distanceBetween(Cycle2D other) {
		
		return other.getCenter().distance(this.getCenter()) - (other.getRadius() + this.getRadius());
	}
	
	@Override
	public Vector2D vectorBetween(Vector2D point) {
		
		double distance = this.distanceBetween(point);
		
		Vector2D between = point.difference(this.center);
		
		return between.scale(distance);
	}
	
	@Override
	public Mass createMass(double density) {
		return this.circle.createMass(density);
	}
	
	@Override
	public Vector2D getPointClosestToVector(Vector2D toVector) {
		
		return null;
	}
	
	public boolean isIntersected(Polygon2D polygon) {
		
		return this.isIntersected(polygon.polygonAsSegments());
	}
	
	/**
	 * checks if a polygon intersects with a circle
	 * 
	 * @param segment
	 * @return boolean
	 */
	public boolean isIntersected(Segment2D polySegment) {
		
		Vector2D pointClosest = polySegment.getPointOnSegmentClosestToVector(this.center);
		
		if (pointClosest.distance(this.center) > this.getRadius()) {			
			
			return false;
		}
		return true;
	}
//	@Override
//	public Vector2D[] getAxes(Vector2D[] foci, Transform transform) {
//		
//		Vector2[] foci2 = new Vector2[foci.length];
//		
//		for (int i = 0; i <= foci.length; i++) {
//			
//			foci2[i] = new Vector2(foci[i].getVector(), foci[i].getVector());
//		}
//		
//		foci2 = this.circle.getAxes(foci2, transform);
//		
//		for (int i = 0; i <= foci.length; i++) {
//			
//			foci[i] = new Vector2D(foci2[i].x, foci2[i].y);
//		}
//		
//		return foci;
//	}
	
//	@Override
//	public Vertex getFarthestFeature(Vector2D point, Transform transform) {
//		return this.circle.getFarthestFeature(point.getVector(), transform);
//	}
//	
//	@Override
//	public Vector2D getFarthestPoint(Vector2D point, Transform transform) {
//		
//		Vector2 farthest = this.circle.getFarthestPoint(point.getVector(), transform);
//		
//		return new Vector2D(farthest.x, farthest.y);
//	}

//	@Override
//	public Vector2D[] getFoci(Transform transform) {
//		
//		Vector2[] foci2 = this.circle.getFoci(transform);
//		
//		Vector2D[] foci2D = new Vector2D[foci2.length];
//		
//		for (int i = 0; i <= foci2.length; i++) {
//			
//			foci2D[i] = new Vector2D(foci2[i].x, foci2[i].y);
//		}
//		
//		return foci2D;
//	}
//	
	@Override
	public double getRadius(Vector2D center) {
		return this.circle.getRadius(center.getVector());
	}
	
	@Override
	public double getRadius() {
		return this.circle.getRadius();
	}
	
//	@Override
//	public Interval project(Vector2D axis, Transform transform) {
//		return this.circle.project(axis.getVector(), transform);
//	}
//	
//	@Override
//	public Interval project(Vector2D vector) {
//		return this.circle.project(vector.getVector());
//	}

	@Override
	public void rotate(double theta) {
		this.circle.rotate(theta);
	}
	
	@Override
	public void rotate(double theta, double x, double y) {
		this.circle.rotate(theta, x, y);
	}	
	
	@Override
	public void rotate(double theta, Vector2D point) {
		this.circle.rotate(theta, point.getVector());
	}
	
	@Override
	public void translate(double x, double y) {
		this.circle.translate(x, y);
	}	

	@Override
	public void translate(Vector2D vector) {
		this.circle.translate(vector.getVector());
	}

	/**
	 * Referring to algorithm by David H. Eberly (2006), 
	 * 3D game engine design: a practical approach to real-time computer graphics, 2nd edition, Morgan Kaufmann. ISBN 0-12-229063-1
	 * (http://en.wikipedia.org/wiki/Line–sphere_intersection)
	 * 
	 * @param segment
	 * @return all intersections or null
	 */
	@Override
	public ArrayList<Vector2D> getIntersection(Segment2D polySegment) {
			
		Vector2D pointClosest = polySegment.getPointOnSegmentClosestToVector(this.center);
		
		if (pointClosest.distance(this.center) > this.getRadius()) {
			
			return new ArrayList<Vector2D>();
		}
		else if (pointClosest.distance(this.center) == this.getRadius()) {
			
			ArrayList<Vector2D> intersectionPoints = new ArrayList<Vector2D>();
			intersectionPoints.add(pointClosest);
			return intersectionPoints;
		}
		else {
			
			ArrayList<Vector2D> intersectionPoints = new ArrayList<Vector2D>();
			List<Vector2D> vertices = polySegment.getVertices();
			Vector2D discriminantUnitVector = null;
			
			for(int iter = 0; iter < vertices.size() - 1; iter++) {
				
				discriminantUnitVector = vertices.get(iter).difference(vertices.get(iter + 1)).scale(1);
				double dotProduct = discriminantUnitVector.dot(vertices.get(iter).subtract(this.center));
				double differenceMagnitued = vertices.get(iter).subtract(this.center).getMagnitude();
				double discriminant = FastMath.pow(dotProduct, 2) - (differenceMagnitued - FastMath.pow(this.getRadius(), 2));
				
				if (discriminant < 0) {
					return new ArrayList<Vector2D>();
				}
				else if (discriminant == 0) {
	
					Vector2D touch = polySegment.getLineSegments().get(iter).getPointOnSegmentClosestToVector(this.center);
					intersectionPoints.add(new Vector2D(touch));
				}
				else {
	
					double distanceToGo = (-1) * dotProduct + discriminant;
					intersectionPoints.add(vertices.get(iter).sum(discriminantUnitVector.scale(distanceToGo)));
					
					distanceToGo = (-1) * dotProduct - discriminant;
					intersectionPoints.add(vertices.get(iter).sum(discriminantUnitVector.scale(distanceToGo)));
				}
			}
			
			return intersectionPoints;
		}
	}

	@Override
	public ArrayList<Vector2D> getIntersection(Polygon2D polygon) {
		
		return this.getIntersection(polygon.polygonAsSegments());
	}

	public Vector2D getClosestPointTo(Cycle2D otherCycle) {
	
		Vector2D fromTo = otherCycle.getCenter().subtract(this.getCenter()); //this is start, other is target
		return this.getCenter().sum(fromTo.scale(this.getRadius()));
	}
	
	/**
	 * http://paulbourke.net/geometry/circlesphere/
	 * and
	 * http://stackoverflow.com/questions/3349125/circle-circle-intersection-points
	 * @param otherCycle
	 * @return
	 */
	@Override
	public ArrayList<Vector2D> getIntersection(Cycle2D otherCycle) {

	    double centerDistance = otherCycle.getCenter().distance(this.getCenter());
	    
	    if(centerDistance > otherCycle.getRadius() + this.getRadius()) {
	    	
	    	return null; // not intersection
	    }
	    
	    if(centerDistance < FastMath.abs(otherCycle.getRadius() - this.getRadius())) {
	    	
	    	return null; // inside of each other
	    }
	    
	    // a = (r_0^2 - r_1^2 + d^2 ) / (2d) 
	    double distanceCenterToMiddle = (otherCycle.getRadius() * otherCycle.getRadius() 
	    		- this.getRadius() * this.getRadius() 
	    		+ centerDistance * centerDistance) 
	    		/ (2.0 * centerDistance);
	    

	    // h = sqrt(r_0^2 - a^2)
	    double distanceMiddleToIntersection = FastMath.sqrt(otherCycle.getRadius() * otherCycle.getRadius() 
	    		- distanceCenterToMiddle * distanceCenterToMiddle);
	    		
	    // P_2 = P_0 + a ( P_1 - P_0 ) / d
	    Vector2D middle = otherCycle.getCenter().sum(
	    		this.getCenter().subtract(otherCycle.getCenter()).multiply(distanceCenterToMiddle / centerDistance)
	    		);	
	    
	    ArrayList<Vector2D> intersections = new ArrayList<Vector2D>();
	    
	    // x3 = x2 +- h ( y1 - y0 ) / d
	    // y3 = y2 -+ h ( x1 - x0 ) / d
	    double xIntersectionA = middle.getXComponent() + 
	    		(distanceMiddleToIntersection / centerDistance) 
	    		* (otherCycle.getCenter().getYComponent() - this.getCenter().getYComponent());
	    
	    double yIntersectionA = middle.getYComponent() -
	    		(distanceMiddleToIntersection / centerDistance) 
	    		* (otherCycle.getCenter().getXComponent() - this.getCenter().getXComponent());
	    
	    double xIntersectionB = middle.getXComponent() - 
	    		(distanceMiddleToIntersection / centerDistance) 
	    		* (otherCycle.getCenter().getYComponent() - this.getCenter().getYComponent());
	    
	    double yIntersectionB = middle.getYComponent() +
	    		(distanceMiddleToIntersection / centerDistance) 
	    		* (otherCycle.getCenter().getXComponent() - this.getCenter().getXComponent());
	    
	    Vector2D intersectionA = GeometryFactory.createVector(xIntersectionA, yIntersectionA);
	    Vector2D intersectionB = GeometryFactory.createVector(xIntersectionB, yIntersectionB);
 
	    intersections.add(intersectionA);
	    intersections.add(intersectionB);

		return intersections;
	}
	
	/**
	 * Checks if a vector is on the cycle.
	 * @param cycle
	 * @param vector
	 * @return
	 */
	@Override
	public boolean isOnLines(Vector2D vector, double precision) {
		
		if (this.getCenter().distance(vector) < this.getRadius() + precision &&
				this.getCenter().distance(vector) > this.getRadius() - precision) {
			
			return true;
		}
		else {
			
			return false;
		}
	}
	
	@Override
	public boolean isOnCorners(Vector2D vector, double precision) {
		
		return vector.subtract(this.center).getMagnitude() <= this.getRadius() + precision;
	}
	
	/**
	 * !! this implementation will return null
	 */
	@Override
	public List<Vector2D> getVertices() {
		
		return null;
	}
	
	/**
	 *  !! this implementation will return null
	 * @return
	 */
	@Override
	public List<Segment2D> getSegments() {
		
		return null;
	}
		
	/**
	* Creates an AABB Axis Aligned BoundingBox from this Shape.
	*
	* @return AABB
	*/
	@Override
	AABB createAxisAlignedBoundingBox() {
		
		return this.circle.createAABB();
	}

	@Override
	public double area() {
		
		return FastMath.PI * FastMath.pow(this.circle.getRadius(), 2.0);
	}
	
	//	@Override
	//	public AABB createAABB(Transform transform) {
	//		return this.circle.createAABB(transform);
	//	}
	//
// cycle intersections stuff
//	Variation 1:
//	double eps = Double.MIN_VALUE;
//	double phi = 0;
//	Vector2D pointOnCycle = null;
//	
//	while (phi < 2 * FastMath.PI || intersectionPoints.size() < 2) {
//		
//		Vector2D radialVector = GeometryFactory.createVector(this.getRadius() * FastMath.cos(phi), 
//																this.getRadius() * FastMath.sin(phi));
//		pointOnCycle = this.center.add(radialVector);
//		
//		if (intersectingLine.contains(pointOnCycle) == true) {
//			intersectionPoints.add(pointOnCycle);
//		}
//		
//		phi += eps;
//	}
//	
//	return (Vector2D[])intersectionPoints.toArray();
	
//	Variation 2:
//	double eps = Double.MIN_VALUE;
//	Vector2D wayVectorScaledToEps = intersectingLine.getPoint2().difference(intersectingLine.getPoint1()).scale(eps);
//	double lengthOfSegment = intersectingLine.getPoint1().distance(intersectingLine.getPoint2());
//	Vector2D currentPointToCheck = intersectingLine.getPoint1();
//	
//	while (intersectionPoints.size() < 2 || wayVectorScaledToEps.getMagnitude() <= lengthOfSegment) {
//		
//		if (this.contains(currentPointToCheck) == true) {
//			intersectionPoints.add(currentPointToCheck);
//		}
//		
//		currentPointToCheck.add(wayVectorScaledToEps);
//	}
//	
//	return (Vector2D[])intersectionPoints.toArray();
	
//	Variation 3:
}
