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

package tum.cms.sim.momentum.utility.graph.buildAlgorithm.routeMapGraphBuild;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.apache.commons.math3.util.FastMath;
import tum.cms.sim.momentum.utility.geometry.AxisAlignedBoundingBox2D;
import tum.cms.sim.momentum.utility.geometry.Geometry2D;
import tum.cms.sim.momentum.utility.geometry.GeometryFactory;
import tum.cms.sim.momentum.utility.geometry.Segment2D;
import tum.cms.sim.momentum.utility.geometry.Vector2D;
import tum.cms.sim.momentum.utility.geometry.operation.GeometryAdditionals;
import tum.cms.sim.momentum.utility.graph.Edge;
import tum.cms.sim.momentum.utility.graph.Graph;
import tum.cms.sim.momentum.utility.graph.GraphTheoryFactory;
import tum.cms.sim.momentum.utility.graph.Path;
import tum.cms.sim.momentum.utility.graph.Vertex;
import tum.cms.sim.momentum.utility.graph.buildAlgorithm.kneidlGraphBuild.VisibliyVertexPrunerAlgorithm;
import tum.cms.sim.momentum.utility.graph.pathAlgorithm.ShortestPathAlgorithm;
import tum.cms.sim.momentum.utility.graph.pathAlgorithm.weightOperation.DijkstraWeightCalculator;
import tum.cms.sim.momentum.utility.graph.topologyAlgorithm.ConnectedComponentTopologyAlgorithm;
import tum.cms.sim.momentum.utility.lattice.ILattice;
import tum.cms.sim.momentum.utility.lattice.LatticeTheoryFactory;
import tum.cms.sim.momentum.utility.lattice.Lattice.Occupation;
import tum.cms.sim.momentum.utility.lattice.operation.GraphOnLatticeOperation;
import tum.cms.sim.momentum.utility.lattice.operation.MorphologicalLatticeOperation;
import tum.cms.sim.momentum.configuration.model.lattice.LatticeModelConfiguration.LatticeType;
import tum.cms.sim.momentum.configuration.model.lattice.LatticeModelConfiguration.NeighborhoodType;

public class RouteMapGraphBuildAlgorithm {
	
	private int maxIterations;
	private String weightName = "RouteMap";
	
	public Graph buildNormal(Graph graph, 
				double scenarioSize,
				AxisAlignedBoundingBox2D boundingBox,
				Collection<Geometry2D> knownSeeds, 					//origin, destination, ...
				Collection<Geometry2D> blockingGeometries) {

		//parameters
		double cellEdgeSize = 1.44;
		maxIterations = 10000;
		double mergeDistance = 5;
		double usefulEdgeLength = 10;
		
		//display
		boolean paint = false;
		boolean console = true;
		
		//initialization of variables
		ArrayList<Vertex> knownSeedVertices = new ArrayList<Vertex>();
		knownSeeds.forEach(knownSeed -> knownSeedVertices.add(GraphTheoryFactory.createVertex(knownSeed)));	    
		
		
		
		GraphOnLatticeOperation latticeOperation = new GraphOnLatticeOperation();
		ILattice latticeGraph = latticeOperation.createGraphOnLattice(cellEdgeSize, boundingBox, blockingGeometries);
		
		
		if(console) System.out.print("Getting and processing node candidates ... ");
		
		//get all candidates for graph nodes
		List<Vector2D> nodeCandidates = MorphologicalLatticeOperation.findNodeCandidates(latticeGraph, true);
		
		//merge candidates if necessary
		List<Vector2D> mergedCandidates = new ArrayList<Vector2D>();
		boolean merge = true;
		do {
			merge = this.candidatesToMerge(nodeCandidates, mergedCandidates, cellEdgeSize);
			if(maxIterations < 0)
				break;
		} while (merge);
		if(console) System.out.print("done\n");
		
		if(paint) {
			ILattice latticeNodes = LatticeTheoryFactory.createLattice("latNodes", 
					LatticeType.Quadratic, 
					NeighborhoodType.Touching, //wichtig
					cellEdgeSize, //cell edge size
					boundingBox.getMaxX(), boundingBox.getMinX(), 
					boundingBox.getMaxY(), boundingBox.getMinY()); 
			mergedCandidates.stream().forEach(cand -> latticeNodes.occupyCell(latticeNodes.getCellIndexFromPosition(cand), Occupation.Fixed));
				
			System.out.println("\n\nLattice with merged candidates:");
			latticeNodes.paintLattice();
		}
		
		
		//create the graph
		if(console) System.out.print("Creating full visibility graph ... ");
		
		//first, add all known seeds as vertices
		knownSeedVertices.forEach(seed -> graph.addVertex(seed));
		
		//then add the merged candidates
		mergedCandidates.stream().forEach(cand -> graph.addVertex(GraphTheoryFactory.createVertexCyleBased(cand))); 
		
		//then connect all vertices based on visibility
		graph.connectAllVisibleVertices(blockingGeometries, 0.0);
		
		if(console) System.out.print("done\n");
		
		
		//prune the vertices
		if(console) System.out.print("Pruning vertices ... ");
		VisibliyVertexPrunerAlgorithm vertexPruner = new VisibliyVertexPrunerAlgorithm();
		vertexPruner.pruneVertices(graph, new ArrayList<Vertex>(graph.getVertices()), new HashSet<Vertex>(knownSeedVertices), blockingGeometries, mergeDistance);
		if(console) System.out.print("done\n");
		
		
		
		//convert the graph to a minimum spanning tree
		if(console) System.out.print("Converting to a MST ... ");
		graph.computeEuklideanEdgeWeights(weightName);
		graph.convertToMST(weightName);
		if(console) System.out.print("done\n");
		
		
		//delete all non-reachable edges and vertices
		if(console) System.out.print("Delete unreachable regions of the graph ... ");
		ConnectedComponentTopologyAlgorithm connectivityAlgorithm = new ConnectedComponentTopologyAlgorithm();
    	ArrayList<Vertex> toDelete = new ArrayList<Vertex>(connectivityAlgorithm.getUnreachableVertices(graph, knownSeedVertices));
    	toDelete.stream().forEach(vertex -> graph.removeVertex(vertex));
    	if(console) System.out.print("done\n");
    	
    	
    	//add vertices at intersections
    	if(console) System.out.print("Adding useful vertices ... ");
    	boolean usefulVertices = true;
		do {
			usefulVertices = this.addVertexAtIntersection(graph);
			if(maxIterations < 0)
				break;
		} while (usefulVertices);
    	if(console) System.out.print("done\n");
    	

		//add useful edges to the graph
    	if(console) System.out.print("Adding useful edges ... ");
		this.addUsefulEdges(graph, blockingGeometries, usefulEdgeLength, weightName);
		if(console) System.out.print("done\n");
		

		return graph;
	}
	
	/**
	 * Processes a list of all candidates for graph nodes. If candidates are too close to each other,
	 * they will be merged to the center of a polygon created by those candidates. The result is stored
	 * in the list <code>mergedCandidates</code>.<p>
	 * @param candidates the candidates for graph nodes
	 * @param mergedCandidates the processed candidates are stored here
	 * @param threshold minimum distance between two graph nodes, e.g. cellEdgeSize of the lattice
	 * @return
	 */
	private boolean candidatesToMerge(List<Vector2D> candidates, List<Vector2D> mergedCandidates, double threshold) {
		List<Vector2D> neighboringCandidates = new ArrayList<Vector2D>();
		double dist = FastMath.sqrt(2*FastMath.pow(threshold, 2.));
		
		for(Vector2D current : candidates) {
			candidates.stream().forEach(cand -> {
				if(current.distance(cand)<=dist) 
					neighboringCandidates.add(cand);
				});
			
			mergedCandidates.add(GeometryAdditionals.calculateVectorCenter(neighboringCandidates));
			candidates.removeAll(neighboringCandidates);
			this.maxIterations--;
			return true;
			
		}
		return false;
	}
	
	/**
	 * This functions adds "useful" edges according to Overmars (2006) - Creating high quality road maps.
	 * An edge is useful, if it creates a path which is by the factor K smaller than the current connection
	 * present in the graph.
	 * @param graph
	 * @param blockingGeometries
	 * @param K
	 * @param weightName
	 */
	private void addUsefulEdges(Graph graph, Collection<Geometry2D> blockingGeometries, Double K, String weightName) {
		boolean inSight = false;
		double directCost = 0;
		double graphCost = 0;
		
		ShortestPathAlgorithm dijkstraAlgorithm = new ShortestPathAlgorithm(new DijkstraWeightCalculator("RouteMap", null));
		
		for(Vertex start : graph.getVertices()) {
			
			for(Vertex end : graph.getVertices()) {
				
				//continue, if start and end are the same
				if(start.equals(end))
					continue;
				
				//continue, if there is no direct path between start and end 
				inSight = GeometryAdditionals.calculateIntersection(blockingGeometries, 
						start.getGeometry().getCenter(), 
						end.getGeometry().getCenter(), 0.0);
				if(!inSight)
					continue;
				
//				//check, if the new edge is useful
				Path path = dijkstraAlgorithm.calculateShortestPath(graph, start, end);
				
				directCost = start.euklidDistanceBetweenVertex(end);
				graphCost = path.distance();
				
				if(K * directCost < graphCost) {
		    		graph.doublyConnectVertices(start, end);
				}
			}
		}
	}
	
	/**
	 * Adds vertices at intersection points of two edges
	 * @param graph
	 */
	private boolean addVertexAtIntersection(Graph graph) {
		List<Edge> edgeList = graph.getAllEdges();
		List<Vector2D> intersection = null;
//		HashMap<Vertex, Vertex> verticesToAdd = new HashMap<Vertex, Vertex>();
		Vertex newVertex = null;
		
		for(Edge currentEdge : edgeList) {
			Segment2D currentSegment = GeometryFactory.createSegment(
					currentEdge.getStart().getGeometry().getCenter(), 
					currentEdge.getEnd().getGeometry().getCenter());
			
			for(Edge observedEdge : edgeList) {
				if(currentEdge.equals(observedEdge))
					continue;
				
				if(currentEdge.getStart().equals(observedEdge.getEnd())
						|| currentEdge.getEnd().equals(observedEdge.getStart())
						|| currentEdge.getStart().equals(observedEdge.getStart())
						|| currentEdge.getEnd().equals(observedEdge.getEnd()))
					continue;
				
				Segment2D observedSegment = GeometryFactory.createSegment(
						observedEdge.getStart().getGeometry().getCenter(), 
						observedEdge.getEnd().getGeometry().getCenter());
				
				intersection = currentSegment.getIntersection(observedSegment);
//				if(intersection.isEmpty())
//					continue;
				for(Vector2D currentIntersection : intersection) {
					newVertex = GraphTheoryFactory.createVertexCyleBased(currentIntersection);
					graph.disconnectVertices(currentEdge.getStart(), currentEdge.getEnd());
					graph.disconnectVertices(observedEdge.getStart(), observedEdge.getEnd());
					graph.addVertex(newVertex);
					graph.doublyConnectVertices(currentEdge.getStart(), newVertex);
					graph.doublyConnectVertices(currentEdge.getEnd(), newVertex);
					graph.doublyConnectVertices(observedEdge.getStart(), newVertex);
					graph.doublyConnectVertices(observedEdge.getEnd(), newVertex);
					maxIterations--;
					return true;
				}
	
			}
		}
		
		return false;
		
	}
	
	@SuppressWarnings("unused")
	private void printSegment(Segment2D segment) {
		System.out.println("First Point ( " + segment.getFirstPoint().getXComponent()
				+ "/" + segment.getFirstPoint().getYComponent()
				+ ") - Second Point ("+ segment.getLastPoint().getXComponent()
				+ "/" + segment.getLastPoint().getXComponent() + ")");
	}
	

	

	 
//	 public static void main(String[] args) throws Exception {
//		 ArrayList<Geometry2D> corners = new ArrayList<Geometry2D>();
//		
////		 corners.add(new Vector2D(0, 1));
//
//		 
//		 
//	 }
}
