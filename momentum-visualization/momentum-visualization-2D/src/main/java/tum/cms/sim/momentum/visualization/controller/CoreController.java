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
import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import tum.cms.sim.momentum.utility.csvData.CsvType;
import tum.cms.sim.momentum.utility.csvData.reader.SimulationOutputReader;
import tum.cms.sim.momentum.visualization.model.CoreModel;
import tum.cms.sim.momentum.visualization.model.PlaybackModel;

public class CoreController implements Initializable {

	// View
	@FXML
	private HBox menuBarView;
	@FXML
	private VBox detailView;
	@FXML
	private HBox interactionView;
	@FXML
	private AnchorPane playbackView;
	@FXML
	private VBox layerConfigurationView;
	@FXML
	private AnchorPane framePane;
	@FXML
	private GridPane frameGrid;

	// controller
	@FXML
	private MenuBarController menuBarViewController;
	@FXML
	private InteractionController interactionViewController;
	@FXML
	private PlaybackController playbackViewController;
	@FXML
	private LayerConfigurationController layerConfigurationViewController;
	@FXML
	private LoadedFilesController loadedFilesViewController;
	@FXML
	private LoadedFilesController loadedFilesController;
	@FXML
	private DetailController detailViewController;

	// models
	@FXML
	private static CoreModel coreModel = new CoreModel();

	// listener
	public ChangeListener<Number> onMaxSizeChangedListener = new ChangeListener<Number>() {

		@Override
		public void changed(ObservableValue<? extends Number> arg0, Number oldNumber, Number newNumber) {

			// TODO newNumber should be the new value
			double x = newNumber.doubleValue(); // visualizationController.getVisualizationModel().maxSize();
			double m = (1.0 - 7.0) / (2000.0 - 1.0);
			double t = 7 - m + 1;
			int resolution = (int) (m * x + t);
			resolution = resolution < 1 ? 1 : resolution;
			coreModel.setResolution(resolution);
		}
	};

	public DetailController getDetailController() {
		return detailViewController;
	}
	
	public LayerConfigurationController getLayerConfigurationController() {
		return layerConfigurationViewController;
	}

	public CoreModel getCoreModel() {
		return CoreController.coreModel;
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		PlaybackModel playbackModel = playbackViewController.getPlaybackModel();

		playbackModel.maxSizeXProperty().removeListener(onMaxSizeChangedListener);
		playbackModel.maxSizeXProperty().addListener(onMaxSizeChangedListener);
		playbackModel.maxSizeYProperty().removeListener(onMaxSizeChangedListener);
		playbackModel.maxSizeYProperty().addListener(onMaxSizeChangedListener);

		interactionViewController.bindCoreModel(this);
		menuBarViewController.bindCoreModel(this);
		playbackViewController.bindCoreModel(this);
		layerConfigurationViewController.bindCoreModel(this);
		detailViewController.bindCoreModel(this);
	}

	public void resetCoreModel() throws Exception {

		clearSimulationOutputReaders();
		coreModel.setResolution(1);
		coreModel.setLayoutLoaded(false);
		coreModel.setCsvLoaded(false);
		interactionViewController.resetTimeLineModel();
		playbackViewController.clearAll();
		layerConfigurationViewController.resetCheckBox();
	}

	public Collection<SimulationOutputReader> getOutputReaders() {

		return coreModel.getSimulationOutputReaders().values();
	}

	public void clearSimulationOutputReaders() throws Exception {

		for (SimulationOutputReader simReader : coreModel.getSimulationOutputReaders().values()) {

			simReader.endReadDataSetAsync();
		}
		coreModel.getSimulationOutputReaders().clear();
	}

	public void cleanOnExit() throws Exception {

		resetCoreModel();

		getPlaybackController().getVisibilitiyModel().createForPreferences();
		getPlaybackController().getSnapshotModel().createForPreferences();
	}

	public PlaybackController getPlaybackController() {
		return playbackViewController;
	}

	public InteractionController getInteractionViewController() {
		return interactionViewController;
	}

	public ArrayList<SimulationOutputReader> getSimulationOutputReaderListOfType(CsvType csvType) {

		ArrayList<SimulationOutputReader> simReaderList = new ArrayList<>();

		for (SimulationOutputReader simReader : coreModel.getSimulationOutputReaders().values()) {

			if (simReader.getCsvType().equals(csvType)) {

				simReaderList.add(simReader);
			}
		}

		return simReaderList;
	}

	/**
	 * Waits until all active {@link SimulationOutputReader}s are ready. The data is
	 * nod loaded but the index system is loaded!
	 * 
	 * This method considers if the timeStep does exists in the
	 * {@link SimulationOutputReader}s.
	 * 
	 * @param timeStep,
	 *            the time step
	 * @param previous,
	 *            the number of previous time steps for each reader to make ready to
	 *            use
	 * @param next,
	 *            the number of next time steps for each reader to make ready to use
	 * @return if all active {@link SimulationOutputReader}s are loaded
	 * @throws Exception
	 */
	public boolean waitUntilReadersReady(Double timeStep, Integer previous, Integer next) throws Exception {

		ArrayList<SimulationOutputReader> loadStatusReaders = new ArrayList<>();

		for (SimulationOutputReader simReader : getOutputReaders()) {
			loadStatusReaders.add(simReader);
		}

		while (!loadStatusReaders.isEmpty()) { // Wait until data is ready or not existent

			for (int iter = 0; iter < loadStatusReaders.size(); iter++) {

				double timeStepDifference = loadStatusReaders.get(iter).getTimeStepDifference();

				if (loadStatusReaders.get(iter).makeReadyForIndex(timeStep)
						&& loadStatusReaders.get(iter).dataReady(timeStep - timeStepDifference * previous)
						&& loadStatusReaders.get(iter).dataReady(timeStep + timeStepDifference * next)) {

					loadStatusReaders.remove(iter);
					iter--;
				}
			}

			if (loadStatusReaders.isEmpty()) {
				break;
			}

			try {

				Thread.sleep(150L);

			} catch (InterruptedException e) {

				e.printStackTrace();
				return false;
			}
		}

		return true;
	}
}
