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

package tum.cms.sim.momentum.visualization.model.geometry;

import java.util.ArrayList;

import javafx.scene.shape.Line;
import tum.cms.sim.momentum.configuration.model.lattice.LatticeModelConfiguration;
import tum.cms.sim.momentum.configuration.scenario.ScenarioConfiguration;
import tum.cms.sim.momentum.visualization.controller.CoreController;
import tum.cms.sim.momentum.visualization.controller.CustomizationController;
import tum.cms.sim.momentum.visualization.model.VisibilitiyModel;

public class LatticeModel {
	
	private ArrayList<Line> latticeLines = null;
	
	public ArrayList<Line> getLatticeLines() {
		return latticeLines;
	}
	
	//
	
	public void setVisibility(boolean isVisible) {
		
		latticeLines.forEach(line -> line.setVisible(isVisible));
	}
	
	public LatticeModel(LatticeModelConfiguration latticeConfiguration, 
			ScenarioConfiguration scenarioConfiguration, 
			CoreController coreController,
			CustomizationController customizationController,
			VisibilitiyModel visibilitiyModel) {
		
		latticeLines = new ArrayList<Line>();
		
		Line intermediate = null;
 		double nextStep = (latticeConfiguration.getCellEdgeSize() * coreController.getCoreModel().getResolution());

		for(double iter = (scenarioConfiguration.getMinX() + 0.5 * latticeConfiguration.getCellEdgeSize()) * coreController.getCoreModel().getResolution() ; 
			iter <= scenarioConfiguration.getMaxX() * coreController.getCoreModel().getResolution(); 
			iter += nextStep) {
		
			intermediate = new Line(iter,
					scenarioConfiguration.getMinY() * coreController.getCoreModel().getResolution(), 
					iter, 
					scenarioConfiguration.getMaxY() * coreController.getCoreModel().getResolution());
			
			intermediate.setTranslateZ(0.002 * coreController.getCoreModel().getResolution());
			intermediate.setStrokeWidth(0.05);
			intermediate.strokeProperty().bind(customizationController.getCustomizationModel().latticeColorProperty());
			intermediate.visibleProperty().bind(visibilitiyModel.latticeVisibilityProperty());
			
			latticeLines.add(intermediate);
		}
		
		for(double iter = (scenarioConfiguration.getMinY() + 0.5 * latticeConfiguration.getCellEdgeSize())  * coreController.getCoreModel().getResolution(); 
		
			iter <= scenarioConfiguration.getMaxY() * coreController.getCoreModel().getResolution(); 
			iter += nextStep) {
			
			intermediate = new Line(scenarioConfiguration.getMinX() * coreController.getCoreModel().getResolution(),
					iter,
					scenarioConfiguration.getMaxX() * coreController.getCoreModel().getResolution(),
					iter);
			
			intermediate.setTranslateZ(0.002 * coreController.getCoreModel().getResolution());
			intermediate.setStrokeWidth(0.05);
			intermediate.strokeProperty().bind(customizationController.getCustomizationModel().latticeColorProperty());
			intermediate.visibleProperty().bind(visibilitiyModel.latticeVisibilityProperty());
			
			latticeLines.add(intermediate);
		}
	}
}
