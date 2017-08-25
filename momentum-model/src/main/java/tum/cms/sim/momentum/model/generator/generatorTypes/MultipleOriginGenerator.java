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

package tum.cms.sim.momentum.model.generator.generatorTypes;

import java.util.ArrayList;
import java.util.Collection;

import tum.cms.sim.momentum.configuration.generator.GeneratorConfiguration.GeneratorType;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.generator.GeneratorGeometryFactory;
import tum.cms.sim.momentum.model.generator.GeneratorIntervalFactory;
import tum.cms.sim.momentum.model.generator.geometry.GeneratorGeometry;

public class MultipleOriginGenerator extends OriginGenerator {
	
	private static String subTypeName = "subType";
	private static String originsName= "origins";
	
	protected Integer scenarioId = null;
	protected ArrayList<SingleOriginGenerator> subGenerators = new ArrayList<>();

	protected GeneratorType generatorType = null;
	
	public MultipleOriginGenerator(int scenarioId) {
		
		this.scenarioId = scenarioId;
	}
	
	@Override
	public void callPreProcessing(SimulationState simulationState) {

		super.callPreProcessing(simulationState);
		
		this.generatorType = GeneratorType.valueOf(this.properties.getStringProperty(subTypeName));
		
		this.intervalSet = GeneratorIntervalFactory.createIntervalSet(this.properties,
				this.generatorType,
				this.generatorStartTime, 
				this.generatorEndTime, 
				this.maximalPedestrians);
		
		ArrayList<Integer> originIds = this.properties.<Integer>getListProperty(originsName);
		
		for(Integer originId : originIds) {
			
			SingleOriginGenerator generator = new SingleOriginGenerator(originId, this.generatorType, this.scenarioId);
			generator.setPropertyBackPack(this.properties);
			generator.setScenarioManager(this.scenarioManager);
			generator.setPedestrianSeed(this.pedestrianSeed);
			generator.setPedestrianManager(this.pedestrianManager);
			
			GeneratorGeometry generatorGeometry = GeneratorGeometryFactory.create(
					this.generatorGeometry.getGeometryType());
			
			generator.setGeneratorGeometry(generatorGeometry);
			generator.callPreProcessing(simulationState);
			
			this.subGenerators.add(generator);
		}
	}
	
	@Override
	public void execute(Collection<? extends Void> splittTask, SimulationState simulationState) {
		
		for(SingleOriginGenerator generator : subGenerators) {
			
			generator.execute(splittTask, simulationState);
		}
	}

	@Override
	public void callPostProcessing(SimulationState simulationState) {

		// nothing to do
	}
}
