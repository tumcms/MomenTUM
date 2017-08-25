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

package tum.cms.sim.momentum.model.meta.transitum.data;

import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;
import tum.cms.sim.momentum.infrastructure.exception.InconsistentModelData;
import tum.cms.sim.momentum.utility.geometry.Vector2D;

public class TransitionArea {
		
	public enum TransitionAreaType {
	
			MicroscopicMesoscopic
	}
	
	private TransitionAreaType transitionAreaType = null;
	private Double innerRadiusOfArea = null;
	private Double outerRadiusOfArea = null;
	private Vector2D centerOfArea = null;

	
	public TransitionAreaType getTransitionAreaType() {
		return transitionAreaType;
	}

	public void setTransitionAreaType(TransitionAreaType transitionAreaType) {
		this.transitionAreaType = transitionAreaType;
	}

	public Double getInnerRadiusOfArea() {
		return innerRadiusOfArea;
	}

	public void setRadiiOfArea(Double innerRadiusOfArea, Double outerRadiusOfArea) {
		
		if(innerRadiusOfArea >= outerRadiusOfArea) {
			
			String inconsistentDataConfiguration = InconsistentModelData.inconsistentModelDataDescriptoin("innerRadiusOfArea", ">=", "outerRadiusOfArea");
			try {
				throw new InconsistentModelData(inconsistentDataConfiguration);
				
			} catch (InconsistentModelData e) {
				e.printStackTrace();
			}		
		}		
		this.outerRadiusOfArea = outerRadiusOfArea;
		this.innerRadiusOfArea = innerRadiusOfArea;
	}

	public Double getOuterRadiusOfArea() {
		return outerRadiusOfArea;
	}

	
	public Vector2D getCenterOfArea() {
		return centerOfArea;
	}


	public void setCenterOfArea(Vector2D centerOfArea) {
		this.centerOfArea = centerOfArea;
	}
	
	public TransitionArea(TransitionAreaType transitionAreaType, Double innerRadiusOfArea, Double outerRadiusOfArea, Vector2D centerOfArea) {
		
		this.transitionAreaType = transitionAreaType;
		this.setRadiiOfArea(innerRadiusOfArea, outerRadiusOfArea);
		this.centerOfArea = centerOfArea;
	}
	
	public boolean containsPosition(Vector2D position) {
		
		Double distance = position.distance(centerOfArea);
		
		return (distance >= innerRadiusOfArea && distance <= outerRadiusOfArea);
	}

	public boolean containsPedestrian(IRichPedestrian pedestrian) {
		
		Vector2D position = pedestrian.getPosition();
		Double bodyRadius = pedestrian.getBodyRadius();
		Double distance = position.distance(centerOfArea);
		
		return (distance  >= innerRadiusOfArea - bodyRadius && distance <= outerRadiusOfArea + bodyRadius);
	}
}
