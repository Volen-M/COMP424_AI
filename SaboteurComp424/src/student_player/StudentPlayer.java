package student_player;

import boardgame.Move;

import Saboteur.SaboteurPlayer;
import Saboteur.cardClasses.SaboteurBonus;
import Saboteur.cardClasses.SaboteurCard;
import Saboteur.cardClasses.SaboteurDestroy;
import Saboteur.cardClasses.SaboteurDrop;
import Saboteur.cardClasses.SaboteurMalus;
import Saboteur.cardClasses.SaboteurMap;
import Saboteur.cardClasses.SaboteurTile;
import Saboteur.SaboteurBoardState;
import Saboteur.SaboteurMove;

import java.util.*;

/** A player file submitted by a student. */
public class StudentPlayer extends SaboteurPlayer {

	private int[] nbCardsLeft;
	private float[] cardApproxProbs;
	private int malusBefore;
	private int enemyMalusBefore;
	private int dropOrMapAmt;
	private boolean[] goalInterest;
	private boolean goalUncovered;

	private SaboteurTile[][] oldBoard;
	private SaboteurTile[][] newBoard;
	private ArrayList<SaboteurCard> hand;

	public static ArrayList<String> blockCards = new ArrayList<String>(
			Arrays.asList("1", "2", "3", "4", "11", "12", "13", "14", "15"));
	public static ArrayList<String> priorityDrop = new ArrayList<String>(
			Arrays.asList("Destroy", "10","bonus", "7", "5", "9", "0", "6", "8"));

	/**
	 * You must modify this constructor to return your student number. This is
	 * important, because this is what the code that runs the competition uses to
	 * associate you with your agent. The constructor should do nothing else.
	 */

	public StudentPlayer() {
		super("260746982");
		cardApproxProbs = new float[20];
		dropOrMapAmt = 0;
		enemyMalusBefore = 0;
		malusBefore = 0;
		nbCardsLeft = new int[] { 4, 1, 1, 1, 1, 4, 5, 5, 5, 5, 3, 1, 1, 1, 1, 1, 3, 2, 4, 6 };
		goalInterest = new boolean[] { true, true, true };
		goalUncovered = false;
		oldBoard = new SaboteurTile[14][14];
		newBoard = new SaboteurTile[14][14];
	}

	/**
	 * This is the primary method that you need to implement. The ``boardState``
	 * object contains the current state of the game, which your agent must use to
	 * make decisions.
	 */

	public Move chooseMove(SaboteurBoardState boardState) {

		// --------- Initial Setup ------------
		Move myMove;
		int[][] boardInt = boardState.getHiddenIntBoard();
		int[][] intBoard = new int[42][42];
		intBoard = boardInt.clone();
		updateGoals(boardState, intBoard);
		DeckApproximator(boardState);
		UpdateProbabilities(boardState);
		enemyMalusBefore = boardState.getNbMalus((boardState.getTurnPlayer() + 1) % 2);
		ArrayList<SaboteurCard> hand = boardState.getCurrentPlayerCards();
		System.out.println("Goal 1: " + Boolean.toString(goalInterest[0]) + " - Goal 2: "
				+ Boolean.toString(goalInterest[1]) + " - Goal 3:" + Boolean.toString(goalInterest[2]));
		// ----------------------------------
		
		if (boardState.getNbMalus(boardState.getTurnPlayer()) > 0) {
			myMove = malusCase(boardState, intBoard, hand);
		} else {
			myMove = nonMalusCase(boardState, intBoard, hand);
		}

		// ---------- Ending Stats to be kept ---------------
		oldBoard = boardState.getHiddenBoard();
		malusBefore = boardState.getNbMalus(boardState.getTurnPlayer());
		// --------------------------------------------------
		if(!boardState.isLegal((SaboteurMove)myMove)) {
			myMove = myMove = boardState.getRandomMove();
		}
		return myMove;
	}

	public Move nonMalusCase(SaboteurBoardState boardState, int[][] intBoard, ArrayList<SaboteurCard> hand) {
		boolean avoidLoosing = false;
		ArrayList<int[]> closeEnds = MyTools.getOneTileAwayEnds(intBoard, goalInterest);
		ArrayList<SaboteurMove> legalMoves = boardState.getAllLegalMoves();
		if (closeEnds.size() != 0) {
			for (SaboteurMove move : legalMoves) {
				if (MyTools.isWinningMove(move, intBoard, goalInterest)) {
					return move;
				}
			}
			if (hasCard("malus", hand)) {
				SaboteurMove move = new SaboteurMove(new SaboteurMalus(), 0, 0, boardState.getTurnPlayer());
				if (boardState.isLegal(move)) {
					enemyMalusBefore += 1;
					return move;
				}
			}
			else if (hasCard("destroy", hand)) {
				SaboteurMove move = new SaboteurMove(new SaboteurDestroy(), (int) (closeEnds.get(0)[0] / 3),
						(int) (closeEnds.get(0)[1] / 3), boardState.getTurnPlayer());
				if (boardState.isLegal(move)) {
					return move;
				}
			}
		}
		

		float boardScore = MyTools.bestScoreBoard(intBoard, goalInterest);
		float bestScore = 100000000;
		float currScore = 100000000;
		SaboteurMove bestMove = null;
		for (SaboteurMove move : legalMoves) {
			currScore = MyTools.newBestScoreFromPlay(move, intBoard, goalInterest);
			if (currScore < bestScore) {
				bestScore = currScore;
				bestMove = move;
			}
			int cardNb = MyTools.GetCardNumber(move.getCardPlayed().getName());
			if (cardNb >15 || cardNb == -1) {
				continue;
			}
			if (MyTools.givesWinOpening(bestMove, intBoard, goalInterest)){
				if (hasCard("malus", hand)) {
					SaboteurMove newMove = new SaboteurMove(new SaboteurMalus(), 0, 0, boardState.getTurnPlayer());
					if (boardState.isLegal(newMove)) {
						enemyMalusBefore += 1;
						return newMove;
					}
				}
			}
		}
		
		System.out.println(Float.toString(bestScore) + " -- " + Float.toString(boardScore));
		avoidLoosing = MyTools.givesWinOpening(bestMove, intBoard, goalInterest);
		if (bestScore <= boardScore
				&& (!avoidLoosing || boardState.getNbMalus((boardState.getTurnNumber() + 1) % 2) > 0)) {
			return bestMove;
		}
		if(avoidLoosing) {
			//PROBABILITY ESTIMATE GOES HERE BUT IS REMOVED DUE TO LACK OF USE!!!!!!!!!!!!!!!!!
		}

		System.out.println(Float.toString(bestScore) + " -- " + Float.toString(boardScore));
		if (bestScore <= boardScore && !avoidLoosing) {
			return bestMove;
		} else {
			avoidLoosing = false;
			if (hasCard("map", hand)) {
				if (!goalUncovered) {
					if (goalInterest[0]) {
						SaboteurMove move = new SaboteurMove(new SaboteurMap(), 12, 3, boardState.getTurnPlayer());
						if (boardState.isLegal(move)) {
							return move;
						}
					}
					if (goalInterest[2]) {
						SaboteurMove move = new SaboteurMove(new SaboteurMap(), 12, 7, boardState.getTurnPlayer());
						if (boardState.isLegal(move)) {
							return move;
						}
					}
					if (goalInterest[1]) {
						SaboteurMove move = new SaboteurMove(new SaboteurMap(), 12, 5, boardState.getTurnPlayer());
						if (boardState.isLegal(move)) {
							return move;
						}
					}
				} else {
					for (int i = 0; i < hand.size(); i++) {
						SaboteurCard card = hand.get(i);
						if (GetCardNumber(card.getName()) == GetCardNumber("map")) {
							SaboteurMove move = new SaboteurMove(new SaboteurDrop(), i, 0, boardState.getTurnPlayer());
							if (boardState.isLegal(move)) {
								return move;
							}
						}

					}
				}

			}
			for (int i = 0; i < hand.size(); i++) {
				SaboteurCard card = hand.get(i);
				for (String s : blockCards) {
					if (GetCardNumber(card.getName()) == GetCardNumber(s)) {
						SaboteurMove move = new SaboteurMove(new SaboteurDrop(), i, 0, boardState.getTurnPlayer());
						if (boardState.isLegal(move)) {
							return move;
						}
					}
				}
			}
			if (hasCard("malus", hand)) {
				SaboteurMove move = new SaboteurMove(new SaboteurMalus(), 0, 0, boardState.getTurnPlayer());
				if (boardState.isLegal(move)) {
					enemyMalusBefore += 1;
					return move;
				}
			}
			for (int i = 0; i < hand.size(); i++) {
				SaboteurCard card = hand.get(i);
				for (String s : priorityDrop) {
					if (GetCardNumber(card.getName()) == GetCardNumber(s)) {
						SaboteurMove move = new SaboteurMove(new SaboteurDrop(), i, 0, boardState.getTurnPlayer());
						if (boardState.isLegal(move)) {
							return move;
						}
					}
				}
			}
		}

		System.out.println("WHY DID THIS FAIL");
		Move myMove = boardState.getRandomMove();
		return myMove;
	}

	public Move malusCase(SaboteurBoardState boardState, int[][] intBoard, ArrayList<SaboteurCard> hand) {
		ArrayList<int[]> EndCoord = MyTools.getOneTileAwayEnds(intBoard, goalInterest);
		if (EndCoord.size() != 0) {
			if (hasCard("destroy", hand)) {

				SaboteurMove move = new SaboteurMove(new SaboteurDestroy(), (int) (EndCoord.get(0)[0] / 3),
						(int) (EndCoord.get(0)[1] / 3), boardState.getTurnPlayer());
				if (boardState.isLegal(move)) {
					return move;
				}
			}
			if (hasCard("malus", hand)) {
				SaboteurMove move = new SaboteurMove(new SaboteurMalus(), 0, 0, boardState.getTurnPlayer());
				if (boardState.isLegal(move)) {
					enemyMalusBefore += 1;
					return move;
				}
			}
		}
		if (hasCard("bonus", hand)) {
			SaboteurMove move = new SaboteurMove(new SaboteurBonus(), 0, 0, boardState.getTurnPlayer());
			if (boardState.isLegal(move)) {
				return move;
			}
		}
		if (hasCard("map", hand)) {
			if (!goalUncovered) {
				if (goalInterest[0]) {
					SaboteurMove move = new SaboteurMove(new SaboteurMap(), 12, 3, boardState.getTurnPlayer());
					if (boardState.isLegal(move)) {
						return move;
					}
				}
				if (goalInterest[2]) {
					SaboteurMove move = new SaboteurMove(new SaboteurMap(), 12, 7, boardState.getTurnPlayer());
					if (boardState.isLegal(move)) {
						return move;
					}
				}
				if (goalInterest[1]) {
					SaboteurMove move = new SaboteurMove(new SaboteurMap(), 12, 5, boardState.getTurnPlayer());
					if (boardState.isLegal(move)) {
						return move;
					}
				}
			} else {
				for (int i = 0; i < hand.size(); i++) {
					SaboteurCard card = hand.get(i);
					if (GetCardNumber(card.getName()) == GetCardNumber("map")) {
						SaboteurMove move = new SaboteurMove(new SaboteurDrop(), i, 0, boardState.getTurnPlayer());
						if (boardState.isLegal(move)) {
							return move;
						}
					}

				}
			}

		}

		for (int i = 0; i < hand.size(); i++) {
			SaboteurCard card = hand.get(i);
			for (String s : blockCards) {
				if (GetCardNumber(card.getName()) == GetCardNumber(s)) {
					SaboteurMove move = new SaboteurMove(new SaboteurDrop(), i, 0, boardState.getTurnPlayer());
					if (boardState.isLegal(move)) {
						return move;
					} else {
						System.out.print("There a Block Filter Bug 1");
					}
				}
			}
		}
		if (hasCard("malus", hand)) {
			SaboteurMove move = new SaboteurMove(new SaboteurMalus(), 0, 0, boardState.getTurnPlayer());
			if (boardState.isLegal(move)) {
				enemyMalusBefore += 1;
				return move;
			}
		}
		for (int i = 0; i < hand.size(); i++) {
			SaboteurCard card = hand.get(i);
			for (String s : priorityDrop) {
				if (GetCardNumber(card.getName()) == GetCardNumber(s)) {
					SaboteurMove move = new SaboteurMove(new SaboteurDrop(), i, 0, boardState.getTurnPlayer());
					if (boardState.isLegal(move)) {
						return move;
					}
				}
			}
		}

		System.out.println("Malus - WHY DID THIS FAIL");
		Move myMove = boardState.getRandomMove();
		return myMove;

	}

	public Move test(SaboteurBoardState boardState) {
		int[][] boardInt = boardState.getHiddenIntBoard();
		int[][] intBoard = new int[42][42];
		intBoard = boardInt.clone();
		updateGoals(boardState, intBoard);
		DeckApproximator(boardState);
		UpdateProbabilities(boardState);
		if (boardState.getTurnNumber() > 1) {
			for (int i = 0; i < 20; i++) {
				System.out.println(Integer.toString(i) + " : " + Integer.toString(nbCardsLeft[i]) + ", "
						+ Float.toString(cardApproxProbs[i]));
			}
		}
		System.out.println("Goal 1: " + Boolean.toString(goalInterest[0]) + " - Goal 2: "
				+ Boolean.toString(goalInterest[1]) + " - Goal 3:" + Boolean.toString(goalInterest[2]));
		Move myMove = boardState.getRandomMove();
		oldBoard = boardState.getHiddenBoard();
		ArrayList<int[]> allEnds = MyTools.getAllEnds(intBoard);
		for (int[] end : allEnds) {
			System.out.println("[" + end[0] + "," + end[1] + "]");
		}
		return myMove;
	}



	public boolean hasCard(String cardID, ArrayList<SaboteurCard> hand) {
		int cardNumber = GetCardNumber(cardID);
		for (SaboteurCard card : hand) {
			int handCardNumber = GetCardNumber(card.getName());
			if (cardNumber == handCardNumber) {
				return true;
			}
		}
		return false;
	}

	public void DeckApproximator(SaboteurBoardState boardState) {
		newBoard = boardState.getHiddenBoard();
		// System.out.println(Integer.toString(boardState.getTurnNumber()));
		if (boardState.getTurnNumber() < 1) {
			oldBoard = newBoard;
			return;
		}

		// Checks Malus Usage
		if (malusBefore < boardState.getNbMalus(boardState.getTurnPlayer())) {
			RemoveFromUsed("malus");
			return;
		}
		if (enemyMalusBefore > boardState.getNbMalus((boardState.getTurnPlayer() + 1) % 2)) {
			RemoveFromUsed("bonus");
			return;
		}

		// Checks Board Change
		for (int i = 0; i < 14; i++) {
			for (int j = 0; j < 14; j++) {
				if (!((i == 12 && (j == 3 || j == 5 || j == 7)) || (i == 5 && j == 5))) {
					if (oldBoard[i][j] == null && newBoard[i][j] != null) {
						RemoveFromUsed(newBoard[i][j].getName());
						return;
					}
					if (oldBoard[i][j] != null && newBoard[i][j] == null) {
						RemoveFromUsed("destroy");
						return;
					}
					if (oldBoard[i][j] != null && newBoard[i][j] != null) {
						if (!oldBoard[i][j].getName().toLowerCase().equals(newBoard[i][j].getName().toLowerCase())) {
							RemoveFromUsed(newBoard[i][j].getName());
							return;
						}
					}
				}
			}
		}

		// If nothing else
		// Its most likely a drop or map which neither help
		// Need to update
		dropOrMapAmt += 1;
		return;
	}

	private void updateGoals(SaboteurBoardState board, int[][] intBoard) {
		SaboteurCard[][] boardCards = board.getHiddenBoard();
		if (!goalUncovered) {
			String goal1Name = boardCards[12][3].getName().toLowerCase();
			// System.out.println(goal1Name + " is Goal 1");
			if (goal1Name.contains("8")) {
				goalInterest[0] = true;

			} else if (goal1Name.contains("nugget")) {
				goalUncovered = true;
				goalInterest[0] = true;
				goalInterest[1] = false;
				goalInterest[2] = false;
				return;
			} else if (goal1Name.contains("hidden")) {
				goalInterest[0] = false;

			}

			String goal2Name = boardCards[12][5].getName().toLowerCase();
			// System.out.println(goal2Name + " is Goal 2");
			if (goal2Name.contains("8")) {
				goalInterest[1] = true;

			} else if (goal2Name.contains("nugget")) {
				goalUncovered = true;
				goalInterest[1] = true;
				goalInterest[0] = false;
				goalInterest[2] = false;
				return;
			} else if (goal2Name.contains("hidden")) {
				goalInterest[1] = false;

			}
			String goal3Name = boardCards[12][7].getName().toLowerCase();
			// System.out.println(goal3Name + " is Goal 3");
			if (goal3Name.contains("8")) {
				goalInterest[2] = true;

			} else if (goal3Name.contains("nugget")) {
				goalUncovered = true;
				goalInterest[2] = true;
				goalInterest[0] = false;
				goalInterest[1] = false;
				return;
			} else if (goal3Name.contains("hidden")) {
				goalInterest[2] = false;

			}
			if (MyTools.boolToInt(goalInterest[0]) + MyTools.boolToInt(goalInterest[1])
					+ MyTools.boolToInt(goalInterest[2]) == 1) {
				goalUncovered = true;
			}
		}
		return;
	}

	public void RemoveFromUsed(String cardID) {
		cardID = cardID.toLowerCase();
		int cardInt = GetCardNumber(cardID);
		if (cardInt != -1) {
			nbCardsLeft[cardInt] = Math.max(0, nbCardsLeft[cardInt] - 1);
		}
		return;
	}

	public int GetCardNumber(String cardID) {
		// System.out.println("Looking for card: " + cardID);
		cardID = cardID.toLowerCase();
		switch (cardID) {
		case "0":
			return 0;
		case "1":
			return 1;
		case "2":
			return 2;
		case "3":
			return 3;
		case "4":
			return 4;
		case "5":
			return 5;
		case "6":
			return 6;
		case "7":
			return 7;
		case "8":
			return 8;
		case "9":
			return 9;
		case "10":
			return 10;
		case "11":
			return 11;
		case "12":
			return 12;
		case "13":
			return 13;
		case "14":
			return 14;
		case "15":
			return 15;
		case "tile:0":
			return 0;
		case "tile:1":
			return 1;
		case "tile:2":
			return 2;
		case "tile:2_flip":
			return 2;
		case "tile:3":
			return 3;
		case "tile:3_flip":
			return 3;
		case "tile:4":
			return 4;
		case "tile:4_flip":
			return 4;
		case "tile:5":
			return 5;
		case "tile:5_flip":
			return 5;
		case "tile:6":
			return 6;
		case "tile:6_flip":
			return 6;
		case "tile:7":
			return 7;
		case "tile:7_flip":
			return 7;
		case "tile:8":
			return 8;
		case "tile:9":
			return 9;
		case "tile:9_flip":
			return 9;
		case "tile:10":
			return 10;
		case "tile:11":
			return 11;
		case "tile:11_flip":
			return 11;
		case "tile:12":
			return 12;
		case "tile:12_flip":
			return 12;
		case "tile:13":
			return 13;
		case "tile:14":
			return 14;
		case "tile:14_flip":
			return 14;
		case "tile:15":
			return 15;
		case "destroy":
			return 16;
		case "malus":
			return 17;
		case "bonus":
			return 18;
		case "map":
			return 19;
		}
		return -1;
	}

	/*
	 * public void RemoveFromUsed(Sring cardID){ if
	 * (cardID.split(":")[0].equals("Tile")){ if(cardID.contains("flip")){ cardID =
	 * cardID.split("_")[0]; } }
	 * 
	 * float estimatedVal = estimatedCardsLeft.get(cardID) -1; if(estimatedVal >=
	 * 0){ estimatedVal = 0; } estimatedCardsLeft.put(cardID, estimatedVal); return;
	 * }
	 */

	private void UpdateProbabilities(SaboteurBoardState boardState) {
		float sum = 0;
		int[] nbCardsLeft2 = new int[20];
		nbCardsLeft2 = nbCardsLeft.clone();
		sum = 55 - boardState.getTurnNumber();

		ArrayList<SaboteurCard> cardsInHand = boardState.getPlayerCardsForDisplay(boardState.getTurnPlayer());
		for (SaboteurCard card : cardsInHand) {
			nbCardsLeft2[GetCardNumber(card.getName())] -= 1;
		}
		if (sum <= 0) {
			sum = 1;
		}
		for (int i = 0; i < 20; i++) {
			cardApproxProbs[i] = (float) (Math.max((float) nbCardsLeft2[i] - (float) nbCardsLeft2[i] / sum, 0.2) / sum);
		}
		return;
	}

	public class ScoreMove {
		public int score = 0;
		public SaboteurMove move;
	}
}
