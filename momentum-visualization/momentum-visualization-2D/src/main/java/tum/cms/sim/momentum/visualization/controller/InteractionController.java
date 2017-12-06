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

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ResourceBundle;

import javafx.animation.ParallelTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import tum.cms.sim.momentum.visualization.enums.Smoothness;
import tum.cms.sim.momentum.visualization.enums.SpeedUp;
import tum.cms.sim.momentum.visualization.handler.PlaybackHandler;
import tum.cms.sim.momentum.visualization.model.GestureModel;
import tum.cms.sim.momentum.visualization.model.TimeLineModel;
import tum.cms.sim.momentum.visualization.utility.AnimationCalculations;
import tum.cms.sim.momentum.visualization.view.dialogControl.InformationDialogCreator;

public class InteractionController implements Initializable {

	// view
	@FXML
	private HBox box;
	@FXML
	private Slider slider;
	@FXML
	private Button stop;
	@FXML
	private Button play;
	@FXML
	private Button reset;
	@FXML
	private Button leftStep;
	@FXML
	private Button rightStep;
	@FXML
	private TextField textFieldTimeStepPointer;
	@FXML
	private TextField textFieldMaxTimeStep;
	@FXML
	private TextField textFieldZoomFactor;
	@FXML
	private ComboBox<Smoothness> smoothBox;
	@FXML
	private ComboBox<SpeedUp> speedBox;
	@FXML
	private ProgressIndicator spinner;

	private DoubleBinding timeLineBinding = null;
	
	private ParallelTransition walkingAnimation = null;
	private ParallelTransition playBackAnimation = null;

	// Controller
	private PlaybackHandler playbackHandler = null;
	private CoreController coreController;

	// Model
	@FXML
	private TimeLineModel timeLineModel;

	// Listeners
	private ChangeListener<Number> smoothnessSelectionIndexListener = new ChangeListener<Number>() {

		@Override
		public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
			timeLineModel.setSelectedSmoothness(Smoothness.values()[newValue.intValue()]);

		}
	};

	private ChangeListener<Number> speedSelectionIndexListener = new ChangeListener<Number>() {

		@Override
		public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {

			timeLineModel.setSelectedSpeedUp(SpeedUp.values()[newValue.intValue()]);
		}
	};

	public TimeLineModel getTimeLineModel() {
		return timeLineModel;
	}

	public void bindCoreModel(CoreController coreController) {
		
		this.coreController = coreController;

		box.disableProperty()
				.bind(coreController.getCoreModel().csvLoadedProperty().not()
				.or(coreController.getCoreModel().layoutLoadedProperty().not()));

		textFieldZoomFactor.textProperty()
				.bind(Bindings.format("%.4f", coreController.getPlaybackController().getGestureModel().getScaleProperty()));

	}

	@Override
	public void initialize(URL arg0, ResourceBundle resource) {

		stop.disableProperty().bind(timeLineModel.playingProperty().not().or(timeLineModel.isAnimatingProperty().not()));

		reset.disableProperty().bind(timeLineModel.playingProperty().or(timeLineModel.isAnimatingProperty()));
		play.disableProperty().bind(timeLineModel.playingProperty().or(timeLineModel.isAnimatingProperty()));
		leftStep.disableProperty().bind(timeLineModel.playingProperty().or(timeLineModel.isAnimatingProperty()));
		rightStep.disableProperty().bind(timeLineModel.playingProperty().or(timeLineModel.isAnimatingProperty()));
		slider.disableProperty().bind(timeLineModel.playingProperty().or(timeLineModel.isAnimatingProperty()));
		speedBox.disableProperty().bind(timeLineModel.playingProperty().or(timeLineModel.isAnimatingProperty()));
		smoothBox.disableProperty().bind(timeLineModel.playingProperty().or(timeLineModel.isAnimatingProperty()));

		slider.blockIncrementProperty()
				.bind(timeLineModel.timeStepMultiplicatorProperty().multiply(timeLineModel.timeStepDurationProperty()));
		slider.majorTickUnitProperty()
				.bind(timeLineModel.timeStepMultiplicatorProperty().multiply(timeLineModel.timeStepDurationProperty()));
		slider.setMinorTickCount(0);
		slider.valueProperty().addListener(onSliderValueChangeListener);
		
     	textFieldTimeStepPointer.textProperty().bind(Bindings.format("%.2f", slider.valueProperty()));
		textFieldTimeStepPointer.setOnAction(onTextFieldEnterPressed);
		textFieldTimeStepPointer.focusedProperty().addListener(onTextFieldTimeStepFocus);
		textFieldMaxTimeStep.textProperty().bind(Bindings.format("/ %.2f", timeLineModel.endTimeProperty()));

		textFieldZoomFactor.setOnAction(onTextFieldEnterPressed);
		textFieldZoomFactor.focusedProperty().addListener(onTextFieldZoomFactorFocus);

		speedBox.getSelectionModel().selectedIndexProperty()
				.addListener(speedSelectionIndexListener);
		speedBox.getSelectionModel().select(SpeedUp.X32);

		smoothBox.getSelectionModel().selectedIndexProperty().addListener(smoothnessSelectionIndexListener);
		smoothBox.getSelectionModel().select(Smoothness.Cubic);

		playbackHandler = new PlaybackHandler(slider.valueProperty(), slider.blockIncrementProperty(),
				slider.maxProperty(), timeLineModel.isAnimatingProperty(), timeLineModel.playingProperty());
		
        timeLineBinding = new DoubleBinding() {
                    {
                           super.bind(slider.valueProperty());
                    }
                    
                    @Override
                    public void dispose() {
                    	
                           super.unbind(slider.valueProperty());
                 }
                    @Override
                    protected double computeValue() {
                    	
                           return roundTimelineValue((slider.getValue() / slider.getMajorTickUnit()) * timeLineModel.getTimeStepMultiplicator());
                    }
       };
	}
	
	private ChangeListener<Number> onSliderValueChangeListener = new ChangeListener<Number>() {

		@Override
		public void changed(ObservableValue<? extends Number> arg0, Number oldNumber, Number newNumber) {
			if (slider.getValue() < slider.getMax()) {

				double roundedSlider = roundTimelineValue(
						(slider.getValue() / slider.getMajorTickUnit()) * timeLineModel.getTimeStepMultiplicator());
				
					try {
						
						coreController.getLayerConfigurationController().updateTrajectories();
					} 
					catch (Exception e) {
						
						e.printStackTrace();
					}

//				InteractionController.this.timeLineModel.setCurrentTime(roundedSlider);

				InteractionController.this.startPlaying(roundedSlider);
			}
		}
	};

	private EventHandler<ActionEvent> onTextFieldEnterPressed = new EventHandler<ActionEvent>() {

		@Override
		public void handle(ActionEvent arg0) {
			box.requestFocus();
		}
	};

	private ChangeListener<Boolean> onTextFieldTimeStepFocus = new ChangeListener<Boolean>() {

		@Override
		public void changed(ObservableValue<? extends Boolean> arg0, Boolean focusLost, Boolean focusGained) {

			if (focusGained) {
				textFieldTimeStepPointer.textProperty().unbind();
			}

			if (focusLost) {
				NumberFormat currentNumberFormat = NumberFormat.getInstance();

				double newTimeStepValue = 0;
				try {
					newTimeStepValue = currentNumberFormat.parse(textFieldTimeStepPointer.getText()).doubleValue();
				} catch (ParseException e) {
					InformationDialogCreator.createErrorDialog("Animation", "The time step must be a valid number");
					newTimeStepValue = slider.getValue();
				}

				if (newTimeStepValue > slider.getMin() && newTimeStepValue < slider.getMax()) {
					slider.setValue(newTimeStepValue);
				}

				if (newTimeStepValue <= slider.getMin()) {
					slider.setValue(slider.getMin());
				}

				if (newTimeStepValue >= slider.getMax()) {
					slider.setValue(slider.getMax());
				}

				textFieldTimeStepPointer.textProperty().set(String.valueOf(slider.getValue()));
				textFieldTimeStepPointer.textProperty().bind(Bindings.format("%.2f", slider.valueProperty()));
			}
		}
	};

	private ChangeListener<Boolean> onTextFieldZoomFactorFocus = new ChangeListener<Boolean>() {

		@Override
		public void changed(ObservableValue<? extends Boolean> arg0, Boolean focusLost, Boolean focusGained) {

			// TODO: How can those be accessed from the ExtendedCanvas?
			double maxScale = 15.0d;
			double minScale = 0.00001d;

			if (focusGained) {
				textFieldZoomFactor.textProperty().unbind();
			}

			if (focusLost) {
				GestureModel gestureModel = coreController.getPlaybackController().getGestureModel();
				NumberFormat currentNumberFormat = NumberFormat.getInstance();

				double oldZoomFactor = gestureModel.getScale();
				double zoomFactor = 0.;

				try {
					zoomFactor = currentNumberFormat.parse(textFieldZoomFactor.getText()).doubleValue();
				} catch (ParseException e) {
					InformationDialogCreator.createErrorDialog("Visualization",
							"The zoom factor must be a valid number");
					zoomFactor = oldZoomFactor;
				}

				if (zoomFactor < minScale || zoomFactor > maxScale) {
					zoomFactor = oldZoomFactor;
					InformationDialogCreator.createWarningDialog("Visualization",
							"The scaling factor must be between " + minScale + " and " + maxScale);
				}

				gestureModel.setScale(zoomFactor);
				textFieldZoomFactor.textProperty().bind(Bindings.format("%.4f", gestureModel.getScaleProperty()));
			}
		}
	};

	@FXML
	public void onPlay(MouseEvent event) {
		 
		if (slider.getValue() < slider.getMax()) {

			this.timeLineModel.setPlaying(true);
			slider.setValue(slider.getValue() + slider.getBlockIncrement());
		}
	}

	private void startPlaying(double timeStep) {

		try {

			playBackAnimation = AnimationCalculations.calculateVisualizationOfTimeStep(timeStep, coreController);
		}
		catch (Exception e) {

			e.printStackTrace();
		}

		if (timeLineModel.getPlaying()) {

			playBackAnimation.setOnFinished(playbackHandler);
			timeLineModel.setIsAnimating(true);
		}

		spinner.setVisible(false);
		playBackAnimation.play();
	}

	public void startRecording() {

		this.timeLineModel.setRecording(true);
		this.onPlay(null);
	}

	@FXML
	public void onPause(MouseEvent event) {

		this.timeLineModel.setIsAnimating(false);
		this.timeLineModel.setPlaying(false);
	}

	@FXML
	public void onReset(MouseEvent event) {

		this.timeLineModel.setPlaying(false);
		slider.setValue(slider.getMin());
	}

	@FXML
	public void onLeftStep(MouseEvent event) {

		if (slider.getValue() >= slider.getMin()) {

			slider.setValue(slider.getValue() - slider.getBlockIncrement());
		}
	}

	@FXML
	public void onRightStep(MouseEvent event) {

		if (slider.getValue() <= slider.getMax()) {

			slider.setValue(slider.getValue() + slider.getBlockIncrement());
		}
	}
	
	public double roundTimelineValue(double continiuousSliderValue) {

		int preDigits = Integer.toString((int) continiuousSliderValue).length();
		BigDecimal sliderValue = new BigDecimal(continiuousSliderValue);
		double roundedSlider = sliderValue.round(new MathContext(preDigits, RoundingMode.HALF_EVEN)).doubleValue();
		
		int diff = (int) (roundedSlider % timeLineModel.getTimeStepMultiplicator());

		if (diff != 0) {

			roundedSlider = roundedSlider - diff;
		}
		return roundedSlider;
	}
	
	public void resetTimeLineModel() throws Exception {
		
		coreController.clearSimulationOutputReaders();
		timeLineModel.isAnimatingProperty().set(false);
		timeLineModel.playingProperty().set(false);
		timeLineModel.recordingProperty().set(false);
		timeLineModel.sliderDraggedProperty().set(false);
		timeLineModel.endTimeProperty().set(0.0);
		timeLineModel.timeStepMultiplicatorProperty().set(1.0);
		timeLineModel.timeStepDurationProperty().set(0.1);
	}

	public double getTimeLineBindingValue() {
		return timeLineBinding.get();
	}

	public void setTimeLineBindingValue(DoubleBinding timeLineBinding) {
		this.timeLineBinding = timeLineBinding;
	}
}
