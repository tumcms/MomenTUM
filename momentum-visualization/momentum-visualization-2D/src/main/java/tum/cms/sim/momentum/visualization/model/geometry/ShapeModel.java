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

package tum.cms.sim.momentum.visualization.model.geometry;

import java.util.LinkedHashMap;
import java.util.List;

import tum.cms.sim.momentum.visualization.handler.SelectionHandler;
import tum.cms.sim.momentum.visualization.handler.SelectionHandler.SelectionStates;
import tum.cms.sim.momentum.visualization.model.DetailsModel;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public abstract class ShapeModel {

	protected static final String nameDetails = "Name";
	protected static final String positionXDetails = "positionX";
	protected static final String positionYDetails = "positionY";
	protected static final String headingXDetails = "headingX";
	protected static final String headingYDetails = "headingY";
	protected static final String widthRadius = "widthRadius";
	protected static final String depthRadius = "depthRadius";
	protected static final String targetId = "targetId";
	protected static final String groupId = "groupId";
	protected static final String seedId = "seedId";	
	protected static final String leader = "leader";
	protected static final String behavior = "behavior";
	protected static final String motoric = "motoric";
	protected static final String leftVertex = "Left Vertex";
	protected static final String rightVertex = "Right Vertex";
	protected static final String adjacent = "Adjacent";
	protected static final String message = "msg";
	
	protected SelectionHandler selectionHandler = null;
	
	public void registerSelectable(SelectionHandler selectionHandler) {
		
		if(selectionHandler != null) {
			
			this.selectionHandler = selectionHandler;
			this.selectionHandler.registerClickableObject(this);

			if(this.getClickableShapes() != null) {
				this.getClickableShapes().stream()
						.filter(shape -> shape != null)
						.forEach(shape -> shape.setOnMouseClicked(new EventHandler<MouseEvent>() {

					@Override
					public void handle(MouseEvent mouseEvent) {

						if (mouseEvent.getButton() == MouseButton.PRIMARY) {

							DetailsModel details = new DetailsModel(ShapeModel.this.getDataProperties());
							ShapeModel.this.selectionHandler.setSelection(ShapeModel.this, mouseEvent.isControlDown(), details);
						}
					}
				}));
			}
		}
	}
	
	public void unregisterSelectable() {
		
		if(this.selectionHandler != null) {
			
			this.selectionHandler.removeClickableObject(this.getIdentification());
			this.selectionHandler = null;
		}
	}

	public Group getShape() {
		
		return null;
	}
	
	public abstract void setVisibility(boolean isVisible);

	//public abstract boolean isVisible();
	
	public abstract void changeSelectionMode(SelectionStates selectionState);
	
	public abstract String getIdentification();
	
	public abstract List<Node> getClickableShapes();

	public abstract LinkedHashMap<String, String> getDataProperties();

}
