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

package tum.cms.sim.momentum.model.generator.generatorTypes;

import java.util.Collection;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import tum.cms.sim.momentum.data.agent.car.CarManager;
import tum.cms.sim.momentum.data.agent.car.state.other.StaticState;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.infrastructure.network.ChannelManager;
import tum.cms.sim.momentum.model.generator.Generator;
import tum.cms.sim.momentum.utility.geometry.GeometryFactory;
import tum.cms.sim.momentum.utility.geometry.Vector2D;

public class CarNetworkGenerator extends Generator {
		
	protected CarManager carManager = null;
	
	// TODO: not sure if allowed due to increased coupling
	private ChannelManager networkChannel = new ChannelManager();
	
	public CarManager getCarManager() {
		return carManager;
	}

	public void setCarManager(CarManager carManager) {
		this.carManager = carManager;
	}

	@Override
	public void callPreProcessing(SimulationState simulationState) {
		
		networkChannel.start(simulationState.getNetworkHostAddress(), simulationState.getNetworkDataTopicName());
	}

	@Override
	public void execute(Collection<? extends Void> splittTask, SimulationState additonalData) {
		
		//  called every time step
		
		String messageTextSimulationData = networkChannel.pullMessage();
		if( Parser.SimulationMessageToObjectType(messageTextSimulationData) == Parser.ObjectType.car) {
			
			HashMap<Integer, Parser.Car> carMap = Parser.SimulationMessageToCarList(messageTextSimulationData);
			
			for ( Parser.Car car : carMap.values()) {
				
				StaticState staticState = new StaticState( car.sizeLength, car.sizeWidth );
			    staticState.setId(car.Id);
				carManager.createCar(staticState, car.position, car.velocity, car.heading, car.validTime);
			}
		}
		
	}

	@Override
	public void callPostProcessing(SimulationState simulationState) {
		
		networkChannel.close();
	}
	
	static class Parser {
		
		// Simulation message parsing
		protected static final String SimulationDataRoot = "ObjectList";
		protected static final String SimulationDataType = "type";
		protected static final String SimulationDataTypeCar = "car";
			
		protected static final String SimulationDataId = "id";
		protected static final String SimulationDataValidTime = "time";
		protected static final String SimulationDataPositionX = "x";
		protected static final String SimulationDataPositionY = "y";
		protected static final String SimulationDataVelocityX = "xVelocity";
		protected static final String SimulationDataVelocityY = "yVelocity";
		protected static final String SimulationDataHeadingX = "xHeading";
		protected static final String SimulationDataHeadingY = "yHeading";
		//protected static final String SimulationDataAngleHeading = "angleHeading";
		//protected static final String SimulationDataAnglePitch = "anglePitch";
		//protected static final String SimulationDataAngleRoll = "angleRoll";
		protected static final String SimulationDataSizeLength = "sizeLength";
		protected static final String SimulationDataSizeWidth = "sizeWidth";
		//protected static final String SimulationDataSizeHeight = "sizeHeight";
		
		public enum ObjectType {
			car,
			pedestrian
		}
		
		public static ObjectType SimulationMessageToObjectType(String textMessage) {
			ObjectType type = ObjectType.pedestrian;
					
	        try {
	        	JSONObject jo = new JSONObject(textMessage);
	        	JSONArray parameterListJSON = jo.getJSONArray( SimulationDataRoot);
	        	
	            for (int i = 0; i < parameterListJSON.length(); i++) {

					JSONObject curObj = parameterListJSON.getJSONObject(i);
					if(curObj.getString(SimulationDataType).equals( SimulationDataTypeCar) ) {
						return ObjectType.car;
					}
	            }
	        	
	        } 
	        catch (Exception e) {
	        	return type;
	        }
	        
			return type;
		}
		
		public static class Car {
			public int Id = 0;
			public Double validTime = null;
			
			Vector2D position = null;
			Vector2D velocity = null;
			Vector2D heading = null;
			
			//public double angleHeading = 0;
			//public double anglePitch = 0;
			//public double angleRoll = 0;
			
			public double sizeLength = 0;
			public double sizeWidth = 0;
			//public double sizeHeight = 0;
			
		}
		
		public static HashMap<Integer, Car> SimulationMessageToCarList(String textMessage) {
			HashMap<Integer, Car> vehicleList = new HashMap<Integer, Car>(); 

	        try {
	        	JSONObject jo = new JSONObject(textMessage);
	        	JSONArray jsonArray = jo.getJSONArray( SimulationDataRoot);
	        	// System.out.println("JSON Array: " + jsonArray.toString() + " length=" + jsonArray.length());
	        	
	            for (int i = 0; i < jsonArray.length(); i++) {

	    			JSONObject curJsonObject = jsonArray.getJSONObject(i);
	    			Car currentCar = new Car();
	    			currentCar.Id = curJsonObject.getInt( SimulationDataId );
	    			currentCar.validTime = curJsonObject.getDouble( SimulationDataValidTime );
	    			
	    			currentCar.position = GeometryFactory.createVector( curJsonObject.getDouble(SimulationDataPositionX),
	    					curJsonObject.getDouble(SimulationDataPositionY) );
	    			currentCar.velocity = GeometryFactory.createVector( curJsonObject.getDouble(SimulationDataVelocityX),
	    					curJsonObject.getDouble(SimulationDataVelocityY) );
	    			currentCar.heading = GeometryFactory.createVector( curJsonObject.getDouble(SimulationDataHeadingX),
	    					curJsonObject.getDouble(SimulationDataHeadingY) );

	    			//currentCar.angleHeading = curJsonObject.getDouble( SimulationDataAngleHeading );
	    			//currentCar.anglePitch = curJsonObject.getDouble( SimulationDataAnglePitch );
	    			//currentCar.angleRoll = curJsonObject.getDouble( SimulationDataAngleRoll );
	    			currentCar.sizeLength = curJsonObject.getDouble( SimulationDataSizeLength );
	    			currentCar.sizeWidth = curJsonObject.getDouble( SimulationDataSizeWidth );
	    			//currentCar.sizeHeight = curJsonObject.getDouble( SimulationDataSizeHeight );
	    			
	    			vehicleList.put(currentCar.Id, currentCar);
	            }
	        	
	        } 
	        catch (Exception e) {
	        	System.out.println("Parser: SimulationMessageToCarList: Exception " + e);
	        }
	                
	        return vehicleList;
		}
	}
}
