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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import tum.cms.sim.momentum.visualization.handler.ExitHandler;
import tum.cms.sim.momentum.visualization.handler.LoadBackgroundImageHandler;
import tum.cms.sim.momentum.visualization.handler.LoadCsvHandler;
import tum.cms.sim.momentum.visualization.handler.LoadLayoutHandler;
import tum.cms.sim.momentum.visualization.handler.QuickloadHandler;
import tum.cms.sim.momentum.visualization.handler.SnapshotHandler;
import tum.cms.sim.momentum.visualization.view.dialogControl.CustomizationDialogCreator;
import tum.cms.sim.momentum.visualization.view.dialogControl.FindDialogCreator;
import tum.cms.sim.momentum.visualization.view.dialogControl.InformationDialogCreator;

public class MenuBarController implements Initializable {

	// view
	@FXML
	private MenuBar menuBar;
	@FXML
	private MenuItem record;
	@FXML
	private MenuItem color;
	@FXML
	private MenuItem find;
	@FXML
	private MenuItem snapshotCustomizationMenu;
	@FXML
	private MenuItem snapshot;
	@FXML
	private MenuItem setCamera;
	@FXML
	private MenuItem switchView;
	@FXML
	private MenuItem loadBackgroundImage;

	// Controller
	private CoreController coreController;

	
	public void bindCoreModel(CoreController coreController) {

		this.coreController = coreController;
		record.disableProperty()
				.bind(coreController.getCoreModel().csvLoadedProperty().not().and(coreController.getCoreModel().layoutLoadedProperty().not()));
		snapshot.disableProperty()
				.bind(coreController.getCoreModel().csvLoadedProperty().not().and(coreController.getCoreModel().layoutLoadedProperty().not()));
		snapshotCustomizationMenu.disableProperty()
				.bind(coreController.getCoreModel().csvLoadedProperty().not().and(coreController.getCoreModel().layoutLoadedProperty().not()));
		color.disableProperty()
				.bind(coreController.getCoreModel().csvLoadedProperty().not().and(coreController.getCoreModel().layoutLoadedProperty().not()));
		find.disableProperty()
				.bind(coreController.getCoreModel().csvLoadedProperty().not().and(coreController.getCoreModel().layoutLoadedProperty().not()));
		
		menuBar.disableProperty().bind(coreController.getInteractionViewController().getTimeLineModel().isAnimatingProperty());
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
	}

	private void loadLayout(ActionEvent event) throws Exception {
		
		try {
			
			new LoadLayoutHandler().load(coreController, menuBar.getScene().getWindow(), 0.0);
		}
		catch (Exception e) {
			coreController.resetCoreModel();
			throw e;
		}
	}

	@FXML
	public void onLoadLayout(ActionEvent event) throws Exception {
		loadLayout(event);
	}
	
	@FXML
	public void onLoadCsvOutput(ActionEvent event) throws Exception {

		if (!coreController.getCoreModel().getLayoutLoaded()) {
			InformationDialogCreator.createOnLoadCsvErrorDialog();
			loadLayout(event);
		} 
		
		if(coreController.getCoreModel().getLayoutLoaded()){

			try {
				
				new LoadCsvHandler().load(coreController,
						menuBar.getScene().getWindow(),
						coreController.getInteractionViewController().getTimeLineBindingValue());
			}
			catch (Exception e) {
				
				coreController.resetCoreModel();
				throw e;
			}
		}
	}

	@FXML
	public void onQuickLoad(ActionEvent event) throws Exception {

		QuickloadHandler.quickload(coreController, 0.0);
	}

	@FXML
	public void onUnload(ActionEvent event) throws Exception {

		coreController.resetCoreModel();
	}

	@FXML
	public void onSnapshot(ActionEvent event) throws IOException {

		File snapshotFile = coreController.getPlaybackController().getSnapshotModel().getSnapshotPath();
		double pixelScale = coreController.getPlaybackController().getSnapshotModel().getPixelScale();

		SnapshotHandler snapshotHandler = new SnapshotHandler(coreController.getPlaybackController().getPlayBackCanvas(), pixelScale,
				snapshotFile.getAbsolutePath());

		snapshotHandler.snapshot();
	}

	@FXML
	public void onRecord(ActionEvent event) {

		if (coreController.getCoreModel().getCsvLoaded() && coreController.getCoreModel().getLayoutLoaded()) {

			coreController.getInteractionViewController().startRecording();
		} else {

			InformationDialogCreator.createOnRecordErrorDialog();
		}
	}

	@FXML
	public void onFind(ActionEvent event) {

		FindDialogCreator.createFindDialog(coreController, PlaybackController.getSelectionHandler());
	}

	@FXML
	public void onColor(ActionEvent event) {

		CustomizationDialogCreator.createColorDialog(coreController.getPlaybackController());
	}

	@FXML
	public void onSnapshotMenu(ActionEvent event) {

		coreController.getPlaybackController().getSnapshotModel().fillFromPreferences();
		CustomizationDialogCreator.createSnapshotDialog(coreController.getPlaybackController().getSnapshotModel(),
				coreController);
		coreController.getPlaybackController().getSnapshotModel().createForPreferences();
	}

	@FXML
	public void onSetCamera(ActionEvent event) {
		// CameraOptionsDialogCreator.createCameraOptionsDialog(coreModel,
		// coreModel.getPlaybackController());
	}

	@FXML
	public void onSwitchView(ActionEvent event) {
		
		//switchView(this.menuBar, coreController.getDetailController().getDetailBox());
	}
	
	public void switchView(MenuBar menuBar, VBox detailsBar) {
		
		if (coreController.getCoreModel().getSwitchDetailsView().get() != 0.0) {

			coreController.getCoreModel().getSwitchDetailsView().set(0.0);
			coreController.getCoreModel().getSwitchDetailInverseView().set(coreController.getCoreModel().getDetailsWidth());
			coreController.getCoreModel().getSwitchLayerView().set(0.0);

			GridPane.setColumnIndex(menuBar, 1);
			GridPane.setColumnSpan(menuBar, 1);
			GridPane.setColumnSpan(detailsBar, 1);
		} else {
			
			coreController.getCoreModel().getSwitchDetailsView().set(coreController.getCoreModel().getDetailsWidth());
			coreController.getCoreModel().getSwitchDetailInverseView().set(0.0);
			coreController.getCoreModel().getSwitchLayerView().set(coreController.getCoreModel().getLayerWidth());

			GridPane.setColumnIndex(menuBar, 0);
			GridPane.setColumnSpan(menuBar, 2);

			GridPane.setColumnSpan(detailsBar, 2);
		}
	}

	@FXML
	public void onLoadBackgroundImage(ActionEvent event) {

		// coreModel.getVisualizationModel().get
		LoadBackgroundImageHandler backgroundImageLoader = new LoadBackgroundImageHandler();

		backgroundImageLoader.imageOpener(coreController);
	}

	@FXML
	public void onQuit(ActionEvent event) throws Exception {

		new ExitHandler(coreController).clean();
	}
}
