// line 1 "JsonReader.rl"
// Do not edit this file! Generated by Ragel.
// Ragel.exe -G2 -J -o JsonReader.java JsonReader.rl
/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.badlogic.gdx.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonValue.ValueType;

/** Lightweight JSON parser.<br>
 * <br>
 * The default behavior is to parse the JSON into a DOM containing {@link JsonValue} objects. Extend this class and override
 * methods to perform event driven parsing. When this is done, the parse methods will return null.
 * @author Nathan Sweet */
public class JsonReader implements BaseJsonReader {
	public JsonValue parse (String json) {
		char[] data = json.toCharArray();
		return parse(data, 0, data.length);
	}

	public JsonValue parse (Reader reader) {
		try {
			char[] data = new char[1024];
			int offset = 0;
			while (true) {
				int length = reader.read(data, offset, data.length - offset);
				if (length == -1) break;
				if (length == 0) {
					char[] newData = new char[data.length * 2];
					System.arraycopy(data, 0, newData, 0, data.length);
					data = newData;
				} else
					offset += length;
			}
			return parse(data, 0, offset);
		} catch (IOException ex) {
			throw new SerializationException(ex);
		} finally {
			StreamUtils.closeQuietly(reader);
		}
	}

	public JsonValue parse (InputStream input) {
		try {
			return parse(new InputStreamReader(input, "UTF-8"));
		} catch (IOException ex) {
			throw new SerializationException(ex);
		} finally {
			StreamUtils.closeQuietly(input);
		}
	}

	public JsonValue parse (FileHandle file) {
		try {
			return parse(file.reader("UTF-8"));
		} catch (Exception ex) {
			throw new SerializationException("Error parsing file: " + file, ex);
		}
	}

	public JsonValue parse (char[] data, int offset, int length) {
		int cs, p = offset, pe = length, eof = pe, top = 0;
		int[] stack = new int[4];

		int s = 0;
		Array<String> names = new Array(8);
		boolean needsUnescape = false, stringIsName = false, stringIsUnquoted = false;
		RuntimeException parseRuntimeEx = null;

		boolean debug = false;
		if (debug) System.out.println();

		try {

			// line 3 "JsonReader.java"
			{
				cs = json_start;
				top = 0;
			}

			// line 8 "JsonReader.java"
			{
				int _klen;
				int _trans = 0;
				int _acts;
				int _nacts;
				int _keys;
				int _goto_targ = 0;

				_goto:
				while (true) {
					switch (_goto_targ) {
					case 0:
						if (p == pe) {
							_goto_targ = 4;
							continue _goto;
						}
						if (cs == 0) {
							_goto_targ = 5;
							continue _goto;
						}
					case 1:
						_match:
						do {
							_keys = _json_key_offsets[cs];
							_trans = _json_index_offsets[cs];
							_klen = _json_single_lengths[cs];
							if (_klen > 0) {
								int _lower = _keys;
								int _mid;
								int _upper = _keys + _klen - 1;
								while (true) {
									if (_upper < _lower) break;

									_mid = _lower + ((_upper - _lower) >> 1);
									if (data[p] < _json_trans_keys[_mid])
										_upper = _mid - 1;
									else if (data[p] > _json_trans_keys[_mid])
										_lower = _mid + 1;
									else {
										_trans += (_mid - _keys);
										break _match;
									}
								}
								_keys += _klen;
								_trans += _klen;
							}

							_klen = _json_range_lengths[cs];
							if (_klen > 0) {
								int _lower = _keys;
								int _mid;
								int _upper = _keys + (_klen << 1) - 2;
								while (true) {
									if (_upper < _lower) break;

									_mid = _lower + (((_upper - _lower) >> 1) & ~1);
									if (data[p] < _json_trans_keys[_mid])
										_upper = _mid - 2;
									else if (data[p] > _json_trans_keys[_mid + 1])
										_lower = _mid + 2;
									else {
										_trans += ((_mid - _keys) >> 1);
										break _match;
									}
								}
								_trans += _klen;
							}
						} while (false);

						_trans = _json_indicies[_trans];
						cs = _json_trans_targs[_trans];

						if (_json_trans_actions[_trans] != 0) {
							_acts = _json_trans_actions[_trans];
							_nacts = (int)_json_actions[_acts++];
							while (_nacts-- > 0) {
								switch (_json_actions[_acts++]) {
								case 0:
								// line 104 "JsonReader.rl"
								{
									stringIsName = true;
								}
									break;
								case 1:
								// line 107 "JsonReader.rl"
								{
									String value = new String(data, s, p - s);
									if (needsUnescape) value = unescape(value);
									outer:
									if (stringIsName) {
										stringIsName = false;
										if (debug) System.out.println("name: " + value);
										names.add(value);
									} else {
										String name = names.size > 0 ? names.pop() : null;
										if (stringIsUnquoted) {
											if (value.equals("true")) {
												if (debug) System.out.println("boolean: " + name + "=true");
												bool(name, true);
												break outer;
											} else if (value.equals("false")) {
												if (debug) System.out.println("boolean: " + name + "=false");
												bool(name, false);
												break outer;
											} else if (value.equals("null")) {
												string(name, null);
												break outer;
											}
											boolean couldBeDouble = false, couldBeLong = true;
											outer2:
											for (int i = s; i < p; i++) {
												switch (data[i]) {
												case '0':
												case '1':
												case '2':
												case '3':
												case '4':
												case '5':
												case '6':
												case '7':
												case '8':
												case '9':
												case '-':
												case '+':
													break;
												case '.':
												case 'e':
												case 'E':
													couldBeDouble = true;
													couldBeLong = false;
													break;
												default:
													couldBeDouble = false;
													couldBeLong = false;
													break outer2;
												}
											}
											if (couldBeDouble) {
												try {
													if (debug) System.out.println("double: " + name + "=" + Double.parseDouble(value));
													number(name, Double.parseDouble(value));
													break outer;
												} catch (NumberFormatException ignored) {
												}
											} else if (couldBeLong) {
												if (debug) System.out.println("double: " + name + "=" + Double.parseDouble(value));
												try {
													number(name, Long.parseLong(value));
													break outer;
												} catch (NumberFormatException ignored) {
												}
											}
										}
										if (debug) System.out.println("string: " + name + "=" + value);
										string(name, value);
									}
									stringIsUnquoted = false;
									s = p;
								}
									break;
								case 2:
								// line 181 "JsonReader.rl"
								{
									String name = names.size > 0 ? names.pop() : null;
									if (debug) System.out.println("startObject: " + name);
									startObject(name);
									{
										if (top == stack.length) {
											int[] newStack = new int[stack.length * 2];
											System.arraycopy(stack, 0, newStack, 0, stack.length);
											stack = newStack;
										}
										{
											stack[top++] = cs;
											cs = 5;
											_goto_targ = 2;
											if (true) continue _goto;
										}
									}
								}
									break;
								case 3:
								// line 187 "JsonReader.rl"
								{
									if (debug) System.out.println("endObject");
									pop();
									{
										cs = stack[--top];
										_goto_targ = 2;
										if (true) continue _goto;
									}
								}
									break;
								case 4:
								// line 192 "JsonReader.rl"
								{
									String name = names.size > 0 ? names.pop() : null;
									if (debug) System.out.println("startArray: " + name);
									startArray(name);
									{
										if (top == stack.length) {
											int[] newStack = new int[stack.length * 2];
											System.arraycopy(stack, 0, newStack, 0, stack.length);
											stack = newStack;
										}
										{
											stack[top++] = cs;
											cs = 17;
											_goto_targ = 2;
											if (true) continue _goto;
										}
									}
								}
									break;
								case 5:
								// line 198 "JsonReader.rl"
								{
									if (debug) System.out.println("endArray");
									pop();
									{
										cs = stack[--top];
										_goto_targ = 2;
										if (true) continue _goto;
									}
								}
									break;
								case 6:
								// line 203 "JsonReader.rl"
								{
									if (debug) System.out.println("comment /" + data[p]);
									if (data[p++] == '/') {
										while (data[p] != '\n')
											p++;
										p--;
									} else {
										while (data[p] != '*' || data[p + 1] != '/')
											p++;
										p++;
									}
								}
									break;
								case 7:
								// line 215 "JsonReader.rl"
								{
									if (debug) System.out.println("unquotedChars");
									s = p;
									needsUnescape = false;
									stringIsUnquoted = true;
									if (stringIsName) {
										outer:
										while (true) {
											switch (data[p]) {
											case '\\':
												needsUnescape = true;
												break;
											case ':':
											case '/':
											case '\r':
											case '\n':
												break outer;
											}
											if (debug) System.out.println("unquotedChar (name): '" + data[p] + "'");
											p++;
											if (p == eof) break;
										}
									} else {
										outer:
										while (true) {
											switch (data[p]) {
											case '\\':
												needsUnescape = true;
												break;
											case '}':
											case ']':
											case ',':
											case '/':
											case '\r':
											case '\n':
												break outer;
											}
											if (debug) System.out.println("unquotedChar (value): '" + data[p] + "'");
											p++;
											if (p == eof) break;
										}
									}
									p--;
									while (data[p] == ' ')
										p--;
								}
									break;
								case 8:
								// line 261 "JsonReader.rl"
								{
									if (debug) System.out.println("quotedChars");
									s = ++p;
									needsUnescape = false;
									outer:
									while (true) {
										switch (data[p]) {
										case '\\':
											needsUnescape = true;
											p++;
											break;
										case '"':
											break outer;
										}
										// if (debug) System.out.println("quotedChar: '" + data[p] + "'");
										p++;
										if (p == eof) break;
									}
									p--;
								}
									break;
								// line 304 "JsonReader.java"
								}
							}
						}

					case 2:
						if (cs == 0) {
							_goto_targ = 5;
							continue _goto;
						}
						if (++p != pe) {
							_goto_targ = 1;
							continue _goto;
						}
					case 4:
						if (p == eof) {
							int __acts = _json_eof_actions[cs];
							int __nacts = (int)_json_actions[__acts++];
							while (__nacts-- > 0) {
								switch (_json_actions[__acts++]) {
								case 1:
								// line 107 "JsonReader.rl"
								{
									String value = new String(data, s, p - s);
									if (needsUnescape) value = unescape(value);
									outer:
									if (stringIsName) {
										stringIsName = false;
										if (debug) System.out.println("name: " + value);
										names.add(value);
									} else {
										String name = names.size > 0 ? names.pop() : null;
										if (stringIsUnquoted) {
											if (value.equals("true")) {
												if (debug) System.out.println("boolean: " + name + "=true");
												bool(name, true);
												break outer;
											} else if (value.equals("false")) {
												if (debug) System.out.println("boolean: " + name + "=false");
												bool(name, false);
												break outer;
											} else if (value.equals("null")) {
												string(name, null);
												break outer;
											}
											boolean couldBeDouble = false, couldBeLong = true;
											outer2:
											for (int i = s; i < p; i++) {
												switch (data[i]) {
												case '0':
												case '1':
												case '2':
												case '3':
												case '4':
												case '5':
												case '6':
												case '7':
												case '8':
												case '9':
												case '-':
												case '+':
													break;
												case '.':
												case 'e':
												case 'E':
													couldBeDouble = true;
													couldBeLong = false;
													break;
												default:
													couldBeDouble = false;
													couldBeLong = false;
													break outer2;
												}
											}
											if (couldBeDouble) {
												try {
													if (debug) System.out.println("double: " + name + "=" + Double.parseDouble(value));
													number(name, Double.parseDouble(value));
													break outer;
												} catch (NumberFormatException ignored) {
												}
											} else if (couldBeLong) {
												if (debug) System.out.println("double: " + name + "=" + Double.parseDouble(value));
												try {
													number(name, Long.parseLong(value));
													break outer;
												} catch (NumberFormatException ignored) {
												}
											}
										}
										if (debug) System.out.println("string: " + name + "=" + value);
										string(name, value);
									}
									stringIsUnquoted = false;
									s = p;
								}
									break;
								// line 402 "JsonReader.java"
								}
							}
						}

					case 5:
					}
					break;
				}
			}

			// line 297 "JsonReader.rl"

		} catch (RuntimeException ex) {
			parseRuntimeEx = ex;
		}

		JsonValue root = this.root;
		this.root = null;
		current = null;
		lastChild.clear();

		if (p < pe) {
			int lineNumber = 1;
			for (int i = 0; i < p; i++)
				if (data[i] == '\n') lineNumber++;
			throw new SerializationException("Error parsing JSON on line " + lineNumber + " near: "
				+ new String(data, p, Math.min(256, pe - p)), parseRuntimeEx);
		} else if (elements.size != 0) {
			JsonValue element = elements.peek();
			elements.clear();
			if (element != null && element.isObject())
				throw new SerializationException("Error parsing JSON, unmatched brace.");
			else
				throw new SerializationException("Error parsing JSON, unmatched bracket.");
		} else if (parseRuntimeEx != null) {
			throw new SerializationException("Error parsing JSON: " + new String(data), parseRuntimeEx);
		}
		return root;
	}

	// line 412 "JsonReader.java"
	private static byte[] init__json_actions_0 () {
		return new byte[] {0, 1, 1, 1, 2, 1, 3, 1, 4, 1, 5, 1, 6, 1, 7, 1, 8, 2, 0, 7, 2, 0, 8, 2, 1, 3, 2, 1, 5};
	}

	private static final byte _json_actions[] = init__json_actions_0();

	private static byte[] init__json_key_offsets_0 () {
		return new byte[] {0, 0, 8, 10, 11, 13, 21, 27, 33, 35, 43, 50, 57, 59, 60, 62, 63, 65, 73, 80, 87, 96, 97, 99, 101, 103,
			108, 113, 113};
	}

	private static final byte _json_key_offsets[] = init__json_key_offsets_0();

	private static char[] init__json_trans_keys_0 () {
		return new char[] {13, 32, 34, 47, 91, 123, 9, 10, 42, 47, 34, 42, 47, 13, 32, 34, 47, 58, 125, 9, 10, 13, 32, 47, 58, 9,
			10, 13, 32, 47, 58, 9, 10, 42, 47, 13, 32, 34, 47, 91, 123, 9, 10, 9, 10, 13, 32, 44, 47, 125, 9, 10, 13, 32, 44, 47,
			125, 42, 47, 34, 42, 47, 34, 42, 47, 13, 32, 34, 47, 91, 123, 9, 10, 9, 10, 13, 32, 44, 47, 93, 9, 10, 13, 32, 44, 47,
			93, 13, 32, 34, 47, 91, 93, 123, 9, 10, 34, 42, 47, 42, 47, 42, 47, 13, 32, 47, 9, 10, 13, 32, 47, 9, 10, 0};
	}

	private static final char _json_trans_keys[] = init__json_trans_keys_0();

	private static byte[] init__json_single_lengths_0 () {
		return new byte[] {0, 6, 2, 1, 2, 6, 4, 4, 2, 6, 7, 7, 2, 1, 2, 1, 2, 6, 7, 7, 7, 1, 2, 2, 2, 3, 3, 0, 0};
	}

	private static final byte _json_single_lengths[] = init__json_single_lengths_0();

	private static byte[] init__json_range_lengths_0 () {
		return new byte[] {0, 1, 0, 0, 0, 1, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 1, 1, 0, 0};
	}

	private static final byte _json_range_lengths[] = init__json_range_lengths_0();

	private static short[] init__json_index_offsets_0 () {
		return new short[] {0, 0, 8, 11, 13, 16, 24, 30, 36, 39, 47, 55, 63, 66, 68, 71, 73, 76, 84, 92, 100, 109, 111, 114, 117,
			120, 125, 130, 131};
	}

	private static final short _json_index_offsets[] = init__json_index_offsets_0();

	private static byte[] init__json_indicies_0 () {
		return new byte[] {1, 1, 2, 3, 4, 5, 1, 0, 6, 6, 7, 8, 7, 9, 9, 7, 11, 11, 12, 13, 7, 14, 11, 10, 15, 15, 16, 17, 15, 7,
			18, 18, 19, 20, 18, 7, 21, 21, 7, 20, 20, 23, 24, 25, 26, 20, 22, 27, 28, 27, 27, 28, 29, 30, 7, 31, 11, 31, 31, 11, 32,
			14, 7, 33, 33, 7, 27, 7, 34, 34, 7, 15, 7, 35, 35, 7, 37, 37, 38, 39, 40, 41, 37, 36, 42, 43, 42, 42, 43, 44, 45, 7, 46,
			47, 46, 46, 47, 48, 49, 7, 47, 47, 38, 50, 40, 49, 41, 47, 36, 42, 7, 51, 51, 7, 52, 52, 7, 53, 53, 7, 8, 8, 54, 8, 7,
			55, 55, 56, 55, 7, 7, 7, 0};
	}

	private static final byte _json_indicies[] = init__json_indicies_0();

	private static byte[] init__json_trans_targs_0 () {
		return new byte[] {25, 1, 3, 4, 26, 26, 26, 0, 26, 1, 6, 5, 15, 16, 27, 7, 8, 9, 7, 8, 9, 7, 10, 13, 14, 11, 11, 11, 5, 12,
			27, 11, 12, 11, 9, 5, 18, 17, 21, 24, 19, 19, 19, 20, 23, 28, 19, 20, 23, 28, 22, 20, 19, 17, 2, 26, 2};
	}

	private static final byte _json_trans_targs[] = init__json_trans_targs_0();

	private static byte[] init__json_trans_actions_0 () {
		return new byte[] {13, 0, 15, 0, 7, 3, 11, 0, 1, 11, 17, 0, 20, 0, 5, 1, 1, 1, 0, 0, 0, 11, 13, 15, 0, 7, 3, 1, 1, 1, 23,
			0, 0, 11, 11, 11, 13, 0, 15, 0, 7, 3, 1, 1, 1, 26, 0, 0, 0, 9, 0, 11, 11, 11, 1, 0, 0};
	}

	private static final byte _json_trans_actions[] = init__json_trans_actions_0();

	private static byte[] init__json_eof_actions_0 () {
		return new byte[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0};
	}

	private static final byte _json_eof_actions[] = init__json_eof_actions_0();

	static final int json_start = 1;
	static final int json_first_final = 25;
	static final int json_error = 0;

	static final int json_en_object = 5;
	static final int json_en_array = 17;
	static final int json_en_main = 1;

	// line 327 "JsonReader.rl"

	private final Array<JsonValue> elements = new Array(8);
	private final Array<JsonValue> lastChild = new Array(8);
	private JsonValue root, current;

	private void addChild (String name, JsonValue child) {
		child.setName(name);
		if (current == null) {
			current = child;
			root = child;
		} else if (current.isArray() || current.isObject()) {
			if (current.size == 0)
				current.child = child;
			else {
				JsonValue last = lastChild.pop();
				last.next = child;
				child.prev = last;
			}
			lastChild.add(child);
			current.size++;
		} else
			root = current;
	}

	protected void startObject (String name) {
		JsonValue value = new JsonValue(ValueType.object);
		if (current != null) addChild(name, value);
		elements.add(value);
		current = value;
	}

	protected void startArray (String name) {
		JsonValue value = new JsonValue(ValueType.array);
		if (current != null) addChild(name, value);
		elements.add(value);
		current = value;
	}

	protected void pop () {
		root = elements.pop();
		if (current.size > 0) lastChild.pop();
		current = elements.size > 0 ? elements.peek() : null;
	}

	protected void string (String name, String value) {
		addChild(name, new JsonValue(value));
	}

	protected void number (String name, double value) {
		addChild(name, new JsonValue(value));
	}

	protected void number (String name, long value) {
		addChild(name, new JsonValue(value));
	}

	protected void bool (String name, boolean value) {
		addChild(name, new JsonValue(value));
	}

	private String unescape (String value) {
		int length = value.length();
		StringBuilder buffer = new StringBuilder(length + 16);
		for (int i = 0; i < length;) {
			char c = value.charAt(i++);
			if (c != '\\') {
				buffer.append(c);
				continue;
			}
			if (i == length) break;
			c = value.charAt(i++);
			if (c == 'u') {
				buffer.append(Character.toChars(Integer.parseInt(value.substring(i, i + 4), 16)));
				i += 4;
				continue;
			}
			switch (c) {
			case '"':
			case '\\':
			case '/':
				break;
			case 'b':
				c = '\b';
				break;
			case 'f':
				c = '\f';
				break;
			case 'n':
				c = '\n';
				break;
			case 'r':
				c = '\r';
				break;
			case 't':
				c = '\t';
				break;
			default:
				throw new SerializationException("Illegal escaped character: \\" + c);
			}
			buffer.append(c);
		}
		return buffer.toString();
	}
}
