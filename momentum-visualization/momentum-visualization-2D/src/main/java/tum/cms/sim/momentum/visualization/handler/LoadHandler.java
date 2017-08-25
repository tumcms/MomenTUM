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

import javafx.stage.FileChooser;
import javafx.stage.Window;
import tum.cms.sim.momentum.visualization.controller.CoreController;
import tum.cms.sim.momentum.visualization.enums.PropertyType;

public abstract class LoadHandler {
	
	/**
	 * Use this method if the user has to specify which file is loaded.
	 * @param coreController
	 * @param parentWindow
	 * @throws Exception
	 */
	public abstract void load(CoreController coreController, Window parentWindow) throws Exception;
	
	/**
	 * Use this method to load a specific file
	 * @param coreController
	 * @param file
	 * @param preferenceManager
	 * @throws Exception
	 */
	public abstract void load(CoreController coreController, File file) throws Exception;

	/**
	 * Opens a dialog where the user can select a file to load
	 * @param fileChooserTitle
	 * @param extensionFilter
	 * @param parentWindow
	 * @return
	 */
	protected File getFile(String fileChooserTitle, FileChooser.ExtensionFilter extensionFilter, Window parentWindow) {
		
		File file = new File(UserPreferenceHandler.loadProperty(PropertyType.layoutPath));
	
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle(fileChooserTitle);
		fileChooser.getExtensionFilters().add(extensionFilter);
	
		if(file.exists()) {
			
			fileChooser.setInitialDirectory(file);
		}
		
		file = fileChooser.showOpenDialog(parentWindow);
		
		return file;
	}
}
