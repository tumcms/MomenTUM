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
import java.util.LinkedHashMap;

import javafx.scene.Node;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeLineJoin;
import tum.cms.sim.momentum.configuration.scenario.AreaConfiguration;
import tum.cms.sim.momentum.configuration.scenario.AreaConfiguration.AreaType;
import tum.cms.sim.momentum.visualization.controller.CoreController;
import tum.cms.sim.momentum.visualization.controller.CustomizationController;
import tum.cms.sim.momentum.visualization.handler.SelectionHandler.SelectionStates;
import tum.cms.sim.momentum.visualization.model.CustomizationModel;

public class AreaModel extends ShapeModel {

	private CustomizationModel customizationModel = null;
	private Polygon areaShape = null;
	private AreaConfiguration areaConfiguration = null;

	public Polygon getAreaShape() {
		
		return this.areaShape;
	}

	@Override
	public ArrayList<Node> getClickableShapes() {
		
		ArrayList<Node> clickableShapes = new ArrayList<Node>();
		clickableShapes.add(this.areaShape);
		
		return clickableShapes;
	}
	
	@Override
	public String getIdentification() {
		
		return this.areaConfiguration.getId().toString();
	}
	
	public AreaType getType() {
		
		return this.areaConfiguration.getType();
	}

	@Override
	public void changeSelectionMode(SelectionStates selectionState) {

		areaShape.fillProperty().unbind();
		areaShape.strokeProperty().unbind();
		
		switch(selectionState) {
		case NotSelected:
			switch(areaConfiguration.getType()) {
			
			case Destination:
				areaShape.fillProperty().bind(customizationModel.destinationColorProperty());
				areaShape.strokeProperty().bind(customizationModel.destinationColorProperty());
				break;
			case Intermediate:
				areaShape.fillProperty().bind(customizationModel.intermediateColorProperty());
				areaShape.strokeProperty().bind(customizationModel.intermediateColorProperty());
				break;
			case Origin:
				areaShape.fillProperty().bind(customizationModel.originColorProperty());
				areaShape.strokeProperty().bind(customizationModel.originColorProperty());
				break;
			case Information:
				areaShape.fillProperty().bind(customizationModel.informationColorProperty());
				areaShape.strokeProperty().bind(customizationModel.informationColorProperty());
			default:
				break;
			}
			break;
		case Selected:
	
			areaShape.fillProperty().bind(customizationModel.selectedColorProperty());
			areaShape.strokeProperty().bind(customizationModel.selectedColorProperty());
			break;
		}
	}
	
	@Override
	public LinkedHashMap<String, String> getDataProperties() {
		
		LinkedHashMap<String, String> details = new LinkedHashMap<>();
		
		details.put(ShapeModel.nameDetails, this.areaConfiguration.getName());
		details.put(this.areaConfiguration.getType().getClass().getSimpleName(), this.areaConfiguration.getType().name());
		details.put(ShapeModel.targetId, Integer.toString(this.areaConfiguration.getId()));
		
		return details;
	}
	
	public AreaModel(AreaConfiguration areaConfiguration, CoreController coreController, CustomizationController customizationController) {
	
		this.customizationModel = customizationController.getCustomizationModel();
		this.areaConfiguration = areaConfiguration;
	
		Double[] points = new Double[areaConfiguration.getPoints().size() * 2];
		
		for(int iter = 0, input = 0; iter < areaConfiguration.getPoints().size(); iter++, input += 2) {
			
			points[input] = areaConfiguration.getPoints().get(iter).getX() * coreController.getCoreModel().getResolution();
			points[input+1] = areaConfiguration.getPoints().get(iter).getY() * coreController.getCoreModel().getResolution();
		}
		
		areaShape = new Polygon(); 
		areaShape.getPoints().addAll(points);
		areaShape.setStrokeWidth(0.5);
		areaShape.setStrokeLineJoin(StrokeLineJoin.MITER);
		
		switch(areaConfiguration.getType()) {
		
		case Information:
			areaShape.fillProperty().bind(customizationModel.informationColorProperty());
			areaShape.strokeProperty().bind(customizationModel.informationColorProperty());
			areaShape.setTranslateZ(0.0004);
		case Destination:
			areaShape.fillProperty().bind(customizationModel.destinationColorProperty());
			areaShape.strokeProperty().bind(customizationModel.destinationColorProperty());
			areaShape.setTranslateZ(0.0003);
			break;
		case Intermediate:
			areaShape.fillProperty().bind(customizationModel.intermediateColorProperty());
			areaShape.strokeProperty().bind(customizationModel.intermediateColorProperty());
			areaShape.setTranslateZ(0.0002);
			break;
		case Origin:
			areaShape.fillProperty().bind(customizationModel.originColorProperty());
			areaShape.strokeProperty().bind(customizationModel.originColorProperty());
			areaShape.setTranslateZ(0.0001);
			break;
		default:
			break;
		}
	}

	@Override
	public void setVisibility(boolean isVisible) {
		
		this.areaShape.setVisible(isVisible);
	}
}
