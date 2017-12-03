package tum.cms.sim.momentum.model.operational.walking.neuralNetworkModel.NetworkMlpModel;

import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrianExtension;
import tum.cms.sim.momentum.utility.geometry.GeometryFactory;
import tum.cms.sim.momentum.utility.geometry.Vector2D;

public class NetworkMlpPedestrianExtension implements IPedestrianExtension {

	private Vector2D lastVelocity = GeometryFactory.createVector(0.0, 0.0);

	public Vector2D getLastVelocity() {
		return lastVelocity;
	}

	public void setLastVelocity(Vector2D lastVelocity) {
		this.lastVelocity = lastVelocity;
	}
	
	private Vector2D secondToLastVelocity = GeometryFactory.createVector(0.0, 0.0);

	public Vector2D getSecondToLastVelocity() {
		return secondToLastVelocity;
	}

	public void setSecondToLastVelocity(Vector2D secondToLastVelocity) {
		this.secondToLastVelocity = secondToLastVelocity;
	}

}
