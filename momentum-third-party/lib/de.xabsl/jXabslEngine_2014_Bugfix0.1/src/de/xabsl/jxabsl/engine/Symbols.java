/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabsl.engine;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.xabsl.jxabsl.EngineInitializationException;
import de.xabsl.jxabsl.symbols.BooleanInputSymbol;
import de.xabsl.jxabsl.symbols.BooleanOutputSymbol;
import de.xabsl.jxabsl.symbols.DecimalInputSymbol;
import de.xabsl.jxabsl.symbols.DecimalOutputSymbol;
import de.xabsl.jxabsl.symbols.EnumeratedInputSymbol;
import de.xabsl.jxabsl.symbols.EnumeratedOutputSymbol;
import de.xabsl.jxabsl.symbols.Enumeration;
import de.xabsl.jxabsl.utils.DebugMessages;

/**
 * Handles the symbols of the engine.
 */

public class Symbols {

	protected DebugMessages debug;

	private Map<String, DecimalInputSymbol> decimalInputSymbols = new HashMap<String, DecimalInputSymbol>();

	private Map<String, EnumeratedInputSymbol> enumeratedInputSymbols = new HashMap<String, EnumeratedInputSymbol>();

	private Map<String, BooleanInputSymbol> booleanInputSymbols = new HashMap<String, BooleanInputSymbol>();

	private Map<String, DecimalOutputSymbol> decimalOutputSymbols = new HashMap<String, DecimalOutputSymbol>();

	private Map<String, BooleanOutputSymbol> booleanOutputSymbols = new HashMap<String, BooleanOutputSymbol>();

	private Map<String, EnumeratedOutputSymbol> enumeratedOutputSymbols = new HashMap<String, EnumeratedOutputSymbol>();

	private Map<String, Enumeration> enumerations = new HashMap<String, Enumeration>();

	/**
	 * 
	 * @param debug
	 *            A debug output stream
	 */

	public Symbols(DebugMessages debug) {
		this.debug = debug;

	}

	/**
	 * Register an enumeration
	 * 
	 * @param enumeration
	 */
	public void registerEnumeration(Enumeration enumeration) {
		enumerations.put(enumeration.getName(), enumeration);
	}

	/**
	 * Register an enumerated input symbol.
	 * 
	 * @param name
	 *            The name of the symbol
	 * @param inputSymbol
	 *            The input symbol
	 */

	public void registerEnumeratedInputSymbol(String name,
			EnumeratedInputSymbol inputSymbol) {
		enumeratedInputSymbols.put(name, inputSymbol);
		debug.printlnInit("Registering enumerated input symbol " + inputSymbol
				+ " as \"" + name + "\"");
	}

	/**
	 * Register a decimal input symbol.
	 * 
	 * @param name
	 *            The name of the symbol
	 * @param inputSymbol
	 *            The input symbol
	 */

	public void registerDecimalInputSymbol(String name,
			DecimalInputSymbol inputSymbol) {
		decimalInputSymbols.put(name, inputSymbol);
		debug.printlnInit("Registering decimal input symbol " + inputSymbol
				+ " as \"" + name + "\"");
	}

	/**
	 * Register a boolean input symbol.
	 * 
	 * @param name
	 *            The name of the symbol
	 * @param inputSymbol
	 *            The input symbol
	 */

	public void registerBooleanInputSymbol(String name,
			BooleanInputSymbol inputSymbol) {
		booleanInputSymbols.put(name, inputSymbol);
		debug.printlnInit("Registering boolean input symbol " + inputSymbol
				+ " as \"" + name + "\"");
	}

	/**
	 * Register an enumerated output symbol.
	 * 
	 * @param name
	 *            The name of the symbol
	 * @param inputSymbol
	 *            The input symbol
	 */

	public void registerEnumeratedOutputSymbol(String name,
			EnumeratedOutputSymbol outputSymbol) {
		enumeratedOutputSymbols.put(name, outputSymbol);
		debug.printlnInit("Registering enumerated output symbol "
				+ outputSymbol + " as \"" + name + "\"");
	}

	/**
	 * Register a decimal output symbol.
	 * 
	 * @param name
	 *            The name of the symbol
	 * @param inputSymbol
	 *            The input symbol
	 */

	public void registerDecimalOutputSymbol(String name,
			DecimalOutputSymbol outputSymbol) {
		decimalOutputSymbols.put(name, outputSymbol);
		debug.printlnInit("Registering decimal output symbol " + outputSymbol
				+ " as \"" + name + "\"");
	}

	/**
	 * Register a boolean output symbol.
	 * 
	 * @param name
	 *            The name of the symbol
	 * @param inputSymbol
	 *            The input symbol
	 */

	public void registerBooleanOutputSymbol(String name,
			BooleanOutputSymbol outputSymbol) {
		booleanOutputSymbols.put(name, outputSymbol);
		debug.printlnInit("Registering boolean output symbol " + outputSymbol
				+ " as \"" + name + "\"");
	}

	/**
	 * Returns a previously registered decimal input symbol
	 * 
	 * @param name
	 *            the name of the symbol
	 * @param decimalParameterNames
	 *            names of the decimal parameters
	 * @param booleanParameterNames
	 *            names of the boolean parameters
	 * @param enumeratedParameterNames
	 *            names of the enumerated parameters
	 * @return the input symbol
	 * @throws SymbolNotRegisteredException
	 *             if the symbol was not registered
	 */

	public DecimalInputSymbol getDecimalInputSymbol(String name,
			Set<String> decimalParameterNames,
			Set<String> booleanParameterNames,
			Set<String> enumeratedParameterNames) {

		// is the symbol registered?
		if (decimalInputSymbols.containsKey(name)) {
			return decimalInputSymbols.get(name);
		} else {
			throw new SymbolNotRegisteredException("No decimal input symbol \""
					+ name + "\" has been registered");
		}

	}

	/**
	 * Returns a previously registered boolean input symbol
	 * 
	 * @param name
	 *            the name of the symbol
	 * @param decimalParameterNames
	 *            names of the decimal parameters
	 * @param booleanParameterNames
	 *            names of the boolean parameters
	 * @param enumeratedParameterNames
	 *            names of the enumerated parameters
	 * @return the input symbol
	 * @throws SymbolNotRegisteredException
	 *             if the symbol was not registered
	 */

	public BooleanInputSymbol getBooleanInputSymbol(String name,
			Set<String> decimalParameterNames,
			Set<String> booleanParameterNames,
			Set<String> enumeratedParameterNames) {

		if (booleanInputSymbols.containsKey(name))
			return booleanInputSymbols.get(name);
		else
			throw new SymbolNotRegisteredException("No boolean input symbol \""
					+ name + "\" has been registered");

	}

	/**
	 * Returns a previously registered enumerated input symbol
	 * 
	 * @param name
	 *            the name of the symbol
	 * @param decimalParameterNames
	 *            names of the decimal parameters
	 * @param booleanParameterNames
	 *            names of the boolean parameters
	 * @param enumeratedParameterNames
	 *            names of the enumerated parameters
	 * @return the input symbol
	 * @throws SymbolNotRegisteredException
	 *             if the symbol was not registered
	 */

	public EnumeratedInputSymbol getEnumeratedInputSymbol(String name,
			Set<String> decimalParameterNames,
			Set<String> booleanParameterNames,
			Set<String> enumeratedParameterNames) {

		if (enumeratedInputSymbols.containsKey(name))
			return enumeratedInputSymbols.get(name);
		else {
			throw new SymbolNotRegisteredException(
					"No enumerated input symbol \"" + name
							+ "\" has been registered");
		}
	}

	/**
	 * Returns a previously registered decimal output symbol
	 * 
	 * @param name
	 *            the name of the symbol
	 * @return the output symbol
	 * @throws SymbolNotRegisteredException
	 *             if the symbol was not registered
	 */

	public DecimalOutputSymbol getDecimalOutputSymbol(String name) {
		if (decimalOutputSymbols.containsKey(name))
			return decimalOutputSymbols.get(name);
		else
			throw new SymbolNotRegisteredException(
					"No decimal output symbol \"" + name
							+ "\" has been registered");

	}

	/**
	 * Returns a previously registered boolean output symbol
	 * 
	 * @param name
	 *            the name of the symbol
	 * @return the output symbol
	 * @throws SymbolNotRegisteredException
	 *             if the symbol was not registered
	 */

	public BooleanOutputSymbol getBooleanOutputSymbol(String name) {

		if (booleanOutputSymbols.containsKey(name))
			return booleanOutputSymbols.get(name);
		else
			throw new SymbolNotRegisteredException(
					"No boolean output symbol \"" + name
							+ "\" has been registered");

	}

	/**
	 * Returns a previously registered enumerated output symbol
	 * 
	 * @param name
	 *            the name of the symbol
	 * @return the output symbol
	 * @throws SymbolNotRegisteredException
	 *             if the symbol was not registered
	 */

	public EnumeratedOutputSymbol getEnumeratedOutputSymbol(String name) {

		if (enumeratedOutputSymbols.containsKey(name))
			return enumeratedOutputSymbols.get(name);
		else
			throw new SymbolNotRegisteredException(
					"No enumerated output symbol \"" + name
							+ "\" has been registered");

	}

	/**
	 * Returns a previously registered enumeration
	 * 
	 * @param name
	 *            the name of the enumeration
	 * @return the enumeration
	 */

	public Enumeration getEnumeration(String name) {

		if (enumerations.containsKey(name))
			return enumerations.get(name);
		else
			throw new SymbolNotRegisteredException("No enumeration \"" + name
					+ "\" has been registered");

	}

	/**
	 * Registers an enum element for an internal enumeration. If the enumeration
	 * does not exist yet, it is created.
	 */
	protected void registerInternalEnumElement(String enumName, String name) {
		if (!enumerations.containsKey(enumName)) {
			enumerations.put(enumName, new Enumeration(enumName, this.debug));
		}
		if (enumerations.get(enumName).getElement(name) != null) {
			throw new EngineInitializationException("enum element " + name
					+ " for enumeration " + enumName
					+ " was already registered");
		}

		((Enumeration) enumerations.get(enumName)).add(name);
	}

	// TODO DEBUG necessary?
	// public void resetOutputSymbols();

	/**
	 * Returns a map of all registered enumerations. Intended for debugging
	 * purposes, use <code>registerEnumeration()</code> and
	 * <code>getEnumeration()</code> for all other purposes.
	 */
	public Map<String, Enumeration> getEnumerations() {
		return enumerations;
	}

	/**
	 * Returns a map of all registered decimal input symbols. Intended for
	 * debugging purposes, use <code>registerDecimalInputSymbol()</code> and
	 * <code>getDecimalInputSymbol()</code> for all other purposes.
	 */
	public Map<String, DecimalInputSymbol> getDecimalInputSymbols() {
		return decimalInputSymbols;
	}

	/**
	 * Returns a map of all registered boolean input symbols. Intended for
	 * debugging purposes, use <code>registerBooleanInputSymbol()</code> and
	 * <code>getBooleanInputSymbol()</code> for all other purposes.
	 */
	public Map<String, BooleanInputSymbol> getBooleanInputSymbols() {
		return booleanInputSymbols;
	}

	/**
	 * Returns a map of all registered enumerated input symbols. Intended for
	 * debugging purposes, use <code>registerEnumeratedInputSymbol()</code>
	 * and <code>getEnumeratedInputSymbol()</code> for all other purposes.
	 */
	public Map<String, EnumeratedInputSymbol> getEnumeratedInputSymbols() {
		return enumeratedInputSymbols;
	}

	/**
	 * Returns a map of all registered decimal output symbols. Intended for
	 * debugging purposes, use <code>registerDecimalOutputSymbol()</code> and
	 * <code>getDecimalOutputSymbol()</code> for all other purposes.
	 */
	public Map<String, DecimalOutputSymbol> getDecimalOutputSymbols() {
		return decimalOutputSymbols;
	}

	/**
	 * Returns a map of all registered boolean output symbols. Intended for
	 * debugging purposes, use <code>registerBooleanOutputSymbol()</code> and
	 * <code>getBooleanOutputSymbol()</code> for all other purposes.
	 */
	public Map<String, BooleanOutputSymbol> getBooleanOutputSymbols() {
		return booleanOutputSymbols;
	}

	/**
	 * Returns a map of all registered enumerated output symbols. Intended for
	 * debugging purposes, use <code>registerEnumeratedOutputSymbol()</code>
	 * and <code>getEnumeratedOutputSymbol()</code> for all other purposes.
	 */
	public Map<String, EnumeratedOutputSymbol> getEnumeratedOutputSymbols() {
		return enumeratedOutputSymbols;
	}

}