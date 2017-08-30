/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabslx.conversions.impl;

import de.xabsl.jxabsl.EngineInitializationException;
import de.xabsl.jxabslx.conversions.EnumeratedConversion;

/**
 * Conversion between XABSL enumerated and java enum
 */

public class EnumeratedToEnumConversion implements EnumeratedConversion {

	// (Java 6) @Override
	public Object from(Object value) {
		return value;
	}

	// (Java 6) @Override
	public Object to(Object value) {
		return value;
	}

	// (Java 6) @Override
	public Class<?> type() {
		return Enum.class;
	}

	// (Java 6) @Override
	public String getEnumerationName(Class<?> type) {
		if (Enum.class.isAssignableFrom(type)) {
			return type.getSimpleName();
		} else {
			throw new EngineInitializationException("The type " + type
					+ "must be a subtype of " + Enum.class);
		}
	}

}
