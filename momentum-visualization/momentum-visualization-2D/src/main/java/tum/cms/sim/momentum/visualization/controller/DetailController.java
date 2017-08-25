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
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import tum.cms.sim.momentum.visualization.model.DetailsModelEntry;

public class DetailController implements Initializable {

	@FXML private TableView<DetailsModelEntry> tableView;
	
	public TableView<DetailsModelEntry> getTableView() {
		return tableView;
	}

	@FXML private TableColumn<DetailsModelEntry, String> detailHeader;
	@FXML private TableColumn<DetailsModelEntry, String> nameHeader;
	@FXML private VBox vbox;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		nameHeader.prefWidthProperty().bind(tableView.widthProperty().multiply(1.0/3.0).subtract(1)); 
		detailHeader.prefWidthProperty().bind(tableView.widthProperty().multiply(2.0/3.0).subtract(1));
		nameHeader.setCellValueFactory(new PropertyValueFactory<DetailsModelEntry,String>("name"));
		detailHeader.setCellValueFactory(new PropertyValueFactory<DetailsModelEntry,String>("detail"));
		
	}
	
	public void bindCoreModel(CoreController coreController) {
		
		this.vbox.disableProperty().bind(coreController.getVisualizationModel().is3DViewProperty());
	}

	public VBox getDetailBox() {

		return vbox;
	}
}
