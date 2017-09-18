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

package tum.cms.sim.momentum.model.support.perceptional;

import java.util.List;
import java.util.stream.Collectors;

import tum.cms.sim.momentum.data.agent.pedestrian.state.other.MetaState;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrian;
import tum.cms.sim.momentum.data.layout.area.Area;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.support.PedestrianSupportModel;
import tum.cms.sim.momentum.utility.geometry.Vector2D;
import tum.cms.sim.momentum.utility.graph.Edge;
import tum.cms.sim.momentum.utility.graph.Vertex;

public abstract class PerceptionalModel extends PedestrianSupportModel {

//	public List<IPedestrian> getNearestPedestrians(IPedestrian pedestrian, double distance) {
//	
//		MetaState pedestrianMetaState = pedestrian.getMetaState();
//		
//		if (pedestrianMetaState.getCallableModelNames() == null) {
//				
//			return this.pedestrianManager.getNearestPedestriansImmutable(pedestrian, distance)
//			.stream()
//			.filter(other -> other.getId() != pedestrian.getId())
//			.collect(Collectors.toList());
//		}
//				
//		return this.pedestrianManager.getNearestPedestriansImmutable(pedestrian, distance)
//				.stream()
//				.filter(other -> pedestrianMetaState.areModelIdsVisible(other.getMetaState().getCallableModelNames()))
//				.filter(other -> other.getId() != pedestrian.getId())
//				.collect(Collectors.toList());
//	}
	
	public List<IPedestrian> getAllPedestrians(IPedestrian pedestrian) {
	
		MetaState pedestrianMetaState = pedestrian.getMetaState();
		
		if (pedestrianMetaState.getCallableModelNames() == null) {
				
			return this.pedestrianManager.getAllPedestriansImmutable()
			.stream()
			.filter(other -> other.getId() != pedestrian.getId())
			.collect(Collectors.toList());
		}
				
		return this.pedestrianManager.getAllPedestrians()
				.stream()
				.filter(other -> pedestrianMetaState.areModelIdsVisible(other.getMetaState().getCallableModelNames()))
				.filter(other -> other.getId() != pedestrian.getId())
				.collect(Collectors.toList());
	}
	
	public abstract List<IPedestrian> getPerceptedPedestrians(IPedestrian pedestrian, SimulationState simulationState);
	
	public abstract boolean isVisible(IPedestrian currentPedestrian, IPedestrian otherPedestrian);
	
	public abstract boolean isVisible(Vector2D viewPort, Vector2D position);

	public abstract boolean isVisible(IPedestrian pedestrian, List<Vector2D> positionList);
	
	//public abstract boolean isVisible(Vector2D viewPort, Area area);
	
	public abstract boolean isVisible(Vector2D viewPort, Vertex vertex);

	public abstract boolean isVisible(Vector2D viewPort, Edge edge);		
}
