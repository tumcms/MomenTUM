/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabsl.symbols;

/**
 * Represents an enumerated output symbol
 */

public interface EnumeratedOutputSymbol {
	/**
	 * Sets the symbol's value
	 * 
	 * @param value
	 *            the symbols new value
	 */

	public void setValue(Object value);

	/**
	 * @return the symbol's value
	 */

	public Object getValue();

	/**
	 * 
	 * @return the enumeration that is the domain of the symbol
	 */
	public Enumeration getEnumeration();

}
