/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabsl.symbols;

import de.xabsl.jxabsl.parameters.Parameters;

/**
 * Represents a boolean input symbol
 */

public interface BooleanInputSymbol {

	/**
	 * Returns the symbol's value. Before calling this function, obtain the
	 * parameters via <code>getParameters()</code> and set the parameter
	 * values
	 * 
	 * @return the symbol's value
	 */
	public boolean getValue();

	/**
	 * @return the symbol's parameters
	 */
	public Parameters getParameters();

}