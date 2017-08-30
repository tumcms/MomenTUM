/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabsl.expression.bool;

import de.xabsl.jxabsl.utils.DebugMessages;

/**
 * Represents an 'greater-than' expression
 */

public class GreaterThanOperator extends RelationalAndEqualityOperator {

	public GreaterThanOperator(DebugMessages debug) {
		super(debug);
	}

	@Override
	public boolean getValue() {
		return operand1.getValue() > operand2.getValue();
	}

}
