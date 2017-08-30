/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabslx.conversions.impl;

import de.xabsl.jxabslx.conversions.DecimalConversion;

/**
 * Conversion between XABSL decimal and java int
 */

public class DecimalToIntegerConversion implements DecimalConversion {

	public double from(Object value) {
		return (double) ((Integer) value);
	}

	public Object to(double value) {
		return (int) (Math.round(value));
	}

	public Class<?> type() {
		return Integer.class;
	}

}
