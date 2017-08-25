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

package tum.cms.sim.momentum.data.agent.pedestrian.state.operational;

import tum.cms.sim.momentum.utility.geometry.GeometryFactory;
import tum.cms.sim.momentum.utility.geometry.Vector2D;

public class StandingState {
	
	private Vector2D standingPosition = null;

	public Vector2D getStandingPosition() {
		return standingPosition;
	}

	private Vector2D standingHeading = null;

	public Vector2D getStandingHeading() {
		return standingHeading;
	}

	private Vector2D standingVelocity = null;
	
	public Vector2D getStandingVelocity() {
		return standingVelocity;
	}

	public StandingState(Vector2D standingPosition, Vector2D standingVelocity, Vector2D standingHeading) {
		
		this.standingPosition = standingPosition;
		this.standingHeading = standingHeading;
		this.standingVelocity = standingVelocity;
	}
	
	public StandingState(Vector2D standingPosition, Vector2D standingHeading) {
		
		this.standingPosition = standingPosition;
		this.standingHeading = standingHeading;
		this.standingVelocity = GeometryFactory.createVector(0.0, 0.0);
	}
	public StandingState(StandingState other) {
		
		this.standingPosition = other.getStandingPosition();
		this.standingHeading = other.getStandingHeading();
		this.standingVelocity = other.getStandingVelocity();
	}
}
