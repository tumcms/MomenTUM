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

import javafx.collections.FXCollections;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import tum.cms.sim.momentum.utility.csvData.CsvType;

public class CustomCSVDialogCreator {
	
	private static String customCSVTitle = "MomenTumV2";
	private static String customCSVHeader = "Choose type of custom CSV:";
	
	public static CsvType createCustomCSVDialog(CsvType preferredType) {
		
		Dialog<Void> dialog = new Dialog<Void>();
		dialog.initModality(Modality.WINDOW_MODAL);
    	dialog.setTitle(customCSVTitle);
    	dialog.setHeaderText(customCSVHeader);
    	
    	ButtonType doneButtonType = new ButtonType("Done", ButtonData.OK_DONE);    	
    	dialog.getDialogPane().getButtonTypes().addAll(doneButtonType);
    	
    	ComboBox<CsvType> customTypeChooser = new ComboBox<CsvType>();
    	
    	customTypeChooser.getSelectionModel().select(preferredType);
    	
    	customTypeChooser.setPrefWidth(160);
    	customTypeChooser.getItems().setAll(FXCollections.observableArrayList(CsvType.values()));
    	
    	GridPane grid = new GridPane();
    	grid.setHgap(20);
    	grid.setVgap(20);

    	grid.add(customTypeChooser, 1, 0);
		
		dialog.getDialogPane().setContent(grid);
    	dialog.showAndWait();
    	return customTypeChooser.getValue();
	}
}
