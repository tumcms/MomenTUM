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

import tum.cms.sim.momentum.configuration.model.lattice.LatticeModelConfiguration.BehaviorType;
import tum.cms.sim.momentum.configuration.model.lattice.LatticeModelConfiguration.LatticeType;
import tum.cms.sim.momentum.configuration.model.lattice.LatticeModelConfiguration.NeighbourhoodType;

public class LatticeTheoryFactory {
	
	private static int latticeSeed = -1;

	public static synchronized int getNewLatticeId() {
			
		int result = ++LatticeTheoryFactory.latticeSeed;
		
		return result;
	}
	
	private LatticeTheoryFactory() { }
	
	public static ILattice createLattice(String name,
			LatticeType latticeType,
			NeighbourhoodType neighborhoodType,
			double cellEdgeSize,
			double maxX,
			double minX,
			double maxY,
			double minY) {
		
		SynchronizedLattice lattice = new SynchronizedLattice(latticeType, neighborhoodType, cellEdgeSize, maxX, minX, maxY, minY);
		lattice.setName(name);
		lattice.setId(LatticeTheoryFactory.getNewLatticeId());
		
		return lattice;
	}
	
	public static ILattice createLattice(String name,
			BehaviorType behaviorType,
			LatticeType latticeType,
			NeighbourhoodType neighborhoodType,
			double cellEdgeSize,
			double maxX,
			double minX,
			double maxY,
			double minY) {
		
		ILattice result = null;
		
		/* 
		 * The default behavior should be synchronized, hence if the user does not specify a lattice behavior type 
		 * (behaviorType == null) in the configuration we use the synchronized version.
		 */
		if(behaviorType == BehaviorType.Synchronized || behaviorType == null) {
			
			result = new SynchronizedLattice(latticeType, neighborhoodType, cellEdgeSize, maxX, minX, maxY, minY);
			
		}
		else {
			
			result = new Lattice(latticeType, neighborhoodType, cellEdgeSize, maxX, minX, maxY, minY);
		}

		result.setName(name);
		result.setId(LatticeTheoryFactory.getNewLatticeId());
		
		return result;
	}
	
	public static ILattice copyLattice(ILattice original, String name) {
		
		SynchronizedLattice lattice = new SynchronizedLattice(original.getType(),
				original.getNeighborhoodType(),
				original.getCellEdgeSize(),
				original.getMaxPositionBoundingBox().getXComponent(),
				original.getMinPositionBoundingBox().getXComponent(),
				original.getMaxPositionBoundingBox().getYComponent(),
				original.getMinPositionBoundingBox().getYComponent());
		
		lattice.setName(name);
		lattice.setId(LatticeTheoryFactory.getNewLatticeId());
		
		return lattice;
	}
	
	public static CellIndex createCellIndex(int[] rowColumnIndex) {
		
		return new CellIndex(rowColumnIndex);
	}
	
	public static CellIndex createCellIndex(int rowIndex, int columnIndex) {
		
		return new CellIndex(rowIndex, columnIndex);
	}
}
