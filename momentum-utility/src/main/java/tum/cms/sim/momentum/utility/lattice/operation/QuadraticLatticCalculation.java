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
import java.util.HashMap;

import tum.cms.sim.momentum.utility.geometry.GeometryFactory;
import tum.cms.sim.momentum.utility.geometry.Polygon2D;
import tum.cms.sim.momentum.utility.geometry.Vector2D;
import tum.cms.sim.momentum.utility.lattice.CellIndex;
import tum.cms.sim.momentum.utility.lattice.ILattice;
import tum.cms.sim.momentum.utility.lattice.LatticeTheoryFactory;

public class QuadraticLatticCalculation {
	
	private static int[][] neighborsMoore = { {0, +1}, {-1, +1}, {-1, 0}, {-1, -1}, {0, -1}, {+1, -1}, {+1, 0}, {+1, +1} };
	private static int[][] neighborsNeumann = { {0, +1}, {-1, 0}, {0, -1}, {+1, 0} };
	
	public static int[] getCellIndexFromPosition(Vector2D position, double cellEdgeSize, Vector2D startCellPosition) {
		
		double deltaX = position.getXComponent() - startCellPosition.getXComponent();
		double deltaY = position.getYComponent() - startCellPosition.getYComponent();
		
		int row = (int) ((deltaY + 0.5 * cellEdgeSize) / cellEdgeSize);
		int column = (int) ((deltaX + 0.5 * cellEdgeSize) / cellEdgeSize);
		int [] index = { row , column };
		
		return index;
	}
	
//	public static Vector2D getCornerPositionFromCellIndex(int row, int column, double cellEdgeSize, Vector2D startCell) {
//		
//		double positionX = startCell.getXComponent() + column * cellEdgeSize;
//		double positionY = startCell.getYComponent() + row * cellEdgeSize;
//		
//		return GeometryFactory.createVector(positionX, positionY);
//	}
	
	public static Vector2D getCenterPositionFromCellIndex(int row, int column, double cellEdgeSize, Vector2D startCell) {
		
		double positionX = startCell.getXComponent()  + column * cellEdgeSize;
		double positionY = startCell.getYComponent()  + row * cellEdgeSize;
			
		return GeometryFactory.createVector(positionX, positionY);
	}
	
	public static int[] getNeighborMoore(int row, int column, int direction){//0 -> , then anti-clock wise
	
		int[] d = neighborsMoore[direction];
		int[] neighborCoordinate = {row + d[0], column + d[1]};
		
		return  neighborCoordinate ;	
	}
	
	public static ArrayList<CellIndex> getAllMooreNeighborIndices(CellIndex cellIndex, int maxX, int maxY) {
		
		int row = cellIndex.getRow();
		int column = cellIndex.getColumn();
		ArrayList<CellIndex> listOfNeighbors = new ArrayList<CellIndex>();
		
		// 9 directions by  Moore quadratic cells
		for (int direction = 0; direction < 8; direction++) { 
			
			CellIndex index = LatticeTheoryFactory.createCellIndex(QuadraticLatticCalculation.getNeighborMoore(row, column, direction));
			if(index.getColumn() < 0 || index.getRow() < 0) {
				continue;
			}
			
			if(index.getColumn() > maxX - 1 || index.getRow() > maxY - 1) {
				continue;
			}
			listOfNeighbors.add(index);
		}	
		
		return listOfNeighbors;
	}	
	
	public static int[] getNeighborNeumann(int row, int column, int direction){ //0 -> , then anti-clock wise
		
		int[] d = neighborsNeumann[direction];
		int[] neighborCoordinate = {row + d[1], column + d[0]};
		
		return  neighborCoordinate ;	
	}
	
	public static ArrayList<CellIndex> getAllNeumannNeighborIndices(CellIndex cellIndex, int maxX, int maxY) {
		
		int row = cellIndex.getRow();
		int column = cellIndex.getColumn();
		ArrayList<CellIndex> listOfNeighbors = new ArrayList<CellIndex>();
		
		// 4 directions by  Von-Neumann quadratic cells
		for (int direction = 0; direction <= 3; direction++) { 
			
			CellIndex index = LatticeTheoryFactory.createCellIndex(QuadraticLatticCalculation.getNeighborNeumann(row, column, direction));
			
			if(index.getColumn() < 0 || index.getRow() < 0) {
				continue;
			}
			
			if(index.getColumn() > maxX - 1 || index.getRow() > maxY - 1) {
				continue;
			}
			
			listOfNeighbors.add(index);
		}		
		
		return listOfNeighbors;
	}

	public static HashMap<CellIndex, Vector2D> calculateInsideCellPositions(Polygon2D polygon, ILattice lattice) {
		
		double polygonMinX = polygon.getSmallestVertixValueOfX();
		double polygonMinY = polygon.getSmallestVertixValueOfY();	
		double polygonMaxX = polygon.getLargestVertixValueOfX();
		double polygonMaxY = polygon.getLargestVertixValueOfY();	
		
		CellIndex cellWithMinValues = lattice.getCellIndexFromPosition(polygonMinX, polygonMinY);
		CellIndex cellWithMaxValues = lattice.getCellIndexFromPosition(polygonMaxX, polygonMaxY);
		
		int rowStart = cellWithMinValues.getRow();
		int columnStart = cellWithMinValues.getColumn();
		int rowEnd = cellWithMaxValues.getRow();
		int columnEnd = cellWithMaxValues.getColumn();
		
		HashMap<CellIndex, Vector2D> insideCellPositions = new HashMap<CellIndex, Vector2D>();
		
		for(int rowIter = rowStart; rowIter < rowEnd; rowIter++) {
			
			for(int columnIter = columnStart; columnIter < columnEnd; columnIter++)  {
				
				Vector2D cellPosition = QuadraticLatticCalculation.getCenterPositionFromCellIndex(rowIter,
						columnIter, 
						lattice.getCellEdgeSize(), 
						lattice.getStartCellPosition());
						
				if(polygon.contains(cellPosition)) {
					
					insideCellPositions.put(LatticeTheoryFactory.createCellIndex(rowIter, columnIter), cellPosition);
				}
			}
		}
		return insideCellPositions;
	}	
	
	public static int calculateMaxIndex(double max, double min, double cellEdgeSize) {
		
		return (int)(((max - min + 0.5 * cellEdgeSize) / cellEdgeSize) + 1);	
	}
	
	public static Polygon2D calculateCellPolygon(Vector2D cellCenter, double cellEdgeSize) {
		
		ArrayList<Vector2D> cellPoints = new ArrayList<Vector2D>();
	
		cellPoints.add(GeometryFactory.createVector(cellCenter.getXComponent() + 0.5 * cellEdgeSize, cellCenter.getYComponent() - 0.5 * cellEdgeSize));
		cellPoints.add(GeometryFactory.createVector(cellCenter.getXComponent() + 0.5 * cellEdgeSize, cellCenter.getYComponent() + 0.5 * cellEdgeSize));
		cellPoints.add(GeometryFactory.createVector(cellCenter.getXComponent() - 0.5 * cellEdgeSize, cellCenter.getYComponent() + 0.5 * cellEdgeSize));
		cellPoints.add(GeometryFactory.createVector(cellCenter.getXComponent() - 0.5 * cellEdgeSize, cellCenter.getYComponent() - 0.5 * cellEdgeSize));
		
		return GeometryFactory.createPolygon(cellPoints);	
	}

	public static HashMap<CellIndex, Vector2D> calculateBorderCellPositions(Polygon2D polygon, ILattice lattice) {
		
		double polygonMinX = polygon.getSmallestVertixValueOfX();
		double polygonMinY = polygon.getSmallestVertixValueOfY();	
		double polygonMaxX = polygon.getLargestVertixValueOfX();
		double polygonMaxY = polygon.getLargestVertixValueOfY();	
		
		CellIndex cellWithMinValues = lattice.getCellIndexFromPosition(polygonMinX, polygonMinY);
		CellIndex cellWithMaxValues = lattice.getCellIndexFromPosition(polygonMaxX, polygonMaxY);
		
		int rowStart = cellWithMinValues.getRow();
		int columnStart = cellWithMinValues.getColumn();
		int rowEnd = cellWithMaxValues.getRow();
		int columnEnd = cellWithMaxValues.getColumn();
		
		HashMap<CellIndex, Vector2D> borderCellPositions = new HashMap<CellIndex, Vector2D>();
		
		// test on truncated cells
		if (rowStart > 0) { 
			rowStart = rowStart - 1;
		}
		if (rowEnd < lattice.getNumberOfRows()) {
			rowEnd = rowEnd + 1;
		}
		if (columnStart > 0) {
			columnStart = columnStart - 1;
		}
		if (columnEnd < lattice.getNumberOfColumns()) {
			columnEnd = columnEnd + 1;
		}
		
		
		
		for(int rowIter = rowStart; rowIter < rowEnd; rowIter++) {
			
			for(int columnIter = columnStart; columnIter < columnEnd; columnIter++)  {				
				
				CellIndex currentCellIndex = LatticeTheoryFactory.createCellIndex(rowIter, columnIter);
				Polygon2D currentCellPolygon = lattice.getCellPolygon(currentCellIndex);
				
				if (!polygon.getIntersection(currentCellPolygon).isEmpty()) {
					
					borderCellPositions.put(currentCellIndex, lattice.getCenterPosition(currentCellIndex));
				}					
			}
		}	
		return borderCellPositions;
	}
}
