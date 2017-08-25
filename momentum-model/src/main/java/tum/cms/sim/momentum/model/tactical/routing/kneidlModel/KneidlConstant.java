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

package tum.cms.sim.momentum.model.tactical.routing.kneidlModel;

import org.apache.commons.math3.util.FastMath;

public class KneidlConstant {

	public enum KneidlNavigationType  {
		FastestEuklid,
		BeelineHeuristic,
		GreedyBeelineHeuristic,
		StraightAndLongLegs,
		HumanAntColony,
	}

	/*
	 * Generic Constants
	 */
	public static String NumberOfPedestriansOnEdge = "A";
	
	/*
	 * Ant Constants
	 */
	public static double PheromoneStartWeight = 1.0;
	public static double PheromoneUpdate = 0.2222;
	public static double PheromoneDecay = 0.00005;
	public static double PheromoneDecayTime = 6.0 ;// Each x in seconds a complete pheromoneDecay 
	public static String AntOptColonyEdgePheromoneWeightNameSeed = "B";
	
	/*
	 * Beeline Constants
	 */
    public static double BeelineAlpha = 1.5;
    public static double BeelineRandomError = 0.0; // if not 0.0 then set percent e.g 20% error -> 90% - 110% distance estimation
    public static String BeelineVertexWeightNameSeed ="C";
    		
	/*
	 * SALL Constants
	 */
    public static double SallLegAngleThreshold = 20.0 * ((2.0 * FastMath.PI) / 360.0); // is a straightLeg if this is not exceeded
	public static double SallAngleRatio = 0.75;

	
	/*
	 * Fastest Path Constants
	 */
	//public static double FastestMeanSpeed = 1.34; Weidmann
	public static double FastestMeanSpeed = 1.34; // Pre-BTTW
	public static String FastestEdgeMeanSpeedWeightName = "D"; // for each edge
	public static String FastestVertexWeightNameSeed = "E"; // plus pedestrian id
	
	/*
	 * Kielar  Constants
	 */
	public static String LandmarkEdgeWeightName = "F"; // for each vertex and landmark
	
	public static String AntGbSallEdgePheromoneWeightName = "G";
	
//	public static double UnifiedNotFamiliar = 0.95;
//	public static double UnifiedFamiliar = 0.05;
//	public static double UnifiedFamiliarNetwork = 0.5;
//	public static double UnifiedFamiliarTrained = 0.5;
//
//	public static double AntGbSallWeightBeeline = 1.0; 
//	public static double AntGbSallWeightSall = 0.0; 
}

