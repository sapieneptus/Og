/*
//
//
//Og.java by christopher fuentes
//
//HUID 60857326
//
//cfuentes@fas.harvard.edu
//
//
//
 * 
 * 
 * Og.java sets up a DIM by DIM board and then prompts the user
 * for their play preferences (human vs human, computer vs computer etc...). 
 * 
 * By default, it uses an Alpha Beta Pruning MiniMax search to 
 * determine the best moves for the computer players.
 * 
 * You can turn this off by uncommenting the appropriate line in the
 * class ComputerPlayer.
 * 
 * Several classes are implemented to play the game:
 * 
 * Og - the actual game play, calls on Players to return move coordinates.
 * Player - parent of HumanPlayer and ComputerPlayer
 * HumanPlayer - prompts the human user for input on each ply
 * ComputerPlayer - calculates moves using minimax search
 * BoardState - a theoretical board configuration.
 * Point - standard geometric point
 * Action - a Point and an Integer value associated with moving on that point.
 * TTable - a transposition table which stores all explored BoardStates and
 * 			all of their symmetries. Used to check if an identical node or 
 * 			node symmetrical to a node has been explored already and if so
 * 			get that node's final value.
 * 
 * Rules:
 * 
 * Each player chooses one square to fill on their ply. 
 * 
 * A square is "captured" by a player if it is surrounded
 * 	on all four cardinal sides by either edges of the board
 *  or filled squares of that same player.
 *  
 * Players can not capture squares that have already been filled
 * 	by either player.
 * 
 * If by filling that square a player has captured other squares, 
 * 	those squares will automatically be filled.
 * 
 * Additionally, the player will get another move on that ply. 
 * 		If more squares are captured they too will be filled, 
 * 		but the player will end their ply immediately thereafter.
 * 		(I.E. this is not an infinite loop). 
 * 		
 * The game ends when the board is full.
 * 
 * The player controlling the most squares at the end wins. 
 *  
 * 
 */


import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Og
{
    //board dimensions
    public static final int DIM = 4;
    public static final char NO_WINNER = '_';
    public static final char PLAYER_1 = 'X';
    public static final char PLAYER_2 = 'O';
    public static final char TIE = '3';

    //game board
    private static char[][] board;
    //= { {'O', '_', 'O', '_' }, { 'X','_','_','X'},{'_','X','_','O'},{'_','_','_','_'}};
    
    private static Player player1;
    private static Player player2;
    
    //a switch used to determine whose turn it is.
    private static boolean flag = true;
    
    public static void main(String[] args)
    {
        board = new char[DIM][DIM];
        //init board to '0'
        for (int i = 0; i < DIM; i++)
            for (int j = 0; j < DIM; j++)
            board[i][j] = NO_WINNER;
        
        board[0][0] = NO_WINNER;

        println("Welcome to Og!");
        println("Who is playing?");
        
        //determine HVH, CVC, HVC or CVH
        int choice;
        String prompt1 = "(Type 1 for Human v Human, 2 for Comp v "
            + "Comp, 3 for Comp v Human) ";
        choice = getValidChoice(1, 3, prompt1);
        
        switch (choice)
        {
            case 1:
                println("Ok, human 1 is X, human 2 is O");
                printBoard();
                playHVH();
                break;
                
            case 2:
                println("Ok, comp 1 is X, comp 2 is O");
                printBoard();
                playCVC();
                break;
                
            case 3:
                String prompt2 = "Who goes first?\n(type 1 for human, "
                +"2 for computer)";
                choice = getValidChoice(1, 2, prompt2);
                
                switch (choice)
                {
                    case 1:
                        println("Ok, human is X, comp is O");
                        printBoard();
                        playHVC();
                        break;
                    case 2:
                        println("Ok, comp is X, human is O");
                        printBoard();
                        playCVH();
                        break;
                }
          }
    }
    
    //Get a move from a player. 
    //check if new squares are captured.
    //if so, fill them, get another move.
    //check if more squares are captured.
    //if so, fill them. Player's ply ends.
    public static void play()
    {
        Point move;
        char winner;
        
        //Player 1
        do
        {
            if (flag)
            {
                println("Player 1's Turn");
                move = player1.move(board);
                board[move.x][move.y] = PLAYER_1;
                
                //if spots are captured, give player 1 free turn
                if (spotsHaveJustBeenCaptured(board, PLAYER_1))
                {
                    println("Captured a spot!");
                    fillCapturedSpots(board, PLAYER_1);
                	if ((winner = winner(board)) != NO_WINNER)
                	{
                		printBoard();
                		break;
                	}
                    printBoard();
                    println("Player 1 moves again");
                    move = player1.move(board);
                    board[move.x][move.y] = PLAYER_1;
                    
                    if (spotsHaveJustBeenCaptured(board, PLAYER_1))
                    {
                    		println("Captured more spots!");
                            fillCapturedSpots(board, PLAYER_1);
                    }
                }       
                flag = false;
            }
            
            //Player 2
            else
            {
                println("Player 2's Turn");
                move = player2.move(board);
                board[move.x][move.y] = PLAYER_2;
                flag = true;
                
                //if spots are captured, give player 1 free turn
                if (spotsHaveJustBeenCaptured(board, PLAYER_2))
                {
                    println("Captured a spot!");
                    fillCapturedSpots(board, PLAYER_2);
                    if ((winner = winner(board)) != NO_WINNER)
                	{
                		printBoard();
                		break;
                	}
                    printBoard();
                    println("Player 2 moves again");
                    move = player2.move(board);
                    board[move.x][move.y] = PLAYER_2;
                    
                    if (spotsHaveJustBeenCaptured(board, PLAYER_2))
                    {
                    		println("Captured more spots!");
                            fillCapturedSpots(board, PLAYER_2);
                    }
                } 
            }
            printBoard();
        }
        while( (winner = winner(board)) == NO_WINNER);
        
        //declare winner
        switch (winner)
        {
            case TIE:
                println("Nobody won....");
                break;
            case PLAYER_1:
                println("Player 1 wins!");
                break;
            case PLAYER_2:
                println("Player 2 wins!");
                break;
        }
    }
    
    public static void playHVH()
    {
        player1 = new HumanPlayer();
        player2 = new HumanPlayer();
        play();
    }
    
    public static void playCVC()
    {
        player1 = new ComputerPlayer(PLAYER_1, PLAYER_2);
        player2 = new ComputerPlayer(PLAYER_2, PLAYER_1);
        play();
    }
    
    public static void playHVC()
    {
        player1 = new HumanPlayer();
        player2 = new ComputerPlayer(PLAYER_2, PLAYER_1);
        play();
    }
    
    public static void playCVH()
    {
        player1 = new ComputerPlayer(PLAYER_1, PLAYER_2);
        player2 = new HumanPlayer();
        play();
    }
    
    //get a valid choice from human between the range lolim and hilim, inclusive
    public static int getValidChoice(int loLim, int hiLim, String prompt)
    {
        Scanner input = new Scanner(System.in);
        int choice;
        do 
        {
            print(prompt);
            try {
                choice = input.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Looking for more of a number....");
                input.next();
                choice = -1;
            }
        }
        while (choice < loLim || choice > hiLim);
        
        return choice;
    }
    
    //return number of spots the winner controlls
    public static int evalWinner(char[][] aBoard, char player)
    {
        int pCount = 0;
        
        for (int i = 0; i < DIM; i++)
            for (int j = 0; j < DIM; j++)
        {
            if (aBoard[i][j] == player) pCount++;
        }
        return pCount;
    }
    
    //if board is full, game is over
    public static boolean terminalTest(char[][] board)
    {
        int voidCount = 0;
        for (int i = 0; i < DIM; i++)
            for (int j = 0; j < DIM; j++)
            	if (board[i][j] == NO_WINNER) voidCount++;
        
        return (voidCount == 0);
    }
    
    //determine who won
    public static char winner(char[][] aBoard)
    {
        int p1Count = 0;
        int p2Count = 0;
        
        //make sure board is full
        for (int i = 0; i < DIM; i++)
            for (int j = 0; j < DIM; j++)
        {
            if (aBoard[i][j] == NO_WINNER) return NO_WINNER;
            if (aBoard[i][j] == PLAYER_1) p1Count++;
            if (aBoard[i][j] == PLAYER_2) p2Count++;
        }
        
        if (p1Count == p2Count) return TIE;
        return ((p1Count > p2Count) ? PLAYER_1 : PLAYER_2);
    }
        
   //     
   //Helper methods
   //
    
    //printing methods
    public static void print(String s)
    {
        System.out.print(s);
    }
    
    public static void println(String s)
    {
        System.out.println(s);
    }
    
    public static void newLine()
    {
        System.out.println();
    }
    
    public static void printBar()
    {
        print("__");
        for (int i = 0; i < DIM; i++)
            print("___");
        
        print("__");
        newLine();
    }
    
    public static void printBoard()
    {
        System.out.println("Here is the current state of the board:");
        
        printBar();
        for (int i = 0; i < DIM; i++)
        {
            print("|");
            for (int j = 0; j < DIM; j++)
            {
               print("|" + board[i][j] + "|");
            }
            print("|");
            newLine();
            printBar();
        }
        newLine();
    }
    
    public static void printBoard(char[][] b)
    {
        System.out.println("Here is the current state of the board:");
        
        printBar();
        for (int i = 0; i < DIM; i++)
        {
            print("|");
            for (int j = 0; j < DIM; j++)
            {
               print("|" + b[i][j] + "|");
            }
            print("|");
            newLine();
            printBar();
        }
        newLine();
    }
    
    //see if spots have just been captured
    public static boolean spotsHaveJustBeenCaptured(char[][] b, char player)
    {
        for (int i = 0; i < DIM; i++)
            for (int j = 0; j < DIM; j++)
            if (board[i][j] == NO_WINNER && 
                spotIsCaptured(new Point(i, j), b, player))
                    return true;
              
        return false;
    }
    
    //check if a single spot is captured
    private static boolean spotIsCaptured(Point spot, char[][] b, char player)
    {
        if (b[spot.x][spot.y] != NO_WINNER) return false;
        
        //4 conditions must be true in order to be captured
        boolean[] checks = new boolean[4];
        
        if (spot.x > 0) checks[0] = (b[spot.x - 1][spot.y] == player);
        else checks[0] = true; //it's against a wall on the left
        
        if (spot.x < DIM - 1) checks[1] = (b[spot.x + 1][spot.y] == player);
        else checks[1] = true; //it's against a wall on the right
        
        if (spot.y > 0) checks[2] = (b[spot.x][spot.y - 1] == player);
        else checks[2] = true; //it's against a wall on the top
        
        if (spot.y < DIM - 1) checks[3] = (b[spot.x][spot.y + 1] == player);
        else checks[3] = true; //it's against a wall on the right
        
        for (int i = 0; i < 4; i ++)
        if (!checks[i]) return false;
        
        return true;
    }
    
    //fill captured spots
    public static void fillCapturedSpots(char[][] b, char player)
    {
        for (int i = 0; i < DIM; i++)
            for (int j = 0; j < DIM; j++)
             if (spotIsCaptured(new Point(i, j), b, player))
                    b[i][j] = player;
    }
        
}

////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////      POINT      /////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////

//A standard geometric Point containing int x and int y
class Point
{
    public int x;
    public int y;
    
    public Point(int x, int y)
    {
        this.x = x;
        this.y = y;
    }
    
    //returns "x = #, y = #"
    public String toString()
    {
        return "x = " + this.x + ", y = " + this.y;
    }
}

////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////      PLAYER     /////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////

//ADT of a player.
//Given a board, player can return a move. 
class Player
{
    public Point move(char[][] board)
    {
        //should be overridden
        
        //default return result is invalid
        return new Point(-1, -1);
    }
}

////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////   HUMAN PLAYER  /////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////

//Human player works by asking a human for the move.
class HumanPlayer extends Player
{   
    public Point move(char[][] board)
    {
        
        int row;
        int col;
        
        do 
        {
           row = Og.getValidChoice(0, Og.DIM - 1, "Which row?"); 
           col = Og.getValidChoice(0, Og.DIM - 1, "Which column?"); 
        }
        while (board[row][col] != Og.NO_WINNER);
        
        return new Point(row, col);
    }
}

	////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////// COMPUTER PLAYER /////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////

//Computer Player uses miniMax to calculate move
class ComputerPlayer extends Player
{
	//"infinity" values (that is, unreachable utility values)
	public static final int INFINITY = Og.DIM * Og.DIM;
	public static final int NEGATIVE_INFINITY = (-1 * Og.DIM * Og.DIM);
	
    // ivars
    private char playerChar;
    private char otherPlayer;
    private int count;
    private int uniqueCount;
    private static TTable tTable = new TTable();
    
    public ComputerPlayer(char c, char o)
    {
    	this.uniqueCount = 0;
        this.count = 0;
        this.otherPlayer = o;
        this.playerChar = c;
    }
    
    public Point move(char[][] board)
    {
    	boolean alphaBeta = true;
    	
    	/* ********************************************************** *
    	 * Uncomment the following line to turn OFF AlphaBeta Pruning *
    	 * ********************************************************** */
    	
    	//alphaBeta = false;
    	
        this.count = 0;
        this.uniqueCount = 0;
        Point p;
        if (alphaBeta)
        	p = abMiniMaxDecision(board);
        else 
        	p = miniMaxDecision(board);
        
        Og.println("Checked " + count + " states");
        Og.println("Checked " + uniqueCount + " unique states");
        return p;
    }
    
    
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////// NORMAL MINIMAX DECISION ////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private Point miniMaxDecision(char[][] board)
    {
        BoardState b;
        List<Action> actions = getActions(new BoardState(board), this.playerChar);
        for (Action a : actions)
        {
            b = new BoardState(board, a.p, this.playerChar);
            BoardState match;
            if ((match = tTable.containsBoard(b)) != null)
            {  
            	a.v = tTable.valForBoardState(match); 
            	count++; 
            	continue; 
            }
            else 
            {
                a.v = minValue(b);
                tTable.addBoard(b, a.v);
                count++;
                uniqueCount++;
                //if (a.v > WIN) return a.p;
            }
        }
        return getMax(actions).p;
    }
    
    public int minValue(BoardState b)
    {
    	//Og.println("in Min");
        if (Og.terminalTest(b.board))
            return utility(b.board);
        int val = Og.DIM * Og.DIM;
        for (Action a : getActions(b, this.otherPlayer))
        {
            BoardState next = new BoardState(b.board, a.p, this.otherPlayer);
            BoardState match;
            if ((match = tTable.containsBoard(next)) != null)
            {
            	count++;
                return tTable.valForBoardState(match);
            }
            a.v = val = Math.min(val, maxValue(next));
            tTable.addBoard(next, a.v);
            count++;
            uniqueCount++;
        }
       return val;
    }
    
    public int maxValue(BoardState b)
    {
    	//Og.println("in Max");
        if (Og.terminalTest(b.board))
            return utility(b.board);
        int val = -(Og.DIM * Og.DIM);
        for (Action a : getActions(b, this.playerChar))
        {
            BoardState next = new BoardState(b.board, a.p, this.playerChar);
            BoardState match;
            if ((match = tTable.containsBoard(next)) != null)
            {	
            	count++;
                return tTable.valForBoardState(match);
            }
            
            a.v = val = Math.max(val, minValue(next));
            tTable.addBoard(next, a.v);
            count++;
            uniqueCount++;
        }
        return val;
    }
    
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////// END NORMAL MINIMAX ////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    
    
    
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////// Alpha Beta MINIMAX DECISION /////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private Point abMiniMaxDecision(char[][] board)
    {
        BoardState b;
        int alpha = NEGATIVE_INFINITY;
        int beta =  INFINITY;
        List<Action> actions = getActions(new BoardState(board), this.playerChar);
        for (Action a : actions)
        {
        	count++;
            b = new BoardState(board, a.p, this.playerChar);
            BoardState match;
            if ((match = tTable.containsBoard(b)) != null)
            {  
            	a.v = tTable.valForBoardState(match); 
            	continue; 
            }
            else 
            {
                uniqueCount++;
                a.v = minValue(b, alpha, beta);
                tTable.addBoard(b, a.v);
            }
        }
        return getMax(actions).p;
    }
    
    public int minValue(BoardState b, int alpha, int beta)
    {
        if (Og.terminalTest(b.board))
            return utility(b.board);
        int val = INFINITY;
        for (Action a : getActions(b, this.otherPlayer))
        {
        	count++;
            BoardState next = new BoardState(b.board, a.p, this.otherPlayer);
            BoardState match;
            if ((match = tTable.containsBoard(next)) != null)
            {
                return tTable.valForBoardState(match);
            }
            uniqueCount++;
            a.v = val = Math.min(val, maxValue(next, 0, 0));
            tTable.addBoard(next, a.v);
            if (val <= alpha)
            	return val;
            beta = Math.min(beta, val);
        }
       return val;
    }
    
    public int maxValue(BoardState b, int alpha, int beta)
    {
        if (Og.terminalTest(b.board))
            return utility(b.board);
        int val = NEGATIVE_INFINITY;
        for (Action a : getActions(b, this.playerChar))
        {
        	count++;
            BoardState next = new BoardState(b.board, a.p, this.playerChar);
            BoardState match;
            if ((match = tTable.containsBoard(next)) != null)
            {	
                return tTable.valForBoardState(match);
            }
            uniqueCount++;
            a.v = val = Math.max(val, minValue(next, 0, 0));
            tTable.addBoard(next, a.v);

            if (val >= beta)
            	return val;
            alpha = Math.max(alpha, val);
        }
        return val;
    }
    
    
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////// END Alpha Beta MINIMAX///////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    
    //return how many of your piece is on the
    //final (full) board
    private int utility(char[][] board)
    {
    	//Og.println("in Utility");
        int boardLength = board.length;
        int count = 0;
        for (int i = 0; i < boardLength; i++)
            for (int j = 0; j < boardLength; j++)
            {
            if (board[i][j] == this.playerChar) 
            count++;
            }
        
        return count;
    }
    
    private List<Action> getActions(BoardState s, char player)
    {
    	//Og.println("in GetActions");
        List<Action> a = new CopyOnWriteArrayList<Action>();
        int boardLength = s.board.length;
        Point p1 = null;
        for (int i = 0; i < boardLength; i++)
        {
        	for (int j = 0; j < boardLength; j++)
        	{
        		if (s.board[i][j] != Og.NO_WINNER) continue;
        		Point p2;
        		boolean sym = false;
        		if (a.size() == 0) 
        			a.add(new Action(new Point(i, j)));
        		for (Action act : a)
        		{ 
        			p2 = act.p;
        			if (TTable.areSymmetries(s.board, player, 
            				(p1 = new Point(i,j)), p2))
        			{
        				sym = true;
        				break;
        			}
        		}
        		if (!sym)
        			a.add(new Action(p1));
        	}
        }
        return a;
    }
    
    public Action getMin(List<Action> list)
    {
    	//collect garbage
    	System.gc();
        Action act = null;
        int lowest = (Og.DIM * Og.DIM);
        for (Action a : list)
        {
            if (a.v < lowest) 
            {
                lowest = a.v;
                act = a;
            }
        }
        return act;
    }
    
    public Action getMax(List<Action> list)
    {
    	//collect garbage
    	System.gc();
        Action act = null;
        int highest = -(Og.DIM * Og.DIM);
        for (Action a : list)
        {
            if (a.v > highest) 
            {
                highest = a.v;
                act = a;
            }
        }
        return act;
    }
}


////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////   BOARD STATE   /////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////


//A board state takes either a board with no action, 
//or a board with a given move played.
//It then checks to see if that move would allow
//any more spaces to become captured and if so
//processes subsequent capturing. 
//The resulting board config is stored as a char[][]
class BoardState
{
    public char[][] board;
    
    public BoardState(char[][] b, Point p, char player)
    {
        int l = b.length;
        this.board = new char[l][l];
        for (int i = 0; i < l; i++)
            for (int j = 0; j < l; j++)
            this.board[i][j] = b[i][j];
            
        this.board[p.x][p.y] = player;
        
        if (Og.spotsHaveJustBeenCaptured(this.board, player))
        {
            Og.fillCapturedSpots(this.board, player); 
            
            //player gets 1 free move
            boolean shouldBreak = false; 
            for (int i = 0; i < l; i++)
            {
                if (shouldBreak) break;
                for (int j = 0; j < l; j++)
                    if (this.board[i][j] == Og.NO_WINNER)
                {
                    this.board[i][j] = player;
                    shouldBreak = true;
                    break;
                }
            }
        }
        
        //fill any new captured spots, if any
        if (Og.spotsHaveJustBeenCaptured(this.board, player))
            Og.fillCapturedSpots(this.board, player); 
        
    }
    
    public String toString()
    {
    	String s = "";
    	int l = this.board.length;
    	for (int i = 0; i < l; i++)
            for (int j = 0; j < l; j++)
            	s += this.board[i][j];
    	
    	return s;
    	
    }
    
    public BoardState(char[][] b)
    {
        int l = b.length;
        this.board = new char[l][l];
        for (int i = 0; i < l; i++)
            for (int j = 0; j < l; j++)
            this.board[i][j] = b[i][j];
    }
}

////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////     T TABLE     /////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////

//Helper class containing a list of board state (char[][])
//and values associated with them stored as type Integer
class TTable
{
    public List<BoardState> states;
    public List<Integer> utility;
    public List<String> stateStrings;
    
    public TTable()
    {
        states = new CopyOnWriteArrayList<BoardState>();
        utility = new CopyOnWriteArrayList<Integer>();
        stateStrings = new CopyOnWriteArrayList<String>();
    }
    
    //check a point to see if adding it produces a symmetry
    public static boolean areSymmetries(char[][] b, char player, Point p1, Point p2)
    {
    	String s = (new BoardState(b, p1, player).toString());
    	for (String sym : getSymmetries(new BoardState(b, p2, player)))
    		if (sym.equals(s)) return true;
    	
    	return false;
    }
    
    //checks all current board states and returns
    //any that have identical board configurations
    public BoardState containsBoard(BoardState b)
    {
    	String s = b.toString();
    	//Og.println("in Max");
        for (String state : this.stateStrings)
        {
        	if (s.equals(state))
        		return this.states.get(this.stateStrings.indexOf(state));

        }
        return null;
    }
    
    //get all symmetries associated with a board state
    public static List<String> getSymmetries(BoardState b)
    {
    	List<String> symmetries = new CopyOnWriteArrayList<String>();
    	BoardState b2;
    	b2 = sym(b.board);
    	symmetries.add(b2.toString());
        
        b2 = rotate(b.board);
        symmetries.add(b2.toString());
        
        b2 = trans(b.board);
        symmetries.add(b2.toString());
        
        b2 = symTrans(b.board);
        symmetries.add(b2.toString());
        
        b2 = rotTrans(b.board);
        symmetries.add(b2.toString());
        
        b2 = rotSym(b.board);
        symmetries.add(b2.toString());
        
        return symmetries;
    }
    //Add a board, associate a value with it,
    //and generate all symmetries. 
    public void addBoard(BoardState b, int val)
    {
    	BoardState b2;
        this.states.add(b);
        this.stateStrings.add(b.toString());
        
        b2 = sym(b.board);
        this.states.add(b2);
        this.stateStrings.add(b2.toString());
        
        b2 = rotate(b.board);
        this.states.add(b2);
        this.stateStrings.add(b2.toString());
        
        b2 = trans(b.board);
        this.states.add(b2);
        this.stateStrings.add(b2.toString());
        
        b2 = symTrans(b.board);
        this.states.add(b2);
        this.stateStrings.add(b2.toString());
        
        b2 = rotTrans(b.board);
        this.states.add(b2);
        this.stateStrings.add(b2.toString());
        
        b2 = rotSym(b.board);
        this.states.add(b2);
        this.stateStrings.add(b2.toString());
        
        for (int i = 0; i < 7; i++)
            this.utility.add(val);
    }
    
    //flips each element across the line y = x
    public static BoardState sym(char[][] a)
    {
        int l = a.length;
        char[][] b = new char[l][l];
        
        for (int i = 0; i < l; i++)
            for (int j = 0; j < l; j++)
            b[j][i] = a[(l - 1) - j][(l - 1) - i];
        
        return new BoardState(b);
    }
    
    //flips each element across the line y = -x
    public static BoardState trans(char[][] a)
    {
        int l = a.length;
        char[][] b = new char[l][l];
        
        for (int i = 0; i < l; i++)
            for (int j = 0; j < l; j++)
            b[j][i] = a[i][j];
        
        return new BoardState(b);
    }
    
    //rotates board 90 degrees
    public static BoardState rotate(char[][] a)
    {

    int l = a.length;
    char[][] b = new char[l][l];
    for (int i = 0; i < l; i++) 
        for (int j = 0; j < l; j++) 
            b[j][l - 1 - i] = a[i][j];
        
        return new BoardState(b);
    }
    
    //flip over y = x and then flip over y = -x
    public static BoardState symTrans(char[][] a)
    {
        return sym(trans(a).board);
    }
    
    //rotate 90 degrees then flip over y = x
    public static BoardState rotSym(char[][] a)
    {
        return rotate(sym(a).board);
    }
    
    //rotate 90 degrees then flip over y = -x
    public static BoardState rotTrans(char[][] a)
    {
        return rotate(trans(a).board);
    }
    
    //given a board, return the state associated with it. 
    public int valForBoardState(BoardState b)
    {
        return this.utility.get(states.indexOf(b));
    }
}

////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////      ACTION     /////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////

//Helper class containing a Point and an int value.
class Action
{
    public Point p;
    public Integer v;
    
    public Action(Point p)
    {
        this.p = p;
    }
}