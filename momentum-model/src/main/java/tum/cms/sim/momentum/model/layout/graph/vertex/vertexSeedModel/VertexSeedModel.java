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

package tum.cms.sim.momentum.model.layout.graph.vertex.vertexSeedModel;

import java.util.ArrayList;

import tum.cms.sim.momentum.data.layout.area.Area;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.layout.graph.GraphOperation;
import tum.cms.sim.momentum.utility.graph.GraphTheoryFactory;

/**
 * Instantiated by GraphType <code>VertexCreateSeedBased</code>
 *
 */
public class VertexSeedModel extends GraphOperation {

	@Override
	public void callPreProcessing(SimulationState simulationState) {
	
		ArrayList<Area> areas = new ArrayList<>();
		areas.addAll(this.scenarioManager.getOrigins());
		areas.addAll(this.scenarioManager.getIntermediates());
		areas.addAll(this.scenarioManager.getDestinations());
		
		areas.stream()
				.map(Area::getGeometry)
				.map(areaGeometry -> GraphTheoryFactory.createVertex(areaGeometry, true))
				.forEach(areaVertex -> this.scenarioManager.getGraph().addVertex(areaVertex));
	}

	@Override
	public void callPostProcessing(SimulationState simulationState) {
		
		// nothing to do	
	}
}
