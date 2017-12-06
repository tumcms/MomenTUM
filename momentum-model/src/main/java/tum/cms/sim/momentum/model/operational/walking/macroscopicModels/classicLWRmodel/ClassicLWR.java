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

package tum.cms.sim.momentum.model.operational.walking.macroscopicModels.classicLWRmodel;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import tum.cms.sim.momentum.data.agent.pedestrian.types.IOperationalPedestrian;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrianExtension;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.operational.walking.WalkingModel;
import tum.cms.sim.momentum.model.operational.walking.macroscopicModels.data.Density;
import tum.cms.sim.momentum.model.operational.walking.macroscopicModels.data.Intersection;
import tum.cms.sim.momentum.model.operational.walking.macroscopicModels.data.MacroscopicAbsorber;
import tum.cms.sim.momentum.model.operational.walking.macroscopicModels.data.MacroscopicEdge;
import tum.cms.sim.momentum.model.operational.walking.macroscopicModels.data.MacroscopicGenerator;
import tum.cms.sim.momentum.model.operational.walking.macroscopicModels.data.MacroscopicNetwork;
import tum.cms.sim.momentum.model.operational.walking.macroscopicModels.data.MacroscopicNode;
import tum.cms.sim.momentum.model.operational.walking.macroscopicModels.data.MacroscopicNode.NodeType;
import tum.cms.sim.momentum.model.operational.walking.macroscopicModels.distributor.DistributorFactory;
import tum.cms.sim.momentum.model.operational.walking.macroscopicModels.distributor.FixedDistributor;
import tum.cms.sim.momentum.model.operational.walking.macroscopicModels.distributor.DistributorFactory.DistributorType;
import tum.cms.sim.momentum.model.operational.walking.macroscopicModels.solver.SolverFactory;
import tum.cms.sim.momentum.model.operational.walking.macroscopicModels.solver.StructuredModel;
import tum.cms.sim.momentum.model.operational.walking.macroscopicModels.solver.SolverFactory.SolverType;
import tum.cms.sim.momentum.model.operational.walking.macroscopicModels.velocityModel.VFFModel;
import tum.cms.sim.momentum.model.operational.walking.macroscopicModels.velocityModel.VelocityModelFactory;
import tum.cms.sim.momentum.model.operational.walking.macroscopicModels.velocityModel.VelocityModelFactory.VelocityModelType;
import tum.cms.sim.momentum.utility.geometry.GeometryFactory;
import tum.cms.sim.momentum.utility.geometry.Vector2D;


public class ClassicLWR extends WalkingModel {
	
	private ArrayList<MacroscopicNode> allScenarioNodes = new ArrayList<MacroscopicNode>();
	private ArrayList<MacroscopicEdge> allScenarioEdges = new ArrayList<MacroscopicEdge>();
	private ArrayList<MacroscopicGenerator> allMacroscopicGenerators = new ArrayList<MacroscopicGenerator>();
	
	private FixedDistributor distributor = (FixedDistributor) DistributorFactory.getDistributor(DistributorType.FixedDistributor);
	private StructuredModel solver = (StructuredModel) SolverFactory.getSolver(SolverType.StructuredModel);
	private VFFModel pedestrianModel = (VFFModel) VelocityModelFactory.getVelocityModel(VelocityModelType.VffPedestrian);
	
	private MacroscopicNetwork newNetwork = new MacroscopicNetwork(distributor, solver, pedestrianModel);
	
	
	@Override
	public void callPreProcessing(SimulationState simulationState) {
		
		HashMap<Integer, ArrayList<Double>> hashGenerators = this.getPropertyBackPack().getMatrixProperty("Generators");
		
		for (ArrayList<Double> hashGeneratorValue : hashGenerators.values()) {
			
			Integer id = hashGeneratorValue.get(0).intValue();
			Double totalNumberOfPedestrians = hashGeneratorValue.get(1);
			
			Integer sizeOfHashGeneratorValue = hashGeneratorValue.size();
			ArrayList<Pair<Double, Double>> generatorIntervals = new ArrayList<Pair<Double, Double>>();
			
			for (int index = 2; index < sizeOfHashGeneratorValue; index = index + 2 ) {
				
				Pair<Double, Double> generatorElement = Pair.of(hashGeneratorValue.get(index), hashGeneratorValue.get(index+1));
				
				generatorIntervals.add(generatorElement);
			}
			MacroscopicGenerator generator = new MacroscopicGenerator(id, totalNumberOfPedestrians, generatorIntervals, simulationState.getSimulationEndTime(), pedestrianModel);
			allMacroscopicGenerators.add(generator);
		}
		
		HashMap<Integer, ArrayList<Double>> hashNodes = this.getPropertyBackPack().getMatrixProperty("Nodes");

		for (ArrayList<Double> hashNodeValue : hashNodes.values()) {
					
			Integer id = hashNodeValue.get(0).intValue();
			Vector2D position = GeometryFactory.createVector(hashNodeValue.get(1), hashNodeValue.get(2));
			
			Integer originId = hashNodeValue.get(3).intValue();
			Integer destinationId = hashNodeValue.get(4).intValue();
			Boolean isOrigin = originId < 0 ? false : true;
			Boolean isDestination = destinationId < 0 ? false : true;
			
			MacroscopicNode node;
			
			if (isOrigin) {
				node = new Intersection(id, position,NodeType.ENTRY);
				MacroscopicGenerator relatedGenerator = allMacroscopicGenerators.stream().filter(generator -> generator.getId() == originId).findAny().get();
				node.createOrigin(relatedGenerator);
			}
			else if (isDestination) {
				node = new Intersection(id, position,NodeType.EXIT);
				MacroscopicAbsorber relatedAbsorber = new MacroscopicAbsorber(destinationId);
				node.createDestination(relatedAbsorber);
			}
			else {
				node = new Intersection(id, position,NodeType.INTERSECTION);
			}
			allScenarioNodes.add(node);			
		}
		
		HashMap<Integer, ArrayList<Double>> hashEdges = this.getPropertyBackPack().getMatrixProperty("Edges");
		
		for (ArrayList<Double> hashEdgeValue : hashEdges.values()) {
			
			Integer id = hashEdgeValue.get(0).intValue();
			MacroscopicNode firstNode = allScenarioNodes.stream().filter(node -> node.getId() == hashEdgeValue.get(1).intValue()).findAny().get();
			MacroscopicNode secondNode = allScenarioNodes.stream().filter(node -> node.getId() == hashEdgeValue.get(2).intValue()).findAny().get();
			Double width = hashEdgeValue.get(3);
			Double maximalDensity = hashEdgeValue.get(4);
			Double startDensity = hashEdgeValue.get(5);
			
			MacroscopicEdge edge = new MacroscopicEdge(id, firstNode, secondNode, width, maximalDensity, startDensity);
			allScenarioEdges.add(edge);
		}
		
		newNetwork.setUpNetwork(allScenarioEdges, allScenarioNodes, distributor);
		
		solver.setDt(simulationState.getTimeStepDuration());  // time step
		solver.setDxapprox(0.2);  //
		
		Double peopleIn = 0.0;
		Density InitialPeople = new Density(peopleIn, pedestrianModel, newNetwork.getEndNodes().size());
		MacroscopicNode startNode = newNetwork.getStartNodes().get(0);
		startNode.setDensity(InitialPeople);
		double newDestination[] = new double[newNetwork.getEndNodes().size()];
		newDestination[0] = 1.0;
		startNode.getDensity().setDestination(newDestination);
		startNode.setIsComputed(true);
	}

	@Override
	public IPedestrianExtension onPedestrianGeneration(IRichPedestrian pedestrian) {
		return null;

	}

	@Override
	public void onPedestrianRemoval(IRichPedestrian pedestrian) {

	}

	@Override
	public void callPedestrianBehavior(IOperationalPedestrian pedestrian, SimulationState simulationState) {
		
	}

	@Override
	public void callPostProcessing(SimulationState simulationState) { }

	@Override
	public void callBeforeBehavior(SimulationState simulationState, Collection<IRichPedestrian> pedestrians) {

		allMacroscopicGenerators.get(0).generatePedestriansForTimestep(
				simulationState.getCurrentTime(), 
				newNetwork.getStartNodes().get(0), 
				simulationState.getTimeStepDuration());
		
		distributor.computeWeights(newNetwork);
		newNetwork.resetIsComputed();
		
		newNetwork.startComputation(newNetwork.getStartNodes());

		//then  set the current density
		for (MacroscopicEdge edge:newNetwork.getMacroscopicEdges()) {
			
			
			edge.setCurrentDensity(edge.getPeople() / (edge.getWidth() * edge.getLength()));
		}
	}

	@Override
	public void callAfterBehavior(SimulationState simulationState, Collection<IRichPedestrian> pedestrians) {
		// TODO Auto-generated method stub
		
	}

	public List<MacroscopicEdge> getMacroscopicEdges() {
		
		return allScenarioEdges;
	}
}
