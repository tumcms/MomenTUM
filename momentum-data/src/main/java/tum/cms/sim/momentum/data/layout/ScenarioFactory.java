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
import java.util.HashMap;

import tum.cms.sim.momentum.data.layout.area.Area;
import tum.cms.sim.momentum.data.layout.area.AvoidanceArea;
import tum.cms.sim.momentum.data.layout.area.DestinationArea;
import tum.cms.sim.momentum.data.layout.area.IntermediateArea;
import tum.cms.sim.momentum.data.layout.area.OriginArea;
import tum.cms.sim.momentum.data.layout.area.TaggedArea;
import tum.cms.sim.momentum.data.layout.obstacle.Obstacle;
import tum.cms.sim.momentum.data.layout.obstacle.OneWayWallObstacle;
import tum.cms.sim.momentum.data.layout.obstacle.SolidObstacle;
import tum.cms.sim.momentum.data.layout.obstacle.WallObstacle;
import tum.cms.sim.momentum.infrastructure.exception.BadConfigurationException;
import tum.cms.sim.momentum.infrastructure.logging.LoggingManager;
import tum.cms.sim.momentum.configuration.scenario.AreaConfiguration;
import tum.cms.sim.momentum.configuration.scenario.ObstacleConfiguration;
import tum.cms.sim.momentum.configuration.scenario.PointConfiguration;
import tum.cms.sim.momentum.configuration.scenario.ScenarioConfiguration;
import tum.cms.sim.momentum.configuration.scenario.TaggedAreaConfiguration;
import tum.cms.sim.momentum.utility.generic.PropertyBackPackFactory;
import tum.cms.sim.momentum.utility.geometry.GeometryFactory;
import tum.cms.sim.momentum.utility.geometry.Polygon2D;
import tum.cms.sim.momentum.utility.geometry.Segment2D;
import tum.cms.sim.momentum.utility.geometry.Vector2D;

public class ScenarioFactory {

	private ScenarioFactory() { }
	
	public static Scenario createScenario(HashMap<Integer, Scenario> scenarios,
			ScenarioConfiguration scenarioConfiguration) {
		
		Scenario scenario = new Scenario(scenarioConfiguration.getName());
		
		scenarios.put(scenarioConfiguration.getId(), scenario);	
		scenario.setPropertyBackPack(PropertyBackPackFactory.fillProperties(scenarioConfiguration));
		
		scenario.setId(scenarioConfiguration.getId());
		scenario.setMinY(scenarioConfiguration.getMinY());
		scenario.setMinX(scenarioConfiguration.getMinX());
		scenario.setMaxY(scenarioConfiguration.getMaxY());
		scenario.setMaxX(scenarioConfiguration.getMaxX());

		return scenario;
	}
	
	public static ArrayList<Obstacle> createObstacles(ArrayList<ObstacleConfiguration> obstacleConfigurations) throws Exception {
		
		ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>(); 
		
		if(obstacleConfigurations != null) {
			
			for (ObstacleConfiguration obstacleConfiguration : obstacleConfigurations) {
				
				try {
					
					Obstacle obstacle = ScenarioFactory.createObstacle(obstacleConfiguration);
					
					boolean duplicate = false;
					
					for(Obstacle created : obstacles) {
						
						if(created.getGeometry().equals(obstacle.getGeometry())) {
							
							duplicate = true;
							break;
						}
					}
					
					if(!duplicate) {
						
						obstacles.add(obstacle);
					}
					
				}
				catch(Exception ex) {
					
					throw new Exception(obstacleConfiguration.getName() + " " + ex.getMessage());
				}
			
			}
		}
		
		return obstacles;
	}
		
	private static Obstacle createObstacle(ObstacleConfiguration obstacleConfiguration) {
		
		Obstacle obstacle = null;
		
		switch(obstacleConfiguration.getType()) {
		
		case Solid:
			
			ArrayList<Vector2D> solidPoints = null;
			
			ArrayList<PointConfiguration> solidPointConfigurations = obstacleConfiguration.getPoints();
			solidPoints = new ArrayList<>();
			
			for (int k = 0; k < solidPointConfigurations.size(); k++) {
				
				solidPoints.add(GeometryFactory.createVector(solidPointConfigurations.get(k).getX(),
						solidPointConfigurations.get(k).getY()));
			}		
		
			Polygon2D polygon;
			
			try {
				
				polygon = GeometryFactory.createPolygon(solidPoints);
			
			}
			catch(java.lang.IllegalArgumentException ex) {
				
				Boolean inverse = true;
				polygon = GeometryFactory.createPolygon(solidPoints, inverse);
			}
			
			obstacle = new SolidObstacle(obstacleConfiguration.getId(), obstacleConfiguration.getName(), polygon);
			
			break;
			
		case Wall:
			
			Segment2D segmentWall = ScenarioFactory.createWall(obstacleConfiguration);
			obstacle = new WallObstacle(obstacleConfiguration.getId(), obstacleConfiguration.getName(), segmentWall);		
			break;
		
		case VirtualWall:
			
			break;
		case OneWayWall:
			
			Segment2D segmentOnWay = ScenarioFactory.createWall(obstacleConfiguration);
			OneWayWallObstacle oneWay = new OneWayWallObstacle(obstacleConfiguration.getId(), obstacleConfiguration.getName(), segmentOnWay);
			Vector2D direction = GeometryFactory.createVector(obstacleConfiguration.getDirection().getX(),
					obstacleConfiguration.getDirection().getY());
			oneWay.setDirection(direction);
			obstacle = oneWay;
			
			break;
		default:
			break;
		}
		
		return obstacle;
	}
	
	private static Segment2D createWall(ObstacleConfiguration obstacleConfiguration) {
		
		ArrayList<PointConfiguration> wallPoints = obstacleConfiguration.getPoints();
		ArrayList<Vector2D> wallCorners = new ArrayList<Vector2D>();
		
		for(PointConfiguration point : wallPoints) {
		
			wallCorners.add(GeometryFactory.createVector(point.getX(),point.getY()));
		}
		
		boolean twoTimesPoint = false;
		
		for(int iter = 0; iter < wallCorners.size() - 1; iter++) {
			
			if(wallCorners.get(iter).getXComponent() == wallCorners.get(iter + 1).getXComponent() &&
			   wallCorners.get(iter).getYComponent() == wallCorners.get(iter + 1).getYComponent()) {
				
				twoTimesPoint = true;
				break;
			}
		}
		
		if(twoTimesPoint) {
			return null;
		}
		
		return GeometryFactory.createSegment(wallCorners);
	}
	
	public static ArrayList<Area> createAreas(ArrayList<AreaConfiguration> areaConfigurations) {
		
		ArrayList<Area> areas = new ArrayList<Area>(); 
		
		if(areaConfigurations != null) {
					
			for (AreaConfiguration areaConfiguration : areaConfigurations) {
				
				try {
					
					areas.add(ScenarioFactory.createArea(areaConfiguration));
				}
				catch(Exception exception) {
					
					LoggingManager.logUser(exception);
				}
			}
		}
		
		return areas;
	}
		
	private static Area createArea(AreaConfiguration areaConfiguration) throws BadConfigurationException {
		
		Area area = null;
		
		try {
			
			switch(areaConfiguration.getType()) {
		
			case Destination:
				
				DestinationArea destinationArea = null;
				
				ArrayList<PointConfiguration> destinationPolygonPoints = areaConfiguration.getPoints();
				
				Vector2D[] destinationPolygonNodes = new Vector2D[destinationPolygonPoints.size()];
				
				for (int i = 0; i < destinationPolygonPoints.size(); i++) {
					
					destinationPolygonNodes[i] = GeometryFactory.createVector(destinationPolygonPoints.get(i).getX(), destinationPolygonPoints.get(i).getY());
				}
				
				destinationArea = new DestinationArea(areaConfiguration.getId(),
						areaConfiguration.getName(),
						GeometryFactory.createPolygon(destinationPolygonNodes),
						areaConfiguration.getCategories());
				
				area = destinationArea;
				
				break;
				
			case Intermediate:
			case Information:
				
				IntermediateArea intermediateArea = null;
					
				ArrayList<PointConfiguration> intermeidatePolygonPoints = areaConfiguration.getPoints();
				
				Vector2D[] intermediatePolygonNodes = new Vector2D[intermeidatePolygonPoints.size()];
				
				for (int i = 0; i < intermeidatePolygonPoints.size(); i++) {
					
					intermediatePolygonNodes[i] = GeometryFactory.createVector(intermeidatePolygonPoints.get(i).getX(),
							intermeidatePolygonPoints.get(i).getY());
				}
						
				intermediateArea = new IntermediateArea(areaConfiguration.getId(),
						areaConfiguration.getName(),
						GeometryFactory.createPolygon(intermediatePolygonNodes),
						areaConfiguration.getCategories());		
						
				if(areaConfiguration.getGatheringLine() != null) {
				
					ArrayList<PointConfiguration> gatheringLinePoints = areaConfiguration.getGatheringLine();
					
					Vector2D[] gatheringLineSegmentNodes = new Vector2D[gatheringLinePoints.size()];
					
					for (int i = 0; i < gatheringLinePoints.size(); i++) {
						
						gatheringLineSegmentNodes[i] = GeometryFactory.createVector(gatheringLinePoints.get(i).getX(),
								gatheringLinePoints.get(i).getY());
					}
					
					Segment2D gatheringLine = GeometryFactory.createSegment(gatheringLineSegmentNodes);
					
					intermediateArea.setGatheringSegment(gatheringLine);
				}
				
				area =  intermediateArea;
				
				break;
				
			case Origin:
				
				OriginArea originArea = null;
				
				ArrayList<PointConfiguration> originPolygonPoints = areaConfiguration.getPoints();
				
				Vector2D[] originPolygonNodes = new Vector2D[originPolygonPoints.size()];
				
				for (int i = 0; i < originPolygonPoints.size(); i++) {
					
					originPolygonNodes[i] = GeometryFactory.createVector(originPolygonPoints.get(i).getX(), 
							originPolygonPoints.get(i).getY());
				}
	
				originArea = new OriginArea(areaConfiguration.getId(),
						areaConfiguration.getName(),
						GeometryFactory.createPolygon(originPolygonNodes),
						areaConfiguration.getCategories());			
				
				originArea.setOverlappingDestination(areaConfiguration.getOverlappingArea());
	
				area = originArea;
				
				break;
					
			case Avoidance:
				AvoidanceArea avoidanceArea = null;
				
				ArrayList<PointConfiguration> avoidancePolygonPoints = areaConfiguration.getPoints();
				
				Vector2D[] avoidancePolygonNodes = new Vector2D[avoidancePolygonPoints.size()];
				
				for (int i = 0; i < avoidancePolygonPoints.size(); i++) {
					
					avoidancePolygonNodes[i] = GeometryFactory.createVector(avoidancePolygonPoints.get(i).getX(), 
							avoidancePolygonPoints.get(i).getY());
				}
				
				avoidanceArea = new AvoidanceArea(areaConfiguration.getId(),
						areaConfiguration.getName(),
						GeometryFactory.createPolygon(avoidancePolygonNodes),
						areaConfiguration.getCategories());			
				
				area = avoidanceArea;
				
				break;
			default:
				break;
			
			}
			
			if(areaConfiguration.getGatheringLine() != null) {
				ArrayList<PointConfiguration> points = areaConfiguration.getGatheringLine();
				ArrayList<Vector2D> gatheringLine = new ArrayList<Vector2D>();
				
				for(PointConfiguration point : points) {
				
					gatheringLine.add(GeometryFactory.createVector(point.getX(),point.getY()));
				}
				
				Segment2D gatheringSegment = GeometryFactory.createSegment(gatheringLine);
				
				area.setGatheringSegment(gatheringSegment);
			}
		}
		catch(Exception ex) {
		
			throw new BadConfigurationException(ex.getMessage() + ": id " +
					Integer.toString(areaConfiguration.getId()) +
					", name: " + 
					areaConfiguration.getName());
		}
		
		return area;
	}
	
	public static ArrayList<TaggedArea> createTaggedAreas(ArrayList<TaggedAreaConfiguration> taggedAreaConfigurations) {
		
		ArrayList<TaggedArea> taggedAreas = new ArrayList<TaggedArea>(); 
		
		if(taggedAreaConfigurations != null) {
					
			for (TaggedAreaConfiguration taggedAreaConfiguration : taggedAreaConfigurations) {
				
				try {
					
					taggedAreas.add(ScenarioFactory.createTaggedArea(taggedAreaConfiguration));
				}
				catch(Exception exception) {
					
					LoggingManager.logUser(exception);
				}
			}
		}
		
		return taggedAreas;
	}
	
	private static TaggedArea createTaggedArea(TaggedAreaConfiguration taggedAreaConfiguration) throws BadConfigurationException {
		
		TaggedArea taggedArea = null;
		
		ArrayList<PointConfiguration> taggedAreaPolygonPoints = taggedAreaConfiguration.getPoints();
		
		Vector2D[] taggedAreaPolygonNodes = new Vector2D[taggedAreaPolygonPoints.size()];
		
		for (int i = 0; i < taggedAreaPolygonPoints.size(); i++) {
			
			taggedAreaPolygonNodes[i] = GeometryFactory.createVector(taggedAreaPolygonPoints.get(i).getX(), 
					taggedAreaPolygonPoints.get(i).getY());
		}
		
		taggedArea = new TaggedArea(taggedAreaConfiguration.getId(),
				taggedAreaConfiguration.getName(),
				GeometryFactory.createPolygon(taggedAreaPolygonNodes),
				taggedAreaConfiguration.getCategories(),
				TaggedArea.Type.valueOf(taggedAreaConfiguration.getType().toString()));
		
		return taggedArea;
	}
}
