/*
 * <pre> 
 * Class: <b>BinaryHelper</b> 
 * File: BinaryHelper.java 
 * Course: TCSS 342 – Winter 2016
 * Assignment 4 – Compressed Literature
 * Copyright 2016 Benjamin Abdipour
 * </pre>
 */

public class BinaryHelper {

	/**
	 * Convert an array of booleans to an array of bytes packed to the right.
	 * 
	 * @param booleans
	 *            The booleans representing 1's and 0's
	 * @return A byte[] representation of the given boolean array
	 */
	public static byte[] booleansToBytes(Boolean[] booleans) {

		byte[] bytes = new byte[(booleans.length + Byte.SIZE - 1) / Byte.SIZE];

		int index = booleans.length - 1;
		for (int i = bytes.length - 1; i >= 0; i--) {

			for (int j = 0; j < Byte.SIZE && index >= 0; j++) {

				bytes[i] |= (booleans[index--] ? 1 : 0) << j;
			}
		}

		return bytes;
	}

	/**
	 * Convert an array of byte to an array of booleans packed to the right.
	 * 
	 * @param length
	 *            The length of the boolean array to return
	 * @param bytes
	 *            The bytes representing an array of true and false values
	 * @return A Boolean[] representation of the given byte array
	 */
	public static Boolean[] bytesToBooleans(int length, byte[] bytes) {

		Boolean[] booleans = new Boolean[length];

		int index = length - 1;

		for (int i = bytes.length - 1; i >= 0; i--) {

			for (int j = 0; j < Byte.SIZE && index >= 0; j++) {

				booleans[index--] = ((bytes[i] >>> j) & 0x00000001) == 1;
			}
		}

		return booleans;
	}

	/**
	 * Convert a single byte to a boolean array.
	 * 
	 * @param value
	 *            The byte to convert
	 * @return a boolean array
	 */
	public static boolean[] byteToBooleans(byte value) {

		boolean[] bools = new boolean[Byte.SIZE];

		for (int i = 0; i < Byte.SIZE; i++) {

			bools[i] = ((value >>> (Byte.SIZE - i - 1)) & 0x01) == 1;
		}

		return bools;
	}
}
