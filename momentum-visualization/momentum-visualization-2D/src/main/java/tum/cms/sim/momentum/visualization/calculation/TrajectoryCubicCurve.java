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

import javafx.geometry.Point2D;
import javafx.scene.shape.CubicCurveTo;

public class TrajectoryCubicCurve {

	public CubicCurveTo buildCurve(Point2D previousPlacement,
			Point2D overNextPlacement,
			double resolution,
			double oldX,
			double oldY,
			double newX,
			double newY) {
		
		double middleZeroX = 0.0;
		double middleZeroY = 0.0;
		double middleFirstX = 0.0;
		double middleFirstY = 0.0;
		double middleSecondX = 0.0;
		double middleSecondY = 0.0;
		
		middleFirstX = oldX * resolution + (newX * resolution - oldX * resolution) / 2.0;
		middleFirstY = oldY * resolution + (newY * resolution - oldY * resolution) / 2.0;	
		
		if(previousPlacement == null) { // first segement
	
			middleZeroX = middleFirstX;
			middleZeroY = middleFirstY;
		}
		else {
			
			middleZeroX = previousPlacement.getX() * resolution 
					+ (oldX * resolution - previousPlacement.getX() * resolution) / 2.0;
			middleZeroY = previousPlacement.getY() * resolution 
					+ (oldY * resolution - previousPlacement.getY() * resolution) / 2.0;
		}
	
		if(overNextPlacement == null) { // last Segment
			
			middleSecondX = middleFirstX;
			middleSecondY = middleFirstY;
		}
		else {
			
			middleSecondX = newX  * resolution + (overNextPlacement.getX() * resolution - newX * resolution) / 2.0;
			middleSecondY = newY  * resolution + (overNextPlacement.getY() * resolution - newY * resolution) / 2.0;			
		}

		double controlFirstX = oldX * resolution + (middleFirstX - middleZeroX) / 2.0;
		double controlFirstY = oldY * resolution + (middleFirstY - middleZeroY) / 2.0;
		double controlSecondX = newX * resolution + (middleFirstX - middleSecondX) / 2.0;
		double controlSecondY = newY * resolution + (middleFirstY - middleSecondY) / 2.0;
		
		return new CubicCurveTo(
				controlFirstX,
				controlFirstY,
				controlSecondX,
				controlSecondY,
				newX * resolution,
				newY * resolution);
	}
}
