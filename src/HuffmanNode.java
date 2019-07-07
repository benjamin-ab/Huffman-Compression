/*
 * <pre> 
 * Class: <b>HuffmanNode</b> 
 * File: HuffmanNode.java 
 * Course: TCSS 342 – Winter 2016
 * Assignment 4 – Compressed Literature
 * Copyright 2016 Benjamin Abdipour
 * </pre>
 */

public class HuffmanNode {
	Boolean[] path;
	String value;

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(value);
		sb.append("=");

		for (int i = 0; i < path.length; i++) {
			sb.append(path[i] ? '1' : '0');
		}

		return sb.toString();
	}
}
