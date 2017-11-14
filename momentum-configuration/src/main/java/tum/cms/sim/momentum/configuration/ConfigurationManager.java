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

package tum.cms.sim.momentum.configuration;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.thoughtworks.xstream.XStream;

import tum.cms.sim.momentum.configuration.absorber.*;
import tum.cms.sim.momentum.configuration.execution.*;
import tum.cms.sim.momentum.configuration.generator.*;
import tum.cms.sim.momentum.configuration.generic.*;
import tum.cms.sim.momentum.configuration.model.*;
import tum.cms.sim.momentum.configuration.model.analysis.*;
import tum.cms.sim.momentum.configuration.model.graph.*;
import tum.cms.sim.momentum.configuration.model.lattice.*;
import tum.cms.sim.momentum.configuration.model.operational.*;
import tum.cms.sim.momentum.configuration.model.other.*;
import tum.cms.sim.momentum.configuration.model.output.*;
import tum.cms.sim.momentum.configuration.model.spaceSyntax.*;
import tum.cms.sim.momentum.configuration.model.strategical.*;
import tum.cms.sim.momentum.configuration.model.tactical.*;
import tum.cms.sim.momentum.configuration.scenario.*;
import tum.cms.sim.momentum.configuration.simulation.*;

public class ConfigurationManager {

	public static final String simulationConfigurationName = "simulator";
	
	private final static String xmlHeader = "<?xml version=\"1.0\"  encoding=\"utf-8\" ?>";
	private static int idSeed = -1;

	public static synchronized int getNewId() {
			
		int result = ++ConfigurationManager.idSeed;
		
		return result;
	}
	
	private SimulatorConfiguration simulatorConfiguration = null;

	public SimulatorConfiguration getSimulatorConfiguration() {
		return simulatorConfiguration;
	}
	
	//if false, csv lists etc. are not loaded and only the file link is stored in the configuration
	private Boolean loadExternalFiles = true;
	
	public Boolean isLoadExternalFiles() {
		return this.loadExternalFiles;
	}
	
	public void setLoadExternalFiles(Boolean value) {
		this.loadExternalFiles = value;
	}
	
	public SimulatorConfiguration deserializeUpdateConfiguration(String configurationFileName,
			List<String> subtituteNames,
			List<String> subtituteWith) throws IOException {
		
		Path configuration = new File(configurationFileName).toPath();	
		Charset charset = StandardCharsets.UTF_8;
		
		String updatedContent = new String(Files.readAllBytes(configuration), charset);

		if(subtituteNames != null && !subtituteNames.isEmpty()) {
			
			for(int iter = 0; iter < subtituteNames.size(); iter++) {

				updatedContent = updatedContent.replaceAll(
						"\\$" + subtituteNames.get(iter).toUpperCase() + "\\$",
						subtituteWith.get(iter));
			}
		}
		
		XStream deserializer = new XStream();

		this.processAnnotiations(deserializer);
		this.registerConverters(deserializer);
		
		this.simulatorConfiguration = this.fromText(updatedContent, deserializer);
		
		return this.simulatorConfiguration;
	}
	
	// XStream
	public void deserializeCompleteConfiguration(String completeConfigurationFileName) throws FileNotFoundException {
		
		XStream deserializer = new XStream();

		this.processAnnotiations(deserializer);
		this.registerConverters(deserializer);

		this.simulatorConfiguration = this.fromXml(completeConfigurationFileName, deserializer);
	}

	public ScenarioConfiguration loadExternalLayout(String layoutLink) throws FileNotFoundException {

		// because the input may have a different xml depth, but we dont want to force the user to choose one, we check
		
		Object output = this.<Object>deserializePartConfiguration(layoutLink);
		ScenarioConfiguration result = null;
		
		if(output instanceof SimulatorConfiguration) {
			
			result = ((SimulatorConfiguration)output).getLayouts().get(0);
		}
		else {
			
			result = ((ScenarioConfiguration)output);
		}
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T deserializePartConfiguration(String partConfigurationFileName) throws FileNotFoundException {
		
		XStream deserializer = new XStream();

		this.processAnnotiations(deserializer);
		this.registerConverters(deserializer);

		return (T)this.fromXml(partConfigurationFileName, deserializer);
	}
	
	/**
	 * Finds the configuration part with the given name and
	 * returns a new configuration comprising the configuration part
	 * as field (property)
	 */
	public SimulatorConfiguration buildConfigurationForTag(SimulatorConfiguration sourceConfiguration, String name) throws Exception {
	
		Object element = null;
		
		Method[] configurationMethods = sourceConfiguration.getClass().getMethods();
		
		for(int iter = 0; iter < configurationMethods.length; iter++) {
			
			String compareAgainst = "get" + name.substring(0, 1).toUpperCase() + name.substring(1);
			
			if(configurationMethods[iter].getName().equals(compareAgainst)) {
			
				element = configurationMethods[iter].invoke(sourceConfiguration);
				break;
			}
		}
		
		SimulatorConfiguration targetConfiguration = new SimulatorConfiguration();
		
		for(int iter = 0; iter < configurationMethods.length; iter++) {
			
			String compareAgainst = "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
			
			if(configurationMethods[iter].getName().equals(compareAgainst)) {
			
				configurationMethods[iter].invoke(targetConfiguration, element);
				break;
			}
		}
		
		targetConfiguration.setSimulationName(sourceConfiguration.getSimulationName());
		targetConfiguration.setVersion(sourceConfiguration.getVersion());

		return targetConfiguration;
	}
	
	public String serializeToString(SimulatorConfiguration configuration) {
		
		XStream serializer = new XStream();

		this.processAnnotiations(serializer);
		this.registerConverters(serializer);
	
		String resultString = serializer.toXML(configuration);
		
		return resultString;
	}

	public void writeScenarioConfigToXml(String fileName, ScenarioConfiguration scenarioConfiguration) throws IOException {
		
		XStream serializer = new XStream();

		this.processAnnotiations(serializer);
		this.registerConverters(serializer);
		
		FileWriter writer = new FileWriter(fileName);
		serializer.toXML(scenarioConfiguration, writer);
		writer.close();
	}
	
	public void writeConfigToStream(PrintStream printer, SimulatorConfiguration configuration) {
		
		XStream serializer = new XStream();

		this.processAnnotiations(serializer);
		this.registerConverters(serializer);
		
		String xmlOut = xmlHeader + System.lineSeparator() + serializer.toXML(configuration);
		printer.print(xmlOut);
	}
	
	public void writeLayoutToXml(File file, SimulatorConfiguration layoutConfiguration) throws IOException {
		
		XStream serializer = new XStream();

		this.processAnnotiations(serializer);
		this.registerConverters(serializer);
		
		FileWriter writer = new FileWriter(file);
		serializer.toXML(layoutConfiguration, writer);
	}

	public void writeConfigToXml(String fileName, SimulatorConfiguration configuration) throws IOException {
		
		XStream serializer = new XStream();

		this.processAnnotiations(serializer);
		this.registerConverters(serializer);
		
		FileWriter writer = new FileWriter(fileName);
		serializer.toXML(configuration, writer);
		writer.close();
	}

	@SuppressWarnings("unchecked")
	private <T> T fromXml(String fileName, XStream deserializer) throws FileNotFoundException {
		
		Object result = deserializer.fromXML(new BufferedInputStream(new FileInputStream(new File(fileName))));
		return (T)result;
	}
	
	@SuppressWarnings("unchecked")
	private  <T> T fromText(String configurationText, XStream deserializer) {
		
		Object result = deserializer.fromXML(configurationText);
		return (T)result;
	}
	
	private void processAnnotiations(XStream serializer) {
		
		serializer.processAnnotations(NameIdNodeConfiguration.class); 
		serializer.processAnnotations(PropertyContainerNode.class);
		serializer.processAnnotations(ComplexPropertyConfiguration.class);
		serializer.processAnnotations(SimplePropertyConfiguration.class);
	
		serializer.processAnnotations(LoggingConfiguration.class);
		serializer.processAnnotations(LoggingStateConfiguration.class);
		
		serializer.processAnnotations(TimeStateConfiguration.class);
		serializer.processAnnotations(ThreadingStateConfiguration.class);
		serializer.processAnnotations(PedestrianBehaviorModelConfiguration.class);
	
		serializer.processAnnotations(OperationalModelConfiguration.class);
		serializer.processAnnotations(OperationalModelConfiguration.StandingReference.class);
		serializer.processAnnotations(OperationalModelConfiguration.WalkingReference.class);
		serializer.processAnnotations(WalkingModelConfiguration.class);
		serializer.processAnnotations(StandingModelConfiguration.class);
				
		serializer.processAnnotations(TacticalModelConfiguration.class);
		serializer.processAnnotations(TacticalModelConfiguration.StayingReference.class);
		serializer.processAnnotations(TacticalModelConfiguration.QueuingReference.class);
		serializer.processAnnotations(TacticalModelConfiguration.RoutingReference.class);
		serializer.processAnnotations(TacticalModelConfiguration.SearchingReference.class);
		serializer.processAnnotations(StayingModelConfiguration.class);
		serializer.processAnnotations(QueuingModelConfiguration.class);
		serializer.processAnnotations(RoutingModelConfiguration.class);
		serializer.processAnnotations(SearchingModelConfiguration.class);
		
		serializer.processAnnotations(StrategicalModelConfiguration.class);
		
		serializer.processAnnotations(MetaModelConfiguration.class);
		serializer.processAnnotations(PerceptualModelConfiguration.class);
		
		serializer.processAnnotations(GraphOperationConfiguration.class);
		serializer.processAnnotations(GraphModelConfiguration.class);
		
		serializer.processAnnotations(SpaceSyntaxOperationConfiguration.class);
		serializer.processAnnotations(SpaceSyntaxModelConfiguration.class);
				
		serializer.processAnnotations(ExecutionOrderConfiguration.class);
		serializer.processAnnotations(ExecutionBlockConfiguration.class);
		serializer.processAnnotations(ExecutionModelConfiguration.class);
		
		serializer.processAnnotations(LoopVariableConfiguration.class);
		serializer.processAnnotations(LoopConfiguration.class);
		
		serializer.processAnnotations(PointConfiguration.class);
		serializer.processAnnotations(DiameterConfiguration.class);
		serializer.processAnnotations(GeometryConfiguration.class);	
		
		serializer.processAnnotations(AreaConfiguration.class);
		serializer.processAnnotations(TaggedAreaConfiguration.class);
		serializer.processAnnotations(ObstacleConfiguration.class);
		serializer.processAnnotations(ScenarioConfiguration.class);
		
		serializer.processAnnotations(LatticeModelConfiguration.class);
		serializer.processAnnotations(GraphScenarioConfiguration.class);
		
		serializer.processAnnotations(AbsorberConfiguration.class);
		serializer.processAnnotations(GeneratorGeometryConfiguration.class);
		serializer.processAnnotations(PedestrianSeedConfiguration.class);
		serializer.processAnnotations(GeneratorConfiguration.class);
	
		serializer.processAnnotations(WriterSourceConfiguration.class);
		serializer.processAnnotations(WriterTargetConfiguration.class);
		serializer.processAnnotations(WriterFormatConfiguration.class);
		serializer.processAnnotations(OutputWriterConfiguration.class);
		
		serializer.processAnnotations(MeasureConfiguration.class);
		serializer.processAnnotations(AnalysisConfiguration.class);
		
		serializer.processAnnotations(SimulatorConfiguration.class);
	}
	
	private void registerConverters(XStream serializer) {
		
		serializer.registerConverter(SimplePropertyConfiguration.getSimplePropertyTypeConverter());
		serializer.registerConverter(ComplexPropertyConfiguration.getComplexPropertyTypeConverter());

		serializer.registerConverter(LoggingStateConfiguration.getTypeConverterLoggingLevel());
		serializer.registerConverter(LoggingStateConfiguration.getTypeConverterLoggingType());
		
		serializer.registerConverter(new ComplexPropertyConverter(this.loadExternalFiles));
		serializer.registerConverter(PerceptualModelConfiguration.getTypeConverter());
		
		serializer.registerConverter(MetaModelConfiguration.getTypeConverter());
		serializer.registerConverter(PerceptualModelConfiguration.getTypeConverter());
		
		serializer.registerConverter(WalkingModelConfiguration.getTypeConverter());
		serializer.registerConverter(StandingModelConfiguration.getTypeConverter());
		
		serializer.registerConverter(StayingModelConfiguration.getTypeConverter());
		serializer.registerConverter(QueuingModelConfiguration.getTypeConverter());
		serializer.registerConverter(RoutingModelConfiguration.getTypeConverter());
		serializer.registerConverter(SearchingModelConfiguration.getTypeConverter());
		
		serializer.registerConverter(StrategicalModelConfiguration.getTypeConverter());
		
		serializer.registerConverter(LatticeModelConfiguration.getLatticeTypeConverter());
		serializer.registerConverter(LatticeModelConfiguration.getNeighbourhoodTypeConverter());
		serializer.registerConverter(LatticeModelConfiguration.getBehaviorTypeConverter());
		
		serializer.registerConverter(GraphOperationConfiguration.getTypeConverter());
		serializer.registerConverter(SpaceSyntaxOperationConfiguration.getTypeConverter());
		serializer.registerConverter(AreaConfiguration.getTypeConverter());
		serializer.registerConverter(TaggedAreaConfiguration.getTypeConverter());
		serializer.registerConverter(ObstacleConfiguration.getTypeConverter());
		
		serializer.registerConverter(PedestrianSeedConfiguration.getTypeConverter());
		serializer.registerConverter(AbsorberConfiguration.getTypeConverter());
		serializer.registerConverter(GeneratorGeometryConfiguration.getGeometryTypeConverter());
		
		serializer.registerConverter(WriterSourceConfiguration.getTypeConverter());
		serializer.registerConverter(WriterFormatConfiguration.getTypeConverter());
		serializer.registerConverter(WriterTargetConfiguration.getTypeConverter());
		
		serializer.registerConverter(MeasureConfiguration.getTypeConverter());
		
		serializer.registerConverter(LoopConfiguration.getTypeConverter());
	}
}
