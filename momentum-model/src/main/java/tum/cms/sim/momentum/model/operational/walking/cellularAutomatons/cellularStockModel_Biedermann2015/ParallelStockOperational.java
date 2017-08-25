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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.stream.Collectors;

import tum.cms.sim.momentum.data.agent.pedestrian.state.tactical.TacticalState.Motoric;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.operational.walking.cellularAutomatons.CellularAutomatonsUtility;
import tum.cms.sim.momentum.utility.lattice.CellIndex;
import tum.cms.sim.momentum.utility.lattice.Lattice.Occupation;

/***
 * A Parallel implementation of the StockOperational pedestrian simulation model. This model
 * should be used with the unsynchronized lattice to allow parallel movement of pedestrian.
 * 
 * <lattice id="0" scenarioId="0" ...  behaviorType="Unsynchronized"/>
 * 
 * @author Sven Lauterbach (sven.lauterbach@tum.de)
 *
 */
public class ParallelStockOperational extends StockOperational {
	
	private ForkJoinPool forkjoinPool;
	private int threads = 4;

	@Override
	public void callPreProcessing(SimulationState simulationState) {
		super.callPreProcessing(simulationState);

		Optional<Integer> threads = Optional.ofNullable(this.properties.getIntegerProperty("threads"));
		
		if(threads.isPresent()) {
			
            this.threads = threads.get();
        }
		else {
			
			this.threads = simulationState.getNumberOfThreads();
		}

		/*
		 * The common forkjoin pool uses a thread count of "number of processors" - 1. If the user 
		 * configured a different thread number we use our own thread pool.
		 */
		if(ForkJoinPool.commonPool().getParallelism() == this.threads) {
			forkjoinPool = ForkJoinPool.commonPool();
		} else {
			forkjoinPool = new ForkJoinPool(this.threads);
		}
	}

	private void movePedestrian(IRichPedestrian ped) {

		if(ped.getMotoricTask() == Motoric.Standing) {
			CellIndex desiredCell = this.lattice.getCellIndexFromPosition(ped.getPosition());
			lattice.occupyCell(desiredCell, Occupation.Dynamic);
		}

		StockPedestrianExtension pedestrianExtension = (StockPedestrianExtension)ped.getExtensionState(this);
		CellIndex desiredCell = pedestrianExtension.getDesiredCell();

		movePedestrianOnLattice(desiredCell, ped);
	}	

	/**
	 * Return the "Canton Pairing" Number for the desired Cell of the pedestrian,
	 * see https://en.wikipedia.org/wiki/Pairing_function
	 * 
	 * @param p The pedestrian to get the "Canton Pairing" Number for
	 * @return The Canton Pairing number of the desired Cell of the pedestrian.
	 */
	private Long getCantorNumber(IRichPedestrian p) {
		return (long)((StockPedestrianExtension) p.getExtensionState(this)).getDesiredCell().hashCode();
	}

	@Override
	public void callAfterBehavior(SimulationState timeStepInformation, Collection<IRichPedestrian> pedestrians) {
		
		StockComparator stockComparator = new StockComparator(this);		
		
		/*
		 * In the previous "callPedestrianBehavior" phase we calculate the desired cell of each pedestrian.
		 * To update all pedestrian occupied cells in parallel, we need to group all pedestrians by their
		 * desired cell. If two pedestrians have the same desired cell we have a conflict and those two 
		 * pedestrians should be moved sequential. In an optimal case every pedestrian has a unique
		 * desired cell which allow us to moved them in parallel.
		 */
		
		/*
		 * we wrap the parallelStram() method in our own frokjoinPool so the method use this pool instead
		 * of the common fork join pool.
		 */
		//group all pedestrians by desired cell
		ForkJoinTask<Map<Long, List<IRichPedestrian>>> groupingTask = forkjoinPool.submit(() ->  
			pedestrians.parallelStream()
					.collect(Collectors.groupingByConcurrent(p -> getCantorNumber(p))));
		
		Map<Long, List<IRichPedestrian>> desiredCells = groupingTask.join();

		//move all pedestrians in parallel
		ForkJoinTask<?> movingTask = forkjoinPool.submit(() -> {
			desiredCells.values().parallelStream().forEach(list -> {
	
				//only one pedestrian want to move to this cell...
				if (list.size() == 1) {
					IRichPedestrian pedestrian = list.get(0);
					movePedestrian(pedestrian);
	
				} else {
					/*
					 * there are multiple pedestrians who want to move to the same cell, hence we
					 * have to sort them by their stock value and move the pedestrian with the highest 
					 * stock value. All other pedestrians in the list are moved to there current cell - so
					 * they keep their positions. This behavior is copied from the original model.
					 */
					list.sort(stockComparator);
	
					IRichPedestrian pedestrian = list.get(0);
					movePedestrian(pedestrian);
	
					List<IRichPedestrian> remaining = list.subList(1, list.size());
					for (IRichPedestrian ped : remaining) {
						CellIndex desiredCell = this.lattice
									.getCellIndexFromPosition(ped.getPosition());
	
						movePedestrianOnLattice(desiredCell, ped);
					}
				}
			});
		});
		
		movingTask.join();

		CellularAutomatonsUtility.freeDynamicCells(pedestrians, lattice);
	}

	@Override
	public void callPostProcessing(SimulationState timeStepInformation) {
		super.callPostProcessing(timeStepInformation);

		forkjoinPool.shutdown();
	}
}
