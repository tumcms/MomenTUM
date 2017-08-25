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

import java.io.File;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import tum.cms.sim.momentum.visualization.enums.PropertyType;
import tum.cms.sim.momentum.visualization.handler.UserPreferenceHandler;


public class SnapshotModel {
	
	private final ObjectProperty<File> snapshotPath;
	private final ObjectProperty<String> snapshotName;
	private final ObjectProperty<Double> pixelScale;
	
	public SnapshotModel() {
		snapshotPath = new SimpleObjectProperty<File>(this, "snapshotPath", new File(PropertyType.getDefaultValue(PropertyType.snapshotPath)));
		snapshotName = new SimpleObjectProperty<String>(this, "snapshotName", new String(PropertyType.getDefaultValue(PropertyType.snapShotName)));
		pixelScale = new SimpleObjectProperty<Double>(this, "pixelScale", Double.valueOf(PropertyType.getDefaultValue(PropertyType.snapshotPixelScale)));
	}
	
	public void fillFromPreferences() {
		
		setPixelScale(Double.parseDouble((UserPreferenceHandler.loadProperty(PropertyType.snapshotPixelScale))));
		setSnapshotName(UserPreferenceHandler.loadProperty(PropertyType.snapShotName));
		setSnapshotPath(new File(UserPreferenceHandler.loadProperty(PropertyType.snapshotPath)));
	}
	
	public void createForPreferences() {
		
		UserPreferenceHandler.putProperty(PropertyType.snapshotPixelScale, getPixelScale().toString());
		UserPreferenceHandler.putProperty(PropertyType.snapshotPath, getSnapshotPath().getAbsolutePath());
		UserPreferenceHandler.putProperty(PropertyType.snapShotName, getSnapshotName());
	}
	
	public void resetSnapshotPreferences() {
		setPixelScale(1);
		setSnapshotPath(new File(System.getProperty("user.dir")));
		setSnapshotName(new String("snapshot.png"));
	}
	
	public ObjectProperty<File> snapshotPathProperty() {
		return snapshotPath;
	}
	
	public File getSnapshotPath() {
		return snapshotPath.get();
	}
	
	public void setSnapshotPath(File newPath) {
		snapshotPath.set(newPath);
		createForPreferences();
	}
	
	public ObjectProperty<String> snapshotNameProperty() {
		return snapshotName;
	}
	
	public String getSnapshotName() {
		return snapshotName.get();
	}
	
	public void setSnapshotName(String newName) {
		snapshotName.set(newName);
		createForPreferences();
	}
	
	public File getAbsoluteSnapshotPath() {
		File absolutePath = new File(this.getSnapshotPath().getAbsolutePath() + "\\" + this.getSnapshotName());
		
		return absolutePath;
	}
	
	public ObjectProperty<Double> pixelScaleProperty() {
		return pixelScale;
	}
	
	public Double getPixelScale() {
		return pixelScale.get();
	}
	
	public void setPixelScale(double newPixelScale) {
		pixelScale.set(newPixelScale);
		createForPreferences();
	}

}

