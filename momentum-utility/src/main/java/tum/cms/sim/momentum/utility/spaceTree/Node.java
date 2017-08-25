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

import java.util.HashMap;

/***
 * Simple Class for representing a generic tree whose nodes can have an 
 * arbitary number of childs
 * 
 * @author Sven Lauterbach (sven.lauterbach@tum.de)
 *
 * @param <T> Type which represents the data hold by a node
 */
public class Node<T> {

    private T data;
    protected HashMap<Integer, Node<? extends T>> childs = new HashMap<>();

    /***
     * Create a Node holding the data
     * @param data Data for the node
     */
    public Node(T data) {
        this.data = data;
    }

	/***
     * Return the data of the node.
     * @return Data of the node
     */
    public T getData() {
        return data;
    }

    /***
     * Adds a child node to this node.
     * @param child Child node to add
     * @param index Index of the child
     */
    public void addChild(Node<? extends T> child, int index) {
        childs.put(index, child);
    }

    /***
     * Returns a child with the specifed index.
     * @param i Index of the Child to retrieve.
     * @return Child node.
     */
    public Node<? extends T> getChild(int i) {    	
    	return childs.get(i);    	
    }
}
