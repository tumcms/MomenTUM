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

package tum.cms.sim.momentum.model.operational.walking.macroscopicModels.velocityModel;

import tum.cms.sim.momentum.model.operational.walking.macroscopicModels.data.MacroscopicEdge.MacroscopicEdgeType;
import tum.cms.sim.momentum.utility.probability.distrubution.Normal;
import org.apache.commons.math3.util.FastMath;

public class VFFModel implements IVelocityModel  {
	private MacroscopicEdgeType type; //cars or pedestrians
	private double mean;
	private double standardDeviation;
	private double gamma;
	private double maximumDensity;
	private Normal freeFlowVelocity;
	private double maximumVelocity;
	private double minimumVelocity;
	private static int numberOfVelocityClasses;
	private double[] velocities;
	private double[] probabilities;
	
	protected VFFModel(MacroscopicEdgeType type_in) {
		
		type = type_in;
		
		switch(type) 
		{ 
		case WALKWAY:
			setMean(1.34);
			setStandardDeviation(0.26);
			setGamma(1.913);
			setMaximumDensity(5.4);
			break;
		case STREET:
			setMean(15);
			setStandardDeviation(2.91);
			setGamma(6.83);
			setMaximumDensity(0.12);
			break;
		default :
			
			break;
		}
		
		freeFlowVelocity = new Normal(getMean(), getStandardDeviation());
		maximumVelocity = getMean() + 4 * getStandardDeviation();
		minimumVelocity = getMean() - 4 * getStandardDeviation();
		
		numberOfVelocityClasses = 10;
		double dvff = (maximumVelocity-minimumVelocity)/numberOfVelocityClasses; 
		
		velocities = new double[numberOfVelocityClasses];
		probabilities = new double[numberOfVelocityClasses];
		
		for (int i=0;i<numberOfVelocityClasses;i++)
		{	
			velocities[i] = minimumVelocity + dvff*(i + 0.5); //average velocity
			probabilities[i] = freeFlowVelocity.getCumulativeProbability(minimumVelocity + dvff*(i+1)) - freeFlowVelocity.getCumulativeProbability(minimumVelocity + dvff*i);
		}	
	}
		
	public double getVhat(double density_in, MacroscopicEdgeType type) {
		
		double n = 1.81;
		
		if (density_in > getMaximumDensity())
			return 0;
		
		else
		{
			if (type == MacroscopicEdgeType.WALKWAY)
				return 1-FastMath.exp(-getGamma()*(1/density_in-1/getMaximumDensity()));
			else
				return (FastMath.pow(getMaximumDensity(), n) - FastMath.pow(density_in, n))/(FastMath.pow(getMaximumDensity(),n) + getGamma()*FastMath.pow(density_in, n));
		}	
	}
	


	private double getMean() {
		return mean;
	}

	private void setMean(double mean) {
		this.mean = mean;
	}

	private double getStandardDeviation() {
		return standardDeviation;
	}

	private void setStandardDeviation(double standardDeviation) {
		this.standardDeviation = standardDeviation;
	}

	private double getGamma() {
		return gamma;
	}

	private void setGamma(double gamma) {
		this.gamma = gamma;
	}

	private double getMaximumDensity() {
		return maximumDensity;
	}

	private void setMaximumDensity(double maximumDensity) {
		this.maximumDensity = maximumDensity;
	}
	
	public double[] getVelocities() {
		return velocities;
	}
	
	public double[] getProbabilities() {
		return probabilities;
	}
	
	public int getNumberOfClasses() {
		return numberOfVelocityClasses;
	}
}
