/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabslx.conversions;

/** Converts an object into a XABSL decimal value */

public interface EnumeratedConversion {

	/**
	 * Convert an object to an enumerated value
	 * 
	 * @param value
	 *            Must be of the type returned by type()
	 * @return The converted value
	 */
	public Object from(Object value);

	/**
	 * Convert an enumerated value to an Object
	 * 
	 * @param value
	 *            Any enumerated value
	 * @return The converted value. The object must be of the type returned by
	 *         type()
	 */
	public Object to(Object value);

	/**
	 * @return The type that this converter handles.
	 */
	public Class<?> type();

	/**
	 * Returns the enumeration's name from a certain type. If this is not
	 * possible, the enumeration must be registered manually.
	 * 
	 * @param type
	 * @return
	 */
	public String getEnumerationName(Class<?> type);
}
