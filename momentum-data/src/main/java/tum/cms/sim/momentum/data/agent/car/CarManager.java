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

package tum.cms.sim.momentum.data.agent.car;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import tum.cms.sim.momentum.data.agent.car.state.operational.MovementState;
import tum.cms.sim.momentum.data.agent.car.state.other.StaticState;
import tum.cms.sim.momentum.data.agent.car.types.IRichCar;
import tum.cms.sim.momentum.utility.geometry.Vector2D;

public class CarManager {

	private CarContainer carContainer = new CarContainer();
	
	/**
	 * Creates a new car from the scratch
	 */
	public synchronized void createCar(StaticState staticState,
			Vector2D position,
			Vector2D velocity,
			Vector2D heading,
			Double currentTime) {

		createCar(staticState);
		updateState(staticState.getId(), position, velocity, heading, currentTime);
	}

	public synchronized void createCar(StaticState staticState) {

		Car newCar = new Car(staticState);
		newCar.setId(staticState.getId());
		newCar.setName(staticState.getId().toString());

		if(!carContainer.containsKey(newCar.getId())) {
			// add a new car
			carContainer.put(newCar.getId(), newCar);
		}
	}

	public synchronized void updateState(int id,
									   Vector2D position,
									   Vector2D velocity,
									   Vector2D heading,
									   Double currentTime) {

		MovementState movementState = new MovementState(position, velocity, heading);
		this.carContainer.getCar(id).setMovementState(movementState);
	}
	
	public synchronized void removeCar(int carId) {
		if(carContainer.containsKey(carId)) {
			carContainer.remove(carId);
		}
	}
	
	public synchronized void removeCars(List<Integer> carIds) {
		
		for(Integer removeIds : carIds) {
			
			if(carContainer.containsKey(removeIds)) {
				Car carToDelete = carContainer.getCar(removeIds);
				carContainer.remove(carToDelete);
			}
		}
	}

	
	public Collection<IRichCar> getAllCars() {
		return carContainer.getAllCars();
	}

	public boolean containsCar(int id) {return carContainer.containsKey(id); }

	public ArrayList<Integer> getAllCarIds()  {return carContainer.getAllCarIds(); }


	private class CarContainer {
		
		public Car getCar(Integer id) {
			return originalCars.get(id);
		}
		
		public boolean containsKey(Integer id) {
			return originalCars.containsKey(id);
		}

		public void put(Integer id, Car car) {
			originalCars.put(id, car);
		}
		
		public void remove(Integer id) {
			originalCars.remove(id);
		}
		
		public void remove(Car car) {
			originalCars.remove(car.state.staticState.getId());
		}
		
		@SuppressWarnings("unchecked")
		public Collection<IRichCar> getAllCars() {
			return (Collection<IRichCar>)((Collection<? extends IRichCar>)originalCars.values());
		}

		public ArrayList<Integer> getAllCarIds() {
			ArrayList<Integer> idList = new ArrayList<Integer>();

			for (Car curCar : this.originalCars.values()) {
				idList.add(curCar.getId());
			}
			return idList;
		};

		
		private HashMap<Integer, Car> originalCars = new HashMap<Integer, Car>();
	}
}
