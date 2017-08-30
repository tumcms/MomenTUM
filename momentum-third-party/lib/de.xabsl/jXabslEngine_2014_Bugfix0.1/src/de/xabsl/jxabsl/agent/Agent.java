/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabsl.agent;

import de.xabsl.jxabsl.behavior.Behavior;
import de.xabsl.jxabsl.symbols.NamedItem;
import de.xabsl.jxabsl.utils.DebugMessages;

/**
 * Combines some options to an agent
 */

public class Agent extends NamedItem {
	private Behavior rootOption;

	/**
	 * Constructor
	 * 
	 * @param name
	 *            The name of the agent
	 * @param rootOption
	 *            The initial option of the agent
	 * @param debug
	 *            For printing debug messages
	 */

	public Agent(String name, Behavior rootOption, DebugMessages debug) {
		super(name, debug);
		this.rootOption = rootOption;
		debug.printlnInit("Created Agent " + name + " with root option "
				+ rootOption.getName());
	}

	/** Returns the root option */
	public Behavior getRootOption() {
		return rootOption;
	}

}
