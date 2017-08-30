/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabsl.expression.enumerated;

import java.util.List;

import de.xabsl.jxabsl.IntermediateCodeMalformedException;
import de.xabsl.jxabsl.TimeFunction;
import de.xabsl.jxabsl.action.Action;
import de.xabsl.jxabsl.behavior.OptionParameters;
import de.xabsl.jxabsl.engine.SymbolNotRegisteredException;
import de.xabsl.jxabsl.engine.Symbols;
import de.xabsl.jxabsl.parameters.ParameterAssignment;
import de.xabsl.jxabsl.symbols.EnumeratedInputSymbol;
import de.xabsl.jxabsl.symbols.Enumeration;
import de.xabsl.jxabsl.utils.DebugMessages;
import de.xabsl.jxabsl.utils.InputSource;

/**
 * Represents an 'enumerated-input-symbol-ref' expression of the option graph
 */
public class EnumeratedInputSymbolRef extends EnumeratedExpression {

	private EnumeratedInputSymbol inputSymbol;
	private ParameterAssignment paramAssignment;

	/**
	 * Constructor. Creates the element
	 * 
	 * @param enumeration
	 *            A reference to the enumeration which is the domain of this
	 *            expression
	 * @param input
	 *            An input source for the intermediate code. It must be opened
	 *            and read until A position where a expression starts.
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

	public EnumeratedInputSymbolRef(Enumeration enumeration, InputSource input,
			OptionParameters optionParameters, Symbols symbols,
			DebugMessages debug, TimeFunction timeOfOptionExecution,
			TimeFunction timeOfStateExecution, List<Action> actions)
			throws IntermediateCodeMalformedException {

		super(debug);

		String name = input.next();

		debug.printlnInit("Creating a reference to enumerated input symbol "
				+ name);

		paramAssignment = new ParameterAssignment(debug, null);

		paramAssignment.create(input, optionParameters, symbols,
				timeOfOptionExecution, timeOfStateExecution, actions);

		inputSymbol = symbols.getEnumeratedInputSymbol(name, paramAssignment
				.getDecimalNames(), paramAssignment.getBooleanNames(),
				paramAssignment.getEnumeratedNames());

		this.enumeration = inputSymbol.getEnumeration();

		if (enumeration != null && enumeration != this.enumeration)
			throw new SymbolNotRegisteredException("Enumeration input symbol "
					+ this.enumeration + " does not match enumeration type "
					+ enumeration);

		paramAssignment.setRefParameters(inputSymbol.getParameters());

	}

	@Override
	public Object getValue() {
		paramAssignment.set();
		return inputSymbol.getValue();
	}

}
