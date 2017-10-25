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

package tum.cms.sim.momentum.model.generator.geometry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import tum.cms.sim.momentum.configuration.generator.GeneratorGeometryConfiguration.GeometryGeneratorType;
import tum.cms.sim.momentum.configuration.model.lattice.LatticeModelConfiguration.NeighbourhoodType;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;
import tum.cms.sim.momentum.data.layout.ScenarioManager;
import tum.cms.sim.momentum.data.layout.area.Area;
import tum.cms.sim.momentum.model.layout.lattice.LatticeModel;
import tum.cms.sim.momentum.utility.geometry.AxisAlignedBoundingBox2D;
import tum.cms.sim.momentum.utility.geometry.GeometryFactory;
import tum.cms.sim.momentum.utility.geometry.Polygon2D;
import tum.cms.sim.momentum.utility.geometry.Vector2D;
import tum.cms.sim.momentum.utility.lattice.CellIndex;
import tum.cms.sim.momentum.utility.lattice.ILattice;
import tum.cms.sim.momentum.utility.lattice.LatticeTheoryFactory;
import tum.cms.sim.momentum.utility.lattice.Lattice.Occupation;

public class LatticeGeneratorGeometry extends GeneratorGeometry {

	private Double safetyDistance = 0.0;
	private Collection<Vector2D> spawnPositions = null;
	ILattice lattice = null;
	
	public LatticeGeneratorGeometry() {
		
		this.geometryType = GeometryGeneratorType.Lattice;
	}

	@Override
	public void initialize(ScenarioManager scenarioManager, int scenarioLatticeID, Area area, double safetyDistance, double maximalRadius) {
		
		this.safetyDistance = safetyDistance;
		
		AxisAlignedBoundingBox2D boundingBox = GeometryFactory.createAxisAlignedBoundingBox(area.getGeometry());
		Vector2D minPoint = boundingBox.getMinPoint();
		
		ILattice scenarioLattice = scenarioManager.getScenarios().getLattices().get(scenarioLatticeID);
		double scenarioCellEdgeSize = scenarioLattice.getCellEdgeSize();
		
		switch(scenarioLattice.getType()) {
		
		case Hexagon:
			break;
		
		case Quadratic:
			
			double boundingBox_maxX = minPoint.getXComponent() + boundingBox.getWidth();
			double boundingBox_minX = minPoint.getXComponent();
			double boundingBox_maxY = minPoint.getYComponent() + boundingBox.getHeight();
			double boundingBox_minY = minPoint.getYComponent();
			
			Vector2D boundingBox_minX_minY = GeometryFactory.createVector(boundingBox_minX, boundingBox_minY);
			Vector2D boundingBox_maxX_maxY = GeometryFactory.createVector(boundingBox_maxX, boundingBox_maxY);
			
			CellIndex cellIndexBoundingBox_minX_minY = scenarioLattice.getCellIndexFromPosition(boundingBox_minX_minY);
			CellIndex cellIndexBoundingBox_maxX_maxY = scenarioLattice.getCellIndexFromPosition(boundingBox_maxX_maxY);
			
			Vector2D cellCenterBoundingBox_minX_minY = scenarioLattice.getCenterPosition(cellIndexBoundingBox_minX_minY);
			Vector2D cellCenterBoundingBox_maxX_maxY = scenarioLattice.getCenterPosition(cellIndexBoundingBox_maxX_maxY);
			
			double matchedMaxX = cellCenterBoundingBox_maxX_maxY.getXComponent() + scenarioCellEdgeSize;
			double matchedMinX = cellCenterBoundingBox_minX_minY.getXComponent() - scenarioCellEdgeSize;
			double matchedMaxY = cellCenterBoundingBox_maxX_maxY.getYComponent() + scenarioCellEdgeSize;
			double matchedMinY = cellCenterBoundingBox_minX_minY.getYComponent() - scenarioCellEdgeSize;
			
			lattice = LatticeTheoryFactory.createLattice(null,
					scenarioLattice.getType(),
					NeighbourhoodType.Touching,
					scenarioCellEdgeSize,
					matchedMaxX, 
					matchedMinX, 
					matchedMaxY, 
					matchedMinY);

			break;
		}

		HashMap<CellIndex, Vector2D> positionForCells = this.lattice.getInsidePositionForCells((Polygon2D)area.getGeometry());

		// for each solid and wall find polygon elements on the lattice and occupy them
		LatticeModel.fillLatticeForObstacles(lattice, scenarioManager.getScenarios());
		this.lattice.occupyCells(positionForCells.keySet(), Occupation.Empty);		

		this.spawnPositions = new ArrayList<Vector2D>();
		
		for(Vector2D spawnPosition : positionForCells.values()) {
			
			CellIndex positinIndex = this.lattice.getCellIndexFromPosition(spawnPosition);

			if(lattice.isCellFree(positinIndex)) {
				
				this.spawnPositions.add(spawnPosition);
			}
		}
	}
	
	@Override
	public Collection<Vector2D> calculateFreeSpawnPositions(Collection<IRichPedestrian> allPedestrians, double maximalRadius) {

		ArrayList<Vector2D> availablePositions = new ArrayList<Vector2D>();
		
		this.spawnPositions.forEach(spawnPosition -> availablePositions.add(spawnPosition));
		
		for(IRichPedestrian pedestrian : allPedestrians) {
			
			if(availablePositions.isEmpty()) {
				break;
			}
			
			for(int iter = 0; iter < availablePositions.size(); iter++) {
				
				if(availablePositions.get(iter).distance(pedestrian.getPosition()) - (pedestrian.getBodyRadius() + maximalRadius) 
						< this.safetyDistance) {
					
					availablePositions.remove(iter);
					iter--;
				}
			}
		}

		return availablePositions;
	}
}
