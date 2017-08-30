/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabsl.expression.decimal;

import java.util.InputMismatchException;
import java.util.NoSuchElementException;

import de.xabsl.jxabsl.IntermediateCodeMalformedException;
import de.xabsl.jxabsl.utils.DebugMessages;
import de.xabsl.jxabsl.utils.InputSource;

/**
 * Represents a literal decimal value.
 */

public class DecimalValue extends DecimalExpression {

	private double value;

	/**
	 * Constructor. Creates an expression for a fixed decimal value
	 * 
	 * @param value
	 *            The decimal value
	 */

	public DecimalValue(double value, DebugMessages debug) {
		super(debug);
		this.value = value;
		debug.printlnInit("Created: " + this);

	}

	/**
	 * Constructor. Creates the value
	 * 
	 * @param input
	 *            An input source for the intermediate code. It must be opened
	 *            and read until A position where a value starts.
	 * @param debug
	 *            For debugging output
	 */

	public DecimalValue(InputSource input, DebugMessages debug)
			throws IntermediateCodeMalformedException {
		super(debug);

		try {
			value = input.nextDouble();
		} catch (InputMismatchException e) {
			throw new IntermediateCodeMalformedException(
					"Expected token: decimal value", e);

		} catch (NoSuchElementException e) {

			throw new IntermediateCodeMalformedException(
					IntermediateCodeMalformedException.UNEXPECTED_END, e);
		}

		debug.printlnInit("Created: " + this);

	}

	@Override
	public double getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "DecimalValue[" + value + "]";
	}

}
