/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabsl.expression.decimal;

import java.util.List;

import de.xabsl.jxabsl.IntermediateCodeMalformedException;
import de.xabsl.jxabsl.TimeFunction;
import de.xabsl.jxabsl.action.Action;
import de.xabsl.jxabsl.behavior.OptionParameters;
import de.xabsl.jxabsl.engine.Symbols;
import de.xabsl.jxabsl.expression.bool.BooleanExpression;
import de.xabsl.jxabsl.utils.DebugMessages;
import de.xabsl.jxabsl.utils.InputSource;

/**
 * Represents a (condition?expression:expression) question mark operator
 */

public class ConditionalDecimalExpression extends DecimalExpression {
	private BooleanExpression condition;

	private DecimalExpression expression1;

	private DecimalExpression expression2;

	/**
	 * Constructor. Creates the expression
	 * 
	 * @param input
	 *            An input source for the intermediate code. It must be opened
	 *            and read until A position where a expression starts.
	 * @param actions
	 *            The subsequent behaviors i.e options and basic behaviors of
	 *            the state.
	 * @param debug
	 *            For debugging output
	 * @param parameters
	 *            The parameters of the option
	 * @param symbols
	 *            All available symbols
	 * @param timeOfOptionExecution
	 *            The time how long the option is already active
	 * @param timeOfStateExecution
	 *            The time how long the state is already active
	 */

	public ConditionalDecimalExpression(InputSource input,
			List<Action> actions, DebugMessages debug,
			OptionParameters parameters, Symbols symbols,
			TimeFunction timeOfOptionExecution,
			TimeFunction timeOfStateExecution)
			throws IntermediateCodeMalformedException {
		super(debug);
		debug.printlnInit("Creating: Question mark operator (type decimal)");

		condition = BooleanExpression.create(input, actions, debug, parameters,
				symbols, timeOfOptionExecution, timeOfStateExecution);

		expression1 = DecimalExpression.create(input, actions, debug,
				parameters, symbols, timeOfOptionExecution,
				timeOfStateExecution);
		expression2 = DecimalExpression.create(input, actions, debug,
				parameters, symbols, timeOfOptionExecution,
				timeOfStateExecution);

	}

	@Override
	public double getValue() {

		if (condition.getValue())
			return expression1.getValue();
		else
			return expression2.getValue();
	}
}