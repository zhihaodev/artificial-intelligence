package edu.jhu.cs435.hw1;

/**
 * This class represents node in search-tree graph.
 * 
 * @author Zhihao Cao
 *
 */
public class Node {
	private int x, y;
	private Node parent;
	private int currentCost;
	private double g, h, f;
	private int depth;
	
	/**
	 * Construct a node with specified parameters.
	 * 
	 * @param x	x coordinate of the node
	 * @param y y coordinate of the node
	 * @param parent parent of the node
	 * @param currentCost cost of terrain where the node on
	 * @param g path cost so far of the node
	 * @param h heuristic function 
	 */
	public Node(int x, int y, Node parent, int currentCost, double g, double h) {
		this.x = x;
		this.y = y;
		this.parent = parent;
		this.currentCost = currentCost;
		this.g = g;
		this.h = h;
		this.f = g + h;
	}
	
	/**
	 * Intended for DFS and IDDFS only.
	 * 
	 * @return depth of the node
	 */
	public int getDepth() {
		return this.depth;
	}

	/**
	 * Intended for DFS and IDDFS only.
	 * 
	 * @param depth	depth of the node
	 */
	public void setDepth(int depth) {
		this.depth = depth;
	}
	
	/**
	 * 
	 * @return current cost of the node
	 */
	public double getCurrentCost() {
		return this.currentCost;
	}

	/**
	 * 
	 * @param currentCost current cost of the node
	 */
	public void setCurrentCost(int currentCost) {
		this.currentCost = currentCost;
	}
	
	/**
	 * 
	 * @return	past cost of the node
	 */
	public double getG() {
		return this.g;
	}

	/**
	 * 
	 * @param g	past cost of the node
	 */
	public void setG(double g) {
		this.g = g;
	}
	
	/**
	 * 
	 * @return heuristics value of the node
	 */
	public double getH() {
		return this.h;
	}

	/**
	 * 
	 * @param h	heuristics value of the node
	 */
	public void setH(double h) {
		this.h = h;
	}
	
	/**
	 * 
	 * @param f	sum of past cost and heuristics value of the node
	 */
	public void setF(double f) {
		this.f = f;
	}
	
	/**
	 * 
	 * @return sum of past cost and heuristics value of the node
	 */
	public double getF() {
		return this.f;
	}

	/**
	 * 
	 * @return x coordinate of the node
	 */
	public int getX() {
		return this.x;
	}

	/**
	 * 
	 * @return y coordinate of the node
	 */
	public int getY() {
		return this.y;
	}

	/**
	 * 
	 * @return parent of the node
	 */
	public Node getParent() {
		return this.parent;
	}

	/**
	 * 
	 * @param parent parent of the node
	 */
	public void setParent(Node parent) {
		this.parent = parent;
	}
	
	/**
	 * 
	 * @return pair of x,y coordinates
	 */
	@Override
	public String toString() {
		return("(" + x + ", " + y + ") ");
	}

	/**
	 * 
	 * @return <code>true</code> if two nodes have the same x,y coordinates
	 */
	@Override
	public boolean equals(Object obj) {
		return this.x == ((Node)obj).getX() && this.y == ((Node)obj).getY() ; ///
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	
	
	
}
