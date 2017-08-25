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

package tum.cms.sim.momentum.utility.geometry;

import org.dyn4j.geometry.Vector2;
import org.apache.commons.math3.util.FastMath;
/**
 * The Vector2D class represents a vector in 2-dimensional euclidean space.
 * 
 * All method descriptions have been adopted from the dyn4j library
 * documentation http://docs.dyn4j.org/v3.1.10/.
 * 
 * @author berndtornede, pk
 * 
 */
public class Vector2D {

	// ATTRIBUTES
	// -------------------------------------------------------------------------------

	private Vector2 vector = null;

	// CONSTRUCTORS
	// -------------------------------------------------------------------------------

	Vector2 getVector() {
		return vector;
	}

	/**
	 * Default constructor.
	 */
	protected Vector2D() {
		this.vector = new Vector2();
	}

	/**
	 * Creates a unit length vector in the given direction.
	 * 
	 * @param direction
	 */
	Vector2D(double direction) {
		this.vector = new Vector2(direction);
	}

	/**
	 * Optional constructor.
	 * 
	 * @param x
	 * @param y
	 */
	Vector2D(double x, double y) {
		this.vector = new Vector2(x, y);
	}

	/**
	 * Creates a Vector2D from the first point to the second point.
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 */
	Vector2D(double x1, double y1, double x2, double y2) {
		this.vector = new Vector2(x1, y1, x2, y2);
	}

	/**
	 * Copy constructor.
	 * 
	 * @param vector
	 */
	Vector2D(Vector2D vector) {
		this.vector = vector.vector;
	}
	
	/**
	 * Dyn4j Vector2 to Vector2D copy constructor
	 * 
	 * @param vector
	 */
	Vector2D(Vector2 vector) {
		this.vector = new Vector2(vector);
	}
	/**
	 * Creates a Vector2D from the first point to the second point.
	 * 
	 * @param point1
	 * @param point2
	 */
	Vector2D(Vector2D point1, Vector2D point2) {
		this.vector = new Vector2(point1.vector, point2.vector);
	}

	// METHODS
	// -------------------------------------------------------------------------------

	/**
	 * Returns a copy of this Vector2D.
	 * 
	 * @return Vector2D
	 */
	public Vector2D copy() {

		Vector2 copy = this.vector.copy();

		return new Vector2D(copy.x, copy.y);
	}

	/**
	 * Returns a new Vector2D given the magnitude and direction.
	 * 
	 * @param magnitude
	 * @param direction
	 * @return Vector2D
	 */
	public Vector2D create(double magnitude, double direction) {
		
		Vector2 creation = Vector2.create(magnitude, direction);

		return new Vector2D(creation.x, creation.y);
	}
	
	/**
	 * Returns the cross product of this Vector2D and the given Vector2D.
	 * 
	 * @param vector
	 * @return Vector2D
	 */
	


	/**
	 * Returns the cross product of this Vector2D and the z value of the right
	 * Vector2D.
	 * 
	 * @param z
	 * @return Vector2D
	 */
	public Vector2D cross(double z) {

		Vector2 cross = this.vector.cross(z);

		return new Vector2D(cross.x, cross.y);
	}

	/**
	 * Returns the cross product of the this Vector2D and the given Vector2D.
	 * 
	 * @param x
	 * @param y
	 * @return double
	 */
	public double cross(double x, double y) {
		return this.vector.cross(x, y);
	}

	/**
	 * Returns the cross product of the this Vector2D and the given Vector2D.
	 * 
	 * @param vector
	 * @return double
	 */
	public double cross(Vector2D vector) {

		Vector2 cross = new Vector2(vector.vector);

		return this.vector.cross(cross);
	}

	/**
	 * Subtracts the given Vector2D from this Vector2D returning a new Vector2D
	 * containing the result.
	 * 
	 * @param x
	 * @param y
	 * @return Vector2D
	 */
	public Vector2D difference(double x, double y) {

		Vector2 difference = this.vector.difference(x, y);

		return new Vector2D(difference.x, difference.y);
	}

	/**
	 * Subtracts the given Vector2D from this Vector2D returning a new Vector2D
	 * containing the result.
	 * 
	 * @param vector
	 * @return Vector2D
	 */
	public Vector2D difference(Vector2D vector) {

		Vector2 difference = this.vector.difference(vector.vector);

		return new Vector2D(difference.x, difference.y);
	}

	/**
	 * Returns the distance from this point to the given point.
	 * 
	 * @param x
	 * @param y
	 * @return double
	 */
	public double distance(double x, double y) {
		return this.vector.distance(x, y);
	}

	/**
	 * Returns the distance from this point to the given point.
	 * 
	 * @param x
	 * @param y
	 * @return double
	 */
	public double distance(Vector2D point) {
		return this.vector.distance(point.vector);
	}

	/**
	 * Returns the distance from this point to the given point squared.
	 * 
	 * @param x
	 * @param y
	 * @return double
	 */
	public double distanceSquared(double x, double y) {
		return this.vector.distanceSquared(x, y);
	}

	/**
	 * Returns the distance from this point to the given point squared.
	 * 
	 * @param x
	 * @param y
	 * @return double
	 */
	public double distanceSquared(Vector2D point) {
		return this.vector.distanceSquared(point.vector);
	}

	/**
	 * Returns the dot product of the given Vector2D and this Vector2D.
	 * 
	 * @param x
	 * @param y
	 * @return double
	 */
	public double dot(double x, double y) {
		return this.vector.dot(x, y);
	}

	/**
	 * Returns the dot product of the given Vector2D and this Vector2D.
	 * 
	 * @param x
	 * @param y
	 * @return double
	 */
	public double dot(Vector2D vector) {
		return this.vector.dot(vector.vector);
	}

	/**
	 * Returns true if the x and y components of this Vector2D are the same as
	 * the given x and y components.
	 * 
	 * @param x
	 * @param y
	 * @return boolean
	 */

	public boolean equals(double x, double y) {
		return this.vector.equals(x, y);
	}

	/**
	 * Returns true if the x and y components of this Vector2D are the same as
	 * the given Vector2D.
	 * 
	 * @param vector
	 * @return boolean
	 */
	
	public boolean equals(Vector2D vector) {
		return this.vector.equals(vector.vector);
	}
	
	@Override
	public boolean equals(Object other){
		return this.equals((Vector2D)other);
	}
	
	@Override
	public int hashCode() {
		
		return this.vector.hashCode();
	}
	
	/**
	 * rounds for all decimal places e.g.
	 * 
	 * precision is 100 -> 2 places
	 * this x is 11.1 -> (100 * 11.112) / 100  = (int)1111 / 100 = 11.11 
	 *
	 * This approach seems to have some flaws, be carefull nad 
	 */
	public Vector2D roundTo(Double precision) {
		
		if(precision == null) {
			
			return this.copy();
		}
		
		double x = (FastMath.round(this.getXComponent() * precision) / precision);
		double y = (FastMath.round(this.getYComponent() * precision) / precision);
		return GeometryFactory.createVector(x, y);
	}
	/**
	 * Returns the smallest angle between the given Vector2Ds.
	 * Negative or positive
	 * @param vector
	 * @return double
	 */
	public double getAngleBetween(Vector2D vector) {

		return this.vector.getAngleBetween(vector.vector);
	}

	/**
	 * Returns the direction of this Vector2D as an angle in radians.
	 * 
	 * @return double
	 */
	public double getDirection() {
		return this.vector.getDirection();
	}

	/**
	 * Returns the left-handed normal of this vector.
	 * 
	 * @return Vector2D
	 */
	public Vector2D getLeftHandOrthogonalVector() {

		Vector2 orthoVector = this.vector.getLeftHandOrthogonalVector();

		return new Vector2D(orthoVector.x, orthoVector.y);
	}

	/**
	 * Returns the magnitude of this Vector2D.
	 * 
	 * @return double
	 */
	public double getMagnitude() {
		return this.vector.getMagnitude();
	}

	/**
	 * Returns the magnitude of this Vector2D squared.
	 * 
	 * @return double
	 */
	public double getMagnitudeSquared() {
		return this.vector.getMagnitudeSquared();
	}

	/**
	 * Returns a Vector2D which is the negative of this Vector2D.
	 * 
	 * @return Vector2D
	 */
	public Vector2D getNegative() {

		Vector2 negative = this.vector.getNegative();

		return new Vector2D(negative.x, negative.y);
	}

	/**
	 * Returns a unit Vector2D of this Vector2D.
	 * 
	 * @return Vector2D
	 */
	public Vector2D getNormalized() {

		Vector2 normalized = this.vector.getNormalized();

		return new Vector2D(normalized.x, normalized.y);
	}

	/**
	 * Returns the right-handed normal of this vector.
	 * 
	 * @return Vector2D
	 */
	public Vector2D getRightHandOrthogonalVector() {

		Vector2 orthoVector = this.vector.getRightHandOrthogonalVector();

		return new Vector2D(orthoVector.x, orthoVector.y);
	}

	/**
	 * Returns the x component of this Vector2D.
	 * 
	 * @return Vector2D
	 */
	public double getXComponent() {
		return this.vector.x;
	}

	/**
	 * Returns the y component of this Vector2D.
	 * 
	 * @return Vector2D
	 */
	public double getYComponent() {
		return this.vector.y;
	}

	/**
	 * Returns true if the given Vector2D is orthogonal (perpendicular) to this
	 * Vector2D.
	 * 
	 * @param x
	 * @param y
	 * @return boolean
	 */
	public boolean isOrthogonal(double x, double y) {
		return this.vector.isOrthogonal(x, y);
	}

	/**
	 * Returns true if the given Vector2D is orthogonal (perpendicular) to this
	 * Vector2D.
	 * 
	 * @param x
	 * @param y
	 * @return boolean
	 */
	public boolean isOrthogonal(Vector2D vector) {
		return this.vector.isOrthogonal(vector.vector);
	}

	/**
	 * Returns true if this Vector2D is the zero Vector2D.
	 * 
	 * @return boolean
	 */
	public boolean isZero() {
		return this.vector.isZero();
	}

	/**
	 * Sets this vector to the left-handed normal of this vector.
	 * 
	 * @return Vector2D
	 */
	public Vector2D left() {

		Vector2 left = this.vector.copy().left();

		return new Vector2D(left.x, left.y);
	}

	/**
	 * Multiplies this Vector2D by the given scalar.
	 * 
	 * @param scalar
	 * @return Vector2D
	 */
	public Vector2D multiply(double scalar) {

		Vector2 multiplied = this.vector.copy().multiply(scalar);

		return new Vector2D(multiplied.x, multiplied.y);
	}

	/**
	 * Negates this Vector2D.
	 * 
	 * @return Vector2D
	 */
	public Vector2D negate() {

		Vector2 negate = this.vector.copy().negate();

		return new Vector2D(negate.x, negate.y);
	}

	/**
	 * Converts this Vector2D into a unit Vector2D and returns the magnitude
	 * before normalization.
	 * 
	 * @return double
	 */
	public double normalize() {
	
		return this.vector.copy().normalize();
	}

	/**
	 * Multiplies this Vector2D by the given scalar returning a new Vector2D
	 * containing the result.
	 * 
	 * @param scalar
	 * @return Vector2D
	 */
	public Vector2D product(double scalar) {

		Vector2 product = this.vector.product(scalar);

		return new Vector2D(product.x, product.y);
	}

	/**
	 * Projects this Vector2D onto the given Vector2D.
	 * 
	 * @param vector
	 * @return Vector2D
	 */
	public Vector2D project(Vector2D vector) {

		Vector2 projection = this.vector.project(vector.vector);

		return new Vector2D(projection.x, projection.y);
	}

	/**
	 * Sets this vector to the right-handed normal of this vector.
	 * 
	 * @return Vector2D
	 */
	public Vector2D right() {

		Vector2 right = this.vector.copy().right();

		return new Vector2D(right.x, right.y);
	}

	/**
	 * Rotates about the origin.
	 * The rotation is between -PI and PI, do not use more or less
	 * 
	 * @param theta
	 * @return Vector2D
	 */
	public Vector2D rotate(double theta) {

		Vector2 rotated = this.vector.copy().rotate(theta);

		return new Vector2D(rotated.x, rotated.y);
	}

	/**
	 * Rotates the Vector2D about the given coordinates. 
	 * The rotation is between -PI and PI, do not use more or less
	 * 
	 * @param theta
	 * @param x
	 * @param y
	 * @return Vector2D
	 */
	public Vector2D rotate(double theta, double x, double y) {

		Vector2 rotated = this.vector.copy().rotate(theta, x, y);

		return new Vector2D(rotated.x, rotated.y);
	}

	/**
	 * Rotates the Vector2 about the given point.
	 * The rotation is between -PI and PI, do not use more or less
	 * 
	 * @param theta
	 * @param point
	 * @return Vector2D
	 */
	public Vector2D rotate(double theta, Vector2D point) {

		Vector2 rotated = this.vector.copy().rotate(theta, point.vector);

		return new Vector2D(rotated.x, rotated.y);
	}

	/**
	 * Sets this Vector2D to the given Vector2D.
	 * 
	 * @param x
	 * @param y
	 * @return Vector2D
	 */
	public Vector2D set(double x, double y) {

		Vector2 set = this.vector.set(x, y);

		return new Vector2D(set.x, set.y);
	}

	/**
	 * Sets this Vector2D to the given Vector2D.
	 * 
	 * @param x
	 * @param y
	 * @return Vector2D
	 */
	public Vector2D set(Vector2D vector) {

		Vector2 set = this.vector.set(vector.vector);

		return new Vector2D(set.x, set.y);
	}

	/**
	 * Sets the direction of this Vector2D.
	 * 
	 * @param angle
	 * @return Vector2D
	 */
	public Vector2D setDirection(double angle) {

		Vector2 aligned = this.vector.setDirection(angle);

		return new Vector2D(aligned.x, aligned.y);
	}

	/**
	 * Sets the magnitude of this Vector2D.
	 * 
	 * @param magnitude
	 * @return Vector2D
	 */
	public Vector2D setMagnitude(double magnitude) {

		Vector2 corrected = this.vector.setMagnitude(magnitude);

		return new Vector2D(corrected.x, corrected.y);
	}

	/**
	 * Subtracts the given Vector2D from this Vector2D.
	 * 
	 * @param x
	 * @param y
	 * @return Vector2D
	 */
	public Vector2D subtract(double x, double y) {

		Vector2 subtracted = this.vector.copy().subtract(x, y);

		return new Vector2D(subtracted.x, subtracted.y);
	}

	/**
	 * Subtracts the given Vector2D from this Vector2D.
	 * 
	 * @param x
	 * @param y
	 * @return Vector2D
	 */
	public Vector2D subtract(Vector2D vector) {

		Vector2 subtracted = this.vector.copy().subtract(vector.vector);

		return new Vector2D(subtracted.x, subtracted.y);
	}

	/**
	 * Adds this Vector2D and the given Vector2D returning a new Vector2D
	 * containing the result.
	 * 
	 * @param x
	 * @param y
	 * @return Vector2D
	 */
	public Vector2D sum(double x, double y) {

		Vector2 sum = this.vector.sum(x, y);

		return new Vector2D(sum.x, sum.y);
	}

	/**
	 * Adds this Vector2D and the given Vector2D returning a new Vector2D
	 * containing the result.
	 * 
	 * @param x
	 * @param y
	 * @return Vector2D
	 */
	public Vector2D sum(Vector2D vector) {

		Vector2 sum = this.vector.sum(vector.vector);

		return new Vector2D(sum.x, sum.y);
	}

	/**
	 * Creates a Vector2D from this Vector2D to the given Vector2D.
	 * 
	 * @param x
	 * @param y
	 * @return Vector2D
	 */
	public Vector2D to(double x, double y) {

		Vector2 connection = this.vector.to(x, y);

		return new Vector2D(connection.x, connection.y);
	}

	/**
	 * Creates a Vector2D from this Vector2D to the given Vector2D.
	 * 
	 * @param x
	 * @param y
	 * @return Vector2D
	 */
	public Vector2D to(Vector2D vector) {

		Vector2 connection = this.vector.to(vector.vector);

		return new Vector2D(connection.x, connection.y);
	}
	
	/**
	 * The triple product of Vector2s is defined as:
	 * 
	 * a x (b x c)
	 * 
	 * However, this method performs the following triple product:
	 * 
	 * (a x b) x c
	 * 
	 * this can be simplified to:
	 * 
	 * -a * (b · c) + b * (a · c)
	 * 
	 * or:
	 * 
	 * b * (a · c) - a * (b · c)
	 * 
	 * @param a
	 * @param b
	 * @param c
	 * @return Vector2D
	 */
	public Vector2D tripleProduct(Vector2D a, Vector2D b, Vector2D c) {

		Vector2 product = Vector2.tripleProduct(a.vector, b.vector, c.vector);

		return new Vector2D(product.x, product.y);
	}

	/**
	 * Sets the Vector2 to the zero Vector2D.
	 * 
	 * @return Vector2D
	 */
	public Vector2D setZero() {

		Vector2 zero = this.vector.zero();

		return new Vector2D(zero.x, zero.y);
	}

	// -------------------------------------------------------------------------------

	/**
	 * Returns the x and y components of this vector together as one array.
	 * 
	 * @return double[]
	 */
	public double[] getComponents() {
		return new double[] {this.vector.x, this.vector.y};
	}

	/**
	 * Returns the cosine of the angle between this Vector2D and another given
	 * Vector2D.
	 * 
	 * @param vector
	 * @return double
	 */
	public double cosineAlpha(Vector2D vector) {
		return this.dot(vector) / (this.getMagnitude() * vector.getMagnitude());
	}

	/**
	 * Scales the vector such that norm of the vector is equal to 'length'.
	 * 
	 * @param length
	 * @return Vector2D
	 */
	public Vector2D scale(double length) {

		double x = this.getComponents()[0];
		double y = this.getComponents()[1];

		double temp = length / this.getMagnitude();

		return new Vector2D(x * temp, y * temp);
	}
	
	/**
	 * Gives a String with the x- and y-coordinates of the vector
	 * 
	 * 
	 * @return String
	 */
	
	public String toString() {
		
		return "("+ this.getXComponent() + "," + this.getYComponent() +")";
	}
	
	/**
	 * Gives distance from the vector to 
	 * 
	 * @param referencePointOfLine
	 * @param gradientVectorOfLine
	 * @return double
	 */	
}
