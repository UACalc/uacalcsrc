package org.uacalc.io;

import java.io.*;
import java.util.*;
import org.uacalc.alg.*;
import org.uacalc.alg.op.*;

/**
 * Reading Mace4 model files into algebras (only the operations are loaded,
 * relations are ignored).
 */
public final class Mace4Reader {
	private BufferedReader reader;

	public Mace4Reader(InputStream stream) throws IOException {
		this.reader = new BufferedReader(new InputStreamReader(stream, "UTF8"));
		lineno = 0;
	}

	private String line;
	private int lineno;
	private int index;

	char peekChar() {
		if (line == null)
			return 0;
		else if (index < line.length())
			return line.charAt(index);
		else
			return '\n';
	}

	void nextChar() throws IOException {
		if (line == null)
			;
		else if (index < line.length())
			index += 1;
		else {
			line = reader.readLine();
			lineno += 1;
			index = 0;
		}
	}

	char getChar() throws IOException {
		eatSpaces();
		char c = peekChar();
		nextChar();
		return c;
	}

	void eatSpaces() throws IOException {
		while (Character.isWhitespace(peekChar()))
			nextChar();
	}

	void error(String message) throws BadAlgebraFileException {
		throw new BadAlgebraFileException(message + " at line " + lineno
				+ " column " + (1 + index));
	}

	void eatChar(char c) throws IOException, BadAlgebraFileException {
		char d = getChar();
		if (c != d)
			error("Character " + c + " is expected");
	}

	int parseNumber() throws IOException, BadAlgebraFileException {
		char c = getChar();
		if (!Character.isDigit(c))
			error("Invalid number");
		long a = c - '0';

		for (;;) {
			c = peekChar();

			if (!Character.isDigit(c))
				break;

			a *= 10;
			a += c - '0';

			if (a > Integer.MAX_VALUE)
				error("Too large integer");

			nextChar();
		}

		return (int) a;
	}

	public static boolean isOrdinaryCharacter(char c) {
		return ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z') || c == '$'
				|| c == '_';
	}

	private static final String SPECIAL_CHARS = "{+-*/\\^<>=`~?@&|!#';}";

	public static boolean isSpecialCharacter(char c) {
		return SPECIAL_CHARS.indexOf(c) >= 0;
	}

	String parseSymbol() throws IOException, BadAlgebraFileException {
		StringBuffer b = new StringBuffer();

		char c = getChar();
		boolean ordinary = false;

		if (isOrdinaryCharacter(c))
			ordinary = true;
		else if (!isSpecialCharacter(c))
			error("Invalid symbol character");

		b.append(c);

		for (;;) {
			c = peekChar();
			if ((ordinary && !isOrdinaryCharacter(c) && !Character.isDigit(c))
					|| (!ordinary && !isSpecialCharacter(c)))
				break;

			b.append(c);
			nextChar();
		}

		return b.toString();
	}

	int[] parseNumberTable() throws IOException, BadAlgebraFileException {
		ArrayList<Integer> table = new ArrayList<Integer>();

		eatChar('[');
		eatSpaces();
		if (peekChar() != ']') {
			for (;;) {
				table.add(parseNumber());

				char c = getChar();
				if (c == ']')
					break;
				else if (c != ',')
					error("Comma is expected");
			}
		}

		int[] array = new int[table.size()];
		for (int i = 0; i < array.length; i++)
			array[i] = table.get(i);

		return array;
	}

	void eatBlock(char b, char e) throws IOException, BadAlgebraFileException {
		eatSpaces();
		eatChar(b);

		int a = 1;
		while (a > 0) {
			eatSpaces();
			int c = peekChar();

			if (c == b)
				a += 1;
			else if (c == e)
				a -= 1;

			nextChar();
		}
	}

	public SmallAlgebra parseAlgebra() throws IOException,
			BadAlgebraFileException {
		do {
			line = reader.readLine();
			lineno += 1;

			if (line == null)
				return null;
		} while (!line.startsWith("interpretation("));

		index = 0;

		String s = parseSymbol();
		assert s == "interpretation";

		eatChar('(');

		int cardinality = parseNumber();
		List<Operation> operations = new ArrayList<Operation>();

		eatChar(',');
		Map<String, Integer> stats = parseStats();
		eatChar(',');
		eatChar('[');

		if (peekChar() != ']') {
			for (;;) {
				s = parseSymbol();
				if (s.equals("function")) {
					eatChar('(');
					String opname = parseSymbol();
					int arity = parseArity();
					eatChar(',');
					int[] table = parseNumberTable();
					eatChar(')');

					operations.add(Operations.makeIntOperation(opname, arity,
							cardinality, table));
				} else
					eatBlock('(', ')');

				char c = getChar();
				if (c == ']')
					break;
				else if (c != ',')
					error("Comma is expected");
			}
		}

		eatChar(')');
		eatChar('.');

		String name = "model";
		Integer number = stats.get("number");
		if (number != null)
			name += number.toString();

		return new BasicAlgebra(name, cardinality, operations);
	}

	Map<String, Integer> parseStats() throws IOException,
			BadAlgebraFileException {
		Map<String, Integer> map = new HashMap<String, Integer>();

		eatChar('[');
		for (;;) {
			String s = parseSymbol();
			eatChar('=');
			int n = parseNumber();

			map.put(s, n);

			char c = getChar();
			if (c == ']')
				break;
			else if (c != ',')
				error("Comma is expected");
		}

		return map;
	}

	int parseArity() throws IOException, BadAlgebraFileException {
		eatSpaces();
		char c = peekChar();
		if (c != '(')
			return 0;
		nextChar();

		int a = 0;
		for (;;) {
			c = getChar();
			if (c == '_')
				a += 1;
			else if (c == ')')
				break;
			else if (c != ',')
				error("Invalid formal argument");
		}

		return a;
	}

	public List<SmallAlgebra> parseAlgebraList() throws IOException,
			BadAlgebraFileException {
		List<SmallAlgebra> algebras = new ArrayList<SmallAlgebra>();

		for (;;) {
			SmallAlgebra alg = parseAlgebra();
			if (alg == null)
				break;

			algebras.add(alg);
		}

		return algebras;
	}
}
