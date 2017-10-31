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

import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrianExtansion;
import tum.cms.sim.momentum.model.tactical.routing.unifiedRoutingModel.UnifiedRoutingConstant.DecisionDuration;

public class UnifiedRoutingPedestrianExtension implements IPedestrianExtansion {

	private double greedyBeelineWeightProportion = 0.0;
	private double shortestWeightProportion = 0.0;
	private double beelineWeightProportion = 0.0;
	private double sallWeightProportion = 0.0;
	private double sallAngle = 0.0;
	
	private double fastestWeightProportion = 0.0;
	private double leaderWeightProportion = 0.0;
	
	private Double herdingProportion = 0.0;
	
	private DecisionDuration decisionDuration = DecisionDuration.None;
	
	public DecisionDuration getDecisionDuration() {
		return decisionDuration;
	}

	private double rightHandWeightProportion = 0.0;
	
	public double getRightHandWeightProportion() {
		return rightHandWeightProportion;
	}
	
	private double avoidanceWeightPropoertion = 0.0;

	public double getAvoidanceWeightProportion() {
		return avoidanceWeightPropoertion;
	}

	public double getGreedyBeelineWeightProportion() {
		return greedyBeelineWeightProportion;
	}

	public double getSallWeightProportion() {
		return sallWeightProportion;
	}

	public double getShortestWeightProportion() {
		return shortestWeightProportion;
	}

	public double getBeelineWeightProportion() {
		return beelineWeightProportion;
	}

	
	public double getLeaderWeightProportion() {
		return leaderWeightProportion;
	}
	
	public double getFastestWeightProportion() {
		return fastestWeightProportion;
	}	
	
	
	public Double getHerdingProportion() {
		return herdingProportion;
	}

	public Double getSallAngle() {
		
		return sallAngle;
	}
	
	public UnifiedRoutingPedestrianExtension(UnifiedTypeExtractor typeExtractor,
			Boolean herding,
			Boolean rightHandSide,
			DecisionDuration decisionDuration,
			double angleSall) {

		typeExtractor.selectSpatialType();
		
		this.decisionDuration = decisionDuration;
		
		this.greedyBeelineWeightProportion = typeExtractor.getGreedyBeelineHeuristicsProportion();
		this.sallWeightProportion = typeExtractor.getStraightAndLongLegsProportion();
		this.shortestWeightProportion = typeExtractor.getShortestPathProportion();
		this.beelineWeightProportion = typeExtractor.getBeelineHeuristicsProportion();
		this.sallAngle = angleSall;
//		if(rightHandSide) {
//			
//			this.rightHandWeightProportion = typeExtractor.getRightHandSideProportion();
//		}
//
		if(typeExtractor.getBaseAvoidance() > 0.0) {
			
			this.avoidanceWeightPropoertion = typeExtractor.getBaseAvoidance();
		}
		
		if(herding) {
			
			this.fastestWeightProportion = typeExtractor.getFastestPathProportion();
			this.leaderWeightProportion = typeExtractor.getLeaderPathProportion();
			
			double lowKnowledge = this.greedyBeelineWeightProportion + this.sallWeightProportion;
			
			double highKnowledge = UnifiedRoutingConstant.SpatialBoundary + 
					this.shortestWeightProportion + 
					this.beelineWeightProportion;
			
			this.herdingProportion  = lowKnowledge / (lowKnowledge + highKnowledge);
		}
		else {
			
			this.herdingProportion = 0.0;
		}
	}
}
