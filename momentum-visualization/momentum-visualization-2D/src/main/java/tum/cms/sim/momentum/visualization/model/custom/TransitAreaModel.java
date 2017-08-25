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

import java.util.LinkedHashMap;
import java.util.List;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import tum.cms.sim.momentum.visualization.handler.SelectionHandler.SelectionStates;
import tum.cms.sim.momentum.visualization.model.geometry.ShapeModel;

public class TransitAreaModel extends ShapeModel {

	private Group group = new Group();
	private Circle innerTransitAreaShape = null;
	private Circle outerTransitAreaShape = null;
	
	public Circle getInnerTransitAreaShape() {
		return innerTransitAreaShape;
	}

	public Circle getOuterTransitAreaShape() {
		return outerTransitAreaShape;
	}

	private String identification = null;
	
	public String getIdentification() {
		return identification;
	}
	
	/**
	 * @param obstacleConfiguration
	 * @param coreModel
	 */
	public TransitAreaModel(String id) {
		
		identification = id;
	}
	
	public void createShape(double innerX, double innerY, double innerRadius, double outerRadius, double resolution) {
		
		innerTransitAreaShape = new Circle(innerX  * resolution,
				innerY * resolution,
				innerRadius * resolution);
		
		innerTransitAreaShape.setFill(Color.ORANGE); //Color.BLUEVIOLET
		innerTransitAreaShape.setOpacity(0.2);
		innerTransitAreaShape.setTranslateZ(0.0006 * resolution);
		
		outerTransitAreaShape = new Circle(innerX  * resolution,
				innerY * resolution,
				outerRadius * resolution);
		
		outerTransitAreaShape.setFill(Color.MEDIUMTURQUOISE);
		outerTransitAreaShape.setOpacity(0.2);	
		outerTransitAreaShape.setTranslateZ(0.0005 * resolution);	
		
		group.setTranslateX(0);
		group.setTranslateY(0);
		
		group.getChildren().add(innerTransitAreaShape);
		group.getChildren().add(outerTransitAreaShape);
	}

	public void placeShape(double innerX, double innerY, double innerRadius, double outerRadius, double resolution) {
		
		group.setTranslateX(innerX  * resolution - innerTransitAreaShape.getCenterX());
		group.setTranslateY(innerY  * resolution - innerTransitAreaShape.getCenterY());
	}
	
	@Override
	public Group getShape() {
		
		return group;
	}
	
	@Override
	public void setVisibility(boolean isVisible) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void changeSelectionMode(SelectionStates selectionState) {
		// TODO Auto-generated method stub
	}

	@Override
	public List<Node> getClickableShapes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LinkedHashMap<String, String> getDataProperties() {
		// TODO Auto-generated method stub
		return null;
	}

}
