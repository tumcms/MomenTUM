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

package tum.cms.sim.momentum.visualization.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.AmbientLight;
import javafx.scene.Node;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import tum.cms.sim.momentum.utility.csvData.CsvType;
import tum.cms.sim.momentum.visualization.handler.SelectionHandler;
import tum.cms.sim.momentum.visualization.model.GestureModel;
import tum.cms.sim.momentum.visualization.model.SnapshotModel;
import tum.cms.sim.momentum.visualization.model.VisibilitiyModel;
import tum.cms.sim.momentum.visualization.model.VisualizationModel;
import tum.cms.sim.momentum.visualization.model.geometry.AreaModel;
import tum.cms.sim.momentum.visualization.model.geometry.EdgeModel;
import tum.cms.sim.momentum.visualization.model.geometry.LatticeModel;
import tum.cms.sim.momentum.visualization.model.geometry.ObstacleModel;
import tum.cms.sim.momentum.visualization.model.geometry.PedestrianModel;
import tum.cms.sim.momentum.visualization.model.geometry.ShapeModel;
import tum.cms.sim.momentum.visualization.model.geometry.TrajectoryModel;
import tum.cms.sim.momentum.visualization.model.geometry.VertexModel;
import tum.cms.sim.momentum.visualization.view.userControl.ExtendedCanvas;

public class VisualizationController implements Initializable {

	@FXML
	private VisualizationModel visualizationModel;
	@FXML
	private AnchorPane playBackPane;
	@FXML
	private ExtendedCanvas extendedCanvas;
	@FXML
	private AnchorPane playbackObjectsPane;
	@FXML
	private AnchorPane movablePane;
	private SubScene viewScene = null;

	private CoreController coreController;
	private static SelectionHandler selectionHandler = null;

	private CustomizationController customizationController = new CustomizationController();
	private SnapshotModel snapshotModel = new SnapshotModel();
	private VisibilitiyModel visibilitiyModel = new VisibilitiyModel();
	private GestureModel gestureModel = new GestureModel();

	public VisualizationModel getVisualizationModel() {
		return visualizationModel;
	}

	public AnchorPane getPlayBackCanvas() {
		return playBackPane;
	}

	public ExtendedCanvas getExtendedCanvas() {
		return extendedCanvas;
	}

	public AnchorPane getPlaybackObjectsPane() {
		return playbackObjectsPane;
	}

	public void putTrajectoriesIntoPedestrians(HashMap<String, TrajectoryModel> trajectories) {
		for (PedestrianModel pedestrianShapeModel : visualizationModel.getPedestrianShapes().values()) {
			pedestrianShapeModel.putTrajectory(trajectories.get(pedestrianShapeModel.getIdentification()));
		}
	}

	public void clearAll() {

		visualizationModel.getAreaShapes().clear();
		visualizationModel.getObstacleShapes().clear();
		visualizationModel.getVertexShapes().clear();
		visualizationModel.getEdgeShapes().clear();
		visualizationModel.getLatticeShapes().clear();
		visualizationModel.getPedestrianShapes().clear();
		visualizationModel.getTrajectoryShapes().clear();
		visualizationModel.getRedPedestrianGroupColor().clear();
		visualizationModel.getBluePedestrianGroupColor().clear();

		if (visualizationModel.getPreviousPedestrianPoints() != null) {

			visualizationModel.getPreviousPedestrianPoints().clear();
		}

		if (visualizationModel.getOverNextPedestrianPoints() != null) {

			visualizationModel.getOverNextPedestrianPoints().clear();
		}

		visualizationModel.is3DViewProperty().set(false);
		visualizationModel.maxSizeXProperty().set(1.0);
		visualizationModel.maxSizeYProperty().set(1.0);
	}

	public void bindCustomShapes(CsvType type) {

		this.visualizationModel.getSpecificCustomShapesMap(type)
				.addListener(new MapChangeListener<String, ShapeModel>() {

					@Override
					public void onChanged(MapChangeListener.Change<? extends String, ? extends ShapeModel> changed) {
						if (changed.getMap().size() > 0) {
							if (!changed.wasRemoved()) {
								playbackObjectsPane.getChildren().add(changed.getValueAdded().getShape());
							}
						} else {
							playbackObjectsPane.getChildren().removeIf(node -> !(node instanceof AnchorPane));
						}
						playBackPane.toBack();
					}
				});
	}

	public void bindCoreModel(CoreController coreController) {

		this.coreController = coreController;

		movablePane.layoutXProperty().set(00.0);
		movablePane.layoutYProperty().set(0.0);

		System.out.println("pbop maxHeight=" + playbackObjectsPane.getMaxHeight());
		System.out.println("pbop maxWidth=" + playbackObjectsPane.getMaxWidth());
		System.out.println("maxHeight=" + movablePane.maxHeightProperty().get());
		System.out.println("maxWidth=" + movablePane.maxWidthProperty().get());

		this.extendedCanvas.setGestureModel(getGestureModel());
		this.extendedCanvas.setRotatableChild(playbackObjectsPane);

		// pivot point and rota, click on object is on the back side and cannot
		// reach the simu objects
		Rotate initalRotate = new Rotate(180.0, Rotate.X_AXIS);
		movablePane.getTransforms().add(initalRotate);

		this.extendedCanvas.setMovableChild(movablePane);
		VisualizationController.selectionHandler = new SelectionHandler(
				coreController.getDetailController().getTableView());
	}

	public static SelectionHandler getSelectionHandler() {

		return selectionHandler;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		playBackPane.getChildren().clear(); // remove extendedCancas als child

		viewScene = new SubScene(extendedCanvas, playBackPane.getWidth(), playBackPane.getHeight(), false,
				SceneAntialiasing.BALANCED);

		viewScene.setFill(Color.TRANSPARENT);
		viewScene.widthProperty().bind(playBackPane.widthProperty());
		viewScene.heightProperty().bind(playBackPane.heightProperty());

		playBackPane.getChildren().add(viewScene);

		AmbientLight ambient = new AmbientLight();

		extendedCanvas.prefWidthProperty().bind(playBackPane.widthProperty());
		extendedCanvas.prefHeightProperty().bind(playBackPane.heightProperty());
		extendedCanvas.getChildren().add(ambient);

		playBackPane.setOnMousePressed(extendedCanvas.getOnMousePressedEventHandler());
		playBackPane.setOnMouseDragged(extendedCanvas.getOnMouseDraggedEventHandler());
		playBackPane.setOnScroll(extendedCanvas.getOnScrollEventHandler());

		extendedCanvas.setFocusTraversable(true);
		extendedCanvas.setOnKeyPressed(extendedCanvas.getOnKeyRotationPressedEventHandler());
		playBackPane.setOnKeyPressed(onKey3DViewKeyEventHandler);

		visualizationModel.areaShapesProperty().addListener(onAreaShapesListChangedListener);
		visualizationModel.obstacleShapesProperty().addListener(onObstracleShapesListChangedListener);
		visualizationModel.pedestrianShapesProperty().addListener(onPedestrianShapesListChangedListener);

		visualizationModel.vertexShapesProperty().addListener(onVertexShapesListChangedListener);
		visualizationModel.edgeShapesProperty().addListener(onEdgeShapesListChangedListener);

		visualizationModel.trajectoryShapesProperty().addListener(onTrajectoryShapesListChangedListener);
		visualizationModel.latticeShapesProperty().addListener(onLatticeShapesListChangedListener);
	}

	private EventHandler<KeyEvent> onKey3DViewKeyEventHandler = new EventHandler<KeyEvent>() {

		@Override
		public void handle(KeyEvent event) {

			if (event.getCode() == KeyCode.I && !VisualizationController.this.visualizationModel.getIs3DView()
					|| event.getCode() == KeyCode.SPACE
							&& VisualizationController.this.visualizationModel.getIs3DView()) {

				Boolean is3DView = VisualizationController.this.visualizationModel.getIs3DView();
				VisualizationController.this.visualizationModel.setIs3DView(!is3DView);
				boolean isZBuffer = false;

				if (VisualizationController.this.visualizationModel.getIs3DView()) {

					isZBuffer = true;
				}

				viewScene.setRoot(new AnchorPane());
				viewScene.widthProperty().unbind();
				viewScene.heightProperty().unbind();
				extendedCanvas.getChildren().clear();
				playBackPane.getChildren().clear(); // remove extendedCancas als
													// child

				viewScene = new SubScene(extendedCanvas, playBackPane.getWidth(), playBackPane.getHeight(), isZBuffer,
						SceneAntialiasing.BALANCED);

				viewScene.setFill(Color.TRANSPARENT);
				viewScene.widthProperty().bind(playBackPane.widthProperty());
				viewScene.heightProperty().bind(playBackPane.heightProperty());

				playBackPane.getChildren().add(viewScene);
				extendedCanvas.getChildren().add(movablePane);

				if (!VisualizationController.this.visualizationModel.getIs3DView()) {

					AmbientLight light = new AmbientLight();
					extendedCanvas.getChildren().add(light);
				}
			}
		}
	};

	public void onMouseClicked(MouseEvent event) {

		// System.out.print("Mouse X: " + event.getX() /
		// this.coreModel.getResolution() + " ");
		// System.out.print("Mouse Y: " + event.getY() /
		// this.coreModel.getResolution());
		// System.out.print(System.lineSeparator() + System.lineSeparator());
		// To Find Pedestrians for instantGenerator use this!
		System.out.print(event.getX() / coreController.getCoreModel().getResolution() + ";"
				+ event.getY() / coreController.getCoreModel().getResolution() + ";1.0;2.0" + System.lineSeparator());
	}

	public void onMouseClickedUnscaled(MouseEvent event) {
		System.out.print("unscaled:");
		System.out.print("Mouse X: " + event.getX());
		System.out.print("Mouse Y: " + event.getY());
		System.out.print(System.lineSeparator() + System.lineSeparator());
	}

	public void centerViewPoint(Rectangle2D boundingBox) {
		// scaling, work in progress
		// System.out.println("scale=" +
		// coreModel.getVisualizationModel().getGestureModel().getScaleProperty().get());
		// double dx = boundingBox.getMaxX() - boundingBox.getMinX();
		// double dy = boundingBox.getMaxY() - boundingBox.getMinY();
		// double scalingFactor = 1.0;
		// double scaleX = 1.0;
		// double scaleY = 1.0;
		// if (viewScene.getWidth() < dx || viewScene.getHeight() < dy) {
		// scaleX = viewScene.getWidth() / dx;
		// scaleY = viewScene.getHeight() / dy;
		// scalingFactor = 0.9 * FastMath.min(viewScene.getWidth() / dx,
		// viewScene.getHeight() / dy);
		// coreModel.getVisualizationModel().getGestureModel().setScale(scalingFactor);
		// }
		// System.out.println("scale=" +
		// coreModel.getVisualizationModel().getGestureModel().getScaleProperty().get());

		// centering
		double centerX = (boundingBox.getMaxX() + boundingBox.getMinX()) / 2;
		double centerY = (boundingBox.getMaxY() + boundingBox.getMinY()) / 2;
		double newX = viewScene.getWidth() / 2 - centerX;
		double newY = viewScene.getHeight() / 2 + centerY;

		movablePane.layoutXProperty().set(newX);
		movablePane.layoutYProperty().set(newY);

	}

	private MapChangeListener<String, AreaModel> onAreaShapesListChangedListener = new MapChangeListener<String, AreaModel>() {

		@Override
		public void onChanged(MapChangeListener.Change<? extends String, ? extends AreaModel> changed) {

			if (changed.getMap().size() > 0) {

				if (!changed.wasRemoved()) {

					changed.getValueAdded().registerSelectable(VisualizationController.selectionHandler);
					playbackObjectsPane.getChildren().add(changed.getValueAdded().getAreaShape());
				} else {
					playbackObjectsPane.getChildren().remove(changed.getValueRemoved().getAreaShape());
				}

			} else {

				playbackObjectsPane.getChildren().removeIf(node -> !(node instanceof AnchorPane));
			}
			playbackObjectsPane.toFront();
			playBackPane.toBack();
		}
	};

	private ListChangeListener<ObstacleModel> onObstracleShapesListChangedListener = new ListChangeListener<ObstacleModel>() {

		@Override
		public void onChanged(ListChangeListener.Change<? extends ObstacleModel> changed) {

			changed.next();
			if (changed.getList().size() > 0) {

				ArrayList<Node> topShapes = new ArrayList<Node>();
				ArrayList<Node> borderShapes = new ArrayList<Node>();
				ArrayList<Node> bottomShapes = new ArrayList<Node>();

				for (ObstacleModel obstacles : changed.getAddedSubList()) {

					if (!changed.wasRemoved()) {

						topShapes.add(obstacles.getObstacleTopShape());
						borderShapes.addAll(obstacles.getObstacleBorderShape());
						bottomShapes.add(obstacles.getObstacleBottomShape());
					}
					changed.next();
				}

				if (topShapes.size() > 0) {

					playbackObjectsPane.getChildren().addAll(bottomShapes);
					playbackObjectsPane.getChildren().addAll(borderShapes);
					playbackObjectsPane.getChildren().addAll(topShapes);
				}
			} else {

				playbackObjectsPane.getChildren().removeIf(node -> !(node instanceof AnchorPane));
			}

			playBackPane.toBack();
		}
	};

	private ListChangeListener<LatticeModel> onLatticeShapesListChangedListener = new ListChangeListener<LatticeModel>() {

		@Override
		public void onChanged(ListChangeListener.Change<? extends LatticeModel> changed) {

			changed.next();
			if (changed.getList().size() > 0) {

				for (LatticeModel lattice : changed.getAddedSubList()) {

					if (!changed.wasRemoved()) {

						playbackObjectsPane.getChildren().addAll(lattice.getLatticeLines());
					}
					changed.next();
				}
			} else {

				playbackObjectsPane.getChildren().removeIf(node -> !(node instanceof AnchorPane));
			}

			playBackPane.toBack();
		}
	};

	private MapChangeListener<String, PedestrianModel> onPedestrianShapesListChangedListener = new MapChangeListener<String, PedestrianModel>() {

		@Override
		public void onChanged(MapChangeListener.Change<? extends String, ? extends PedestrianModel> changed) {

			if (changed.getMap().size() > 0) {

				if (!changed.wasRemoved()) {

					changed.getValueAdded().registerSelectable(VisualizationController.selectionHandler);
					playbackObjectsPane.getChildren().add(changed.getValueAdded().getPedestrianShape());
				}
			} else {

				playbackObjectsPane.getChildren().removeIf(node -> !(node instanceof AnchorPane));
			}

			playBackPane.toBack();
		}
	};

	private MapChangeListener<Integer, VertexModel> onVertexShapesListChangedListener = new MapChangeListener<Integer, VertexModel>() {

		@Override
		public void onChanged(MapChangeListener.Change<? extends Integer, ? extends VertexModel> changed) {

			if (changed.getMap().size() > 0) {

				if (!changed.wasRemoved()) {

					changed.getValueAdded().registerSelectable(VisualizationController.selectionHandler);
					playbackObjectsPane.getChildren().add(changed.getValueAdded().getVertexShape());
				}
			} else {

				playbackObjectsPane.getChildren().removeIf(node -> !(node instanceof AnchorPane));
			}

			playBackPane.toBack();
		}
	};

	private MapChangeListener<String, EdgeModel> onEdgeShapesListChangedListener = new MapChangeListener<String, EdgeModel>() {

		@Override
		public void onChanged(MapChangeListener.Change<? extends String, ? extends EdgeModel> changed) {

			if (changed.getMap().size() > 0) {

				if (!changed.wasRemoved()) {

					changed.getValueAdded().registerSelectable(VisualizationController.selectionHandler);
					playbackObjectsPane.getChildren().add(changed.getValueAdded().getEdgeShape());
				}
			} else {

				playbackObjectsPane.getChildren().removeIf(node -> !(node instanceof AnchorPane));
			}

			playBackPane.toBack();
		}
	};

	private MapChangeListener<String, TrajectoryModel> onTrajectoryShapesListChangedListener = new MapChangeListener<String, TrajectoryModel>() {

		@Override
		public void onChanged(MapChangeListener.Change<? extends String, ? extends TrajectoryModel> changed) {

			if (changed.getMap().size() > 0) {

				if (!changed.wasRemoved()) {

					playbackObjectsPane.getChildren().add(changed.getValueAdded().getTrajectory());
				}
			} else {

				playbackObjectsPane.getChildren().removeIf(node -> !(node instanceof AnchorPane));
			}

			playBackPane.toBack();
		}
	};

	public CustomizationController getCustomizationController() {
		return customizationController;
	}

	public SnapshotModel getSnapshotModel() {
		return snapshotModel;
	}

	public VisibilitiyModel getVisibilitiyModel() {
		return visibilitiyModel;
	}

	public GestureModel getGestureModel() {
		return gestureModel;
	}

}
