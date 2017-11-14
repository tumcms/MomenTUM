package tum.cms.sim.momentum.utility.lattice;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Test;

import tum.cms.sim.momentum.configuration.model.lattice.LatticeModelConfiguration.LatticeType;
import tum.cms.sim.momentum.configuration.model.lattice.LatticeModelConfiguration.NeighborhoodType;
import tum.cms.sim.momentum.utility.geometry.GeometryFactory;
import tum.cms.sim.momentum.utility.geometry.Vector2D;
import tum.cms.sim.momentum.utility.lattice.Lattice.Occupation;

public class LatticeTest {

	@Test
	public void getAllOnCircleBorder_BasicParameter_WithSuccess() throws Exception {
		
		// Arrange		
		ILattice lattice = LatticeTheoryFactory.createLattice("testLattice",
				LatticeType.Quadratic,
				NeighborhoodType.Touching,
				1,
				10, // 11 cells
				0,  
				10, // 11 cells
				0);
		
		double radius = 5; // max radius on border cells 0,10
		Vector2D center = GeometryFactory.createVector(5, 5); // center
		
		// Act
		List<CellIndex> inOrderCells = lattice.getAllOnCircleBorder(radius, center);
		
		// Assert
		// check only first octant and the starting cells
		// resons, the computation is mirror into all octants -> same
		ArrayList<CellIndex> expectedResult = new ArrayList<>();
		//oct1
		expectedResult.add(LatticeTheoryFactory.createCellIndex(5,10));
		expectedResult.add(LatticeTheoryFactory.createCellIndex(6,10));
		expectedResult.add(LatticeTheoryFactory.createCellIndex(7,10));
		expectedResult.add(LatticeTheoryFactory.createCellIndex(8,9));
		expectedResult.add(LatticeTheoryFactory.createCellIndex(9,8));	
		expectedResult.add(LatticeTheoryFactory.createCellIndex(10,7));
		expectedResult.add(LatticeTheoryFactory.createCellIndex(10,6));
		//oct2
		expectedResult.add(LatticeTheoryFactory.createCellIndex(10,5));
		//oct4
		expectedResult.add(LatticeTheoryFactory.createCellIndex(5,0));
		//oct6
		expectedResult.add(LatticeTheoryFactory.createCellIndex(0,5));
		//oct8
		
		assertTrue(CollectionUtils.containsAll(inOrderCells, expectedResult));
		
		// Visual check if necessary
		lattice.occupyCells(inOrderCells, Occupation.Fixed);
		lattice.paintLattice();
		
	}
}
