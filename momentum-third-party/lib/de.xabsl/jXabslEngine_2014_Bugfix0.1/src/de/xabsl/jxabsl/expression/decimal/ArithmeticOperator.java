/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabsl.expression.decimal;

import de.xabsl.jxabsl.utils.DebugMessages;

/**
 * Base class for the +, -, *, / and % operator.
 */
public abstract class ArithmeticOperator extends DecimalExpression {

	protected DecimalExpression operand1;

	protected DecimalExpression operand2;

	public ArithmeticOperator(DebugMessages debug) {
		super(debug);
	}

	/**
	 * Creates the operator
	 * 
	 * @param operand1
	 *            The first operand
	 * @param operand2
	 *            The second operand
	 */

	public void create(DecimalExpression operand1, DecimalExpression operand2) {
		this.operand1 = operand1;
		this.operand2 = operand2;

		debug.printlnInit("Created: " + this);

	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "[" + operand1 + ", "
				+ operand2 + "]";
	}

}
