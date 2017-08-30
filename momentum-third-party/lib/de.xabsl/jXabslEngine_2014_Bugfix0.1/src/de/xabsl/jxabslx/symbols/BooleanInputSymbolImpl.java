/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabslx.symbols;

import de.xabsl.jxabsl.engine.Symbols;
import de.xabsl.jxabsl.symbols.BooleanInputSymbol;
import de.xabsl.jxabsl.utils.DebugMessages;
import de.xabsl.jxabslx.conversions.BooleanConversion;
import de.xabsl.jxabslx.io.Input;

/**
 * An implementation for a boolean input symbol. Takes its values from an Input
 * object and converts it via a BooleanConversion
 */
public class BooleanInputSymbolImpl extends InputSymbolImpl implements
		BooleanInputSymbol {

	private BooleanConversion conversion;

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
	public BooleanInputSymbolImpl(Input input, BooleanConversion conversion,
			String[] parameterNames, Symbols symbols, DebugMessages debug) {
		super(input, parameterNames, symbols, debug);

		this.conversion = conversion;
	}

	public boolean getValue() {
		return conversion.from(super.getRawValue());
	}

}
