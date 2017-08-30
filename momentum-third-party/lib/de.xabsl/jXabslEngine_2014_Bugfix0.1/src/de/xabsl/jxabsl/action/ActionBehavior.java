/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabsl.action;

import de.xabsl.jxabsl.TimeFunction;
import de.xabsl.jxabsl.behavior.Behavior;
import de.xabsl.jxabsl.parameters.ParameterAssignment;

/**
 * Represents an action execution. This is either a subsequent option or basic
 * behavior to be executed.
 */

public abstract class ActionBehavior extends Action {

	protected ParameterAssignment parameterAssignment;

	/**
	 * Constructor.
	 * 
	 * @param timeFunction
	 *            A pointer to a function that returns the system time in ms.
	 */

	public ActionBehavior(TimeFunction timeFunction) {
		super(timeFunction);
	}

	/**
	 * The option or basic behavior that is executed
	 */

	abstract public Behavior getBehavior();

	@Override
	public void execute() {
		parameterAssignment.set();
		Behavior b = getBehavior();
		if (!b.wasActive())
			b.setTimeWhenActivated(timeFunction.getTime());
		b.setTimeOfExecution(timeFunction.getTime() - b.getTimeWhenActivated());

		b.execute();
		b.setActive(true);
	}
	
	public ParameterAssignment getParameterAssignment () {
		return parameterAssignment;
	}

}
