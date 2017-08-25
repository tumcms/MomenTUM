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

package tum.cms.sim.momentum.simulator.factory.behaviorModelFactory;

import tum.cms.sim.momentum.configuration.model.strategical.StrategicalModelConfiguration;
import tum.cms.sim.momentum.model.strategical.DestinationChoiceModel;
import tum.cms.sim.momentum.model.strategical.StrategicalModel;
import tum.cms.sim.momentum.model.strategical.cognitiveSpatialChoiceModel.CognitiveSpatialChoiceStrategical;
import tum.cms.sim.momentum.model.strategical.interestModel.InterestStrategical;
import tum.cms.sim.momentum.model.strategical.noDecisionModel.NoDecisionStrategical;
import tum.cms.sim.momentum.model.strategical.odMatrixModel.ODMatrixStrategical;
import tum.cms.sim.momentum.model.strategical.shortestDestinationModel.ShortestDestinationStrategical;
import tum.cms.sim.momentum.model.strategical.strictOrderModel.StrictOrderStrategical;
import tum.cms.sim.momentum.model.support.perceptional.PerceptionalModel;
import tum.cms.sim.momentum.model.support.query.BasicQueryModel;
import tum.cms.sim.momentum.simulator.component.ComponentManager;
import tum.cms.sim.momentum.simulator.factory.ModelFactory;
import tum.cms.sim.momentum.utility.generic.PropertyBackPackFactory;
import tum.cms.sim.momentum.utility.generic.Unique;

public class StrategicalModelFactory extends ModelFactory<StrategicalModelConfiguration, StrategicalModel>{

	@Override
	public StrategicalModel createModel(StrategicalModelConfiguration configuration,
			ComponentManager componentManager) {	
		
		StrategicalModel strategicalModel = new StrategicalModel();
		DestinationChoiceModel subStrategicModel = null;
		
		switch(configuration.getType()) {
		
		case ODMatrix:
			
			subStrategicModel = new ODMatrixStrategical();
			
			break;
			
		case NoDecision: 
			
			subStrategicModel = new NoDecisionStrategical();
			break;
			
		case InterestFunction:
			
			subStrategicModel = new InterestStrategical();
			break;
			
		case ShortestDestination:
			
			subStrategicModel = new ShortestDestinationStrategical();
			break;
			
		case StrictOrder:
			
			subStrategicModel = new StrictOrderStrategical();
			break;
			
		case CognitiveSpatialChoice:
			
			subStrategicModel = new CognitiveSpatialChoiceStrategical();
			break;
			
		default:
			break;

		}

		Unique.generateUnique(strategicalModel, configuration);
		strategicalModel.setExeuctionId(strategicalModel.getId());
		strategicalModel.setDestinationChoiceModel(subStrategicModel);
		
		subStrategicModel.setPropertyBackPack(PropertyBackPackFactory.fillProperties(configuration));
		Unique.generateUnique(subStrategicModel, configuration);		
	
		PerceptionalModel perceptualModel = componentManager.getPerceptionalModel(configuration.getPerceptualModel());
		BasicQueryModel queryModel = componentManager.getQueryModels().stream().findFirst().get();
		
		this.fillComposition(strategicalModel, perceptualModel, queryModel, componentManager);
		this.fillComposition(subStrategicModel, perceptualModel, queryModel, componentManager);
		
		return strategicalModel;
	}
}
