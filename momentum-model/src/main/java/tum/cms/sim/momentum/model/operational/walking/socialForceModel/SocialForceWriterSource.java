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

package tum.cms.sim.momentum.model.operational.walking.socialForceModel;

import tum.cms.sim.momentum.model.operational.walking.socialForceModel.ParallelHelbingOperational.BarnesHutParallelHelbingOperational;
import tum.cms.sim.momentum.model.output.writerSources.genericWriterSources.ModelPedestrianWriterSource;
import tum.cms.sim.momentum.utility.geometry.Vector2D;

public class SocialForceWriterSource
		extends ModelPedestrianWriterSource<BarnesHutParallelHelbingOperational, SocialForcePedestrianExtension> {

	private static final String PedestrianInteractionForce = "PedestrianInteractionForce";
	private static final String ObstacleInteractionForce = "ObstacleInteractionForce";
	private static final String IndividualDirection = "IndividualDirection";
	private static final String SelfDrivingForce = "SelfDrivingForce";
	private static final String Acceleration = "Acceleration";

	@Override
	protected boolean canWrite(SocialForcePedestrianExtension currentPedestrianExtension) {
		return true;
	}

	@Override
	protected String getPedestrianData(SocialForcePedestrianExtension currentPedestrianExtension, String format,
			String dataElement) {

		Vector2D result = null;

		switch (dataElement) {

		case PedestrianInteractionForce:
			result = currentPedestrianExtension.getPedestrianInteractionForce();
			break;

		case ObstacleInteractionForce:
			result = currentPedestrianExtension.getObstacleInteractionForce();
			break;

		case IndividualDirection:
			result = currentPedestrianExtension.getIndividualDirection();
			break;

		case SelfDrivingForce:
			result = currentPedestrianExtension.getSelfDrivingForce();
			break;

		case Acceleration:
			result = currentPedestrianExtension.getAcceleration();
			break;

		default:
			break;
		}

		return result.toString();
	}
}
