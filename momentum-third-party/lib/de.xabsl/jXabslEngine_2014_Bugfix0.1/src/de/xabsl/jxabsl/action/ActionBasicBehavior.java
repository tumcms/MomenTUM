/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabsl.action;

import de.xabsl.jxabsl.TimeFunction;
import de.xabsl.jxabsl.behavior.BasicBehavior;
import de.xabsl.jxabsl.behavior.Behavior;

/**
 * Represents a basic behavior execution.
 */
public class ActionBasicBehavior extends ActionBehavior {

	protected BasicBehavior basicBehavior;

	/**
	 * Constructor.
	 * 
	 * @param timeFunction
	 *            a pointer to a function that returns the system time in ms.
	 */

	public ActionBasicBehavior(TimeFunction timeFunction) {
		super(timeFunction);
	}

	@Override
	public Behavior getBehavior() {
		return basicBehavior;

	}

}
