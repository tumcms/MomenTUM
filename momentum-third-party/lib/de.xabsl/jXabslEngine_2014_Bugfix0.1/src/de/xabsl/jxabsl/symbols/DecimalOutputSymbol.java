/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabsl.symbols;

/**
 * Represents a decimal output symbol
 */

public interface DecimalOutputSymbol {
	/**
	 * Sets the symbol's value
	 * 
	 * @param value
	 *            the symbols new value
	 */

	public void setValue(double value);

	/**
	 * @return the symbol's value
	 */

	public double getValue();

}
