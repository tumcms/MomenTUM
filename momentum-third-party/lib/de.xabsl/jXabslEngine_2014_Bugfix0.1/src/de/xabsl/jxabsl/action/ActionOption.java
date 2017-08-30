/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabsl.action;

import de.xabsl.jxabsl.TimeFunction;
import de.xabsl.jxabsl.behavior.Behavior;
import de.xabsl.jxabsl.behavior.Option;

/**
 * Represents an action execution. In this case an option is to be executed.
 */
public class ActionOption extends ActionBehavior {

	protected Option option;

	/**
	 * Constructor.
	 * 
	 * @param timeFunction
	 *            A pointer to a function that returns the system time in ms.
	 */

	public ActionOption(TimeFunction timeFunction) {
		super(timeFunction);
	}

	@Override
	public Behavior getBehavior() {
		return option;
	}

}
