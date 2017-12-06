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

package tum.cms.sim.momentum.data.agent.pedestrian;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import tum.cms.sim.momentum.configuration.ModelTypConstants.ModelType;
import tum.cms.sim.momentum.data.agent.pedestrian.state.operational.StandingState;
import tum.cms.sim.momentum.data.agent.pedestrian.state.operational.WalkingState;
import tum.cms.sim.momentum.data.agent.pedestrian.state.other.MetaState;
import tum.cms.sim.momentum.data.agent.pedestrian.state.other.StaticState;
import tum.cms.sim.momentum.data.agent.pedestrian.state.strategic.StrategicalState;
import tum.cms.sim.momentum.data.agent.pedestrian.state.tactical.TacticalState.Behavior;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrian;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrianExtension;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;
import tum.cms.sim.momentum.data.layout.area.OriginArea;
import tum.cms.sim.momentum.infrastructure.execute.IUpdateState;
import tum.cms.sim.momentum.infrastructure.execute.threading.IThreadingTaskSplitter;
import tum.cms.sim.momentum.utility.geometry.Vector2D;

public class PedestrianManager implements IThreadingTaskSplitter<IRichPedestrian>, IUpdateState {
		
	private static int idSeedPedestrian = 0;

	private static int idSeedGroup = 0;
	
	/**
	 * Gets a new id for a pedestrian
	 * @return A integer id, starting at zero till infinite
	 */
	public static synchronized int getPedestrianNewId() {

		int result = ++PedestrianManager.idSeedPedestrian;
	
		return result;
	}
	
	/**
	 * Gets a new id for a group
	 * @return A integer id, starting at zero till infinite
	 */
	public static synchronized int getGroupNewId() {

		int result = ++PedestrianManager.idSeedGroup;
	
		return result;
	}

	private ArrayList<IExtendsPedestrian> extenders = new ArrayList<IExtendsPedestrian>();
	
	public void addPedestrianExtenders(IExtendsPedestrian extender) {
		
		this.extenders.add(extender);
	}

	private PedestrianContainer pedestrianContainer = new PedestrianContainer();
	
	public Collection<IPedestrian> getAllPedestriansImmutable() {
		
		return pedestrianContainer.getAllPedestriansImmutable();
	}
	
	public IPedestrian getPedestrian(Integer id) {
		
		return pedestrianContainer.getPedestrian(id);
	}
	
	public Collection<IPedestrian> getPedestrians(List<Integer> ids) {
		
		return pedestrianContainer.getPedestriansForList(ids);
	}
	
	public Collection<IRichPedestrian> getAllPedestrians() {
	
		return pedestrianContainer.getAllPedestrians();
	}
	
//	public Collection<IPedestrian> getNearestPedestriansImmutable(IPedestrian pedestrian, double distance) {
//	
//		return pedestrianContainer.getNearestPedestriansImmutable(pedestrian, distance);
//	}

	@Override
	public Collection<IRichPedestrian> getWork() {
		
		return this.getAllPedestrians();
	}
	
	@Override
	public int getWorkSize() {
		
		return pedestrianContainer.size();
	}
	
	@Override
	public Collection<? extends IRichPedestrian> getWorkForThread(int startFilterIndex, int endFilterIndex) {

		return pedestrianContainer.getPedestrainsForThread(startFilterIndex, endFilterIndex);
	}

	@Override
	public void updateState(ModelType modelType) {
		
		switch(modelType) {
		
		case Operational:
			
			pedestrianContainer.updateOperational();
			//pedestrianContainer.updateSpatialPedestrian();
			break;
			
		case Tactical:
			
			pedestrianContainer.updateTactical();
			break;
			
		case Strategic:
			
			pedestrianContainer.updateStrategic();
			break;
			
		case Meta:
		default:
			
			// nothing to do
			break;
		}
	}
	/**
	 * 
	 * Creates a new pedestrian from the scratch
	 */
	public synchronized void createPedestrian(StaticState staticState, 
			OriginArea originArea,
			Vector2D position,
			Vector2D heading,
			Double currentTime) {
		
		this.createPedestrian(staticState, originArea, position, heading, null, currentTime);
	}
		
	/**
	 * Creates a new pedestrian from the scratch
	 */
	public synchronized void createPedestrian(StaticState staticState, 
			OriginArea originArea,
			Vector2D position,
			Vector2D heading,
			Vector2D velocity,
			Double currentTime) {
		
		if(!pedestrianContainer.containsKey(staticState.getId())) {
			
			StrategicalState strategicalState = new StrategicalState(originArea, Behavior.None);
		
				
			Pedestrian pedestrian = new Pedestrian(staticState);
			pedestrian.setId(staticState.getId());
			pedestrian.setName(staticState.getId().toString());
			
			pedestrian.setStrategicalState(strategicalState);
			pedestrian.setMetaState(new MetaState(null, null, null));
			pedestrian.getMetaState().setGenerationTime(currentTime);
			
			Pedestrian afterImage = new Pedestrian(staticState);
			afterImage.setId(staticState.getId());
			afterImage.setName(staticState.getId().toString());
	
			afterImage.setStrategicalState(strategicalState);
			afterImage.setMetaState(new MetaState(null, null, null));
			afterImage.getMetaState().setGenerationTime(currentTime);
			
			if(velocity == null) {
				
				StandingState standingState = new StandingState(position, heading);
				pedestrian.setStandingState(standingState);
				afterImage.setStandingState(standingState);
			}
			else {
				
				WalkingState walkingState = new WalkingState(position, velocity, heading);
				pedestrian.setWalkingState(walkingState);
				afterImage.setWalkingState(walkingState);
			}
			
			pedestrianContainer.put(pedestrian.getId(), pedestrian, afterImage);
			
			for(IExtendsPedestrian extender : extenders) {
				
				IPedestrianExtension extension = extender.onPedestrianGeneration(pedestrian);
				pedestrian.setExtensionState(extension, extender);
				afterImage.setExtensionState(extension, extender);
			}
		}
	}

	public synchronized void removeAllPedestrians() {
		
		List<Integer> pedestriansToRemove = pedestrianContainer.getAllPedestrians().stream()
				.map(IRichPedestrian::getId)
				.collect(Collectors.toList());
		
		for(Integer pedestrianId : pedestriansToRemove) {
			
			Pedestrian pedestrianToDelete = pedestrianContainer.getPedestrian(pedestrianId);
			
			for(IExtendsPedestrian extender : extenders) {
				
				extender.onPedestrianRemoval(pedestrianToDelete);				
			}
		
			pedestrianContainer.remove(pedestrianToDelete);
		}
	}
	
	public synchronized void removePedestrian(int pedestrianId) {
		
		if(pedestrianContainer.containsKey(pedestrianId)) {
			
			Pedestrian pedestrianToDelete = pedestrianContainer.getPedestrian(pedestrianId);
			
			for(IExtendsPedestrian extender : extenders) {
				
				extender.onPedestrianRemoval(pedestrianToDelete);				
			}
		
			pedestrianContainer.remove(pedestrianToDelete);
		}
	}
	
	public synchronized void removePedestrians(List<Integer> pedestrianIds) {
		
		for(Integer removeIds : pedestrianIds) {
			
			if(pedestrianContainer.containsKey(removeIds)) {
				
				Pedestrian pedestrianToDelete = pedestrianContainer.getPedestrian(removeIds);
				
				for(IExtendsPedestrian extender : extenders) {
					
					extender.onPedestrianRemoval(pedestrianToDelete);				
				}
				
				pedestrianContainer.remove(pedestrianToDelete);
			}
		}
	}
	
	private class PedestrianContainer {
		
		public Pedestrian getPedestrian(Integer id) {
			
			return originalPedestrians.get(id);
		}
		
		public boolean containsKey(Integer id) {
			
			return originalPedestrians.containsKey(id);
		}
		
		public int size() {
			
			return originalPedestrians.size();
		}

		@SuppressWarnings("unchecked")
		public Collection<IRichPedestrian> getAllPedestrians() {
		
			return (Collection<IRichPedestrian>)((Collection<? extends IRichPedestrian>)originalPedestrians.values());
		}
		
		@SuppressWarnings("unchecked")
		public Collection<IPedestrian> getAllPedestriansImmutable() {
		
			return (Collection<IPedestrian>)((Collection<? extends IPedestrian>)afterImagePedestrians.values());
		}

		public Collection<IRichPedestrian> getPedestrainsForThread(int startFilterFromIndex, int endFilterToIndex) {
			
			return new ArrayList<IRichPedestrian>(originalPedestrians.values()).subList(startFilterFromIndex, endFilterToIndex);
		}

		public Collection<IPedestrian> getPedestriansForList(List<Integer> ids) {
			
			ArrayList<IPedestrian> pedestrianList = new ArrayList<>();
			ids.forEach(id -> pedestrianList.add(originalPedestrians.get(id)));
			return pedestrianList;
		}
		
//		public Collection<IPedestrian> getNearestPedestriansImmutable(IPedestrian pedestrian, double distance) {
//			
//			List<IPedestrian> result = null;
			
//			try {
//				
//				result = afterImageSpatialPedestrians.computeNearestEuclidean(
//						pedestrian.getPosition(),
//						distance);
//			}
//			catch (Exception exception) {
//				
//				LoggingManager.logUser(exception);
//			}
	
//			return result;
//		}
		
		public void put(Integer id, Pedestrian pedestrian, Pedestrian afterImagePedestrian) {
			
			originalPedestrians.put(id, pedestrian);
			afterImagePedestrians.put(pedestrian, afterImagePedestrian);
//			
//			try {
//				
//				afterImageSpatialPedestrians.insert(pedestrian.getPosition(), afterImagePedestrian);
//			} 
//			catch (Exception exception) {
//				
//				LoggingManager.logUser(exception);
//			}
		}
		
		public void remove(Pedestrian pedestrianToRemove) {
			
			afterImagePedestrians.remove(originalPedestrians.remove(pedestrianToRemove.getId()));
			
//			try {
//				
//				afterImageSpatialPedestrians.remove(pedestrianToRemove.getPosition());
//			} 
//			catch (Exception exception) {
//				
//				LoggingManager.logUser(exception);
//			}
		}
		
		public void updateStrategic() {
			
			afterImagePedestrians.entrySet().parallelStream()
				.forEach(entry -> entry.getValue().updateStrategicalState(entry.getKey()));
		}
		
		public void updateTactical() {

			afterImagePedestrians.entrySet().parallelStream()
				.forEach(entry -> entry.getValue().updateTacticalState(entry.getKey()));
		}
		
		public void updateOperational() {
			
			afterImagePedestrians.entrySet().parallelStream()
				.forEach(entry -> entry.getValue().updateOperationalState(entry.getKey()));
		}
		
//		public void updateSpatialPedestrian() {
//			
//			afterImageSpatialPedestrians = SpaceTreeFactory.createTreeKd(2);
//			
//			afterImagePedestrians.entrySet().parallelStream()
//				.forEach(entry -> {
//				
//					try {
//						
//						afterImageSpatialPedestrians.insert(
//								entry.getValue().getPosition(),
//								entry.getValue());
//					} 
//					catch (Exception exception) {
//						
//						LoggingManager.logUser(exception);
//					}
//			});
//		}
		
		//private TreeKD<IPedestrian> afterImageSpatialPedestrians = SpaceTreeFactory.createTreeKd(2);
		
		private HashMap<Integer, Pedestrian> originalPedestrians = new HashMap<Integer, Pedestrian>();
		private HashMap<Pedestrian, Pedestrian> afterImagePedestrians = new HashMap<Pedestrian, Pedestrian>();
	}
}
