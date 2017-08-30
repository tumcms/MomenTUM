/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabsl.expression.bool;

import java.util.ArrayList;

import de.xabsl.jxabsl.utils.DebugMessages;

/**
 * Represents a logical 'or' operator
 */
public class OrOperator extends BooleanExpression {

	protected ArrayList<BooleanExpression> operands = new ArrayList<BooleanExpression>();

	public OrOperator(DebugMessages debug) {
		super(debug);
	}

	/** Adds an operand */

	public void addOperand(BooleanExpression operand) {
		operands.add(operand);
		debug.printlnInit("OrOperator: Added operand, now: " + this);
	}

	@Override
	public boolean getValue() {
		for (BooleanExpression operand : operands) {
			if (operand.getValue())
				return true;
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuffer s = new StringBuffer("OrOperator[");
		for (BooleanExpression op : operands) {
			s.append(op + ", ");
		}
		s.replace(s.length() - 2, s.length(), "]");

		return new String(s);
	}

}
