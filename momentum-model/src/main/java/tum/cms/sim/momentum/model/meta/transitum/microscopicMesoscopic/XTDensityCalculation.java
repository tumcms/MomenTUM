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

package tum.cms.sim.momentum.model.meta.transitum.microscopicMesoscopic;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;
import tum.cms.sim.momentum.utility.lattice.CellIndex;
import tum.cms.sim.momentum.utility.lattice.ILattice;
import tum.cms.sim.momentum.utility.lattice.LatticeTheoryFactory;

public class XTDensityCalculation {
	
	private ArrayList<Pair<CellIndex, Long>> pedestriansInCurrentTimeRange = null; //time-depending pedestrian list <PedestrianPosition, timeStep>
	private Long updateTimeStepsRange = null;
	private ILattice measureLattice = null;
	private Long lastUpdatedTimeStep = null;

	public XTDensityCalculation(Long updateTimeStepsRange, ILattice scenarioLattice) {
		
		this.updateTimeStepsRange = updateTimeStepsRange;
		
		measureLattice = LatticeTheoryFactory.copyLattice(scenarioLattice, "XTDensityTransiTUM");
		lastUpdatedTimeStep = (long) 0;
		
		pedestriansInCurrentTimeRange = new ArrayList<Pair<CellIndex, Long>>();
	}

	public void updateDensityList(Collection<? extends IRichPedestrian> pedestrians, Long currentTimeStep) {
		
		//one update per timeStep
		if (lastUpdatedTimeStep.equals(currentTimeStep)) {
			
			return;
		}
		//remove all pedestrian densities older than (currentTimeStep - timeStepRange)
		pedestriansInCurrentTimeRange = pedestriansInCurrentTimeRange.stream().filter(arg -> arg.getRight() > currentTimeStep - updateTimeStepsRange)
				.collect(Collectors.toCollection(ArrayList::new));
		
		//add pedestrians from this timeStep to the collection
		pedestrians.forEach(ped -> pedestriansInCurrentTimeRange.add(new ImmutablePair<CellIndex, Long>(measureLattice.getCellIndexFromPosition(ped.getPosition()), currentTimeStep)));
		
		lastUpdatedTimeStep = currentTimeStep;
	}
	
	public HashMap<CellIndex, Double> getXTDensity() {
		
		Iterator<Pair<CellIndex, Long>> densityIterator =  pedestriansInCurrentTimeRange.iterator();
		HashMap<CellIndex, Double> densityMap = new HashMap<CellIndex, Double>();
		
		while (densityIterator.hasNext()) {
			
			CellIndex densityPointCellIndex = densityIterator.next().getLeft();
			
			if (densityMap.containsKey(densityPointCellIndex)) {
				
				densityMap.put(densityPointCellIndex, (Double) (densityMap.get(densityPointCellIndex) + 1/(updateTimeStepsRange * measureLattice.getCellArea())));		
			}
			else {
				densityMap.put(densityPointCellIndex, (Double) (1/(updateTimeStepsRange * measureLattice.getCellArea())));
			}
		}		
		return densityMap;	
	}


}
