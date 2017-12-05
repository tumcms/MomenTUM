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

package tum.cms.sim.momentum.simulator.factory.outputModelFactory;

import tum.cms.sim.momentum.configuration.model.output.WriterSourceConfiguration;
import tum.cms.sim.momentum.data.layout.ScenarioManager;
import tum.cms.sim.momentum.model.IPedestrianBehavioralModel;
import tum.cms.sim.momentum.model.absorber.Absorber;
import tum.cms.sim.momentum.model.absorber.AbsorberWriterSource;
import tum.cms.sim.momentum.model.analysis.AnalysisModel;
import tum.cms.sim.momentum.model.meta.transitum.TransiTumModel;
import tum.cms.sim.momentum.model.meta.transitum.multiscaleOutputSource.TransitZonesOutputSource;
import tum.cms.sim.momentum.model.operational.walking.csvPlackback.CsvPlaybackOperational;
import tum.cms.sim.momentum.model.operational.walking.csvPlackback.CsvPlaybackWriterSource;
import tum.cms.sim.momentum.model.operational.walking.macroscopicModels.classicLWRmodel.ClassicLWR;
import tum.cms.sim.momentum.model.operational.walking.macroscopicModels.classicLWRmodel.ClassicLWROutputSource;
import tum.cms.sim.momentum.model.operational.walking.socialForceModel.SocialForceWriterSource;
import tum.cms.sim.momentum.model.operational.walking.socialForceModel.ZengOperational.ZengSocialForceWriterSource;
import tum.cms.sim.momentum.model.output.writerSources.WriterSource;
import tum.cms.sim.momentum.model.output.writerSources.specificWriterSources.AnalysisWriterSource;
import tum.cms.sim.momentum.model.output.writerSources.specificWriterSources.CarWriterSource;
import tum.cms.sim.momentum.model.output.writerSources.specificWriterSources.ConfigurationWriterSource;
import tum.cms.sim.momentum.model.output.writerSources.specificWriterSources.PedestrianWriterSource;
import tum.cms.sim.momentum.model.output.writerSources.specificWriterSources.SpaceSyntaxWriterSource;
import tum.cms.sim.momentum.model.output.writerSources.specificWriterSources.TimeWriterSource;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.CognitiveSpatialChoiceOutputSource;
import tum.cms.sim.momentum.model.tactical.routing.unifiedRoutingModel.UnifiedRoutingOutputSource;
import tum.cms.sim.momentum.simulator.component.ComponentManager;
import tum.cms.sim.momentum.simulator.factory.ModelFactory;
import tum.cms.sim.momentum.utility.generic.PropertyBackPackFactory;
import tum.cms.sim.momentum.utility.generic.Unique;

public class WriterSourceFactory extends ModelFactory<WriterSourceConfiguration, WriterSource>{
	
	@Override
	public WriterSource createModel(WriterSourceConfiguration configuration, ComponentManager componentManager) {
		
		WriterSource writerSource = null;

		switch(configuration.getSourceType()) {
		
		case Configuration:
			
			ConfigurationWriterSource configurationSource = new ConfigurationWriterSource();
			configurationSource.setConfigurationManager(componentManager.getConfigurationManager());
			writerSource = configurationSource;
			break;
			
		case Pedestrian:
			
			PedestrianWriterSource pedestrianSource = new PedestrianWriterSource();
			pedestrianSource.setPedestrianManager(componentManager.getPedestrianManager());
			pedestrianSource.setTimeManager(componentManager.getTimeManager());
			writerSource = pedestrianSource;
			break;
			
		case Car:
			CarWriterSource carSource = new CarWriterSource();
			carSource.setCarManager(componentManager.getCarManager());
			carSource.setTimeManager(componentManager.getTimeManager());
			writerSource = carSource;
			break;
			
		case BarnesHut_SocialForce_Pedestrian:
			SocialForceWriterSource socialForceSource = new SocialForceWriterSource();
			socialForceSource.setPedestrianManager(componentManager.getPedestrianManager());
			socialForceSource.setTimeManager(componentManager.getTimeManager());
			
			IPedestrianBehavioralModel barnesHut = componentManager.getWalkingModel(configuration.getAdditionalId());
			socialForceSource.setPedetrianBehavioralModel(barnesHut);
			
			writerSource = socialForceSource;
			break;

		case Zeng_SocialForce_Pedestrian:
			ZengSocialForceWriterSource zengSocialForceSource = new ZengSocialForceWriterSource();
			zengSocialForceSource.setPedestrianManager(componentManager.getPedestrianManager());
			zengSocialForceSource.setTimeManager(componentManager.getTimeManager());

			IPedestrianBehavioralModel zeng = componentManager.getWalkingModel(configuration.getAdditionalId());
			zengSocialForceSource.setPedetrianBehavioralModel(zeng);

			writerSource = zengSocialForceSource;
			break;
			
		case Time:
			
			TimeWriterSource timeStepSource = new TimeWriterSource();
			timeStepSource.setTimeManager(componentManager.getTimeManager());
			writerSource = timeStepSource;
			break;
			
		case Analysis:
	
			AnalysisWriterSource analysisOutputSource = new AnalysisWriterSource();
			AnalysisModel analysisModel = componentManager.getAnalysisModel(configuration.getAdditionalId());
			analysisOutputSource.setAnalysisModel(analysisModel);
			analysisOutputSource.setTimeManager(componentManager.getTimeManager());
			writerSource = analysisOutputSource;
			break;
			
		case SpaceSyntax:
			
			SpaceSyntaxWriterSource spaceSyntaxWriterSource = new SpaceSyntaxWriterSource();
			
			ScenarioManager scenarioManager = componentManager.getScenarioManager();
			spaceSyntaxWriterSource.setScenarioManager(scenarioManager);

			Integer additionalId = configuration.getAdditionalId();
			spaceSyntaxWriterSource.setAdditionalId(additionalId);
			
			writerSource = spaceSyntaxWriterSource;
			break;
			
		case UPRM_Pedestrian:

			UnifiedRoutingOutputSource uprmSource = new UnifiedRoutingOutputSource();
			uprmSource.setPedestrianManager(componentManager.getPedestrianManager());
			uprmSource.setTimeManager(componentManager.getTimeManager());
			
			IPedestrianBehavioralModel uprmModel = componentManager.getRoutingModel(configuration.getAdditionalId());
			uprmSource.setPedetrianBehavioralModel(uprmModel);
			
			writerSource = uprmSource;
			break;
		
		case CSC:
			
			CognitiveSpatialChoiceOutputSource cscSource = new CognitiveSpatialChoiceOutputSource();
			cscSource.setPedestrianManager(componentManager.getPedestrianManager());
			cscSource.setTimeManager(componentManager.getTimeManager());
			
			IPedestrianBehavioralModel cscModel = componentManager.getDestinationChoiceModel(configuration.getAdditionalId());
			cscSource.setPedetrianBehavioralModel(cscModel);
			
			writerSource = cscSource;
			break; 
			
		case TransitZones:
			
			TransitZonesOutputSource transitSource = new TransitZonesOutputSource();		
			transitSource.setTimeManager(componentManager.getTimeManager());
			
			TransiTumModel metaModel = (TransiTumModel) componentManager.getMetaModel(configuration.getAdditionalId());
			transitSource.setCallable(metaModel);
			
			writerSource = transitSource;
			break;
			
		case Absorber:
			
			AbsorberWriterSource absorberSource = new AbsorberWriterSource();
			absorberSource.setTimeManager(componentManager.getTimeManager());
			
			Absorber absorber = componentManager.getAbsorber(configuration.getAdditionalId());
			absorberSource.setCallable(absorber);
			
			writerSource = absorberSource;
			break;
			
		case CSC_Pedestrian:
			// TODO
			break;
			
		case ClassicLWR:
			
			ClassicLWROutputSource classicLWRSource = new ClassicLWROutputSource();
			classicLWRSource.setTimeManager(componentManager.getTimeManager());
			
			ClassicLWR classicLWRModel = (ClassicLWR) componentManager.getWalkingModel(configuration.getAdditionalId());
			classicLWRSource.setCallable(classicLWRModel);
			
			writerSource = classicLWRSource;			
			break;

		case CsvPlayback:

			CsvPlaybackWriterSource csvPlaybackWriterSource = new CsvPlaybackWriterSource();
			csvPlaybackWriterSource.setTimeManager(componentManager.getTimeManager());
			csvPlaybackWriterSource.setPedestrianManager(componentManager.getPedestrianManager());

			CsvPlaybackOperational csvPlaybackOperational = (CsvPlaybackOperational) componentManager.getWalkingModel(configuration.getAdditionalId());
			csvPlaybackWriterSource.setPedetrianBehavioralModel(csvPlaybackOperational);

			writerSource = csvPlaybackWriterSource;
			break;

		default:
			break;
		}
		
		Unique.generateUnique(writerSource, configuration);
		writerSource.setPropertyBackPack(PropertyBackPackFactory.fillProperties(configuration));

		return writerSource;
	}
}

