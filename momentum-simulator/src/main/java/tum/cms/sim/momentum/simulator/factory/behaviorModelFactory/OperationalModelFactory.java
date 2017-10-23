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

import tum.cms.sim.momentum.configuration.model.operational.OperationalModelConfiguration;
import tum.cms.sim.momentum.model.operational.OperationalModel;
import tum.cms.sim.momentum.model.operational.standing.StandingModel;
import tum.cms.sim.momentum.model.operational.walking.WalkingModel;
import tum.cms.sim.momentum.model.perceptional.PerceptionalModel;
import tum.cms.sim.momentum.simulator.component.ComponentManager;
import tum.cms.sim.momentum.simulator.factory.ModelFactory;
import tum.cms.sim.momentum.utility.generic.PropertyBackPackFactory;
import tum.cms.sim.momentum.utility.generic.Unique;

public class OperationalModelFactory extends ModelFactory<OperationalModelConfiguration, OperationalModel>{

	@Override
	public OperationalModel createModel(OperationalModelConfiguration configuration,
			ComponentManager componentManager) {

		OperationalModel operationalModel = new OperationalModel();
		
		Unique.generateUnique(operationalModel, configuration);
		operationalModel.setPropertyBackPack(PropertyBackPackFactory.fillProperties(configuration));
		operationalModel.setExeuctionId(operationalModel.getId());
		
		PerceptionalModel perceptualModel = componentManager.getPerceptionalModel(configuration.getPerceptualModel());
	
		this.fillComposition(operationalModel, perceptualModel, componentManager);
		
		WalkingModel walkingModel = componentManager.getWalkingModel(configuration.getWalkingReference().getModelId());
		this.fillComposition(walkingModel, perceptualModel, componentManager);
		operationalModel.setWalkingModel(walkingModel);
		
		if(configuration.getStandingReference() != null) {
			
			StandingModel standingModel = componentManager.getStandingModel(configuration.getStandingReference().getModelId());
			this.fillComposition(standingModel, perceptualModel, componentManager);
			operationalModel.setStandingModel(standingModel);
		}
		
		return operationalModel;
	}
}
