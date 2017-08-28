/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabsl.expression.decimal;

import de.xabsl.jxabsl.TimeFunction;
import de.xabsl.jxabsl.utils.DebugMessages;

/**
 * Represents a time-of-option-execution or time-of-state-execution expression
 * in the option graph
 */

public class TimeRef extends DecimalExpression {

	private TimeFunction time;

	/**
	 * Constructor
	 * 
	 * @param debug
	 *            For debugging output
	 * @param time
	 *            the referenced time
	 */

	public TimeRef(DebugMessages debug, TimeFunction time) {
		super(debug);
		debug
				.printlnInit("creating a reference to state or option execution time");
		this.time = time;
	}

	@Override
	public double getValue() {
		return (double) time.getTime();
	}

}
