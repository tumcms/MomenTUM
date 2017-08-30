/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabslx.symbols;

import de.xabsl.jxabsl.EngineInitializationException;
import de.xabsl.jxabsl.symbols.BooleanOutputSymbol;
import de.xabsl.jxabsl.utils.DebugMessages;
import de.xabsl.jxabslx.conversions.BooleanConversion;
import de.xabsl.jxabslx.io.Input;
import de.xabsl.jxabslx.io.Output;

/**
 * An implementation for a boolean output symbol. Takes its values from an Input
 * object, writes values via an Output object and converts via a
 * BooleanConversion.
 */

public class BooleanOutputSymbolImpl extends OutputSymbolImpl implements BooleanOutputSymbol {

	private BooleanConversion conversion;

	/**
	 * Constructor
	 * 
	 * @param output
	 *            a value goes here
	 * @param input
	 *            a value comes from here
	 * @param conversion
	 *            is converted via this conversion
	 * @param debug
	 *            for debugging output
	 */
	public BooleanOutputSymbolImpl(Output output, Input input,
			BooleanConversion conversion, DebugMessages debug) {

		super(output, input);
		this.conversion = conversion;
	}

	public boolean getValue() {
		return conversion.from(input.getValue());
	}

	public void setValue(boolean value) {
		output.setValue(conversion.to(value));
	}
}
