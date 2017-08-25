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

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class ChannelManager {

	private String hostAddress = null;
	private String channelName = null;
	
	private Connection connection = null;
	private Session session = null;
	private Destination destination = null;
	private MessageConsumer messageConsumer = null;
	private MessageProducer messageProducer = null;

	public void start(String hostAddress, String channelName) {
		this.hostAddress = hostAddress;
		this.channelName = channelName;
		
		try {
	        // Create a ConnectionFactory
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(this.hostAddress);

	        // Create a Connection
			connection = connectionFactory.createConnection();
	        connection.start();

	        // Create a Session
	        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

	        // Create the destination (Topic or Queue)
	        destination = session.createTopic(this.channelName);
	        
	        // Create a MessageConsumer from the Session to the Topic or Queue
	        messageConsumer = session.createConsumer(destination);
	        messageProducer = session.createProducer(destination);
	        
		} catch (Exception e) {
            e.printStackTrace();
		}
	}
	
	public void close() {
		try {
			messageProducer.close();
			messageConsumer.close();
			session.close();
			connection.close();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	public String pullMessage() {
		String receivedMessageText = "";
		
        try {
        	Message message = messageConsumer.receiveNoWait();
            if (message instanceof TextMessage) {
            	TextMessage textMessage = (TextMessage) message;
    	        receivedMessageText = textMessage.getText();
            }
			
		} catch (JMSException e1) {
			e1.printStackTrace();
		}
                
        return receivedMessageText;
	}
	
	public void sendMessage(String message) {
		try {
        	TextMessage textMessage = session.createTextMessage(message);
        	messageProducer.send(textMessage);
        }
		 catch (Exception e) {
	        e.printStackTrace();
	    }
	}
}
