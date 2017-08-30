/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabslx.symbols;

import de.xabsl.jxabsl.engine.Symbols;
import de.xabsl.jxabsl.symbols.EnumeratedInputSymbol;
import de.xabsl.jxabsl.symbols.Enumeration;
import de.xabsl.jxabsl.utils.DebugMessages;
import de.xabsl.jxabslx.conversions.EnumeratedConversion;
import de.xabsl.jxabslx.io.Input;

/**
 * An implementation for an enumerated input symbol. Takes its values from an
 * Input object and converts it via an EnumeratedConversion
 */

public class EnumeratedInputSymbolImpl extends InputSymbolImpl implements
		EnumeratedInputSymbol {

	private EnumeratedConversion conversion;

	protected Enumeration enumeration;

	/**
	 * Constructor.
	 * 
	 * @param enumeration
	 *            the enumeration which is the domain of this symbol
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

	public EnumeratedInputSymbolImpl(Enumeration enumeration, Input input,
			EnumeratedConversion conversion, String[] parameterNames,
			Symbols symbols, DebugMessages debug) {
		super(input, parameterNames, symbols, debug);

		this.conversion = conversion;

		this.enumeration = enumeration;

	}

	public Object getValue() {
		return conversion.from(super.getRawValue());
	}

	// (Java 6) @Override
	public Enumeration getEnumeration() {
		return enumeration;
	}
}
