/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabslx.conversions.impl;

import de.xabsl.jxabslx.conversions.BooleanConversion;

/**
 * Conversion between XABSL boolean and java boolean
 */
public class BooleanToBooleanConversion implements BooleanConversion {

	public boolean from(Object value) {
		return (Boolean) value;
	}

	public Object to(boolean value) {
		return value;
	}

	public Class<?> type() {
		return Boolean.class;
	}

}
