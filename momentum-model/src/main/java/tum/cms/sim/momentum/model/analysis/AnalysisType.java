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

package tum.cms.sim.momentum.model.analysis;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import tum.cms.sim.momentum.configuration.model.analysis.MeasureConfiguration.MeasuringType;
import tum.cms.sim.momentum.configuration.model.output.WriterSourceConfiguration.OutputType;

public class AnalysisType {
	
	public static final String timeStep = OutputType.timeStep.name();
	public static final String xPositionType = OutputType.x.name();
	public static final String yPositionType = OutputType.y.name();
	public static final String tacticalTargetType = OutputType.currentVertexID.name();
	public static final String strategicTargetType = OutputType.targetID.name();
	public static final String behaviorType = OutputType.behavior.name();
	
	public static final String crossedLineType = "linesCrossed";
	public static final String insidePolygonType = MeasuringType.InsidePolygon.name();
	public static final String turingAngleType = MeasuringType.TurningAngle.name();
	public static final String navigationIdType = MeasuringType.navigationId.name();
	public static final String walkingDistanceType = "walkingDistance";
	public static final String occupancyType = "areaOccupancy";
	public static final String densityXtType = "density";
	public static final String existensType = "exists";
	public static final String cellCenterX = "cellCenterX";
	public static final String cellCenterY = "cellCenterY";
	public static final String cornerSize = "cornerSize";
	public static final String density = "density";
	public static final String maximalDensity = "maximalDensity";
	
	/**
	 * Add the typing of the data elements here, this is less ugly than 
	 * checking this dynamically with by exceptions and will help you to
	 * have a type overview of the analysis elements types.
	 */
	public static final Set<String> analysisIntegerTypes = new HashSet<>(
		Arrays.asList(new String[] { 
				tacticalTargetType, 
				crossedLineType,
				insidePolygonType,
				navigationIdType,
				strategicTargetType
			})
		);
	
	/**
	 * This is redundant but a nice to have regarding the analysis element types.
	 */
	public static final Set<String> analysisDoubleTypes = new HashSet<>(
		Arrays.asList(new String[] { 
				existensType,
				xPositionType, 
				yPositionType,
				turingAngleType,
				walkingDistanceType				
			})
		);
	
	/**
	 * This is redundant but a nice to have regarding the analysis element types.
	 */
	public static final Set<String> enumDoubleTypes = new HashSet<>(
		Arrays.asList(new String[] { 
				behaviorType
			})
		);
}
