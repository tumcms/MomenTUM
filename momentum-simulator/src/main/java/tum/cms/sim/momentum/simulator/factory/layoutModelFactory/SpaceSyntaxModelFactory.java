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

package tum.cms.sim.momentum.simulator.factory.layoutModelFactory;

import java.util.ArrayList;
import java.util.Comparator;

import tum.cms.sim.momentum.configuration.model.spaceSyntax.SpaceSyntaxModelConfiguration;
import tum.cms.sim.momentum.configuration.model.spaceSyntax.SpaceSyntaxOperationConfiguration;
import tum.cms.sim.momentum.configuration.scenario.ScenarioConfiguration;
import tum.cms.sim.momentum.model.layout.spaceSyntax.SpaceSyntaxModel;
import tum.cms.sim.momentum.model.layout.spaceSyntax.SpaceSyntaxOperation;
import tum.cms.sim.momentum.model.layout.spaceSyntax.visibilityGraph.VisibilityGraphOperation;
import tum.cms.sim.momentum.simulator.component.ComponentManager;
import tum.cms.sim.momentum.simulator.factory.ModelFactory;
import tum.cms.sim.momentum.utility.generic.PropertyBackPackFactory;
import tum.cms.sim.momentum.utility.generic.Unique;

public class SpaceSyntaxModelFactory extends ModelFactory<SpaceSyntaxModelConfiguration, SpaceSyntaxModel> {

	@Override
	public SpaceSyntaxModel createModel(SpaceSyntaxModelConfiguration configuration, ComponentManager componentManager) {
		
		SpaceSyntaxModel spaceSyntaxModel = new SpaceSyntaxModel();
		
		Unique.generateUnique(spaceSyntaxModel, configuration);
		spaceSyntaxModel.setPropertyBackPack(PropertyBackPackFactory.fillProperties(configuration));
		
		configuration.getSpaceSyntaxOperations().sort(new Comparator<SpaceSyntaxOperationConfiguration>() {
			
			@Override
			public int compare(SpaceSyntaxOperationConfiguration left, SpaceSyntaxOperationConfiguration right) {
				return left.getId().compareTo(right.getId());
			}
		});
		
		for(SpaceSyntaxOperationConfiguration spaceSyntaxOperationConfiguration : configuration.getSpaceSyntaxOperations()) {
			
			SpaceSyntaxOperation operation = this.createOperation(
					componentManager.getConfigurationManager().getSimulatorConfiguration().getLayouts(),
					spaceSyntaxOperationConfiguration);
			
			Unique.generateUnique(operation, spaceSyntaxOperationConfiguration);
			operation.setPropertyBackPack(PropertyBackPackFactory.fillProperties(spaceSyntaxOperationConfiguration));
			operation.setScenario(componentManager.getScenarioManager());
			spaceSyntaxModel.getOperations().add(operation);
		}
		
		return spaceSyntaxModel;
	}

	private SpaceSyntaxOperation createOperation(ArrayList<ScenarioConfiguration> scenarioConfigurations,
			SpaceSyntaxOperationConfiguration spaceSyntaxOperationConfiguration) {
		
		SpaceSyntaxOperation spaceSyntaxOperation = null;
		
		switch (spaceSyntaxOperationConfiguration.getType()) {
		case VisibilityGraph:
			spaceSyntaxOperation = new VisibilityGraphOperation();
		}
		
		return spaceSyntaxOperation;
	}
}
