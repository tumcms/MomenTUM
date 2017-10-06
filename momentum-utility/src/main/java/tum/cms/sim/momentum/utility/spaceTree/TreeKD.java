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

package tum.cms.sim.momentum.utility.spaceTree;

import java.util.List;

import edu.wlu.cs.levy.CG.Checker;
import edu.wlu.cs.levy.CG.KDTree;
import edu.wlu.cs.levy.CG.KeySizeException;
import tum.cms.sim.momentum.utility.geometry.Vector2D;

/**
 * This class is a KDTree and wraps the implementation of
 * edu.wlu.cs.levy.CG.KDTree
 * 
 * @author Peter M. Kielar
 *
 * @param <T>, the type of object to store in the TreeKD
 */
public class TreeKD<T> {

	private KDTree<T> kdTree = null;
	private int dimension = -1;
	
	public TreeKD(int dimension) {
		
		kdTree = new KDTree<>(dimension);
		this.dimension = dimension;
	}
	
	public int size() {
		
		return kdTree.size();
	}
	/**
	 * This method adds new elements into the tree.
	 * 
	 * @param position, the position of the element 
	 * @param element, the to be stored element
	 * @throws If the operation failed the exception underlying tree implementation is throw
	 */
	public void insert(Vector2D position, T element) throws Exception {

		try {
			
			double[] simplePosition = new double[this.dimension];
			simplePosition[0] = position.getXComponent();
			simplePosition[1] = position.getYComponent();
	
			kdTree.insert(simplePosition, element);
		} 
		catch (Exception exception) {
			
			throw exception;
		}
	}
	
	/**
	 * This method finds all elements in the tree that are in some
	 * euclidean distance to the position.
	 * 
	 * @param position, origin regarding the distance check
	 * @param distance, maximal distance to look at
	 * @return the list of found objects
	 * @throws If the operation failed the exception underlying tree implementation is throw
	 */
	public List<T> computeNearestEuclidean(Vector2D position, double distance) throws Exception {
		
		List<T> results = null;
	
		double[] simplePosition = new double[this.dimension];
		simplePosition[0] = position.getXComponent();
		simplePosition[1] = position.getYComponent();
		
		results = kdTree.nearestEuclidean(simplePosition, distance);
		
		return results;
	}
	
	/**
	 * This method finds the number nearest neighbors of the position
	 * @param position, origin regarding the distance check
	 * @param number, how many neighbors
	 * @return the list of found objects
	 * @throws If the operation failed the exception underlying tree implementation is throw
	 */
	public List<T> computeNearestNeighbor(Vector2D position, int number, Checker<T> checker) throws Exception {

		double[] simplePosition = new double[this.dimension];
		simplePosition[0] = position.getXComponent();
		simplePosition[1] = position.getYComponent();
		
		List<T> nearest = null;
		
		if(checker != null) {
			
			nearest = this.kdTree.nearest(simplePosition, 1, checker);
		}
		else {
			
			nearest = this.kdTree.nearest(simplePosition, 1);
		}
		
		return nearest;
	}
	
	/**
	 * This method finds the nearest neighbors of the position
	 * @param position, origin regarding the distance check
	 * @return the found object
	 * @throws If the operation failed the exception underlying tree implementation is throw
	 */
	public T computeNearestNeighbor(Vector2D position, Checker<T> checker) throws Exception {

		double[] simplePosition = new double[this.dimension];
		simplePosition[0] = position.getXComponent();
		simplePosition[1] = position.getYComponent();
		
		List<T> nearest = null;
		
		if(checker != null) {
			
			nearest = this.kdTree.nearest(simplePosition, 1, checker);
		}
		else {
			
			nearest = this.kdTree.nearest(simplePosition, 1);
		}
	    
	    return nearest == null ? null : nearest.get(0);
	}
	
	/**
	 * Wraps insert and does it for a list.
	 * The index of positions correspond to the index of objects
	 * 
	 * @param positions, insert elements
	 * @param objects, the objects to insert
	 * @throws Exception 
	 */
	public void insertAll(List<Vector2D> positions, List<T> objects) throws Exception {
		
		for(int iter = 0; iter < positions.size(); iter++) {
			
			double[] simplePosition = new double[2];
			simplePosition[0] = positions.get(iter).getXComponent();
			simplePosition[1] = positions.get(iter).getYComponent();
			
			if(kdTree.search(simplePosition) == null) {
			
				this.insert(positions.get(iter), objects.get(iter));
			}
		}
	}
	
	/**
	 * This method removes an element from the tree based on the element's position.
	 * However, the element is only marked as deleted for performance reasons.
	 * 
	 * @param elementsPosition, position of the element to remove
	 * @throws If the operation failed the exception underlying tree implementation is throw
	 */
	public void remove(Vector2D elementsPosition) throws Exception {
		
		double[] simplePosition = new double[2];
		simplePosition[0] = elementsPosition.getXComponent();
		simplePosition[1] = elementsPosition.getYComponent();
		
		kdTree.delete(simplePosition);
	}
	
	/**
	 * This method finds an element in the tree.
	 * 
	 * @param elementsPosition
	 * @return The element or null if not found
	 * @throws KeySizeException
	 */
	
	public T searchFor(Vector2D elementsPosition) throws KeySizeException {
		
		double[] simplePosition = new double[2];
		simplePosition[0] = elementsPosition.getXComponent();
		simplePosition[1] = elementsPosition.getYComponent();
		
		return kdTree.search(simplePosition);
	}
}
