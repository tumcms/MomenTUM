/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabsl.expression.decimal;

import de.xabsl.jxabsl.utils.DebugMessages;

/**
 * Represents a / operator in the option graph
 */

public class DivideOperator extends ArithmeticOperator {

	public DivideOperator(DebugMessages debug) {
		super(debug);
	}

	@Override
	public double getValue() {

		// do not divide by zero
		double o2 = operand2.getValue();
		if (o2 == 0)
			return operand1.getValue() / 0.0000001;
		else
			return operand1.getValue() / o2;

	}

}
