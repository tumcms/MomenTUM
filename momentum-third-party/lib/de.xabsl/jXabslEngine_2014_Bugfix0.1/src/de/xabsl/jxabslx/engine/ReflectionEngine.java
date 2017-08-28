/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabslx.engine;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.xabsl.jxabsl.EngineInitializationException;
import de.xabsl.jxabsl.TimeFunction;
import de.xabsl.jxabsl.behavior.BasicBehavior;
import de.xabsl.jxabsl.engine.Engine;
import de.xabsl.jxabsl.engine.SymbolNotRegisteredException;
import de.xabsl.jxabsl.symbols.BooleanInputSymbol;
import de.xabsl.jxabsl.symbols.BooleanOutputSymbol;
import de.xabsl.jxabsl.symbols.DecimalInputSymbol;
import de.xabsl.jxabsl.symbols.DecimalOutputSymbol;
import de.xabsl.jxabsl.symbols.EnumeratedInputSymbol;
import de.xabsl.jxabsl.symbols.EnumeratedOutputSymbol;
import de.xabsl.jxabsl.symbols.Enumeration;
import de.xabsl.jxabsl.utils.DebugMessages;
import de.xabsl.jxabslx.conversions.BooleanConversion;
import de.xabsl.jxabslx.conversions.Conversions;
import de.xabsl.jxabslx.conversions.DecimalConversion;
import de.xabsl.jxabslx.conversions.EnumeratedConversion;
import de.xabsl.jxabslx.io.Input;
import de.xabsl.jxabslx.io.InputFromField;
import de.xabsl.jxabslx.io.InputFromMethod;
import de.xabsl.jxabslx.io.MethodBasicBehavior;
import de.xabsl.jxabslx.io.Output;
import de.xabsl.jxabslx.io.OutputToField;
import de.xabsl.jxabslx.io.OutputToSetterMethod;
import de.xabsl.jxabslx.symbols.BooleanInputSymbolImpl;
import de.xabsl.jxabslx.symbols.BooleanOutputSymbolImpl;
import de.xabsl.jxabslx.symbols.DecimalInputSymbolImpl;
import de.xabsl.jxabslx.symbols.DecimalOutputSymbolImpl;
import de.xabsl.jxabslx.symbols.EnumeratedInputSymbolImpl;
import de.xabsl.jxabslx.symbols.EnumeratedOutputSymbolImpl;
import de.xabsl.jxabslx.symbols.JavaEnumeration;
import de.xabsl.jxabslx.utils.ClassUtils;
import de.xabsl.jxabslx.utils.ClassUtils.XabslType;

/**
 * An engine that looks up input- and output symbols and basic behaviors in a
 * registered java object ("agent").
 */
public class ReflectionEngine extends Engine {

	private Object agent;

	private static String[] emptyParameterNames = new String[0];

	private Comparator<String> parameterOrder = null;

	/**
	 * Constructor. Register an object with the <code>agent</code> parameter.
	 * On option graph construction, the engine will look at fields and methods of the
	 * registered object to find matches for any unregistered input- and output
	 * symbols or basic behaviors. <br>
	 * For any missing input- or output symbol <code>foo</code> the engine
	 * will look for the following, in this order: <br>
	 * <ol>
	 * <li> Getter-/setter methods, i.e. methods that have the name
	 * <code>foo</code> / <code> getFoo</code> or <code> setFoo</code>.
	 * Parameter list must match. One and only one method must match, or the
	 * engine will issue an error</li>
	 * <li> A publicly accessible field <code>foo</code></li>
	 * </ol>
	 * For any missing basic behavior <code>foo</code>, the engine will look
	 * for a method called <code> foo </code> with an appropriate parameter
	 * list. One and only one method must match, or the engine will issue an
	 * error.<br>
	 * <br>
	 * If an object of type <code> java.lang.Class</code> is passed as an
	 * argument, the engine will look for static fields and methods.
	 * 
	 * @param debug
	 *            for debugging output
	 * @param timeFunction
	 *            a function that supplies the system time in ms.
	 * @param agent
	 *            An object which contains input-, output symbols and basic
	 *            behaviors. If the object is of type class, static fields and
	 *            methods will be looked for in that class.
	 */

	//TODO Add convenience methods/constructors for system time, file input, debugging
	public ReflectionEngine(DebugMessages debug, TimeFunction timeFunction,
			Object agent) {
		super(debug, timeFunction);
		this.agent = agent;

	}

	@Override
	public BooleanInputSymbol getBooleanInputSymbol(String name,
			Set<String> decimalParameterNames,
			Set<String> booleanParameterNames,
			Set<String> enumeratedParameterNames) {
		try {
			return super.getBooleanInputSymbol(name, decimalParameterNames,
					booleanParameterNames, enumeratedParameterNames);
		} catch (SymbolNotRegisteredException ex) {

			// the symbol has not been registered, look for it
			BooleanInputSymbol symbol = (BooleanInputSymbol) getInputSymbol(
					XabslType.bool, name, decimalParameterNames,
					booleanParameterNames, enumeratedParameterNames);
			registerBooleanInputSymbol(name, symbol);
			return symbol;
		}
	}

	@Override
	public DecimalInputSymbol getDecimalInputSymbol(String name,
			Set<String> decimalParameterNames,
			Set<String> booleanParameterNames,
			Set<String> enumeratedParameterNames) {
		try {
			return super.getDecimalInputSymbol(name, decimalParameterNames,
					booleanParameterNames, enumeratedParameterNames);
		} catch (SymbolNotRegisteredException ex) {

			// the symbol has not been registered, look for it
			DecimalInputSymbol symbol = (DecimalInputSymbol) getInputSymbol(
					XabslType.decimal, name, decimalParameterNames,
					booleanParameterNames, enumeratedParameterNames);
			registerDecimalInputSymbol(name, symbol);
			return symbol;
		}

	}

	@Override
	public EnumeratedInputSymbol getEnumeratedInputSymbol(String name,
			Set<String> decimalParameterNames,
			Set<String> booleanParameterNames,
			Set<String> enumeratedParameterNames) {
		try {
			return super.getEnumeratedInputSymbol(name, decimalParameterNames,
					booleanParameterNames, enumeratedParameterNames);
		} catch (SymbolNotRegisteredException ex) {
			// the symbol has not been registered, look for it
			EnumeratedInputSymbol symbol = (EnumeratedInputSymbol) getInputSymbol(
					XabslType.enumerated, name, decimalParameterNames,
					booleanParameterNames, enumeratedParameterNames);
			registerEnumeratedInputSymbol(name, symbol);
			return symbol;
		}

	}

	@Override
	public BooleanOutputSymbol getBooleanOutputSymbol(String name) {
		try {
			return super.getBooleanOutputSymbol(name);
		} catch (SymbolNotRegisteredException ex) {
			// the symbol has not been registered, look for it
			BooleanOutputSymbol symbol = (BooleanOutputSymbol) getOutputSymbol(
					XabslType.bool, name);
			registerBooleanOutputSymbol(name, symbol);
			return symbol;
		}
	}

	@Override
	public DecimalOutputSymbol getDecimalOutputSymbol(String name) {
		try {
			return super.getDecimalOutputSymbol(name);
		} catch (SymbolNotRegisteredException ex) {
			// the symbol has not been registered, look for it
			DecimalOutputSymbol symbol = (DecimalOutputSymbol) getOutputSymbol(
					XabslType.decimal, name);
			registerDecimalOutputSymbol(name, symbol);
			return symbol;
		}

	}

	@Override
	public EnumeratedOutputSymbol getEnumeratedOutputSymbol(String name) {
		try {
			return super.getEnumeratedOutputSymbol(name);
		} catch (SymbolNotRegisteredException ex) {
			// the symbol has not been registered, look for it
			EnumeratedOutputSymbol symbol = (EnumeratedOutputSymbol) getOutputSymbol(
					XabslType.enumerated, name);
			registerEnumeratedOutputSymbol(name, symbol);
			return symbol;
		}

	}

	@Override
	public BasicBehavior getBasicBehavior(String name,
			Set<String> decimalParameterNames,
			Set<String> booleanParameterNames,
			Set<String> enumeratedParameterNames) {

		try {
			return super.getBasicBehavior(name, decimalParameterNames,
					booleanParameterNames, enumeratedParameterNames);
		} catch (SymbolNotRegisteredException ex) {

			// try to find an apt function
			// Find possible methods
			List<Method> methods = ClassUtils.findMethods(agent, name, null,
					decimalParameterNames.size(), booleanParameterNames.size(),
					enumeratedParameterNames.size());

			if (methods.size() == 1) {

				// Found a method
				// Order parameter names
				String[] orderedParameterNames = orderParameterNames(methods
						.get(0), decimalParameterNames, booleanParameterNames,
						enumeratedParameterNames);

				if (orderedParameterNames == null)
					throw new EngineInitializationException(
							"Cannot determine parameter order for basic behavior "
									+ name + ", register manually");

				MethodBasicBehavior behavior = new MethodBasicBehavior(name,
						methods.get(0), orderedParameterNames, agent, this,
						debug);
				registerBasicBehavior(behavior);
				return behavior;

			} else {

				// 0 or >2 methods
				// ambiguous
				throw new EngineInitializationException(
						"Found "
								+ +methods.size()
								+ " methods while looking for "
								+ "basic behavior "
								+ name
								+ ". There must be one and only one method that matches. Candidates are: "
								+ methods);
			}

		}
	}

	private Object getInputSymbol(XabslType type, String name,
			Set<String> decimalParameterNames,
			Set<String> booleanParameterNames,
			Set<String> enumeratedParameterNames) {

		if (decimalParameterNames.size() + booleanParameterNames.size()
				+ enumeratedParameterNames.size() == 0) {

			// no parameters, look for field

			List<Field> fields = ClassUtils.findFields(agent, name, type);

			if (fields.size() > 1)

				// ambiguous

				throw new EngineInitializationException(
						"Found "
								+ fields.size()
								+ " fields while looking for "
								+ type
								+ " input symbol "
								+ name
								+ ". There must be one and only one field that matches. Candidates are: "
								+ fields);

			else if (fields.size() == 1)

				return createInputSymbol(type, new InputFromField(
						fields.get(0), agent), fields.get(0).getType(),
						emptyParameterNames, debug);

		}

		// Find possible methods
		List<Method> methods = ClassUtils.findMethods(agent, name, type,
				decimalParameterNames.size(), booleanParameterNames.size(),
				enumeratedParameterNames.size());

		// Add "getter" methods
		String getterName = "get" + name.substring(0, 1).toUpperCase()
				+ name.substring(1, name.length());

		methods.addAll(ClassUtils.findMethods(agent, getterName, type,
				decimalParameterNames.size(), booleanParameterNames.size(),
				enumeratedParameterNames.size()));

		if (methods.size() == 1) {

			// Found a method
			// Order parameter names
			String[] orderedParameterNames = orderParameterNames(
					methods.get(0), decimalParameterNames,
					booleanParameterNames, enumeratedParameterNames);

			if (orderedParameterNames == null)
				throw new EngineInitializationException(
						"Cannot determine parameter order for " + type
								+ " input symbol " + name
								+ ", register this symbol manually");

			return createInputSymbol(type, new InputFromMethod(methods.get(0),
					agent), methods.get(0).getReturnType(),
					orderedParameterNames, debug);

		} else {

			// 0 or >2 methods
			// ambiguous
			throw new EngineInitializationException(
					"Found 0 fields and "
							+ methods.size()
							+ " methods while looking for "
							+ type
							+ " input symbol "
							+ name
							+ ". There must be one and only one method that matches. Candidates are: "
							+ methods);
		}

	}

	@Override
	public Enumeration getEnumeration(String name) {
		try {
			return super.getEnumeration(name);
		} catch (SymbolNotRegisteredException ex) {

			Class<?>[] classes = agent.getClass().getClasses();
			for (Class<?> c : classes) {
				if (c.isEnum() && c.getSimpleName().equals(name)) {
					Enumeration e = new JavaEnumeration(name, c, this.debug);
					registerEnumeration(e);
					return (e);
				}
			}

			throw new EngineInitializationException("No enumeration " + name
					+ " has been registered and none could be found in "
					+ agent);
		}
	}

	private Object getOutputSymbol(XabslType type, String name) {

		// no parameters, look for field

		List<Field> fields = ClassUtils.findFields(agent, name, type);

		if (fields.size() > 1)

			// ambiguous

			throw new EngineInitializationException(
					"Found "
							+ fields.size()
							+ " fields while looking for "
							+ type
							+ " output symbol "
							+ name
							+ ". There must be one and only one field that matches. Candidates are: "
							+ fields);

		else if (fields.size() == 1)

			return createOutputSymbol(type, new InputFromField(fields.get(0),
					agent), new OutputToField(fields.get(0), agent), fields
					.get(0).getType(), debug);

		// Find possible methods

		// Find "getter" methods; methods to get the value: value() or
		// getValue()

		List<Method> getterMethods = ClassUtils.findMethods(agent, name, type,
				0, 0, 0);

		String getterName = "get" + name.substring(0, 1).toUpperCase()
				+ name.substring(1, name.length());

		getterMethods.addAll(ClassUtils.findMethods(agent, getterName, type, 0,
				0, 0));

		// Find "setter" methods
		String setterName = "set" + name.substring(0, 1).toUpperCase()
				+ name.substring(1, name.length());

		List<Method> setterMethods = ClassUtils.findMethods(agent, setterName,
				null, type == XabslType.decimal ? 1 : 0,
				type == XabslType.bool ? 1 : 0,
				type == XabslType.enumerated ? 1 : 0);

		Method getterMethod, setterMethod;

		if (getterMethods.size() == 1) {

			// Found a method

			getterMethod = getterMethods.get(0);

		} else {

			// 0 or >2 methods
			// ambiguous
			throw new EngineInitializationException(
					"Found 0 fields and "
							+ getterMethods.size()
							+ " getter methods while looking for "
							+ type
							+ " output symbol "
							+ name
							+ ". An output symbol can be read from, you must provide a method or field. There must be one and only one method that matches. Candidates are: "
							+ getterMethods);
		}

		if (setterMethods.size() == 1) {

			// Found a method

			setterMethod = setterMethods.get(0);

		} else {

			// 0 or >2 methods
			// ambiguous
			throw new EngineInitializationException(
					"Found 0 fields and "
							+ setterMethods.size()
							+ " setter methods while looking for "
							+ type
							+ " output symbol "
							+ name
							+ ". There must be one and only one method that matches. Candidates are: "
							+ setterMethods);
		}

		if (!getterMethod.getReturnType().equals(
				setterMethod.getParameterTypes()[0])) {
			throw new EngineInitializationException("Found a getter method "
					+ getterMethod + " and a setter method " + setterMethod
					+ " for " + type + " output symbol " + name
					+ ", but their types do not match");
		}

		return createOutputSymbol(type,
				new InputFromMethod(getterMethod, agent),
				new OutputToSetterMethod(setterMethod, agent), getterMethod
						.getReturnType(), debug);

	}

	/**
	 * Match parameter names by ordering the lists.
	 * 
	 * @param method
	 * @param decimalParameterNames
	 * @param booleanParameterNames
	 * @param enumeratedParameterNames
	 * @return Null if no unambiguous order can be found
	 */
	private String[] orderParameterNames(Method method,
			Set<String> decimalParameterNames,
			Set<String> booleanParameterNames,
			Set<String> enumeratedParameterNames) {

		List<String> orderedDecimal = new ArrayList<String>(
				decimalParameterNames);
		List<String> orderedBoolean = new ArrayList<String>(
				booleanParameterNames);
		List<String> orderedEnumerated = new ArrayList<String>(
				enumeratedParameterNames);

		// Can we map the parameters unambiguously?
		if (decimalParameterNames.size() > 1
				|| booleanParameterNames.size() > 1
				|| enumeratedParameterNames.size() > 1) {

			if (parameterOrder == null)
				// We don't know how to order
				return null;

			// sort according to parameterOrder

			Collections.sort(orderedDecimal, parameterOrder);
			Collections.sort(orderedBoolean, parameterOrder);
			Collections.sort(orderedEnumerated, parameterOrder);
		}

		Class<?>[] parameterTypes = method.getParameterTypes();

		List<String> orderedParameterNames = new ArrayList<String>();

		Iterator<String> di = orderedDecimal.iterator();
		Iterator<String> bi = orderedBoolean.iterator();
		Iterator<String> ei = orderedEnumerated.iterator();

		for (int i = 0; i < parameterTypes.length; i++) {
			switch (Conversions.javaTypeToXabslType(parameterTypes[i])) {
			case decimal:
				orderedParameterNames.add(di.next());
				break;

			case bool:
				orderedParameterNames.add(bi.next());
				break;

			case enumerated:
				orderedParameterNames.add(ei.next());
				break;

			}

		}

		return orderedParameterNames.toArray(new String[0]);
	}

	private Object createOutputSymbol(XabslType type, Input input,
			Output output, Class fromType, DebugMessages debug) {
		switch (type) {

		case decimal: {
			DecimalConversion conversion = Conversions
					.getDecimalConversion(fromType);
			return new DecimalOutputSymbolImpl(output, input, conversion, debug);
		}

		case bool: {
			BooleanConversion conversion = Conversions
					.getBooleanConversion(fromType);
			return new BooleanOutputSymbolImpl(output, input, conversion, debug);

		}

		case enumerated: {
			EnumeratedConversion conversion = Conversions
					.getEnumeratedConversion(fromType);
			return new EnumeratedOutputSymbolImpl(null, output, input,
					conversion, debug);

		}

		default:
			throw new IllegalArgumentException(
					"type must be one of decimal, bool, enumerated");
		}
	}

	private Object createInputSymbol(XabslType type, Input input,
			Class<?> fromType, String[] parameterNames, DebugMessages debug) {
		switch (type) {

		case decimal: {
			DecimalConversion conversion = Conversions
					.getDecimalConversion(fromType);
			return new DecimalInputSymbolImpl(input, conversion,
					parameterNames, this, debug);
		}

		case bool: {
			BooleanConversion conversion = Conversions
					.getBooleanConversion(fromType);
			return new BooleanInputSymbolImpl(input, conversion,
					parameterNames, this, debug);
		}

		case enumerated: {
			EnumeratedConversion conversion = Conversions
					.getEnumeratedConversion(fromType);
			return new EnumeratedInputSymbolImpl(getEnumeration(conversion
					.getEnumerationName(fromType)), input, conversion,
					parameterNames, this, debug);

		}

		default:
			throw new IllegalArgumentException(
					"type must be one of decimal, bool, enumerated");
		}
	}

	/**
	 * Defines a mapping of Java method parameters (given by position) and XABSL
	 * parameters (given by name)
	 * 
	 */
	public void setParameterOrder(Comparator<String> order) {
		this.parameterOrder = order;
	}
}
