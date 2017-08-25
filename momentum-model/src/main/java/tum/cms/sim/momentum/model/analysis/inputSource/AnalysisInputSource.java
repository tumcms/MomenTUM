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

package tum.cms.sim.momentum.model.analysis.inputSource;

public abstract class AnalysisInputSource  {

//	private final static String analysisStartStepName = "analysisStartStep";
//	private final static String analysisEndStepName = "analysisEndStep";
//	private final static String callName = "call";
//	private final static String timeStepDifferenceName = "timeStepDifference";
//	
//	protected int timeStepDifference = 1;
//	
//	public int getTimeStepDifference() {
//		return timeStepDifference;
//	}
//
//	public void setTimeStepDifference(int timeStepDifference) {
//		this.timeStepDifference = timeStepDifference;
//	}
//
//	protected PropertyBackPack properties = null;
//	
//	@Override
//	public PropertyBackPack getPropertyBackPack() {
//		return properties;
//	}
//	
//	@Override
//	public void setPropertyBackPack(PropertyBackPack propertyContainer) {
//
//		this.properties = propertyContainer; 
//	}
//	
//	protected WriterSource<String> writerSource = null;
//	
//	public void setWriterSource(WriterSource<String> writerSource) {
//		this.writerSource = writerSource;
//	}
//	
//	protected long currentTimeStep = 0;
//	
//	protected double timeStepDuration = 0d;
//	
//	public void setTimeStepDuration(double timeStepDuration) {
//		this.timeStepDuration = timeStepDuration;
//	}
//
//	protected double simulationEndTime = Double.MAX_VALUE;
//
//	public void setSimulationEndTime(double simulationEndTime) {
//		this.simulationEndTime = simulationEndTime;
//	}
//
//	protected ScenarioManager scenarioManager = null;
//
//	public void setScenarioManager(ScenarioManager scenarioManager) {
//		this.scenarioManager = scenarioManager;
//	}
//
//	private long analysisStartStep = 0L;
//
//	public long getAnalysisStartStep() {
//		return analysisStartStep;
//	}
//
//	private long analysisEndStep = Long.MAX_VALUE;
//	
//	public long getAnalysisEndStep() {
//		return analysisEndStep;
//	}
//	
//	private int call = Integer.MAX_VALUE;
//	
//	public int getCall() {
//		return call;
//	}
//
//	public void loadConfiguration(AnalysisInputSourceConfiguration configuration) {
//		
// 		this.analysisStartStep = this.properties.getIntegerProperty(analysisStartStepName);
//		this.analysisEndStep = this.properties.getIntegerProperty(analysisEndStepName);
//
//		Integer call = this.properties.getIntegerProperty(callName);
//		
//		if(call != null) {
//			
//			this.call = call;
//		}
//		
//		if(this.properties.getIntegerProperty(timeStepDifferenceName) != null) {
//		
//			this.timeStepDifference = this.properties.getIntegerProperty(timeStepDifferenceName);
//		}
//	}
//
//	/**
//	 * 
//	 * @return The current time step.
//	 */
//	public abstract long readNextDataSet();
//	
//	public abstract boolean hasNextDataSet();
//	
//	public abstract AnalysisElementSet getData(String dataType, long timeStep);
//	
//	protected abstract Set<String> getPedestrianIds();
//	
}
