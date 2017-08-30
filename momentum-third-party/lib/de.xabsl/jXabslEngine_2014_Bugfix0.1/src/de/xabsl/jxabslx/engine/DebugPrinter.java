package de.xabsl.jxabslx.engine;

import java.io.PrintStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import de.xabsl.jxabsl.action.Action;
import de.xabsl.jxabsl.action.ActionBehavior;
import de.xabsl.jxabsl.agent.Agent;
import de.xabsl.jxabsl.behavior.Behavior;
import de.xabsl.jxabsl.behavior.Option;
import de.xabsl.jxabsl.engine.Engine;
import de.xabsl.jxabsl.parameters.ParameterAssignment;
import de.xabsl.jxabsl.state.State;

/**
 * Prints the state of an engine and demonstrates how to use the debugging
 * interface.
 * 
 */
public class DebugPrinter {

	public static void printDebug(PrintStream out, Engine engine) {

		// Agents
		out.print("Agents are: ");
		Set<String> agents = engine.getAgents().keySet();
		Iterator<String> i = agents.iterator();
		
		while (i.hasNext()) {
			String name = i.next();
			
			out.print(engine.getSelectedAgentNames().contains(name) ? "*" : " ");

			out.print(name);
			out.print(" ");
		}
		out.println();

		// Options
		out.print("Options: ");
		Set<String> options = engine.getOptions().keySet();
		i = options.iterator();
		while (i.hasNext()) {
			String name = i.next();
			out.print(name);
			out.print(" ");
		}
		out.println();

		// Execution path
		out.println("Execution path: ");

		for(Entry<String, Agent> agent : engine.getAgents().entrySet()) {
			ActionBehavior rootAction = engine.getRootAction(agent.getValue());
			printExecutionPath(0, rootAction, out);
		}
	}

	private static void printExecutionPath(int level, ActionBehavior action,
			PrintStream out) {

		for (int j = 0; j <= level; j++) {
			out.print("   ");
		}

		Behavior behavior = action.getBehavior();

		out.print(behavior + " (");

		ParameterAssignment pa = action.getParameterAssignment();

		boolean[] booleanDebugValues = pa.getBooleanDebugValues();
		double[] decimalDebugValues = pa.getDecimalDebugValues();
		Object[] enumeratedDebugValues = pa.getEnumeratedDebugValues();

		String s = "";

		if (booleanDebugValues != null && decimalDebugValues != null
				&& enumeratedDebugValues != null) {
			for (int i = 0; i < booleanDebugValues.length; i++)
				s += pa.getBooleanDebugName(i) + " = " + booleanDebugValues[i]
						+ ", ";

			for (int i = 0; i < decimalDebugValues.length; i++)
				s += pa.getDecimalDebugName(i) + " = " + decimalDebugValues[i]
						+ ", ";

			for (int i = 0; i < enumeratedDebugValues.length; i++)
				s += pa.getEnumeratedDebugName(i) + " = "
						+ enumeratedDebugValues[i] + ", ";

		}

		out.print(s);

		out.print(")");

		if (behavior instanceof Option) {
			out.print(" : ");
			Option option = (Option) behavior;
			Collection<State> states = option.getStates().values();
			Iterator<State> i = states.iterator();
			while (i.hasNext()) {
				State state = i.next();
				String name = state.getName();
				out.print(name.equals(option.getActiveState().getName()) ? "*"
						: " ");

				out.print(name);
				out.print(state.isTargetState() ? "!" : " ");
				out.print(" ");
			}
			out.println();

			List<Action> actions = option.getActiveState().getActions();

			for (Action a : actions) {
				if (a instanceof ActionBehavior) {
					printExecutionPath(level + 1, ((ActionBehavior) a), out);

				}
			}
		} else {
			out.println();
		}
	}

}
