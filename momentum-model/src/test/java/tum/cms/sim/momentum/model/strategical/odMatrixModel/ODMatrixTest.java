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

package tum.cms.sim.momentum.model.strategical.odMatrixModel;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import tum.cms.sim.momentum.configuration.ConfigurationManager;
import tum.cms.sim.momentum.configuration.model.strategical.StrategicalModelConfiguration;
import tum.cms.sim.momentum.data.agent.pedestrian.state.tactical.TacticalState.Behavior;
import tum.cms.sim.momentum.data.layout.ScenarioManager;
import tum.cms.sim.momentum.data.layout.area.Area;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.strategical.odMatrixModel.ODMatrixStrategical;
import tum.cms.sim.momentum.utility.generic.PropertyBackPackFactory;
import tum.cms.sim.momentum.utility.probability.ProbabilitySet;

@RunWith(JUnitParamsRunner.class)
public class ODMatrixTest {
	
	private ScenarioManager scenarioManager;
	private ConfigurationManager configurationManager;
	private ODMatrixStrategical odMatrixModel;
	
	private void createConfiguration(String configurationFile,
			List<String> substituteName,
			List<String> substituteWith) throws Exception {
		
		String configurationPath = this.getClass().getResource(configurationFile).getFile();
		configurationManager.deserializeUpdateConfiguration(configurationPath, substituteName, substituteWith);
	
		StrategicalModelConfiguration configuration = configurationManager.getSimulatorConfiguration()
				.getStrategicalModels()
				.stream().findFirst().get();

		odMatrixModel.setPropertyBackPack(PropertyBackPackFactory.fillProperties(configuration));
		scenarioManager.createLayouts(configurationManager.getSimulatorConfiguration());
	}
	
	@Before
	public void setUp() throws Exception {
		
		configurationManager = new ConfigurationManager();
		scenarioManager = new ScenarioManager();
		odMatrixModel = new ODMatrixStrategical();
		odMatrixModel.setScenario(scenarioManager);
	}

	@After
	public void tearDown() throws Exception {
		
		configurationManager = null;
		scenarioManager = null;
		odMatrixModel = null;
	}
	
	@Test
	@Parameters({"/strategic_test/ODMatrix_behaviorType_Test.xml"})
	public void loadConfiguration_behaviorType_WithSuccess(String configurationFile) throws Exception {
		
		// Arrange		
		createConfiguration(configurationFile, null, null);
		SimulationState state = mock(SimulationState.class);
		
		// Act
		odMatrixModel.callPreProcessing(state);
		
		// Assert
		Map<Area, Behavior> expectedResult = new LinkedHashMap<>();
		expectedResult.put(scenarioManager.getArea(1), Behavior.Staying);
		expectedResult.put(scenarioManager.getArea(2), Behavior.Queuing);
		expectedResult.put(scenarioManager.getArea(3), Behavior.Queuing);
		expectedResult.put(scenarioManager.getArea(4), Behavior.Staying);
		expectedResult.put(scenarioManager.getArea(5), Behavior.Staying);
		expectedResult.put(scenarioManager.getArea(6), Behavior.Staying);
		expectedResult.put(scenarioManager.getArea(7), Behavior.Staying);
		
		assertTrue(CollectionUtils.isEqualCollection(odMatrixModel.behaviorTypeMap.keySet(), expectedResult.keySet()));
		assertTrue(CollectionUtils.isEqualCollection(odMatrixModel.behaviorTypeMap.values(), expectedResult.values()));
	}

	@Test
	@Parameters({"/strategic_test/ODMatrix_fulfilmentDuration_Test.xml"})
	public void loadConfiguration_fulfilmentDuration_WithSuccess(String configurationFile) throws Exception {
		
		// Arrange		
		createConfiguration(configurationFile, null, null);
		SimulationState state = mock(SimulationState.class);
		
	
		// Act
		odMatrixModel.callPreProcessing(state);
		
		Map<Area, Double> expectedResult = new LinkedHashMap<>();
		expectedResult.put(scenarioManager.getArea(1), 0.0);
		expectedResult.put(scenarioManager.getArea(2), 240.0);
		expectedResult.put(scenarioManager.getArea(3), 180.0);
		expectedResult.put(scenarioManager.getArea(4), 0.0);
		expectedResult.put(scenarioManager.getArea(5), 2.0);
		expectedResult.put(scenarioManager.getArea(6), 3.0);
		expectedResult.put(scenarioManager.getArea(7), 4.4);

		// Assert
		assertTrue(CollectionUtils.isEqualCollection(odMatrixModel.fulfilmentMap.keySet(), expectedResult.keySet()));
		assertTrue(CollectionUtils.isEqualCollection(odMatrixModel.fulfilmentMap.values(), expectedResult.values()));
	}
	
	@Test
	@Parameters( {"/strategic_test/ODMatrix_fulfilmentOverallDuration_Test.xml" })
	public void loadConfiguration_fulfilmentOverallDuration_WithSuccess(String configurationFile) throws Exception {
	
		// Arrange		
		createConfiguration(configurationFile, null, null);
		SimulationState state = mock(SimulationState.class);
		
		// Act
		odMatrixModel.callPreProcessing(state);
		
		// Assert
		double expectedResult = 11.1;
		assertEquals(expectedResult, odMatrixModel.fulfilmentOverallDuration, 0.0);
	}
	
	@Test
	@Parameters( {"/strategic_test/ODMatrix_originDestinationSingleMatrix_Test.xml" })
	public void loadConfiguration_originDestinationSingleMatrix_WithSuccess(String configurationFile) throws Exception {
		
		// Arrange		
		String csvFilePath = this.getClass()
				.getResource("/strategic_test/ODMatrix_originDestinationSingleMatrix_TestFile.csv")
				.getFile();
		
		createConfiguration(configurationFile,
				Arrays.asList("TESTPATH"),
				Arrays.asList(csvFilePath));

		SimulationState state = mock(SimulationState.class);
		
		// Act
		odMatrixModel.callPreProcessing(state);
			
		// Assert
		Map<Area, ProbabilitySet<Area>> expectedFirstResultMatrix = new LinkedHashMap<>();
			
		ProbabilitySet<Area> expectedResultFirstSet = new ProbabilitySet<>();
		expectedResultFirstSet.append(scenarioManager.getArea(3), 0.4);
		expectedResultFirstSet.append(scenarioManager.getArea(4), 0.6);
		expectedFirstResultMatrix.put(scenarioManager.getArea(0), expectedResultFirstSet);
		
		ProbabilitySet<Area> expectedResultSecondSet = new ProbabilitySet<>();
		expectedResultSecondSet.append(scenarioManager.getArea(3), 0.0);
		expectedResultSecondSet.append(scenarioManager.getArea(4), 1.0);
		expectedFirstResultMatrix.put(scenarioManager.getArea(3), expectedResultSecondSet);
	
		ODMatrixState matrixStateFirst = odMatrixModel.odMatrices.get(0);
		
		assertEquals(odMatrixModel.odMatrices.size(), 1);
		
		assertNotNull(matrixStateFirst);
		
		assertEquals(matrixStateFirst.getStartTime(), 0.0, 0.0);
		
		assertTrue(CollectionUtils.isEqualCollection(matrixStateFirst.getOdMatrix().get(scenarioManager.getArea(0)).getSet(),
				expectedFirstResultMatrix.get(scenarioManager.getArea(0)).getSet()));
		assertTrue(CollectionUtils.isEqualCollection(matrixStateFirst.getOdMatrix().get(scenarioManager.getArea(3)).getSet(),
				expectedFirstResultMatrix.get(scenarioManager.getArea(3)).getSet()));
	}
	
	@Test
	@Parameters( { "/strategic_test/ODMatrix_originDestinationMultiMatrix_Test.xml" })
	public void loadConfiguration_originDestinationMultiMatrix_WithSuccess(String configurationFile) throws Exception {

		// Arrange		
		String csvFilePath = this.getClass()
				.getResource("/strategic_test/ODMatrix_originDestinationMultiMatrix_TestFile.csv")
				.getFile();
		
		createConfiguration(configurationFile,
				Arrays.asList("TESTPATH"),
				Arrays.asList(csvFilePath));

		SimulationState state = mock(SimulationState.class);
		
		// Act
		odMatrixModel.callPreProcessing(state);
			
		// Assert
		Map<Area, ProbabilitySet<Area>> expectedFirstResultMatrix = new LinkedHashMap<>();
			
		ProbabilitySet<Area> expectedResultFirstSet = new ProbabilitySet<>();
		expectedResultFirstSet.append(scenarioManager.getArea(3), 0.4);
		expectedResultFirstSet.append(scenarioManager.getArea(4), 0.6);
		expectedFirstResultMatrix.put(scenarioManager.getArea(0), expectedResultFirstSet);
		
		ProbabilitySet<Area> expectedResultSecondSet = new ProbabilitySet<>();
		expectedResultSecondSet.append(scenarioManager.getArea(3), 0.0);
		expectedResultSecondSet.append(scenarioManager.getArea(4), 1.0);
		expectedFirstResultMatrix.put(scenarioManager.getArea(3), expectedResultSecondSet);
		
		Map<Area, ProbabilitySet<Area>> expectedSecondResultMatrix = new LinkedHashMap<>();
		
		expectedResultFirstSet = new ProbabilitySet<>();
		expectedResultFirstSet.append(scenarioManager.getArea(3), 0.6);
		expectedResultFirstSet.append(scenarioManager.getArea(4), 0.4);
		expectedSecondResultMatrix.put(scenarioManager.getArea(0), expectedResultFirstSet);
		
		expectedResultSecondSet = new ProbabilitySet<>();
		expectedResultSecondSet.append(scenarioManager.getArea(3), 1.0);
		expectedResultSecondSet.append(scenarioManager.getArea(4), 0.0);
		expectedSecondResultMatrix.put(scenarioManager.getArea(3), expectedResultSecondSet);
		
		ODMatrixState matrixStateFirst = odMatrixModel.odMatrices.get(0);
		ODMatrixState matrixStateSecond = odMatrixModel.odMatrices.get(1);
		
		assertEquals(odMatrixModel.odMatrices.size(), 2);
		
		assertNotNull(matrixStateFirst);
		assertNotNull(matrixStateSecond);
		
		assertEquals(matrixStateFirst.getStartTime(), 0.0, 0.0);
		assertEquals(matrixStateSecond.getStartTime(), 5.0, 0.0);	
		
		assertTrue(CollectionUtils.isEqualCollection(matrixStateFirst.getOdMatrix().get(scenarioManager.getArea(0)).getSet(),
				expectedFirstResultMatrix.get(scenarioManager.getArea(0)).getSet()));
		assertTrue(CollectionUtils.isEqualCollection(matrixStateFirst.getOdMatrix().get(scenarioManager.getArea(3)).getSet(),
				expectedFirstResultMatrix.get(scenarioManager.getArea(3)).getSet()));
		
		assertTrue(CollectionUtils.isEqualCollection(matrixStateSecond.getOdMatrix().get(scenarioManager.getArea(0)).getSet(),
				expectedSecondResultMatrix.get(scenarioManager.getArea(0)).getSet()));
		assertTrue(CollectionUtils.isEqualCollection(matrixStateSecond.getOdMatrix().get(scenarioManager.getArea(3)).getSet(),
				expectedSecondResultMatrix.get(scenarioManager.getArea(3)).getSet()));
	}
	
	@Test
	@Parameters( { "/strategic_test/ODMatrix_serviceTimeDistribution_Test.xml" })
	public void loadConfiguration_serviceTimeDistribution_WithSuccess(String configurationFile) throws Exception {
	
		// Arrange		
		String csvFilePath = this.getClass()
				.getResource("/strategic_test/ODMatrix_serviceTimeDistribution_TestFile.csv")
				.getFile();
		
		createConfiguration(configurationFile,
				Arrays.asList("TESTPATH"),
				Arrays.asList(csvFilePath));

		SimulationState state = mock(SimulationState.class);
	
		
		// Act
		odMatrixModel.callPreProcessing(state);
			
		// Assert

		Map<Integer, ProbabilitySet<Double>> serviceTimeDistributions = new LinkedHashMap<>();

		ProbabilitySet<Double> expectedResultFirstSet = new ProbabilitySet<>();
		expectedResultFirstSet.append(405.0, 0.5);
		expectedResultFirstSet.append(1920.0, 0.5);
		serviceTimeDistributions.put(0, expectedResultFirstSet);
		
		ProbabilitySet<Double> expectedResultSecondSet = new ProbabilitySet<>();
		expectedResultSecondSet.append(5.0, 0.076923077);
		expectedResultSecondSet.append(35.0, 0.307692308);
		expectedResultSecondSet.append(130.0, 0.076923077);
		expectedResultSecondSet.append(195.0, 0.076923077);
		expectedResultSecondSet.append(280.0, 0.076923077);
		expectedResultSecondSet.append(480.0, 0.153846154);
		expectedResultSecondSet.append(565.0, 0.076923077);
		expectedResultSecondSet.append(740.0, 0.076923077);
		expectedResultSecondSet.append(1690.0, 0.076923077);
		serviceTimeDistributions.put(3, expectedResultSecondSet);
		
		assertEquals(odMatrixModel.serviceTimeDistributions.size(), 2);
		assertNotNull(odMatrixModel.serviceTimeDistributions.get(0));
		assertNotNull(odMatrixModel.serviceTimeDistributions.get(3));
		
		assertTrue(CollectionUtils.isEqualCollection(odMatrixModel.serviceTimeDistributions.get(0).getSet(),
				serviceTimeDistributions.get(0).getSet()));
		assertTrue(CollectionUtils.isEqualCollection(odMatrixModel.serviceTimeDistributions.get(3).getSet(),
				serviceTimeDistributions.get(3).getSet()));
		
	}
}
