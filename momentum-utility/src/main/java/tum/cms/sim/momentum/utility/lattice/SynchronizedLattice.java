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

package tum.cms.sim.momentum.utility.lattice;

import java.util.Collection;
import tum.cms.sim.momentum.configuration.model.lattice.LatticeModelConfiguration.LatticeType;
import tum.cms.sim.momentum.configuration.model.lattice.LatticeModelConfiguration.NeighbourhoodType;

//Internal Agreement: 
//	Occupied by fixed Object: Double.NaN; 
//  Occupied by dynamic Object: Double.MAX_VALUE;
// 	other cases: cell is empty 

public class SynchronizedLattice extends Lattice implements ILattice {

	protected SynchronizedLattice(LatticeType latticeType, NeighbourhoodType neigborhoodType, double cellEdgeSize,
			double maxX, double minX, double maxY, double minY) {

		super(latticeType, neigborhoodType, cellEdgeSize, maxX, minX, maxY, minY);
	}

	public synchronized void occupyCells(Collection<CellIndex> cells, Occupation occupation) {
		
		super.occupyCells(cells, occupation);
	}
	
	public synchronized Boolean freeCell(int row, int column) {
		
		return super.freeCell(row, column);
	}
	
	public synchronized Boolean isCellFree(CellIndex cellIndex) {
		
		return super.isCellFree(cellIndex);	
	}
	
	public synchronized void increaseCellNumberValue(CellIndex cellIndex, double addToNumberValue) {
		
		super.increaseCellNumberValue(cellIndex, addToNumberValue);
	}
	
	public synchronized void setCellTo(CellIndex cellIndex, Occupation occupation) {
		
		super.setCellTo(cellIndex, occupation);
	}
	
	public synchronized boolean occupyCell(CellIndex cellIndex, Occupation occupation) {
		
		return super.occupyCell(cellIndex, occupation);
	}
	
	public synchronized Boolean occupyCellIfFree(CellIndex cellIndex, Occupation occupation) {
		
		return super.occupyCellIfFree(cellIndex, occupation);
	}
	
	public synchronized Boolean occupyCell(int row, int column, Occupation occupation) {
		
		return super.occupyCell(row, column, occupation);
	}
	
	public synchronized boolean setCellIfFree(CellIndex cellIndex){
		
		return super.setCellIfFree(cellIndex);	
	}
	
	public synchronized boolean freeCell(CellIndex cellIndex){
		
		return super.freeCell(cellIndex);	
	}
}
