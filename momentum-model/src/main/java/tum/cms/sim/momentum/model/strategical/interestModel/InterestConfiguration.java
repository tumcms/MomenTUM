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

package tum.cms.sim.momentum.model.strategical.interestModel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import tum.cms.sim.momentum.utility.generic.PropertyBackPack;
import tum.cms.sim.momentum.utility.probability.ProbabilitySet;

public class InterestConfiguration {

	private static String separatorName = "separator";
	private static String pedestrianNumberFileName = "pedestrianNumbers";
	private static String interarrivalDistributionFileName = "interarrivalDistributions";
	private static String serviceTimeDistributionFileName = "serviceTimeDistributions";
	

	private ArrayList<Pair<Double,Double>> pedestrianNumbersSlots = new ArrayList<Pair<Double,Double>>();
	
	public ArrayList<Pair<Double, Double>> getPedestrianNumbersSlots() {
		return pedestrianNumbersSlots;
	}

	private HashMap<Integer, ArrayList<Integer>> pedestrianCountLocation = new HashMap<Integer, ArrayList<Integer>>();
	
	public HashMap<Integer, ArrayList<Integer>> getPedestrianCountLocation() {
		return pedestrianCountLocation;
	}
	
	private ArrayList<Integer> pedestrianInFlowNumbers = new ArrayList<Integer>();
	
	public ArrayList<Integer> getPedestrianInFlowNumbers() {
		return pedestrianInFlowNumbers;
	}
	
	private ArrayList<Integer> pedestrianStartNumbers = new ArrayList<Integer>();
	
	public ArrayList<Integer> getPedestrianStartNumbers() {
		return pedestrianStartNumbers;
	}

	private ArrayList<Integer> pedestrianEndNumbers = new ArrayList<Integer>();

	public ArrayList<Integer> getPedestrianEndNumbers() {
		return pedestrianEndNumbers;
	}

	private HashMap<Integer, ArrayList<Pair<Double, Double>>> interarrivalSlots = new HashMap<Integer, ArrayList<Pair<Double,Double>>>();

	public ArrayList<Pair<Double, Double>> getInterarrivalSlots(Integer locationId) {
		return interarrivalSlots.get(locationId);
	}
	
	private HashMap<Integer, ArrayList<ProbabilitySet<Double>>> interarrivalDistributions = new HashMap<Integer, ArrayList<ProbabilitySet<Double>>>();
	
	public ArrayList<ProbabilitySet<Double>> getInterarrivalDistributions(Integer locationId) {
		return interarrivalDistributions.get(locationId);
	}
	
	private HashMap<Integer, ArrayList<Pair<Double, Double>>> serviceTimeSlots = new HashMap<Integer, ArrayList<Pair<Double,Double>>>();
	
	public ArrayList<Pair<Double, Double>> getServiceTimeSlots(Integer locationId) {
		return serviceTimeSlots.get(locationId);
	}

	private HashMap<Integer, ArrayList<ProbabilitySet<Double>>> serviceTimeDistributions = new HashMap<Integer, ArrayList<ProbabilitySet<Double>>>();
	
	public ArrayList<ProbabilitySet<Double>> getServiceTimeDistributions(Integer locationId) {
		
		return serviceTimeDistributions.get(locationId);
	}
	
	public void loadDistributions(PropertyBackPack interestStrategicBackPack) throws IOException { 
		
		String separator = interestStrategicBackPack.getStringProperty(InterestConfiguration.separatorName);
		File interarrivalCsvFile = interestStrategicBackPack.getFileProperty(InterestConfiguration.interarrivalDistributionFileName);
		
		ArrayList<ArrayList<String>> interarrivalRawCsv = this.loadComplexCsv(interarrivalCsvFile, separator);
		this.generateConfiguration(interarrivalRawCsv, this.interarrivalSlots, this.interarrivalDistributions);
		
		File serviceTimeCsvFile = interestStrategicBackPack.getFileProperty(InterestConfiguration.serviceTimeDistributionFileName);
		
		ArrayList<ArrayList<String>> serviceTimeRawCsv = this.loadComplexCsv(serviceTimeCsvFile, separator);
		this.generateConfiguration(serviceTimeRawCsv, this.serviceTimeSlots, this.serviceTimeDistributions);
	}
	
	public void loadPedestrianNumbers(PropertyBackPack interestStrategicaBackPack) throws IOException {
		
		String separator = interestStrategicaBackPack.getStringProperty(InterestConfiguration.separatorName);
		File pedestrianNumberFile = interestStrategicaBackPack.getFileProperty(InterestConfiguration.pedestrianNumberFileName);
		ArrayList<ArrayList<String>> pedestrianNumberRawCsv = this.loadComplexCsv(pedestrianNumberFile, separator);

		for(int iter = 0; iter < pedestrianNumberRawCsv.size(); iter++) {
					
			if(iter == 0 || pedestrianNumberRawCsv.get(iter).size() == 0) { // next pedestrian measrument count for location
				
				if(pedestrianNumberRawCsv.size() < iter + 2) {
					
					continue; // no more time slots
				}
				
				if(iter > 0) {
				
					iter++;
				}
				
				Double start = Double.parseDouble(pedestrianNumberRawCsv.get(iter).get(0));
				Double end = Double.parseDouble(pedestrianNumberRawCsv.get(iter).get(1));
				Integer inFlow = Integer.parseInt(pedestrianNumberRawCsv.get(iter).get(2));
				
				Pair<Double,Double> timeSlot = new MutablePair<Double, Double>(start, end);
				this.pedestrianNumbersSlots.add(timeSlot);
				this.pedestrianInFlowNumbers.add(inFlow);
				
				// add pedestrians for slot first is max, second is start count
				iter++;
			
				Integer startNumber = Integer.parseInt(pedestrianNumberRawCsv.get(iter).get(0));	
				Integer endNumber = Integer.parseInt(pedestrianNumberRawCsv.get(iter).get(1));
				this.pedestrianEndNumbers.add(endNumber);
				this.pedestrianStartNumbers.add(startNumber);
				
			}
			else { // new time slot
				
				Integer locationPedestrianCount = Integer.parseInt(pedestrianNumberRawCsv.get(iter).get(0));
				Integer locationId = Integer.parseInt(pedestrianNumberRawCsv.get(iter).get(1));
				
				if(!this.pedestrianCountLocation.containsKey(locationId)) {
					
					this.pedestrianCountLocation.put(locationId, new ArrayList<Integer>());
				}
				
				this.pedestrianCountLocation.get(locationId).add(locationPedestrianCount);
			}
		}
	}
	
	private void generateConfiguration(ArrayList<ArrayList<String>> interarrivalRawCsv,
			HashMap<Integer, ArrayList<Pair<Double,Double>>> slots,
			HashMap<Integer, ArrayList<ProbabilitySet<Double>>> distributions) {
		
		ArrayList<Pair<Double,Double>> locationSlots = null;	
		ArrayList<ProbabilitySet<Double>> locationDistributions = null;
		
		Double itemContent = null;
		Double valueContent = null;
		
		for(int iter = 0; iter < interarrivalRawCsv.size(); iter++) {

			if(interarrivalRawCsv.get(iter).size() == 1) { // new location
				
				// read location id
				Integer locationId = Integer.parseInt(interarrivalRawCsv.get(iter).get(0));
				
				// create new location container
				locationSlots = new ArrayList<Pair<Double,Double>>();
				locationDistributions = new ArrayList<ProbabilitySet<Double>>();
				slots.put(locationId, locationSlots);		
				distributions.put(locationId, locationDistributions);
			
				// next line content
				iter++;
				
				// new slot and distribution
				itemContent = Double.parseDouble(interarrivalRawCsv.get(iter).get(0));
				valueContent = Double.parseDouble(interarrivalRawCsv.get(iter).get(1));
				locationSlots.add(new MutablePair<Double, Double>(itemContent, valueContent));
				locationDistributions.add(new ProbabilitySet<Double>());
			}
			else if(interarrivalRawCsv.get(iter).size() == 0) { // new time slot for location
				
				// next line content
				if(interarrivalRawCsv.size() >= iter + 1) {
					
					if(interarrivalRawCsv.get(iter + 1).size() == 1) {
						continue; // new distribution and location following
					}
				}
				iter++;

				// read start end for slot
				itemContent = Double.parseDouble(interarrivalRawCsv.get(iter).get(0));
				valueContent = Double.parseDouble(interarrivalRawCsv.get(iter).get(1));
				
				// new slot and distribution
				locationSlots.add(new MutablePair<Double, Double>(itemContent, valueContent));
				locationDistributions.add(new ProbabilitySet<Double>());
			}
			else { // new current distribution entry

				itemContent = Double.parseDouble(interarrivalRawCsv.get(iter).get(0));
				valueContent = Double.parseDouble(interarrivalRawCsv.get(iter).get(1));
				locationDistributions.get(locationDistributions.size() - 1).append(itemContent, valueContent);
			}
		}
	}
	
	private ArrayList<ArrayList<String>> loadComplexCsv(File csvFile, String separator) throws IOException {
		
		ArrayList<ArrayList<String>> dataListsOfList = new ArrayList<ArrayList<String>>();
		List<String> input = Files.readAllLines(csvFile.toPath());

		for(String line : input) {
			
			ArrayList<String> dataList = new ArrayList<String>();
			dataListsOfList.add(dataList);
			String [] items = line.split(separator);	
		
			for(int iter = 0; iter < items.length; iter++) {
				
				dataList.add(items[iter]);
			}
		}
		
		return dataListsOfList;	
	}
}
