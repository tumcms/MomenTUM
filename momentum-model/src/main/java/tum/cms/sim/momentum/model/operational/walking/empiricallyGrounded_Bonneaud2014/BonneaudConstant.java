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

package tum.cms.sim.momentum.model.operational.walking.empiricallyGrounded_Bonneaud2014;

public class BonneaudConstant {
	
	private BonneaudConstant() { }
	
	public static double STATIC_TARGET_DAMPING_COEF;
	public static double STATIC_TARGET_ATTRACT_COEF;
	public static double STATIC_TARGET_DISTANCE_COEF;
	public static double STATIC_TARGET_ATTRACT_ASSURANCE_COEF;
	
	public static double STATIC_OBSTACLE_REPULSION_COEF;
	public static double STATIC_OBSTACLE_REPULSION_DECAY_COEF;
	public static double STATIC_OBSTACLE_DISTANCE_COEF;

	public static double MOVING_OBSTACLE_REPULSION_COEF;
	public static double MOVING_OBSTACLE_HEADING_COEF;
	public static double MOVING_OBSTACLE_DISTANCE_COEF;
	
	//Model improvement: takes the distances between pedestrian and pedestrian to avoid into account
	//the higher this coefficient, the higher the influence of the distance's change in the repulsion reaction
	//(the other influence is the heading's change
	public static double MOVING_OBSTACLE_CHANGE_IN_DISTANCE_COEF;
	
	public static double TAU_SPEED_PREFERED_COEF;
	public static double TAU_SPEED_DEACCEL_COEF;
	public static double TAU_SPEED_BEARING_COEF;
	
	//not in model description: all obstacles are divided into parts where non of them is longer than MAX_OBSTACLE_SIZE
	public static double MAX_OBSTACLE_SIZE;
	
	// for further use, atm only static targets
	//	public double MOVING_TARGET_DAMPING_COEF;
	//	public double MOVING_TARGET_ATTRACT_COEF;
	//	public double MOVING_TARGET_DISTANCE_COEF;
	//	public double MOVING_TARGET_ATTRACT_ASSURANCE_COEF;
	
}
