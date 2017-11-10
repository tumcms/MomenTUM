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

package tum.cms.sim.momentum.visualization.handler;

import java.io.File;

import javafx.stage.FileChooser;
import javafx.stage.Window;
import tum.cms.sim.momentum.configuration.model.output.WriterSourceConfiguration;
import tum.cms.sim.momentum.configuration.model.output.WriterSourceConfiguration.OutputType;
import tum.cms.sim.momentum.utility.csvData.CsvType;
import tum.cms.sim.momentum.utility.csvData.reader.CsvReader;
import tum.cms.sim.momentum.utility.csvData.reader.SimulationOutputReader;
import tum.cms.sim.momentum.visualization.enums.PropertyType;
import tum.cms.sim.momentum.visualization.controller.CoreController;
import tum.cms.sim.momentum.visualization.utility.AnimationCalculations;
import tum.cms.sim.momentum.visualization.utility.CsvFile;
import tum.cms.sim.momentum.visualization.view.dialogControl.CustomCSVDialogCreator;
import tum.cms.sim.momentum.visualization.view.dialogControl.InformationDialogCreator;

public class LoadCsvHandler extends LoadHandler {

	private final static String titleOutputChooser = "Open simulation ouput file";
	private final static String csvDelimiter = ";";

	@Override
	public void load(CoreController coreController, Window parentWindow, double currentTimeStep) throws Exception {

		File selectedFile = getFile(titleOutputChooser, new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv"), parentWindow);

		if (selectedFile != null) {
			
			CsvFile file = new CsvFile(selectedFile.getPath());
				
			try {
				
				file.setType(CustomCSVDialogCreator.createCustomCSVDialog(CsvFile.getCsvTypeFromFile(file.getPath())));
				this.load(coreController, file, currentTimeStep);
				
			} 
			catch (Exception e) {
				
				InformationDialogCreator.createErrorDialog("", "Error loading csv data", e);
			}
		}
	}

	@Override
	public void load(CoreController coreController, File file, double currentTimeStep)
			throws Exception {
		
		if (file instanceof CsvFile) {
			
			CsvFile csvFile = new CsvFile(file.getPath());
			csvFile.setType(((CsvFile) file).getType());
			
			try {

				CsvReader dataSetReader = new CsvReader(csvFile.getAbsolutePath(), OutputType.timeStep.name(),
						csvDelimiter);
				
				SimulationOutputReader simulationOutputReader = new SimulationOutputReader(dataSetReader, 
						OutputType.timeStep.name(), 
						coreController.getInteractionViewController().getTimeLineModel().getEndTime(), 
						coreController.getInteractionViewController().getTimeLineModel().getTimeStepDuration(),
						csvFile.getType());
				
				simulationOutputReader.setInnerClusterSeparator(csvFile.getType().getIdHeader());
				simulationOutputReader.readIndex(WriterSourceConfiguration.indexString);

				coreController.getCoreModel().putSimulationOutputReader(file.getAbsolutePath(), simulationOutputReader);
				simulationOutputReader.readDataSet(currentTimeStep);
				simulationOutputReader.startReadDataSetAsync();
				
				if (CsvType.isCustomType(csvFile.getType())) {
					
					coreController.getPlaybackController().bindCustomShapes(csvFile.getType());
				}
				else {
					
					// Pedestrian shapes are not generic and already bound
					// the pedestrian time step is the guideline for the simulation
					coreController.getInteractionViewController().getTimeLineModel()
						.setTimeStepMultiplicator(simulationOutputReader.getTimeStepDifference());
				}

				AnimationCalculations.calculateVisualizationOfTimeStep(currentTimeStep, coreController);
				
				AnimationCalculations.calculateVisualizationOfTimeStep(currentTimeStep, coreController);
				
				UserPreferenceHandler.putProperty(PropertyType.outputCsvPath, csvFile.getParent());
				QuickloadHandler.addFile(csvFile);
				coreController.getCoreModel().setCsvLoaded(true);
			}
			catch (Exception e) {

				coreController.getCoreModel().setCsvLoaded(false);
				coreController.getPlaybackController().getPlaybackModel().clearPedestrians();
				coreController.getPlaybackController().getPlaybackModel().clearCustom();
				InformationDialogCreator.createErrorDialog(null, "Error loading csv data", e);
				throw e;
			}
		}
	}
}
