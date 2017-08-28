/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabslx.conversions;

/** Converts an object into a XABSL decimal value */

public interface DecimalConversion {

	/**
	 * Convert an object to a double
	 * 
	 * @param value
	 *            Must be of the type returned by type()
	 * @return The converted value
	 */
	public double from(Object value);

	/**
	 * Convert a double to an Object
	 * 
	 * @param value
	 *            Any double value
	 * @return The converted value. The object must be of the type returned by
	 *         type()
	 */
	public Object to(double value);

	/**
	 * @return The type that this converter handles.
	 */
	public Class<?> type();
}
