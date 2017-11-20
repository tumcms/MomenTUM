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

import java.util.Iterator;

import tum.cms.sim.momentum.configuration.generic.FormatString;
import tum.cms.sim.momentum.configuration.model.output.WriterSourceConfiguration.OutputType;
import tum.cms.sim.momentum.data.agent.car.CarManager;
import tum.cms.sim.momentum.data.agent.car.types.IRichCar;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.output.writerSources.genericWriterSources.SingleSetWriterSource;

public class CarWriterSource extends SingleSetWriterSource {
	
	private Iterator<IRichCar> currentCars = null;
	private IRichCar currentCar = null;
	private CarManager carManager = null;

	public void setCarManager(CarManager carManager) {
		this.carManager = carManager;
	}
	
	@Override
	public void initialize(SimulationState simulationState) {
		
		this.dataItemNames.addAll(this.properties.getFormatNames());
	}
	
	@Override
	public void loadSet() {

		currentCars = this.carManager.getAllCars().iterator();
	}
	
	@Override
	public void loadSetItem() {
		
		currentCar = currentCars.next();
	}

	@Override
	public boolean hasNextSetItem() {
		
		return currentCars.hasNext();
	}
	
	private String getTime(String format) {
		
	    return String.format(format, this.timeManager.getCurrentTime());
	}
	
	private String getTimeStep(String format) {
	
	    return String.format(format, this.timeManager.getCurrentTimeStep());
	}
	
	private String getTimeStepDuration(String format) {
		
		return String.format(format, this.timeManager.getTimeStepDuration());
	}
	
	private String getCurrentId(String format) {
		
	    return String.format(format, currentCar.getId());
	}
	
	private String getCurrentX(String format) {
	
	    return String.format(format, currentCar.getPosition().getXComponent());
	}
	
	private String getCurrentY(String format) {
		
	    return String.format(format, currentCar.getPosition().getYComponent());
	}

	private String getCurrentXHeading(String format) {
		
		return String.format(format, currentCar.getHeading().getXComponent());
	}
	
	private String getCurrentYHeading(String format) {
		
		return String.format(format, currentCar.getHeading().getYComponent());
	}
	
	private String getCurrentXVelocity(String format) {
		
		return String.format(format, currentCar.getVelocity().getXComponent());
	}
	
	private String getCurrentYVelocity(String format) {
		
		return String.format(format, currentCar.getVelocity().getYComponent());
	}
	
	private String getLength(String format) {
		
		return String.format(format, currentCar.getLength());
	}
	
	private String getWidth(String format) {
		
		return String.format(format, currentCar.getWidth());
	}

	@Override
	public String readSingleValue(String outputTypeName) {

		OutputType outputType = OutputType.valueOf(outputTypeName);
		String result = null;
		FormatString formatter = this.properties.getFormatProperty(outputType.toString());
		String format = formatter.getFormat();
		
		switch(outputType) {

		case timeStepDuration:
			result = this.getTimeStepDuration(format);
			break;
		case timeStep:
			result = this.getTimeStep(format);
			break;
		case time:
			result = this.getTime(format);
			break;
		case id:
			result = this.getCurrentId(format);
			break;
		case x:
			result = this.getCurrentX(format);
			break;
		case y:
			result = this.getCurrentY(format);
			break;
		case xVelocity:
			result = this.getCurrentXVelocity(format);
			break;
		case yVelocity:
			result = this.getCurrentYVelocity(format);
			break;
		case xHeading:
			result = this.getCurrentXHeading(format);
			break;
		case yHeading:
			result = this.getCurrentYHeading(format);
			break;
		case length:
			result = this.getLength(format);
			break;
		case width:
			result = this.getWidth(format);
			break;
			
		default:
			break;	
		}
		return result;
	}
}
