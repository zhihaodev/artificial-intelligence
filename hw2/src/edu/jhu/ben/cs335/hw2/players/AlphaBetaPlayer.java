package edu.jhu.ben.cs335.hw2.players;

import java.util.ArrayList;

import edu.jhu.ben.cs335.hw2.Node;
import edu.jhu.ben.cs335.hw2.players.Player;
import edu.jhu.ben.cs335.hw2.board.Board;
import edu.jhu.ben.cs335.hw2.board.Chip;
import edu.jhu.ben.cs335.hw2.board.Move;

/** alpha-beta pruning Player class for Konane game
 *
 * @author Zhihao Cao
 */
public class AlphaBetaPlayer extends Player {

	private int maximumDepthReached;
	private int maximumDepthReachedThisTurn;
	private int maxDepth;
	private int nodesExplored;
	private int nodesExploredThisTurn;
	private Chip player;
	private boolean moveOrdering;
	
	/**
	 * Create an AlphaBetaPlayer
	 * 
	 * @param player		the color of this player
	 * @param maxDepth		depth bound
	 * @param moveOrdering	whether this player need move ordering
	 */
	public AlphaBetaPlayer(Chip player, int maxDepth, boolean moveOrdering) {
		this.player = player;
		this.maxDepth = maxDepth;
		this.moveOrdering = moveOrdering;
		this.nodesExplored = 0;
		this.nodesExploredThisTurn = 0;
		this.maximumDepthReachedThisTurn = 0;
		this.maximumDepthReached = 0;
	}

	/**
	 * Get a best move determined by Minimax with alpha-beta pruning algorithm
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
		
		ret = this.alphaBetaSearch(state);
		
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
	 * Subfunction of Alpha-Beta search algorithm
	 * 
	 * @param state	current state in the search tree
	 * @return	a best move determined by Alpha-Beta search algorithm
	 */
	private Move alphaBetaSearch(Node state) {
		int v = this.maxValue(state, Integer.MIN_VALUE, Integer.MAX_VALUE);
		this.nodesExplored--;
		this.nodesExploredThisTurn--;
		
		return state.getBestMove();
	}

	/**
	 * Subfunction of Alpha-Beta search algorithm
	 * 
	 * @param state	current state in the search tree
	 * @param alpha	the value of the best choice we have found so far at any choice point along the path for MAX
	 * @param beta	the value of the best choice we have found so far at any choice point along the path for MIN
	 * @return	maximum utility value of all its successors
	 */
	private int maxValue(Node state, int alpha, int beta) {
		this.nodesExplored++;
		this.nodesExploredThisTurn++;

		if (this.cutoffTest(state)) {
			return state.eval(this.player);

		}
		int v = Integer.MIN_VALUE;
		Move bestMove = null;

		ArrayList<Move> legalMoves = state.getGame().getLegalMoves();
		
		if (this.moveOrdering) {
			this.quickSort(state, legalMoves, 0, legalMoves.size() - 1, false);
		}

		for (Move move : legalMoves) {

			int value = this.minValue(state.result(move), alpha, beta);
			if (v < value) { //
				v = value;
				bestMove = move;
			}

			if (v >= beta) {
				state.setBestMove(bestMove);

				return v;
			}
			alpha = Math.max(v, alpha);
		}
		state.setBestMove(bestMove);
		return v;
	}
	
	/**
	 * Subfunction of Alpha-Beta search algorithm
	 * 
	 * @param state	current state in the search tree
	 * @param alpha	the value of the best choice we have found so far at any choice point along the path for MAX.
	 * @param beta	the value of the best choice we have found so far at any choice point along the path for MIN
	 * @return	minimum utility value of all its successors
	 */
	private int minValue(Node state, int alpha, int beta) {
		this.nodesExplored++;
		this.nodesExploredThisTurn++;

		if (this.cutoffTest(state)) {
			return state.eval(this.player);
		}
		int v = Integer.MAX_VALUE;
		Move bestMove = null;

		ArrayList<Move> legalMoves = state.getGame().getLegalMoves();
		if (this.moveOrdering) {
			this.quickSort(state, legalMoves, 0, legalMoves.size() - 1, true);
		}

		for (Move move : legalMoves) {
			int value = this.maxValue(state.result(move), alpha, beta);
			if (v > value) { //
				v = value;
				bestMove = move;
			}

			if (v <= alpha) {
				state.setBestMove(bestMove);
				return v;
			}
			beta = Math.min(v, beta);
		}
		state.setBestMove(bestMove);
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

	/**
	 * partition function for quicksort
	 * 
	 * @param state	current state in the search tree
	 * @param A				ArrayList needed to be sort
	 * @param p				beginning index
	 * @param r				ending index
	 * @param isAscending	whether A should be sorted in ascending order
	 * @return				index of the pivot
	 */
	private int partition(Node state, ArrayList<Move> A, int p, int r, boolean isAscending) {
		Move pivot = A.get(r);
		int i = p - 1;
		for (int j = p; j < r; j++) {
			
			if (isAscending) {
				if (state.result(A.get(j)).eval(this.player) <= state.result(pivot).eval(this.player)) {
					i++;
					Move temp = A.get(i);
					A.set(i, A.get(j));
					A.set(j, temp);
				}
			} else {
				if (state.result(A.get(j)).eval(this.player) >= state.result(pivot).eval(this.player)) {
					i++;
					Move temp = A.get(i);
					A.set(i, A.get(j));
					A.set(j, temp);
				}
			}
		}
		Move temp = A.get(i + 1);
		A.set(i + 1, A.get(r));
		A.set(r, temp);
		return (i + 1);
	}

	/**
	 * Sort a given arraryList with quicksort
	 * 
	 * @param state	current state in the search tree
	 * @param A				ArrayList needed to be sort
	 * @param p				beginning index
	 * @param r				ending index
	 * @param isAscending	whether A should be sorted in ascending order
	 */
	private void quickSort(Node state, ArrayList<Move> A, int p, int r, boolean isAscending) {
		if (p < r) {
			int q = this.partition(state, A, p, r, isAscending);
			this.quickSort(state, A, p, q - 1, isAscending);
			this.quickSort(state, A, q + 1, r, isAscending);
		}
	}
	

}

