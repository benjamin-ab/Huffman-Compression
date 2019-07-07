/*
 * <pre> 
 * Class: <b>Main</b> 
 * File: Main.java 
 * Course: TCSS 342 – Winter 2016
 * Assignment 4 – Compressed Literature
 * Copyright 2016 Benjamin Abdipour
 * </pre>
 */

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

	public final static String inputFileName = "WarAndPeace.txt";

	public static void main(String[] args) throws IOException {
		File inFile = new File(inputFileName);
		if (!inFile.exists()) {
			System.err.print("invalid file!");
		} else {
			String message = new String(Files.readAllBytes(Paths.get(inputFileName)));
			new CodingTree(message);
		}
	}
}
