/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabsl.expression.bool;

import de.xabsl.jxabsl.utils.DebugMessages;

/**
 * Represents an 'greater-than-or-equal-to' expression
 */

public class GreaterThanOrEqualToOperator extends RelationalAndEqualityOperator {

	public GreaterThanOrEqualToOperator(DebugMessages debug) {
		super(debug);
	}

	@Override
	public boolean getValue() {
		return operand1.getValue() >= operand2.getValue();
	}

}
