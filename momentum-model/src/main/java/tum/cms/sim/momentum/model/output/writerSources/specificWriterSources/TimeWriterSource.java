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

package tum.cms.sim.momentum.model.output.writerSources.specificWriterSources;

import tum.cms.sim.momentum.configuration.generic.FormatString;
import tum.cms.sim.momentum.configuration.generic.FormatString.TimeFormat;
import tum.cms.sim.momentum.configuration.model.output.WriterSourceConfiguration.OutputType;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.infrastructure.time.TimeManager;
import tum.cms.sim.momentum.model.output.writerSources.genericWriterSources.SingleElementWriterSource;

/**
 * The TimeWriterSource provides access to the TimeManagers statistics data for
 * the runtime of models and simulation parts.
 * 
 * @author Peter M. Kielar
 *
 */
public class TimeWriterSource extends SingleElementWriterSource {

	@Override
	public void initialize(SimulationState simulationState) {
		
		this.dataItemNames.addAll(this.properties.getFormatNames());
	}
	
	@Override
	public String readSingleValue(String outputTypeName) {
		
		OutputType outputType = OutputType.valueOf(outputTypeName);
		String result = null;
		FormatString formatter = this.properties.getFormatProperty(outputType.toString());
		String format = formatter.getFormat();
		
		switch(outputType) {

		case runTime:
			result = this.toTimeFormat(this.timeManager.getExecutionTimeProcessing(), format);
			break;
			
		case overheadTime:
			result = this.toTimeFormat(this.timeManager.getExecutionTimeOverhead(), format);
			break;
			
		case preProcessingTime:
			result = this.toTimeFormat(this.timeManager.getPreProcessingTime(), format);
			break;
			
		case postProcessingTime:
			result = this.toTimeFormat(this.timeManager.getPostProcessingTime(), format);
			break;
			
		default:
			double modelExecutionTime = this.timeManager.getExeuctionTimeModel(outputTypeName);
			result = this.toTimeFormat(modelExecutionTime, format);
			break;	
		}
		
		return result;
	}
	
	private String toTimeFormat(double value, String timeFormat) {
		
		TimeFormat time = TimeFormat.valueOf(timeFormat);
		double result = 0.0;
		
		switch(time) {
		case hr:
			result = value / TimeManager.toHours;
			break;
		case min:
			result = value / TimeManager.toMinutes;
			break;
		default:
		case sec:
			result = value / TimeManager.toSeconds;
			break;
		}
		
		return String.format("%.2f", result);
	}
}
