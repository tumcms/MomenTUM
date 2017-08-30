/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabsl.expression.decimal;

import de.xabsl.jxabsl.utils.DebugMessages;

/**
 * Represents a + operator in the option graph
 */

public class PlusOperator extends ArithmeticOperator {

	public PlusOperator(DebugMessages debug) {
		super(debug);
	}

	@Override
	public double getValue() {
		return operand1.getValue() + operand2.getValue();
	}

}
