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
import java.util.List;

import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Transform;

import tum.cms.sim.momentum.utility.geometry.Cycle2D;
import tum.cms.sim.momentum.utility.geometry.Polygon2D;
import tum.cms.sim.momentum.utility.geometry.Segment2D;
import tum.cms.sim.momentum.utility.geometry.Vector2D;

/**
 * All class and method descriptions have been adopted from the dyn4j library
 * documentation http://docs.dyn4j.org/v3.1.10/.
 * 
 * Represents a geometric shape.
 * 
 * Shapes are transformable, however, in general a Transformable2D object should
 * be used instead of directly transforming the Shape2D. Doing so will allow
 * reuse of the same Shape object in multiple places, where only the Transform
 * differs.
 * 
 * @author berndtornede, pk
 * 
 */
public interface IShape2D extends ITransformable2D {

	/**
	 * Creates a Mass object using the geometric properties of this Shape2D and
	 * the given density.
	 * 
	 * @param density
	 * @return Mass
	 */
	Mass createMass(double density);

	/**
	 * Returns the maximum radius of the shape from the center.
	 * 
	 * @return double
	 */
	double getRadius();

	/**
	 * Returns the radius of the shape if the given point was the center for
	 * this shape.
	 * 
	 * @param vector
	 * @return double
	 */
	double getRadius(Vector2D vector);
	
	/**
	 * Returns the center/centroid of the Shape in local coordinates.
	 * 
	 * @return Vector2D
	 */
	Vector2D getCenter();

	/**
	 * Returns the Interval of this Shape projected onto the given Vector2 given
	 * the Transform.
	 * 
	 * @param vector
	 * @return Interval
	 */
	//Interval project(Vector2D vector);

	/**
	 * Returns the Interval of this Shape projected onto the given Vector2 given
	 * the Transform.
	 * 
	 * @param vector
	 * @param transform
	 * @return Interval
	 */
	//Interval project(Vector2D vector, Transform transform);

	/**
	 * Returns true if the given point is inside or on this.
	 * 
	 * @param point
	 * @return boolean
	 */
	boolean contains(Vector2D point);

	/**
	 * Returns true if the given point is inside or on this.
	 * 
	 * @param points
	 * @param transform
	 * @return boolean
	 */
	boolean contains(Vector2D points, Transform transform);

	/**
	 * Returns the distance between this figure and a given point.
	 * 
	 * @param point
	 * @return double
	 */
	public abstract double distanceBetween(Vector2D point);
	

	/**
	 * Returns the minimal distance between this figure and a given point.
	 * 
	 * @param point
	 * @return double
	 */
	public abstract double minimalDistanceBetween(List<Vector2D> points);

	/**
	 * Returns the local Vector2D between this figure and a given point.
	 * Vector is directed from point to geometry.
	 * @param point
	 * @return Vector2D
	 */
	public abstract Vector2D vectorBetween(Vector2D point);
	
	/**
	 * Returns the line intersection of the given Segment2D and this Segment2D.
	 * 
	 * @param line
	 * @return Vector2D
	 */
	public ArrayList<Vector2D> getIntersection(Segment2D segment);
	
	/**
	 * Returns the line intersection of the given Segment2D and this Segment2D.
	 * 
	 * @param line
	 * @return Vector2D
	 */
	public ArrayList<Vector2D> getIntersection(Polygon2D polygon);
	
	/**
	 * Returns the line intersection of the given Segment2D and this Segment2D.
	 * 
	 * @param line
	 * @return Vector2D
	 */
	public ArrayList<Vector2D> getIntersection(Cycle2D segment);
	
	/**
	 * Returns true if the vector is at a corner of the shape, if the
	 * shape do not have corners (= vectors which describe the shape) this
	 * will always return false.
	 * @param vector
	 * @param precision
	 * @return
	 */
	public boolean isOnCorners(Vector2D vector, double precision);
	
	/**
	 * Returns true if the vector is at a line of the shape, but not on the corner (see IsOnCorner).
	 * If the shape itself is made of infinite vectors (= cycle) this
	 * will test if the given vector is on that "line".
	 * @param vector
	 * @param precision
	 * @return
	 */
	public boolean isOnLines(Vector2D vector, double precision);
	
	
	/**
	 * If possible return corner vertices
	 * @return
	 */
	public List<Vector2D> getVertices();
	
	/**
	 * If possible return segments from corner to corner
	 */
	public List<Segment2D> getSegments();
	
	
	public Vector2D getPointClosestToVector(Vector2D toVector);
	
	/**
	 * Calculates the area (e.g. m^2) of the shape.
	 * @return
	 */
	public abstract double area();

	
	// Not used atm
//	
//	/**
//	 * Returns an array of separating axes to test for this Shape2D.
//	 * 
//	 * @param vector
//	 * @param transform
//	 * @return Vector2D[]
//	 */
//	Vector2D[] getAxes(Vector2D[] vector, Transform transform);
//
//	/**
//	 * Returns an array of world space foci points for circular curved edges.
//	 * 
//	 * @param transform
//	 * @return Vector2D[]
//	 */
//	Vector2D[] getFoci(Transform transform);
	
//	/**
//	 * Returns the feature farthest in the direction of n.
//	 * 
//	 * @param vector
//	 * @param transform
//	 * @return Feature
//	 */
//	Feature getFarthestFeature(Vector2D vector, Transform transform);
//
//	/**
//	 * Returns the point farthest in the direction of n.
//	 * 
//	 * @param vector
//	 * @param transform
//	 * @return Vector2D
//	 */
//	Vector2D getFarthestPoint(Vector2D vector, Transform transform);

	//
	//
	// /**
	// * Creates an AABB from this Shape after applying the given transformation
	// to the shape.
	// *
	// * @param transform
	// * @return AABB
	// */
	// AABB createAABB(Transform transform);
}
