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

package tum.cms.sim.momentum.utility.probability;

import java.util.ArrayList;

public class Histogram1DSet {
 
	private ArrayList<Double> set = new ArrayList<Double>();
	private double binWidth = 0.0;
	private double binWidthStart = 0.0;
	private double currentSumOfValues = 0.0;
	private ProbabilitySet<Integer> probabilitySet = null;
	
	public double getBinWidth() {
		return binWidth;
	}
	
	public double getBinContent(int bin) {
		
		return set.get(bin);
	}
	
	public double getBinValue(int bin) {
		
		return binWidthStart + binWidth * (bin - 1);
	}
	
	public double getMaxValue() {
		
		return this.getBinValue(set.size());
	}
	
	public int getNumberOfBins() {
		
		return set.size();
	}

	public Histogram1DSet(double binWidthStart, double binWidth) {
		
		this.binWidthStart = binWidthStart;
		this.binWidth = binWidth;
	}
	
	public double getMean() {
		
		this.initializeSet();
		
		double mean = (this.binWidthStart + binWidth) * this.probabilitySet.getSet().get(0).getRight();
		
		double binMulit = binWidth;
		
		for(int iter = 1; iter < this.probabilitySet.getSet().size(); iter++) {
		
			binMulit += binWidth;
			mean += binMulit * this.probabilitySet.getSet().get(iter).getRight();
		}

		return mean;
	}
	
	public void appendBinValue(double item) {
	
		probabilitySet = null; // reset probability set
		set.add(item);
		currentSumOfValues += item;
	}
	
	private void initializeSet() {
		
		if(probabilitySet == null) {
			
			probabilitySet = new ProbabilitySet<Integer>();
			
			for(int iter = 0; iter < set.size(); iter++) {
				
				probabilitySet.append(iter, set.get(iter) / currentSumOfValues);
			}
		}
	}
	
	public double getBinValueEquallyDistributed() {
		
		this.initializeSet();

		if(probabilitySet.getSet().size() == 0) {
			
			return binWidthStart + binWidth;
		}
		
		int indexOfDesired = probabilitySet.getItemEquallyDistributed();
		
		return binWidthStart + indexOfDesired * binWidth;
	}	
}
