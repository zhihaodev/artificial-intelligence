package edu.jhu.ben.cs335.hw2.players;

import java.util.ArrayList;
import edu.jhu.ben.cs335.hw2.Node;
import edu.jhu.ben.cs335.hw2.board.Board;
import edu.jhu.ben.cs335.hw2.board.Chip;
import edu.jhu.ben.cs335.hw2.board.Move;

/** time bounded iterative deepening alpha-beta pruning Player class for Konane game
*
* @author Zhihao Cao
*/
public class TimeBoundedIterativeDeepeningAlphaBetaPlayer extends Player {

	private int maximumDepthReached;
	private double maxTime;
	private int maximumDepthReachedThisTurn;
	private int maxDepth;
	private Chip player;
	private int nodesExplored;
	private Move bestMoveSoFar;
	private long startMili;
	private boolean isEndGameStatusFound;
	private boolean moveOrdering;
	private int nodesExploredThisTurn;
	
	public TimeBoundedIterativeDeepeningAlphaBetaPlayer(Chip player, double maxTime, boolean moveOrdering) {
		this.player = player;
		this.maxTime = maxTime;
		this.moveOrdering = moveOrdering;
		this.nodesExplored = 0;
		this.maximumDepthReachedThisTurn = 0;
		this.maxDepth = 0;
		this.bestMoveSoFar = null;
		this.isEndGameStatusFound = false;
		this.maximumDepthReached = 0;
		this.nodesExploredThisTurn = 0;
	}	
	
	/**
	 * Get a best move determined by time bounded iterative deepening alpha-beta pruning algorithm
	 * 
	 * @param game the current game state
	 */
	@Override
	public Move getMove(Board game) {
		
		this.maximumDepthReachedThisTurn = 0;
		ArrayList<Move> moveList = game.getLegalMoves();
		/* tell the player what her options are */
		System.out.println("Turn " + game.getTurn() + ", legal moves (" + moveList.size() + "): ");
		for(Move m : moveList) {
			System.out.println(m.toString());
		}
		
		this.nodesExploredThisTurn = 0;
		this.isEndGameStatusFound = false;
		this.maxDepth = 1;
		this.bestMoveSoFar = null;
		this.startMili = System.currentTimeMillis();
	
		while (System.currentTimeMillis() - startMili < this.maxTime * 1000) {
			Node state = new Node(game, null, 0);
			this.bestMoveSoFar = this.alphaBetaSearch(state);
			
			if (this.isEndGameStatusFound)
				break;
			
			this.maxDepth++;
		}

		long endMili=System.currentTimeMillis();
		double duration = ((double)(endMili - startMili)) / 1000.0;
		
		this.maximumDepthReached += this.maximumDepthReachedThisTurn;
		System.out.println("Maximum depth reached at this turn: " + this.maximumDepthReachedThisTurn);
		System.out.println("Maximum depth reached from game start state: " + this.maximumDepthReached);
		System.out.println("Nodes explored at this turn: " + this.nodesExploredThisTurn);
		System.out.println("Total nodes explored: " + this.nodesExplored);
		System.out.println("Time to decide on a move: " + duration);

		return this.bestMoveSoFar;
		
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
		
		if (System.currentTimeMillis() - startMili > this.maxTime * 1000) {
			return this.bestMoveSoFar;
		}

		if (Math.abs(v) == Integer.MAX_VALUE / 2 || Math.abs(v) == Math.abs(Integer.MIN_VALUE / 2) ) {
			
			this.isEndGameStatusFound = true;
			return state.getBestMove();
		}
		
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
		
		ArrayList<Move> legalMoves = state.getGame().getLegalMoves();
	
		if (this.moveOrdering) {
			this.quickSort(state, legalMoves, 0, legalMoves.size() - 1, false);
		}
		
		int v = Integer.MIN_VALUE;
		Move bestMove = null;
		for (Move move : legalMoves) {
			
			if (System.currentTimeMillis() - startMili > this.maxTime * 1000) {
				return 0;
			}
			
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
		
		
		ArrayList<Move> legalMoves = state.getGame().getLegalMoves();
		
		if (this.moveOrdering) {
			this.quickSort(state, legalMoves, 0, legalMoves.size() - 1, true);
		}
		
		int v = Integer.MAX_VALUE;
		Move bestMove = null;
		for (Move move : legalMoves) {

			if (System.currentTimeMillis() - startMili > this.maxTime * 1000)
				return 0;
			
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
