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

package tum.cms.sim.momentum.model.operational.walking.cellularAutomatons;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;
import tum.cms.sim.momentum.utility.geometry.GeometryFactory;
import tum.cms.sim.momentum.utility.geometry.Line2D;
import tum.cms.sim.momentum.utility.geometry.Vector2D;
import tum.cms.sim.momentum.utility.geometry.operation.GeometryAdditionals;
import tum.cms.sim.momentum.utility.lattice.CellIndex;
import tum.cms.sim.momentum.utility.lattice.ILattice;
import tum.cms.sim.momentum.utility.lattice.Lattice.Occupation;

public class CellularAutomatonsUtility {

	// computes the heading from a given pedestrian to a given target; heading is the direction a pedestrian is heading to :D
	public static Vector2D computeHeading(IRichPedestrian pedestrian, Vector2D target) {
		
		return target.subtract(pedestrian.getPosition()).getNormalized();
	}
	

	
	// returns a list with all cells which are closer to the next target than the current cell
	public static List<CellIndex> findeCellsCloserToTarget(List<CellIndex> cells, double distanceCurrentPositionToTarget, Vector2D target, ILattice lattice) {
		
		List<CellIndex> cellsCloserToTarget =  cells.stream()
														.filter(cell -> 
														  lattice.getCenterPosition(cell).distance(target) <= distanceCurrentPositionToTarget)
														.collect(Collectors.toList());
		return cellsCloserToTarget;
	}
	
	// returns the cell from a list of cells which is closest to the beeline from last target to the current target
	public static CellIndex findCellClosestToBeeline(Vector2D currentPosition, Vector2D currentTarget, 
			Vector2D previousTarget, List<CellIndex> cellsToTest, ILattice lattice) {
	
		Line2D beelineToTarget = GeometryFactory.createLine2D(previousTarget, currentTarget.subtract(previousTarget));		
		
		double currentPositionDistanceToBeeline = GeometryAdditionals.distanceFromPointToLine(currentPosition, beelineToTarget);
		CellIndex closestCell = null;
		double closestDistance = Double.NEGATIVE_INFINITY;
		
		for (CellIndex closerCell: cellsToTest) {
			
			Vector2D newPosition = lattice.getCenterPosition(closerCell);
			double newPositionDistanceToBeeline = GeometryAdditionals.distanceFromPointToLine(newPosition, beelineToTarget);
			
			if (newPositionDistanceToBeeline < currentPositionDistanceToBeeline && closestCell == null) {
				closestCell = closerCell;
				closestDistance = newPositionDistanceToBeeline;
			}	
			if (newPositionDistanceToBeeline < closestDistance) {
				closestCell = closerCell;
				closestDistance = newPositionDistanceToBeeline;
			}		
		}		
		return closestCell;
	}
	
	// returns the cell from a list of cells which is clostest to a given target
	public static CellIndex getCellClosestToTarget(Vector2D currentPosition, Vector2D currentTarget, List<CellIndex> cellsCloserToTarget, ILattice lattice) {
		
		CellIndex cellClosestToTarget = null;
		double minimalDistanceToTarget = Double.NEGATIVE_INFINITY;
		
		for (CellIndex cell : cellsCloserToTarget) {
			
			double cellDistanceToTarget = lattice.getCenterPosition(cell).distance(currentTarget);
			
			if (cellClosestToTarget == null) {
				cellClosestToTarget = cell;
				minimalDistanceToTarget = cellDistanceToTarget;
			}
			if (cellDistanceToTarget < minimalDistanceToTarget) {
				cellClosestToTarget = cell;
				minimalDistanceToTarget = cellDistanceToTarget;
			}
		}	
		return cellClosestToTarget;
	}
	
	// returns all cells from a list of cells which are inside of a given lattice
	public static ArrayList<CellIndex> getCellsWithinLattice(ArrayList<CellIndex> cells, ILattice lattice) {
		
		ArrayList<CellIndex> reachableCells = new ArrayList<CellIndex>();
	
		for (CellIndex cell : cells) {
			
			if (cell.getRow() < lattice.getNumberOfRows() && 
					cell.getColumn() < lattice.getNumberOfColumns() &&
					cell.getRow() > -1 && cell.getColumn() > -1){
				
				reachableCells.add(cell);
			}
		}
		return reachableCells;
	}
	
	public static CellIndex getCellClosestToPosition(ArrayList<CellIndex> cells, Vector2D position, ILattice lattice) {
		
		Double minimalDistance = Double.POSITIVE_INFINITY;
		CellIndex closestCell = null;
		
		for (CellIndex cell : cells) {
			
			Double distanceOfCurrentCell = lattice.getCenterPosition(cell).distance(position);
			
			if (distanceOfCurrentCell < minimalDistance) {
				
				minimalDistance = distanceOfCurrentCell;
				closestCell = cell;
			}	
		}
		return closestCell;
	}

	public static void setDynamicCells(Collection<IRichPedestrian> pedestrians, ILattice lattice) {

		pedestrians.stream()
		.forEach(pedestrian -> lattice.occupyCell(
				lattice.getCellIndexFromPosition(pedestrian.getPosition()), Occupation.Dynamic));
	}
	
	// removes all dynamic objects from a given lattice
	public static void freeDynamicCells(Collection<IRichPedestrian> pedestrians, ILattice lattice) {
		
		pedestrians.stream()
			.forEach(ped -> lattice.freeCell(lattice.getCellIndexFromPosition(ped.getPosition())));
	}


	// closes cells of pedestrians, who cross more than one cell
	public static void setTemporalDynamicCells(Collection<IRichPedestrian> pedestrians, ILattice lattice) {

//		ArrayList<CellIndex> temporalDynamicCells = new ArrayList<CellIndex>();
//		
//		ArrayList<IRichPedestrian> nonFitingPedestrians = pedestrians.stream()
//				.filter(ped -> ped.getPosition().distance(lattice.getCenterPositionFromCellIndex(lattice.getCellIndexFromPosition(ped.getPosition()))) > 0.01 * lattice.getCellEdgeSize() )
//				.collect(Collectors.toCollection(ArrayList::new));
		
//		for (IRichPedestrian ped : pedestrians) {
//			
//			Vector2D position = ped.getPosition();
//			Vector2D cellCenter = lattice.getCenterPositionFromCellIndex(lattice.getCellIndexFromPosition(position));
//			if (position.distance(cellCenter) > 0.01 * lattice.getCellEdgeSize()) {
//				System.out.println(ped);
//			}
//			System.out.println(position.distance(cellCenter));
//		}
		
		System.out.println(pedestrians.size());
		
		
	}

}
