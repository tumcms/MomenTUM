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
import de.xabsl.jxabsl.parameters.ParameterAssignment;
import de.xabsl.jxabsl.symbols.DecimalInputSymbol;
import de.xabsl.jxabsl.utils.DebugMessages;
import de.xabsl.jxabsl.utils.InputSource;

/**
 * Represents a reference to a decimal input symbol.
 */

public class DecimalInputSymbolRef extends DecimalExpression {

	private DecimalInputSymbol inputSymbol;
	private ParameterAssignment paramAssignment;

	/**
	 * Constructor. Creates the expression depending on the input.
	 * 
	 * @param input
	 *            An input source for the intermediate code. It must be opened
	 *            and read until A position where the function reference starts.
	 * @param actions
	 *            The subsequent behaviors i.e options and basic behaviors of
	 *            the state.
	 * @param debug
	 *            For debugging output
	 * @param optionParameters
	 *            The parameters of the option
	 * @param symbols
	 *            All available symbols
	 * @param timeOfOptionExecution
	 *            The time how long the option is already active
	 * @param timeOfStateExecution
	 *            The time how long the state is already active
	 */

	public DecimalInputSymbolRef(InputSource input,
			OptionParameters optionParameters, Symbols symbols,
			DebugMessages debug, TimeFunction timeOfOptionExecution,
			TimeFunction timeOfStateExecution, List<Action> actions)
			throws IntermediateCodeMalformedException {

		super(debug);

		String name = input.next();

		debug.printlnInit("Creating a reference to decimal input symbol "
				+ name);

		paramAssignment = new ParameterAssignment(debug, null);

		paramAssignment.create(input, optionParameters, symbols,
				timeOfOptionExecution, timeOfStateExecution, actions);

		inputSymbol = symbols.getDecimalInputSymbol(name, paramAssignment
				.getDecimalNames(), paramAssignment.getBooleanNames(),
				paramAssignment.getEnumeratedNames());

		paramAssignment.setRefParameters(inputSymbol.getParameters());

	}

	@Override
	public double getValue() {
		paramAssignment.set();
		return inputSymbol.getValue();
	}

}
