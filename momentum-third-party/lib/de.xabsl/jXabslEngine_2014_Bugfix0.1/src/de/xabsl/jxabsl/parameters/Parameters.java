/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabsl.parameters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import de.xabsl.jxabsl.EngineInitializationException;
import de.xabsl.jxabsl.engine.SymbolNotRegisteredException;
import de.xabsl.jxabsl.symbols.Enumeration;
import de.xabsl.jxabsl.utils.DebugMessages;

/**
 * Represents the current set of parameters of a behavior or an input symbol.
 * Parameters are referenced by integer positions. The position can be obtained
 * by givng the parameters name
 */

public class Parameters {

	protected DebugMessages debug;

	protected LinkedHashMap<String, Integer> decimal = new LinkedHashMap<String, Integer>();
	protected LinkedHashMap<String, Integer> bool = new LinkedHashMap<String, Integer>();
	protected LinkedHashMap<String, Integer> enumerated = new LinkedHashMap<String, Integer>();

	protected List<DecimalParameter> decimalParameters = new ArrayList<DecimalParameter>();
	protected List<BooleanParameter> booleanParameters = new ArrayList<BooleanParameter>();
	protected List<EnumeratedParameter> enumeratedParameters = new ArrayList<EnumeratedParameter>();

	protected List<Enumeration> enumerations = new ArrayList<Enumeration>();

	/**
	 * Registers a decimal parameter with a name.
	 * 
	 * @param name
	 *            The name of the parameter
	 * @param parameter
	 *            The reference to a parameter
	 */

	public void registerDecimal(String name, DecimalParameter parameter) {
		if (decimal.containsKey(name))
			throw new EngineInitializationException("Decimal parameter" + name
					+ " has already been registered!");
		decimalParameters.add(parameter);
		int pos = decimalParameters.size() - 1;
		decimal.put(name, pos);
	}

	/**
	 * Registers a boolean parameter with a name.
	 * 
	 * @param name
	 *            The name of the parameter
	 * @param parameter
	 *            The reference to a parameter
	 */

	public void registerBoolean(String name, BooleanParameter parameter) {
		if (bool.containsKey(name))
			throw new EngineInitializationException("Boolean parameter" + name
					+ " has already been registered!");
		booleanParameters.add(parameter);
		int pos = booleanParameters.size() - 1;
		bool.put(name, pos);
	}

	/**
	 * Registers an enumerated parameter with a name.
	 * 
	 * @param name
	 *            The name of the parameter
	 * @param parameter
	 *            The reference to a parameter
	 */

	public void registerEnumerated(String name, Enumeration enumeration,
			EnumeratedParameter parameter) {
		if (enumerated.containsKey(name))
			throw new EngineInitializationException("Enumerated parameter"
					+ name + " has already been registered!");
		enumeratedParameters.add(parameter);
		int pos = enumeratedParameters.size() - 1;
		enumerated.put(name, pos);
		enumerations.add(enumeration);
	}

	/**
	 * @return The number of decimal parameters
	 */
	public int getDecimalSize() {
		return decimalParameters.size();
	}

	/**
	 * @return The number of boolean parameters
	 */
	public int getBooleanSize() {
		return booleanParameters.size();
	}

	/**
	 * @return The number of enumerated parameters
	 */
	public int getEnumeratedSize() {
		return enumeratedParameters.size();
	}

	/**
	 * Get the position of a decimal parameter. The parameters value can then be
	 * set using that position
	 * 
	 * @param name
	 *            The name of the parameter
	 * @return The position of the parameter
	 */
	public int getDecimalPosition(String name) {
		if (!decimal.containsKey(name))
			throw new EngineInitializationException("The decimal parameter "
					+ name + " has not been registered!");
		else
			return decimal.get(name);
	}

	/**
	 * Get the position of a boolean parameter. The parameters value can then be
	 * set using that position
	 * 
	 * @param name
	 *            The name of the parameter
	 * @return The position of the parameter
	 */

	public int getBooleanPosition(String name) {
		if (!bool.containsKey(name))
			throw new EngineInitializationException("The boolean parameter "
					+ name + " has not been registered!");
		else
			return bool.get(name);
	}

	/**
	 * Get the position of an enumerated parameter. The parameters value can
	 * then be set using that position
	 * 
	 * @param name
	 *            The name of the parameter
	 * @return The position of the parameter
	 */

	public int getEnumeratedPosition(String name) {
		if (!enumerated.containsKey(name))
			throw new EngineInitializationException("The enumerated parameter "
					+ name + " has not been registered!");
		else
			return enumerated.get(name);
	}

	/**
	 * Set the value of a decimal parameter
	 * 
	 * @param pos
	 *            The parameters position. Can be obtained via
	 *            <code>getDecimalPosition()</code>
	 * @param value
	 *            The parameter's value to set
	 */
	public void setDecimal(int pos, double value) {
		decimalParameters.get(pos).set(value);
	}

	/**
	 * Set the value of a boolean parameter
	 * 
	 * @param pos
	 *            The parameters position. Can be obtained via
	 *            <code>getBooleanPosition()</code>
	 * @param value
	 *            The parameter's value to set
	 */

	public void setBoolean(int pos, boolean value) {
		booleanParameters.get(pos).set(value);
	}

	/**
	 * Set the value of an enumerated parameter
	 * 
	 * @param pos
	 *            The parameters position. Can be obtained via
	 *            <code>getEnumeratedPosition()</code>
	 * @param value
	 *            The parameter's value to set
	 */

	public void setEnumerated(int pos, Object value) {
		enumeratedParameters.get(pos).set(value);
	}

	public Parameters(DebugMessages debug) {
		this.debug = debug;
	}

	/**
	 * Returns the name of a boolean parameter
	 * 
	 * @param position
	 *            The position of the parameter
	 * @return
	 */
	public String getBooleanName(int position) {
		for (String s : bool.keySet()) {
			if (bool.get(s).equals(position))
				return s;
		}
		throw new IllegalArgumentException();
	}

	/**
	 * Returns the name of a decimal parameter
	 * 
	 * @param position
	 *            The position of the parameter
	 * @return
	 */
	public String getDecimalName(int position) {
		for (String s : decimal.keySet()) {
			if (decimal.get(s).equals(position))
				return s;
		}
		throw new IllegalArgumentException();
	}

	/**
	 * Returns the name of an enumerated parameter
	 * 
	 * @param position
	 *            The position of the parameter
	 * @return
	 */
	public String getEnumeratedName(int position) {
		for (String s : enumerated.keySet()) {
			if (enumerated.get(s).equals(position))
				return s;
		}
		throw new IllegalArgumentException();
	}

	/**
	 * @throws SymbolNotRegisteredException
	 *             if the parameter was not registered
	 * @param parameterName
	 * @return The enumeration which is the domain of the parameter
	 */
	public Enumeration getEnumeration(String parameterName) {

		if (!enumerated.containsKey(parameterName)) {
			throw new SymbolNotRegisteredException(
					"Enumerated option parameter " + parameterName
							+ " does not exist");

		} else

			return enumerations.get(enumerated.get(parameterName));
	}

	// Set all parameters to default values
	public void reset() {

		ListIterator<BooleanParameter> boolIter = this.booleanParameters
				.listIterator();

		while (boolIter.hasNext()) {
			boolIter.next().set(false);
		}

		ListIterator<DecimalParameter> decimalIter = this.decimalParameters
				.listIterator();

		while (decimalIter.hasNext()) {
			decimalIter.next().set(0);
		}

		for (int i = 0; i < enumeratedParameters.size(); i++) {
			enumeratedParameters.get(i).set(enumerations.get(i));
		}

	}
}
