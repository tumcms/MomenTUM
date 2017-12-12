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
import org.tensorflow.Session.Runner;

/**
 * This class wraps the Tensorflow api for usage in a neural network.
 * It further provides utility function that focus on restoring sessions
 * that where trained e.g. in Python and running computations for input data.
 * 
 * @author Peter M. Kielar
 *
 */
public class NeuralNetwork {

	/**
	 * Used for indicating incorrect naming of tensors in run. 
	 */
	private static String exceptionTensorRun = "The tensor name %s is not found in the tensorflow-graph";
	
	/**
	 * Tag for the `serving` graph.
	 * This refers to the `tags` parameter of `builder.add_meta_graph_and_variables`
	 * in the saved_model Tensorflow api
	 */
	private static String tagServe = "serve";
	
	/**
	 * The model of the neural network that was restored.
	 */
	private SavedModelBundle modelBundle;
	
	/**
	 * This list stores all names of the operation.
	 * Lazy loading.
	 */
	ArrayList<String> operationNames;
	
	/**
	 * The current session that will give a runner that executes a network and will receive a input and output tensor
	 */
	private Session session = null;
	
	/**
	 * This method restores a saved Tensorflow model.
	 *  
	 * @param pathToSavedNetworkFolder, a path that points to a folder where the model is.
	 */
	protected NeuralNetwork(String pathToSavedNetworkFolder) {
		
		this.modelBundle = SavedModelBundle.load(pathToSavedNetworkFolder, tagServe);
		this.session = this.modelBundle.session();
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
	
			Iterator<Operation> iter = this.modelBundle.graph().operations();
			
			while(iter.hasNext()) {
				
				Operation operation = iter.next();
				this.operationNames.add(operation.name());
			}
		}
		
		return this.operationNames;
	}
	
	/**
	 * Run the trained network.
	 * The inTensor needs to be filled with data and comply to the tensorflow-graph.
	 * The outTensor only needs a name and a dimension but should not contain data.
	 * The outTensor will hold new data after successful execution of this method.
	 * 
	 * This method will compute a single output tensor only.
	 * 
	 * @param inTensor
	 * @param outTensor
	 * @throws Exception 
	 */
	public void executeNetwork(NeuralTensor inTensor, NeuralTensor outTensor) throws Exception {
		
		if(!this.getOperationNames().contains(inTensor.getName())) {
		
			throw new Exception(String.format(exceptionTensorRun, inTensor.getName()));
		}
		
		if(!this.getOperationNames().contains(outTensor.getName())) {
			
			throw new Exception(String.format(exceptionTensorRun, outTensor.getName()));
		}

		List<Tensor<?>> output = this.session.runner()
				.feed(inTensor.getName(), inTensor.getTensor())
				.fetch(outTensor.getName())
				.run();	
		
		if(output.size() != 1) {
		
			// should never happen!
		}
		
		outTensor.insertData(output.get(0));
	}
	
	/**
	 * Cleanup the network objects including graph and session.
	 */
	public void close() {
		
		this.modelBundle.close();
		this.modelBundle = null;
		
		this.operationNames.clear();
		this.operationNames = null;
	}
}
