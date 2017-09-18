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

package tum.cms.sim.momentum.model.operational.walking.cellularAutomatons.cellularStockModel_Biedermann2015;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.math3.util.FastMath;
import tum.cms.sim.momentum.configuration.model.lattice.LatticeModelConfiguration.NeighbourhoodType;
import tum.cms.sim.momentum.data.agent.pedestrian.state.operational.WalkingState;
import tum.cms.sim.momentum.data.agent.pedestrian.state.tactical.TacticalState.Motoric;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IOperationalPedestrian;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrianExtension;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.operational.walking.WalkingModel;
import tum.cms.sim.momentum.model.operational.walking.cellularAutomatons.CellularAutomatonsUtility;
import tum.cms.sim.momentum.model.operational.walking.cellularAutomatons.cellularStockModel_Biedermann2015.StockPedestrianExtension.WalkPotentialType;
import tum.cms.sim.momentum.utility.geometry.Vector2D;
import tum.cms.sim.momentum.utility.lattice.CellIndex;
import tum.cms.sim.momentum.utility.lattice.ILattice;
import tum.cms.sim.momentum.utility.lattice.Lattice.Occupation;
import tum.cms.sim.momentum.utility.lattice.operation.QuadraticLatticCalculation;
import tum.cms.sim.momentum.utility.probability.HighQualityRandom;

public class StockOperational extends WalkingModel {
	
//	private ArrayList<CellIndex> temporalDynamicCells = null; // transiTUM extension
	
	protected ILattice lattice = null;
	private Integer timeStepMultiplicator = null;
	private Double mesoscopicTimeStep = null;
	
	private static String timeStepMultiplicatorName = "timeStepMultiplicator";
	private static String scenarioLatticeIdName = "scenarioLatticeId";
	
	HighQualityRandom randomGenerator = new HighQualityRandom(System.currentTimeMillis());
	
	@Override
	public void callPreProcessing(SimulationState simulationState) {
		
		int latticeId = this.properties.getIntegerProperty(scenarioLatticeIdName);
		
		timeStepMultiplicator = this.properties.getIntegerProperty(timeStepMultiplicatorName);
		mesoscopicTimeStep = simulationState.getTimeStepDuration() * timeStepMultiplicator;
				
		lattice = this.getLatticeByLatticeID(latticeId);
		
//		if (lattice.getFillingType() != FillingType.ScenarioLayout) {
//			throw new IllegalArgumentException("The operational Lattice with ID " + lattice.getId() + " is not of FillingType " +
//									FillingType.ScenarioLayout.toString() + ". The operational model can use only a lattice which " + 
//									"contains the scenario layout." + this.getName() + ".");
//		}
	}

	@Override
	public IPedestrianExtension onPedestrianGeneration(IRichPedestrian pedestrian) {
		
		StockPedestrianExtension newExtension = new StockPedestrianExtension();	
		return newExtension;
	}

	@Override
	public void onPedestrianRemoval(IRichPedestrian pedestrian) {
		
		// Nothing to do
	}

	@Override
	public void callBeforeBehavior(SimulationState simulationState, Collection<IRichPedestrian> pedestrians) {
		
		CellularAutomatonsUtility.setDynamicCells(pedestrians, lattice);
	}
	
	@Override
	public void callAfterBehavior(SimulationState simulationState, Collection<IRichPedestrian> pedestrians) {
		
		ArrayList<CellIndex> closedCells = new ArrayList<CellIndex>();
		
		for (IRichPedestrian pedestrian : pedestrians.stream()
				.filter(pedestrian -> pedestrian.getMotoricTask() == Motoric.Standing)
				.collect(Collectors.toList())) {
			
			StockPedestrianExtension pedestrianExtension = (StockPedestrianExtension)pedestrian.getExtensionState(this);		
			pedestrianExtension.setDesiredCell(null);
		}
				
		List<IRichPedestrian> pedestriansSortedByStock = pedestrians.stream()
				.sorted((leftPedestrian, rightPedestrian) -> 
						Double.compare((-1) * ((StockPedestrianExtension)leftPedestrian.getExtensionState(this)).getStock(),
									   (-1) * ((StockPedestrianExtension)rightPedestrian.getExtensionState(this)).getStock())) // -1 * for inverse sort
				.collect(Collectors.toList());
		
		for (IRichPedestrian pedestrian : pedestriansSortedByStock) {
			
			StockPedestrianExtension pedestrianExtension = (StockPedestrianExtension)pedestrian.getExtensionState(this);		
			CellIndex desiredCell = pedestrianExtension.getDesiredCell();
			
			if(desiredCell == null) {
				
				desiredCell = lattice.getCellIndexFromPosition(pedestrian.getPosition());
				lattice.occupyCell(desiredCell, Occupation.Dynamic);
			}
			else {
				
				for (CellIndex closedCell : closedCells) {
					
					if (desiredCell.equals(closedCell)) {
						
						Vector2D pedestrianPosition = pedestrian.getPosition();
						desiredCell = lattice.getCellIndexFromPosition(pedestrianPosition);
						break;
					}
				}
	
				this.movePedestrianOnLattice(desiredCell, pedestrian);
			}
			closedCells.add(desiredCell);
		}
		
		CellularAutomatonsUtility.freeDynamicCells(pedestrians, lattice);
		//lattice.paintLattice();
	}

	@Override
	public void callPedestrianBehavior(IOperationalPedestrian pedestrian, SimulationState simulationState) {		
		
		StockPedestrianExtension pedestrianExtension = (StockPedestrianExtension)pedestrian.getExtensionState(this);
		
		double cellEdgeSize = lattice.getCellEdgeSize();
		
		Vector2D currentPosition = pedestrian.getPosition();
		CellIndex currentCellIndex = lattice.getCellIndexFromPosition(currentPosition);
		Vector2D target = pedestrian.getNextWalkingTarget();

		double deltaStock = pedestrian.getDesiredVelocity() * mesoscopicTimeStep;
		double currentStock = pedestrianExtension.getStock() + deltaStock;
		pedestrianExtension.setStock(currentStock);
		
		WalkPotentialType walkability = this.calculatePotential(pedestrian, cellEdgeSize);
		pedestrianExtension.setWalkPotential(walkability);
		
		ArrayList<CellIndex> reachableCells = this.calculateReachableCells(pedestrian);
		
		reachableCells = CellularAutomatonsUtility.getCellsWithinLattice(reachableCells, lattice);

		Stream<CellIndex> freeCellStream = reachableCells.stream().filter(cellIndex -> (lattice.isCellFree(cellIndex)));
		List<CellIndex> possibleMovingCells = new ArrayList<CellIndex>();
		
		if(freeCellStream.count() > 0) {						

			possibleMovingCells = reachableCells.stream()
					.filter(cellIndex -> (lattice.isCellFree(cellIndex)))
					.collect(Collectors.toList());	
		}		
		CellIndex desiredCell = this.calculateDesiredCell(possibleMovingCells, currentCellIndex, pedestrian, target);

		pedestrianExtension.setDesiredCell(desiredCell);
	}

	@Override
	public void callPostProcessing(SimulationState simulationState) { }

	// private functions
	private CellIndex calculateDesiredCell(List<CellIndex> cells, 
			CellIndex currentCell, 
			IOperationalPedestrian pedestrian, 
			Vector2D currentTarget) {
		
		StockPedestrianExtension pedestrianExtension = (StockPedestrianExtension)pedestrian.getExtensionState(this);
		
		CellIndex desiredCell = null;
		Vector2D currentPosition = pedestrian.getPosition();
		double distanceCurrentPositionToTarget = currentPosition.distance(currentTarget);
		
		if (cells.isEmpty()) {
			
			return currentCell;
		}	
	
		List<CellIndex> cellsCloserToTarget = CellularAutomatonsUtility.findeCellsCloserToTarget(cells, distanceCurrentPositionToTarget, currentTarget, lattice);

		if (cellsCloserToTarget.isEmpty() ) {
			
			if (pedestrianExtension.getStock() < 10 * lattice.getCellEdgeSize()) {

				return desiredCell = currentCell;
			}
			
			Double randomNumber = FastMath.random();
			Double indexDouble = randomNumber * cells.size();
			CellIndex randomCell = cells.get(indexDouble.intValue());

			return randomCell;
		}
		
		Vector2D previousTarget = pedestrian.getLastWalkingTarget() != null ?
				pedestrian.getLastWalkingTarget().getGeometry().getCenter() :
					null;
		
		if (previousTarget != null) {
			
			desiredCell = CellularAutomatonsUtility.findCellClosestToBeeline(currentPosition,
					currentTarget, previousTarget, cellsCloserToTarget, lattice);
			
			if (desiredCell != null) {
				
				return desiredCell;
			}
		}
		
		desiredCell = CellularAutomatonsUtility.getCellClosestToTarget(currentPosition, currentTarget, cellsCloserToTarget, lattice);
		
		return desiredCell;
	}
	
	private WalkPotentialType calculatePotential(IOperationalPedestrian pedestrian, double cellEdgeSize) {
		
		StockPedestrianExtension pedestrianExtension = (StockPedestrianExtension)pedestrian.getExtensionState(this);
		
		WalkPotentialType walkability = WalkPotentialType.None;
		double currentStock = pedestrianExtension.getStock();
		
		switch (lattice.getType()) {
		
		case Hexagon:
			break;
			
		case Quadratic:
			
			double neumannDistance = cellEdgeSize;
			double mooreDistance = cellEdgeSize * FastMath.sqrt(2.0);
			
			if (currentStock >= neumannDistance && lattice.getNeighborhoodType() == NeighbourhoodType.Edge) {
				
				walkability = WalkPotentialType.EdgeNeighbour;
			}
			
			if (currentStock >= mooreDistance && lattice.getNeighborhoodType() == NeighbourhoodType.Touching) {
				
				walkability = WalkPotentialType.TouchingNeighbour;
			}		
			break;	
			
		default:
			break;	
		}
		return walkability;
	}
	
	private ArrayList<CellIndex> calculateReachableCells(IOperationalPedestrian pedestrian) {
		
		StockPedestrianExtension pedestrianExtension = (StockPedestrianExtension)pedestrian.getExtensionState(this);
		
		WalkPotentialType walkPotential = pedestrianExtension.getWalkPotential();
		CellIndex cellIndexPedestrian = lattice.getCellIndexFromPosition(pedestrian.getPosition());
		
		ArrayList<CellIndex> reachableCells = new ArrayList<CellIndex>();
		
		if (walkPotential == WalkPotentialType.None) {
			return reachableCells;
		}
		
		switch (lattice.getType()) {
		
		case Hexagon:
			break;
			
		case Quadratic:
				
			if (walkPotential == WalkPotentialType.EdgeNeighbour) {
				reachableCells = QuadraticLatticCalculation.getAllNeumannNeighborIndices(cellIndexPedestrian);
			}
			if (walkPotential == WalkPotentialType.TouchingNeighbour) {
				reachableCells = QuadraticLatticCalculation.getAllMooreNeighborIndices(cellIndexPedestrian);
			}
			break;
			
		default:
			break;		
		}
		return reachableCells;
	}
	
	protected void movePedestrianOnLattice(CellIndex desiredCell, IRichPedestrian pedestrian) {
		
		StockPedestrianExtension pedestrianExtension = (StockPedestrianExtension)pedestrian.getExtensionState(this);
		
		CellIndex currentCellIndex = lattice.getCellIndexFromPosition(pedestrian.getPosition());
		
		Vector2D newPosition = lattice.getCenterPosition(desiredCell);
		
		Vector2D movingVector = newPosition.subtract(pedestrian.getPosition());
		double movingDistance = movingVector.getMagnitude();
		
		double newStock = pedestrianExtension.getStock() - movingDistance;
		pedestrianExtension.setStock(newStock);
		
		Vector2D velocity = movingVector.multiply(1.0 / mesoscopicTimeStep);
		Vector2D heading = CellularAutomatonsUtility.computeHeading(pedestrian, pedestrian.getNextWalkingTarget());
		
//		if(pedestrian.getOperationalState().getMotoricTask().getHeading() != null) {
//		
//			heading = pedestrian.getOperationalState().getMotoricTask().getHeading();
//		}
		
		WalkingState novelState = new WalkingState(newPosition, velocity, heading) ;	
		pedestrian.setWalkingState(novelState);
			
		lattice.freeCell(currentCellIndex);
		lattice.occupyCell(desiredCell, Occupation.Dynamic);
	}
	
	private ILattice getLatticeByLatticeID(int latticeID) {
		
		ArrayList<ILattice> lattices = this.scenarioManager.getScenarios().getLattices();	
		
		ILattice lattice = lattices.stream()
				.filter(grid -> grid.getId() == latticeID)
				.findFirst()
				.get();
				
		return lattice;
	}
}
