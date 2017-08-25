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

package tum.cms.sim.momentum.simulator;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeoutException;

import tum.cms.sim.momentum.configuration.ConfigurationManager;
import tum.cms.sim.momentum.data.agent.car.CarManager;
import tum.cms.sim.momentum.data.agent.pedestrian.PedestrianManager;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;
import tum.cms.sim.momentum.data.layout.ScenarioManager;
import tum.cms.sim.momentum.infrastructure.argument.ArgumentManager;
import tum.cms.sim.momentum.infrastructure.exception.BadConfigurationException;
import tum.cms.sim.momentum.infrastructure.exception.InvalidSimulationArgument;
import tum.cms.sim.momentum.infrastructure.execute.CallController;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.infrastructure.execute.threading.ThreadingManager;
import tum.cms.sim.momentum.infrastructure.logging.LoggingManager;
import tum.cms.sim.momentum.infrastructure.loop.LoopManager;
import tum.cms.sim.momentum.infrastructure.network.NetworkManager;
import tum.cms.sim.momentum.infrastructure.time.TimeManager;
import tum.cms.sim.momentum.simulator.component.ComponentManager;
import tum.cms.sim.momentum.simulator.component.PedestrianSimulationSetup;

/**
 * The core entry point of the framework.
 * Corresponds to the simulation layer.
 * 
 * Here, all manager interact to run simulations.
 * 
 * @author Peter Kielar
 */
public class SimulatorManager {

	
	private TimeManager timeManager = null;	
	private ArgumentManager argumentContainer = null;	
	private NetworkManager networkManager = null;

	private ConfigurationManager configurationManager = null;	
	private LoopManager loopManager = null;
	
	private CallController<IRichPedestrian> callController = null;
	private PedestrianSimulationSetup setupPedestrianSimulation = null;
	
	private ScenarioManager scenarioManager = null;	
	private PedestrianManager pedestrianManager = null;	
	private CarManager carManager = null;
	
	private ComponentManager componentManager = null;	
	
	/**
	 * Call the loop manager to check if a simulation should be re-run.
	 * Needs a <loop> tag in the simulation configuration file
	 * @return false, if the simulation should be re-run.
	 */
	public boolean doSimulationLooping() {

		boolean rerunLooping = false;
		
		if(loopManager != null) {

			rerunLooping = !loopManager.stopLooping();
		}
		
		if(rerunLooping) {
			
			LoggingManager.logUser(SimulatorStrings.LogReRun);
		}
	
		return rerunLooping;
	}
	
	/**
	 * Setting the language to default English.
	 * However, the output (e.g. print numbers) style is still based on the language of the
	 * operating system.
	 */
	public void languageSetup() {
		
		Locale.setDefault(Locale.forLanguageTag(Locale.ENGLISH.getLanguage()));
	}
	
	/** 
	 * Call the argument manager to parse the command-line arguments. 
	 * @param args, command-line arguments
	 * @throws InvalidSimulationArgument, --config is the only argument of momentum
	 */
	public void parseArguments(String[] args) throws InvalidSimulationArgument {
		
		argumentContainer = new ArgumentManager(args);
		argumentContainer.parseArguments();
	}
	
	/**
	 * Call the configuration manager to parse the xml configuration.
	 * If a loop tag exists, the loop manager will handle the replaceable $VAR$ elements
	 * of the configuration.
	 * @throws IOException, File not found and similar because of file access
	 */
	public void configurate() throws IOException {

		if(configurationManager == null) {
			
			configurationManager = new ConfigurationManager();
			configurationManager.deserializeCompleteConfiguration(argumentContainer.getConfigFileName());
		}

		if(configurationManager.getSimulatorConfiguration().getLoop() != null) {
		
			if(loopManager == null) {
				
				loopManager = new LoopManager(configurationManager.getSimulatorConfiguration().getLoop());
			}

			configurationManager.deserializeUpdateConfiguration(argumentContainer.getConfigFileName(),
					loopManager.getLoopSubtitutes(),
					loopManager.getLoopVariableUpdates());
			
			loopManager.updateLoopVariables();
		}
	}
	
	/**
	 * Creates all managers and links them.
	 * 
	 * @throws BadConfigurationException
	 */
	public void createManagers() throws BadConfigurationException {
	
		LoggingManager.setupLoggingManager(configurationManager.getSimulatorConfiguration().getLogging());
		componentManager = new ComponentManager();
		scenarioManager = new ScenarioManager();
		timeManager = new TimeManager(configurationManager.getSimulatorConfiguration().getTimeState());
		networkManager = new NetworkManager(configurationManager.getSimulatorConfiguration().getNetwork());
		
		componentManager.setNetworkManager(networkManager);
		componentManager.setScenarioManager(scenarioManager);
		componentManager.setTimeManager(timeManager);
		componentManager.setConfigurationManager(configurationManager);
	}
	
	/**
	 * Call pedestrian related manager methods.
	 * If in the future other simulations should be done with the framework
	 * keep the manager and setup methods for each agent type separated. 
	 * 
	 * @throws Exception
	 */
	public void createPedestrianSimulation() throws Exception  {
		
		pedestrianManager = new PedestrianManager();
		carManager = new CarManager();
		componentManager.setPedestrianManager(pedestrianManager);
		componentManager.setCarManager(carManager);
		
		// Create a pedestrian only threading manager
		// Thus, IRichPedestrian are the "working objects"
		ThreadingManager<IRichPedestrian, SimulationState> threadingManager = new ThreadingManager<>(
				configurationManager.getSimulatorConfiguration().getThreadingState());
		
		threadingManager.setTaskSplitter(pedestrianManager);	
		
		callController = new CallController<IRichPedestrian>();
		callController.setThreadingManager(threadingManager);
		callController.setTaskSplitter(pedestrianManager);
		callController.setTimeManager(timeManager);
		callController.setNetworkManager(networkManager);
	
		// Layout First
		scenarioManager.createLayouts(configurationManager.getSimulatorConfiguration());
		setupPedestrianSimulation = new PedestrianSimulationSetup();
		
		setupPedestrianSimulation.setupComponents(componentManager);	
		setupPedestrianSimulation.setupProcessing(callController, componentManager);
	}
	
	/**
	 * Runs the pre-processing methods of all models that implement IPrePostProcessing
	 */
	public void preProcessing() {
			
		LoggingManager.logUser(SimulatorStrings.LogPreProcessing);
		
		networkManager.initializeNetwork();
		timeManager.startGenericTimeMeasurment();
		
		callController.callPreProcessing(setupPedestrianSimulation.setupPreProcessing(componentManager));
		
		timeManager.updatePreProcessingTimeMeasurment(timeManager.stopGenericTimeMeasurment());
		networkManager.startSimulationServer();
	}

	/**
	 * Execution simulation main body
	 * 
	 * @throws TimeoutException
	 */
	public void runProcessing() throws TimeoutException {
		
		if(callController.isClassical()) { // Basic strategic, tactical and operational approach
			
			LoggingManager.logUser(SimulatorStrings.LogExecutingClassical);
			this.runSimulationClassical();
		}
		else { // Complex model execution based on exeuctionOrder configurations
			
			LoggingManager.logUser(SimulatorStrings.LogExecutingCustom);
			this.runSimulationComplex();
		}
	}
	
	/**
	 * This method will only be used if an executionOrder XML tag is present.
	 * Hence if a non-standard simulation was designed.
	 * 
	 * @throws TimeoutException
	 */
	public void runSimulationComplex() throws TimeoutException {
		
		timeManager.startSimulationTimeMeasurment();
		
		// starts at step 0, if end is step n, in sum n+1 steps are taken
		while(!timeManager.isFinished()) { 
			
			// Remove simulation objects
			callController.callGenericCallable(componentManager.getAbsorbers());
		
			// Add simulation objects
			callController.callGenericCallable(componentManager.getGenerators());
			
			// Execute blocks and underlying meta models and models in corresponding order if allowed
			callController.callBlockExecution(timeManager);

			// Run time-based analysis
			callController.callGenericCallable(componentManager.getAnalysisModels());
			
			// Save simulation output
			callController.callGenericCallable(componentManager.getOutputWriters());
			
			// Handle network communiation 
			networkManager.handleNetwork(timeManager);
			
			// Start the next step, in the final loop this will be the endTime + 1 step, which will not be executed
			timeManager.nextTimeStep();
		}
	}
	
	/**
	 * Runs typical simulation based on the three behavior layers
	 * strategic, tactical and operational
	 * 
	 * @throws TimeoutException
	 */
	public void runSimulationClassical() throws TimeoutException {
		
		timeManager.startSimulationTimeMeasurment();
	
		// starts at step 0, if end is step n, in sum n+1 steps are taken
		while(!timeManager.isFinished()) { 
			
			// Remove simulation objects
			callController.callGenericCallable(componentManager.getAbsorbers());
		
			// Add simulation objects
			callController.callGenericCallable(componentManager.getGenerators());
			
			// Act 
			callController.callExecutionCallable(componentManager.getStrategicalModels(), pedestrianManager);
			callController.callExecutionCallable(componentManager.getTacticalModels(), pedestrianManager);
			callController.callExecutionCallable(componentManager.getOperationalModels(), pedestrianManager);

			// Run time-based analysis
			callController.callGenericCallable(componentManager.getAnalysisModels());
			
			// Save simulation output
			callController.callGenericCallable(componentManager.getOutputWriters());
			
			// Handle network communication 
			networkManager.handleNetwork(timeManager);
			
			// Start the next step, in the final loop this will be the endTime + 1 step, which will not be executed
			timeManager.nextTimeStep();
			
			// Fail due to runtime-exception
			if(callController.hasFailed()) {
				
				break;
			}
		}
		
		timeManager.updateSimulationExecutionTime();
	}
	
	/**
	 * Runs the post-processing methods of all models that implement IPrePostProcessing
	 */
	public void postProcessing() throws TimeoutException {
	              
		timeManager.startGenericTimeMeasurment();
		
		callController.callPostProcessing(setupPedestrianSimulation.setupPostProcessing(componentManager));
		
		timeManager.updatePostProcessingTimeMeasurment(timeManager.stopGenericTimeMeasurment());
		
		networkManager.endNetwork();		
		scenarioManager.clear();
		callController.cleanUp();
		
		LoggingManager.logUser(SimulatorStrings.LogPostProcessing);
	}
}
