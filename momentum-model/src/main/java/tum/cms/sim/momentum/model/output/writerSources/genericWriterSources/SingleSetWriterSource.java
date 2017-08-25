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

import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.output.writerSources.WriterSource;

/**
 * This is a base class for all writer sources that provides
 * a single set. The set can be read only once for a time-step.
 * After reading the set in a time-step, this class will not provide
 * any further output content.
 * This is useful for e.g. the pedestrian writer source.
 * 
 * @author Peter M. Kielar
 *
 */
public abstract class SingleSetWriterSource extends WriterSource {

	private long currentTimeStep = -1;

	/**
	 * For each time-step there is a set. After reading the set it will be empty for the current time-step.
	 */
	@Override
	public boolean hasNextSet() {
	
		if(currentTimeStep == this.timeManager.getCurrentTimeStep()) {
			
			return false;
		}
		
		currentTimeStep = this.timeManager.getCurrentTimeStep();
			
		return true;
	}
	
	/**
	 * All output times are put into the list of format properties.
	 * This is valid for all SingleSetWriterSources, e.g.:
	 *   <writerSource sourceType="Absorber" additionalId="0"> 
     *          <property name="timeStep" type="Format" value="%d"/>
     *          <property name="removedCount" type="Format" value="%d"/>
     *   </writerSource>
	 */
	@Override
	public void initialize(SimulationState simulationState) {
		this.dataItemNames.addAll(this.properties.getFormatNames());
	}
}
