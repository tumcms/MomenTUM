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

import tum.cms.sim.momentum.data.output.WriterData;
import tum.cms.sim.momentum.model.output.writerSources.WriterSource;

/**
 * This class helps to format output data that comprises a list of identical elements.
 * Thus, it provides the data newline separated for a single header element.
 * 
 * @author Peter M. Kielar
 *
 */
public class ListDataWriterFormat extends WriterFormat {
	
	protected String dataType = null;

	@Override
	public void initialize() {
	
		// nothing to do
	}

	@Override
	public WriterData formatData(WriterSource writerSource) {
		
		WriterData writerData = new WriterData();
		StringBuilder listDataBuilder = new StringBuilder();
		
		// only the first set is written
		if(writerSource.hasNextSet()) {
			
			writerSource.loadSet();
			
			listDataBuilder.append(writerSource.getDataItemNames().get(0));
			listDataBuilder.append(System.lineSeparator());
			
			while(writerSource.hasNextSetItem()) {
				
				writerSource.loadSetItem();
				
				writerData.setData(writerSource.readSingleValue(writerSource.getDataItemNames().get(0)));
				
				if(writerSource.hasNextSetItem()) {
					
					listDataBuilder.append(System.lineSeparator());
				}
				else {
					
					break;
				}
			}
		}
		
		writerData.setData(listDataBuilder.toString());
		
		return writerData;
	}

}
