/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabsl.statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.xabsl.jxabsl.IntermediateCodeMalformedException;
import de.xabsl.jxabsl.TimeFunction;
import de.xabsl.jxabsl.action.Action;
import de.xabsl.jxabsl.behavior.OptionParameters;
import de.xabsl.jxabsl.engine.Symbols;
import de.xabsl.jxabsl.expression.bool.BooleanExpression;
import de.xabsl.jxabsl.state.State;
import de.xabsl.jxabsl.utils.DebugMessages;
import de.xabsl.jxabsl.utils.InputSource;

/**
 * An element of a decision tree that that contains of an if - (else-if) - else
 * block
 */

public class IfElseBlock extends Statement {

	private BooleanExpression ifCondition;
	// TODO is this ever used?? It's not here. Just as in the original c++
	// code.
	private List<BooleanExpression> elseIfConditions = new ArrayList<BooleanExpression>();

	private Statement ifStatement;

	// TODO is this ever used?? no.
	private List<Statement> elseIfStatements = new ArrayList<Statement>();

	private Statement elseStatement;

	/**
	 * Constructor. Creates the if / else statement
	 * 
	 * @param input
	 *            An input source for the intermediate code. It must be opened
	 *            and read until A position where a transition starts.
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

	public IfElseBlock(InputSource input, List<Action> actions,
			DebugMessages debug, Map<String, State> states,
			OptionParameters parameters, Symbols symbols,
			TimeFunction timeOfOptionExecution,
			TimeFunction timeOfStateExecution)
			throws IntermediateCodeMalformedException {
		// if case
		debug.printlnInit("Creating if statement.");

		ifCondition = BooleanExpression.create(input, actions, debug,
				parameters, symbols, timeOfOptionExecution,
				timeOfStateExecution);
		ifStatement = Statement.createStatement(input, actions, debug, states,
				parameters, symbols, timeOfOptionExecution,
				timeOfStateExecution);

		// else case
		debug.printlnInit("Creating else statement.");
		elseStatement = Statement.createStatement(input, actions, debug,
				states, parameters, symbols, timeOfOptionExecution,
				timeOfStateExecution);

	}

	@Override
	public State getNextState() {
		if (ifCondition.getValue())
			return ifStatement.getNextState();

		for (int i = 0; i < elseIfConditions.size(); i++) {
			if (elseIfConditions.get(i).getValue())
				return elseIfStatements.get(i).getNextState();
		}
		return elseStatement.getNextState();

	}

}
