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
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import tum.cms.sim.momentum.visualization.handler.SelectionHandler.SelectionStates;
import tum.cms.sim.momentum.visualization.model.CoreModel;
import tum.cms.sim.momentum.visualization.model.CustomizationModel;
import tum.cms.sim.momentum.visualization.model.geometry.ShapeModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class CarModel extends ShapeModel {

	protected static final String stringHeadingAngle = "headingAngle";
	protected static final String stringLength = "length";
	protected static final String stringWidth = "width";
	protected static final String stringHeight = "height";

	private static Point2D groundVector = new Point2D(1.0,0.0);

    private CustomizationModel customizationModel = null;

	private Box body = null;
	private Group carShape = null;
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

		body.setVisible(isVisible);
	}

	public CarModel(String id) {
		
		this.id = id;
	}


	public void createShape(CoreModel coreModel,
			double positionX,
			double positionY,
			double length,
			double width,
			double height,
			double headingX,
			double headingY,
            CustomizationModel customizationModel) {

	    this.customizationModel = customizationModel;

		this.length = length;
		this.width = width;
		this.height = height;

		this.body = createBody(coreModel.getResolution(), width, height, length);
		carShape = new Group(this.body);

		placeShape(coreModel, positionX, positionY, headingX, headingY);
	}

	public void placeShape(CoreModel coreModel,
                           double positionX,
                           double positionY,
                           double headingX,
                           double headingY) {

		carShape.setTranslateX(positionX * coreModel.getResolution());
		carShape.setTranslateY(positionY * coreModel.getResolution());

		double angle = calculateAngle(headingX, headingY);
		if(!Double.isNaN(angle)) {
			this.angle = angle;
		}
		else {
			angle = this.angle;
		}

		this.carShape.setRotate(this.angle);


        this.positionX = positionX;
        this.positionY = positionY;
        this.headingX = headingX;
        this.headingY = headingY;
	}

	@Override
	public Group getShape() {
		return carShape;
	}
	
	@Override
	public void changeSelectionMode(SelectionStates selectionState) {
		this.body.materialProperty().unbind();

		switch(selectionState) {
			case NotSelected:


				this.body.materialProperty().bind(this.customizationModel.carBodyMaterialProperty());

				break;
			case Selected:

				this.body.materialProperty().bind(this.customizationModel.selectedCarBodyMaterialProperty());

				break;
		}
	}

	@Override
	public String getIdentification() {
	
		return id;
	}

	@Override
	public List<Node> getClickableShapes() {

		ArrayList<Node> clickableShapes = new ArrayList<Node>();
		clickableShapes.add(this.body);

		return clickableShapes;
	}

	@Override
	public LinkedHashMap<String, String> getDataProperties() {

		LinkedHashMap<String, String> details = new LinkedHashMap<>();

		details.put(ShapeModel.nameDetails, this.id);
		details.put(ShapeModel.positionXDetails, Double.toString(this.positionX));
		details.put(ShapeModel.positionYDetails, Double.toString(this.positionY));
		details.put(ShapeModel.headingXDetails, Double.toString(this.headingX));
		details.put(ShapeModel.headingYDetails, Double.toString(this.headingY));
		details.put(CarModel.stringHeadingAngle, Double.toString(this.angle));
		details.put(CarModel.stringLength, Double.toString(this.length));
		details.put(CarModel.stringWidth, Double.toString(this.width));
		details.put(CarModel.stringHeight, Double.toString(this.height));

		return details;
	}

    private Box createBody(double resolution,
						   double width,
						   double height,
						   double length)
	{
		Box createdBody = new Box(width * resolution,
				height * resolution,
				length * resolution);

        createdBody.materialProperty().bind(this.customizationModel.carBodyMaterialProperty());
        createdBody.getTransforms().add(new Rotate(90, Rotate.X_AXIS));

		return createdBody;
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
