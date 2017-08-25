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

package tum.cms.sim.momentum.utility.geometry.operation;

import tum.cms.sim.momentum.utility.geometry.Vector2D;

/**
 * All class and method descriptions have been adopted from the dyn4j library
 * documentation http://docs.dyn4j.org/v3.1.10/.
 * 
 * The Transformable2D represents an object that is transformable.
 * 
 * @author berndtornede, pk
 * 
 */
public interface ITransformable2D {

	/**
	 * Rotates the object about the origin.
	 * 
	 * @param theta
	 */
	void rotate(double theta);

	/**
	 * Rotates the object about the given coordinates.
	 * 
	 * @param theta
	 * @param x
	 * @param y
	 */
	void rotate(double theta, double x, double y);

	/**
	 * Rotates the object about the given point.
	 * 
	 * @param theta
	 * @param x
	 * @param y
	 */
	void rotate(double theta, Vector2D vector);

	/**
	 * Translates the object the given amounts in the respective directions.
	 * 
	 * @param x
	 * @param y
	 */
	void translate(double x, double y);

	/**
	 * Translates the object along the given vector.
	 * 
	 * @param vector
	 */
	void translate(Vector2D vector);
}
