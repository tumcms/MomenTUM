/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabsl.behavior;

import de.xabsl.jxabsl.engine.SymbolNotRegisteredException;
import de.xabsl.jxabsl.engine.Symbols;
import de.xabsl.jxabsl.parameters.BooleanParameter;
import de.xabsl.jxabsl.parameters.DecimalParameter;
import de.xabsl.jxabsl.parameters.EnumeratedParameter;
import de.xabsl.jxabsl.parameters.Parameters;
import de.xabsl.jxabsl.utils.DebugMessages;
import de.xabsl.jxabsl.utils.InputSource;

/**
 * Represents the current set of parameters of an option or basic behavior
 */
public class OptionParameters extends Parameters {

	// Stores the values
	double[] decimalValues;
	boolean[] booleanValues;
	Object[] enumeratedValues;

	/**
	 * Constructor.
	 * 
	 * @param input
	 *            An input source for the intermediate code. It must be opened
	 *            and read until A position where an option starts.
	 * @param debug
	 *            For printing debug information
	 * @param symbols
	 *            All available symbols
	 */

	public OptionParameters(InputSource input, DebugMessages debug,
			Symbols symbols) {
		super(debug);
		int numberOfOptionParameters = input.nextInt();

		// initialize with max. size, a little waste oh well
		decimalValues = new double[numberOfOptionParameters];
		booleanValues = new boolean[numberOfOptionParameters];
		enumeratedValues = new Object[numberOfOptionParameters];

		// count
		int decimalNr = 0;
		int booleanNr = 0;
		int enumeratedNr = 0;

		for (int i = 0; i < numberOfOptionParameters; i++) {
			String type = input.next();
			String parameterName;
			switch (type.getBytes()[0]) {

			case 'd': {
				parameterName = input.next();
				debug.printlnInit("Registering decimal option parameter "
						+ parameterName);
				final int position = decimalNr++;

				DecimalParameter p = new DecimalParameter() {
					private int pos = position;

					public void set(double value) {
						decimalValues[pos] = value;
					}
				};

				registerDecimal(parameterName, p);
				break;
			}

			case 'b': {
				parameterName = input.next();
				debug.printlnInit("Registering boolean option parameter "
						+ parameterName);

				final int position = booleanNr++;

				BooleanParameter p = new BooleanParameter() {
					private int pos = position;

					public void set(boolean value) {
						booleanValues[pos] = value;
					}
				};

				registerBoolean(parameterName, p);
				break;
			}
			case 'e': {
				String enumerationName = input.next();

				// check if the enumeration is registered
				symbols.getEnumeration(enumerationName);

				de.xabsl.jxabsl.symbols.Enumeration enumeration = symbols
						.getEnumeration(enumerationName);

				if (enumeration.getNrElements() == 0)
					throw new SymbolNotRegisteredException(
							"No enumeration elements for " + enumeration
									+ "were registered");

				parameterName = input.next();

				debug.printlnInit("Registering enumerated option parameter "
						+ parameterName);

				final int position = enumeratedNr++;

				EnumeratedParameter p = new EnumeratedParameter() {
					private int pos = position;

					public void set(Object value) {
						enumeratedValues[pos] = value;
					}
				};

				registerEnumerated(parameterName, enumeration, p);
				break;

			}

			}

		}
	}

	/**
	 * Returns the value for a decimal parameter
	 * 
	 * @param i
	 *            The position of the parameter as given by
	 *            <code>getDecimalPosition(name) </code>
	 */
	public double getDecimal(int i) {
		return decimalValues[i];
	}

	/**
	 * Returns the value for a boolean parameter
	 * 
	 * @param i
	 *            The position of the parameter as given by
	 *            <code>getBooleanPosition(name) </code>
	 */

	public boolean getBoolean(int i) {
		return booleanValues[i];
	}

	/**
	 * Returns the value for an enumerated parameter
	 * 
	 * @param i
	 *            The position of the parameter as given by
	 *            <code>getEnumeratedPosition(name) </code>
	 */

	public Object getEnumerated(int i) {
		return enumeratedValues[i];
	}

}
