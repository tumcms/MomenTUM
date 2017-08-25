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

package tum.cms.sim.momentum.model.analysis.measure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import tum.cms.sim.momentum.data.analysis.AnalysisElement;
import tum.cms.sim.momentum.data.analysis.AnalysisElementSet;
import tum.cms.sim.momentum.model.analysis.AnalysisType;
import tum.cms.sim.momentum.utility.geometry.Vector2D;
import tum.cms.sim.momentum.utility.lattice.CellIndex;
import tum.cms.sim.momentum.utility.lattice.ILattice;
import tum.cms.sim.momentum.utility.lattice.LatticeTheoryFactory;

public class MeasureXtDensitiy extends Measure {

	private final static String latticeIdName = "latticeId";
	private final static String timeRangeName = "timeRange";
	private final static String maximalDensityName = "maximalDensity";
	
	private ILattice measuringLattice = null;

	private ArrayList<ArrayList<CellIndex>> measuringLattices = new ArrayList<>();
	private ILattice resultGrid = null;
	private double maximalDensity = 1.0;
	
	/**
	 * Number of time steps used for XT density method, has to be uneven
	 */
	private int timeRange = 3;
	
	private int densityMeasureTimestep = 0;
	
	@Override
	public void initialize() {
		
		this.inputTypes.add(AnalysisType.xPositionType);
		this.inputTypes.add(AnalysisType.yPositionType);
		
		this.outputTypes.add(AnalysisType.cellCenterX);
		this.outputTypes.add(AnalysisType.cellCenterY);
		this.outputTypes.add(AnalysisType.cornerSize);
		this.outputTypes.add(AnalysisType.density);
		this.outputTypes.add(AnalysisType.maximalDensity);
		
		int latticeId = this.properties.getIntegerProperty(latticeIdName);
		this.timeRange = this.properties.getIntegerProperty(timeRangeName);
		this.maximalDensity = this.properties.getDoubleProperty(maximalDensityName);
		
		this.measuringLattice = this.scenarioManager.getLattices().stream()
				.filter(lattice -> lattice.getId().intValue() == latticeId)
				.findAny()
				.get();

		this.resultGrid = LatticeTheoryFactory.copyLattice(this.measuringLattice, "measureXtResult");
		
		for(int iter = 0; iter < this.timeRange; iter++) {
			
			this.measuringLattices.add(new ArrayList<>());
		}
	}
	
	@Override
	public void measure(long timeStep,
			HashMap<String, AnalysisElementSet> inputMap,
			HashMap<String, AnalysisElementSet> outputMap) {
		
		Iterator<AnalysisElement> currentXPositions = inputMap.get(AnalysisType.xPositionType)
				.getObjectOrderedData().iterator();
		
		Iterator<AnalysisElement> currentYPositions = inputMap.get(AnalysisType.yPositionType)
				.getObjectOrderedData().iterator();

		// clear the current list of cells which is the oldest index
		// this is a ring buffer
		//
		// e.g. 3 elements in currentLattice
		//
		// 0 run, densityMeasureTimestep % timeRange = 0
		// (clear,empty,empty) -> (new,empty,empty)
		// 1 run, densityMeasureTimestep % timeRange = 1
		// (data,clear,empty) -> (data,new,empty)
		// 2 run, densityMeasureTimestep % timeRange = 2
		// (data,data,clear) -> (data,data,new)
		// 3 run, densityMeasureTimestep % timeRange = 0
		// (clear,data,data) -> (new,data,data)
		
		ArrayList<CellIndex> currentLattice = this.measuringLattices.get(densityMeasureTimestep % timeRange);
		currentLattice.clear();

		// for each pedestrian in current time step
		while(currentXPositions.hasNext() && currentYPositions.hasNext()) {
			
			CellIndex inCell = this.measuringLattice.getCellIndexFromPosition(
					currentXPositions.next().getData().doubleValue(),
					currentYPositions.next().getData().doubleValue());
			
			currentLattice.add(inCell);
		}

		// start computing all lists in measuringLattices comprise for xt method
		if(timeRange <= densityMeasureTimestep) {
			
			this.measuringLattices.forEach(cellList -> {
				
				cellList.stream().forEach(cell -> this.resultGrid.increaseCellNumberValue(cell, 1.0));
			});

			// use the grid list to compute the final result, the data is already correct in the list
			this.computeXtDensity(timeRange, timeStep, outputMap);
			
			// clear the result grid every time
			this.resultGrid.setAllCells(0.0);
		}
	
		densityMeasureTimestep++;
	}
	
	private void computeXtDensity(int timeRange, long timeStep, HashMap<String, AnalysisElementSet> outputMap) {
	
		for(int xCellIndex = 0; xCellIndex < this.measuringLattice.getNumberOfRows(); xCellIndex++) {
			
			for(int yCellIndex = 0; yCellIndex < this.measuringLattice.getNumberOfColumns(); yCellIndex++) {

				double densityXt = this.resultGrid.getCellNumberValue(xCellIndex, yCellIndex) / timeRange;
				Vector2D cellCenter = this.resultGrid.getCenterPosition(xCellIndex, yCellIndex);
				
				String id = String.valueOf(LatticeTheoryFactory.createCellIndex(xCellIndex, yCellIndex).hashCode());
				
				outputMap.get(AnalysisType.cornerSize).addElement(
						new AnalysisElement(id, 
								this.resultGrid.getCellEdgeSize(),
								timeStep));
				
				outputMap.get(AnalysisType.maximalDensity).addElement(
						new AnalysisElement(id, 
								maximalDensity,
								timeStep));
				
				outputMap.get(AnalysisType.density).addElement(
						new AnalysisElement(id, 
								densityXt,
								timeStep));
				
				outputMap.get(AnalysisType.cellCenterX).addElement(
						new AnalysisElement(id, 
								cellCenter.getXComponent(),
								timeStep));
				
				outputMap.get(AnalysisType.cellCenterY).addElement(
						new AnalysisElement(id, 
								cellCenter.getYComponent(),
								timeStep));
			}
		}			
	}
}
