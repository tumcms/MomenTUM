/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */
package de.xabsl.jxabslx.conversions;

import java.util.HashMap;
import java.util.Map;

import de.xabsl.jxabsl.EngineInitializationException;
import de.xabsl.jxabslx.conversions.impl.BooleanToBooleanConversion;
import de.xabsl.jxabslx.conversions.impl.DecimalToDoubleConversion;
import de.xabsl.jxabslx.conversions.impl.DecimalToFloatConversion;
import de.xabsl.jxabslx.conversions.impl.DecimalToIntegerConversion;
import de.xabsl.jxabslx.conversions.impl.DecimalToLongConversion;
import de.xabsl.jxabslx.conversions.impl.EnumeratedToEnumConversion;
import de.xabsl.jxabslx.utils.ClassUtils;
import de.xabsl.jxabslx.utils.ClassUtils.XabslType;

/**
 * Registers and provides conversions between native java data types and XABSL
 * data types
 */
public class Conversions {

	private static Map<Class<?>, BooleanConversion> booleanConversions = new HashMap<Class<?>, BooleanConversion>();
	private static Map<Class<?>, DecimalConversion> decimalConversions = new HashMap<Class<?>, DecimalConversion>();
	private static Map<Class<?>, EnumeratedConversion> enumeratedConversions = new HashMap<Class<?>, EnumeratedConversion>();

	static {

		// Load all the predefined conversions
		addBoolean(new BooleanToBooleanConversion());
		addDecimal(new DecimalToDoubleConversion());
		addDecimal(new DecimalToFloatConversion());
		addDecimal(new DecimalToIntegerConversion());
		addDecimal(new DecimalToLongConversion());
		addEnumerated(new EnumeratedToEnumConversion());

	}

	/**
	 * Add a boolean conversion.
	 */
	public static void addBoolean(BooleanConversion conversion) {
		Class<?> type = conversion.type();
		booleanConversions.put(type, conversion);
	}

	/**
	 * Add a decimal conversion.
	 */

	public static void addDecimal(DecimalConversion conversion) {
		Class<?> type = conversion.type();
		decimalConversions.put(type, conversion);
	}

	/**
	 * Add an enumerated conversion.
	 */

	public static void addEnumerated(EnumeratedConversion conversion) {
		Class<?> type = conversion.type();
		enumeratedConversions.put(type, conversion);
	}

	/**
	 * Looks up a conversion for a certain java type
	 * 
	 * @param type
	 *            a java class
	 * @throws EngineInitializationException
	 *             if no conversion exists for the type
	 */
	public static BooleanConversion getBooleanConversion(Class<?> type) {

		Class<?> t = ClassUtils.wrapperTypeForPrimitive(type);

		// check for all superclasses of type if a conversion exists
		do {

			BooleanConversion c = booleanConversions.get(t);
			if (c != null)
				return c;

			t = t.getSuperclass();

		} while (t != null);

		throw new EngineInitializationException("No boolean conversion for "
				+ type);
	}

	/**
	 * Looks up a conversion for a certain java type
	 * 
	 * @param type
	 *            a java class
	 * @throws EngineInitializationException
	 *             if no conversion exists for the type
	 */

	public static DecimalConversion getDecimalConversion(Class<?> type) {

		Class<?> t = ClassUtils.wrapperTypeForPrimitive(type);

		// check for all superclasses of type if a conversion exists
		do {

			DecimalConversion c = decimalConversions.get(t);
			if (c != null)
				return c;

			t = t.getSuperclass();

		} while (t != null);

		throw new EngineInitializationException("No decimal conversion for "
				+ type);

	}

	/**
	 * Looks up a conversion for a certain java type
	 * 
	 * @param type
	 *            a java class
	 * @throws EngineInitializationException
	 *             if no conversion exists for the type
	 */

	public static EnumeratedConversion getEnumeratedConversion(Class<?> type) {

		Class<?> t = ClassUtils.wrapperTypeForPrimitive(type);

		// check for all superclasses of type if a conversion exists
		do {

			EnumeratedConversion c = enumeratedConversions.get(t);
			if (c != null)
				return c;

			t = t.getSuperclass();

		} while (t != null);

		throw new EngineInitializationException("No enumerated conversion for "
				+ type);

	}

	/**
	 * For a given Java type, find a XABSL type (boolean, decimal, enumerated)
	 * so that a conversion exists between the two. Subtypes take precendence
	 * over supertypes; bool takes precedence over decimal over enumerated.
	 * 
	 * @param type
	 * @return
	 */
	public static XabslType javaTypeToXabslType(Class type) {

		// convert a primitive type, if given, into an apt wrapper type
		Class<?> t = ClassUtils.wrapperTypeForPrimitive(type);

		// check for all superclasses of type if a conversion exists
		do {

			if (booleanConversions.containsKey(t)) {
				return XabslType.bool;
			} else if (decimalConversions.containsKey(t)) {
				return XabslType.decimal;
			} else if (enumeratedConversions.containsKey(t)) {
				return XabslType.enumerated;
			}

			t = t.getSuperclass();
		} while (t != null);

		return XabslType.none;
	}

}
