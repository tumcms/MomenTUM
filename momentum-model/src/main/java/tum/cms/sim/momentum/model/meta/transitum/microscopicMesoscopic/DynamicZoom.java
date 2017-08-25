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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.math3.util.Pair;

import tum.cms.sim.momentum.model.meta.transitum.data.TransitionArea;
import tum.cms.sim.momentum.utility.geometry.Cycle2D;
import tum.cms.sim.momentum.utility.geometry.GeometryFactory;
import tum.cms.sim.momentum.utility.geometry.Vector2D;
import tum.cms.sim.momentum.utility.lattice.CellIndex;
import tum.cms.sim.momentum.utility.lattice.ILattice;
import tum.cms.sim.momentum.utility.lattice.LatticeTheoryFactory;

public class DynamicZoom <T>{
	
	private ArrayList<TransitionArea> currentZoomAreas = null;
	private ILattice scenarioLattice = null;
	private Double tolerableDensity = null;
	private Double influenceRadius = null;
	private Double maximalVelocity = null;
	private Double mesoscopicTimeStepDuration = null;
	private Integer numberOfTimeStepsBetweenZooms = null;
	private Integer maximalNumberOfZoomAreas = null;
	private Double influenceSphereMultiplicator = null;
	
	public DynamicZoom(ILattice scenarioLattice, Double tolerableDensity, Double maximalVelocity, Double mesoscopicTimeStepDuration, Integer numberOfTimeStepsBetweenZooms, Integer maximalNumberOfZoomAreas, Double influenceSphereMultiplicator) {
		
		this.scenarioLattice = LatticeTheoryFactory.copyLattice(scenarioLattice, "DynamicZoomTransiTUM");	
		this.tolerableDensity = tolerableDensity;
		this.maximalVelocity = maximalVelocity;
		this.mesoscopicTimeStepDuration = mesoscopicTimeStepDuration;
		this.numberOfTimeStepsBetweenZooms = numberOfTimeStepsBetweenZooms;
		this.maximalNumberOfZoomAreas = maximalNumberOfZoomAreas;
		this.influenceSphereMultiplicator = influenceSphereMultiplicator;
		
		influenceRadius = this.calculateInfluenceRadius();
	}
	
	private Double calculateInfluenceRadius() {

		return mesoscopicTimeStepDuration * maximalVelocity * numberOfTimeStepsBetweenZooms;
	}

	public ArrayList<TransitionArea> getCurrentZoomAreas(HashMap<CellIndex, Double> currentDensityMap) {
		
		return this.getZoomAreasFromInfluenceSpheres(this.getAllInfluenceSpheres(currentDensityMap));
	}
	
	private ArrayList<TransitionArea> getZoomAreasFromInfluenceSpheres(
			List<ArrayList<Pair<CellIndex, Double>>> influenceSpheres) {

		Iterator sphereIterator = influenceSpheres.iterator();
		
		while (sphereIterator.hasNext()) {
			
			ArrayList<Pair<CellIndex, T>> influenceSphere = (ArrayList<Pair<CellIndex, T>>) sphereIterator.next();			
			
			Vector2D centerOfMassInfluenceSphere = MicroMesoUtility.getWeightedCenterOfMass(this.transformPairListFromCellIndexToPosition(influenceSphere));
			Integer densityThresholdMultiplicator = this.getDensityThresholdMultiplicator(influenceSphere, centerOfMassInfluenceSphere);		

			if (densityThresholdMultiplicator < 1) {
				
			break;
			}
			
		}
		
		
		
		return null;
	}







	private Integer getDensityThresholdMultiplicator(ArrayList influenceSphere, Vector2D centerOfMass) {
		
		Cycle2D zoomCircle = GeometryFactory.createCycle(centerOfMass, influenceRadius);
		
		List<CellIndex> cellsInCircle = scenarioLattice.getAllCircleCells(zoomCircle);

		
		return null;
	}

	private List<ArrayList<Pair<CellIndex, Double>>> getAllInfluenceSpheres(HashMap<CellIndex, Double> densityMap) {
		
		List<ArrayList<Pair<CellIndex, Double>>> influenceSpheres = new ArrayList<ArrayList<Pair<CellIndex, Double>>>();
		
		Map<CellIndex, Double> sortedDensityMap = densityMap.entrySet().stream()
					.sorted(Collections.reverseOrder(HashMap.Entry.comparingByValue()))
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                           (e1, e2) -> e2, LinkedHashMap::new));

		Iterator<Map.Entry<CellIndex, Double>> sortedDensityIterator = sortedDensityMap.entrySet().iterator();
		
		while (sortedDensityIterator.hasNext()) {
		
			Map.Entry<CellIndex, Double> sortedDensityEntry = sortedDensityIterator.next();
			
			if (!this.isLargerThanTolerableDensity(sortedDensityEntry.getValue())) {
			
				break;
			}		
			influenceSpheres.add(this.getInfluenceSphere(new Pair<CellIndex, Double>(sortedDensityEntry.getKey(), sortedDensityEntry.getValue()), sortedDensityMap));
		//sortedDensityMap.forEach(entry -> System.out.println(entry.getValue()));
		}
		
		return influenceSpheres;	
	}

	private ArrayList<Pair<CellIndex, Double>> getInfluenceSphere(Pair<CellIndex, Double> highDensityEntry,
			Map<CellIndex, Double> densityMap) {
		
		ArrayList<Pair<CellIndex, Double>> influenceSphere = new ArrayList<Pair<CellIndex, Double>>();
		Iterator<Map.Entry<CellIndex, Double>> densityIterator = densityMap.entrySet().iterator();
		
		while (densityIterator.hasNext()) {
			
			Map.Entry<CellIndex, Double> densityEntry = densityIterator.next();
			
			// true if it is the highDensityEntry OR if it is in the influence sphere of the high density entry
			if (densityEntry.getKey().equals(highDensityEntry.getKey()) || this.isInExtendedInfluenceSphere(densityEntry.getKey(), highDensityEntry.getKey())) {
				
				influenceSphere.add(new Pair<CellIndex, Double>(densityEntry.getKey(), densityEntry.getValue()));
			}
		}
		return influenceSphere;
	}
	
	private boolean isInExtendedInfluenceSphere(CellIndex currentCell, CellIndex cellToCompare) {

		Vector2D positionCurrentCell = scenarioLattice.getCenterPosition(currentCell);
		Vector2D positionCellToCompare = scenarioLattice.getCenterPosition(cellToCompare);
		
		Double distanceBetweenCells = positionCellToCompare.distance(positionCurrentCell);
		
		return distanceBetweenCells < (influenceRadius * influenceSphereMultiplicator) ? true : false;
	}
	

	private boolean isLargerThanTolerableDensity(Double value) {

		return value > tolerableDensity ? true : false;
	}
	
	private List transformPairListFromCellIndexToPosition(List<Pair<CellIndex, T>> influenceSphere) {

		List<Pair<Vector2D, T>> transformedPositions = new ArrayList<Pair<Vector2D, T>>();
		Iterator sphereIterator =  influenceSphere.iterator();
		
		while (sphereIterator.hasNext()) {
			
			Pair<CellIndex, T> entry = (Pair<CellIndex, T>) sphereIterator.next();
			transformedPositions.add(new Pair<Vector2D, T>(scenarioLattice.getCenterPosition(entry.getKey()), entry.getValue()));
		}	
		return transformedPositions;
	}
}
