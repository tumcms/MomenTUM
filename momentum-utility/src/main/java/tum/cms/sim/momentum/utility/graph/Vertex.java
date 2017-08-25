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

package tum.cms.sim.momentum.utility.graph;

import tum.cms.sim.momentum.utility.geometry.Geometry2D;
import tum.cms.sim.momentum.utility.geometry.Vector2D;
import tum.cms.sim.momentum.utility.geometry.operation.GeometryAdditionals;

public class Vertex extends Weighted {

	@Override
	public boolean equals(Object other) {
		
		if(other instanceof Vertex) {
			
			Vertex otherVertex = (Vertex)other;
			
			if(this.getId().equals(otherVertex.getId()) || 
			   this.getGeometry().getCenter().equals(otherVertex.getGeometry().getCenter())) {
				
				return true;
			}
		}
	
		return false;
	}
	
    private Geometry2D geometry = null;

	public Geometry2D getGeometry() {
		return geometry;
	}

	public void setGeometry(Geometry2D geometry) {
		this.geometry = geometry;
	}

	Vertex(Geometry2D geometry) {
		
		this.geometry = geometry;
	}

	Vertex(Geometry2D geometry, boolean isSeed) {
		
		this.geometry = geometry;
		this.isSeed = isSeed;
	}
	public boolean isTemporary() {
		
		return this.getId() == -1;
	}
	
	private boolean isSeed = false;
	
	public boolean isSeed() {
		return isSeed;
	}
	
	public double euklidDistanceBetweenVertex(Vertex other) {
			 
		 Vector2D currentCenter = this.getGeometry().getCenter();
		 Vector2D targetCenter = other.getGeometry().getCenter();
		 
		 return currentCenter.distance(targetCenter);
	}
	
	public double euklidDistanceBetweenVertex(Vector2D other) {
		 
		 Vector2D currentCenter = this.getGeometry().getCenter();

		 return currentCenter.distance(other);
	}
	
	/**
	 * Calculates the angle (left - center - right) at the center vertex from 0 to PI radiant
	 * The result is always a positive.
	 * @param left
	 * @param right
	 * @return
	 */
	public double angleBetweenVertex(Vertex left, Vertex right) {

		return GeometryAdditionals.angleBetween0And180(
				left.getGeometry().getCenter(),
				this.getGeometry().getCenter(), 
				right.getGeometry().getCenter());
	}
	
	/**
	 * Calculates the angle (left - center - right) at the center vertex from -PI to PI radiant
	 * @param left
	 * @param right
	 * @return
	 */
	public double angleBetweenVertexPlusMinus(Vertex left, Vertex right) {

		return GeometryAdditionals.angleBetweenPlusMinus180(
				left.getGeometry().getCenter(),
				this.getGeometry().getCenter(), 
				right.getGeometry().getCenter());
	}
}
