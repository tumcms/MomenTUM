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

package tum.cms.sim.momentum.model.tactical.routing.dijkstraModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import tum.cms.sim.momentum.data.agent.pedestrian.state.tactical.RoutingState;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrianExtension;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;
import tum.cms.sim.momentum.data.agent.pedestrian.types.ITacticalPedestrian;
import tum.cms.sim.momentum.data.layout.area.TaggedArea;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.tactical.routing.RoutingModel;
import tum.cms.sim.momentum.utility.geometry.GeometryFactory;
import tum.cms.sim.momentum.utility.geometry.Segment2D;
import tum.cms.sim.momentum.utility.geometry.Vector2D;
import tum.cms.sim.momentum.utility.graph.Edge;
import tum.cms.sim.momentum.utility.graph.Graph;
import tum.cms.sim.momentum.utility.graph.Path;
import tum.cms.sim.momentum.utility.graph.Vertex;

public class DijkstraRiskTactical extends RoutingModel {

	private static String weightName = "dijkstraWeight";
	private HashMap<Edge, Double> euklidDistances = new HashMap<>();
	private HashMap<Edge, Double> shareNotForPedestrian = new HashMap<>();

	private Graph visibilityGraph = null;

	@Override
	public void callPreProcessing(SimulationState simulationState) {

		visibilityGraph = this.scenarioManager.getGraph();

		initializeEdgeWeights(this.visibilityGraph);
	}

	@Override
	public IPedestrianExtension onPedestrianGeneration(IRichPedestrian pedestrian) {

		return new DijkstraRiskExtension(DijkstraRiskTactical.weightName, Integer.toString(pedestrian.getId()));
	}

	@Override
	public void onPedestrianRemoval(IRichPedestrian pedestrian) {

		((DijkstraRiskExtension)pedestrian.getExtensionState(this)).removeWeightsForPedestrian(this.visibilityGraph);
	}

	@Override
	public void callBeforeBehavior(SimulationState simulationState, Collection<IRichPedestrian> pedestrians) {

		// Nothing to do
	}

	@Override
	public void callPedestrianBehavior(ITacticalPedestrian pedestrian, SimulationState simulationState) {

		Vertex start = this.findNavigationStartPoint(pedestrian, this.perception, this.scenarioManager);
		Vertex end = this.visibilityGraph.getGeometryVertex(pedestrian.getNextNavigationTarget().getGeometry());

		this.updateEdgeWeights(this.visibilityGraph, simulationState.getCalledOnThread(), DijkstraRiskTactical.weightName);

		DijkstraRiskExtension router = (DijkstraRiskExtension)pedestrian.getExtensionState(this);
		Path route = router.route(this.visibilityGraph, start, end);

		RoutingState routingState = this.updateRouteState(this.perception, pedestrian, route);
		pedestrian.setRoutingState(routingState);
	}


	@Override
	public void callAfterBehavior(SimulationState simulationState, Collection<IRichPedestrian> pedestrians) {

		// nothing to do
	}

	@Override
	public void callPostProcessing(SimulationState simulationState) {

		// nothing to do
	}

	private void updateEdgeWeights(Graph graph, int threadID, String edgeWeightName) {

		String weightName = edgeWeightName + String.valueOf(threadID);
		double weight = 0;
		Edge edge = null;


		for(Vertex current : graph.getVertices()) {

			for(Vertex successor : graph.getSuccessorVertices(current)) {

				if(current == successor) {

					continue;
				}

				edge = graph.getEdge(current, successor);
				weight = this.euklidDistances.get(edge) * this.shareNotForPedestrian.get(edge);
				//weight = 555;
				//System.out.println(edge.getName() + " " + " euklid=" + weight);
				//edge.setWeight(edgeWeightName, percentageNotForPedestrian);
				edge.setWeight(weightName, weight);
			}
		}
	}

	private void initializeEdgeWeights(Graph graph) {

		Edge edge = null;
		Vector2D currentPosition = null;
		Vector2D successorPosition = null;
		Segment2D currentLineSegment = null;

		double percentageNotForPedestrian = 0;
		double euklidDistance = 0;

		for(Vertex current : graph.getVertices()) {
			for(Vertex successor : graph.getSuccessorVertices(current)) {

				if(current == successor) {

					continue;
				}
				currentPosition = current.getGeometry().getCenter();
				successorPosition = successor.getGeometry().getCenter();
				currentLineSegment = GeometryFactory.createSegment(currentPosition, successorPosition);

				euklidDistance = current.euklidDistanceBetweenVertex(successor);
				percentageNotForPedestrian = calculatePercentageNotForPedestrian(currentLineSegment);

				edge = graph.getEdge(current, successor);

				this.shareNotForPedestrian.put(edge, percentageNotForPedestrian);
				this.euklidDistances.put(edge, euklidDistance);
			}
		}

	}


	private double calculatePercentageNotForPedestrian(Segment2D lineSegment) {

		ArrayList<Segment2D> lineSegmentsSplit = splitByAreas(lineSegment);

		double distanceForPedestrian = 0;
		double distanceNotForPedestrian = 0;

		for(Segment2D curSegment : lineSegmentsSplit) {

			if(this.pointContainedByAreaForPedestrians(curSegment.getCenter())) {
				distanceForPedestrian += curSegment.getLenghtDistance();
			} else {
				distanceNotForPedestrian += curSegment.getLenghtDistance();
			}
		}

		return  distanceNotForPedestrian / (distanceForPedestrian + distanceNotForPedestrian);
	}

	private boolean pointContainedByAreaForPedestrians(Vector2D point) {

		for(TaggedArea curArea : this.scenarioManager.getTaggedAreas()) {
			if(curArea.getGeometry().contains(point))
				return true;
		}

		return false;
	}

	private ArrayList<Segment2D> splitByAreas(Segment2D segment) {

		ArrayList<Segment2D> splitSegments = new ArrayList<Segment2D>();
		ArrayList<Segment2D> splitSegmentsTemp = new ArrayList<Segment2D>();
		splitSegments.add(segment);

		for(TaggedArea curArea : this.scenarioManager.getTaggedAreas()) {

			splitSegmentsTemp.clear();

			for(Segment2D curSegment : splitSegments) {
				splitSegmentsTemp.addAll(curSegment.getSegmentSplittedByPolygon(curArea.getGeometry()));
			}

			splitSegments.clear();
			splitSegments.addAll(splitSegmentsTemp);
		}

		return splitSegments;
	}
}
