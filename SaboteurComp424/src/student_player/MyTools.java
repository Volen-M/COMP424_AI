package student_player;

import java.util.ArrayList;

import Saboteur.SaboteurMove;

public class MyTools {
    
	
	
	
	//Even non flips were added 
	static int[][][] cardToInt = new int[][][]{{{0,0,0},{1,1,1},{0,0,0}}, //0
                {{0,0,0},{0,0,0},{0,0,0}}, //0 flip NA
				{{0,0,0},{1,0,1},{0,0,0}}, //1 
                {{0,0,0},{0,0,0},{0,0,0}}, //1 flip NA
               {{0,0,0},{1,0,1},{0,1,0}}, //2 
               {{0,1,0},{1,0,1},{0,0,0}}, //2 flip
               {{0,0,0},{0,0,1},{0,1,0}}, //3 
               {{0,1,0},{1,0,0},{0,0,0}}, //3 flip
               {{0,0,0},{0,1,0},{0,1,0}}, //4 
               {{0,1,0},{0,1,0},{0,0,0}}, //4 flip
               {{0,0,0},{1,1,0},{0,1,0}}, //5
               {{0,1,0},{0,1,1},{0,0,0}}, //5 flip
               {{0,1,0},{1,1,1},{0,0,0}}, //6 
               {{0,0,0},{1,1,1},{0,1,0}}, //6 flip
               {{0,0,0},{0,1,1},{0,1,0}}, //7 
               {{0,1,0},{1,1,0},{0,0,0}}, //7 flip
               {{0,1,0},{1,1,1},{0,1,0}}, //8 
               {{0,0,0},{0,0,0},{0,0,0}}, //8 flip NA
               {{0,1,0},{1,1,0},{0,1,0}}, //9 
               {{0,1,0},{0,1,1},{0,1,0}}, //9 flip
               {{0,1,0},{0,1,0},{0,1,0}}, //10
               {{0,0,0},{0,0,0},{0,0,0}}, //10 flip NA
                {{0,1,0},{1,0,0},{0,1,0}}, //11
               {{0,1,0},{0,0,1},{0,1,0}}, //11 flip
               {{0,0,0},{0,1,1},{0,0,0}}, //12 
               {{0,0,0},{1,1,0},{0,0,0}}, //12 flip
               {{0,1,0},{1,0,1},{0,1,0}}, //13
               {{0,0,0},{0,0,0},{0,0,0}}, //13 flip NA
               {{0,1,0},{0,0,1},{0,0,0}}, //14
               {{0,0,0},{1,0,0},{0,1,0}}, //14 flip
               {{0,1,0},{0,0,0},{0,1,0}}, //15
				{{0,0,0},{0,0,0},{0,0,0}}}; //15 flip NA
	
	public static double getSomething() {
        return Math.random();
    }
	
	public ArrayList<String> getPossibleTiles(int tileX, int tileY, int[][] board){
		ArrayList<String> tiles= new ArrayList<String>();;
		
		int[][] aim = new int[][] {{0,0,0},{0,1,0},{0,0,0}};
		if (board[tileX*3-1][tileY*3+1] == 1){
			aim[1][2] = 1;
		}
		if (board[tileX*3+1][tileY*3-1] == 1){
			aim[0][1] = 1;
		}
		if (board[tileX*3+1][tileY*3+2] == 1){
			aim[2][1] = 1;
		}
		if (board[tileX*3+2][tileY*3+1] == 1){
			aim[1][0] = 1;
		}
		
		for(int i = 0; i<30; i ++){
			if(cardToInt[i][1][1] == aim[1][1] && cardToInt[i][1][2] == aim[1][2] && cardToInt[i][0][1] == aim[0][1] && cardToInt[i][2][1] == aim[2][1] && cardToInt[i][1][0] == aim[1][0]){
				if (i%2 == 0){
					tiles.add(Integer.toString(i/2));
				}
				else{
					tiles.add(Integer.toString(i/2) + "_flip");
				}	
			}
		}
		return tiles;
		
	}
	
	public static boolean isWinningMove(SaboteurMove move, int[][] board, boolean[] goalInterest){
		int cardNb = GetCardNumber(move.getCardPlayed().getName());
		if (cardNb >15 || cardNb == -1) {
			return false;
		}
		
		int[][] newBoard = new int[42][42];
		newBoard = playBoardSaboteur(move, board);
		
		if(goalInterest[0] && pathExist(12*3+1,3*3+1,newBoard,5*3+1, 5*3+1)) {
			return true;
		}
		if(goalInterest[1] && pathExist(12*3+1,5*3+1,newBoard,5*3+1, 5*3+1)) {
			return true;
		}
		if(goalInterest[2] && pathExist(12*3+1,7*3+1,newBoard,5*3+1, 5*3+1)) {
			return true;
		}
		return false;
	}
	
	public static float bestScoreBoard(int[][] intBoard, boolean[] goalInterest){
		int[][] board = new int[42][42];
		for (int i = 0; i<42;i++) {
			for(int j = 0; j<42;j++) {
				board[i][j] = intBoard[i][j];
			}
		}
		
		float bestScore = 10000000;
		float score;
		int[][] bestCoord = null;
		for (int x = 41; x>16; x--) {
			for(int y = 0; y<42; y++) {
				if(board[x][y] == 1 && pathExist(x,y,board,5*3+1, 5*3+1)) {
					score = boolToInt(goalInterest[0])*distanceInt(x,y,12*3+1,3*3+1) +
							boolToInt(goalInterest[1])*distanceInt(x,y,12*3+1,5*3+1) +
							boolToInt(goalInterest[2])*distanceInt(x,y,12*3+1,7*3+1);
					bestScore = bestScore < score ? bestScore : score;
				}
			}
		}
		return bestScore;
	}
	
	public static float newBestScoreFromPlay(SaboteurMove move, int[][] intBoard, boolean[] goalInterest){
		int cardNb = GetCardNumber(move.getCardPlayed().getName());
		if (cardNb >15 || cardNb == -1) {
			return 10000000;
		}
		
		int[][] newBoard = new int[42][42];
		for (int i = 0; i<42;i++) {
			for(int j = 0; j<42;j++) {
				newBoard[i][j] = intBoard[i][j];
			}
		}
		int[][] board = playBoardSaboteur(move, newBoard);

		int[] coord = new int[] {-1,-1};
		float bestScore = 10000000;
		float score;
		
		int[][] bestCoord = null;
		for (int x = 41; x>16; x--) {
			for(int y = 0; y<42; y++) {
				if(board[x][y] == 1 && pathExist(x,y,board,5*3+1, 5*3+1)) {
					score = boolToInt(goalInterest[0])*distanceInt(x,y,12*3+1,3*3+1) +
							boolToInt(goalInterest[1])*distanceInt(x,y,12*3+1,5*3+1) +
							boolToInt(goalInterest[2])*distanceInt(x,y,12*3+1,7*3+1);
					bestScore = bestScore < score ? bestScore : score;
				}
			}
		}
		return bestScore;
	}
	
	
	public static float distanceInt(int x1, int y1, int x2, int y2) {
		return (float) Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
	}
	
	@Deprecated
	public static float bestScoreFromPlay(SaboteurMove move, int[][] intBoard, boolean[] goalInterest){
		int cardNb = GetCardNumber(move.getCardPlayed().getName());
		if (cardNb >15 || cardNb == -1) {
			return 10000000;
		}
		
		int[][] newBoard = new int[42][42];
		for (int i = 0; i<42;i++) {
			for(int j = 0; j<42;j++) {
				newBoard[i][j] = intBoard[i][j];
			}
		}
		int[][] board = playBoardSaboteur(move, newBoard);

		int[] coord = new int[] {-1,-1};
		float bestScore = 10000000;
		float score;
		
		coord = new int[] {move.getPosPlayed()[0]*3+2,move.getPosPlayed()[1]*3+1};
		if (pathExist(coord[0],coord[1],board,5*3+1, 5*3+1)) {
			score = boolToInt(goalInterest[0])*distanceInt(coord[0],coord[1],12*3+1,3*3+1) +
					boolToInt(goalInterest[1])*distanceInt(coord[0],coord[1],12*3+1,5*3+1) +
					boolToInt(goalInterest[2])*distanceInt(coord[0],coord[1],12*3+1,7*3+1);
			bestScore = bestScore < score ? bestScore : score;
		}
		
		coord = new int[] {move.getPosPlayed()[0]*3+1,move.getPosPlayed()[1]*3+2};
		if (pathExist(coord[0],coord[1],board,5*3+1, 5*3+1)) {
			score = boolToInt(goalInterest[0])*distanceInt(coord[0],coord[1],12*3+1,3*3+1) +
					boolToInt(goalInterest[1])*distanceInt(coord[0],coord[1],12*3+1,5*3+1) +
					boolToInt(goalInterest[2])*distanceInt(coord[0],coord[1],12*3+1,7*3+1);
			bestScore = bestScore < score ? bestScore : score;
		}
		
		coord = new int[] {move.getPosPlayed()[0]*3+1,move.getPosPlayed()[1]*3};
		if (pathExist(coord[0],coord[1],board,5*3+1, 5*3+1)) {
			score = boolToInt(goalInterest[0])*distanceInt(coord[0],coord[1],12*3+1,3*3+1) +
					boolToInt(goalInterest[1])*distanceInt(coord[0],coord[1],12*3+1,5*3+1) +
					boolToInt(goalInterest[2])*distanceInt(coord[0],coord[1],12*3+1,7*3+1);
			bestScore = bestScore < score ? bestScore : score;
		}
		
		coord = new int[] {move.getPosPlayed()[0]*3,move.getPosPlayed()[1]*3+1};
		if (pathExist(coord[0],coord[1],board,5*3+1, 5*3+1)) {
			score = boolToInt(goalInterest[0])*distanceInt(coord[0],coord[1],12*3+1,3*3+1) +
					boolToInt(goalInterest[1])*distanceInt(coord[0],coord[1],12*3+1,5*3+1) +
					boolToInt(goalInterest[2])*distanceInt(coord[0],coord[1],12*3+1,7*3+1);
			bestScore = bestScore < score ? bestScore : score;
		}
		return bestScore;
	}
	
	public static int[][] playBoardSaboteur(SaboteurMove move, int[][] board){
		int tilex = move.getPosPlayed()[0];
		int tiley = move.getPosPlayed()[1];
		String cardName = move.getCardPlayed().getName();
		
		return playBoard(tilex,tiley,board,cardName);
	}
	
	public static int[][] playBoard(int tilex, int tiley, int[][] intBoard, String cardName){
		int[][] board = new int[42][42];
		for (int i = 0; i<42;i++) {
			for(int j = 0; j<42;j++) {
				board[i][j] = intBoard[i][j];
			}
		}

		
		//System.out.println("Play Board plays Card: " + cardName);
		int cardPlayed = GetCardNumber(cardName)*2;
		if(cardName.contains("flip")){
			cardPlayed +=1;
		}
		int[][] cardInt = cardToInt[cardPlayed];
		board[tilex*3][tiley*3] = cardInt[0][2];
		board[tilex*3+1][tiley*3] = cardInt[0][1];
		board[tilex*3+2][tiley*3] = cardInt[0][0];
		
		board[tilex*3][tiley*3+1] = cardInt[1][2];
		board[tilex*3+1][tiley*3+1] = cardInt[1][1];
		board[tilex*3+2][tiley*3+1] = cardInt[1][0];
		
		board[tilex*3][tiley*3+2] = cardInt[2][2];
		board[tilex*3+1][tiley*3+2] = cardInt[2][1];
		board[tilex*3+2][tiley*3+2] = cardInt[2][0];
		//System.out.println(printBoard(board));
		return board;
	}
	
	public static int[][] playBoardFromInt(int tilex, int tiley, int[][] intBoard, int modifiedCardID){
		int[][] board = new int[42][42];
		for (int i = 0; i<42;i++) {
			for(int j = 0; j<42;j++) {
				board[i][j] = intBoard[i][j];
			}
		}

		
		//System.out.println("Play Board plays Card: " + cardName);
		
		int[][] cardInt = cardToInt[modifiedCardID];
		board[tilex*3][tiley*3] = cardInt[0][2];
		board[tilex*3+1][tiley*3] = cardInt[0][1];
		board[tilex*3+2][tiley*3] = cardInt[0][0];
		
		board[tilex*3][tiley*3+1] = cardInt[1][2];
		board[tilex*3+1][tiley*3+1] = cardInt[1][1];
		board[tilex*3+2][tiley*3+1] = cardInt[1][0];
		
		board[tilex*3][tiley*3+2] = cardInt[2][2];
		board[tilex*3+1][tiley*3+2] = cardInt[2][1];
		board[tilex*3+2][tiley*3+2] = cardInt[2][0];
		//System.out.println(printBoard(board));
		return board;
	}
	
	public static String printBoard(int[][] intBoard) {
		StringBuilder boardString = new StringBuilder();
        for (int i = 0; i < 14*3; i++) {
            for (int j = 0; j < 14*3; j++) {
                boardString.append(intBoard[i][j]);
                boardString.append(",");
            }
            boardString.append("\n");
        }
        return boardString.toString();
	}
	
	static public int GetCardNumber(String cardID) {
		//System.out.println("Looking for card: " + cardID);
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
		case "2_flip":
			return 2;
		case "3_flip":
			return 3;
		case "4_flip":
			return 4;
		case "5_flip":
			return 5;
		case "6_flip":
			return 6;
		case "7_flip":
			return 7;
		case "9_flip":
			return 9;
		case "11_flip":
			return 11;
		case "12_flip":
			return 12;
		case "14_flip":
			return 14;
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
	
	static public boolean pathExist(int initialx,int initialy,int[][] intBoard,int finalx, int finaly){
		int[][] board = new int[42][42];
		for (int i = 0; i<42;i++) {
			for(int j = 0; j<42;j++) {
				board[i][j] = intBoard[i][j];
			}
		}
		if (board[initialx][initialy] != 1){
			return false;
		}
		if (board[finalx][finaly] != 1){
			return false;
		}
		
		if( initialx == finalx && initialy == finaly){
			return true;
		}
			
		board[initialx][initialy] = 0;
		if (initialx > 0){
			if(pathExist(initialx -1, initialy, board, finalx, finaly)){return true;}
		}
		if (initialx < 3*14){
			if(pathExist(initialx + 1, initialy, board, finalx, finaly)){return true;}
		}
		if (initialy > 0){
			if(pathExist(initialx, initialy - 1, board, finalx, finaly)){return true;}
		}
		if (initialy < 3*14){
			if(pathExist(initialx, initialy + 1, board, finalx, finaly)){return true;}
		}
		return false;
	}
	
	public static boolean isEnd(int x,int y,int[][] intBoard){
		if (intBoard[x][y] == 1 ){
			if(x%3 == 2) {
				if (intBoard[x+1][y] !=  1 && intBoard[x-1][y] == 1 && intBoard[x][y+1] != 1 && intBoard[x][y-1] != 1 ){
					return true;
				}
				return false;
			}
			if (x%3 == 0) {
				if (intBoard[x+1][y] == 1 && intBoard[x-1][y] != 1 && intBoard[x][y+1] != 1 && intBoard[x][y-1] != 1 ){
					return true;
				}
				return false;
				
			}
			if (y%3 == 2) {
				if (intBoard[x+1][y] != 1 && intBoard[x-1][y] != 1 && intBoard[x][y+1] != 1 && intBoard[x][y-1] == 1 ){
					return true;
				}
				return false;
			}
			if (y%3 == 0) {
				if (intBoard[x+1][y] != 1 && intBoard[x-1][y] != 1 && intBoard[x][y+1] == 1 && intBoard[x][y-1] != 1 ){
					return true;
				}
				return false;
			}
		}
		return false;
	}
	
	public static ArrayList<int[]> getAllEnds(int[][] board){
		ArrayList<int[]> allEnds = new ArrayList<int[]>();
		for (int x = 2; x<40; x++){
			for (int y = 2; y<40; y++){
				//System.out.println("Is End: " + Boolean.toString(isEnd(x,y,board)));
				if(isEnd(x,y,board)){
					if(pathExist(x,y,board,5*3+1, 5*3+1)){
						allEnds.add(new int[]{x,y});
					}
				}
			}
		}
		return allEnds;
	}
	
	public static ArrayList<int[]> getAllEnds(int[][] board, int startX, int endX, int startY, int endY){
		ArrayList<int[]> allEnds = new ArrayList<int[]>();
		for (int x = startX; x<=endX; x++){
			for (int y = startY; y<=endY; y++){
				if(isEnd(x,y,board)){
					if(pathExist(x,y,board,5*3+1, 5*3+1)){
						allEnds.add(new int[]{x,y});
					}
				}
			}
		}
		return allEnds;
	}
	
	public static int boolToInt(boolean bool) {
		int i = bool? 1 : 0;
		return i;
	}
	
	@Deprecated
	public static boolean moveMakesGameOneTileAway(SaboteurMove move, int [][] intBoard, boolean[] goals) {
		int[][] board = new int[42][42];
		for (int i = 0; i<42;i++) {
			for(int j = 0; j<42;j++) {
				board[i][j] = intBoard[i][j];
			}
		}
		int[][] newBoard = playBoardSaboteur(move, board);
		ArrayList<int[]> tilesEnds = getOneTileAwayEnds(newBoard, goals);
		int[] coord = move.getPosPlayed();
		for(int[] tile: tilesEnds) {
			if((int)tile[0]/3 == coord[0] && (int)tile[1]/3 == coord[1]) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean pathExistsToGoal(int[][] board,boolean[] goals) {
		if(goals[0]) {
				if(pathExist(12*3+1, 3*3+1, board, 5*3+1, 5*3+1)) {
					return true;
				}
		}
		if(goals[1]) {
				if(pathExist(12*3+1, 3*5+1, board, 5*3+1, 5*3+1)) {
					return true;
				}
		}
		if (goals[2]) {
				if(pathExist(12*3+1, 3*7+1, board, 5*3+1, 5*3+1)) {
					return true;
				}
		}
		return false;
	}
	
	public static boolean pathExistsToEntrance(int[][] board, int x, int y) {
			
		return pathExist(x, y, board, 5*3+1, 5*3+1);
	}
	
	public static boolean givesWinOpening(SaboteurMove move, int[][] intBoard, boolean[] goals) {
		int cardNb = MyTools.GetCardNumber(move.getCardPlayed().getName());
		if (cardNb >15 || cardNb == -1) {
			return false;
		}
//		int[] posPlayed = move.getPosPlayed();
//		boolean[] goalsToCheck = {false, false,false};
//		if (goals[0] == true) {
//			if (Math.abs(posPlayed[0] - 12) + Math.abs(posPlayed[1] - 3) == 2) {
//				goalsToCheck[0] = true;
//			}
//
//		}
//		if (goals[1] == true) {
//			if (Math.abs(posPlayed[0] - 12) + Math.abs(posPlayed[1] - 5) == 2) {
//				goalsToCheck[1] = true;
//
//			}
//
//		}
//		if (goals[2] == true) {
//			if (Math.abs(posPlayed[0] - 12) + Math.abs(posPlayed[1] - 7) == 2) {
//				goalsToCheck[2] = true;
//			}
//
//		}
//		
		int initialEndAmount = getOneTileAwayEnds(intBoard, goals).size();
		int[][] board = new int[42][42];
		
		for (int i = 0; i<42;i++) {
			for(int j = 0; j<42;j++) {
				board[i][j] = intBoard[i][j];
			}
		}
//		int x = 0;
//		int y = 0;
		int[][] newBoard = playBoardSaboteur(move, board);
//		for(int i = 0; i<3; i++) {
//			for(int j= 0; j<3; j++) {
//				x = i+posPlayed[0]*3;
//				y = j+posPlayed[1]*3;
//				if(newBoard[x][y] == 1 && endIsOneTileAwayFromGoal(x,y,goalsToCheck) && pathExistsToEntrance(newBoard, x,y) && isEnd(x, y, newBoard)) {
//					return true;
//				}
//			}
//		}
		if (getOneTileAwayEnds(newBoard, goals).size() > initialEndAmount) {
			return true;
		}

		return false;
	}
	
	//Get Entry point tile
	public static int[] getFollowingTile(int[][] intBoard, int[] coord ) {
		int x = 0;
		int y = 0;
		
		if (coord[0]%3 == 0) {
			x = (int)(coord[0]/3) -1;
		}
		else if (coord[0]%3 == 2){
			x = (int)(coord[0]/3) +1;
		}
		else {
			x = (int)(coord[0]/3);
		}
		if (coord[1]%3 == 0) {
			y = (int)(coord[1]/3) -1;
		}
		else if (coord[1]%3 == 2){
			y = (int)(coord[1]/3) +1;
		}
		else {
			y = (int)(coord[1]/3);
		}
		return new int[] {x,y};
	}
	
	
	public static ArrayList<int[]> getOneTileAwayEnds(int[][] intBoard, boolean[] goals) {
		ArrayList<int[]> ends = new ArrayList<int[]>();
		int x = 0;
		int y = 0;
			
		for(int i = -5; i<=4; i++) {
			for(int j= -5; j<=5; j++) {
				if (Math.abs(i) != 1 && Math.abs(j) != 1 && Math.abs(i)+ Math.abs(j) == 5) {
					if (goals[0]) {
						x = i+12*3+1;
						y = j+3*3+1;
						//System.out.println(Integer.toString(x) + " || " + Integer.toString(y));
						if(intBoard[x][y] == 1 && pathExistsToEntrance(intBoard, x,y) && isEnd(x, y, intBoard)) {
							ends.add(new int[]{x,y});
						}
					}
					if (goals[1]) {
						x = i+12*3+1;
						y = j+5*3+1;
						if(intBoard[x][y] == 1 && pathExistsToEntrance(intBoard, x,y) && isEnd(x, y, intBoard)) {
							ends.add(new int[]{x,y});
						}
					}
					if (goals[2]) {
						x = i+12*3+1;
						y = j+7*3+1;
						if(intBoard[x][y] == 1 && pathExistsToEntrance(intBoard, x,y) && isEnd(x, y, intBoard)) {
							ends.add(new int[]{x,y});
						}
					}
				}
			}
		}
		return ends;
	}
	
	
	
	public static boolean endIsOneTileAwayFromGoal(int x, int y, boolean[] goals) {
		if (goals[0] && Math.abs(x-12*3+1) != 4 && Math.abs(y-3*3+1) != 4 && Math.abs(x-12*3+1) + Math.abs(y-3*3+1) == 5) {
			return true;
		}
		if (goals[1] && Math.abs(x-12*3+1) != 4 && Math.abs(y-5*3+1) != 4 && Math.abs(x-12*3+1) + Math.abs(y-3*5+1) == 5) {
			return true;
		}
		if (goals[2] && Math.abs(x-12*3+1) != 4 && Math.abs(y-7*3+1) != 4 && Math.abs(x-12*3+1) + Math.abs(y-3*7+1) == 5) {
			return true;
		}
		return false;
	}
	
	
	public static ArrayList<Integer> GetTileListThatAllowGoalReach(int[][] intBoard, boolean[] goals, int x, int y){
		int[][] board = new int[42][42];
		for (int i = 0; i<42;i++) {
			for(int j = 0; j<42;j++) {
				board[i][j] = intBoard[i][j];
			}
		}
		
		ArrayList<Integer> winningTiles = new ArrayList<Integer>();
		int[][] newBoard = new int[42][42];
		for (int i = 0; i <32; i++) {
			newBoard = playBoardFromInt(x,y,board, i);
			if(goals[0]) {
				if ((Math.abs(x-12) == 1) ^ (Math.abs(y-3) == 1)) {
					if(pathExist(12*3+1, 3*3+1, newBoard, 5*3+1, 5*3+1)) {
						winningTiles.add(i);
						continue;
					}
				}
			}
			if(goals[1]) {
				if ((Math.abs(x-12) == 1) ^ (Math.abs(y-5) == 1)) {
					if(pathExist(12*3+1, 3*5+1, newBoard, 5*3+1, 5*3+1)) {
						winningTiles.add(i);
						continue;
					}
				}
			}
			if (goals[2]) {
				if ((Math.abs(x-12) == 1) ^ (Math.abs(y-7) == 1)) {
					if(pathExist(12*3+1, 3*7+1, newBoard, 5*3+1, 5*3+1)) {
						winningTiles.add(i);
						continue;
					}
				}
			}
		}
		return winningTiles;
		
	}
	
	public static int[] OneTileAwayEnd(int [][] board){
		int[][] expectedEnds = {{5,37},{7,35},{8,34},{10,32},{12,34},{13,35},{14,34},{16,32},{18,34},{19,35},{20,34},{22,32},{24,34},{25,35},{27,37}};		
		for (int i = 0; i<expectedEnds.length; i++){
			if(isEnd(expectedEnds[i][1],expectedEnds[i][0], board)){
				if(pathExist(expectedEnds[i][1],expectedEnds[i][0],board, 5*3+1, 5*3+1)) { return new int[]{expectedEnds[i][1],expectedEnds[i][0]} ;}
			}
		}
		return null;
	}
	
	@Deprecated
	public static ArrayList<int[]> OneTileAwayEndList(int [][] board, boolean[] goals){
		ArrayList<int[]> allEnds = new ArrayList<int[]>();
		int[][] expectedEnds;
		if (goals[0] == true){
			if (goals[1] == true){
				if (goals[2] == true){
					expectedEnds = new int[][]{{5,37},{7,35},{8,34},{10,32},{12,34},{13,35},{14,34},{16,32},{18,34},{19,35},{20,34},{22,32},{24,34},{25,35},{27,37}};		
				}
				else{
					expectedEnds = new int[][]{{5,37},{7,35},{8,34},{10,32},{12,34},{13,35},{14,34},{16,32},{18,34},{19,35}};
				}
			}
			else{
				if (goals[2] == true){
					expectedEnds = new int[][]{{5,37},{7,35},{8,34},{10,32},{12,34},{13,35},{19,35},{20,34},{22,32},{24,34},{25,35},{27,37}};
				}
				else{
					expectedEnds = new int[][]{{5,37},{7,35},{8,34},{10,32},{12,34},{13,35}};
				}
			}
		}
		else{
			if (goals[1] == true){
				if (goals[2] == true){
					expectedEnds = new int[][]{{13,35},{14,34},{16,32},{18,34},{19,35},{20,34},{22,32},{24,34},{25,35},{27,37}};		
				}
				else{
					expectedEnds = new int[][]{{19,35},{20,34},{22,32},{24,34},{25,35},{27,37}};
				}
			}
			else{
				if (goals[2] == true){
					expectedEnds = new int[][]{{13,35},{14,34},{16,32},{18,34},{19,35}};
				}
				else{
					expectedEnds = new int[][]{{}};
				}
			}
		}
		for (int i = 0; i<expectedEnds.length; i++){
			if(isEnd(expectedEnds[i][1],expectedEnds[i][0], board)){
				if(pathExist(expectedEnds[i][1],expectedEnds[i][0],board, 5*3+1, 5*3+1)) { 
					allEnds.add(new int[]{expectedEnds[i][1],expectedEnds[i][0]});
				}
			}
		}
		return allEnds;
	}
	/*
	public int[] OneTileAwayEndLeft(int [][] board){
		int[][] expectedEnds = {{5,37},{7,35},{8,34},{10,32},{12,34},{13,35}};
		for (int i = 0; i<expectedEnds.length; i++){
			if(isEnd(expectedEnds[i][1],expectedEnds[i][0]){
				if(pathExist(expectedEnds[i][1],expectedEnds[i][0],board, 5*3+1, 5*3+1)) { return new int[]{expectedEnds[i][1],expectedEnds[i][0]} ;}
			}
		}
		return null;
	}
	
	public int[] OneTileAwayEndMid(int [][] board){
		int[][] expectedEnds = {{13,35},{14,34},{16,32},{18,34},{19,35}};
		for (int i = 0; i<expectedEnds.length; i++){
			if(isEnd(expectedEnds[i][1],expectedEnds[i][0]){
				if(pathExist(expectedEnds[i][1],expectedEnds[i][0],board, 5*3+1, 5*3+1)) { return new int[]{expectedEnds[i][1],expectedEnds[i][0]} ;}
			}
		}
		return null;
	}
	public int[] OneTileAwayEndRight(int [][] board){
		int[][] expectedEnds = {{19,35},{20,34},{22,32},{24,34},{25,35},{27,37}};
		for (int i = 0; i<expectedEnds.length; i++){
			if(isEnd(expectedEnds[i][1],expectedEnds[i][0]){
				if(pathExist(expectedEnds[i][1],expectedEnds[i][0],board, 5*3+1, 5*3+1)) { return new int[]{expectedEnds[i][1],expectedEnds[i][0]} ;}
			}
		}
		return null;
	}
	*/
}