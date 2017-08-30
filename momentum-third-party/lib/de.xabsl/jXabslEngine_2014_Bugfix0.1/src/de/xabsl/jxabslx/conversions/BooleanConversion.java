/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabslx.conversions;

/** Converts an object into a XABSL boolean value */

public interface BooleanConversion {
	/**
	 * Convert an object to a boolean
	 * 
	 * @param value
	 *            Must be of the type returned by type()
	 * @return The converted value
	 */
	public boolean from(Object value);

	/**
	 * Convert a boolean to an Object
	 * 
	 * @param value
	 *            Any boolean value
	 * @return The converted value. The object must be of the type returned by
	 *         type()
	 */
	public Object to(boolean value);

	/**
	 * @return The type that this converter handles.
	 */
	public Class<?> type();
}
