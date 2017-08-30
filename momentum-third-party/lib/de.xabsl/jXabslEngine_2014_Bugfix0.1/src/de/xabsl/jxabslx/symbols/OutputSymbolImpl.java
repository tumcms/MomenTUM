package de.xabsl.jxabslx.symbols;

import de.xabsl.jxabsl.EngineInitializationException;
import de.xabsl.jxabslx.io.Input;
import de.xabsl.jxabslx.io.Output;

public class OutputSymbolImpl {

	protected Output output;
	protected Input input;

	public OutputSymbolImpl(Output output, Input input) {
		
		this.output = output;
		this.input = input;
		
		if (input.getParameters().length != 0) {
			throw new EngineInitializationException(
					"Number of parameters must be 0: An output symbol does not have parameters");
		}
	}
}
