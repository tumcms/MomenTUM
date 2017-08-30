/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabsl.expression.bool;

import java.util.ArrayList;

import de.xabsl.jxabsl.utils.DebugMessages;

/**
 * Represents a logical 'and' operator
 */

public class AndOperator extends BooleanExpression {

	protected ArrayList<BooleanExpression> operands = new ArrayList<BooleanExpression>();

	public AndOperator(DebugMessages debug) {
		super(debug);
	}

	/** Adds an operand */
	public void addOperand(BooleanExpression operand) {
		operands.add(operand);
		debug.printlnInit("AndOperator: Added operand, now: " + this);

	}

	@Override
	public boolean getValue() {
		for (BooleanExpression operand : operands) {
			if (!operand.getValue())
				return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuffer s = new StringBuffer("AndOperator[");
		for (BooleanExpression op : operands) {
			s.append(op + ", ");
		}
		s.replace(s.length() - 2, s.length(), "]");

		return new String(s);
	}
}
