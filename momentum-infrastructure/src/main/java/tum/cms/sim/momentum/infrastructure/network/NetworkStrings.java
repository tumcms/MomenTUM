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

public class NetworkStrings {

	// Logging
	protected static final String BrokerStarted = "Network: Broker started, clients can connect now";
	protected static final String WaitingForStart = "Network: Waiting for start event";
	protected static final String StartingSimulation = "Network: Starting simulation";
	
	protected static final String EventReceivedPause = "Network: Received pause event";
	protected static final String EventReceivedFinish = "Network: Received finish event";
	protected static final String EventReceivedSynchronize = "Network: Received synchronize event";
	
	// Control message parsing
	protected static final String CommandPrefix = "MomenTUMv2.";
	protected static final String CommandSimulate = "Simulate";
	protected static final String CommandPause = "Pause";
	protected static final String CommandFinish = "Finish";
	protected static final String CommandSynchronize = "Synchronize";
	protected static final String CommandNetowrk = "Network";
	
	protected static final String EventTag = "Event";
	protected static final String ParameterTag = "Parameter";
	
	private NetworkStrings () { }
}
