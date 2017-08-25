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

package tum.cms.sim.momentum.model.operational.walking.groupBehaviour_Moussaid2010;

import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrianExtansion;
import tum.cms.sim.momentum.utility.geometry.Vector2D;

public class MoussaidPedestrianExtension implements IPedestrianExtansion {

	// ATTRIBUTES
	// ----------------------------------------------------------------------

	private double socialInteractionStrength; // = 4;
	private double attractionEffectsStrength;
	private double repulsionStrength;
	private Vector2D gazingDirection;

	private double relaxationTime; // = 0.5;
	private double panicDegree; // e.g. = 0.5;
	private double A; // e.g. = 1.0;
	private double B;
	

	// CONSTRUCTORS
	// ----------------------------------------------------------------------

	/**
	 * Default constructor.
	 */
	public MoussaidPedestrianExtension() {
		
	}

	// METHODS
	// ----------------------------------------------------------------------
	
	public double getSocialInteractionStrength() {
		return this.socialInteractionStrength;
	}
	
	public void setSocialInteractionStrength(double strength) {
		this.socialInteractionStrength = strength;
	}
	
	public double getAttractionEffectsStrength() {
		return this.attractionEffectsStrength;
	}
	
	public void setAttractionEffectsStrength(double strength) {
		this.attractionEffectsStrength = strength;
	}
	
	public double getRepulsionStrength() {
		return this.repulsionStrength;
	}
	
	public void setRepulsionStrength(double strength) {
		this.repulsionStrength = strength;
	}
	
	public Vector2D getGazingDirection() {
		return this.gazingDirection;
	}
	
	public void setGazingDirection(Vector2D direction) {
		this.gazingDirection = direction;
	}
	
	
	
	public double getRelaxationTime() {
		return this.relaxationTime;
	}
	
	public void setRelaxationTime(double relaxationTime) {
		this.relaxationTime = relaxationTime;
	}
	
	public double getPanicDegree() {
		return this.panicDegree;
	}
	
	public void setPanicDegree(double panic) {
		this.panicDegree = panic;
	}
	
	
	public double[] getMassBehaviourConstants() {
		return new double[] {this.A, this.B};
	}
	
	public void setMassBehaviourConstants(double[] constants) {
		this.A = constants[0];
		this.B = constants[1];
	}
}
