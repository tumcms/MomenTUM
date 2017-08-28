/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabslx.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import de.xabsl.jxabslx.conversions.Conversions;

/**
 * Various useful functions in dealing with types
 */
public class ClassUtils {

	/**
	 * Internal types that the engine uses
	 */
	public enum XabslType {
		decimal, bool, enumerated, /** no valid XABSL type */
		none
	}

	// A list of primitive types (double, boolean ...) that the XABSL engine can
	// handle
	private static List<Class> validPrimitiveTypes;
	static {
		validPrimitiveTypes = new ArrayList<Class>();
		validPrimitiveTypes.add(Double.TYPE);
		validPrimitiveTypes.add(Float.TYPE);
		validPrimitiveTypes.add(Integer.TYPE);
		validPrimitiveTypes.add(Boolean.TYPE);
	}

	/**
	 * 
	 * @param type
	 *            a primitive java type
	 * @return the appropriate wrapper type
	 */
	public static Class wrapperTypeForPrimitive(Class type) {
		if (type.equals(Double.TYPE))
			return Double.class;
		if (type.equals(Float.TYPE))
			return Float.class;
		if (type.equals(Integer.TYPE))
			return Integer.class;
		if (type.equals(Long.TYPE))
			return Long.class;
		if (type.equals(Boolean.TYPE))
			return Boolean.class;

		return type;
	}

	/**
	 * 
	 * Finds all member methods of an object that match the given criteria.
	 * 
	 * @param instance
	 *            Object to contain methods. If of type Class, then only static
	 *            methods in that class will be considered.
	 * @param methodName
	 * @param returntype
	 *            null for any type
	 * @param numberOfDecimalParameters
	 * @param numberOfBooleanParameters
	 * @param numberOfEnumeratedParameters
	 * @return
	 */
	// TODO Find all methods with a superset of given parameters, since calls
	// from the XABSL source can omit parameters
	public static List<Method> findMethods(Object instance, String methodName,
			XabslType returnType, int numberOfDecimalParameters,
			int numberOfBooleanParameters, int numberOfEnumeratedParameters) {

		List<Method> candidates = new ArrayList<Method>();

		// Find out if we are supposed to only look for static methods
		Method[] methods = ((Class) (instance instanceof Class<?> ? instance
				: instance.getClass())).getMethods();

		for (int i = 0; i < methods.length; i++) {

			boolean add = true;

			// does the method have the right name?
			if (!methods[i].getName().equals(methodName))
				add = false;

			// does the method have the right return type?
			if ((returnType != null)
					&& (Conversions.javaTypeToXabslType(methods[i]
							.getReturnType()) != returnType))
				add = false;

			// is the field public?
			if (!Modifier.isPublic((methods[i]).getModifiers()))
				add = false;

			// does the method have the right parameter list?
			Class[] pTypes = methods[i].getParameterTypes();
			int d = 0, b = 0, e = 0;
			for (int j = 0; j < pTypes.length; j++) {

				switch (Conversions.javaTypeToXabslType(pTypes[j])) {
				case decimal:
					d++;
					break;

				case bool:
					b++;
					break;

				case enumerated:
					e++;
					break;

				default:
					add = false;
					break;

				}
			}

			if (!(d == numberOfDecimalParameters
					&& b == numberOfBooleanParameters && e == numberOfEnumeratedParameters))
				add = false;

			if (add)
				candidates.add(methods[i]);
		}

		return candidates;
	}

	/**
	 * Finds all field with a certain name in an instance
	 */
	public static List<Field> findFields(Object instance, String fieldName,
			XabslType type) {

		List<Field> candidates = new ArrayList<Field>();

		// Find out if we are supposed to only look for static fields

		Field[] fields = ((Class) (instance instanceof Class<?> ? instance
				: instance.getClass())).getFields();

		for (int i = 0; i < fields.length; i++) {

			boolean add = true;

			// does the field have the right name?
			if (!fields[i].getName().equals(fieldName))
				add = false;

			// does the field have the right return type?
			if ((type == null)
					|| (Conversions.javaTypeToXabslType(fields[i].getType()) != type))
				add = false;

			// is the field public?
			if (!Modifier.isPublic((fields[i]).getModifiers()))
				add = false;

			if (add)
				candidates.add(fields[i]);
		}

		return candidates;
	}

}
