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

package tum.cms.sim.momentum.model.operational.walking.socialForceModel.ZengOperational;

import tum.cms.sim.momentum.utility.generic.PropertyBackPack;

public class ZengModelParameters {

	/**
	 * Computational precision
	 */
	static double computationalPrecision = 1E-9;

	/**
	 * Relaxation time [s] (necessary for acceleration).
	 */
	private double relaxationTime;

	/**
	 * Lambda_alpha, range: 0.0 - 1.0
	 * Strength of the influence of forces exerted from other pedestrians.
	 */
	private double conflictingPedestrianStrengthAngularEffect = 0.7;

	/**
	 * A^r_beta [m/s^2], range: 0.1-2.0
	 */
	private double conflictingPedestrianInteractionStrength = 1.28;

	/**
	 * B^r_beta [m^-1], range: 0-16
	 */
	private double conflictingPedestrianInteractionRangeRelativeDistance = 1.42;

	/**
	 * B^r_beta,alpha [s^-1], range: 0.1-0.8
	 */
	private double conflictingPedestrianInteractionRangeRelativeTime = 0.51;

	/**
	 * A_v [m/s^2], range: 0.1-1.6
	 */
	private double carRepulsiveInteractionStrength = 0.86;

	/**
	 * B_v [m^-1], range: 0.1-1.8
	 */
	private double carRepulsiveInteractionRange = 0.36;

	/**
	 * A^r_b [m/s^2], range: 0.1-0.6
	 */
	private double crosswalkRepulsiveInteractionStrength = 0.35;

	/**
	 * B^r_b [m^-1], range: 0.1-5.0
	 */
	private double crosswalkRepulsiveInteractionRange = 2.65;

	/**
	 * A^a_b [m/s^2], range: 0.1-0.45
	 */
	private double crosswalkAttractiveInteractionStrength = 0.25;

	/**
	 * B^a_b [m^-1], range: 0.1-0.7
	 */
	private double crosswalkAttractiveInteractionRange = 0.46;


	double getComputationalPrecision() {
		return computationalPrecision;
	}

	double getRelaxationTime() {
		return relaxationTime;
	}

	double getConflictingPedestrianStrengthAngularEffect() {
		return conflictingPedestrianStrengthAngularEffect;
	}

	double getConflictingPedestrianInteractionStrength() {
		return conflictingPedestrianInteractionStrength;
	}

	double getConflictingPedestrianInteractionRangeRelativeDistance() {
		return conflictingPedestrianInteractionRangeRelativeDistance;
	}

	double getConflictingPedestrianInteractionRangeRelativeTime() {
		return conflictingPedestrianInteractionRangeRelativeTime;
	}

	double getInteractionStrengthRepulsiveForceFromConflictingVehicle() {
		return carRepulsiveInteractionStrength;
	}

	double getCarRepulsiveInteractionRange() {
		return carRepulsiveInteractionRange;
	}

	double getCrosswalkRepulsiveInteractionStrength() {
		return crosswalkRepulsiveInteractionStrength;
	}

	double getCrosswalkRepulsiveInteractionRange() {
		return crosswalkRepulsiveInteractionRange;
	}

	double getCrosswalkAttractiveInteractionStrength() {
		return crosswalkAttractiveInteractionStrength;
	}

	double getCrosswalkAttractiveInteractionRange() {
		return crosswalkAttractiveInteractionRange;
	}


	ZengModelParameters(PropertyBackPack properties) {
		relaxationTime = properties.getDoubleProperty("relaxation_time");
		conflictingPedestrianStrengthAngularEffect = properties.getDoubleProperty("conflped_strength_angular_effect");
		conflictingPedestrianInteractionStrength = properties.getDoubleProperty("conflped_interaction_strength");
		conflictingPedestrianInteractionRangeRelativeDistance = properties.getDoubleProperty("conflped_interaction_range_relative_distance");
		conflictingPedestrianInteractionRangeRelativeTime = properties.getDoubleProperty("conflped_interaction_range_relative_time");
		carRepulsiveInteractionStrength = properties.getDoubleProperty("car_repulsive_interaction_strength");
		carRepulsiveInteractionRange = properties.getDoubleProperty("car_repulsive_interaction_range");
		crosswalkRepulsiveInteractionStrength = properties.getDoubleProperty("crosswalk_repulsive_interaction_strength");
		crosswalkRepulsiveInteractionRange = properties.getDoubleProperty("crosswalk_repulsive_interaction_range");
		crosswalkAttractiveInteractionStrength = properties.getDoubleProperty("crosswalk_attractive_interaction_strength");
		crosswalkAttractiveInteractionRange = properties.getDoubleProperty("crosswalk_attractive_interaction_range");
	}
}
