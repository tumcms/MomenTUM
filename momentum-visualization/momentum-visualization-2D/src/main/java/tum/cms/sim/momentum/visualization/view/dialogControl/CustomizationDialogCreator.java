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

package tum.cms.sim.momentum.visualization.view.dialogControl;

import java.io.File;
import java.io.IOException;
import org.apache.commons.math3.util.FastMath;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import tum.cms.sim.momentum.visualization.controller.CoreController;
import tum.cms.sim.momentum.visualization.controller.PlaybackController;
import tum.cms.sim.momentum.visualization.handler.SnapshotHandler;
import tum.cms.sim.momentum.visualization.model.CustomizationModel;
import tum.cms.sim.momentum.visualization.model.SnapshotModel;

public class CustomizationDialogCreator {

	private static String colorDialogTitle = "Simulation Color Customize";
	private static String colorDialogHeader = "Change the apperance of the output elements.";
	private static String colorDialogSelectedColor = "Selected Object";
	private static String colorDialogAxisColor = "Axis";
	private static String colorDialogLatticeColor = "Lattice";
	private static String colorDialogGraphColor = "Graph";
	private static String colorDialogVertexSize = "Vertex Size";
	private static String colorDialogEdgeThickness = "Edge Thickness";
	private static String colorDialogPedestrianBody = "Pedestrian Body";
	private static String colorDialogPedestrianDirection = "Pedestrian Direction";
	private static String colorDialogTrajectoryColor = "Trajectory Color";
	private static String colorDialogTrajectoryRandom = "Random Color";
	private static String colorDialogTrajectoryThickness = "Trajectory Thickness";
	private static String colorDialogDestinationColor = "Destination";
	private static String colorDialogOriginColor = "Origin";
	private static String colorDialogIntermediateColor = "Intermediate";
	private static String colorDialogInformationColor = "Information";
	private static String colorDialogObstacleColor = "Obstacle";
	private static String colorDialogVirtualObstacleColor = "Virtual Obstacle";
	private static String colorDialogResetColor = "Reset Colors";
	private static String snapshotDialogTitle = "Snapshot Preferences";
	private static String snapshotDialogHeader = "Snapshot Preferences";
	private static String snapshotDialogPath = "Snapshot path:";
	private static String snapshotDialogName = "Snapshot name:";
	private static String snapshotDialogScale = "Scaling factor:";
	private static String trajectoryTimeInterval = "Trajectory Visibility Interval:";
	
	public static void createColorDialog(PlaybackController playbackController) {
		
		CustomizationModel customizationModel = playbackController.getCustomizationController().getCustomizationModel();
		Double endTimeSteps = playbackController.getCoreController().getInteractionViewController().getTimeLineModel().getEndTime() /
				playbackController.getCoreController().getInteractionViewController().getTimeLineModel().getTimeStepDuration();
		
		Dialog<Void> dialog = new Dialog<Void>();
		dialog.initModality(Modality.WINDOW_MODAL);
    	dialog.setTitle(colorDialogTitle);
    	dialog.setHeaderText(colorDialogHeader);
    	ButtonType loginButtonType = new ButtonType("Done", ButtonData.OK_DONE);
    	
    	dialog.getDialogPane().getButtonTypes().addAll(loginButtonType);
    	
    	GridPane grid = new GridPane();
    	grid.setHgap(10);
    	grid.setVgap(10);
    	grid.setPadding(new Insets(20, 150, 10, 10));
    	int rowIndex = 0;
    	
    	EventHandler<ActionEvent> enterPressed = new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				
				grid.requestFocus();
			}
    	};

    	Label textItem = new Label();
    	textItem.setText(colorDialogSelectedColor);
    	ColorPicker colorPickerSelected = new ColorPicker();
    	colorPickerSelected.setValue((Color) customizationModel.getSelectedColor());
    	colorPickerSelected.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent arg0) {
			
				customizationModel.setSelectedColor(colorPickerSelected.getValue());
				customizationModel.setSelectedPedestrianBodyMaterial(new PhongMaterial(colorPickerSelected.getValue()));
			}
		});
    	
    	grid.add(textItem, 0, rowIndex);
    	grid.add(colorPickerSelected, 1, rowIndex);
    	rowIndex++;
  
     	textItem = new Label();
    	textItem.setText(colorDialogAxisColor);
    	ColorPicker colorPickerAxis = new ColorPicker();
    	colorPickerAxis.setValue(customizationModel.getAxisColor());
    	colorPickerAxis.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent arg0) {
			
				customizationModel.setAxisColor(colorPickerAxis.getValue());
			}
		});
    	
    	grid.add(textItem, 0, rowIndex);
    	grid.add(colorPickerAxis, 1, rowIndex);
    	rowIndex++;
      	
    	textItem = new Label();
    	textItem.setText(colorDialogLatticeColor);
    	ColorPicker colorPickerLattice = new ColorPicker();
    	colorPickerLattice.setValue(customizationModel.getLatticeColor());
    	colorPickerLattice.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent arg0) {
			
				customizationModel.setLatticeColor(colorPickerLattice.getValue());
			}
		});
 	
    	grid.add(textItem, 0, rowIndex);
    	grid.add(colorPickerLattice, 1, rowIndex);
    	rowIndex++;
    	
    	textItem = new Label();
    	textItem.setText(colorDialogGraphColor);
    	ColorPicker colorPickerGraph = new ColorPicker();
    	colorPickerGraph.setValue(customizationModel.getGraphColor());
    	colorPickerGraph.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent arg0) {
			
				customizationModel.setGraphColor(colorPickerGraph.getValue());
			}
    	});
      	
    	grid.add(textItem, 0, rowIndex);
    	grid.add(colorPickerGraph, 1, rowIndex);
    	rowIndex++;
    	
    	textItem = new Label();
    	textItem.setText(colorDialogEdgeThickness);
    	
    	TextField edgeThicknessTextField = new TextField();
    	edgeThicknessTextField.setPrefWidth(130);
    	edgeThicknessTextField.setText(String.valueOf(customizationModel.getEdgeThickness()));
    	
    	edgeThicknessTextField.setOnAction(enterPressed);
    	edgeThicknessTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean focusLost, Boolean focusGained) {

				if (focusLost) {
					if (edgeThicknessTextField.getText().isEmpty()) {
						InformationDialogCreator.createErrorDialog("Customization", "The edge thickness must be a valid number");
						edgeThicknessTextField.textProperty().set(String.valueOf(customizationModel.getEdgeThickness()));
						return;
					}
					try {
						customizationModel.setEdgeThickness(Double.parseDouble(edgeThicknessTextField.getText()));
					}
					catch (Exception e) {
						edgeThicknessTextField.textProperty().set(String.valueOf(customizationModel.getEdgeThickness()));
						InformationDialogCreator.createErrorDialog("Customization", "The edge thickness must be a valid number");
					}
				}
				
			}
		});
    	
    	grid.add(textItem, 0, rowIndex);
    	grid.add(edgeThicknessTextField, 1, rowIndex);
    	
    	textItem = new Label();
    	textItem.setText(colorDialogVertexSize);
    	
    	TextField vertexSizeTextField = new TextField();
    	vertexSizeTextField.setPrefWidth(130);
    	vertexSizeTextField.setText(String.valueOf(customizationModel.getVertexSize()));
    	
    	vertexSizeTextField.setOnAction(enterPressed);
    	vertexSizeTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean focusLost, Boolean focusGained) {

				if (focusLost) {
					if (vertexSizeTextField.getText().isEmpty()) {
						InformationDialogCreator.createErrorDialog("Customization", "The vertex size must be a valid number");
						vertexSizeTextField.textProperty().set(String.valueOf(customizationModel.getVertexSize()));
						return;
					}
					try {
						customizationModel.setVertexSize(Double.parseDouble(vertexSizeTextField.getText()));
					}
					catch (Exception e) {
						vertexSizeTextField.textProperty().set(String.valueOf(customizationModel.getVertexSize()));
						InformationDialogCreator.createErrorDialog("Customization", "The vertex size must be a valid number");
					}
				}
				
			}
		});
    	
    	grid.add(textItem, 2, rowIndex);
    	grid.add(vertexSizeTextField, 3, rowIndex);
    	rowIndex++;
    	
    	textItem = new Label();
    	textItem.setText(colorDialogPedestrianBody);
    	ColorPicker colorPickerPedestrianBody = new ColorPicker();
    	colorPickerPedestrianBody.setValue(customizationModel.getPedestrianBodyMaterial().getDiffuseColor());
    	colorPickerPedestrianBody.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent arg0) {
				
				PhongMaterial phongMaterial = new PhongMaterial(colorPickerPedestrianBody.getValue());
				phongMaterial.setSpecularColor(Color.GRAY);
				phongMaterial.setSpecularPower(100.0);
				customizationModel.setPedestrianBodyMaterial(phongMaterial);
			}
    	});
	
    	grid.add(textItem, 0, rowIndex);
    	grid.add(colorPickerPedestrianBody, 1, rowIndex);
    	rowIndex++;
    	
    	textItem = new Label();
    	textItem.setText(colorDialogPedestrianDirection);
    	ColorPicker colorPickerPedestrianDirection = new ColorPicker();
    	colorPickerPedestrianDirection.setValue(customizationModel.getPedestrianDirectionColor());
    	colorPickerPedestrianDirection.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent arg0) {
			
				customizationModel.setPedestrianDirectionColor(colorPickerPedestrianDirection.getValue());
			}
    	});

    	grid.add(textItem, 0, rowIndex);
    	grid.add(colorPickerPedestrianDirection, 1, rowIndex);
    	rowIndex++;
    	
    	textItem = new Label();
    	textItem.setText(colorDialogDestinationColor);
    	ColorPicker colorPickerDestination = new ColorPicker();
    	colorPickerDestination.setValue((Color) customizationModel.getDestinationColor());
    	colorPickerDestination.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent arg0) {
			
				customizationModel.setDestinationColor(colorPickerDestination.getValue());
			}
    	});
	
    	grid.add(textItem, 0, rowIndex);
    	grid.add(colorPickerDestination, 1, rowIndex);
    	rowIndex++;
    	
    	textItem = new Label();
    	textItem.setText(colorDialogTrajectoryColor);
    	ColorPicker colorPickerTrajectory = new ColorPicker();
    	colorPickerTrajectory.setDisable(customizationModel.isTrajectoryRandomColor());
    	colorPickerTrajectory.setValue((Color) customizationModel.getTrajectoryColor());
    	colorPickerTrajectory.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {

				customizationModel.setTrajectoryColor(colorPickerTrajectory.getValue());
				
			}
    		
    	});
    	
    	grid.add(textItem, 0, rowIndex);
    	grid.add(colorPickerTrajectory, 1, rowIndex);
    	
    	textItem = new Label();
    	textItem.setText(colorDialogTrajectoryRandom);
    	CheckBox randomTrajectoryColorCheckBox = new CheckBox();
    	randomTrajectoryColorCheckBox.setSelected(customizationModel.isTrajectoryRandomColor());
    	randomTrajectoryColorCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldValue, Boolean newValue) {

				customizationModel.setTrajectoryIsRandomColor(newValue);
				colorPickerTrajectory.setDisable(customizationModel.isTrajectoryRandomColor());

				if (randomTrajectoryColorCheckBox.selectedProperty().get()) {
					
					playbackController.getPlaybackModel().getTrajectoryShapes().forEach((id,trajectoryShape) -> {
						trajectoryShape.getStrokeProperty().unbind();
						trajectoryShape.setColor(new Color(
								FastMath.random() % 0.5 + 0.25,
								FastMath.random() % 0.5 + 0.25,
								FastMath.random() % 0.5 + 0.25,
								1.0));
					});
				}
				
				else {
					playbackController.getPlaybackModel().getTrajectoryShapes().forEach((id, trajectoryShape) -> {
						trajectoryShape.getStrokeProperty().bind(customizationModel.trajectoryColorProperty());
					});
				}
				
				
			}
    		
    	});
    	
    	grid.add(textItem, 2, rowIndex);
    	grid.add(randomTrajectoryColorCheckBox, 3, rowIndex);
    	rowIndex++;
    	
    	textItem = new Label();
    	textItem.setText(colorDialogTrajectoryThickness);
    	TextField trajectoryThicknessTextField = new TextField();
    	trajectoryThicknessTextField.setPrefWidth(130);
    	trajectoryThicknessTextField.setText(String.valueOf(customizationModel.getTrajectoryThickness()));
    	
    	trajectoryThicknessTextField.setOnAction(enterPressed);
    	trajectoryThicknessTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean focusLost, Boolean focusGained) {

				if (focusLost) {
					if (trajectoryThicknessTextField.getText().isEmpty()) {
						InformationDialogCreator.createErrorDialog("Customization", "The trajectory thickness must be a valid number");
						trajectoryThicknessTextField.textProperty().set(String.valueOf(customizationModel.getTrajectoryThickness()));
						return;
					}
					try {
						customizationModel.setTrajectoryThickness(Double.parseDouble(trajectoryThicknessTextField.getText()));
					}
					catch (Exception e) {
						trajectoryThicknessTextField.textProperty().set(String.valueOf(customizationModel.getTrajectoryThickness()));
						InformationDialogCreator.createErrorDialog("Customization", "The trajectory thickness must be a valid number");
					}
				}
				
			}
		});
    	grid.add(textItem, 0, rowIndex);
    	grid.add(trajectoryThicknessTextField, 1, rowIndex);
 
    	
    	textItem = new Label();
    	textItem.setText(trajectoryTimeInterval);
    	TextField trajectoryTimeIntervalTextField = new TextField();
    	trajectoryTimeIntervalTextField.setPrefWidth(130);
    	trajectoryTimeIntervalTextField.setText(String.valueOf(customizationModel.getTrajectoryTimeInterval()));
    	trajectoryTimeIntervalTextField.setOnAction(enterPressed);
    	trajectoryTimeIntervalTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean focusLost, Boolean focusGained) {

				if (focusLost) {
					
					if(trajectoryTimeIntervalTextField.getText().isEmpty()) {
						
						InformationDialogCreator.createErrorDialog("Customization", "The trajectory time interval has to be a valid number between 0.0 and "+ endTimeSteps.toString());
						trajectoryTimeIntervalTextField.textProperty().set(String.valueOf(customizationModel.getTrajectoryTimeInterval()));
						return;
					}
					
					try {
						
						if(Double.parseDouble(trajectoryTimeIntervalTextField.getText()) > endTimeSteps) {
							
							trajectoryTimeIntervalTextField.setText(endTimeSteps.toString());
						}
						
						customizationModel.setTrajectoryTimeInterval(Double.parseDouble(trajectoryTimeIntervalTextField.getText()));
						playbackController.getCoreController().getLayerConfigurationController().updateTrajectories();
					}
					catch (Exception numberForException) {
						
						InformationDialogCreator.createErrorDialog("Customization", "The trajectory time interval has to be a valid number between 0.0 and "+ endTimeSteps.toString());
						trajectoryTimeIntervalTextField.textProperty().set(String.valueOf(customizationModel.getTrajectoryTimeInterval()));
					}
				}
			}
		});
    	
    	grid.add(textItem, 2, rowIndex);
    	grid.add(trajectoryTimeIntervalTextField, 3, rowIndex);
    	rowIndex++;
    	
       	textItem = new Label();
    	textItem.setText(colorDialogOriginColor);
    	ColorPicker colorPickerOrigin = new ColorPicker();
    	colorPickerOrigin.setValue((Color) customizationModel.getOriginColor());
    	colorPickerOrigin.setOnAction(new EventHandler<ActionEvent>() {
		
			@Override
			public void handle(ActionEvent arg0) {
			
				customizationModel.setOriginColor(colorPickerOrigin.getValue());
			}
    	});
    	
    	grid.add(textItem, 0, rowIndex);
    	grid.add(colorPickerOrigin, 1, rowIndex);
    	rowIndex++;
    	
       	textItem = new Label();
    	textItem.setText(colorDialogIntermediateColor);
    	ColorPicker colorPickerIntermediate = new ColorPicker();
    	colorPickerIntermediate.setValue(customizationModel.getIntermediateColor());
    	colorPickerIntermediate.setOnAction(new EventHandler<ActionEvent>() {
    		
			@Override
			public void handle(ActionEvent arg0) {
			
				customizationModel.setIntermediateColor(colorPickerIntermediate.getValue());
			}
    	});
    	
    	grid.add(textItem, 0, rowIndex);
    	grid.add(colorPickerIntermediate, 1, rowIndex);
    	rowIndex++;
    	
    	textItem = new Label();
    	textItem.setText(colorDialogInformationColor);
    	ColorPicker colorPickerInformation = new ColorPicker();
    	colorPickerInformation.setValue(customizationModel.getInformationColor());
    	colorPickerInformation.setOnAction(new EventHandler<ActionEvent>() {
    		
			@Override
			public void handle(ActionEvent arg0) {
			
				customizationModel.setInformationColor(colorPickerInformation.getValue());
			}
    	});
    	
    	grid.add(textItem, 0, rowIndex);
    	grid.add(colorPickerInformation, 1, rowIndex);
    	rowIndex++;
    	
     	textItem = new Label();
    	textItem.setText(colorDialogObstacleColor);
    	ColorPicker colorPickerObstacle = new ColorPicker();
    	colorPickerObstacle.setValue(customizationModel.getObstacleColor());
    	colorPickerObstacle.setOnAction(new EventHandler<ActionEvent>() {
    		
    			@Override
    			public void handle(ActionEvent arg0) {
    			
    				customizationModel.setObstacleColor(colorPickerObstacle.getValue());
    			}
        	});
    	
    	grid.add(textItem, 0, rowIndex);
    	grid.add(colorPickerObstacle, 1, rowIndex);
    	rowIndex++;
    	
     	textItem = new Label();
    	textItem.setText(colorDialogVirtualObstacleColor);
    	ColorPicker colorPickerVirtualObstacle = new ColorPicker();
    	colorPickerVirtualObstacle.setValue(customizationModel.getVirtualObstacleColor());
    	colorPickerVirtualObstacle.setOnAction(new EventHandler<ActionEvent>() {
    		
    			@Override
    			public void handle(ActionEvent arg0) {
    			
    				customizationModel.setVirtualObstacleColor(colorPickerVirtualObstacle.getValue());
    			}
        	});
    	
    	grid.add(textItem, 0, rowIndex);
    	grid.add(colorPickerVirtualObstacle, 1, rowIndex);
    	rowIndex++;
    	
    	Button resetColors = new Button(colorDialogResetColor);
    	resetColors.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent arg0) {
				
				playbackController.getCustomizationController().resetCustomizedValues();
				
		    	colorPickerSelected.setValue((Color) customizationModel.getSelectedColor());
				colorPickerAxis.setValue(customizationModel.getAxisColor());

		    	colorPickerGraph.setValue(customizationModel.getGraphColor());
		    	
		    	colorPickerPedestrianBody.setValue(customizationModel.getPedestrianBodyMaterial().getDiffuseColor());
		    	colorPickerPedestrianDirection.setValue(customizationModel.getPedestrianDirectionColor());
		    	
		    	colorPickerTrajectory.setValue((Color) customizationModel.getTrajectoryColor());
		    	randomTrajectoryColorCheckBox.setSelected(customizationModel.isTrajectoryRandomColor());
		    	trajectoryThicknessTextField.setText(String.valueOf(customizationModel.getTrajectoryThickness()));
		    	trajectoryTimeIntervalTextField.setText(String.valueOf(customizationModel.getTrajectoryTimeInterval()));
		    	
		    	colorPickerDestination.setValue(customizationModel.getDestinationColor());
		    	colorPickerOrigin.setValue(customizationModel.getOriginColor());
				colorPickerIntermediate.setValue(customizationModel.getIntermediateColor());
				colorPickerObstacle.setValue(customizationModel.getObstacleColor());
				
				edgeThicknessTextField.setText(String.valueOf(customizationModel.getEdgeThickness()));
				vertexSizeTextField.setText(String.valueOf(customizationModel.getVertexSize()));
			}
		});
    	
     	grid.add(resetColors, 1, rowIndex);
     	rowIndex++;
    	
    	dialog.getDialogPane().setContent(grid);
    	dialog.showAndWait();
	}
	
	public static void createSnapshotDialog(SnapshotModel snapshotModel, CoreController coreController) {
		
		Dialog<Void> dialog = new Dialog<Void>();
		dialog.initModality(Modality.WINDOW_MODAL);
    	dialog.setTitle(snapshotDialogTitle);
    	dialog.setHeaderText(snapshotDialogHeader);
    	
    	ButtonType doneButtonType = new ButtonType("Done", ButtonData.OK_DONE);    	
    	dialog.getDialogPane().getButtonTypes().addAll(doneButtonType);
    	
    	
    	GridPane grid = new GridPane();
    	grid.setHgap(10);
    	grid.setVgap(10);
    	grid.setPadding(new Insets(20, 150, 10, 10));
    	
    	EventHandler<ActionEvent> enterPressed = new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				
				grid.requestFocus();
				
			}
    		
    	};
    	
    	Label snapshotPathLabel = new Label();
    	snapshotPathLabel.setText(snapshotDialogPath);
    	Label snapshotNameLabel = new Label();
    	snapshotNameLabel.setText(snapshotDialogName);
    	DirectoryChooser snapshotPathFileChooser = new DirectoryChooser();
    	
    	File snapshotFilePath = snapshotModel.getSnapshotPath();
    	String snapshotFileName = snapshotModel.getSnapshotName();
    	
    	//Text field for file path
    	TextField snapshotPathTextField = new TextField ();
    	snapshotPathTextField.setPrefSize(400, 10);
    	snapshotPathTextField.setText(snapshotFilePath.getAbsolutePath());

    	snapshotPathTextField.setOnAction(enterPressed);
    	snapshotPathTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean focusLost, Boolean focusGained) {

				if (focusLost) {
					File newPath = new File(snapshotPathTextField.getText());
					if (newPath.isDirectory()) {
						snapshotModel.setSnapshotPath(newPath);
						snapshotPathFileChooser.setInitialDirectory(newPath);
					}
					else {
						snapshotPathTextField.textProperty().set(snapshotModel.getSnapshotPath().getAbsolutePath());
						InformationDialogCreator.createErrorDialog("Path does not exist", "Please enter an eisting path");
					}
					
				}
				
			}
		});
    	
    	//Text field for file name
    	TextField snapshotNameTextField = new TextField();
    	snapshotNameTextField.setPrefSize(400, 10);
    	snapshotNameTextField.setText(snapshotFileName);
    	
    	snapshotNameTextField.setOnAction(enterPressed);
    	snapshotNameTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean focusLost, Boolean arg2) {

				if (focusLost) {
					String newFileName = snapshotNameTextField.textProperty().get().replaceAll("[^a-zA-Z0-9.-]", "_");
					if (newFileName.isEmpty()) {
						InformationDialogCreator.createErrorDialog("Snapshot", "Please define a file name");
						snapshotNameTextField.textProperty().set(snapshotModel.getSnapshotName());
						return;
					}
					snapshotModel.setSnapshotName(newFileName);
				}
				
			}
    		
    	});
    	
    	//File path chooser
    	
    	snapshotPathFileChooser.setTitle("Choose Snapshot Path");   
    	snapshotPathFileChooser.setInitialDirectory(snapshotModel.getSnapshotPath());

    	Button choosePathButton = new Button("Choose path");

    	choosePathButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {

				File chosenDirectory = snapshotPathFileChooser.showDialog(dialog.getOwner());
				
				if (chosenDirectory != null) {
					snapshotModel.setSnapshotPath(chosenDirectory);
					snapshotPathTextField.setText(chosenDirectory.getAbsolutePath());
					snapshotPathFileChooser.setInitialDirectory(chosenDirectory);
				}
			}
    	});
    	
    	//Pixel scale
    	Label pixelScaleLabel = new Label();
    	pixelScaleLabel.setText(snapshotDialogScale);
    	
    	TextField pixelScaleTextField = new TextField ();
    	pixelScaleTextField.setPrefSize(40, 10);
    	pixelScaleTextField.setText(snapshotModel.getPixelScale().toString());
    	
    	pixelScaleTextField.setOnAction(enterPressed);
    	pixelScaleTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean focusLost, Boolean focusGained) {

				if (focusLost) {
					
					if (pixelScaleTextField.getText().isEmpty()) {
						InformationDialogCreator.createErrorDialog("Snapshot", "The scaling factor must be a valid number");
						pixelScaleTextField.textProperty().set(snapshotModel.getPixelScale().toString());
						return;
					}
					
					try {
						
						snapshotModel.setPixelScale(Double.parseDouble(pixelScaleTextField.getText()));
					}
					catch (Exception e) {
						
						pixelScaleTextField.textProperty().set(snapshotModel.getPixelScale().toString());
						InformationDialogCreator.createErrorDialog("Snapshot", "The scaling factor must be a valid number");
					}
				}
			}
		});

    	Button takeSnapshotButton = new Button("Take snapshot");
    	
    	takeSnapshotButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {

				SnapshotHandler snapshotHandler = new SnapshotHandler(coreController.getPlaybackController().getPlayBackCanvas(),
		    			snapshotModel.getPixelScale(),
		    			snapshotModel.getAbsoluteSnapshotPath().getAbsolutePath());
		    	
		    	try {
		    		
					snapshotHandler.snapshot();
				} 
		    	catch (IOException e) {
			
					e.printStackTrace();
				}
			}
    	});
    	
    	grid.add(snapshotPathLabel,0,0);
    	grid.add(snapshotPathTextField, 1, 0);
    	grid.add(choosePathButton, 2, 0);
    	grid.add(snapshotNameLabel, 0, 1);
    	grid.add(snapshotNameTextField, 1, 1);
    	grid.add(pixelScaleLabel, 0, 2);
    	grid.add(pixelScaleTextField, 1, 2);
    	grid.add(takeSnapshotButton, 1, 3);
    	
    	dialog.getDialogPane().setContent(grid);
    	dialog.showAndWait();
	}
}
