/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabsl.expression.bool;

import java.util.List;

import de.xabsl.jxabsl.IntermediateCodeMalformedException;
import de.xabsl.jxabsl.TimeFunction;
import de.xabsl.jxabsl.action.Action;
import de.xabsl.jxabsl.behavior.OptionParameters;
import de.xabsl.jxabsl.engine.Symbols;
import de.xabsl.jxabsl.expression.enumerated.EnumeratedExpression;
import de.xabsl.jxabsl.utils.DebugMessages;
import de.xabsl.jxabsl.utils.InputSource;

/**
 * Represents a comparison of enumerated expressions
 */

public class EnumeratedExpressionComparison extends BooleanExpression {

	/**
	 * Constructor. Creates the element
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

	public EnumeratedExpressionComparison(InputSource input,
			List<Action> actions, DebugMessages debug,
			OptionParameters parameters, Symbols symbols,
			TimeFunction timeOfOptionExecution, TimeFunction timeOfStateExecution)
			throws IntermediateCodeMalformedException {
		super(debug);

		operand1 = EnumeratedExpression.create(null, input, actions, debug,
				parameters, symbols, timeOfOptionExecution,
				timeOfStateExecution);
		operand2 = EnumeratedExpression.create(null, input, actions, debug,
				parameters, symbols, timeOfOptionExecution,
				timeOfStateExecution);

	}

	private EnumeratedExpression operand1;
	private EnumeratedExpression operand2;

	@Override
	public boolean getValue() {
		return operand1.getValue() == operand2.getValue();
	}
}
