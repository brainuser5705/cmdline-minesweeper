package game;

import build.*;

import java.util.*;
import java.util.function.Predicate;

public class MinesweeperGame extends Field{

    private static MinesweeperGame instance;

    private int flagsLeft;
    private int gameOverMode = 1;

    private boolean isGameOver;
    private boolean isWinner;

    private static final Map< Integer, Predicate< MinesweeperGame >>
            gameOverChecker = Map.of(
            1, MinesweeperGame::checkWinningGame1,
            2, MinesweeperGame::checkWinningGame2,
            3, MinesweeperGame::checkWinningGame3
    );

    public MinesweeperGame(int numMines, int numRows, int numCols){
        super(numMines, numRows, numCols);
        isGameOver = false;
        isWinner = true;
        flagsLeft = numMines;
    }

    public MinesweeperGame(Level level){
        super(level.getNumMines(), level.getNumRows(), level.getNumCols());
        flagsLeft = level.getNumMines();
    }

    public void playMinesweeper() {
        CommandLine cmd = new CommandLine(this);
        Scanner s = new Scanner(System.in);

        while(!isGameOver){
            //print field
            printField();

            //get command and execute
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
                isGameOver = gameOverChecker.get( gameOverMode ).test( this );
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

    public boolean checkWinningGame1(){
        Block[][] field = getField();
        for (Block[] row : field){
            for (Block block : row){
                if ((block.isMine() && !block.isFlag()) || (!block.isMine() && !block.isReveal())){ // not working?
                    return false;
                }
            }
        }
        return true;
    }

    public boolean checkWinningGame2(){

        // check if all mines are flagged
        MineBlock[] mineCoords = getMineCoords();
        for (MineBlock mine : mineCoords){
            if (!mine.isFlag()) {
                return false;
            }
        }

        return true;

    }

    public boolean checkWinningGame3(){
        Block[][] field = getField();
        for (Block[] row : field){
            for (Block block : row){
                if (!block.isMine() && !block.isReveal()) return false;
            }
        }
        flagsLeft = 0;
        return true;
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

