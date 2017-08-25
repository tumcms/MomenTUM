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

package tum.cms.sim.momentum.model.output.writerFormats;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tum.cms.sim.momentum.data.output.WriterData;
import tum.cms.sim.momentum.infrastructure.logging.LoggingManager;
import tum.cms.sim.momentum.model.output.writerSources.WriterSource;

/**
 * This class helps to format output data into json.
 * The json format is simply a root objects and at least a single list of key value pairs.
 *
 * The json structure is
 * ObjectRoot
 * 		ObjectList
 * 			Key Value Pairs
 * 
 * @author Peter M. Kielar, Benedikt Schwab
 *
 */
public class JsonWriterFormat extends WriterFormat {

	private static final String jsonSetRootTag = "ObjectList";
	private static final String jsonRootTag = "ObjectRoot";
	
	
	@Override
	public void initialize() {
		
		// nothing to do
	}
	
	@Override
	public WriterData formatData(WriterSource writerSource) {
		
		WriterData writerData = new WriterData();
		JSONObject jsonRoot = new JSONObject();
		JSONObject jsonSetRoot = null;
		int foundSets = 0;
		
		while(writerSource.hasNextSet()) {
		
			writerSource.loadSet();
			jsonSetRoot = new JSONObject();
			foundSets++;
			
			JSONArray jsonKeyValues = new JSONArray();
			
			while(writerSource.hasNextSetItem()) {
				
				writerSource.loadSetItem();
 				
				JSONObject currentObject = new JSONObject();
				
				for(int iter = 0; iter < writerSource.getDataItemNames().size(); iter++) {
				
					String itemType = writerSource.getDataItemNames().get(iter);
					String itemContent = writerSource.readSingleValue(itemType);
					
					if(itemContent != null && !itemContent.isEmpty()) {
											
						try {
							
							currentObject.put(itemType, itemContent);
						} 
						catch (JSONException jsonException) {

							LoggingManager.logUser(this, jsonException);
						}
					}
				}
				
				try {
					
					jsonKeyValues.put(currentObject);
					jsonSetRoot.put(jsonSetRootTag, jsonKeyValues);
				} 
				catch (JSONException jsonException) {
	
					LoggingManager.logUser(this, jsonException);
				}
			
			}
			
			try {
				
				jsonRoot.put(jsonRootTag, jsonSetRoot);
			} 
			catch (JSONException jsonException) {

				LoggingManager.logUser(this, jsonException);
			}
		}

		// In case only a single set was found, return without root
		if(foundSets == 1) {
			
			jsonSetRoot.toString();
		}
		
		writerData.setData(jsonSetRoot.toString());
		return writerData;
	}
}
