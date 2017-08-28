/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabslx.symbols;

import de.xabsl.jxabsl.EngineInitializationException;
import de.xabsl.jxabsl.symbols.DecimalOutputSymbol;
import de.xabsl.jxabsl.utils.DebugMessages;
import de.xabsl.jxabslx.conversions.DecimalConversion;
import de.xabsl.jxabslx.io.Input;
import de.xabsl.jxabslx.io.Output;

/**
 * An implementation for a decimal output symbol. Takes its values from an Input
 * object, writes values via an Output object and converts via a
 * DecimalConversion.
 */

public class DecimalOutputSymbolImpl extends OutputSymbolImpl implements DecimalOutputSymbol {

	private DecimalConversion conversion;

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

	public DecimalOutputSymbolImpl(Output output, Input input,
			DecimalConversion conversion, DebugMessages debug) {

		super(output, input);

		this.conversion = conversion;

	}

	public double getValue() {
		return conversion.from(input.getValue());
	}

	public void setValue(double value) {
		output.setValue(conversion.to(value));
	}

}
