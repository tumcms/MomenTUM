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

import javafx.scene.Node;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeLineJoin;
import tum.cms.sim.momentum.configuration.scenario.AreaConfiguration.AreaType;
import tum.cms.sim.momentum.configuration.scenario.TaggedAreaConfiguration;
import tum.cms.sim.momentum.visualization.controller.CoreController;
import tum.cms.sim.momentum.visualization.controller.CustomizationController;
import tum.cms.sim.momentum.visualization.handler.SelectionHandler.SelectionStates;
import tum.cms.sim.momentum.visualization.model.CustomizationModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class TaggedAreaModel extends ShapeModel {

	private CustomizationModel customizationModel = null;
	private Polygon taggedAreaShape = null;
	private TaggedAreaConfiguration taggedAreaConfiguration = null;

	public Polygon getTaggedAreaShape() {

		return this.taggedAreaShape;
	}

	@Override
	public ArrayList<Node> getClickableShapes() {

		ArrayList<Node> clickableShapes = new ArrayList<Node>();
		clickableShapes.add(this.taggedAreaShape);

		return clickableShapes;
	}

	@Override
	public String getIdentification() {

		return this.taggedAreaConfiguration.getId().toString();
	}

	public AreaType getType() {

		//return this.taggedAreaConfiguration.getType();
		return AreaType.Information;
	}

	@Override
	public void changeSelectionMode(SelectionStates selectionState) {

		taggedAreaShape.fillProperty().unbind();
		taggedAreaShape.strokeProperty().unbind();

		switch(selectionState) {
		case NotSelected:
			taggedAreaShape.fillProperty().bind(customizationModel.destinationColorProperty());
			taggedAreaShape.strokeProperty().bind(customizationModel.destinationColorProperty());
			/*switch(taggedAreaConfiguration.getType()) {
			case Destination:
				taggedAreaShape.fillProperty().bind(customizationModel.destinationColorProperty());
				taggedAreaShape.strokeProperty().bind(customizationModel.destinationColorProperty());
				break;
			case Intermediate:
				taggedAreaShape.fillProperty().bind(customizationModel.intermediateColorProperty());
				taggedAreaShape.strokeProperty().bind(customizationModel.intermediateColorProperty());
				break;
			case Origin:
				taggedAreaShape.fillProperty().bind(customizationModel.originColorProperty());
				taggedAreaShape.strokeProperty().bind(customizationModel.originColorProperty());
				break;
			case Information:
				taggedAreaShape.fillProperty().bind(customizationModel.informationColorProperty());
				taggedAreaShape.strokeProperty().bind(customizationModel.informationColorProperty());
			default:
				break;
			}*/
			break;
		case Selected:

			taggedAreaShape.fillProperty().bind(customizationModel.selectedColorProperty());
			taggedAreaShape.strokeProperty().bind(customizationModel.selectedColorProperty());
			break;
		}
	}

	@Override
	public LinkedHashMap<String, String> getDataProperties() {

		LinkedHashMap<String, String> details = new LinkedHashMap<>();

		details.put(ShapeModel.nameDetails, this.taggedAreaConfiguration.getName());
		details.put(this.taggedAreaConfiguration.getType().getClass().getSimpleName(), this.taggedAreaConfiguration.getType().name());
		details.put(ShapeModel.targetId, Integer.toString(this.taggedAreaConfiguration.getId()));

		return details;
	}

	public TaggedAreaModel(TaggedAreaConfiguration taggedAreaConfiguration, CoreController coreController, CustomizationController customizationController) {
	
		this.customizationModel = customizationController.getCustomizationModel();
		this.taggedAreaConfiguration = taggedAreaConfiguration;
	
		Double[] points = new Double[taggedAreaConfiguration.getPoints().size() * 2];
		
		for(int iter = 0, input = 0; iter < taggedAreaConfiguration.getPoints().size(); iter++, input += 2) {
			
			points[input] = taggedAreaConfiguration.getPoints().get(iter).getX() * coreController.getCoreModel().getResolution();
			points[input+1] = taggedAreaConfiguration.getPoints().get(iter).getY() * coreController.getCoreModel().getResolution();
		}
		
		taggedAreaShape = new Polygon();
		taggedAreaShape.getPoints().addAll(points);
		taggedAreaShape.setStrokeWidth(0.5);
		taggedAreaShape.setStrokeLineJoin(StrokeLineJoin.MITER);


		taggedAreaShape.fillProperty().bind(customizationModel.destinationColorProperty());
		taggedAreaShape.strokeProperty().bind(customizationModel.destinationColorProperty());
		taggedAreaShape.setTranslateZ(0.0005);

		/*switch(taggedAreaConfiguration.getType()) {
		
		case Information:
			taggedAreaShape.fillProperty().bind(customizationModel.informationColorProperty());
			taggedAreaShape.strokeProperty().bind(customizationModel.informationColorProperty());
			taggedAreaShape.setTranslateZ(0.0004);
		case Destination:
			taggedAreaShape.fillProperty().bind(customizationModel.destinationColorProperty());
			taggedAreaShape.strokeProperty().bind(customizationModel.destinationColorProperty());
			taggedAreaShape.setTranslateZ(0.0003);
			break;
		case Intermediate:
			taggedAreaShape.fillProperty().bind(customizationModel.intermediateColorProperty());
			taggedAreaShape.strokeProperty().bind(customizationModel.intermediateColorProperty());
			taggedAreaShape.setTranslateZ(0.0002);
			break;
		case Origin:
			taggedAreaShape.fillProperty().bind(customizationModel.originColorProperty());
			taggedAreaShape.strokeProperty().bind(customizationModel.originColorProperty());
			taggedAreaShape.setTranslateZ(0.0001);
			break;
		default:
			break;
		}*/
	}

	@Override
	public void setVisibility(boolean isVisible) {
		
		this.taggedAreaShape.setVisible(isVisible);
	}
}
