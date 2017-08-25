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

package tum.cms.sim.momentum.model.operational.walking.macroscopicModels.data;


public class MacroscopicEdge {
	
	public enum MacroscopicEdgeType {
		STREET, WALKWAY
	}
	
	private Integer id = null;
	private MacroscopicNode firstNode = null;
	private MacroscopicNode secondNode = null;
	private Double width = null;
	private Double maximalDensity = null;
	private Double currentDensity = null;
	private Double dx;
	private Integer divisions;
	private MacroscopicEdgeType type;
	private Density[] density;
	private Integer fullCells;
	private Boolean isComputed;
	private Integer order;

	
	public MacroscopicEdge(Integer id, MacroscopicNode firstNode, MacroscopicNode secondNode, Double width, Double maximalDensity, Double currentDensity) {

		this.setId(id);
		this.setFirstNode(firstNode);
		this.setSecondNode(secondNode);
		this.setWidth(width);
		this.setMaximalDensity(maximalDensity);
		this.setCurrentDensity(currentDensity);
		firstNode.addOutgoing(this);
		secondNode.addIncoming(this);
		setFullCells(0);
		setIsComputed(false);
	}
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}

	public Double getCurrentDensity() {
		return currentDensity;
	}

	public void setCurrentDensity(Double currentDensity) {
		this.currentDensity = currentDensity;
	}

	public MacroscopicNode getSecondNode() {
		return secondNode;
	}

	public void setSecondNode(MacroscopicNode secondNode) {
		this.secondNode = secondNode;
	}

	public MacroscopicNode getFirstNode() {
		return firstNode;
	}

	public void setFirstNode(MacroscopicNode firstNode) {
		this.firstNode = firstNode;
	}

	public Double getWidth() {
		return width;
	}

	public void setWidth(Double width) {
		this.width = width;
	}

	public Double getMaximalDensity() {
		return maximalDensity;
	}

	public void setMaximalDensity(Double maximalDensity) {
		this.maximalDensity = maximalDensity;
	}
	
	public Double getLength() {
		return this.getFirstNode().getPosition().distance(this.getSecondNode().getPosition());
	}

	public Double getDx() {
		return dx;
	}

	public void setDx(Double dx) {
		this.dx = dx;
	}

	public Integer getDivisions() {
		return divisions;
	}

	public void setDivisions(Integer divisions) {
		
		this.divisions = divisions;
		this.setDx(this.getLength() / (this.divisions - 1));
	}

	public MacroscopicEdgeType getType() {
		return type;
	}

	public void setType(MacroscopicEdgeType type) {
		this.type = type;
	}

	public Density[] getDensity() {
		return density;
	}

	public void setDensity(Density[] density) {
		this.density = density;
	}

	public int getFullCells() {
		return fullCells;
	}

	public void setFullCells(int fullCells) {
		this.fullCells = fullCells;
	}

	public void distributeLastDensities() {	
		//Delete the penultimate and send it to the ultimate. Check if ultimate is full and increase full cells
		//first, define a friction (supposed to be 1 in default case)
		double friction = 1;
		maximalDensity = 5.4;
		//full cell case
		if(density[divisions-1-fullCells].getAmount() + density[divisions-2-fullCells].getAmount() > maximalDensity)	{	
			friction = (maximalDensity - density[divisions-1-fullCells].getAmount())/density[divisions-2-fullCells].getAmount();
			
			//fix friction for stability
			if(friction>1) friction = 1; 
			if(friction<0) friction = 0;
			
			Density densityToMove = density[divisions-2-fullCells].clone();
			densityToMove = densityToMove.getScaledDensity(friction);
			
			density[divisions-1-fullCells].Add(densityToMove); //move forward
			fullCells++; //increase by one the number of full cells
			density[divisions-2-fullCells].Subtract(densityToMove); //stay
			
			//its a recursive function, the penultimate cell cannot have people
			this.distributeLastDensities();
		}
		//normal cell
		else {
			density[divisions-1-fullCells].Add(density[divisions-2-fullCells]);
			density[divisions-2-fullCells].Nullify();
		}
	}

	public Boolean getIsComputed() {
		return isComputed;
	}

	public void setIsComputed(Boolean isComputed) {
		this.isComputed = isComputed;
	}
	
	public void deleteEndDensity() {
		this.density[this.divisions-2].Nullify();
	}
	
	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}
	
	public double getPeople() {
		
		double sum = 0;
		
		for (int i=0; i<divisions; i++) {
			
			sum += this.density[i].getAmount();
		}
		return getDx() * width * sum;
	}

}
