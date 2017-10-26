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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import tum.cms.sim.momentum.data.layout.area.OriginArea;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.layout.lattice.LatticeModel;
import tum.cms.sim.momentum.model.layout.spaceSyntax.SpaceSyntaxOperation;
import tum.cms.sim.momentum.utility.geometry.Geometry2D;
import tum.cms.sim.momentum.utility.lattice.CellIndex;
import tum.cms.sim.momentum.utility.lattice.ILattice;
import tum.cms.sim.momentum.utility.spaceSyntax.DepthMap;
import tum.cms.sim.momentum.utility.spaceSyntax.DepthMapSubArea;

public class DepthMapModel extends SpaceSyntaxOperation {
	private static String scenarioLatticeIdName = "scenarioLatticeId";

	@Override
	public void callPreProcessing(SimulationState simlationState) {

		int id = this.properties.getIntegerProperty(scenarioLatticeIdName);
		
		ILattice lattice = this.scenarioManager.getScenarios().getLattices().values()
				.stream()
				.filter(grid -> grid.getId() == id)
				.findFirst().get();
		
		List<CellIndex> originCenterCells = super.scenarioManager.getOrigins().stream()
				.map(OriginArea::getGeometry)
				.map(Geometry2D::getCenter)
				.map(center -> lattice.getCellIndexFromPosition(center))
				.collect(Collectors.toList());
		
		LatticeModel.fillLatticeForObstacles(lattice, super.scenarioManager.getScenarios());
		
		List<Set<CellIndex>> connectedAreas = this.createConnectedAreas(originCenterCells, lattice);
		
		this.computeDepthMap(connectedAreas, lattice);

		List<DepthMapSubArea> subAreas = this.computeMinMaxForSubAreas(connectedAreas, lattice);
		
		this.scenarioManager.getSpaceSyntax().setDepthMap(
				new DepthMap(lattice, 
						subAreas,
						super.scenarioManager.getScenarios().getMaxX(), 
						super.scenarioManager.getScenarios().getMinX(),
						super.scenarioManager.getScenarios().getMaxY(), 
						super.scenarioManager.getScenarios().getMinY())
				);
		
		//this.writeResultAsImage(subAreas, lattice);
	}
	
	@Override
	public void callPostProcessing(SimulationState simlationState) {

		// nothing to do
	}

	/**
	 * This method floods the given lattice for given CellIndices of type 'Origin' and creates a set of indices.
	 * If the given starting indices are connected i.e. pedestrians can walk from one origin to the next origin, 
	 * then no additional set is generated for this origin.
	 * If all starting points are connected, this method returns a list containing only one set.
	 * @param originCenterCells a list of starting points for flooding
	 * @param lattice on which the flooding happens
	 * @return a list of sets, which represent an index structure for connected areas
	 */
	private List<Set<CellIndex>> createConnectedAreas(List<CellIndex> originCenterCells, ILattice lattice) {
		
		List<Set<CellIndex>> connectedAreas = new ArrayList<Set<CellIndex>>();
		
		for(CellIndex current: originCenterCells) {
			
			boolean newAreaRequired = true;
			
			for(Set<CellIndex> disconnectedArea: connectedAreas){
				if(disconnectedArea.contains(current)) {
					newAreaRequired = false;
					break;
				}
			}
			
			if(newAreaRequired == true) {
				connectedAreas.add(lattice.flood(current));
			}
		}
		
		return connectedAreas;
	}

	/**
	 * Computes the actual DepthMap Metric for each connected Area respectively.
	 * @param connectedAreas a list of connected sets containing indices, which are basically an index structure for connected areas on top of the provided lattice
	 * @param lattice the lattice where the result is written to
	 */
	private void computeDepthMap(List<Set<CellIndex>> connectedAreas, ILattice lattice) {
		
		for(Set<CellIndex> connectedCells: connectedAreas) {
			connectedCells.stream().parallel().forEach(start -> connectedCells.forEach(end -> {
				
				if (lattice.breshamLineCast(start, end, Integer.MAX_VALUE) == 0.0) {
					
					lattice.increaseCellNumberValue(start, 1.0);
					
				}
			}));
		}
	}
	
	/**
	 * Computes the minimum and maximum Double value respectively for each Area.
	 * @param connectedAreas 
	 * @return a List which contains the minimum and maximum paired to the respective connected area.
	 */
	
	private List<DepthMapSubArea> computeMinMaxForSubAreas(List<Set<CellIndex>> disconnectedAreas, ILattice lattice) {
		
		List<DepthMapSubArea> result = new ArrayList<DepthMapSubArea>(disconnectedAreas.size());
		
		for(int i = 0; i < disconnectedAreas.size(); i ++) {
			
			Set<CellIndex> connectedCells = disconnectedAreas.get(i);
			
			Double[] minMax = new Double[] { Double.MAX_VALUE, Double.MIN_VALUE }; // first value is min, second is max
			
			connectedCells.stream().forEach(cellIndex -> {
	
				Double currentValue = (Double) lattice.getCellNumberValue(cellIndex);
	
				if (currentValue < minMax[0]) {
					minMax[0] = currentValue;
				}
	
				if (currentValue > minMax[1]) {
					minMax[1] = currentValue;
				}
			});
			
			result.add(new DepthMapSubArea(connectedCells, minMax[0], minMax[1], i));
		}
		
		return result;
	}
	/*
	private void writeResultAsImage(List<DepthMapSubArea> subAreas, ILattice lattice) {

		int width = lattice.getNumberOfColumns();
		int height = lattice.getNumberOfRows();
		int heightImage = height - 1; 

		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		subAreas.forEach(subArea ->{
			Double newMax = subArea.getMaximum() - subArea.getMinimum();
	
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
						
						Double newValue = currentValue - subArea.getMinimum();
						b = (short) FastMath.round((newValue / newMax) * 255.0);
					}
	
					int p = (r << 16) | (g << 8) | b; // pixel
					// int p = new java.awt.Color(r, g, b, a).getRGB();
					img.setRGB(x, heightImage - y, p);
				}
			}
			
			Double minAsPercent = ((int) (subArea.getMinimum() / subArea.getConnectedIndices().size() * 10000)) / 100.0;
			Double maxAsPercent = ((int) (subArea.getMaximum() / subArea.getConnectedIndices().size() * 10000)) / 100.0;
			LoggingManager.logUser("Subarea #" + subArea + " MinAbs: " + subArea.getMinimum() 
			+ " MaxAbs: " + subArea.getMaximum() + " MinPercent: " + minAsPercent + " MaxPercent: " + maxAsPercent);
		});
		
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
		String outputPath = "./" + dateFormat.format(date) + "_" + lattice.getName() + ".jpg";
		File output = new File(outputPath);
		
		try {
			if(ImageIO.write(img, "jpg", output))
				LoggingManager.logUser("Successfully written image to: " + output.toString());
			else
				LoggingManager.logUser("Something else happened!?");
		} catch (IOException e) {
			LoggingManager.logUser("Schreiben des Bildes fehlgeschlagen...");
		}
	}*/
}
