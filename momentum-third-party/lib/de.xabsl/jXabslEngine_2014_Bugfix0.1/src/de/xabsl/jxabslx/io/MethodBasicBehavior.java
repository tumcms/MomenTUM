/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabslx.io;

import java.lang.reflect.Method;

import de.xabsl.jxabsl.behavior.BasicBehavior;
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
import de.xabsl.jxabslx.utils.ClassUtils;
import de.xabsl.jxabslx.utils.ClassUtils.XabslType;

/**
 * A basic behavior that executes a java method.
 */
public class MethodBasicBehavior extends BasicBehavior {

	private MethodAccess methodAccess;

	/**
	 * Store the parameters to call the method with
	 */
	private Object[] methodParameters;

	public MethodBasicBehavior(String name, Method method,
			String[] parameterNames, Object container, Symbols symbols,
			DebugMessages debug) {
		super(name, debug);

		methodAccess = new MethodAccess(method, container) {
		};

		methodParameters = new Object[method.getParameterTypes().length];

		createParameters(parameterNames, symbols, debug);
	}

	@Override
	public void execute() {

		methodAccess.invokeMethod(methodParameters);

	}

	protected void createParameters(String[] parameterNames, Symbols symbols,
			DebugMessages debug) {

		parameters = new Parameters(debug);

		final Class<?>[] parameterTypes = methodAccess.getMethod()
				.getParameterTypes();

		for (int i = 0; i < parameterTypes.length; i++) {

			// we guess which XABSL-Type

			Class<?> type = parameterTypes[i];

			XabslType xabslType = Conversions.javaTypeToXabslType(ClassUtils
					.wrapperTypeForPrimitive(type));

			final int pos = i;

			switch (xabslType) {

			case bool: {

				BooleanParameter p = new BooleanParameter() {

					private OutputToObjectArray out = new OutputToObjectArray(
							methodParameters, pos);
					private BooleanConversion conversion = Conversions
							.getBooleanConversion(parameterTypes[pos]);

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
							methodParameters, pos);
					private DecimalConversion conversion = Conversions
							.getDecimalConversion(parameterTypes[pos]);

					public void set(double value) {
						out.setValue(conversion.to(value));
					}

				};

				parameters.registerDecimal(parameterNames[i], p);
				break;
			}

			case enumerated: {

				final EnumeratedConversion ec = Conversions
						.getEnumeratedConversion(parameterTypes[pos]);

				EnumeratedParameter p = new EnumeratedParameter() {

					private OutputToObjectArray out = new OutputToObjectArray(
							methodParameters, pos);
					private EnumeratedConversion conversion = ec;

					public void set(Object value) {
						out.setValue(conversion.to(value));
					}

				};

				parameters.registerEnumerated(parameterNames[i], symbols
						.getEnumeration(ec.getEnumerationName(type)), p);
				break;
			}

			case none:  {
				
				// TODO Throw exception
			}
			}
		}
	}

}
