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

package tum.cms.sim.momentum.data.layout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import tum.cms.sim.momentum.data.layout.area.Area;
import tum.cms.sim.momentum.data.layout.area.AvoidanceArea;
import tum.cms.sim.momentum.data.layout.area.DestinationArea;
import tum.cms.sim.momentum.data.layout.area.IntermediateArea;
import tum.cms.sim.momentum.data.layout.area.OriginArea;
import tum.cms.sim.momentum.data.layout.area.TaggedArea;
import tum.cms.sim.momentum.data.layout.obstacle.Obstacle;
import tum.cms.sim.momentum.data.layout.obstacle.OneWayWallObstacle;
import tum.cms.sim.momentum.configuration.ConfigurationManager;
import tum.cms.sim.momentum.configuration.scenario.AreaConfiguration;
import tum.cms.sim.momentum.configuration.scenario.ObstacleConfiguration;
import tum.cms.sim.momentum.configuration.scenario.ScenarioConfiguration;
import tum.cms.sim.momentum.configuration.scenario.TaggedAreaConfiguration;
import tum.cms.sim.momentum.configuration.simulation.SimulatorConfiguration;
import tum.cms.sim.momentum.utility.geometry.Vector2D;
import tum.cms.sim.momentum.utility.graph.Graph;
import tum.cms.sim.momentum.utility.lattice.ILattice;
import tum.cms.sim.momentum.utility.spaceSyntax.SpaceSyntax;

public class ScenarioManager {

	private HashMap<Integer, Scenario> scenarios = new HashMap<Integer, Scenario>();
	private Integer scenarioId = null;
	private Integer graphId = 0;
	
	public void swapCurrentGraph(Integer id) {
		
		this.graphId = id;
	}
	
	public void swapCurrentScenario(Integer id) {
		
		this.scenarioId = id;
	}
	
	public Scenario getScenarios() {
		
		return scenarios.get(this.scenarioId);
	}

	// problem with obstacles borders, thus this finds only obstacles regarding the corners and distance to the position
//	public ArrayList<Obstacle> getNearestObstacles(Vector2D position, double distance) {
//		
//		return scenarios.get(this.scenarioId).getNearestObstacles(position, distance);
//	}
	
	public ArrayList<Obstacle> getObstacles() {
		
		return scenarios.get(this.scenarioId).getObstacles();
	}
	
	public List<OneWayWallObstacle> getOneWayWalls() {
		
		return scenarios.get(this.scenarioId).getOneWayWallObstacle();
	}
	
	public List<Area> getAreas() {
		
		return scenarios.get(this.scenarioId).getAreas();
	}
	
	public Area getArea(int id) {
		
		return scenarios.get(this.scenarioId).getAreas().stream().filter(area -> area.getId().equals(id)).findFirst().orElse(null);
	}
	
	public List<DestinationArea> getDestinations() {
		
		return scenarios.get(this.scenarioId).getDestinationAreas();
	}
	
	public List<OriginArea> getOrigins() {
		
		return scenarios.get(this.scenarioId).getOriginAreas();
	}
	
	public List<IntermediateArea> getIntermediates() {
		
		return scenarios.get(this.scenarioId).getIntermediateAreas();
	}
	
	public List<AvoidanceArea> getAvoidances() {
		
		return scenarios.get(this.scenarioId).getAvoidanceAreas();
	}
	
	public List<TaggedArea> getTaggedAreas() {
		
		return scenarios.get(this.scenarioId).getTaggedAreas();
	}
	
	public List<TaggedArea> getTaggedAreas(TaggedArea.Type type) {
		
		return scenarios.get(this.scenarioId).getTaggedAreas(type);
	}

	public ILattice getLattice(int latticeId) {

		return scenarios.get(this.scenarioId).getLattices().get(latticeId);
	}
	
	public Collection<ILattice> getLattices() {
		
		return scenarios.get(this.scenarioId).getLattices().values();
	}
	
	public Graph getGraph() {
		
		return scenarios.get(this.scenarioId).getGraphs().get(this.graphId);
	}
	
	public ArrayList<Graph> getGraphs() {
		
		return scenarios.get(this.scenarioId).getGraphs();
	}
	
	public SpaceSyntax getSpaceSyntax() {
		
		return scenarios.get(this.scenarioId).getSpaceSyntaxes();
	}
	
	/**
	 * Creates all LayoutObjects from the given ScenarioConfigurations
	 * and stores them in the appropriate lists.
	 * 
	 * @param layouts
	 * @throws Exception 
	 */
	public void createLayouts(SimulatorConfiguration simulatorConfiguration) throws Exception {
		
		if(simulatorConfiguration.getLayouts() == null) {
			
			return;
		}
		
		for(int iter = 0; iter < simulatorConfiguration.getLayouts().size(); iter++) {

			ScenarioConfiguration scenarioConfiguration = null;
			
			// first load external scenario file, than create the object
			if(simulatorConfiguration.getLayouts().get(iter).getLayoutLink() != null) {
				
				scenarioConfiguration = (new ConfigurationManager()).loadExternalLayout(simulatorConfiguration.getLayouts().get(iter).getLayoutLink());
			}
			else {
				
				scenarioConfiguration = simulatorConfiguration.getLayouts().get(0);
			}
			
			if(scenarioId == null) {
				
				this.scenarioId = scenarioConfiguration.getId();
				simulatorConfiguration.getLayouts().set(iter, scenarioConfiguration);
			}
			
			this.scenarioId = scenarioConfiguration.getId();

			if(scenarioId == null) {
				
				this.scenarioId = 0;
			}
			
			Scenario scenario = ScenarioFactory.createScenario(scenarios, scenarioConfiguration);
			
			ArrayList<ObstacleConfiguration> obstacleConfigurations = scenarioConfiguration.getObstacles();			
			scenario.setObstacles(ScenarioFactory.createObstacles(obstacleConfigurations));
			
			ArrayList<AreaConfiguration> areaConfigurations = scenarioConfiguration.getAreas();
			scenario.setAreas(ScenarioFactory.createAreas(areaConfigurations));
			
			ArrayList<TaggedAreaConfiguration> taggedAreaConfigurations = scenarioConfiguration.getTaggedAreas();
			scenario.setTaggedAreas(ScenarioFactory.createTaggedAreas(taggedAreaConfigurations));
			
			for(DestinationArea destination : scenario.getDestinationAreas()) {
				
				for(OriginArea origin : scenario.getOriginAreas()) {
					
					boolean overlapping = origin.getGeometry().getVertices().stream()
												.filter(corner -> destination.getGeometry().distanceBetween(corner) < 0.01)
												.count() > 0;
					if(overlapping || destination.getGeometry().contains(origin.getGeometry().getCenter())) {
						
						destination.setOverlappingOrigin(origin.getId());
					}
					
					if(origin.getOverlappingDestination().intValue() == destination.getId().intValue()) {
						
						destination.setOverlappingOrigin(origin.getId());
					}
					else {
						
						for(Vector2D corner : origin.getGeometry().getVertices()) {
							
							if(destination.getGeometry().contains(corner)) {
								
								destination.setOverlappingOrigin(origin.getId());
								break;
							}
						}
					}
				}
		
			}
		
			for(OriginArea origin: scenario.getOriginAreas()) {
				
				for(DestinationArea destination : scenario.getDestinationAreas()) {

					boolean overlapping = destination.getGeometry().getVertices().stream()
							.filter(corner -> origin.getGeometry().distanceBetween(corner) < 0.01)
							.count() > 0;
							
					if(overlapping || origin.getGeometry().contains(destination.getGeometry().getCenter())) {
						
						origin.setOverlappingDestination(destination.getId());
					}
					if(destination.getOverlappingOrigin().intValue() == origin.getId().intValue()) {
						
						origin.setOverlappingDestination(destination.getId());
					}
					else {
						
						for(Vector2D corner : destination.getGeometry().getVertices()) {
							
							if(origin.getGeometry().contains(corner)) {
								
								origin.setOverlappingDestination(destination.getId());
								break;
							}
						}
					}
				}
			}
		}
	}
	
	public void clear() {
		
		if(this.getScenarios() != null) {
			
			this.getScenarios().clear();
		}
	}
}
