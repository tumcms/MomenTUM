/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabsl.utils;

/**
 * Print messages during engine initialization/execution
 */
public interface DebugMessages {

	/**
	 * Print a message concerning engine initialization
	 * 
	 * @param message
	 */
	public void printlnInit(String message);


	/**
	 * Print a warning
	 * 
	 * @param message
	 */
	public void printlnWarning(String message);
}
