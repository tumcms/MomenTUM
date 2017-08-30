/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabsl.expression.bool;

import de.xabsl.jxabsl.utils.DebugMessages;

/**
 * Represents an logical 'not' operator
 */

public class NotOperator extends BooleanExpression {

	protected BooleanExpression operand;

	public NotOperator(BooleanExpression operand, DebugMessages debug) {
		super(debug);
		this.operand = operand;
	}

	@Override
	public boolean getValue() {
		return !operand.getValue();
	}

}
