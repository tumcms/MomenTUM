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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.scene.control.TableView;
import tum.cms.sim.momentum.visualization.model.DetailsModel;
import tum.cms.sim.momentum.visualization.model.DetailsModelEntry;
import tum.cms.sim.momentum.visualization.model.geometry.ShapeModel;

public class SelectionHandler {

	public enum SelectionStates {
		Selected,
		NotSelected
	}
	
	private HashSet<ShapeModel> selectedShapes = new HashSet<ShapeModel>();
	private TableView<DetailsModelEntry> detailsView = null;
	private HashMap<String, ShapeModel> clickableObjects = new HashMap<String, ShapeModel>();
	
	public SelectionHandler(TableView<DetailsModelEntry> detailsView) {
		
		this.detailsView = detailsView;
	}
	
	public void registerClickableObject(ShapeModel shapeModel) {
		
		this.clickableObjects.put(shapeModel.getIdentification(), shapeModel);
	}
	
	public void removeClickableObject(String shapeModelIdentification) {
		
		this.clickableObjects.remove(shapeModelIdentification);
	}
	
	public void clearClickableObjects() {
		
		this.clickableObjects.clear();
	}

	public void setSelection(ShapeModel shapeModel, Boolean isControlDown, DetailsModel shapesDetailsModel) {
		
		if(isControlDown) {
			
			if(shapeModel != null) {
				
				if(selectedShapes.contains(shapeModel)) { 
					
					shapeModel.changeSelectionMode(SelectionStates.NotSelected);
					selectedShapes.remove(shapeModel);
				}
				else { // newly selected 
					
					shapeModel.changeSelectionMode(SelectionStates.Selected);
					selectedShapes.add(shapeModel);		
				}
			}
		}
		else {
			
			if(shapeModel != null) {
					
				if(selectedShapes.contains(shapeModel)) { //delete selected
					
					selectedShapes.forEach(shape -> shape.changeSelectionMode(SelectionStates.NotSelected));
					selectedShapes.clear();
				}
				else if(!selectedShapes.isEmpty()) { //select another
					
					selectedShapes.forEach(shape -> shape.changeSelectionMode(SelectionStates.NotSelected));
					selectedShapes.clear();
					shapeModel.changeSelectionMode(SelectionStates.Selected);
					selectedShapes.add(shapeModel);
					
				}
				else { // newly selected 
					
					selectedShapes.clear();
					shapeModel.changeSelectionMode(SelectionStates.Selected);
					selectedShapes.add(shapeModel);

				}
			}
		}
		
		if(selectedShapes.size() != 1) {
			
			this.detailsView.setItems(FXCollections.observableArrayList());
		}
		else {
			
			if(shapesDetailsModel != null) {
				this.detailsView.setItems(shapesDetailsModel.createViewContent());
			}
		}
	}
	
	public Set<ShapeModel> getSelected() {
		
		return selectedShapes;
	}
	
	public void clearSelection() {
		
		if (!selectedShapes.isEmpty()) {
			selectedShapes.forEach(shape -> shape.changeSelectionMode(SelectionStates.NotSelected));
			this.selectedShapes.clear();
		}
	}
 
	public void clearDetailsView() {
		
		selectedShapes.forEach(shape -> shape.changeSelectionMode(SelectionStates.NotSelected));
		selectedShapes.clear();
		this.detailsView.setItems(FXCollections.observableArrayList());
	}
}
