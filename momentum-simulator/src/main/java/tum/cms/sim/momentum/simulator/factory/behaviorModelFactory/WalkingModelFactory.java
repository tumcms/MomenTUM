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

import tum.cms.sim.momentum.configuration.model.operational.WalkingModelConfiguration;
import tum.cms.sim.momentum.model.operational.walking.WalkingModel;
import tum.cms.sim.momentum.model.operational.walking.cellularAutomatons.cellularStockModel_Biedermann2015.ParallelStockOperational;
import tum.cms.sim.momentum.model.operational.walking.cellularAutomatons.cellularStockModel_Biedermann2015.StockOperational;
import tum.cms.sim.momentum.model.operational.walking.cellularAutomatons.dynamicFloorfield_Kneidl2012.DynamicFloorfieldOperational;
import tum.cms.sim.momentum.model.operational.walking.csvPlackback.CsvPlaybackOperational;
import tum.cms.sim.momentum.model.operational.walking.empiricallyGrounded_Bonneaud2014.BonneaudOperational;
import tum.cms.sim.momentum.model.operational.walking.groupBehaviour_Moussaid2010.MoussaidOperational;
import tum.cms.sim.momentum.model.operational.walking.macroscopicModels.classicLWRmodel.ClassicLWR;
import tum.cms.sim.momentum.model.operational.walking.moussaidHeuristic.MoussaidHeuristicOperational;
import tum.cms.sim.momentum.model.operational.walking.noInteractionModel.NoInteractionOperational;
import tum.cms.sim.momentum.model.operational.walking.socialForceModel.HelbingOperational.HelbingOperational;
import tum.cms.sim.momentum.model.operational.walking.socialForceModel.ParallelHelbingOperational.BarnesHutParallelHelbingOperational;
import tum.cms.sim.momentum.model.operational.walking.socialForceModel.sharedSpaces_Zeng2014.SharedSpaceForceOperational;
import tum.cms.sim.momentum.simulator.component.ComponentManager;
import tum.cms.sim.momentum.simulator.factory.ModelFactory;
import tum.cms.sim.momentum.utility.generic.PropertyBackPackFactory;
import tum.cms.sim.momentum.utility.generic.Unique;

public class WalkingModelFactory extends ModelFactory<WalkingModelConfiguration, WalkingModel>{

	@Override
	public WalkingModel createModel(WalkingModelConfiguration configuration, ComponentManager componentManager) {
	
		WalkingModel walkingModel = null;
		
		switch (configuration.getType()) {
		
		case CsvPlayback:
			walkingModel = new CsvPlaybackOperational();
			break;
			
		case NoInteraction:
			walkingModel = new NoInteractionOperational();
			break;
			
		case SocialForce:
			walkingModel = new HelbingOperational();
			break;
			
		case ParallelSocialForce:
			walkingModel = new BarnesHutParallelHelbingOperational();
			break;
			
		case Bonneaud:
			walkingModel = new BonneaudOperational();
			break;
			
		case StockCellular:
			walkingModel = new StockOperational();
			break;
			
		case ParallelStockCellular:
			walkingModel = new ParallelStockOperational();
			break;
		
		case Moussaid:
			walkingModel = new MoussaidOperational();
			break;
		
		case DynamicFloorfield:
			walkingModel = new DynamicFloorfieldOperational();
			break;

		case MoussaidHeuristic:
			walkingModel = new MoussaidHeuristicOperational();
			break;

		case ClassicLWR:
			walkingModel = new ClassicLWR();
			break;
			
		case SharedSpaceForce:
			SharedSpaceForceOperational shareSpaceForceOperational = new SharedSpaceForceOperational();
			shareSpaceForceOperational.setCarManager(componentManager.getCarManager());
			walkingModel = shareSpaceForceOperational;
			break;
			
		default:
			break;
		}
		
		Unique.generateUnique(walkingModel, configuration);
		walkingModel.setPropertyBackPack(PropertyBackPackFactory.fillProperties(configuration));
		
		return walkingModel;
	}
}
