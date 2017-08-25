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

package tum.cms.sim.momentum.model.layout.graph.vertex.vertexCornerModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import tum.cms.sim.momentum.data.layout.obstacle.Obstacle;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.layout.graph.GraphOperation;
import tum.cms.sim.momentum.utility.geometry.Geometry2D;
import tum.cms.sim.momentum.utility.graph.Vertex;

/**
 * Instantiated by GraphType <code>VertexCreateAtCornersEnriched</code>
 * @param cornerDistance new vertices are created at geometry corners in a distance of cornerDistance
 * @param segmentSegregation new vertices are created along the borders of the geometry in a distance of segmentSegregation
 *
 */
public class VertexCornerModelEnriched extends GraphOperation {
	
	private static String cornerDistanceName = "cornerDistance";
	private static String segmentSegregationName = "segmentSegregation";

	@Override
	public void callPreProcessing(SimulationState simulationState) {

		VisibilityVertexBuildAlgorithm vertexBuilder = new VisibilityVertexBuildAlgorithm();
		Collection<Geometry2D> obstacleGeometries = this.scenarioManager.getObstacles().stream().map(Obstacle::getGeometry).collect(Collectors.toList());
		double cornerDistance = this.properties.getDoubleProperty(cornerDistanceName);
		double segmentSegregation = this.properties.getDoubleProperty(segmentSegregationName);
		
		ArrayList<Vertex> cornerVerticesSeeds = vertexBuilder.createGraphEnrichedSeeds(obstacleGeometries,
       			cornerDistance,
       			segmentSegregation);
		cornerVerticesSeeds.forEach(cornerSeed -> this.scenarioManager.getGraph().addVertex(cornerSeed));
		
	}

	@Override
	public void callPostProcessing(SimulationState simulationState) {
		// nothing to do
		
	}

}
