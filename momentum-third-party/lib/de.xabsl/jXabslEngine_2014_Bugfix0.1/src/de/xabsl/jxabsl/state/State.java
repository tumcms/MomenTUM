/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabsl.state;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.xabsl.jxabsl.IntermediateCodeMalformedException;
import de.xabsl.jxabsl.TimeFunction;
import de.xabsl.jxabsl.action.Action;
import de.xabsl.jxabsl.action.ActionOption;
import de.xabsl.jxabsl.behavior.Option;
import de.xabsl.jxabsl.behavior.OptionParameters;
import de.xabsl.jxabsl.engine.Engine;
import de.xabsl.jxabsl.statement.Statement;
import de.xabsl.jxabsl.symbols.NamedItem;
import de.xabsl.jxabsl.utils.DebugMessages;
import de.xabsl.jxabsl.utils.InputSource;

/**
 * Represents a single state
 */
public class State extends NamedItem {
	/**
	 * Constructor. Does not create the state.
	 * 
	 * @param name
	 *            The name of the state. For debugging purposes.
	 * @param debug
	 *            For debugging output
	 * @param timeFunction
	 *            a function that returns the system time in ms.
	 */

	public State(String name, DebugMessages debug,
			final TimeFunction timeFunction) {
		super(name, debug);
		targetState = false;
		this.timeFunction = timeFunction;
		this.timeOfStateExecution = new TimeFunction() {

			public long getTime() {

				return timeFunction.getTime() - timeWhenStateWasActivated;
			}

		};
	}

	/**
	 * Creates the state and its subelements from the intermediate code.
	 * 
	 * @param input
	 *            An input source for the intermediate code. It must be opened
	 *            and read until A position where a state starts.
	 * @param options
	 *            All available options
	 * @param basicBehaviors
	 *            All available basicBehaviors
	 * @param states
	 *            All states of the option
	 * @param parameters
	 *            The parameters of the option
	 * @param symbols
	 *            All available symbols
	 * @param timeOfOptionExecution
	 *            The time how long the option is already active
	 */

	public void create(InputSource input, Map<String, Option> options,
			Engine engine, Map<String, State> states,
			OptionParameters parameters, TimeFunction timeOfOptionExecution)
			throws IntermediateCodeMalformedException {

		debug.printlnInit("Creating state: " + name);
		String c = input.next();
		if (c.charAt(0) == '1')
			this.targetState = true;

		int numberOfActions = input.nextInt();

		for (int i = 0; i < numberOfActions; i++) {
			Action action = Action.create(input, options, engine, parameters,
					timeOfOptionExecution, timeOfStateExecution, actions,
					debug, timeFunction);

			if (subsequentOption == null)
				if (action instanceof ActionOption)
					subsequentOption = (Option) ((ActionOption) action)
							.getBehavior();

			actions.add(action);

		}
		// transition to state or if-else-block
		decisionTree = Statement
				.createStatement(input, actions, debug, states, parameters,
						engine, timeOfOptionExecution, timeOfStateExecution);

	}

	/**
	 * Executes the decision tree and determines the next active state (can be
	 * the same).
	 */

	public State getNextState() {
		// In the original code, the time of state execution is calculated once,
		// as below. Shouldn't make much of a difference.
		// timeOfStateExecution = timeFunction.getTime()
		// - timeWhenStateWasActivated;
		return decisionTree.getNextState();

	}

	private List<Action> actions = new ArrayList<Action>();

	private TimeFunction timeOfStateExecution;

	/** Sets the time when the state was activated to 0 */

	public void reset() {
		timeWhenStateWasActivated = timeFunction.getTime();

	}

	/** Returns wheter the state is a target state */

	public boolean isTargetState() {
		return targetState;
	}

	private long timeWhenStateWasActivated;
	private boolean targetState;
	private Statement decisionTree;
	private TimeFunction timeFunction;
	private Option subsequentOption;

	public List<Action> getActions() {
		return actions;
	}

	@Override
	public String toString() {
		return getName();
	}

}
