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

public class SimulatorStrings {

	/**
	 * The compile version is
	 * Major change dot feature dot fix
	 * 
	 * A major change is a fundamental change on of the frameworks mechanisms,
	 * e.g. adding macroscopic support or multi-model simulations
	 * 
	 * A feature is a fundamental change on of the simulation aspects,
	 * e.g. adding another model or adding multi-story support
	 * 
	 * A fix addresses changes in models that create configuration changes,
	 * e.g. adding another model parameter 
	 */
	protected final static String CompileVersion = "2.0.2";
	
	protected final static String StartupTestResource = "licence.txt";
	protected static final String LogResource = "momentum-log4j.properties"; 
	
	protected final static String LogConfigurateFramework = "Configuring Framework";
	protected final static String LogConfigurateSimulation = "Configuring Simulation";
	
	protected final static String LogExecuteSimulation = "Begin Simulation";
	protected final static String LogExecutingCustom = "Executing Simulation Custom";
	protected final static String LogExecutingClassical = "Executing Simulation Classical";
	
	protected final static String LogPreProcessing = "Executing Pre-Processing";	
	protected final static String LogPostProcessing = "Executing Post-Processing";	
	
	protected final static String LogReRun = "Re-running Simulation";	
	protected final static String LogTerminate = "Terminating Simulation";	
}
