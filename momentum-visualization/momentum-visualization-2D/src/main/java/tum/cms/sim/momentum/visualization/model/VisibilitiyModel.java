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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import tum.cms.sim.momentum.visualization.enums.PropertyType;
import tum.cms.sim.momentum.visualization.handler.UserPreferenceHandler;

public class VisibilitiyModel {

	private final BooleanProperty latticeVisibility = new SimpleBooleanProperty(this, "latticeVisibility", true);
	private final BooleanProperty graphVisibility = new SimpleBooleanProperty(this, "graphVisibility", true);
	private final BooleanProperty obstacleVisibility = new SimpleBooleanProperty(this, "obstacleVisibility", true);
	private final BooleanProperty originVisibility = new SimpleBooleanProperty(this, "originVisibility", true);
	private final BooleanProperty intermediateVisibility = new SimpleBooleanProperty(this, "intermediateVisibility", true);
	private final BooleanProperty destinationVisibility = new SimpleBooleanProperty(this, "destinationVisibility", true);

	public void fillFromPreferences() {

		setLatticeVisibility(Boolean.valueOf(UserPreferenceHandler.loadProperty(PropertyType.latticeVisibility)));
		setGraphVisibility(Boolean.valueOf(UserPreferenceHandler.loadProperty(PropertyType.graphVisibility)));
		setObstacleVisibility(Boolean.valueOf(UserPreferenceHandler.loadProperty(PropertyType.obstacleVisibility)));
		setOriginVisibility(Boolean.valueOf(UserPreferenceHandler.loadProperty(PropertyType.originVisibility)));
		setIntermediateVisibility(Boolean.valueOf(UserPreferenceHandler.loadProperty(PropertyType.intermediateVisibility)));
		setDestinationVisibility(Boolean.valueOf(UserPreferenceHandler.loadProperty(PropertyType.destinationVisibility)));
	}

	public void createForPreferences() {
		
		UserPreferenceHandler.putProperty(PropertyType.latticeVisibility, getLatticeVisibility().toString());
		UserPreferenceHandler.putProperty(PropertyType.graphVisibility, getGraphVisibility().toString());
		UserPreferenceHandler.putProperty(PropertyType.obstacleVisibility, getObstacleVisibility().toString());
		UserPreferenceHandler.putProperty(PropertyType.originVisibility, getOriginVisibility().toString());
		UserPreferenceHandler.putProperty(PropertyType.intermediateVisibility, getIntermediateVisibility().toString());
		UserPreferenceHandler.putProperty(PropertyType.destinationVisibility, getDestinationVisibility().toString());
	}

	public BooleanProperty latticeVisibilityProperty() {
		return latticeVisibility;
	}

	public Boolean getLatticeVisibility() {
		return latticeVisibility.get();
	}

	public void setLatticeVisibility(Boolean visibility) {
		latticeVisibility.set(visibility);
	}

	public BooleanProperty graphVisibilityProperty() {
		return graphVisibility;
	}

	public Boolean getGraphVisibility() {
		return graphVisibility.get();
	}

	public void setGraphVisibility(Boolean visibility) {
		graphVisibility.set(visibility);
	}

	public BooleanProperty obstacleVisibilityProperty() {
		return obstacleVisibility;
	}

	public Boolean getObstacleVisibility() {
		return obstacleVisibility.get();
	}

	public void setObstacleVisibility(Boolean visibility) {
		obstacleVisibility.set(visibility);
	}
	public BooleanProperty originVisibilityProperty() {
		return originVisibility;
	}

	public Boolean getOriginVisibility() {
		return originVisibility.get();
	}

	public void setOriginVisibility(Boolean visibility) {
		originVisibility.set(visibility);
	}
	
	public BooleanProperty intermediateVisibilityProperty() {
		return intermediateVisibility;
	}

	public Boolean getIntermediateVisibility() {
		return intermediateVisibility.get();
	}

	public void setIntermediateVisibility(Boolean visibility) {
		intermediateVisibility.set(visibility);
	}
	public BooleanProperty destinationVisibilityProperty() {
		return destinationVisibility;
	}

	public Boolean getDestinationVisibility() {
		return destinationVisibility.get();
	}

	public void setDestinationVisibility(Boolean visibility) {
		destinationVisibility.set(visibility);
	}
	public void resetVisibility() {
		
		setLatticeVisibility(true);
		setGraphVisibility(true);
		setObstacleVisibility(true);
		setOriginVisibility(true);
		setIntermediateVisibility(true);
		setDestinationVisibility(true);
	}
}
