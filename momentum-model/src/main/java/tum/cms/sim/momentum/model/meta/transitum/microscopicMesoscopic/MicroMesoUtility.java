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

package tum.cms.sim.momentum.model.meta.transitum.microscopicMesoscopic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.Pair;

import tum.cms.sim.momentum.data.agent.pedestrian.state.operational.StandingState;
import tum.cms.sim.momentum.data.agent.pedestrian.state.operational.WalkingState;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;
import tum.cms.sim.momentum.model.meta.transitum.TransiTumModel;
import tum.cms.sim.momentum.model.meta.transitum.TransiTumModel.NextTransformation;
import tum.cms.sim.momentum.model.meta.transitum.data.MultiscaleArea;
import tum.cms.sim.momentum.model.meta.transitum.data.TransitionArea;
import tum.cms.sim.momentum.model.meta.transitum.data.MultiscaleArea.AreaType;
import tum.cms.sim.momentum.model.meta.transitum.data.TransitionArea.TransitionAreaType;
import tum.cms.sim.momentum.utility.geometry.Cycle2D;
import tum.cms.sim.momentum.utility.geometry.GeometryFactory;
import tum.cms.sim.momentum.utility.geometry.Polygon2D;
import tum.cms.sim.momentum.utility.geometry.Vector2D;
import tum.cms.sim.momentum.utility.lattice.CellIndex;
import tum.cms.sim.momentum.utility.lattice.ILattice;

public class MicroMesoUtility<T> {
	
	public static ArrayList<TransitionArea> updateTransitionAreas(ArrayList<MultiscaleArea> multiscaleAreas, double transitionRadiusMicroMeso) {
		
		ArrayList<TransitionArea> transitionAreas = new ArrayList<TransitionArea>();
				
		for (MultiscaleArea multiscaleArea: multiscaleAreas) {
			
			if (multiscaleArea.getAreaType().equals(AreaType.Microscopic)) {
				
				Double innerRadius = multiscaleArea.getRadiusOfArea();
				
				TransitionArea microMesoTransitionArea = new TransitionArea(TransitionAreaType.MicroscopicMesoscopic, 
					innerRadius	, innerRadius + transitionRadiusMicroMeso, multiscaleArea.getCenterOfArea());
				
				transitionAreas.add(microMesoTransitionArea);
			}
		}
		return transitionAreas;
	}
	
	public static NextTransformation getNextTransformation(Double transitionLevel, Double mesoscopicTimeStep, Double transitionAreaFactor) {

		if (transitionLevel >= mesoscopicTimeStep * transitionAreaFactor * 0.99) {
			
			return NextTransformation.MicroscopicMesoscopic;
		}
		return NextTransformation.None;
	}

	public static boolean isPedestrianInAnyTransitionArea(IRichPedestrian pedestrian, ArrayList<TransitionArea> transitionAreas) { 
		
		for (TransitionArea transitionArea : transitionAreas) {
			
			if (transitionArea.containsPedestrian(pedestrian)) {
				return true;
			}
		}
		return false;
	}

	public static Integer getPedestrianTransitionAreaNumber(IRichPedestrian pedestrian, ArrayList<TransitionArea> transitionAreas) {
		
		for (Integer number = 0; number < transitionAreas.size(); number++) {
			
			if (transitionAreas.get(number).containsPedestrian(pedestrian)) {
				
				return number;
			}
		}
		return -1;
	}

	public static Vector2D getPropagationVector(IRichPedestrian pedestrian, Double maximalVelocity, Double timeFrameBetweenTransitions) {

		Vector2D heading = pedestrian.getHeading().getNormalized();
		
		Double possibleWalkingDistance = timeFrameBetweenTransitions * maximalVelocity;
		
		Vector2D propagationVector = heading.multiply(possibleWalkingDistance);
					
		return propagationVector;
	}
	
	// gives the possible area, a pedestrian may walk until the next transformation
	public static Polygon2D getWalkingPolygon(IRichPedestrian pedestrian, Vector2D propagationVector) {
		
		ArrayList<Vector2D> pointsForWalkingPolygong = new ArrayList<Vector2D>();			
		
		Vector2D currentPosition = pedestrian.getPosition();
		pointsForWalkingPolygong.add(currentPosition);			
		
		Vector2D rightHandEndPoint = getRightHandEndPoint(currentPosition, propagationVector);
		
		Vector2D leftHandEndPoint = getLeftHandEndPoint(currentPosition, propagationVector);
		
		pointsForWalkingPolygong.add(leftHandEndPoint);
		pointsForWalkingPolygong.add(rightHandEndPoint);
		
		Polygon2D walkingPolygon = GeometryFactory.createPolygon(pointsForWalkingPolygong);
		
		return walkingPolygon;
	}
	
	public static Vector2D getRightHandEndPoint(Vector2D currentPosition, Vector2D propagationVector) {
		
		Double deviationFactor = propagationVector.getMagnitude() * FastMath.tan(FastMath.PI/18);
		
		Vector2D deviationVectorRightHand = propagationVector.getRightHandOrthogonalVector().getNormalized().multiply(deviationFactor);
		
		Vector2D rightHandEndPoint = (currentPosition.sum(propagationVector)).sum(deviationVectorRightHand);
		
		return rightHandEndPoint;	
	}
	
	public static Vector2D getLeftHandEndPoint(Vector2D currentPosition, Vector2D propagationVector) {
			
			Double deviationFactor = propagationVector.getMagnitude() * FastMath.tan(FastMath.PI/18);
			
			Vector2D deviationVectorLeftHand = propagationVector.getLeftHandOrthogonalVector().getNormalized().multiply(deviationFactor);
			
			Vector2D leftHandEndPoint = (currentPosition.sum(propagationVector)).sum(deviationVectorLeftHand);
			
			return leftHandEndPoint;	
		}
	
	public static void updateWalkingAndStandingStates(Vector2D newPosition, IRichPedestrian microPed) {
		
		if (microPed.getWalkingState() != null) {
			
			WalkingState newWalkingState = new WalkingState(newPosition, microPed.getWalkingState().getWalkingVelocity(), microPed.getWalkingState().getWalkingHeading());
			microPed.setWalkingState(newWalkingState);
		}	
		if (microPed.getStandingState() != null) {
			
			StandingState newStandingState = new StandingState(newPosition, microPed.getStandingState().getStandingHeading());
			microPed.setStandingState(newStandingState);	
		}
	}
		
	// returns a list with all pedestrians except the given one
	public static ArrayList<IRichPedestrian> getOtherPedestrians(IRichPedestrian pedestrian, ArrayList<IRichPedestrian> microscopicPedestrians) {
		
		Integer thisPedestrianId = pedestrian.getId();
		
		ArrayList<IRichPedestrian> otherPedestrians = microscopicPedestrians.stream()	
				.filter(ped -> ped.getId() != thisPedestrianId)
				.collect(Collectors.toCollection(ArrayList::new));
		
		return otherPedestrians;
	}

	public static boolean isCollisionFree(IRichPedestrian pedestrian, ArrayList<IRichPedestrian> pedestriansToTest) {
		
		return isCollisionFree(pedestrian.getPosition(), pedestrian.getBodyRadius(), pedestriansToTest);
	}

	public static boolean isCollisionFree(Vector2D positionOfPedestrian, double bodyRadiusOfPedestrian, ArrayList<IRichPedestrian> pedestriansToTest) {
		
		for (IRichPedestrian pedToTest : pedestriansToTest) {
			
			if (!isCollisionFree(positionOfPedestrian, bodyRadiusOfPedestrian, pedToTest)) {
				return false;
			}
		}
		return true;
	}
		
	public static boolean isCollisionFree(IRichPedestrian pedestrian, IRichPedestrian pedestrianToTest) {
		
		return isCollisionFree(pedestrian.getPosition(), pedestrian.getBodyRadius(), pedestrianToTest);
	}

	public static boolean isCollisionFree(Vector2D positionOfPedestrian, double bodyRadiusOfPedestrian, IRichPedestrian pedestrianToTest) {

		Double distance = positionOfPedestrian.distance(pedestrianToTest.getPosition());
		
		Double  minimalDistance = bodyRadiusOfPedestrian + pedestrianToTest.getBodyRadius();
		
		if (distance > minimalDistance) {
		
			return true;
		}
		return false;
	}
	
	public static ArrayList<IRichPedestrian> getPedestriansInTransitionZoneMicroMeso(Collection<IRichPedestrian> pedestrians, ArrayList<TransitionArea> transitionAreasMicroMeso) {
		
		ArrayList<IRichPedestrian> pedestriansInTransitionZone = pedestrians.stream()
				.filter(ped -> MicroMesoUtility.isPedestrianInAnyTransitionArea(ped, transitionAreasMicroMeso))
				.collect(Collectors.toCollection(ArrayList::new));
		
		return pedestriansInTransitionZone;
	}
	
	public static ArrayList<CellIndex> getTemporalDynamicCells(ILattice lattice) {
		
		ArrayList<CellIndex> temporalDynamicCells = new ArrayList<CellIndex>();

		ArrayList<IRichPedestrian> pedestriansInTransitionZone = TransiTumModel.getPedestriansInTransitionZone();
		ArrayList<IRichPedestrian> nonFitingPedestrians = new ArrayList<IRichPedestrian>();
		
		if (pedestriansInTransitionZone == null) {
			return temporalDynamicCells;
		}
			
		nonFitingPedestrians = pedestriansInTransitionZone.stream()
			.filter(ped -> ped.getPosition().distance(lattice.getCenterPosition(lattice.getCellIndexFromPosition(ped.getPosition()))) > 0.01 * lattice.getCellEdgeSize() )
			.collect(Collectors.toCollection(ArrayList::new));
		
		if (nonFitingPedestrians.isEmpty()) {
			return temporalDynamicCells;
		}
		
		for (IRichPedestrian ped : nonFitingPedestrians) {
			
			Cycle2D pedBody = GeometryFactory.createCycle(ped.getPosition(), ped.getBodyRadius());
			
			CellIndex mainCellofPedestrian = lattice.getCellIndexFromPosition(ped.getPosition());
			ArrayList<CellIndex> neighboringCells = lattice.getAllNeighborIndices(mainCellofPedestrian);
			
			for (CellIndex cellIndex : neighboringCells) {
				
				if (pedBody.isIntersected(lattice.getCellPolygon(cellIndex))) {
					
					temporalDynamicCells.add(cellIndex);
				}
			}
		}
		return temporalDynamicCells;
	}
	
	public static <T> Vector2D getWeightedCenterOfMass(List<Pair<T, Double>> positionsWithWeight) {

		Iterator<Pair<T, Double>> comIterator = positionsWithWeight.iterator();
		
		Double totalDensity = 0.0;
		Vector2D totalPosition = GeometryFactory.createVector(0, 0);
		
		while (comIterator.hasNext()) {
			
			Pair<T, Double> entry = comIterator.next();
			Vector2D position = (Vector2D) entry.getKey();
			Double densityWeight = entry.getValue();		
			Vector2D weightedPosition = position.multiply(densityWeight);
			
			totalDensity = totalDensity + densityWeight;
			totalPosition = totalPosition.sum(weightedPosition);
		}	
		return totalPosition.multiply(1/totalDensity);
	}
	

	
}
