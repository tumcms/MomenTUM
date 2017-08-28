/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabsl.behavior;

import de.xabsl.jxabsl.utils.DebugMessages;

/**
 * The base class for basic behaviors that are used by the engine
 */
public abstract class BasicBehavior extends Behavior {

	/**
	 * Constructor
	 * 
	 * @param name
	 *            The name of the basic behavior
	 * @param debug
	 *            For printing debugging messages
	 */

	public BasicBehavior(String name, DebugMessages debug) {
		super(name, debug);
	}

}
