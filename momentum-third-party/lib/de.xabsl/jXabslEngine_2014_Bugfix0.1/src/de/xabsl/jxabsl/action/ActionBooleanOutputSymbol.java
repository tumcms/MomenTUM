/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabsl.action;

import de.xabsl.jxabsl.TimeFunction;
import de.xabsl.jxabsl.expression.bool.BooleanExpression;
import de.xabsl.jxabsl.symbols.BooleanOutputSymbol;

/**
 * Represents an action execution, in this case a boolean output symbol
 * assignment
 */
public class ActionBooleanOutputSymbol extends Action {

	private BooleanOutputSymbol outputSymbol;
	private BooleanExpression expression;

	/**
	 * Constructor.
	 * 
	 * @param timeFunction
	 *            A pointer to a function that returns the system time in ms.
	 */

	public ActionBooleanOutputSymbol(TimeFunction timeFunction,
			BooleanOutputSymbol outputSymbol, BooleanExpression expression) {
		super(timeFunction);
		this.outputSymbol = outputSymbol;
		this.expression = expression;

	}

	@Override
	public void execute() {
		outputSymbol.setValue(expression.getValue());

	}

}
