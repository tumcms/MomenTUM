/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabsl.expression.bool;

import de.xabsl.jxabsl.utils.DebugMessages;

/**
 * Represents a 'less-than' expression
 */

public class LessThanOperator extends RelationalAndEqualityOperator {

	public LessThanOperator(DebugMessages debug) {
		super(debug);
	}

	@Override
	public boolean getValue() {
		return operand1.getValue() < operand2.getValue();
	}

}
