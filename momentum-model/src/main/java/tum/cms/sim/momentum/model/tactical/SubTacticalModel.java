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

package tum.cms.sim.momentum.model.tactical;

import java.util.Collection;

import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;
import tum.cms.sim.momentum.data.agent.pedestrian.types.ITacticalPedestrian;
import tum.cms.sim.momentum.data.layout.ScenarioManager;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.infrastructure.execute.callable.Callable;
import tum.cms.sim.momentum.model.IPedestrianBehavioralModel;
import tum.cms.sim.momentum.model.perceptional.PerceptionalModel;
import tum.cms.sim.momentum.utility.generic.IHasProperties;
import tum.cms.sim.momentum.utility.generic.PropertyBackPack;

public abstract class SubTacticalModel extends Callable
	implements IPedestrianBehavioralModel, IHasProperties {

	protected PerceptionalModel perception = null;

	public PerceptionalModel getPerception() {
		return perception;
	}

	public void setPerceptionalModel(PerceptionalModel perception) {
		this.perception = perception;
	}	
	
	protected ScenarioManager scenarioManager = null;

	public void setScenario(ScenarioManager scenario) {
		this.scenarioManager = scenario;
	}

	protected PropertyBackPack properties = null;
	
	@Override
	public PropertyBackPack getPropertyBackPack() {
		return properties;
	}
	
	@Override
	public void setPropertyBackPack(PropertyBackPack propertyContainer) {

		this.properties = propertyContainer; 
	}
	
	public abstract void callBeforeBehavior(SimulationState simulationState, Collection<IRichPedestrian> pedestrians);
	
	public abstract void callAfterBehavior(SimulationState simulationState, Collection<IRichPedestrian> pedestrians);
	
	public abstract void callPedestrianBehavior(ITacticalPedestrian pedestrian, SimulationState simulationState);
}
