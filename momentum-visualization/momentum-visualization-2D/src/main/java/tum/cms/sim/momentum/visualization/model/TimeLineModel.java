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

import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import tum.cms.sim.momentum.visualization.enums.Smoothness;
import tum.cms.sim.momentum.visualization.enums.SpeedUp;

public class TimeLineModel {

	private final BooleanProperty playing = new SimpleBooleanProperty(this, "playing", false);
	private final BooleanProperty recording = new SimpleBooleanProperty(this, "recording", false);
	private final BooleanProperty isAnimating = new SimpleBooleanProperty(this, "isAnimating", false);
	private final DoubleProperty timeStepDuration = new SimpleDoubleProperty(this, "timeStepDuration", 0.1);
	private final DoubleProperty timeStepMultiplicator = new SimpleDoubleProperty(this, "timeStepMultiplicator", 1.0);
	private final BooleanProperty sliderDragged = new SimpleBooleanProperty(this, "sliderDragged", false);
	private final DoubleProperty endTime = new SimpleDoubleProperty(this, "endTime", 0.0);
	private final DoubleProperty startTime = new SimpleDoubleProperty(this, "startTime", 0.0);
	private final ObjectProperty<SpeedUp> selectedSpeedUp = new SimpleObjectProperty<SpeedUp>(this, "selectedSpeedUp",
			SpeedUp.X32);
	private final ObjectProperty<Smoothness> selectedSmoothness = new SimpleObjectProperty<Smoothness>(this,
			"selectedSmoothness", Smoothness.Cubic);
	private List<String> speedUp = SpeedUp.SPEEDUP_VALUES;
	private List<String> smoothness = Smoothness.SMOOTHNESS_VALUES;
	
	public BooleanProperty playingProperty() {
		return playing;
	}

	public Double getStartTime() {
		return startTime.get();
	}

	public Boolean getPlaying() {
		return playing.get();
	}

	public void setPlaying(Boolean playing) {
		this.playing.set(playing);
	}

	public BooleanProperty recordingProperty() {
		return recording;
	}

	public Boolean getRecording() {
		return recording.get();
	}

	public void setRecording(Boolean recording) {
		this.recording.set(recording);
	}

	public BooleanProperty isAnimatingProperty() {
		return isAnimating;
	}

	public Boolean getIsAnimating() {
		return isAnimating.get();
	}

	public void setIsAnimating(Boolean isAnimating) {
		this.isAnimating.set(isAnimating);
	}

	public DoubleProperty timeStepDurationProperty() {
		return timeStepDuration;
	}

	public Double getTimeStepDuration() {
		return timeStepDuration.get();
	}

	public void setTimeStepDuration(Double timeStepDuration) {
		this.timeStepDuration.set(timeStepDuration);
	}

	public DoubleProperty timeStepMultiplicatorProperty() {
		return timeStepMultiplicator;
	}

	public Double getTimeStepMultiplicator() {
		return timeStepMultiplicator.get();
	}

	public void setTimeStepMultiplicator(Double timeStepMultiplicator) {
		this.timeStepMultiplicator.set(timeStepMultiplicator);
	}

	public BooleanProperty sliderDraggedProperty() {
		return sliderDragged;
	}

	public Boolean getSliderDragged() {
		return sliderDragged.get();
	}

	public void setSliderDragged(Boolean sliderDragged) {
		this.sliderDragged.set(sliderDragged);
	}

	public DoubleProperty endTimeProperty() {
		return endTime;
	}

	public Double getEndTime() {
		return endTime.get();
	}

	public void setEndTime(Double endTime) {
		this.endTime.set(endTime);
	}

	public ObjectProperty<SpeedUp> selectedSpeedUpProperty() {
		return selectedSpeedUp;
	}

	public SpeedUp getSelectedSpeedUp() {
		return selectedSpeedUp.get();
	}

	public void setSelectedSpeedUp(SpeedUp selectedSpeedUp) {
		this.selectedSpeedUp.set(selectedSpeedUp);
	}

	public ObjectProperty<Smoothness> selectedSmoothnessProperty() {
		return selectedSmoothness;
	}

	public Smoothness getSelectedSmoothness() {
		return selectedSmoothness.get();
	}

	public void setSelectedSmoothness(Smoothness selectedSmoothness) {
		this.selectedSmoothness.set(selectedSmoothness);
	}

	public List<String> getSpeedUp() {
		return speedUp;
	}

	public List<String> getSmoothness() {
		return smoothness;
	}
}
