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

package tum.cms.sim.momentum.utility.spaceSyntax;

import java.util.Set;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import tum.cms.sim.momentum.utility.lattice.CellIndex;
import tum.cms.sim.momentum.utility.lattice.ILattice;

@XStreamAlias("SpaceSyntax")
public class DepthMap extends SpaceSyntax {
	
	@XStreamAsAttribute
	private final int domainRows;
	@XStreamAsAttribute
	private final int domainColumns;
	
	@XStreamAsAttribute
	private final double minValue;
	@XStreamAsAttribute
	private final double maxValue;
	
	@XStreamAsAttribute
	private final double minX;
	@XStreamAsAttribute
	private final double minY;
	@XStreamAsAttribute
	private final double maxX;
	@XStreamAsAttribute
	private final double maxY;
	
	public DepthMap(ILattice lattice, Set<CellIndex> connectedIndices) {
		
		super(lattice, connectedIndices);
		
		this.domainRows = lattice.getNumberOfRows();
		this.domainColumns = lattice.getNumberOfColumns();

		Double[] latticeMinMaxValues = lattice.getMinMaxValuesForIndices(connectedIndices);
		this.minValue = latticeMinMaxValues[0];
		this.maxValue = latticeMinMaxValues[0];
		
		this.minX = lattice.getMinPositionBoundingBox().getXComponent();
		this.minY = lattice.getMinPositionBoundingBox().getYComponent();
		this.maxX = lattice.getMaxPositionBoundingBox().getXComponent();
		this.maxY = lattice.getMaxPositionBoundingBox().getYComponent();
	}
	
	public ILattice getLattice() {
		return lattice;
	}
	
	public Set<CellIndex> getConnectedAreas() {
		return connectedIndices;
	}
	
	public int getDomainRows() {
		return domainRows;
	}
	
	public int getDomainColumns() {
		return domainColumns;
	}

	public double getMinValue() {
		return minValue;
	}

	public double getMaxValue() {
		return maxValue;
	}

	public double getMinX() {
		return minX;
	}

	public double getMinY() {
		return minY;
	}

	public double getMaxX() {
		return maxX;
	}

	public double getMaxY() {
		return maxY;
	}
}
