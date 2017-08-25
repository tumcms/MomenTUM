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


import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Transform;

import tum.cms.sim.momentum.utility.geometry.operation.IShape2D;
import tum.cms.sim.momentum.utility.geometry.operation.ITransformable2D;

/**
 * The Geometry2D is a superior class of the geometric figures Segment2D,
 * Polygon2D and Cycle2D and defines the methods all of those figures have to
 * contain.
 * 
 * @author berndtornede, pk
 * 
 */
public abstract class Geometry2D implements IShape2D, ITransformable2D {

	public abstract boolean contains(Vector2D point);

	public abstract boolean contains(Vector2D point, Transform transform);

	public abstract Mass createMass(double density);
	
	public abstract double getRadius();

	public abstract double getRadius(Vector2D vector);
	
	public abstract Vector2D getCenter();
	
//	public abstract Interval project(Vector2D vector);
//
//	public abstract Interval project(Vector2D vector, Transform transform);

	
	public abstract void rotate(double theta);

	public abstract void rotate(double theta, double x, double y);

	public abstract void rotate(double theta, Vector2D vector);

	public abstract void translate(double x, double y);

	public abstract void translate(Vector2D vector);

	abstract AABB createAxisAlignedBoundingBox();
}
