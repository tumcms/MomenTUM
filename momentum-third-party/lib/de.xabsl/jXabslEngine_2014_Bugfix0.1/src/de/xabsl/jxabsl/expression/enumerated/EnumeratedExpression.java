/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabsl.expression.enumerated;

import java.util.List;
import java.util.NoSuchElementException;

import de.xabsl.jxabsl.IntermediateCodeMalformedException;
import de.xabsl.jxabsl.TimeFunction;
import de.xabsl.jxabsl.action.Action;
import de.xabsl.jxabsl.behavior.OptionParameters;
import de.xabsl.jxabsl.engine.Symbols;
import de.xabsl.jxabsl.symbols.Enumeration;
import de.xabsl.jxabsl.utils.DebugMessages;
import de.xabsl.jxabsl.utils.InputSource;

/**
 * Base class for all enumerated expressions in the option graph.
 */

public abstract class EnumeratedExpression {

	protected DebugMessages debug;

	protected Enumeration enumeration;

	protected EnumeratedExpression(DebugMessages debug) {

		this.debug = debug;
	}

	public abstract Object getValue();

	/**
	 * Creates an enumerated expression depending on the input.
	 * 
	 * @param enumeration
	 *            A reference to the enumeration which is the domain of this
	 *            expression
	 * @param input
	 *            An input source for the intermediate code. It must be opened
	 *            and read until A position where a enumerated expression
	 *            starts.
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

	public static EnumeratedExpression create(Enumeration enumeration,
			InputSource input, List<Action> actions, DebugMessages debug,
			OptionParameters parameters, Symbols symbols,
			TimeFunction timeOfOptionExecution,
			TimeFunction timeOfStateExecution)
			throws IntermediateCodeMalformedException {

		String token;

		try {
			token = input.next();
		} catch (NoSuchElementException e) {
			throw new IntermediateCodeMalformedException(
					IntermediateCodeMalformedException.UNEXPECTED_END, e);
		}

		switch (token.charAt(0)) {

		case 'v':
			return new EnumeratedValue(enumeration, input, symbols, debug);
		case 'p':
			return new EnumeratedOptionParameterRef(enumeration, input, debug,
					parameters);
		case 'i':
			return new EnumeratedInputSymbolRef(enumeration, input, parameters,
					symbols, debug, timeOfOptionExecution,
					timeOfStateExecution, actions);
		case 'o':
			return new EnumeratedOutputSymbolRef(enumeration, input, debug,
					symbols);
		case 'q':
			return new ConditionalEnumeratedExpression(enumeration, input,
					actions, debug, parameters, symbols, timeOfOptionExecution,
					timeOfStateExecution);
		default:
			throw new IntermediateCodeMalformedException(
					"Expected: Token to determine type; one of: v p i o q)");
		}

	}

	/**
	 * Extract "enumeration" from a name like "enumeration.element"
	 */
	public static String enumerationFromIntermediateCode(String name) {

		if (name.matches("^.+?\\..+"))
			return name.substring(0, name.lastIndexOf("."));
		else
			throw new IllegalArgumentException("Enum element \"" + name + "\""
					+ " does not match the form enumeration.element.");

	}

	/**
	 * Extract "element" from a name like "enumeration.element"
	 */

	public static String elementFromIntermediateCode(String name) {

		if (name.matches("^.+?\\..+"))
			return name.substring(name.lastIndexOf(".") + 1, name.length());
		else
			throw new IllegalArgumentException("Enum element \"" + name
					+ "\" does not match the form enumeration.element.");

	}

	/**
	 * 
	 * @return The expression that is the domain of the enumeration
	 */
	public Enumeration getEnumeration() {
		return enumeration;
	}
}
