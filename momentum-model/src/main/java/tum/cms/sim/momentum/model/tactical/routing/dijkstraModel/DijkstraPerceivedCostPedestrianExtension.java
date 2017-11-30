package tum.cms.sim.momentum.model.tactical.routing.dijkstraModel;

import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrianExtension;

public class DijkstraPerceivedCostPedestrianExtension implements IPedestrianExtension {

    public DijkstraPerceivedCostPedestrianExtension(double pedestrianDedicatedAreaFactor) {

        this.pedestrianDedicatedAreaFactor = pedestrianDedicatedAreaFactor;
    }

    public double getPedestrianDedicatedAreaFactor() {
        return pedestrianDedicatedAreaFactor;
    }

    private double pedestrianDedicatedAreaFactor = 0.0;

}
