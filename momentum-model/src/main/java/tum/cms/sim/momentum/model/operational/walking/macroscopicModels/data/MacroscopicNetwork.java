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

package tum.cms.sim.momentum.model.operational.walking.macroscopicModels.data;

import java.util.ArrayList;
import javax.swing.JOptionPane;

import tum.cms.sim.momentum.model.operational.walking.macroscopicModels.data.MacroscopicEdge.MacroscopicEdgeType;
import tum.cms.sim.momentum.model.operational.walking.macroscopicModels.data.MacroscopicNode.NodeType;
import tum.cms.sim.momentum.model.operational.walking.macroscopicModels.distributor.IDistributor;
import tum.cms.sim.momentum.model.operational.walking.macroscopicModels.solver.ISolver;
import tum.cms.sim.momentum.model.operational.walking.macroscopicModels.velocityModel.IVelocityModel;

public class MacroscopicNetwork {
	
	private ArrayList<MacroscopicEdge> edges = new ArrayList<MacroscopicEdge>();
	private ArrayList<MacroscopicNode> nodes = new ArrayList<MacroscopicNode>();
	
	private IDistributor distributor;
	private ISolver solver;
	private IVelocityModel velocityModel;
	
	private ArrayList<Intersection> startNodes = new ArrayList<Intersection>();
	private ArrayList<Intersection> endNodes = new ArrayList<Intersection>();

	public MacroscopicNetwork(IDistributor distributor, ISolver solver, IVelocityModel velocityModel) {
		
		setDistributor(distributor);
		setSolver(solver);
		setVelocityModel(velocityModel);
		
		edges.clear();
		nodes.clear();
		startNodes.clear();
		endNodes.clear();
	}
	
	public IDistributor getDistributor() {
		return distributor;
	}

	public void setDistributor(IDistributor distributor) {
		this.distributor = distributor;
	}

	public ISolver getSolver() {
		return solver;
	}

	public void setSolver(ISolver solver) {
		this.solver = solver;
	}
	
	public IVelocityModel getVelocityModel() {
		return velocityModel;
	}
	
	public void setVelocityModel(IVelocityModel velocityModel) {
		this.velocityModel = velocityModel;
	}

	public void addMacroscopicEdge(MacroscopicEdge edge) {
		
		edges.add(edge);
	}
	
	public void removeMacroscopicEdge(MacroscopicEdge edge) {
		edges.remove(edge);
	}
	
	public ArrayList<MacroscopicEdge> getMacroscopicEdges() {
		return edges;
	}
	
	public void addNode(MacroscopicNode node) {
		
		nodes.add(node);
		
		switch (node.getType()) {
		
		case ENTRY:
			startNodes.add((Intersection)node);
			break;
			
		case EXIT:
			endNodes.add((Intersection)node);
			break;
			
		case INTERSECTION:
			break;
			
		default:
			break;
		}	
	}
	
	public void removeNode(MacroscopicNode node) {
		nodes.remove(node);
	}
	
	public ArrayList<MacroscopicNode> getNodes() {
		return nodes;
	}
	
	public ArrayList<Intersection> getStartNodes() {
		return startNodes;
	}

	public void setStartNodes(ArrayList<Intersection> startNodes) {
		this.startNodes = startNodes;
	}

	public ArrayList<Intersection> getEndNodes() {
		return endNodes;
	}

	public void setEndNodes(ArrayList<Intersection> endNodes) {
		this.endNodes = endNodes;
	}

	public void clearNetwork() {
		this.edges.clear();
		this.nodes.clear();
		this.startNodes.clear();
		this.endNodes.clear();
	}
	
	private void traverse(MacroscopicNode currentNode) {
		
		for(int i=0; i < currentNode.getIn().size(); i++) {
			
			if(currentNode.getIn().get(i).getIsComputed() != true) {
				return;
			}
		}
		
		distributor.distribute(currentNode, this, solver.getDxapprox());

		currentNode.setIsComputed(true);
		
		for(int i=0; i < currentNode.getOut().size(); i++) {
				traverse(currentNode.getOut().get(i));
		}
	}

	private void traverse(MacroscopicEdge currentEdge) {

		if(!currentEdge.getSecondNode().isFull()) {
			
			double endDensity = currentEdge.getDensity()[currentEdge.getDivisions() - 2].getAmount();
			//if there is density, add it to the end node and delete it in the edge afterwards
			if(endDensity > 0) {

				currentEdge.getSecondNode().addDensity(currentEdge.getDensity()[currentEdge.getDivisions() - 2].getScaledDensity(currentEdge.getWidth() * solver.getDxapprox()));
				
				currentEdge.deleteEndDensity();
			}
		}
		
		solver.solve(currentEdge, this.getVelocityModel());
		
		if(!currentEdge.getSecondNode().isFull()) {
			
			Density endDensity = currentEdge.getDensity()[currentEdge.getDivisions() - 2];

			if(endDensity.getAmount() > 0) {

				currentEdge.getSecondNode().addDensity(endDensity.getScaledDensity(currentEdge.getWidth() * solver.getDxapprox()));
				
				currentEdge.deleteEndDensity();
			}
		}
		currentEdge.setIsComputed(true);
		traverse(currentEdge.getSecondNode());
	}
	
	public void startComputation(ArrayList<Intersection> startNodes) {
		
		for (MacroscopicNode startNode:startNodes) {
			
			traverse(startNode);
		}
	}
	
	public void resetIsComputed() {
		
		for (int i=0; i<this.getMacroscopicEdges().size(); i++) {
			this.getMacroscopicEdges().get(i).setIsComputed(false);
		}
	}
	
	public void setUpMacroscopicEdgeTypes() {
		
		for (MacroscopicEdge e:this.edges) {
			if (e.getType() == null) {
				e.setType(MacroscopicEdgeType.WALKWAY);
			}
		}
	}
	
	public void setUpNetwork(ArrayList<MacroscopicEdge> edgeList, 
			ArrayList<MacroscopicNode> nodeList, IDistributor distributor) {
		
		this.setDistributor(distributor);
		
		nodes = nodeList;
		
		for (MacroscopicNode n:nodes) {
			if (n.getType() == NodeType.ENTRY) {
				startNodes.add((Intersection) n);
			}
			else if (n.getType() == NodeType.EXIT) {
				endNodes.add((Intersection) n);
			}
			else {		
			}
		}

		for (MacroscopicNode n:nodes) {
			n.setDensity(new Density(0, this.getVelocityModel(), this.getEndNodes().size()));
			double nodeDestination[] = new double[getEndNodes().size()];
			nodeDestination[0] = 1.0; ///TODO : set different destinations here 
			n.getDensity().setDestination(nodeDestination);
		}
		
		edges = edgeList;
		
		for (MacroscopicEdge edge:edges) {
			Integer divisions = (int) (edge.getLength() / solver.getDxapprox() + 1); //TODO : is +1 necessary
			edge.setDivisions(divisions);
			Density[] density = new Density[divisions];
			for (int j=0; j<divisions; j++) {
				
				density[j] = new Density(edge.getCurrentDensity(), this.getVelocityModel(), this.getEndNodes().size());
				double edgeDestination[] = new double[getEndNodes().size()];
				edgeDestination[0] = 1.0; ///TODO : set different destinations here 
				density[j].setDestination(edgeDestination);
			}
			edge.setDensity(density);
		}
		if (!checkNetwork()) {
			clearNetwork();
			return;
		}
		setUpMacroscopicEdgeTypes();
		
		for (MacroscopicNode node:nodes) {
			node.computeArea();
		}
	}	
	
	public void initialCondition(MacroscopicNode startNode, double newAmount, double[] newDestination) {
		
		startNode.setDensity(new Density(newAmount, this.getVelocityModel(), newDestination));	
	}
	
	public boolean checkNetwork() {
		boolean result = true;	
		
		//does the start node only have outgoing edges?
		for (int i=0; i<this.getStartNodes().size(); i++) {
			if (!this.getStartNodes().get(i).getIn().isEmpty()) {
				JOptionPane.showMessageDialog(null,"One start node has incoming edges","Error" , JOptionPane.ERROR_MESSAGE);
				result = false;
			}
		}	
		for (int i=0; i<this.getEndNodes().size(); i++) {
			
			if (!this.getEndNodes().get(i).getOut().isEmpty()) {
				JOptionPane.showMessageDialog(null,"One end node has outgoing edges","Error" , JOptionPane.ERROR_MESSAGE);
				result = false;
			}
		}	
		int alone = 0;
		for(int i=0; i<this.nodes.size(); i++) {
			if (this.nodes.get(i).getIn().isEmpty() && this.nodes.get(i).getOut().isEmpty()) {
				alone ++;
			}
		}
		if (alone != 0) {
			JOptionPane.showMessageDialog(null,"There are solitary nodes","Error" , JOptionPane.ERROR_MESSAGE);
			result = false;
		}	
		return result;
	}
}
