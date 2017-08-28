/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabslx.io;

import java.lang.reflect.Field;

import de.xabsl.jxabsl.symbols.XABSLInternalErrorException;

/**
 * Writes a value to a java field.
 */

public class OutputToField extends FieldAccess implements Output {

	public OutputToField(Field field, Object container) {
		super(field, container);

	}

	public void setValue(Object value) {
		try {
			field.set(container, value);
		} catch (IllegalArgumentException e) {
			throw new XABSLInternalErrorException(
					"Wrong type. Tried to set field " + field
							+ ", which is of " + field.getType()
							+ " to a value of type " + value.getClass());
		} catch (IllegalAccessException e) {

			throw generateFieldInaccessibleException(e);
		}
	}
}