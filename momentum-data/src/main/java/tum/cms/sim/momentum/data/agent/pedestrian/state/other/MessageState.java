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

package tum.cms.sim.momentum.data.agent.pedestrian.state.other;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Save a message as state and export it the csv for the visualizer
 *
 */
public class MessageState {

	private static String messageSeparatorString = "MSGSEP";
	private static String topicSeparatorString = "TOPSEP";

	private static String contentSeparatorString = ", ";

	private HashMap<String, String> messages = new HashMap<String, String>();

	public void clear() {
		this.messages.clear();
	}

	public void clearTopic(String topic) {
		this.messages.remove(topic);
	}

	public void addMessage(String topic, String messageContent) {
		this.messages.put(topic, messageContent);
	}

	public void appendMessage(String topic, String messageAppendance) {

		String existing = messages.get(topic);
		messages.put(topic, existing == null ? messageAppendance : existing + contentSeparatorString + messageAppendance);
	}

	public HashMap<String, String> getAllMessages() {
		return messages;
	}

	public String getMessageString() {
		String messageString = "";

		Iterator it = messages.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry)it.next();
			messageString += pair.getKey() + topicSeparatorString + pair.getValue();
			if(it.hasNext()) {
				messageString += messageSeparatorString;
			}
			it.remove();
		}

		return messageString;
	}
}
