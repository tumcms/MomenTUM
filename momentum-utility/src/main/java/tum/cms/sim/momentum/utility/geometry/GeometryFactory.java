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
import java.util.stream.Collectors;

import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Triangle;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.geometry.hull.GrahamScan;

/**
 * The GeometryManager handles all geometric objects. It takes over
 * the creation and calculation of Vector2Ds, Segment2Ds, Polygon2Ds and Cycle2Ds.
 * 
 * @author berndtornede, pk
 * 
 */
public class GeometryFactory {

	private GeometryFactory() { }
	
	/**
	 * Returns a Polygon2D from the given vertices.
	 * 
	 * @param nodeArray
	 * @return Polygon2D
	 */
	public static Polygon2D createPolygon(Vector2D[] nodeArray) {
		return new Polygon2D(nodeArray);
	}
	
	/** In correct counter-clockwise winding!
	 * 
	 */
	public static Polygon2D createPolygon(Vector2D first, Vector2D second, Vector2D third) {
		
		Vector2D[] list = new Vector2D[3] ;
		list[0] = first;
		list[1] = second;
		list[2] = third;
		
		return new Polygon2D(list);
	}
	/**
	 * Returns a Polygon2D from the given vertices.
	 * 
	 * @param nodeArray
	 * @return Polygon2D
	 */
	public static Polygon2D createPolygon(Vector2D[] nodeArray, boolean inverse) {
		return new Polygon2D(nodeArray, inverse);
	}
	
	/**
	 * Returns a Polygon2D from the given vertices.
	 * 
	 * @param nodeArray
	 * @return Polygon2D
	 */
	public static Polygon2D createPolygon(ArrayList<Vector2D> nodeArray, boolean inverse) {
		return new Polygon2D(nodeArray, inverse);
	}
	
	/**
	 * Returns a Polygon2D from the given vertices.
	 * 
	 * @param nodeArray
	 * @return Polygon2D
	 */
	public static Polygon2D createPolygon(List<Vector2D> nodeArray) {
		return new Polygon2D(nodeArray);
	}
	

    public static List<Vector2D> calculateConvexHull(List<Vector2D> vectors) {
    	
    	GrahamScan scan = new GrahamScan();
    	List<Vector2> targetVectors = vectors.stream().map(Vector2D::getVector).collect(Collectors.toList());
    	Vector2[] convexHull = scan.generate(targetVectors.toArray(new Vector2[targetVectors.size()]));
    	
    	if(Geometry.getWinding(convexHull) < 0) {
    		
    		Geometry.reverseWinding(convexHull);
    	}
    	
    	ArrayList<Vector2D> result = new ArrayList<>();
    	
    	for(int iter = 0; iter < convexHull.length; iter++) {
    		
    		result.add(GeometryFactory.createVector(convexHull[iter].x, convexHull[iter].y));
    	}
    	
    	return result;
    }
    
	public static List<Polygon2D> triangulatePolygon(List<Vector2D> polygonVertices) {
		
		List<Polygon2D> createdPolygons = new ArrayList<Polygon2D>();
		
		List<Triangle> triangulatedPolygon = SweepLine2D.triangulate(polygonVertices);
		
		for (int i = 0; i < triangulatedPolygon.size(); i++) {
			
			Vector2[] currentTriangleVertices = triangulatedPolygon.get(i).getVertices();
			List<Vector2D> triangleVertices2D = new ArrayList<Vector2D>();
			
			for (int m = 0; m < currentTriangleVertices.length; m++) {
				triangleVertices2D.add(GeometryFactory.createVector(currentTriangleVertices[m].x, currentTriangleVertices[m].y));
			}
			
			createdPolygons.add(GeometryFactory.createPolygon(triangleVertices2D));
		}

		return createdPolygons;
	}
	
	/**
	 * Returns a Segment2D from the given vertices.
	 * 
	 * @param nodeArray
	 * @return Segment2D
	 */
	public static Segment2D createSegment(List<Vector2D> nodeArray) {
		return new Segment2D(nodeArray);
	}
	
	public static Segment2D createSegment(Vector2D[] nodeArray) {
		return new Segment2D(nodeArray);
	}
	/**
	 * Returns a Segment2D from the given vertices.
	 * 
	 * @param nodeArray
	 * @return Segment2D
	 */
	public static Segment2D createSegment(Vector2D start, Vector2D end) {
		return new Segment2D(start, end);
	}
	
	/**
	 * Returns a Cycle2D from the given x, y, and radius.
	 * 
	 * @param centerX
	 * @param centerY
	 * @param radius
	 * @return Cycle2D
	 */
	public static Cycle2D createCycle(double centerX, double centerY, double radius) {
		return new Cycle2D(centerX, centerY, radius);
	}
	
	/**
	 * Returns a Cycle2D from the given center and radius.
	 * 
	 * @param center
	 * @param radius
	 * @return Cycle2D
	 */
	public static Cycle2D createCycle(Vector2D center, double radius) {
		return new Cycle2D(center, radius);
	}
	
	/**
	 * Returns a Rectangle2D from the given center, direction, width, and height
	 * @param center
	 * @param direction
	 * @param width
	 * @param height
	 * @return
	 */
	public static Rectangle2D createRectangle(Vector2D center, Vector2D direction, double width, double height) {
		return new Rectangle2D(center, direction, width, height);
	}

	/**
	 * Returns an Ellipse2D from a given center and direction vector, major and minor axis length
	 * @param center center of ellipse
	 * @param direction direction vector
	 * @param majorAxis length of major axis
	 * @param minorAxis length of minor axis
	 * @return
	 */
	public static Ellipse2D createEllipse(Vector2D center, Vector2D direction, double majorAxis, double minorAxis) {
		return new Ellipse2D(center, direction, majorAxis, minorAxis);
	}

	/**
	 * Returns an Ellipse2D given it's focal po
	 * @param F1 first focal point
	 * @param F2 second focal point
	 * @param minorAxis length of minor axis
	 * @return
	 */
	public static Ellipse2D createEllipse(Vector2D F1, Vector2D F2, double minorAxis) {

		Vector2D center = F1.sum(F2).multiply(1.0/2);
		Vector2D direction = F2.subtract(F1);
		double majorAxis = Math.sqrt( Math.pow(F2.subtract(F1).getMagnitude()/2,2) +
				Math.pow(minorAxis, 2));
		return new Ellipse2D(center, direction, majorAxis, minorAxis);
	}

	/**
	 * Returns a Vector2D from the given local coordinates.
	 * 
	 * @param x
	 * @param y
	 * @return Vector2D
	 */
	public static Vector2D createVector(double x, double y) {
		return new Vector2D(x,y);
	}
		
	/**
	 * Returns a Vector2D from point1 to point2.
	 * 
	 * @param point1
	 * @param point2
	 * @return Vector2D
	 */
	public static Vector2D createVector(Vector2D point1, Vector2D point2) {
		return new Vector2D(point1, point2);
	}
	
	/**
	 * Left can be 0 and right 90 - clockwise orientation
	 */
	public static AngleInterval2D createAngleInterval(double left, double interval) {
		return new AngleInterval2D(left, interval);
	}
	
	/**
	* Creates an AABB Axis Aligned BoundingBox from this Shape.
	*
	* @return AABB
	*/
	public static AxisAlignedBoundingBox2D createAxisAlignedBoundingBox(Geometry2D geometry) {
		
		return new AxisAlignedBoundingBox2D(geometry);
	}
	
	public static Line2D createLine2D(Vector2D referencePoint, Vector2D gradientVector) {
		
		return new Line2D(referencePoint, gradientVector);
	}
}
