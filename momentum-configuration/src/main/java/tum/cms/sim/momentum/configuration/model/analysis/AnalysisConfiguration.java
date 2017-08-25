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

package tum.cms.sim.momentum.configuration.model.analysis;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import tum.cms.sim.momentum.configuration.generic.PropertyContainerNode;
import tum.cms.sim.momentum.configuration.model.output.WriterSourceConfiguration;

@XStreamAlias("analysis")
public class AnalysisConfiguration extends PropertyContainerNode {

	private WriterSourceConfiguration writerSource = null;

	public WriterSourceConfiguration getWriterSource() {
		return writerSource;
	}

	public void setWriterSource(WriterSourceConfiguration writerSource) {
		this.writerSource = writerSource;
	}

	private MeasureConfiguration measure = null;

	public MeasureConfiguration getMeasure() {
		return measure;
	}

	public void setMeasure(MeasureConfiguration measure) {
		this.measure = measure;
	}
}
