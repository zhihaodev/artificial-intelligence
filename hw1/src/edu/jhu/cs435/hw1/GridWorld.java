package edu.jhu.cs435.hw1;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;

/**
 * This class implements several searching algorithms to solve grid world problems.
 * 
 * @author Zhihao Cao
 *
 */
public class GridWorld {

	private static final char COST_OF_ONE_SIGN = '.';
	private static final char OBSTACLE_SIGN = '#';
	private static final char GOAL_SIGN = 'g';
	private static final char START_SIGN = 's';
		
	private char[][] world;
	private Node start, goal;
	private int width;
	private int height;
	private int nodesExpanded;
	private boolean isMaxDepthReach;
	
	/**
	 * Construct a grid world via a map file specified by @param path.
	 *
	 * @param path	path of map file
	 */
	public GridWorld(String path) {
		this.readWorld(path);
		this.nodesExpanded = 0;
	}
	
	/**
	 * Initialize parameters of a grid world in terms of given map file.
	 * @param path	path of map file
	 */
	public void readWorld(String path) {
		
		try {
			Scanner sc = new Scanner(new File(path));
			width = sc.nextInt();
			height = sc.nextInt();
			world = new char[height][width];
			
			int i = 0;
			while (sc.hasNext()) {
				String str = sc.next();
				if (str.contains(START_SIGN + ""))
					start = new Node(i, str.indexOf(START_SIGN + ""), null, 1, 0, 0);
				if (str.contains(GOAL_SIGN + ""))
					goal = new Node(i, str.indexOf(GOAL_SIGN + ""), null, 1, 0, 0);
				world[i] = str.toCharArray();
				i++;
			}
			sc.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Implement BFS algorithm to find a path from start state to goal state
	 * 
	 * @return	the Node in goal state, or <code>null</code>
	 */
	public Node BFS() {
		boolean[][] isGridVisited = new boolean[height][width];
		Queue<Node> queue = new LinkedList<Node>();
		isGridVisited[start.getX()][start.getY()] = true;
		queue.add(start);
		nodesExpanded++;
		while (!queue.isEmpty()) {

			Node node = queue.remove();

			for (int dx = -1; dx <= 1; dx++) {
				for (int dy = -1; dy <= 1; dy++) {
					
					if (dx== 1 && dy == 0 || dx== -1 && dy == 0 || dx == 0 && dy == 1 || dx== 0 && dy == -1) {
						int childX = dx + node.getX();
						int childY = dy + node.getY();
						if (isValid( childX, childY) && !isGridVisited[childX][childY]) {

							isGridVisited[childX][childY] = true;
							Node child = new Node(childX,childY, node, computeCost(world[childX][childY]), 0, 0);

							if (world[child.getX()][child.getY()] == GOAL_SIGN) {
								return child;
							}

							queue.add(child);
							nodesExpanded++;
						}
					}
				}
			}

		}
		return null;
	}
	/**
	 * Check whether a given node is valid. It cannot be placed outside the grid world or be placed on obstacles.
	 * 
	 * @param x	x coordinate of a node
	 * @param y y coordinate of a node
	 * @return
	 */
	private boolean isValid(int x, int y) {
		return x >= 0 && x <= height && y >= 0 && y <= width && (world[x][y] != OBSTACLE_SIGN);
	}	
		
	
	/**
	 * Implement DFS algorithm to find a path from start state to goal state. 
	 * limit &lt; 0, it becomes standard DFS; limit &gt;= 0, it becomes Depth-limited DFS and can be used to realize Iterative Deepening DFS; 
	 * 
	 * @param limit	depth limit of search tree 
	 * @return	the Node in goal state, or <code>null</code>
	 */
	public Node DFS(int limit) {
		int childrenNum = 0;
		int depth = 0;
		isMaxDepthReach = false;
		
		boolean[][] isGridVisited = new boolean[height][width];
		Stack<Node> stack = new Stack<Node>();
		start.setDepth(0);
		isGridVisited[start.getX()][start.getY()] = true;
		stack.push(start);

		nodesExpanded++;
		while (!stack.isEmpty()) {
			Node node = stack.pop();
			if (node.getDepth() > depth) depth = node.getDepth();
			
			if (world[node.getX()][node.getY()] == GOAL_SIGN) {
				return node;
			}
			
			if(childrenNum == 0) {
				
				for (int i = 0; i < height; i++)
					for (int j = 0; j < width; j++)
						isGridVisited[i][j] = false;
				Node temp = node;
				isGridVisited[node.getX()][node.getY()] = true;
				while(temp.getParent() != null) {
					temp = temp.getParent();
					isGridVisited[temp.getX()][temp.getY()] = true;
				}
				
			}

			childrenNum = 0;
			if (limit < 0 || node.getDepth() < limit) {

				for (int dx = -1; dx <= 1; dx++) {
					for (int dy = -1; dy <= 1; dy++) {

						if (dx== 1 && dy == 0 || dx== -1 && dy == 0 || dx == 0 && dy == 1 || dx== 0 && dy == -1) {
							int childX = dx + node.getX();
							int childY = dy + node.getY();
							if (isValid( childX, childY) && !isGridVisited[childX][childY]) {
								isGridVisited[childX][childY] = true;
								Node child = new Node(childX,childY, node, computeCost(world[childX][childY]), 0, 0);
								child.setDepth(node.getDepth() + 1);
								stack.push(child);
								childrenNum++;
								nodesExpanded++;
							}
						}
					}
				}

			}
			

		}
		isMaxDepthReach = depth >= limit;
		return null;
	}
	
	/**
	 * Implement A* search algorithm to find a path from start state to goal state.
	 * 
	 * Reference: http://en.wikipedia.org/wiki/A*_search_algorithm
	 * 
	 * @return	the Node in goal state, or <code>null</code>
	 */
	public Node AStarSearch() {
		
		double[][] lowestCost = new double[height][width];
		boolean[][] isInOpenSet = new boolean[height][width];
		
		PriorityQueue<Node> openSet = new PriorityQueue<Node>(11,
				new Comparator<Node>() {
					@Override
					public int compare(Node node1, Node node2) {
						double diff = node1.getF() - node2.getF();
						return (diff > 0) ? 1 : (diff < 0) ? -1: 0;
					}
				}
				);
		boolean isGridVisited[][] = new boolean[height][width];

		start.setG(0);
		start.setH(computeHeuristics(start.getX(), start.getY()));;
		start.setF(start.getG() + start.getH());
		openSet.add(start);
		lowestCost[start.getX()][start.getY()] = start.getG();
		isInOpenSet[start.getX()][start.getY()] = true;
				
		nodesExpanded++;
		while(!openSet.isEmpty()) {
			Node node = openSet.remove();
			isInOpenSet[node.getX()][node.getY()] = false;
			
			isGridVisited[node.getX()][node.getY()] = true;
			
			if (world[node.getX()][node.getY()] == GOAL_SIGN) {
				return node;
			}
			
			for (int dx = -1; dx <= 1; dx++) {
				for (int dy = -1; dy <= 1; dy++) {

					if (dx== 1 && dy == 0 || dx== -1 && dy == 0 || dx == 0 && dy == 1 || dx== 0 && dy == -1) {
						int childX = dx + node.getX();
						int childY = dy + node.getY();
						if (isValid( childX, childY) && !isGridVisited[childX][childY]) {
							
			                Node child = new Node(childX,childY, node, computeCost(world[childX][childY]), 0, 0);

			                double tempG = node.getG() + child.getCurrentCost();

							if (!isInOpenSet[child.getX()][child.getY()] || tempG < lowestCost[child.getX()][child.getY()] ) {
								
								child.setParent(node);
								child.setG(tempG);
								child.setH(computeHeuristics(child.getX(), child.getY()));
								child.setF(child.getG() + child.getH());
								
								lowestCost[child.getX()][child.getY()] = child.getG();
								
								if (!isInOpenSet[child.getX()][child.getY()]) {
									
									
									openSet.add(child);
									isInOpenSet[child.getX()][child.getY()] = true;
								} else {
									
									openSet.remove(child);
									openSet.add(child);
								}
								nodesExpanded++;
							}
			                
						}
					}
				}
			}
			
		}
		return null;
	}
	
	/**
	 * Implement Iterative Deepening DFS algorithm to find a path from start state to goal state.
	 * 
	 * @return	the Node in goal state, or <code>null</code>
	 */
	public Node IDDFS() {

		isMaxDepthReach = true;
		int i = 1;
		while (isMaxDepthReach) {
			Node goal = this.DFS(i);
			i++;
			if (goal != null)
				return goal;
		}
		
		return null;
	}
	
	/**
	 * Implement Bidirectional Search algorithm to find a path from start state to goal state.
	 * 
	 * @return the Node in goal state, or <code>null</code>
	 */
	public Node bidirectionalSearch() {
		boolean[][] isGridVisited1 = new boolean[height][width];
		boolean[][] isGridVisited2 = new boolean[height][width];
		LinkedList<Node> forwardQueue = new LinkedList<Node>();
		LinkedList<Node> backwardQueue = new LinkedList<Node>();
		forwardQueue.add(start);
		backwardQueue.add(goal);
		nodesExpanded += 2;
		
		isGridVisited1[start.getX()][start.getY()] = true;
		isGridVisited2[goal.getX()][goal.getY()] = true;
		
		while (!forwardQueue.isEmpty() && !backwardQueue.isEmpty()) {
			Node node = forwardQueue.remove();
			
			for (int dx = -1; dx <= 1; dx++) {
				for (int dy = -1; dy <= 1; dy++) {

					if (dx== 1 && dy == 0 || dx== -1 && dy == 0 || dx == 0 && dy == 1 || dx== 0 && dy == -1) {
						int childX = dx + node.getX();
						int childY = dy + node.getY();
						if (isValid( childX, childY) && !isGridVisited1[childX][childY]) {

							isGridVisited1[childX][childY] = true;
			                Node child = new Node(childX,childY, node, computeCost(world[childX][childY]), 0, 0);
			                
							if (world[child.getX()][child.getY()] == GOAL_SIGN) {
								return child;
							}
			                
							if (backwardQueue.contains(child)) {
							return connectPath(node, backwardQueue.get(backwardQueue.indexOf(child)));
						} 
							
			                forwardQueue.add(child);
			                nodesExpanded++;
						}
					}
				}
			}
			
			node = backwardQueue.remove();
			
			for (int dx = -1; dx <= 1; dx++) {
				for (int dy = -1; dy <= 1; dy++) {

					if (dx== 1 && dy == 0 || dx== -1 && dy == 0 || dx == 0 && dy == 1 || dx== 0 && dy == -1) {
						int childX = dx + node.getX();
						int childY = dy + node.getY();
						if (isValid( childX, childY) && !isGridVisited2[childX][childY]) {

							isGridVisited2[childX][childY] = true;
			                Node child = new Node(childX,childY, node, computeCost(world[childX][childY]), 0, 0);
			                
							if (world[child.getX()][child.getY()] == START_SIGN) {
								return connectPath(null, node);
							}
							
							if (forwardQueue.contains(child)) {
							return connectPath(forwardQueue.get(forwardQueue.indexOf(child)), node);
						}
			                
			                backwardQueue.add(child);
			                nodesExpanded++;
						}
					}
				}
			}
			
		}
		return null;
	}

	/**
	 * Connect two nodes and their parents in order to obtain a complete path from start node to goal.
	 * 
	 * @param node1	a node starting from the start node
	 * @param node2 a node starting from the goal
	 * @return
	 */
	private Node connectPath(Node node1, Node node2) {
		while (node2.getParent() != null) {
			Node temp = node2.getParent();
			node2.setParent(node1);
			node1 = node2;
			node2 = temp;
		}
		node2.setParent(node1);
		return node2;
	}
	
	/**
	 * Compute heuristic function. We use Euclidian distance here.
	 * 
	 * @param x	x coordinate of a given node
	 * @param y y coordinate of a given node
	 * @return
	 */
	private double computeHeuristics(int x, int y) {
//		return Math.abs(goal.getX() - x) + Math.abs(goal.getY());
		return Math.sqrt((goal.getX() - x) * (goal.getX() - x) + (goal.getY() - y) * (goal.getY() - y));
	}
	
	/**
	 * Map terrains to costs.
	 * 
	 * @param ch	terrain
	 * @return	corresponding cost
	 */
	private int computeCost(char ch) {
		if (ch == COST_OF_ONE_SIGN || ch == START_SIGN || ch ==GOAL_SIGN)
			return 1;
		else
			return 2;
	}
	
	/**
	 * Get the number of nodes expanded.
	 * 
	 * @return	number of nodes expanded
	 */
	public int getNodesExpanded() {
		return this.nodesExpanded;
	}
	
	public static void main(String[] args) {
		GridWorld gridWorld = new GridWorld(args[0]);
		Node goal;
		System.out.println("Please input a DIGIT to choose a search algorithm (1: BFS 2: DFS 3: A* Search 4: IDDFS 5: Bidirectional Search):");
		Scanner sc = new Scanner(System.in);
		switch (sc.nextInt()) {
		case 1: goal = gridWorld.BFS(); break;
		case 2: goal = gridWorld.DFS(-1); break;
		case 3: goal = gridWorld.AStarSearch(); break;
		case 4: 
			goal = gridWorld.IDDFS(); 
			break;
		case 5: goal = gridWorld.bidirectionalSearch(); break;
		default: 
			System.out.println("Input error!");
			sc.close();
			return;
		}
		sc.close();
		
		if (goal != null) {
			int cost = 0;
			System.out.println("path found:");
			Stack<Node> route = new Stack<Node>();
			while (goal != null) {
				route.push(goal);
				cost += goal.getCurrentCost();
				goal = goal.getParent();
			}
			cost--;
			int count = 0;
			while (!route.isEmpty()) {
				Node node = route.pop();
				System.out.print(node);
				count++;
				if (count > 10) {
					System.out.println();
					count = 0;
				}
			}
			System.out.println();
			System.out.println("total cost: " + cost);
			System.out.println("total number of nodes expanded: " + gridWorld.getNodesExpanded());
		} else {
			System.out.println("No path found");
			System.out.println("total cost: infinity");
			System.out.println("total number of nodes expanded: " + gridWorld.getNodesExpanded());
		}
		
	}
	

	
}
