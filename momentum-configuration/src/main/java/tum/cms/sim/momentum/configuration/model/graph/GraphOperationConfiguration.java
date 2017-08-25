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

package tum.cms.sim.momentum.configuration.model.graph;

import java.util.HashMap;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.converters.enums.EnumToStringConverter;

import tum.cms.sim.momentum.configuration.generic.PropertyContainerNode;

@XStreamAlias("graphOperation")
public class GraphOperationConfiguration extends PropertyContainerNode {

	public enum GraphType {

		VertexCreateSeedBased,
		VertexCreateAtCorners,
		VertexCreateAtCornersEnriched,
		VertexCreateVoronoiBased,
		VertexCreateAtIntersections,
		VertexCreateMedialAxis,
		
		VertexRemoveVisibilityBased,
		VertexRemoveSimple,
		VertexRemoveAlongLine,
		
		EdgeCreateVisibilityConnect, 
		EdgeCreateVisibilityAngleBased,
		EdgeCreateVisibilityAngleBasedReduced,
		EdgeCreateOvermarsUseful,
		
		EdgeRemoveMST,
		EdgeRemoveUnreachable, 
		EdgeRemoveOneWay,
		
		RawGraph,
		ToConfiguration,
		FromConfiguration
	}
	
	@SuppressWarnings("rawtypes")
	public static EnumToStringConverter getTypeConverter() {
		
		HashMap<String, GraphType> map = new HashMap<>();
		map.put(GraphType.VertexCreateSeedBased.toString(), GraphType.VertexCreateSeedBased);
		map.put(GraphType.VertexCreateAtCorners.toString(), GraphType.VertexCreateAtCorners);
		map.put(GraphType.VertexCreateAtCornersEnriched.toString(), GraphType.VertexCreateAtCornersEnriched);
		map.put(GraphType.VertexCreateVoronoiBased.toString(), GraphType.VertexCreateVoronoiBased);
		map.put(GraphType.VertexCreateAtIntersections.toString(), GraphType.VertexCreateAtIntersections);
		map.put(GraphType.VertexCreateMedialAxis.toString(), GraphType.VertexCreateMedialAxis);
		
		map.put(GraphType.VertexRemoveVisibilityBased.toString(), GraphType.VertexRemoveVisibilityBased);
		map.put(GraphType.VertexRemoveSimple.toString(), GraphType.VertexRemoveSimple);
		map.put(GraphType.VertexRemoveAlongLine.toString(), GraphType.VertexRemoveAlongLine);
		
		map.put(GraphType.EdgeCreateVisibilityConnect.toString(), GraphType.EdgeCreateVisibilityConnect);
		map.put(GraphType.EdgeCreateVisibilityAngleBased.toString(), GraphType.EdgeCreateVisibilityAngleBased);
		map.put(GraphType.EdgeCreateVisibilityAngleBasedReduced.toString(), GraphType.EdgeCreateVisibilityAngleBasedReduced);
		map.put(GraphType.EdgeCreateOvermarsUseful.toString(), GraphType.EdgeCreateOvermarsUseful);
		
		map.put(GraphType.EdgeRemoveMST.toString(), GraphType.EdgeRemoveMST);
		map.put(GraphType.EdgeRemoveUnreachable.toString(), GraphType.EdgeRemoveUnreachable);
		map.put(GraphType.EdgeRemoveOneWay.toString(), GraphType.EdgeRemoveOneWay);
		
		map.put(GraphType.RawGraph.toString(), GraphType.RawGraph);
		map.put(GraphType.ToConfiguration.toString(), GraphType.ToConfiguration);
		map.put(GraphType.FromConfiguration.toString(), GraphType.FromConfiguration);
		
		return new EnumToStringConverter<>(GraphType.class, map);
	}
	
	@XStreamAsAttribute
	private GraphType type;

	public GraphType getType() {
		return type;
	}

	public void setType(GraphType type) {
		this.type = type;
	}
	
	@Deprecated
	@XStreamAsAttribute
	private Integer order;
	
	@Deprecated
	public Integer getOrder() {
		return order;
	}
	
	@Deprecated
	public void setOrder(Integer order) {
		this.order = order;
	}
}
