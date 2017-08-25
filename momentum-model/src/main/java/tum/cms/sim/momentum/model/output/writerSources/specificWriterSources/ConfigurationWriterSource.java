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

package tum.cms.sim.momentum.model.output.writerSources.specificWriterSources;

import tum.cms.sim.momentum.configuration.ConfigurationManager;
import tum.cms.sim.momentum.configuration.simulation.SimulatorConfiguration;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.infrastructure.logging.LoggingManager;
import tum.cms.sim.momentum.model.output.writerSources.genericWriterSources.SingleElementWriterSource;

/**
 * This class is the writer source for the simulation configuration.
 * It provides a xml tag and all children of that tag as data.
 * 
 * This class provides a dataElement property:
 * <property name="dataElement" type="String" value="layoutConfiguration"/>
 * This is the name of the single data element.
 * 
 * @author Peter M. Kielar
 *
 */
public class ConfigurationWriterSource extends SingleElementWriterSource {

	private static final String dataElementName = "dataElement";
	protected String dataElement = null;
	
	private ConfigurationManager configurationManager = null;

	public void setConfigurationManager(ConfigurationManager configurationManager) {
		this.configurationManager = configurationManager;
	}
	
	@Override
	public void initialize(SimulationState simulationState) {
		
		this.dataElement = this.properties.getStringProperty(dataElementName);
		
		this.dataItemNames.add(this.dataElement);
	}
	
	@Override
	public String readSingleValue(String outputTypeName) {
	
		SimulatorConfiguration configuration = this.configurationManager.getSimulatorConfiguration();
	
		if(!this.dataElement.equals(ConfigurationManager.simulationConfigurationName)) {
			
			try {
				
				configuration = this.configurationManager.buildConfigurationForTag(configuration, outputTypeName);
			}
			catch (Exception exception) {
				
				LoggingManager.logUser(this, exception);
			}
		}
		
		configuration.setTimeState(this.configurationManager.getSimulatorConfiguration().getTimeState());
		String resultElement = this.configurationManager.serializeToString(configuration);
		
		return resultElement;
	}
}
