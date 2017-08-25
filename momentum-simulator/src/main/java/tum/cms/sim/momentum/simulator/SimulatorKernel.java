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
import java.util.concurrent.TimeoutException;

import tum.cms.sim.momentum.infrastructure.exception.BadConfigurationException;
import tum.cms.sim.momentum.infrastructure.exception.InvalidSimulationArgument;
import tum.cms.sim.momentum.infrastructure.logging.LoggingManager;

/**
 * Starting point of MomenTUMv2.
 * The class corresponds to the framework layer.
 * 
 * @author Peter Kielar
 *
 */
public class SimulatorKernel {

	public static String version = "0.9.0";
	
	/**
	 * Entry point for command-line execution of a simulation.
	 * 
	 * Typical command line arguments are:
	 * --config "filePath.xml", full path to configuration xml
	 * 
	 * To run the framework from command line:
	 * java -jar MomenTUMv2.jar --config "C:\mypath\configuration.xml"
	 * 
	 * @param args, command-line arguments
	 */
	public static void main(String[] args) 
			throws InvalidSimulationArgument, 
				   TimeoutException, 
				   IOException,
				   Exception,
				   BadConfigurationException {
		
		// Initial logging
		LoggingManager.initialize(SimulatorKernel.class.getClassLoader().getResource(SimulatorStrings.LogResource));
	
		// Show initial text
		LoggingManager.printResourceText(SimulatorKernel.class.getClassLoader().getResource(SimulatorStrings.StartupTestResource));
		LoggingManager.logUser(SimulatorStrings.LogConfigurateFramework);
		
		// Core manager, corresponds to simulation layer. 
		SimulatorManager simulationManager = new SimulatorManager();
		
		// Set to English
		simulationManager.languageSetup();
		
		// Parse command line arguments
		simulationManager.parseArguments(args);
		
		do { // if configured, re-run the simulation
			
			LoggingManager.logUser(SimulatorStrings.LogConfigurateSimulation);
			// Parse configuration xml to plain objects,
			// The objects are the basis for creating simulation objects (models)
			simulationManager.configurate();	
			
			// Create service objects (managers)
			simulationManager.createManagers();
			
			// Create simulation objects (models)
			simulationManager.createPedestrianSimulation();
		
			LoggingManager.logUser(SimulatorStrings.LogExecuteSimulation);
			
			// Execute simulation
			simulationManager.preProcessing();
			simulationManager.runProcessing();
			simulationManager.postProcessing();
			
		} while (simulationManager.doSimulationLooping()); // Repeat simulation if true
		
		LoggingManager.logUser(SimulatorStrings.LogTerminate);
	}
}
