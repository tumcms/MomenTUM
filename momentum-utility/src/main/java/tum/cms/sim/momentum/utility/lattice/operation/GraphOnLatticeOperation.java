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

package tum.cms.sim.momentum.utility.lattice.operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import tum.cms.sim.momentum.utility.geometry.AxisAlignedBoundingBox2D;
import tum.cms.sim.momentum.utility.geometry.Geometry2D;
import tum.cms.sim.momentum.utility.geometry.Polygon2D;
import tum.cms.sim.momentum.utility.geometry.Segment2D;
import tum.cms.sim.momentum.utility.geometry.Vector2D;
import tum.cms.sim.momentum.utility.geometry.operation.GeometryAdditionals;
import tum.cms.sim.momentum.utility.lattice.CellIndex;
import tum.cms.sim.momentum.utility.lattice.ILattice;
import tum.cms.sim.momentum.utility.lattice.LatticeTheoryFactory;
import tum.cms.sim.momentum.utility.lattice.Lattice.Occupation;
import tum.cms.sim.momentum.utility.voronoi.VoronoiTheoryFactory;
import tum.cms.sim.momentum.configuration.model.lattice.LatticeModelConfiguration.LatticeType;
import tum.cms.sim.momentum.configuration.model.lattice.LatticeModelConfiguration.NeighborhoodType;

public class GraphOnLatticeOperation {
	
	/**
	 * Returns the medial axis consisting of occupied cells on a given lattice for the respective geometry.
	 * The algorithm uses Voronoi diagrams, c.f. <a href="https://en.wikipedia.org/wiki/Voronoi_diagram">
	 * https://en.wikipedia.org/wiki/Voronoi_diagram</a>
	 * @param cellEdgeSize
	 * @param boundingBox
	 * @param blockingGeometries
	 * @return Lattice with occupied medial axis
	 */
	public ILattice createGraphOnLattice(double cellEdgeSize, 
			AxisAlignedBoundingBox2D boundingBox,
			Collection<Geometry2D> blockingGeometries) {
		boolean console = false;
		boolean paint = false;
		
		
		if(console) System.out.print("Creating lattice ... ");
		ILattice lattice = LatticeTheoryFactory.createLattice("routeMapLattice", 
				LatticeType.Quadratic, 
				NeighborhoodType.Touching, 
				cellEdgeSize, //cell edge size
				boundingBox.getMaxX(), boundingBox.getMinX(), 
				boundingBox.getMaxY(), boundingBox.getMinY()); 
		if(console) System.out.print("done\n");
		
		
		if(console) System.out.print("Creating voronoi points ... ");
		List<Vector2D> voronoiPoints = this.occupyObstacleCells(blockingGeometries, lattice);
		if (console) System.out.print("done\n");
		if(paint) {
			System.out.println("Lattice with occupied obstacles:");
			lattice.paintLattice();
		}
		
		
		if(console) System.out.print("Creating triangulation ... ");
		List<Segment2D> voronoiSegments = VoronoiTheoryFactory.createVoronoiSegments(voronoiPoints, boundingBox);
//				boundingBox.getMaxX(), boundingBox.getMinX(), 
//				boundingBox.getMaxY(), boundingBox.getMinY());
		if(console) System.out.print("done\n");
		
		
		//delete all segments which are intersecting or are inside obstacles
		if(console) System.out.print("Cleaning triangulation ... ");
		List<Segment2D> clean = this.deleteIntersectingSegments(voronoiSegments, blockingGeometries, boundingBox.getGeometry());
		if(console) System.out.print("done\n");
		
		
		//now occupy only the clean set and derive the graph from this
		if(console) System.out.print("Process graph on lattice ... ");
		lattice.setAllCells(Occupation.Empty);
		lattice.occupyAllSegmentCells(clean, Occupation.Dynamic);
		if(paint){
			System.out.println("\n\nLattice with graph:");
			lattice.paintLattice();
		}
		//thin the occupied cells
		MorphologicalLatticeOperation.thinLatticeGraph(lattice, Occupation.Dynamic);
		if(paint) {
			System.out.println("\n\nThinned lattice graph:");
			lattice.paintLattice();
		}
		System.out.print("done\n");
	
		
		
		return lattice;
	}
	
	/**
	 * Finds all cells on a lattice intersecting borders of blocking geometries.
	 * @param blocking
	 * @param lattice
	 * @return List of vectors of all found Voronoi points
	 */
	private List<Vector2D> occupyObstacleCells(Collection<Geometry2D> blocking, ILattice lattice) {
		
		List<Vector2D> borderPoints = new ArrayList<Vector2D>();
		
		blocking.forEach(obs -> {
			if (obs instanceof Polygon2D) {
				HashMap<CellIndex, Vector2D> borderCellPositions = lattice.getBorderPositionForCells((Polygon2D) obs);
				for(Entry<CellIndex, Vector2D> cellIndexPosition : borderCellPositions.entrySet()) {
							borderPoints.add(cellIndexPosition.getValue());
				}
				
				lattice.occupyAllPolygonCells((Polygon2D) obs, Occupation.Fixed);
			}
			else if (obs instanceof Segment2D) {
				HashMap<CellIndex, Vector2D> borderCellPositions = lattice.getBorderPositionForCells((Segment2D) obs);
				for(Entry<CellIndex, Vector2D> cellIndexPosition : borderCellPositions.entrySet()) {
							borderPoints.add(cellIndexPosition.getValue());
				}
				
				lattice.occupyAllSegmentCells(obs.getSegments(), Occupation.Fixed);
			}
		});

		return borderPoints;
	}
	
	/**
	 * Deletes all segments from the Voronoi diagram which intersect the blocking geometries or are not
	 * inside the lattice boundaries.
	 * @param segments Voronoi segments
	 * @param blockingGeometries
	 * @param boundary of the lattice
	 * @return the cleaned segment list
	 */
	private List<Segment2D> deleteIntersectingSegments(Collection<Segment2D> segments, 
			Collection<Geometry2D> blockingGeometries,
			Geometry2D boundary) {
		
		List<Segment2D> cleanSegments = new ArrayList<Segment2D>();
		List<Boolean> intersectingFlag = new ArrayList<Boolean>();
		for(Segment2D current : segments) {
			intersectingFlag.clear();
			for(Geometry2D obs : blockingGeometries) {
				if(obs.getIntersection(current).isEmpty()
						&& !GeometryAdditionals.contains(obs, current)
						&& GeometryAdditionals.contains(boundary, current))
					intersectingFlag.add(Boolean.FALSE);
				else
					intersectingFlag.add(Boolean.TRUE);
			}
			if(!intersectingFlag.contains(Boolean.TRUE)) {
				cleanSegments.add(current);
			}
		}
		return cleanSegments;
	}
	
	public ILattice findMedialAxis(double cellEdgeSize, 
			AxisAlignedBoundingBox2D boundingBox,
			Collection<Geometry2D> blockingGeometries) {
		
		ILattice lattice = LatticeTheoryFactory.createLattice("medialAxisLattice", 
				LatticeType.Quadratic, 
				NeighborhoodType.Edge, 
				cellEdgeSize,
				boundingBox.getMaxX(), boundingBox.getMinX(), 
				boundingBox.getMaxY(), boundingBox.getMinY()); 
		
		HashMap<CellIndex, Boolean> visitedMap = new HashMap<CellIndex, Boolean>();
		
//		lattice.paintLattice();
		boolean freeCells = true;
		double distance = 1.0;

		//configure the HashMap and set all obstacle cells to visited == true
		for (CellIndex cell : lattice.getAllCellsWith(Occupation.Fixed)) {
			visitedMap.put(cell, true);
		}
		
		for (CellIndex cell : lattice.getAllCellsWith(Occupation.Empty)) {
			visitedMap.put(cell, false);
		}
		
		//save the "name" of the obstacle on every cell occupied by it
		HashMap<CellIndex, String> obstacleMap = this.createObstacleMap(blockingGeometries, lattice, Occupation.Fixed);
		
		//assign distance = 1 to every unvisited neighbor of the obstacles and save the name of the nearest
		//obstacle in the obstacleMap
		for (CellIndex cell : lattice.getAllCellsWith(Occupation.Fixed)) {
			this.floodNeighbors(lattice, cell, distance, Occupation.Dynamic, visitedMap, obstacleMap);
		}

		//repeat, until all cells are filled
		while (freeCells) {
			distance++;
			
			for (CellIndex cell : lattice.getAllCellsWithValue(distance - 1)) {
				this.floodNeighbors(lattice, cell, distance, Occupation.Dynamic, visitedMap, obstacleMap);
			}
			for (CellIndex cell : lattice.getAllCellsWithValue(distance - 1)) {
				visitedMap.put(cell, true);
			}
			
			if (lattice.getAllCellsWithValue(0.0).isEmpty()) {
				freeCells = false;
			}
		}

		
//		lattice.paintLattice();
//		System.out.println(lattice.getNeighborhoodType());
		
		//change the neighborhood type so the thinning algorithm can work properly
		lattice.setNeighborhoodType(NeighborhoodType.Touching);
		MorphologicalLatticeOperation.thinLatticeGraph(lattice, Occupation.Dynamic);
		
//		System.out.println(lattice.getNeighborhoodType());
//		lattice.paintLattice();
		
		return lattice;
	}
	
	/**
	 * Floods all neighbors of the cell with the next higher distance, if the neighbor does not yet
	 * have a distance assigned. Also occupies cells, which have an equidistant distance to two or more 
	 * obstacles.
	 * @param lattice
	 * @param cell
	 * @param distance
	 * @param occupation
	 * @param visitedMap
	 * @param obstacleMap
	 */
	private void floodNeighbors(ILattice lattice, CellIndex cell, Double distance, Occupation occupation,
			HashMap<CellIndex, Boolean> visitedMap,
			HashMap<CellIndex, String> obstacleMap) {
		
		for (CellIndex c : lattice.getAllNeighborIndices(cell)) {
			Boolean visited = visitedMap.get(c);
			
			if (lattice.isInLatticeBounds(c) && !visited) {
				
				if (obstacleMap.get(c) != null && !obstacleMap.get(c).equals(obstacleMap.get(cell))) {
					//this is the case, if the current cell has not yet been visited and
					//has two different equidistant obstacles
//					String o1 = obstacleMap.get(c);
//					String o2 = obstacleMap.get(cell);
					lattice.occupyCell(c, occupation);
				} else {
					//otherwise, the obstacle of the parent is copied to the current cell
					String obstacle = obstacleMap.get(cell);
					obstacleMap.put(c, obstacle);
				}
				
				if (lattice.getCellNumberValue(c) == 0) {
					lattice.setCellNumberValue(c, distance);
				}
			}
		}
		
	}
	
	/**
	 * Occupies all cells blocked by an obstacle and returns a HashMap of the occupied cells liked to
	 * the name of the obstacle which blocks them.
	 * @param blockingGeometries
	 * @param lattice
	 * @param occupation
	 * @return
	 */
	private HashMap<CellIndex, String> createObstacleMap(Collection<Geometry2D> blockingGeometries, 
			ILattice lattice,
			Occupation occupation) {
		
		HashMap<CellIndex, String> obstacleMap = new HashMap<CellIndex, String>();
		
		blockingGeometries.forEach(obs -> {
			if (obs instanceof Polygon2D) {
				HashMap<CellIndex, Vector2D> borderCellPositions = lattice.getBorderPositionForCells((Polygon2D) obs);
				for(Entry<CellIndex, Vector2D> cellIndexPosition : borderCellPositions.entrySet()) {

					String vertexString = obs.getVertices().toString();
					obstacleMap.put(cellIndexPosition.getKey(), vertexString);
				}
				
				lattice.occupyAllPolygonCells((Polygon2D) obs, occupation);
			}
			else if (obs instanceof Segment2D) {
				HashMap<CellIndex, Vector2D> borderCellPositions = lattice.getBorderPositionForCells((Segment2D) obs);
				for(Entry<CellIndex, Vector2D> cellIndexPosition : borderCellPositions.entrySet()) {
					
					String vertexString = obs.getVertices().toString();
					obstacleMap.put(cellIndexPosition.getKey(), vertexString);
				}
				
				lattice.occupyAllSegmentCells(obs.getSegments(), occupation);
			}
		});
		
		return obstacleMap;
	}

}
