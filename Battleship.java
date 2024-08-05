import java.util.Scanner;

public class Battleship extends ConsoleProgram {
    private static final int MAX_COL = 10;
    private static final char MAX_ROW = 'J';

    private static final int HORIZONTAL = 0;
    private static final int VERTICAL = 1;
    
    private static final Scanner scanner = new Scanner(System.in);

    public void run() {
        System.out.println("------------------ WELCOME TO ------------------");
        System.out.println(" ____        _   _   _           _     _       ");
        System.out.println("|  _ \\      | | | | | |         | |   (_)      ");
        System.out.println("| |_) | __ _| |_| |_| | ___  ___| |__  _ _ __  ");
        System.out.println("|  _ < / _` | __| __| |/ _ \\/ __| '_ \\| | '_ \\ ");
        System.out.println("| |_) | (_| | |_| |_| |  __/\\__ \\ | | | | |_) |");
        System.out.println("|____/ \\__,_|\\__|\\__|_|\\___||___/_| |_|_| ,__/ ");
        System.out.println("                                        |_|    ");
        System.out.println("------------------ START HERE ------------------");
        System.out.println();

        Player human = new Player();
        Player computer = new Player();

        setUpShips(human, computer);

        // Only prompt once to start guessing
        readLine("\nHit enter to start guessing.");

        boolean gameOver = false;

        while (!gameOver) {
            gameOver = playRound(human, computer);
        }
        System.out.println();
        if (human.hasWon()) {
            System.out.println("YOU WON! CONGRATS");
        } else {
            System.out.println("YOU LOST! TRY AGAIN");
        }
        System.out.println();
        System.out.println("THANKS FOR PLAYING!");
    }

    private boolean playRound(Player human, Player computer) {
        // Human's turn
        humanTurn(human, computer);

        // Check if human has won after their turn
        if (human.hasWon()) {
            return true;
        }

        // Computer's turn
        computerTurn(human, computer);

        // Check if computer has won after their turn
        return computer.hasWon();
    }

    private void humanTurn(Player human, Player computer) {
        clearScreen();
        System.out.println("  ---- ENEMY TERRITORY");
        human.printMyGuesses();
        System.out.println();
        System.out.println("  --------- YOUR FLEET");
        printOpponentHiddenShips(computer, human);
        System.out.println();
        System.out.println("It's your turn to guess.");
        int row = readRow();
        int col = readCol();

        boolean hit = human.makeGuess(row, col, computer);

        if (hit) {
            System.out.println("\nYou got a hit!\n");
        } else {
            System.out.println("\nNope, that was a miss.\n");
        }

        System.out.println("Your guess:");
        human.printMyGuesses();
        System.out.println();
        human.printHitsDelivered();
    }

    private void computerTurn(Player human, Player computer) {
        int row = computer.getRandomRowGuess();
        int col = computer.getRandomColGuess();
    
        boolean hit = computer.makeGuess(row, col, human);
    
        if (hit) {
            System.out.println("\nComputer got a hit!");
            System.out.println();
        } else {
            System.out.println("\nComputer missed.");
            System.out.println();
        }
        readLine("\nPress enter to continue.");
    }

    private void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private void printOpponentHiddenShips(Player opponent, Player human) {
        Grid opponentGrid = opponent.getOpponentGrid();
        Grid humanGuessGrid = human.getOpponentGrid();
        Grid humanShipGrid = human.getMyShipsGrid();
    
        System.out.println("  1 2 3 4 5 6 7 8 9 10");
        for (int row = 0; row < Grid.NUM_ROWS; row++) {
            System.out.print((char) ('A' + row) + " ");
            for (int col = 0; col < Grid.NUM_COLS; col++) {
                if (opponentGrid.alreadyGuessed(row, col)) {
                    if (opponentGrid.get(row, col).checkHit()) {
                        System.out.print("🟥 "); // Red box for hit
                    } else {
                        System.out.print("⬜ "); // White box for miss
                    }
                } else {
                    if (humanGuessGrid.hasShip(row, col)) {
                        System.out.print("⬛ "); // Black box for guessed ship
                    } else if (humanShipGrid.hasShip(row, col)) {
                        System.out.print("⬛ "); // Black box for player's ship
                    } else {
                        System.out.print("🟦️️️ "); // Blue box for empty space
                    }
                }
            }
            System.out.println();
        }
    }

    private void setUpShips(Player human, Player computer) {
        initializeShipsFromInput(human);
    
        readLine("\nHit enter for the enemy to choose their ship locations.");
    
        computer.initializeShipsRandomly();
    
        System.out.println("\nThe enemy has placed their ships.");
    }

    private boolean readYesOrNo(String prompt) {
        while (true) {
            System.out.print(prompt + " (Y/N): ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("y") || input.equals("yes")) {
                System.out.println();
                return true;
            } else if (input.equals("n") || input.equals("no")) {
                System.out.println();
                return false;
            } else {
                System.out.println("Invalid input. Please enter Y or N.");
            }
        }
    }
    
    private void initializeShipsFromInput(Player player) {
        boolean randomizeShips = readYesOrNo("Do you want to place your own ships?");
        if (!randomizeShips) {
            clearScreen(); // Clear the screen before displaying the randomized ships
            player.initializeShipsRandomly();
        } else {
            for (int i = 0; i < player.NUM_SHIPS; i++) {
                readLine("\nHit enter to place the next ship.");
                System.out.println();
                clearScreen();
                System.out.println("  --------- YOUR FLEET");
                player.printMyShips();
                System.out.println();
                int length = player.SHIP_LENGTHS[i];
                System.out.println("Now you need to place a ship of length " + length);
    
                Ship ship = new Ship(length);
    
                while (true) {
                    int row = readRow();
                    int col = readCol();
                    int dir = readDirection();
                    ship.setLocation(row, col);
                    ship.setDirection(dir);
                    if (player.addShip(ship)) {
                        break;
                    }
                    System.out.println("Invalid ship placement. Please try again.");
                }
            }
        }
        
        // Print the grid after all ships have been placed
        clearScreen();
        System.out.println("  --------- YOUR FLEET");
        player.printMyShips();
    }

    private int readDirection() {
        while (true) {
            String dir = readLine("Horizontal or Vertical? (H/V) ");
            dir = dir.toUpperCase();

            if (dir.length() > 0) {
                if (dir.charAt(0) == 'H') {
                    return HORIZONTAL;
                } else if (dir.charAt(0) == 'V') {
                    return VERTICAL;
                }
            }
            System.out.println("Invalid direction, please try again.");
        }
    }

    private int readRow() {
        while (true) {
            String row = readLine("Which row? (A-" + MAX_ROW + ") ");
            row = row.toUpperCase();
            if (row.length() > 0) {
                char ch = row.charAt(0);
                if (ch >= 'A' && ch <= MAX_ROW) {
                    return ch - 'A';
                }
            }
            System.out.println("Invalid row, please try again.");
        }
    }
    
    private int readCol() {
        while (true) {
            int col = readInt("Which column? (1-" + MAX_COL + ") ");
            if (col >= 1 && col <= MAX_COL) {
                return col - 1;
            }
            System.out.println("Invalid column, please try again.");
        }
    }
}