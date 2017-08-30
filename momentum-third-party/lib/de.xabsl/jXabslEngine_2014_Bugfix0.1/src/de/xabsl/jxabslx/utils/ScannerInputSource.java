/**
 * XABSL Java implementation
 * 
 * @author Moritz Wissenbach (m.wissenbach@stud.tu-darmstadt.de)
 */

package de.xabsl.jxabslx.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.channels.ReadableByteChannel;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Pattern;

import de.xabsl.jxabsl.utils.InputSource;

/**
 * InputSource implementation utilizing java.util.Scanner. Reads intermediate
 * code from a variety of sources. Ignores C-style <code> //....EOL  </code>
 * comments.
 */

public class ScannerInputSource implements InputSource {

	private Scanner scanner;

	/* ---------------- public constructors ------------------------------------ */

	public ScannerInputSource(File file) throws FileNotFoundException {
		this(new Scanner(file));
	}

	public ScannerInputSource(InputStream inputStream) {
		this(new Scanner(inputStream));
	}

	public ScannerInputSource(Readable readable) {
		this(new Scanner(readable));
	}

	public ScannerInputSource(ReadableByteChannel readableByteChannel) {
		this(new Scanner(readableByteChannel));
	}

	public ScannerInputSource(String string) {
		this(new Scanner(string));
	}

	public ScannerInputSource(File file, String charsetName)
			throws FileNotFoundException {
		this(new Scanner(file, charsetName));
	}

	public ScannerInputSource(InputStream inputStream, String charsetName) {
		this(new Scanner(inputStream, charsetName));
	}

	public ScannerInputSource(ReadableByteChannel readableByteChannel,
			String charsetName) {
		this(new Scanner(readableByteChannel, charsetName));

	}

	/* --------------- end public constructors -------------------------------- */

	private ScannerInputSource(Scanner input) {

		this.scanner = input;

		// We want to use decimal dots.
		scanner.useLocale(Locale.US);

		// ignore whitespace and comments.
		this.scanner.useDelimiter(Pattern.compile(
				"(\\p{javaWhitespace}|//.*$)+", Pattern.MULTILINE));

	}

	public String next() {
		return scanner.next();
	}

	public double nextDouble() {
		return scanner.nextDouble();
	}

	public int nextInt() {
		return scanner.nextInt();
	}

	public boolean nextBoolean() {
		return scanner.nextBoolean();
	}

	@Override
	protected void finalize() throws Throwable {
		scanner.close();
		super.finalize();
	}

}
