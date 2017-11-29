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
import tum.cms.sim.momentum.data.agent.pedestrian.PedestrianManager;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.output.writerSources.genericWriterSources.SingleSetWriterSource;

public class PedestrianWriterSource extends SingleSetWriterSource {
	
	private Iterator<IRichPedestrian> currentPedestrians = null;
	private IRichPedestrian currentPedestrian = null;
	private PedestrianManager pedestrianManager = null;

	public void setPedestrianManager(PedestrianManager pedestrianManager) {
		this.pedestrianManager = pedestrianManager;
	}
	
	@Override
	public void initialize(SimulationState simulationState) {
		
		this.dataItemNames.addAll(this.properties.getFormatNames());
	}
	
	@Override
	public void loadSet() {

		currentPedestrians = this.pedestrianManager.getAllPedestrians().iterator();
	}
	
	@Override
	public void loadSetItem() {
		
		currentPedestrian = currentPedestrians.next();
	}

	@Override
	public boolean hasNextSetItem() {
		
		return currentPedestrians.hasNext();
	}
	
	private String getTime(String format) {
		
	    return String.format(format, this.timeManager.getCurrentTime());
	}
	
	private String getTimeStep(String format) {
	
	    return String.format(format, this.timeManager.getCurrentTimeStep());
	}
	
	private String getCurrentId(String format) {
		
	    return String.format(format, currentPedestrian.getId());
	}
	
	private String getCurrentX(String format) {
	
	    return String.format(format, currentPedestrian.getPosition().getXComponent());
	}
	
	private String getCurrentY(String format) {
		
	    return String.format(format, currentPedestrian.getPosition().getYComponent());
	}
	
	private String getCurrentBodyRadius(String format) {
		
		return String.format(format, currentPedestrian.getBodyRadius());
	}
	
	private String getCurrentVertexID(String format) {

		String result = null;
		
		if(currentPedestrian.getRoutingState() != null && currentPedestrian.getRoutingState().getNextVisit() != null) {
			
			result = String.format(format, currentPedestrian.getRoutingState().getNextVisit().getId());
		}
		else {
			
			result = "-1";
		}
		
		return result;
	}
	
	private String getCurrentDesiredVelocity(String format) {
		
		return String.format(format, currentPedestrian.getDesiredVelocity());
	}

	private String getCurrentMaximalVelocity(String format) {
		
		return String.format(format, currentPedestrian.getMaximalVelocity());
	}
	
	private String getCurrentTargetID(String format) {
		
		String result = null;
		
		if(currentPedestrian.getStrategicalState() != null &&
		   currentPedestrian.getStrategicalState().getNextTargetArea() != null) {
			
			result = String.format(format, currentPedestrian.getStrategicalState().getNextTargetArea().getId());
		}
		else {
			
			result = "-1";
		}
		
		return result;
	}

	private String getCurrentGroupID(String format) {
		
		return String.format(format, currentPedestrian.getGroupId());
	}

	private String getCurrentSeedID(String format) {
		
		return String.format(format, currentPedestrian.getSeedId());
	}

	private String getCurrentXHeading(String format) {
		
		return String.format(format, currentPedestrian.getHeading().getXComponent());
	}
	
	private String getCurrentYHeading(String format) {
		
		return String.format(format, currentPedestrian.getHeading().getYComponent());
	}
	
	private String getCurrentXVelocity(String format) {
		
		return String.format(format, currentPedestrian.getVelocity().getXComponent());
	}
	
	private String getCurrentYVelocity(String format) {
		
		return String.format(format, currentPedestrian.getVelocity().getYComponent());
	}
	
	private String getCurrentXNextWalkingTarget(String format) {
		
		return String.format(format, currentPedestrian.getNextWalkingTarget().getXComponent());
	}
	
	private String getCurrentYNextWalkingTarget(String format) {
		
		return String.format(format, currentPedestrian.getNextWalkingTarget().getYComponent());
	}
	
	private String getCurrentLeader(String format) {
		
		return String.valueOf(currentPedestrian.isLeader());
	}
	
	private String getCurrentBehavior(String format) {
		
		return String.format(format, currentPedestrian.getBehavior().ordinal());
	}
	
	private String getCurrentMotoric(String format) {
		
		return String.format(format, currentPedestrian.getMotoricTask().name());
	}

	public String getStartLocationId(String format) {
		return String.format(format, currentPedestrian.getStartLocationId());
	}
	
	private String getTimeStepDuration(String format) {
		
		return String.format(format, this.timeManager.getTimeStepDuration());
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
		case bodyRadius:
			result = this.getCurrentBodyRadius(format);
			break;
		case desiredVelocity:
			result = this.getCurrentDesiredVelocity(format);
			break;
		case maximalVelocity:
			result = this.getCurrentMaximalVelocity(format);
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
		case xNextWalkingTarget:
			result = this.getCurrentXNextWalkingTarget(format);
			break;
		case yNextWalkingTarget:
			result = this.getCurrentYNextWalkingTarget(format);
			break;
		case currentVertexID:
			result = this.getCurrentVertexID(format);
			break;
		case targetID:
			result = this.getCurrentTargetID(format);
			break;
		case startLocationID:
			result = this.getStartLocationId(format);
			break;
		case groupID:
			result = this.getCurrentGroupID(format);
			break;
		case seedID:
			result = this.getCurrentSeedID(format);
			break;
		case leader:
			result = this.getCurrentLeader(format);
			break;
		case behavior:
			result = this.getCurrentBehavior(format);
			break;
		case motoric:
			result = this.getCurrentMotoric(format);
			break;
		default:
			break;	
		}
		return result;
	}
}
