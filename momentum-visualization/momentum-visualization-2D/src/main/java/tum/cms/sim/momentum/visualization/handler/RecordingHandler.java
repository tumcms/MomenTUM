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
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class RecordingHandler {
	
	//private Process ffmpegProcess = null;
	
	private final static String ffmpegCommand = "C://Programmierung//MomenTumV2_workspace//MomenTumV2Visualization//third_party_program//"
										      + "ffmpeg.exe"
										      + " -framerate 24" 
										      + " -i %s" 
										      + " -s:v %dx%d" 
										      + " -c:v libx264" 
											  + " -profile:v high"
											  + " -crf 20 -pix_fmt yuv420p"
											  + " %s";
	
	private final static String imageType = "png";
	private  static String videoFile = "./recording/videoOut.mp4";
	private final static String tempImageNaming = "./recording/img%06d.png";
	private Integer imageNamingIter = 0;
	
	public synchronized Integer nextImageNamingIter() {
		return ++imageNamingIter;
	}

	public synchronized Integer prevImageNamingIter() {
		return ++imageNamingIter;
	}
	
	private Timeline recordTimeLine = null;
	private ThreadPoolExecutor workerPool = null;
	private Pane recordingCanvas = null;
	private BooleanProperty playingProperty = null;
	private BooleanProperty recordingProperty = null;
	private Queue<WritableImage> imageCaputureBuffer = new LinkedBlockingQueue<WritableImage>();
	private Queue<WritableImage> imageWriterBuffer = new LinkedBlockingQueue<WritableImage>();
	private ImageWriter worker;
	
	public RecordingHandler(BooleanProperty playingProperty,
			BooleanProperty recordingProperty,
			Pane recordingCanvas) {
		
		this.playingProperty = playingProperty;
		this.recordingProperty = recordingProperty;
		this.recordingCanvas = recordingCanvas;
		workerPool = new ThreadPoolExecutor(1,
				1,
				0,
				TimeUnit.MILLISECONDS, 
				new ArrayBlockingQueue<Runnable>(1));
		
		worker = new ImageWriter(imageWriterBuffer);
		workerPool.execute(worker);
	}
	
	public void record() {

		this.recordTimeLine = new Timeline(
			new KeyFrame(Duration.seconds(1.0/24.0), 
			new EventHandler<ActionEvent>() {

		    @Override
		    public void handle(ActionEvent event) {

		    	if(!playingProperty.get()) {

		    		while(imageWriterBuffer.size() > 0) {
		    			
		    			try {
							
		    				Thread.sleep(100);
						} 
		    			catch (InterruptedException e) {
					
							e.printStackTrace();
						}
		    		}
		    		
		    		//Platform.runLater(() -> {
		    		
		    			RecordingHandler.this.finalizeRecording();
		    		//});
		    	}
		    	else { // record
		    		
			    	RecordingHandler.this.capture();
		    	}
		    }
		}));
		
		this.recordTimeLine.setCycleCount(Timeline.INDEFINITE);
		this.recordTimeLine.play();
	}


	private synchronized void capture() {
	 	
		WritableImage captureImage = new WritableImage((int)this.recordingCanvas.getWidth(), (int)this.recordingCanvas.getHeight());
		captureImage = this.recordingCanvas.snapshot(new SnapshotParameters(), captureImage);
		imageCaputureBuffer.add(captureImage);
		
		if(imageCaputureBuffer.size() > 1) {
     		
			imageWriterBuffer.addAll(imageCaputureBuffer);
			imageCaputureBuffer.clear();
		}
	}
	
	private synchronized void stop() {
		
		worker.setToFileBuffer(null);
		imageWriterBuffer = null;
		recordTimeLine.stop();
		recordingProperty.set(false);
	}
	
	private synchronized void finalizeRecording() {
		
		recordTimeLine.stop();
		recordingProperty.set(false);
		
		String command = String.format(ffmpegCommand, 
				tempImageNaming,
				(int)this.recordingCanvas.getWidth(),
				(int)this.recordingCanvas.getHeight(),
				videoFile);
				
		ProcessBuilder builder = new ProcessBuilder(command);
		
		builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
		builder.redirectError(ProcessBuilder.Redirect.INHERIT);
		
//		try {
//			
//			//ffmpegProcess = builder.start();
//		} 
//		catch (IOException e) {
//
//			e.printStackTrace();
//		}
	}
	
	private synchronized void write(Queue<WritableImage> imageWriterBuffer) {
		
		if(imageWriterBuffer.size() == 0) {
			
			return;
		}
		
		WritableImage capture = imageWriterBuffer.poll();
		
		int nameIndex = RecordingHandler.this.nextImageNamingIter();
		
		String imageName = String.format(tempImageNaming, nameIndex);
		File outputImage = null;
		BufferedImage image = null;
		outputImage = new File(imageName);
		
		image = SwingFXUtils.fromFXImage(capture, image);
		
		try {
			
			ImageIO.write(image, imageType, outputImage);
		} 
		catch (IOException e) {
			
			RecordingHandler.this.stop();
			e.printStackTrace();
		}
	}
	
	private class ImageWriter implements Runnable {

		Queue<WritableImage> toFileBuffer = null;
		
		public void setToFileBuffer(Queue<WritableImage> toFileBuffer) {
			this.toFileBuffer = toFileBuffer;
		}

		public ImageWriter(Queue<WritableImage> imageWriterBuffer) {
			
			toFileBuffer = imageWriterBuffer;
		}

		@Override
		public void run() {
	
			while(toFileBuffer != null) {
				
				try {
					
					Thread.sleep(100);
				} 
				catch (InterruptedException e) {
				
					e.printStackTrace();
				}
							
				RecordingHandler.this.write(toFileBuffer);
			}					
		}
	}
}
