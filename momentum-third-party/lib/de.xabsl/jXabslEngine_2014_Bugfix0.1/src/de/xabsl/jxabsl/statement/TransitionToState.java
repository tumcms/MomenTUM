/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabsl.statement;

import java.util.Map;

import de.xabsl.jxabsl.IntermediateCodeMalformedException;
import de.xabsl.jxabsl.state.State;
import de.xabsl.jxabsl.utils.DebugMessages;
import de.xabsl.jxabsl.utils.InputSource;

/**
 * Represents a transition to a state inside a decision tree
 */

public class TransitionToState extends Statement {

	private State nextState;

	/**
	 * Constructor. Creates the transition object
	 * 
	 * @param input
	 *            An input source for the intermediate code. It must be opened
	 *            and read until A position where a transition starts.
	 * @param debug
	 *            For debugging output
	 * @param states
	 *            All states of the option
	 */

	public TransitionToState(InputSource input, DebugMessages debug,
			Map<String, State> states)
			throws IntermediateCodeMalformedException {

		String name = input.next();
		debug.printlnInit("Creating a transition to state " + name);

		nextState = states.get(name);

		if (nextState == null)
			throw new IntermediateCodeMalformedException(
					"Trying to create transition to state " + name
							+ ", but that state does not exist!");

	}

	@Override
	public State getNextState() {
		return nextState;
	}

}
