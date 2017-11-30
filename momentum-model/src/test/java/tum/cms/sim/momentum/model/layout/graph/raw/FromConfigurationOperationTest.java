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

package tum.cms.sim.momentum.model.layout.graph.raw;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import tum.cms.sim.momentum.configuration.ConfigurationManager;
import tum.cms.sim.momentum.configuration.model.graph.GraphModelConfiguration;
import tum.cms.sim.momentum.data.layout.ScenarioManager;
import tum.cms.sim.momentum.data.layout.area.Area;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.utility.generic.PropertyBackPackFactory;
import tum.cms.sim.momentum.utility.graph.Vertex;

@RunWith(JUnitParamsRunner.class)
public class FromConfigurationOperationTest {

	private ScenarioManager scenarioManager;
	private ConfigurationManager configurationManager;
	private FromConfigurationOperation graphOperation;

	private void createConfiguration(String configurationFile) throws Exception {
		
		String configurationPath = this.getClass().getResource(configurationFile).getFile();
		configurationManager.deserializeCompleteConfiguration(configurationPath);
		scenarioManager.createLayouts(configurationManager.getSimulatorConfiguration());
		
		GraphModelConfiguration operationConfiguration = configurationManager.getSimulatorConfiguration().getGraphs().get(0);

		graphOperation = new FromConfigurationOperation(configurationManager.getSimulatorConfiguration().getLayouts());
		graphOperation.setScenario(scenarioManager);
		graphOperation.setPropertyBackPack(PropertyBackPackFactory.fillProperties(operationConfiguration.getGraphOperations().get(0)));
	}
	
	@Before
	public void setUp() throws Exception {
		
		configurationManager = new ConfigurationManager();
		scenarioManager = new ScenarioManager();
	}

	@After
	public void tearDown() throws Exception {
		
		configurationManager = null;
		scenarioManager = null;
		graphOperation = null;
	}
	
	@Test
	@Parameters({"/layout_graph_test/FromGraphConfigurationOperation_insideArea_test.xml"})
	public void fromConfiguration_loadsSingleVertexInsideArea_WithSuccess(String configurationFile) throws Exception {
	
		// Arrange
		createConfiguration(configurationFile);
		SimulationState state = mock(SimulationState.class);
		
		// Act
		graphOperation.callPreProcessing(state);
		
		// Assert
		Vertex vertexInSideArea = scenarioManager.getGraph().getVertex(6);
		
		boolean isSeed = vertexInSideArea.isSeed();
		boolean isInArea = scenarioManager.getArea(2).getGeometry().contains(vertexInSideArea.getGeometry().getCenter());
		
		assertTrue(isSeed && isInArea);
	}
	
	@Test
	@Parameters({"/layout_graph_test/FromGraphConfigurationOperation_insideArea_test.xml"})
	public void fromConfiguration_loadsOnlySingleVertexInsideArea_WithSuccess(String configurationFile) throws Exception {
	
		// Arrange
		createConfiguration(configurationFile);
		SimulationState state = mock(SimulationState.class);
		
		// Act
		graphOperation.callPreProcessing(state);
		
		// Assert
		Vertex vertexInSideAreaFirst = scenarioManager.getGraph().getVertex(1);
		Vertex vertexInSideAreaSecond = scenarioManager.getGraph().getVertex(2);
		
		boolean isFirstSeed = vertexInSideAreaFirst.isSeed();
		boolean isFirstInArea = scenarioManager.getArea(3).getGeometry().contains(vertexInSideAreaFirst.getGeometry().getCenter());
		boolean isSecSeed = vertexInSideAreaSecond.isSeed();
		boolean isSecInArea = scenarioManager.getArea(3).getGeometry().contains(vertexInSideAreaSecond.getGeometry().getCenter());
		
		assertTrue(
				(isFirstSeed && isFirstInArea && !isSecSeed && isSecInArea) ||
				(!isFirstSeed && isFirstInArea && isSecSeed && isSecInArea)
				);
	}
	
	@Test
	@Parameters({"/layout_graph_test/FromGraphConfigurationOperation_insideArea_test.xml"})
	public void fromConfiguration_ignoresVertexOutsideArea_WithSuccess(String configurationFile) throws Exception {
		
		// Arrange
		createConfiguration(configurationFile);
		SimulationState state = mock(SimulationState.class);
		
		// Act
		graphOperation.callPreProcessing(state);
		
		// Assert
		Vertex vertexInSideArea = scenarioManager.getGraph().getVertex(3);
		
		boolean isSeed = vertexInSideArea.isSeed();
		boolean isInArea = false;
	
		for(Area area : scenarioManager.getAreas()) {
			
			isInArea = isInArea || area.getGeometry().contains(vertexInSideArea.getGeometry().getCenter());
		}

		assertTrue(!isSeed && !isInArea);
	}
	
	@Test
	@Parameters({"/layout_graph_test/FromGraphConfigurationOperation_insideArea_test.xml"})
	public void fromConfiguration_loadsSingleVertexOnBorderOfArea_WithoutSuccess(String configurationFile) throws Exception {
		
		// Arrange
		createConfiguration(configurationFile);
		
		// Arrange
		createConfiguration(configurationFile);
		SimulationState state = mock(SimulationState.class);
		
		// Act
		graphOperation.callPreProcessing(state);
		
		// Assert
		Vertex vertexInSideArea = scenarioManager.getGraph().getVertex(5);
		
		boolean isSeed = vertexInSideArea.isSeed();
		boolean isInArea = scenarioManager.getArea(1).getGeometry().contains(vertexInSideArea.getGeometry().getCenter());

		assertTrue(!isSeed && !isInArea);
	}

	@Test
	@Parameters({"/layout_graph_test/FromGraphConfigurationOperation_withPrecisionSeed_test.xml"})
	
	public void fromConfiguration_loadsSingleVertexWithPrecision_WithSuccess(String configurationFile) throws Exception {
		// Arrange
		createConfiguration(configurationFile);
		
		// Arrange
		createConfiguration(configurationFile);
		SimulationState state = mock(SimulationState.class);
		
		// Act
		graphOperation.callPreProcessing(state);
		
		// Assert
		Vertex vertexInSideArea = scenarioManager.getGraph().getVertex(4);
		
		boolean isSeed = vertexInSideArea.isSeed();
		boolean isInArea = scenarioManager.getArea(1).getGeometry().contains(vertexInSideArea.getGeometry().getCenter());

		assertTrue(isSeed && isInArea);
	}
}
