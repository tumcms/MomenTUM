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

package tum.cms.sim.momentum.visualization;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import tum.cms.sim.momentum.visualization.controller.CoreController;
import tum.cms.sim.momentum.visualization.handler.ExitHandler;
import tum.cms.sim.momentum.visualization.handler.QuickloadHandler;
import tum.cms.sim.momentum.visualization.handler.UserPreferenceHandler;
import tum.cms.sim.momentum.visualization.model.CoreModel;

public class VisualizationKernel extends Application {
	
	@Override
	public void start(Stage primaryStage) throws IOException {
		
		// start core view
		URL uri = this.getClass().getResource("view/CoreView.fxml");
		ResourceBundle bundle = ResourceBundle.getBundle("tum.cms.sim.momentum.visualization.view.resources");  
		FXMLLoader loader = new FXMLLoader(uri, bundle);
	    Parent parent = loader.load();
	    CoreController coreController = loader.getController();
	    
	    //load stored properties
	    UserPreferenceHandler.loadPropertiesFromFile();
	    QuickloadHandler.loadQuickloadDataFromProperties();

	    try {
			
			Scene scene = new Scene(parent);
			
			primaryStage.setMaximized(true);
			primaryStage.setScene(scene);
			primaryStage.setMinHeight(600.0);
			primaryStage.setMinWidth(1200.0);

			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				
				@Override
				public void handle(WindowEvent arg0) {

					new ExitHandler(coreController).clean();
				}
			});
			
			primaryStage.show();
			
		} catch(Exception e) {
			
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
