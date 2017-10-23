package tum.cms.sim.momentum.model.tactical.routing.cognitiveRoutingModel;

import tum.cms.sim.momentum.utility.generic.PropertyBackPack;
import tum.cms.sim.momentum.utility.probability.distrubution.DistributionFactory;
import tum.cms.sim.momentum.utility.probability.distrubution.IDistribution;

public class CognitiveRoutingParameter {

	/**
	 * Defines a distribution which is used to model the pedestrians reactivity
	 * if the next vertex of the current vertex is visible. Normally the
	 * agent will re-route on sight.
	 * Type of distribution: Gamma
	 * Independent of learning; thus, describes natural habits.
	 * Calibrates the reactivity for each agent.
	 */
	static String nodeReachDistributionShapeName = "NodeReachedDistributionShape";
	static String nodeReachDistributionScaleName = "NodeReachedDistributionScale";
	private IDistribution nodeReachedDistribution = null;
	
	public IDistribution getNodeReachedDistribution() {
		return nodeReachedDistribution;
	}

	public void setNodeReachedDistribution(PropertyBackPack propertyBackPack) {
		
		double shape = propertyBackPack.getDoubleProperty(nodeReachDistributionShapeName);
		double scale = propertyBackPack.getDoubleProperty(nodeReachDistributionScaleName);
		this.nodeReachedDistribution = DistributionFactory.createGammaDistribution(shape, scale);
	}
	
	/**
	 * Defines the initial knowledge of the routing graph of the agent.
	 * Important, this is not simple a percentage of known vertex (random) but
	 * if the agent has more details on the graph network. Thus, low knowledge means
	 * that the agent can access main routes. The algorithm will find all
	 * main routes (long and straight routes) via Space Syntax.
	 * Type of distribution: Gamma.
	 * Independent of learning but dependent on the population.
	 * Calibrates the initial graph knowledge.
	 */
	static String initialKnowledgeDistributionShapeName = "initialKnowledgeDistributionShape";
	static String initialKnowledgeDistributionScaleName = "initialKnowledgeDistributionScale";
	private IDistribution initialKnowledgDistribution = null;

	public IDistribution getInitialKnowledgDistribution() {
		return initialKnowledgDistribution;
	}

	public void setInitialKnowledgDistribution(PropertyBackPack propertyBackPack) {
		
		double shape = propertyBackPack.getDoubleProperty(initialKnowledgeDistributionShapeName);
		double scale = propertyBackPack.getDoubleProperty(initialKnowledgeDistributionScaleName);
		this.initialKnowledgDistribution = DistributionFactory.createGammaDistribution(shape, scale);
	}
	
	/**
	 * Defines the distances at which non-adjacent node will be added into the
	 * agents memory. Use a distribution for that. Calibrates the spatial learning
	 * for every agent skill of an agent.
	 * Type of distribution: Gamma
	 * Dependent on learning percentage of the graph.
	 */
	static String learningDistanceDistributionShapeName = "LearningDistanceDistributionShape";
	static String learningDistanceDistributionScaleName = "LearningDistanceDistributionScale";
	private IDistribution learingDistanceDistribution = null;
	
	public IDistribution getLearingDistanceDistribution() {
		return learingDistanceDistribution;
	}
	
	public void setLearingDistanceDistribution(PropertyBackPack propertyBackPack) {
		
		double shape = propertyBackPack.getDoubleProperty(learningDistanceDistributionShapeName);
		double scale = propertyBackPack.getDoubleProperty(learningDistanceDistributionScaleName);
		this.learingDistanceDistribution = DistributionFactory.createGammaDistribution(shape, scale);
	}
	
	/**
	 * Defines the accuracy of pointing to a goal location based on Tyler Thrash experiments eth Zürich.
	 * This defines the probability the pointing error to to goal. Important, this will
	 * provide a positive value; thus, multiply randomly with + or - for use.
	 * Type of distribution: Exponential
	 * Dependent on learning percentage of the graph.
	 * Calibrates the navigation accuracy of the agent.
	 */
	static String pointingAccuracyDistributionShapeName = "pointingAccuracyDistributionShape";
	static String pointingAccuracyDistributionScaleName = "pointingAccuracyDistributionScale";
	private IDistribution pointingAccuracyDistribution = null; 
	
	public IDistribution getPointingAccuracyDistribution() {
		return pointingAccuracyDistribution;
	}

	public void setPointingAccuracyDistribution(PropertyBackPack propertyBackPack) {
		
		double shape = propertyBackPack.getDoubleProperty(pointingAccuracyDistributionShapeName);
		double scale = propertyBackPack.getDoubleProperty(pointingAccuracyDistributionScaleName);
		this.pointingAccuracyDistribution = DistributionFactory.createGammaDistribution(shape, scale);
	}

	/**
	 * Defines a distribution that models the distance in which agents try to
	 * find a node to reach. Normally agents use the next vertex only (or
	 * UPRM use the most distant vertex that is still visible). A low value
	 * will reduce the computation speed. 
	 * Type of distribution: Gamma
	 * Dependent on learning percentage of the graph.
	 * Calibrates the reactivity for each agent.
	 * This interacts with the perception depth of the perception model.
	 */
	static String lookAheadDistributionShapeName = "LookAheadDistributionShape";
	static String lookAheadDistributionScaleName = "LookAheadDistributionScale";
	private IDistribution lookAheadDistribution = null;
	
	public IDistribution getLookAheadDistribution() {
		return lookAheadDistribution;
	}

	public void setLookAheadDistribution(PropertyBackPack propertyBackPack) {
		
		double shape = propertyBackPack.getDoubleProperty(lookAheadDistributionShapeName);
		double scale = propertyBackPack.getDoubleProperty(lookAheadDistributionScaleName);
		this.lookAheadDistribution = DistributionFactory.createGammaDistribution(shape, scale);
	}	
}
