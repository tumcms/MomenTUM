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

package tum.cms.sim.momentum.utility.probability.distrubution;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

public class DistributionFactory {

	private DistributionFactory() { }

	/**
	 * mean is k (shape) * theta (scale), variance is shape * theta^2
	 * sometimes shape is p and theta is 1/b
	 * Use ? to fit the parameter to a data sample if it is gamma distributed
	 * @param shape
	 * @param scale
	 * @return
	 */
	public static Gamma createGammaDistribution(double shape, double scale) {
		
		return new Gamma(shape, scale);
	}
	
	public static Discret createDiscretDistrubtion(List<Pair<Double,Double>> valueProbabilityList) {
		
		return new Discret(valueProbabilityList); 
	}
	
	/**
	 * lambda is variance and mean
	 * sometimes lambda is my (Greek letter)
	 * @param lambda
	 * @param scale
	 * @return
	 */
	public static Exponential createExponentialDistribution(double lambda) {
		
		return new Exponential(lambda);
	}
	
	/**
	 * lambda is variance and mean
	 * sometimes lambda is my (Greek letter)
	 * @param lambda
	 * @return
	 */
	public static Poisson createPoissonDistribution(double lambda) {
		
		return new Poisson(lambda);
	}
	

	public static Normal createNormalDistribution(double mean, double variance) {
		
		return new Normal(mean, variance);
	}
	
	/**
	 * scale is lambda
	 * form is k
	 * mean is 1/lamda * GammaFunc (1 + 1/k)
	 * var is 1/lamda^2 * [GammaFunc (1+2/k) - GammaFunc^2(1+1/k)]
	 * Use Matlab to find the scale and form if you have mean and var given.
	 * For more info look at wikipedia (german page is quite nice) 
	 * @param scale
	 * @param form
	 * @return
	 */
	public static Weibull createWeibullDistrubtion(double scale, double form) {
		
		return new Weibull(scale, form);
	}
}
