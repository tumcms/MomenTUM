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

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("vertex")
public class VertexConfiguration {

	@XStreamAsAttribute
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XStreamAlias("center")
	private PointConfiguration point = null;

	public PointConfiguration getPoint() {
		return point;
	}

	public void setPoint(PointConfiguration point) {
		this.point = point;
	}
	
	@XStreamAsAttribute
	private Integer id = null;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
//	@XStreamAsAttribute
//	private AreaType type;
//
//	public AreaType getType() {
//		return type;
//	}
//
//	public void setType(AreaType type) {
//		this.type = type;
//	} 
//	
//	@XStreamAsAttribute
//	private Integer area;
//
//	public Integer getArea() {
//		return area;
//	}
//
//	public void setArea(Integer area) {
//		this.area = area;
//	}
	
	
//	@XStreamAlias("diameter")
//	private DiameterConfiguration diameterConfiguration;
//
//	public DiameterConfiguration getDiameterConfiguration() {
//		return diameterConfiguration;
//	}
//
//	public void setDiameterConfiguration(DiameterConfiguration diameterConfiguration) {
//		this.diameterConfiguration = diameterConfiguration;
//	}
}
