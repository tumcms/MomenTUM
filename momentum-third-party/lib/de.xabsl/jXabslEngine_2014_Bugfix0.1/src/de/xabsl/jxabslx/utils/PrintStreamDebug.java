/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabslx.utils;

import java.io.PrintStream;

import de.xabsl.jxabsl.utils.DebugMessages;

/**
 * Prints debug messages to a PrintStream
 */
public class PrintStreamDebug implements DebugMessages {

	private PrintStream messages;
	private PrintStream warnings;

	/**
	 * Constructor.
	 * 
	 * @param messages
	 *            Initialization/execution messages are printed here. May be
	 *            null.
	 * 
	 * @param warnings
	 *            Warnings are printed here. May be null.
	 */
	public PrintStreamDebug(PrintStream messages, PrintStream warnings) {
		this.messages = messages;

		this.warnings = warnings;
	}


	// (Java 6) @Override
	public void printlnInit(String message) {
		if (messages != null)
			messages.println("ENGINE INITIALIZATION: " + message);
	}

	// (Java 6) @Override
	public void printlnWarning(String message) {
		if (warnings != null)
			warnings.println("WARNING: " + message);

	}
}
