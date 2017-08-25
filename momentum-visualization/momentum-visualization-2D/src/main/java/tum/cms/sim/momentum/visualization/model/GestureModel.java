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

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class GestureModel {

	private final DoubleProperty mouseX = new SimpleDoubleProperty(this, "mouseX", 0.0);
	private final DoubleProperty mouseY = new SimpleDoubleProperty(this, "mouseY", 0.0);
	private final DoubleProperty scale = new SimpleDoubleProperty(this, "scale", 1.0);
	
	public DoubleProperty getMouseXProperty() {
		return mouseX;
	}
	
    public double getMouseX() {
        return mouseX.get();
    }
    
    public void setMouseX(Double mouseX) {
    	this.mouseX.set(mouseX);
    }

	public DoubleProperty getMouseYProperty() {
		return mouseY;
	}
	
    public double getMouseY() {
        return mouseY.get();
    }
    
    public void setMouseY(Double mouseY) {
    	this.mouseY.set(mouseY);
    }   

	public DoubleProperty getScaleProperty() {
		return scale;
	}
	
    public double getScale() {
        return scale.get();
    }
    
    public void setScale(Double scale) {
    	this.scale.set(scale);
    }
    
	
	public void fillFromPreferences(String gestureString) {
		
		if(gestureString == null || gestureString.isEmpty()) {
			return;
		}
		
		try {
			
			String[] gesture = gestureString.split(":");
		
			setMouseX(Double.parseDouble(gesture[0]));
			setMouseY(Double.parseDouble(gesture[1]));
			setScale(Double.parseDouble(gesture[2]));
		}
		catch(Exception errorInConfig) {
			
			this.resetGesture();
		}
	}
	
	public String createForPreferences() {
	
		return String.valueOf(getMouseX()) + ":" +
				String.valueOf(getMouseY()) + ":" +
				String.valueOf(getScale());
	}
	
	public void resetGesture() {
		
		setMouseX(0.0);
		setMouseY(0.0);
		setScale(1.0);
	}	
}
