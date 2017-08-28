/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabslx.io;

import java.lang.reflect.Method;

import de.xabsl.jxabsl.EngineInitializationException;

/**
 * Writes a value by invoking a setter method, i.e. a method with one parameter
 * which is the value
 * 
 */
public class OutputToSetterMethod extends MethodAccess implements Output {

	public OutputToSetterMethod(Method method, Object container) {

		super(method, container);

		checkContainer();

		checkParameter();

		checkAccess();

	}

	/**
	 * Check if the function has exactly one parameter.
	 */
	protected void checkParameter() {

		Class[] parameterTypes = method.getParameterTypes();

		if (parameterTypes.length != 1) {
			throw new EngineInitializationException("The method "
					+ method.getName() + " must have exacly one parameter");
		}

	}

	public void setValue(Object value) {

		invokeMethod(new Object[] { value });

	}

}