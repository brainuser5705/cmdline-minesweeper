package game;

import build.*;

import java.util.*;

public class MinesweeperGame extends Field{

    private static MinesweeperGame instance;

    private int flagsLeft;
    private int gameOverMode = 1;

    private boolean isGameOver;
    private boolean isWinner;

    public MinesweeperGame(int numMines, int numRows, int numCols){
        super(numMines, numRows, numCols);
        isGameOver = false;
        isWinner = true;
        flagsLeft = numMines;
        instance = this;
    }

    public MinesweeperGame(Level level){
        super(level.getNumMines(), level.getNumRows(), level.getNumCols());
        flagsLeft = level.getNumMines();
        instance = this;
    }

    public static synchronized MinesweeperGame getInstance(){
        if (instance == null){
            instance = new MinesweeperGame(Level.BEGINNER);
        }
        return instance;
    }

    public void playMinesweeper() {
        CommandLine cmd = new CommandLine(this);
        Scanner s = new Scanner(System.in);

        while(!isGameOver){
            //print field
            printField();

            //get command and executre
            String c = s.nextLine();
            String[] args = c.split(" ");
            CommandLine.Command command = cmd.commandBuilder(args);
            if (command != null){
                cmd.executeCommand(command);
            }else{
                System.out.println("No command found");
            }

            //check if game over
            if (!isGameOver) {

                GameOver.Mode mode = switch(gameOverMode){
                    case 1 -> GameOver.Mode.GAME_OVER_ONE;
                    case 2 -> GameOver.Mode.GAME_OVER_TWO;
                    case 3 -> GameOver.Mode.GAME_OVER_THREE;
                    default -> throw new IllegalStateException("Unexpected value: " + gameOverMode);
                };

                isGameOver = mode.check();
            }

            System.out.println("Flags left: " + flagsLeft);

        }

        // end game
        revealField();
        printField();
        if (isWinner)
            System.out.println("You win!");
        else
            System.out.println("You lose!");
    }

    public int getFlagsLeft() {
        return flagsLeft;
    }

    public void setFlagsLeft(int newAmount){
        this.flagsLeft = newAmount;
    }

    public void setGameOver(){
        isWinner = false;
        isGameOver = true;
    }

    public void setGameOverMode(int mode){
        this.gameOverMode = mode;
    }


}

