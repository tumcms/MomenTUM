/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabsl.expression.bool;

import java.util.InputMismatchException;
import java.util.List;
import java.util.NoSuchElementException;

import de.xabsl.jxabsl.IntermediateCodeMalformedException;
import de.xabsl.jxabsl.TimeFunction;
import de.xabsl.jxabsl.action.Action;
import de.xabsl.jxabsl.behavior.OptionParameters;
import de.xabsl.jxabsl.engine.Symbols;
import de.xabsl.jxabsl.expression.decimal.DecimalExpression;
import de.xabsl.jxabsl.utils.DebugMessages;
import de.xabsl.jxabsl.utils.InputSource;

/**
 * Base class for all boolean expressions in the option graph.
 * 
 */
public abstract class BooleanExpression {

	protected DebugMessages debug;

	protected BooleanExpression(DebugMessages debug) {

		this.debug = debug;
	}

	/**
	 * Returns the value of the boolean expression.
	 * 
	 * @return the value of the boolean expression
	 */
	public abstract boolean getValue();

	/**
	 * Creates a boolean expression from an input source.
	 * 
	 * @param input
	 *            An input source for the intermediate code. It must be opened
	 *            and read until A position where a boolean expression starts.
	 * @param actions
	 *            The subsequent behaviors i.e options and basic behaviors of
	 *            the state.
	 * @param debug
	 *            For debuggin output
	 * @param parameters
	 *            The parameters of the option
	 * @param symbols
	 *            All available symbols
	 * @param timeOfOptionExecution
	 *            The time how long the option is already active
	 * @param timeOfStateExecution
	 *            The time how long the state is already active
	 */

	public static BooleanExpression create(InputSource input,
			List<Action> actions, DebugMessages debug,
			OptionParameters parameters, Symbols symbols,
			TimeFunction timeOfOptionExecution,
			TimeFunction timeOfStateExecution)
			throws IntermediateCodeMalformedException {

		String token;

		try {
			token = input.next();
		} catch (NoSuchElementException e) {
			throw new IntermediateCodeMalformedException(
					IntermediateCodeMalformedException.UNEXPECTED_END, e);
		}

		switch (token.charAt(0)) {

		case 'v':
			return new BooleanValue(input, debug);
		case 'p':
			return new BooleanOptionParameterRef(input, debug, parameters);

		case 'i':
			return new BooleanInputSymbolRef(input, parameters, symbols, debug,
					timeOfOptionExecution, timeOfStateExecution, actions);
		case 'o':
			return new BooleanOutputSymbolRef(input, debug, symbols);
		case 't':
			return new SubsequentOptionReachedTargetStateCondition(actions,
					debug);
		case 'c':
			return new EnumeratedExpressionComparison(input, actions, debug,
					parameters, symbols, timeOfOptionExecution,
					timeOfStateExecution);
		case '&': {
			debug.printlnInit("Creating: AND operator");

			int numberOfOperands;
			try {
				numberOfOperands = input.nextInt();
			} catch (InputMismatchException e) {
				throw new IntermediateCodeMalformedException(
						"Expected: number of operands (int token)", e);
			} catch (NoSuchElementException e) {
				throw new IntermediateCodeMalformedException(
						IntermediateCodeMalformedException.UNEXPECTED_END, e);
			}
			AndOperator andOperator = new AndOperator(debug);

			for (int i = 0; i < numberOfOperands; i++)
				andOperator.addOperand(create(input, actions, debug,
						parameters, symbols, timeOfOptionExecution,
						timeOfStateExecution));

			return andOperator;

		}
		case '|': {
			debug.printlnInit("Creating: OR operator");

			int numberOfOperands;
			try {
				numberOfOperands = input.nextInt();
			} catch (InputMismatchException e) {
				throw new IntermediateCodeMalformedException(
						"Expected: number of operands (int token)", e);
			} catch (NoSuchElementException e) {
				throw new IntermediateCodeMalformedException(
						IntermediateCodeMalformedException.UNEXPECTED_END, e);
			}

			OrOperator orOperator = new OrOperator(debug);

			for (int i = 0; i < numberOfOperands; i++) {
				orOperator.addOperand(BooleanExpression.create(input, actions,
						debug, parameters, symbols, timeOfOptionExecution,
						timeOfStateExecution));
			}
			return orOperator;
		}

		case '!':
			debug.printlnInit("Creating: NOT operator");

			return new NotOperator(create(input, actions, debug, parameters,
					symbols, timeOfOptionExecution, timeOfStateExecution),
					debug);
		case '=': {
			debug.printlnInit("Creating: == operator");

			DecimalExpression decimalOperand1 = DecimalExpression.create(input,
					actions, debug, parameters, symbols, timeOfOptionExecution,
					timeOfStateExecution);

			DecimalExpression decimalOperand2 = DecimalExpression.create(input,
					actions, debug, parameters, symbols, timeOfOptionExecution,
					timeOfStateExecution);

			EqualToOperator equalToOperator = new EqualToOperator(debug);
			equalToOperator.create(decimalOperand1, decimalOperand2);

			return equalToOperator;
		}

		case 'n': {
			debug.printlnInit("Creating: != operator");

			DecimalExpression decimalOperand1 = DecimalExpression.create(input,
					actions, debug, parameters, symbols, timeOfOptionExecution,
					timeOfStateExecution);

			DecimalExpression decimalOperand2 = DecimalExpression.create(input,
					actions, debug, parameters, symbols, timeOfOptionExecution,
					timeOfStateExecution);

			NotEqualToOperator notEqualToOperator = new NotEqualToOperator(
					debug);
			notEqualToOperator.create(decimalOperand1, decimalOperand2);

			return notEqualToOperator;

		}

		case '<': {
			debug.printlnInit("Creating: < operator");

			DecimalExpression decimalOperand1 = DecimalExpression.create(input,
					actions, debug, parameters, symbols, timeOfOptionExecution,
					timeOfStateExecution);

			DecimalExpression decimalOperand2 = DecimalExpression.create(input,
					actions, debug, parameters, symbols, timeOfOptionExecution,
					timeOfStateExecution);

			LessThanOperator lessThanOperator = new LessThanOperator(debug);
			lessThanOperator.create(decimalOperand1, decimalOperand2);

			return lessThanOperator;

		}

		case 'l': {
			debug.printlnInit("Creating: <= operator");

			DecimalExpression decimalOperand1 = DecimalExpression.create(input,
					actions, debug, parameters, symbols, timeOfOptionExecution,
					timeOfStateExecution);

			DecimalExpression decimalOperand2 = DecimalExpression.create(input,
					actions, debug, parameters, symbols, timeOfOptionExecution,
					timeOfStateExecution);

			LessThanOrEqualToOperator lessThanOrEqualToOperator = new LessThanOrEqualToOperator(
					debug);
			lessThanOrEqualToOperator.create(decimalOperand1, decimalOperand2);

			return lessThanOrEqualToOperator;

		}

		case '>': {
			debug.printlnInit("Creating: > operator");

			DecimalExpression decimalOperand1 = DecimalExpression.create(input,
					actions, debug, parameters, symbols, timeOfOptionExecution,
					timeOfStateExecution);

			DecimalExpression decimalOperand2 = DecimalExpression.create(input,
					actions, debug, parameters, symbols, timeOfOptionExecution,
					timeOfStateExecution);

			GreaterThanOperator greaterThanOperator = new GreaterThanOperator(
					debug);
			greaterThanOperator.create(decimalOperand1, decimalOperand2);

			return greaterThanOperator;

		}
		case 'g': {
			debug.printlnInit("Creating: >= operator");

			DecimalExpression decimalOperand1 = DecimalExpression.create(input,
					actions, debug, parameters, symbols, timeOfOptionExecution,
					timeOfStateExecution);

			DecimalExpression decimalOperand2 = DecimalExpression.create(input,
					actions, debug, parameters, symbols, timeOfOptionExecution,
					timeOfStateExecution);

			GreaterThanOrEqualToOperator greaterThanOrEqualToOperator = new GreaterThanOrEqualToOperator(
					debug);
			greaterThanOrEqualToOperator.create(decimalOperand1,
					decimalOperand2);

			return greaterThanOrEqualToOperator;

		}

		default:
			throw new IntermediateCodeMalformedException(
					"Expected: Token to determine type; one of: v p i o t c & | ! = n < l > g )");

		}

	}

}
