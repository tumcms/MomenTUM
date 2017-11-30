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

package tum.cms.sim.momentum.utility.neuralNetwork;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.tensorflow.*;

/**
 * This class wraps the Tensorflow api for usage in a neural network.
 * It further provides utility function that focus on restoring sessions
 * that where trained e.g. in Python and running computations for input data.
 * 
 * @author Peter M. Kielar
 *
 */
public class NeuralNetwork {

	private static String graphMissingException = "Cannot create session, graph object is not initialized.";
	
	/**
	 * The operation graph for processing the neural network.
	 */
	private Graph graph;
	
	/**
	 * The session environment of the neural network.
	 */
	private Session session;
	
	/**
	 * This list stores all names of the operation.
	 * Lazy loading.
	 */
	ArrayList<String> operationNames;
	
	protected NeuralNetwork() {
		
	}
	
	/**
	 * This method restores a saved Tensorflow session.
	 *  
	 * @param pathToSavedNetwork
	 */
	public void restore(String pathToSavedNetwork) {
		
		SavedModelBundle modelBundle = SavedModelBundle.load(pathToSavedNetwork);
		this.graph = modelBundle.graph();
	}
	
	/**
	 * This method start a session based on a graph.
	 *
	 * @throws Exception, in case the Graph object is not initialized.
	 */
	public void startSession() throws Exception {
		
		if(this.graph == null) {
			
			throw new Exception(graphMissingException);
		}
		
		this.session = new Session(this.graph);
	}
	
	/**
	 * Gives you all names of the operations stored in the graph.
	 * The method will collect the name only once for a graph and
	 * will return the old list in case of multiple calls.
	 * 
	 * @return List of the names of the operations of the graph
	 */
	public List<String> getOperationNames() {
		
		if(this.operationNames == null) {
			
			this.operationNames = new ArrayList<String>();
	
			Iterator<Operation> iter = this.graph.operations();
			
			while(iter.hasNext()) {
				
				Operation operation = iter.next();
				this.operationNames.add(operation.name());
			}
		}

		return this.operationNames;
	}
	
	/**
	 * Wraps numOutputs() of a graph operation from Tensorflow:
	 * Returns the number of tensors produced by this operation.
	 * 
	 * @param operationName
	 * @return The number of tensors of the operation.
	 */
	public int getOperationOutputSize(String operationName) {
		
		return this.graph.operation(operationName).numOutputs();
	}
	
	/**
	 * Cleanup the network objects including graph and session.
	 */
	public void close() {
		
		this.operationNames = null;
		this.session.close();
		this.graph.close();
	}
}
