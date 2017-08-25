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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.converters.enums.EnumToStringConverter;

import tum.cms.sim.momentum.configuration.generic.NameIdNodeConfiguration;
import tum.cms.sim.momentum.configuration.generic.NameNodeConfiguration;

@XStreamAlias("taggedArea")
public class TaggedAreaConfiguration extends NameIdNodeConfiguration {

	public enum TaggedAreaType {

		Crosswalk,
		Sidewalk
		
	}

	@SuppressWarnings("rawtypes")
	public static EnumToStringConverter getTypeConverter() {

		HashMap<String, TaggedAreaType> map = new HashMap<>();
		map.put(TaggedAreaType.Crosswalk.toString(), TaggedAreaType.Crosswalk);
		map.put(TaggedAreaType.Sidewalk.toString(), TaggedAreaType.Sidewalk);
		
		return new EnumToStringConverter<>(TaggedAreaType.class, map);
	}

	@XStreamAsAttribute
	private TaggedAreaType type;

	public TaggedAreaType getType() {
		return type;
	}

	public void setType(TaggedAreaType type) {
		this.type = type;
	}
	
	@XStreamAsAttribute
	private Integer overlappingArea = null;
	
	public Integer getOverlappingArea() {
		return overlappingArea;
	}

	public void setOverlappingArea(Integer overlappingArea) {
		this.overlappingArea = overlappingArea;
	}

	@XStreamImplicit
	private ArrayList<PointConfiguration> points;

	public ArrayList<PointConfiguration> getPoints() {

		return points;
	}

	public void setPoints(ArrayList<PointConfiguration> points) {
		this.points = points;
	}

	private ArrayList<PointConfiguration> gatheringLine = null;

	public ArrayList<PointConfiguration> getGatheringLine() {
		return gatheringLine;
	}

	public void setGatheringLine(ArrayList<PointConfiguration> gatheringLine) {
		this.gatheringLine = gatheringLine;
	}


	@XStreamAsAttribute
	private String category = null;
	
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
	@XStreamImplicit
	@XStreamAlias("category")
	private ArrayList<NameNodeConfiguration> categories = null;

	public List<String> getCategories() {
		return categories != null ? categories.stream().map(NameNodeConfiguration::getName).collect(Collectors.toList()) : Collections.emptyList();
	}

	public void setCategories(ArrayList<NameNodeConfiguration> categories) {
		this.categories = categories;
	}
}
