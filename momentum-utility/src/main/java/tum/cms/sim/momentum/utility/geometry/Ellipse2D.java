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

//import georegression.struct.shapes.EllipseRotated_F64;
//import georegression.fitting.ellipse.ClosestPointEllipseAngle_F64;
//import georegression.struct.point.Point2D_F64;

public class Ellipse2D {

	//private EllipseRotated_F64 ellipse = null;
	//private ClosestPointEllipseAngle_F64 closestPointCalculations = new ClosestPointEllipseAngle_F64(1E-8,100);

	public Ellipse2D(Vector2D center, Vector2D direction, double majorAxis, double minorAxis)
	{
		//double phi = Math.atan2(direction.getYComponent(), direction.getXComponent());
		//this.ellipse = new EllipseRotated_F64(center.getXComponent(), center.getYComponent(), majorAxis, minorAxis, phi);
	}

	/**
	 * Returns the point, which is on the ellipse and closest to the given point
	 * @param point given point
	 * @return closest point
	 */
	public Vector2D closestPoint(Vector2D point) {

		//closestPointCalculations.setEllipse(ellipse);
		//Point2D_F64 pointConverted = new Point2D_F64(point.getXComponent(), point.getYComponent());
		//closestPointCalculations.process(pointConverted);
		//Point2D_F64 closestPoint = closestPointCalculations.getClosest();
		//return new Vector2D(closestPoint.x, closestPoint.y);
		return null;
	}

	/**
	 * Returns a vector from this figure to the given point
	 * @param point given point
	 * @return vector from closest point on ellipse to the given point
	 */
	public Vector2D vectorBetween(Vector2D point) {

		Vector2D closestPoint = closestPoint(point);
		return point.subtract(closestPoint);
	}

	/**
	 * Returns the normal vector
	 * @param point
	 * @return
	 */
	public Vector2D normal(Vector2D point) {
		Vector2D pointOnEllipse = closestPoint(point);
		// transform
		pointOnEllipse = pointOnEllipse.subtract(this.getCenter()).rotate(-this.getOrientation());

		Vector2D normal = new Vector2D(pointOnEllipse.getXComponent() * getMinorAxis()/getMajorAxis(),
				pointOnEllipse.getYComponent() * getMajorAxis()/getMinorAxis());

		return normal.rotate(this.getOrientation()).getNormalized();
	}


	public Vector2D getCenter() {
		//return new Vector2D(ellipse.getCenter().x, ellipse.getCenter().y);
		return null;
	}

	public void setCenter(Vector2D center) {
		//this.ellipse.setCenter(new Point2D_F64(center.getXComponent(), center.getYComponent()));
    }

	public double getOrientation() {
		//return ellipse.getPhi();
		return 0;
	}

	public double getMajorAxis() {
		//return ellipse.getA();
		return 0;
	}

	public double getMinorAxis() {
		//return this.ellipse.getB();
		return 0;
	}

	public void translate(double x, double y) {
		//Point2D_F64 translatedCenter = new Point2D_F64(ellipse.center.x + x, ellipse.center.y + y);
		//this.ellipse.setCenter(translatedCenter);

	}

	public void translate(Vector2D vector) {
		this.translate(vector.getXComponent(), vector.getYComponent());
	}


}