/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabslx.io;

/**
 * Provides a value from the native java environment
 */
public interface Input {

	/**
	 * The parameter types as java classes in the correct order
	 */
	public abstract Class<?>[] getParamTypes();

	/**
	 * An array to which parameters will be written to prior to getValue()
	 * 
	 */
	public abstract Object[] getParameters();

	/**
	 * Returns the value. Set parameters before calling this function
	 */
	public Object getValue();
}