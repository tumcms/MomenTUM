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

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.paint.PhongMaterial;
import tum.cms.sim.momentum.visualization.enums.PropertyType;
import tum.cms.sim.momentum.visualization.handler.UserPreferenceHandler;

/**
 * Stores customization values. If a value is changed, it automatically updates the properties file.
 *
 */
public class CustomizationModel {

	private final ObjectProperty<Color> selectedColor = new SimpleObjectProperty<Color>(this, "selectedColor", Color.RED); 
	private final ObjectProperty<Color> latticeColor = new SimpleObjectProperty<Color>(this, "latticeColor", Color.BLACK);
	private final ObjectProperty<Color> axisColor = new SimpleObjectProperty<Color>(this, "axisColor", Color.LIGHTGRAY); 
	private final ObjectProperty<PhongMaterial> pedestrianBodyMaterial = new SimpleObjectProperty<PhongMaterial>(this, "pedestrianBodyMaterial",  new PhongMaterial(Color.GREEN));
	private final ObjectProperty<PhongMaterial> selectedPedestrianBodyMaterial = new SimpleObjectProperty<PhongMaterial>(this, "selectedPedestrianBodyMaterial", new PhongMaterial(Color.RED));
	private final ObjectProperty<Color> pedestrianDirectionColor = new SimpleObjectProperty<Color>(this, "pedestrianDirectionColor", Color.RED);
	private final ObjectProperty<Color> graphColor = new SimpleObjectProperty<Color>(this, "graphColor", Color.ORANGE);
	private final ObjectProperty<Color> destinationColor = new SimpleObjectProperty<Color>(this, "destinationColor", Color.LIGHTSALMON);
	private final ObjectProperty<Color> originColor = new SimpleObjectProperty<Color>(this, "originColor", Color.LIGHTCYAN);
	private final ObjectProperty<Color> intermediateColor = new SimpleObjectProperty<Color>(this, "intermediateColor", Color.VIOLET);
	private final ObjectProperty<Color> informationColor = new SimpleObjectProperty<Color>(this, "informationColor", Color.BROWN);
	private final ObjectProperty<Color> obstacleColor = new SimpleObjectProperty<Color>(this, "obstacleColor", Color.DARKGRAY);
	private final ObjectProperty<Color> virutalObstacleColor = new SimpleObjectProperty<Color>(this, "virutalObstacleColor", Color.BLUEVIOLET);
	private final ObjectProperty<Paint> trajectoryPaint = new SimpleObjectProperty<Paint>(this, "trajectoryColor", Color.GRAY);
	private final ObjectProperty<Boolean> trajectoryIsRandomColor = new SimpleObjectProperty<Boolean>(this, "trajectoryIsRandomColor", false);
	private final ObjectProperty<Double> trajectoryThickness = new SimpleObjectProperty<Double>(this, "trajectoryThickness", 0.5);
	private final ObjectProperty<Double> edgeThickness = new SimpleObjectProperty<Double>(this, "edgeThickness", 1.);
	private final ObjectProperty<Double> vertexSize = new SimpleObjectProperty<Double>(this, "vertexSize", 3.);

	public ObjectProperty<Color> selectedColorProperty() {
		return selectedColor;
	}
	
	public Color getSelectedColor() {
		return selectedColor.get();
	}
	
	public void setSelectedColor(Color color) {
		selectedColor.set(color);
		UserPreferenceHandler.putProperty(PropertyType.selectedColor, color.toString());
	}

	public ObjectProperty<Color> latticeColorProperty() {
		return latticeColor;
	}

	public Color getLatticeColor() {
		return latticeColor.get();
	}
	
	public void setLatticeColor(Color color) {
		latticeColor.set(color);
		UserPreferenceHandler.putProperty(PropertyType.latticeColor, color.toString());
	}

	public ObjectProperty<Color> axisColorProperty() {
		return axisColor;
	}

	public Color getAxisColor() {
		return axisColor.get();
	}
	
	public void setAxisColor(Color color) {
		axisColor.set(color);
		UserPreferenceHandler.putProperty(PropertyType.axisColor, color.toString());
	}
	
	public ObjectProperty<PhongMaterial> pedestrianBodyMaterialProperty() {
		return pedestrianBodyMaterial;
	}

	public PhongMaterial getPedestrianBodyMaterial() {
		return pedestrianBodyMaterial.get();
	}
	
	public void setPedestrianBodyMaterial(PhongMaterial material) {
		pedestrianBodyMaterial.set(material);
		UserPreferenceHandler.putProperty(PropertyType.phongMaterialColor, material.getDiffuseColor().toString());
	}
	
	public ObjectProperty<PhongMaterial> selectedPedestrianBodyMaterial() {
		return selectedPedestrianBodyMaterial;
	}
	
	public PhongMaterial getSelectedPedestrianBodyMaterial() {
		return selectedPedestrianBodyMaterial.get();
	}
	
	public void setSelectedPedestrianBodyMaterial(PhongMaterial material) {
		selectedPedestrianBodyMaterial.set(material);
		// TODO set for preferences
	}
		
	public ObjectProperty<Color> pedestrianDirectionColorProperty() {
		return pedestrianDirectionColor;
	}

	public Color getPedestrianDirectionColor() {
		return pedestrianDirectionColor.get();
	}
	
	public void setPedestrianDirectionColor(Color color) {
		pedestrianDirectionColor.set(color);
		UserPreferenceHandler.putProperty(PropertyType.pedestrianDirectionColor, color.toString());
	}
	
	public ObjectProperty<Paint> trajectoryColorProperty() {
		return trajectoryPaint;
	}
	
	public Paint getTrajectoryColor() {
		return trajectoryPaint.get();
	}
	
	public void setTrajectoryColor(Color color) {
		trajectoryPaint.set(color);
		UserPreferenceHandler.putProperty(PropertyType.trajectoryColor, color.toString());
	}
	
	public ObjectProperty<Boolean> trajectoryIsRandomColorProperty() {
		return trajectoryIsRandomColor;
	}
	
	public Boolean isTrajectoryRandomColor() {
		return trajectoryIsRandomColor.get();
	}
	
	public void setTrajectoryIsRandomColor(Boolean b) {
		trajectoryIsRandomColor.set(b);
		UserPreferenceHandler.putProperty(PropertyType.trajectoryIsRandomColor, String.valueOf(b));
	}
	
	public ObjectProperty<Double> trajectoryThicknessProperty() {
		return trajectoryThickness;
	}
	
	public double getTrajectoryThickness() {
		return trajectoryThickness.get();
	}
	
	public void setTrajectoryThickness(double thickness) {
		trajectoryThickness.set(thickness);
		UserPreferenceHandler.putProperty(PropertyType.trajectoryThickness, String.valueOf(thickness));
	}

	public ObjectProperty<Color> graphColorProperty() {
		return graphColor;
	}
	
	public Color getGraphColor() {
		return graphColor.get();
	}
	
	public void setGraphColor(Color color) {
		graphColor.set(color);
		UserPreferenceHandler.putProperty(PropertyType.graphColor, color.toString());
	}
	
	public ObjectProperty<Color> destinationColorProperty() {
		return destinationColor;
	}

	public Color getDestinationColor() {
		return destinationColor.get();
	}
	
	public void setDestinationColor(Color color) {
		destinationColor.set(color);
		UserPreferenceHandler.putProperty(PropertyType.destinationColor, color.toString());
	}
	
	public ObjectProperty<Color> originColorProperty() {
		return originColor;
	}

	public Color getOriginColor() {
		return originColor.get();
	}
	
	public void setOriginColor(Color color) {
		originColor.set(color);
		UserPreferenceHandler.putProperty(PropertyType.originColor, color.toString());
	}
	
	public ObjectProperty<Color> intermediateColorProperty() {
		return intermediateColor;
	}

	public Color getIntermediateColor() {
		return intermediateColor.get();
	}

	public void setIntermediateColor(Color color) {
		intermediateColor.set(color);
		UserPreferenceHandler.putProperty(PropertyType.intermediateColor, color.toString());
	}
	
	public ObjectProperty<Color> informationColorProperty() {
		return informationColor;
	}

	public Color getInformationColor() {
		return informationColor.get();
	}
	
	public void setInformationColor(Color color) {
		informationColor.set(color);
		UserPreferenceHandler.putProperty(PropertyType.informationColor, color.toString());
	}
	
	public ObjectProperty<Color> obstacleColorProperty() {
		return obstacleColor;
	}

	public Color getObstacleColor() {
		return obstacleColor.get();
	}
	
	public void setObstacleColor(Color color) {
		obstacleColor.set(color);
		UserPreferenceHandler.putProperty(PropertyType.obstacleColor, color.toString());
	}
	
	public ObjectProperty<Color> virutalObstacleColorProperty() {
		return virutalObstacleColor;
	}

	public Color getVirutalObstacleColor() {
		return virutalObstacleColor.get();
	}
	
	public void setVirutalObstacleColor(Color color) {
		virutalObstacleColor.set(color);
		UserPreferenceHandler.putProperty(PropertyType.virtualObstacleColor, color.toString());
	}
	
	public ObjectProperty<Double> edgeThicknessProperty() {
		return edgeThickness;
	}
	
	public double getEdgeThickness() {
		return edgeThickness.get();
	}
	
	public void setEdgeThickness(Double thickness) {
		edgeThickness.set(thickness);
		UserPreferenceHandler.putProperty(PropertyType.edgeThickness, String.valueOf(thickness));
	}
	
	public ObjectProperty<Double> vertexSizeProperty() {
		return vertexSize;
	}
	
	public double getVertexSize() {
		return vertexSize.get();
	}
	
	public void setVertexSize(Double size) {
		vertexSize.set(size);
		UserPreferenceHandler.putProperty(PropertyType.vertexSize, String.valueOf(size));
	}
}
