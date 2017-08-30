/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabsl.behavior;

import de.xabsl.jxabsl.TimeFunction;
import de.xabsl.jxabsl.parameters.Parameters;
import de.xabsl.jxabsl.symbols.NamedItem;
import de.xabsl.jxabsl.utils.DebugMessages;

/**
 * Parent class for Option and BasicBehavior
 */

public abstract class Behavior extends NamedItem {

	protected boolean active;
	protected boolean wasActive;
	// will be returned by timeOfExecution
	protected long tOE;
	protected TimeFunction timeOfExecution = new TimeFunction(){

		public long getTime() {
			return tOE;
		}
		
	};
	protected long timeWhenActivated;
	protected Parameters parameters;

	/** the behavior is activated in the current path through the option graph */

	public boolean isActive() {
		return active;
	}

	/**
	 * set if the behavior is activated in the current path through the option
	 * graph
	 */

	public void setActive(boolean active) {
		this.active = active;
	}

	/** the behavior was activated in the last path through the option graph */

	public boolean wasActive() {
		return wasActive;
	}

	/**
	 * set if the behavior was activated in the last path through the option
	 * graph
	 */

	public void setWasActive(boolean active) {
		this.wasActive = active;
	}

	/**
	 * Constructor.
	 * 
	 * @param name
	 *            The name of the behavior. For debugging purposes.
	 */

	public Behavior(String name, DebugMessages debug) {
		super(name, debug);
	}

	/**
	 * executes the behavior
	 */

	public abstract void execute();

	/**
	 * @return The parameters of the behavior
	 */
	public Parameters getParameters() {
		return parameters;
	}

	/** set the time when the option was activated */

	public void setTimeWhenActivated(long timeWhenActivated) {
		this.timeWhenActivated = timeWhenActivated;
	}

	/** the time when the option was activated */
	public long getTimeWhenActivated() {
		return timeWhenActivated;
	}

	/** set the time how long the option is already active */

	public void setTimeOfExecution(long timeOfExecution) {
		this.tOE = timeOfExecution;
	}

	/** the time how long the option is already active */

	public long getTimeOfExecution() {
		return tOE;
	}

}
