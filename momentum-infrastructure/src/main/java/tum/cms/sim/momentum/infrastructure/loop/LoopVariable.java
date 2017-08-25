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

package tum.cms.sim.momentum.infrastructure.loop;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class LoopVariable {
	
	private BigDecimal startValue = null;
	private BigDecimal currentValue = null;
	private BigDecimal changeValue = null;
	private BigDecimal maximumValue = null;

	private String substitute = null;
	private int maximalSteps = 0;
	private int currentStep = 0;
	
	public BigDecimal getCurrentValue() {
		return currentValue;
	}

	public String getSubstitute() {
		return substitute;
	}

	public int getMaximalSteps() {
		return maximalSteps;
	}

	public int getCurrentStep() {
		return currentStep;
	}

	public LoopVariable(Double initialValue, double startValue, double changeValue, int steps, String substitute) {
		
		if(initialValue == null) {
		
			initialValue = startValue;
		}

		this.currentValue = new BigDecimal(initialValue);
		this.startValue = new BigDecimal(startValue);
		this.changeValue =  new BigDecimal(changeValue);
		this.substitute = substitute;
		this.maximalSteps = steps;
		this.maximumValue = this.startValue.add(this.changeValue.multiply(new BigDecimal(maximalSteps)));
		
		if(this.startValue.compareTo(this.currentValue) != 0) {
			
			this.currentStep = this.maximumValue.subtract(this.currentValue)
					.divide(this.changeValue)
					.subtract(new BigDecimal(steps))
					.multiply(new BigDecimal(-1.0))
					.intValue();
		}
	}

	/**
	 * Increments the loop variable by adding th change
	 * value to the current value
	 * @param digits 
	 * @return true, if overflow and set to startState
	 */
	public boolean incrementStep(int digits) {
		
		boolean overflow = false;
		this.currentValue = this.currentValue.add(this.changeValue)
				.round(new MathContext(digits, RoundingMode.HALF_EVEN));
		this.currentStep++;
		
		if(this.currentValue.compareTo(this.maximumValue) > 0) {
		
			overflow = true;
			this.currentValue = this.startValue;
			this.currentStep = 0;
		}
		
		return overflow;
	}
}
