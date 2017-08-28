/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabsl.engine;

/**
 * An exception to be thrown when the user has not registered a symbol or basic
 * behavior declared in the XABSL source
 */
public class SymbolNotRegisteredException extends RuntimeException {

	public SymbolNotRegisteredException(String message) {
		super(message);
	}

}
