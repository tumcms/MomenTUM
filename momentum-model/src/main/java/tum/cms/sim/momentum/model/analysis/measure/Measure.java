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

package tum.cms.sim.momentum.model.analysis.measure;

import java.util.ArrayList;
import java.util.HashMap;

import tum.cms.sim.momentum.configuration.model.output.WriterSourceConfiguration.OutputType;
import tum.cms.sim.momentum.data.analysis.AnalysisElementSet;
import tum.cms.sim.momentum.data.layout.ScenarioManager;
import tum.cms.sim.momentum.utility.generic.IHasProperties;
import tum.cms.sim.momentum.utility.generic.PropertyBackPack;
import tum.cms.sim.momentum.utility.generic.Unique;

public abstract class Measure extends Unique implements IHasProperties {

	protected PropertyBackPack properties = null;
	
	protected ArrayList<String> inputTypes = new ArrayList<String>();
	protected ArrayList<String> outputTypes = new ArrayList<String>();
	
	protected ScenarioManager scenarioManager = null;

	@Override
	public PropertyBackPack getPropertyBackPack() {
		return properties;
	}
	
	@Override
	public void setPropertyBackPack(PropertyBackPack propertyContainer) {

		this.properties = propertyContainer; 
	}

	public void setScenarioManager(ScenarioManager scenarioManager) {
		this.scenarioManager = scenarioManager;
	}
	
	public ArrayList<String> getInputTypes() {
		return inputTypes;
	}

	public ArrayList<String> getOutputTypes() {
		
		ArrayList<String> outputTypesReturn = new ArrayList<>(outputTypes);
		outputTypesReturn.add(0, OutputType.id.name());
		outputTypesReturn.add(0, OutputType.timeStep.name());
		
		return outputTypesReturn;
	}

	public abstract void initialize();
	
	public abstract void measure(long timeStep, HashMap<String, AnalysisElementSet> inputMap, 
			HashMap<String, AnalysisElementSet> outputMap);
	
}
