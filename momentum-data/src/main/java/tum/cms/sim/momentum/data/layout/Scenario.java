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
import java.util.stream.Collectors;

import org.apache.commons.math3.util.FastMath;

import edu.wlu.cs.levy.CG.KDTree;
import edu.wlu.cs.levy.CG.KeySizeException;
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
import tum.cms.sim.momentum.utility.generic.IHasProperties;
import tum.cms.sim.momentum.utility.generic.PropertyBackPack;
import tum.cms.sim.momentum.utility.geometry.AxisAlignedBoundingBox2D;
import tum.cms.sim.momentum.utility.geometry.GeometryFactory;
import tum.cms.sim.momentum.utility.geometry.Polygon2D;
import tum.cms.sim.momentum.utility.geometry.Vector2D;
import tum.cms.sim.momentum.utility.graph.Graph;
import tum.cms.sim.momentum.utility.lattice.ILattice;
import tum.cms.sim.momentum.utility.spaceSyntax.SpaceSyntax;

public class Scenario extends LayoutObject implements IHasProperties {
	
	protected PropertyBackPack properties = null;
	
	@Override
	public PropertyBackPack getPropertyBackPack() {
		return properties;
	}
	
	@Override
	public void setPropertyBackPack(PropertyBackPack propertyContainer) {

		this.properties = propertyContainer; 
	}

	public Scenario(String name) {
		super(name);
	}
	
	private HashMap<Integer, ILattice> lattices = new HashMap<>();
	
	public HashMap<Integer, ILattice> getLattices() {
		return lattices;
	}
	
	public void setLattices(Collection<ILattice> latticeList) {
		
		latticeList.forEach(lattice -> this.lattices.put(lattice.getId(), lattice));
	}
	
	private ArrayList<Graph> graphs = new ArrayList<>(); 
	
	public ArrayList<Graph> getGraphs() {
		return graphs;
	}

	public void setGraphs(ArrayList<Graph> graphs) {
		this.graphs = graphs;
	}
	
	private List<SpaceSyntax> spaceSyntaxes;
	
	public List<SpaceSyntax> getSpaceSyntaxes() {
		
		if (spaceSyntaxes == null) {
			spaceSyntaxes = new ArrayList<>();
		}

		return this.spaceSyntaxes;
	}

	private KDTree<ArrayList<Obstacle>> obstacles = new KDTree<ArrayList<Obstacle>>(2);
	
	private ArrayList<Obstacle> obstacleList = new ArrayList<Obstacle>();
	
	public ArrayList<Obstacle> getNearestObstacles(Vector2D position, double distance) {
		
		double[] key = new double[2];
		
		key[0] = position.getXComponent();
		key[1] = position.getYComponent();
		
		try {
			return obstacles.nearestEuclidean(key, distance).get(0);
		} catch (KeySizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public ArrayList<Obstacle> getObstacles() {
		return obstacleList;
	}

	public void setObstacles(ArrayList<Obstacle> obstacleList) {
		
		wallObstacles = null;
		solidObstacles = null;
		oneWayWallObstacles = null;

		this.obstacleList = obstacleList;
		
		this.getWallObstacles();
		this.getSolidObstacles();
		this.getOneWayWallObstacle();
		
		this.obstacleList = new ArrayList<>();
		
		HashMap<Vector2D, ArrayList<Obstacle>> kdTreeKeyToValues = new HashMap<Vector2D, ArrayList<Obstacle>>();
		
		obstacleList.stream()
			.filter(obstacle -> !(obstacle instanceof OneWayWallObstacle))
			.forEach(obstacle ->  {
				
				this.obstacleList.add(obstacle);
				obstacle.getGeometry().getVertices().stream()
				.forEach(corner -> {
					
					if(!kdTreeKeyToValues.containsKey(corner)) {
						
						kdTreeKeyToValues.put(corner, new ArrayList<Obstacle>());
					}
					
					kdTreeKeyToValues.get(corner).add(obstacle);
				});
			});
		
		
		kdTreeKeyToValues.forEach((corner, obstacle) -> {
					
			double[] position = new double[2];
			
			position[0] = corner.getXComponent();
			position[1] = corner.getYComponent();
			
			try {
				obstacles.insert(position, obstacle);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
	
	private ArrayList<SolidObstacle> solidObstacles = null;
	
	public ArrayList<SolidObstacle> getSolidObstacles() {
		
		if(solidObstacles == null) {
			
			solidObstacles = new ArrayList<>();
			
			this.obstacleList.stream()
					.filter(obstacle -> obstacle instanceof SolidObstacle)
					.forEach(obstacle -> solidObstacles.add((SolidObstacle)obstacle));
					
		}
		
		return solidObstacles;
	}

	
	private ArrayList<WallObstacle> wallObstacles = null;
	
	public ArrayList<WallObstacle> getWallObstacles() {
		
		if(wallObstacles == null) {
			
			wallObstacles = new ArrayList<>();
			
			this.obstacleList.stream()
					.filter(obstacle -> obstacle instanceof WallObstacle)
					.filter(obstacle -> !(obstacle instanceof OneWayWallObstacle))
					.forEach(obstacle -> wallObstacles.add((WallObstacle)obstacle));
					
		}
		
		return wallObstacles;
	}

	private ArrayList<OneWayWallObstacle> oneWayWallObstacles = null;
	
	public ArrayList<OneWayWallObstacle> getOneWayWallObstacle() {
		
		if(oneWayWallObstacles == null) {
			
			oneWayWallObstacles = new ArrayList<>();
			
			this.obstacleList.stream()
					.filter(obstacle -> obstacle instanceof OneWayWallObstacle)
					.forEach(obstacle -> oneWayWallObstacles.add((OneWayWallObstacle)obstacle));
					
		}
		
		return oneWayWallObstacles;
	}

	private List<Area> areas = new ArrayList<Area>();
	
	public void setAreas(ArrayList<Area> areas) {
		this.destinationAreas = null;
		this.originAreas = null;
		
		this.intermediataAreas = null;
		
		avoidanceAreas = new ArrayList<>();
		
		areas.stream()
				.filter(area -> area instanceof AvoidanceArea)
				.forEach(area -> avoidanceAreas.add((AvoidanceArea)area));
		
		this.areas = areas.stream().filter(area -> !(area instanceof AvoidanceArea)).collect(Collectors.toList());
	}

	public List<Area> getAreas() {
		return areas;
	}

	private ArrayList<AvoidanceArea> avoidanceAreas = null;
	
	public ArrayList<AvoidanceArea> getAvoidanceAreas() {
		
		return avoidanceAreas;
	}

	private ArrayList<IntermediateArea> intermediataAreas = null;
	
	public ArrayList<IntermediateArea> getIntermediateAreas() {
		
		if(intermediataAreas == null) {
			
			intermediataAreas = new ArrayList<>();
			
			this.areas.stream()
					.filter(area -> area instanceof IntermediateArea)
					.forEach(area -> intermediataAreas.add((IntermediateArea)area));
		}
		
		return intermediataAreas;
	}

	private ArrayList<OriginArea> originAreas = null;

	public ArrayList<OriginArea> getOriginAreas() {
		
		if(originAreas == null) {
			
			originAreas = new ArrayList<>();
			
			this.areas.stream()
					.filter(area -> area instanceof OriginArea)
					.forEach(area -> originAreas.add((OriginArea)area));
		}
		
		return originAreas;
	}

	private ArrayList<DestinationArea> destinationAreas = null;
	
	public ArrayList<DestinationArea> getDestinationAreas() {
		
		if(destinationAreas == null) {
			
			destinationAreas = new ArrayList<>();
			
			this.areas.stream()
					.filter(area -> area instanceof DestinationArea)
					.forEach(area -> destinationAreas.add((DestinationArea)area));
		}
		
		return destinationAreas;
	}
	
	private ArrayList<TaggedArea> taggedAreas = null;
	
	public void setTaggedAreas(ArrayList<TaggedArea> taggedAreas) {
		this.taggedAreas = taggedAreas;
	}
	
	public ArrayList<TaggedArea> getTaggedAreas() {
		return taggedAreas;
	}
	
	public ArrayList<TaggedArea> getTaggedAreas(TaggedArea.Type type) {
		
		ArrayList<TaggedArea> selectedTaggedAreas = new ArrayList<TaggedArea>();
		
		if(taggedAreas == null) {
			taggedAreas = new ArrayList<>();
		}
		
		this.taggedAreas.stream()
			.filter(area -> area.getType() == type)
			.forEach(area -> selectedTaggedAreas.add((TaggedArea)area));
		
		return selectedTaggedAreas;
	}	
	
	private Double maxX;

	public Double getMaxX() {
		return maxX;
	}

	public void setMaxX(Double maxX) {
		this.maxX = maxX;
	}
	
	private Double maxY;

	public Double getMaxY() {
		return maxY;
	}

	public void setMaxY(Double maxY) {
		this.maxY = maxY;
	}

	private Double minX;

	public Double getMinX() {
		return minX;
	}

	public void setMinX(Double minX) {
		this.minX = minX;
	}

	private Double minY;

	public Double getMinY() {
		return minY;
	}

	public void setMinY(Double minY) {
		this.minY = minY;
	}

	public double getScenarioSize() {
		
		return FastMath.sqrt(FastMath.pow(FastMath.abs(this.maxX) + FastMath.abs(this.minX), 2) +
				FastMath.pow(FastMath.abs(this.maxY) + FastMath.abs(this.minY), 2));
	}
	
	public AxisAlignedBoundingBox2D getBoundingBox() {
		
		return GeometryFactory.createAxisAlignedBoundingBox(this.getGeometry());
	}
	
	public Polygon2D getGeometry() {

		Vector2D leftBot = GeometryFactory.createVector(minX, minY);
		Vector2D rightBot = GeometryFactory.createVector(maxX, minY);
		Vector2D rightTop = GeometryFactory.createVector(maxX, maxY);
		Vector2D leftTop = GeometryFactory.createVector(minX, maxY);
		Polygon2D boundingRectangle = null;
		
		try {
			
			boundingRectangle = GeometryFactory.createPolygon(new Vector2D[] { leftBot, rightBot, rightTop, leftTop });
		}
		catch(IllegalArgumentException iae) {
			
			boundingRectangle = GeometryFactory.createPolygon(new Vector2D[] { leftBot, leftTop, rightTop, rightBot });
		}
		
		return boundingRectangle;
	}

	public void clear() {
		
	
		if(this.areas != null) {
				
			this.areas.clear();
			this.areas = null;
		}
		
		
		if(this.avoidanceAreas != null) {
			
			this.avoidanceAreas.clear();
			this.avoidanceAreas = null;
		}
		
		if(this.intermediataAreas != null) {
			
			this.destinationAreas.clear();
			this.destinationAreas = null;
		}
		
		if(this.intermediataAreas != null) {
			
			this.intermediataAreas.clear();
			this.intermediataAreas = null;
		}
		
		if(this.lattices != null) {
			
			this.lattices.clear();
			this.lattices = null;
		}
		
		if(this.obstacleList != null) {
			
			this.obstacleList.clear();
			this.obstacleList = null;
			obstacles = new KDTree<ArrayList<Obstacle>>(2);
		}

		if(this.oneWayWallObstacles != null) {
			
			this.oneWayWallObstacles.clear();
			this.oneWayWallObstacles = null;
		}
		
		if(this.wallObstacles != null) {
			
			this.wallObstacles.clear();
			this.wallObstacles = null;
		}
	
		if(this.solidObstacles != null) {
			
			this.solidObstacles.clear();
			this.solidObstacles = null;
		}
		
		if(this.graphs != null) {
			
			this.graphs.forEach(graph -> graph.clearGraph());
			this.graphs = null;
		}
	}
}
