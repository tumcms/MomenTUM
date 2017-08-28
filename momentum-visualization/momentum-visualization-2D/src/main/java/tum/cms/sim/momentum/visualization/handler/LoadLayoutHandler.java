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
import java.io.FileNotFoundException;

import javafx.geometry.Rectangle2D;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import tum.cms.sim.momentum.configuration.ConfigurationManager;
import tum.cms.sim.momentum.visualization.controller.CoreController;
import tum.cms.sim.momentum.visualization.enums.PropertyType;
import tum.cms.sim.momentum.visualization.utility.AnimationCalculations;
import tum.cms.sim.momentum.visualization.utility.GeometryModelBusinessLogic;
import tum.cms.sim.momentum.visualization.view.dialogControl.InformationDialogCreator;

public class LoadLayoutHandler extends LoadHandler {

	private final static String titleLayoutChooser = "Open simulation layout file";

	@Override
	public void load(CoreController coreController, Window parentWindow) throws Exception {

		File layoutFile = getFile(titleLayoutChooser, new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml"),
				parentWindow);

		if (layoutFile != null) {
			
			QuickloadHandler.resetCsvFiles();
			this.load(coreController, layoutFile);
		}

	}

	@Override
	public void load(CoreController coreController, File file) throws Exception {
		
		if (coreController.getCoreModel().getLayoutLoaded()) {

			coreController.resetCoreModel();
		}

		try {

			ConfigurationManager configurationManager = new ConfigurationManager();
			configurationManager.deserializeCompleteConfiguration(file.getAbsolutePath());

			coreController.getVisualizationController().getCustomizationController().fillCustomizationModelFromPreferences();
			coreController.getVisualizationController().getVisibilitiyModel().fillFromPreferences();

			GeometryModelBusinessLogic.createLayout(configurationManager.getSimulatorConfiguration().getLayouts().get(0), coreController);


			if(configurationManager.getSimulatorConfiguration().getTimeState() != null) {
				
				coreController.getInteractionViewController().getTimeLineModel().setEndTime(
					configurationManager.getSimulatorConfiguration().getTimeState().getSimulationEndTime());
				coreController.getInteractionViewController().getTimeLineModel().setTimeStepDuration(
					configurationManager.getSimulatorConfiguration().getTimeState().getTimeStepDuration());
			}
			else {
				
				coreController.getInteractionViewController().getTimeLineModel().setEndTime(
						configurationManager.getSimulatorConfiguration().getSimEnd());
					coreController.getInteractionViewController().getTimeLineModel().setTimeStepDuration(
						configurationManager.getSimulatorConfiguration().getTimeStepDuration());
			}
			
			UserPreferenceHandler.putProperty(PropertyType.layoutPath, file.getParent());
			Rectangle2D boundingBox = AnimationCalculations.computeObstacleCenterOfGravity2D(
					configurationManager.getSimulatorConfiguration().getLayouts().get(0), coreController.getVisualizationController().getVisualizationModel());

			coreController.getVisualizationController().centerViewPoint(boundingBox);
			QuickloadHandler.setLatestLayout(file);
			coreController.getCoreModel().setLayoutLoaded(true);
		} catch (FileNotFoundException e) {

			coreController.getCoreModel().setLayoutLoaded(false);
			coreController.getVisualizationController().clearAll();
			InformationDialogCreator.createErrorDialog("", "Error loading layout file", e);
			throw e;
		}

	}
}
