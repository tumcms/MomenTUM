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

package tum.cms.sim.momentum.model.operational.walking.cellularAutomatons.dynamicFloorfield_Kneidl2012;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import tum.cms.sim.momentum.utility.geometry.Vector2D;
import tum.cms.sim.momentum.utility.lattice.CellIndex;
import tum.cms.sim.momentum.utility.lattice.ILattice;

public class EikonalCalculator {

	private HashMap<String, CellIndex> predecessorMap = new HashMap<>();
	private ILattice lattice = null;
	
	private Vector2D target = null;
	private double szenarioSize = 0.0;
	
	public void setSzenarioSize(double szenarioSize) {
		this.szenarioSize = szenarioSize;
	}

	public ILattice getLattice() {
		return lattice;
	}

	public void setLattice(ILattice lattice) {
		this.lattice = lattice;
	}

	public CellIndex nextStep(CellIndex origin,CellIndex destination) {
		
		if (origin.equals(destination)) {
			
			return destination;
		}
		
		this.target = this.lattice.getCenterPosition(origin);
		this.lattice.setAllCellIfFree(this.szenarioSize);
		this.lattice.setCellNumberValue(destination, 0.0);
		CellIndex next = this.findNextByFlood(origin, destination);

		predecessorMap.clear();
		
		return next;
	}
	
	/**
	 * Flood from destination to origin
	 * @param origin
	 * @param destination
	 */
	private CellIndex findNextByFlood(CellIndex origin, CellIndex destination) {

		Vector2D originPosition = lattice.getCenterPosition(origin);
		
		ArrayList<CellIndex> candidates = this.aStarComputationOnNeighbours(destination, origin, originPosition);
		
		while(candidates.size() > 0) {
			
			if(candidates.size() > 1) {
        		
            	Collections.sort(candidates, weightComparator);
            }

			CellIndex current = candidates.remove(candidates.size() - 1); //smallest at the end
	
			if (current.equals(origin)) {
				
				break;
			}
		
			candidates.addAll(this.aStarComputationOnNeighbours(current, origin, originPosition));
		}

		return this.predecessorMap.get(String.valueOf(origin.getRow() + "_" + origin.getColumn()));
	}
	
	private ArrayList<CellIndex> aStarComputationOnNeighbours(CellIndex current,
			CellIndex origin,
			Vector2D originPosition) {
		
		ArrayList<CellIndex> realNextCandidates = new ArrayList<>();
		ArrayList<CellIndex> neigbhoourCandidates = this.getComputationNeigbhours(current, origin);
		Vector2D currentPosition = lattice.getCenterPosition(current);
		
		for(CellIndex neighbour : neigbhoourCandidates) {

			Vector2D neighbourPosition = lattice.getCenterPosition(neighbour);
			
			double fromCurrentToNeighbourToTarget = lattice.getCellNumberValue(current) + 
					neighbourPosition.distance(currentPosition) +
					originPosition.distance(neighbourPosition);
			double fromNeighbourToTarget = lattice.getCellNumberValue(neighbour) + originPosition.distance(neighbourPosition);
			
			if (fromCurrentToNeighbourToTarget < fromNeighbourToTarget) {
				
				lattice.setCellNumberValue(neighbour, lattice.getCellNumberValue(current) + neighbourPosition.distance(currentPosition));
				predecessorMap.put(String.valueOf(neighbour.getRow() + "_" + neighbour.getColumn()), current);
				realNextCandidates.add(neighbour);
			}
		}
		
		return realNextCandidates;
	}
	
	/**
	 * Get free NeibhorIndices or target cell if close
	 * @param cellIndex
	 * @param origin
	 * @return
	 */
	private ArrayList<CellIndex> getComputationNeigbhours(CellIndex current, CellIndex target) {
		
		ArrayList<CellIndex> neighbours = this.lattice.getAllNeighborIndices(current);
		
		if(neighbours.contains(target)) {

			neighbours.clear();
			neighbours.add(target);
			return neighbours;
		}
		
		neighbours.removeIf(neighbour -> !this.lattice.isInLatticeBounds(neighbour) ||
				Double.isNaN(this.lattice.getCellNumberValue(neighbour)) ||
				Double.MAX_VALUE == this.lattice.getCellNumberValue(neighbour));
		
		return neighbours;	
	}

	
	private Comparator<CellIndex> weightComparator = new Comparator<CellIndex>() {

		@Override
		public int compare(CellIndex o1, CellIndex o2) {
			
			return -1 * Double.compare(lattice.getCellNumberValue(o1) + lattice.getCenterPosition(o1).distance(target), 
					lattice.getCellNumberValue(o2) + lattice.getCenterPosition(o2).distance(target));
		}
	};
	
}
