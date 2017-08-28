/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabsl.symbols;

import de.xabsl.jxabsl.utils.DebugMessages;

/**
 * An item which has a name.
 */
public abstract class NamedItem {

	protected String name;

	protected DebugMessages debug;

	public NamedItem(String name, DebugMessages debug) {

		this.name = name;
		this.debug = debug;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {

		return getName();
	}

}
