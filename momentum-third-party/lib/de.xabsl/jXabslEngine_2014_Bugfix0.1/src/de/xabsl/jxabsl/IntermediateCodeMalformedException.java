/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabsl;

/**
 * Exception indicating that the XABSL intermediate code is syntactically or
 * otherwise incorrect.
 */

public class IntermediateCodeMalformedException extends Exception {

	private static final long serialVersionUID = 2517168028871860462L;

	public static final String UNEXPECTED_END = "Unexpected end of input!";

	public IntermediateCodeMalformedException(String message, Throwable cause) {
		super(message, cause);
	}

	public IntermediateCodeMalformedException(String message) {
		super(message);
	}
}
