package tum.cms.sim.momentum.model.perceptional.shadowPerceptionModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrian;
import tum.cms.sim.momentum.data.layout.area.Area;
import tum.cms.sim.momentum.data.layout.obstacle.Obstacle;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.layout.lattice.LatticeModel;
import tum.cms.sim.momentum.model.perceptional.PerceptionalModel;
import tum.cms.sim.momentum.utility.geometry.Segment2D;
import tum.cms.sim.momentum.utility.geometry.Vector2D;
import tum.cms.sim.momentum.utility.geometry.operation.GeometryAdditionals;
import tum.cms.sim.momentum.utility.graph.Edge;
import tum.cms.sim.momentum.utility.graph.Vertex;
import tum.cms.sim.momentum.utility.lattice.CellIndex;
import tum.cms.sim.momentum.utility.lattice.ILattice;

/**
 * The shadow geometry perception model finds visible objects via shadow maping lines.
 * @author Peter Kielar
 */
public class ShadowPerceptionModel extends PerceptionalModel {

	private static String latticeIdName = "latticeIdName";
	private static String perceptionDistanceName = "perceptionDistance";
	private static String perceptionAngleDegreeName = "perceptionAngleDegree";
	
	private int perceptionDistance = 250;
	private int latticeId = 0;
	private double perceptionRadiant = 360.0;
	
	private HashMap<Integer, HashSet<IPedestrian>> pedestrainToPedestrian = new HashMap<>();
	private HashMap<Integer, HashSet<Vertex>> pedestrainToVertex = new HashMap<>();
	private HashMap<Integer, HashSet<Edge>> pedestrainToEdge = new HashMap<>();
	private HashMap<Integer, HashSet<Obstacle>> pedestrainToObstacle = new HashMap<>();
	private HashMap<Integer, HashSet<Area>> pedestrainToArea = new HashMap<>();
	private HashSet<Integer> pedestrianComputed= new HashSet<>();
	
	@Override
	public List<IPedestrian> getPerceptedPedestrians(IPedestrian pedestrian, SimulationState simulationState) {
		
		return pedestrainToPedestrian.get(pedestrian.getId()).stream().collect(Collectors.toList());
	}

	/**
	 * This method use a bresenham line as alternative to avoid heavy computation 
	 */
	@Override
	public boolean isVisible(Vector2D viewPort, Vector2D position) {
		
		CellIndex from = this.scenarioManager.getLattice(latticeId).getCellIndexFromPosition(viewPort);
		CellIndex towards = this.scenarioManager.getLattice(latticeId).getCellIndexFromPosition(position);
			
		return this.scenarioManager.getLattice(latticeId).breshamLineCast(from, towards, perceptionDistance);
	}

	/**
	 * This method use a bresenham line as alternative to avoid heavy computation 
	 */
	@Override
	public boolean isVisible(IPedestrian pedestrian, Vector2D position) {
		
		this.updateVisibleObjectsForPedestrian(pedestrian.getId(), pedestrian.getPosition(), pedestrian.getHeading());
		return this.isVisible(pedestrian.getPosition(), position);
	}

	@Override
	public boolean isVisible(IPedestrian pedestrian, Area area) {
		
		this.updateVisibleObjectsForPedestrian(pedestrian.getId(), pedestrian.getPosition(), pedestrian.getHeading());
		return pedestrainToArea.get(pedestrian.getId()).contains(area);
	}
	
	@Override
	public boolean isVisible(IPedestrian pedestrian, IPedestrian otherPedestrian) {

		this.updateVisibleObjectsForPedestrian(pedestrian.getId(), pedestrian.getPosition(), pedestrian.getHeading());
		return pedestrainToPedestrian.get(pedestrian.getId()).contains(otherPedestrian);
	}

	@Override
	public boolean isVisible(IPedestrian pedestrian, Vertex vertex) {

		this.updateVisibleObjectsForPedestrian(pedestrian.getId(), pedestrian.getPosition(), pedestrian.getHeading());
		return pedestrainToVertex.get(pedestrian.getId()).contains(vertex);
	}

	@Override
	public boolean isVisible(IPedestrian pedestrian, Edge edge) {
		
		this.updateVisibleObjectsForPedestrian(pedestrian.getId(), pedestrian.getPosition(), pedestrian.getHeading());
		return pedestrainToEdge.get(pedestrian.getId()).contains(edge);
	}

	private void updateVisibleObjectsForPedestrian(Integer pedestrianId, Vector2D pedestrianViewPoint, Vector2D viewDirection) {
		
		synchronized(pedestrianComputed) {
			
			if(pedestrianId != null && pedestrianComputed.contains(pedestrianId)) {
				
				return;
			}
		
			pedestrianComputed.add(pedestrianId);
		}
		
		double view = this.perceptionRadiant;
		double distance = this.perceptionDistance;
		
		// compute shadow map
	}

	/**
	 * Update positions of pedestrians in parallel
 	 * This updates the lattice values for shadow map perception.
	 */
	@Override
	protected void supportModelUpdate(SimulationState simulationState) {
		
		pedestrianComputed.clear();
		
		ILattice visibilityMap = this.scenarioManager.getLattice(this.latticeId);
		visibilityMap.setAllCells(0.0);
		
		this.pedestrianManager.getAllPedestrians().parallelStream().forEach(pedestrian -> {
			
			List<CellIndex> cellsToOccupy = visibilityMap.getAllCircleCells(pedestrian.getBodyRadius(),	pedestrian.getPosition());
			double pedestrainId = (double) pedestrian.getId();
			visibilityMap.setCells(cellsToOccupy, pedestrainId);
		});
		
		LatticeModel.fillLatticeForObstacles(visibilityMap, this.scenarioManager.getScenarios());
	}

	@Override
	public void callPreProcessing(SimulationState simulationState) {

		this.perceptionDistance = this.properties.getIntegerProperty(perceptionDistanceName);
		this.latticeId = this.properties.getIntegerProperty(latticeIdName);
		this.perceptionRadiant = GeometryAdditionals.translateToRadiant(this.properties.getDoubleProperty(perceptionAngleDegreeName));
		ArrayList<Segment2D> obstacleParts = new ArrayList<Segment2D>();
		
		this.scenarioManager.getObstacles()
			.stream()
			.map(Obstacle::getGeometry)
			.forEach(obstacleGeometry -> obstacleParts.addAll(obstacleGeometry.getSegments()));

		ILattice visibilityMap = this.scenarioManager.getLattice(this.latticeId);
		LatticeModel.fillLatticeForObstacles(visibilityMap, this.scenarioManager.getScenarios());
	}

	@Override
	public void callPostProcessing(SimulationState simulationState) {
		// Nothing to do
	}

}
