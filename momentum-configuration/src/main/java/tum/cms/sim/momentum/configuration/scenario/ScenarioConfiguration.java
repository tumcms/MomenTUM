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

package tum.cms.sim.momentum.configuration.scenario;

import java.util.ArrayList;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import tum.cms.sim.momentum.configuration.generic.PropertyContainerNode;
import tum.cms.sim.momentum.configuration.model.lattice.LatticeModelConfiguration;

@XStreamAlias("scenario")
public class ScenarioConfiguration extends PropertyContainerNode {
	
	@XStreamAsAttribute
	private Double maxX;

	public Double getMaxX() {
		return maxX;
	}

	public void setMaxX(Double maxX) {
		this.maxX = maxX;
	}
	
	@XStreamAsAttribute
	private Double maxY;

	public Double getMaxY() {
		return maxY;
	}

	public void setMaxY(Double maxY) {
		this.maxY = maxY;
	}
	
	@XStreamAsAttribute
	private Double minX;

	public Double getMinX() {
		return minX;
	}

	public void setMinX(Double minX) {
		this.minX = minX;
	}

	@XStreamAsAttribute
	private Double minY;

	public Double getMinY() {
		return minY;
	}

	public void setMinY(Double minY) {
		this.minY = minY;
	}
	
	@XStreamAsAttribute
	private String layoutLink;

	public String getLayoutLink() {
		return layoutLink;
	}

	public void setLayoutLink(String layoutLink) {
		this.layoutLink = layoutLink;
	}
	
	@XStreamImplicit
	private ArrayList<GraphScenarioConfiguration> graphs;
	
	public ArrayList<GraphScenarioConfiguration> getGraphs() {
		
		return graphs;
	}

	public void setGraphs(ArrayList<GraphScenarioConfiguration> graphs) {
		this.graphs = graphs;
	}

	@XStreamImplicit
	private ArrayList<AreaConfiguration> areas;

	public ArrayList<AreaConfiguration> getAreas() {
		return areas;
	}

	public void setAreas(ArrayList<AreaConfiguration> areas) {
		this.areas = areas;
	}
	
	@XStreamImplicit
	private ArrayList<TaggedAreaConfiguration> taggedAreas;

	public ArrayList<TaggedAreaConfiguration> getTaggedAreas() {
		return taggedAreas;
	}

	public void setTaggedAreas(ArrayList<TaggedAreaConfiguration> taggedAreas) {
		this.taggedAreas = taggedAreas;
	}
	
	@XStreamImplicit
	private ArrayList<ObstacleConfiguration> obstacles;

	public ArrayList<ObstacleConfiguration> getObstacles() {
		return obstacles;
	}

	public void setObstacles(ArrayList<ObstacleConfiguration> obstacles) {
		this.obstacles = obstacles;
	}
	
	@XStreamImplicit
	private ArrayList<LatticeModelConfiguration> lattices = null;
		
	public  ArrayList<LatticeModelConfiguration> getLattices() {
		return lattices;
	}

	public void setLattices(ArrayList<LatticeModelConfiguration> lattices) {
		
		this.lattices = lattices;
	}
}
