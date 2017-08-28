/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabslx.conversions.impl;

import de.xabsl.jxabslx.conversions.DecimalConversion;

/**
 * Conversion between XABSL decimal and java float
 */

public class DecimalToFloatConversion implements DecimalConversion {

	public double from(Object value) {
		return (double) ((Float) value);
	}

	public Object to(double value) {
		return (float) value;
	}

	public Class<?> type() {
		return Float.class;
	}

}
