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
import tum.cms.sim.momentum.visualization.utility.ColorGenerator;
import tum.cms.sim.momentum.visualization.utility.IdExtension;

public class LayerConfigurationController implements Initializable {
	
	private CoreController coreController;
	private HashMap<String, TrajectoryModel> trajectories = new HashMap<String, TrajectoryModel>();


	public void bindCoreModel(CoreController coreController) {
		
		this.coreController = coreController;
		this.layerConfigurationBox.disableProperty().bind(coreController.getCoreModel().layoutLoadedProperty().not());
		
		Bindings.bindBidirectional(showLatticesCheckBox.selectedProperty(),
				coreController.getPlaybackController().getVisibilitiyModel().latticeVisibilityProperty());
		
		Bindings.bindBidirectional(showGraphCheckBox.selectedProperty(),
				coreController.getPlaybackController().getVisibilitiyModel().graphVisibilityProperty());
		
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
	@FXML CheckBox showTaggedAreasCheckBox;
	@FXML CheckBox showDensityEdgeCheckBox;
	@FXML CheckBox showGroupColoring;
	@FXML CheckBox showSeedColoring;
	@FXML CheckBox show3D;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}
	
	@FXML void onCheckAllTrajectories(ActionEvent actionEvent) throws Exception {
		
		if(coreController.getPlaybackController().getPlaybackModel().getTrajectoryShapes().size() == 0) {
			
			HashMap<String, TrajectoryModel> trajectories = loadTrajectories();
			coreController.getPlaybackController().putTrajectoriesIntoPedestrians(trajectories);
			coreController.getPlaybackController().getPlaybackModel().putTrajectoryShapes(trajectories);
		}
		
		coreController.getPlaybackController().getPlaybackModel()
			.getTrajectoryShapes()
			.forEach((id,trajectoryShape) -> trajectoryShape.setVisibility(showAllTrajetoriesCheckBox.isSelected()));
	}
	
	@FXML void onCheckShowPedestrian(ActionEvent actionEvent) {
		
		coreController.getPlaybackController().getPlaybackModel()
			.getPedestrianShapes()
			.values()
			.forEach(pedestrianShape -> pedestrianShape.setVisibility(showPedestrianCheckBox.isSelected()));
	}
	
	@FXML void onCheckShowObstacle(ActionEvent actionEvent) {
		
		coreController.getPlaybackController().getPlaybackModel()
			.getObstacleShapes()
			.forEach(obstacleShape -> obstacleShape.setVisibility(showObstaclesCheckBox.isSelected()));
	}
	
	@FXML void onCheckShowOrigin(ActionEvent actionEvent) {
		
		coreController.getPlaybackController().getPlaybackModel()
			.getAreaShapes().values().stream().filter(area -> area.getType() == AreaType.Origin)
			.forEach(originShape -> originShape.setVisibility(showOriginsCheckBox.isSelected()));
	}
	
	@FXML void onCheckShowIntermediate(ActionEvent actionEvent) {
		
		coreController.getPlaybackController().getPlaybackModel()
			.getAreaShapes().values().stream().filter(area -> area.getType() == AreaType.Intermediate || area.getType() == AreaType.Information)
			.forEach(intermediateShape -> intermediateShape.setVisibility(showIntermediatesCheckBox.isSelected()));
	}
	
	@FXML void onCheckShowDestination(ActionEvent actionEvent) {
		
		coreController.getPlaybackController().getPlaybackModel()
			.getAreaShapes().values().stream().filter(area -> area.getType() == AreaType.Destination)
			.forEach(destinationShape -> destinationShape.setVisibility(showDestinationsCheckBox.isSelected()));
	}

	@FXML void onCheckShowTaggedArea(ActionEvent actionEvent) {

		coreController.getPlaybackController().getPlaybackModel()
				.getTaggedAreaShapes().values()
				.forEach(taggedAreaShape -> taggedAreaShape.setVisibility(showTaggedAreasCheckBox.isSelected()));
	}
	
	@FXML void onCheckShowDensityEdge(ActionEvent actionEvent) {
		
		coreController.getPlaybackController().getPlaybackModel()
		.getEdgeShapes().values()
		.forEach(edgeShape -> edgeShape.setVisibility(showPedestrianCheckBox.isSelected()));
	}
	
	@FXML void onCheckShowGroups(ActionEvent actionEvent) {
		
		ColorGenerator.generateGroupColors(coreController.getPlaybackController().getPlaybackModel());
		coreController.getPlaybackController().getPlaybackModel()
			.getPedestrianShapes().forEach((id, shape) -> shape.setIsGroupColored(!shape.getIsGroupColored()));
	}
	
	@FXML void onCheckShowSeedColoring(ActionEvent actionEvent) {
		
		ColorGenerator.generateSeedColors(coreController.getPlaybackController().getPlaybackModel());
		coreController.getPlaybackController()
			.getPlaybackModel()
			.getPedestrianShapes().forEach((id, shape) -> shape.setIsSeedColored(!shape.getIsSeedColored()));
	}
	
	public HashMap<String, TrajectoryModel> loadTrajectories() throws Exception {

		double timeStep = 0.0;
		IdExtension createID = new IdExtension();
		
		for(SimulationOutputReader simReader : coreController.getSimulationOutputReaderListOfType(CsvType.Pedestrian)) {
			
			while (timeStep < simReader.getEndCluster() * simReader.getTimeStepDifference()) {
	
				SimulationOutputCluster dataStepCurrent = simReader.readDataSet(timeStep);
	
				if (dataStepCurrent != null && !dataStepCurrent.isEmpty()) {
	
					for (String identification : dataStepCurrent.getIdentifications()) {
	
						String hashId = createID.createUniqueId(identification, simReader.getFilePathHash());
						if (!trajectories.containsKey(hashId)) {
	
							trajectories.put(hashId,
									new TrajectoryModel(hashId,
											coreController.getPlaybackController().getCustomizationController(),
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
	
					simReader.clearBuffer(timeStep);
				}
	
				timeStep += simReader.getTimeStepDifference();
			}
		}
		
		return trajectories;
	}
	
	public void resetTrajectories() {
		trajectories.clear();
	}
}
