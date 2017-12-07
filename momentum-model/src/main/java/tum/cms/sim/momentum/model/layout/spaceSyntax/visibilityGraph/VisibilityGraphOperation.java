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

package tum.cms.sim.momentum.model.layout.spaceSyntax.visibilityGraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import tum.cms.sim.momentum.data.layout.area.OriginArea;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.infrastructure.logging.LoggingManager;
import tum.cms.sim.momentum.model.layout.spaceSyntax.SpaceSyntaxOperation;
import tum.cms.sim.momentum.utility.geometry.Geometry2D;
import tum.cms.sim.momentum.utility.lattice.CellIndex;
import tum.cms.sim.momentum.utility.lattice.ILattice;
import tum.cms.sim.momentum.utility.spaceSyntax.VisibilityGraph;

public class VisibilityGraphOperation extends SpaceSyntaxOperation {
	
	private static final String scenarioLatticeIdName = "scenarioLatticeId";

	@Override
	public void callPreProcessing(SimulationState simlationState) {

		int id = this.properties.getIntegerProperty(scenarioLatticeIdName);

		ILattice lattice = this.scenarioManager.getScenarios().getLattices().values()
				.stream()
				.filter(grid -> grid.getId() == id)
				.findFirst()
				.get();

		List<CellIndex> originCenterCells = super.scenarioManager.getOrigins()
				.stream()
				.map(OriginArea::getGeometry)
				.map(Geometry2D::getCenter)
				.map(center -> lattice.getCellIndexFromPosition(center))
				.collect(Collectors.toList());

		Set<CellIndex> connectedIndices = this.floodLatticeFromOrigins(originCenterCells, lattice);
		this.computeVisibilityGraph(connectedIndices, lattice);

		VisibilityGraph visibilityGraph = new VisibilityGraph(
				lattice, 
				connectedIndices,
				scenarioManager.getScenarios().getName()
		);
		visibilityGraph.setId(this.getId());
		visibilityGraph.setName(this.getName());
		
		this.scenarioManager.getSpaceSyntaxes().add(visibilityGraph);
	}

	@Override
	public void callPostProcessing(SimulationState simlationState) {

		// nothing to do
	}

	/**
	 * This method floods the given lattice for given CellIndices of type
	 * 'Origin' and creates a set of indices. If some of the given starting indices are
	 * not connected i.e. pedestrians can not walk from one origin to the next origin,
	 * then only one connected area is returned in the set. The user is notified via logging
	 * in case this happens.
	 * 
	 * @param originCenterCells
	 *            a list of starting points for flooding
	 * @param lattice
	 *            on which the flooding happens
	 * @return a set of connected indices
	 */
	private Set<CellIndex> floodLatticeFromOrigins(List<CellIndex> originCenterCells, ILattice lattice) {

		if (originCenterCells == null || originCenterCells.size() < 1) {
			LoggingManager.logDebug("Error: No 'Origins' were specified by the layout or failed in initialization phase.\n"
					+ "There must be at least one 'Origin' whose the center does not lie within an obstacle.");
		}
		
		List<Set<CellIndex>> connectedAreas = new ArrayList<Set<CellIndex>>();

		for (CellIndex originCenter : originCenterCells) {

			boolean newAreaRequired = true;

			for (Set<CellIndex> disconnectedArea : connectedAreas) {
				
				if (disconnectedArea.contains(originCenter)) {
					newAreaRequired = false;
					break;
				}
			}

			if (newAreaRequired == true) {
				connectedAreas.add(lattice.flood(originCenter));
			}
		}
		
		if (connectedAreas.size() > 1) {
			LoggingManager.logUser("Warning: Not all 'Origin's are connected to each other. \n"
					+ "All 'Origin's should be reachable from each other.");
		}

		return connectedAreas.get(0);
	}

	/**
	 * Computes the actual VisibilityGraph Metric for each connected Area respectively.
	 * 
	 * @param connectedIndices
	 *            a list of connected sets containing indices, which are
	 *            basically an index structure for connected areas on top of the
	 *            provided lattice
	 * @param lattice
	 *            the lattice where the result is written to
	 */
	private void computeVisibilityGraph(Set<CellIndex> connectedIndices, ILattice lattice) {

		connectedIndices.stream()
			.parallel()
			.forEach(start -> connectedIndices.forEach(end -> {

				if (lattice.breshamLineCast(start, end)) {

					lattice.increaseCellNumberValue(start, 1.0);
				}
			}));
	}
}
