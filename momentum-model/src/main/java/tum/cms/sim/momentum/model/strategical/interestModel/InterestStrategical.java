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

package tum.cms.sim.momentum.model.strategical.interestModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrianExtansion;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IStrategicPedestrian;
import tum.cms.sim.momentum.data.layout.area.Area;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.strategical.DestinationChoiceModel;

public class InterestStrategical extends DestinationChoiceModel {

	private ArrayList<Area> interestLocations = new ArrayList<Area>();
	private InterestConfiguration configuration = null;
	
	@Override
	public IPedestrianExtansion onPedestrianGeneration(IRichPedestrian pedestrian) {
	
		return new InterestExtension(this, 
				this.interestLocations, 
				this.configuration);
	}

	@Override
	public void onPedestrianRemoval(IRichPedestrian pedestrian) { /* nothing to do */ }

	@Override
	public void callPreProcessing(SimulationState simulationState) {
		
		this.interestLocations.addAll(this.scenarioManager.getDestinations());
		this.interestLocations.addAll(this.scenarioManager.getIntermediates());
		
		try {
			
			this.configuration = new InterestConfiguration();
			this.configuration.loadPedestrianNumbers(this.properties);
			this.configuration.loadDistributions(this.properties);
		}
		catch (IOException e) { // cannot load config

			e.printStackTrace();
		}
	}

	@Override
	public void callPostProcessing(SimulationState simulationState) { /* nothing to do */ }
	
	@Override
	public void callBeforeBehavior(SimulationState simulationState, Collection<IRichPedestrian> pedestrians) { /* nothing to do */ }
	
	@Override
	public void callPedestrianBehavior(IStrategicPedestrian pedestrian, SimulationState simulationState) {

		InterestExtension extension = (InterestExtension)pedestrian.getExtensionState(this);
		extension.updateInterests(simulationState, pedestrian);
	}
	
	@Override
	public void callAfterBehavior(SimulationState simulationState, Collection<IRichPedestrian> pedestrians) { /* nothing to do */ }
}
