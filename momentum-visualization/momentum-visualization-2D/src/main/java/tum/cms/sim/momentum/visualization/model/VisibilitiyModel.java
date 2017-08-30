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

	private final BooleanProperty gridVisibility = new SimpleBooleanProperty(this, "gridVisibility", true);
	private final BooleanProperty latticeVisibility = new SimpleBooleanProperty(this, "latticeVisibility", true);
	private final BooleanProperty graphVisibility = new SimpleBooleanProperty(this, "graphVisibility", true);

	public void fillFromPreferences() {

		setGridVisibility(Boolean.valueOf(UserPreferenceHandler.loadProperty(PropertyType.gridVisibility)));
		setLatticeVisibility(Boolean.valueOf(UserPreferenceHandler.loadProperty(PropertyType.latticeVisibility)));
		setGraphVisibility(Boolean.valueOf(UserPreferenceHandler.loadProperty(PropertyType.graphVisibility)));
	}

	public void createForPreferences() {
		
		UserPreferenceHandler.putProperty(PropertyType.gridVisibility, getGridVisibility().toString());
		UserPreferenceHandler.putProperty(PropertyType.latticeVisibility, getLatticeVisibility().toString());
		UserPreferenceHandler.putProperty(PropertyType.graphVisibility, getGraphVisibility().toString());
	}

	public BooleanProperty gridVisibilityProperty() {
		return gridVisibility;
	}

	public Boolean getGridVisibility() {
		return gridVisibility.get();
	}

	public void setGridVisibility(Boolean visibility) {
		gridVisibility.set(visibility);
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

	public void resetVisibility() {
		setGridVisibility(true);
		setLatticeVisibility(true);
		setGraphVisibility(true);
	}
}
