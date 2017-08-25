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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import tum.cms.sim.momentum.model.PedestrianBehaviorModel;
import tum.cms.sim.momentum.utility.probability.ProbabilitySet;

public class UnifiedTypeExtractor {
	
	private static final int Shortest = 0;
	private static final int Beeline = 1;
	private static final int GreedyBeeline = 2;
	private static final int Sall = 3;

	public enum GenerationType {
		
		Stock,
		Probability,
		Random
	}
	
	public enum RunType {
		
		Calibration,
		Result
	}
	
	private HashSet<String> calibrationSet = null;
	
	private int calibrationDiscretisation = 10;
	private double calibrationFactor = 10;
	
	private int calibrationFastest = 0;
	private int calibrationBeeline = 0;
	private int calibrationGreedyBeeline = 0;
	private int calibrationSall = 0;
	private int calibrationBasics = 4;
	
	private double fastestPath = 0.0;
	private double leaderPath = 0.0;
	
	private double baseAvoidance = 0.0;
	
	public double getBaseAvoidance() {
		return baseAvoidance;
	}

	public void setBaseAvoidance(double baseAvoidance) {
		this.baseAvoidance = baseAvoidance;
	}

	private GenerationType generationType = null;
	private RunType runType = null;
	
	private int currentTypeNumberIter = -1;
	private ProbabilitySet<Integer> probabilitySet = null;
	private HashMap<Integer, ArrayList<Double>> typesResult = null;
	
	public void setResultMode(HashMap<Integer, ArrayList<Double>> resultMode) {
		
		this.runType = RunType.Result;
		this.typesResult = resultMode;
	}
	
	public void setCalibrationMode(Integer calibrationDiscretisation) {
		
		this.runType = RunType.Calibration;
		this.calibrationDiscretisation = calibrationDiscretisation;
		this.calibrationFactor = 1.0 / calibrationDiscretisation;
		this.calibrationSet = new HashSet<String>();
	}

	public void setProbabilityMode(ArrayList<Double> probabilityMode) {
		
		this.generationType = GenerationType.Probability;
		this.probabilitySet = new ProbabilitySet<Integer>();
		
		this.typesResult.forEach((key, value) -> {

			this.probabilitySet.append(key, probabilityMode.get(key));
		});
	}
	
	public void setRandomMode() {
		
		this.generationType = GenerationType.Random;
		double equal = 1.0 / ((double)this.typesResult.size());
		this.probabilitySet = new ProbabilitySet<Integer>();
		
		this.typesResult.forEach((key, value) -> {

			this.probabilitySet.append(key, equal);
		});
	}
	
	public void setStockMode() {
		
		this.generationType = GenerationType.Stock;
		this.currentTypeNumberIter = -1;
	}

	public void setHerding(Double leaderPathProportion, Double fastestPathProportion) {
		
		this.leaderPath = leaderPathProportion;
		this.fastestPath = fastestPathProportion;
	}
	
//	public double getRightHandSideProportion() {
//		
//		return 0.5;
//	}
//	

	
	public void selectSpatialType() {
		
		switch(this.runType) {
		
		case Calibration:
			
			switch(this.generationType) {
			
			case Probability: // not valid set to random
			case Stock:	 //  todo similar to loop
			case Random: //
				
				this.computeCalibrationValueRandom();
				break;
			}
			
			break;
			
		case Result:
			
			switch(this.generationType) {

			case Random:
			case Probability: // same
				
				currentTypeNumberIter = this.probabilitySet.getItemEquallyDistributed();
				break;
				
			case Stock:
				
				if(currentTypeNumberIter == this.typesResult.size() - 1) {
					
					currentTypeNumberIter = 0; 
				}
				else {
					
					currentTypeNumberIter++;
				}
				
				break;
			}
			
			break;
		}
	}
		
	public double getGreedyBeelineHeuristicsProportion() {
		
		double result = 0.0;
		
		switch (runType) {
		
		case Calibration:
			
			result = this.calibrationGreedyBeeline * this.calibrationFactor;
			break;
			
		case Result:	
			
			result = this.typesResult.get(currentTypeNumberIter).get(GreedyBeeline);
			break;	
		}
		
		return result;
	}

	public double getStraightAndLongLegsProportion() {

		double result = 0.0;
		
		switch (runType) {
		
		case Calibration:
			
			result = this.calibrationSall * this.calibrationFactor;
			break;
		case Result:	
			
			result = this.typesResult.get(currentTypeNumberIter).get(Sall);
			break;
		}
		
		return result;
	}

	public double getShortestPathProportion() {
	
		double result = 0.0;
	
		switch (runType) {
		
		case Calibration:
			
			result = this.calibrationFastest * this.calibrationFactor;
			break;
			
		case Result:	
			
			result = this.typesResult.get(currentTypeNumberIter).get(Shortest);
			break;
		}
		
		return result;
	}

	public double getBeelineHeuristicsProportion() {
	
		double result = 0.0;
		
		switch (runType) {
		
		case Calibration:
			
			result = this.calibrationBeeline * this.calibrationFactor;
			break;
		case Result:
			
			result = this.typesResult.get(currentTypeNumberIter).get(Beeline);
			break;
		}
		
		return result;
	}
	
	public double getFastestPathProportion() {
		
		return fastestPath;
	}
	
	public double getLeaderPathProportion() {

		return leaderPath;
	}
	
	private void computeCalibrationValueRandom() {

		String calibrationData = null;
		
		while(calibrationData == null || this.calibrationSet.contains(calibrationData)) {
			
			if(this.calibrationBasics == 4) {
				
				this.calibrationFastest = this.calibrationDiscretisation;
				this.calibrationBeeline = 0;
				this.calibrationGreedyBeeline = 0;	
				this.calibrationSall = 0;
			}
			else if(this.calibrationBasics == 3) {
				
				this.calibrationFastest = 0;	
				this.calibrationBeeline = this.calibrationDiscretisation;
				this.calibrationGreedyBeeline = 0;	
				this.calibrationSall = 0;
			}
			else if(this.calibrationBasics == 2) {
				
				this.calibrationFastest = 0;	
				this.calibrationBeeline = 0;
				this.calibrationGreedyBeeline = this.calibrationDiscretisation;
				this.calibrationSall = 0;
			}
			else if(this.calibrationBasics == 1) {
				
				this.calibrationFastest = 0; 
				this.calibrationBeeline = 0;
				this.calibrationGreedyBeeline = 0;	
				this.calibrationSall = this.calibrationDiscretisation;
			}
			else {
				
				this.calibrationFastest = PedestrianBehaviorModel.getRandom().nextInt(this.calibrationDiscretisation + 1);	
				this.calibrationBeeline = PedestrianBehaviorModel.getRandom().nextInt(this.calibrationDiscretisation + 1);	
				this.calibrationGreedyBeeline = PedestrianBehaviorModel.getRandom().nextInt(this.calibrationDiscretisation + 1);	
				this.calibrationSall = PedestrianBehaviorModel.getRandom().nextInt(this.calibrationDiscretisation + 1);	
			}
		
			this.calibrationBasics--;
			
			if(this.calibrationFastest + this.calibrationBeeline + this.calibrationGreedyBeeline + this.calibrationSall == 0) {
				continue;
			}

			calibrationData = String.valueOf(this.calibrationFastest) + 
				String.valueOf(this.calibrationBeeline) + 
				String.valueOf(this.calibrationGreedyBeeline) + 
				String.valueOf(this.calibrationSall);
			
			calibrationData = 
					(this.calibrationFastest < 10 ? "00" + String.valueOf(this.calibrationFastest) :
						(this.calibrationFastest < 100 ? "0" + String.valueOf(this.calibrationFastest) : 
							String.valueOf(this.calibrationFastest))) +
					(this.calibrationBeeline < 10 ? "00" + String.valueOf(this.calibrationBeeline) :
						(this.calibrationBeeline < 100 ? "0" + String.valueOf(this.calibrationBeeline) : 
							String.valueOf(this.calibrationBeeline))) + 
					(this.calibrationGreedyBeeline < 10 ? "00" + String.valueOf(this.calibrationGreedyBeeline) :
						(this.calibrationGreedyBeeline < 100 ? "0" + String.valueOf(this.calibrationGreedyBeeline) : 
							String.valueOf(this.calibrationGreedyBeeline))) +
					(this.calibrationSall < 10 ? "00" + String.valueOf(this.calibrationSall) :
						(this.calibrationSall < 100 ? "0" + String.valueOf(this.calibrationSall) : 
							String.valueOf(this.calibrationSall)));
		}
		
		this.calibrationSet.add(calibrationData);
	}
	
	
/* Testing Artifical Word Types */
//Double[][] types = new Double[][]
//	{{0.08,0.91,0.09,0.9},{0.17,0.63,0.2,0.44},{0.48,0.66,0.09,0.47},
//{0.16,0.74,0.19,0.56},{0.19,0.92,0.33,0.72},{0.22,0.67,0.21,0.82},
//{0.29,0.81,0.04,0.5},{0.22,0.78,0.35,0.79},{0.09,0.41,0.11,0.24},{0.1,0.52,0.06,0.99},{0.69,0.7,0.0,0.04},
//{0.22,0.93,0.06,0.65},{0.31,0.59,0.06,0.34},{0.18,0.54,0.21,0.44},{0.43,0.71,0.09,0.7},{0.3,0.95,0.27,0.76},{0.52,0.72,0.09,0.33},{0.15,0.61,0.16,0.77},{0.38,0.59,0.3,0.76},{0.43,0.8,0.01,0.7},{0.12,0.72,0.19,0.63},{0.68,0.88,0.11,0.8},{0.22,0.97,0.19,0.44},{0.08,0.86,0.03,0.39},{0.35,0.57,0.1,0.42},{0.07,0.55,0.08,0.2},{0.25,0.86,0.25,0.53},{0.27,0.88,0.04,0.39},{0.32,0.95,0.3,0.83},{0.32,0.64,0.13,0.87},{0.16,0.94,0.05,0.85},
//{0.11,0.27,0.09,0.22},{0.49,0.92,0.06,0.3},{0.28,0.67,0.06,0.69},{0.29,0.62,0.01,0.79},{0.28,0.92,0.09,0.97},{0.49,0.71,0.12,0.86},{0.39,0.74,0.11,0.47},{0.44,0.77,0.2,0.99},{0.37,0.66,0.06,0.23},{0.12,0.91,0.03,0.97},{0.23,0.98,0.04,0.47},{0.4,0.63,0.01,0.96},{0.25,0.92,0.2,0.47},{0.42,0.71,0.06,0.81},{0.43,0.74,0.28,0.76},{0.34,0.89,0.16,0.66},{0.21,0.53,0.04,0.96},{0.83,0.89,0.01,0.1},{0.39,0.68,0.24,0.81},{0.31,0.66,0.14,0.6},{0.17,0.86,0.1,0.58},{0.31,0.63,0.21,0.72},{0.35,0.78,0.39,0.96},{0.23,0.97,0.1,0.57},{0.29,0.78,0.23,0.57}};
	
//		{{0.0, 0.0, 1.0, 0.0},
//		{0.01, 0.0, 0.71, 0.64},
//		{0.05, 0.03, 0.89, 0.34},
//		{0.01, 0.04, 0.26, 0.89},
//		{0.04, 0.07, 0.39, 0.76},
//		{0.1, 0.79, 0.2, 0.08},
//		{0.5, 0.5, 0.33, 0.44},
//		{0.64, 0.66, 0.74, 0.17},
//		{0.0, 1.0, 0.0, 0.0},
//		{0.61, 0.9, 0.51, 0.22}, //10
//		
//		{0.94, 0.96, 0.6, 0.56},
//		{0.84, 0.9, 0.5, 0.89},
//		{0.19, 0.21, 0.04, 0.75},
//		{0.13, 0.12, 0.82, 0.0},
//		{0.11, 0.11, 0.67, 0.57},
//		{0.68, 0.68, 0.84, 0.56},
//		{0.17, 0.06, 0.96, 0.48},
//		{0.38, 0.35, 0.69, 0.06},
//		{0.99, 0.95, 0.78, 0.98},
//		{0.81, 0.8, 0.44, 0.29}, // 20
//		{0.96, 0.94, 0.98, 0.38},
//		{0.86, 0.82, 0.9, 0.19},
//		{0.96, 0.95, 0.45, 0.78},
//		{0.0, 0.0, 0.0, 1.0},
//		{1.0, 0.0, 0.0, 0.0},
//		{0.86, 0.68, 0.08, 0.59},
//		{0.3, 0.28, 0.18, 0.99},
//		{0.82, 0.37, 0.38, 0.3}}; // 28

		
				// Mannheim Stadtraum
//				{{0.05,0.05,0.82,0.77}, // LLL
//				{0.0,1.0,0.0,0.0}, // LLR
//				{0.0,0.0,0.0,1.0}, // LRL
//				{0.0,0.0,1.0,0.0}, // RLL 
//				{0.13,0.14,0.8,0.9}, // RLR 
//				{1.0,0.0,0.0,0.0}}; //RRR
				
				// BTTW
//				{
//				//{0.98,0.68,0.99,0.96}, // G
//				{0.13,0.09,0.29,0.98}, // A
//				{0.08,0.10,0.96,0.86}, // B
//				{0.00,0.00,1.00,0.00}, // C 
//				{0.25,0.00,0.88,0.77}, // D
//				{1.00,0.94,0.93,0.84}, // E
//				{0.02,0.02,0.56,0.15}, // F
//				};

// Double[] randomTypes = new Double[] {
//		 // BTTW
//		 0.12, // A
//		 0.24, // B
//		 0.4055, // C
//		 0.5255, // D
//		 0.8345, // E
//		 1.0}; // F


 	//Mannheim Stadtraum
//		 0.166666667, //LLL
//		 0.333333333, //LLR
//		 0.5, // LRL
//		 0.666666667, // RRL
//		 0.833333333, // RLR
//		 1.0};  // RRR


}
