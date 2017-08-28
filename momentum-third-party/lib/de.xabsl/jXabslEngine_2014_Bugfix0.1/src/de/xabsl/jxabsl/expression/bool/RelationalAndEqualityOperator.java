/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabsl.expression.bool;

import de.xabsl.jxabsl.expression.decimal.DecimalExpression;
import de.xabsl.jxabsl.utils.DebugMessages;

/**
 * Base class for the operators <, <=, >, >=, == and !=
 * 
 */
public abstract class RelationalAndEqualityOperator extends BooleanExpression {

	protected DecimalExpression operand1;

	protected DecimalExpression operand2;

	public RelationalAndEqualityOperator(DebugMessages debug) {
		super(debug);
	}

	/**
	 * Creates the element.
	 * 
	 * @param operand1
	 *            A decimal expression
	 * @param operand2
	 *            A decimal expression
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
