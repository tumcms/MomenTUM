/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabslx.conversions.impl;

import de.xabsl.jxabslx.conversions.DecimalConversion;

/**
 * Conversion between XABSL decimal and java double
 */

public class DecimalToDoubleConversion implements DecimalConversion {

	public double from(Object value) {
		return (Double) value;
	}

	public Object to(double value) {
		return value;
	}

	public Class<?> type() {
		return Double.class;
	}

}
