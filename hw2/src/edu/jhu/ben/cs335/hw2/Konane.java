package edu.jhu.ben.cs335.hw2;

import java.util.Scanner;

import edu.jhu.ben.cs335.hw2.board.Board;
import edu.jhu.ben.cs335.hw2.board.Move;
import edu.jhu.ben.cs335.hw2.board.Chip;
import edu.jhu.ben.cs335.hw2.players.MinimaxPlayer;
import edu.jhu.ben.cs335.hw2.players.Player;
import edu.jhu.ben.cs335.hw2.players.HumanPlayer;
import edu.jhu.ben.cs335.hw2.players.AlphaBetaPlayer;
import edu.jhu.ben.cs335.hw2.players.TimeBoundedIterativeDeepeningAlphaBetaPlayer;
import edu.jhu.ben.cs335.hw2.players.TimeBoundedIterativeDeepeningMiniMaxPlayer;

/**
 * Driver class for Konane program; see README for description and terms of use
 * @author Ben Mitchell
 */
public class Konane {

	public static void main(String[] args) {
		Board game;
		int boardSize;
		Player[] players = new Player[2];
		Scanner stdin = new Scanner(System.in);

		try {

			/* prompt user for game setup */
			System.out.println("Let's play Konane!");
			System.out.print("Enter board size: ");
			boardSize = stdin.nextInt();

			/* initialize game */
			game = new Board(boardSize);

			for (int i=0; i<2; i++) {
				System.out.print("Enter player type for ");
				if (i == 0)
					System.out.print("Black");
				else
					System.out.print("White");

				System.out.print(" (1=human, 2=minmax, 3=alphabeta): ");
				int choice = stdin.nextInt();
				
				Chip player = i == 0 ? Chip.BLACK : Chip.WHITE;
				
				switch (choice) {
				case 1:
					players[i] = new HumanPlayer();
					break;
				case 2:
					System.out.print("please input minimax type(1 = fixed depth bound, 2 = fixed time bound): ");
					if (stdin.nextInt() == 1) {
						System.out.print("Please input a maximum search depth: ");
						int maxDepth = stdin.nextInt();
						players[i] = new MinimaxPlayer(player, maxDepth);
					} else {
						System.out.print("Please input a maximum time in seconds: ");
						int maxTime = stdin.nextInt();
						players[i] = new TimeBoundedIterativeDeepeningMiniMaxPlayer(player, maxTime);
					}
					break;
				case 3:
					System.out.print("please input alphabeta type(1 = fixed depth bound, 2 = fixed time bound): ");
					if (stdin.nextInt() == 1) {
						System.out.print("Please input a maximum search depth: ");
						int maxDepth = stdin.nextInt();
						System.out.print("Please input move-ordering type (0 = naive move-ordering, 1 = heuristic move-ordering): ");
						boolean moveOrdering = stdin.nextInt() == 0 ? false : true;
						players[i] = new AlphaBetaPlayer(player, maxDepth, moveOrdering);
					} else {
						System.out.print("Please input a maximum time in seconds: ");
						int maxTime = stdin.nextInt();
						System.out.print("Please input move-ordering type (0 = naive move-ordering, 1 = heuristic move-ordering): ");
						boolean moveOrdering = stdin.nextInt() == 0 ? false : true;
						players[i] = new TimeBoundedIterativeDeepeningAlphaBetaPlayer(player, maxTime, moveOrdering);
						///
					}
					break;
	
				default:
					System.out.println("bad agent type given, please try again...");
							i--;
				}
			}

			System.out.println("\n===================");

			/* take turns until gameover */
			while ( game.gameWon() == Chip.NONE ) {  
				System.out.print("Turn " + game.getTurn() + ", ");
				if (game.getTurn()%2 == 0) {
					System.out.println("black to play:");
				} else {
					System.out.println("white to play:");
				}

				System.out.println(game);

				Move m = players[game.getTurn()%players.length].getMove(game);
				game.executeMove(m);

				System.out.println("This player has played: " + m.toString());
				System.out.println();
			}

			System.out.println("Game over!  Final board state:\n" + game);

			if (game.gameWon() == Chip.BLACK) {
				System.out.println("Game won by Black after " + game.getTurn() + " turns");
			} else {
				System.out.println("Game won by White after " + game.getTurn() + " turns");
			}


		} catch (Exception e) {
			System.err.println("Caught an exception: \n\t" + e.toString());
		}

	}

}
