/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabsl.expression.bool;

import java.util.List;

import de.xabsl.jxabsl.action.Action;
import de.xabsl.jxabsl.action.ActionOption;
import de.xabsl.jxabsl.behavior.Option;
import de.xabsl.jxabsl.utils.DebugMessages;

/**
 * Represents a 'subsequent-option-reached-target-state' expression
 */

public class SubsequentOptionReachedTargetStateCondition extends
		BooleanExpression {

	private List<Action> actions;

	/**
	 * Constructor. Creates the element.
	 * 
	 * @param actions
	 *            The subsequent behaviors i.e options and basic behaviors of
	 *            the state.
	 * @param debug
	 *            For debugging output
	 */

	public SubsequentOptionReachedTargetStateCondition(List<Action> actions,
			DebugMessages debug) {
		super(debug);
		debug
				.printlnInit("Creating subsequent-option-reached-target-state element");
		this.actions = actions;
	}

	@Override
	public boolean getValue() {
		for (Action a : actions) {
			if (a instanceof ActionOption) {
				Option o = (Option) (((ActionOption) a).getBehavior());
				if (o.getOptionReachedATargetState())
					return true;
			}
		}
		return false;
	}
}