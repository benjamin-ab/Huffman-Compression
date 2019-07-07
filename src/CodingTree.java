
/*
 * <pre> 
 * Class: <b>CodingTree</b> 
 * File: CodingTree.java 
 * Course: TCSS 342 – Winter 2016
 * Assignment 4 – Compressed Literature
 * Copyright 2016 Benjamin Abdipour
 * </pre>
 */

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class CodingTree {

	// MyHashTable<String, Integer> has been implemented
	public MyHashTable<String, String> codes;

	public MyHashTable<String, Integer> wordCount;
	public MyHashTable<String, Boolean[]> wordCodeBinary;
	public String bits;
	private HuffmanNode[] huffmanNodeArray;
	private final int CHUNK_SIZE = 4096;
	private final int BUCKET_SIZE = 32768;
	private final static String inputFileName = Main.inputFileName;
	private final static String outputFileNameCompressed = "compressed.txt";
	private final static String outputFileNameCode = "codes.txt";
	private final static String outputFileNameTrace = "trace.txt";
	public static long startTime = 0;
	public static long endTime = 0;
	public final static File inFile = new File(inputFileName);
	public final static File outFile = new File(outputFileNameCompressed);
	public final static File outFileCode = new File(outputFileNameCode);
	public final static File outFileTrace = new File(outputFileNameTrace);

	public CodingTree(String fulltext) throws IOException {

		startTime = System.currentTimeMillis();
		huffmanNodeArray = encode(fulltext);
		endTime = System.currentTimeMillis();
		writeEncodedFile(inFile, outFile, outFileCode);
		wordCount.stats();
	}

	/**
	 * Create a huffman array from the file to compress.
	 * 
	 * @param inFile
	 *            The file to compress
	 * @return An array representation of the huffman tree.
	 * @throws IOException
	 * @throws InterruptedException
	 */

	public HuffmanNode[] encode(String input) throws IOException {
		byte[] chunk = new byte[CHUNK_SIZE];
		StringBuilder sb = new StringBuilder();
		wordCount = new MyHashTable<String, Integer>(BUCKET_SIZE);

		InputStream in = null;
		try {
			in = new BufferedInputStream(new ByteArrayInputStream(input.getBytes()), CHUNK_SIZE);
			int bytes = in.available();

			while (bytes > 0) {
				int readBytes = in.read(chunk);

				for (int i = 0; i < readBytes; i++) {
					if ((chunk[i] >= 'a' && chunk[i] <= 'z') || (chunk[i] >= 'A' && chunk[i] <= 'Z')
							|| (chunk[i] >= '0' && chunk[i] <= '9') || chunk[i] == '\'' || chunk[i] == '-') {
						sb.append((char) chunk[i]);
					} else {
						if (sb.length() > 0) {
							int tempValue = 0;
							if (wordCount.containsKey(sb.toString())) {
								tempValue = wordCount.get(sb.toString());

							}
							tempValue++;
							wordCount.put(sb.toString(), tempValue);
							sb.delete(0, sb.length());
						}

						if (Byte.toUnsignedInt(chunk[i]) != 0) {
							int tempValue = 0;
							if (wordCount.containsKey(String.valueOf((char) chunk[i]))) {
								tempValue = wordCount.get(String.valueOf((char) chunk[i]));
							}
							tempValue++;
							wordCount.put(String.valueOf((char) chunk[i]), tempValue);
						}
					}
				}

				bytes -= CHUNK_SIZE;
			}

			HuffmanTree treeWord = new HuffmanTree(wordCount);
			huffmanNodeArray = treeWord.getHuffmanNodeArray();

			wordCodeBinary = new MyHashTable<String, Boolean[]>(BUCKET_SIZE);
			for (int i = 0; i < huffmanNodeArray.length; i++) {
				wordCodeBinary.put(huffmanNodeArray[i].value, huffmanNodeArray[i].path);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {

			if (in != null) {

				in.close();
			}
		}

		return huffmanNodeArray;
	}

	/**
	 * Write out the compressed file by appending the bits non-byte aligned.
	 * 
	 * @param fileIn
	 *            the file to compress
	 * @param fileOut
	 *            the new compressed file
	 * @param outFileCode
	 *            the code file
	 * @throws IOException
	 */
	public void writeEncodedFile(File fileIn, File fileOut, File outFileCode) throws IOException {
		BufferedInputStream in = null;
		BufferedOutputStream out = null;

		try {
			FileWriter fw = new FileWriter(outFileCode);
			fw.write(Arrays.toString(huffmanNodeArray));
			fw.close();
			out = new BufferedOutputStream(new FileOutputStream(fileOut));

			// input filereader
			in = new BufferedInputStream(new FileInputStream(fileIn), CHUNK_SIZE);

			// output file writer
			int bytes = in.available();
			byte[] chunk = new byte[CHUNK_SIZE];
			StringBuilder sb = new StringBuilder();
			StringBuilder sbBits = new StringBuilder();
			while (bytes > 0) {
				int readBytes = in.read(chunk);

				for (int i = 0; i < readBytes; i++) {

					if ((chunk[i] >= 'a' && chunk[i] <= 'z') || (chunk[i] >= 'A' && chunk[i] <= 'Z')
							|| (chunk[i] >= '0' && chunk[i] <= '9') || chunk[i] == '\'' || chunk[i] == '-') {
						sb.append((char) chunk[i]);
						sbBits.append((char) chunk[i]);
					} else {
						if (sb.length() > 0) {
							byte[] bytesTemp = BinaryHelper.booleansToBytes(wordCodeBinary.get(sb.toString()));
							out.write(bytesTemp);
							sb.delete(0, sb.length());
						}
					}
				}

				bytes -= CHUNK_SIZE;
			}
			bits = sbBits.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				out.close();
			}
			if (in != null) {
				in.close();
			}
		}
	}
}