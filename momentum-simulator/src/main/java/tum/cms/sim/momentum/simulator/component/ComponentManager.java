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

package tum.cms.sim.momentum.simulator.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import tum.cms.sim.momentum.configuration.ConfigurationManager;
import tum.cms.sim.momentum.configuration.absorber.*;
import tum.cms.sim.momentum.configuration.generator.GeneratorConfiguration;
import tum.cms.sim.momentum.configuration.generator.PedestrianSeedConfiguration;
import tum.cms.sim.momentum.configuration.model.analysis.AnalysisConfiguration;
import tum.cms.sim.momentum.configuration.model.graph.GraphModelConfiguration;
import tum.cms.sim.momentum.configuration.model.lattice.LatticeModelConfiguration;
import tum.cms.sim.momentum.configuration.model.operational.*;
import tum.cms.sim.momentum.configuration.model.other.*;
import tum.cms.sim.momentum.configuration.model.output.OutputWriterConfiguration;
import tum.cms.sim.momentum.configuration.model.output.WriterFormatConfiguration;
import tum.cms.sim.momentum.configuration.model.output.WriterSourceConfiguration;
import tum.cms.sim.momentum.configuration.model.output.WriterTargetConfiguration;
import tum.cms.sim.momentum.configuration.model.spaceSyntax.SpaceSyntaxModelConfiguration;
import tum.cms.sim.momentum.configuration.model.strategical.StrategicalModelConfiguration;
import tum.cms.sim.momentum.configuration.model.tactical.*;
import tum.cms.sim.momentum.configuration.scenario.ScenarioConfiguration;
import tum.cms.sim.momentum.data.agent.car.CarManager;
import tum.cms.sim.momentum.data.agent.pedestrian.PedestrianManager;
import tum.cms.sim.momentum.data.layout.ScenarioManager;
import tum.cms.sim.momentum.infrastructure.network.NetworkManager;
import tum.cms.sim.momentum.infrastructure.time.TimeManager;
import tum.cms.sim.momentum.model.absorber.Absorber;
import tum.cms.sim.momentum.model.analysis.AnalysisModel;
import tum.cms.sim.momentum.model.analysis.measure.Measure;
import tum.cms.sim.momentum.model.generator.*;
import tum.cms.sim.momentum.model.generator.seed.PedestrianSeed;
import tum.cms.sim.momentum.model.layout.graph.GraphModel;
import tum.cms.sim.momentum.model.layout.lattice.LatticeModel;
import tum.cms.sim.momentum.model.layout.spaceSyntax.SpaceSyntaxModel;
import tum.cms.sim.momentum.model.meta.MetaModel;
import tum.cms.sim.momentum.model.operational.OperationalModel;
import tum.cms.sim.momentum.model.operational.standing.StandingModel;
import tum.cms.sim.momentum.model.operational.walking.WalkingModel;
import tum.cms.sim.momentum.model.output.OutputWriter;
import tum.cms.sim.momentum.model.output.writerFormats.WriterFormat;
import tum.cms.sim.momentum.model.output.writerSources.WriterSource;
import tum.cms.sim.momentum.model.output.writerTargets.WriterTarget;
import tum.cms.sim.momentum.model.strategical.DestinationChoiceModel;
import tum.cms.sim.momentum.model.strategical.StrategicalModel;
import tum.cms.sim.momentum.model.support.perceptional.PerceptionalModel;
import tum.cms.sim.momentum.model.support.query.BasicQueryModel;
import tum.cms.sim.momentum.model.tactical.TacticalModel;
import tum.cms.sim.momentum.model.tactical.participating.StayingModel;
import tum.cms.sim.momentum.model.tactical.queuing.QueuingModel;
import tum.cms.sim.momentum.model.tactical.routing.RoutingModel;
import tum.cms.sim.momentum.model.tactical.searching.SearchingModel;
import tum.cms.sim.momentum.simulator.factory.ModelFactory;

public class ComponentManager {

	private PedestrianManager pedestrianManager = null;	
	private CarManager carManager = null;

	private ScenarioManager scenarioManager = null;
	private NetworkManager networkManager =  null;
	private TimeManager timeManager = null;
	private ConfigurationManager configurationManager = null;

	public NetworkManager getNetworkManager() {
		return networkManager;
	}
	
	public void setNetworkManager(NetworkManager networkManager) {
		this.networkManager = networkManager;
	}

	public ConfigurationManager getConfigurationManager() {
		return configurationManager;
	}

	public void setConfigurationManager(ConfigurationManager configurationManager) {
		this.configurationManager = configurationManager;
	}

	public void setTimeManager(TimeManager timeStepController) {
		this.timeManager = timeStepController;
	}
	
	public TimeManager getTimeManager() {
		return timeManager;
	}
	
	public void setPedestrianManager(PedestrianManager pedestrianManager) {
		this.pedestrianManager = pedestrianManager;
	}
	
	public PedestrianManager getPedestrianManager() {
		return pedestrianManager;
	}
	
	public CarManager getCarManager() {
		return carManager;
	}

	public void setCarManager(CarManager carManager) {
		this.carManager = carManager;
	}

	public void setScenarioManager(ScenarioManager scenarioManager) {
		this.scenarioManager = scenarioManager;
	}
	
	public ScenarioManager getScenarioManager() {
		return scenarioManager;
	}

	private HashMap<Integer, OutputWriter> outputWriters = null;
	private HashMap<Integer, Generator> generators = null;
	private HashMap<Integer, Absorber> absorbers = null;
	private HashMap<Integer, PedestrianSeed> pedestrianSeeds = null;
	
	private HashMap<Integer, OperationalModel> operationalModels = null;
	private HashMap<Integer, WalkingModel> walkingModels = null;
	private HashMap<Integer, StandingModel> standingModels = null;
	
	private HashMap<Integer, TacticalModel> tacticalModels = null;
	private HashMap<Integer, StayingModel> stayingModels = null;
	private HashMap<Integer, QueuingModel> queuingModels = null;
	private HashMap<Integer, RoutingModel> routingModels = null;
	private HashMap<Integer, SearchingModel> searchingModels = null;
	
	private HashMap<Integer, StrategicalModel> strategicalModels = null;
	private HashMap<Integer, DestinationChoiceModel> destinationChoiceModels = null;
	
	private HashMap<Integer, PerceptionalModel> perceptionalModels = null;
	private HashMap<Integer, BasicQueryModel> queryModels = null;

	private HashMap<Integer, MetaModel> metaModels = null;
	private HashMap<Integer, AnalysisModel> analysisModels = null;
	
	private HashMap<Integer, GraphModel> graphModels = null;
	private HashMap<Integer, SpaceSyntaxModel> spaceSyntaxModels = null;
	private HashMap<Integer, LatticeModel> latticeModels = null;

	public HashMap<Integer, PedestrianSeed> getPedestrianSeeds() {
		return pedestrianSeeds;
	}
	
	public PedestrianSeed getPedestrianSeed(int id) {
		return pedestrianSeeds.get(id);
	}

	public Collection<LatticeModel> getLatticeModels() {
		return latticeModels.values();
	}
	
	public Collection<SpaceSyntaxModel> getSpaceSyntaxModels() {
		return spaceSyntaxModels.values();
	}

	public Collection<GraphModel> getGraphModels() {
		return graphModels.values();
	}
	
	public Map<Integer, GraphModel> getGraphModelMap() {
		return graphModels;
	}
	
	public GraphModel getGraphModel(int id) {
		return graphModels.get(id);
	}
	
	public Collection<AnalysisModel> getAnalysisModels() {
		return analysisModels.values();
	}
	
	public Map<Integer, AnalysisModel> getAnalysisModelMap() {
		return analysisModels;
	}
	
	public AnalysisModel getAnalysisModel(int id) {
		return analysisModels.get(id);
	}
	
	public Collection<OutputWriter> getOutputWriters() {
		return outputWriters.values();
	}

	public OutputWriter getOutputWriter(int id) {
		return outputWriters.get(id);
	}
	
	public Collection<Generator> getGenerators() {
		return generators.values();
	}

	public Generator getGenerator(int id) {
		return generators.get(id);
	}
	
	public Collection<Absorber> getAbsorbers() {
		return absorbers.values();
	}

	public Absorber getAbsorber(int id) {
		return absorbers.get(id);
	}
	
	public Collection<OperationalModel> getOperationalModels() {
		return operationalModels.values();
	}
	
	public Map<Integer, OperationalModel> getOperationalModelMap() {
		return operationalModels;
	}
	
	public OperationalModel getOperationalModel(int id) {
		return operationalModels.get(id);
	}
	
	public Collection<WalkingModel> getWalkingModels() {
		return walkingModels.values();
	}
	
	public WalkingModel getWalkingModel(int id) {
		return walkingModels.get(id);
	}
	
	public Collection<StandingModel> getStandingModels() {
		return standingModels.values();
	}
	
	public StandingModel getStandingModel(int id) {
		return standingModels.get(id);
	}
	
	public Collection<TacticalModel> getTacticalModels() {
		return tacticalModels.values();
	}
	
	public Map<Integer, TacticalModel> getTacticalModelMap() {
		return tacticalModels;
	}
	
	public TacticalModel getTacticalModel(int id) {
		return tacticalModels.get(id);
	}
	
	public Collection<StayingModel> getStayingModels() {
		return stayingModels.values();
	}
	
	public StayingModel getStayingModel(int id) {
		return stayingModels.get(id);
	}
	
	public Collection<QueuingModel> getQueuingModels() {
		return queuingModels.values();
	}
	
	public QueuingModel getQueuingModel(int id) {
		return queuingModels.get(id);
	}
	
	public Collection<RoutingModel> getRoutingModels() {
		return routingModels.values();
	}
	
	public RoutingModel getRoutingModel(int id) {
		return routingModels.get(id);
	}
	
	public Collection<SearchingModel> getSearchingModels() {
		return searchingModels.values();
	}
	
	public SearchingModel getSearchingModel(int id) {
		return searchingModels.get(id);
	}
	
	public Collection<StrategicalModel> getStrategicalModels() {
		return strategicalModels.values(); 
	}
	
	public DestinationChoiceModel getDestinationChoiceModel(int id) {
		return destinationChoiceModels.get(id);
	}
	
	public Map<Integer, StrategicalModel> getStrategicalModelMap() {
		return strategicalModels;
	}
	
	public StrategicalModel getStrategicalModel(int id) {
		return strategicalModels.get(id);
	}

	public Collection<DestinationChoiceModel> getDestinationChoiceModels() {
		return destinationChoiceModels.values();
	}
	
	public Collection<PerceptionalModel> getPerceptionalModels() {
		return perceptionalModels.values();
	}
	
	public PerceptionalModel getPerceptionalModel(int id) {
		return perceptionalModels.get(id);
	}
	
	public Collection<BasicQueryModel> getQueryModels() {
		return queryModels.values();
	}
	
	public BasicQueryModel getQueryModel(int id) {
		return queryModels.get(id);
	}

	public Collection<MetaModel> getMetaModels() {
		return metaModels.values();
	}
	
	public Map<Integer, MetaModel> getMetaModelMap() {
		return metaModels;
	}
	
	public MetaModel getMetaModel(int id) {
		return metaModels.get(id);
	}

	public void createLattices(ArrayList<ScenarioConfiguration> scenarioConfigurations,
			ArrayList<LatticeModelConfiguration> latticeModelConfigurations) {
		
		this.latticeModels = new HashMap<>();
		
		if(latticeModelConfigurations != null) {
				
			for(LatticeModelConfiguration latticeModelConfiguration : latticeModelConfigurations) {
				
				LatticeModel latticeModel = ModelFactory.getLatticeFactory().createModel(latticeModelConfiguration, this);
				this.latticeModels.put(latticeModel.getId(), latticeModel);
			}
		}
	}
	
	public void createSpaceSyntaxes(ArrayList<ScenarioConfiguration> scenarioConfigurations, ArrayList<SpaceSyntaxModelConfiguration> spaceSyntaxModelConfigurations) {
		
		this.spaceSyntaxModels = new HashMap<>();
		
		if(spaceSyntaxModelConfigurations == null) {
			return;
		}
		
		for(SpaceSyntaxModelConfiguration spaceSyntaxModelConfiguration : spaceSyntaxModelConfigurations) {

			SpaceSyntaxModel spaceSyntaxModel = ModelFactory.getSpaceSyntaxModelFactory().createModel(spaceSyntaxModelConfiguration, this);
			this.spaceSyntaxModels.put(spaceSyntaxModel.getId(), spaceSyntaxModel);
		}
	}
	
	public void createGraphs(ArrayList<ScenarioConfiguration> scenarioConfigurations, ArrayList<GraphModelConfiguration> graphModelConfigurations) {
		
		this.graphModels = new HashMap<>();
		
		if(graphModelConfigurations == null) {
			
			return;
		}
		
		for(GraphModelConfiguration graphModelConfiguration : graphModelConfigurations) {
			
			if(graphModelConfigurations != null && graphModelConfigurations.size() > 0) {
					
				GraphModel graphModel = ModelFactory.getGraphModelFactory().createModel(graphModelConfiguration, this);
				this.graphModels.put(graphModel.getId(), graphModel);
			}
		}
	}
	
	public void createOutputWriter(ArrayList<OutputWriterConfiguration> outputWriterConfigurations) {
		
		this.outputWriters = new HashMap<Integer, OutputWriter>();
		
		if(outputWriterConfigurations != null) {

			for(OutputWriterConfiguration outputWriterConfiguration : outputWriterConfigurations) {

				OutputWriter outputWriter = ModelFactory.getOutputWriterFactory()
						.createModel(outputWriterConfiguration, this);	
				
				WriterSource writerSource = this.createWriterSources(outputWriterConfiguration.getWriterSource());
				WriterTarget writerTarget = this.createWriterTarget(outputWriterConfiguration.getWriterTarget());
				WriterFormat writerFormat = this.createWriterFormat(outputWriterConfiguration.getWriterFormat());
				
				outputWriter.setWriterSource(writerSource);
				outputWriter.setWriterTarget(writerTarget);
				outputWriter.setWriterFormat(writerFormat);
				
				this.outputWriters.put(outputWriterConfiguration.getId(), outputWriter);
			}		
		}
	}
	
	public void createAnalysisModels(ArrayList<AnalysisConfiguration> analysisConfigurations) {
		
		this.analysisModels = new HashMap<>();
		
		if(analysisConfigurations != null) {

			for(AnalysisConfiguration analysisConfiguration : analysisConfigurations) {
				
				AnalysisModel analysisModel = ModelFactory.getAnalysisModelFactory().createModel(analysisConfiguration,	this);
				
				Measure measureModel = ModelFactory.getMeasureFactory().createModel(
						analysisConfiguration.getMeasure(),
						this);
				
				WriterSource writerSource = this.createWriterSources(analysisConfiguration.getWriterSource());
				
				analysisModel.setMeasure(measureModel);
				analysisModel.setWriterSource(writerSource);
				
				this.analysisModels.put(analysisModel.getId(), analysisModel);
			}
		}
	}

	public void createPedestrianSeeds(ArrayList<PedestrianSeedConfiguration> seedConfigurations) {
		
		this.pedestrianSeeds = new HashMap<Integer, PedestrianSeed>();
		
		if(seedConfigurations != null) {

			for(PedestrianSeedConfiguration seedConfiguration : seedConfigurations) {
				
				PedestrianSeed pedestrianSeed = ModelFactory.getPedestrianSeedFactory().createModel(seedConfiguration, this);
				this.pedestrianSeeds.put(pedestrianSeed.getId(), pedestrianSeed);
			}
		}
	}

	public void createGenerators(ArrayList<GeneratorConfiguration> generatorConfigurations) {
		
		this.generators = new HashMap<Integer, Generator>();
		
		if(generatorConfigurations != null) {
	
			for(GeneratorConfiguration generatorConfiguration : generatorConfigurations) {

				Generator generator = ModelFactory.getGeneratorFactory().createModel(generatorConfiguration, this);
				this.generators.put(generator.getId(), generator);
			}
		}
	}

	public void createAbsorbers(ArrayList<AbsorberConfiguration> absorberConfigurations) {

		this.absorbers = new HashMap<Integer, Absorber>();
		
		if(absorberConfigurations != null) {

			for(AbsorberConfiguration configuration : absorberConfigurations) {
				
				Absorber absorber = ModelFactory.getAbsorberFactory().createModel(configuration, this);
				this.absorbers.put(absorber.getId(), absorber);
			}
		}
	}

	public void createPerceptualModels(ArrayList<PerceptualModelConfiguration> perceptualModels) {
	
		this.perceptionalModels = new HashMap<Integer, PerceptionalModel>();
	
		if(perceptualModels != null) {

			for(PerceptualModelConfiguration perceptualModelConfiguration : perceptualModels) {
				
				PerceptionalModel perceptualModel = ModelFactory.getPerceptionalModelFactory().createModel(perceptualModelConfiguration, this);	
				this.perceptionalModels.put(perceptualModel.getId(), perceptualModel);
			}
		}
	}
	
	public void createQueryModel() {
		
		this.queryModels = new HashMap<Integer, BasicQueryModel>();
	
		BasicQueryModel queryModel = new BasicQueryModel();
		
		queryModel.setPedestrianManager(this.getPedestrianManager());
		queryModel.setScenarioManager(this.getScenarioManager());
		
		queryModel.setId(0);
		queryModel.setName(BasicQueryModel.class.getSimpleName());
		
		this.queryModels.put(queryModel.getId(), queryModel);
	}

	public void createMetaModels(ArrayList<MetaModelConfiguration> metaModels) {
		
		this.metaModels = new HashMap<Integer, MetaModel>();
		
		if(metaModels != null) {

			for(MetaModelConfiguration metaModelConfiguration : metaModels) {
				
				MetaModel metaModel = ModelFactory.getMetaModelFactory().createModel(metaModelConfiguration, this);
				this.metaModels.put(metaModel.getId(), metaModel);
			}
		}
	}
	
	public void createWalkingModels(ArrayList<WalkingModelConfiguration> walkingModelConfigurations) {
		
		this.walkingModels = new HashMap<Integer, WalkingModel>();
		
		if(walkingModelConfigurations != null) {
				
			for(WalkingModelConfiguration walkingModelConfiguration : walkingModelConfigurations) {
				
				WalkingModel walkingModel = ModelFactory.getWalkingModelFactory().createModel(walkingModelConfiguration, this);
				this.walkingModels.put(walkingModel.getId(), walkingModel);	
			}
		}
	}
	
	public void createStandingModels(ArrayList<StandingModelConfiguration> standingModelConfigurations) {
		
		this.standingModels = new HashMap<Integer, StandingModel>();
		
		if(standingModelConfigurations != null) {
			
			for(StandingModelConfiguration standingModelConfiguration : standingModelConfigurations) {
				
				StandingModel standingModel = ModelFactory.getStandingModelFactory().createModel(standingModelConfiguration, this);
				this.standingModels.put(standingModel.getId(), standingModel);
			}
		}
	}
	
	public void createOperationalModels(ArrayList<OperationalModelConfiguration> operationalModels) {
		
		this.operationalModels = new HashMap<Integer, OperationalModel>();
		
		if(operationalModels != null) {

			for(OperationalModelConfiguration operationalModelConfiguration : operationalModels) {
				
				OperationalModel operationalModel = ModelFactory.getOperationalModelFactory()
						.createModel(operationalModelConfiguration, this);
				this.operationalModels.put(operationalModel.getId(), operationalModel);
			}
		}
	}

	public void createStayingModels(ArrayList<StayingModelConfiguration> stayingModelConfigurations) {
		
		this.stayingModels = new HashMap<Integer, StayingModel>();
		
		if(stayingModelConfigurations != null) {		

			for(StayingModelConfiguration stayingModelConfiguration : stayingModelConfigurations) {
				
				StayingModel stayingModel = ModelFactory.getStayingModelFactory()
						.createModel(stayingModelConfiguration, this);
				this.stayingModels.put(stayingModel.getId(), stayingModel);
			}
		}
	}
	
	public void createQueuingModels(ArrayList<QueuingModelConfiguration> queuingModelConfigurations) {
		
		this.queuingModels = new HashMap<Integer, QueuingModel>();
		
		if(queuingModelConfigurations != null) {		
			
			for(QueuingModelConfiguration queuingModelConfiguration : queuingModelConfigurations) {
				
				QueuingModel queuingModel = ModelFactory.getQueuingModelFactory().createModel(queuingModelConfiguration, this);
				this.queuingModels.put(queuingModel.getId(), queuingModel);
			}
		}
	}
	
	public void createRoutingModels(ArrayList<RoutingModelConfiguration> routingModelConfigurations) {
		
		this.routingModels = new HashMap<Integer, RoutingModel>();
		
		if(routingModelConfigurations != null) {		
			
			for(RoutingModelConfiguration routingModelConfiguration : routingModelConfigurations) {
				
				RoutingModel routingModel = ModelFactory.getRoutingModelFactory().createModel(routingModelConfiguration, this);
				this.routingModels.put(routingModel.getId(), routingModel);
			}
		}
	}
	
	public void createSearchingModels(ArrayList<SearchingModelConfiguration> searchingModelConfigurations) {
		
		this.searchingModels = new HashMap<Integer, SearchingModel>();
		
		if(searchingModelConfigurations != null) {		
			
			for(SearchingModelConfiguration searchingModelConfiguration : searchingModelConfigurations) {
				
				SearchingModel searchingModel = ModelFactory.getSearchingModelFactory().createModel(searchingModelConfiguration, this);
				this.searchingModels.put(searchingModel.getId(), searchingModel);
			}
		}
	}
	
	public void createTacticalModels(ArrayList<TacticalModelConfiguration> tacticalModels) {
		
		this.tacticalModels = new HashMap<Integer, TacticalModel>();
		
		if(tacticalModels != null) {

			for(TacticalModelConfiguration tacticalModelConfiguration : tacticalModels) {
	
				TacticalModel tacticalModel = ModelFactory.getTacticalModelFactory()
						.createModel(tacticalModelConfiguration, this);
				this.tacticalModels.put(tacticalModel.getId(), tacticalModel);
			}
		}
	}

	public void createStrategicalModels(ArrayList<StrategicalModelConfiguration> strategicalModels) {

		this.strategicalModels = new HashMap<Integer, StrategicalModel>();
		this.destinationChoiceModels = new HashMap<>();
		
		if(strategicalModels != null) {

			for(StrategicalModelConfiguration strategicalModelConfiguration : strategicalModels) {
				
				StrategicalModel strategicalModel = ModelFactory.getStrategicalModelFactory()
						.createModel(strategicalModelConfiguration, this);
				
				this.strategicalModels.put(strategicalModel.getId(), strategicalModel);
				this.destinationChoiceModels.put(strategicalModel.getDestinationChoiceModel().getId(),
						strategicalModel.getDestinationChoiceModel());
			}
		}
	}
	
	
	private WriterTarget createWriterTarget(WriterTargetConfiguration writerTargetConfiguration) {

		return ModelFactory.getWriterTargetFactory().createModel(writerTargetConfiguration, this);
	}
	
	private WriterFormat createWriterFormat(WriterFormatConfiguration writerFormatConfiguration) {

		return ModelFactory.getWriterFormatFactory().createModel(writerFormatConfiguration, this);
	}
	
	private WriterSource createWriterSources(WriterSourceConfiguration writerSourceConfiguration) {

		return ModelFactory.getWriterSourceFactory().createModel(writerSourceConfiguration, this);
	}
}
