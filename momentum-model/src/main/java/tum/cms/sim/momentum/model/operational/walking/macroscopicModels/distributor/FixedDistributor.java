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

package tum.cms.sim.momentum.model.operational.walking.macroscopicModels.distributor;


import tum.cms.sim.momentum.model.operational.walking.macroscopicModels.data.Density;
import tum.cms.sim.momentum.model.operational.walking.macroscopicModels.data.MacroscopicEdge;
import tum.cms.sim.momentum.model.operational.walking.macroscopicModels.data.MacroscopicNetwork;
import tum.cms.sim.momentum.model.operational.walking.macroscopicModels.data.MacroscopicNode;

public class FixedDistributor implements IDistributor {
	
	protected FixedDistributor() {
		
	}

	@Override
	public void computeWeights(MacroscopicNetwork n) {
		//This function is currently unused. We use the same density for all outgoing edges
	}
	
	@Override
	public void distribute(MacroscopicNode n, MacroscopicNetwork macroscopicNetwork, double dxApprox) {
		
		if(n.getOut().isEmpty() == true) {
			return;
		}
		
		if(n.getDensity().getAmount() < 0) {
			return;
		}
		
		Density collectedDensity = n.getDensity().clone();
		Density densityToSubtract = collectedDensity.clone();
		
		//all the density is divided by (total width * dx)
		double totalWidth = 0;
		
		for(int j=0; j < n.getOut().size(); j++) {
			totalWidth = totalWidth + n.getOut().get(j).getWidth();
		}
		
		Density densityToDistribute = collectedDensity.getScaledDensity(1/(n.getArea() * dxApprox));

		for(int i=0; i<n.getOut().size(); i++) {
			MacroscopicEdge out = n.getOut().get(i);   
			double maxCapacityAdjacent = out.getMaximalDensity() - out.getDensity()[1].getAmount();
			double friction = 1;

			if(densityToDistribute.getAmount() > maxCapacityAdjacent) {
				n.setFull(true);

				friction = maxCapacityAdjacent/densityToDistribute.getAmount();
				densityToDistribute = densityToDistribute.getScaledDensity(friction);
				out.getDensity()[1].Add(densityToDistribute);
				
				densityToSubtract.Subtract(densityToDistribute.getScaledDensity(out.getWidth() * dxApprox));
			}
			
			else {
				
				n.setFull(false);

				out.getDensity()[1].Add(densityToDistribute);
				
				densityToSubtract.Subtract(densityToDistribute.getScaledDensity(out.getWidth() * dxApprox));			
			}			
		}
		n.setDensity(densityToSubtract);
	}
	
	public boolean reachable(MacroscopicNode start, MacroscopicNode end, MacroscopicNetwork MacroscopicNetwork) {
		
		return true;  
	}
}
