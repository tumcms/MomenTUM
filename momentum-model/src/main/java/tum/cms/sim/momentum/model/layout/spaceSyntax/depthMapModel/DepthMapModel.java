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

package tum.cms.sim.momentum.model.layout.spaceSyntax.depthMapModel;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.apache.commons.math3.util.FastMath;

import tum.cms.sim.momentum.data.layout.area.OriginArea;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.infrastructure.logging.LoggingManager;
import tum.cms.sim.momentum.model.layout.spaceSyntax.SpaceSyntaxOperation;
import tum.cms.sim.momentum.utility.geometry.Geometry2D;
import tum.cms.sim.momentum.utility.lattice.CellIndex;
import tum.cms.sim.momentum.utility.lattice.ILattice;
import tum.cms.sim.momentum.utility.spaceSyntax.DepthMap;

public class DepthMapModel extends SpaceSyntaxOperation {
	
	private static String scenarioLatticeIdName = "scenarioLatticeId";

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


		Set<CellIndex> connectedArea = this.floodLatticeFromOrigins(originCenterCells, lattice);
		this.computeDepthMap(connectedArea, lattice);
		Double[] latticeMinMaxValues = this.getLatticeMinMaxForConnectedArea(connectedArea, lattice);

		DepthMap depthMap = new DepthMap(
				lattice, 
				connectedArea,
				latticeMinMaxValues[0],
				latticeMinMaxValues[1]
		);
		depthMap.setId(this.getId());
		depthMap.setName(this.getName());
		
		this.scenarioManager.getSpaceSyntax().setDepthMap(depthMap);

		this.writeResultAsImage(depthMap, lattice);
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
			LoggingManager.logUser("Error: No 'Origins' were specified by the layout.\n"
					+ "There must be at least one 'Origin' where the center does not lie within an obstacle.");
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
			LoggingManager.logUser("Warning: Flooding from origins define multiple connected areas. \n"
					+ "All 'Origin' should be reachable from eachother in terms of walking pedestrians.");
		}

		return connectedAreas.get(0);
	}

	/**
	 * Computes the actual DepthMap Metric for each connected Area respectively.
	 * 
	 * @param connectedIndices
	 *            a list of connected sets containing indices, which are
	 *            basically an index structure for connected areas on top of the
	 *            provided lattice
	 * @param lattice
	 *            the lattice where the result is written to
	 */
	private void computeDepthMap(Set<CellIndex> connectedIndices, ILattice lattice) {

		connectedIndices.stream()
			.parallel()
			.forEach(start -> connectedIndices.forEach(end -> {

				if (lattice.breshamLineCast(start, end)) {

					lattice.increaseCellNumberValue(start, 1.0);
				}
			}));
	}

	/**
	 * Computes the minimum and maximum Double value respectively for each Area.
	 * 
	 * @param connectedAreas
	 * @return a List which contains the minimum and maximum paired to the
	 *         respective connected area.
	 */

	private Double[] getLatticeMinMaxForConnectedArea(Set<CellIndex> connectedIndices, ILattice lattice) {

			Double[] minMax = new Double[] {
					Double.MAX_VALUE, // first value is minimum
					Double.MIN_VALUE}; // second value is maximum

			connectedIndices.stream()
				.forEach(cellIndex -> {

					Double currentValue = (Double) lattice.getCellNumberValue(cellIndex);
	
					if (currentValue < minMax[0]) {
						minMax[0] = currentValue;
					}
	
					if (currentValue > minMax[1]) {
						minMax[1] = currentValue;
					}
			});

		return minMax;
	}

	
	private void writeResultAsImage(DepthMap depthMap, ILattice lattice) {

		int width = depthMap.getDomainColumns();
		int height = depthMap.getDomainRows();
		int heightImage = height - 1;

		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		Double colorMax = depthMap.getMaxValue() - depthMap.getMinValue();

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {

				Double currentValue = lattice.getCellNumberValue(y, x);
				short r = 0;
				short g = 0;
				short b = 0;

				if (currentValue.equals(Double.NaN)) {
					r = 255;
					g = 255;
					b = 255;
				} else {

					Double newValue = currentValue - depthMap.getMinValue();
					b = (short) FastMath.round((newValue / colorMax) * 255.0);
				}

				int p = (r << 16) | (g << 8) | b; // pixel
				// int p = new java.awt.Color(r, g, b, a).getRGB();
				img.setRGB(x, heightImage - y, p);
			}
		}
		
		LoggingManager.logUser("DepthMap #" + depthMap.getId() 
			+ " MinAbs: " + depthMap.getMinValue() 
			+ " MaxAbs: " + depthMap.getMaxValue());
		

		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
		String outputPath = "./" + dateFormat.format(date) + "_" + depthMap.getName() + ".jpg";
		File output = new File(outputPath);

		try {
			if (ImageIO.write(img, "jpg", output))
				LoggingManager.logUser("Successfully written image to: " + output.toString());
			else
				LoggingManager.logUser("Something else happened!?");
		} catch (IOException e) {
			LoggingManager.logUser("Schreiben des Bildes fehlgeschlagen...");
		}
	}
}
