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

public class CellGrid {
	
	private Double[][] array;

	private int numberOfRows = 0;

	private int numberOfColumns = 0 ;
	
	public int getNumberOfRows() {
		return numberOfRows;
	}
	
	public int getNumberOfColumns() {
		return numberOfColumns;
	}
	
	public CellGrid(int numberOfRows, int numberOfColumns) {
		
		this(numberOfRows, numberOfColumns, 0.0);
	}
	
	public CellGrid(int numberOfRows, int numberOfColumns, Double initialValue) {
		
		this.numberOfRows = numberOfRows;
		this.numberOfColumns = numberOfColumns;
		array = new Double[numberOfRows][numberOfColumns];
		
		if(initialValue != 0.0d) {
			
			this.setAllCells(initialValue);
		}		
	}
	
	public void setCell(CellIndex cellIndex, Double cellValue) { //setter
		
		int row = cellIndex.getRow();
		int column = cellIndex.getColumn();
	
		array[row][column] = cellValue; 
	}

	public void setCell(int row, int column, Double cellValue) { //setter
		
		array[row][column] = cellValue; 
	}
	
public Double getCell(CellIndex cellIndex) {  
		
		int row = cellIndex.getRow();
		int column = cellIndex.getColumn();
	
		if(array[row][column] == null) {
			
			return new Double(0.0);
		}
		
		return array[row][column];
	}
	
	public Double getCell(int row, int column) {  

		if(array[row][column] == null) {
			
			return new Double(0.0);
		}
		
		return array[row][column];
	}
	
	public void setAllCells(double cellValue) {
		
		int maxRows = this.getNumberOfRows();
		int maxColumns = this.getNumberOfColumns();
		
		for (int row = 0; row < maxRows; row++) {
			
			for (int column = 0; column < maxColumns; column++) {
				
				this.setCell(row, column, cellValue);
			}
		}
	}
}
