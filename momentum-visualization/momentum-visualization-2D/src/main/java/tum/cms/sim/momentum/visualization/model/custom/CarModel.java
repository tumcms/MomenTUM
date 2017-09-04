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
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import tum.cms.sim.momentum.visualization.calculation.DensityColor;
import tum.cms.sim.momentum.visualization.handler.SelectionHandler.SelectionStates;
import tum.cms.sim.momentum.visualization.model.CoreModel;
import tum.cms.sim.momentum.visualization.model.geometry.ShapeModel;

import java.util.LinkedHashMap;
import java.util.List;

public class CarModel extends ShapeModel {

	private Rectangle cell = null;
	private Group group = new Group();
	private String id = null;

	// temporary
	private double density = 0.5;
	private double maximalDensity = 1;

	// car properties
    double centerX;
    double centerY;
    double length;
    double width;
    double xHeading;
    double yHeading;

	@Override
	public void setVisibility(boolean isVisible) {

		cell.setVisible(isVisible);
	}

	public CarModel(String rowColumnId) {
		
		id = rowColumnId;
	}


	public void createShape(CoreModel coreModel,
			double cellCenterX,
			double cellCenterY,
			double length,
			double width,
			double xHeading,
			double yHeading) {

		cell = new Rectangle(cellCenterX * coreModel.getResolution() -
				coreModel.getResolution() * 0.5,
				cellCenterY * coreModel.getResolution() -
				coreModel.getResolution() * 0.5,
                width * coreModel.getResolution(),
                length * coreModel.getResolution());

        //Create a Rotate Object
        Rotate rotate = new Rotate();
        rotate.setPivotX(cellCenterX); //Pivot X Top-Left corner
        rotate.setPivotY(cellCenterY); //Pivot Y
        rotate.setAngle(Math.atan2(xHeading, yHeading)); //Angle degrees
        cell.getTransforms().add(rotate);

		cell.setTranslateZ(0.002 * coreModel.getResolution());
		cell.setStrokeWidth(0.0);

		Paint densityColor = DensityColor.getColor(density, maximalDensity, 0.25, true);
		cell.setFill(densityColor);
		
		group.getChildren().add(cell);
	}

	public void placeShape(CoreModel coreModel, double cellCenterX, double cellCenterY, double xHeading,
                           double yHeading) {

        cell.setX(cellCenterX * coreModel.getResolution() -
                coreModel.getResolution() * 0.5);
        cell.setY(cellCenterY * coreModel.getResolution() -
                coreModel.getResolution() * 0.5);

        //Create a Rotate Object
        Rotate rotate = new Rotate();
        rotate.setPivotX(cellCenterX); //Pivot X Top-Left corner
        rotate.setPivotY(cellCenterY); //Pivot Y
        rotate.setAngle(Math.atan2(xHeading, yHeading)); //Angle degrees
        cell.getTransforms().add(rotate);



		Paint densityColor = DensityColor.getColor(density, maximalDensity, 0.25, true);
		cell.setFill(densityColor);
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

}
