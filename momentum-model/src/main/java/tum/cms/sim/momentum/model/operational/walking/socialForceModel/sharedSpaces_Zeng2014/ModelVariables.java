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

package tum.cms.sim.momentum.model.operational.walking.socialForceModel.sharedSpaces_Zeng2014;

import tum.cms.sim.momentum.utility.generic.PropertyBackPack;

public class ModelVariables {

	/**
	 * Computational precision
	 */
	public double computationalPrecision = 1E-9;

	/**
	 * Relaxation time [s] (necessary for acceleration).
	 */
	private double relaxationTime;
	
	/**
	 * Radius of the visual range [m]
	 */
	private double visualRangeRadius = 30.0;
	
	/**
	 * Angle of the visual range [deg]
	 */
	private double visualRangeAngle = 120;
	
	/**
	 * A^r_beta [m/s^2], range: 0.1-2.0
	 */
	private double interactionStrengthForRepulsiveForceFromSurroundingPedestrians = 1.28;
	
	/**
	 * B^r_beta [m^-1], range: 0-16
	 */
	private double interactionRangeForRelativeDistance = 1.42;
	
	/**
	 * B^r_beta,alpha [s^-1], range: 0.1-0.8
	 */
	private double interactionRangForRelativeConflictingTime = 0.51;
	
	/**
	 * A^a_beta [m/s^2], range: 0.1-0.28
	 */
	private double interactionStrengthForFootprintEffect = 0.23;
	
	/**
	 * B^a_beta [m^-1], range: 0.1-0.8
	 */
	private double interactionRangeForFootprintEffect = 0.55;
	
	/**
	 * A_v [m/s^2], range: 0.1-1.6
	 */
	private double interactionStrengthRepulsiveForceFromConflictingVehicle = 0.86;
	
	/**
	 * B_v [m^-1], range: 0.1-1.8
	 */
	private double interactionRangeForRepulsiveForceFromConflictingVehicle = 0.36;
	
	/**
	 * A^r_b [m/s^2], range: 0.1-0.6
	 */
	private double interactionStrengthForRepulsiveForceFromCrosswalkBoundary = 0.35;
	
	/**
	 * B^r_b [m^-1], range: 0.1-5.0
	 */
	private double interactionRangeForRepulsiveForceFromCrosswalkBoundary = 2.65;
	
	/**
	 * A^a_b [m/s^2], range: 0.1-0.45
	 */
	private double interactionStrengthForAttractiveForceFromCrosswalkBoundary = 0.25;
	
	/**
	 * B^a_b [m^-1], range: 0.1-0.7
	 */
	private double interactionRangeForAttractiveForceFromCrosswalkBoundary = 0.46;
	
	
	public double getComputationalPrecision() {
		return computationalPrecision;
	}

	public double getRelaxationTime() {
		return relaxationTime;
	}

	public double getVisualRangeRadius() {
		return visualRangeRadius;
	}

	public double getVisualRangeAngle() {
		return visualRangeAngle;
	}

	public double getInteractionStrengthForRepulsiveForceFromSurroundingPedestrians() {
		return interactionStrengthForRepulsiveForceFromSurroundingPedestrians;
	}

	public double getInteractionRangeForRelativeDistance() {
		return interactionRangeForRelativeDistance;
	}

	public double getInteractionRangForRelativeConflictingTime() {
		return interactionRangForRelativeConflictingTime;
	}

	public double getInteractionStrengthForFootprintEffect() {
		return interactionStrengthForFootprintEffect;
	}

	public double getInteractionRangeForFootprintEffect() {
		return interactionRangeForFootprintEffect;
	}

	public double getInteractionStrengthRepulsiveForceFromConflictingVehicle() {
		return interactionStrengthRepulsiveForceFromConflictingVehicle;
	}

	public double getInteractionRangeForRepulsiveForceFromConflictingVehicle() {
		return interactionRangeForRepulsiveForceFromConflictingVehicle;
	}

	public double getInteractionStrengthForRepulsiveForceFromCrosswalkBoundary() {
		return interactionStrengthForRepulsiveForceFromCrosswalkBoundary;
	}

	public double getInteractionRangeForRepulsiveForceFromCrosswalkBoundary() {
		return interactionRangeForRepulsiveForceFromCrosswalkBoundary;
	}

	public double getInteractionStrengthForAttractiveForceFromCrosswalkBoundary() {
		return interactionStrengthForAttractiveForceFromCrosswalkBoundary;
	}

	public double getInteractionRangeForAttractiveForceFromCrosswalkBoundary() {
		return interactionRangeForAttractiveForceFromCrosswalkBoundary;
	}
	
	
	public ModelVariables(PropertyBackPack properties) {
		relaxationTime = properties.getDoubleProperty("relaxation_time");
		
		// TODO: add all variables
	}
	
}
