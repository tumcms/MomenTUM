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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.math3.util.FastMath;
import tum.cms.sim.momentum.configuration.model.lattice.LatticeModelConfiguration.NeighborhoodType;
import tum.cms.sim.momentum.data.agent.pedestrian.state.operational.WalkingState;
import tum.cms.sim.momentum.data.agent.pedestrian.state.tactical.TacticalState.Motoric;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IOperationalPedestrian;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrianExtension;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.operational.walking.WalkingModel;
import tum.cms.sim.momentum.model.operational.walking.cellularAutomatons.CellularAutomatonsUtility;
import tum.cms.sim.momentum.utility.geometry.Vector2D;
import tum.cms.sim.momentum.utility.lattice.CellIndex;
import tum.cms.sim.momentum.utility.lattice.ILattice;
import tum.cms.sim.momentum.utility.lattice.LatticeTheoryFactory;
import tum.cms.sim.momentum.utility.lattice.Lattice.Occupation;
import tum.cms.sim.momentum.utility.lattice.operation.QuadraticLatticCalculation;

public class DynamicFloorfieldOperational extends WalkingModel {

	private static String scenarioLatticeIdName = "scenarioLatticeId";
	
	private HashMap<Integer, EikonalCalculator> calculators = new HashMap<>();
	private ILattice layoutLattice = null;

	@Override
	public IPedestrianExtension onPedestrianGeneration(IRichPedestrian pedestrian) {
		
		DynamicFloorfieldPedestrianExtension newExtension = new DynamicFloorfieldPedestrianExtension();	
		return newExtension;
	}

	@Override
	public void onPedestrianRemoval(IRichPedestrian pedestrian) {
		// nothing to do
	}

	@Override
	public void callPreProcessing(SimulationState simulationState) {
		
		//load the lattice from the config and get it from the scenario manager
		int latticeId = this.properties.getIntegerProperty(scenarioLatticeIdName);
		
		layoutLattice = this.scenarioManager.getLattice(latticeId);

		for(int iter = 1; iter <= simulationState.getNumberOfThreads(); iter++) {
			
			ILattice doubleLattice = LatticeTheoryFactory.createLattice(layoutLattice.getName() + "_" + String.valueOf(iter),
					layoutLattice.getType(),
					layoutLattice.getNeighborhoodType(), 
					layoutLattice.getCellEdgeSize(),
					scenarioManager.getScenarios().getMaxX(),
					scenarioManager.getScenarios().getMinX(), 
					scenarioManager.getScenarios().getMaxY(),
					scenarioManager.getScenarios().getMinY());
			
			doubleLattice.setAllCells(layoutLattice);
			
			EikonalCalculator calculator = new EikonalCalculator();
			calculator.setLattice(doubleLattice);
			calculator.setSzenarioSize(this.scenarioManager.getScenarios().getScenarioSize()); 
			calculators.put(iter, calculator);
		}
	}

	@Override
	public void callBeforeBehavior(SimulationState simulationState, Collection<IRichPedestrian> pedestrians) {

		// occupy the lattice with the current position of all pedestrians
		pedestrians.stream().forEach(pedestrian -> {
		
			layoutLattice.occupyCell(layoutLattice.getCellIndexFromPosition(pedestrian.getPosition()),Occupation.Dynamic);
			
			this.calculators.values().forEach(calculator -> 
				calculator.getLattice().occupyCell(
						calculator.getLattice().getCellIndexFromPosition(pedestrian.getPosition()),Occupation.Dynamic));
		});
	}

	@Override
	public void callPedestrianBehavior(IOperationalPedestrian pedestrian, SimulationState simulationState) {		
		
		DynamicFloorfieldPedestrianExtension pedestrianExtension = (DynamicFloorfieldPedestrianExtension)pedestrian.getExtensionState(this);
		
		double cellEdgeSize = layoutLattice.getCellEdgeSize();
		
		Vector2D currentPosition = pedestrian.getPosition();
		CellIndex currentCellIndex = layoutLattice.getCellIndexFromPosition(currentPosition);
		
		Vector2D currentTarget = pedestrian.getNextWalkingTarget();//pedestrian.getNextNavigationTarget().getPointOfInterest();//getNextWalkingTarget();
		CellIndex targetCellIndex = layoutLattice.getCellIndexFromPosition(currentTarget);
		
		pedestrianExtension.setStock(pedestrianExtension.getStock() + pedestrian.getDesiredVelocity() * simulationState.getTimeStepDuration());
		
		ArrayList<CellIndex> reachableCells = this.calculateReachableCells(currentCellIndex, pedestrianExtension, cellEdgeSize);

		CellIndex desiredCell = calculators.get(simulationState.getCalledOnThread())
				.nextStep(currentCellIndex, targetCellIndex);
		
		if (desiredCell != null && reachableCells != null && reachableCells.contains(desiredCell)) { //to avoid broken paths
			
			pedestrianExtension.setDesiredCell(desiredCell);
		}
		else {
			
			pedestrianExtension.setDesiredCell(currentCellIndex);
		}	
	}
	
	@Override
	public void callAfterBehavior(SimulationState simulationState, Collection<IRichPedestrian> pedestrians) {
		
		this.calculators.values().forEach(calculator -> 
			CellularAutomatonsUtility.freeDynamicCells(pedestrians, calculator.getLattice()));
		
		ArrayList<CellIndex> closedCells = new ArrayList<CellIndex>();
		
		for (IRichPedestrian pedestrian : pedestrians.stream()
				.filter(pedestrian -> pedestrian.getMotoricTask() == Motoric.Standing)
				.collect(Collectors.toList())) {
			
			DynamicFloorfieldPedestrianExtension pedestrianExtension =
					(DynamicFloorfieldPedestrianExtension)pedestrian.getExtensionState(this);		
			pedestrianExtension.setDesiredCell(null);
		}
				
		List<IRichPedestrian> pedestriansSortedByStock = pedestrians.stream()
				.sorted((leftPedestrian, rightPedestrian) -> 
						Double.compare((-1) * ((DynamicFloorfieldPedestrianExtension)leftPedestrian.getExtensionState(this)).getStock(),
									   (-1) * ((DynamicFloorfieldPedestrianExtension)rightPedestrian.getExtensionState(this)).getStock())) // -1 * for inverse sort
				.collect(Collectors.toList());

		
		for (IRichPedestrian pedestrian : pedestriansSortedByStock) {
			
			DynamicFloorfieldPedestrianExtension pedestrianExtension =
					(DynamicFloorfieldPedestrianExtension)pedestrian.getExtensionState(this);		
			CellIndex desiredCell = pedestrianExtension.getDesiredCell();
			
			if(desiredCell == null) {
				
				desiredCell = layoutLattice.getCellIndexFromPosition(pedestrian.getPosition());
				layoutLattice.occupyCell(desiredCell, Occupation.Dynamic);
			}
			else {
				
				for (CellIndex closedCell : closedCells) {
					
					if (desiredCell.equals(closedCell)) {
						
						Vector2D pedestrianPosition = pedestrian.getPosition();
						desiredCell = layoutLattice.getCellIndexFromPosition(pedestrianPosition);
						break;
					}
				}
	
				this.movePedestrianOnLattice(desiredCell, pedestrian, simulationState);
			}
			closedCells.add(desiredCell);
		}
		
		CellularAutomatonsUtility.freeDynamicCells(pedestrians, layoutLattice);

	}
	
	private ArrayList<CellIndex> calculateReachableCells(CellIndex currentCellIndex,
			DynamicFloorfieldPedestrianExtension pedestrianExtension,
			double cellEdgeSize) {

		double currentStock = pedestrianExtension.getStock();
		
		ArrayList<CellIndex> reachableCells = null;

		switch (layoutLattice.getType()) {
		
		case Hexagon:
			break;
			
		case Quadratic:
				
			if (currentStock >= cellEdgeSize * FastMath.sqrt(2.0) && layoutLattice.getNeighborhoodType() == NeighborhoodType.Touching) {
				
				reachableCells = QuadraticLatticCalculation.getAllMooreNeighborIndices(currentCellIndex,
						layoutLattice.getNumberOfColumns(),
						layoutLattice.getNumberOfRows());
			}
			else if (currentStock >= cellEdgeSize) {
				
				reachableCells =  QuadraticLatticCalculation.getAllNeumannNeighborIndices(currentCellIndex,
						layoutLattice.getNumberOfColumns(),
						layoutLattice.getNumberOfRows());
			}

			break;
		}
		
		return reachableCells;
	}
	
	private void movePedestrianOnLattice(CellIndex desiredCell, IRichPedestrian pedestrian, SimulationState simulationState) {
		
		DynamicFloorfieldPedestrianExtension pedestrianExtension = (DynamicFloorfieldPedestrianExtension)pedestrian.getExtensionState(this);
		
		CellIndex currentCellIndex = layoutLattice.getCellIndexFromPosition(pedestrian.getPosition());
		
		Vector2D newPosition = layoutLattice.getCenterPosition(desiredCell);
		
		Vector2D movingVector = newPosition.subtract(pedestrian.getPosition());
		double movingDistance = movingVector.getMagnitude();
		
		pedestrianExtension.setStock(pedestrianExtension.getStock() - movingDistance);

		Vector2D velocity = movingVector.multiply(1.0 / simulationState.getTimeStepDuration());
		Vector2D heading = CellularAutomatonsUtility.computeHeading(pedestrian, pedestrian.getNextWalkingTarget());

		if(heading.isZero()) {
		
			heading = pedestrian.getHeading();
		}
		
		WalkingState novelState = new WalkingState(newPosition, velocity, heading) ;	
		pedestrian.setWalkingState(novelState);
			
		layoutLattice.freeCell(currentCellIndex);
		layoutLattice.occupyCell(desiredCell, Occupation.Dynamic);
	}
	
	@Override
	public void callPostProcessing(SimulationState simulationState) {
		
		// Nothing to do
	}
}
