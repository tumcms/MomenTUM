/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabslx.io;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import de.xabsl.jxabsl.EngineInitializationException;
import de.xabsl.jxabsl.symbols.XABSLInternalErrorException;

/**
 * Base class for classes that access a java field.
 */
public abstract class FieldAccess {

	protected Field field;
	protected Object container;

	public FieldAccess(Field field, Object container) {

		this.field = field;
		this.container = container;

		checkAccess();
		checkContainer();
	}

	protected void checkContainer() {
		// if container is null, field is static. don't check
		if (container != null) {
			Field[] fields = container.getClass().getFields();
			boolean containsField = false;
			for (int i = 0; i < fields.length; i++) {
				if (fields[i].equals(field))
					containsField = true;
			}
			if (!containsField)
				throw new EngineInitializationException("The object "
						+ container + " of class "
						+ container.getClass().getCanonicalName()
						+ " does not contain the field " + field);

		}
	}

	protected void checkAccess() {
		int modifiers = field.getModifiers();
		if (!Modifier.isPublic(modifiers))
			throw new EngineInitializationException("The field "
					+ field.getName() + " is not publically accessible");

	}

	protected XABSLInternalErrorException generateFieldInaccessibleException(
			Exception cause) {
		return new XABSLInternalErrorException(this.getClass().getSimpleName()
				+ ": The field " + container.getClass() + "." + field.getName()
				+ " is inaccessible!", cause);

	}

}