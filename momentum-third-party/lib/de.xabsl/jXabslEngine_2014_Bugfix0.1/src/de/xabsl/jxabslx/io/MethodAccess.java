/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabslx.io;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import de.xabsl.jxabsl.EngineInitializationException;
import de.xabsl.jxabsl.symbols.XABSLInternalErrorException;

/**
 * Base class for classes that access a java method.
 */

public abstract class MethodAccess {

	protected Method method;
	protected Object container;

	public MethodAccess(Method method, Object container) {

		this.method = method;
		this.container = container;
	}

	protected void checkContainer() {

		// if the container is null, the method is static, do not check
		if (container != null) {
			Method[] methods = container.getClass().getMethods();
			boolean containsMethod = false;
			for (int i = 0; i < methods.length; i++) {
				if (methods[i].equals(method))
					containsMethod = true;
			}
			if (!containsMethod)
				throw new EngineInitializationException("The object "
						+ container + " of class "
						+ container.getClass().getCanonicalName()
						+ " does not contain the method" + method);
		}

	}

	protected void checkAccess() {
		int modifiers = method.getModifiers();
		if (!Modifier.isPublic(modifiers))
			throw new EngineInitializationException("The method " + method
					+ " is not publically accessible");

	}

	protected Object invokeMethod(Object[] parameters) {

		try {
			return this.method.invoke(container, parameters);
		} catch (IllegalArgumentException e) {
			throw this.generateIllegalArgumentException(e);
		} catch (IllegalAccessException e) {
			throw this.generateMethodInaccessibleException(e);
		} catch (InvocationTargetException e) {
			throw this.generateInvocationTagetException(e);
		}

	}

	protected XABSLInternalErrorException generateMethodInaccessibleException(
			Exception cause) {
		return new XABSLInternalErrorException("The method " + method
				+ " is inaccessible", cause);

	}

	protected XABSLInternalErrorException generateInvocationTagetException(
			InvocationTargetException cause) {
		return new XABSLInternalErrorException("The method " + method
				+ " threw an exception during its execution", cause);
	}

	protected XABSLInternalErrorException generateIllegalArgumentException(
			IllegalArgumentException cause) {
		return new XABSLInternalErrorException("The method " + method
				+ " was invoked with an illegal argument", cause);

	}

	public Method getMethod() {
		return method;
	}

}
