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

package tum.cms.sim.momentum.visualization.utility;

import java.util.Random;

import javafx.scene.paint.Color;
import tum.cms.sim.momentum.visualization.model.VisualizationModel;
import tum.cms.sim.momentum.visualization.model.geometry.PedestrianModel;

public abstract class ColorGenerator {

	public static void generateGroupColors(VisualizationModel visualizationModel) {

		Random random = new Random();

		for (PedestrianModel pedestrianShapeModel : visualizationModel.getPedestrianShapes().values()) {

			if (!PedestrianModel.getGroupColorMap().containsKey(pedestrianShapeModel.getGroupId())) {

//				int gamble = 10;

//				while (gamble > 0) {

					double redRandom = random.nextInt(600) / 1000.0 + 0.25;
					double blueRandom = random.nextInt(600) / 1000.0 + 0.25;

//					if (!visualizationModel.getRedPedestrianGroupColor().contains(redRandom)
//							&& !visualizationModel.getBluePedestrianGroupColor().contains(blueRandom)) {

						visualizationModel.getRedPedestrianGroupColor().add(redRandom);
						visualizationModel.getBluePedestrianGroupColor().add(blueRandom);

						Color groupColor = new Color(redRandom, blueRandom, 0.25, 1.0);

						PedestrianModel.getGroupColorMap().put(pedestrianShapeModel.getGroupId(), groupColor);
//						break;
//					}
//				}
			}
		}
	}
	
	public static void generateSeedColors(VisualizationModel visualizationModel) {

		int colorSpace = 0;

		for (PedestrianModel pedestrianShapeModel : visualizationModel.getPedestrianShapes().values()) {

			colorSpace += 200;

			if (!PedestrianModel.getSeedColorMap().containsKey(pedestrianShapeModel.getSeedId())) {

				if (colorSpace > 250) {

					colorSpace = 0;
				}

				Color seedColor = new Color((255.0 - colorSpace) / 255.0, colorSpace / 255.0, 0.25, 1.0);

				PedestrianModel.getSeedColorMap().put(pedestrianShapeModel.getSeedId(), seedColor);
			}
		}
	}
	
}
