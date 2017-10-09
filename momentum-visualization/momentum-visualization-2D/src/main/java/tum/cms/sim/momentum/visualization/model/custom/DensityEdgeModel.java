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

package tum.cms.sim.momentum.visualization.model.custom;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import tum.cms.sim.momentum.utility.geometry.Polygon2D;
import tum.cms.sim.momentum.visualization.controller.CustomizationController;
import tum.cms.sim.momentum.visualization.handler.SelectionHandler.SelectionStates;
import tum.cms.sim.momentum.visualization.model.CoreModel;
import tum.cms.sim.momentum.visualization.model.CustomizationModel;
import tum.cms.sim.momentum.visualization.model.geometry.ShapeModel;
import tum.cms.sim.momentum.visualization.utility.DensityColor;


public class DensityEdgeModel extends ShapeModel {
	
	private Group group = new Group();
	private String identification = null;
	private Polygon2D polygonShape = null;
	private Double density = null;
	
	private Line edgeShape = null;
	private Circle startNode = null;
	private Circle endNode = null;
//	private Circle innerTransitAreaShape;
//	private Circle outerTransitAreaShape;
	
	private CustomizationModel customizationModel = null;

	/**
	 * @param customizationController.getCustomizationModel() 
	 * @param obstacleConfiguration
	 */
	public DensityEdgeModel(String id, CustomizationController customizationController) {
		
		this.customizationModel = customizationController.getCustomizationModel();
		this.identification = id;
	}
	
	@Override
	public Group getShape() {
		
		return group;
	}
	
	public String getIdentification() {
		return identification;
	}
	public Polygon2D getPolygonShape() {
		return polygonShape;
	}
	
	public Double getDensity() {
		return density;
	}
	
	public void createShape(CoreModel coreModel, double startLeftX, double startLeftY, double endLeftX, double endLeftY, double density, double width, double maximalDensity) {
		
//		Integer resolution = coreModel.getResolution();
		
//		innerTransitAreaShape = new Circle(startLeftX  * resolution,
//				startLeftY * resolution,
//				5 * resolution);
//		
//		innerTransitAreaShape.setFill(Color.BLUEVIOLET);
//		innerTransitAreaShape.setOpacity(0.2);
//		innerTransitAreaShape.setTranslateZ(0.0006 * resolution);
//		
//		outerTransitAreaShape = new Circle(startLeftX  * resolution,
//				startLeftY * resolution,
//				15 * resolution);
//		
//		outerTransitAreaShape.setFill(Color.MEDIUMTURQUOISE);
//		outerTransitAreaShape.setOpacity(0.2);	
//		outerTransitAreaShape.setTranslateZ(0.0005 * resolution);	
//		
//		group.setTranslateX(0);
//		group.setTranslateY(0);
//		
//		group.getChildren().add(innerTransitAreaShape);
//		group.getChildren().add(outerTransitAreaShape);
		
		Integer resolution = coreModel.getResolution();
		
		startNode = new Circle(startLeftX * resolution,
				startLeftY * resolution,
				1.3 * width * resolution);
		
		startNode.setFill(Color.BLACK);
		//startNode.setTranslateZ(0.0006 * resolution);
		startNode.setTranslateZ(0.0006 * resolution);
		
		endNode = new Circle(endLeftX * resolution,
				endLeftY * resolution,
				1.3 * width * resolution);
		
		endNode.setFill(Color.BLACK);
		//endNode.setTranslateZ(0.0006 * resolution);
		endNode.setTranslateZ(0.0006 * resolution);
			
		edgeShape = new Line(startLeftX * resolution,
				startLeftY * resolution,
				endLeftX * resolution,
				endLeftY * resolution);
		
		Paint densityColor = DensityColor.getColor(density, maximalDensity);
		edgeShape.setStroke(densityColor);

		edgeShape.setStrokeWidth(width * resolution);
		//edgeShape.setTranslateZ(0.001 * resolution);
		edgeShape.setTranslateZ(0.001 * resolution);
	
		group.setTranslateX(0);
		group.setTranslateY(0);
		
		group.getChildren().add(edgeShape);
		group.getChildren().add(startNode);
		group.getChildren().add(endNode);
	}
	
	@Override
	public void setVisibility(boolean isVisible) {

	}
	
	@Override
	public void changeSelectionMode(SelectionStates selectionState) {		

		edgeShape.fillProperty().unbind();
		edgeShape.strokeProperty().unbind();
			
			switch(selectionState) {
			
			case NotSelected:

				edgeShape.fillProperty().bind(customizationModel.originColorProperty());
				edgeShape.strokeProperty().bind(customizationModel.originColorProperty());
				break;
	
			case Selected:
		
				edgeShape.fillProperty().bind(customizationModel.selectedColorProperty());
				edgeShape.strokeProperty().bind(customizationModel.selectedColorProperty());
				break;
			}
		}
	
	@Override
	public List<Node> getClickableShapes() {
		
		ArrayList<Node> clickableShapes = new ArrayList<Node>();
		clickableShapes.add(this.edgeShape);
		
		return clickableShapes;
	}

	@Override
	public LinkedHashMap<String, String> getDataProperties() {

		LinkedHashMap<String, String> details = new LinkedHashMap<>();
		
		details.put(ShapeModel.targetId, identification);
		
		return details;
	}

	public void placeShape(CoreModel coreModel, double startLeftX, double startLeftY, double endLeftX, double endLeftY, double density, double width, double maximalDensity) {
		
		Integer resolution = coreModel.getResolution();
		
		Paint densityColor = DensityColor.getColor(density, maximalDensity);
		edgeShape.setStroke(densityColor);

		edgeShape.setStrokeWidth(width * resolution);
//		group.setTranslateX(startLeftX  * resolution - innerTransitAreaShape.getCenterX());
//		group.setTranslateY(startLeftY  * resolution - innerTransitAreaShape.getCenterY());

	}
}
