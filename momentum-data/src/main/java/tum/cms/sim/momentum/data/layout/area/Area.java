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

package tum.cms.sim.momentum.data.layout.area;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

import tum.cms.sim.momentum.data.layout.LayoutObject;
import tum.cms.sim.momentum.utility.geometry.Polygon2D;
import tum.cms.sim.momentum.utility.geometry.Segment2D;
import tum.cms.sim.momentum.utility.geometry.Vector2D;

public class Area extends LayoutObject {

	protected HashSet<String> categories = new HashSet<>();
	
	public Stream<String> getCategories() {
		
		if(categories == null) {
			
			return null;
		}
		
		return categories.stream();
	}

	public boolean isInCategories(Collection<String> otherCategories) {
		
		boolean containsCategory = false;
		
		for(String otherCategory : otherCategories) {
			
			if(this.isOfCategory(otherCategory)) {
				
				containsCategory = true;
				break;
			}
		}
		
		return containsCategory;
	}
	public boolean isOfCategory(String category) {
		
		boolean containsCategory = false;
		
		if(categories != null) {
			
			containsCategory = categories.contains(category);
		}
		
		return containsCategory;
	}

	protected Vector2D pointOfInterest = null;
	
	protected Polygon2D geometry;

	public Polygon2D getGeometry() {
		return this.geometry;
	}
	
	private Segment2D gatheringSegment = null;
	
	public Segment2D getGatheringSegment() {
		return gatheringSegment;
	}

	public void setGatheringSegment(Segment2D gatheringSegment) {
		this.gatheringSegment = gatheringSegment;
	}

	public Area(int id, String name, Polygon2D polygon, List<String> categories) {

		super(id, name);

		this.categories.addAll(categories);
		this.geometry = polygon;
	}
	
	public Vector2D getPointOfInterest() {
		
		if(pointOfInterest == null) {
			
			return this.geometry.getCenter();
		}
		else {
			
			return this.pointOfInterest;
		}
	}
	
	public void updatePointOfInterest(Vector2D pointOfInterst) {
		
		 this.pointOfInterest = pointOfInterst;
	}
}
