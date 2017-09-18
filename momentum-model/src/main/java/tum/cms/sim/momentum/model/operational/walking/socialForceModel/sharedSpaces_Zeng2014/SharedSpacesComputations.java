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

package tum.cms.sim.momentum.model.operational.walking.socialForceModel.sharedSpaces_Zeng2014;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import tum.cms.sim.momentum.data.agent.pedestrian.types.IOperationalPedestrian;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrian;
import tum.cms.sim.momentum.utility.geometry.GeometryFactory;
import tum.cms.sim.momentum.utility.geometry.Vector2D;

public class SharedSpacesComputations {


	public static boolean inVisualRange(Vector2D position, Vector2D direction, double visual_range_radius, double visual_range_angle, List<Vector2D> points) {
		for(Vector2D point : points) {
			if (inVisualRange(position, direction, visual_range_radius, visual_range_angle, point)) {
				return true;
			}
		}
		return false;
	}

	public static boolean inVisualRange(Vector2D position, Vector2D direction, double visual_range_radius, double visual_range_angle, Vector2D point) {
		if(position.distance(point) <= visual_range_radius && getRelativeAngle(position, direction, point) <= visual_range_angle ) {
			return true;
		} else {
			return false;
		}
	}


	public static Vector2D computeRepulsiveForceConflictingPedestrians(IOperationalPedestrian pedestrian, Collection<IPedestrian> otherPedestrians, double timeStepDuration, double visual_range_radius, double visual_range_angle,
																	   double interaction_strength_for_repulsive_force_from_surrounding_pedestrians, double interaction_range_for_relative_distance, double range_for_relative_conflicting_time, double precision)
	{
		List<IPedestrian> otherPedestriansInVisualRange = otherPedestrians.stream()
				.filter(o ->  inVisualRange(pedestrian.getPosition(), pedestrian.getVelocity(), visual_range_radius, visual_range_angle, o.getPosition()))
				.collect(Collectors.toList());

		Vector2D force = GeometryFactory.createVector(0, 0);

		for(IPedestrian otherPedestrian : otherPedestriansInVisualRange)
		{
			double timeToConflictPoint = SharedSpacesComputations.getTimeToConflictPoint(pedestrian.getPosition(), pedestrian.getVelocity(), otherPedestrian.getPosition(), otherPedestrian.getVelocity(), precision);

			Vector2D distanceVector = pedestrian.getPosition().subtract(otherPedestrian.getPosition());
			double b = 0.5 * Math.sqrt(
					Math.pow(distanceVector.getMagnitude() + distanceVector.subtract(otherPedestrian.getVelocity().multiply(timeStepDuration)).getMagnitude(),2) -
							Math.pow(otherPedestrian.getVelocity().multiply(timeStepDuration).getMagnitude(),2));

			// interaction angle factor that explains the anisotropic behavior
			double omega = 1;

			// normalized vector perpendicular to the tangent line of the elliptical force field of pedestrian b_i
			SharedSpacesComputations.Ellipse forceEllipse = new SharedSpacesComputations.Ellipse(otherPedestrian.getPosition(),
					otherPedestrian.getPosition().sum(otherPedestrian.getVelocity()), b);

			Vector2D ellipseNormal = forceEllipse.getNormal(pedestrian.getPosition());
			//System.out.println("normal: (" + perpendicularVectorToTangent.getXComponent() + "," + perpendicularVectorToTangent.getYComponent() + ")");

			double multiplier = interaction_strength_for_repulsive_force_from_surrounding_pedestrians *
					Math.exp(-interaction_range_for_relative_distance*b - range_for_relative_conflicting_time*timeToConflictPoint) *
					omega;
			/*if(multiplier > 0.01)
			{
				//System.out.println("multiplier: " + multiplier);
				TestPrint.printPedestrian(pedestrian, otherPedestrians);
			}*/

			force = force.sum(ellipseNormal.multiply(multiplier));
		}

		// repulsive force from conflicting pedestrian

		return force;
	}


	public static double getRelativeAngle(Vector2D mePos, Vector2D meDirection, Vector2D youPos)
	{
		// see https://stackoverflow.com/a/4880152
		double angle1 = Math.atan2(youPos.getYComponent() - mePos.getYComponent(),
				youPos.getXComponent() - mePos.getXComponent());
		double angle2 = Math.atan2(meDirection.getYComponent(), meDirection.getXComponent());
		double rel_angle = angle2 - angle1;
		rel_angle = Math.toDegrees(rel_angle);

		// reduce the angle to -180 < angle <= 180, see https://stackoverflow.com/a/2323034
		rel_angle =  rel_angle % 360;
		rel_angle = (rel_angle + 360) % 360;
		if (rel_angle > 180)
			rel_angle -= 360;

		return rel_angle;
	}

	public static double getTimeToConflictPoint(Vector2D mePosition, Vector2D meVelocity, Vector2D youPosition, Vector2D youVelocity, double precision)
	{

		double x12 = mePosition.getXComponent() - mePosition.sum(meVelocity).getXComponent();
		double x34 = youPosition.getXComponent() - youPosition.sum(youVelocity).getXComponent();
		double y12 = mePosition.getYComponent() - mePosition.sum(meVelocity).getYComponent();
		double y34 = youPosition.getYComponent() - youPosition.sum(youVelocity).getYComponent();

		double c = x12 * y34 - y12 * x34;
		if (Math.abs(c) < precision)
		{
			// lines are parallel

			// mePos-->meVel-----youVel<--youPos
			double distance_mePos_meVel = meVelocity.getMagnitude();
			double distance_mePos_youVel = mePosition.distance(youPosition.sum(youVelocity));
			double distance_youPos_youVel = youVelocity.getMagnitude();
			double distance_meVel_youPos = mePosition.sum(meVelocity).distance(youPosition);

			double distance_mePos_youPos = mePosition.distance(youPosition);

			if( Math.abs(distance_mePos_meVel + distance_meVel_youPos - distance_mePos_youPos) < precision &&
					Math.abs(distance_mePos_youVel + distance_youPos_youVel - distance_mePos_youPos) < precision)
			{
				// lines are identical, and velocity vectors point to each other
				return 0.0;
			}
			else
			{
				// lines are parallel, and either not identical or the velocity vectors do not point to each other
				return Double.POSITIVE_INFINITY;
			}

		}
		double a = mePosition.getXComponent() * mePosition.sum(meVelocity).getYComponent() - mePosition.getYComponent() * mePosition.sum(meVelocity).getXComponent();
		double b = youPosition.getXComponent() * youPosition.sum(youVelocity).getYComponent() - youPosition.getYComponent() * youPosition.sum(youVelocity).getXComponent();

		double conflictX = (a * x34 - b * x12) / c;
		double conflictY = (a * y34 - b * y12) / c;

		Vector2D conflictPoint = GeometryFactory.createVector(conflictX , conflictY);

		double meCos = Math.cos(meVelocity.getAngleBetween(conflictPoint.subtract(mePosition)));
		double youCos = Math.cos(youVelocity.getAngleBetween(conflictPoint.subtract(youPosition)));
		double meTimeToConflictPoint = (conflictPoint.subtract(mePosition).getMagnitude() / meVelocity.getMagnitude()) * meCos;
		double youTimeToConflictPoint = (conflictPoint.subtract(youPosition).getMagnitude() / youVelocity.getMagnitude()) * youCos;

		if (meTimeToConflictPoint > 0 && youTimeToConflictPoint > 0) {
			return Math.abs(meTimeToConflictPoint - youTimeToConflictPoint);
		} else if(Double.isNaN(meTimeToConflictPoint) || Double.isNaN(youTimeToConflictPoint))
		{
			// this is the case, if no velocity or parallel trajectories
			return Double.POSITIVE_INFINITY;
		} else {
			// this is the case, if pedestrian already passed the conflict point
			return Double.POSITIVE_INFINITY;
		}
	}
	
    static class Ellipse {
    	Vector2D F1, F2;
    	double lengthMajorAxis = 0;
    	double lengthMinorAxis = 0;
    	
    	public Ellipse(Vector2D F1, Vector2D F2, double lengthMinorAxis) {
    		this.F1 = F1;
    		this.F2 = F2;
    		this.lengthMinorAxis = lengthMinorAxis;
    		
    		this.lengthMajorAxis = Math.sqrt( Math.pow(F2.subtract(F1).getMagnitude()/2,2) +
    				Math.pow(lengthMinorAxis, 2));
    	}
    	
    	public Vector2D getNormal(Vector2D point)
    	{
    		// https://stackoverflow.com/a/13532809
    		point = this.tranformWorldCoordinatesToEllipse(point);
    		
    		Vector2D normal = GeometryFactory.createVector(point.getXComponent() * lengthMinorAxis/lengthMajorAxis,
    				point.getYComponent() * lengthMajorAxis/lengthMinorAxis).getNormalized();
    		
    		normal = tranformEllipseNormalToWorld(normal);
    		return normal;
    	}
    	
    	public Vector2D tranformWorldCoordinatesToEllipse(Vector2D point)
    	{
    		Vector2D pointTranslated = point.subtract(getCenter());
    		double angle = -this.getRotationAngle();
    		Vector2D rotated = GeometryFactory.createVector(
    				Math.cos(angle)*pointTranslated.getXComponent()-Math.sin(angle)*pointTranslated.getYComponent(),
    				Math.sin(angle)*pointTranslated.getXComponent()+Math.cos(angle)*pointTranslated.getYComponent());
    		return rotated;
    	}
    	
    	public Vector2D tranformEllipseNormalToWorld(Vector2D point)
    	{
    		double angle = this.getRotationAngle();
    		return GeometryFactory.createVector(
    				Math.cos(angle)*point.getXComponent()-Math.sin(angle)*point.getYComponent(),
    				Math.sin(angle)*point.getXComponent()+Math.cos(angle)*point.getYComponent());
    	}
    	
    	public Vector2D getCenter()
    	{
    		return GeometryFactory.createVector( (F1.getXComponent()+F2.getXComponent())/2, (F1.getYComponent()+F2.getYComponent())/2);
    	}
    	
    	public double getRotationAngle()
    	{
    		Vector2D xAxis = GeometryFactory.createVector(1, 0);
    		return xAxis.getAngleBetween(F2.subtract(F1));
    	}
    }
	
}