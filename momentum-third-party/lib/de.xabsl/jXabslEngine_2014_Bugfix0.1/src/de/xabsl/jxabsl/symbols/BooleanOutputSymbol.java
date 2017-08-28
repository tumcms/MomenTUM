/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabsl.symbols;

/**
 * Represents a boolean output symbol
 */

public interface BooleanOutputSymbol {

	/**
	 * Sets the symbol's value
	 * 
	 * @param value
	 *            the symbols new value
	 */
	public void setValue(boolean value);

	/**
	 * @return the symbol's value
	 */
	public boolean getValue();

}
