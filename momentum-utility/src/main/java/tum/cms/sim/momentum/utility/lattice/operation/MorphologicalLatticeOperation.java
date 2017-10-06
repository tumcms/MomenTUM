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
import java.util.List;

import net.algart.arrays.Matrix;
import net.algart.arrays.SimpleMemoryModel;
import net.algart.arrays.UpdatableBitArray;
import net.algart.matrices.skeletons.OctupleThinningSkeleton2D;
import tum.cms.sim.momentum.utility.geometry.Vector2D;
import tum.cms.sim.momentum.utility.lattice.CellIndex;
import tum.cms.sim.momentum.utility.lattice.ILattice;
import tum.cms.sim.momentum.utility.lattice.Lattice.Occupation;

public class MorphologicalLatticeOperation {
	
	private MorphologicalLatticeOperation() { }
	/**
	 * Performs a thinning of the graph on a lattice with help of the octuple skinning skeleton algorithm
	 * presented in the <code>net.algart</code> library
	 * @param lattice a lattice where the graph is represented by occupied cells
	 */
	public static void thinLatticeGraph(ILattice lattice, Occupation occupation) {
		
		int columns = lattice.getNumberOfColumns();
		int rows = lattice.getNumberOfRows();
		Matrix<UpdatableBitArray> morphMatrix = SimpleMemoryModel.getInstance().newBitMatrix(rows, columns);
		
		//fill the morphMatrix with the lattice graph
		for(int i=0; i<rows; i++) {
			
			for(int j=0; j<columns; j++) {
				
				if(lattice.getCellValue(i, j) == occupation) {
//				if(!lattice.isCellFree(i, j)) {
					
					morphMatrix.array().setBit(morphMatrix.index(i,j));
				}
			}
		}

		//create the thinning algorithm
		OctupleThinningSkeleton2D thinningSkeleton = OctupleThinningSkeleton2D.getInstance(null, morphMatrix, true, false);
	
		if(!thinningSkeleton.done()) {
			
			thinningSkeleton.performIteration(null);	
		}
		
		//update the lattice graph with the results from the thinning algorithm
		for(int i=0; i<rows; i++) {
			
			for(int j=0; j<columns; j++) {
				
				if(morphMatrix.array().getBit(morphMatrix.index(i, j))) {
					
					lattice.occupyCell(i, j, Occupation.Dynamic);
				}
				else if(!morphMatrix.array().getBit(morphMatrix.index(i, j))) {
					
					lattice.freeCell(i, j);
				}
			}
		}
	}
	
	/**
	 * Finds all candidates for nodes, i.e. points on the lattice which have 3 or more
	 * occupied neighbors.
	 * @param lattice
	 * @param occupyCandidates occupy candidate cells in lattice (for display/debug)
	 */
	public static ArrayList<Vector2D> findNodeCandidates(ILattice lattice, boolean occupyCandidates) {
		
		ArrayList<Vector2D> candidates = new ArrayList<Vector2D>();
		List<CellIndex> candidateCells = new ArrayList<CellIndex>();
		List<CellIndex> latticeCells = lattice.getCellsInOrder();
		
		latticeCells.stream().forEach(cell -> {
			
			if(!lattice.isCellFree(cell) && lattice.getNumberOfOccupiedNeighbors(cell) != 2) {
				
				candidateCells.add(cell);
			}
			
		});
		
		if(occupyCandidates) {
			
			candidateCells.stream().forEach(cell -> lattice.freeCell(cell));
			candidateCells.stream().forEach(cell -> lattice.occupyCell(cell, Occupation.Fixed));
		}
		
		candidateCells.stream().forEach(cell -> {
			candidates.add(lattice.getCenterPosition(cell));
		});
		
		return candidates;
	}
	
	/**
	 * 
	 */
	//public static createDistanceOccupancy(ILattice lattice, )
}
