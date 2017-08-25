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

package tum.cms.sim.momentum.visualization.view.dialogControl;

import java.io.PrintWriter;
import java.io.StringWriter;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;

public class InformationDialogCreator {
	
	private static String infoboxTitle = "MomenTumV2";
	private static String snapshotConfirmationHeader = "Snapshot saved";
	private static String onRecordErrorHeader = "No data found";
	private static String onRecordErrorContent = "Load Layout and Output First!";
	private static String onLoadCsvErrorHeader = "No data found";
	private static String onLoadCsvErrorContent = "Load Layout First!";
	
	public static void createSnapshotConfirmationDialog(String path) {
		
		Alert dialog = new Alert(AlertType.INFORMATION);
		
		dialog.initModality(Modality.WINDOW_MODAL);
    	dialog.setTitle(infoboxTitle);
    	dialog.setHeaderText(snapshotConfirmationHeader);
    	dialog.showAndWait();
		
	}
	
	public static void createOnRecordErrorDialog() {
		
		Alert dialog = new Alert(AlertType.ERROR);
		
		dialog.initModality(Modality.WINDOW_MODAL);
    	dialog.setTitle(infoboxTitle);
    	dialog.setHeaderText(onRecordErrorHeader);
    	dialog.setContentText(onRecordErrorContent);
    	dialog.showAndWait();
		
	}
	
	public static void createOnLoadCsvErrorDialog() {
		
		Alert dialog = new Alert(AlertType.ERROR);
		
		dialog.initModality(Modality.WINDOW_MODAL);
    	dialog.setTitle(infoboxTitle);
    	dialog.setHeaderText(onLoadCsvErrorHeader);
    	dialog.setContentText(onLoadCsvErrorContent);
    	dialog.showAndWait();
		
	}
	
	public static void createErrorDialog(String header, String content) {
		
		Alert dialog = new Alert(AlertType.ERROR);
		
		dialog.initModality(Modality.WINDOW_MODAL);
    	dialog.setTitle(infoboxTitle);
    	dialog.setHeaderText(header);
    	dialog.setContentText(content);
    	dialog.showAndWait();
	}
	
	/**
	 * Error dialog that prints the stacktrace
	 * @param header
	 * @param content
	 * @param exception
	 */
	public static void createErrorDialog(String header, String content, Exception exception) {
		
		Alert dialog = new Alert(AlertType.ERROR);
		
		dialog.initModality(Modality.WINDOW_MODAL);
		dialog.setTitle(infoboxTitle);
		dialog.setHeaderText(header);
		dialog.setContentText(content);
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		exception.printStackTrace(printWriter);
		String exceptionText = stringWriter.toString();
		Label label = new Label("Exception Stacktrace:");
		TextArea textArea = new TextArea(exceptionText);
		textArea.setEditable(false);
		textArea.setWrapText(true);
		textArea.setMaxHeight(Double.MAX_VALUE);
		textArea.setMaxWidth(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);
		GridPane gridPane= new GridPane();
		gridPane.setMaxWidth(Double.MAX_VALUE);
		gridPane.add(label, 0, 0);
		gridPane.add(textArea, 0, 1);
		dialog.getDialogPane().setExpandableContent(gridPane);
		dialog.showAndWait();
	}
	
	public static void createWarningDialog(String header, String content) {
		
		Alert dialog = new Alert(AlertType.WARNING);
		
		dialog.initModality(Modality.WINDOW_MODAL);
    	dialog.setTitle(infoboxTitle);
    	dialog.setHeaderText(header);
    	dialog.setContentText(content);
    	dialog.showAndWait();
	}

}
