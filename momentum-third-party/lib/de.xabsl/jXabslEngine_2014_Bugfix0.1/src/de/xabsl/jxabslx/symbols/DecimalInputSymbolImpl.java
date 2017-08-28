/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabslx.symbols;

import de.xabsl.jxabsl.engine.Symbols;
import de.xabsl.jxabsl.symbols.DecimalInputSymbol;
import de.xabsl.jxabsl.utils.DebugMessages;
import de.xabsl.jxabslx.conversions.DecimalConversion;
import de.xabsl.jxabslx.io.Input;

/**
 * An implementation for a decimal input symbol. Takes its values from an Input
 * object and converts it via a DecimalConversion
 */

public class DecimalInputSymbolImpl extends InputSymbolImpl implements
		DecimalInputSymbol {

	private DecimalConversion conversion;

	/**
	 * Constructor.
	 * 
	 * @param input
	 *            A value comes from here
	 * @param conversion
	 *            Is converted via this conversion
	 * @param parameterNames
	 *            parameter names in the correct order
	 * @param symbols
	 *            the symbols of the engine
	 * @param debug
	 *            for debugging output
	 */

	public DecimalInputSymbolImpl(Input input, DecimalConversion conversion,
			String[] parameterNames, Symbols symbols, DebugMessages debug) {
		super(input, parameterNames, symbols, debug);

		this.conversion = conversion;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see xabsl.symbols.IDecimalInputSymbol#getValue()
	 */
	public double getValue() {
		return conversion.from(super.getRawValue());
	}
}
