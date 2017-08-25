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

package tum.cms.sim.momentum.configuration.model.tactical;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import tum.cms.sim.momentum.configuration.model.PedestrianBehaviorModelConfiguration;

@XStreamAlias("tactical")
public class TacticalModelConfiguration extends PedestrianBehaviorModelConfiguration {

	private StayingReference stayingReference = null;
		
	public StayingReference getStayingReference() {
		return stayingReference;
	}

	public void setStayingReference(StayingReference stayingReference) {
		this.stayingReference = stayingReference;
	}
	
	private QueuingReference queuingReference = null;
	
	public QueuingReference getQueuingReference() {
		return queuingReference;
	}

	public void setQueuingReference(QueuingReference queuingReference) {
		this.queuingReference = queuingReference;
	}

	private RoutingReference routingReference = null;

	public RoutingReference getRoutingReference() {
		return routingReference;
	}

	public void setRoutingReference(RoutingReference routingReference) {
		this.routingReference = routingReference;
	}
	
	private SearchingReference searchingReference = null;

	public SearchingReference getSerachingReference() {
		return searchingReference;
	}

	public void setSerachingReference(SearchingReference searchingReference) {
		this.searchingReference = searchingReference;
	}

	@XStreamAlias("stayingReference")
	public class StayingReference {
		
		@XStreamAsAttribute
		private Integer modelId = null;

		public Integer getModelId() {
			return modelId;
		}

		public void setModelId(Integer modelId) {
			this.modelId = modelId;
		}
	}
	
	@XStreamAlias("queuingReference")
	public class QueuingReference {
		
		@XStreamAsAttribute
		private Integer modelId = null;

		public Integer getModelId() {
			return modelId;
		}

		public void setModelId(Integer modelId) {
			this.modelId = modelId;
		}
	}

	@XStreamAlias("routingReference")
	public class RoutingReference {
		
		@XStreamAsAttribute
		private Integer modelId = null;

		public Integer getModelId() {
			return modelId;
		}

		public void setModelId(Integer modelId) {
			this.modelId = modelId;
		}
	}

	@XStreamAlias("searchingReference")
	public class SearchingReference {
		
		@XStreamAsAttribute
		private Integer modelId = null;

		public Integer getModelId() {
			return modelId;
		}

		public void setModelId(Integer modelId) {
			this.modelId = modelId;
		}
	}
}
