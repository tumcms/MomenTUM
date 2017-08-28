/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabsl.expression.enumerated;

import de.xabsl.jxabsl.EngineInitializationException;
import de.xabsl.jxabsl.IntermediateCodeMalformedException;
import de.xabsl.jxabsl.engine.SymbolNotRegisteredException;
import de.xabsl.jxabsl.engine.Symbols;
import de.xabsl.jxabsl.symbols.Enumeration;
import de.xabsl.jxabsl.utils.DebugMessages;
import de.xabsl.jxabsl.utils.InputSource;

/**
 * Represents a literal enumerated value.
 */

public class EnumeratedValue extends EnumeratedExpression {

	protected Object value;

	/**
	 * 
	 * @param enumeration
	 *            The enumeration which is the domain of this expression. May
	 *            not be null.
	 * @param element
	 *            The enumerated value.
	 * @param debug
	 */
	public EnumeratedValue(Enumeration enumeration, Object element,
			DebugMessages debug) {

		super(debug);

		this.enumeration = enumeration;

		if (enumeration == null) {
			throw new EngineInitializationException(
					"Enumerated value can not be created without specifying enumeration");
		}

		if (enumeration.getElementName(element) == null)
			throw new IllegalArgumentException("The enum element " + element
					+ " was not registered with the enumeration " + enumeration
					+ " !");

		value = element;
		debug.printlnInit("Created: " + this);
	}

	/**
	 * Constructor. Creates the value.
	 * 
	 * @param enumeration
	 *            The enumeration which is the domain of this expression. May be
	 *            null, then no check is performed.
	 * @param input
	 *            An input source for the intermediate code. It must be opened
	 *            and read until a position where the value starts. The enum
	 *            element name has to be given in the form
	 *            "Enumeration.EnumElement"
	 * @param debug
	 *            For debugging output
	 * @throws IntermediateCodeMalformedException
	 */
	public EnumeratedValue(Enumeration enumeration, InputSource input,
			Symbols symbols, DebugMessages debug)
			throws IntermediateCodeMalformedException {

		super(debug);

		String elementName = input.next();

		this.enumeration = symbols
				.getEnumeration(enumerationFromIntermediateCode(elementName));

		if (enumeration != null && enumeration != this.enumeration)
			throw new SymbolNotRegisteredException("Enumeration "
					+ this.enumeration + " does not match enumeration type "
					+ enumeration);

		value = this.enumeration
				.getElement(elementFromIntermediateCode(elementName));

		if (value == null)
			throw new SymbolNotRegisteredException("The enum element "
					+ elementName + " was not registered with the "
					+ this.enumeration + " !");

		debug.printlnInit("Created: " + this);
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public String toString() {

		return this.getClass().getSimpleName() + "[" + enumeration.getName()
				+ "." + value + "]";
	}

}
