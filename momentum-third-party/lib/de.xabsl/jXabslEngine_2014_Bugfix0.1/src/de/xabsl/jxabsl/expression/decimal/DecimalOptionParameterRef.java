/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabsl.expression.decimal;

import de.xabsl.jxabsl.behavior.OptionParameters;
import de.xabsl.jxabsl.utils.DebugMessages;
import de.xabsl.jxabsl.utils.InputSource;

/**
 * Represents a reference to a decimal option parameter.
 */

public class DecimalOptionParameterRef extends DecimalExpression {

	private int parameterPos;
	private OptionParameters parameters;

	/**
	 * Constructor. Creates the reference
	 * 
	 * @param input
	 *            An input source for the intermediate code. It must be opened
	 *            and read until A position where the expression starts.
	 * @param debug
	 *            For debugging output
	 * @param parameters
	 *            The parameters of the option
	 */

	public DecimalOptionParameterRef(InputSource input, DebugMessages debug,
			OptionParameters parameters) {
		super(debug);
		this.parameters = parameters;
		String parameterName = input.next();
		debug.printlnInit("Creating a reference to decimal option parameter "
				+ parameterName);

		parameterPos = parameters.getDecimalPosition(parameterName);

	}

	@Override
	public double getValue() {
		return parameters.getDecimal(parameterPos);

	}

}
