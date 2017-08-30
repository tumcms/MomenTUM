/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabsl.statement;

import java.util.List;
import java.util.Map;

import de.xabsl.jxabsl.IntermediateCodeMalformedException;
import de.xabsl.jxabsl.TimeFunction;
import de.xabsl.jxabsl.action.Action;
import de.xabsl.jxabsl.behavior.OptionParameters;
import de.xabsl.jxabsl.engine.Symbols;
import de.xabsl.jxabsl.state.State;
import de.xabsl.jxabsl.utils.DebugMessages;
import de.xabsl.jxabsl.utils.InputSource;

/**
 * An element of a decision that that determines a transition to a state. It can
 * be either a transition to a state or a if/else-if/else block containing other
 * statements.
 */

public abstract class Statement {
	/**
	 * Creates a statement depending on the input.
	 * 
	 * @param input
	 *            An input source for the intermediate code. It must be opened
	 *            and read until A position where a statement starts.
	 * @param subsequentOption
	 *            The subsequent option of the state. 0 if the subsequent
	 *            behavior is a basic behavior
	 * @param debug
	 *            For debugging output
	 * @param states
	 *            All states of the option
	 * @param parameters
	 *            The parameters of the option
	 * @param symbols
	 *            All available symbols
	 * @param timeOfOptionExecution
	 *            The time how long the option is already active
	 * @param timeOfStateExecution
	 *            The time how long the state is already active
	 */

	public static Statement createStatement(InputSource input,
			List<Action> actions, DebugMessages debug,
			Map<String, State> states, OptionParameters parameters,
			Symbols symbols, TimeFunction timeOfOptionExecution,
			TimeFunction timeOfStateExecution)
			throws IntermediateCodeMalformedException {

		String type = input.next();
		switch (type.charAt(0)) {
		case 't':
			return new TransitionToState(input, debug, states);
		case 'i':
			return new IfElseBlock(input, actions, debug, states, parameters,
					symbols, timeOfOptionExecution, timeOfStateExecution);
		default:
			throw new IntermediateCodeMalformedException(
					"Unknown statement type \'" + type.charAt(0) + "\'");

		}
	}

	/**
	 * Executes the statement and determines the next active state (can be the
	 * same).
	 */

	public abstract State getNextState();

}
