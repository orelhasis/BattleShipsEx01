package BattleShipsUI;

import BattleShipsLogic.Definitions.GameStatus;
import BattleShipsLogic.Definitions.MoveResults;
import BattleShipsLogic.GameObjects.Player;
import BattleShipsLogic.GameObjects.Point;

import java.util.Observable;
import java.util.Scanner;

public class BattleShipConsoleUI extends BattleShipUI {

    @Override
    public void StartGame() {
        int choice = 0;
        showWelcomeMessage();
        while (choice != EXIT_GAME) {
            printChoices();
            choice = getUserInput();
            System.out.println("----------------------------");
            handleUserInput(choice);
            System.out.println("----------------------------");
        }
    }

    @Override
    protected void printGameStartsMessage() {
        System.out.println("Let The Battle Begin!!!");
    }

    @Override
    protected void publishWinnerResults() {
        System.out.println("Congratulations " + theGame.getWinnerPlayer().getName().toString() + ", you are the winner!");
        System.out.println("Results:");
        System.out.println(theGame.getPlayers()[0].getName().toString() + ": " + theGame.getPlayers()[0].getScore());
        System.out.println(theGame.getPlayers()[1].getName().toString() + ": " + theGame.getPlayers()[1].getScore());
    }

    @Override
    protected void printStatistics() {
        System.out.println("All Kind of statistics to be printed here");
    }

    @Override
    protected void showGameLoadFailedMessage() {
        System.out.println("Failed To Load:");
        //TODO ADD LOGIC TO PRINT REASON FOR LOADING ERROR
    }

    @Override
    protected void showGameLoadedMessage() {
        System.out.println("Game Was Successfully Loaded");
    }

    @Override
    public void update(Observable o, Object arg) {

    }

    protected int getUserInput(){
        Scanner in = new Scanner(System.in);
        while(!in.hasNextInt()){
            System.out.println("Invalid Input");
            in.nextLine();
            System.out.print("Your Choice:");
        }
        int choice = in.nextInt();
        while(!isLegalChoice(choice)){
            System.out.println("----------------------------");
            System.out.println("Invalid Choice, Please try again");
            System.out.println("----------------------------");
            printChoices();
            choice = -1;
            while(in.hasNextLine() && choice == -1){
                in.nextLine();
                if(!in.hasNextInt()) {
                    System.out.println("----------------------------");
                    System.out.println("Invalid Input");
                    System.out.println("----------------------------");
                    printChoices();
                }
                else{
                    choice = in.nextInt();
                }
            }
        }
        return choice;
    }

    private void printChoices() {
        System.out.println("Choose one of the following options:");
        switch(theGame.getStatus()){
            case INIT:
                System.out.println("1 - Load Game.");
                System.out.println("7 - Exit Game.");
                break;
            case LOADED:
                System.out.println("1 - Load Game.");
                System.out.println("2 - Start Game.");
                System.out.println("7 - Exit Game.");
                break;
            case RUN:
                System.out.println("---" + theGame.getCurrentPlayer().getName() + "---");
                System.out.println("3 - Show game status.");
                System.out.println("4 - Make a move.");
                System.out.println("5 - Get statistics.");
                System.out.println("6 - Surrender");
                System.out.println("7 - Exit Game.");
                break;
            case OVER:
                System.out.println("1 - Load Game.");
                System.out.println("2 - Restart Game.");
                System.out.println("7 - Exit Game.");
                break;
        }
        System.out.print("Your choice: ");
    }
    private void handleUserInput(int choice){
        switch(choice) {
            case LOAD_GAME:
                if(loadGame()){
                    theGame.setStatus(GameStatus.LOADED);
                    showGameLoadedMessage();
                }
                else{
                    showGameLoadFailedMessage();
                }
                break;
            case START_GAME:
                if(theGame.getStatus() == GameStatus.OVER){ //handles game restart
                    loadGame();
                }
                theGame.setStatus(GameStatus.RUN);
                printGameStartsMessage();
                break;
            case GET_GAME_STATUS:
                System.out.println(theGame.getCurrentPlayer().getName().toString() + " it's your turn.");
                showBoards(theGame.getCurrentPlayer(), theGame);
                System.out.println("Current score: " + theGame.getCurrentPlayer().getScore() + ".");
                break;
            case MAKE_A_MOVE:
                makeMove();
                if(theGame.getStatus() == GameStatus.OVER) {
                    publishWinnerResults();
                }
                break;
            case GET_STATISTICS:
                printStatistics();
                break;
            case QUIT:
                System.out.println("You Just Surrendered!");
                theGame.setStatus(GameStatus.OVER);
                break;
            case EXIT_GAME:
                System.out.println("Thank you and Good-Bye!");
                break;
        }
    }

    private Point getAttackedPoint() {
        Point resPoint = new Point(0,0);
        Scanner in = new Scanner(System.in);
        System.out.print("Insert Point  To attach (i.e B5):");
        String playerInput = in.nextLine();
        if(playerInput.length() != 2){
            resPoint.setX(-1);
            resPoint.setY(-1);
        }
        else{
            resPoint.setX(playerInput.toCharArray()[0] - 'A');
            resPoint.setY(playerInput.toCharArray()[1] - '0' - 1);
        }
        return resPoint;
    }

    @Override
    protected void showWelcomeMessage() {
        System.out.println("Welcome to the Battle Ships game!");
    }
    @Override
    protected void showPrimaryGrid(Player player) {
        char[][] playerPrimaryGrid = player.getPlayerPrimaryGrid();
        System.out.println("Primary grid of " + player.getName().toString());
        showBoard(playerPrimaryGrid);
    }

    @Override
    protected void showTrackingGrid(Player player) {
        System.out.println("Tracking grid of " + player.getName().toString());
        showBoard(player.getPlayerTrackingGrid());
    }

    @Override
    protected void showBoard(char[][] board) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                System.out.print(" "+board[i][j]+" |");
                if(j == board.length-1) {
                    System.out.println(" ");
                }
            }
        }
    }

    protected MoveResults makeMove() {
        Point attackedPoint;
        do{
            attackedPoint = getAttackedPoint();
        }while(!isLegalPoint(attackedPoint));
        return attackAPoint(attackedPoint);
    }
}