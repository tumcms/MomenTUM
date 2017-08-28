/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabsl.action;

import de.xabsl.jxabsl.TimeFunction;
import de.xabsl.jxabsl.expression.decimal.DecimalExpression;
import de.xabsl.jxabsl.symbols.DecimalOutputSymbol;

/**
 * Represents an action execution, in this case a decimal output symbol
 * assignment
 */
public class ActionDecimalOutputSymbol extends Action {

	private DecimalOutputSymbol outputSymbol;
	private DecimalExpression expression;

	/**
	 * Constructor.
	 * 
	 * @param timeFunction
	 *            A pointer to a function that returns the system time in ms.
	 */

	public ActionDecimalOutputSymbol(TimeFunction timeFunction,
			DecimalOutputSymbol outputSymbol, DecimalExpression expression) {
		super(timeFunction);
		this.outputSymbol = outputSymbol;
		this.expression = expression;

	}

	@Override
	public void execute() {
		outputSymbol.setValue(expression.getValue());

	}

}
