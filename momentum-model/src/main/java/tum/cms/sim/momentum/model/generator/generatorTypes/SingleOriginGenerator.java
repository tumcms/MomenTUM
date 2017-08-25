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

package tum.cms.sim.momentum.model.generator.generatorTypes;

import java.util.Collection;
import org.apache.commons.math3.util.FastMath;
import tum.cms.sim.momentum.configuration.generator.GeneratorConfiguration.GeneratorType;
import tum.cms.sim.momentum.data.agent.pedestrian.state.other.StaticState;
import tum.cms.sim.momentum.data.layout.area.OriginArea;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.generator.GeneratorIntervalFactory;
import tum.cms.sim.momentum.utility.geometry.Vector2D;
import tum.cms.sim.momentum.utility.geometry.operation.GeometryAdditionals;
import tum.cms.sim.momentum.utility.probability.distrubution.DistributionFactory;
import tum.cms.sim.momentum.utility.probability.distrubution.IDistribution;

public class SingleOriginGenerator extends OriginGenerator {

	protected OriginArea area = null;	
	protected Integer originId = null;
	protected Integer scenarioId = null;
	protected GeneratorType generatorType = null;

	public SingleOriginGenerator(Integer originId, GeneratorType generatorType, int scenarioId) {
		
		this.originId = originId;
		this.generatorType = generatorType;
		this.scenarioId = scenarioId;
	}
	
	@Override
	public void callPreProcessing(SimulationState simulationState) {

		super.callPreProcessing(simulationState);
		
		if(pedestrianVariance != null) {		
			
			IDistribution distrubtion = DistributionFactory.createNormalDistribution(maximalPedestrians, pedestrianVariance);
		
			this.maximalPedestrians = 0;
			
			while(this.maximalPedestrians <= 0) {
				this.maximalPedestrians = (int)FastMath.round(distrubtion.getSample());
			}
		}
		
		this.intervalSet = GeneratorIntervalFactory.createIntervalSet(this.properties,
				this.generatorType,
				this.generatorStartTime, 
				this.generatorEndTime, 
				this.maximalPedestrians);
		
		this.area = (OriginArea) this.scenarioManager.getOrigins()
				.stream()
				.filter(origin -> origin.getId().equals(originId))
				.findFirst()
				.get();
		
		this.generatorGeometry.initialize(this.scenarioManager,
				this.scenarioLatticeId,
				this.area, 
				this.safetyDistance, 
				this.pedestrianSeed.getMaximalRadius());
	}
	
	@Override
	public void execute(Collection<? extends Void> splittTask, SimulationState simulationState) {
		
		double amountPedestriansToGenerate = 0;
		Collection<Vector2D> freePositions = null;
		Collection<Vector2D> selectedPositions = null;
		
		
		if(this.maximalPedestrians - (this.generatedPedestrians + this.remainingPedestrians) > 0) {
			
			amountPedestriansToGenerate = this.intervalSet.allowPedestrianGeneration(simulationState,
				this.generatedPedestrians,
				this.maximalPedestrians);
		}
		
		amountPedestriansToGenerate += this.remainingPedestrians;
				
		if(amountPedestriansToGenerate >= 1) {
	
			freePositions = this.generatorGeometry.calculateFreeSpawnPositions(this.pedestrianManager.getAllPedestrians(), 
					this.pedestrianSeed.getMaximalRadius());
		}

		if(freePositions != null && freePositions.size() > 0) {
				
			selectedPositions = this.generatorGeometry.selectFromFreePositions(freePositions, (int)amountPedestriansToGenerate);
		}
		
		if(selectedPositions != null && 
		   selectedPositions.size() >= pedestrianSeed.getNextGroupSize() &&
		   amountPedestriansToGenerate >= pedestrianSeed.getNextGroupSize()) {
			
			int currentToGenerate = pedestrianSeed.getNextGroupSize();
			
			if(currentToGenerate == 0) {
				
				currentToGenerate = Integer.MIN_VALUE;
			}
			
			for(Vector2D position : selectedPositions) {
				
				StaticState staticState = pedestrianSeed.generateStaticState(this.area.getId(), this.scenarioId);
				Vector2D heading = GeometryAdditionals.createUnitVectorForAngle(this.basicHeading);
			
				if(currentToGenerate > 1) {
					
					staticState.setLeader(false);
				}
			
				pedestrianManager.createPedestrian(staticState, 
						this.area,
						position, 
						heading,
						simulationState.getCurrentTime());
				
				generatedPedestrians++;
				amountPedestriansToGenerate -= 1;
				
				if(staticState.getGroupSize() > 1) {
					
					currentToGenerate--;	
				}
				
				if(currentToGenerate == 0 || amountPedestriansToGenerate == 0) {
					
					break;
				}
			}
		}
	
		this.remainingPedestrians = amountPedestriansToGenerate;
	}

	@Override
	public void callPostProcessing(SimulationState simulationState) {

		// nothing to do
	}
}
