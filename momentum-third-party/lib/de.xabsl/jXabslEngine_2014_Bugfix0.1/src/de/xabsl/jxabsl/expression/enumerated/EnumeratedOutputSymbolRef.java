/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabsl.expression.enumerated;

import de.xabsl.jxabsl.engine.SymbolNotRegisteredException;
import de.xabsl.jxabsl.engine.Symbols;
import de.xabsl.jxabsl.symbols.EnumeratedOutputSymbol;
import de.xabsl.jxabsl.symbols.Enumeration;
import de.xabsl.jxabsl.utils.DebugMessages;
import de.xabsl.jxabsl.utils.InputSource;

/**
 * Represents a reference to a enumerated input symbol.
 */

public class EnumeratedOutputSymbolRef extends EnumeratedExpression {

	private EnumeratedOutputSymbol outputSymbol;

	/**
	 * Constructor. Creates the function call depending on the input.
	 * 
	 * @param enumeration
	 *            A reference to the enumeration which is the domain of this
	 *            expression
	 * @param input
	 *            An input source for the intermediate code. It must be opened
	 *            and read until A position where the function reference starts.
	 * @param debug
	 *            For debugging output
	 * @param symbols
	 *            All available symbols
	 */

	public EnumeratedOutputSymbolRef(Enumeration enumeration,
			InputSource input, DebugMessages debug, Symbols symbols) {

		super(debug);

		String name = input.next();

		outputSymbol = symbols.getEnumeratedOutputSymbol(name);

		this.enumeration = outputSymbol.getEnumeration();

		if (enumeration != null && enumeration != this.enumeration)
			throw new SymbolNotRegisteredException("Enumeration output symbol"
					+ this.enumeration + " does not match enumeration type "
					+ enumeration);

	}

	@Override
	public Object getValue() {
		return outputSymbol.getValue();

	}

	public Enumeration getEnumeration() {
		return enumeration;
	}
}
