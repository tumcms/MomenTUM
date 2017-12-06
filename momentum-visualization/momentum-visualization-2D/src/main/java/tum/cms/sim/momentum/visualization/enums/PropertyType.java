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

package tum.cms.sim.momentum.visualization.enums;

import javafx.scene.paint.Color;

/**
 * Contains all possible keys for the property file
 * @author Martin Sigl
 *
 */
public enum PropertyType {
	
	layoutPath,
	outputCsvPath,
	snapshotPath,
	snapshotPixelScale,
	snapShotName,
	latticeVisibility,
	graphVisibility,
	obstacleVisibility,
	originVisibility,
	intermediateVisibility,
	destinationVisibility,
	selectedColor,
	axisColor,
	latticeColor,
	phongMaterialColor,
	pedestrianDirectionColor,
	graphColor,
	destinationColor,
	originColor,
	intermediateColor,
	obstacleColor,
	virtualObstacleColor,
	trajectoryColor,
	trajectoryIsRandomColor,
	trajectoryThickness,
	trajectoryTimeInterval,
	edgeThickness,
	vertexSize,
	quickloadCsvPaths,
	quickloadLayoutPath,
	informationColor;
	
	private static final String DEFAULT_SNAPSHOT_NAME = "snapshot.png";
	private static final String USER_DIR = System.getProperty("user.dir");
	public static final String DELIMITER = ";";
	
	/**
	 * returns the default value for a property type
	 * @param type
	 * @return the default value as string
	 */
	public static String getDefaultValue(PropertyType type) {
		switch(type) {
		case selectedColor:
			return Color.RED.toString();
		case latticeColor:
			return Color.BLACK.toString();
		case axisColor:
			return Color.BLACK.toString();
		case destinationColor:
			return Color.LIGHTSALMON.toString();
		case edgeThickness:
			return Double.toString(1.);
		case graphColor:
			return Color.ORANGE.toString();
		case graphVisibility:
			return Boolean.toString(true);
		case obstacleVisibility:
			return Boolean.toString(true);
		case originVisibility:
			return Boolean.toString(true);
		case intermediateVisibility:
			return Boolean.toString(true);
		case destinationVisibility:
			return Boolean.toString(true);
		case informationColor:
			return Color.BLACK.toString();
		case intermediateColor:
			return Color.VIOLET.toString();
		case latticeVisibility:
			return Boolean.toString(true);
		case layoutPath:
			return USER_DIR;
		case obstacleColor:
			return Color.DARKGREY.toString();
		case virtualObstacleColor:
			return Color.BLUEVIOLET.toString();
		case originColor:
			return Color.LIGHTCYAN.toString();
		case outputCsvPath:
			return USER_DIR;
		case pedestrianDirectionColor:
			return Color.RED.toString();
		case phongMaterialColor:
			return Color.GREEN.toString();
		case snapShotName:
			return DEFAULT_SNAPSHOT_NAME;
		case snapshotPath:
			return System.getProperty("user.dir");
		case snapshotPixelScale:
			return Double.toString(1.0);
		case trajectoryColor:
			return Color.GREY.toString();
		case trajectoryIsRandomColor:
			return Boolean.toString(false);
		case trajectoryThickness:
			return Double.toString(0.5);
		case trajectoryTimeInterval:
			return Double.toString(0.0);
		case vertexSize:
			return Double.toString(3.);
		case quickloadCsvPaths:
			return "";
		case quickloadLayoutPath:
			return "";
		default:
			return "";
		}
	}
}
