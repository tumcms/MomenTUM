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

import tum.cms.sim.momentum.configuration.model.output.WriterSourceConfiguration;
import tum.cms.sim.momentum.data.agent.car.CarManager;
import tum.cms.sim.momentum.data.agent.car.state.other.StaticState;
import tum.cms.sim.momentum.data.agent.car.types.IRichCar;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.infrastructure.logging.LoggingManager;
import tum.cms.sim.momentum.model.generator.Generator;
import tum.cms.sim.momentum.utility.csvData.CsvType;
import tum.cms.sim.momentum.utility.csvData.reader.CsvReader;
import tum.cms.sim.momentum.utility.csvData.reader.SimulationOutputCluster;
import tum.cms.sim.momentum.utility.csvData.reader.SimulationOutputReader;
import tum.cms.sim.momentum.utility.geometry.GeometryFactory;
import tum.cms.sim.momentum.utility.geometry.Vector2D;

import static org.apache.camel.component.xslt.XsltOutput.file;

public class CarCsvGenerator extends Generator {
		
	protected CarManager carManager = null;
	
	public CarManager getCarManager() {
		return carManager;
	}

	public void setCarManager(CarManager carManager) {
		this.carManager = carManager;
	}
	
	private final static String csvFileName = "csvFile";
	private final static String delimiterName = "delimiter";
	private final static String timeStepDurationName = "timeStepDuration";

	protected static final String VariableTime = "time";
	protected static final String VariableId = "id";
	protected static final String VariablePositionX = "x";
	protected static final String VariablePositionY = "y";
	protected static final String VariableHeadingX = "xHeading";
	protected static final String VariableHeadingY = "yHeading";
	protected static final String VariableWidth = "width";
	protected static final String VariableLength = "length";
	protected static final String VariableHeight = "height";

	double timeStepDuration = 0;

	SimulationOutputReader outputReader;

	@Override
	public void callPreProcessing(SimulationState simulationState) {

		String csvFile = this.properties.getStringProperty(csvFileName);
		String delimiter = this.properties.getStringProperty(delimiterName);

		try {
			CsvReader csvReader = new CsvReader(csvFile, WriterSourceConfiguration.OutputType.timeStep.name(), delimiter);
			this.outputReader = new SimulationOutputReader(csvReader,
					WriterSourceConfiguration.OutputType.timeStep.name(),
					simulationState.getSimulationEndTime(),
					simulationState.getTimeStepDuration(),
					CsvType.Car);

			this.outputReader.setInnerClusterSeparator(VariableId);
			this.outputReader.readIndex(WriterSourceConfiguration.indexString);
		}
		catch (Exception exception) {
			LoggingManager.logUser(this, exception);
		}

		timeStepDuration = this.properties.getDoubleProperty(timeStepDurationName);
	}

	@Override
	public void execute(Collection<? extends Void> splitTask, SimulationState simulationState) {

		double currentSimulationTime = simulationState.getCurrentTime();
		double targetReaderTimeStep = currentSimulationTime / this.timeStepDuration;

		int timeStepFloor = (int) Math.floor(targetReaderTimeStep);
		double timeFloor = timeStepFloor * this.timeStepDuration;
		int timeStepCeil = (int) Math.ceil(targetReaderTimeStep);
		double timeCeil = timeStepCeil * this.timeStepDuration;

		double weightCeil = (currentSimulationTime - timeFloor) / this.timeStepDuration;
		double weightFloor = 1 - weightCeil;


		SimulationOutputCluster dataStepFloor = null;
		SimulationOutputCluster dataStepCeil = null;
		try {
			while (dataStepFloor == null) {
				dataStepFloor = this.outputReader.asyncReadDataSet(timeStepFloor);
			}

			while (dataStepCeil == null) {
				dataStepCeil = this.outputReader.asyncReadDataSet(timeStepCeil);
			}
		}
		catch (Exception exception) {

			LoggingManager.logUser(this, exception);
		}


		// remove cars
		ArrayList<Integer> allExistingCarIds = this.carManager.getAllCarIds();
		for(Integer currentCarId : allExistingCarIds) {

			if(dataStepFloor.isEmpty() || !dataStepFloor.containsIdentification(String.valueOf(currentCarId))) {
				this.carManager.removeCar(currentCarId);
			}
		}


		if (!dataStepFloor.isEmpty()) {

			for (String id : dataStepFloor.getIdentifications()) {

				int idInteger = Integer.parseInt(id);


				if(!carManager.containsCar(idInteger)) {
					// new car
					StaticState staticState = new StaticState(
							dataStepFloor.getDoubleData(id, VariableLength),
							dataStepFloor.getDoubleData(id, VariableWidth),
							dataStepFloor.getDoubleData(id, VariableHeight));
					staticState.setId(idInteger);

					this.carManager.createCar(staticState);
				}

				if(!dataStepCeil.isEmpty() && dataStepCeil.containsIdentification(id)) {
					// update car

					double positionX = dataStepFloor.getDoubleData(id, VariablePositionX) * weightFloor +
							dataStepCeil.getDoubleData(id, VariablePositionX) * weightCeil;
					double positionY = dataStepFloor.getDoubleData(id, VariablePositionY) * weightFloor +
							dataStepCeil.getDoubleData(id, VariablePositionY) * weightCeil;
					Vector2D position = GeometryFactory.createVector(positionX, positionY);

					double headingX = dataStepFloor.getDoubleData(id, VariableHeadingX) * weightFloor +
							dataStepCeil.getDoubleData(id, VariableHeadingX) * weightCeil;
					double headingY = dataStepFloor.getDoubleData(id, VariableHeadingY) * weightFloor +
							dataStepCeil.getDoubleData(id, VariableHeadingY) * weightCeil;
					Vector2D heading = GeometryFactory.createVector(headingX, headingY);

					double velocityX = (dataStepCeil.getDoubleData(id, VariablePositionX) -
							dataStepFloor.getDoubleData(id, VariablePositionX)) / this.timeStepDuration;
					double velocityY = (dataStepCeil.getDoubleData(id, VariablePositionY) -
							dataStepFloor.getDoubleData(id, VariablePositionY)) / this.timeStepDuration;
					Vector2D velocity = GeometryFactory.createVector(velocityX, velocityY);

					carManager.updateState(idInteger, position, velocity, heading, currentSimulationTime);
				}

			}
		}
	}

	@Override
	public void callPostProcessing(SimulationState simulationState) {

	}

}
