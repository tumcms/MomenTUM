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

package tum.cms.sim.momentum.model.output.writerSources.genericWriterSources;

import tum.cms.sim.momentum.configuration.generic.FormatString;
import tum.cms.sim.momentum.configuration.model.output.WriterSourceConfiguration.OutputType;
import tum.cms.sim.momentum.infrastructure.execute.callable.Callable;

/**
 * The model writer can provide a set of items in each time step.
 * The data is based on properties within the model class.
 * 
 * @author Peter M. Kielar
 *
 * @param <T> a model that is callable
 */
public abstract class ModelWriterSource<T extends Callable> extends SingleSetWriterSource {

	protected T callable = null;

	@SuppressWarnings("unchecked")
	public void setCallable(Callable callable) {
		this.callable = (T)callable;
	}

	@Override
	public String readSingleValue(String outputTypeName) {
		OutputType outputType = null;
		
		try {
			
			outputType = OutputType.valueOf(outputTypeName);
		}
		catch(IllegalArgumentException iae) {
			
			// nothing to do, that is ok
		}
		
		String result = null;
		
		if(outputType == null) {
			
			FormatString formatter = this.properties.getFormatProperty(outputTypeName);
			String format = formatter.getFormat();
			result = this.getModelData((T)callable, format, outputTypeName);
		}
		else {
			
			FormatString formatter = this.properties.getFormatProperty(outputType.toString());
			String format = formatter.getFormat();

			switch(outputType) {

			case timeStep:
				result = this.getTimeStep(format);
				break;
			case time:
				result = this.getTime(format);
				break;
			default:
				break;
			}
		}

		return result;
	}
	
	protected String getTime(String format) {
		
	    return String.format(format, this.timeManager.getCurrentTime());
	}
	
	protected String getTimeStep(String format) {
	
	    return String.format(format, this.timeManager.getCurrentTimeStep());
	}
	
	protected abstract String getModelData(T model, String format, String dataElement);
}
