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

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import tum.cms.sim.momentum.visualization.handler.SelectionHandler.SelectionStates;
import tum.cms.sim.momentum.visualization.model.CoreModel;
import tum.cms.sim.momentum.visualization.model.geometry.ShapeModel;

import java.util.LinkedHashMap;
import java.util.List;

public class CarModel extends ShapeModel {

	private static Color diffuseColor = Color.DARKBLUE;
	private static Color specularColor = Color.BLUE;

	private Box box = null;
	private Group group = new Group();
	private String id = null;

	// car properties
    double centerX;
    double centerY;
    double length;
    double width;
    double height;
    double xHeading;
    double yHeading;

	@Override
	public void setVisibility(boolean isVisible) {

		box.setVisible(isVisible);
	}

	public CarModel(String rowColumnId) {
		
		id = rowColumnId;
	}


	public void createShape(CoreModel coreModel,
			double centerX,
			double centerY,
			double length,
			double width,
			double height,
			double xHeading,
			double yHeading) {

		/* box = new Box(centerX * coreModel.getResolution() -
				coreModel.getResolution() * 0.5,
                centerY * coreModel.getResolution() -
				coreModel.getResolution() * 0.5,
                width * coreModel.getResolution(),
                length * coreModel.getResolution());*/

		box = new Box(width * coreModel.getResolution(),
				height * coreModel.getResolution(),
				length * coreModel.getResolution());

		box.setTranslateX(centerX * coreModel.getResolution());
		box.setTranslateY(centerY * coreModel.getResolution());
		box.setTranslateZ(1.0 * height * coreModel.getResolution());

		/*
        Rotate rotate = new Rotate();
        rotate.setPivotX(centerX * coreModel.getResolution());
        rotate.setPivotY(centerY * coreModel.getResolution());
        rotate.setAngle(Math.atan2(xHeading, yHeading));
        box.getTransforms().add(rotate);*/

        // adjust color
		box.materialProperty().unbind();
		PhongMaterial material = new PhongMaterial();
		material.setDiffuseColor(diffuseColor);
		material.setSpecularColor(specularColor);
		box.setMaterial(material);


		group.getChildren().add(box);

        this.centerX = centerX;
        this.centerY = centerY;
        this.length = length;
        this.width = width;
        this.height = height;
        this.xHeading = xHeading;
        this.yHeading = yHeading;
	}

	public void placeShape(CoreModel coreModel,
                           double centerX,
                           double centerY,
                           double xHeading,
                           double yHeading) {

		box.setTranslateX(centerX * coreModel.getResolution());
		box.setTranslateY(centerY * coreModel.getResolution());

        /* box.setX(centerX * coreModel.getResolution() -
                coreModel.getResolution() * 0.5);
        box.setY(centerY * coreModel.getResolution() -
                coreModel.getResolution() * 0.5);*/



        /*
        double xx = box.getLocalToSceneTransform().getMxx();
        double xy = box.getLocalToSceneTransform().getMxy();
        double oldAngle = Math.atan2(-xy, xx);

        Rotate rotate = new Rotate();
        rotate.setPivotX(centerX * coreModel.getResolution());
        rotate.setPivotY(centerY * coreModel.getResolution());
        double newAngle = Math.atan2(xHeading, yHeading);
        rotate.setAngle(newAngle - oldAngle);
        box.getTransforms().add(rotate);*/


        this.centerX = centerX;
        this.centerY = centerY;
        this.xHeading = xHeading;
        this.yHeading = yHeading;
	}
	
	public Group getShape() {
		
		return group;
	}
	
	@Override
	public void changeSelectionMode(SelectionStates selectionState) { }

	@Override
	public String getIdentification() {
	
		return id;
	}

	@Override
	public List<Node> getClickableShapes() {
		
		return null;
	}

	@Override
	public LinkedHashMap<String, String> getDataProperties() {
		
		return null;
	}

	private double getHeadingAngle() {
	    return Math.atan2(xHeading, yHeading);
    }

}
