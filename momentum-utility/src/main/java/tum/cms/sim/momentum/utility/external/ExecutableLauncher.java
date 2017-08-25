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

package tum.cms.sim.momentum.utility.external;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

import tum.cms.sim.momentum.utility.generic.PropertyBackPack;

public class ExecutableLauncher  {
	
	private final static String parametersName = "parameter"; 
	private final static String engineName = "engine"; 
	private final static String runDirectoryName = "runDirectory"; 
	
	protected Boolean isStarted = false;
	protected Process runningProcess = null;
	protected PropertyBackPack properties = null;
	
	
	ExecutableLauncher(PropertyBackPack executablepropertyContainer) {
			
		this.properties = executablepropertyContainer;
	}
	
	public String runSequenical() throws IOException, InterruptedException {
		
		ArrayList<String> parameters = this.properties.<String>getListProperty(parametersName);
		String[] command = new String[parameters.size() + 1];
		
		command[0] =  properties.getStringProperty(engineName);
		
		for(int iter = 0; iter < parameters.size(); iter++) {
			
			command[iter + 1] = parameters.get(iter);
		}

		ProcessBuilder builder = new ProcessBuilder(command);
		builder.directory(new File(properties.getStringProperty(runDirectoryName)));
		builder.redirectErrorStream(true);
		runningProcess = builder.start();
		
		String line;
		Reader re = new InputStreamReader(runningProcess.getInputStream());
		BufferedReader in = new BufferedReader(re);
		StringBuilder resultReader = new StringBuilder();
		
		while(runningProcess.isAlive()) {
			
			while((line = in.readLine()) != null) {
				resultReader.append(line);
			}
			
			Thread.sleep(100);
		}
	
		in.close();
		
		return resultReader.toString();
	}
}
