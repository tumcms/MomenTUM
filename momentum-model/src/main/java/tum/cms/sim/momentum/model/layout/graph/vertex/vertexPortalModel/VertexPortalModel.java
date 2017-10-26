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

package tum.cms.sim.momentum.model.layout.graph.vertex.vertexPortalModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import tum.cms.sim.momentum.configuration.model.lattice.LatticeModelConfiguration.LatticeType;
import tum.cms.sim.momentum.configuration.model.lattice.LatticeModelConfiguration.NeighborhoodType;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.layout.graph.GraphOperation;
import tum.cms.sim.momentum.model.layout.lattice.LatticeModel;
import tum.cms.sim.momentum.utility.geometry.GeometryFactory;
import tum.cms.sim.momentum.utility.geometry.Vector2D;
import tum.cms.sim.momentum.utility.graph.GraphTheoryFactory;
import tum.cms.sim.momentum.utility.graph.Vertex;
import tum.cms.sim.momentum.utility.lattice.CellIndex;
import tum.cms.sim.momentum.utility.lattice.ILattice;
import tum.cms.sim.momentum.utility.lattice.LatticeTheoryFactory;

/**
 * This model finds portal nodes in a layout based on the ideas from
 * https://hal.inria.fr/inria-00510188/document
 * The nodes are not circles but segments (different geometry). 
 * However, the tactical model do not consider segments jet.
 * 
 * @author Peter M. Kielar
 *
 */
public class VertexPortalModel extends GraphOperation {

	private static String cellSizeName = "cellSize";
	
	/**
	 * The portal node generation method:
	 * Create a distance map using a lattice.
	 * Use watershed method to find where the "sinks" touch.
	 * At the touching points nodes are generated.
	 */
	@Override
	public void callPreProcessing(SimulationState simulationState) {

		double cellSize = this.properties.getDoubleProperty(cellSizeName);
		
		ILattice lattice = LatticeTheoryFactory.createLattice(
				"distanceMap", 
				LatticeType.Quadratic,
				NeighborhoodType.Touching,
				cellSize,
				this.scenarioManager.getScenarios().getMaxX() + cellSize,
				this.scenarioManager.getScenarios().getMinX() - cellSize,
				this.scenarioManager.getScenarios().getMaxY() + cellSize,
				this.scenarioManager.getScenarios().getMinY() - cellSize);
		
		int max = (int)(((this.scenarioManager.getScenarios().getMaxX() > this.scenarioManager.getScenarios().getMaxY() ?
				this.scenarioManager.getScenarios().getMaxX() :
					this.scenarioManager.getScenarios().getMaxY()) + 1)
					/ cellSize);
				
 		List<CellIndex> occupiedCells = LatticeModel.fillLatticeForObstaclesWithValue(lattice, this.scenarioManager.getScenarios(), max);
	
 		// compute the distance map, each cell contains the value minimal distance to obstacles
		int globalMinima = lattice.computeDistanceMap(occupiedCells);
		
		// find all local minimal connected cell set in the distance map, each set is a list 
		List<List<CellIndex>> localMinimal = lattice.findLocalMinimal(max, globalMinima);
	
		// find all portals, the portal cells have value 0
		this.waterShedFindPortals(lattice, localMinimal, globalMinima, max);
			
		// find all connected cells with zero and create a vertex in the center of the portal cells
		List<Vertex> portalCells = this.findPortalVertices(lattice);
		
		// fill graph with portal cells
		portalCells.forEach(portalCell -> this.scenarioManager.getGraph().addVertex(portalCell));
	}
	
	private List<Vertex> findPortalVertices(ILattice lattice) {
		
		
		HashSet<CellIndex> visited = new HashSet<>();
		HashMap<Integer, ArrayList<CellIndex>> connectedCellSets = new HashMap<>();
		
		int currentPortalSetId = 0;
		
		for(CellIndex cell : lattice.getCellsInOrder()) {
			
			if(!visited.contains(cell) && (int)lattice.getCellNumberValue(cell) == 0) { // part of portal
				
				// new set
				currentPortalSetId++;
				connectedCellSets.put(currentPortalSetId, new ArrayList<>());
				
				LinkedList<CellIndex> exploreSet = new LinkedList<>(); // exploration list to find set cells
				exploreSet.push(cell);		
				visited.add(cell);
				
				while(!exploreSet.isEmpty() ) {
					
					// fill connected cell list
					CellIndex currentCell = exploreSet.pop();
					visited.add(currentCell);
					connectedCellSets.get(currentPortalSetId).add(currentCell);
					
					// explore adjacent cells 
					List<CellIndex> adjacentCells = lattice.getAllNeighborIndices(currentCell);

					for(CellIndex adjacentCell : adjacentCells) {
						
						if(!visited.contains(adjacentCell) && (int)lattice.getCellNumberValue(adjacentCell) == 0) {
							
							exploreSet.add(adjacentCell);
							visited.add(adjacentCell);
						}
					}
				}			
			}
		}
		
		// now we have all connected cells in the hashmap
		// create vertices based on here the method
		ArrayList<Vertex> vertices = new ArrayList<>();
		
		for(ArrayList<CellIndex> connectedCells : connectedCellSets.values()) {
			
			vertices.add(this.createMassVertex(lattice, connectedCells));
		}
		
		return vertices;
	}

	private void waterShedFindPortals(ILattice lattice, List<List<CellIndex>> localMinimal, int globalMinima, int max) {

		// Initialize local minimal region with region ids.
		int currentId = -1;
	
		for(List<CellIndex> minimalSet : localMinimal) {
		
			for(CellIndex minimalCell : minimalSet) {
				
				lattice.setCellNumberValue(minimalCell, currentId);
			}

			currentId--;
		}
		
		HashSet<CellIndex> visited = new HashSet<>();
		LinkedList<CellIndex> currentPool = new LinkedList<>();
		
		for(List<CellIndex> minimalSet : localMinimal) {
		
			for(CellIndex cell : minimalSet) {
				
				List<CellIndex> adjacentCells = lattice.getAllNeighborIndices(cell);

				for(CellIndex adjacentCell : adjacentCells) {
					
					if((int)lattice.getCellNumberValue(adjacentCell) < 0) {
						
						currentPool.add(adjacentCell);
						visited.add(adjacentCell);
					}
				}
			}
		}
		
		int currentDepth = globalMinima;

		 while(currentDepth < max) { // watershed step by step
			
			LinkedList<CellIndex> nextPool = new LinkedList<>();
			
			while(!currentPool.isEmpty()) { // watershed from current sources 
				
				CellIndex cell = currentPool.pop();
				int cellValue = (int)lattice.getCellNumberValue(cell);
				
				if(cellValue > currentDepth) { // stick to depth
					
					nextPool.add(cell);
					continue;
				}
				
				List<CellIndex> adjacentCells = lattice.getAllNeighborIndices(cell);
				HashSet<Integer> adjacentValues = new HashSet<>();
				
				// processes adjacent minimal cells
				for(CellIndex adjacentCell : adjacentCells) {
					
					// get current minimal value
					int adjacentValue = (int)lattice.getCellNumberValue(adjacentCell);
					
					if(adjacentValue < 0 && cellValue > 0) { // is a region close by, remember that
						
						adjacentValues.add(adjacentValue);
					}
					else if(!visited.contains(adjacentCell)) { // never add a visited cell
						
						currentPool.add(adjacentCell);
						visited.add(adjacentCell);
					}
				}
				
				if(cellValue < 0) { // never change values of already existng regions
					continue;
				}
				
				if(adjacentValues.size() == 1) { // existing region
					
					lattice.setCellNumberValue(cell, adjacentValues.iterator().next());
				}
				else { // region boundary reached
					
					lattice.setCellNumberValue(cell, 0);
				}
			}
			
			currentPool.addAll(nextPool);
			currentDepth++;
		}
	}
		
	private Vertex createMassVertex(ILattice lattice, ArrayList<CellIndex> connectedCells) {
		
		double x = 0.0;
		double y = 0.0;
		
		for(CellIndex cell : connectedCells) {

			Vector2D cellPosition = lattice.getCenterPosition(cell);
			x += cellPosition.getXComponent();
			y += cellPosition.getYComponent();
		}
		
		Vector2D vertexPosition = GeometryFactory.createVector(x / connectedCells.size(), y / connectedCells.size());
		
		return GraphTheoryFactory.createVertex(GeometryFactory.createCycle(vertexPosition, 1.0));
	}
	
	/**
	 * Nothing to do here
	 */
	@Override
	public void callPostProcessing(SimulationState simulationState) {
		// nothing to do
	}

}
