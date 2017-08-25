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

package tum.cms.sim.momentum.visualization.handler;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class PlaybackHandler implements EventHandler<ActionEvent>{
	
	private DoubleProperty chaingingProperty = null;
	private DoubleProperty targetStopValue = null;
	private DoubleProperty incrementProperty = null;
	private BooleanProperty isAnimatingProperty = null;
	private BooleanProperty playingProperty = null;
	private boolean isForward = true;
	private int playbackStack = 0;
	
	public void setForwardPlay() {
		
		this.isForward = true;
	}
	
	public void setBackwardPlay() {
		
		this.isForward = false;
	}
	
	public PlaybackHandler(DoubleProperty chaingingProperty,
			DoubleProperty incrementProperty, 
			DoubleProperty targetStopValue,
			BooleanProperty isAnimatingProperty,
			BooleanProperty playingProperty) {
		
		this.chaingingProperty = chaingingProperty;
		this.targetStopValue = targetStopValue;
		this.incrementProperty = incrementProperty;
		this.isAnimatingProperty = isAnimatingProperty;
		this.playingProperty = playingProperty;
	}

	@Override
	public void handle(ActionEvent event) {
		
		playbackStack++;
//		VisualizationModel.timeAnimate += System.currentTimeMillis() - VisualizationModel.start;
				
		if(chaingingProperty.getValue() < targetStopValue.getValue() && playingProperty.get()) {
			
			if(playbackStack == 20) {
				
				playbackStack = 0;
				
				Platform.runLater(new Runnable() {
		            @Override
		            public void run() {
		            	
		            	double directionPlay = isForward ? 1.0 : -1.0;
		            	chaingingProperty.setValue(chaingingProperty.getValue() + incrementProperty.getValue() * directionPlay);
		            }
		       });
			}
			else {
				
				double directionPlay = isForward ? 1.0 : -1.0;
				chaingingProperty.setValue(chaingingProperty.getValue() + incrementProperty.getValue() * directionPlay);
			}
		}
		else {
			
			playingProperty.set(false);
			isAnimatingProperty.set(false);
		}
	}
}
