/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabslx.io;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.xabsl.jxabsl.symbols.XABSLInternalErrorException;

/**
 * Reads a value from a native java method.
 * 
 */
public class InputFromMethod extends MethodAccess implements Input {

	private final Object[] parameters;

	public InputFromMethod(Method method, Object container) {

		super(method, container);
		// initialize parameter array
		parameters = new Object[method.getParameterTypes().length];

	}

	public Object[] getParameters() {
		return parameters;
	}

	public Class<?>[] getParamTypes() {
		return this.method.getParameterTypes();
	}

	protected Object invokeMethod() {
		try {
			return this.method.invoke(container, parameters);

		} catch (IllegalArgumentException e) {
			throw new XABSLInternalErrorException("The method " + method
					+ " was invoked with an illegal argument", e);

		} catch (IllegalAccessException e) {
			throw new XABSLInternalErrorException(this.getClass()
					.getSimpleName()
					+ ": The method "
					+ container.getClass()
					+ "."
					+ method.getName() + " is inaccessible!", e);

		} catch (InvocationTargetException e) {
			throw new XABSLInternalErrorException("The method " + method
					+ " threw an exception during its execution", e);
		}

	}

	public Object getValue() {

		return invokeMethod();

	}

	@Override
	public String toString() {
		return "Input from method " + this.method;
	}

}
