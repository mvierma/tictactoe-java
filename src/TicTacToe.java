import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * @author Mikko Vierma <mikko.vierma@cs.tamk.fi>
 * @version 2018.1012
 * @since 2018.1611
 */
class TicTacToe {

    static int playArea = 0;    //size of the game board
    static int playerCount;     //player count for game mode selection
    static String[][] gameBoard;    //2D-array that holds the game board and tics
    static boolean isOver = false;  //boolean for checking game status
    static int turn = 0;            //0 for X's turn - 1 for O's turn
    static int winCondition = 0;    //how many in a row is needed for win
    static int buffer = 0;          //buffer used in checking methods
    static boolean boardFull = false;   //boolean for checking tie
    static int[] recentMove = new int[2];   //array for saving last move coordinates
    static String[] turnArray = {"[X]", "[O]"}; //helper array for checking contents of a cell
    public static void main(String[] args) {

        // Game start: play area and number of players are asked and board initialized
        System.out.println("Welcome to TicTacToe!");
        getArea();
        setWinCondition();
        getPlayers(); 
        setBoardSize();
        initializeBoard();
        printBoard();

        // Main game loop
        while(!isOver) {
            placeMarker();
            printBoard();
            checkHorizontalIsOver();
            checkVerticalIsOver();
            checkDiagonalIsOver();
            checkWin();
            checkFull(); 
            changeTurn();
        }
    }

    /**
     * Get the size of the game board from user.
     * 
     * Takes integer from user input and checks the value for positive numbers.
     */
    private static void getArea() {
        boolean isValid = true;
        do {
            do {
                try {
                    Scanner scan = new Scanner(System.in);
                    System.out.println("Please enter the size of the play area: (min: 3, max: 99)");
                    playArea = scan.nextInt();
                    isValid = false;
                } catch (InputMismatchException | NumberFormatException ex) {
                    System.out.println("Invalid input, try again.");
                } catch (Exception e) {
                    System.out.println("Invalid input, try again.");
                }
            } while (isValid);
        } while (playArea < 3 || playArea > 99);
        
    }

    /**
     * Get the number of players.
     * 
     * Method for deciding whether user plays against CPU, other human or lets CPU play against itself.
     * ONly allows integers from range 0-2.
     */
    private static void getPlayers() {
        boolean isValid = true;
        do {
            do {
                try {
                    Scanner scan = new Scanner(System.in);
                    System.out.println("How many players? 1 for playing against CPU, 2 for Versus mode");
                    playerCount = scan.nextInt();
                    isValid = false;
                } catch (Exception e) {
                    System.out.println("Invalid input, try again.");
                }
            } while (isValid);
            
            
        } while (playerCount < 1 || playerCount > 2);
        
    }

    /**
     * Sets the size of the board.
     * 
     * Initializes new String array with the size of playArea * playArea.
     */
    private static void setBoardSize() {
        gameBoard = new String[playArea][playArea];
    }

    /**
     * Fills out the board.
     * 
     * Inserts empty brackets into each element of the gameBoard array.
     */
    private static void initializeBoard() {
        for (int i = 0; i < gameBoard.length; i++) {
            for (int j = 0; j < gameBoard[i].length; j++) {
                gameBoard[i][j] = "[ ]";
            }
        }
    }

    /**
     * Prints out the board.
     * 
     * Iterates of the gameBoard array printing out every element.
     */
    private static void printBoard() {
        if (playArea < 10) {
            System.out.print("  ");
            for (int i = 0; i < playArea; i++) {
                System.out.print(" " + i + " ");
            }
            System.out.println();
            for (int i = 0; i < gameBoard.length; i++) {
                System.out.print(i + " ");
                for (int j = 0; j < gameBoard[i].length; j++) {
                    System.out.print(gameBoard[i][j]);
                }
                System.out.println();
            }
        } else if (playArea >= 10) {
            System.out.print("   ");
            for (int i = 0; i < playArea; i++) {
                if (i < 10) {
                    System.out.print(" " + i + " ");
                } else {
                    System.out.print(i + " ");
                }
            }
            System.out.println();
            for (int i = 0; i < gameBoard.length; i++) {
                if (i < 10) {
                    System.out.print(i + "  ");
                } else {
                    System.out.print(i + " ");
                }
                for (int j = 0; j < gameBoard[i].length; j++) {
                    System.out.print(gameBoard[i][j]);
                }
                System.out.println();
            }
        }
    }

    /**
     * Places X and O on the board and updates the board.
     * 
     * Takes in turn information and asks user input for placement of the marker.
     * Uses isMoveLegal for validating the user input and then updates the gameBoard
     * array accordingly.
     */
    private static void placeMarker() {
        Scanner scan = new Scanner(System.in);
        
        if (turn == 0) {
            System.out.println("X's turn. Select empty square with coordinates - x,y");
            String buffer = scan.nextLine();
            while (!isMoveLegal(buffer)) {
                System.out.println("Invalid input, try again and make sure to select an empty square. Example: 0,0");
                buffer = scan.nextLine();
            }
            
            int x = Integer.parseInt(buffer.substring(0,buffer.indexOf(",")));
            int y = Integer.parseInt(buffer.substring(buffer.indexOf(",")+1));
            recentMove[0] = x;
            recentMove[1] = y;

            String[][] tempArray = gameBoard;
            tempArray[x][y] = turnArray[turn];
            gameBoard = tempArray;

        } else if (turn == 1 && playerCount == 2) {
            System.out.println("O's turn. Select empty square with coordinates - x,y");
            String buffer = scan.nextLine();
            while (!isMoveLegal(buffer)) {
                System.out.println("Invalid input, try again and make sure to select an empty square. Example: 0,0");
                buffer = scan.nextLine();
            }
            
            int x = Integer.parseInt(buffer.substring(0,buffer.indexOf(",")));
            int y = Integer.parseInt(buffer.substring(buffer.indexOf(",")+1));
            recentMove[0] = x;
            recentMove[1] = y;

            String[][] tempArray = gameBoard;
            tempArray[x][y] = turnArray[turn];
            gameBoard = tempArray;
        } else if (turn == 1 && playerCount == 1) {
            String buffer = getCpuMove();
            int x = Integer.parseInt(buffer.substring(0,buffer.indexOf(",")));
            int y = Integer.parseInt(buffer.substring(buffer.indexOf(",")+1));
            recentMove[0] = x;
            recentMove[1] = y;

            String[][] tempArray = gameBoard;
            tempArray[x][y] = turnArray[turn];
            gameBoard = tempArray;
        }
    }

    /**
     * Randomizes CPU move
     * 
     * Builds a string containing the CPU move using
     * Math.random for x and y coordinates. Checks move
     * validity using isMoveLegal method until valid move is
     * built. Returns combined moved as a string.
     * @return String containing CPU move "x,y"
     */
    private static String getCpuMove() {
        String a;
        do {
            int x = (int)(Math.random() * playArea);
            int y = (int)(Math.random() * playArea);
            a = x + "," + y;
        } while (!isMoveLegal(a));
        return a;
    }

    /**
     * Checks validity of a player move
     * 
     * Checks user input for correct length and that the selected location in
     * the array is empty. Returns true when move is valid
     * @param a user input String
     * @return true when move is legal, false otherwise
     */
    private static boolean isMoveLegal(String a) {
        if (a.length() < 3 || a.length() > 5) {
            return false;
        } else if (a.charAt(1) != ',' && a.charAt(2) != ',') {
            return false;
        } 
        try {
             if (Integer.parseInt(a.substring(0,a.indexOf(","))) < 0 || 
                    Integer.parseInt(a.substring(0,a.indexOf(","))) > playArea -1 ||
                    Integer.parseInt(a.substring(a.indexOf(",")+1)) < 0 ||
                    Integer.parseInt(a.substring(a.indexOf(",")+1)) > playArea -1) {
                return false;
            }
        } catch (InputMismatchException | NumberFormatException ex) {
            return false;
        } catch (Exception e) {
            return false;
        }
        if (!gameBoard[Integer.parseInt(a.substring(0,a.indexOf(",")))][Integer.parseInt(a.substring(a.indexOf(",")+1))].equals("[ ]")) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Updates player turn.
     * 
     */
    private static void changeTurn() {
        if (turn == 0) {
            turn = 1;
        } else {
            turn = 0;
        }
    }

    /**
     * Defines how many in a row is required for game to end.
     * 
     * For a 3x3 or 4x4 board winCondition is set to 3, so in 3 in a row ends
     * the game. For boards equal or larger than 5x5 condition is set to 5.
     */
    private static void setWinCondition() {
        if (playArea < 5) {
            winCondition = 3;
        } else if (playArea >= 5) {
            winCondition = 5;
        }
    }

    /**
     * Checks if winCondition is met horizontally.
     * 
     * Iterates over the gameBoard array starting from the recent player move
     * location, incrementing a buffer for each matching symbol in adjacent cells.
     * Checks if buffer count matches win condition and sets the isOver variable to
     * true if buffer reaches the winCondition.
     */
    private static void checkHorizontalIsOver() {
        buffer = 0;
        for (int i = 0; i < winCondition; i++) {
            if (recentMove[1] + i + 1 < playArea) {
                if (gameBoard[recentMove[0]][recentMove[1] + i].equals(turnArray[turn]) && gameBoard[recentMove[0]][recentMove[1] + i + 1].equals(turnArray[turn])) {
                    buffer++;
                }
            }
        }

        for (int i = 0; i < winCondition; i++) {
            if (recentMove[1] - i - 1 >= 0) {
                if (gameBoard[recentMove[0]][recentMove[1] - i].equals(turnArray[turn]) && gameBoard[recentMove[0]][recentMove[1] - i - 1].equals(turnArray[turn])) {
                    buffer++;
                }
            }
        }
        if (buffer == winCondition-1) {
            isOver = true;
        }
    }

    /**
     * Checks if winCondition is met vertically.
     * 
     * Iterates over the gameBoard array starting from the recent player move
     * location, incrementing a buffer for each matching symbol in adjacent cells.
     * Checks if buffer count matches win condition and sets the isOver variable to
     * true if buffer reaches the winCondition.
     */
    private static void checkVerticalIsOver() {
        buffer = 0;
        for (int i = 0; i < winCondition; i++) {
            if (recentMove[0] + i + 1 < playArea) {
                if (gameBoard[recentMove[0] + i][recentMove[1]].equals(turnArray[turn]) && gameBoard[recentMove[0] + i + 1][recentMove[1]].equals(turnArray[turn])) {
                    buffer++;
                }
            }
        }

        for (int i = 0; i < winCondition; i++) {
            if (recentMove[0] - i - 1 >= 0) {
                if (gameBoard[recentMove[0] - i][recentMove[1]].equals(turnArray[turn]) && gameBoard[recentMove[0] - i - 1][recentMove[1]].equals(turnArray[turn])) {
                    buffer++;
                }
            }
        }
        if (buffer == winCondition-1) {
            isOver = true;
        }
    }
    /**
     * Checks if winCondition is met diagonally.
     * 
     * Iterates over the gameBoard array starting from the recent player move
     * location, incrementing a buffer for each matching symbol in adjacent cells.
     * Checks if buffer count matches win condition and sets the isOver variable to
     * true if buffer reaches the winCondition.
     */
    private static void checkDiagonalIsOver() {
        buffer = 0;
        for (int i = 0; i < winCondition; i++) {
            if (recentMove[0] + i + 1 < playArea && recentMove[1] + i + 1 < playArea) {
                if (gameBoard[recentMove[0] + i][recentMove[1] + i].equals(turnArray[turn]) && gameBoard[recentMove[0] + i + 1][recentMove[1] + i + 1].equals(turnArray[turn])) {
                    buffer++;
                }
            }
        }

        for (int i = 0; i < winCondition; i++) {
            if (recentMove[0] - i - 1 >= 0 && recentMove[1] - i - 1 >= 0) {
                if (gameBoard[recentMove[0] - i][recentMove[1] - i].equals(turnArray[turn]) && gameBoard[recentMove[0] - i - 1][recentMove[1] - i - 1].equals(turnArray[turn])) {
                    buffer++;
                }
            }
        }
        if (buffer == winCondition - 1) {
            isOver = true;
        }

        buffer = 0;

        for (int i = 0; i < winCondition; i++) {
            if (recentMove[0] + i + 1 < playArea && recentMove[1] - i - 1 >= 0) {
                if (gameBoard[recentMove[0] + i][recentMove[1] - i].equals(turnArray[turn]) && gameBoard[recentMove[0] + i + 1][recentMove[1] - i - 1].equals(turnArray[turn])) {
                    buffer++;
                }
            }
        }

        for (int i = 0; i < winCondition; i++) {
            if (recentMove[0] - i - 1 >= 0 && recentMove[1] + i + 1 < playArea) {
                if (gameBoard[recentMove[0] - i][recentMove[1] + i].equals(turnArray[turn]) && gameBoard[recentMove[0] - i - 1][recentMove[1] + i + 1].equals(turnArray[turn])) {
                    buffer++;
                }
            }
        }
        
        if (buffer == winCondition - 1) {
            isOver = true;
        }
    }

    /**
     * Checks which player won if game is over.
     * 
     * Checks isOver variable and if it's true and then checks
     * current turn. Prints out the winner based on turn.
     */
    private static void checkWin() {
        if (isOver == true && boardFull == false) {
            if (turn == 0) {
                System.out.println("X wins!");
            } else if (turn == 1) {
                System.out.println("O wins!");
            }
        } else if (isOver == true && boardFull == true) {
            System.out.println("Game Over!");
        }
    }

    /**
     * Checks board for a situation with no winner and no more empty cells
     * 
     * Iterates over the gameBoard array checking for empty cells. Buffer is
     * incremented for each empty cell. If buffer is 0 isOver is set to true
     * and boardFull is set to true.
     */
    private static void checkFull() {
        int buffer = 0;
        for (int i = 0; i < gameBoard.length; i++) {
            for (int j = 0; j < gameBoard[i].length; j++) {
                if (gameBoard[i][j].equals("[ ]")) {
                    buffer++;
                }
            }
        }
        if (buffer == 0) {
            isOver = true;
            boardFull = true;
        }
    }
}