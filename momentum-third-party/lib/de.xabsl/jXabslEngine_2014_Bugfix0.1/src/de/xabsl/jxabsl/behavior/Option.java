/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabsl.behavior;

import java.util.LinkedHashMap;
import java.util.Map;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import de.xabsl.jxabsl.IntermediateCodeMalformedException;
import de.xabsl.jxabsl.TimeFunction;
import de.xabsl.jxabsl.action.Action;
import de.xabsl.jxabsl.engine.Engine;
import de.xabsl.jxabsl.engine.Symbols;
import de.xabsl.jxabsl.state.State;
import de.xabsl.jxabsl.utils.DebugMessages;
import de.xabsl.jxabsl.utils.InputSource;

/**
 * Represents a single option
 */

public class Option extends Behavior {

	protected State activeState;

	protected State initialState;

	protected TimeFunction timeFunction;

	protected LinkedHashMap<String, State> states = new LinkedHashMap<String, State>();

	/**
	 * Constructor. Does not create the option.
	 * 
	 * @param name
	 *            The name of the option. For debugging purposes.
	 * @param input
	 *            An input source for the intermediate code. It must be opened
	 *            and read until A position where an option starts.
	 * @param debug
	 *            Print debugging messages
	 * @param symbols
	 *            All available symbols
	 * @param timeFunction
	 *            a pointer to a function that returns the system time in ms.
	 */

	public Option(String name, InputSource input, DebugMessages debug,
			Symbols symbols, TimeFunction timeFunction /* FIXME , int index */) {
		super(name, debug);
		parameters = new OptionParameters(input, debug, symbols);
		this.timeFunction = timeFunction;

	}

	/**
	 * Creates the option and its states from the intermediate code.
	 * 
	 * @param input
	 *            An input source for the intermediate code. It must be opened
	 *            and read until a position where an option starts.
	 * @param options
	 *            All other options
	 * @param engine
	 *            The engine that provides symbols and basic behaviors
	 */
	public void create(InputSource input, Map<String, Option> options,
			Engine engine /* FIXME agentPriority, synchronizationTicks */)
			throws IntermediateCodeMalformedException {

		int i;

		// number of states of the option
		int numberOfStates = input.nextInt();

		// register all states
		String stateName;

		for (i = 0; i < numberOfStates; i++) {
			stateName = input.next();

			// cooperative state or not
			String type = input.next();
			switch (type.charAt(0)) {

			case 'n':
				states.put(stateName, new State(stateName, debug, timeFunction
				/* ,index, i */));
				break;
			case 's':
				// states.put(stateName, new SynchronizedState(stateName, debug,
				// timeFunction, index, i, agentPriority,
				// synchronizationTicks, input.nextInt()));
				// break;
			case 'c':
				// states.put(stateName, new CapacityState(stateName, debug,
				// timeFunction, index, i, agentPriority,
				// synchronizationTicks, input.nextInt()));
				// break;

				// FIXME implement above
				throw new IllegalArgumentException(
						"Synchronized/Capacity states are not yet implemented! Sorry :(");
			}

		}

		debug.printlnInit("Option " + this.name + ": registered "
				+ numberOfStates + " states.");

		// set the initial State
		stateName = input.next();
		initialState = states.get(stateName);
		activeState = initialState;

		debug.printlnInit("Option " + this.name + ": initial state "
				+ initialState.getName());

		// create the states and their subelements

		for (State s : states.values()) {
			s.create(input, options, engine, states,
					(OptionParameters) parameters, timeOfExecution);
		}

	}

	/** if a target state was reached */
	public boolean getOptionReachedATargetState() {
		return wasActive && activeState.isTargetState();
	}

	@Override
	public void execute() {
		if (active) {
			debug
					.printlnWarning("Option "
							+ this.name
							+ "is executed multiple times. This is unsupported. Resulting behavior might be unexpected.");

		}

		if (!wasActive) {
			activeState = initialState;
			activeState.reset();
		}

		State newState = activeState.getNextState();
		if (newState != activeState) {
			activeState = newState;
			activeState.reset();
		}

		for (Action a : activeState.getActions()) {
			a.execute();
		}
	}

	
	public LinkedHashMap<String, State> getStates() {
		return states;
	}

	
	public State getActiveState() {
		return activeState;
	}

}
