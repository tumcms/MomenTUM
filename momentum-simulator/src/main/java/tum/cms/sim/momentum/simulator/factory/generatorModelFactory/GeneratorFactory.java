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

package tum.cms.sim.momentum.simulator.factory.generatorModelFactory;

import tum.cms.sim.momentum.configuration.generator.GeneratorConfiguration;
import tum.cms.sim.momentum.model.generator.Generator;
import tum.cms.sim.momentum.model.generator.GeneratorGeometryFactory;
import tum.cms.sim.momentum.model.generator.generatorTypes.CarNetworkGenerator;
import tum.cms.sim.momentum.model.generator.generatorTypes.CsvGenerator;
import tum.cms.sim.momentum.model.generator.generatorTypes.CarCsvGenerator;
import tum.cms.sim.momentum.model.generator.generatorTypes.InstantGenerator;
import tum.cms.sim.momentum.model.generator.generatorTypes.MultipleOriginGenerator;
import tum.cms.sim.momentum.model.generator.generatorTypes.SingleOriginGenerator;
import tum.cms.sim.momentum.model.generator.geometry.GeneratorGeometry;
import tum.cms.sim.momentum.simulator.component.ComponentManager;
import tum.cms.sim.momentum.simulator.factory.ModelFactory;
import tum.cms.sim.momentum.utility.generic.PropertyBackPackFactory;
import tum.cms.sim.momentum.utility.generic.Unique;

public class GeneratorFactory extends ModelFactory<GeneratorConfiguration, Generator> {

	@Override
	public Generator createModel(GeneratorConfiguration configuration, ComponentManager componentManager) {

		Generator generator = null;
		
		switch(configuration.getType()) {
		
		case External:
		case Distribution:
		case Plan:
		case Stock:
			
			SingleOriginGenerator singleGenerator = new SingleOriginGenerator(configuration.getOrigin(), configuration.getType(), configuration.getScenario());
			
			GeneratorGeometry generatorGeometry = GeneratorGeometryFactory.create(
					configuration.getGeometry().getGeometryType());
			
			singleGenerator.setGeneratorGeometry(generatorGeometry);
			generator = singleGenerator;
			
			break;

		case Multiple:
			MultipleOriginGenerator multipleOriginGenerator = new MultipleOriginGenerator(configuration.getScenario());
			
			generatorGeometry = GeneratorGeometryFactory.create(
					configuration.getGeometry().getGeometryType());
			
			multipleOriginGenerator.setGeneratorGeometry(generatorGeometry);
			generator = multipleOriginGenerator;
			
			break;
			
		case Instant:
			generator = new InstantGenerator();
			break;
			
		case Csv:
			generator = new CsvGenerator();
			break;
			
		case CarNetwork:
			CarNetworkGenerator carNetworkGenerator = new CarNetworkGenerator();
			carNetworkGenerator.setCarManager(componentManager.getCarManager());
			generator = carNetworkGenerator;
			
			break;
			
		case CarCsv:
			CarCsvGenerator carCsvGenerator = new CarCsvGenerator();
			carCsvGenerator.setCarManager(componentManager.getCarManager());
			generator = carCsvGenerator;
			
			break;
			
		default:
			break;
		}

		generator.setPedestrianManager(componentManager.getPedestrianManager());
		generator.setScenarioManager(componentManager.getScenarioManager());
		generator.setPedestrianSeed(componentManager.getPedestrianSeed(configuration.getSeed()));
		
		Unique.generateUnique(generator, configuration);
		generator.setPropertyBackPack(PropertyBackPackFactory.fillProperties(configuration));
		
		return generator;
	}
}
