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

package tum.cms.sim.momentum.visualization.utility;

import java.io.File;

import tum.cms.sim.momentum.utility.csvData.CsvType;

@SuppressWarnings("serial")
public class CsvFile extends File {

	private CsvType type;
	
	public CsvType getType() {
		return type;
	}

	public void setType(CsvType type) {
		this.type = type;
	}

	public CsvFile(File file) {
		super(file.getAbsolutePath());
	}
	
	public CsvFile(String pathname) {
		super(pathname);
		this.type = getCsvTypeFromFile(pathname);
	}
	
	/**
	 * Tries to find the csv type by analyzing the file name.
	 * @param path
	 * @return the csv file type
	 */
	public static CsvType getCsvTypeFromFile(String path) {

		CsvType type = null;
		// TODO decide in a better way which csv file type is loaded
		// TODO add other custom types
		if (path.toLowerCase().contains("density")) {
			type = CsvType.xtDensity;
		}
		if (path.toLowerCase().contains("pedestrian")) {
			type = CsvType.Pedestrian;
		}
		if (path.toLowerCase().contains("zones")) {
			type = CsvType.TransitZones;
		}
		if (path.toLowerCase().contains("network")) {
			type = CsvType.MacroscopicNetwork;
		}
		return type;
	}
	
	/**
	 * 
	 * @return if this {@link CsvFile} has a custom type.
	 */
	public boolean isCustomType() {
		if(getType().equals(CsvType.Pedestrian)) {
			return false;
		} else {
			return true;
		}
	}

}
