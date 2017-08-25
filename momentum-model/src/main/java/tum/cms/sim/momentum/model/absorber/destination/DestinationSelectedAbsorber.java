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

package tum.cms.sim.momentum.model.absorber.destination;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.utility.geometry.Geometry2D;

public class DestinationSelectedAbsorber extends DestinationAbsorber {

	private static String vanishTimeName = "vanishTime"; 
	
	private double vanishTime = 0.1;
	private HashMap<Integer, Double> vanishFadePedestrians = new HashMap<Integer, Double>();
	
	public DestinationSelectedAbsorber(Integer destinationId) {
		super(destinationId);
	}
	
	@Override
	public void callPreProcessing(SimulationState simulationState) {	
		super.callPreProcessing(simulationState);
		
		this.vanishTime = this.properties.getDoubleProperty(vanishTimeName);
	}
	
//	private static Integer leftPedestrians = 0;
	@Override
	public void execute(Collection<? extends Void> splittTask, SimulationState simulationState) {
		
		lastRemovedCount = 0;
		
		Geometry2D destinationGeometry = this.area.getGeometry();
		
		List<Integer> visitingPedestrianIds = this.pedestrianManager
				.getAllPedestrians()
				.stream()
				.filter(pedestrian -> destinationGeometry.contains(pedestrian.getPosition()))
				.filter(pedestrian -> pedestrian.getStrategicalState() != null)
				.filter(pedestrian -> pedestrian.getStrategicalState().getNextTargetArea() != null)
				.filter(pedestrian -> pedestrian.getStrategicalState().getNextTargetArea().getId().equals(this.destinationId))
				.map(IRichPedestrian::getGroupId)
				.collect(Collectors.toList());

		for(Integer pedestrianId : visitingPedestrianIds) {
			
			if(this.vanishFadePedestrians.containsKey(pedestrianId)) {
				
				double currentVanishTime = this.vanishFadePedestrians.get(pedestrianId);
				vanishFadePedestrians.put(pedestrianId, currentVanishTime - simulationState.getTimeStepDuration());
			}
			else {
				
				vanishFadePedestrians.put(pedestrianId, this.vanishTime);
			}
		}
		
		List<Integer> removeList = vanishFadePedestrians.entrySet().stream()
			.filter(entry -> entry.getValue() <= 0.0)
			.map(entry -> entry.getKey())
			.collect(Collectors.toList());
		
		if(removeList.size() > 0) {
		
			ArrayList<Integer> pedestrianRemoveId = new ArrayList<>();
			
			for(IRichPedestrian pedestrian : this.pedestrianManager.getAllPedestrians()) {
				
				for(Integer removeGroupId : removeList) {
					
					if(removeGroupId.equals(pedestrian.getGroupId())) {
						
						pedestrianRemoveId.add(pedestrian.getId());
					}
				}
			}

			lastRemovedCount = pedestrianRemoveId.size();  
			this.pedestrianManager.removePedestrians(pedestrianRemoveId);
			
			removeList.forEach(removed -> vanishFadePedestrians.remove(removed));
		}
	}

	@Override
	public void callPostProcessing(SimulationState simulationState) {
		// nothing to do
	}
}

