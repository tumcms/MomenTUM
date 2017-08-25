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

/**
 * All class and method descriptions have been adopted from the dyn4j library documentation 
 * http://docs.dyn4j.org/v3.1.10/.
 * 
 * Represents a AxisAlignedBoundingBox (AABB of a shape.
 * 
 * A AxisAlignedBoundingBox2D's radius must be larger than zero.
 * 
 * @author  pk
 *
 */
public class AxisAlignedBoundingBox2D {

	private AABB boundingBox = null;
	private Geometry2D underlyingGeometry = null;
	
	AxisAlignedBoundingBox2D(Geometry2D geometry) {
		
		boundingBox = geometry.createAxisAlignedBoundingBox();
		this.underlyingGeometry = geometry; 
	}
	
	public Geometry2D getGeometry() {
		return underlyingGeometry;
	}
	
	public Vector2D getMinPoint() {
		
		return GeometryFactory.createVector(boundingBox.getMinX(), boundingBox.getMinY());
	}
	
	public double getMaxX() {
		return boundingBox.getMaxX();
	}
	
	public double getMinX() {
		return boundingBox.getMinX();
	}
	
	public double getMaxY() {
		return boundingBox.getMaxY();
	}
	
	public double getMinY() {
		return boundingBox.getMinY();
	}
	
	public double getWidth() {
		return boundingBox.getWidth();
	}
	
	public double getHeight() {
		return boundingBox.getHeight();
	}
}
