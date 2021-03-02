
import java.util.ArrayList; 
import java.util.Scanner;
import java.math.*;
import java.util.*;
public class Reversi {

	public static void main(String[] args) {
		playgame();	
	}
	static void playgame() {
		Reversi game = new Reversi();

		Scanner scnr = new Scanner(System.in);
		boolean sidevalid = false;
		int side;
		//asks the user for board size
		System.out.println("Reversi by Siddhant Choudhary\nChoose your game\n 1.Small 4x4 Reversi\n 2.Medium 6x6 Reversi\n 3.Standard 8x8 Reversi\n Your Choice?");
		do{
			int sideoption = scnr.nextInt();
			side = (sideoption + 1)*2;
			if(side == 4 | side == 6 | side == 8) {
				sidevalid = true;
			}
			else {
				System.out.println("Please try again!");
			}
			
		}while (!sidevalid);
		
		boolean compvalid;
		Agent compagent = null;

		do{
			//asks the user for agent type and initilizes the correct agent
			System.out.println(" your opponent:\n1. An agent that plays randomly\n2. An agent that uses MINIMAX\n3. An agent that uses MINIMAX with alpha-beta pruning\n4. An agent that uses H-MINIMAX with a fixed depth cutoff and a-b pruning");
			System.out.println("Your Choice? (Tip: use random agent or HMINMAX with for board size > 4x4) ");
			int compoption = scnr.nextInt();
			if(compoption == 1) {
				compagent = game.new randomAgent();
				compvalid = true;
			}
			else if(compoption == 2) {
				compagent = game.new minmaxAgent();
				compvalid = true;

			}
			else if(compoption == 3) {
				compagent = game.new minmaxpruneAgent();
				compvalid = true;

			}
			else if(compoption == 4) {
				System.out.println("What depth-cutoff would you like? (Anything above 2 is quite hard to beat)");
				int depth = scnr.nextInt();
				compagent = game.new hminmaxAgent(depth);
				compvalid = true;
			}
			else {
				System.out.println("Please try again!");
				compvalid = false;
			}
			
		}while (!compvalid);
	
		char[][] gameboard = new char[side][side];
		gameboard[side/2 - 1][side/2] = 'x';
		gameboard[side/2][side/2 -1] = 'x';
		gameboard[side/2][side/2] = 'o';
		gameboard[side/2 - 1][side/2 - 1] = 'o';
		boolean colorvalid = false;
		boolean skipturn = false;
		char usercolor = 'x';
		char compcolor = 'o';
	//  gets the player color
		System.out.println("Do you want to play DARK (x) or LIGHT (o) ?");
		do{	
			usercolor = scnr.next().charAt(0);			
			if (usercolor == 'x') {
				compcolor = 'o';
				skipturn = false;
				colorvalid = true;
			}
			else if(usercolor == 'o') {
				compcolor = 'x';
				skipturn = true;
				colorvalid = true;
			}
			else {
				System.out.println("Please try again!");
			}
			
		}while (!colorvalid);

		String command = null;
		String compmove = null;
		printboard(gameboard);
		// initialize variables to keep total times
		long totalusertime = 0;
		long totalcomptime = 0;
		do {
			if (!skipturn) {
				// execute to get input from user
				System.out.print("Next to play: ");
				if(usercolor == 'x') {
					System.out.println("Dark");
				}
				else {
					System.out.println("Light");
				}
				
				long start = System.currentTimeMillis();
				do {
					// loop until a valid move is received
					System.out.print("Your move? ");
					command = scnr.next();
					if(validmove(gameboard, usercolor, command)){
						System.out.println(command);
						long finish = System.currentTimeMillis();
						System.out.println("Elapsed time: " + (double)(finish -start)/1000);
						totalusertime += finish - start;
						System.out.println(usercolor + ": " + command);
						break;
						}
					else if (command.equals("quit")) {
						break;
					}
					else{
						// tell the user possible moves if move is invalid
						System.out.println("Invalid move!\n");
						
						System.out.print("Valid moves are: " );
						ArrayList<Integer> movelist = movefinder(gameboard, usercolor);
						for (Integer i: movelist) {
							String curmove = new StringBuilder().append((char)(97+ i % gameboard.length)).append((char)(49 + i / gameboard.length )).toString();
							System.out.print(" " + curmove);
						}
						System.out.println("\nTry again!");
					}
				
				}while(true);
			}
			if (command.equals("quit")) 
				break;
			//execute when it is computer's turn
			if (skipturn || validmove(gameboard, usercolor, command )) {
				if(!skipturn) {
					//perform the user's move
					gameboard = result(gameboard, usercolor, command);
					printboard(gameboard);

				}
				skipturn = false;
				// execute is game is over
				if(terminaltest(gameboard) ) {
					gameover(gameboard, usercolor, compcolor);
					System.out.println("Total time: ");
					if (usercolor == 'x') {
						System.out.println("Dark: " + (double)totalusertime/1000);
						System.out.println("Light: " + (double)totalcomptime/1000);
						
					}
					else if(usercolor == 'o') {
						System.out.println("Dark: " + (double)totalcomptime/1000);
						System.out.println("Light: " +(double)totalusertime/1000);
					}
					
					
					break;
				}
				// execute to make the computer perform its move. skip if the computer can't make a move
				else if(movefinder(gameboard, compcolor).size() != 0){
					System.out.print("Next to play: ");
					if(compcolor == 'x') {
						System.out.println("Dark");
					}
					else {
						System.out.println("Light");
					}
					long start = System.currentTimeMillis();
					compmove = compagent.nextmove(gameboard, compcolor);
					long finish = System.currentTimeMillis();
					System.out.println("Elapsed time: " + (double)(finish -start)/1000);
					gameboard = result(gameboard, compcolor,compmove);
					System.out.println(compcolor + ": " + compmove);
					printboard(gameboard);	
					totalcomptime += finish - start;
					//execute if game is over
					if(terminaltest(gameboard)) {
						gameover(gameboard, usercolor, compcolor);
						System.out.println("Total time: ");
						if (usercolor == 'x') {
							System.out.println("Dark: " + (double)totalusertime/1000);
							System.out.println("Light: " + (double)totalcomptime/1000);
							
						}
						else if(usercolor == 'o') {
							System.out.println("Dark: " + (double)totalcomptime/1000);
							System.out.println("Light: " +(double)totalusertime/1000);
						}
						break;
					}
					// if the user cannot play a move, skip the user's turn
					skipturn = (movefinder(gameboard, usercolor).size() == 0);					}
				}	
			else {
				System.out.println("Sorry, not a valid move");
			}
		}while(true);


	}
	
	// prints the board
	 static void printboard(char[][] board) {
		 
		int side = board.length;
		System.out.print(" ");
		for (int i = 0; i < side; i++){
			System.out.print(" " + (char) (97 + i) );	
		}
		for (int i = 0; i < side; i++) {
			System.out.println("");
			System.out.print(i+1 );	
			for(int j = 0; j < side; j++ ) {
				System.out.print(" " + board[i][j] );	

			}
			System.out.print(" " + (1 + i) );
			
		}
		System.out.println();
		System.out.print(" ");

		for (int i = 0; i < side; i++){
			System.out.print(" " + (char) (97 + i) );	
		}
		
		System.out.println();
		
	}	
	 // checks if the board has reached the terminal state
	 static boolean terminaltest(char[][]board) {
			return movefinder(board, 'o').size() == 0 && movefinder(board, 'x').size() == 0;

	 }
	 
	 // prints a message telling the user if he won, lost, or the game was a draw
	 static void gameover(char[][]board, char urcolor, char comcolor) {
		 	int numuser = piececount(board,urcolor);
			int numcomp = piececount(board,comcolor);
			if (numuser > numcomp) {
				System.out.println("game over, you won!");
			}
			else if (numuser == numcomp) {
				System.out.println("game over, its a draw!");
			}
			
			else {
				System.out.println("game over, you lost!");
			}
	 }
	 
	 // counts the number of pieces of a given color on the board
	 static int piececount(char[][]board, char color) {
			int count = 0;
			for(int i = 0; i  < board.length ; i++) {
				for(int j = 0;  j < board.length; j++) {
					if (board[i][j] == color) {
						count++;
					}
				}
			}
			return count;
		}
	 
	 // checks if a given move is valid or not
	static boolean validmove(char[][] board, char color, String move) {
		if (move.length() > 2) {
			return false;
		}
		
		int row = Character.getNumericValue(move.charAt(1)) - 1 ;
		int col = move.charAt(0) - 97;
		
		char oppcolor;
		if (color == 'x') {
			oppcolor = 'o';
		}
		else {
			oppcolor = 'x';
		}
		
		if (board[row][col] =='x' | (board[row][col] =='o')) {
			return false;
		}
		if ( row > board.length | row < 0 | col < 0 | col > board.length) {
			return false;
		}
		
		// checks north west direction
		if (row>0 & col > 0 && board[row -1][col -1] == oppcolor ) {
			int i = row - 2;
			int j = col - 2;
			while (i >= 0 && j >= 0) {
				if (board[i][j] == color) {
					return true;
				}
				else if(board[i][j] == 0) {
					break;
				}
				i--;
				j--;
			}		
		}
		// checks north  direction

		if (row>0 && board[row -1][col] == oppcolor ) {
			int i = row - 2;
			int j = col;
			while (i>=0) {
				if (board[i][j] == color) {
					return true;
				}
				else if(board[i][j] == 0) {
					break;
				}
				i--;
			}	
		}
		
		// checks north east direction

		if (row> 0 && col < board.length -1 && board[row - 1][col + 1] == oppcolor ) {
			int i = row - 2;
			int j = col + 2;
			while (i >= 0 & j < board.length) {
				
				if (board[i][j] == color) {
					return true;
				}
				else if(board[i][j] == 0) {
					break;
				}
				i--;
				j++;
			}	
		}
		// checks east direction

		if (col < board.length-1 && board[row][col + 1] == oppcolor ) {
			int i = row;
			int j = col + 2;
			while (j < board.length) {
				if (board[i][j] == color) {
					return true;
				}
				else if(board[i][j] == 0) {
					break;
				}
				j++;
			}	
		}
		
		// checks south east direction

		if (row < board.length - 1 && col < board.length - 1 && board[row+1][col + 1] == oppcolor ) {
			int i = row + 2;
			int j = col + 2;
			while (i < board.length & j < board.length) {
				if (board[i][j] == color) {
					return true;
				}
				else if(board[i][j] == 0) {
					break;
				}
				i++;
				j++;
			}	
		}
		// checks south direction

		if (row < board.length - 1 && board[row+1][col] == oppcolor ) {
			int i = row + 2;
			int j = col ;
			while (i < board.length) {
				if (board[i][j] == color) {
					return true;
				}
				else if(board[i][j] == 0) {
					break;
				}
				i++;
				
			}	
		}
		// checks south west direction

		if ( row < board.length - 1 && col > 0 && board[row+1][col-1] == oppcolor ) {
			int i = row + 2;
			int j = col - 2;
			while (i < board.length & j >= 0) {
				if (board[i][j] == color) {
					return true;
				}
				else if(board[i][j] == 0) {
					break;
				}
				i++;
				j--;
				
			}	
		}
		// checks west direction

		if(col > 0 && board[row][col-1] == oppcolor) {
			int i = row ;
			int j = col - 2;
			while (j >= 0) {
				if (board[i][j] == color) {
					return true;
				}
				else if(board[i][j] == 0) {
					break;
				}
				j--;
				
			}	
		}
		
		
		return false;
		
	}
	// produces a list of all legal moves
	static ArrayList<Integer> movefinder(char[][] board, char color) {
		ArrayList<Integer> arr = new ArrayList<Integer>();
		for (int i = 0; i < board.length; i++) {
			for(int j = 0; j < board.length; j++) {
				String s = new StringBuilder().append((char)(97+j)).append((char)(49+i)).toString();
				if (board[i][j] != 'o' && board[i][j] != 'x' && validmove(board, color, s)) {
					arr.add(i*board.length + j);
				}
			}
		}
		return arr;
			
	}
	// produces a copy of the board
	static char[][] boardcopy(char[][] board){
		char[][] newboard = new char[board.length][board.length];
		for (int i = 0; i < board.length; i++) {
			newboard[i] = board[i].clone();
		}
		return newboard;
		
	}

	// produces a board (char[][] array) which is the result of making a particular move on a given boaard
	static char[][] result(char[][] board, char color, String move){
		char[][]resultboard = boardcopy(board);
		int row = Character.getNumericValue(move.charAt(1)) - 1 ;
		int col = move.charAt(0) - 97;
		char oppcolor;
		if (color == 'x') {
			oppcolor = 'o';
		}
		else {
			oppcolor = 'x';
		}
		//checks all 8 directions and flips the relevant pieces
		if (row>0 & col > 0 && board[row -1][col -1] == oppcolor ) {
			int i = row - 2;
			int j = col - 2;
			while (i >= 0 && j >= 0) {
				if (board[i][j] == color) {
					int k = row;
					int m = col;
					for(k = row; k >= i; k-- ) {
						resultboard[k][m--] = color;
					}
					break;
				}
				else if(board[i][j] == 0) {
					break;
				}
				i--;
				j--;
			}		
		}

		if (row>0 && board[row -1][col] == oppcolor ) {
			int i = row - 2;
			int j = col;
			while (i>=0) {
				if (board[i][j] == color) {
					int k = row;
					int m = col;
					for(k = row; k >= i; k-- ) {
						resultboard[k][m] = color;
					}
					break;
				}
				else if(board[i][j] == 0) {
					break;
				}

				else if(board[i][j] == 0) {
					break;
				}
				i--;
			}	
		}
		if (row> 0 && col < board.length -1 && board[row - 1][col + 1] == oppcolor ) {
			int i = row - 2;
			int j = col + 2;
			while (i >= 0 & j < board.length) {
				
				if (board[i][j] == color) {
					int k = row;
					int m = col;
					for(k = row; k >= i; k-- ) {
						resultboard[k][m++] = color;
					}
					break;
				}
				else if(board[i][j] == 0) {
					break;
				}

				else if(board[i][j] == 0) {
					break;
				}
				i--;
				j++;
			}	
		}
		
		if (col < board.length-1 && board[row][col + 1] == oppcolor ) {
			int i = row;
			int j = col + 2;
			while (j < board.length) {
				if (board[i][j] == color) {
					int k = row;
					int m = col;
					for(m = col; m <=j; m++ ) {
						resultboard[k][m] = color;
					}
					break;
				}
				else if(board[i][j] == 0) {
					break;
				}

				else if(board[i][j] == 0) {
					break;
				}
				j++;
			}	
		}
		if (row < board.length - 1 && col < board.length - 1 && board[row+1][col + 1] == oppcolor ) {
			int i = row + 2;
			int j = col + 2;
			while (i < board.length & j < board.length) {
				if (board[i][j] == color) {
					int k = row;
					int m = col;
					for(m = col; m <= j; m++ ) {
						resultboard[k++][m] = color;
					}
					break;
				}
		
				else if(board[i][j] == 0) {
					break;
				}

				else if(board[i][j] == 0) {
					break;
				}
				i++;
				j++;
			}	
		}
		
		if (row < board.length - 1 && board[row+1][col] == oppcolor ) {
			int i = row + 2;
			int j = col ;
			while (i < board.length) {
				if (board[i][j] == color) {
					int k = row;
					int m = col;
					for(k = row; k <= i; k++ ) {
						resultboard[k][m] = color;
					}
					break;
				}
				else if(board[i][j] == 0) {
					break;
				}
				i++;
			}	
		}
		if ( row < board.length - 1 && col > 0 && board[row+1][col-1] == oppcolor ) {
			int i = row + 2;
			int j = col - 2;
			while (i < board.length & j >= 0) {
				if (board[i][j] == color) {
					int k = row;
					int m = col;
					for(k = row; k <= i; k++ ) {
						resultboard[k][m--] = color;
					}
					break;
				}
				else if(board[i][j] == 0) {
					break;
				}
				i++;
				j--;
				
			}	
		}
		if(col > 0 && board[row][col-1] == oppcolor) {
			int i = row ;
			int j = col - 2;
			while (j >= 0) {
				if (board[i][j] == color) {
					int k = row;
					int m = col;
					for(m = col; m >= j; m-- ) {
						resultboard[k][m] = color;
					}
					break;
				}
				else if(board[i][j] == 0) {
					break;
				}
				j--;
				
			}	
		}
		
		
		return resultboard;
		
	}
	// Abstract class for all agents
	interface Agent{
		String nextmove(char[][]board, char color);
	}
	
	// agent class which randomly selects a move
	class randomAgent implements Agent{
		public String nextmove(char[][] board, char color){
			ArrayList curarray = movefinder(board, color);
			int nummoves = curarray.size();
			int randommove = (int) (nummoves * Math.random());
			int moveint = (int) curarray.get(randommove);
			String movestring = new StringBuilder().append((char)(97+ moveint % board.length)).append((char)(49 + moveint / board.length )).toString();
			return movestring;
			
		}
	}
	//agent class which implements the minmax algorithm
	class minmaxAgent implements Agent{
		
		public String nextmove(char[][] board, char color){
			char oppcolor;
			if (color == 'x') {
				oppcolor = 'o';
			}
			else {
				oppcolor = 'x';
			}
			String bestmove=null;
			String curmove;
			int bestscore= -1000;
			int curscore;
			ArrayList<Integer> curarray = movefinder(board, color);
			//iterates through all the possible moves and finds the best possible move (which has the maximum (guaranteed) utility)
			for (Integer i: curarray) {
				curmove = new StringBuilder().append((char)(97+ i % board.length)).append((char)(49 + i / board.length )).toString();
				char[][] newboard = result(board, color, curmove);
				if(movefinder(newboard, oppcolor).size() == 0) {
					curscore = minmax(newboard, color, true);
				}
				else {
					curscore = minmax(newboard, color, false);
				}
			

				if (curscore > bestscore) {
					bestmove = curmove;
					bestscore = curscore;
				}
			}
		
			return bestmove;
	
		}
		// calculates the utility for a given board 
		public int utility(char[][]board, char color) {
			char oppcolor;
			int score = 0;
			if (color == 'x') {
				oppcolor = 'o';
			}
			else {
				oppcolor = 'x';
			}
			for(int i = 0; i < board.length; i++) {
				for(int j = 0; j < board.length; j++) {
					if (board[i][j] == color){
						score++;
					}
					else if(board[i][j] == oppcolor) {
						score--;
					}
				}
			}
			
			return score;
		}
		// minmax algorithm, finds the maximum (or minimum) utility for a given move
		public int minmax(char[][] board, char color, boolean maximizing) {
			char oppcolor;
			if (color == 'x') {
				oppcolor = 'o';
			}
			else {
				oppcolor = 'x';
			}
			//calculates the utility if terminal state is reached
			if (terminaltest(board)) {
				return utility(board, color);
			}
			//if we are maximizing on this move, we need to find the next move which gives the maximum utility
			else if(maximizing) {
				String bestmove= null;
				String curmove;
				int bestscore= -1000;
				int curscore;
				ArrayList<Integer> curarray = movefinder(board, color);
				// takes care of case where two consecutive moves are made my same player
				for (Integer i: curarray) {
					curmove = new StringBuilder().append((char)(97+ i % board.length)).append((char)(49 + i / board.length )).toString();
					char[][] newboard = result(board, color, curmove);
					if(movefinder(newboard, oppcolor).size() == 0) {
						curscore = minmax(newboard, color, true);
					}
					else {
						curscore = minmax(newboard, color, false);
					}
					
					if (curscore > bestscore) {
						bestscore = curscore;
					}
				}
				
				return bestscore;
			}
			//if we are minimizing on this move, we need to find the next move which gives the minimum utility

			else {
				String bestmove;
				String curmove;
				int bestscore= +1000;
				int curscore;
				ArrayList<Integer> curarray = movefinder(board, oppcolor);
				//iterates through all valid next moves and finds the one which gurantees the minimum utility
				for (Integer i: curarray) {
					curmove = new StringBuilder().append((char)(97+ i % board.length)).append((char)(49 + i / board.length )).toString();
					char[][] newboard = result(board, oppcolor, curmove);
					if(movefinder(newboard, color).size() == 0) {
						curscore = minmax(newboard, color, false);
					}
					else {
						curscore = minmax(newboard, color, true);
					}
					if (curscore < bestscore) {
						bestscore = curscore;
					}
				}
				return bestscore;
			}
					
		}
	}
	
	// identical to minmax algorithm, but we pass an alpha (or beta) value to prune unnecessary exploration 
	class minmaxpruneAgent extends minmaxAgent implements Agent{
		public String nextmove(char[][] board, char color){
			char oppcolor;
			if (color == 'x') {
				oppcolor = 'o';
			}
			else {
				oppcolor = 'x';
			}
			String bestmove=null;
			String curmove;
			int bestscore= -10;
			int curscore;
			ArrayList<Integer> curarray = movefinder(board, color);
			for (Integer i: curarray) {
				curmove = new StringBuilder().append((char)(97+ i % board.length)).append((char)(49 + i / board.length )).toString();
				char[][] newboard = result(board, color, curmove);
				if(movefinder(newboard, oppcolor).size() == 0) {
					curscore = minmax(newboard, color, true, true, bestscore);
				}
				else {
					curscore = minmax(newboard, color, false, true, bestscore);
				}
			

				if (curscore > bestscore) {
					bestmove = curmove;
					bestscore = curscore;
					}
				
			}
			

			return bestmove;
		
		}
		
		// contains extra paramenters at the end. the boolean alpha tells if the previous node was alpha or beta, and "value" passes its value
		public int minmax(char[][] board, char color, boolean maximizing, boolean alpha, int value) {
			char oppcolor;
			if (color == 'x') {
				oppcolor = 'o';
			}
			else {
				oppcolor = 'x';
			}
			if (terminaltest(board)) {
				return utility(board, color);
			}
			else if(maximizing) {
				String bestmove;
				String curmove;
				int bestscore= -10;
				int curscore;
				ArrayList<Integer> curarray = movefinder(board, color);
				for (Integer i: curarray) {
					curmove = new StringBuilder().append((char)(97+ i % board.length)).append((char)(49 + i / board.length )).toString();
					char[][] newboard = result(board, color, curmove);
					//since we are maximizing, we send the value of alpha as true
					if(movefinder(newboard, oppcolor).size() == 0) {
						curscore = minmax(newboard, color, true, true, bestscore);
					}
					else {
						curscore = minmax(newboard, color, false, true, bestscore);
					}
					
					if (curscore > bestscore) {
						bestscore = curscore;
						if(!alpha && bestscore >= value) {
							break;
						}
					}
				}
				
				return bestscore;
			}
			else {
		
				String bestmove;
				String curmove;
				int bestscore= +10;
				int curscore;
				ArrayList<Integer> curarray = movefinder(board, oppcolor);
				for (Integer i: curarray) {
					curmove = new StringBuilder().append((char)(97+ i % board.length)).append((char)(49 + i / board.length )).toString();
					char[][] newboard = result(board, oppcolor, curmove);
					if(movefinder(newboard, color).size() == 0) {
						// since we are minimizing, we send the value of alpha as false
						curscore = minmax(newboard, color, false, false, bestscore);
					}
					else {
						curscore = minmax(newboard, color, true, false, bestscore);
					}
					if (curscore < bestscore) {
						bestscore = curscore;
						if(alpha && bestscore <= value) {
							break;
						}
					}
				}
				return bestscore;
			}
		}
		
	}
	//similar to alpha beta pruning, but has a depth cutoff 
	class hminmaxAgent extends minmaxpruneAgent implements Agent{
		int depth;
		public hminmaxAgent(int d) {
			depth = d;
		}
		public String nextmove(char[][] board, char color){
			char oppcolor;
			if (color == 'x') {
				oppcolor = 'o';
			}
			else {
				oppcolor = 'x';
			}
			String bestmove=null;
			String curmove;
			int bestscore= -1000;
			int curscore;
			ArrayList<Integer> curarray = movefinder(board, color);
			for (Integer i: curarray) {
				curmove = new StringBuilder().append((char)(97+ i % board.length)).append((char)(49 + i / board.length )).toString();
				char[][] newboard = result(board, color, curmove);
				if(movefinder(newboard, oppcolor).size() == 0) {
					curscore = minmax(newboard, color, true, true, bestscore, depth);
				}
				else {
					curscore = minmax(newboard, color, false, true, bestscore, depth);
				}
			

				if (curscore > bestscore) {
					bestmove = curmove;
					bestscore = curscore;
					}
				
			}
			

			return bestmove;
		
		}
		// a depth parameter is added
		public int minmax(char[][] board, char color, boolean maximizing, boolean alpha, int value, int depth) {
			char oppcolor;
			if (color == 'x') {
				oppcolor = 'o';
			}
			else {
				oppcolor = 'x';
			}
			// stop the search and calculate the utility when depth = 
			if (terminaltest(board) || depth == 0) {
				return utility(board, color);
			}
			else if(maximizing) {
				String bestmove;
				String curmove;
				int bestscore= -1000;
				int curscore;
				ArrayList<Integer> curarray = movefinder(board, color);
				// need to take care of case where two consecutive moves are made my same player
				for (Integer i: curarray) {
					curmove = new StringBuilder().append((char)(97+ i % board.length)).append((char)(49 + i / board.length )).toString();
					char[][] newboard = result(board, color, curmove);
					//with each layer, we decrement the depth
					if(movefinder(newboard, oppcolor).size() == 0) {
						curscore = minmax(newboard, color, true, true, bestscore, depth -1);
					}
					else {
						curscore = minmax(newboard, color, false, true, bestscore, depth -1 );
					}
					
					if (curscore > bestscore) {
						bestscore = curscore;
						if(!alpha && bestscore >= value) {
							break;
						}
					}
				}
				
				return bestscore;
			}
			else {
				
				String bestmove;
				String curmove;
				int bestscore= +1000;
				int curscore;
				ArrayList<Integer> curarray = movefinder(board, oppcolor);
				for (Integer i: curarray) {
					curmove = new StringBuilder().append((char)(97+ i % board.length)).append((char)(49 + i / board.length )).toString();
					char[][] newboard = result(board, oppcolor, curmove);
					
					if(movefinder(newboard, color).size() == 0) {
						curscore = minmax(newboard, color, false, false, bestscore, depth -1 );
					}
					else {
						curscore = minmax(newboard, color, true, false, bestscore, depth - 1);
					}
					if (curscore < bestscore) {
						bestscore = curscore;
						if(alpha && bestscore <= value) {
							break;
						}
					}
				}
				return bestscore;
			}
		}
		
	}
	
}
	

	
		
		
	


