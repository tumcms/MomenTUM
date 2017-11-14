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

package tum.cms.sim.momentum.model.meta.transitum;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrianExtension;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;
import tum.cms.sim.momentum.model.meta.transitum.data.MultiscaleArea;
import tum.cms.sim.momentum.model.meta.transitum.data.MultiscaleArea.AreaType;
import tum.cms.sim.momentum.utility.geometry.Vector2D;

// gives the model (operational, tactical, strategical) that belongs to this pedestrian
public class TransiTumModelDescisionerExtension implements IPedestrianExtension {
	
	private HashSet<Integer> simulationModels = new HashSet<>();
	private SimulationType simulationType = null;
	
	public enum SimulationType {
		
		Mesoscopic,
		Microscopic
	}
	
	public HashSet<Integer> getSimulationModels() {
		return simulationModels;
	}
	
	public SimulationType getSimulationType() {
		return simulationType;
	}
	
	public void setSimulationModelsAndType(ArrayList<Integer> simulationModels, SimulationType simulationType) {
		
		this.simulationType = simulationType;
		this.simulationModels.clear();
		this.simulationModels.addAll(simulationModels);
	}

	public TransiTumModelDescisionerExtension(List<MultiscaleArea> multiscaleAreas, IRichPedestrian pedestrian,
			ArrayList<Integer> microscopicModel, ArrayList<Integer> mesoscopicModel) {
	
		
		Vector2D pedestrianPosition = pedestrian.getPosition();
		AreaType areaType = null;
		
		for (MultiscaleArea area: multiscaleAreas) {
			
			if (pedestrianPosition.distance(area.getCenterOfArea()) <= area.getRadiusOfArea()) {
				
				areaType = area.getAreaType();
			}
		}
		if (areaType == null) {
			areaType = AreaType.Mesoscopic;
		}		
		if (areaType.equals(AreaType.Microscopic)) {
			
			this.simulationModels.addAll(microscopicModel);
			this.simulationType = SimulationType.Microscopic;
		}
		if (areaType.equals(AreaType.Mesoscopic)) {
			
			this.simulationModels.addAll(mesoscopicModel);
			this.simulationType = SimulationType.Mesoscopic;
		}
	}
}
