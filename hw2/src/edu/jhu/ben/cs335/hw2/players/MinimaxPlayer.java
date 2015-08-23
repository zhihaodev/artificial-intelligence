package edu.jhu.ben.cs335.hw2.players;

import java.util.ArrayList;

import edu.jhu.ben.cs335.hw2.Node;
import edu.jhu.ben.cs335.hw2.board.Board;
import edu.jhu.ben.cs335.hw2.board.Chip;
import edu.jhu.ben.cs335.hw2.board.Move;

/** minimax Player class for Konane game
 *
 * @author Zhihao Cao
 */
public class MinimaxPlayer extends Player {

	private int maximumDepthReached;
	private int maximumDepthReachedThisTurn;
	private int maxDepth;
	private Chip player;
	private int nodesExplored;
	private int nodesExploredThisTurn;

	/**
	 * Create a new MinimaxPlayer
	 * 
	 * @param player	the color of this player
	 * @param maxDepth	depth bound
	 */
	public MinimaxPlayer(Chip player, int maxDepth) {
		this.player = player;
		this.maxDepth = maxDepth;
		this.nodesExplored = 0;
		this.nodesExploredThisTurn = 0;
		this.maximumDepthReachedThisTurn = 0;
		this.maximumDepthReached = 0;
	}
	
	
	/**
	 * Get a best move determined by Minimax algorithm
	 * 
	 * @param game the current game state
	 */
	public Move getMove(Board game) {
		
		this.maximumDepthReachedThisTurn = 0;
		Move ret = new Move(-1, -1, -1, -1);

		ArrayList<Move> moveList = game.getLegalMoves();
		/* tell the player what her options are */
		System.out.println("Turn " + game.getTurn() + ", legal moves (" + moveList.size() + "): ");
		for(Move m : moveList) {
			System.out.println(m.toString());
		}
		
		this.nodesExploredThisTurn = 0;
		Node state = new Node(game, null, 0);
		
		long startTime = System.nanoTime();
		
		ret = this.minimaxDecision(state);

		long endTime = System.nanoTime();
		double duration = ((double)(endTime - startTime)) / 1000000000.0;
		
		this.maximumDepthReached += this.maximumDepthReachedThisTurn;
		System.out.println("Maximum depth reached from game start state: " + this.maximumDepthReached);
		System.out.println("Nodes explored at this turn: " + this.nodesExploredThisTurn);
		System.out.println("Total nodes explored: " + this.nodesExplored);
		System.out.println("Time to decide on a move: " + duration);

		return ret;
	}

	/**
	 * Subfunction of Minimax algorithm
	 * 
	 * @param state	current state in the search tree
	 * @return	a best move determined by Minimax algorithm
	 */
	private Move minimaxDecision(Node state) {
		int max = Integer.MIN_VALUE;
		Move bestMove = null;
		for (Move move : state.getGame().getLegalMoves()) {
			int value = this.minValue(state.result(move));
			if (max < value) { 
				max = value;
				bestMove = move;
			}
		}
		return bestMove;
	}
	
	/**
	 * Subfunction of Minimax algorithm
	 * 
	 * @param state	current state in the search tree
	 * @return	maximum utility value of all its successors
	 */
	private int maxValue(Node state) {
		this.nodesExplored++;
		this.nodesExploredThisTurn++;
		
		if (this.cutoffTest(state)) {
			return state.eval(this.player);
		}

		int v = Integer.MIN_VALUE;
		for (Move move : state.getGame().getLegalMoves()) {
			int value = this.minValue(state.result(move));
			v = Math.max(v, value);
		}
		return v;
	}

	/**
	 * Subfunction of Minimax algorithm
	 * 
	 * @param state current state in the search tree
	 * @return	minimum utility value of all its successors
	 */
	private int minValue(Node state) {
		this.nodesExplored++;
		this.nodesExploredThisTurn++;
		
		if (this.cutoffTest(state)) {
			return state.eval(this.player);
			
		}

		int v = Integer.MAX_VALUE;
		for (Move move : state.getGame().getLegalMoves()) {
			int value = this.maxValue(state.result(move));
			v = Math.min(v, value);
		}
		return v;
	}

	/**
	 * Subfunction of Minimax algorithm
	 * 
	 * @param state	current state in the search tree
	 * @return	true if the search tree need to be cut off; false otherwise
	 */
	private boolean cutoffTest(Node state) {
		boolean checkCutOff = state.getGame().gameWon() != Chip.NONE || state.getDepth() >= this.maxDepth;
		if (checkCutOff) {
			if (this.maximumDepthReachedThisTurn < state.getDepth())
				this.maximumDepthReachedThisTurn = state.getDepth();
		}
			
		return checkCutOff;
	}



}
