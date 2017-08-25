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

package tum.cms.sim.momentum.model.meta.transitum.microscopicMesoscopic;

import java.util.ArrayList;
import java.util.stream.Collectors;

import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;
import tum.cms.sim.momentum.model.meta.transitum.data.TransitionArea;
import tum.cms.sim.momentum.model.operational.walking.cellularAutomatons.CellularAutomatonsUtility;
import tum.cms.sim.momentum.utility.geometry.Vector2D;
import tum.cms.sim.momentum.utility.lattice.CellIndex;
import tum.cms.sim.momentum.utility.lattice.ILattice;

public class Micro2MesoTransformation {

	public static boolean shouldBeTransformed(IRichPedestrian microPed, TransitionArea transitionArea, Double transformationThreshold, Double maximalVelocity, ArrayList<TransitionArea> transitionAreasMicroMeso) {
		
		Vector2D propagationVector = MicroMesoUtility.getPropagationVector(microPed, maximalVelocity, transformationThreshold);
		
		Vector2D currentPosition = microPed.getPosition();
		
		Vector2D leftHandEndPoint = MicroMesoUtility.getLeftHandEndPoint(currentPosition, propagationVector);
		Vector2D rightHandEndPoint = MicroMesoUtility.getRightHandEndPoint(currentPosition, propagationVector);
		
		if (transitionArea.containsPosition(leftHandEndPoint) && transitionArea.containsPosition(rightHandEndPoint)) {
			
			return false;
		}	
		
		// tests if propagation vector hits any microscopic zones
		for (TransitionArea transitArea : transitionAreasMicroMeso) {
			
			Vector2D centerOfMicroscopicArea = transitArea.getCenterOfArea();
			double microRadius = transitArea.getInnerRadiusOfArea();
			
			if (leftHandEndPoint.distance(centerOfMicroscopicArea) <= microRadius || rightHandEndPoint.distance(centerOfMicroscopicArea) <= microRadius || currentPosition.distance(centerOfMicroscopicArea) <= microRadius) {
				
				return false;
			}
		}
		
		Vector2D propagationEndposition = microPed.getPosition().sum(propagationVector);
		Vector2D microscopicAreaCenter = transitionArea.getCenterOfArea();
		
		if (propagationEndposition.distance(microscopicAreaCenter) < currentPosition.distance(microscopicAreaCenter)) {
			
			return false;
		}
		return true;
	}

	public static boolean isNeighboringCellFree(CellIndex cellIndex, TransitionArea transitionArea, ILattice lattice) {
			
		CellIndex cell = getFreeNeigboringCell(cellIndex, transitionArea, lattice);
		
		if (cell != null) {
			return true;
		}
		return false;
	}

	public static CellIndex getFreeNeigboringCell(CellIndex cellIndex, TransitionArea transitionArea, ILattice lattice) {
		
		ArrayList<CellIndex> neighboringCells = lattice.getAllNeighborIndices(cellIndex);
		
		for (CellIndex cell : neighboringCells) {
			
			Vector2D centerOfCell = lattice.getCenterPosition(cell);
			
			if (lattice.isCellFree(cell) && transitionArea.containsPosition(centerOfCell)) {
				return cell;
			}
		}		
		return null;
	}


	public static CellIndex getNearestConflictFreeCell(ILattice lattice, TransitionArea transitionArea, ArrayList<IRichPedestrian> microscopicPedestrians, IRichPedestrian pedestrian) {
		
		ArrayList<IRichPedestrian> otherMicroscopicPedestrians = MicroMesoUtility.getOtherPedestrians(pedestrian, microscopicPedestrians);
		CellIndex currentCell = lattice.getCellIndexFromPosition(pedestrian.getPosition());
		Double pedestrianBodyRadius = pedestrian.getBodyRadius();
		Vector2D centerOfCurrentCell = lattice.getCenterPosition(currentCell);
		
		if (lattice.isCellFree(currentCell) && MicroMesoUtility.isCollisionFree(centerOfCurrentCell, pedestrianBodyRadius, otherMicroscopicPedestrians)) {
			
			return currentCell;
		}
		ArrayList<CellIndex> neighboringCells = lattice.getAllNeighborIndices(currentCell);
		
		ArrayList<CellIndex> freeNeighboringCells = neighboringCells.stream()
				.filter(cell -> lattice.isCellFree(cell) && transitionArea.containsPosition(lattice.getCenterPosition(cell)))
				.collect(Collectors.toCollection(ArrayList::new));

		CellIndex closestConflictFreeCell = CellularAutomatonsUtility.getCellClosestToPosition(freeNeighboringCells, centerOfCurrentCell, lattice);
				
		return closestConflictFreeCell;
	}
	

	
	
}


//Polygon2D walkingPolygon = MicroscopicMesoscopicUtility.getWalkingPolygon(microPed, propagationVector);
		// Cycle2D microscopicArea = GeometryFactory.createCycle(transitionArea.getCenterOfArea(), transitionArea.getInnerRadiusOfArea());
		//microscopicArea.getIntersection(walkingPolygon.polygonAsSegments()).isEmpty()
