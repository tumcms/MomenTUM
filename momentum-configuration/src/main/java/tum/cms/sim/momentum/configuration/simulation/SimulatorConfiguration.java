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

package tum.cms.sim.momentum.configuration.simulation;

import java.util.ArrayList;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import tum.cms.sim.momentum.configuration.absorber.AbsorberConfiguration;
import tum.cms.sim.momentum.configuration.execution.ExecutionOrderConfiguration;
import tum.cms.sim.momentum.configuration.execution.LoggingConfiguration;
import tum.cms.sim.momentum.configuration.execution.LoopConfiguration;
import tum.cms.sim.momentum.configuration.execution.ThreadingStateConfiguration;
import tum.cms.sim.momentum.configuration.execution.TimeStateConfiguration;
import tum.cms.sim.momentum.configuration.generator.GeneratorConfiguration;
import tum.cms.sim.momentum.configuration.generator.PedestrianSeedConfiguration;
import tum.cms.sim.momentum.configuration.model.PedestrianBehaviorModelConfiguration;
import tum.cms.sim.momentum.configuration.model.analysis.AnalysisConfiguration;
import tum.cms.sim.momentum.configuration.model.graph.GraphModelConfiguration;
import tum.cms.sim.momentum.configuration.model.lattice.LatticeModelConfiguration;
import tum.cms.sim.momentum.configuration.model.operational.OperationalModelConfiguration;
import tum.cms.sim.momentum.configuration.model.operational.StandingModelConfiguration;
import tum.cms.sim.momentum.configuration.model.operational.WalkingModelConfiguration;
import tum.cms.sim.momentum.configuration.model.other.MetaModelConfiguration;
import tum.cms.sim.momentum.configuration.model.other.PerceptualModelConfiguration;
import tum.cms.sim.momentum.configuration.model.output.OutputWriterConfiguration;
import tum.cms.sim.momentum.configuration.model.spaceSyntax.SpaceSyntaxModelConfiguration;
import tum.cms.sim.momentum.configuration.model.strategical.StrategicalModelConfiguration;
import tum.cms.sim.momentum.configuration.model.tactical.QueuingModelConfiguration;
import tum.cms.sim.momentum.configuration.model.tactical.RoutingModelConfiguration;
import tum.cms.sim.momentum.configuration.model.tactical.SearchingModelConfiguration;
import tum.cms.sim.momentum.configuration.model.tactical.StayingModelConfiguration;
import tum.cms.sim.momentum.configuration.model.tactical.TacticalModelConfiguration;
import tum.cms.sim.momentum.configuration.network.NetworkConfiguration;
import tum.cms.sim.momentum.configuration.scenario.ScenarioConfiguration;

@XStreamAlias("simulator")
public class SimulatorConfiguration {

	@XStreamAsAttribute
	private Double simEnd;
	
	public Double getSimEnd() {
		return simEnd;
	}

	public void setSimEnd(Double simEnd) {
		this.simEnd = simEnd;
	}

	@XStreamAsAttribute
	private Integer threads;
	
	public Integer getThreads() {
		return threads;
	}

	public void setThreads(Integer threads) {
		this.threads = threads;
	}
	
	@XStreamAsAttribute
	private Double timeStepDuration;	

	public Double getTimeStepDuration() {
		return timeStepDuration;
	}

	public void setTimeStepDuration(Double timeStepDuration) {
		this.timeStepDuration = timeStepDuration;
	}
	
	@XStreamAsAttribute
	private String version;
	
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@XStreamAsAttribute
	private String simulationName;

	public String getSimulationName() {
		return simulationName;
	}

	public void setSimulationName(String simulationName) {
		this.simulationName = simulationName;
	}

	private ThreadingStateConfiguration threadingState;
	
	public ThreadingStateConfiguration getThreadingState() {
		return threadingState;
	}

	public void setThreadingState(ThreadingStateConfiguration threadingState) {
		this.threadingState = threadingState;
	}

	private TimeStateConfiguration timeState;
	
	public TimeStateConfiguration getTimeState() {
		return timeState;
	}

	public void setTimeState(TimeStateConfiguration timeState) {
		this.timeState = timeState;
	}
	
	private LoggingConfiguration logging;

	public LoggingConfiguration getLogging() {
		return logging;
	}

	public void setLogging(LoggingConfiguration logging) {
		this.logging = logging;
	}

	private NetworkConfiguration network;
	
	public NetworkConfiguration getNetwork() {
		return network;
	}

	public void setNetwork(NetworkConfiguration network) {
		this.network = network;
	}

	private LoopConfiguration loop;

	public LoopConfiguration getLoop() {
		return loop;
	}

	public void setLoop(LoopConfiguration loop) {
		this.loop = loop;
	}

	private ExecutionOrderConfiguration executionOrder;

	public ExecutionOrderConfiguration getExecutionOrder() {
		return executionOrder;
	}

	public void setExecutionOrder(ExecutionOrderConfiguration executionOrder) {
		this.executionOrder = executionOrder;
	}
	
	private ArrayList<WalkingModelConfiguration> walkingModels;
	
	public ArrayList<WalkingModelConfiguration> getWalkingModels() {
		return walkingModels;
	}

	public void setWalkingModels(ArrayList<WalkingModelConfiguration> walkingModels) {
		this.walkingModels = walkingModels;
	}

	
	private ArrayList<StandingModelConfiguration> standingModels;
	
	public ArrayList<StandingModelConfiguration> getStandingModels() {
		return standingModels;
	}

	public void setStandingModels(ArrayList<StandingModelConfiguration> standingModels) {
		this.standingModels = standingModels;
	}

	
	private ArrayList<OperationalModelConfiguration> operationalModels;

	public ArrayList<OperationalModelConfiguration> getOperationalModels() {
		return operationalModels;
	}

	public void setOperationalModels(ArrayList<OperationalModelConfiguration> operationalModels) {
		this.operationalModels = operationalModels;
	}
	
	private ArrayList<StayingModelConfiguration> stayingModels;

	public ArrayList<StayingModelConfiguration> getStayingModels() {
		return stayingModels;
	}

	public void setStayingModels(ArrayList<StayingModelConfiguration> stayingModels) {
		this.stayingModels = stayingModels;
	}
	
	private ArrayList<QueuingModelConfiguration> queuingModels;
	
	public ArrayList<QueuingModelConfiguration> getQueuingModels() {
		return queuingModels;
	}

	public void setQueuingModels(ArrayList<QueuingModelConfiguration> queuingModels) {
		this.queuingModels = queuingModels;
	}

	private ArrayList<RoutingModelConfiguration> routingModels;
	
	public ArrayList<RoutingModelConfiguration> getRoutingModels() {
		return routingModels;
	}

	public void setRoutingModels(ArrayList<RoutingModelConfiguration> routingModels) {
		this.routingModels = routingModels;
	}

	private ArrayList<SearchingModelConfiguration> searchingModels;
	
	public ArrayList<SearchingModelConfiguration> getSearchingModels() {
		return searchingModels;
	}

	public void setSearchingModels(ArrayList<SearchingModelConfiguration> searchingModels) {
		this.searchingModels = searchingModels;
	}

	
	private ArrayList<TacticalModelConfiguration> tacticalModels;
	
	public ArrayList<TacticalModelConfiguration> getTacticalModels() {
		return tacticalModels;
	}

	public void setTacticalModels(ArrayList<TacticalModelConfiguration> tacticalModels) {
		this.tacticalModels = tacticalModels;
	}

	private ArrayList<StrategicalModelConfiguration> strategicalModels;
	
	public ArrayList<StrategicalModelConfiguration> getStrategicalModels() {
		return strategicalModels;
	}

	public void setStrategicalModels(ArrayList<StrategicalModelConfiguration> strategicalModels) {
		this.strategicalModels = strategicalModels;
	}
	
	public ArrayList<PedestrianBehaviorModelConfiguration> getAllModels() {
		
		ArrayList<PedestrianBehaviorModelConfiguration> result = new ArrayList<PedestrianBehaviorModelConfiguration>();
		
		result.addAll(this.strategicalModels);
		result.addAll(this.tacticalModels);
		result.addAll(this.operationalModels);
		
		return result;
	}
	
	private ArrayList<MetaModelConfiguration> metaModels;
	
	public ArrayList<MetaModelConfiguration> getMetaModels() {
		return metaModels;
	}

	public void setMetaModels(ArrayList<MetaModelConfiguration> metaModels) {
		this.metaModels = metaModels;
	}

	private ArrayList<AnalysisConfiguration> analysisModels;
	
	public ArrayList<AnalysisConfiguration> getAnalysisModels() {
		return analysisModels;
	}

	public void setTimeAnalysisModels(ArrayList<AnalysisConfiguration> analysisModels) {
		this.analysisModels = analysisModels;
	}
	
	private ArrayList<PerceptualModelConfiguration> perceptualModels;
	
	public ArrayList<PerceptualModelConfiguration> getPerceptualModels() {
		return perceptualModels;
	}

	public void setPerceptualModels(ArrayList<PerceptualModelConfiguration> perceptualModels) {
		this.perceptualModels = perceptualModels;
	}

	private ArrayList<GeneratorConfiguration> generators;

	public ArrayList<GeneratorConfiguration> getGenerators() {
		return generators;
	}

	public void setGenerators(ArrayList<GeneratorConfiguration> generators) {
		this.generators = generators;
	}
	
	private ArrayList<AbsorberConfiguration> absorbers;

	public ArrayList<AbsorberConfiguration> getAbsorbers() {
		return absorbers;
	}

	public void setAbsorbers(ArrayList<AbsorberConfiguration> absorbers) {
		this.absorbers = absorbers;
	}

	private ArrayList<PedestrianSeedConfiguration> pedestrianSeeds;

	public ArrayList<PedestrianSeedConfiguration> getPedestrianSeeds() {
		return pedestrianSeeds;
	}

	public void setPedestrianSeeds(
			ArrayList<PedestrianSeedConfiguration> pedestrianSeeds) {
		this.pedestrianSeeds = pedestrianSeeds;
	}

	private ArrayList<LatticeModelConfiguration> lattices;
	
	public ArrayList<LatticeModelConfiguration> getLattices() {
		return lattices;
	}

	public void setLattices(ArrayList<LatticeModelConfiguration> lattices) {
		this.lattices = lattices;
	}

	private ArrayList<GraphModelConfiguration> graphs;
	
	public ArrayList<GraphModelConfiguration> getGraphs() {
		return graphs;
	}

	public void setGraphs(ArrayList<GraphModelConfiguration> graphs) {
		this.graphs = graphs;
	}
	
	private ArrayList<SpaceSyntaxModelConfiguration> spaceSyntaxes;
	
	public ArrayList<SpaceSyntaxModelConfiguration> getSpaceSyntaxes() {
		return spaceSyntaxes;
	}
	
	public void setSpaceSyntaxes(ArrayList<SpaceSyntaxModelConfiguration> spaceSyntaxes) {
		this.spaceSyntaxes = spaceSyntaxes;
	}

	private ArrayList<OutputWriterConfiguration> outputWriters;

	public ArrayList<OutputWriterConfiguration> getOutputWriters() {
		return outputWriters;
	}

	public void setOutputWriters(ArrayList<OutputWriterConfiguration> outputWriters) {
		this.outputWriters = outputWriters;
	}

	private ArrayList<ScenarioConfiguration> layouts;

	public ArrayList<ScenarioConfiguration> getLayouts() {
		return layouts;
	}

	public void setLayouts(ArrayList<ScenarioConfiguration> layouts) {
		this.layouts = layouts;
	}
}

