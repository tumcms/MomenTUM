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

package tum.cms.sim.momentum.model.tactical.routing.unifiedRoutingModel.underylingModels;

import org.apache.commons.math3.util.FastMath;
import tum.cms.sim.momentum.utility.geometry.Vector2D;
import tum.cms.sim.momentum.utility.geometry.operation.GeometryAdditionals;
import tum.cms.sim.momentum.utility.graph.Graph;
import tum.cms.sim.momentum.utility.graph.Vertex;

public class RightHandSideCalculator extends UnifiedIterativeCalculator {
	
	@Override
	public double calculateWeight(Graph graph, Vertex previousVisit, Vertex target, Vertex current, Vertex successor) {

		double weight = 1.0; // if 1.0 then there is no preference
		if(successor != target && previousVisit != null) {
			
			Vector2D lastVector = previousVisit.getGeometry().getCenter();
			Vector2D currentVector = current.getGeometry().getCenter();
			Vector2D nextVector = successor.getGeometry().getCenter();
			double angle = current.angleBetweenVertex(previousVisit, successor);
			
			if(angle > FastMath.PI/2.0) {
				
				angle -= FastMath.PI/2.0;
				double diff = FastMath.PI/8.0;
				if(GeometryAdditionals.isLeftOf(lastVector, currentVector, nextVector) ||
					angle > FastMath.PI/2.0 - diff) {
			
				
					weight = 0.5 + (1.0 - (angle + FastMath.PI/4.0 / (FastMath.PI/2.0 + diff))) * 0.75;
				}
				else {
					
					weight = (angle - diff) / (FastMath.PI/2.0 - diff) * 0.5;
				}	
			}

				// the lower the better
//				double angle = current.angleBetweenVertex(previousVisit, successor);
//				
//				if(angle < FastMath.PI/2.0 || angle > FastMath.PI + FastMath.PI/8.0) { //- FastMath.PI/8.0
//					
//					angle = 1.0;
//				}
//				else {
//					
//					//weight = 0.1;
//					angle -= (FastMath.PI/2.0 + FastMath.PI/8.0); // - FastMath.PI/8.0
//					weight = angle / (FastMath.PI/2.0 + FastMath.PI/8.0); //- FastMath.PI/8.0
//				}

		}
//		else {
//			
//			double currentX = current.getGeometry().getCenter().getXComponent();
//			double currentY = current.getGeometry().getCenter().getYComponent();
//			double nextX = current.getGeometry().getCenter().getXComponent();
//			double nextY = current.getGeometry().getCenter().getYComponent();
//			if(currentY < nextY) { // next above
//			
//				if(currentX < nextX) { // next is on the right
//					
//					weight = 0.25;
//				}
//				else {
//					
//					weight = 1.0;
//				}
//			}
//			else {
//				
//				if(currentX > nextX) { // next is on the right
//					
//					weight = 0.25;
//				}
//				else {
//					
//					weight = 1.0;
//				}
//			}
//		}

		return weight;
	}
}
