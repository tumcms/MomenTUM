/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabsl.symbols;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.xabsl.jxabsl.utils.DebugMessages;

/**
 * An enumeration. Maps a name to a unique object and an ordinal. Use the format
 * <code> "element" </code> for names, not <code> "enumeration.element" </code>
 * (as it is given in the intermediate code).
 * 
 */
public class Enumeration extends NamedItem {

	private Map<Object, String> byReference = new HashMap<Object, String>();
	private Map<String, Object> byName = new HashMap<String, Object>();
	private List<String> byOrdinal = new ArrayList<String>();

	/**
	 * Constructor
	 * 
	 * @param name
	 *            the enumeration's name
	 * @param debug
	 *            For debugging output
	 */
	public Enumeration(String name, DebugMessages debug) {
		super(name, debug);
	}

	/**
	 * return an element's unique reference
	 */
	public Object getElement(String name) {
		return byName.get(name);
	}

	/**
	 * return an elements name by its unique reference
	 */
	public String getElementName(Object element) {
		return byReference.get(element);
	}

	/**
	 * return an element's name by its ordinal
	 */
	public String getElementName(int ordinal) {
		return byOrdinal.get(ordinal);
	}

	/**
	 * 
	 * @return the size of the enumeration
	 */
	public int getNrElements() {
		return byOrdinal.size();
	}

	/**
	 * Adds an element to the enumeration. The elements' ordinal is assigned in
	 * the sequence in which they are added
	 * 
	 * @param name
	 *            the element's name
	 * @param reference
	 *            a reference which no other element in the enumeration shares
	 */
	public void add(String name, Object reference) {

		byName.put(name, reference);
		byReference.put(reference, name);
		byOrdinal.add(name);

	}

	/**
	 * For convenience. The added elements reference is the reference to the
	 * string s
	 * 
	 */
	public void add(String s) {

		add(s, s);
	}

	@Override
	public String toString() {
		return "Enumeration:" + getName();
	}

}
