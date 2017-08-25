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

package tum.cms.sim.momentum.visualization.handler;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import org.apache.commons.math3.util.FastMath;
import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.transform.Transform;
import tum.cms.sim.momentum.visualization.view.dialogControl.InformationDialogCreator;

public class SnapshotHandler {
	
	private final static String imageType = "png";
	private final static String snapshotAlertText = "Taking Snapshot...";
	
	private Node targetOfSnapshot = null;
	private double pixelScale = 1;
	private String outputFileName = null;
	
	public SnapshotHandler(Node targetOfSnapshot, double pixelScale, String outputFileName) {
		
		this.targetOfSnapshot = targetOfSnapshot;
		this.pixelScale = pixelScale;
		this.outputFileName = outputFileName;
	}
	
	public void snapshot() throws IOException {
		
		WritableImage writableImage = new WritableImage(
		    		(int)FastMath.rint(pixelScale * targetOfSnapshot.getBoundsInParent().getWidth()), 
		    		(int)FastMath.rint(pixelScale * targetOfSnapshot.getBoundsInParent().getHeight()));
	    
	    SnapshotParameters spa = new SnapshotParameters();
	    spa.setTransform(Transform.scale(pixelScale, pixelScale));
	    spa.setDepthBuffer(true);
	    writableImage = targetOfSnapshot.snapshot(spa, writableImage);
		BufferedImage image = null;
	    image = SwingFXUtils.fromFXImage(writableImage, image);
		
		if (!outputFileName.endsWith(".png")) {
			
			ImageIO.write(image, imageType, new File(outputFileName + "." + imageType));
		}
		else {
			
			ImageIO.write(image, imageType, new File(outputFileName));
		}
		
		InformationDialogCreator.createSnapshotConfirmationDialog(outputFileName);
	}

	public String getAlertText() {

		return snapshotAlertText;
	}
}
