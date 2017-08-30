/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabsl.action;

import java.util.List;
import java.util.Map;

import de.xabsl.jxabsl.IntermediateCodeMalformedException;
import de.xabsl.jxabsl.TimeFunction;
import de.xabsl.jxabsl.behavior.BasicBehavior;
import de.xabsl.jxabsl.behavior.Behavior;
import de.xabsl.jxabsl.behavior.Option;
import de.xabsl.jxabsl.behavior.OptionParameters;
import de.xabsl.jxabsl.engine.Engine;
import de.xabsl.jxabsl.expression.bool.BooleanExpression;
import de.xabsl.jxabsl.expression.decimal.DecimalExpression;
import de.xabsl.jxabsl.expression.enumerated.EnumeratedExpression;
import de.xabsl.jxabsl.parameters.ParameterAssignment;
import de.xabsl.jxabsl.symbols.EnumeratedOutputSymbol;
import de.xabsl.jxabsl.utils.DebugMessages;
import de.xabsl.jxabsl.utils.InputSource;

/**
 * Represents an action execution. This is either a subsequent option or basic
 * behavior to be executed, or an output symbol assignment.
 * 
 */
public abstract class Action {
	/**
	 * Constructor.
	 * 
	 * @param timeFunction
	 *            a pointer to a function that returns the system time in ms.
	 */
	public Action(TimeFunction timeFunction) {
		this.timeFunction = timeFunction;
	}

	/**
	 * Creates an action definition which just calls a single option or basic
	 * behavior without setting any parameters.
	 * 
	 * @param behavior
	 *            The referenced option or basic behavior.
	 * @param debug
	 *            A stream for debug messages
	 * @param timeFunction
	 *            a pointer to a function that returns the system time in ms.
	 */

	public static ActionBehavior create(Behavior behavior, DebugMessages debug,
			TimeFunction timeFunction) {

		ActionBehavior action = null;

		if (behavior instanceof Option) {
			action = new ActionOption(timeFunction);
			((ActionOption) action).option = (Option) behavior;
		} else if (behavior instanceof BasicBehavior) {
			action = new ActionBasicBehavior(timeFunction);
			((ActionBasicBehavior) action).basicBehavior = (BasicBehavior) behavior;

		}
		if (action != null)
			action.parameterAssignment = new ParameterAssignment(debug,
					behavior.getParameters());

		return action;
	}

	/**
	 * Creates an action definition.
	 * 
	 * @param input
	 *            An input source for the intermediate code. It must be opened
	 *            and read until a position where a state starts.
	 * @param options
	 *            All available options
	 * @param optionParameters
	 *            The parameters of the option
	 * @param symbols
	 *            All available symbols
	 * @param timeOfOptionExecution
	 *            The time how long the option is already active
	 * @param timeOfStateExecution
	 *            The time how long the current state is already active
	 * @param actions
	 *            The subsequent behaviors i.e options and basic behaviors of
	 *            the state.
	 * @param debu
	 *            A stream for debug output
	 * @param timeFunction
	 *            a pointer to a function that returns the system time in ms.
	 */

	public static Action create(InputSource input, Map<String, Option> options,
			Engine engine, OptionParameters optionParameters,
			TimeFunction timeOfOptionExecution, TimeFunction timeOfStateExecution,
			List<Action> actions, DebugMessages debug, TimeFunction timeFunction)
			throws IntermediateCodeMalformedException {

		String type = input.next();
		String name = input.next();

		switch (type.charAt(0)) {
		case 'o':
			debug.printlnInit("Creating reference to option " + name);

			if (!options.containsKey(name))
				throw new IntermediateCodeMalformedException("Unknown option "
						+ name + " was encountered!");
			else {
				ActionOption action = new ActionOption(timeFunction);
				action.option = options.get(name);
				action.parameterAssignment = new ParameterAssignment(debug,
						action.option.getParameters());
				action.parameterAssignment.create(input, optionParameters,
						engine, timeOfOptionExecution, timeOfStateExecution,
						actions);
				return action;
			}

		case 'a': {
			debug.printlnInit("Creating reference to basic behavior " + name);

			ActionBasicBehavior action = new ActionBasicBehavior(timeFunction);

			ParameterAssignment paramAssignment = new ParameterAssignment(
					debug, null);

			action.parameterAssignment = paramAssignment;

			paramAssignment.create(input, optionParameters, engine,
					timeOfOptionExecution, timeOfStateExecution, actions);

			action.basicBehavior = engine.getBasicBehavior(name,
					paramAssignment.getDecimalNames(), paramAssignment
							.getBooleanNames(), paramAssignment
							.getEnumeratedNames());

			paramAssignment.setRefParameters(action.basicBehavior
					.getParameters());

			return action;

		}
		case 'd': {
			debug.printlnInit("Creating reference to decimal output symbol "
					+ name);

			ActionDecimalOutputSymbol action = new ActionDecimalOutputSymbol(
					timeFunction, engine.getDecimalOutputSymbol(name),
					DecimalExpression.create(input, actions, debug,
							optionParameters, engine, timeOfOptionExecution,
							timeOfStateExecution));
			return action;
		}
		case 'b': {
			debug.printlnInit("Creating reference to boolean	output symbol "
					+ name);

			ActionBooleanOutputSymbol action = new ActionBooleanOutputSymbol(
					timeFunction, engine.getBooleanOutputSymbol(name),
					BooleanExpression.create(input, actions, debug,
							optionParameters, engine, timeOfOptionExecution,
							timeOfStateExecution));
			return action;
		}

		case 'e': {
			debug.printlnInit("Creating reference to enumerated output symbol "
					+ name);

			EnumeratedOutputSymbol outputSymbol = engine
					.getEnumeratedOutputSymbol(name);

			ActionEnumeratedOutputSymbol action = new ActionEnumeratedOutputSymbol(
					timeFunction, outputSymbol, EnumeratedExpression.create(
							outputSymbol.getEnumeration(), input, actions,
							debug, optionParameters, engine,
							timeOfOptionExecution, timeOfStateExecution));
			return action;

		}
		}
		throw new IntermediateCodeMalformedException(
				"Error creating action, was expecting one of: o a d b e");
	}

	/**
	 * Execute the action.
	 */
	public abstract void execute();

	protected TimeFunction timeFunction;
}