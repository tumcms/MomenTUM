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

//import java.util.ArrayList;
//import java.util.List;
//
//import tum.cms.sim.momentumV2.configuration.generator.GeneratorConfiguration;
//import tum.cms.sim.momentumV2.configuration.generator.GeneratorGeometryConfiguration;
//import tum.cms.sim.momentumV2.configuration.generator.GeneratorConfiguration.GeneratorType;
//import tum.cms.sim.momentumV2.configuration.generator.GeneratorGeometryConfiguration.GeneratorFillingType;
//import tum.cms.sim.momentumV2.configuration.generator.GeneratorGeometryConfiguration.GeometryGeneratorType;
//import tum.cms.sim.momentumV2.configuration.model.lattice.LatticeModelConfiguration.FillingType;
//import tum.cms.sim.momentumV2.configuration.model.lattice.LatticeModelConfiguration.LatticeType;
//import tum.cms.sim.momentumV2.configuration.model.lattice.LatticeModelConfiguration.NeighbourhoodType;
//import tum.cms.sim.momentumV2.domain.data.layout.Scenario;
//import tum.cms.sim.momentumV2.domain.data.layout.area.Area;
//import tum.cms.sim.momentumV2.domain.data.layout.obstacle.Obstacle;
//import tum.cms.sim.momentumV2.domain.data.layout.obstacle.SolidObstacle;
//import tum.cms.sim.momentumV2.domain.data.layout.obstacle.WallObstacle;
//import tum.cms.sim.momentumV2.utility.geometry2D.GeometryFactory;
//import tum.cms.sim.momentumV2.utility.geometry2D.Polygon2D;
//import tum.cms.sim.momentumV2.utility.geometry2D.Segment2D;
//import tum.cms.sim.momentumV2.utility.geometry2D.Vector2D;
//import tum.cms.sim.momentumV2.utility.latticeTheory.CellIndex;
//import tum.cms.sim.momentumV2.utility.latticeTheory.EikonalLattice;
//import tum.cms.sim.momentumV2.utility.latticeTheory.LatticeTheoryFactory;
//import tum.cms.sim.momentumV2.utility.latticeTheory.Lattice.Occupation;

public class MainFloorField {
	
	public static void main(String[] args) {
		
//		EikonalLattice lattice = LatticeTheoryFactory.createEikonalLattice("carlos", 
//				LatticeType.Quadratic,
//				NeighbourhoodType.Touching, FillingType.Empty, 0.40, 8, 0, 8, 0);
//		
//		List<Vector2D> thePoints = new ArrayList<Vector2D>();
//		
//		thePoints.add(GeometryFactory.createVector(1, 1));
//		thePoints.add(GeometryFactory.createVector(2, 1));
//		thePoints.add(GeometryFactory.createVector(2, 2));
//		thePoints.add(GeometryFactory.createVector(1, 2));
//		
//		Vector2D startWall = GeometryFactory.createVector(6, 5);
//		Vector2D endWall = GeometryFactory.createVector(6, 7);
//		
//		Polygon2D rectangle = GeometryFactory.createPolygon(thePoints);
////		Cycle2D circle1 = GeometryFactory.createCycle(6, 2, 1);
////		Cycle2D circle2 = GeometryFactory.createCycle(2, 6, 1);
//		
//		Segment2D wall = GeometryFactory.createSegment(startWall, endWall);
//		
//		List<Segment2D> theWalls = new ArrayList<Segment2D>();
//		theWalls.add(wall);
//		
//		lattice.occupyAllPolygonCells(rectangle, Occupation.Fixed);
//		lattice.occupyAllSegmentCells(theWalls, Occupation.Fixed);
//		
//		//if I want to occupy all the border cells... forget it, function is private :(
////		List<CellIndex> allTheCells = lattice.getCellsInOrder();
////		for (CellIndex index : allTheCells)
////		{
////			if (lattice.isInLatticeBounds(index))
////				lattice.occupyCell(index, Occupation.Fixed);
////		}
//		
//		//Origin, destination, intermediate area, landmark
//		ArrayList<Area> areaList = new ArrayList<Area>();
//	//	areaList.add(new OriginArea(1,"The Origin",GeometryFactory.createCycle(6, 2, 1)));
//	//	areaList.add(new DestinationArea(2,"The Destination",GeometryFactory.createCycle(2, 6, 1)));
//		
//		ArrayList<Obstacle> theObstacles = new ArrayList<Obstacle>();
//		theObstacles.add(new SolidObstacle(1,"Rectangle",rectangle));
//		theObstacles.add(new WallObstacle(2,"Wall",wall));
//		//circle obstacle is missing
//		
//		//Graph theGraph = new Graph();
//		
//		GeneratorGeometryConfiguration configGenGeo = new GeneratorGeometryConfiguration();
//		configGenGeo.setFillingType(GeneratorFillingType.Complete);
//		configGenGeo.setGeometryType(GeometryGeneratorType.Lattice);
//		
//		GeneratorConfiguration configGen = new GeneratorConfiguration();
//		configGen.setGeometry(configGenGeo);
//		configGen.setId(1);
//		configGen.setName("The Generator");
//		configGen.setOrigin(0);
//		configGen.setType(GeneratorType.Distribution);
//		//und viel mehr...
//		
////		ScenarioConfiguration configScenario = new ScenarioConfiguration();
////		GraphScenarioConfiguration configGraph = new GraphScenarioConfiguration();
//		
//		
//		
//		Scenario rockavaria = new Scenario("Rockavaria");
//		rockavaria.setAreas(areaList);
//		rockavaria.setId(1);
//		//Question: What is a graph?
//		//rockavaria.setGraph(graph); 
//		rockavaria.setName("Rockavaria");
//		
//		
//		
//		CellIndex cellIndex = LatticeTheoryFactory.createCellIndex(10, 10);
//		CellIndex thePedestrianIndex = LatticeTheoryFactory.createCellIndex(1, 1);
//		
//		lattice.occupyCell(cellIndex, Occupation.Fixed);
//		lattice.occupyCell(thePedestrianIndex, Occupation.Dynamic);
//		
//		//initialize cells with density [0-5]
//		lattice.setAllDensityCells(0);
//		lattice.setDensityValue(thePedestrianIndex, 5.0);
//		
//		//find velocity field using fundamental relation
//		
//		//find abs(grad(T)) using eikonal relation
//		
//		
//		lattice.setAllEikonalCells(2.543545);
//		lattice.setEikonalValue(LatticeTheoryFactory.createCellIndex(5, 5), 9.234324);
//		
////		for (int i = 0; i<lattice.getNumberOfRows();i++) {
////			for (int j = 0; j<lattice.getNumberOfColumns();j++) {
////				lattice.setVelocityValue(LatticeTheoryFactory.createCellIndex(i, j), 
////						FastMath.sqrt((i-10)*(i-10) + (j-10)*(j-10))/(FastMath.sqrt(2)*10));
////			}
////		}
//		
//		lattice.setAllVelocityCells(1.0);
//		
//		lattice.paintVelocityLattice();		
//		
//
//		
//		EikonalCalculator theCalculator = new EikonalCalculator();
//		
//		CellIndex origin = LatticeTheoryFactory.createCellIndex(0, 0);
//		CellIndex destination = LatticeTheoryFactory.createCellIndex(15,15);
//		theCalculator.nextStep(lattice,origin,destination);
//		
//		ArrayList<CellIndex> thePath = (ArrayList<CellIndex>) theCalculator.shortestPath(lattice,origin,destination);
//
//		lattice.paintLattice();
//		lattice.paintTLattice();
//		lattice.paintSomething(thePath,"-");
//		
//		System.out.print("Next step: ("+thePath.get(thePath.size()-2).getRow()+","+thePath.get(thePath.size()-2).getColumn()+")");
//		
//		lattice.setAllVelocityCells(1.0);
//		lattice.setAllDensityCells(0.0);
//			
//		//Algorithms from Wikipedia - Djikstra algorithm
	}
}
