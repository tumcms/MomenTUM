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

import java.util.ArrayList;

import tum.cms.sim.momentum.utility.geometry.Vector2D;

public class MacroscopicNode {
	
	public enum NodeType {
		//As we have "NodeType" and "EdgeType", we can not rename both to just "Type"
		INTERSECTION, ENTRY, EXIT, PARKING
	}	

	private Vector2D position = null;
	private Integer id = null;
	private Boolean isOrigin = null;
	private Boolean isDestination = null;
	
	private ArrayList<MacroscopicEdge> in = new ArrayList<MacroscopicEdge>();
	private ArrayList<MacroscopicEdge> out = new ArrayList<MacroscopicEdge>();
	
	private MacroscopicGenerator generator = null;
	private MacroscopicAbsorber absorber = null;
	private NodeType type;
	private Density density;
	private Boolean isComputed;
	private Boolean isFull;
	private Double area;
	
	
	public MacroscopicNode(Integer id, Vector2D position, NodeType type) {

		this.setPosition(position);
		this.setId(id);
		
		this.removeOrigin();
		this.removeDestination();
		setType(type);
		
		if (type == NodeType.ENTRY)
			setIsOrigin(true);
		
		if (type == NodeType.EXIT)
			setIsDestination(true);
		
		setFull(false);
		setIsComputed(false);
	}

	public Integer getId() {
		return id;
	} 
	
	public void setId(Integer id) {
		this.id = id;
	}

	public Vector2D getPosition() {
		return position;
	}

	public void setPosition(Vector2D position) {
		this.position = position;
	}

	public Boolean getIsOrigin() {
		return isOrigin;
	}
	
	
	private void setIsDestination(Boolean isDestination) {
		this.isDestination = isDestination;
	}
	
	private void setIsOrigin(Boolean isOrigin) {
		this.isOrigin = isOrigin;
	}


	public Boolean getIsDestination() {
		return isDestination;
	}
	
	public void createDestination(MacroscopicAbsorber absorber) {
		
		this.setIsDestination(true);
		this.setAbsorber(absorber);
	}
	
	public void removeDestination() {
				
		this.setIsDestination(false);
		this.setAbsorber(null);
	}
	
	public void createOrigin(MacroscopicGenerator generator) {
		
		this.setIsOrigin(true);
		this.setGenerator(generator);
	}
	
	public void removeOrigin() {
				
		this.setIsOrigin(false);
		this.setGenerator(null);
	}

	public MacroscopicGenerator getGenerator() {
		return generator;
	}

	public MacroscopicAbsorber getAbsorber() {
		return absorber;
	}

	
	private void setGenerator(MacroscopicGenerator generator) {
		this.generator = generator;
	}

	private void setAbsorber(MacroscopicAbsorber absorber) {
		this.absorber = absorber;
	}

	public NodeType getType() {
		return type;
	}

	public void setType(NodeType type) {
		this.type = type;
	}

	public ArrayList<MacroscopicEdge> getIn() {
		return in;
	}

	public void setIn(ArrayList<MacroscopicEdge> in) {
		this.in = in;
	}

	public ArrayList<MacroscopicEdge> getOut() {
		return out;
	}

	public void setOut(ArrayList<MacroscopicEdge> out) {
		this.out = out;
	}

	public Density getDensity() {
		return density;
	}

	public void addIncoming(MacroscopicEdge incoming) {
		in.add(incoming);
		computeArea();
	}
	
	public void addOutgoing(MacroscopicEdge outgoing) {
		out.add(outgoing);
		computeArea();
	}

	public void setDensity(Density density) {
		this.density = density;
	}
	
	public void addDensity(Density density) {
		this.density.Add(density);
	}
	
	public void subtractDensity(Density density) {
		this.density.Subtract(density);
		
		if (this.density.getAmount() < 0) {
			this.deleteDensity();
		}
	}
	
	public void deleteDensity() {
		this.density.Nullify();
	}	

	public Boolean getIsComputed() {
		return isComputed;
	}

	public void setIsComputed(Boolean isComputed) {
		this.isComputed = isComputed;
	}
	
	public boolean isFull() {
		return isFull;
	}

	public void setFull(boolean isFull) {
		if(isFull) {
			for(int i=0;i<in.size();i++) {
				if(in.get(i).getFullCells() < 1) {
					in.get(i).setFullCells(1);
				}
			}
		}
		else {
			for(int i=0;i<in.size();i++) {	
				in.get(i).setFullCells(0);
			}
		}
		
		this.isFull = isFull;
	}
	
	public void computeArea() {
		if (this.out.isEmpty()) {
			this.setArea(Double.MAX_VALUE);
		}
		else {
			double totalWidth = 0;
			for (MacroscopicEdge e:this.out) {
				totalWidth += e.getWidth();
			}
			this.setArea(totalWidth);
		}
		
	}

	public Double getArea() {
		return area;
	}

	public void setArea(Double area) {
		this.area = area;
	}

}
