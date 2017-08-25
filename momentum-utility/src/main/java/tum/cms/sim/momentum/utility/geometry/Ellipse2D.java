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

import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Ellipse;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Transform;

public class Ellipse2D extends Geometry2D {

	private Ellipse ellipse = null;
	
	public Ellipse2D(Vector2D F1, Vector2D F2, double lengthMinorAxis)
	{
		double height = lengthMinorAxis * 2;
		double width = 2*Math.sqrt( Math.pow(F2.subtract(F1).getMagnitude()/2,2) +
				Math.pow(lengthMinorAxis, 2));
		this.ellipse = new Ellipse(width, height);
		
		Vector2D direction = F2.subtract(F1);
		if (direction.getMagnitude() != 0) {
			Vector2D yAxis = new Vector2D(0, 1);
			double angle = yAxis.getAngleBetween(direction);
			this.ellipse.rotate( angle );
		}

		this.ellipse.translate(F1.sum(F2).multiply(1/2).getVector());
	}
	
	public Ellipse2D(Vector2D center, Vector2D direction, double width, double height)
	{
		this.ellipse = new Ellipse(width, height);
		if (direction.getMagnitude() != 0) {
			Vector2D yAxis = new Vector2D(0, 1);
			double angle = yAxis.getAngleBetween(direction);
			this.ellipse.rotate( angle );
		}
		this.ellipse.translate(center.getVector());
	}

	
	@Override
	public double distanceBetween(Vector2D point) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double minimalDistanceBetween(List<Vector2D> points) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Vector2D vectorBetween(Vector2D point) {
		
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Vector2D> getIntersection(Segment2D segment) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Vector2D> getIntersection(Polygon2D polygon) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Vector2D> getIntersection(Cycle2D segment) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isOnCorners(Vector2D vector, double precision) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isOnLines(Vector2D vector, double precision) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 *  !! this implementation will return null
	 * @return
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

	@Override
	public Vector2D getPointClosestToVector(Vector2D toVector) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double area() {
		return Math.PI * ellipse.getHalfWidth() * ellipse.getHalfHeight();
	}

	@Override
	public boolean contains(Vector2D point) {
		return ellipse.contains(point.getVector());
	}

	@Override
	public boolean contains(Vector2D point, Transform transform) {
		return ellipse.contains(point.getVector(), transform);
	}

	@Override
	public Mass createMass(double density) {
		return ellipse.createMass(density);
	}

	@Override
	public double getRadius() {
		return ellipse.getRadius();
	}

	@Override
	public double getRadius(Vector2D vector) {
		return ellipse.getRadius(vector.getVector());
	}

	@Override
	public Vector2D getCenter() {
		return new Vector2D(ellipse.getCenter().x, ellipse.getCenter().y);
	}

	@Override
	public void rotate(double theta) {
		ellipse.rotate(theta);
	}

	@Override
	public void rotate(double theta, double x, double y) {
		ellipse.rotate(theta, x, y);
	}

	@Override
	public void rotate(double theta, Vector2D vector) {
		ellipse.rotate(theta, vector.getVector());
		
	}

	@Override
	public void translate(double x, double y) {
		ellipse.translate(x, y);
	}

	@Override
	public void translate(Vector2D vector) {
		ellipse.translate(vector.getVector());
		
	}

	@Override
	AABB createAxisAlignedBoundingBox() {
		return ellipse.createAABB();
	}

}
