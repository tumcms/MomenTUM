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
import tum.cms.sim.momentum.visualization.model.VisualizationModel;

public class CoreController implements Initializable {

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
	private VisualizationController playbackViewController;
	@FXML
	private LayerConfigurationController layerConfigurationViewController;
	@FXML
	private DetailController detailViewController;
	@FXML
	private LoadedFilesController loadedFilesController;

	// models
	@FXML
	private static CoreModel coreModel = new CoreModel();
	@FXML
	private VisualizationModel visualizationModel = null;
	private DetailController detailController = null;
	private ArrayList<SimulationOutputReader> simulationOutputReaderList = new ArrayList<>();

	// listener
	public ChangeListener<Number> onMaxSizeChangedListener = new ChangeListener<Number>() {

		@Override
		public void changed(ObservableValue<? extends Number> arg0, Number oldNumber, Number newNumber) {

			double x = visualizationModel.maxSize();
			double m = (1.0 - 7.0) / (2000.0 - 1.0);
			double t = 7 - m + 1;
			int resolution = (int) (m * x + t);
			resolution = resolution < 1 ? 1 : resolution;
			coreModel.setResolution(resolution);
		}
	};

	public DetailController getDetailController() {
		return detailController;
	}

	public void setDetailController(DetailController detailController) {
		this.detailController = detailController;
	}

	public CoreModel getCoreModel() {
		return CoreController.coreModel;
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		setVisualizationModel(playbackViewController.getVisualizationModel());
		setDetailController(detailViewController);

		interactionViewController.bindCoreModel(this);
		menuBarViewController.bindCoreModel(this);
		playbackViewController.bindCoreModel(this);
		layerConfigurationViewController.bindCoreModel(this);
		detailViewController.bindCoreModel(this);
	}

	public void resetCoreModel() throws Exception {

		coreModel.setResolution(1);
		coreModel.setLayoutLoaded(false);
		coreModel.setCsvLoaded(false);

		interactionViewController.resetTimeLineModel();
		playbackViewController.clearAll();
	}

	public void setVisualizationModel(VisualizationModel visualizationModel) {

		this.visualizationModel = visualizationModel;

		this.visualizationModel.maxSizeXProperty().removeListener(onMaxSizeChangedListener);
		this.visualizationModel.maxSizeXProperty().addListener(onMaxSizeChangedListener);

		this.visualizationModel.maxSizeYProperty().removeListener(onMaxSizeChangedListener);
		this.visualizationModel.maxSizeYProperty().addListener(onMaxSizeChangedListener);
	}

	public VisualizationModel getVisualizationModel() {
		return visualizationModel;
	}

	public void cleanOnExit() {

		try {
			resetCoreModel();
			for (SimulationOutputReader simReader : simulationOutputReaderList) {
				simReader.endReadDataSetAsync();
			}

			getVisualizationController().getVisibilitiyModel().createForPreferences();
			getVisualizationController().getSnapshotModel().createForPreferences();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public VisualizationController getVisualizationController() {
		return playbackViewController;
	}

	public InteractionController getInteractionViewController() {
		return interactionViewController;
	}

	public ArrayList<SimulationOutputReader> getSimulationOutputReaderList() {
		return simulationOutputReaderList;
	}

	public ArrayList<SimulationOutputReader> getActiveSimulationOutputReaderList() {
		ArrayList<SimulationOutputReader> activeSimulationOutputReaders = new ArrayList<>();
		for (SimulationOutputReader simReader : simulationOutputReaderList) {
			if (simReader.isActiveAnimating()) {
				activeSimulationOutputReaders.add(simReader);
			}
		}
		return activeSimulationOutputReaders;
	}

	public ArrayList<SimulationOutputReader> getSimulationOutputReaderListOfType(CsvType csvType) {
		ArrayList<SimulationOutputReader> simReaderList = new ArrayList<>();
		for (SimulationOutputReader simReader : simulationOutputReaderList) {
			if (simReader.getCsvType().equals(csvType)) {
				simReaderList.add(simReader);
			}
		}
		return simReaderList;
	}

	/**
	 * Waits until all active {@link SimulationOutputReader}s are loaded for the
	 * given time step. SimulationOutputreaders that do not contain the given timeStep 
	 * are not considered.
	 * 
	 * @param timeStep
	 *            the time step
	 * @return if all active {@link SimulationOutputReader}s are loaded
	 */
	public boolean waitUntilActiveSimulationOutputReadersAreLoaded(Double timeStep) {
		ArrayList<Boolean> loadStatusOfActiveSimulationOutputReaders = new ArrayList<>();
		while (!loadStatusOfActiveSimulationOutputReaders.isEmpty()
				|| loadStatusOfActiveSimulationOutputReaders.contains(false)) {
			loadStatusOfActiveSimulationOutputReaders = new ArrayList<>();
			for (SimulationOutputReader simReader : getActiveSimulationOutputReaderList()) {
				if (simReader.containsTimeStep(timeStep)) {
					loadStatusOfActiveSimulationOutputReaders.add(simReader.isLoadedForIndex(timeStep));
				}
			}
			try {
				// TODO reasonable sleep interval
				Thread.sleep(150L);
			} catch (InterruptedException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

}
