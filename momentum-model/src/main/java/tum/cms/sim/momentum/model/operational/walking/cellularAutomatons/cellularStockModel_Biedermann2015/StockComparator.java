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

package tum.cms.sim.momentum.model.operational.walking.cellularAutomatons.cellularStockModel_Biedermann2015;

import java.util.Comparator;

import tum.cms.sim.momentum.data.agent.pedestrian.IExtendsPedestrian;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;

/**
 * Comparator for comparing two Pedestrians by their stock vlaue 
 * 
 * @author Sven Lauterbach (sven.lauterbach@tum.de)
 */
class StockComparator implements Comparator<IRichPedestrian> {

	private IExtendsPedestrian model;

	/**
	 * Creates a Comparator which gets the ExtensionState containing the stock value from the supplied model
	 * @param model The model which PedestrianExtensionState contains the stock value.
	 */
	public StockComparator(IExtendsPedestrian model) {
		this.model = model;
	}

	@Override
	public int compare(IRichPedestrian leftPedestrian, IRichPedestrian rightPedestrian) {

		return Double.compare((-1) * ((StockPedestrianExtension) leftPedestrian.getExtensionState(model)).getStock(),
						(-1) * ((StockPedestrianExtension) rightPedestrian.getExtensionState(model)).getStock());
	}
}
