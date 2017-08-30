/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabsl.expression.bool;

import java.util.InputMismatchException;
import java.util.NoSuchElementException;

import de.xabsl.jxabsl.IntermediateCodeMalformedException;
import de.xabsl.jxabsl.utils.DebugMessages;
import de.xabsl.jxabsl.utils.InputSource;

/**
 * Represents a literal boolean value.
 */

public class BooleanValue extends BooleanExpression {

	private boolean value;

	/**
	 * Constructor. Creates an expression for a fixed boolean value
	 * 
	 * @param value
	 *            The boolean value
	 * @param debug
	 *            For debugging output
	 * 
	 */

	public BooleanValue(boolean value, DebugMessages debug) {
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
	public BooleanValue(InputSource input, DebugMessages debug)
			throws IntermediateCodeMalformedException {
		super(debug);
		try {
			value = input.nextBoolean();
		} catch (InputMismatchException e) {
			throw new IntermediateCodeMalformedException(
					"Expected token: boolean value", e);

		} catch (NoSuchElementException e) {

			throw new IntermediateCodeMalformedException(
					IntermediateCodeMalformedException.UNEXPECTED_END, e);
		}
		/**
		 * 
		 */

		debug.printlnInit("Created: " + this);

	}

	@Override
	public boolean getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "BooleanValue[" + value + "]";
	}

}
