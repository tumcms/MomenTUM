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
import java.util.List;

import org.apache.commons.math3.util.FastMath;
import javafx.scene.DepthTest;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.transform.Rotate;
import tum.cms.sim.momentum.configuration.scenario.ObstacleConfiguration;
import tum.cms.sim.momentum.utility.geometry.operation.GeometryAdditionals;
import tum.cms.sim.momentum.visualization.controller.CoreController;
import tum.cms.sim.momentum.visualization.controller.CustomizationController;

public class ObstacleModel {
	
	private static double height = 1.0;
	
	private Polyline obstacleTopShape = null;
	private Polyline obstacleBottomShape = null;
	private List<Node> obstacleBorders = null;

	private int identification = 0;
	
	public int getIdentification() {
		return identification;
	}
	
	public Node getObstacleTopShape() {
		return obstacleTopShape;
	}
	
	public Node getObstacleBottomShape() {
		return obstacleBottomShape;
	}
	
	public List<Node> getObstacleBorderShape() {
		return obstacleBorders;
	}
	
	public void setVisibility(boolean isVisible) {
		
		obstacleTopShape.setVisible(isVisible);
		obstacleBottomShape.setVisible(isVisible);
		obstacleBorders.forEach(border -> border.setVisible(isVisible));
	}
	
	public ObstacleModel(ObstacleConfiguration obstacleConfiguration,
			CoreController coreController,
			CustomizationController customizationController) {
		
		identification = obstacleConfiguration.getId();
		
		ArrayList<Double> points = new ArrayList<Double>(obstacleConfiguration.getPoints().size() * 2);
		
		for(int iter = 0; iter < obstacleConfiguration.getPoints().size(); iter++) {
			
			points.add(obstacleConfiguration.getPoints().get(iter).getX() * coreController.getCoreModel().getResolution());
			points.add(obstacleConfiguration.getPoints().get(iter).getY() * coreController.getCoreModel().getResolution());
		}
		
		obstacleTopShape = new Polyline(); 
		obstacleBottomShape = new Polyline(); 
		obstacleBorders = new ArrayList<>();
		
		switch (obstacleConfiguration.getType()) {
		
		default:
		case Solid:
			
			obstacleTopShape.fillProperty().bind(customizationController.getCustomizationModel().obstacleColorProperty());
			obstacleTopShape.setStroke(customizationController.getCustomizationModel().obstacleColorProperty().get());//Color.BLACK);

			obstacleBottomShape.setStroke(customizationController.getCustomizationModel().obstacleColorProperty().get());//Color.BLACK);

				points.add(points.get(0));
				points.add(points.get(1));

			break;

		case Wall:
			
			//obstacleTopShape.setStroke(Color.BLACK);
			//obstacleBottomShape.setStroke(Color.BLACK);
			obstacleTopShape.setStroke(customizationController.getCustomizationModel().obstacleColorProperty().get());//Color.BLACK);
			obstacleBottomShape.setStroke(customizationController.getCustomizationModel().obstacleColorProperty().get());//Color.BLACK);
			
			
			obstacleTopShape.setStroke(Color.BLACK);
			obstacleBottomShape.setStroke(Color.BLACK);
			obstacleBottomShape.setStrokeWidth(35 * coreController.getCoreModel().getResolution());
			obstacleTopShape.setStrokeWidth(35 * coreController.getCoreModel().getResolution());
			break;
		
		case VirtualWall: 
			
			obstacleTopShape.strokeProperty().bind(customizationController.getCustomizationModel().virutalObstacleColorProperty());
			obstacleBottomShape.strokeProperty().bind(customizationController.getCustomizationModel().virutalObstacleColorProperty());
			break;
		}

		for(int iter = 0; iter < points.size() - 2; iter = iter + 2) {
			
			double width = FastMath.pow(
						FastMath.pow(points.get(iter) - points.get(iter + 2), 2) + 
						FastMath.pow(points.get(iter + 1) - points.get(iter + 3), 2)
					, 0.5);
			
 			Rectangle border = new Rectangle(0,0,
					width, 
					height * coreController.getCoreModel().getResolution());
			
 			double radiant = GeometryAdditionals.angleBetween0And360CCW(1, 0,
 					points.get(iter + 2) - points.get(iter),
 					points.get(iter + 3) - points.get(iter + 1));
 			
 			double angle = GeometryAdditionals.translateToDegree(radiant);
 			border.getTransforms().add(new Rotate(angle, Rotate.Z_AXIS));
			border.getTransforms().add(new Rotate(90, Rotate.X_AXIS));
 			
 			border.fillProperty().bind(customizationController.getCustomizationModel().obstacleColorProperty());

			border.setTranslateX(points.get(iter));
			border.setTranslateY(points.get(iter + 1));
			border.setStroke(Color.BLACK);
			border.setStrokeWidth(0.5);

			border.setStrokeLineJoin(StrokeLineJoin.MITER);
			border.setStrokeLineCap(StrokeLineCap.ROUND);
			
			obstacleBorders.add(border);
		}
		
		obstacleBottomShape.getPoints().addAll(points);
		obstacleBottomShape.setDepthTest(DepthTest.ENABLE);
		obstacleBottomShape.setStrokeLineCap(StrokeLineCap.ROUND);
		obstacleBottomShape.setStrokeWidth(0.5);
		
		obstacleTopShape.getPoints().addAll(points);	
		obstacleTopShape.setDepthTest(DepthTest.ENABLE);
		obstacleTopShape.setTranslateZ(1.0 * height * coreController.getCoreModel().getResolution());
		obstacleTopShape.setStrokeWidth(0.5);
		obstacleTopShape.setStrokeLineJoin(StrokeLineJoin.MITER);
		obstacleTopShape.setStrokeLineCap(StrokeLineCap.ROUND);	
	}
}
