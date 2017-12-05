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

import tum.cms.sim.momentum.model.output.writerSources.genericWriterSources.ModelPedestrianWriterSource;

public class ZengSocialForceWriterSource
		extends ModelPedestrianWriterSource<ZengOperational, ZengPedestrianExtension> {

	private static final String Acceleration = "Acceleration";
	private static final String AccelerationStrength = "AccelerationStrength";
	private static final String IndividualDirection = "IndividualDirection";

	private static final String SelfDrivingForce = "SelfDrivingForce";
	private static final String SelfDrivingStrength = "SelfDrivingStrength";
	private static final String PedestrianInteractionForce = "PedestrianInteractionForce";
	private static final String PedestrianInteractionStrength = "PedestrianInteractionStrength";
	private static final String CarInteractionForce = "CarInteractionForce";
	private static final String CarInteractionStrength = "CarInteractionStrength";
	private static final String CrosswalkInteractionForce = "CrosswalkInteractionForce";
	private static final String CrosswalkInteractionStrength = "CrosswalkInteractionStrength";
	private static final String ObstacleInteractionForce = "ObstacleInteractionForce";
	private static final String ObstacleInteractionStrength = "ObstacleInteractionStrength";

	@Override
	protected boolean canWrite(ZengPedestrianExtension currentPedestrianExtension) {
		return true;
	}

	@Override
	protected String getPedestrianData(ZengPedestrianExtension currentPedestrianExtension, String format,
			String dataElement) {


		String result = "";

		switch (dataElement) {

			case Acceleration:
				result = String.format(format, currentPedestrianExtension.getAcceleration());
				break;

			case AccelerationStrength:
				result = String.format(format, currentPedestrianExtension.getAcceleration().getMagnitude());
				break;

			case IndividualDirection:
				result = String.format(format, currentPedestrianExtension.getIndividualDirection());
				break;

			case SelfDrivingForce:
				result = String.format(format, currentPedestrianExtension.getSelfDrivingForce());
				break;

			case SelfDrivingStrength:
				result = String.format(format, currentPedestrianExtension.getSelfDrivingForce().getMagnitude());
				break;

			case PedestrianInteractionForce:
				result = String.format(format, currentPedestrianExtension.getPedestrianInteractionForce());
				break;

			case PedestrianInteractionStrength:
				result = String.format(format, currentPedestrianExtension.getPedestrianInteractionForce().getMagnitude());
				break;

			case CarInteractionForce:
				result = String.format(format, currentPedestrianExtension.getCarInteractionForce());
				break;

			case CarInteractionStrength:
				result = String.format(format, currentPedestrianExtension.getCarInteractionForce().getMagnitude());
				break;

			case CrosswalkInteractionForce:
				result = String.format(format, currentPedestrianExtension.getCrosswalkInteractionForce());
				break;

			case CrosswalkInteractionStrength:
				result = String.format(format, currentPedestrianExtension.getCrosswalkInteractionForce().getMagnitude());
				break;

			case ObstacleInteractionForce:
				result = String.format(format, currentPedestrianExtension.getObstacleInteractionForce());
				break;

			case ObstacleInteractionStrength:
				result = String.format(format, currentPedestrianExtension.getObstacleInteractionForce().getMagnitude());
				break;

			default:
				break;
		}

		return result;
	}
}
