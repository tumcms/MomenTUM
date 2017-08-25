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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.shape.Ellipse;
import tum.cms.sim.momentum.configuration.scenario.GraphScenarioConfiguration;
import tum.cms.sim.momentum.configuration.scenario.VertexConfiguration;
import tum.cms.sim.momentum.visualization.controller.CoreController;
import tum.cms.sim.momentum.visualization.controller.CustomizationController;
import tum.cms.sim.momentum.visualization.handler.SelectionHandler.SelectionStates;
import tum.cms.sim.momentum.visualization.model.CustomizationModel;
import tum.cms.sim.momentum.visualization.model.VisibilitiyModel;

public class VertexModel extends ShapeModel {
	
	private CustomizationModel customizationModel = null;
	private VertexConfiguration vertexConfiguration = null;
	private ArrayList<EdgeModel> adjacentEdges = new ArrayList<EdgeModel>();
	private Ellipse vertexShape = null;
	
	public VertexModel(GraphScenarioConfiguration graphConfiguration, 
			CoreController coreController,
			CustomizationController customizationController,
			VisibilitiyModel visibilitiyModel,
			VertexConfiguration vertex) {
	
		this.customizationModel = customizationController.getCustomizationModel();
		this.vertexConfiguration = vertex;
		
		vertexShape = new Ellipse(vertex.getPoint().getX() * coreController.getCoreModel().getResolution() , 
				vertex.getPoint().getY() * coreController.getCoreModel().getResolution(),
				coreController.getCoreModel().getResolution()/2.0, // / 2.0
				coreController.getCoreModel().getResolution()/2.0);

		vertexShape.fillProperty().bind(customizationModel.graphColorProperty());
		vertexShape.visibleProperty().bind(visibilitiyModel.graphVisibilityProperty());
		vertexShape.setTranslateZ(0.001 * coreController.getCoreModel().getResolution());
		vertexShape.radiusXProperty().bind(customizationModel.vertexSizeProperty());
		vertexShape.radiusYProperty().bind(customizationModel.vertexSizeProperty());
	}
	
	public Ellipse getVertexShape() {
		
		return this.vertexShape;
	}
	
	public List<EdgeModel> getAdjacentEdges() {
		
		return this.adjacentEdges;
	}
	
	public void addAdjacentEdge(EdgeModel edge) {
		
		this.adjacentEdges.add(edge);
	}

	@Override
	public void setVisibility(boolean isVisible) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void changeSelectionMode(SelectionStates selectionState) {
		
		this.vertexShape.fillProperty().unbind();
		this.adjacentEdges.forEach(edge -> edge.getEdgeShape().strokeProperty().unbind());
		
		switch(selectionState) {
		
		case NotSelected:
			this.vertexShape.fillProperty().bind(customizationModel.graphColorProperty());
			this.adjacentEdges.forEach(edge -> edge.getEdgeShape().strokeProperty().bind(customizationModel.graphColorProperty()));
			break;
			
		case Selected:
			this.vertexShape.fillProperty().bind(customizationModel.selectedColorProperty());
			this.vertexShape.toFront();
			this.adjacentEdges.forEach(edge -> {
				edge.getEdgeShape().strokeProperty().bind(customizationModel.selectedColorProperty());
//				edge.getEdgeShape().toFront();
			});
			break;
		}
	}

	@Override
	public String getIdentification() {
		
		return Integer.toString(this.vertexConfiguration.getId());
	}

	@Override
	public List<Node> getClickableShapes() {

		ArrayList<Node> clickableShapes = new ArrayList<Node>();
		clickableShapes.add(this.vertexShape);
		
		return clickableShapes;
	}

	@Override
	public LinkedHashMap<String, String> getDataProperties() {
		
		LinkedHashMap<String, String> details = new LinkedHashMap<>();
		
		details.put(ShapeModel.nameDetails, this.vertexConfiguration.getName());
		
		for(int i = 0; i < this.adjacentEdges.size(); i++) {
			details.put(ShapeModel.adjacent + " " + i, this.adjacentEdges.get(i).getIdentification());
		}		
		
		return details;
	}
}
