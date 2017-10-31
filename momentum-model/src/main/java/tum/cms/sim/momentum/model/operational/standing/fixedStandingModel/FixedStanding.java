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

package tum.cms.sim.momentum.model.operational.standing.fixedStandingModel;

import java.util.Collection;

import tum.cms.sim.momentum.data.agent.pedestrian.state.operational.StandingState;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IOperationalPedestrian;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrianExtansion;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.operational.standing.StandingModel;
import tum.cms.sim.momentum.utility.geometry.Vector2D;
import tum.cms.sim.momentum.utility.lattice.CellIndex;
import tum.cms.sim.momentum.utility.lattice.ILattice;
import tum.cms.sim.momentum.utility.lattice.Lattice.Occupation;

public class FixedStanding extends StandingModel {

	private static String latticeIdName = "latticeId";
	private static String isCAName = "isCA";
	private boolean isCA = false;
	private int latticeId = 0;
	
	@Override
	public IPedestrianExtansion onPedestrianGeneration(IRichPedestrian pedestrian){
		
		return null; // Nothing to do
	}

	@Override
	public void onPedestrianRemoval(IRichPedestrian pedestrian) {
		
		// Nothing to do
	}

	@Override
	public void callBeforeBehavior(SimulationState simulationState, Collection<IRichPedestrian> pedestrians) {
		
		// Nothing to do
	}

	@Override
	public void callAfterBehavior(SimulationState simulationState, Collection<IRichPedestrian> pedestrians) {
		
		// Nothing to do
	}

	@Override
	public void callPedestrianBehavior(IOperationalPedestrian pedestrian, SimulationState simulationState) {
		
		if(pedestrian.getStandingState() == null) {
			
			Vector2D heading = pedestrian.getNextHeading();
			Vector2D position = pedestrian.getNextWalkingTarget();
			
			// workaround, this fixed standing model solves also CA model behavior
				
			if(this.scenarioManager.getLattices().size() > 0 && isCA) {
				
				ILattice caLattice = this.scenarioManager.getLattice(latticeId);

				CellIndex newStandingPosition = caLattice.getCellIndexFromPosition(position);
				position = caLattice.getCenterPosition(newStandingPosition);
				
				if(!caLattice.occupyCellIfFree(newStandingPosition, Occupation.Dynamic)) {
					
					position = pedestrian.getPosition();
					caLattice.occupyCell(caLattice.getCellIndexFromPosition(pedestrian.getPosition()), Occupation.Dynamic);
				}
			}
			
			StandingState standingState = new StandingState(position, heading);
	
			pedestrian.setStandingState(standingState);
		}
	}

	@Override
	public void callPreProcessing(SimulationState simulationState) {
	
		if(this.properties.getBooleanProperty(isCAName) != null) {
			
			isCA = this.properties.getBooleanProperty(isCAName);
			
			if(this.properties.getIntegerProperty(latticeIdName) != null) {
				
				latticeId = this.properties.getIntegerProperty(latticeIdName); 
			}
		}
	}

	@Override
	public void callPostProcessing(SimulationState simulationState) {

		// Nothing to do
	}
}
