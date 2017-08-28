/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabsl.expression.enumerated;

import de.xabsl.jxabsl.behavior.OptionParameters;
import de.xabsl.jxabsl.engine.SymbolNotRegisteredException;
import de.xabsl.jxabsl.symbols.Enumeration;
import de.xabsl.jxabsl.utils.DebugMessages;
import de.xabsl.jxabsl.utils.InputSource;

/**
 * Represents a reference to a enumerated option parameter.
 */

public class EnumeratedOptionParameterRef extends EnumeratedExpression {

	private int parameterPos;
	private OptionParameters parameters;

	/**
	 * Constructor. Creates the reference
	 * 
	 * @param enumeration
	 *            A reference to the enumeration which is the domain of this
	 *            expression
	 * @param input
	 *            An input source for the intermediate code. It must be opened
	 *            and read until A position where the expression starts.
	 * @param debug
	 *            For debugging output
	 * @param parameters
	 *            The parameters of the option
	 */

	public EnumeratedOptionParameterRef(Enumeration enumeration,
			InputSource input, DebugMessages debug, OptionParameters parameters) {

		super(debug);

		String parameterName = input.next();

		debug
				.printlnInit("Creating a reference to enumerated option parameter "
						+ parameterName);

		this.parameters = parameters;

		this.enumeration = parameters.getEnumeration(parameterName);

		if (enumeration != null && enumeration != this.enumeration) {
			throw new SymbolNotRegisteredException("Option parameter "
					+ parameterName + " does not match enumeration type "
					+ enumeration);
		}

		parameterPos = parameters.getEnumeratedPosition(parameterName);

	}

	@Override
	public Object getValue() {
		return parameters.getEnumerated(parameterPos);
	}

}
