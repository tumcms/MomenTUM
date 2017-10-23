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

package tum.cms.sim.momentum.model.perceptional.blockingGeometriesModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import tum.cms.sim.momentum.configuration.model.lattice.LatticeModelConfiguration.LatticeType;
import tum.cms.sim.momentum.configuration.model.lattice.LatticeModelConfiguration.NeighbourhoodType;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrian;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;
import tum.cms.sim.momentum.data.layout.area.OriginArea;
import tum.cms.sim.momentum.data.layout.obstacle.Obstacle;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.layout.lattice.LatticeModel;
import tum.cms.sim.momentum.model.perceptional.PerceptionalModel;
import tum.cms.sim.momentum.utility.geometry.Geometry2D;
import tum.cms.sim.momentum.utility.geometry.Segment2D;
import tum.cms.sim.momentum.utility.geometry.Vector2D;
import tum.cms.sim.momentum.utility.graph.Edge;
import tum.cms.sim.momentum.utility.graph.Vertex;
import tum.cms.sim.momentum.utility.lattice.CellIndex;
import tum.cms.sim.momentum.utility.lattice.ILattice;
import tum.cms.sim.momentum.utility.lattice.LatticeTheoryFactory;
import tum.cms.sim.momentum.utility.lattice.Lattice.Occupation;

/**
 * The blocking geometry perception model is highly important.
 * Version 1.0 uses bresenham only because the shadow mapping implementation did not work.
 * Version 2.0 uses bresenham for single point checks but a FOV Octant based method
 * (https://blogs.msdn.microsoft.com/ericlippert/2011/12/12/shadowcasting-in-c-part-one/)
 * to find a set of visible objects in the field of view. This also changed the concept
 * of the perception model. Now it is a an callable model that updates the positions
 * of all pedestrian on the perception grid in parallel.
 * 
 * @author Peter Kielar
 *
 */
public class BlockingGeometriesPerception extends PerceptionalModel {

	private int distance = 250;
	
//	private HashMap<Area, ArrayList<Polygon2D>> areaVisibilityMap = new HashMap<Area, ArrayList<Polygon2D>>();
//	private HashMap<Vertex, ArrayList<Polygon2D>> vertexVisibilityMap = new HashMap<Vertex, ArrayList<Polygon2D>>();

	private long currentTimeStepBuffer = 0;
	private HashMap<Integer, HashSet<Integer>> pedestrianBuffer = new HashMap<>();
	
	private ILattice visibilityMap = null;
	private double accuracy = 0.5;
	private ArrayList<Segment2D> obstacleParts = new ArrayList<Segment2D>();
	
	@Override
	public void callPreProcessing(SimulationState simulationState) {

		if(this.properties.getDoubleProperty("accuracy") != null) {
			
			this.accuracy = this.properties.getDoubleProperty("accuracy");
		}
		
		this.scenarioManager.getObstacles()
			.stream()
			.map(Obstacle::getGeometry)
			.forEach(obstacleGeometry -> obstacleParts.addAll(obstacleGeometry.getSegments()));

		DescriptiveStatistics edgeStatistics = new DescriptiveStatistics();

        this.scenarioManager.getGraph().getAllEdges().stream()
        	.forEach(edge -> edgeStatistics.addValue(edge.euklideanLenght()));
     
		double cellsMax = edgeStatistics.getMax() / this.accuracy;
		this.distance = (int)(cellsMax * 2);
	
		this.visibilityMap = LatticeTheoryFactory.createLattice(
			"rayTracing",
			LatticeType.Quadratic,
			NeighbourhoodType.Edge,
			accuracy,
			this.scenarioManager.getScenarios().getMaxX() + accuracy * 3, 
			this.scenarioManager.getScenarios().getMinX() - accuracy * 3,
			this.scenarioManager.getScenarios().getMaxY() + accuracy * 3,
			this.scenarioManager.getScenarios().getMinY() - accuracy * 3);
		
		visibilityMap.setAllCells(Occupation.Empty);
		
		List<CellIndex> originCenterCells = this.scenarioManager.getOrigins().stream()
				.map(OriginArea::getGeometry)
				.map(Geometry2D::getCenter)
				.map(center -> visibilityMap.getCellIndexFromPosition(center))
				.collect(Collectors.toList());
		
		LatticeModel.fillLatticeForObstacles(visibilityMap, this.scenarioManager.getScenarios());
		
		// flood to find the non-visible areas beginning from all origin seeds
		visibilityMap.flood(originCenterCells);	
	}


	@Override
	protected void supportModelUpdate(SimulationState simulationState) {
		// Update positions of pedestrians, be careful this is done in parallel
		// Thus use a no synchronized lattice
	}
	
//	private List<Polygon2D> createVisibilityTriangles(Geometry2D objectToSee, List<Segment2D> obstacleParts) {
//
//		ArrayList<Segment2D> toCheckObstacleParts = new ArrayList<>();
//
//		ArrayList<ArrayList<Vector2D>> inSightTriangles = new ArrayList<>();
//		toCheckObstacleParts.addAll(obstacleParts);
//		ArrayList<Pair<Vector2D, Segment2D>> testObstacleCorner = new ArrayList<>();
//		
//		toCheckObstacleParts.forEach(part -> part.getVertices()
//				.forEach(corner -> testObstacleCorner.add(new MutablePair<Vector2D, Segment2D>(corner, part))));
//		
//		// sort obstacle parts for shadow mapping
//		testObstacleCorner.sort(new ObstacleLineComparator(objectToSee.getCenter()));
//		
//		inSightTriangles.addAll(GeometryAdditionals.calculateSightCorners(testObstacleCorner, objectToSee.getCenter(), this.accuracy / 100.0));
//		
//		inSightTriangles.removeIf(triangleList-> triangleList.size() == 0);
//		
//		List<Polygon2D> outerVertices = new ArrayList<>();
//		
//		inSightTriangles.forEach(sightTriangle -> {
//
//			if(sightTriangle.size() > 3) {
//				
//				List<Vector2D> sortedTriangle = sightTriangle.subList(0, sightTriangle.size() - 1);
//				sortedTriangle.sort(new CloseToAxisComperator());
//				
//				for(int iter = 0; iter < sortedTriangle.size() - 1; iter += 2) {
//					
//					List<Vector2D> corners = new ArrayList<>();
//					corners.add(sortedTriangle.get(iter));
//					corners.add(sortedTriangle.get(iter + 1));
//					corners.add(sightTriangle.get(sightTriangle.size() - 1));
//					
//					if(!GeometryAdditionals.polygonHasCounterClockwiseWielding(corners)) {
//						
//						corners = GeometryAdditionals.switchOrderOfVertices(corners);
//					}
//					
//					outerVertices.add(GeometryFactory.createPolygon(corners));
//				}
//			}
//			else {
//
//				List<Vector2D> corners = sightTriangle;
//				if(!GeometryAdditionals.polygonHasCounterClockwiseWielding(sightTriangle)) {
//					
//					corners = GeometryAdditionals.switchOrderOfVertices(sightTriangle);
//				}
//				
//				try {
//
//					outerVertices.add(GeometryFactory.createPolygon(corners));	
//				}
//				catch(Exception ex) {
//					
//					ex = null;
//				}
//			}
//			
//		});
//		
//		//this.checkVisibility(outerVertices);
//		
//		return outerVertices;
//	}
	
//	public void checkVisibility(List<Polygon2D> triangles) {
//		
//		visibilityMap.paintLattice();
//		
//		triangles.forEach(triangle -> {
//		
//			visibilityMap.occupyAllPolygonCells(triangle, Occupation.Dynamic);
//		});
//	
//		visibilityMap.paintLattice(); // take care regarding numerical errors some cells that can see, but cannot
//	}
	
	@Override
	public void callPostProcessing(SimulationState simulationState) { /* nothing to do */ }
	
	
	@Override
	public boolean isVisible(Vector2D viewPort, Edge edge) {
		
		return this.isVisible(viewPort, edge.getStart()) || this.isVisible(viewPort, edge.getEnd());
	}

	@Override
	public boolean isVisible(IPedestrian currentPedestrian, IPedestrian otherPedestrian) {
		
		return this.isVisible(currentPedestrian.getPosition(), otherPedestrian.getPosition());
	}
	
	@Override
	public boolean isVisible(Vector2D viewPort, Vertex vertex) {
		
		return this.isVisible(viewPort, vertex.getGeometry().getCenter());
	}

	@Override
	public boolean isVisible(Vector2D viewPort, Vector2D position) {

		CellIndex from = this.visibilityMap.getCellIndexFromPosition(viewPort);
		CellIndex towards = this.visibilityMap.getCellIndexFromPosition(position);
			
		boolean hitTarget = this.visibilityMap.breshamLineCast(from, towards, distance);
		
		return hitTarget;
	}
	
	@Override
	public List<IPedestrian> getPerceptedPedestrians(IPedestrian currentPedestrian, SimulationState simulationState) {

		synchronized (this) {
			
			if(simulationState.getCurrentTimeStep() != this.currentTimeStepBuffer) {
				
				this.currentTimeStepBuffer = simulationState.getCurrentTimeStep();
				this.pedestrianBuffer.clear();
				this.pedestrianManager.getAllPedestrians().forEach(pedestrian -> 
					this.pedestrianBuffer.put(pedestrian.getId(), new HashSet<>()));
				
			}
		}
		
		HashSet<Integer> viewForPedestrian = this.pedestrianBuffer.get(currentPedestrian.getId());
		HashSet<Integer> othersView = null;
		
		List<IPedestrian> visiblePedestrians = new ArrayList<>();
		CellIndex from = this.visibilityMap.getCellIndexFromPosition(currentPedestrian.getPosition());
		
		for(IRichPedestrian other : this.pedestrianManager.getAllPedestrians()) {
			
			othersView = this.pedestrianBuffer.get(other.getId());
			
			if(othersView.contains(currentPedestrian.getId())) {
				
				viewForPedestrian.add(other.getId());
				visiblePedestrians.add(other);
				continue;
			}
			
			if(viewForPedestrian.contains(other.getId())) {
				
				othersView.add(currentPedestrian.getId());
				visiblePedestrians.add(other);
				continue;
			}
			
			CellIndex towards = this.visibilityMap.getCellIndexFromPosition(other.getPosition());
			boolean hitTarget = this.visibilityMap.breshamLineCast(from, towards, distance);

			if(hitTarget) {
			
				visiblePedestrians.add(other);
				
				viewForPedestrian.add(other.getId());
				othersView.add(currentPedestrian.getId());
			}
		}
		
		return visiblePedestrians;
	}

	
//	//@Override
//	public List<IPedestrian> getPerceptedPedestrians2(IPedestrian currentPedestrian, ITimeStepInformation simulationState) {
//		
//		List<IPedestrian> visiblePedestrians = new ArrayList<>();
//
//		if(perceptedPedestrianBuffer == null || perceptedPedestrianBuffer.getValue0() != simulationState.getCurrentTimeStep()) {
//			
//			perceptedPedestrianBuffer = new Pair<Long, HashMap<Integer,List<IPedestrian>>>(simulationState.getCurrentTimeStep(),
//					new HashMap<>());
//		}
//	
//		if(!perceptedPedestrianBuffer.getValue1().containsKey(currentPedestrian.getId())) {
//			
//			ArrayList<Segment2D> toCheckObstacleParts = new ArrayList<>();
//			toCheckObstacleParts.addAll(obstacleParts);
//			ArrayList<Pair<Vector2D, Segment2D>> testCorners = new ArrayList<>();
//	
//			toCheckObstacleParts.forEach(part -> part.getVertices()
//					.forEach(corner -> testCorners.add(new Pair<Vector2D, Segment2D>(corner, part))));
//			
//			c = System.currentTimeMillis() / 1000.0;
//			testCorners.sort(new ObstacleLineComparator(currentPedestrian.getPosition()));
//			timeC = timeC + (System.currentTimeMillis() / 1000.0 - c);	
//			
//			a = System.currentTimeMillis() / 1000.0;
//			this.pedestrianManager.getAllPedestrians().stream().forEach(pedestrian -> {
//				
//				Line2D ray = GeometryFactory.createLine2D(currentPedestrian.getPosition(),
//						pedestrian.getPosition().subtract(currentPedestrian.getPosition()).getNormalized());
//				
//				boolean blocked = false;
//						
//				for(Pair<Vector2D, Segment2D> toCheck : testCorners) {
//					
//					// close, check
//					if(currentPedestrian.getPosition().distance(pedestrian.getPosition()) <
//							currentPedestrian.getPosition().distance(toCheck.getValue0())) {
//							
//						Vector2D intersetion = GeoAdditionals.intersectionToRay(toCheck.getValue1(), ray);
//						
//						if(intersetion != null) { // obstacle in between
//							
//							blocked = true;
//							break;
//						}
//					}
//					else { // too far away stop checking
//						
//						break;
//					}
//				}
//				
//				if(!blocked) { // add if visible
//					
//					visiblePedestrians.add(pedestrian);
//				}
//			});
//			
//			timeA = timeA + (System.currentTimeMillis() / 1000.0 - a);	
//			perceptedPedestrianBuffer.getValue1().put(currentPedestrian.getId(), visiblePedestrians);	
//		}
//		else {
//			
//			visiblePedestrians.addAll(perceptedPedestrianBuffer.getValue1().get(currentPedestrian.getId()));
//		}
//		
//		return visiblePedestrians;
//	}

//	@Override
//	public boolean isVisible(Vector2D viewPort, Vertex vertex) {
//		
//		boolean isVisible = false;
//
////		if(this.vertexVisibilityMap.containsKey(vertex)) {
////			
////			for(Polygon2D triangle : this.vertexVisibilityMap.get(vertex))  {
////				
////				if(triangle.contains(viewPort)) {
////					
////					isVisible = true;
////					break;
////				}	
////			}
////		}
////		else {
//			
//			isVisible = this.isVisible(viewPort, vertex.getGeometry().getCenter());
////		}
//				
//		return isVisible;
//	}

	
//	@Override
//	public boolean isVisible(Vector2D viewPort, Area area) {
//
//		boolean isVisible = false;
//		
//		if(this.areaVisibilityMap.containsKey(area)) {
//			
//			if(!area.getGeometry().contains(viewPort)) {
//				
//				for(Polygon2D triangle : this.areaVisibilityMap.get(area))  {
//					
//					if(triangle.contains(viewPort)) {
//						
//						isVisible = true;
//						break;
//					}	
//				}
//			}
//			else {
//				
//				isVisible = true;
//			}
//		}
//		else {
//			
//			isVisible = this.isVisible(viewPort, area.getGeometry().getCenter());
//		}
//		
//		return isVisible;
//	}

//	private class ObstacleLineComparator implements Comparator<Pair<Vector2D, Segment2D>> {
//
//		private Vector2D viewPort = null;
//		
//		public ObstacleLineComparator(Vector2D viewPort) {
//			
//			this.viewPort = viewPort;
//		}
//		
//		@Override
//		public int compare(Pair<Vector2D, Segment2D> first, Pair<Vector2D, Segment2D> second) {
//
//		
//			double distanceToFirst = viewPort.distance(first.getLeft());//().distanceBetween(viewPort);
//			double distanceToSecond = viewPort.distance(second.getLeft());// second.getRight().distanceBetween(viewPort);
//					
//			if(distanceToFirst < distanceToSecond)  {
//			
//				return -1;
//			}
//			else if(distanceToFirst > distanceToSecond) {
//				
//				return 1;
//			}
//			
//			return 0;
//		}
//	}	
	
//	private class CloseToAxisComperator implements Comparator<Vector2D> {
//
//		private Vector2D axis = GeometryFactory.createVector(0.0, 0.0);
//		
//		@Override
//		public int compare(Vector2D first, Vector2D second) {
//
//			double distanceToFirst = axis.distance(first); 
//			double distanceToSecond = axis.distance(second);
//					
//			if(distanceToFirst < distanceToSecond)  {
//			
//				return -1;
//			}
//			else if(distanceToFirst > distanceToSecond) {
//				
//				return 1;
//			}
//			
//			return 0;
//		}
//	}
}
