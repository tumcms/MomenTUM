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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.math3.util.FastMath;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.Shape;
import tum.cms.sim.momentum.visualization.controller.CustomizationController;
import tum.cms.sim.momentum.visualization.handler.SelectionHandler.SelectionStates;
import tum.cms.sim.momentum.visualization.model.VisualizationModel;

public class TrajectoryModel extends ShapeModel{

	private String id;
	
	private Path trajectory = null;
	
	VisualizationModel visualizationModel = null;
	
	LinkedHashMap<String, String> details = new LinkedHashMap<>();
	
	public void setVisibility(boolean visible) {

		trajectory.setVisible(visible);
	}
	
	public Shape getTrajectory() {
		return trajectory;
	}
	
	public String getIdentification() {
		return this.id;
	}
	
	public void setColor(Color color) {
		
		trajectory.setStroke(color);
	}
	
	public ObjectProperty<Paint> getStrokeProperty() {
		
		return trajectory.strokeProperty();
	}

	public TrajectoryModel(String id, CustomizationController customizationController, double x, double y, int resolution) {
		
		this.id = id;
		trajectory = new Path();
		if (customizationController.getCustomizationModel().isTrajectoryRandomColor()) {
			trajectory.setStroke(new Color(
		            FastMath.random() % 0.5 + 0.25,
		            FastMath.random() % 0.5 + 0.25,
		            FastMath.random() % 0.5 + 0.25,
		            1.0));
		}
		else {
			
			trajectory.strokeProperty().bind(customizationController.getCustomizationModel().trajectoryColorProperty());
		}
		
		trajectory.getElements().add(new MoveTo(
				x * resolution, 
				y * resolution));
		trajectory.strokeWidthProperty().bind(customizationController.getCustomizationModel().trajectoryThicknessProperty());
		trajectory.setTranslateZ(0.002);
	}
	
	public void append(double x, double y, int resolution) {
	
//		if(last == null || FastMath.abs(last.getX() - x * resolution) > 0.01 && 
//				 FastMath.abs(last.getY() - y * resolution) > 0.01) {
			
			LineTo element = new LineTo(
					x * resolution, 
					y * resolution);
	
			trajectory.getElements().add(element);
		//}
	}

	public void clear() {
		trajectory.getElements().clear();
	}

	@Override
	public void changeSelectionMode(SelectionStates selectionState) {
		 
		switch(selectionState) {
		
		case NotSelected:
			if(this.trajectory != null) {
				
				visualizationModel.getTrajectoryShapes()
					.forEach((id,shape)-> shape.setVisibility(true));
			}
			break;
			
		case Selected:
			
			if(this.trajectory != null) {
				
				visualizationModel.getTrajectoryShapes()
					.forEach((id,shape)-> shape.setVisibility(false));
				trajectory.setVisible(true);
			}
			break;
		}
	}

	@Override
	public ArrayList<Node> getClickableShapes() {
		
		ArrayList<Node> clickableShapes = new ArrayList<Node>();
		
		clickableShapes.add(this.trajectory);

		return clickableShapes;
	}

	@Override
	public LinkedHashMap<String, String> getDataProperties() {
	
		this.details.put(ShapeModel.nameDetails, id);
		
		return this.details;
		
	}
	
	public void setDataProperties(LinkedHashMap<String, String> details) {
		
		this.details=details;
	}
	
	public void setModel(VisualizationModel visualizationModel) {
		
		this.visualizationModel = visualizationModel;
	}
	
}
