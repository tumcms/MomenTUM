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

package tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel;

public class CognitiveConstant {
	
	// Cognitive Function Models
	
	public static String DistancePerceptionWeight = "CSC";
	
	public enum DistancePerceptionType {
		
		None,
		Real,
		Leaky
	}
	
	public enum OccupancyPerceptionType {
		
		None,
		Space
	}
	
	public enum RescheduleType {
		
		None,
		DecisionFieldTheory
	}
	
	public enum ScheduleType {
		
		Rank,
		NearestNeighbor,
		ConvexHull
	}
	
	public enum PreferenceType {
		
		None,
		Interest
	}
	
	public enum ValuationType {
		
		Weighted,
		IntraAttribute
	}
	
	// Simulation States
	
	public enum Availability {
		
		Impossible,
		Performable
	}
	
	public enum Actualization { 
		
		Unfinished,
		Ongoing,
		Achieved		
	}
	
	public enum OccupancyType { 
		
		None,
		Waiting,
		Engage
	}
	
	public enum Deciding {
		
		Scheduling,
		Rescheduling,
		Finished
	}
	

	public enum Familiarity {
		
		None,
		Low,
		Medium,
		High
	}
	
	// TODO create familiarity (accuracy) function
	public static Double fromFamilirty(Familiarity familiarity) {
		
		Double result = 0.0;
		
		switch(familiarity) {
		
		case High:
			result = 1.0;
			break;
			
		case Medium:
			result = 0.9;
			break;
			
		case Low:
			result = 0.8;
			break;
			
		case None:
		default:
			result = 0.5;
			break;
		
		}
		
		return result;
	}
	
	public class RescheduleThreshold {
		
		private RescheduleThreshold() { }
		
		public static final double EqualitySubstitution = 1.0; // self substitution in DFT
		
		public static final int AttributeNumber = 3; // Occupancy, Interest, Distance
	}
	
}
