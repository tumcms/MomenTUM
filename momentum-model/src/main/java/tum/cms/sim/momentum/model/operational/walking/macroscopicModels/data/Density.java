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

import tum.cms.sim.momentum.model.operational.walking.macroscopicModels.velocityModel.IVelocityModel;
import tum.cms.sim.momentum.model.operational.walking.macroscopicModels.velocityModel.VFFModel;
import org.apache.commons.math3.util.FastMath;

public class Density {
	
	//How many people are in the dx:
	private double amount;
	//Number of class densities
	private int numberOfClassDensities;
	//All class densities for this dx:
	private double[] classDensity;
	//The destinations for this dx:
	private double[] destination;

	
	public Density(double amount, IVelocityModel velocityModel, int numberOfDestinations) {
		
		setAmount(amount);
		setNumberOfClassDensities(velocityModel.getNumberOfClasses());
		setClassDensity(new double[numberOfClassDensities]);
		for (int i=0;i<numberOfClassDensities;i++) {
			classDensity[i] = velocityModel.getProbabilities()[i]*amount;
		}
		
		//The destination array is empty in the beginning and can be filled initially by the function
		//setDestination below
		this.destination = new double[numberOfDestinations];
	}
	
	public Density(double amount, IVelocityModel velocityModel, double[] destinations) {
		setAmount(amount);
		
		//We set up the class densities
		setNumberOfClassDensities(velocityModel.getNumberOfClasses());
		setClassDensity(new double[numberOfClassDensities]);
		
		for (int i=0;i<numberOfClassDensities;i++) {
			classDensity[i] = velocityModel.getProbabilities()[i]*amount;
		}
		
		this.destination = destinations.clone();
	}
	
	public Density(double amount, int numberOfClassDensities, int numberOfDestinations) {
		setAmount(amount);
		setNumberOfClassDensities(numberOfClassDensities);
		setClassDensity(new double[numberOfClassDensities]);
		this.destination = new double[numberOfDestinations];
	}
	
	public void copy(Density densityToCopy) {
		setAmount(densityToCopy.getAmount());
		setClassDensity(densityToCopy.getClassDensity());
		setNumberOfClassDensities(densityToCopy.getNumberOfClassDensities());
		setDestination(densityToCopy.getDestination());
	}
	

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public int getNumberOfClassDensities() {
		return numberOfClassDensities;
	}

	public void setNumberOfClassDensities(int numberOfClassDensities) {
		this.numberOfClassDensities = numberOfClassDensities;
	}

	public double[] getClassDensity() {
		return classDensity;
	}

	public void setClassDensity(double[] classDensity) {
		this.classDensity = classDensity;
	}
	
	public void setClassDensity(int id, double newClassDensity) {
		this.classDensity[id] = newClassDensity;
	}

	public double[] getDestination() {
		return destination;
	}

	public void setDestination(double[] newDestination) {
		this.destination = newDestination.clone();
	}
	
	public void setDestination(int id, double newDestination) {
		this.destination[id] = newDestination;
	}
	
	/**
	 * Sets every parameter of the density to 0.
	 */
	public void Nullify() {
		this.amount = 0;
		for (int i=0; i<this.numberOfClassDensities; i++) {
			this.classDensity[i] = 0;
		}
		for (int i=0; i<this.destination.length; i++) {
			this.destination[i] = 0;
		}
	}
	
	/**
	 * Adds two densities.
	 * @param densityToAdd the density which will be added
	 */
	public void Add(Density densityToAdd) {
		double friction = 1;
		if(this.destination.length == densityToAdd.getDestination().length) {
			double sum = 0;
			
			for(int i=0; i<destination.length; i++) {
				destination[i] = (densityToAdd.getDestination()[i] * densityToAdd.getAmount() + this.destination[i] * this.amount ) / 
						(densityToAdd.getAmount() + this.amount);
				if(Double.isNaN(this.destination[i])) {
					this.destination[i] = 1;
				}
				sum += destination[i];
			}
			
			if(sum > 1) {
				friction = 1/sum;
				for (int i=0; i<destination.length; i++) {
					destination[i] = destination[i] * friction;
				}
			}
		}
		amount += densityToAdd.getAmount();
		for(int i=0; i<numberOfClassDensities; i++) {
			classDensity[i] += densityToAdd.getClassDensity()[i]; 
		}

	}
	
	/**
	 * Subtracts two densities.
	 * @param densityToSubtract the density which will be subtracted
	 */
	public void Subtract(Density densityToSubtract) {
		double friction = 1;
		if(this.destination.length == densityToSubtract.getDestination().length ){
			double sum = 0;
			
			for(int i=0; i<destination.length; i++) {
				destination[i] = FastMath.abs((this.destination[i] * this.amount - densityToSubtract.getDestination()[i] * densityToSubtract.getAmount()) /
						(this.amount - densityToSubtract.getAmount()));
				if(Double.isNaN(this.destination[i])) {
					this.destination[i] = 0;
				}
				sum += destination[i];
			}

			if (sum > 1) {
				friction = 1/sum;
				for (int i=0; i<destination.length; i++) {
					destination[i] = destination[i] * friction;
				}
			}
		}
		
		amount -= densityToSubtract.getAmount();
		for (int i=0; i<numberOfClassDensities; i++) {
			classDensity[i] -= densityToSubtract.getClassDensity()[i]; 
		}

	}
	
	public Density getScaledDensity(double scaling) {
		
		if(this.amount == 0) {
			return this;
		}
		
		Density theNewDensity = new Density(0,numberOfClassDensities,this.destination.length);
		theNewDensity.setAmount(scaling*amount);
		
		double[] theNewClassDensity = new double[numberOfClassDensities];
		for (int i=0; i<numberOfClassDensities; i++) 
		{
			theNewClassDensity[i] = scaling*classDensity[i] ; 
		}
		theNewDensity.setClassDensity(theNewClassDensity);
		
		if(scaling == 0) {
			for(int i=0; i<this.destination.length; i++) {
				theNewDensity.setDestination(i, 0);
			}
		}
		else {
			theNewDensity.setDestination(this.destination);
		}
		
		return theNewDensity;
	}
	
	public Density getScaledClasses(double[] scales)
	{
		if(this.amount == 0) {
			return this;
		}
		
		if(this.getNumberOfClassDensities() != scales.length) {
			System.out.println("Arrays mismatch!");
			return this;
		}
		
		Density theNewDensity = new Density(0,numberOfClassDensities,this.destination.length);
		double newAmount = 0;
		
		double[] theNewClassDensity = new double[numberOfClassDensities];
		for (int i=0; i<numberOfClassDensities; i++) {
			theNewClassDensity[i] = scales[i]*classDensity[i] ;
			newAmount += scales[i]*classDensity[i];
		}
		
		theNewDensity.setAmount(newAmount);
		theNewDensity.setClassDensity(theNewClassDensity);
		theNewDensity.setDestination(this.destination);
		
		return theNewDensity;
	}
	
	public void initializeClassDensities(VFFModel theVelocityModel) {
		for(int k=0; k<numberOfClassDensities - 1; k++) {
			this.classDensity[k] = amount * theVelocityModel.getProbabilities()[k];
		}
	}

	public Density clone() {
		Density newDensity = new Density(0, 0, this.destination.length);
		
		newDensity.setAmount(this.getAmount());
		newDensity.setNumberOfClassDensities(this.getNumberOfClassDensities());
		newDensity.setDestination(this.getDestination());
		double[] newClassDensity = new double[this.getNumberOfClassDensities()];
		for (int i=0; i<this.getNumberOfClassDensities(); i++) {
			newClassDensity[i] = this.getClassDensity()[i];
		}
		newDensity.setClassDensity(newClassDensity);
		newDensity.setDestination(this.destination);
		
		return newDensity;
	}

}
