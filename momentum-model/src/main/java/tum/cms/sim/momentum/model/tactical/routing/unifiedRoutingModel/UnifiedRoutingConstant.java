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

package tum.cms.sim.momentum.model.tactical.routing.unifiedRoutingModel;

import org.apache.commons.math3.util.FastMath;

public class UnifiedRoutingConstant {

	/*
	 * Defines the weight which works versus the herding behavior
	 * Good values are 1.5 - 2.5
	 */
	public static Double SpatialBoundary = 0.0;
	
	/*
	 * Beeline Heuristics Constants
	 */
    public static double BeelineAlpha = 1.5;
    public static String BeelineVertexWeightNameSeed ="kC";
    		
	/*
	 * SALL Constants
	 */
    public static double SallLegAngleThreshold = 20.0 * ((2.0 * FastMath.PI) / 360.0); //20.0
    // 15 degree for huge simulation scenarios with no sharp edges
    // 20 degree for small simulation scenarios with sharp edges
    // 30 for the searching algorithm
    // is a straightLeg if this is not exceeded
    public static double SallCalculationAngleInfluence = 0.6;//0.5 0.75;
	
    /*
     * Shortest Path Constants
     */
    public static String ShortestVertexWeightNameSeed = "sP";
    		
    /*
	 * Fastest Path Constants
	 */
	public static double FastestMeanSpeed = 1.34; //1.644901429 Pre-BTTW, 1.34 Weidmann
	public static String FastestEdgeMeanSpeedWeightName = "kD"; // for each edge
	public static String FastestVertexWeightNameSeed = "kE"; // plus pedestrian id

	/*
	 * Leader path Constants
	 */
	// Kielar extension getHerdingProportion() by Configuration
	public static String NumberOfPedestriansOnEdge = "kA";
	public static Double LeaderBoundary = Double.MAX_VALUE;
	public static Double LostPedsPerSecond = Double.MAX_VALUE;
	
	/*
	 * Avoidance behavior
	 */
	public static String AvoidancePowerOnEdge = "aP";
	
	/*
	 * Decision Duration
	 */
	public enum DecisionDuration {
		
		City,
		Open,
		None
	}
	
	public static Double CityTimeRoute = 0.1;
	
	public static Double OpenTimeRoute = 0.05;
}

