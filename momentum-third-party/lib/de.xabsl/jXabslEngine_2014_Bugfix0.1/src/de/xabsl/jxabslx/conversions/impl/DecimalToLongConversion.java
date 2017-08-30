/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabslx.conversions.impl;

import de.xabsl.jxabslx.conversions.DecimalConversion;

/**
 * Conversion between XABSL decimal and java long
 */

public class DecimalToLongConversion implements DecimalConversion {

	public double from(Object value) {
		return (double) ((Long) value);
	}

	public Object to(double value) {
		return (Long) Math.round(value);
	}

	public Class<?> type() {
		return Long.class;
	}

}
