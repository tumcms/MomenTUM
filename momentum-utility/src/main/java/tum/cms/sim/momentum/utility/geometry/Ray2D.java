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

import org.dyn4j.geometry.Ray;

public class Ray2D {

	private double EPSILON = 1E-10;

	private Ray ray = null;

	Ray2D(Vector2D start, Vector2D direction) {

		this.ray = new Ray(start.getVector(), direction.getVector());
	}

	public Vector2D getStart() {
		return new Vector2D(ray.getStart().x, ray.getStart().y);
	}

	public void setStart(Vector2D start) {
		this.ray.setStart(start.getVector());
	}

	public Vector2D getDirection() {
		return new Vector2D(ray.getDirectionVector().x, ray.getDirectionVector().y);
	}

	public void setDirection(Vector2D direction) {
		this.ray.setDirection(direction.getVector());
	}

	public Vector2D intersectionPoint(Ray2D other) {

		double dx = other.getStart().getXComponent() - this.getStart().getXComponent();
		double dy = other.getStart().getYComponent() - this.getStart().getYComponent();
		double det = other.getDirection().getXComponent() * this.getDirection().getYComponent() -
				other.getDirection().getYComponent() * this.getDirection().getXComponent();

		if(det == 0) {
			// parallel rays
			return null;
		}

		double u = (dy * other.getDirection().getXComponent() - dx * other.getDirection().getYComponent()) / det;
		double v = (dy * this.getDirection().getXComponent() - dx * this.getDirection().getYComponent()) / det;

		if(u >= 0 && v >= 0) {
			return this.getStart().sum(this.getDirection().multiply(u));
		} else {
			// no intersection due to opposite direction
			return null;
		}
	}

	public Ray2D intersectionRay(Ray2D other) {

		double dotProduct = this.getDirection().getNormalized().dot(other.getDirection().getNormalized());

		if(this.contains(other.getStart()) && !other.contains(this.getStart()) &&
				Math.abs(1.0 - dotProduct) < EPSILON) {
			return new Ray2D(other.getStart(), other.getDirection());
		} else if(!this.contains(other.getStart()) && other.contains(this.getStart()) &&
				Math.abs(1.0 - dotProduct) < EPSILON) {
			return new Ray2D(this.getStart(), this.getDirection());
		} else {
			return null;
		}
	}

	public Segment2D intersectionSegment(Ray2D other) {
		if(this.contains(other.getStart()) && other.contains(this.getStart())) {
			return new Segment2D(this.getStart(), other.getStart());
		} else {
			return null;
		}
	}

	public boolean contains(Vector2D point) {

		Vector2D newNorm = point.subtract(this.getStart()).getNormalized();

		double dotProduct = newNorm.dot(this.getDirection().getNormalized());
		return Math.abs(1.0 - dotProduct) < EPSILON;
	}

	public boolean isParallel(Ray2D other) {
		return this.getDirection().cross(other.getDirection()) == 0;
	}


	public boolean equals(Ray2D other) {
		return this.getStart().equals(other.getStart()) &&
				this.getDirection().getNormalized().equals(other.getDirection().getNormalized());
	}

	public String toString() {
		if(this.ray == null)
			return "[null]";
		else return "[start: " + this.getStart().toString() + ", direction: " + this.getDirection().toString() + "]";
	}

}
