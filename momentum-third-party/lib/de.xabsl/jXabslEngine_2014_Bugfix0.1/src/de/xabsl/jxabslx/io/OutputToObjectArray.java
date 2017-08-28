/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabslx.io;

import de.xabsl.jxabsl.EngineInitializationException;

/**
 * Writes a value to an array.
 */
public class OutputToObjectArray implements Output {

	private Object[] array;
	private int pos;

	/**
	 * 
	 * @param array
	 *            Will write to this array.
	 * @param pos
	 */
	public OutputToObjectArray(Object[] array, int pos) {

		this.array = array;
		this.pos = pos;

		if (array.length <= pos || pos < 0)
			throw new EngineInitializationException("The array" + array
					+ "does not contain the index" + pos);

	}

	public void setValue(Object value) {
		array[pos] = value;
	}
}
