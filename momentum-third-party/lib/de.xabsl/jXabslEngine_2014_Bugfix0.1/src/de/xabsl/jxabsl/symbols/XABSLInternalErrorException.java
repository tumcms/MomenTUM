/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabsl.symbols;

/**
 * Indicates that an unexpected error has occurred in the XABSL engine. This
 * exception indicates an internal error, in which the user has no part.
 * 
 */

public class XABSLInternalErrorException extends RuntimeException {

	public XABSLInternalErrorException(String message, Throwable cause) {
		super(message, cause);
	}

	public XABSLInternalErrorException(String message) {
		super(message);
	}

	private static final long serialVersionUID = 7204846165399556901L;

}
