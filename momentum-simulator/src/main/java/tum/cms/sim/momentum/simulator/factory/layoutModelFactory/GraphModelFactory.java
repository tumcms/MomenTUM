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

import tum.cms.sim.momentum.configuration.model.graph.GraphModelConfiguration;
import tum.cms.sim.momentum.configuration.model.graph.GraphOperationConfiguration;
import tum.cms.sim.momentum.configuration.scenario.ScenarioConfiguration;
import tum.cms.sim.momentum.model.layout.graph.GraphModel;
import tum.cms.sim.momentum.model.layout.graph.GraphOperation;
import tum.cms.sim.momentum.model.layout.graph.edge.edgeCreateOvermarsModel.EdgeCreateOvermarsModel;
import tum.cms.sim.momentum.model.layout.graph.edge.edgeDeleteOneWayModel.EdgeDeleteOnWayModel;
import tum.cms.sim.momentum.model.layout.graph.edge.edgeDeleteUnreachablesModel.EdgeDeleteUnreachablesModel;
import tum.cms.sim.momentum.model.layout.graph.edge.edgeMinimumSpanningTreeModel.EdgeMinimumSpanningTreeModel;
import tum.cms.sim.momentum.model.layout.graph.edge.edgeVisibilityModel.EdgeVisibilityAngleBasedModel;
import tum.cms.sim.momentum.model.layout.graph.edge.edgeVisibilityModel.EdgeVisibilityAngleBasedReducedModel;
import tum.cms.sim.momentum.model.layout.graph.edge.edgeVisibilityModel.EdgeVisibilityModel;
import tum.cms.sim.momentum.model.layout.graph.raw.FromConfigurationOperation;
import tum.cms.sim.momentum.model.layout.graph.raw.RawGraphOperation;
import tum.cms.sim.momentum.model.layout.graph.raw.ToConfigurationOperation;
import tum.cms.sim.momentum.model.layout.graph.vertex.vertexCornerModel.VertexCornerModel;
import tum.cms.sim.momentum.model.layout.graph.vertex.vertexCornerModel.VertexCornerModelEnriched;
import tum.cms.sim.momentum.model.layout.graph.vertex.vertexIntersectionModel.VertexIntersectionModel;
import tum.cms.sim.momentum.model.layout.graph.vertex.vertexMedialAxisModel.VertexMedialAxisModel;
import tum.cms.sim.momentum.model.layout.graph.vertex.vertexPruneModel.VertexLineRemoveModel;
import tum.cms.sim.momentum.model.layout.graph.vertex.vertexPruneModel.VertexSimplePruneModel;
import tum.cms.sim.momentum.model.layout.graph.vertex.vertexPruneModel.VertexVisibilityPruneModel;
import tum.cms.sim.momentum.model.layout.graph.vertex.vertexSeedModel.VertexSeedModel;
import tum.cms.sim.momentum.model.layout.graph.vertex.vertexVoronoiModel.VertexVoronoiModel;
import tum.cms.sim.momentum.simulator.component.ComponentManager;
import tum.cms.sim.momentum.simulator.factory.ModelFactory;
import tum.cms.sim.momentum.utility.generic.PropertyBackPackFactory;
import tum.cms.sim.momentum.utility.generic.Unique;

public class GraphModelFactory extends ModelFactory<GraphModelConfiguration, GraphModel>{
	
	
	@Override
	public GraphModel createModel(GraphModelConfiguration configuration, ComponentManager componentManager) {

		GraphModel graphModel = new GraphModel();
		
		Unique.generateUnique(graphModel, configuration);
		graphModel.setPropertyBackPack(PropertyBackPackFactory.fillProperties(configuration));
		
		configuration.getGraphOperations().sort(new Comparator<GraphOperationConfiguration>() {

			@Override
			public int compare(GraphOperationConfiguration left, GraphOperationConfiguration right) {
				return left.getId().compareTo(right.getId());
			}
		});

		for(GraphOperationConfiguration graphOperationConfiguration : configuration.getGraphOperations()) {
			
			GraphOperation operation = this.createOperation(
					componentManager.getConfigurationManager().getSimulatorConfiguration().getLayouts(),
					graphOperationConfiguration);
			
			Unique.generateUnique(operation, graphOperationConfiguration);
			operation.setPropertyBackPack(PropertyBackPackFactory.fillProperties(graphOperationConfiguration));
			operation.setScenario(componentManager.getScenarioManager());
			graphModel.getOperations().add(operation);
		}
		
		return graphModel;
	}

	
	private GraphOperation createOperation(ArrayList<ScenarioConfiguration> scenarioConfigurations,
			GraphOperationConfiguration graphOperationConfiguration) {
		
		GraphOperation graphOperation = null;
		
		switch (graphOperationConfiguration.getType()) {
		case VertexCreateSeedBased:
			graphOperation = new VertexSeedModel();
			break;
			
		case VertexCreateAtCorners:
			graphOperation = new VertexCornerModel();
			break;
			
		case VertexCreateAtCornersEnriched:
			graphOperation = new VertexCornerModelEnriched();
			break;
			
		case VertexCreateVoronoiBased:
			graphOperation = new VertexVoronoiModel();
			break;
			
		case VertexCreateMedialAxis:
			graphOperation = new VertexMedialAxisModel();
			break;
			
		case VertexCreateAtIntersections:
			graphOperation = new VertexIntersectionModel();
			break;
			
		case VertexRemoveVisibilityBased:
			graphOperation = new VertexVisibilityPruneModel();
			break;
			
		case VertexRemoveSimple:
			graphOperation = new VertexSimplePruneModel();
			break;
			
		case VertexRemoveAlongLine:
			graphOperation = new VertexLineRemoveModel();
			break;
			
		case EdgeCreateVisibilityConnect:
			graphOperation = new EdgeVisibilityModel();
			break;
			
		case EdgeCreateVisibilityAngleBased:
			graphOperation = new EdgeVisibilityAngleBasedModel();
			break;
			
		case EdgeCreateVisibilityAngleBasedReduced:
			graphOperation = new EdgeVisibilityAngleBasedReducedModel();
			break;
			
		case EdgeCreateOvermarsUseful:
			graphOperation = new EdgeCreateOvermarsModel();
			break;
			
		case EdgeRemoveMST:
			graphOperation = new EdgeMinimumSpanningTreeModel();
			break;
			
		case EdgeRemoveUnreachable:
			graphOperation = new EdgeDeleteUnreachablesModel();
			break;
	
		case EdgeRemoveOneWay:
			graphOperation = new EdgeDeleteOnWayModel();
			break;
			
		case RawGraph:
			graphOperation = new RawGraphOperation();
			break;
			
		case ToConfiguration:		
			graphOperation = new ToConfigurationOperation(scenarioConfigurations);
			break;
			
		case FromConfiguration:
			graphOperation = new FromConfigurationOperation(scenarioConfigurations);
			break;
				
		default:
			break;
		}
		
		return graphOperation;
	}
}
