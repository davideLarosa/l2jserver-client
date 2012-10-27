package com.l2client.navigation;

import java.util.Vector;

import com.jme3.math.Vector3f;
import com.l2client.util.MinHeap;

/**
 * A NavigationHeap is a priority-ordered list facilitated by the STL heap
 * functions. This class is also used to hold the current path finding session
 * ID and the desired goal point for NavigationCells to query. Thanks to Amit J.
 * Patel for detailing the use of STL heaps in this way. It's much faster than a
 * linked list or multimap approach.
 * 
 * Portions Copyright (C) Greg Snook, 2000
 * 
 * @author TR
 * 
 */
public class Heap {
	private MinHeap nodes = new MinHeap();
	int m_SessionID;

	public int SessionID() {
		return m_SessionID;
	}

	public Vector3f Goal() {
		return m_Goal;
	}

	Vector3f m_Goal;

	public void Setup(int SessionID, Vector3f Goal) {
		m_Goal = Goal;
		m_SessionID = SessionID;
		nodes.clear();
	}

	public void AddCell(Cell pCell) {
		Node NewNode = new Node(pCell, pCell.PathfindingCost());
		nodes.add(NewNode);
	}

	/**
	 * Adjust a cell in the heap to reflect it's updated cost value. NOTE: Cells
	 * may only sort up in the heap.
	 */
	public void AdjustCell(Cell pCell) {
		Node n = FindNodeInterator(pCell);

		if (n != nodes.lastElement()) {
			// update the node data
			n.cell = pCell;
			n.cost = pCell.PathfindingCost();

			nodes.sort();
		}
	}

	/**
	 * @return true if the heap is not empty
	 */
	public boolean NotEmpty() {
		return !nodes.isEmpty();
	}

	/**
	 * Pop the top off the heap and remove the best value for processing.
	 */
	public Node GetTop() {
		return (Node) nodes.deleteMin();
	}

	/**
	 * Search the container for a given cell. May be slow, so don't do this
	 * unless nessesary.
	 */
	public Node FindNodeInterator(Cell pCell) {
		for (Object n : nodes){

			if (((Node) n).cell.equals(pCell))
				return ((Node) n);
		}
		return (Node) nodes.lastElement();
	}
	
	public int size(){
		return nodes.size();
	}
	
	public Vector<Node> getNodes(){
		return nodes;
	}
}
