package edu.jhu.ben.cs335.hw2.players;

import java.util.ArrayList;

import edu.jhu.ben.cs335.hw2.Node;
import edu.jhu.ben.cs335.hw2.board.Board;
import edu.jhu.ben.cs335.hw2.board.Chip;
import edu.jhu.ben.cs335.hw2.board.Move;

/** time bounded iterative deepening minimax Player class for Konane game
*
* @author Zhihao Cao
*/
public class TimeBoundedIterativeDeepeningMiniMaxPlayer extends Player {

	private int maximumDepthReached;
	private double maxTime;
	private int maximumDepthReachedThisTurn;
	private int maxDepth;
	private Chip player;
	private int nodesExplored;
	private Move bestMoveSoFar;
	private long startMili;
	private boolean isEndGameStatusFound;
	private int nodesExploredThisTurn;
	
	/**
	 * Create a TimeBoundedIterativeDeepeningMiniMaxPlayer
	 * @param player	the color of this player
	 * @param maxTime	time bound
	 */
	public TimeBoundedIterativeDeepeningMiniMaxPlayer(Chip player, double maxTime) {
		this.player = player;
		this.maxTime = maxTime;
		this.nodesExplored = 0;
		this.maximumDepthReachedThisTurn = 0;
		this.maxDepth = 0;
		this.bestMoveSoFar = null;
		this.isEndGameStatusFound = false;
		this.maximumDepthReached = 0;
		this.nodesExploredThisTurn = 0;
	}
	
	/**
	 * Get a best move determined by time bounded iterative deepening minimax Minimax algorithm
	 * 
	 * @param game the current game state
	 */
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
			this.bestMoveSoFar = this.minimaxDecision(state);
			
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
	 * Subfunction of Minimax algorithm
	 * 
	 * @param state	current state in the search tree
	 * @return	a best move determined by Minimax algorithm
	 */
	private Move minimaxDecision(Node state) {
		
		if (System.currentTimeMillis() - startMili > this.maxTime * 1000)
			return this.bestMoveSoFar;
		
		double max = Integer.MIN_VALUE;
		Move bestMove = null;
		for (Move move : state.getGame().getLegalMoves()) {
			double value = this.minValue(state.result(move));
			if (max < value) {	//
				max = value;
				bestMove = move;
			}
			if (Math.abs(max) == Integer.MAX_VALUE / 2 || Math.abs(max) == Math.abs(Integer.MIN_VALUE / 2) ) { 
				this.isEndGameStatusFound = true;
				return bestMove;
			}
		}

		if (Math.abs(max) == Integer.MAX_VALUE / 2 || Math.abs(max) == Math.abs(Integer.MIN_VALUE / 2) ) { 
			this.isEndGameStatusFound = true;
		}
		
		return bestMove;
	}

	/**
	 * Subfunction of Minimax algorithm
	 * 
	 * @param state	current state in the search tree
	 * @return	maximum utility value of all its successors
	 */
	private double maxValue(Node state) {
		
		if (System.currentTimeMillis() - startMili > this.maxTime * 1000)
			return 0;
		
		this.nodesExplored++;
		this.nodesExploredThisTurn++;
		
		if (this.cutoffTest(state)) {
			return state.eval(this.player);
		}

		double v = Integer.MIN_VALUE;
		for (Move move : state.getGame().getLegalMoves()) {
			double value = this.minValue(state.result(move));
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
	private double minValue(Node state) {
		
		if (System.currentTimeMillis() - startMili > this.maxTime * 1000)
			return 0;
		
		this.nodesExplored++;
		this.nodesExploredThisTurn++;
		
		if (this.cutoffTest(state)) {
			return state.eval(this.player);
		}

		double v = Integer.MAX_VALUE;
		for (Move move : state.getGame().getLegalMoves()) {
			double value = this.maxValue(state.result(move));
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
