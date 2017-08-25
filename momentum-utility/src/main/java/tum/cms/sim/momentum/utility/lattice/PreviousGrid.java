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

//This is a cell grid of cell indexes (Necessary for shortest path algorithm)
public class PreviousGrid extends CellGrid {
	
	private CellIndex[][] previousArray;
	
	public PreviousGrid(int numberOfRows, int numberOfColumns) {
		
		super(numberOfRows, numberOfColumns);
		previousArray = new CellIndex[numberOfRows][numberOfColumns];
		
		for(int rowIter = 0; rowIter < numberOfRows; rowIter++) {
			for(int columnIter = 0; columnIter < numberOfColumns; columnIter++)  {
				previousArray[rowIter][columnIter] = null;
			}
		}
	}
	
	public void setPreviousCell(CellIndex cellIndex, CellIndex thePrevious) { //setter
		
		int row = cellIndex.getRow();
		int column = cellIndex.getColumn();
	
		previousArray[row][column] = thePrevious; 
	}
	
	public CellIndex getPreviousCell(CellIndex cellIndex) {  
		
		int row = cellIndex.getRow();
		int column = cellIndex.getColumn();
	
		return previousArray[row][column];
	}
	
	public void setAllCellsEmpty() {
		
		int maxRows = this.getNumberOfRows();
		int maxColumns = this.getNumberOfColumns();
		
		for (int row = 0; row < maxRows; row++) {
			
			for (int column = 0; column < maxColumns; column++) {
				
				this.setCell(LatticeTheoryFactory.createCellIndex(row, column), null);
			}
		}
	}
}
