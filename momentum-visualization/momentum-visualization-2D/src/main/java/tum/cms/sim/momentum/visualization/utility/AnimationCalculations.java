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

package tum.cms.sim.momentum.visualization.utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.util.FastMath;

import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.Transition;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.shape.Polyline;
import javafx.util.Duration;
import tum.cms.sim.momentum.configuration.model.output.WriterSourceConfiguration.OutputType;
import tum.cms.sim.momentum.configuration.scenario.ScenarioConfiguration;
import tum.cms.sim.momentum.utility.csvData.CsvType;
import tum.cms.sim.momentum.utility.csvData.reader.SimulationOutputCluster;
import tum.cms.sim.momentum.utility.csvData.reader.SimulationOutputReader;
import tum.cms.sim.momentum.visualization.enums.SpeedUp;
import tum.cms.sim.momentum.visualization.controller.CoreController;
import tum.cms.sim.momentum.visualization.controller.CustomizationController;
import tum.cms.sim.momentum.visualization.model.PlaybackModel;
import tum.cms.sim.momentum.visualization.model.custom.DensityCellModel;
import tum.cms.sim.momentum.visualization.model.custom.DensityEdgeModel;
import tum.cms.sim.momentum.visualization.model.custom.TransitAreaModel;
import tum.cms.sim.momentum.visualization.model.geometry.ObstacleModel;
import tum.cms.sim.momentum.visualization.model.geometry.PedestrianModel;
import tum.cms.sim.momentum.visualization.model.geometry.ShapeModel;

/**
 * This class contains the essentials for animations. It is the interface between
 * loaded data and updating the corresponding models that hold the data for the
 * visualization.
 * 
 * @author Martin Sigl, Peter Kielar
 *
 */
public abstract class AnimationCalculations {
	
	private static IdExtension idCalculator = new IdExtension();
	/**
	 * Calculates all active {@link SimulationOutputReader}s that contain the timestep. Returns a 
	 * {@link ParallelTransition} with all concurrent pedestrian movements
	 * and performs visualization of custom data types. 
	 * @return 
	 */
	public static ParallelTransition calculateVisualizationOfTimeStep(Double timeStep, CoreController coreController) throws Exception {
		
		coreController.waitUntilReadersReady(timeStep);
		ParallelTransition concurrentMovementsOfPedestrians = createConcurrentAnimation();
		
		for(SimulationOutputReader simReader : coreController.getOutputReaders()) {
			
			if(simReader.isDataWithContent(timeStep)) {
				
				if(CsvType.isCustomType(simReader.getCsvType())) {
					
					SimulationOutputCluster customDataForStep = simReader.asyncReadDataSet(timeStep);
					updateCustomData(customDataForStep, coreController, simReader);
				}
				
				if(simReader.getCsvType().equals(CsvType.Pedestrian)) {
					
					SimulationOutputCluster pedestrianDataForStep = createPedestrianAtTimeStep(timeStep, coreController, simReader);
							
					if(pedestrianDataForStep != null) {
						
						ParallelTransition pedestrianAnimation = AnimationCalculations.updatePedestrianShapes(simReader,
								pedestrianDataForStep,
								coreController);
						
						concurrentMovementsOfPedestrians.getChildren().add(pedestrianAnimation);
					}
				}
			}
		}

		return concurrentMovementsOfPedestrians;
	}

	private static void updateCustomData(SimulationOutputCluster dataStep, CoreController coreController, SimulationOutputReader simulationOutputReader) {
		
		ShapeModel customVisualization = null;
		PlaybackModel visualizationModel = coreController.getPlaybackController().getPlaybackModel();
		CustomizationController customizationController = coreController.getPlaybackController().getCustomizationController();
		
		Map<String, ShapeModel> customMap = visualizationModel.getSpecificCustomShapesMap(simulationOutputReader.getCsvType());
		Map<String, ShapeModel> newCustomMap = new HashMap<String, ShapeModel>();

		if (!dataStep.isEmpty()) {

			for (String id : dataStep.getIdentifications()) {

				String hashId = idCalculator.createUniqueId(id, simulationOutputReader.getFilePathHash());
				
				switch (simulationOutputReader.getCsvType()) {

				case TransitZones:

					if (!customMap.containsKey(hashId)) {

						customVisualization = getCustomShapeModel(simulationOutputReader.getCsvType(), id, customizationController);
						newCustomMap.put(hashId, customVisualization);

						TransitAreaModel transit = (TransitAreaModel) customVisualization;

						transit.createShape(dataStep.getDoubleData(id, "transitx"),
								dataStep.getDoubleData(id, "transity"), dataStep.getDoubleData(id, "radiusIn"),
								dataStep.getDoubleData(id, "radiusOut"), coreController.getCoreModel().getResolution());

						//newCustomMap.put(id, transit);
					}
					else { // set position

						customMap = visualizationModel.getSpecificCustomShapesMap(simulationOutputReader.getCsvType());
						TransitAreaModel transit = (TransitAreaModel) customMap.get(hashId);

						transit.placeShape(dataStep.getDoubleData(id, "transitx"),
								dataStep.getDoubleData(id, "transity"), dataStep.getDoubleData(id, "radiusIn"),
								dataStep.getDoubleData(id, "radiusOut"), coreController.getCoreModel().getResolution());
					}
					break;

				case MacroscopicNetwork:

					if (!customMap.containsKey(hashId)) {

						customVisualization = getCustomShapeModel(simulationOutputReader.getCsvType(), id, customizationController);
						newCustomMap.put(hashId, customVisualization);

						DensityEdgeModel densitiyEdgeModel = (DensityEdgeModel) customVisualization;

						densitiyEdgeModel.createShape(coreController.getCoreModel(),
								dataStep.getDoubleData(id, "firstNodeX"), dataStep.getDoubleData(id, "firstNodeY"),
								dataStep.getDoubleData(id, "secondNodeX"), dataStep.getDoubleData(id, "secondNodeY"),
								dataStep.getDoubleData(id, "currentDensity"), dataStep.getDoubleData(id, "width"),
								dataStep.getDoubleData(id, "maximalDensity"));

						//newCustomMap.put(id, densitiyEdgeModel);
					}
					else { // set position

						customMap = visualizationModel.getSpecificCustomShapesMap(simulationOutputReader.getCsvType());
						DensityEdgeModel densitiyEdgeModel = (DensityEdgeModel) customMap.get(hashId);

						densitiyEdgeModel.placeShape(coreController.getCoreModel(),
								dataStep.getDoubleData(id, "firstNodeX"), dataStep.getDoubleData(id, "firstNodeY"),
								dataStep.getDoubleData(id, "secondNodeX"), dataStep.getDoubleData(id, "secondNodeY"),
								dataStep.getDoubleData(id, "currentDensity"), dataStep.getDoubleData(id, "width"),
								dataStep.getDoubleData(id, "maximalDensity"));
					}
					break;

				case xtDensity:

					if (!customMap.containsKey(hashId)) {

						customVisualization = getCustomShapeModel(simulationOutputReader.getCsvType(), id, customizationController);
						newCustomMap.put(hashId, customVisualization);

						DensityCellModel densitiyEdgeModel = (DensityCellModel) customVisualization;

						densitiyEdgeModel.createShape(coreController.getCoreModel(),
								dataStep.getDoubleData(id, "cornerSize"), dataStep.getDoubleData(id, "cellCenterX"),
								dataStep.getDoubleData(id, "cellCenterY"), dataStep.getDoubleData(id, "density"),
								dataStep.getDoubleData(id, "maximalDensity"));

						//newCustomMap.put(id, densitiyEdgeModel);
					}
					else { // set position

						customMap = visualizationModel.getSpecificCustomShapesMap(simulationOutputReader.getCsvType());
						DensityCellModel densitiyEdgeModel = (DensityCellModel) customMap.get(hashId);

						densitiyEdgeModel.placeShape(dataStep.getDoubleData(id, "density"),
								dataStep.getDoubleData(id, "maximalDensity"));
					}
					break;
				default:
					break;
				}
			}
		}
		else { // if empty, no data step exists and we keep the last one
			
			// pass 
		}

		if (newCustomMap.size() > 0) {

			customMap.putAll(newCustomMap); // Update all together to improve shape update
		}

		for (ShapeModel customShape : customMap.values()) {
			customShape.setVisibility(true);
		}
	}
	
	private static ParallelTransition updatePedestrianShapes(SimulationOutputReader simulationOutputReader, SimulationOutputCluster dataStep, CoreController coreController) {
		
		PlaybackModel visualizationModel = coreController.getPlaybackController().getPlaybackModel();
		CustomizationController customizationController = coreController.getPlaybackController().getCustomizationController();

		PedestrianModel pedestrianVisualization = null;
		HashMap<String, PedestrianModel> newPedestrians = new HashMap<String, PedestrianModel>();

		double animationDurationInSecond = calculateAnimationDuration(coreController);

		ParallelTransition concurrentMovementAnimation = createConcurrentAnimation();

		// change or add pedestrian model
		if (!dataStep.isEmpty()) {

			ArrayList<Transition> pedestrianAnimations = new ArrayList<Transition>(
					dataStep.getIdentifications().size() * 2);

			for (String id : dataStep.getIdentifications()) {
				
				String hashId = idCalculator.createUniqueId(id, simulationOutputReader.getFilePathHash());

				if (!visualizationModel.getPedestrianShapes().containsKey(hashId)) {

					pedestrianVisualization = new PedestrianModel(id, hashId);
					newPedestrians.put(hashId, pedestrianVisualization);

					pedestrianVisualization.updateProperties(dataStep);

					pedestrianVisualization.createShape(dataStep.getDoubleData(id, OutputType.x.name()),
							dataStep.getDoubleData(id, OutputType.y.name()),
							dataStep.getDoubleData(id, OutputType.xHeading.name()),
							dataStep.getDoubleData(id, OutputType.yHeading.name()),
							dataStep.getDoubleData(id, OutputType.bodyRadius.name()),
							coreController.getCoreModel().getResolution(), customizationController);
				}
				else {

					pedestrianVisualization = visualizationModel.getPedestrianShapes().get(hashId);

					if (animationDurationInSecond > 0.0 && pedestrianVisualization.isVisible()
							&& animationNeeded(pedestrianVisualization, dataStep, visualizationModel)) {

						Point2D prevPoint = null;
						Point2D nextPoint = null;

						if (visualizationModel.getPreviousPedestrianPoints() != null) {

							prevPoint = visualizationModel.getPreviousPedestrianPoints().get(id);
						}

						if (visualizationModel.getOverNextPedestrianPoints() != null) {

							nextPoint = visualizationModel.getOverNextPedestrianPoints().get(id);
						}

						pedestrianVisualization.setAdjacentPlacements(prevPoint, nextPoint);

						pedestrianVisualization.updateProperties(dataStep);

						pedestrianVisualization.animateShape(pedestrianAnimations,
								dataStep.getDoubleData(id, OutputType.x.name()),
								dataStep.getDoubleData(id, OutputType.y.name()),
								dataStep.getDoubleData(id, OutputType.xHeading.name()),
								dataStep.getDoubleData(id, OutputType.yHeading.name()),
								animationDurationInSecond, coreController.getInteractionViewController()
										.getTimeLineModel().getSelectedSmoothness(),
								coreController.getCoreModel().getResolution());
					} 
					else {

						pedestrianVisualization.updateProperties(dataStep);

						pedestrianVisualization.placeShape(dataStep.getDoubleData(id, OutputType.x.name()),
								dataStep.getDoubleData(id, OutputType.y.name()),
								dataStep.getDoubleData(id, OutputType.xHeading.name()),
								dataStep.getDoubleData(id, OutputType.yHeading.name()),
								coreController.getCoreModel().getResolution());
					}
				}
			}

			concurrentMovementAnimation.getChildren().addAll(pedestrianAnimations);

			if (newPedestrians.size() > 0) {

				visualizationModel.getPedestrianShapes().putAll(newPedestrians);
			}
		}

		for (PedestrianModel pedestrianModel : visualizationModel.getPedestrianShapes().values()) {

			if (dataStep.isEmpty() || !dataStep.containsIdentification(pedestrianModel.getClusterIdentification())) {

				pedestrianModel.setVisibility(false);
			} else {

				pedestrianModel.setVisibility(true);
			}
		}

		if (concurrentMovementAnimation.getChildren().size() == 0
				&& coreController.getInteractionViewController().getTimeLineModel().getPlaying()) {

			PauseTransition waitTransition = new PauseTransition(Duration.seconds(animationDurationInSecond));
			concurrentMovementAnimation.getChildren().add(waitTransition);
		}

		return concurrentMovementAnimation;
	}

	private static boolean animationNeeded(PedestrianModel pedestrianVisualization, SimulationOutputCluster dataStep, PlaybackModel visualizationModel) {

		boolean animation = false;

		double xPosition = dataStep.getDoubleData(pedestrianVisualization.getDisplayId(), OutputType.x.name());
		double yPosition = dataStep.getDoubleData(pedestrianVisualization.getDisplayId(), OutputType.y.name());

		if ((FastMath.abs(pedestrianVisualization.getPositionX() - xPosition) > visualizationModel.getMiniForAnimation()
				|| FastMath.abs(pedestrianVisualization.getPositionY() - yPosition) > visualizationModel
						.getMiniForAnimation())
				&& (FastMath.abs(pedestrianVisualization.getPositionX() - xPosition) < visualizationModel
						.getMaxForAnimation()
						|| FastMath.abs(pedestrianVisualization.getPositionY() - yPosition) < visualizationModel
								.getMaxForAnimation())) {
			animation = true;
		}

		return animation;
	}
	
	private static double calculateAnimationDuration(CoreController coreController) {

		// important: the animation duration describes if a continous playback
		// is running.
		// if it is > 0 the concurrent movement animation will call the next
		// playback step by
		// changing the slider, this done outside because the slider must be
		// controlled.
		// Inside this function the animation duration helps to find the correct
		// interpolation and
		// animation time.
		double animationDurationInSecond = (coreController.getInteractionViewController().getTimeLineModel()
				.getTimeStepDuration()
				* coreController.getInteractionViewController().getTimeLineModel().getTimeStepMultiplicator())
				/ SpeedUp.getSpeedUp(
						coreController.getInteractionViewController().getTimeLineModel().getSelectedSpeedUp());

		animationDurationInSecond = coreController.getInteractionViewController().getTimeLineModel().getPlaying()
				? animationDurationInSecond : 0.0;

		return animationDurationInSecond;
	}
	
	private static ParallelTransition createConcurrentAnimation() {

		ParallelTransition concurrentMovementAnimation = new ParallelTransition();
		concurrentMovementAnimation.setAutoReverse(true);
		return concurrentMovementAnimation;
	}
	
	private static ShapeModel getCustomShapeModel(CsvType type, String id, CustomizationController customizationController) {

		ShapeModel ShapeModelToReturn = null;

		switch (type) {

		case TransitZones:
			ShapeModelToReturn = new TransitAreaModel(id);

			break;

		case MacroscopicNetwork:
			ShapeModelToReturn = new DensityEdgeModel(id, customizationController);

			break;

		case xtDensity:
			ShapeModelToReturn = new DensityCellModel(id);

		default:
			break;
		}
		return ShapeModelToReturn;
	}

	private static HashMap<String, Point2D> updatePedestrianPoints(SimulationOutputCluster dataStep, PlaybackModel visualizationModel) {

		Point2D updatePoint = null;
		HashMap<String, Point2D> updateList = null;

		if (dataStep != null && !dataStep.isEmpty()) {

			updateList = new HashMap<String, Point2D>();

			for (PedestrianModel pedestrianModel : visualizationModel.getPedestrianShapes().values()) {

				if (dataStep.containsIdentification(pedestrianModel.getDisplayId())) {

					updatePoint = new Point2D(dataStep.getDoubleData(pedestrianModel.getDisplayId(), OutputType.x.name()),
							dataStep.getDoubleData(pedestrianModel.getDisplayId(), OutputType.y.name())); // no
																						// resolution!
					updateList.put(pedestrianModel.getDisplayId(), updatePoint);
				}
			}
		}

		return updateList;
	}

	private static SimulationOutputCluster createPedestrianAtTimeStep(Double timeStep, CoreController coreController, SimulationOutputReader simulationOutputReader) throws Exception, InterruptedException {

		// current shapes are loaded, now buffer previous, if previous data step point exists
		if (timeStep - coreController.getInteractionViewController().getTimeLineModel().getTimeStepMultiplicator() >= 0) {
			bufferPreviousPedestrian(timeStep, coreController, simulationOutputReader);
		}

		// and next after the next step, for interpolation
		// if over next data step point exists
		if (timeStep + coreController.getInteractionViewController().getTimeLineModel()
				.getTimeStepMultiplicator() <= simulationOutputReader.getEndCluster()) { 
			bufferNextPedestrian(timeStep, coreController, simulationOutputReader);
		}
		
//		SimulationOutputCluster dataStepCurrent = null;

//		while (dataStepCurrent == null) {
//			// read current data step, load if not buffered
//			dataStepCurrent = 

		return simulationOutputReader.asyncReadDataSet(timeStep);
	}
	
//	private static void createDynamicAtTimeStep(SimulationOutputReader simulationOutputReader, Double timeStep, CoreController coreController) throws Exception {
//
//		SimulationOutputCluster dataStepCurrent = null;
//
//		while (dataStepCurrent == null) {
//
//			// read current data step, load if not buffered
//			dataStepCurrent = simulationOutputReader.asyncReadDataSet(timeStep);
//
//			if (dataStepCurrent == null) {
//
//				Thread.sleep(200);
//			}
//		}
//
//	
//	}
	
	private static void bufferPreviousPedestrian(Double timeStep, CoreController coreController, SimulationOutputReader simulationOutputReader) throws Exception {

		PlaybackModel visualizationModel = coreController.getPlaybackController().getPlaybackModel();
		SimulationOutputCluster dataStepAdjacent = null;
		double previousTimeStep = timeStep - simulationOutputReader.getTimeStepDifference();
		dataStepAdjacent = simulationOutputReader.asyncReadDataSet(previousTimeStep);
		visualizationModel.setPreviousPedestrianPoints(AnimationCalculations.updatePedestrianPoints(dataStepAdjacent, visualizationModel));
	}

	private static void bufferNextPedestrian(Double timeStep, CoreController coreController, SimulationOutputReader simulationOutputReader) throws Exception {

		SimulationOutputCluster dataStepAdjacent = null;
		PlaybackModel visualizationModel = coreController.getPlaybackController().getPlaybackModel();
		double nextTimeStep = timeStep + 2 * simulationOutputReader.getTimeStepDifference();
		dataStepAdjacent = simulationOutputReader.asyncReadDataSet(nextTimeStep);

		if (dataStepAdjacent != null) { // may be possible if not end and
										// current is last data

			visualizationModel.setOverNextPedestrianPoints(AnimationCalculations.updatePedestrianPoints(dataStepAdjacent, visualizationModel));
		}
	}
	
	public static Rectangle2D computeObstacleCenterOfGravity2D(ScenarioConfiguration scenarioConfiguration, PlaybackModel visualizationModel) {

		double minX = scenarioConfiguration.getMinX();
		double maxX = scenarioConfiguration.getMaxX();
		double minY = scenarioConfiguration.getMinY();
		double maxY = scenarioConfiguration.getMaxY();

		for (ObstacleModel obs : visualizationModel.getObstacleShapes()) {

			Polyline poly = (Polyline) obs.getObstacleBottomShape();

			for (int iter = 0; iter < poly.getPoints().size() - 2; iter = iter + 2) {

				if (poly.getPoints().get(iter) < minX)
					minX = poly.getPoints().get(iter);
				if (poly.getPoints().get(iter + 1) < minY)
					minY = poly.getPoints().get(iter + 1);
				if (poly.getPoints().get(iter) > maxX)
					maxX = poly.getPoints().get(iter);
				if (poly.getPoints().get(iter + 1) > maxY)
					maxY = poly.getPoints().get(iter + 1);
			}
		}

		Rectangle2D boundingBox = new Rectangle2D(minX, minY, maxX - minX, maxY - minY);

		return boundingBox;
	}
}
