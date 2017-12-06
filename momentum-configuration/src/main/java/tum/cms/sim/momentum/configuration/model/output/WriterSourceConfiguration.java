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

package tum.cms.sim.momentum.configuration.model.output;

import java.util.HashMap;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.converters.enums.EnumToStringConverter;

import tum.cms.sim.momentum.configuration.generic.PropertyContainerNode;

@XStreamAlias("writerSource")
public class WriterSourceConfiguration extends PropertyContainerNode {

	public static String indexString = ".idx";
	
	public enum OutputType {
		
		id,
		x,
		y,
		time,
		timeStep,
		timeStepDuration,
		endTime,
		
		runTime,
		overheadTime,
		preProcessingTime,
		postProcessingTime,
		
		bodyRadius,
		desiredVelocity,
		maximalVelocity,
		xVelocity,
		yVelocity,
		xHeading,
		xNextWalkingTarget,
		yNextWalkingTarget,
		yHeading,
		currentVertexID,
		targetID,
		groupID,
		seedID,
		leader,
		behavior,
		motoric, 
		startLocationID,
		
		width,
		length,
		height,

		message
	}
	
	public enum SourceType {
		
		// Generic
		Pedestrian,
		Car,
		Time,
		Configuration,
		Analysis,
		SpaceSyntax,
		// Pedestrian output
		UPRM_Pedestrian,
		CSC_Pedestrian,
		BarnesHut_SocialForce_Pedestrian,
		Zeng_SocialForce_Pedestrian,
		// Model output
		CSC,
		TransitZones,
		ClassicLWR,
		CsvPlayback,
		// Absorber output
		Absorber
	}

	@SuppressWarnings("rawtypes")
	public static EnumToStringConverter getTypeConverter() {
		
		HashMap<String, SourceType> map = new HashMap<>();
		map.put(SourceType.Pedestrian.toString(), SourceType.Pedestrian);
		map.put(SourceType.Car.toString(), SourceType.Car);
		map.put(SourceType.Time.toString(), SourceType.Time);
		map.put(SourceType.Configuration.toString(), SourceType.Configuration);
		map.put(SourceType.Analysis.toString(), SourceType.Analysis);
		map.put(SourceType.SpaceSyntax.toString(), SourceType.SpaceSyntax);
		map.put(SourceType.UPRM_Pedestrian.toString(), SourceType.UPRM_Pedestrian);
		map.put(SourceType.CSC_Pedestrian.toString(), SourceType.CSC_Pedestrian);
		map.put(SourceType.BarnesHut_SocialForce_Pedestrian.toString(), SourceType.BarnesHut_SocialForce_Pedestrian);
		map.put(SourceType.Zeng_SocialForce_Pedestrian.toString(), SourceType.Zeng_SocialForce_Pedestrian);
		map.put(SourceType.CSC.toString(), SourceType.CSC);
		map.put(SourceType.TransitZones.toString(), SourceType.TransitZones);
		map.put(SourceType.ClassicLWR.toString(), SourceType.ClassicLWR);
		map.put(SourceType.Absorber.toString(), SourceType.Absorber);
		map.put(SourceType.CsvPlayback.toString(), SourceType.CsvPlayback);
		
		return new EnumToStringConverter<>(SourceType.class, map);
	}
	
	@XStreamAsAttribute
	private SourceType sourceType;

	public SourceType getSourceType() {
		return sourceType;
	}

	public void setSourceType(SourceType sourceType) {
		this.sourceType = sourceType;
	}
	
	/**
	 * Optional integer attribute for unique checks e.g. 
	 * for identifying same behavior oder analysis model types by id.
	 */
	@XStreamAsAttribute
	private Integer additionalId;

	public Integer getAdditionalId() {
		return additionalId;
	}

	public void setAdditionalId(Integer addtionalId) {
		this.additionalId = addtionalId;
	}
}
