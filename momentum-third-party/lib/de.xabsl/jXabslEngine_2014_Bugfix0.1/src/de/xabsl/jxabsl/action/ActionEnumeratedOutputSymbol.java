/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabsl.action;

import de.xabsl.jxabsl.TimeFunction;
import de.xabsl.jxabsl.expression.enumerated.EnumeratedExpression;
import de.xabsl.jxabsl.symbols.EnumeratedOutputSymbol;

/**
 * Represents an action execution, in this case an enumerated output symbol
 * assignment
 */
public class ActionEnumeratedOutputSymbol extends Action {

	private EnumeratedOutputSymbol outputSymbol;
	private EnumeratedExpression expression;

	/**
	 * Constructor.
	 * 
	 * @param timeFunction
	 *            a pointer to a function that returns the system time in ms.
	 */

	public ActionEnumeratedOutputSymbol(TimeFunction timeFunction,
			EnumeratedOutputSymbol outputSymbol, EnumeratedExpression expression) {
		super(timeFunction);
		this.outputSymbol = outputSymbol;
		this.expression = expression;

	}

	@Override
	public void execute() {
		outputSymbol.setValue(expression.getValue());

	}

}
