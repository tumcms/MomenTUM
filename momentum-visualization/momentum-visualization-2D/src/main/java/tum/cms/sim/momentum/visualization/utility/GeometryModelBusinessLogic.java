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

import javafx.collections.FXCollections;
import tum.cms.sim.momentum.configuration.scenario.EdgeConfiguration;
import tum.cms.sim.momentum.configuration.scenario.ScenarioConfiguration;
import tum.cms.sim.momentum.configuration.scenario.VertexConfiguration;
import tum.cms.sim.momentum.visualization.controller.CoreController;
import tum.cms.sim.momentum.visualization.controller.CustomizationController;
import tum.cms.sim.momentum.visualization.controller.PlaybackController;
import tum.cms.sim.momentum.visualization.model.geometry.AreaModel;
import tum.cms.sim.momentum.visualization.model.geometry.EdgeModel;
import tum.cms.sim.momentum.visualization.model.geometry.LatticeModel;
import tum.cms.sim.momentum.visualization.model.geometry.ObstacleModel;
import tum.cms.sim.momentum.visualization.model.geometry.VertexModel;

public abstract class GeometryModelBusinessLogic {

	public static void createLayout(ScenarioConfiguration scenarioConfiguration, CoreController coreController) {

		CustomizationController customizationController = coreController.getPlaybackController().getCustomizationController();
		PlaybackController playbackController = coreController.getPlaybackController();
		
		playbackController.getPlaybackModel().setMaxSizeX(scenarioConfiguration.getMaxX()); // without
		// resolution!
		playbackController.getPlaybackModel().setMaxSizeY(scenarioConfiguration.getMaxY()); // without
		// resolution!

		// 1. lattice
		ArrayList<LatticeModel> tempLatticeList = new ArrayList<LatticeModel>();

		if (scenarioConfiguration.getLattices() != null) {
			// paints only the first lattice
			tempLatticeList.add(new LatticeModel(scenarioConfiguration.getLattices().get(0), scenarioConfiguration,
					coreController, customizationController, playbackController.getVisibilitiyModel()));
		}

		playbackController.getPlaybackModel().getLatticeShapes().clear();
		playbackController.getPlaybackModel().addLatticeShapes(FXCollections.observableArrayList(tempLatticeList));

		// 2. areas
		HashMap<String, AreaModel> tempAreaMap = new HashMap<String, AreaModel>();

		if (scenarioConfiguration.getAreas() != null) {

			scenarioConfiguration.getAreas().forEach(areaConfiguration -> {
				AreaModel currentAreaModel = new AreaModel(areaConfiguration, coreController, customizationController);
				tempAreaMap.put(currentAreaModel.getIdentification(), currentAreaModel);
			});
		}

		playbackController.getPlaybackModel().getAreaShapes().clear();
		playbackController.getPlaybackModel().putAreaShapes(tempAreaMap);

		// 3. graph
		if (scenarioConfiguration.getGraphs() != null) {

			HashMap<String, EdgeModel> tempEdgeMap = new HashMap<String, EdgeModel>();
			HashMap<Integer, VertexModel> tempVertexMap = new HashMap<Integer, VertexModel>();

			if (scenarioConfiguration.getGraphs().get(0).getVertices() != null) {

				scenarioConfiguration.getGraphs().forEach(graphConfig -> {
					for (VertexConfiguration vertex : graphConfig.getVertices()) {
						tempVertexMap.put(vertex.getId(), new VertexModel(graphConfig, coreController,
								customizationController, playbackController.getVisibilitiyModel(), vertex));
					}

					if(graphConfig.getEdges() != null) {
						for (EdgeConfiguration edge : graphConfig.getEdges()) {
							EdgeModel currentEdgeModel = new EdgeModel(coreController, customizationController,
									playbackController.getVisibilitiyModel(), tempVertexMap.get(edge.getIdLeft()),
									tempVertexMap.get(edge.getIdRight()));
	
							tempEdgeMap.put(currentEdgeModel.getIdentification(), currentEdgeModel);
	
							tempVertexMap.get(edge.getIdLeft()).addAdjacentEdge(currentEdgeModel);
							tempVertexMap.get(edge.getIdRight()).addAdjacentEdge(currentEdgeModel);
						}
					}
				});
			}
			
			playbackController.getPlaybackModel().getEdgeShapes().clear();

			if(tempEdgeMap != null) {
				playbackController.getPlaybackModel().putEdgeShapes(tempEdgeMap);
			}

			playbackController.getPlaybackModel().getVertexShapes().clear();
			playbackController.getPlaybackModel().putVertexShapes(tempVertexMap);
		}

		// 4. obstacles
		ArrayList<ObstacleModel> tempObstacleList = new ArrayList<ObstacleModel>();

		if (scenarioConfiguration.getObstacles() != null) {

			scenarioConfiguration.getObstacles().forEach(obstacleConfiguration -> tempObstacleList
					.add(new ObstacleModel(obstacleConfiguration, coreController, customizationController)));
		}

		playbackController.getPlaybackModel().getObstacleShapes().clear();
		playbackController.getPlaybackModel().addObstacleShapes(FXCollections.observableArrayList(tempObstacleList));
	}
}
