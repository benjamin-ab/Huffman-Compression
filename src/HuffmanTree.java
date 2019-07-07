
/*
 * <pre> 
 * Class: <b>HuffmanTree</b> 
 * File: HuffmanTree.java 
 * Course: TCSS 342 – Winter 2016
 * Assignment 4 – Compressed Literature
 * Copyright 2016 Benjamin Abdipour
 * </pre>
 */

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

public class HuffmanTree {
	Node root;

	/**
	 * Build a huffman tree from a frequency table.
	 * 
	 * @param frequencyTable
	 *            a frequency table
	 * @throws IOException
	 */
	public HuffmanTree(MyHashTable<String, Integer> frequencyTable) throws IOException {
		int[] sortedindex = frequencyTable.sort();
		Queue<Node> nodes = new LinkedList<Node>();
		int pointerIndex = -1;
		int currentIndex = -1;
		int nextIndex = -1;
		int readSoFar = 0;

		while (frequencyTable.getSize() > 1 && (readSoFar <= frequencyTable.getSize() || nodes.size() > 1)) {
			Node newNode = null;
			int currentFrequency = 0;
			String currentWord = null;
			int nextFrequency = 0;
			String nextWord = null;

			if (readSoFar < frequencyTable.getSize()) {
				currentIndex = sortedindex[pointerIndex + 1];
				currentWord = frequencyTable.getKey(currentIndex);

				currentFrequency = frequencyTable.get(currentWord);
				readSoFar++;
				pointerIndex++;
			}

			if (readSoFar < frequencyTable.getSize()) {
				nextIndex = sortedindex[pointerIndex + 1];
				nextWord = frequencyTable.getKey(nextIndex);
				nextFrequency = frequencyTable.get(nextWord);
				readSoFar++;
				pointerIndex++;
			}

			if (currentFrequency > 0 && nodes.isEmpty()) {
				newNode = new Node(currentFrequency + nextFrequency);
				newNode.left = new Node(currentFrequency, currentWord);
				newNode.right = new Node(nextFrequency, nextWord);
			} else if (currentFrequency > 0 && currentFrequency <= nodes.peek().frequency) {

				if (nextFrequency > 0 && nextFrequency <= nodes.peek().frequency) {

					newNode = new Node(currentFrequency + nextFrequency);
					newNode.left = new Node(currentFrequency, currentWord);
					newNode.right = new Node(nextFrequency, nextWord);
				} else {
					newNode = new Node(currentFrequency + nodes.peek().frequency);
					newNode.right = nodes.poll();
					newNode.left = new Node(currentFrequency, currentWord);
					if (nextFrequency > 0) {
						readSoFar--;
						pointerIndex--;
					}
				}
			} else if (nodes.size() > 1) {
				Node left = nodes.poll();
				Node right = nodes.poll();
				newNode = new Node(left.frequency + right.frequency);
				newNode.right = right;
				newNode.left = left;
				if (currentFrequency > 0) {
					readSoFar--;
					pointerIndex--;
				}
				if (nextFrequency > 0) {
					readSoFar--;
					pointerIndex--;
				}
			} else if (currentFrequency > 0 && nodes.size() > 0) {
				newNode = new Node(currentFrequency + nodes.peek().frequency);
				newNode.right = nodes.poll();
				newNode.left = new Node(currentFrequency, currentWord);
				if (nextFrequency > 0) {
					readSoFar--;
					pointerIndex--;
				}
			} else if (nodes.size() == 1) {
				break;
			}
			nodes.offer(newNode);
		}

		root = nodes.poll();
	}

	/**
	 * Reconstruct a huffman tree from a dictionary.
	 * 
	 * @param huffmanNodeArray
	 *            a huffman dictionary.
	 */
	public HuffmanTree(HuffmanNode[] huffmanNodeArray) {
		root = new Node(0);
		Node parent = root;

		for (HuffmanNode node : huffmanNodeArray) {

			for (boolean side : node.path) {

				if (side) {

					if (parent.right == null)
						parent.right = new Node(0);
					parent = parent.right;
				} else {

					if (parent.left == null)
						parent.left = new Node(0);
					parent = parent.left;
				}
			}

			parent.value = node.value;
			parent.leaf = true;
			parent = root;
		}
	}

	/**
	 * return a new tree iterator.
	 * 
	 * @return a new TreeIterator
	 */
	public TreeIterator iterator() {

		return new TreeIterator(root);
	}

	/**
	 * Retrieve only the dictionary portion of the huffman tree.
	 * 
	 * @return the dictionary
	 */
	public HuffmanNode[] getHuffmanNodeArray() {
		LinkedList<HuffmanNode> huffmanNodeList = traverse(new LinkedList<HuffmanNode>(), root,
				new LinkedList<Boolean>());

		return huffmanNodeList.toArray(new HuffmanNode[huffmanNodeList.size()]);
	}

	/**
	 * Recursively walk through the tree to find just the leaves.
	 * 
	 * @param huffmanNodeList
	 *            The collection of leaves found so far
	 * @param node
	 *            The parent node
	 * @param path
	 *            the path taken so far.
	 * @return The collection of leaves found so far
	 */
	private LinkedList<HuffmanNode> traverse(LinkedList<HuffmanNode> huffmanNodeList, Node node,
			LinkedList<Boolean> path) {

		if (!node.isLeaf()) {
			path.add(false);
			traverse(huffmanNodeList, node.left, path);
			path.removeLast();

			path.add(true);
			traverse(huffmanNodeList, node.right, path);
			path.removeLast();
		} else {
			HuffmanNode huffmanData = new HuffmanNode();
			huffmanData.path = path.toArray(new Boolean[path.size()]);
			huffmanData.value = node.value;
			huffmanNodeList.add(huffmanData);
			if (node.value.equals(" ")) {
			}
		}

		return huffmanNodeList;
	}

	/**
	 * A simple class providing definitions for a binary node.
	 */
	private static class Node {

		Node left;
		Node right;

		boolean leaf = true;

		int frequency;
		String value;

		/**
		 * Construct a branch node
		 * 
		 * @param frequency
		 *            The frequency of both the child nodes added together.
		 */
		public Node(int frequency) {

			this(frequency, null);
			leaf = false;
		}

		/**
		 * Construct a leaf node.
		 * 
		 * @param frequency
		 *            The frequency at which the value occurs.
		 * @param value
		 *            the value that is represented.
		 */
		public Node(int frequency, String value) {

			this.frequency = frequency;
			this.value = value;
			leaf = true;
		}

		/**
		 * @return whether or not the node is a leaf.
		 */
		public boolean isLeaf() {

			return leaf;
		}

		public String toString() {

			return "[" + frequency + ", " + value + ", " + left + ", " + right + "]";
		}
	}

	/**
	 * Provide a way to traverse through a tree by taking left and right
	 * branches,
	 */
	public class TreeIterator {

		Node parent;

		/**
		 * construct a TreeIterator
		 * 
		 * @param root
		 *            The root of the tree
		 */
		public TreeIterator(Node root) {

			this.parent = root;
		}

		/**
		 * @return whether or not the parent is a branch
		 */
		public boolean hasNext() {

			return !parent.leaf;
		}

		/**
		 * traverse the left node.
		 * 
		 * @return the value of the left node
		 */
		public String left() {

			parent = parent.left;
			return parent.value;
		}

		/**
		 * traverse the right node.
		 * 
		 * @return the value of the right node
		 */
		public String right() {

			parent = parent.right;
			return parent.value;
		}
	}
}
