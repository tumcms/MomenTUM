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

package tum.cms.sim.momentum.model.layout.lattice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tum.cms.sim.momentum.configuration.model.lattice.LatticeModelConfiguration;
import tum.cms.sim.momentum.configuration.scenario.ScenarioConfiguration;
import tum.cms.sim.momentum.data.layout.Scenario;
import tum.cms.sim.momentum.data.layout.ScenarioManager;
import tum.cms.sim.momentum.data.layout.obstacle.SolidObstacle;
import tum.cms.sim.momentum.data.layout.obstacle.WallObstacle;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.infrastructure.execute.callable.Callable;
import tum.cms.sim.momentum.utility.generic.IHasProperties;
import tum.cms.sim.momentum.utility.generic.PropertyBackPack;
import tum.cms.sim.momentum.utility.generic.Unique;
import tum.cms.sim.momentum.utility.geometry.Polygon2D;
import tum.cms.sim.momentum.utility.lattice.CellIndex;
import tum.cms.sim.momentum.utility.lattice.ILattice;
import tum.cms.sim.momentum.utility.lattice.LatticeTheoryFactory;
import tum.cms.sim.momentum.utility.lattice.Lattice.Occupation;

public class LatticeModel extends Callable implements IHasProperties {
	
	private static String addAdditionalCellsName = "addAdditionalCells";
	
   	protected ScenarioManager scenarioManager = null;
	protected ILattice lattice = null;
	
	private ArrayList<ScenarioConfiguration> configurations = null;
	private LatticeModelConfiguration latticeConfiguration = null; 
	
	public LatticeModel(ArrayList<ScenarioConfiguration> configurations, LatticeModelConfiguration latticeConfiguration) {

		this.latticeConfiguration = latticeConfiguration;
		this.configurations = configurations;
	}

	public void setScenario(ScenarioManager scenario) {
		this.scenarioManager = scenario;
	}
	
	protected PropertyBackPack properties = null;

	@Override
	public PropertyBackPack getPropertyBackPack() {
		
		return this.properties;
	}
	
	@Override
	public void setPropertyBackPack(PropertyBackPack propertyContainer) {

		this.properties = propertyContainer; 
	}
	
	@Override
	public void callPreProcessing(SimulationState simulationState) {
		
		Boolean addAdditionalCells = this.properties.getBooleanProperty(this.addAdditionalCellsName);
		
		if (addAdditionalCells == false) {
			this.lattice = LatticeTheoryFactory.createLattice(				
					latticeConfiguration.getName(), 
					latticeConfiguration.getBehaviorType(),
					latticeConfiguration.getLatticeType(), 
					latticeConfiguration.getNeigborhoodType(), 
					latticeConfiguration.getCellEdgeSize(), 
					scenarioManager.getScenarios().getMaxX(), 
					scenarioManager.getScenarios().getMinX(), 
					scenarioManager.getScenarios().getMaxY(), 
					scenarioManager.getScenarios().getMinY());
		} else {
			this.lattice = LatticeTheoryFactory.createLattice(				
				latticeConfiguration.getName(), 
				latticeConfiguration.getBehaviorType(),
				latticeConfiguration.getLatticeType(), 
				latticeConfiguration.getNeigborhoodType(), 
				latticeConfiguration.getCellEdgeSize(), 
				scenarioManager.getScenarios().getMaxX() + latticeConfiguration.getCellEdgeSize(), 
				scenarioManager.getScenarios().getMinX() - latticeConfiguration.getCellEdgeSize(), 
				scenarioManager.getScenarios().getMaxY() + latticeConfiguration.getCellEdgeSize(), 
				scenarioManager.getScenarios().getMinY() - latticeConfiguration.getCellEdgeSize());
		}
		
		
		
		lattice.setPropertyBackPack(this.properties);
		Unique.generateUnique(lattice, latticeConfiguration);		
		
		lattice.setAllCells(Occupation.Empty);

		fillLatticeForObstacles(lattice, this.scenarioManager.getScenarios());
		
		for(ScenarioConfiguration scenarioConfiguration : configurations) {
			
			if(scenarioConfiguration.getId().intValue() ==  latticeConfiguration.getScenarioId()) {
				
				if(scenarioConfiguration.getLattices() == null) {
					
					scenarioConfiguration.setLattices(new ArrayList<>());
				}
				
				scenarioConfiguration.getLattices().add(latticeConfiguration);
				this.scenarioManager.getScenarios().getLattices().put(this.lattice.getId(), this.lattice);
				
				break;
			}
		}
	}

	@Override
	public void callPostProcessing(SimulationState simulationState) {

		// nothing to do
	}

	public static void fillLatticeForObstacles(ILattice lattice, Scenario scenario) {

		ArrayList<SolidObstacle> solidObstacles = scenario.getSolidObstacles();

		solidObstacles.parallelStream().forEach(obs -> lattice.occupyAllPolygonCells((Polygon2D) obs.getGeometry(), Occupation.Fixed));

		ArrayList<WallObstacle> nonSolidObstacles = scenario.getWallObstacles();
		
		nonSolidObstacles.parallelStream().forEach(obs -> {
			
			lattice.occupyAllSegmentCells(obs.getObstacleParts(), Occupation.Fixed);
		});
	}
	
	/**
	 * 
	 * @param lattice
	 * @param scenario
	 * @param value
	 * @return All occupied cells
	 */
	public static List<CellIndex> fillLatticeForObstaclesWithValue(ILattice lattice, Scenario scenario, double value) {

		List<CellIndex> freeCells = Collections.synchronizedList(new ArrayList<>(lattice.getNumberOfColumns() * lattice.getNumberOfRows()));
		freeCells.addAll(lattice.getCellsInOrder());
		
		ArrayList<SolidObstacle> solidObstacles = scenario.getSolidObstacles();
		ArrayList<WallObstacle> nonSolidObstacles = scenario.getWallObstacles();
		List<List<CellIndex>> occupiedCells = Collections.synchronizedList(new ArrayList<>(solidObstacles.size() * nonSolidObstacles.size()));
	
		solidObstacles.parallelStream().forEach(obs -> occupiedCells.add(lattice.occupyAllPolygonCells((Polygon2D) obs.getGeometry(),value)));
		nonSolidObstacles.parallelStream().forEach(obs -> occupiedCells.add(lattice.occupyAllSegmentCells(obs.getObstacleParts(), value)));
		
		ArrayList<CellIndex> occupied = new ArrayList<>();
		
		occupiedCells.stream().forEach(obstacleList -> occupied.addAll(obstacleList));
		
		return occupied;
	}
}
