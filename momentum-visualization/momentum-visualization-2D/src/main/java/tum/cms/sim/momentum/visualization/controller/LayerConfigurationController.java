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

package tum.cms.sim.momentum.visualization.controller;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;
import tum.cms.sim.momentum.configuration.model.output.WriterSourceConfiguration.OutputType;
import tum.cms.sim.momentum.configuration.scenario.AreaConfiguration.AreaType;
import tum.cms.sim.momentum.utility.csvData.CsvType;
import tum.cms.sim.momentum.utility.csvData.reader.SimulationOutputCluster;
import tum.cms.sim.momentum.utility.csvData.reader.SimulationOutputReader;
import tum.cms.sim.momentum.visualization.model.geometry.TrajectoryModel;
import tum.cms.sim.momentum.visualization.utility.AnimationCalculations;
import tum.cms.sim.momentum.visualization.utility.ColorGenerator;
import tum.cms.sim.momentum.visualization.utility.IdExtension;
import tum.cms.sim.momentum.visualization.view.dialogControl.InformationDialogCreator;

public class LayerConfigurationController implements Initializable {
	
	private CoreController coreController;

	public void bindCoreModel(CoreController coreController) {
		
		this.coreController = coreController;
		
		this.layerConfigurationBox.disableProperty().bind(coreController.getCoreModel().layoutLoadedProperty().not());
		
		showAllTrajetoriesCheckBox.disableProperty().bind(coreController.getCoreModel().csvLoadedProperty().not());
		showGroupColoring.disableProperty().bind(coreController.getCoreModel().csvLoadedProperty().not());
		showSeedColoring.disableProperty().bind(coreController.getCoreModel().csvLoadedProperty().not());
		
		Bindings.bindBidirectional(showLatticesCheckBox.selectedProperty(),
				coreController.getVisualizationController().getVisibilitiyModel().latticeVisibilityProperty());
		Bindings.bindBidirectional(showGraphCheckBox.selectedProperty(),
				coreController.getVisualizationController().getVisibilitiyModel().graphVisibilityProperty());
		Bindings.bindBidirectional(showObstaclesCheckBox.selectedProperty(),
				coreController.getVisualizationController().getVisibilitiyModel().obstacleVisibilityProperty());
		Bindings.bindBidirectional(showOriginsCheckBox.selectedProperty(),
				coreController.getVisualizationController().getVisibilitiyModel().originVisibilityProperty());
		Bindings.bindBidirectional(showIntermediatesCheckBox.selectedProperty(),
				coreController.getVisualizationController().getVisibilitiyModel().intermediateVisibilityProperty());
		Bindings.bindBidirectional(showDestinationsCheckBox.selectedProperty(),
				coreController.getVisualizationController().getVisibilitiyModel().destinationVisibilityProperty());
		this.layerConfigurationBox.visibleProperty().bind(coreController.getCoreModel().getSwitchLayerView().isNotEqualTo(0.0, 0.1));
	}
	
	@FXML VBox layerConfigurationBox;
	@FXML CheckBox showAllTrajetoriesCheckBox;
	@FXML CheckBox showGraphCheckBox;
	@FXML CheckBox showPedestrianCheckBox;
	@FXML CheckBox showObstaclesCheckBox;
	@FXML CheckBox showLatticesCheckBox;
	@FXML CheckBox showOriginsCheckBox;
	@FXML CheckBox showIntermediatesCheckBox;
	@FXML CheckBox showDestinationsCheckBox;
	@FXML CheckBox showDensityEdgeCheckBox;
	@FXML CheckBox showGroupColoring;
	@FXML CheckBox showSeedColoring;
	@FXML CheckBox show3D;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}
	
	@FXML void onCheckAllTrajectories(ActionEvent actionEvent) throws Exception {
	
		if(showAllTrajetoriesCheckBox.isSelected()) {
			
			HashMap<String, TrajectoryModel> trajectories = generateTrajectories();
			coreController.getVisualizationModel().putTrajectoryShapes(trajectories);
			coreController.getVisualizationController().putTrajectoriesIntoPedestrians(trajectories);
			
			coreController.getVisualizationModel()
				.getTrajectoryShapes()
				.forEach((id,trajectoryShape) -> trajectoryShape.setVisibility(showAllTrajetoriesCheckBox.isSelected()));
		}
		else {
			
			coreController.getVisualizationModel()
				.getTrajectoryShapes()
				.forEach((id,trajectoryShape) -> trajectoryShape.clear());
			
			coreController.getVisualizationModel().getTrajectoryShapes().clear();
		}
	}
	
	@FXML void onCheckShowPedestrian(ActionEvent actionEvent) {
		
		coreController.getVisualizationModel()
			.getPedestrianShapes()
			.values()
			.forEach(pedestrianShape -> pedestrianShape.setVisibility(showPedestrianCheckBox.isSelected()));
	}
	
	@FXML void onCheckShowObstacle(ActionEvent actionEvent) {
		
		coreController.getVisualizationModel()
			.getObstacleShapes()
			.forEach(obstacleShape -> obstacleShape.setVisibility(showObstaclesCheckBox.isSelected()));
	}
	
	@FXML void onCheckShowOrigin(ActionEvent actionEvent) {
		
		coreController.getVisualizationModel()
			.getAreaShapes().values().stream().filter(area -> area.getType() == AreaType.Origin)
			.forEach(originShape -> originShape.setVisibility(showOriginsCheckBox.isSelected()));
	}
	
	@FXML void onCheckShowIntermediate(ActionEvent actionEvent) {
		
		coreController.getVisualizationModel()
			.getAreaShapes().values().stream().filter(area -> area.getType() == AreaType.Intermediate || area.getType() == AreaType.Information)
			.forEach(intermediateShape -> intermediateShape.setVisibility(showIntermediatesCheckBox.isSelected()));
	}
	
	@FXML void onCheckShowDestination(ActionEvent actionEvent) {
		
		coreController.getVisualizationModel()
			.getAreaShapes().values().stream().filter(area -> area.getType() == AreaType.Destination)
			.forEach(destinationShape -> destinationShape.setVisibility(showDestinationsCheckBox.isSelected()));
	}
	
	@FXML void onCheckShowDensityEdge(ActionEvent actionEvent) {
		
		coreController.getVisualizationModel()
		.getEdgeShapes().values()
		.forEach(edgeShape -> edgeShape.setVisibility(showPedestrianCheckBox.isSelected()));
	}
	
	@FXML void onCheckShowGroups(ActionEvent actionEvent) {
		
		if(showSeedColoring.isSelected()) {
			
			showGroupColoring.selectedProperty().set(false);
			showSeedColoring.fire();
			showGroupColoring.selectedProperty().set(true);
		}
		
		try{
			ColorGenerator.generateGroupColors(coreController.getVisualizationController().getVisualizationModel());
			
			coreController.getVisualizationModel()
				.getPedestrianShapes()
				.forEach((id, shape) -> shape.setIsGroupColored(showGroupColoring.isSelected()));
		}
		catch(Exception e){
			if(ColorGenerator.generateGroupColors(coreController.getVisualizationController().getVisualizationModel())){

				showGroupColoring.selectedProperty().set(false);
				InformationDialogCreator.createErrorDialog(null, "No Group Data", e);
				
			}
			else{
				
				InformationDialogCreator.createErrorDialog(null, "Unknown Error", e);
				showGroupColoring.selectedProperty().set(false);
			}
		}
		
	}
	
	@FXML void onCheckShowSeedColoring(ActionEvent actionEvent) {
		
		if(showGroupColoring.isSelected()) {
			
			showSeedColoring.selectedProperty().set(false);
			showGroupColoring.fire();
			showSeedColoring.selectedProperty().set(true);
		}
		try {
			ColorGenerator.generateSeedColors(coreController.getVisualizationController().getVisualizationModel());
		
			coreController.getVisualizationModel()
				.getPedestrianShapes()
				.forEach((id, shape) -> shape.setIsSeedColored(showSeedColoring.isSelected()));
		}
		catch(Exception e) {
			if(ColorGenerator.generateSeedColors(coreController.getVisualizationController().getVisualizationModel())){

				showGroupColoring.selectedProperty().set(false);
				InformationDialogCreator.createErrorDialog(null, "No Seed Group Data", e);
			}
			else{
				
				InformationDialogCreator.createErrorDialog(null, "Unknown Error", e);
				showGroupColoring.selectedProperty().set(false);
			}
		}
	}
	
	public void updateTrajectories() throws Exception{
		
		if(coreController.getLayerConfigurationController().showAllTrajetoriesCheckBox.isSelected()) {
			
		coreController.getVisualizationModel()
			.getTrajectoryShapes()
			.forEach((id,trajectoryShape) -> trajectoryShape.clear());	
		coreController.getVisualizationModel().getTrajectoryShapes().clear();
		
		HashMap<String, TrajectoryModel> trajectories = generateTrajectories();
		coreController.getVisualizationModel().putTrajectoryShapes(trajectories);
		coreController.getVisualizationController().putTrajectoriesIntoPedestrians(trajectories);
		
		}
	}
	
	public HashMap<String, TrajectoryModel> generateTrajectories() throws Exception{
		
		HashMap<String, TrajectoryModel> trajectories = new HashMap<String, TrajectoryModel>();
		
		double starttime = coreController.getInteractionViewController()
								.roundTimelineValue(coreController.getVisualizationController().getCustomizationController()
								.getCustomizationModel().trajectoryTimeIntervalProperty().getValue()*100);

		double actualtime =  coreController.getInteractionViewController()
								.roundTimelineValue(coreController.getInteractionViewController().getTimeLineBindingValue());
	
		double interval = 0.0;
		
		IdExtension createID = new IdExtension();
		
		for(SimulationOutputReader simReader : coreController.getSimulationOutputReaderListOfType(CsvType.Pedestrian)) {
			
			while(actualtime-starttime<0) {
				
				starttime -= simReader.getTimeStepDifference();
			}
	
			interval = actualtime - starttime;			

			while(interval<=actualtime) {
				
				SimulationOutputCluster dataStepCurrent = simReader.readDataSet(interval);
				
				if (dataStepCurrent != null && !dataStepCurrent.isEmpty()) {
					for (String identification : dataStepCurrent.getIdentifications()) {
						
						String hashId = createID.createUniqueId(identification, simReader.getFilePathHash());
						if (!trajectories.containsKey(hashId)) {
	
							trajectories.put(hashId,
									new TrajectoryModel(hashId,
											coreController.getVisualizationController().getCustomizationController(),
											dataStepCurrent.getDoubleData(identification, OutputType.x.name()),
											dataStepCurrent.getDoubleData(identification, OutputType.y.name()),
											coreController.getCoreModel().getResolution()));
						}
						else {
	
							trajectories.get(hashId).append(
									dataStepCurrent.getDoubleData(identification, OutputType.x.name()),
									dataStepCurrent.getDoubleData(identification, OutputType.y.name()),
									coreController.getCoreModel().getResolution());
						}
					}
					simReader.clearBuffer(interval);
				}
				interval += simReader.getTimeStepDifference();
			}
		}
		return trajectories;
	}
		
	public void resetCheckBox() {
		showAllTrajetoriesCheckBox.selectedProperty().set(false);
		showGroupColoring.selectedProperty().set(false);
		showSeedColoring.selectedProperty().set(false);
	}
	
	public boolean getCheckSeedColoured() {
		return showSeedColoring.isSelected();
	}
	
	public boolean getCheckGroupColoured() {
		return showGroupColoring.isSelected();
	}
	
	}


