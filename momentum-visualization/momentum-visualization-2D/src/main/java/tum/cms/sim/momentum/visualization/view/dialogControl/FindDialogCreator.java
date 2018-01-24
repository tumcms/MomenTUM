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


import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import tum.cms.sim.momentum.visualization.controller.CoreController;
import tum.cms.sim.momentum.visualization.handler.SelectionHandler;
import tum.cms.sim.momentum.visualization.model.DetailsModel;
import tum.cms.sim.momentum.visualization.model.geometry.ShapeModel;

public class FindDialogCreator {
	
	private static String findDialogTitle = "MomenTumV2";
	private static String findDialogHeader = "Find";
	private static String findEntityLabelText = "Entity:";
	private static String idLabelText = "ID:";
	
	private static ShapeModel selectedShape = null;
	private static DetailsModel details = null;
	
	private enum entityType {
		
		Area,
		Edge,
		Pedestrian,
		Vertex
	}
	
	public static void createFindDialog(CoreController coreController, SelectionHandler selectionHandler) {
		
		Dialog<Void> dialog = new Dialog<Void>();
		dialog.initModality(Modality.WINDOW_MODAL);
    	dialog.setTitle(findDialogTitle);
    	dialog.setHeaderText(findDialogHeader);
    	
    	ButtonType doneButtonType = new ButtonType("Done", ButtonData.OK_DONE);    	
    	dialog.getDialogPane().getButtonTypes().addAll(doneButtonType);
    	
    	Label findEntityLabel = new Label();
    	findEntityLabel.setText(findEntityLabelText);
    	Label idLabel = new Label();
    	idLabel.setText(idLabelText);
    	
    	ComboBox<entityType> entityTypeChooser = new ComboBox<entityType>();    
    	ComboBox<String> idChooser = new ComboBox<String>();
    	
    	entityTypeChooser.setPrefWidth(140);
    	idChooser.setPrefWidth(140);
    	entityTypeChooser.getItems().setAll(FXCollections.observableArrayList(entityType.values()));
    	idChooser.disableProperty().set(true);
    	
    	entityTypeChooser.valueProperty().addListener(new ChangeListener<entityType>() {

			@Override
			public void changed(ObservableValue<? extends entityType> observable, entityType oldValue,
					entityType newValue) {
				
				idChooser.disableProperty().set(true);

				switch(newValue) {
				case Area:
					idChooser.getItems().clear();
					idChooser.getItems().setAll(coreController.getPlaybackController().getPlaybackModel().getAreaShapes().keySet());
					Collections.sort(idChooser.getItems(), new NumericStringComparator());
					break;
					
				case Edge:
					idChooser.getItems().clear();
					idChooser.getItems().setAll(coreController.getPlaybackController().getPlaybackModel().getEdgeShapes().keySet());
					//TODO: sort list items
					break;
					
				case Pedestrian:
					idChooser.getItems().clear();
					List<String> pedestrianDisplayIds = coreController.getPlaybackController().getPlaybackModel().getPedestrianShapes()
                            .values().stream()
                            .map(pedestrian -> pedestrian.getDisplayId())
                            .collect(Collectors.toList());
	
					idChooser.getItems().setAll(pedestrianDisplayIds);
					Collections.sort(idChooser.getItems(), new NumericStringComparator());
					
					break;
					
				case Vertex:
					idChooser.getItems().clear();
					coreController.getPlaybackController().getPlaybackModel().getVertexShapes().keySet().forEach(key -> {
						idChooser.getItems().add(String.valueOf(key));
					});
					Collections.sort(idChooser.getItems(), new NumericStringComparator());
					break;
				}
				
				idChooser.disableProperty().set(false);


			}
		});
    	
    	Button findButton = new Button("Find");
    	findButton.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				
				selectionHandler.clearSelection();

				switch(entityTypeChooser.getValue()) {
				case Area:
					selectedShape = coreController.getPlaybackController().getPlaybackModel().getAreaShapes().get(idChooser.getValue());
					details = new DetailsModel(coreController.getPlaybackController().getPlaybackModel().getAreaShapes().get(idChooser.getValue()).getDataProperties());
					selectionHandler.setSelection(selectedShape, true, details);
					
					break;
				case Edge:
					selectedShape = coreController.getPlaybackController().getPlaybackModel().getEdgeShapes().get(idChooser.getValue());
					details = new DetailsModel(coreController.getPlaybackController().getPlaybackModel().getEdgeShapes().get(idChooser.getValue()).getDataProperties());
					selectionHandler.setSelection(selectedShape, true, details);
					break;
				case Pedestrian:
					coreController.getPlaybackController().getPlaybackModel().getPedestrianShapes().keySet().stream().sorted().forEach(key -> 
					{ 	String[] string = new String[2];
						string=key.split("\\.");
							if(idChooser.getValue().equalsIgnoreCase(string[0]))
								{
									selectedShape = coreController.getPlaybackController().getPlaybackModel().getPedestrianShapes().get(key);
									details = new DetailsModel(coreController.getPlaybackController().getPlaybackModel().getPedestrianShapes().get(key).getDataProperties());
									selectionHandler.setSelection(selectedShape, true, details);
								}
					});
					break;
				case Vertex:
					selectedShape = coreController.getPlaybackController().getPlaybackModel().getVertexShapes().get(Integer.parseInt(idChooser.getValue()));
					details = new DetailsModel(coreController.getPlaybackController().getPlaybackModel().getVertexShapes().get(Integer.parseInt(idChooser.getValue())).getDataProperties());
					selectionHandler.setSelection(selectedShape, true, details);
					break;
				}

				

			}
		});
    	
    	GridPane grid = new GridPane();
    	grid.setHgap(20);
    	grid.setVgap(20);

    	grid.add(findEntityLabel, 0, 0);
    	grid.add(entityTypeChooser, 1, 0);
    	grid.add(idLabel, 2, 0);
    	grid.add(idChooser, 3, 0);
    	grid.add(findButton, 4, 0);
		
		dialog.getDialogPane().setContent(grid);
    	dialog.showAndWait();
	}
}
