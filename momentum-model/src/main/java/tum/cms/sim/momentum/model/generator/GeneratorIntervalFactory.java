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

package tum.cms.sim.momentum.model.generator;

import java.io.File;
import java.util.ArrayList;

import tum.cms.sim.momentum.configuration.generator.GeneratorConfiguration.GeneratorType;
import tum.cms.sim.momentum.model.generator.interval.DistributionGeneratorInterval;
import tum.cms.sim.momentum.model.generator.interval.ExternalInterval;
import tum.cms.sim.momentum.model.generator.interval.GeneratorInterval;
import tum.cms.sim.momentum.model.generator.interval.GeneratorIntervalSet;
import tum.cms.sim.momentum.model.generator.interval.PlanGeneratorInterval;
import tum.cms.sim.momentum.model.generator.interval.StockGeneratorInterval;
import tum.cms.sim.momentum.utility.generic.PropertyBackPack;
import tum.cms.sim.momentum.utility.probability.ProbabilitySet;

public class GeneratorIntervalFactory {
	
	private final static String intervalName = "interval";
	private final static String timePointName = "timePoint";
	private final static String timeGapName = "timeGap";
	private final static String stockName = "stock";
	private final static String percentageName = "percentage";
	private final static String exchangeFileName = "exchangeFile";
	
	private GeneratorIntervalFactory() { }
	
	public static GeneratorIntervalSet createIntervalSet(PropertyBackPack generatorsBackPack, 
			GeneratorType type,
			double generatorStartTime,
			double generatorEndTime,
			int maximalPedestrians) {
		
		GeneratorIntervalSet generatorInveralSet = null;
		ArrayList<Double> intervalPercentages = generatorsBackPack.<Double>getListProperty(percentageName);
		
		switch(type) {
		
		case Distribution:
			
			ArrayList<Double> timeGaps = generatorsBackPack.<Double>getListProperty(timeGapName);
			
			generatorInveralSet = GeneratorIntervalFactory.createDistributionIntervalsSet(generatorStartTime,
					generatorEndTime,
					maximalPedestrians,
					timeGaps, 
					intervalPercentages);
			break;
			
		case Plan:
			
			ArrayList<Double> intervalDurations = generatorsBackPack.<Double>getListProperty(intervalName);
			
			generatorInveralSet = GeneratorIntervalFactory.createPlanIntervalsSet(generatorStartTime,
					generatorEndTime, 
					maximalPedestrians,
					intervalDurations,
					intervalPercentages);
			break;
			
		case Stock:
			
			ArrayList<Double> timePoints = generatorsBackPack.<Double>getListProperty(timePointName);
			ArrayList<Integer> stocks = generatorsBackPack.<Integer>getListProperty(stockName);
			
			generatorInveralSet = GeneratorIntervalFactory.createStockIntervalsSet(generatorStartTime,
					generatorEndTime,
					maximalPedestrians,
					timePoints,
					stocks);
			
			break;
			
		case External:
			
			PropertyBackPack executablePropertyBackPack = generatorsBackPack.getChildPropertyBackPacks().stream().findAny().get();
			File exchangeFile = generatorsBackPack.getFileProperty(exchangeFileName);
			generatorInveralSet = GeneratorIntervalFactory.createExecutableIntervalSet(executablePropertyBackPack, exchangeFile);
			break;
			
		case Instant:
			
			// Nothing to do
			
			break;
			
		default:
			break;
		}
		
		return generatorInveralSet;
	}
	
	public static GeneratorIntervalSet createPlanIntervalsSet(double generatorStartTime,
			double generatorEndTime,
			int maximalPedestrians,
			ArrayList<Double> intervalDurationList, 
			ArrayList<Double> intervalPercentageList) {
		
		ArrayList<GeneratorInterval> generatorInverals = new ArrayList<GeneratorInterval>();

		PlanGeneratorInterval currentGeneratorInterval =  null;
		double currentIntervalStartTime = generatorStartTime;
		double currentIntervalDuration = 0.0;
		double currentIntervalPercentage = 0.0;
		
		for(int iter = 0; iter < intervalDurationList.size(); iter++) {
			
			if(iter + 1 < intervalDurationList.size()) {
				
				currentIntervalDuration = intervalDurationList.get(iter + 1) - currentIntervalStartTime;
			}
			else {
				
				currentIntervalDuration = generatorEndTime - intervalDurationList.get(iter);
			}
		
			currentIntervalPercentage = intervalPercentageList.get(iter);
			
			currentGeneratorInterval = new PlanGeneratorInterval(generatorStartTime, generatorEndTime, maximalPedestrians);
			currentGeneratorInterval.loadConfiguration(currentIntervalStartTime, 
					currentIntervalStartTime + currentIntervalDuration, 
					currentIntervalPercentage);
			generatorInverals.add(currentGeneratorInterval);
			
			currentIntervalStartTime += currentIntervalDuration; 
		}
		
		return new GeneratorIntervalSet(generatorInverals);
	}
	
	public static GeneratorIntervalSet createDistributionIntervalsSet(double generatorStartTime,
			double generatorEndTime,
			int maximalPedestrians,
			ArrayList<Double> timeGapList, 
			ArrayList<Double> percentageList) {
	
		ArrayList<GeneratorInterval> generatorInverals = new ArrayList<GeneratorInterval>();
		DistributionGeneratorInterval currentGeneratorInterval =  null;
		
		currentGeneratorInterval = new DistributionGeneratorInterval(generatorStartTime, generatorEndTime, maximalPedestrians);
		
		ProbabilitySet<Double> gapProbailitySet = new ProbabilitySet<Double>();
		
		for(int iter = 0; iter < timeGapList.size(); iter++) {
			
			gapProbailitySet.append(timeGapList.get(iter), percentageList.get(iter));
		}
		
		currentGeneratorInterval.loadConfiguration(gapProbailitySet);
		generatorInverals.add(currentGeneratorInterval);
		
		return new GeneratorIntervalSet(generatorInverals);
	}
	
	public static GeneratorIntervalSet createStockIntervalsSet(double generatorStartTime,
			double generatorEndTime,
			int maximalPedestrians,
			ArrayList<Double> timePoints, 
			ArrayList<Integer> stocks) {
		
		ArrayList<GeneratorInterval> generatorInverals = new ArrayList<GeneratorInterval>();
		
		for(int iter = 0; iter < timePoints.size(); iter++) {
			
			StockGeneratorInterval currentGeneratorInterval = new StockGeneratorInterval(generatorStartTime, generatorEndTime, maximalPedestrians);
			
			currentGeneratorInterval.loadConfiguration(timePoints.get(iter), stocks.get(iter));
			generatorInverals.add(currentGeneratorInterval);
		}
		
		return new GeneratorIntervalSet(generatorInverals);
	}
	
	public static GeneratorIntervalSet createExecutableIntervalSet(PropertyBackPack executablePropertyBackPack, File exchangeFile) {
		
		ArrayList<GeneratorInterval> generatorInverals = new ArrayList<GeneratorInterval>();
		ExternalInterval currentGeneratorInterval = new ExternalInterval(0.0, Double.MAX_VALUE, Integer.MAX_VALUE, executablePropertyBackPack, exchangeFile);
		generatorInverals.add(currentGeneratorInterval);
		
		return new GeneratorIntervalSet(generatorInverals);
	}
}
