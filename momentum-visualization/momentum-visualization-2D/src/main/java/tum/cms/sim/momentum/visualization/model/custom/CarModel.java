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

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import tum.cms.sim.momentum.visualization.handler.SelectionHandler.SelectionStates;
import tum.cms.sim.momentum.visualization.model.CoreModel;
import tum.cms.sim.momentum.visualization.model.geometry.ShapeModel;

import java.util.LinkedHashMap;
import java.util.List;

public class CarModel extends ShapeModel {

	private static Point2D groundVector = new Point2D(1.0,0.0);
	private static Color diffuseColor = Color.DARKBLUE;
	private static Color specularColor = Color.BLUE;

	private Box box = null;
	private Group carShape = new Group();
	private String id = null;

	// car properties
	private double length;
	private double width;
	private double height;

    private double positionX;	// center of the vehicle
    private double positionY;	// center of the vehicle
	private double headingX;
	private double headingY;
	private double angle;

	@Override
	public void setVisibility(boolean isVisible) {

		box.setVisible(isVisible);
	}

	public CarModel(String rowColumnId) {
		
		id = rowColumnId;
	}


	public void createShape(CoreModel coreModel,
			double positionX,
			double positionY,
			double length,
			double width,
			double height,
			double headingX,
			double headingY) {

		/* box = new Box(positionX * coreModel.getResolution() -
				coreModel.getResolution() * 0.5,
                positionY * coreModel.getResolution() -
				coreModel.getResolution() * 0.5,
                width * coreModel.getResolution(),
                length * coreModel.getResolution());*/

		this.box = createBody(coreModel.getResolution(), width, height, length);

		this.positionX = positionX;
		this.positionY = positionY;
		this.length = length;
		this.width = width;
		this.height = height;
		this.headingX = this.headingX;
		this.headingY = this.headingY;

		this.angle = calculateAngle(headingX, headingY);

		if(!Double.isNaN(this.angle)) {

			this.angle = 0.0;
		}


		carShape.getChildren().add(this.box);

		// set to position
		carShape.setTranslateX(positionX * coreModel.getResolution());
		carShape.setTranslateY(positionY * coreModel.getResolution());
		carShape.setTranslateZ(1.0 * height * coreModel.getResolution());

		carShape.setRotate(this.angle);

	}

	public void placeShape(CoreModel coreModel,
                           double centerX,
                           double centerY,
                           double xHeading,
                           double yHeading) {

		carShape.setTranslateX(centerX * coreModel.getResolution());
		carShape.setTranslateY(centerY * coreModel.getResolution());

		double angle = calculateAngle(headingX, headingY);
		if(!Double.isNaN(angle)) {
			this.angle = angle;
		}
		else {
			angle = this.angle;
		}

		this.carShape.setRotate(this.angle);


        this.positionX = centerX;
        this.positionY = centerY;
        this.headingX = xHeading;
        this.headingY = yHeading;
	}
	
	public Group getShape() {
		
		return carShape;
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
	    return Math.atan2(headingX, headingY);
    }

    private Box createBody(double resolution,
						   double width,
						   double height,
						   double length)
	{
		Box body = new Box(width * resolution,
				height * resolution,
				length * resolution);

		// adjust color
		body.materialProperty().unbind();
		PhongMaterial material = new PhongMaterial();
		material.setDiffuseColor(diffuseColor);
		material.setSpecularColor(specularColor);
		body.setMaterial(material);

		body.getTransforms().add(new Rotate(90, Rotate.X_AXIS));

		return body;
	}

	private double calculateAngle(double headingX, double headingY) {

		double angle = CarModel.groundVector.angle(headingX, headingY);

		if(headingX <= 0 && headingY < 0) {
			angle *= -1;//angle += 90;
		}
		else if(headingX > 0 && headingY < 0) {
			angle *= -1;//angle += 270;
		}

		// + 90 because looks at Y zero if 0 degree
		angle += 90;

		return angle;
	}
}
