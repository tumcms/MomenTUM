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
import java.net.MalformedURLException;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import tum.cms.sim.momentum.visualization.controller.CoreController;

public class LoadBackgroundImageHandler {


	 Pane pane = null;
//	 
//	 @FXML
//	 Button open;
//	 
//	 @FXML
//	 public void initialize() {
//	  open.setOnAction(event -> imageOpener());
//	 }
	 
	 public void imageOpener(CoreController coreController) {
		 
		 
		  File imgFile = openImage();
		  try {
		   if (imgFile != null) {
		    displayImage(imgFile, coreController);
		   } 
		  } catch (MalformedURLException e) {
		   showError(e);
		  }
		 }
		 
		 static File openImage() {
		  FileChooser fileChooser = new FileChooser();
		  fileChooser.setTitle("Select an Image File");
		  fileChooser.getExtensionFilters().add(new ExtensionFilter(
		    "Image Files", "*.png", "*.jpg", "*.gif"));
		  return fileChooser.showOpenDialog(null);
	 }
	 
	 public void displayImage(File imgFile, CoreController coreController) throws MalformedURLException {
		 
		 Image background = new Image(imgFile.toURI().toURL().toString());
		 BackgroundSize size = new BackgroundSize(BackgroundSize.AUTO, 
				 BackgroundSize.AUTO, 
				 false, 
				 false, 
				 true, 
				 true);
		 
		 BackgroundImage bimg = new BackgroundImage(background, 
				 BackgroundRepeat.NO_REPEAT, 
				 BackgroundRepeat.NO_REPEAT, 
				 BackgroundPosition.CENTER, 
				 size);
		 
		 AnchorPane grid2 = coreController.getPlaybackController().getPlayBackCanvas();
	 
		 pane = new Pane();
		 ObservableList<Node> list = grid2.getChildren();
		 
		 // grid2.getChildren().remove(grid2.getChildren().get(0));

		 grid2.getChildren().add(0, pane);
		 pane.setMinSize(1186, 674);
		 pane.setLayoutX(-128);
		 pane.setLayoutY(370);
		 pane.setBackground(new Background(bimg));

		 for (Node node : list) {
			    
			 	grid2.getChildren().add(node);
		 }
		  
		 //	pane.minWidthProperty().bind(arg0);	  
	 }
	 
	 public void showError(Exception e) {
		  
		 Alert info = new Alert(AlertType.ERROR);
		 info.setTitle("Image not found");
		 info.setHeaderText("Image not found");
		 info.setContentText(e.getMessage());
		 info.showAndWait();
	 }
}
