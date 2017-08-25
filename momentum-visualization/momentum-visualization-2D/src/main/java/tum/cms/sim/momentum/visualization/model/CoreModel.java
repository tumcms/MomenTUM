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

import java.util.Locale;
import java.util.ResourceBundle;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Contains the data for the whole application, like the resolution 
 * and preferred widths and heights for the specific views.
 */
public class CoreModel {

	// resolution and size parameters
	private IntegerProperty resolution = new SimpleIntegerProperty(this, "resolution", 1);
	private Double detailsWidth = null;
	private Double layerWidth = null;
	private final SimpleDoubleProperty switchDetailsView = new SimpleDoubleProperty(false, "switchDetailsView");
	private final SimpleDoubleProperty switchLayerView = new SimpleDoubleProperty(false, "switchLayerView");
	private final SimpleDoubleProperty switchDetailInverseView = new SimpleDoubleProperty(false,
			"switchDetailInverseView");
	
	private final BooleanProperty csvLoaded = new SimpleBooleanProperty(this, "csvLoaded", false);
	private final BooleanProperty layoutLoaded = new SimpleBooleanProperty(this, "layoutLoaded", false);

	public CoreModel() {
		ResourceBundle bundle = null;
		
		try {
			bundle = ResourceBundle.getBundle("tum.cms.sim.momentum.visualization.view.resources", Locale.ENGLISH);
		} catch (Exception e) {
			e.printStackTrace();
		}

		detailsWidth = Double.parseDouble(bundle.getString("core.grid.left.minWidth"));
		layerWidth = Double.parseDouble(bundle.getString("core.grid.right.minWidth"));
		switchDetailsView.set(detailsWidth);
		switchDetailInverseView.set(0.0);
		switchLayerView.set(layerWidth);
	}
	
	public IntegerProperty resolutionProperty() {
		return resolution;
	}

	public Integer getResolution() {
		return resolution.get();
	}

	public void setResolution(Integer resolution) {
		this.resolution.set(resolution);
	}

	public BooleanProperty csvLoadedProperty() {
		return csvLoaded;
	}

	public Boolean getCsvLoaded() {
		return csvLoaded.get();
	}

	public void setCsvLoaded(Boolean csvLoaded) {
		this.csvLoaded.set(csvLoaded);
	}

	public BooleanProperty layoutLoadedProperty() {
		return layoutLoaded;
	}

	public Boolean getLayoutLoaded() {
		return layoutLoaded.get();
	}

	public void setLayoutLoaded(Boolean layoutLoaded) {
		this.layoutLoaded.set(layoutLoaded);
	}

	public Double getDetailsWidth() {
		return detailsWidth;
	}

	public void setDetailsWidth(Double detailsWidth) {
		this.detailsWidth = detailsWidth;
	}

	public Double getLayerWidth() {
		return layerWidth;
	}

	public void setLayerWidth(Double layerWidth) {
		this.layerWidth = layerWidth;
	}

	public SimpleDoubleProperty getSwitchDetailsView() {
		return switchDetailsView;
	}

	public SimpleDoubleProperty getSwitchLayerView() {
		return switchLayerView;
	}

	public SimpleDoubleProperty getSwitchDetailInverseView() {
		return switchDetailInverseView;
	}

}
