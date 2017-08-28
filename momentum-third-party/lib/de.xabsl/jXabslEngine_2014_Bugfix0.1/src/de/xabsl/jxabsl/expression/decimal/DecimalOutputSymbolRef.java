/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabsl.expression.decimal;

import de.xabsl.jxabsl.engine.Symbols;
import de.xabsl.jxabsl.symbols.DecimalOutputSymbol;
import de.xabsl.jxabsl.utils.DebugMessages;
import de.xabsl.jxabsl.utils.InputSource;

/**
 * Represents a reference to a decimal output symbol.
 */

public class DecimalOutputSymbolRef extends DecimalExpression {

	private DecimalOutputSymbol outputSymbol;

	/**
	 * Constructor. Creates the reference depending on the input.
	 * 
	 * @param input
	 *            An input source for the intermediate code. It must be opened
	 *            and read until A position where the function reference starts.
	 * @param debug
	 *            For debugging output
	 * @param symbols
	 *            All available symbols
	 */

	public DecimalOutputSymbolRef(InputSource input, DebugMessages debug,
			Symbols symbols) {

		super(debug);

		String name = input.next();

		outputSymbol = symbols.getDecimalOutputSymbol(name);
	}

	@Override
	public double getValue() {
		return outputSymbol.getValue();

	}

}
