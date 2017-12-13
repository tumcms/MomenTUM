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
import org.apache.commons.math3.util.FastMath;
import tum.cms.sim.momentum.configuration.model.output.WriterSourceConfiguration.OutputType;
import tum.cms.sim.momentum.utility.csvData.reader.SimulationOutputCluster;
import tum.cms.sim.momentum.visualization.controller.CustomizationController;
import tum.cms.sim.momentum.visualization.enums.Smoothness;
import tum.cms.sim.momentum.visualization.handler.SelectionHandler.SelectionStates;
import tum.cms.sim.momentum.visualization.model.CustomizationModel;
import tum.cms.sim.momentum.visualization.model.PlaybackModel;
import tum.cms.sim.momentum.visualization.utility.TrajectoryCubicCurve;
import javafx.animation.Interpolator;
import javafx.animation.PathTransition;
import javafx.animation.PathTransition.OrientationType;
import javafx.animation.RotateTransition;
import javafx.animation.Transition;
import javafx.geometry.Point2D;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

public class PedestrianModel extends ShapeModel {

	private static Double pedestrianHeight = 3.0;
	private static Point2D groundVector = new Point2D(1.0,0.0);
	private static TrajectoryCubicCurve trajectoryCubicCurve= new TrajectoryCubicCurve();
	private static double jumpDistance = 2.0; //meter in case pedestrian positions are far away
	private String displayId;
	private String identificationId;

	private double positionX;
	private double positionY;
	private double headingX;
	private double headingY;
	private double angle;
	private double widthRadius = 0.23;// * 10.0;
	private double depthRadius = 0.23;// * 10.0;
	private Integer targetId = null;
	private Integer groupId = -1;
	private Integer seedId = -1;
	private Boolean leader = null;
	private Boolean isGroupColored = false;
	private Boolean isSeedColored = false;

	private String behavior = "-"; // none
	private String motoric = "-"; // walking
	
	public void setMotoric(String motoric) {
		this.motoric = motoric;
	}

	public void setBehavior(String behavior) {
		this.behavior = behavior;
	}

	private static HashMap<Integer, Color> groupColorMap = new HashMap<>();
	
	public static HashMap<Integer, Color> getGroupColorMap() {
		return groupColorMap;
	}

	private static HashMap<Integer, Color> seedColorMap = new HashMap<>();
	
	public static HashMap<Integer, Color> getSeedColorMap() {
		return seedColorMap;
	}

	public static void setSeedColorMap(HashMap<Integer, Color> seedColorMap) {
		PedestrianModel.seedColorMap = seedColorMap;
	}

	private CustomizationModel customizationModel = null;
	
	public Boolean getIsGroupColored() {
		return isGroupColored;
	}

	public void setIsGroupColored(Boolean isGroupColored) {
		
		this.isGroupColored = isGroupColored;
		this.swapGroupColorBinding();
	}
	
	public Boolean getIsSeedColored() {
		return isSeedColored;
	}

	public void setIsSeedColored(Boolean isSeedColored) {
		this.isSeedColored = isSeedColored;
		this.swapSeedColorBinding();
	}

	public Boolean isLeader() {
		return leader;
	}
	
	public void setLeader(boolean leader) {
		this.leader = leader;
	}
	public Integer getTargetId() {
		return targetId;
	}
	
	public Integer getGroupId() {
		return groupId;
	}

	public Integer getSeedId() {
		return seedId;
	}
	
	public double getWidthRadius() {
		return widthRadius;
	}

	public double getDepthRadius() {
		return depthRadius;
	}

	public double getPositionX() {
		return positionX;
	}

	public double getPositionY() {
		return positionY;
	}

	public double getHeadingX() {
		return headingX;
	}
	
	public double getHeadingY() {
		return headingY;
	}
	
	public double getAngle() {
		return angle;
	}

	public String getDisplayId() {
		return displayId;
	}

	private Group pedestrianShape = null;
	
	private Cylinder pedestrianBody  = null;
	
	public Group getPedestrianShape() {
		
		return this.pedestrianShape;
	}
	
	public Node getPedestrianBody() {
		return pedestrianBody;
	}
	
	private Polygon pedestrianHeading = null;
	
	public Node getPedestrianHeading() {
		return pedestrianHeading;
	}

	Point2D previousPlacement = null;
	Point2D overNextPlacement = null;
	
	public void setAdjacentPlacements(Point2D previousPlacement, Point2D overNextPlacement) {

		this.previousPlacement = previousPlacement;
		this.overNextPlacement = overNextPlacement;
	}
	
	private Shape trajectory = null;
	
	public Shape getTrajectoryShape() {
		return this.trajectory;
	}
	
	public PedestrianModel(String displayId, String hashId) {
		this.displayId = displayId;
		this.identificationId = hashId;
	}

	public PlaybackModel playbackModel = null;
	
	@Override
	/**
	 * returns the global unique id of this PedestrianModel
	 */
	public String getIdentification() {
		return this.identificationId;
	}
	
	/**
	 * Returns the cluster-unique id
	 */
	public String getClusterIdentification() {
		return this.displayId.split("\\.")[0];
	}
	
	
	@Override
	public void changeSelectionMode(SelectionStates selectionState) {
		
		this.pedestrianBody.materialProperty().unbind();

		switch(selectionState) {
		case NotSelected:
			
			this.pedestrianBody.materialProperty().bind(customizationModel.pedestrianBodyMaterialProperty());
			
			if(this.trajectory != null) {
				
				this.playbackModel.getTrajectoryShapes()
					.forEach((id,shape)-> shape.setVisibility(true));
//				this.trajectory.setVisible(false);
			}
			break;
		case Selected:
			
			this.pedestrianBody.materialProperty().bind(customizationModel.selectedPedestrianBodyMaterial());
			
			if(this.trajectory != null) {
				
				this.playbackModel.getTrajectoryShapes()
					.forEach((id,shape)-> shape.setVisibility(false));
				this.trajectory.setVisible(true);
			}
			break;
		}
	}

	@Override
	public ArrayList<Node> getClickableShapes() {
		
		ArrayList<Node> clickableShapes = new ArrayList<Node>();
		clickableShapes.add(this.pedestrianBody);
		clickableShapes.add(this.pedestrianHeading);

		return clickableShapes;
	}

	@Override
	public LinkedHashMap<String, String> getDataProperties() {
		
		LinkedHashMap<String, String> details = new LinkedHashMap<>();
		
		details.put(ShapeModel.nameDetails, displayId);
		details.put(ShapeModel.positionXDetails, Double.toString(this.positionX));
		details.put(ShapeModel.positionYDetails, Double.toString(this.positionY));
		details.put(ShapeModel.headingXDetails, Double.toString(this.headingX));
		details.put(ShapeModel.headingYDetails, Double.toString(this.headingY));
		details.put(ShapeModel.widthRadius, Double.toString(this.widthRadius));
		details.put(ShapeModel.depthRadius, Double.toString(this.depthRadius));
		details.put(ShapeModel.targetId, this.targetId != null ? Integer.toString(this.targetId) : null);
		details.put(ShapeModel.groupId, this.groupId != null ? Integer.toString(this.groupId) : null);
		details.put(ShapeModel.seedId, this.seedId != null ? Integer.toString(this.seedId) : null);
		details.put(ShapeModel.leader, this.leader != null ? Boolean.toString(this.leader) : null);
		details.put(ShapeModel.behavior, this.behavior != null ? this.behavior : null);
		details.put(ShapeModel.motoric, this.motoric !=  null ? this.motoric : null);
		
		return details;
	}
	
	public void swapGroupColorBinding() {
		
		if(this.isGroupColored && this.groupId != -1 && groupColorMap.containsKey(this.groupId)) {
			
			if(this.pedestrianBody.materialProperty().isBound()) {
				
				this.pedestrianBody.materialProperty().unbind();
				PhongMaterial phongMaterial = new PhongMaterial(groupColorMap.get(this.groupId));
				phongMaterial.setSpecularColor(Color.GRAY);
				phongMaterial.setSpecularPower(100.0);
				this.pedestrianBody.setMaterial(phongMaterial);
			}
		}
		else {
		
			this.pedestrianBody.materialProperty().bind(customizationModel.pedestrianBodyMaterialProperty());
		}
	}
	
	public void swapSeedColorBinding() {
		
		if(this.isSeedColored && this.seedId != -1 && seedColorMap.containsKey(this.seedId)) {
			
			if(this.pedestrianBody.materialProperty().isBound()) {
				
				this.pedestrianBody.materialProperty().unbind();
				PhongMaterial phongMaterial = new PhongMaterial(seedColorMap.get(this.seedId));
				phongMaterial.setSpecularColor(Color.GRAY);
				phongMaterial.setSpecularPower(100.0);
				this.pedestrianBody.setMaterial(phongMaterial);
			}
		}
		else {
		
			this.pedestrianBody.materialProperty().bind(customizationModel.pedestrianBodyMaterialProperty());
		}
	}
	
	public void updateProperties(SimulationOutputCluster dataStep) {
		
		this.targetId = dataStep.getIntegerData(this.displayId, OutputType.targetID.name());
		this.groupId = dataStep.getIntegerData(this.displayId, OutputType.groupID.name());
		this.seedId = dataStep.getIntegerData(this.displayId, OutputType.seedID.name());
		this.leader = dataStep.getBooleanData(this.displayId, OutputType.leader.name());
		this.behavior = dataStep.getStringData(this.displayId, OutputType.behavior.name());
		this.motoric = dataStep.getStringData(this.displayId, OutputType.motoric.name());
	}
	
	public void createShape(double positionX, 
			double positionY, 
			double headingX,
			double headingY, 
			Double bodyRadius,
			double resolution,
			CustomizationController customizationController) {
		
		if(bodyRadius != null) {
			
			this.widthRadius = bodyRadius;
			this.depthRadius = bodyRadius;
		}
		
		this.customizationModel = customizationController.getCustomizationModel();
		pedestrianBody = createBody(resolution, positionX, positionY, widthRadius, depthRadius, customizationController);
		pedestrianHeading = createHeading(resolution, positionX, positionY, widthRadius, depthRadius, customizationController);

		this.positionX = positionX;
		this.positionY = positionY;
		this.headingX = headingX;
		this.headingY = headingY;

		this.angle = calculateAngle(headingX, headingY);
		
		if(!Double.isNaN(this.angle)) {
			
			this.angle = 0.0;
		}
		
		this.pedestrianShape = new Group(pedestrianBody, pedestrianHeading);
		this.pedestrianShape.setCache(true);
		this.pedestrianShape.setCacheHint(CacheHint.DEFAULT);
		this.pedestrianShape.setRotate(this.angle);
	}

	public void setTrajectory(TrajectoryModel trajectoryModel, PlaybackModel playbackModel) {
		
		this.trajectory = trajectoryModel.getTrajectory();	
		this.playbackModel = playbackModel;
	}
	
	public void placeShape(double positionX, 
			double positionY, 
			double headingX,
			double headingY, 
			double resolution) {
	
		this.pedestrianShape.setTranslateX(this.pedestrianShape.getTranslateX() +
				positionX * resolution - this.positionX * resolution);
		
		this.pedestrianShape.setTranslateY(this.pedestrianShape.getTranslateY() +
				positionY * resolution - this.positionY * resolution);
		
		double angle = calculateAngle(headingX, headingY);
		
		if(!Double.isNaN(angle)) {
			
			this.angle = angle;
		}
		else {
			
			angle = this.angle;
		}
		
		this.pedestrianShape.setRotate(this.angle);

		this.positionX = positionX;
		this.positionY = positionY;
		this.headingX = headingX;
		this.headingY = headingY;
	}

	public void animateShape(ArrayList<Transition> pedestrianTransitions,
			double positionX, 
			double positionY, 
			double headingX,
			double headingY, 
			double durationInSeconds,
			Smoothness smoothness,
			double resolution) {
		
		double angle = calculateAngle(headingX, headingY);
	
		if(!Double.isNaN(angle)) {
			
			this.angle = angle;
		}
		else {
			
			angle = this.angle;
		}
	
		Transition walkingBodyTransition = this.createWalkingTansition(resolution, 
				durationInSeconds,
				this.positionX,
				this.positionY,
				positionX,
				positionY,
				smoothness,
				this.pedestrianShape);
		pedestrianTransitions.add(walkingBodyTransition);
		
		RotateTransition bodyRotationTransition = this.createHeadingTransition(durationInSeconds,
				this.angle, 
				angle,
				this.pedestrianShape);
		pedestrianTransitions.add(bodyRotationTransition);

		this.positionX = positionX;
		this.positionY = positionY;
		this.headingX = headingX;
		this.headingY = headingY;
	}

	private double calculateAngle(double headingX, double headingY) {
		
		double angle = PedestrianModel.groundVector.angle(headingX, headingY);
		
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
	
	private Cylinder createBody(double resolution,
			double positionX,
			double positionY,
			double widthRadius,
			double depthRadius,
			CustomizationController customizationController) {
	
		Cylinder body = new Cylinder(widthRadius * resolution, pedestrianHeight);

		body.setTranslateX(positionX * resolution);
		body.setTranslateY(positionY * resolution);
		body.setTranslateZ(1.0 * pedestrianHeight * 0.5);
		body.getTransforms().add(new Rotate(90, Rotate.X_AXIS));
		body.materialProperty().bind(customizationController.getCustomizationModel().pedestrianBodyMaterialProperty());

		return body;
	}
	
	private Polygon createHeading(double resolution,
			double positionX,
			double positionY,
			double widthRadius,
			double depthRadius,
			CustomizationController customizationController) {
		
		Polygon headingPoylgon = new Polygon();
		
		headingPoylgon.getPoints().add(positionX * resolution - (widthRadius * 0.4 * resolution));
		headingPoylgon.getPoints().add(positionY * resolution + (depthRadius * 0.5 * resolution));
		
		headingPoylgon.getPoints().add(positionX * resolution + (widthRadius * 0.4 * resolution));
		headingPoylgon.getPoints().add(positionY * resolution + (depthRadius * 0.5 * resolution));
		
		headingPoylgon.getPoints().add(positionX * resolution);// + (widthRadius * 0.1 * resolution));
		headingPoylgon.getPoints().add(positionY * resolution - (depthRadius * 0.5 * resolution));

		headingPoylgon.getPoints().add(positionX * resolution - (widthRadius * 0.5 * resolution));
		headingPoylgon.getPoints().add(positionY * resolution + (depthRadius * 0.5 * resolution));
		
		headingPoylgon.fillProperty().bind(customizationController.getCustomizationModel().pedestrianDirectionColorProperty());
		headingPoylgon.setStrokeLineJoin(StrokeLineJoin.ROUND);
		
		headingPoylgon.setTranslateZ(1.0 * pedestrianHeight);
		return headingPoylgon;
	}
	
	private RotateTransition createHeadingTransition(double durationInSeconds,
			double oldAngle,
			double newAngle,
			Node rotatingShape) {
		
		RotateTransition rotateTransition = new RotateTransition(Duration.seconds(durationInSeconds - 0.001));
		
		rotateTransition.setInterpolator(Interpolator.LINEAR);
		rotateTransition.setFromAngle(oldAngle);
		rotateTransition.setToAngle(newAngle);
		rotateTransition.setAutoReverse(true);
		rotateTransition.setNode(rotatingShape);

		return rotateTransition;
	}
	
	private Transition createWalkingTansition(double resolution,
			double durationInSeconds,
			double oldX,
			double oldY,
			double newX,
			double newY,
			Smoothness smoothness,
			Node movingShape) {
		
		Interpolator durationInterpolator = Interpolator.LINEAR;
		Path animation = new Path();
		
		if(durationInSeconds > 0.0) { 
			
			durationInSeconds -= 0.001;
		}
		else {
			
			durationInSeconds = 0.005; // minmal timestep
		}
		
		if(previousPlacement == null || overNextPlacement == null) {
			
			smoothness = Smoothness.Linear;
		}
		
		boolean jump = FastMath.sqrt(FastMath.pow(oldX - newX, 2.0) + FastMath.pow(oldY - newY, 2.0)) > jumpDistance;
		
		if(jump) { // TODO still the pedestrian flash at the old position after in case of jumps on play
			
			smoothness = Smoothness.None;
		}
		
		switch(smoothness) {
		
		case Cubic:
			
			durationInterpolator = Interpolator.LINEAR;
			CubicCurveTo curve = trajectoryCubicCurve.buildCurve(previousPlacement, overNextPlacement,
					resolution, oldX, oldY, newX, newY);	
			animation.getElements().add(new MoveTo(oldX * resolution, oldY * resolution));
			animation.getElements().add(curve);		
			break;
			
		case Linear:
			
			durationInterpolator = Interpolator.LINEAR;
			animation.getElements().add(new MoveTo(oldX * resolution, oldY * resolution));
			animation.getElements().add(new LineTo(newX * resolution, newY * resolution));
			break;
			
		case None:
			
			durationInterpolator = Interpolator.DISCRETE;
			
			if(jump) {
				
				animation.getElements().add(new MoveTo(newX * resolution, newY * resolution));
				animation.getElements().add(new LineTo(newX * resolution, newY * resolution));
			}
			else {

				animation.getElements().add(new MoveTo(oldX * resolution, oldY * resolution));
				animation.getElements().add(new LineTo(newX * resolution, newY * resolution));
			}
			
			break;
			
		default:
			break;
		}
		
		PathTransition pathTransition = new PathTransition(Duration.seconds(durationInSeconds), animation);			
		pathTransition.setInterpolator(durationInterpolator);
		pathTransition.setAutoReverse(true);
		pathTransition.setOrientation(OrientationType.NONE);
		pathTransition.setNode(movingShape);	

		return pathTransition;
	}

	@Override
	public void setVisibility(boolean isVisible) {

		this.pedestrianBody.setVisible(isVisible);
		this.pedestrianHeading.setVisible(isVisible);
	}

	public boolean isVisible() {
		
		return this.pedestrianShape.isVisible();
	}
}
