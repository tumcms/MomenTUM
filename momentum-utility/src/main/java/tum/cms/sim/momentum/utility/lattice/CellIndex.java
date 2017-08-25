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

package tum.cms.sim.momentum.utility.lattice;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.util.FastMath;

public class CellIndex {
	
	private Pair<Integer,Integer> index = null;
	
	CellIndex(int[] rawColumnIndex){	
		this.index = new ImmutablePair<Integer,Integer>(rawColumnIndex[0], rawColumnIndex[1]);
	}
	
	CellIndex(int row, int column){
		this.index = new ImmutablePair<Integer,Integer>(row, column);
	}
	
	public boolean equals(CellIndex toCompare) {	
		
		return this.getColumn() == toCompare.getColumn() 
			   && this.getRow() == toCompare.getRow();	
	}

	public int getRow() {
		return index.getLeft();
	}
	
	public int getColumn() {
		return index.getRight();
	}
	
	public void setIndex(int row, int column) {
		this.index = new ImmutablePair<Integer,Integer>(row, column);
	}
	
	public void setIndex(Pair<Integer,Integer> index) {
		this.index = index;
	}
	
	public void setIndex(int[] index) {		
		this.setIndex(index[0], index[1]);
	}
	
	public String getString() {	
		return "(" + this.getRow() + "," + this.getColumn() + ")";
	}
	
	@Override
	public boolean equals(Object other) {
		
		CellIndex cellOther = (CellIndex)other;
	
		if(this.getRow() == cellOther.getRow() && this.getColumn() == cellOther.getColumn()) {
			
			return true;
		}
		
		return false;
	}
	
	public boolean compareTo(CellIndex other) {
		
		if(this.getRow() == other.getRow() && this.getColumn() == other.getColumn()) {
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return bijectionHash();
	}
	
	/**
	 * This method computes a bijectional mapping from N x N -> N according to 'Cantor Pairing Function'.
	 * As input parameters the row value and column value are mapped to a single Integer value.
	 * @return an unique Integer representation for this CellIndex
	 */
	private int bijectionHash() {
		double x = this.getColumn();
		double y = this.getRow();
		
		return (int) y + (int) FastMath.round(0.5 * ((x + y) * (x + y + 1)));
	}
}
