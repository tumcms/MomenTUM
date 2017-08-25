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

package tum.cms.sim.momentum.utility.graph.buildAlgorithm.kneidlGraphBuild;

import java.util.Comparator;

import tum.cms.sim.momentum.utility.graph.Vertex;

/**
 * The three way distance compare takes a vertex and sort a set
 * of a set of other vertices according the the closest euklidian distance
 * to the given vertex. Therefore close vertices are "smaller" then distant
 * vertices.
 * 
 * @author P. Kielar, A. Kneidl
 *
 */
public class VertexThreeWayDistanceComparer implements Comparator<Vertex> {

	private Vertex vertex;    	
	
	public void setVertex(Vertex vertex) {
		this.vertex = vertex;
	}

	@Override
	public int compare(Vertex o1, Vertex o2) {

		if (vertex.getGeometry().getCenter().distance(o1.getGeometry().getCenter()) < 
			vertex.getGeometry().getCenter().distance(o2.getGeometry().getCenter())) {
			
			return -1;
		}
		else if (vertex.getGeometry().getCenter().distance(o1.getGeometry().getCenter()) > 
				 vertex.getGeometry().getCenter().distance(o2.getGeometry().getCenter())) {
			
			return 1;
		}
		else {
			
			return 0;
		}
	}
}
