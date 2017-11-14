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

package tum.cms.sim.momentum.simulator.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tum.cms.sim.momentum.configuration.simulation.SimulatorConfiguration;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;
import tum.cms.sim.momentum.infrastructure.execute.CallController;
import tum.cms.sim.momentum.infrastructure.execute.callable.IExecutionCallable;
import tum.cms.sim.momentum.infrastructure.execute.callable.IPrePostProcessing;

/**
 * This class collects all objects to run a pedestrian simulations.
 * In future, if other agents are needed, this concept can be
 * made generic to provide a xml based path to configure simulations
 * that provide different agents (e.g. cars).
 * 
 * @author Peter M. Kielar
 *
 */
public class PedestrianSimulationSetup {

	public void setupComponents(ComponentManager componentManager) {
		
		SimulatorConfiguration configuration = componentManager.getConfigurationManager().getSimulatorConfiguration();
		
		componentManager.createGraphs(configuration.getLayouts(),
				configuration.getGraphs());	
		
		componentManager.createSpaceSyntaxes(configuration.getLayouts(),
				configuration.getSpaceSyntaxes());
		
		componentManager.createLattices(configuration.getLayouts(),
				configuration.getLattices());
		
		componentManager.createPedestrianSeeds(configuration.getPedestrianSeeds());
		
		// Generators after seeds
		componentManager.createGenerators(configuration.getGenerators());
		componentManager.createAbsorbers(configuration.getAbsorbers());
		
		// Perception Models
		componentManager.createPerceptualModels(configuration.getPerceptualModels());
		
		// Operational Models
		componentManager.createWalkingModels(configuration.getWalkingModels());
		componentManager.createStandingModels(configuration.getStandingModels());
		componentManager.createOperationalModels(configuration.getOperationalModels());
		
		// Tactical Models
		componentManager.createStayingModels(configuration.getStayingModels());
		componentManager.createQueuingModels(configuration.getQueuingModels());
		componentManager.createRoutingModels(configuration.getRoutingModels());
		componentManager.createSearchingModels(configuration.getSearchingModels());
		componentManager.createTacticalModels(configuration.getTacticalModels());
		
		// Strategical Models
		componentManager.createStrategicalModels(configuration.getStrategicalModels());
	
		// Meta models after behavioral models
		componentManager.createMetaModels(configuration.getMetaModels());
	
		// analysis models before output models
		componentManager.createAnalysisModels(configuration.getAnalysisModels());
		
		// output as last item
 		componentManager.createOutputWriter(configuration.getOutputWriters());	
	}

	public List<IPrePostProcessing> setupPreProcessing(ComponentManager componentManager) {

		ArrayList<IPrePostProcessing> preProcessingList = new ArrayList<>();
	
		preProcessingList.addAll(componentManager.getGraphModels());
		preProcessingList.addAll(componentManager.getLatticeModels());
		preProcessingList.addAll(componentManager.getSpaceSyntaxModels());
		
		preProcessingList.addAll(componentManager.getPerceptionalModels());
		
		preProcessingList.addAll(componentManager.getAbsorbers());
		preProcessingList.addAll(componentManager.getGenerators());

		preProcessingList.addAll(componentManager.getStrategicalModels());	
		preProcessingList.addAll(componentManager.getDestinationChoiceModels());	
		
		preProcessingList.addAll(componentManager.getTacticalModels());		
		preProcessingList.addAll(componentManager.getStrategicalModels());	
		preProcessingList.addAll(componentManager.getStayingModels());		
		preProcessingList.addAll(componentManager.getRoutingModels());
		preProcessingList.addAll(componentManager.getQueuingModels());
		
		preProcessingList.addAll(componentManager.getOperationalModels());
		preProcessingList.addAll(componentManager.getWalkingModels());
		preProcessingList.addAll(componentManager.getStandingModels());
		
		preProcessingList.addAll(componentManager.getMetaModels());
		
		preProcessingList.addAll(componentManager.getAnalysisModels());
		preProcessingList.addAll(componentManager.getOutputWriters());
		
		return preProcessingList;
	}
	
	public List<IPrePostProcessing> setupPostProcessing(ComponentManager componentManager) {
		
		ArrayList<IPrePostProcessing> postProcessingList = new ArrayList<>();

		postProcessingList.addAll( componentManager.getStrategicalModels());	
		postProcessingList.addAll(componentManager.getDestinationChoiceModels());	
		
		postProcessingList.addAll(componentManager.getTacticalModels());		
		postProcessingList.addAll(componentManager.getStrategicalModels());	
		postProcessingList.addAll(componentManager.getStayingModels());		
		postProcessingList.addAll(componentManager.getRoutingModels());
		
		postProcessingList.addAll(componentManager.getOperationalModels());
		postProcessingList.addAll(componentManager.getWalkingModels());
		postProcessingList.addAll(componentManager.getStandingModels());
		
		postProcessingList.addAll(componentManager.getMetaModels());
		
		postProcessingList.addAll(componentManager.getAbsorbers());
		postProcessingList.addAll(componentManager.getGenerators());

		postProcessingList.addAll(componentManager.getPerceptionalModels());

		postProcessingList.addAll(componentManager.getGraphModels());
		postProcessingList.addAll(componentManager.getLatticeModels());
		postProcessingList.addAll(componentManager.getSpaceSyntaxModels());
		
		postProcessingList.addAll(componentManager.getAnalysisModels());
		postProcessingList.addAll(componentManager.getOutputWriters());
		
		return postProcessingList;
	}
	
	/**
	 * Creates the execution order engine for pedestrian agent-based simulations.
	 * Make sure that all model ids are unique!
	 * 
	 * @param callController, comprises the execution order engine
	 * @param componentManager, to access to models
	 */
	public void setupProcessing(CallController<IRichPedestrian> callController, ComponentManager componentManager) {
		
		HashMap<Integer, IExecutionCallable<IRichPedestrian>> pedestrianExecutionModels = new HashMap<>();
		pedestrianExecutionModels.putAll(componentManager.getStrategicalModelMap());
		pedestrianExecutionModels.putAll(componentManager.getTacticalModelMap());
		pedestrianExecutionModels.putAll(componentManager.getOperationalModelMap());
		pedestrianExecutionModels.putAll(componentManager.getMetaModelMap());
		
		callController.createExecutionOrder(componentManager.getConfigurationManager().getSimulatorConfiguration().getExecutionOrder(),
				pedestrianExecutionModels,
				componentManager.getPedestrianManager());
	}
}
