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

package tum.cms.sim.momentum.simulator.factory.analysisModelFactory;

import tum.cms.sim.momentum.configuration.model.analysis.MeasureConfiguration;
import tum.cms.sim.momentum.model.analysis.measure.Measure;
import tum.cms.sim.momentum.model.analysis.measure.MeasureAgentExistens;
import tum.cms.sim.momentum.model.analysis.measure.MeasureAreaOccupancy;
import tum.cms.sim.momentum.model.analysis.measure.MeasureInsidePolygon;
import tum.cms.sim.momentum.model.analysis.measure.MeasureLineCrossing;
import tum.cms.sim.momentum.model.analysis.measure.MeasureTurningAngle;
import tum.cms.sim.momentum.model.analysis.measure.MeasureWalkingDistance;
import tum.cms.sim.momentum.model.analysis.measure.MeasureXtDensitiy;
import tum.cms.sim.momentum.simulator.component.ComponentManager;
import tum.cms.sim.momentum.simulator.factory.ModelFactory;
import tum.cms.sim.momentum.utility.generic.PropertyBackPackFactory;
import tum.cms.sim.momentum.utility.generic.Unique;

public class MeasureFactory extends ModelFactory<MeasureConfiguration, Measure> {

	@Override
	public Measure createModel(MeasureConfiguration configuration, ComponentManager componentManager) {

		Measure measureModel = null;
		
		switch(configuration.getType()) {
		
		case InsidePolygon:
			measureModel = new MeasureInsidePolygon();
			break;
			
		case LineCrossing:
			measureModel = new MeasureLineCrossing();
			break;
			
		case WalkingDistance:
			measureModel = new MeasureWalkingDistance();
			break;
			
		case TurningAngle:
			measureModel = new MeasureTurningAngle();
			break;
			
		case AreaOccupancy:
			measureModel = new MeasureAreaOccupancy();
			break;
			
		case AgentExistens:
			measureModel = new MeasureAgentExistens();
			break;
			
		case XtDensity:
			measureModel = new MeasureXtDensitiy();
			
		default:
			break;
			
		}
		
		measureModel.setScenarioManager(componentManager.getScenarioManager());
		measureModel.setPropertyBackPack(PropertyBackPackFactory.fillProperties(configuration));
		Unique.generateUnique(measureModel, configuration);
		
		return measureModel;
	}	
}
