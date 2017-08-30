/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabslx.symbols;

import de.xabsl.jxabsl.EngineInitializationException;
import de.xabsl.jxabsl.engine.SymbolNotRegisteredException;
import de.xabsl.jxabsl.engine.Symbols;
import de.xabsl.jxabsl.parameters.BooleanParameter;
import de.xabsl.jxabsl.parameters.DecimalParameter;
import de.xabsl.jxabsl.parameters.EnumeratedParameter;
import de.xabsl.jxabsl.parameters.Parameters;
import de.xabsl.jxabsl.utils.DebugMessages;
import de.xabsl.jxabslx.conversions.BooleanConversion;
import de.xabsl.jxabslx.conversions.Conversions;
import de.xabsl.jxabslx.conversions.DecimalConversion;
import de.xabsl.jxabslx.conversions.EnumeratedConversion;
import de.xabsl.jxabslx.io.Input;
import de.xabsl.jxabslx.io.OutputToObjectArray;
import de.xabsl.jxabslx.utils.ClassUtils;
import de.xabsl.jxabslx.utils.ClassUtils.XabslType;

/**
 * Base class for input symbol implementations
 */
public abstract class InputSymbolImpl {

	protected Parameters parameters;

	protected final Input input;

	public InputSymbolImpl(Input input, String[] parameterNames,
			Symbols symbols, DebugMessages debug) {

		this.input = input;

		checkConstructorArguments(input, parameterNames);

		createParameters(parameterNames, symbols, debug);

	}

	public Parameters getParameters() {
		return parameters;
	}

	protected void checkConstructorArguments(Input input,
			String[] parameterNames) {
		if (input.getParameters().length != parameterNames.length) {
			throw new EngineInitializationException(
					"Number of parameter names and actual parameters does not match for "
							+ input);

		}

		if (input.getParameters().length != input.getParamTypes().length) {
			throw new EngineInitializationException(
					"Number of parameter types and parameters does not match for "
							+ input);

		}
	}

	protected void createParameters(String[] parameterNames, Symbols symbols,
			DebugMessages debug) {
		parameters = new Parameters(debug);
		for (int i = 0; i < input.getParamTypes().length; i++) {

			// we guess which XABSL-Type

			Class<?> type = input.getParamTypes()[i];
			XabslType xabslType = Conversions.javaTypeToXabslType(ClassUtils
					.wrapperTypeForPrimitive(type));

			final int pos = i;

			switch (xabslType) {

			case bool: {

				BooleanParameter p = new BooleanParameter() {

					private OutputToObjectArray out = new OutputToObjectArray(
							input.getParameters(), pos);
					private BooleanConversion conversion = Conversions
							.getBooleanConversion(input.getParamTypes()[pos]);

					public void set(boolean value) {
						out.setValue(conversion.to(value));
					}

				};

				parameters.registerBoolean(parameterNames[i], p);
				break;
			}

			case decimal: {

				DecimalParameter p = new DecimalParameter() {

					private OutputToObjectArray out = new OutputToObjectArray(
							input.getParameters(), pos);
					private DecimalConversion conversion = Conversions
							.getDecimalConversion(input.getParamTypes()[pos]);

					public void set(double value) {
						out.setValue(conversion.to(value));
					}

				};

				parameters.registerDecimal(parameterNames[i], p);
				break;
			}

			case enumerated: {

				final EnumeratedConversion ec = Conversions
						.getEnumeratedConversion(input.getParamTypes()[pos]);

				EnumeratedParameter p = new EnumeratedParameter() {

					private OutputToObjectArray out = new OutputToObjectArray(
							input.getParameters(), pos);
					private EnumeratedConversion conversion = ec;

					public void set(Object value) {
						out.setValue(conversion.to(value));
					}

				};

				parameters.registerEnumerated(parameterNames[i], symbols
						.getEnumeration(ec.getEnumerationName(type)), p);
				break;
			}

			default:
				throw new SymbolNotRegisteredException("The type "
						+ input.getParamTypes()[i]
						+ " does not match boolean, decimal, or enumerated");
			}
		}
	}

	/**
	 * Gets the value as any object which must be converted properly. Make sure
	 * the types match.
	 */
	protected Object getRawValue() {
		return input.getValue();
	}

}