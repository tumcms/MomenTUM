/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabsl.parameters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.xabsl.jxabsl.IntermediateCodeMalformedException;
import de.xabsl.jxabsl.TimeFunction;
import de.xabsl.jxabsl.action.Action;
import de.xabsl.jxabsl.behavior.OptionParameters;
import de.xabsl.jxabsl.engine.SymbolNotRegisteredException;
import de.xabsl.jxabsl.engine.Symbols;
import de.xabsl.jxabsl.expression.bool.BooleanExpression;
import de.xabsl.jxabsl.expression.decimal.DecimalExpression;
import de.xabsl.jxabsl.expression.enumerated.EnumeratedExpression;
import de.xabsl.jxabsl.symbols.Enumeration;
import de.xabsl.jxabsl.utils.DebugMessages;
import de.xabsl.jxabsl.utils.InputSource;

/**
 * Represents the assignment of parameters of a subsequent basic behaviors or an
 * option or an input symbol, i.e. stores expressions and writes them to the
 * appropriate parameters before execution of a behavior or input symbol.
 * Parameters that have no assigned expression are set to a standard value.
 */

public class ParameterAssignment {

	protected DebugMessages debug;

	/*
	 * The actual Expressions
	 */
	private List<DecimalExpression> decimalExpressions = new ArrayList<DecimalExpression>();
	private List<BooleanExpression> booleanExpressions = new ArrayList<BooleanExpression>();
	private List<EnumeratedExpression> enumeratedExpressions = new ArrayList<EnumeratedExpression>();

	/*
	 * Their position in the referenced Parameters
	 */
	private List<Integer> decimalPositions = new ArrayList<Integer>();
	private List<Integer> booleanPositions = new ArrayList<Integer>();
	private List<Integer> enumeratedPositions = new ArrayList<Integer>();

	/*
	 * Store parsed parameters temporarily when we do not have the refParameters
	 * yet.
	 */
	private Map<String, DecimalExpression> tmpDecimalParameterExpressions = new HashMap<String, DecimalExpression>();

	private Map<String, BooleanExpression> tmpBooleanParameterExpressions = new HashMap<String, BooleanExpression>();

	private Map<String, EnumeratedExpression> tmpEnumeratedParameterExpressions = new HashMap<String, EnumeratedExpression>();

	/**
	 * The referenced Parameters
	 */
	private Parameters refParameters;

	/*
	 * Parameters values are additionally stored here when set. They can be read
	 * for debugging purposes. The order is the same as in *Expressions.
	 * 
	 */
	private double[] decimalDebugValues;
	private boolean[] booleanDebugValues;
	private Object[] enumeratedDebugValues;

	/**
	 * 
	 * @param debug
	 * @param refParameters
	 *            The referenced Parameters. May be null, but in that case
	 *            <code> setRefParameters </code> must be called later on.
	 */
	public ParameterAssignment(DebugMessages debug, Parameters refParameters) {
		this.debug = debug;
		this.refParameters = refParameters;
	}

	/**
	 * Creates the parameter assignment.
	 * 
	 * @param input
	 *            An input source for the intermediate code. It must be opened
	 *            and read until a position where a parameter assignment starts.
	 * @param optionParameters
	 *            The parameters of the option
	 * @param symbols
	 *            All available symbols
	 * @param timeOfOptionExecution
	 *            The time how long the option is already active
	 * @param timeOfStateExecution
	 *            The time how long the current state is already active
	 * @param actions
	 *            The subsequent behaviors i.e options and basic behaviors of
	 *            the state.
	 */

	public void create(InputSource input, OptionParameters optionParameters,
			Symbols symbols, TimeFunction timeOfOptionExecution,
			TimeFunction timeOfStateExecution, List<Action> actions)
			throws IntermediateCodeMalformedException {
		int numberOfParameters = (int) input.nextInt();

		for (int i = 0; i < numberOfParameters; i++) {
			String type = input.next();
			switch (type.charAt(0)) {

			case 'd': {
				String name = input.next();
				debug
						.printlnInit("Creating expression for set decimal parameter "
								+ name);
				DecimalExpression exp = DecimalExpression.create(input,
						actions, debug, optionParameters, symbols,
						timeOfOptionExecution, timeOfStateExecution);
				addDecimalParameter(name, exp);
				break;
			}
			case 'b': {
				String name = input.next();
				debug
						.printlnInit("Creating expression for set boolean parameter "
								+ name);
				BooleanExpression exp = BooleanExpression.create(input,
						actions, debug, optionParameters, symbols,
						timeOfOptionExecution, timeOfStateExecution);
				addBooleanParameter(name, exp);
				break;
			}
			case 'e': {
				String enumName = input.next();
				String name = input.next();
				debug
						.printlnInit("Creating expression for set enumerated parameter "
								+ name);
				Enumeration enumeration = symbols.getEnumeration(enumName);
				EnumeratedExpression exp = EnumeratedExpression.create(
						enumeration, input, actions, debug, optionParameters,
						symbols, timeOfOptionExecution, timeOfStateExecution);
				addEnumeratedParameter(name, exp);
				break;

			}
			}
		}

		booleanDebugValues = new boolean[tmpBooleanParameterExpressions.size()];
		decimalDebugValues = new double[tmpDecimalParameterExpressions.size()];
		enumeratedDebugValues = new Object[tmpEnumeratedParameterExpressions
				.size()];

		if (refParameters != null)
			connectToParameters();
	}

	/**
	 * sets parameter variables to current expression values
	 */

	public void set() {

		// Set the parameters to standard values, since there might be no value
		// assigned to some parameters

		if (refParameters != null)
			refParameters.reset();

		for (int i = 0; i < decimalPositions.size(); i++) {
			double value = decimalExpressions.get(i).getValue();
			refParameters.setDecimal(decimalPositions.get(i), value);
			decimalDebugValues[i] = value;
		}

		for (int i = 0; i < booleanPositions.size(); i++) {
			boolean value = booleanExpressions.get(i).getValue();
			refParameters.setBoolean(booleanPositions.get(i), value);
			booleanDebugValues[i] = value;
		}

		for (int i = 0; i < enumeratedPositions.size(); i++) {
			Object value = enumeratedExpressions.get(i).getValue();
			refParameters.setEnumerated(enumeratedPositions.get(i), value);
			enumeratedDebugValues[i] = value;
		}

	}

	/**
	 * Adds a boolean parameter assignment
	 * 
	 * @param name
	 *            The name of the parameter
	 * @param expression
	 *            value to be set to the parameter value when executing
	 * @return False, if an error occurred
	 */

	private void addBooleanParameter(String name, BooleanExpression expression) {
		tmpBooleanParameterExpressions.put(name, expression);
	}

	/**
	 * Adds a decimal parameter assignment
	 * 
	 * @param name
	 *            The name of the parameter
	 * @param expression
	 *            value to be set to the parameter value when executing
	 * @return False, if an error occurred
	 */

	private void addDecimalParameter(String name, DecimalExpression expression) {
		tmpDecimalParameterExpressions.put(name, expression);
	}

	/**
	 * Adds an enumerated parameter assignment
	 * 
	 * @param name
	 *            The name of the parameter
	 * @param expression
	 *            value to be set to the parameter value when executing
	 * @return False, if an error occurred
	 */

	private void addEnumeratedParameter(String name,
			EnumeratedExpression expression) {

		// enum type check is done in connectToParameters
		tmpEnumeratedParameterExpressions.put(name, expression);
	}

	/**
	 * Must be called after refParameters are set.
	 */

	private void connectToParameters() {
		Iterator<String> i;

		i = tmpBooleanParameterExpressions.keySet().iterator();
		while (i.hasNext()) {
			String name = i.next();
			booleanExpressions.add(tmpBooleanParameterExpressions.get(name));
			booleanPositions.add(refParameters.getBooleanPosition(name));
		}
		// discard temporary map
		tmpBooleanParameterExpressions = null;

		i = tmpDecimalParameterExpressions.keySet().iterator();
		while (i.hasNext()) {
			String name = i.next();
			decimalExpressions.add(tmpDecimalParameterExpressions.get(name));
			decimalPositions.add(refParameters.getDecimalPosition(name));
		}
		// discard temporary map
		tmpDecimalParameterExpressions = null;

		i = tmpEnumeratedParameterExpressions.keySet().iterator();
		while (i.hasNext()) {
			String name = i.next();

			Enumeration expected = tmpEnumeratedParameterExpressions.get(name)
					.getEnumeration();

			Enumeration found = refParameters.getEnumeration(name);

			if (expected != found)
				throw new SymbolNotRegisteredException(
						"Type mismatch on enumerated option parameter " + name
								+ ", expected " + expected + ", found " + found);

			enumeratedExpressions.add(tmpEnumeratedParameterExpressions
					.get(name));
			enumeratedPositions.add(refParameters.getEnumeratedPosition(name));

		}
		// discard temporary map
		tmpEnumeratedParameterExpressions = null;

	}

	@Override
	public String toString() {

		String s = "[";

		if (booleanDebugValues != null && decimalDebugValues != null
				&& enumeratedDebugValues != null) {
			for (int i = 0; i < booleanDebugValues.length; i++)
				s += refParameters.getBooleanName(booleanPositions.get(i))
						+ "=" + booleanDebugValues[i] + ", ";

			for (int i = 0; i < decimalDebugValues.length; i++)
				s += refParameters.getDecimalName(decimalPositions.get(i))
						+ "=" + decimalDebugValues[i] + ", ";

			for (int i = 0; i < enumeratedDebugValues.length; i++)
				s += refParameters
						.getEnumeratedName(enumeratedPositions.get(i))
						+ "=" + enumeratedDebugValues[i] + ", ";

		}
		s += "]";
		return s;
	}

	/**
	 * Set the referenced parameters, to which values will be written
	 * 
	 * @param refParameters
	 *            the referenced parameters
	 */
	public void setRefParameters(Parameters refParameters) {
		if (this.refParameters != null)
			throw new IllegalStateException(
					"Referenced parameters may only be set once!");
		this.refParameters = refParameters;
		connectToParameters();
	}

	/**
	 * May only be called before refParameters are set.
	 * 
	 * @return the names of all decimal parameters to be referenced.
	 */
	public Set<String> getDecimalNames() {
		if (tmpDecimalParameterExpressions == null)
			throw new IllegalStateException(
					"May only be called before refParameters are set");
		return tmpDecimalParameterExpressions.keySet();
	}

	/**
	 * May only be called before refParameters are set.
	 * 
	 * @return the names of all boolean parameters to be referenced.
	 */
	public Set<String> getBooleanNames() {
		if (tmpBooleanParameterExpressions == null)
			throw new IllegalStateException(
					"May only be called before refParameters are set");
		return tmpBooleanParameterExpressions.keySet();
	}

	/**
	 * May only be called before refParameters are set.
	 * 
	 * @return the names of all enumerated parameters to be referenced.
	 */
	public Set<String> getEnumeratedNames() {
		if (tmpEnumeratedParameterExpressions == null)
			throw new IllegalStateException(
					"May only be called before refParameters are set");
		return tmpEnumeratedParameterExpressions.keySet();
	}

	/**
	 * Return the referenced parameters.
	 * 
	 * @return
	 */
	public Parameters getRefParameters() {
		return refParameters;
	}

	/** * for debugging ** */

	public double[] getDecimalDebugValues() {
		return decimalDebugValues;
	}

	public boolean[] getBooleanDebugValues() {
		return booleanDebugValues;
	}

	public Object[] getEnumeratedDebugValues() {
		return enumeratedDebugValues;
	}

	/**
	 * Returns the name of a parameter for debugging
	 * 
	 * @param pos
	 *            The position in the debuggin values
	 * @return The parameter's name
	 */
	public String getBooleanDebugName(int pos) {
		return refParameters.getBooleanName(booleanPositions.get(pos));
	}

	/**
	 * Returns the name of a parameter for debugging
	 * 
	 * @param pos
	 *            The position in the debuggin values
	 * @return The parameter's name
	 */
	public String getDecimalDebugName(int pos) {
		return refParameters.getDecimalName(decimalPositions.get(pos));
	}

	/**
	 * Returns the name of a parameter for debugging
	 * 
	 * @param pos
	 *            The position in the debuggin values
	 * @return The parameter's name
	 */
	public String getEnumeratedDebugName(int pos) {
		return refParameters.getEnumeratedName(enumeratedPositions.get(pos));
	}

}
