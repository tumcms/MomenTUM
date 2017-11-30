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

package tum.cms.sim.momentum.model.perceptional.sightConePerceptionModel;

import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrian;
import tum.cms.sim.momentum.data.layout.area.Area;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.perceptional.PerceptionalModel;
import tum.cms.sim.momentum.utility.geometry.Vector2D;
import tum.cms.sim.momentum.utility.graph.Edge;
import tum.cms.sim.momentum.utility.graph.Vertex;

import java.util.List;
import java.util.stream.Collectors;

public class SightConePerceptionModel extends PerceptionalModel {

    public void setRadius(double radius) {
        this.radius = radius;
    }
    private double radius; // range of vision, [m]


    public void setAngle(double angle) {
        this.angle = angle;
    }

    public double getAngleInRadians() {
        return Math.toRadians(angle);
    }

    private double angle;   // angular aperture, [deg]


	@Override
	public void callPreProcessing(SimulationState simulationState) {

		setRadius(this.properties.getDoubleProperty("radius"));
		setAngle(this.properties.getDoubleProperty("angle"));
	}

	@Override
	public void callPostProcessing(SimulationState simulationState) {
		// nothing to do
	}

	@Override
	public List<IPedestrian> getPerceptedPedestrianPositions(IPedestrian pedestrian, SimulationState simulationState) {
		return null;
	}

	@Override
	public List<Vector2D> getPerceptedObstaclePositions(IPedestrian pedestrian, SimulationState simulationState) {
		return null;
	}

	@Override
	public List<IPedestrian> getPerceptedPedestrians(IPedestrian currentPedestrian, SimulationState simulationState) {

		return this.pedestrianManager.getAllPedestriansImmutable()
				.stream()
				.filter(otherPedestrian -> !otherPedestrian.getId().equals(currentPedestrian.getId()))
				.filter(otherPedestrian -> isVisible(currentPedestrian, otherPedestrian.getPosition()))
				.collect(Collectors.toList());
	}
	
	@Override
	public boolean isVisible(IPedestrian currentPedestrian, IPedestrian otherPedestrian) {
        return isVisible(currentPedestrian, otherPedestrian.getPosition());
	}

	@Override
	public boolean isVisible(IPedestrian pedestrian, Vector2D position) {
		double currentDistance = position.distance(pedestrian.getPosition());
		double currentAngle = pedestrian.getHeading().getAngleBetween(position.difference(pedestrian.getPosition()));

		return currentDistance <= this.radius && Math.abs(currentAngle) <= getAngleInRadians() / 2;
	}

	@Override
	public boolean isVisible(IPedestrian pedestrian, Area area) {
		return false;
	}

	@Override
	public boolean isVisible(IPedestrian pedestrian, Vertex vertex) {
		return false;
	}

	@Override
	public boolean isVisible(IPedestrian pedestrian, Edge edge) {
		return false;
	}

	@Override
	public boolean isVisible(Vector2D viewPort,  Vector2D position) {
		return true;
	}

	@Override
	public double getPerceptionDistance() {
		return 0;
	}

	@Override
	protected void supportModelUpdate(SimulationState simulationState) {

	}
}
