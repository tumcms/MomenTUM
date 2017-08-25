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

package tum.cms.sim.momentum.model.operational.walking.macroscopicModels.data;

import java.util.ArrayList;

import org.apache.commons.lang3.tuple.Pair;

import tum.cms.sim.momentum.model.operational.walking.macroscopicModels.velocityModel.IVelocityModel;

public class MacroscopicGenerator {
	
	private Integer id = null;
	private Double totalNumberOfPedestrians = null;
	private ArrayList<Pair<Double, Double>> generatorIntervals = null;
	private Double maximalScenarioTime = null;
	private IVelocityModel velocityModel = null;
	
	public MacroscopicGenerator(Integer id, Double totalNumberOfPedestrians, ArrayList<Pair<Double, Double>> generatorIntervals, Double maximalScenarioTime, IVelocityModel velocityModel) {
		
		setId(id);
		setTotalNumberOfPedestrians(totalNumberOfPedestrians);
		setGeneratorIntervals(generatorIntervals);
		setMaximalScenarioTime(maximalScenarioTime);
		this.velocityModel = velocityModel;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Double getTotalNumberOfPedestrians() {
		return totalNumberOfPedestrians;
	}

	public void setTotalNumberOfPedestrians(Double totalNumberOfPedestrians) {
		this.totalNumberOfPedestrians = totalNumberOfPedestrians;
	}

	public ArrayList<Pair<Double, Double>> getGeneratorIntervals() {
		return generatorIntervals;
	}

	public void setGeneratorIntervals(ArrayList<Pair<Double, Double>> generatorIntervals) {
		this.generatorIntervals = generatorIntervals;
	}

	public Double getMaximalScenarioTime() {
		return maximalScenarioTime;
	}

	public void setMaximalScenarioTime(Double maximalScenarioTime) {
		this.maximalScenarioTime = maximalScenarioTime;
	}
	
	public void generatePedestriansForTimestep(Double currentTime, MacroscopicNode node, Double timestep)
	{
		Double accumulatedTime = 0.0;
		Double timeFraction = currentTime / maximalScenarioTime;
		Double pedestriansTS;
		
		for (int p=0; p<generatorIntervals.size(); p++) {
			
			accumulatedTime += generatorIntervals.get(p).getLeft();
			
			if (timeFraction < accumulatedTime) {
				
				pedestriansTS = generatorIntervals.get(p).getRight() * totalNumberOfPedestrians * timestep / (maximalScenarioTime * generatorIntervals.get(p).getLeft());

				Density DensityTS = new Density(pedestriansTS, velocityModel, 1);
				double newDestination[] = new double[1];
				newDestination[0] = 1.0;
				DensityTS.setDestination(newDestination);
				node.addDensity(DensityTS);			

				return;
			}
		} 	
	}
}
