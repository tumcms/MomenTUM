/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabsl.utils;

/**
 * Parses XABSL intermediate code and returns tokens.
 */

public interface InputSource {

	/**
	 * Read a string
	 */
	public String next();

	/**
	 * Read an integer
	 */
	public int nextInt();

	/**
	 * Read a decimal
	 */
	public double nextDouble();

	/**
	 * Read d boolean
	 */
	public boolean nextBoolean();

}
