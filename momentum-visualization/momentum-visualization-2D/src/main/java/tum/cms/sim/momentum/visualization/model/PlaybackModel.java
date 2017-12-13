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

package tum.cms.sim.momentum.visualization.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.geometry.Point2D;
import tum.cms.sim.momentum.utility.csvData.CsvType;
import tum.cms.sim.momentum.visualization.model.geometry.AreaModel;
import tum.cms.sim.momentum.visualization.model.geometry.TaggedAreaModel;
import tum.cms.sim.momentum.visualization.model.geometry.EdgeModel;
import tum.cms.sim.momentum.visualization.model.geometry.LatticeModel;
import tum.cms.sim.momentum.visualization.model.geometry.ObstacleModel;
import tum.cms.sim.momentum.visualization.model.geometry.PedestrianModel;
import tum.cms.sim.momentum.visualization.model.geometry.ShapeModel;
import tum.cms.sim.momentum.visualization.model.geometry.TrajectoryModel;
import tum.cms.sim.momentum.visualization.model.geometry.VertexModel;

public class PlaybackModel {

	private double miniForAnimation = 0.00001;
	private double maxForAnimation = 6.0;
	private final DoubleProperty maxSizeX = new SimpleDoubleProperty(this, "maxSizeX", 1.0);
	private final BooleanProperty is3DView = new SimpleBooleanProperty(this, "is3DView", false);
	private final DoubleProperty maxSizeY = new SimpleDoubleProperty(this, "maxSizeY", 1.0);
		
	private final MapProperty<String, AreaModel> areaShapes = new SimpleMapProperty<String, AreaModel>(this,
			"areaShapes", FXCollections.observableHashMap());
	private final MapProperty<String, TaggedAreaModel> taggedAreaShapes = new SimpleMapProperty<String, TaggedAreaModel>(this,
			"taggedAreaShapes", FXCollections.observableHashMap());
	private final MapProperty<Integer, VertexModel> vertexShapes = new SimpleMapProperty<Integer, VertexModel>(this,
			"vertexShapes", FXCollections.observableHashMap());
	private final MapProperty<String, EdgeModel> edgeShapes = new SimpleMapProperty<String, EdgeModel>(this,
			"edgeShapes", FXCollections.observableHashMap());
	private final ListProperty<ObstacleModel> obstacleShapes = new SimpleListProperty<ObstacleModel>(this,
			"obstacleShapes", FXCollections.observableArrayList());

	private final MapProperty<String, TrajectoryModel> trajectoryShapes = new SimpleMapProperty<String, TrajectoryModel>(
			this, "trajectoryShapes", FXCollections.observableHashMap());

	private final ListProperty<LatticeModel> latticeShapes = new SimpleListProperty<LatticeModel>(this, "latticeShapes",
			FXCollections.observableArrayList());

	private HashSet<Double> redPedestrianGroupColor = new HashSet<Double>();
	private HashSet<Double> bluePedestrianGroupColor = new HashSet<Double>();
	private HashMap<CsvType, HashMap<String, Point2D>> previousShapePositionPoints = new HashMap<> ();
	private HashMap<CsvType, HashMap<String, Point2D>> nextShapePositionPoints = new HashMap<>();

	private final MapProperty<CsvType, ObservableMap<String, ShapeModel>> customShapesMap = new SimpleMapProperty<CsvType, ObservableMap<String, ShapeModel>>(
			this, "customShapes", FXCollections.observableHashMap());

	private final MapProperty<String, PedestrianModel> pedestrianShapes = new SimpleMapProperty<String, PedestrianModel>(
			this, "pedestrianShapes", FXCollections.observableHashMap());

	public DoubleProperty maxSizeXProperty() {
		return maxSizeX;
	}

	public Double getMaxSizeX() {
		return maxSizeX.get();
	}

	public void setMaxSizeX(Double maxSizeX) {
		this.maxSizeX.set(maxSizeX);
	}

	public BooleanProperty is3DViewProperty() {
		return is3DView;
	}

	public Boolean getIs3DView() {
		return is3DView.get();
	}

	public void setIs3DView(Boolean is3DView) {

		this.is3DView.set(is3DView);
	}

	public DoubleProperty maxSizeYProperty() {
		return maxSizeY;
	}

	public Double getMaxSizeY() {
		return maxSizeY.get();
	}

	public void setMaxSizeY(Double maxSizeY) {
		this.maxSizeY.set(maxSizeY);
	}

	public Double maxSize() {
		return maxSizeX.get() > maxSizeY.get() ? maxSizeX.get() : maxSizeY.get();
	}

	public MapProperty<String, AreaModel> areaShapesProperty() {
		return areaShapes;
	}

	public Map<String, AreaModel> getAreaShapes() {
		return areaShapes.get();
	}

	public void putAreaShapes(Map<String, AreaModel> areaShapes) {
		this.areaShapes.putAll(FXCollections.observableMap(areaShapes));
	}

	public MapProperty<String, TaggedAreaModel> taggedAreaShapesProperty() {
		return taggedAreaShapes;
	}

	public Map<String, TaggedAreaModel> getTaggedAreaShapes() {
		return taggedAreaShapes.get();
	}

	public void putTaggedAreaShapes(Map<String, TaggedAreaModel> taggedAreaShapes) {
		this.taggedAreaShapes.putAll(FXCollections.observableMap(taggedAreaShapes));
	}

	public MapProperty<Integer, VertexModel> vertexShapesProperty() {
		return vertexShapes;
	}

	public Map<Integer, VertexModel> getVertexShapes() {
		return vertexShapes.get();
	}

	public void putVertexShapes(Map<Integer, VertexModel> vertexShapes) {
		this.vertexShapes.putAll(FXCollections.observableMap(vertexShapes));
	}

	public MapProperty<String, EdgeModel> edgeShapesProperty() {
		return edgeShapes;
	}

	public Map<String, EdgeModel> getEdgeShapes() {
		return edgeShapes.get();
	}

	public void putEdgeShapes(Map<String, EdgeModel> edgeShapes) {
		this.edgeShapes.putAll(edgeShapes);
	}

	public ListProperty<ObstacleModel> obstacleShapesProperty() {
		return obstacleShapes;
	}

	public List<ObstacleModel> getObstacleShapes() {
		return obstacleShapes.get();
	}

	public void addObstacleShapes(ObservableList<ObstacleModel> obstacleShapes) {
		this.obstacleShapes.set(obstacleShapes);
	}

	public ListProperty<LatticeModel> latticeShapesProperty() {
		return latticeShapes;
	}

	public List<LatticeModel> getLatticeShapes() {
		return latticeShapes.get();
	}

	public void addLatticeShapes(ObservableList<LatticeModel> latticeShapes) {
		this.latticeShapes.addAll(latticeShapes);
	}

	public MapProperty<String, TrajectoryModel> trajectoryShapesProperty() {
		return trajectoryShapes;
	}

	public Map<String, TrajectoryModel> getTrajectoryShapes() {
		return trajectoryShapes.get();
	}

	public void putTrajectoryShapes(Map<String, TrajectoryModel> trajectoryShapes) {
		this.trajectoryShapes.putAll(FXCollections.observableMap(trajectoryShapes));
	}

	public MapProperty<String, PedestrianModel> pedestrianShapesProperty() {
		return pedestrianShapes;
	}

	public Map<String, PedestrianModel> getPedestrianShapes() {
		return pedestrianShapes.get();
	}

	public void clearPedestrians() {

		pedestrianShapes.clear();
	}

	public MapProperty<CsvType, ObservableMap<String, ShapeModel>> setCustomShapesMapProperty() {
		return customShapesMap;
	}

	public Map<CsvType, ObservableMap<String, ShapeModel>> getCustomShapesMap() {
		return customShapesMap.get();
	}

	public ObservableMap<String, ShapeModel> getSpecificCustomShapesMap(CsvType type) {

		return this.getCustomShapesMap().get(type);
	}

	/**
	 * Adds a new custom shape observable for a new csv type if this does not exists
	 * @param type
	 * @return true if a new observable was created, otherwise false
	 */
	public boolean addCustomShapes(CsvType type) {

		if(this.customShapesMap.putIfAbsent(type, FXCollections.observableHashMap()) == null) {

			return true;
		}

		return false;
	}

	public void clearCustom() {

		customShapesMap.clear();
	}

	public HashMap<CsvType, HashMap<String, Point2D>> getPreviousShapePositionPoints() {
		return previousShapePositionPoints;
	}

	public HashMap<String, Point2D> getPreviousSpecificShapePositionPoints(CsvType type) {
		return previousShapePositionPoints.get(type);
	}

	public void setPreviousSpecificShapePositionPoints(CsvType type, HashMap<String, Point2D> previousPedestrianPoints) {
		this.previousShapePositionPoints.put(type, previousPedestrianPoints);
	}

	public HashMap<CsvType, HashMap<String, Point2D>> getNextShapePositionPoints() {
		return nextShapePositionPoints;
	}

	public HashMap<String, Point2D> getNextSpecificShapePositionPoints(CsvType type) {
		if(nextShapePositionPoints.get(type) == null) {
			this.nextShapePositionPoints.put(type, new HashMap<>());
		}
		return nextShapePositionPoints.get(type);
	}

	public void setNextSpecificShapePositionPoints(CsvType type, HashMap<String, Point2D> overNextPedestrianPoints) {
		this.nextShapePositionPoints.put(type, overNextPedestrianPoints);
	}

	public double getMiniForAnimation() {
		return miniForAnimation;
	}

	public double getMaxForAnimation() {
		return maxForAnimation;
	}

	public HashSet<Double> getRedPedestrianGroupColor() {
		return redPedestrianGroupColor;
	}

	public void setRedPedestrianGroupColor(HashSet<Double> redPedestrianGroupColor) {
		this.redPedestrianGroupColor = redPedestrianGroupColor;
	}

	public HashSet<Double> getBluePedestrianGroupColor() {
		return bluePedestrianGroupColor;
	}

	public void setBluePedestrianGroupColor(HashSet<Double> bluePedestrianGroupColor) {
		this.bluePedestrianGroupColor = bluePedestrianGroupColor;
	}

}
