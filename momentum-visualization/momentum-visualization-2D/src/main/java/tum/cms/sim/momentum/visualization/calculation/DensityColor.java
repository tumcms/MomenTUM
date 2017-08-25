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

package tum.cms.sim.momentum.visualization.calculation;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class DensityColor {

	public static Paint getColor(double currentDensity, double maximalDensity) {
		
		return DensityColor.getColor(currentDensity, maximalDensity, 1.0, false);
	}
		
	public static Paint getColor(double currentDensity, double maximalDensity, double transparency, boolean invert) {
		
		//maximalDensity = 50.0;
		int base = invert ? 255 : 0 ;
		double densityBase = (currentDensity/maximalDensity);// + 0.5;
		
		if (densityBase <= Double.MIN_VALUE) {
			
			return Color.rgb(base, base, base, transparency);
		}
		
		if(densityBase > 1.0) {
			
			densityBase = 1.0;
		}
		
		Integer rgbValueGreen = DensityColor.calculateRgbForGreen(currentDensity, maximalDensity);
		Integer rgbValueRed = DensityColor.calculateRgbForRed(currentDensity, maximalDensity);
		
//		Color color = Color.rgb(rgbValueRed, rgbValueGreen, 0, transparency);
//		
//		return color.deriveColor(color.getHue(), color.getSaturation(), densityBase, color.getOpacity());
		
		return Color.rgb(rgbValueRed, rgbValueGreen, 0, transparency);
	}
	
	private static Integer calculateRgbForGreen(double currentDensity, double maximalDensity) {
		
		Double doubleValue = (255 * (1 - currentDensity / maximalDensity));
		return doubleValue < 0 ? 0 : doubleValue.intValue();
	}

	private static Integer calculateRgbForRed(double currentDensity, double maximalDensity) {

		Double doubleValue = (255 * (currentDensity / maximalDensity));
		return doubleValue > 255 ? 255 : doubleValue.intValue();
	}
}
