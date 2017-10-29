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

package tum.cms.sim.momentum.model.layout.graph.vertex.vertexMinimalRegionModel;

import java.util.ArrayList;
import java.util.List;
import tum.cms.sim.momentum.configuration.model.lattice.LatticeModelConfiguration.LatticeType;
import tum.cms.sim.momentum.configuration.model.lattice.LatticeModelConfiguration.NeighborhoodType;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.layout.graph.GraphOperation;
import tum.cms.sim.momentum.model.layout.lattice.LatticeModel;
import tum.cms.sim.momentum.utility.geometry.GeometryFactory;
import tum.cms.sim.momentum.utility.geometry.Vector2D;
import tum.cms.sim.momentum.utility.graph.GraphTheoryFactory;
import tum.cms.sim.momentum.utility.graph.Vertex;
import tum.cms.sim.momentum.utility.lattice.CellIndex;
import tum.cms.sim.momentum.utility.lattice.ILattice;
import tum.cms.sim.momentum.utility.lattice.LatticeTheoryFactory;

/**
 * This model finds nodes in a layout based on the ideas that
 * the nodes are position to the maximal local distance to obstacles.
 * Thus, the results are at the start points of a watershed method.
 * The method is a substep of VertexPortalModel
 * 
 * @author Peter M. Kielar
 *
 */
public class VertexMinimalRegionModel extends GraphOperation {

	private static String cellSizeName = "cellSize";
	
	/**
	 * The minimal distance node generation method:
	 * Create a distance map using a lattice.
	 * Find the sinks of the watershed method and create nodes.
	 */
	@Override
	public void callPreProcessing(SimulationState simulationState) {

		double cellSize = this.properties.getDoubleProperty(cellSizeName);
		
		ILattice lattice = LatticeTheoryFactory.createLattice(
				"distanceMap", 
				LatticeType.Quadratic,
				NeighborhoodType.Touching,
				cellSize,
				this.scenarioManager.getScenarios().getMaxX() + cellSize,
				this.scenarioManager.getScenarios().getMinX() - cellSize,
				this.scenarioManager.getScenarios().getMaxY() + cellSize,
				this.scenarioManager.getScenarios().getMinY() - cellSize);
		
		int max = (int)(((this.scenarioManager.getScenarios().getMaxX() > this.scenarioManager.getScenarios().getMaxY() ?
				this.scenarioManager.getScenarios().getMaxX() :
					this.scenarioManager.getScenarios().getMaxY()) + 1)
					/ cellSize);
				
 		List<CellIndex> occupiedCells = LatticeModel.fillLatticeForObstaclesWithValue(lattice, this.scenarioManager.getScenarios(), max);
	
 		// compute the distance map, each cell contains the value minimal distance to obstacles
		int globalMinima = lattice.computeDistanceMap(occupiedCells);
		
		// find all local minimal connected cell set in the distance map, each set is a list 
		List<List<CellIndex>> localMinimal = lattice.findLocalMinimal(max, globalMinima);


		// now we have all connected cells 
		// create vertices based on here the method
		ArrayList<Vertex> vertices = new ArrayList<>();
		
		for(List<CellIndex> connectedCells : localMinimal) {
			
			vertices.add(this.createMassVertex(lattice, connectedCells));
		}
		
		// fill graph with portal cells
		vertices.forEach(portalCell -> this.scenarioManager.getGraph().addVertex(portalCell));
	}

	private Vertex createMassVertex(ILattice lattice, List<CellIndex> connectedCells) {
		
		double x = 0.0;
		double y = 0.0;
		
		for(CellIndex cell : connectedCells) {

			Vector2D cellPosition = lattice.getCenterPosition(cell);
			x += cellPosition.getXComponent();
			y += cellPosition.getYComponent();
		}
		
		Vector2D vertexPosition = GeometryFactory.createVector(x / connectedCells.size(), y / connectedCells.size());
		
		return GraphTheoryFactory.createVertex(GeometryFactory.createCycle(vertexPosition, 1.0));
	}
	
	/**
	 * Nothing to do here
	 */
	@Override
	public void callPostProcessing(SimulationState simulationState) {
		// nothing to do
	}

}
