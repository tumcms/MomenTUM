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
import java.util.ResourceBundle;

import javafx.fxml.Initializable;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import tum.cms.sim.momentum.visualization.enums.PropertyType;
import tum.cms.sim.momentum.visualization.handler.UserPreferenceHandler;
import tum.cms.sim.momentum.visualization.model.CustomizationModel;

/**
 * Loads and stores customization values. Accesses the properties file through {@link UserPreferenceHandler} 
 * and stores the data in a {@link CustomizationModel}.
 * @author Martin Sigl
 *
 */
public class CustomizationController implements Initializable {
	
	private CustomizationModel customizationModel;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
	}
	
	/**
	 * Resets all customization values to its default values defined in {@link PropertyType}
	 */
	public void resetCustomizedValues() {
	
		UserPreferenceHandler.putProperty(PropertyType.selectedColor, PropertyType.getDefaultValue(PropertyType.selectedColor));
		UserPreferenceHandler.putProperty(PropertyType.axisColor, PropertyType.getDefaultValue(PropertyType.axisColor));
		UserPreferenceHandler.putProperty(PropertyType.latticeColor, PropertyType.getDefaultValue(PropertyType.latticeColor));
		UserPreferenceHandler.putProperty(PropertyType.pedestrianDirectionColor, PropertyType.getDefaultValue(PropertyType.pedestrianDirectionColor));
		UserPreferenceHandler.putProperty(PropertyType.graphColor, PropertyType.getDefaultValue(PropertyType.graphColor));
		UserPreferenceHandler.putProperty(PropertyType.destinationColor, PropertyType.getDefaultValue(PropertyType.destinationColor));
		UserPreferenceHandler.putProperty(PropertyType.originColor, PropertyType.getDefaultValue(PropertyType.originColor));
		UserPreferenceHandler.putProperty(PropertyType.intermediateColor, PropertyType.getDefaultValue(PropertyType.intermediateColor));
		UserPreferenceHandler.putProperty(PropertyType.obstacleColor, PropertyType.getDefaultValue(PropertyType.obstacleColor));
		UserPreferenceHandler.putProperty(PropertyType.trajectoryColor, PropertyType.getDefaultValue(PropertyType.trajectoryColor));
		UserPreferenceHandler.putProperty(PropertyType.trajectoryIsRandomColor, PropertyType.getDefaultValue(PropertyType.trajectoryIsRandomColor));
		UserPreferenceHandler.putProperty(PropertyType.trajectoryThickness, PropertyType.getDefaultValue(PropertyType.trajectoryThickness));
		UserPreferenceHandler.putProperty(PropertyType.trajectoryTimeInterval, PropertyType.getDefaultValue(PropertyType.trajectoryTimeInterval));
		UserPreferenceHandler.putProperty(PropertyType.edgeThickness, PropertyType.getDefaultValue(PropertyType.edgeThickness));
		UserPreferenceHandler.putProperty(PropertyType.vertexSize, PropertyType.getDefaultValue(PropertyType.vertexSize));
		
		fillCustomizationModelFromPreferences();
	}
	
	/**
	 * Loads the customization values from the properties file into the {@link CustomizationModel}
	 */
	public void fillCustomizationModelFromPreferences() {
		
		getCustomizationModel().setSelectedColor(Color.web(UserPreferenceHandler.loadProperty(PropertyType.selectedColor)));
		getCustomizationModel().setAxisColor(Color.web(UserPreferenceHandler.loadProperty(PropertyType.axisColor)));
		getCustomizationModel().setLatticeColor(Color.web(UserPreferenceHandler.loadProperty(PropertyType.latticeColor)));
		PhongMaterial phongMaterial = new PhongMaterial(Color.web(UserPreferenceHandler.loadProperty(PropertyType.phongMaterialColor)));
		phongMaterial.setSpecularColor(Color.GRAY);
		phongMaterial.setSpecularPower(100.0);
		getCustomizationModel().setPedestrianBodyMaterial(phongMaterial);
		PhongMaterial phongMaterialSelected = new PhongMaterial(Color.web(UserPreferenceHandler.loadProperty(PropertyType.selectedColor)));
		phongMaterialSelected.setSpecularColor(Color.GRAY);
		phongMaterialSelected.setSpecularPower(100.);
		getCustomizationModel().setPedestrianDirectionColor(Color.web(UserPreferenceHandler.loadProperty(PropertyType.pedestrianDirectionColor)));
		getCustomizationModel().setGraphColor(Color.web(UserPreferenceHandler.loadProperty(PropertyType.graphColor)));
		getCustomizationModel().setDestinationColor(Color.web(UserPreferenceHandler.loadProperty(PropertyType.destinationColor)));
		getCustomizationModel().setOriginColor(Color.web(UserPreferenceHandler.loadProperty(PropertyType.originColor)));
		getCustomizationModel().setIntermediateColor(Color.web(UserPreferenceHandler.loadProperty(PropertyType.intermediateColor)));
		getCustomizationModel().setObstacleColor(Color.web(UserPreferenceHandler.loadProperty(PropertyType.obstacleColor)));
		getCustomizationModel().setTrajectoryColor(Color.web(UserPreferenceHandler.loadProperty(PropertyType.trajectoryColor)));
		getCustomizationModel().setTrajectoryIsRandomColor(Boolean.valueOf(UserPreferenceHandler.loadProperty(PropertyType.trajectoryIsRandomColor)));
		getCustomizationModel().setTrajectoryThickness(Double.valueOf(UserPreferenceHandler.loadProperty(PropertyType.trajectoryThickness)));
		getCustomizationModel().setTrajectoryTimeInterval(Double.valueOf(UserPreferenceHandler.loadProperty(PropertyType.trajectoryTimeInterval)));
		getCustomizationModel().setEdgeThickness(Double.valueOf(UserPreferenceHandler.loadProperty(PropertyType.edgeThickness)));
		getCustomizationModel().setVertexSize(Double.valueOf(UserPreferenceHandler.loadProperty(PropertyType.vertexSize)));
		getCustomizationModel().setInformationColor(Color.web(UserPreferenceHandler.loadProperty(PropertyType.informationColor)));
	}
	
	public CustomizationModel getCustomizationModel() {
		if(customizationModel == null) {
			
			customizationModel = new CustomizationModel();
		}
		return customizationModel;
	}

}
