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

import java.util.Iterator;

import tum.cms.sim.momentum.configuration.generic.FormatString;
import tum.cms.sim.momentum.configuration.model.output.WriterSourceConfiguration.OutputType;
import tum.cms.sim.momentum.data.agent.pedestrian.IExtendsPedestrian;
import tum.cms.sim.momentum.data.agent.pedestrian.PedestrianManager;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrianExtension;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;
import tum.cms.sim.momentum.model.IPedestrianBehavioralModel;

public abstract class ModelPedestrianWriterSource<T extends IExtendsPedestrian, K extends IPedestrianExtension>
	extends SingleSetWriterSource {
	
	private Iterator<IRichPedestrian> currentPedestrians = null;
	protected IRichPedestrian currentPedestrian = null;
	protected T behaviorModel = null;
	private PedestrianManager pedestrianManager = null;

	@SuppressWarnings("unchecked")
	public void setPedetrianBehavioralModel(IPedestrianBehavioralModel behaviorModel)  {
		
		this.behaviorModel = (T)behaviorModel;
	}

	public void setPedestrianManager(PedestrianManager pedestrianManager) {
		this.pedestrianManager = pedestrianManager;
	}

	@SuppressWarnings("unchecked")
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
			result = this.getPedestrianData((K)currentPedestrian.getExtensionState(this.behaviorModel), format, outputTypeName);
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
			case id:
				result = this.getCurrentId(format);
				break;
			default:
				break;
			}
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void loadSet() {
//
//		int number = (int) this.pedestrianManager.getAllPedestrians().stream()
//			.filter(pedestrian -> this.canWrite((K)pedestrian.getExtensionState(this.behaviorModel)))
//			.count();

		currentPedestrians = this.pedestrianManager.getAllPedestrians().stream()
				.filter(pedestrian -> this.canWrite((K)pedestrian.getExtensionState(this.behaviorModel)))
				.iterator();
	}
	
	@Override
	public void loadSetItem() {
		
		currentPedestrian = currentPedestrians.next();
	}

	@Override
	public boolean hasNextSetItem() {
		
		return currentPedestrians.hasNext();
		
	}
	
	protected String getTime(String format) {
		
	    return String.format(format, this.timeManager.getCurrentTime());
	}
	
	protected String getTimeStep(String format) {
	
	    return String.format(format, this.timeManager.getCurrentTimeStep());
	}
	
	protected String getCurrentId(String format) {
		
	    return String.format(format, currentPedestrian.getId());
	}
	
	/**
	 * Here return true if it is ok to extract the data of the pedestrian extensions.
	 * @param currentPedestrianExtension
	 * @return
	 */
	protected abstract boolean canWrite(K currentPedestrianExtension);
	
	/**
	 * Here implement the data extraction from the pedestrian extensions
	 * @param currentPedestrianExtension
	 * @param format
	 * @param dataElement
	 * @return The data as string for dataElement in format from currentPedestrianExtension
	 */
	protected abstract String getPedestrianData(K currentPedestrianExtension, String format, String dataElement);
}
