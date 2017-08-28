/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabsl.symbols;

import de.xabsl.jxabsl.parameters.Parameters;

/**
 * Represents an enumerated input symbol
 */

public interface EnumeratedInputSymbol {
	/**
	 * Returns the symbol's value. Before calling this function, obtain the
	 * parameters via <code>getParameters()</code> and set the parameter
	 * values
	 * 
	 * @return the symbol's value
	 */

	public Object getValue();

	/**
	 * @return the symbol's parameters
	 */

	public Parameters getParameters();

	/**
	 * @return the enumeration that is the domain of the symbol
	 */

	public Enumeration getEnumeration();

}