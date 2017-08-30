/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabslx.symbols;

import de.xabsl.jxabsl.EngineInitializationException;
import de.xabsl.jxabsl.symbols.EnumeratedOutputSymbol;
import de.xabsl.jxabsl.symbols.Enumeration;
import de.xabsl.jxabsl.utils.DebugMessages;
import de.xabsl.jxabslx.conversions.EnumeratedConversion;
import de.xabsl.jxabslx.io.Input;
import de.xabsl.jxabslx.io.Output;

/**
 * An implementation for an enumerated output symbol. Takes its values from an
 * Input object, writes values via an Output object and converts via an
 * EnumeratedConversion.
 */

public class EnumeratedOutputSymbolImpl extends OutputSymbolImpl implements EnumeratedOutputSymbol {

	private Enumeration enumeration;

	private EnumeratedConversion conversion;

	/**
	 * Constructor
	 * 
	 * @param enumeration
	 *            the enumeration which is the domain of this symbol
	 * @param output
	 *            a value goes here
	 * @param input
	 *            a value comes from here
	 * @param conversion
	 *            is converted via this conversion
	 * @param debug
	 *            for debugging output
	 */

	public EnumeratedOutputSymbolImpl(Enumeration enumeration, Output output,
			Input input, EnumeratedConversion conversion, DebugMessages debug) {

		super(output, input);
		
		this.enumeration = enumeration;
		this.conversion = conversion;
	}

	public Object getValue() {
		return conversion.from(input.getValue());
	}

	public void setValue(Object value) {
		output.setValue(conversion.to(value));
	}

	// (Java 6) @Override
	public Enumeration getEnumeration() {
		return enumeration;
	}
}
