/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabslx.symbols;

import de.xabsl.jxabsl.symbols.Enumeration;
import de.xabsl.jxabsl.utils.DebugMessages;

/**
 * An enumeration that is automatically constructed from a java enum.
 */
public class JavaEnumeration extends Enumeration {
	public JavaEnumeration(String name, Class enumeration, DebugMessages debug) {
		super(name, debug);

		if (!enumeration.isEnum())
			throw new IllegalArgumentException("Class" + enumeration
					+ " is not an Enumeration!");

		for (Object c : enumeration.getEnumConstants()) {
			String n = ((Enum) c).name();
			add(n, c);
		}

	}

}
