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

package tum.cms.sim.momentum.utility.graph;

import java.util.HashMap;

import tum.cms.sim.momentum.utility.generic.Unique;

public abstract class Weighted extends Unique {

	protected HashMap<String, Double> weightMap = new HashMap<String, Double>();
	
	public synchronized boolean hasWeight(String weightName) {
		
		return weightMap.containsKey(weightName);
	}
	
	public synchronized Double getWeight(String weightName) {
		
 		return weightMap.get(weightName);
	}
	
	public synchronized void setWeight(String weightName, double weight) {
		
		weightMap.put(weightName, weight);
	}

	public synchronized void increaseWeight(String weightName, double increaseWeightBy) {

		if(!weightMap.containsKey(weightName)) {
			weightMap.put(weightName, 0.0);
		}
		
		double newWeight = weightMap.get(weightName) + increaseWeightBy;
		weightMap.put(weightName, newWeight);
	}

	public synchronized void decreaseWeight(String weightName, double decreaseWeightBy) {
		
		if(!weightMap.containsKey(weightName)) {
			weightMap.put(weightName, 0.0);
		}
		
		double newWeight = weightMap.get(weightName) - decreaseWeightBy;
		weightMap.put(weightName, newWeight);
	}
	

	public synchronized void multiplicateWeight(String weightName, double multiplicateWeightBy) {

		double newWeight = weightMap.get(weightName) * multiplicateWeightBy;
		weightMap.put(weightName, newWeight);
	}
	
	public synchronized void removeWeight(String weightName) {
		
		weightMap.remove(weightName);
	}
}
