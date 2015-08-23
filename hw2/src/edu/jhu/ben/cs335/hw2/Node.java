package edu.jhu.ben.cs335.hw2;

import java.util.ArrayList;

import edu.jhu.ben.cs335.hw2.board.Board;
import edu.jhu.ben.cs335.hw2.board.Chip;
import edu.jhu.ben.cs335.hw2.board.InvalidMoveException;
import edu.jhu.ben.cs335.hw2.board.Move;

/**
 * This class represents a node in the Minimax search tree.
 * 
 * @author Zhihao Cao
 *
 */
public class Node {

	private Board game;
	private Move bestMove;
	private int depth;

	/**
	 * Get the best move of this node
	 * 
	 * @return	the best move of this node
	 */
	public Move getBestMove() {
		return this.bestMove;
	}

	/**
	 * Set the best move of this node
	 * 
	 * @param bestMove	
	 */
	public void setBestMove(Move bestMove) {
		this.bestMove = bestMove;
	}
	
	/**
	 * Get the depth of this node in the search tree
	 * 
	 * @return	the depth of this node in the search tree
	 */
	public int getDepth() {
		return this.depth;
	}
	
	/**
	 * Set the depth of this node in the search tree
	 * 
	 * @param depth
	 */
	public void setDepth(int depth) {
		this.depth = depth;
	}

	/**
	 * Create a new Node
	 * 
	 * @param game		gameboard for the game of Konane
	 * @param depth		depth in the search tree  
	 */
	public Node(Board game, Node parent, int depth) {
		this.game = game;
		this.depth = depth;
	}

	/**
	 * Get the gameboard in this node
	 * 
	 * @return
	 */
	public Board getGame() {
		return game;
	}
	
	/**
	 * Set the gameboard in this node
	 * 
	 * @param game
	 */
	public void setGame(Board game) {
		this.game = game;
	}

	/**
	 * Subfunction of Minimax algorithm
	 * 
	 * @param move	a move in the gameboard
	 * @return	the successor of this node given the move
	 */
	public Node result( Move move) {
		try {
			Board nextGame = new Board(this.game);
			nextGame.executeMove(move);

			Node nextNode = new Node(nextGame, this, this.depth + 1);
			return nextNode;
		} catch ( InvalidMoveException e) {
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * Subfunction of Minimax algorithm; Calculate an estimate of the expected utility of the game
	 * by the evaluation function
	 * 
	 * @param player	the player of this node	
	 * @return			an estimate of the expected utility of the game
	 */
	public int eval(Chip player) {

		int value = 0;
		int minChoice = Integer.MAX_VALUE / 2;
		ArrayList<Move> legalMoves = this.game.getLegalMoves();
		int friendNum = 0;
		int enemyNum = 0;

		if (game.gameWon() == Chip.NONE) {

			for (int i = 0; i < this.game.getSize(); i++) {
				for (int j = 0; j < this.game.getSize(); j++) {
					if (this.game.getTile(i, j).getChip() == player)
						friendNum++;
					else if (this.game.getTile(i, j).getChip() != Chip.NONE)
						enemyNum++;
				}
			}

			for (Move move : legalMoves) {
				int choice = this.result(move).getGame().getLegalMoves().size();
				if (minChoice > choice) {
					minChoice = choice;
				}
			}
			value = legalMoves.size() - 2 * minChoice + friendNum - enemyNum;

		} else if (game.gameWon() == player) {
			value = Integer.MAX_VALUE / 2;
		} else {
			value = Integer.MIN_VALUE / 2;
		}

		if (player == Chip.BLACK && !this.game.isBlackToPlay() || player == Chip.WHITE && this.game.isBlackToPlay() ) {
			if (game.gameWon() == Chip.NONE)
				value = 2 * legalMoves.size() - minChoice + friendNum - enemyNum;
			value = - value;
		}

		return value;

	}

}
