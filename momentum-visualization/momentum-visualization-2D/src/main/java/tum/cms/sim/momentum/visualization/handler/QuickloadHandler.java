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

package tum.cms.sim.momentum.visualization.handler;

import java.io.File;
import java.util.ArrayList;

import tum.cms.sim.momentum.visualization.controller.CoreController;
import tum.cms.sim.momentum.visualization.enums.PropertyType;

/**
 * This class handles quickload events.
 * 
 * @author Martin Sigl
 *
 */
public class QuickloadHandler {

	// contains the last loaded csv files
	private static ArrayList<File> csvFiles;
	// the latest loaded layout
	private static File latestLayout;
	
	public QuickloadHandler() {
		
	}

	public static void quickload(CoreController coreController) {
		if (latestLayout != null) {
			quickloadLayout(coreController);
			if (csvFiles != null) {
				quickloadCsv(coreController);
			}
		}
	}
	/**
	 *
	 * @param coreController
	 *            the coreController
	 * @param loadX
	 *            how many of the last loaded files should be loaded
	 */
	private static void quickloadCsv(CoreController coreController) {
		int loadX = csvFiles.size();
		for (int i = loadX - 1; i >= 0; i--) {;
				quickloadCsv(coreController, csvFiles.get(i));
		}
	}

	private static void quickloadCsv(CoreController coreController, File file) {
		LoadCsvHandler loadCsvHandler = new LoadCsvHandler();
		try {
			loadCsvHandler.load(coreController, file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Loads the latest loaded layout file
	 * @param coreController
	 */
	private static void quickloadLayout(CoreController coreController) {
		LoadLayoutHandler loadLayoutHandler = new LoadLayoutHandler();
		try {
			loadLayoutHandler.load(coreController, latestLayout);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adds a csv file to the quickload file list.
	 * @param file
	 */
	public static void addFile(File file) {
		if (csvFiles == null) {
			csvFiles = new ArrayList<>();
		}
		if (!csvFiles.contains(file)) {
			csvFiles.add(file);
		}
		updatePropertiesFile();
	}
	
	/**
	 * Sets new layout file for quickload and resets stored csv files.
	 * @param file
	 */
	public static void setLatestLayout(File file) {
		latestLayout = file;
		resetCsvFiles();
		updatePropertiesFile();
	}
	
	private static void resetCsvFiles() {
		csvFiles = new ArrayList<>();
		updatePropertiesFile();
	}
	
	/**
	 * Updates and stores the file paths into properties file
	 */
	private static void updatePropertiesFile() {
		String filePaths = "";
		for(File file : csvFiles) {
			filePaths = filePaths+file.getAbsolutePath()+PropertyType.DELIMITER;
		}
		UserPreferenceHandler.putProperty(PropertyType.quickloadCsvPaths, filePaths);
		UserPreferenceHandler.putProperty(PropertyType.quickloadLayoutPath, latestLayout.getAbsolutePath());
	}
	
	/**
	 * Loads quickload data from properties file
	 */
	public static void loadQuickloadDataFromProperties() {
		String joinedPaths = UserPreferenceHandler.loadProperty(PropertyType.quickloadCsvPaths);
		String layoutPath = UserPreferenceHandler.loadProperty(PropertyType.quickloadLayoutPath);
		String[] pathsFromProperties = joinedPaths.split(PropertyType.DELIMITER);
		try {
			latestLayout = new File(layoutPath);
			csvFiles = new ArrayList<>();
			for(String path : pathsFromProperties) {
				File file = new File(path);
				csvFiles.add(file);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
