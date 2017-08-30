/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabsl.expression.bool;

import de.xabsl.jxabsl.engine.Symbols;
import de.xabsl.jxabsl.symbols.BooleanOutputSymbol;
import de.xabsl.jxabsl.utils.DebugMessages;
import de.xabsl.jxabsl.utils.InputSource;

/**
 * Represents a reference to a boolean output symbol.
 */

public class BooleanOutputSymbolRef extends BooleanExpression {

	private BooleanOutputSymbol outputSymbol;

	/**
	 * Constructor. Creates the expression depending on the input.
	 * 
	 * @param input
	 *            An input source for the intermediate code. It must be opened
	 *            and read until A position where the function reference starts.
	 * @param debug
	 *            For debuggging output
	 * @param symbols
	 *            All available symbols
	 */

	public BooleanOutputSymbolRef(InputSource input, DebugMessages debug,
			Symbols symbols) {

		super(debug);

		String name = input.next();

		outputSymbol = symbols.getBooleanOutputSymbol(name);
	}

	@Override
	public boolean getValue() {
		return outputSymbol.getValue();

	}

}
