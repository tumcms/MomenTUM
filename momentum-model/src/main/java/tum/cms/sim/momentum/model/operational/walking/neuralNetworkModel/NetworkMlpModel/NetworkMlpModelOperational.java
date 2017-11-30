package tum.cms.sim.momentum.model.operational.walking.neuralNetworkModel.NetworkMlpModel;

import java.util.Collection;

import tum.cms.sim.momentum.data.agent.pedestrian.types.IOperationalPedestrian;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrianExtension;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.operational.walking.WalkingModel;

public class NetworkMlpModelOperational extends WalkingModel {

	@Override
	public IPedestrianExtension onPedestrianGeneration(IRichPedestrian pedestrian) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onPedestrianRemoval(IRichPedestrian pedestrian) {
		// TODO Auto-generated method stub

	}

	@Override
	public void callBeforeBehavior(SimulationState simulationState, Collection<IRichPedestrian> pedestrians) {
		// TODO Auto-generated method stub

	}

	@Override
	public void callAfterBehavior(SimulationState simulationState, Collection<IRichPedestrian> pedestrians) {
		// TODO Auto-generated method stub

	}

	@Override
	public void callPedestrianBehavior(IOperationalPedestrian pedestrian, SimulationState simulationState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void callPreProcessing(SimulationState simulationState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void callPostProcessing(SimulationState simulationState) {
		// TODO Auto-generated method stub

	}

}
