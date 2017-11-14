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

package tum.cms.sim.momentum.model.meta.transitum;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import tum.cms.sim.momentum.data.agent.pedestrian.state.other.MetaState;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrianExtension;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.meta.MetaModel;
import tum.cms.sim.momentum.model.meta.transitum.TransiTumModelDescisionerExtension.SimulationType;
import tum.cms.sim.momentum.model.meta.transitum.data.MultiscaleArea;
import tum.cms.sim.momentum.model.meta.transitum.data.TransitionArea;
import tum.cms.sim.momentum.model.meta.transitum.data.MultiscaleArea.AreaType;
import tum.cms.sim.momentum.model.meta.transitum.microscopicMesoscopic.XTDensityCalculation;
import tum.cms.sim.momentum.model.meta.transitum.microscopicMesoscopic.DynamicZoom;
import tum.cms.sim.momentum.model.meta.transitum.microscopicMesoscopic.Meso2MicroTransformation;
import tum.cms.sim.momentum.model.meta.transitum.microscopicMesoscopic.Micro2MesoTransformation;
import tum.cms.sim.momentum.model.meta.transitum.microscopicMesoscopic.MicroMesoUtility;
import tum.cms.sim.momentum.utility.generic.PropertyBackPack;
import tum.cms.sim.momentum.utility.geometry.GeometryFactory;
import tum.cms.sim.momentum.utility.geometry.Vector2D;
import tum.cms.sim.momentum.utility.lattice.CellIndex;
import tum.cms.sim.momentum.utility.lattice.ILattice;
import tum.cms.sim.momentum.utility.lattice.LatticeTheoryFactory;
import tum.cms.sim.momentum.utility.lattice.Lattice.Occupation;

public class TransiTumModel extends MetaModel {
	
	private enum TransiTumType {
		
		MicroscopicMesoscopic
	}
	
	public enum NextTransformation {
		
		MicroscopicMesoscopic,
		MesoscopicMacroscopic,
		None
	}
	
	@Override
	public boolean isMultiThreading() {

		return false;
	}
	
	private ArrayList<Integer> notTransformedMi2Me = new ArrayList<Integer>();
	private ArrayList<Integer> transformedMi2Me = new ArrayList<Integer>();
	private ArrayList<Integer> transformedMe2Mi = new ArrayList<Integer>();
	private ArrayList<Integer> notTransformedMe2Mi = new ArrayList<Integer>();
	
	private NextTransformation nextTransformation = NextTransformation.None;
	private TransiTumType transiTumType = null;
	private ArrayList<MultiscaleArea> multiscaleAreas = new  ArrayList<MultiscaleArea>();
	private ArrayList<TransitionArea> transitionAreasMicroMeso = new ArrayList<TransitionArea>();
	
	private final static String areaTypeName ="areaType";
	private final static String radiusName ="radius";
	private final static String xPositionName = "xPosition";
	private final static String yPositionName = "yPosition";
	private final static String maximalVelocityName = "maximalVelocity";
	private final static String microscopicTimeStepMultiplikatorName = "microscopicTimeStepMultiplikator";
	private final static String mesoscopicTimeStepMultiplikatorName = "mesoscopicTimeStepMultiplikator";
	private final static String transitionAreaFactorName = "transitionAreaFactor";
	private final static String microscopicModelIdentifier = "microscopicModel";
	private final static String mesoscopicModelIdentifier = "mesoscopicModel";
	private final static String scenarioLatticeIdName = "scenarioLatticeId";
	private final static String collisionDetectionLatticeIdName = "collisionDetectionLatticeId";
	private final static String dynamicZoomTimeStepMultiplicatorName = "dynamicZoomTimeStepMultiplicator";
	private final static String maximalDensityName = "maximalDensity";
	private final static String tolerableDensityName = "tolerableDensity";
	private final static String influenceSphereMultiplicatorName = "influenceSphereMultiplicator";
	
	private ArrayList<Integer> microscopicModel = null;
	private ArrayList<Integer> mesoscopicModel = null;
	
	private Double transitionRadiusMicroMeso = null;
	private Double maximalVelocity = null;
	private Double mesoscopicTimeStep = null;
	private Double microscopicTimeStep = null;
	private Double thresholdTransformationMicroMeso = null;
	private Double currentTransformationLevelMicroscopicMesoscopic = 0.0;
	private Integer scenarioLatticeId = null;
	private Integer collisionDetectionLatticeId = null;
	private Integer dynamicZoomTimeStepMultiplicator = null;
	private Double maximalDensity = null;
	private Double tolerableDensity = null;
	private Double influenceSphereMultiplicator = null;
	
	private Double timeOfLastCall = 0.0;
	Double planckTimeStep = null;
	Double transitionAreaFactor = null;
	
	ILattice collisionDetectionLattice = null;
	
	XTDensityCalculation XTDesityCalculator = null;
	DynamicZoom dynamicZoomer = null;
	
	private static ArrayList<IRichPedestrian> pedestriansInTransitionZone = null;
	
	public Double getTransitionRadiusMicroMeso() {
		
		return transitionRadiusMicroMeso;
	}
	
	public static ArrayList<IRichPedestrian> getPedestriansInTransitionZone() {
		return pedestriansInTransitionZone;
	}
	
	public List<MultiscaleArea> getMultiscaleAreas() {
		return multiscaleAreas;
	}

	public void setMultiscaleAreas(ArrayList<MultiscaleArea> multiscaleAreas) {
		this.multiscaleAreas = multiscaleAreas;
	}
	
	public ArrayList<Integer> getMicroscopicModel() {
		return microscopicModel;
	}

	public void setMicroscopicModelNames(ArrayList<Integer> microscopicModel) {
		this.microscopicModel = microscopicModel;
	}

	public ArrayList<Integer> getMesoscopicModel() {
		return mesoscopicModel;
	}

	public void setMesoscopicModelNames(ArrayList<Integer> mesoscopicModel) {
		this.mesoscopicModel = mesoscopicModel;
	}
	
	@Override
	public IPedestrianExtension onPedestrianGeneration(IRichPedestrian pedestrian) {
		
		TransiTumModelDescisionerExtension extension = null;	
		
		extension = new TransiTumModelDescisionerExtension(multiscaleAreas, pedestrian, 
				microscopicModel, mesoscopicModel);
		
		pedestrian.setMetaState(new MetaState(extension.getSimulationModels(), null, extension.getSimulationModels()));
		//pedestrian.getMetaState();
		
		return extension;

	}

	@Override
	public void onPedestrianRemoval(IRichPedestrian pedestrian) {
		
		// nothing to do
	}

	@Override
	public void executeBeforeExecute(SimulationState simulationState, Collection<IRichPedestrian> pedestrians) {
		
			XTDesityCalculator.updateDensityList(pedestrians, simulationState.getCurrentTimeStep());
			
			//System.out.println(XTDesityCalculator.getXTDensity().values());
			//System.out.println(XTDesityCalculator.getXTDensity().size());
		
			if (simulationState.getCurrentTimeStep() % dynamicZoomTimeStepMultiplicator == 0) { //execute dynamic zoom dynamic zoom
				
				HashMap<CellIndex, Double> currentDensityMap = XTDesityCalculator.getXTDensity();
				dynamicZoomer.getCurrentZoomAreas(currentDensityMap);
			//	System.out.println(simulationState.getCurrentTimeStep());
		}
		
	}

	@Override
	public void executeAfterExecute(SimulationState simulationState, Collection<IRichPedestrian> pedestrians) {
		
		timeOfLastCall = simulationState.getCurrentTime();
		
		switch (transiTumType) {
		
			case MicroscopicMesoscopic:
				
				if (nextTransformation == NextTransformation.MicroscopicMesoscopic) { // if transformation was executed
					currentTransformationLevelMicroscopicMesoscopic = currentTransformationLevelMicroscopicMesoscopic - thresholdTransformationMicroMeso;
				}
				pedestriansInTransitionZone = MicroMesoUtility.getPedestriansInTransitionZoneMicroMeso(pedestrians, transitionAreasMicroMeso);
				
			break;
		}
	}

	@Override
	public void execute(Collection<? extends IRichPedestrian> splittTask, SimulationState simulationState) {
		
		collisionDetectionLattice.getCenterPosition(LatticeTheoryFactory.createCellIndex(4,5));
		simulationState.getCurrentTime();
		switch (transiTumType) {
		    
		
			case MicroscopicMesoscopic:
				
				currentTransformationLevelMicroscopicMesoscopic = currentTransformationLevelMicroscopicMesoscopic + (simulationState.getCurrentTimeStep()* simulationState.getTimeStepDuration()) - timeOfLastCall;
				nextTransformation = MicroMesoUtility.getNextTransformation(currentTransformationLevelMicroscopicMesoscopic, mesoscopicTimeStep, transitionAreaFactor);				
				ArrayList<Pair<Integer,IRichPedestrian>> pedestriansAndTransitionAreas = new ArrayList<Pair<Integer, IRichPedestrian>>();
				
				ArrayList<ArrayList<IRichPedestrian>> pedestriansSortedByTransitionArea = new ArrayList<ArrayList<IRichPedestrian>>();
				
				while (transitionAreasMicroMeso.size() >= pedestriansSortedByTransitionArea.size()) {
					pedestriansSortedByTransitionArea.add(new ArrayList<IRichPedestrian>());					
				}
				
				if (nextTransformation == NextTransformation.None) {		
					return;
				}
					
				pedestriansAndTransitionAreas = splittTask.stream()
						.filter(ped -> MicroMesoUtility.isPedestrianInAnyTransitionArea(ped, transitionAreasMicroMeso))
						.map(ped -> new MutablePair<Integer,IRichPedestrian>(MicroMesoUtility.getPedestrianTransitionAreaNumber(ped, transitionAreasMicroMeso), ped))
						//.filter(pair -> pair.getValue0() != -1) //TODO: testen welcher filter schneller ist!
						.collect(Collectors.toCollection(ArrayList::new));
				
				pedestriansAndTransitionAreas.stream() // sort pedestrians according to their transition area
						.forEach(obj -> pedestriansSortedByTransitionArea.get(obj.getLeft()).add(obj.getRight()));
				
				
				for (int numberOfArea = 0; numberOfArea < transitionAreasMicroMeso.size(); numberOfArea++) {
					
					ArrayList<IRichPedestrian> pedestriansOfTransitionArea = pedestriansSortedByTransitionArea.get(numberOfArea);
					
					
					if (!pedestriansOfTransitionArea.isEmpty()) {
						
						ArrayList<IRichPedestrian> mesoscopicPedestrians = new ArrayList<IRichPedestrian>();
						ArrayList<IRichPedestrian> microscopicPedestrians = new ArrayList<IRichPedestrian>();
						
						for (IRichPedestrian pedestrian: pedestriansOfTransitionArea){
							
							TransiTumModelDescisionerExtension extension = (TransiTumModelDescisionerExtension) pedestrian.getExtensionState(this);
							SimulationType pedestrianSimulationType = extension.getSimulationType();
							
							if(pedestrianSimulationType == SimulationType.Mesoscopic) {
								mesoscopicPedestrians.add(pedestrian);
							}
							if(pedestrianSimulationType == SimulationType.Microscopic) {
								microscopicPedestrians.add(pedestrian);
							}
						}
						this.transformMicro2MesoPedestrians(microscopicPedestrians, mesoscopicPedestrians, transitionAreasMicroMeso.get(numberOfArea), transitionAreasMicroMeso);
						this.transformMeso2MicroPedestrians(mesoscopicPedestrians, microscopicPedestrians, transitionAreasMicroMeso.get(numberOfArea));
					}
				}
			break;
		}
	}

	@Override
	public void callPreProcessing(SimulationState simulationState) {
		
		planckTimeStep = simulationState.getTimeStepDuration();
		currentTransformationLevelMicroscopicMesoscopic = planckTimeStep;  // 0.0 describes time step from 0.0 to 0.1; therefore we need one planckTimeStep as start value!
		this.getMultiscaleAreasFromConfiguration();
		this.setTransiTumConfigurationData();
		
		switch (transiTumType) {
		
		case MicroscopicMesoscopic:
			
			transitionRadiusMicroMeso = thresholdTransformationMicroMeso * maximalVelocity;		   
			microscopicModel = properties.<Integer>getListProperty(microscopicModelIdentifier);
			mesoscopicModel = properties.<Integer>getListProperty(mesoscopicModelIdentifier);	
			break;	
		}
		transitionAreasMicroMeso = MicroMesoUtility.updateTransitionAreas(multiscaleAreas, transitionRadiusMicroMeso);
		collisionDetectionLattice = this.scenarioManager.getLattice(collisionDetectionLatticeId);
		
		XTDesityCalculator = new XTDensityCalculation(dynamicZoomTimeStepMultiplicator.longValue(), collisionDetectionLattice);
		dynamicZoomer = new DynamicZoom(collisionDetectionLattice, tolerableDensity, maximalVelocity, mesoscopicTimeStep, dynamicZoomTimeStepMultiplicator, transitionAreasMicroMeso.size(), influenceSphereMultiplicator);		
	}

	@Override
	public void callPostProcessing(SimulationState simulationState) {
		
		System.out.println("Transformed (Meso2Micro)");
		LinkedHashSet<Integer> hashsetTransformedMe2Mi = new LinkedHashSet<Integer>(transformedMe2Mi);
		System.out.println(hashsetTransformedMe2Mi.size());	
		
		System.out.println("Not Transformed (Meso2Micro)");
		LinkedHashSet<Integer>hashsetNotTransformedMe2Mi = new LinkedHashSet<Integer>(notTransformedMe2Mi);
		hashsetNotTransformedMe2Mi.removeAll(hashsetTransformedMe2Mi);
		System.out.println(hashsetNotTransformedMe2Mi.size());
		
		System.out.println("Transformed (Micro2Meso)");		
		LinkedHashSet<Integer> hashsetTransformedMi2Me = new LinkedHashSet<Integer>(transformedMi2Me);
		System.out.println(hashsetTransformedMi2Me.size());
		
		System.out.println("Not Transformed (Micro2Meso)");
		LinkedHashSet<Integer> hashsetNotTransformedMi2Me = new LinkedHashSet<Integer>(notTransformedMi2Me);
		hashsetNotTransformedMi2Me.removeAll(hashsetTransformedMi2Me);
		System.out.println(hashsetNotTransformedMi2Me.size());
	}
	
	// private methods
	private List<MultiscaleArea> getMultiscaleAreasFromConfiguration() {
		
		for (PropertyBackPack childBackPack: this.properties.getChildPropertyBackPacks()){
			
				if (childBackPack.getId() > 0) { // id = 0 -> Configuration Stuff
					
				String areaType = childBackPack.getStringProperty(areaTypeName);
				Double radius = childBackPack.getDoubleProperty(radiusName);
				Vector2D centerOfArea = GeometryFactory.createVector(childBackPack.getDoubleProperty(xPositionName),
														childBackPack.getDoubleProperty(yPositionName));
				
				multiscaleAreas.add(new MultiscaleArea(childBackPack.getId(), AreaType.valueOf(areaType), radius, centerOfArea));
			}
		}
		return null;
	}
	
	private void setTransiTumConfigurationData() {

		for (PropertyBackPack childBackPack: this.properties.getChildPropertyBackPacks()){
			
			if (childBackPack.getId().equals(0)) { // id > 0 -> multiscale Areas
				
				maximalVelocity = childBackPack.getDoubleProperty(maximalVelocityName);
				mesoscopicTimeStep = childBackPack.getIntegerProperty(mesoscopicTimeStepMultiplikatorName) * planckTimeStep;
				microscopicTimeStep = childBackPack.getIntegerProperty(microscopicTimeStepMultiplikatorName) * planckTimeStep;
				transitionAreaFactor = childBackPack.getDoubleProperty(transitionAreaFactorName);
				collisionDetectionLatticeId = childBackPack.getIntegerProperty(collisionDetectionLatticeIdName);
				scenarioLatticeId = childBackPack.getIntegerProperty(scenarioLatticeIdName);
				dynamicZoomTimeStepMultiplicator = childBackPack.getIntegerProperty(dynamicZoomTimeStepMultiplicatorName);
				maximalDensity = childBackPack.getDoubleProperty(maximalDensityName);		
				tolerableDensity = childBackPack.getDoubleProperty(tolerableDensityName);
				influenceSphereMultiplicator = childBackPack.getDoubleProperty(influenceSphereMultiplicatorName);
			}		
		}
		if (microscopicTimeStep != null && mesoscopicTimeStep !=null) {
			transiTumType = TransiTumType.MicroscopicMesoscopic;
			thresholdTransformationMicroMeso = mesoscopicTimeStep * transitionAreaFactor;
		}
	}
	
	// Micro2Meso Transformation
	private void transformMicro2MesoPedestrians(ArrayList<IRichPedestrian> microscopicPedestrians, ArrayList<IRichPedestrian> mesoscopicPedestrians, TransitionArea transitionArea, ArrayList<TransitionArea> transitionAreasMicroMeso) {
		
		ILattice mesoscopicLattice = this.scenarioManager.getLattice(scenarioLatticeId);
		
		mesoscopicPedestrians.stream()
					.forEach(ped -> mesoscopicLattice.occupyCell( mesoscopicLattice.getCellIndexFromPosition(ped.getPosition()), Occupation.Dynamic));
		
		ArrayList<IRichPedestrian> pedestriansWithConflicts = new ArrayList<IRichPedestrian>();
		
		for (IRichPedestrian microPed : microscopicPedestrians) { // test if 

			if (Micro2MesoTransformation.shouldBeTransformed(microPed, transitionArea, thresholdTransformationMicroMeso, maximalVelocity, transitionAreasMicroMeso)) {
				
				CellIndex conflictFreeCell = Micro2MesoTransformation.getNearestConflictFreeCell(mesoscopicLattice, transitionArea, microscopicPedestrians, microPed);				
				// transforms all pedestrians, which can be transformed without any conflict
				if (conflictFreeCell != null) {
					
					TransiTumModelDescisionerExtension microPedExtension = (TransiTumModelDescisionerExtension) microPed.getExtensionState(this);
					microPedExtension.setSimulationModelsAndType(mesoscopicModel, SimulationType.Mesoscopic);					
					mesoscopicLattice.occupyCell(conflictFreeCell, Occupation.Dynamic);
					
					Vector2D newPosition = mesoscopicLattice.getCenterPosition(conflictFreeCell);					
					MicroMesoUtility.updateWalkingAndStandingStates(newPosition, microPed);	
					transformedMi2Me.add(microPed.getId());
				}
				else {
					
					pedestriansWithConflicts.add(microPed);
					notTransformedMi2Me.add(microPed.getId());
				}
			}
		}	
	}
	
	// Meso2Micro Transformation
	private void transformMeso2MicroPedestrians(ArrayList<IRichPedestrian> mesoscopicPedestrians, ArrayList<IRichPedestrian> microscopicPedestrians, TransitionArea transitionArea) {
		
		ILattice mesoscopicLattice = this.scenarioManager.getLattice(scenarioLatticeId);
		
		mesoscopicPedestrians.stream()
					.forEach(ped -> mesoscopicLattice.occupyCell( mesoscopicLattice.getCellIndexFromPosition(ped.getPosition()), Occupation.Dynamic));
		
		for (IRichPedestrian mesoPed: mesoscopicPedestrians) {
			
			Vector2D positionToTransform = null;
			
			if (Meso2MicroTransformation.shouldBeTransformed(mesoPed, transitionArea, thresholdTransformationMicroMeso, maximalVelocity)) {
				
				if (Meso2MicroTransformation.isCurrentPositionFree(mesoPed, microscopicPedestrians)) {
					
					transformedMe2Mi.add(mesoPed.getId());
					positionToTransform = mesoPed.getPosition();	
					
					TransiTumModelDescisionerExtension mesoPedExtension = (TransiTumModelDescisionerExtension) mesoPed.getExtensionState(this);
					mesoPedExtension.setSimulationModelsAndType(microscopicModel, SimulationType.Microscopic);					
					mesoscopicLattice.freeCell(mesoscopicLattice.getCellIndexFromPosition(positionToTransform));
				
					MicroMesoUtility.updateWalkingAndStandingStates(positionToTransform, mesoPed);	
				}
				else {
					notTransformedMe2Mi.add(mesoPed.getId());
				//	positionToTransform = getNearestFreePositionInTransitionArea
				}
			}
		}	
	}
}
