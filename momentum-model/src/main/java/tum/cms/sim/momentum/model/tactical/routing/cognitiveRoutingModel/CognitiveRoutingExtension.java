package tum.cms.sim.momentum.model.tactical.routing.cognitiveRoutingModel;

import java.util.Collection;
import java.util.HashSet;

import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrianExtension;
import tum.cms.sim.momentum.utility.graph.Vertex;

public class CognitiveRoutingExtension implements IPedestrianExtension {

	/**
	 * Defines a distribution which is used to model the pedestrians reactivity
	 * if the next vertex of the current vertex is visible. Normally the
	 * agent will re-route on sight.
	 * Type of distribution: Gamma
	 * Independent of learning; thus, describes natural habits.
	 * Calibrates the reactivity for each agent.
	 */
	private double nodeReach;
	
	/**
	 * Defines a distribution that models the distance in which agents try to
	 * find a node to reach. Normally agents use the next vertex only (or
	 * UPRM use the most distant vertex that is still visible). A low value
	 * will reduce the computation speed. 
	 * Dependent on learning percentage of the graph.
	 * Calibrates the reactivity for each agent.
	 * This interacts with the perception depth of the perception model.
	 */
	private double currentLookAhead;

	/**
	 * Defines the accuracy of pointing to a goal location based on Tyler Thrash experiments eth Zürich.
	 * This defines the probability the pointing error to to goal. Important, this will
	 * provide a positive value; thus, multiply randomly with + or - for use.
	 * Dependent on learning percentage of the graph.
	 * Calibrates the navigation accuracy of the agent.
	 */
	private double currentPointingAccuracy;
	
	/**
	 * Defines the distances at which non-adjacent node will be added into the
	 * agents memory. Use a distribution for that. Calibrates the spatial learning
	 * for every agent skill of an agent.
	 * Dependent on learning percentage of the graph.
	 */
	private double currentLearningDistance;
	
	/**
	 * Defines the knowledge of the routing graph of the agent.
	 * Important, this is not simple a percentage of known vertex (random) but
	 * if the agent has more details on the graph network. Thus, low knowledge means
	 * that the agent can access main routes. The algorithm will find all
	 * main routes (long and straight routes) via Space Syntax.
	 * Independent of learning but dependent on the population.
	 * Calibrates the initial graph knowledge.
	 */
	private double currentLearnedPercentage;
	
	/**
	 * The known vertices (as Ids) of the agent
	 */
	private HashSet<Vertex> knownVertices;


	public HashSet<Vertex>  getknownVertices() {
		return knownVertices;
	}

	public void addKnownVertexIds(Collection<Vertex> knownVertices, int numberExistingVertices) {
		
		if(this.knownVertices == null) {
		
			this.knownVertices = new HashSet<>();
		}
		
		this.knownVertices.addAll(knownVertices);
		this.currentLearnedPercentage = this.knownVertices.size() / numberExistingVertices;
	}

	public double getCurrentLearningDistance() {
		return currentLearningDistance;
	}

	public void setCurrentLearningDistance(double currentLearningDistance) {
		this.currentLearningDistance = currentLearningDistance;
	}

	public double getNodeReach() {
		return nodeReach;
	}

	public void setNodeReach(double nodeReach) {
		this.nodeReach = nodeReach;
	}

	public double getCurrentLookAhead() {
		return currentLookAhead;
	}

	public void setCurrentLookAhead(double currentLookAhead) {
		this.currentLookAhead = currentLookAhead;
	}

	public double getCurrentPointingAccuracy() {
		return currentPointingAccuracy;
	}

	public void setCurrentPointingAccuracy(double currentPointingAccuracy) {
		this.currentPointingAccuracy = currentPointingAccuracy;
	}

	public double getCurrentLearnedPercentage() {
		return currentLearnedPercentage;
	}
}
