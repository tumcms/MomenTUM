/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabsl.expression.bool;

import de.xabsl.jxabsl.utils.DebugMessages;

/**
 * Represents an 'equal-to' element of the option graph
 */

public class EqualToOperator extends RelationalAndEqualityOperator {

	/**
	 * Constructor.
	 * 
	 * @param debug
	 *            For debugging output
	 */
	public EqualToOperator(DebugMessages debug) {
		super(debug);
	}

	@Override
	public boolean getValue() {

		return operand1.getValue() == operand2.getValue();
	}

}
