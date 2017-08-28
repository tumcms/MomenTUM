/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabslx.io;

import java.lang.reflect.Field;

/**
 * Reads a value from a java field.
 */
public class InputFromField extends FieldAccess implements Input {

	// A field will not take parameters
	private static final Object[] parameters = new Object[0];
	private static final Class<?>[] paramTypes = new Class<?>[0];

	public InputFromField(Field field, Object container) {

		super(field, container);

	}

	public Object[] getParameters() {
		return parameters;
	}

	public Class<?>[] getParamTypes() {
		return paramTypes;
	}

	public Object getValue() {

		try {

			return field.get(container);

		} catch (IllegalAccessException e) {

			throw generateFieldInaccessibleException(e);

		}

	}

}
