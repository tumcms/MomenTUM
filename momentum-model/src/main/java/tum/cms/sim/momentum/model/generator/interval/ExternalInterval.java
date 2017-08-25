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

package tum.cms.sim.momentum.model.generator.interval;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;

import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.infrastructure.logging.LoggingManager;
import tum.cms.sim.momentum.utility.external.ExecutableLauncher;
import tum.cms.sim.momentum.utility.external.ExternalFactory;
import tum.cms.sim.momentum.utility.generic.PropertyBackPack;

/**
 * The external interval provides a method to read data by running an
 * external command-line program. This can be used to couple the 
 * generator concept of the framework with e.g. bus arrival scheduler.
 * The code will only run the external program if the number of 
 * pedestrian to generate is zero (no pedestrians remaining). 
 * 
 * The synchronization is based on files. Before running the external program,
 * this class writes a file in which the current time of the simulations is stored.
 * Next the external program is executed. If the external program is finished,
 * this class reads the file in which the number of pedestrian is stored.
 * This class will remove this file after reading the number.
 * 
 * @author Peter M. Kielar
 *
 */
public class ExternalInterval extends GeneratorInterval {

	/**
	 * The launcher runs the external program,
	 */
	private ExecutableLauncher launcher = null;
	
	/**
	 * The exchange file defines from where to read the number of pedestrians.
	 * Furthermore, if exchange file does not exists, the simulation will halt!
	 */
	private File exchangeFile = null;
	
	/**
	 * The read number of pedestrians that have to be generated.
	 */
	private int toGenerate = 0;
	
	public ExternalInterval(double generatorStartTime, 
			double generatorEndTime, 
			int maximalPedestrians,
			PropertyBackPack executablePropertyBackPack,
			File exchangeFile) {
		
		super(generatorStartTime, generatorEndTime, maximalPedestrians);
		
		this.launcher = ExternalFactory.createExecutableLauncher(executablePropertyBackPack);
		this.exchangeFile = exchangeFile;
	}

	@Override
	public double allowPedestrianGeneration(SimulationState simulationState, 
			int generatedPedestrians,
			int maximalPedestrians) {
		
		int newToGenerate = 0;
		
		// if the number of generated is reached, run the external program for
		// new pedestrians
		if(this.toGenerate == generatedPedestrians) {
			
			try {
				
				String timeData = String.valueOf(simulationState.getCurrentTime());
	
				Files.write(exchangeFile.toPath(), timeData.getBytes(), StandardOpenOption.CREATE_NEW);
				String resultsMessage = launcher.runSequenical();
				
				LoggingManager.logDebug(resultsMessage);

				List<String> numberOfPedestrian = Files.readAllLines(exchangeFile.toPath());
				newToGenerate = Integer.parseInt(numberOfPedestrian.get(0));
				
				toGenerate += newToGenerate;
				exchangeFile.delete();
			}
			catch (Exception e) {
			
				e.printStackTrace();
			}
		}
		
		return newToGenerate;
	}
}
