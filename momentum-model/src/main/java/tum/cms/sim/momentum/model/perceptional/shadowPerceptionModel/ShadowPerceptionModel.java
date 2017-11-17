package tum.cms.sim.momentum.model.perceptional.shadowPerceptionModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrian;
import tum.cms.sim.momentum.data.layout.area.Area;
import tum.cms.sim.momentum.data.layout.obstacle.Obstacle;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.layout.lattice.LatticeModel;
import tum.cms.sim.momentum.model.perceptional.PerceptionalModel;
import tum.cms.sim.momentum.utility.geometry.GeometryFactory;
import tum.cms.sim.momentum.utility.geometry.Polygon2D;
import tum.cms.sim.momentum.utility.geometry.Segment2D;
import tum.cms.sim.momentum.utility.geometry.Vector2D;
import tum.cms.sim.momentum.utility.geometry.operation.GeometryAdditionals;
import tum.cms.sim.momentum.utility.graph.Edge;
import tum.cms.sim.momentum.utility.graph.Vertex;
import tum.cms.sim.momentum.utility.lattice.CellIndex;
import tum.cms.sim.momentum.utility.lattice.ILattice;
import tum.cms.sim.momentum.utility.lattice.Lattice.Occupation;
import tum.cms.sim.momentum.utility.lattice.LatticeTheoryFactory;

/**
 * The shadow geometry perception model finds visible objects via shadow mapping.
 * It computes an arc (part of a circle) in the direction of view +- radiant and
 * computes bresenham lines to the border (of the arc) beginning at the agents position.
 * Each object on the line will be captured.
 * Each object can occupy multiple cells (based on the lattice cell size).
 * 
 * For a faster computation the types of the elements (objects, people..) are
 * coded with an integer reference value in the visibility lattice.
 * 
 * Because a lattice is free if 0.0 and agents and layout objects can have the id 0
 * we need a shift of +1 in the perception lattices to avoid conflicts (encodeShift).
 * 
 * @author Peter Kielar
 */
public class ShadowPerceptionModel extends PerceptionalModel {
	
	private static String latticeIdName = "latticeId";
	private static String perceptionDistanceName = "perceptionDistance";
	private static String perceptionAngleDegreeName = "perceptionAngleDegree";
	private static int encodeShift = 1;
	
	private double perceptionDistance = 250;
	private double perceptionRadiant = 360.0;

	private ILattice pedestrianMap = null;
	private ILattice vertexMap = null;	
	private ILattice areaMap = null;
	
	private HashMap<Integer, HashSet<Integer>> pedestrainToPedestrian = new HashMap<>();
	private HashMap<Integer, ArrayList<Integer>> pedestrainToPedestrianPositions = new HashMap<>();
	private HashMap<Integer, HashSet<Integer>> pedestrainToVertex = new HashMap<>();
	private HashMap<Integer, HashSet<CellIndex>> pedestrainToObstacle = new HashMap<>();
	private HashMap<Integer, ArrayList<CellIndex>> pedestrainToObstaclePositions = new HashMap<>();
	private HashMap<Integer, HashMap<Integer, HashSet<CellIndex>>> pedestrainToArea = new HashMap<>();

	
	private List<CellIndex> perceptionHorizon = null;
	
	@Override
	public List<Vector2D> getPerceptedObstaclePositions(IPedestrian pedestrian, SimulationState simulationState) {
		
		List<Vector2D> obstaclePositions = new ArrayList<Vector2D>();
		
		pedestrainToObstaclePositions.get(pedestrian.getId()).stream().forEach(
				
				cellObstalce -> {
					
					if(cellObstalce == null) {
						
						obstaclePositions.add(null);
					}
					else {
						
						obstaclePositions.add(this.pedestrianMap.getCenterPosition(cellObstalce));
					}
				
				}
			);
		
		return obstaclePositions;
	}
	
	@Override
	public List<IPedestrian> getPerceptedPedestrianPositions(IPedestrian pedestrian, SimulationState simulationState) {
		
		List<IPedestrian> pedestrians = new ArrayList<IPedestrian>();
		
		pedestrainToPedestrianPositions.get(pedestrian.getId()).stream().forEach(
				
				pedestrianId -> {
					
					if(pedestrianId == null) {
						
						pedestrians.add(null);
					}
					else {
						
						pedestrians.add(pedestrianManager.getPedestrian(pedestrianId));
					}
				}
			);
		
		return pedestrians;
	}
	
	@Override
	public Collection<IPedestrian> getPerceptedPedestrians(IPedestrian pedestrian, SimulationState simulationState) {
		
		List<Integer> pedestrianIds = pedestrainToPedestrian.get(pedestrian.getId()).stream().collect(Collectors.toList());
		
		return this.pedestrianManager.getPedestrians(pedestrianIds);
	}

	/**
	 * This method use a bresenham line as alternative to avoid heavy computations 
	 */
	@Override
	public boolean isVisible(Vector2D viewPort, Vector2D position) {
		
		// the pedestrian map comprises obstacles during the execution of behavior models
		// all perception processings are done after supportModelUpdate
		CellIndex from = this.pedestrianMap.getCellIndexFromPosition(viewPort);
		CellIndex towards = this.pedestrianMap.getCellIndexFromPosition(position);
			
		if(!pedestrianMap.isInLatticeBounds(from) || !pedestrianMap.isInLatticeBounds(towards)) {
			
			return false;
		}
		
		Occupation result = Occupation.convertDoubleToOccupation(
				this.pedestrianMap.breshamLineCast(from,
						towards,
						(int)(perceptionDistance / this.pedestrianMap.getCellEdgeSize())));
		
		return Occupation.Empty.equals(result);
	}

	/**
	 * This method use a bresenham line as alternative to avoid heavy computation 
	 */
	@Override
	public boolean isVisible(IPedestrian pedestrian, Vector2D position) {
		
		return this.isVisible(pedestrian.getPosition(), position);
	}

	@Override
	public boolean isVisible(IPedestrian pedestrian, Area area) {
		
		return isVisible(pedestrian.getPosition(), area.getPointOfInterest());// pedestrainToArea.get(pedestrian.getId()).containsKey(area.getId());
	}
	
	@Override
	public boolean isVisible(IPedestrian pedestrian, IPedestrian otherPedestrian) {

		return pedestrainToPedestrian.get(pedestrian.getId()).contains(otherPedestrian.getId());
	}

	@Override
	public boolean isVisible(IPedestrian pedestrian, Vertex vertex) {

		return isVisible(pedestrian.getPosition(), vertex.getGeometry().getCenter());// pedestrainToVertex.get(pedestrian.getId()).contains(vertex.getId());
	}

	@Override
	public boolean isVisible(IPedestrian pedestrian, Edge edge) {
		
		return this.isVisible(pedestrian, edge.getEnd()) || this.isVisible(pedestrian, edge.getStart());
		//pedestrainToVertex.get(pedestrian.getId()).contains(edge.getStart().getId()) ||
			   //pedestrainToVertex.get(pedestrian.getId()).contains(edge.getEnd().getId());
	}

	@Override
	public double getPerceptionDistance() {
	
		return this.perceptionDistance;
	}

	@Override
	public void callPreProcessing(SimulationState simulationState) {

		this.perceptionDistance = this.properties.getDoubleProperty(perceptionDistanceName);
		int latticeId = this.properties.getIntegerProperty(latticeIdName);
		this.perceptionRadiant = GeometryAdditionals.translateToRadiant(this.properties.getDoubleProperty(perceptionAngleDegreeName));
		ArrayList<Segment2D> obstacleParts = new ArrayList<Segment2D>();
		
		this.scenarioManager.getObstacles()
			.stream()
			.map(Obstacle::getGeometry)
			.forEach(obstacleGeometry -> obstacleParts.addAll(obstacleGeometry.getSegments()));

		this.pedestrianMap = this.scenarioManager.getLattice(latticeId);
		this.vertexMap = LatticeTheoryFactory.copyLattice(this.pedestrianMap, "visibilityVertexLattice");
		this.areaMap = LatticeTheoryFactory.copyLattice(this.pedestrianMap, "visibilityAreaLattice");

		this.scenarioManager.getGraph().getVertices().parallelStream()
			.forEach(vertex -> this.vertexMap.setCellNumberValue(vertex.getGeometry().getCenter(), vertex.getId() + encodeShift));
		
		this.scenarioManager.getScenarios().getAreas().parallelStream()
			.forEach(area -> this.areaMap.occupyAllPolygonCells((Polygon2D) area.getGeometry(), area.getId() + encodeShift));
			
		this.perceptionHorizon = this.pedestrianMap.getAllOnCircleBorder(this.perceptionDistance,
				GeometryFactory.createVector(0.0, 0.0));
		
		LatticeModel.fillLatticeForObstacles(this.pedestrianMap, this.scenarioManager.getScenarios());
		LatticeModel.fillLatticeForObstacles(this.vertexMap, this.scenarioManager.getScenarios());
		LatticeModel.fillLatticeForObstacles(this.areaMap, this.scenarioManager.getScenarios());
	}

	@Override
	public void callPostProcessing(SimulationState simulationState) {
		// Nothing to do
	}

	/**
 	 * This updates all perception areas of all pedestrians in stream parallel.
	 */
	@Override
	protected void supportModelUpdate(SimulationState simulationState) {
		
		// 0. cleanup visibility maps
		pedestrainToPedestrian.clear();
		pedestrainToPedestrianPositions.clear();
		pedestrainToVertex.clear();
		pedestrainToObstacle.clear();
		pedestrainToObstaclePositions.clear();
		pedestrainToArea.clear();
		
		this.pedestrianManager.getAllPedestrians().stream().forEach(pedestrian -> {
			
			pedestrainToPedestrian.put(pedestrian.getId(), new HashSet<>());
			pedestrainToPedestrianPositions.put(pedestrian.getId(), new ArrayList<>());
			pedestrainToVertex.put(pedestrian.getId(), new HashSet<>());
			pedestrainToObstacle.put(pedestrian.getId(), new HashSet<>());
			pedestrainToObstaclePositions.put(pedestrian.getId(), new ArrayList<>());
			pedestrainToArea.put(pedestrian.getId(), new HashMap<>());
		});
		
		// 1. set all pedestrian positions in pedestrianmap
		ArrayList<CellIndex> pedestrianCells = new ArrayList<>();
		this.pedestrianManager.getAllPedestrians().parallelStream().forEach(pedestrian -> {
			
			List<CellIndex> cellsToOccupy = this.pedestrianMap.getAllCircleCells(pedestrian.getBodyRadius(), pedestrian.getPosition());
			ArrayList<CellIndex> pedestrianForCurrentCell = new ArrayList<CellIndex>();
			for(CellIndex cellToOccupy : cellsToOccupy) {
				
				if(this.pedestrianMap.isInLatticeBounds(cellToOccupy) && this.pedestrianMap.isCellFree(cellToOccupy)) {
					
					pedestrianForCurrentCell.add(cellToOccupy);
					this.pedestrianMap.setCellNumberValue(cellToOccupy, pedestrian.getId() + encodeShift);
				}
			}
			
			synchronized(pedestrianCells) {
				pedestrianCells.addAll(pedestrianForCurrentCell);
			}
		});
		
		// 2. compute all perception maps
		this.pedestrianManager.getAllPedestrians().parallelStream().forEach(pedestrian -> {
			
			// start position
			CellIndex pedestriansViewPort = this.pedestrianMap.getCellIndexFromPosition(pedestrian.getPosition());
			
			if(this.pedestrianMap.isInLatticeBounds(pedestriansViewPort)) {
				
				// select cells of the horizon
				List<CellIndex> perceptionBorder = this.findPerceptionBorder(this.pedestrianMap,
						pedestriansViewPort,
						pedestrian.getHeading());
				
				// 2.1 pedestrian perception
				this.perceivePedestriansAndObstacles(pedestrian.getId(), pedestriansViewPort, perceptionBorder);
				// 2.2 vertex perception
				//this.perceiveVertices(pedestrian.getId(), pedestriansViewPort, perceptionBorder);
				// 2.3 area perception
				//this.perceiveAreas(pedestrian.getId(), pedestriansViewPort, perceptionBorder);
				int i = 0;
			}
		});
		
		// 3. clear all pedestrian positions
		pedestrianCells.parallelStream().forEach(cell -> this.pedestrianMap.setCellNumberValue(cell, 0.0));
	}
	
	/**
	 * This method finds all pedestrians in sight and also updates the obstacle in sight.
	 * @param pedestrianId
	 * @param pedestrianViewPoint
	 * @param viewDirection
	 */
	private void perceivePedestriansAndObstacles(Integer pedestrianId, CellIndex viewPort, List<CellIndex> perceptionBorder) {
		
		perceptionBorder.stream().forEach(borderCell -> {
			
			// Send ray from position to border cell
			// Because the border cell is the target (and defines the distance), we use Max for distance
			// Because we like to stop the ray at any value, we give null to stopValue
			// Because we do not like to stop the ray at the pedestrian sending the ray, we set its id as ignore value
			List<Pair<Double,CellIndex>> hitRay = this.pedestrianMap.breshamLineCastTrace(viewPort,
					borderCell,
					null,
					pedestrianId.doubleValue() + encodeShift,
					false);

			// the last element in the ray is the hit target
			Double hitValue = hitRay.get(hitRay.size() - 1).getLeft();
			CellIndex cellValue = hitRay.get(hitRay.size() - 1).getRight();

			
			// if a obstacle was hit store the cell information
			if(Occupation.convertOccupationToDouble(Occupation.Fixed).equals(hitValue)) {
				
				this.pedestrainToObstacle.get(pedestrianId).add(cellValue);
				this.pedestrainToObstaclePositions.get(pedestrianId).add(cellValue);
				this.pedestrainToPedestrianPositions.get(pedestrianId).add(null);
			}
			else if(hitValue > 0.0) { // else if a pedestrian (id > 0) was hit, also store this
				
				// store the pedestrian id
				this.pedestrainToPedestrian.get(pedestrianId).add(hitValue.intValue() - encodeShift);
				this.pedestrainToPedestrianPositions.get(pedestrianId).add(hitValue.intValue() - encodeShift);
				this.pedestrainToObstaclePositions.get(pedestrianId).add(null);
			}
			else {
				
				this.pedestrainToPedestrianPositions.get(pedestrianId).add(null);
				this.pedestrainToObstaclePositions.get(pedestrianId).add(null);
			}
			// if a nothing was hit, ignore it
		});
		
	}
	
	/**
	 * Find all vertices in the perception range and store the information.
	 * The rays will not stop at the vertices. Thus, they do not block each other.
	 * @param pedestrianId
	 * @param viewPort
	 * @param perceptionBorder
	 */
	private void perceiveVertices(Integer pedestrianId,
			CellIndex viewPort,
			List<CellIndex> perceptionBorder) {

		perceptionBorder.parallelStream().forEach(borderCell -> {
			
			// Send ray from position to border cell
			// Because the border cell is the target (and defines the distance), we use Max for distance
			// Because we like to stop the ray at any value, we give null to stopValue
			// Because we do not like to stop the ray at the pedestrian sending the ray, we its id as ignore value
			List<Pair<Double,CellIndex>> hitRay = this.vertexMap.breshamLineCastTrace(viewPort,
					borderCell,
					null,
					pedestrianId.doubleValue() + encodeShift,
					true);
			
			// the ray hit list can comprises multiple vertices; thus, save all 
			for(int iter = 0; iter < hitRay.size(); iter++) {
				
				// the last element in the ray is the hit target
				Double hitValue = hitRay.get(iter).getLeft();
				
				// if NaN == Occupation.Fixed, wall is hit, thus != vertex is hit
				if(!Occupation.convertOccupationToDouble(Occupation.Fixed).equals(hitValue)) { 
					 
					// store the vertex id
					 this.pedestrainToVertex.get(pedestrianId).add(hitValue.intValue() - encodeShift);
				}
			}
		});
	}
	
	/**
	 * Find all areas in the perception range and store the information
	 * The rays will not stop at the vertices. Thus, they do not block each other.
	 * @param pedestrianId
	 * @param viewPort
	 * @param perceptionBorder
	 */
	private void perceiveAreas(Integer pedestrianId,
			CellIndex viewPort,
			List<CellIndex> perceptionBorder) {

		perceptionBorder.parallelStream().forEach(borderCell -> {
			
			// Send ray from position to border cell
			// Because the border cell is the target (and defines the distance), we use Max for distance
			// Because we do not like to stop the ray at any value, we give the stopValue for obstacles
			// Because we do not like to stop the ray at the pedestrian sending the ray, we give it the id as ignore value
			List<Pair<Double,CellIndex>> hitRay = this.areaMap.breshamLineCastTrace(viewPort,
					borderCell,
					Occupation.convertOccupationToDouble(Occupation.Fixed),
					pedestrianId.doubleValue() + encodeShift,
					true);
			
			// the ray hit list can comprises multiple areas; thus, save all 
			for(int iter = 0; iter < hitRay.size(); iter++) {
				
				// the last element in the ray is the hit target
				Double hitValue = hitRay.get(iter).getLeft();
				CellIndex cellValue = hitRay.get(iter).getRight();

				// if NaN == Occupation.Fixed, wall is hit, thus != area is hit
				if(!Occupation.convertOccupationToDouble(Occupation.Fixed).equals(hitValue)) { 
					
					 this.pedestrainToArea.get(pedestrianId).putIfAbsent(hitValue.intValue() - encodeShift, new  HashSet<CellIndex>());
					 this.pedestrainToArea.get(pedestrianId).get(hitValue.intValue() - encodeShift).add(cellValue);
				}
			}
		});
	}
	
	/** 
	 * For pre-processing, compute a circle around zero / zero for later use.
	 * This method transposes all horizon cells regarding to the agent's cell position.
	 * @param visibilityLattice
	 * @param pedestrianViewPoint
	 * @param viewDirection
	 * @return
	 */
	private List<CellIndex> findPerceptionBorder(ILattice visibilityLattice, CellIndex pedestrianViewPoint, Vector2D viewDirection) {
		
		// find "left" cwclockwise horizon intersection
		Vector2D cwclockRotateViewDirection = viewDirection.rotate(this.perceptionRadiant)
				.scale(this.perceptionDistance);
		
		// find "right" clockwise horizon intersection
		Vector2D clockRotateViewDirection = viewDirection.rotate(-1.0 * this.perceptionRadiant)
				.scale(this.perceptionDistance);
		
		// find left and right
		double distanceToOptimalCellLeft = Double.MAX_VALUE;
		int indexOptimalCellLeft = -1;
		double distanceToOptimalCellRight = Double.MAX_VALUE;
		int indexOptimalCellRight = -1;
		
		// find all border cells regarding the zero,zero origin cell
		for(int iter = 0; iter < this.perceptionHorizon.size(); iter++) {
			
			CellIndex horizon = this.perceptionHorizon.get(iter);
			Vector2D horizonPosition = visibilityLattice.getCenterPosition(horizon);
			double distanceToLeft = horizonPosition.distance(cwclockRotateViewDirection);
			double distanceToRight = horizonPosition.distance(clockRotateViewDirection);
			
			if(distanceToOptimalCellLeft > distanceToLeft) {
				
				distanceToOptimalCellLeft = distanceToLeft;
				indexOptimalCellLeft = iter;
			}
			
			if(distanceToOptimalCellRight > distanceToRight) {
				
				distanceToOptimalCellRight = distanceToRight;
				indexOptimalCellRight = iter;
			}
		}
		
		List<CellIndex> perceptionBorder = new ArrayList<>();
		// get all border cells 
		if(indexOptimalCellLeft < indexOptimalCellRight) { // ok
			
			perceptionBorder.addAll(this.perceptionHorizon.subList(indexOptimalCellLeft, indexOptimalCellRight));
		}
		else { // breaks at the end of the list
			
			perceptionBorder.addAll(this.perceptionHorizon.subList(indexOptimalCellLeft, this.perceptionHorizon.size() - 1));
			perceptionBorder.addAll(this.perceptionHorizon.subList(0, indexOptimalCellRight + 1));
		}
		
		// transpose all border cells by the agent's cell
		perceptionBorder.forEach(cell ->
			LatticeTheoryFactory.createCellIndex(cell.getRow() + pedestrianViewPoint.getRow(), cell.getColumn() + pedestrianViewPoint.getColumn()));
		
		return perceptionBorder;
	}
}
