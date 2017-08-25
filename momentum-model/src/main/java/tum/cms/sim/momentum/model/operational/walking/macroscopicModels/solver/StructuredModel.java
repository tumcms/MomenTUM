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

package tum.cms.sim.momentum.model.operational.walking.macroscopicModels.solver;

import tum.cms.sim.momentum.model.operational.walking.macroscopicModels.data.Density;
import tum.cms.sim.momentum.model.operational.walking.macroscopicModels.data.MacroscopicEdge;
import tum.cms.sim.momentum.model.operational.walking.macroscopicModels.velocityModel.IVelocityModel;

public class StructuredModel implements ISolver {
	
	private static double dxapprox;	
	static private double dt;
	static private double alpha;
	
	//just doubles for computation
	private double rhopp,rhopm; 
	//these ones change for every edge
	private int N;
	private Density Fplus;
	private Density[] density;

	
	protected StructuredModel() {
		
		setDt(0.02); 
		setDxapprox(0.2); 
		setAlpha(1); 
	}
	
	
	//always called	
	public void solve(MacroscopicEdge edgeToSolve, IVelocityModel velocityModel)	{
		
		N = edgeToSolve.getDivisions(); 
		
		int numberOfDestinations = edgeToSolve.getDensity()[1].getDestination().length;
		
		Fplus = new Density(0, velocityModel, numberOfDestinations);
		double[] CFLclass = new double[velocityModel.getNumberOfClasses()];
		
		for(int k=0; k<velocityModel.getNumberOfClasses(); k++) 
			
			CFLclass[k] = getDt() * velocityModel.getVelocities()[k] / getDxapprox();
		
		density = new Density[N]; 
		
		//here you will pass the penultimate open cell's density into the last open cell
		if (edgeToSolve.getFullCells() > 0)
			edgeToSolve.distributeLastDensities();
			
		//The density[0] and density[N-1] are always zero due to mass conservation issues.
		//This for loop is changed with the Full Cells variable
		for(int i=1;i<N-1-edgeToSolve.getFullCells();i++) {
			
			//in case you get despicable negative densities
			if (edgeToSolve.getDensity()[i-1].getAmount() < 0) edgeToSolve.getDensity()[i-1].Nullify();
			if (edgeToSolve.getDensity()[i].getAmount() < 0) edgeToSolve.getDensity()[i].Nullify();
			if (edgeToSolve.getDensity()[i+1].getAmount() < 0) edgeToSolve.getDensity()[i+1].Nullify();
			
			//upwindscheme Hartmann & Sivers
			//The unnecesary ones are commented. The people never move back			
			rhopp = (1-getAlpha())*edgeToSolve.getDensity()[i].getAmount() + getAlpha()*edgeToSolve.getDensity()[i+1].getAmount();
			rhopm = (1-getAlpha())*edgeToSolve.getDensity()[i-1].getAmount() + getAlpha()*edgeToSolve.getDensity()[i].getAmount();
		
			//Being careful in the first density (due to destinations)
			if(i == 1) {
				
				Fplus = edgeToSolve.getDensity()[i].getScaledDensity(velocityModel.getVhat(rhopp, edgeToSolve.getType())).clone();
			}
			else {
				
				Fplus = edgeToSolve.getDensity()[i].getScaledDensity(velocityModel.getVhat(rhopp, edgeToSolve.getType())).clone();
				Fplus.Subtract(edgeToSolve.getDensity()[i-1].getScaledDensity(velocityModel.getVhat(rhopm, edgeToSolve.getType())));
			}
			density[i] = edgeToSolve.getDensity()[i].clone();			
			Fplus = Fplus.getScaledClasses(CFLclass);			
			density[i].Subtract(Fplus); //here the destinations should be placed correctly
		}
		
		for (int i=1; i<N-1-edgeToSolve.getFullCells(); i++) { //set only the internal cells
		
			edgeToSolve.getDensity()[i].setAmount(density[i].getAmount());
			edgeToSolve.getDensity()[i].setClassDensity(density[i].getClassDensity());
			edgeToSolve.getDensity()[i].setDestination(density[i].getDestination());
		}
		
		//after computation (if maybe)
		if (edgeToSolve.getFullCells() > 0)
			edgeToSolve.distributeLastDensities();
	}
	
	public double getDxapprox() {
		return dxapprox;
	}

	public void setDxapprox(double dxapprox) {
		StructuredModel.dxapprox = dxapprox;
	}

	public double getDt() {
		return dt;
	}

	public void setDt(double dt) {
		StructuredModel.dt = dt;
	}

	public double getAlpha() {
		return alpha;
	}

	public void setAlpha(double alpha) {
		StructuredModel.alpha = alpha;
	}

	public boolean setParameters(double dxapprox, double dt, double alpha) {
		if (1.34 * dt / dxapprox > 1) {
			return false;
		}
		else {
			setDxapprox(dxapprox);
			setDt(dt);
			setAlpha(alpha);
			return true;
		}
		
	}
}
