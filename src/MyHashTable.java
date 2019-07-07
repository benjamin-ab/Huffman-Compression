
/*
 * <pre> 
 * Class: <b>MyHashTable</b> 
 * File: MyHashTable.java 
 * Course: TCSS 342 – Winter 2016
 * Assignment 4 – Compressed Literature
 * Copyright 2016 Benjamin Abdipour
 * </pre>
 */

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class MyHashTable<K, V> {
	private Object[] keys;
	private Object[] hash;
	private int capacity;
	private static int size;
	public int[] probes;
	Sort mySort;

	public MyHashTable(int capacity) {
		this.capacity = capacity;
		keys = new Object[capacity];
		hash = new Object[capacity];
		size = 0;
		probes = new int[capacity];
	}

	public void put(K searchKey, V newValue) {
		int index[] = indexOf(searchKey);

		if (index[0] != -1) {
			hash[index[0]] = newValue;
		} else {
			index = firstNullIndex(hash(searchKey));
			if (index[0] != -1) {
				keys[index[0]] = searchKey;
				hash[index[0]] = newValue;
				probes[index[1]]++;
				size++;
			} else {
				System.err.print("Table is full!");
			}
		}
	}

	@SuppressWarnings("unchecked")
	public int[] indexOf(K key) {
		int hashCode = hash(key);
		int[] returnData = { -1, 0 };
		int probes = 0;

		if (keys[hashCode] != null) {
			for (int i = 0; i < capacity; i++) {
				if (((K) key).equals((K) keys[hashCode])) {
					returnData[0] = hashCode;
					returnData[1] = probes;
					break;
				} else if (keys[hashCode] == null) {
					break;
				} else if (hashCode >= capacity - 1) {
					hashCode = 0;
					probes++;
				} else {
					hashCode++;
					probes++;
				}
			}
		}

		return returnData;
	}

	public boolean containsKey(K searchKey) {
		int index = indexOf(searchKey)[0];
		boolean bl = (index == -1 ? false : true);
		return (bl);
	}

	@SuppressWarnings("unchecked")
	public V get(K searchKey) {
		Object value = null;
		int index = indexOf(searchKey)[0];

		if (index != -1) {
			value = hash[index];
		}

		return (V) value;
	}

	@SuppressWarnings("unchecked")
	public K getKey(int index) {
		if (index >= 0 && index < capacity)
			return (K) keys[index];
		else
			return null;
	}

	public Object valueAt(int index) {
		return hash[index];
	}

	public int getSize() {
		return size;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < capacity; i++) {
			if (keys[i] != null) {
				sb.append("[");
				sb.append(((K) keys[i]).toString());
				sb.append(", ");
				sb.append(((V) hash[i]).toString());
				sb.append("] ");
			}
		}
		return sb.toString();
	}

	private int hash(K key) {
		int hashCode = -1;
		hashCode = key.hashCode() % capacity;
		hashCode = (hashCode >= 0 ? hashCode : hashCode + capacity);

		return hashCode;
	}

	public int[] sort() {
		mySort = new Sort();
		return mySort.sorthash(hash);
	}

	@SuppressWarnings("unchecked")
	public K getFirst() {
		K key = null;
		int i = 0;
		while (size > 0 && key == null & i < capacity) {
			if (keys[i] != null) {
				key = (K) keys[i];
			}
			i++;
		}

		return key;
	}

	private int[] firstNullIndex(int startingIndex) {
		int hash = startingIndex;
		int[] returnData = { -1, 0 };

		for (int i = 0; i < capacity; i++) {
			if (keys[hash] == null) {
				returnData[0] = hash;
				break;
			}

			if (hash >= capacity - 1) {
				hash = 0;
				returnData[1]++;
			} else {
				hash++;
				returnData[1]++;
			}
		}

		return returnData;
	}

	public int firstNonNullIndex(int index) {
		int nextIndex = -1;
		while (index < capacity && nextIndex == -1) {
			if (keys[index] != null) {
				nextIndex = index;
				break;
			}
			index++;
		}
		return nextIndex;
	}

	public void stats() throws IOException {
		int maxProbes = 0;
		int maxLinearProbe = 0;
		for (int i = 0; i < capacity; i++) {
			if (probes[i] > 0) {
				maxProbes = i;
				maxLinearProbe += (i + 1) * probes[i];
			}
		}

		FileWriter fw = new FileWriter(CodingTree.outFileTrace);
		fw.write("Hash Table Stats" + "\r\n");
		fw.write("================" + "\r\n");
		fw.write("Number of Entries: " + getSize() + "\r\n");
		fw.write("Number of Buckets: " + capacity + "\r\n");
		fw.write("Histogram of Probes: " + Arrays.toString(Arrays.copyOfRange(probes, 0, maxProbes + 1)) + "\r\n");
		fw.write("Fill Percentage: " + String.format("%.6f%%", ((float) getSize() / capacity * 100)) + "\r\n");
		fw.write("Max Linear Prob: " + (maxProbes + 1) + "\r\n");
		fw.write("Average Linear Prob: " + String.format("%.6f", ((float) maxLinearProbe / getSize())) + "\r\n");
		fw.write("\r\nUncompressed file size: " + CodingTree.inFile.length() + " bytes" + "\r\n");
		fw.write("Compressed file size: " + CodingTree.outFile.length() + " bytes" + "\r\n");
		fw.write("Compression ratio: " + (int) ((float) CodingTree.outFile.length() / CodingTree.inFile.length() * 100)
				+ "%\r\n");
		fw.write("Running Time: " + (CodingTree.endTime - CodingTree.startTime) + " milliseconds" + "\r\n");
		fw.close();
	}

	// sorts by V ascending order
	private class Sort {
		public int[] sorthash(Object[] hash) {
			int[] sortedindex = new int[size];
			ArrayList<int[]> arrList = new ArrayList<int[]>();
			ArrayList<Integer> arrListSorted = new ArrayList<Integer>();

			for (int i = 0; i < hash.length; i++) {
				if (hash[i] != null) {
					int[] item = { (int) hash[i], i };
					arrList.add(item);
				}
			}

			int countToBeChecked = 1;
			while (arrListSorted.size() < size) {
				for (int i = 0; i < arrList.size(); i++) {
					if ((int) arrList.get(i)[0] == countToBeChecked) {
						arrListSorted.add(arrList.get(i)[1]);
						arrList.remove(i);
						i--;
					}
				}
				countToBeChecked++;
			}

			for (int i = 0; i < arrListSorted.size(); i++) {
				sortedindex[i] = arrListSorted.get(i);
			}

			return sortedindex;
		}
	}
}