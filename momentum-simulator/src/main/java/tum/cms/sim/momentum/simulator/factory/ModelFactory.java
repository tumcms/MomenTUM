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

package tum.cms.sim.momentum.simulator.factory;

import tum.cms.sim.momentum.configuration.generic.PropertyContainerNode;
import tum.cms.sim.momentum.model.IPedestrianBehavioralModel;
import tum.cms.sim.momentum.model.support.perceptional.PerceptionalModel;
import tum.cms.sim.momentum.model.support.query.BasicQueryModel;
import tum.cms.sim.momentum.simulator.component.ComponentManager;
import tum.cms.sim.momentum.simulator.factory.absorberModelFactory.AbsorberFactory;
import tum.cms.sim.momentum.simulator.factory.analysisModelFactory.*;
import tum.cms.sim.momentum.simulator.factory.behaviorModelFactory.*;
import tum.cms.sim.momentum.simulator.factory.generatorModelFactory.*;
import tum.cms.sim.momentum.simulator.factory.layoutModelFactory.*;
import tum.cms.sim.momentum.simulator.factory.metaModelFactory.MetaModelFactory;
import tum.cms.sim.momentum.simulator.factory.outputModelFactory.*;
import tum.cms.sim.momentum.simulator.factory.supportModelFactory.PerceptionalModelFactory;

public abstract class ModelFactory <T extends PropertyContainerNode, K> {

	public static AbsorberFactory getAbsorberFactory() {
		
		return new AbsorberFactory();
	}
	
	public static AnalysisModelFactory getAnalysisModelFactory() {
		
		return new AnalysisModelFactory();
	}
	
	public static MeasureFactory getMeasureFactory() {
		
		return new MeasureFactory();
	}
	
	public static OperationalModelFactory getOperationalModelFactory() {
		
		return new OperationalModelFactory();
	}
	
	public static StayingModelFactory getStayingModelFactory() {
		
		return new StayingModelFactory();
	}

	public static QueuingModelFactory getQueuingModelFactory() {
		
		return new QueuingModelFactory();
	}
	
	public static RoutingModelFactory getRoutingModelFactory() {
		
		return new RoutingModelFactory();
	}
	
	public static SearchingModelFactory getSearchingModelFactory() {
		
		return new SearchingModelFactory();
	}
	
	public static StandingModelFactory getStandingModelFactory() {
		
		return new StandingModelFactory();
	}
	
	public static StrategicalModelFactory getStrategicalModelFactory() {
		
		return new StrategicalModelFactory();
	}
	
	public static TacticalModelFactory getTacticalModelFactory() {
		
		return new TacticalModelFactory();
	}
	
	public static WalkingModelFactory getWalkingModelFactory() {
		
		return new WalkingModelFactory();
	}
	
	public static GeneratorFactory getGeneratorFactory() {
		
		return new GeneratorFactory();
	}
	
	public static PedestrianSeedFactory getPedestrianSeedFactory() {
		
		return new PedestrianSeedFactory();
	}
	
	public static GraphModelFactory getGraphModelFactory() {
		
		return new GraphModelFactory();
	}
	
	public static SpaceSyntaxModelFactory getSpaceSyntaxModelFactory() {
		
		return new SpaceSyntaxModelFactory();
	}
	
	public static LatticeFactory getLatticeFactory() {
		
		return new LatticeFactory();
	}
	
	public static MetaModelFactory getMetaModelFactory() {
		
		return new MetaModelFactory();
	}
	
	public static WriterSourceFactory getWriterSourceFactory() {
		
		return new WriterSourceFactory();
	}
	
	public static WriterTargetFactory getWriterTargetFactory() {
		
		return new WriterTargetFactory();
	}
	
	public static WriterFormatFactory getWriterFormatFactory() {
		
		return new WriterFormatFactory();
	}
	
	public static OutputWriterFactory getOutputWriterFactory() {
		
		return new OutputWriterFactory();
	}
	
	public static PerceptionalModelFactory getPerceptionalModelFactory() {
		
		return new PerceptionalModelFactory();
	}

	protected void fillComposition(IPedestrianBehavioralModel behavioralModel, 
			PerceptionalModel perceptualModel,
			BasicQueryModel queryModel,
			ComponentManager componentManager) {
		
		componentManager.getPedestrianManager().addPedestrianExtenders(behavioralModel);
		
		behavioralModel.setPerceptionalModel(perceptualModel);
		behavioralModel.setQuery(queryModel);
		behavioralModel.setScenario(componentManager.getScenarioManager());	
	}
	
	protected ModelFactory () { };
	
	public abstract K createModel(T configuration, ComponentManager componentManager); 
}
