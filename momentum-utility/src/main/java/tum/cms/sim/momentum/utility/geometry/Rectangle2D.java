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
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;

public class Rectangle2D extends Geometry2D {

	private Rectangle rectangle = null;
	
	
	public Rectangle2D(Vector2D center, Vector2D direction, double width, double height) {
		 
		this.rectangle = new Rectangle(width, height);
		if (direction.getMagnitude() != 0) {
			Vector2D yAxis = new Vector2D(0, 1);
			double angle = yAxis.getAngleBetween(direction);
			this.rectangle.rotate( angle );
		}
		this.rectangle.translate(center.getVector());
	}


	
	@Override
	public double distanceBetween(Vector2D point) {
		return this.vectorBetween(point).getMagnitude();
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
		return this.rectangleAsSegments().vectorBetween(point);
	}

	@Override
	public ArrayList<Vector2D> getIntersection(Segment2D segment) {
		return this.rectangleAsSegments().getIntersection(segment);
	}

	@Override
	public ArrayList<Vector2D> getIntersection(Polygon2D polygon) {
		return this.rectangleAsSegments().getIntersection(polygon.polygonAsSegments());
	}

	@Override
	public ArrayList<Vector2D> getIntersection(Cycle2D segment) {
		return this.rectangleAsSegments().getIntersection(segment);
	}

	@Override
	public boolean isOnCorners(Vector2D vector, double precision) {
		
		for(Vector2D onPolyPoint : this.getVertices()) {
			if(vector.distance(onPolyPoint) < precision) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean isOnLines(Vector2D vector, double precision) {
		if(!this.isOnCorners(vector, precision)) {
			
			if(this.rectangleAsSegments().getPointOnSegmentClosestToVector(vector).getMagnitude() < precision) {
				
				return true;
			}
		}
		return false;
	}

	@Override
	public List<Vector2D> getVertices() {
		
		Vector2[] vector2List = this.rectangle.getVertices();
		ArrayList<Vector2D> vector2DList = new ArrayList<Vector2D>();
		
		for (int i = 0; i < vector2List.length; i++) {
			vector2DList.add(new Vector2D(vector2List[i].x, vector2List[i].y));
		}

		return vector2DList;
	}
	
	public synchronized Segment2D rectangleAsSegments() {
		
		ArrayList<Vector2D> segmentCorners = new ArrayList<Vector2D>(this.getVertices());
		segmentCorners.add(segmentCorners.get(0));
		
		return GeometryFactory.createSegment(segmentCorners);
	}

	@Override
	public List<Segment2D> getSegments() {
		return rectangleAsSegments().getLineSegments();
	}
	
	@Override
	public Vector2D getPointClosestToVector(Vector2D toVector) {
		return this.rectangleAsSegments().getPointClosestToVector(toVector);
	}

	@Override
	public double area() {
		return rectangle.getWidth() * rectangle.getHeight();
	}

	@Override
	public boolean contains(Vector2D point) {
		return rectangle.contains(point.getVector());
	}

	@Override
	public boolean contains(Vector2D point, Transform transform) {
		return rectangle.contains(point.getVector(), transform);
	}

	@Override
	public Mass createMass(double density) {
		return this.rectangle.createMass(density);
	}

	@Override
	public double getRadius() {
		return rectangle.getRadius();
	}

	@Override
	public double getRadius(Vector2D vector) {
		return rectangle.getRadius(vector.getVector());
	}

	@Override
	public Vector2D getCenter() {
		return new Vector2D(this.rectangle.getCenter().x, this.rectangle.getCenter().y);
	}

	@Override
	public void rotate(double theta) {
		this.rectangle.rotate(theta);
	}

	@Override
	public void rotate(double theta, double x, double y) {
		this.rectangle.rotate(theta, x, y);
	}

	@Override
	public void rotate(double theta, Vector2D vector) {
		this.rectangle.rotate(theta, vector.getVector());
	}

	@Override
	public void translate(double x, double y) {
		this.rectangle.translate(x, y);
		
	}

	@Override
	public void translate(Vector2D vector) {
		this.rectangle.translate(vector.getVector());
	}

	@Override
	AABB createAxisAlignedBoundingBox() {
		return this.rectangle.createAABB();
	}

}
