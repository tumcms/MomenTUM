/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabsl.expression.decimal;

import java.util.List;
import java.util.NoSuchElementException;

import de.xabsl.jxabsl.IntermediateCodeMalformedException;
import de.xabsl.jxabsl.TimeFunction;
import de.xabsl.jxabsl.action.Action;
import de.xabsl.jxabsl.behavior.OptionParameters;
import de.xabsl.jxabsl.engine.Symbols;
import de.xabsl.jxabsl.utils.DebugMessages;
import de.xabsl.jxabsl.utils.InputSource;

/**
 * Base class for all decimal expressions in the option graph.
 */
public abstract class DecimalExpression {

	protected DebugMessages debug;

	public DecimalExpression(DebugMessages debug) {

		this.debug = debug;
	}

	/** Return the value of the expression */

	public abstract double getValue();

	/**
	 * Creates a decimal expression depending on the input.
	 * 
	 * @param input
	 *            An input source for the intermediate code. It must be opened
	 *            and read until A position where a decimal expression starts.
	 * @param actions
	 *            The subsequent behaviors i.e options and basic behaviors of
	 *            the state.
	 * @param debug
	 *            For debugging information
	 * @param parameters
	 *            The parameters of the option
	 * @param symbols
	 *            All available symbols
	 * @param timeOfOptionExecution
	 *            The time how long the option is already active
	 * @param timeOfStateExecution
	 *            The time how long the state is already active
	 */

	public static DecimalExpression create(InputSource input,
			List<Action> actions, DebugMessages debug,
			OptionParameters parameters, Symbols symbols,
			TimeFunction timeOfOptionExecution, TimeFunction timeOfStateExecution)
			throws IntermediateCodeMalformedException {

		String token;

		try {

			token = input.next();
		} catch (NoSuchElementException e) {
			throw new IntermediateCodeMalformedException(
					IntermediateCodeMalformedException.UNEXPECTED_END, e);
		}

		switch (token.charAt(0)) {

		case 'i':
			return new DecimalInputSymbolRef(input, parameters, symbols, debug,
					timeOfOptionExecution, timeOfStateExecution, actions);
		case 'o':
			return new DecimalOutputSymbolRef(input, debug, symbols);
		case 'c':
			// constants are treated as values (there is no difference from the
			// engine's point of view
		case 'v':
			return new DecimalValue(input, debug);

		case 'p':
			return new DecimalOptionParameterRef(input, debug, parameters);

		case '+': {
			DecimalExpression decimalOperand1 = DecimalExpression.create(input,
					actions, debug, parameters, symbols, timeOfOptionExecution,
					timeOfStateExecution);
			DecimalExpression decimalOperand2 = DecimalExpression.create(input,
					actions, debug, parameters, symbols, timeOfOptionExecution,
					timeOfStateExecution);

			debug.printlnInit("Creating: + operator");

			PlusOperator plusOperator = new PlusOperator(debug);

			plusOperator.create(decimalOperand1, decimalOperand2);

			return plusOperator;
		}

		case '-': {
			DecimalExpression decimalOperand1 = DecimalExpression.create(input,
					actions, debug, parameters, symbols, timeOfOptionExecution,
					timeOfStateExecution);
			DecimalExpression decimalOperand2 = DecimalExpression.create(input,
					actions, debug, parameters, symbols, timeOfOptionExecution,
					timeOfStateExecution);

			debug.printlnInit("Creating: - operator");

			MinusOperator minusOperator = new MinusOperator(debug);

			minusOperator.create(decimalOperand1, decimalOperand2);

			return minusOperator;

		}
		case '*': {
			DecimalExpression decimalOperand1 = DecimalExpression.create(input,
					actions, debug, parameters, symbols, timeOfOptionExecution,
					timeOfStateExecution);
			DecimalExpression decimalOperand2 = DecimalExpression.create(input,
					actions, debug, parameters, symbols, timeOfOptionExecution,
					timeOfStateExecution);

			debug.printlnInit("Creating: * operator");

			MultiplyOperator multiplyOperator = new MultiplyOperator(debug);

			multiplyOperator.create(decimalOperand1, decimalOperand2);

			return multiplyOperator;

		}
		case 'd': {
			DecimalExpression decimalOperand1 = DecimalExpression.create(input,
					actions, debug, parameters, symbols, timeOfOptionExecution,
					timeOfStateExecution);
			DecimalExpression decimalOperand2 = DecimalExpression.create(input,
					actions, debug, parameters, symbols, timeOfOptionExecution,
					timeOfStateExecution);

			debug.printlnInit("Creating: / operator");

			DivideOperator divideOperator = new DivideOperator(debug);

			divideOperator.create(decimalOperand1, decimalOperand2);

			return divideOperator;

		}
		case '%': {
			DecimalExpression decimalOperand1 = DecimalExpression.create(input,
					actions, debug, parameters, symbols, timeOfOptionExecution,
					timeOfStateExecution);
			DecimalExpression decimalOperand2 = DecimalExpression.create(input,
					actions, debug, parameters, symbols, timeOfOptionExecution,
					timeOfStateExecution);

			debug.printlnInit("Creating: % operator");

			ModOperator modOperator = new ModOperator(debug);

			modOperator.create(decimalOperand1, decimalOperand2);

			return modOperator;

		}
		case 's':
			return new TimeRef(debug, timeOfStateExecution);
		case 't':
			return new TimeRef(debug, timeOfOptionExecution);
		case 'q':
			return new ConditionalDecimalExpression(input, actions, debug,
					parameters, symbols, timeOfOptionExecution,
					timeOfStateExecution);
		default:
			throw new IntermediateCodeMalformedException(
					"Expected: Token to determine type; one of: i o c v p + - * d % s t q");

		}

	}

}
