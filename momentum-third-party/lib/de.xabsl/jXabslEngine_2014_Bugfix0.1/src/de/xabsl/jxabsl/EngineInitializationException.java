/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabsl;

/**
 * An error during engine initialization has occurred
 */

public class EngineInitializationException extends RuntimeException {

	private static final long serialVersionUID = -3496330403428486773L;

	public EngineInitializationException(String message) {
		super(message);
	}
}
