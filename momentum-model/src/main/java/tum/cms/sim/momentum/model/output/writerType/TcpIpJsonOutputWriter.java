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

package tum.cms.sim.momentum.model.output.writerType;

import tum.cms.sim.momentum.model.output.OutputWriter;

public class TcpIpJsonOutputWriter extends OutputWriter {
	
//	private final static String hostAddressString = "hostAddress";
//	private final static String networkTopicString = "networkTopic";
//	private final static String orderListString = "order";
//
//	private WriterSource<String> writerSource = null;
//	private ArrayList<String> headerList = null;
//	
//	private ActiveMQConnectionFactory connectionFactory = null;
//	private Connection connection = null;
//	private Session session = null;
//	private Destination destination = null;
//	private MessageProducer messageProducer = null;
//	
//	private long currentTimeStep;
//	private double currentTime;
//
//	@Override
//	public void initialize() {
//		// System.out.println("tcpIpWriter: initialize");
//		
//		try {
//	        // Create a ConnectionFactory
//			connectionFactory = new ActiveMQConnectionFactory(this.properties.getStringProperty(hostAddressString));
//	        // Create a Connection
//	        connection = connectionFactory.createConnection();
//	        connection.start();
//	        // Create a Session
//	        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
//	        // Create the destination (Topic or Queue)
//	        destination = session.createTopic(this.properties.getStringProperty(networkTopicString));
//	        // Create a MessageConsumer from the Session to the Topic or Queue
//	        messageProducer = session.createProducer(destination);
//	        
//		} catch (Exception e) {
//            e.printStackTrace();
//		}
//		
//		headerList = this.properties.<String>getListProperty(orderListString);
//		// System.out.println(headerList);
//	}
//
//	@Override
//	public void write() {
//		
//		writerSource.hasNextSet();
//		
//		
//		writerSource.hasNextSet();
//		int valueSetSize = writerSource.loadSet();
//		
//		JSONObject jsonMessage = new JSONObject();
//		jsonMessage.put("ObjectType", "Pedestrian");
//		jsonMessage.put("TimeStep", currentTimeStep);
//		jsonMessage.put("Time", currentTime);
//		
//		
//		JSONArray entryList = new JSONArray();
//		while(valueSetSize-- > 0) {
//			
//			writerSource.loadSetItem();
//
//			JSONObject currentEntry = readDataSource();
//			//System.out.println(currentEntry.toString());
//			
//			entryList.put(currentEntry);
//		}
//		
//		jsonMessage.put("ObjectList", entryList);
//		
//		//System.out.println(jsonMessage.toString());
//		
//        try {
//        	TextMessage message = session.createTextMessage(jsonMessage.toString());
//        	messageProducer.send(message);
//        } catch (Exception e) {
//	        e.printStackTrace();
//	    }
//	}
//
//	@Override
//	public void flush() {
//		// System.out.println("tcpIpWriter: flush");
//		
//		try {
//			messageProducer.close();
//			session.close();
//			connection.close();
//		} catch (JMSException e) {
//			e.printStackTrace();
//		}
//	}
//
//	@Override
//	public void setWriterSource(WriterSource<?> writerSource) {
//		this.writerSource = (WriterSource<String>) writerSource;
//	}
//	
//	public void setTimeInformation(long currentTimeStep, double currentTime) {
//		this.currentTimeStep = currentTimeStep;
//		this.currentTime = currentTime;
//	}
//	
//	private JSONObject readDataSource() {
//		JSONObject currentData = new JSONObject();
//		
//		String item = null;
//		String itemContent = null;
//		
//		for(int iter = 0; iter < headerList.size(); iter++) {
//			item = headerList.get(iter);
//			itemContent = this.writerSource.readSingleValue(item);
//			
//			if(itemContent != null) {
//				currentData.put(item, itemContent);
//			}
//		}
//		
//		return currentData;
//	}

}
