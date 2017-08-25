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

package tum.cms.sim.momentum.model.generator.generatorTypes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import tum.cms.sim.momentum.data.agent.car.CarManager;
import tum.cms.sim.momentum.data.agent.car.state.other.StaticState;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.generator.Generator;
import tum.cms.sim.momentum.utility.geometry.GeometryFactory;
import tum.cms.sim.momentum.utility.geometry.Vector2D;

public class CarCsvGenerator extends Generator {
		
	protected CarManager carManager = null;
	
	public CarManager getCarManager() {
		return carManager;
	}

	public void setCarManager(CarManager carManager) {
		this.carManager = carManager;
	}
	
	protected static final String CarPositionsTag = "carPositions";
	protected static final String VariableNamesTag = "variableNames";

	DataManager dataManager = null;

	@Override
	public void callPreProcessing(SimulationState simulationState) {
		
		this.dataManager = new DataManager(this.properties.getListProperty(VariableNamesTag),
				this.properties.getMatrixProperty(CarPositionsTag), simulationState);

	}

	@Override
	public void execute(Collection<? extends Void> splittTask, SimulationState additonalData) {
		
		ArrayList<DataManager.Car> currentCars = this.dataManager.getCars(additonalData.getCurrentTimeStep());
		for (DataManager.Car car: currentCars) {
			  //if(!this.dataManager.isCarExistent(additonalData.getCurrentTimeStep()-1, car.Id) || ) {
				  // create a new car or update
				  
				StaticState staticState = new StaticState( car.sizeLength, car.sizeWidth );
			    staticState.setId(car.Id);
				carManager.createCar(staticState, car.position, car.velocity, car.heading, car.validTime);
			  //}
		}

		List<Integer> removedCars = this.dataManager.getDeletedCars(additonalData.getCurrentTimeStep());
		this.carManager.removeCars(removedCars);
		
	}

	@Override
	public void callPostProcessing(SimulationState simulationState) {
		
	}
	
	static class DataManager {
		
		protected static final String VariableTime = "time";
		protected static final String VariableId = "id";
		protected static final String VariablePositionX = "x";
		protected static final String VariablePositionY = "y";
		protected static final String VariableWidth = "width";
		protected static final String VariableLength = "length";
		protected static final String VariableOrientation = "orientation";
		
		private int timeIndex = 0;
		private int indexId = 0;

		
		protected static final double Precision = 0.00001;
		
		private HashMap<Integer, ArrayList<Double>> carData = null;
		private ArrayList<String> variableNames = null;
		
		private HashMap<Long, ArrayList<Car>> preparedCarData = new HashMap<Long, ArrayList<Car>>();
		
		public static class Car {
			public int Id = 0;
			public Double validTime = null;
			
			Vector2D position = null;
			Vector2D velocity = null;
			Vector2D heading = null;
			
			public double sizeLength = 0;
			public double sizeWidth = 0;
		}
		
		public DataManager(ArrayList<String> variableNames, HashMap<Integer, ArrayList<Double>> carData, SimulationState simulationState) {
			this.variableNames = variableNames;
			this.carData = carData;
			
			this.timeIndex = this.variableNames.indexOf(DataManager.VariableTime);
			this.indexId = this.variableNames.indexOf(DataManager.VariableId);
			
			int totalTimeSteps = (int) Math.ceil((double) simulationState.getSimulationEndTime() / simulationState.getTimeStepDuration());
			
			double currentTime = 0; // of internal simulation
			
			int separatorRow = 1;
			int previousDataRow = 0;
			int nextDataRow = 0;
			
			for(Long currentTimeStep = 0L; currentTimeStep <= totalTimeSteps; currentTimeStep++) {
				currentTime = currentTimeStep * simulationState.getTimeStepDuration();
				
				if( !this.carData.containsKey(nextDataRow+1) )
					break;
				
				separatorRow = this.getNextSeparator(separatorRow, currentTime);
				previousDataRow = this.getPreviousRow(separatorRow);
				nextDataRow = this.getNextRow(separatorRow);
				// System.out.println("currentTime: " + currentTime + " separator: " + separatorRow + " prevRow: " + previousDataRow + " nextRow: " + nextDataRow);
				/*System.out.println("currentTime: " + currentTime + 
				" separator: " + separatorRow +  
				" prev: " + previousDataRow +  
				" next: " + nextDataRow);
				 */
				/*for(int curI = previousDataRow; curI <= nextDataRow; curI++) {
					System.out.println(curI + ": " + this.carData.get(curI));
				}*/
				
				ArrayList<Car> carsOfTimeStep = new ArrayList<Car>();
				Car currentCar, pairedCar;
				
				for(int currentDataRow = previousDataRow; currentDataRow <= separatorRow; currentDataRow++) {
					
					currentCar = this.getCarFromRow(currentDataRow);
					int pairedDataRow = this.findRowWithId(currentCar.Id, previousDataRow, nextDataRow);
					
					if(currentDataRow != pairedDataRow) {
						pairedCar = this.getCarFromRow( pairedDataRow );
						Car interpolatedCar = this.interpolateCar(currentCar, pairedCar, currentTime);
						
						carsOfTimeStep.add( interpolatedCar );
						
						/*
						System.out.println("currentTime: " + currentTime + 
								" first car: " + currentCar.Id + "(" + currentCar.validTime + ") p(" + currentCar.position.getXComponent() + "," + currentCar.position.getYComponent() + ")" + 
								"; second car: " + pairedCar.Id + "(" + pairedCar.validTime + ") p(" + pairedCar.position.getXComponent() + "," + pairedCar.position.getYComponent() + ")" + 
								"; interpolated v(" + interpolatedCar.velocity.getXComponent() + "," + interpolatedCar.velocity.getYComponent() + ")"); */
					}
					
				}
				this.preparedCarData.put(currentTimeStep, carsOfTimeStep);
				

				//System.out.println("");
				
				//if (currentTimeStep > 10)
				//	break;
				
			}
			
		}
		
		public ArrayList<Car> getCars(Long timeStep) {
			ArrayList<Car> carList = this.preparedCarData.get(timeStep);
			if(carList == null)
				return new ArrayList<Car>();
			else
				return carList;
		}
		
		public boolean isCarExistent(Long timeStep, int id) {
			if(!this.preparedCarData.containsKey(timeStep))
				return false;
			
			for (Car curInstance: this.preparedCarData.get(timeStep)) {
				  if(curInstance.Id == id)
				  	return true;
			}
			return false;
		}
		
		public List<Integer> getDeletedCars(Long timeStep) {
			List<Integer> deletedCarIds = new ArrayList<Integer>();
			ArrayList<Car> lastCars = this.getCars(timeStep - 1);
			
			for(Car car : lastCars) {
				if(!this.isCarExistent(timeStep, car.Id))
					deletedCarIds.add(car.Id);
			}
			
			return deletedCarIds;
		}
		
		private int getNextSeparator(int oldSeparatorRow, double currentTime) {
			int separatorRow = oldSeparatorRow;
			while( this.carData.containsKey(separatorRow) &&
					this.carData.get(separatorRow).get(timeIndex) < currentTime) {
				separatorRow++;
			}
			return --separatorRow;
		}
		
		private int getPreviousRow(int separatorRow) {
			int previousDataRow = separatorRow;
			while( previousDataRow >= 0 && 
					Math.abs(this.carData.get(previousDataRow).get(this.timeIndex) -
							this.carData.get(separatorRow).get(this.timeIndex)) < DataManager.Precision )
				previousDataRow--;
			
			return ++previousDataRow;
		}
		
		private int getNextRow(int separatorRow) {
			int nextDataRow = separatorRow + 1;
			while( this.carData.containsKey(nextDataRow) &&
					Math.abs(this.carData.get(nextDataRow).get(timeIndex) - this.carData.get(separatorRow+1).get(timeIndex)) < DataManager.Precision )
				nextDataRow++;
			return --nextDataRow;
		}
		
		private int findRowWithId(int id, int previousDataRow, int nextDataRow) {
			for(int currentRow = nextDataRow; currentRow >= previousDataRow; currentRow--) {
				if( Math.abs(this.carData.get(currentRow).get(this.indexId) -
						id) < DataManager.Precision) {
					return currentRow;
				}
			}
			return -1;
		}
		
		private DataManager.Car getCarFromRow(int dataRow) {
			Car currentCar = new Car();
			
			if (!this.carData.containsKey(dataRow)) {
				currentCar.Id = -1;
				return currentCar;
			}

			ArrayList<Double> currentContentRow = this.carData.get(dataRow);
			
			currentCar.Id = currentContentRow.get(indexId).intValue();
			currentCar.validTime = currentContentRow.get(timeIndex);
			
			currentCar.position = GeometryFactory.createVector(
					currentContentRow.get(this.variableNames.indexOf(DataManager.VariablePositionX)),
					currentContentRow.get(this.variableNames.indexOf(DataManager.VariablePositionY)));
			
			currentCar.heading = GeometryFactory.createVector(
					Math.cos(currentContentRow.get(this.variableNames.indexOf(DataManager.VariableOrientation))),
					Math.sin(currentContentRow.get(this.variableNames.indexOf(DataManager.VariableOrientation))));
			
			
			currentCar.sizeLength = currentContentRow.get(this.variableNames.indexOf(DataManager.VariableLength));
			currentCar.sizeWidth = currentContentRow.get(this.variableNames.indexOf(DataManager.VariableWidth));
			
			return currentCar;
		}
		
		private DataManager.Car interpolateCar(DataManager.Car carA, DataManager.Car carB, double targetTime) {
			Car interpolatedCar = new Car();
			
			double timeDiffFirstCar = targetTime - carA.validTime;
			double timeDifference = carB.validTime - carA.validTime;
			
			if (timeDifference < DataManager.Precision )
				return carA;
			
			interpolatedCar.Id = carA.Id;
			interpolatedCar.validTime = targetTime;
			
			interpolatedCar.position = GeometryFactory.createVector(
					(carA.position.getXComponent() + carB.position.getXComponent())*(timeDiffFirstCar/timeDifference),
					(carA.position.getYComponent() + carB.position.getYComponent())*(timeDiffFirstCar/timeDifference));
			
			interpolatedCar.heading = GeometryFactory.createVector(
					(carA.heading.getXComponent() + carB.heading.getXComponent())*(timeDiffFirstCar/timeDifference),
					(carA.heading.getYComponent() + carB.heading.getYComponent())*(timeDiffFirstCar/timeDifference));
			
			interpolatedCar.velocity = GeometryFactory.createVector(
					(carB.position.getXComponent() - carA.position.getXComponent()) / timeDifference,
					(carB.position.getYComponent() - carA.position.getYComponent()) / timeDifference);
			
			interpolatedCar.sizeWidth = (carA.sizeWidth + carB.sizeWidth)/2;
			interpolatedCar.sizeLength = (carA.sizeLength + carB.sizeLength)/2;
			
			return interpolatedCar;
		}
		
		
		
	}
}
