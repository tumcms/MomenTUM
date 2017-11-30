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

package tum.cms.sim.momentum.model.operational.walking.socialForceModel.ZengOperational;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import tum.cms.sim.momentum.data.agent.pedestrian.types.IOperationalPedestrian;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrian;
import tum.cms.sim.momentum.data.layout.area.TaggedArea;
import tum.cms.sim.momentum.utility.geometry.*;

public class ZengAdditionalComputations {

	public static Vector2D computeRepulsiveForceConflictingPedestrians(IOperationalPedestrian pedestrian, Collection<IPedestrian> otherPedestrians, double timeStepDuration,
																	   double interaction_strength_for_repulsive_force_from_surrounding_pedestrians, double interaction_range_for_relative_distance,
																	   double range_for_relative_conflicting_time, double strength_of_forces_exerted_from_other_pedestrians)
	{
        // repulsive force from conflicting pedestrian

		Vector2D force = GeometryFactory.createVector(0, 0);

		for(IPedestrian otherPedestrian : otherPedestrians)
		{
			double timeToConflictPoint = ZengAdditionalComputations.calculateTimeToConflictPoint(pedestrian.getPosition(), pedestrian.getVelocity(), otherPedestrian.getPosition(), otherPedestrian.getVelocity());

			Vector2D distanceVector = pedestrian.getPosition().subtract(otherPedestrian.getPosition());
			double b = 0.5 * Math.sqrt(
					Math.pow(distanceVector.getMagnitude() + distanceVector.subtract(otherPedestrian.getVelocity().multiply(timeStepDuration)).getMagnitude(),2) -
							Math.pow(otherPedestrian.getVelocity().multiply(timeStepDuration).getMagnitude(),2));

			// interaction angle factor that explains the anisotropic behavior
			double omega = calculateInteractionAngleFactor(pedestrian.getVelocity(), distanceVector, strength_of_forces_exerted_from_other_pedestrians);

			// normalized vector perpendicular to the tangent line of the elliptical force field of pedestrian b_i
            Ellipse2D forceEllipse = GeometryFactory.createEllipse(otherPedestrian.getPosition(),
					otherPedestrian.getPosition().sum(otherPedestrian.getVelocity().multiply(timeStepDuration)), b);
			Vector2D ellipseNormal = forceEllipse.normal(pedestrian.getPosition());
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

		return force;
	}


	public static Double calculateTimeToConflictPoint(Vector2D currentPosition, Vector2D currentVelocity, Vector2D otherPosition, Vector2D otherVelocity)
	{
		if(currentVelocity.isZero() || otherVelocity.isZero()) {
			return Double.POSITIVE_INFINITY;
		}

		Ray2D currentRay = GeometryFactory.createRay2D(currentPosition, currentVelocity);
		Ray2D otherRay = GeometryFactory.createRay2D(otherPosition, otherVelocity);

		Vector2D conflictPoint = currentRay.intersectionPoint(otherRay);
		if(conflictPoint != null) {
			double curTimeToConflictPoint = (conflictPoint.subtract(currentPosition).getMagnitude() / currentVelocity.getMagnitude());
			double othTimeToConflictPoint = (conflictPoint.subtract(otherPosition).getMagnitude() / otherVelocity.getMagnitude());

			return Math.abs(curTimeToConflictPoint - othTimeToConflictPoint);
		}

		Segment2D conflictSegment = currentRay.intersectionSegment(otherRay);
		if(conflictSegment != null) {
			return 0.0;
		}

		Ray2D conflictRay = currentRay.intersectionRay(otherRay);
		if(conflictRay != null) {
			return 0.0;
		}

		return Double.POSITIVE_INFINITY;
	}


	public static double calculateInteractionAngleFactor(Vector2D currentVelocity, Vector2D distance, double influenceStrength) {
		// interaction angle factor that explains the anisotropic behavior

		double phi = currentVelocity.getAngleBetween(distance);
		double cosPhi = currentVelocity.getNormalized().dot(distance.getNormalized());

		double q = 0.0;
		if( -Math.PI <= phi && phi <= Math.PI ) {
			q = 1.0;
		}

		return q * (influenceStrength + (1-influenceStrength) * (1+cosPhi) / 2.0);
	}

	public static TaggedArea findCorrespondingCrosswalk(IOperationalPedestrian pedestrian, List<TaggedArea> crosswalkAreas) {

		pedestrian.getMessageState().clearTopic("crosswalk in");
		pedestrian.getMessageState().clearTopic("crosswalk out");

		Double currentMinDistance = Double.POSITIVE_INFINITY;
		double currentDistance = 0;
		TaggedArea closestCrosswalk = null;

		for(TaggedArea crosswalkArea : crosswalkAreas) {

			if(crosswalkArea.getGeometry().contains(pedestrian.getPosition())) {
				pedestrian.getMessageState().appendMessage("crosswalk in", crosswalkArea.getName());
				return crosswalkArea;
			} else {
				pedestrian.getMessageState().appendMessage("crosswalk out", crosswalkArea.getName());
				currentDistance = crosswalkArea.getGeometry().distanceBetween(pedestrian.getPosition());

				if(currentDistance < currentMinDistance) {
					closestCrosswalk = crosswalkArea;
					currentMinDistance = currentDistance;
				}

			}
		}

		return closestCrosswalk;
	}

	public static Vector2D findNearestCorsswalkBoundaryPoint(IOperationalPedestrian pedestrian, TaggedArea nextCrosswalk, double epsilon) {

		List<Segment2D> segments = nextCrosswalk.getGeometry().getSegments();
		List<Segment2D> longestSegments = segments.stream()
				.sorted((f1, f2) -> Double.compare(f2.getLenghtDistance(), f1.getLenghtDistance()))
				.limit(2)
				.collect(Collectors.toList());

		Vector2D currentPoint = null;
		Vector2D closestPoint = null;
		double currentDistance = 0;
		double minDistance = Double.POSITIVE_INFINITY;

		for (Segment2D seg : longestSegments) {
			List<Vector2D> segmentVertices = seg.getVertices();
			Vector2D segmentDirection = segmentVertices.get(0).subtract(segmentVertices.get(1));

			currentPoint = seg.getPointClosestToVector(pedestrian.getPosition());
			Vector2D currentDistanceVector = currentPoint.subtract(pedestrian.getPosition());
			currentDistance = currentDistanceVector.getMagnitude();

			boolean isOrthogonal = Math.abs(segmentDirection.getNormalized().dot(currentDistanceVector.getNormalized())) < epsilon;

			if(currentDistance < minDistance && isOrthogonal) {
				closestPoint = currentPoint;
				minDistance = currentDistance;
			}
		}

		return closestPoint;
	}

}
