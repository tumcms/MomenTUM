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

package tum.cms.sim.momentum.infrastructure.network;

import tum.cms.sim.momentum.infrastructure.network.NetworkStrings;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Parser {
	
	public static class ControlMessage {
		
		ControlMessage(MessageType messageType, MessageCommand messageCommand, HashMap<String, String> parameter) {
//			this.Type = messageType;
			this.Command = messageCommand;
			this.Parameter = parameter;
		}
		
		public enum MessageType {
			none,
		    event
		}
		
		public enum MessageCommand {
			none,
			simulate,
			pause,
			finish,
			synchronize,
			network
		}
		
//		private MessageType Type = MessageType.none;
		private MessageCommand Command = MessageCommand.none;
		private HashMap<String, String> Parameter = new HashMap<String, String>();
		
//		public MessageType getMessageType() {
//			return this.Type;
//		}
		
		public MessageCommand getMessageCommand() {
			return this.Command;
		}

//		public HashMap<String, String> getParameter() {
//			return this.Parameter;
//		}
		
		/**
		 * Returns the simulation time since start of simulation.
		 * @return in [s].
		 */
		public double getSystemTime() {
			return Double.parseDouble(Parameter.get("SystemTime"));
		}
		
		/**
		 * Returns the received measured latence.
		 * @return measured latence in [s].
		 */
		public double getLatence() {
			double latence = 0;
			if (Parameter.containsKey("Latence")) {
				latence = Double.parseDouble(Parameter.get("Latence"));
			}
			return latence;
		}
		
	}

	/**
	 * Parse a received text message and convert it to an object
	 * @param textMessage received serialized message
	 * @return object containing the information
	 */
	public ControlMessage parseTextMessage(String textMessage) {

		ControlMessage.MessageType messageType = ControlMessage.MessageType.none;
		ControlMessage.MessageCommand messageCommand = ControlMessage.MessageCommand.none;
		HashMap<String, String> parameter = new HashMap<String, String>();
        
        JSONArray parameterListJSON;
        
        try {
        	JSONObject jo = new JSONObject(textMessage);
        	messageType = ControlMessage.MessageType.event;
        	
        	// controlMessage.Command = jo.getString("Event");
        	switch (jo.getString( NetworkStrings.EventTag )) {
	            case NetworkStrings.CommandPrefix + NetworkStrings.CommandSimulate:
	            	messageCommand = ControlMessage.MessageCommand.simulate;
	                break;
	            case NetworkStrings.CommandPrefix + NetworkStrings.CommandPause:
	            	messageCommand = ControlMessage.MessageCommand.pause;
	                break;
	            case NetworkStrings.CommandPrefix + NetworkStrings.CommandFinish:
	            	messageCommand = ControlMessage.MessageCommand.finish;
	                break;
	            case NetworkStrings.CommandPrefix + NetworkStrings.CommandSynchronize:
	            	messageCommand = ControlMessage.MessageCommand.synchronize;
	                break;
	            case NetworkStrings.CommandPrefix + NetworkStrings.CommandNetowrk:
	            	messageCommand = ControlMessage.MessageCommand.network;
	                break;
	            default:
	            	messageCommand = ControlMessage.MessageCommand.none;
	                break;
        	}
        	
        	parameterListJSON = jo.getJSONArray(NetworkStrings.ParameterTag);
        	
        	
        } 
        catch (Exception e) {
        	return new ControlMessage(messageType, messageCommand, parameter);
        }


        for (int i = 0; i < parameterListJSON.length(); i++) {
			try {
				JSONObject curObj = parameterListJSON.getJSONObject(i);
	        	@SuppressWarnings("unchecked")
				Iterator<String> keysItr = curObj.keys();
	        	String key = keysItr.next();
	        	String value = curObj.getString(key);
	 	        
	        	parameter.put(key, value);
			} 
			catch (JSONException e) {
				e.printStackTrace();
			}
        }
        
        return new ControlMessage(messageType, messageCommand, parameter);
	}
		
}
