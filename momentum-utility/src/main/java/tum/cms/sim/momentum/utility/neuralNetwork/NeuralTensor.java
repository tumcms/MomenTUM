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

import java.nio.Buffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.tensorflow.Tensor;

/**
 * This class wraps the Tensorflow api for tensors in neural network usage.
 * However, this class contains strong simplification and helps to define
 * the input and output tensors for a network.
 * 
 * @author Peter M. Kielar
 *
 */
public class NeuralTensor {
	
	/**
	 * @see NeuralTensor#getName
	 */
	private String name;
		
	/**
	 * In order to use a tensor as input or output of a neural network
	 * it is extremely useful to give it a name. The can be found in the
	 * Tensorflow graph of the neural network. 
	 * The name of the tensor should be exactly the name of the corresponding
	 * tensor in the restored graph.
	 * 
	 * @return the name of the tensor
	 */
	public String getName() {
		return name;
	}

	/**
	 * @see NeuralTensor#getDimension
	 */
	private long[] dimension;

	/**
	 * The dimension of the tensor should be set exactly to the dimension of
	 * the corresponding tensor in the graph that was restored.
	 * Each entry in the dimension array shows the size of the dimensions
	 * E.g [2,3] means 2 elements first and 3 elements in the second dimension.
	 */
	public long[] getDimension() {
		return dimension;
	}

	/**
	 * The data buffer will hold the data of the tensor. 
	 * Because we do not handle the types the user have to provide a specific
	 * Buffer (e.g. FloatBuffer).  The size of this buffer is the multiplication
	 * of all elements of the {@link NeuralTensor#dimension}
	 *  @see Buffer
	 */
	private Buffer data; 

	/**
	 * The tensor of the Tensorflow api.
	 * @see Tensor
	 */
	private Tensor<?> tensor;
	
	/**
	 * The getter of the tensor is on package level in order
	 * to let the NeuralNetwork use the tensor content for computations.
	 * @return
	 */
	Tensor<?> getTensor() {
		return tensor;
	}

	/**
	 * Create a tensor object.
	 * @param name
	 * @param dimension
	 */
	NeuralTensor(String name, long[] dimension)  {
		
		this.name = name;
		this.dimension = dimension;
	}

	/**
	 * Fills integer data into the tensor. The data must have the shape
	 * as defined in the number of elements as dimension defines.
	 * Thus, a [2,3] tensor will be initialized with a [6] array.
	 * 
	 * This method will generate the data buffer based on the given type.
	 * 
	 * @param Data as array
	 */
	public void fill(int[] data) {
		
		long size = 1;
		
		for(int iter = 0; iter < dimension.length; iter++) {
			
			size *= dimension[iter];
		}
		
		IntBuffer intData =  IntBuffer.allocate((int)size);
		this.data = intData;
		this.tensor = Tensor.create(this.dimension, intData);
	}
	
	/**
	 * Fills double data into the tensor. The data must have the shape
	 * as defined in the number of elements as dimension defines.
	 * Thus, a [2,3] tensor will be initialized with a [6] array.
	 * 
	 * This method will generate the data buffer based on the given type.
	 * 
	 * @param Data as array
	 */
	public void fill(double[] data) {
		
		long size = 1;
		
		for(int iter = 0; iter < dimension.length; iter++) {
			
			size *= dimension[iter];
		}
		
		DoubleBuffer doubleData = DoubleBuffer.allocate((int)size);
		this.data = doubleData;
		this.tensor = Tensor.create(this.dimension, doubleData);
	}
	
	/**
	 * Fills float data into the tensor. The data must have the shape
	 * as defined in the number of elements as dimension defines.
	 * Thus, a [2,3] tensor will be initialized with a [6] array.
	 * 
	 * This method will generate the data buffer based on the given type.
	 * 
	 * @param Data as array
	 */
	public void fill(float[] data) {
		
		long size = 1;
		
		for(int iter = 0; iter < dimension.length; iter++) {
			
			size *= dimension[iter];
		}
		
		FloatBuffer floatData = FloatBuffer.allocate((int)size);
		this.data = floatData;
		this.tensor = Tensor.create(this.dimension, floatData);
	}
	
	/**
	 * This package method is used by e.g. NeuralNetwork to infuse
	 * data into the tensor
	 */
	void insertData(Tensor<?> data) {
		
		this.tensor = data;
	}
	
	/**
	 * This method returns the content of the tensors as
	 * flat 1-D integer array. In order to translate the array
	 * back into the multiple dimensions, use {@link NeuralTensor#getDimension}.
	 */
	public int[] getIntegerData() {
		
		if(this.data == null) {

			IntBuffer intData = IntBuffer.allocate((int)this.tensor.numElements());
			this.data = intData;
			this.tensor.writeTo(intData);
		}

		return (int[]) this.data.array();
	}
	
	/**
	 * This method returns the content of the tensors as
	 * flat 1-D double array. In order to translate the array
	 * back into the multiple dimensions, use {@link NeuralTensor#getDimension}.
	 */
	public double[] getDoubleData() {
		
		if(this.data == null) {

			DoubleBuffer doubleData = DoubleBuffer.allocate((int)this.tensor.numElements());
			this.data = doubleData;
			this.tensor.writeTo(doubleData);
		}

		return (double[]) this.data.array();
	}
	
	/**
	 * This method returns the content of the tensors as
	 * flat 1-D float array. In order to translate the array
	 * back into the multiple dimensions, use {@link NeuralTensor#getDimension}.
	 */
	public float[] getFloatData() {
		
		if(this.data == null) {

			FloatBuffer floatData = FloatBuffer.allocate((int)this.tensor.numElements());
			this.data = floatData;
			this.tensor.writeTo(floatData);
		}

		return (float[]) this.data.array();
	}
	
	/**
	 * Clean up after usage of the tensor.
	 */
	public void close() {
		
		this.tensor.close();
		this.data.clear();
	}
}
