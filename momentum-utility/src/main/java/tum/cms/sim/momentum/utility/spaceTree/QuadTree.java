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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import tum.cms.sim.momentum.utility.geometry.GeometryFactory;
import tum.cms.sim.momentum.utility.geometry.Vector2D;

/***
 * QuadTree Implementation. Doesonly support insertation of nodes. Point or Range queries
 * are not supported. The tree can be traversed by getting the root node and using succesive
 * calls of "getChild".
 * 
 * @author Sven Lauterbach (sven.lauterbach@tum.de)
 *
 * @param <NODE> Class representing an inner node
 * @param <LEAF> Class representing leaf nodes
 */
public class QuadTree<NODE extends IQuadTreeBody> {
	
	private final QuadTreeNode<NODE> treeRoot;
    private final ConcurrentHashMap<Integer, List<QuadTreeNode<NODE>>> quadtreelevels = new ConcurrentHashMap<>();
    private final IGenericFactory<NODE> factory;

    /***
     * Creates a Quadtree which spans a area of height and with and is positioned at x and y. Those coordinates are
     * needed if there is a offset. 
     * @param x x coordinate of bottom left corner of the simulation space
     * @param y y coordinate of bottom left corner of the simulation space
     * @param width width of the simulation space
     * @param height height of the simulations space
     * @param factory factory for creating inner nodes of the quadtree
     */
    public QuadTree(double x, double y, double width, double height, IGenericFactory<NODE> factory) {
    	this.factory = factory;
        final double radiusX = width * 0.5;
        final double radiusY = height * 0.5;

        final Vector2D rootPosition = GeometryFactory.createVector(x + radiusX, y + radiusY);
        final NODE rootNode = factory.create();
        treeRoot = new QuadTreeNode<NODE>(rootNode, rootPosition, Math.min(radiusX, radiusY), radiusX, radiusY);

        List<QuadTreeNode<NODE>> rootLevel = new ArrayList<QuadTreeNode<NODE>>();
        rootLevel.add(treeRoot);
        quadtreelevels.put(0,rootLevel);
    }

    /***
     * Insert a body into the quadtree.
     * @param body body to be inserted in the quadtree.
     */
    public void insert(NODE body) {
        insert(treeRoot, body, 0);
    }

    /***
     * Return the root node of the quadtree. This node can be used to traverse
     * the quadtree by using its "getChild" method.
     * 
     * @return root node
     */
    public QuadTreeNode<NODE> getRoot() {
        return treeRoot;
    }
    
    /***
     * Returns a map containing a level number ranging from 0 - <height of quadtree> as key
     * and a list of inner nodes for each level.
     * @return map of innernodes
     */
    public Map<Integer, List<QuadTreeNode<NODE>>> getTreeLevels() {
        return quadtreelevels;
    }

    @SuppressWarnings("unchecked")
	private void insert(QuadTreeNode<NODE> currentRootNode, NODE body, int level) {

        double rx = currentRootNode.getRadiusX();
        double ry = currentRootNode.getRadiusY();

        double x = -(rx * 0.5);
        double y = -(ry * 0.5);
        int quadrantInRootNode = 0;

		/*
		 * ________________
		 * |       |       |
		 * |   2   |  3    |
		 * |-------x--------
		 * |   0   |  1    |
		 * |_______|______ |
		 *
		 * the x is the center of the rootnode, so depending on the position of the body
		 * quadrantInRootNode is between 0...3
		 */
        if (currentRootNode.getNodePosition().getXComponent() < body.getPosition().getXComponent()) {
            quadrantInRootNode = 1;
            x = rx * 0.5;
        }
        if (currentRootNode.getNodePosition().getYComponent() < body.getPosition().getYComponent()) {
            quadrantInRootNode += 2;
            y = ry * 0.5;
        }

        // retrieve the node representing the selected quadrant
		QuadTreeNode<NODE> childNode = (QuadTreeNode<NODE>) currentRootNode.getChild(quadrantInRootNode);

        //...if there is no child, we add a new child node to the currentNode
        if (childNode == null) {
        	QuadTreeNode<NODE> newnode = new QuadTreeNode<NODE>(body);
            currentRootNode.addChild(newnode, quadrantInRootNode);
        } else {
        	
            //INNER child = childNode.getData();

            //..otherwise we recursivly add the body to the child if it is a inner node itself
            if (!(childNode.isLeaf())) {
                insert(childNode, body, level + 1);
            } else {
            	//....or create a new inner node if child is a body 
                Vector2D position = GeometryFactory.createVector(currentRootNode.getNodePosition().getXComponent() + x, 
                												 currentRootNode.getNodePosition().getYComponent() + y);
                NODE newNodeData = factory.create();
                
                QuadTreeNode<NODE> newNode = new QuadTreeNode<NODE>(newNodeData, position, Math.min(rx * 0.5, ry * 0.5), rx * 0.5, ry * 0.5);
                insert(newNode, body, level + 1);
                insert(newNode, childNode.getData(), level + 1);
                
                currentRootNode.addChild(newNode, quadrantInRootNode);
                quadtreelevels.compute(level+1, (key, value) -> {
                    if (value == null) {
                        value = new ArrayList<>(600);
                    }
                    value.add(newNode);
                    return value;
                });
            }
        }
    }
}
