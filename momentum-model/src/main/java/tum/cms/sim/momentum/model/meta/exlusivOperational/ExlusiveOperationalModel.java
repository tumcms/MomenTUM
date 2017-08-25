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

package tum.cms.sim.momentum.model.meta.exlusivOperational;

import java.util.Collection;
import java.util.List;

import tum.cms.sim.momentum.data.agent.pedestrian.state.other.MetaState;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrianExtansion;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.meta.MetaModel;

public class ExlusiveOperationalModel extends MetaModel {

	private final static String randomForbiddenOperationalsName ="randomForbiddenOperationals";
	private List<Integer> operationalModelNames = null;
	
	@Override
	public boolean isMultiThreading() {

		return false;
	}
	
	@Override
	public IPedestrianExtansion onPedestrianGeneration(IRichPedestrian pedestrian) {
		
		ExlusiveOperationalPedestrianExtension extension = new ExlusiveOperationalPedestrianExtension(
				this.operationalModelNames);
		
		pedestrian.setMetaState(new MetaState(null, extension.getNotAllowedOperationalModels(), null));
		
		return extension;
	}

	@Override
	public void onPedestrianRemoval(IRichPedestrian pedestrian) {
		
		// nothing to do
	}

	@Override
	public void executeBeforeExecute(SimulationState simulationState, Collection<IRichPedestrian> pedestrians) {
		
		// nothing to do
	}

	@Override
	public void executeAfterExecute(SimulationState simulationState, Collection<IRichPedestrian> pedestrians) {
		
		// nothing to do
	}

	@Override
	public void execute(Collection<? extends IRichPedestrian> splittTask, SimulationState simulationState) {

		// nothing to do
	}

	@Override
	public void callPreProcessing(SimulationState simulationState) {
		
		this.operationalModelNames = this.properties.<Integer>getListProperty(randomForbiddenOperationalsName);
	}

	@Override
	public void callPostProcessing(SimulationState simulationState) {
		
		// nothing to do
	}

}
