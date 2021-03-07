import build.Block;
import build.Field;
import build.Level;
import build.MineBlock;

import java.util.HashMap;
import java.util.Scanner;

public class MinesweeperGame extends Field{

    private int flagsLeft;
    private int gameOverMode = 1;

    private boolean isGameOver;
    private boolean isWinner;

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

    public void playMinesweeper(){
        Scanner s = new Scanner(System.in);

        while(!isGameOver){
            printField();
            String c = s.nextLine();
            if (c.matches("((r|f|uf) (\\d+,\\d+( )?)+)|p|q|n|reset|auto|(gameover [1-3])")){ // will have index error if digits aren't in bound
                String[] args = c.split(" ");
                HashMap<String, CommandLine.Command> commandMap = new HashMap<>(){{
                    put("r", new CommandLine.Reveal(MinesweeperGame.this, args));
                    put("f", new CommandLine.Flag(MinesweeperGame.this, args));
                    put("uf", new CommandLine.Unflag(MinesweeperGame.this, args));
                    put("p", new CommandLine.Print(MinesweeperGame.this, args));
                    //put("q", new CommandLine.Flag(MinesweeperGame.this, args));
                    //put("auto", new CommandLine.Auto(MinesweeperGame.this, args));
                    //put("reset", new CommandLine.Reset(MinesweeperGame.this, args));
                }};
                CommandLine.Command command = commandMap.get(args[0]);
                System.out.println(command);
                System.out.println(args[0]);
                if (command != null){
                    CommandLine.executeCommand(command);
                }else{
                    // unsupported or not working commands for now
                    switch(args[0]){
                        case "auto" -> autoPlay();
                        case "reset" -> resetGame();
                        case "q" -> {
                            revealField();
                            System.out.println("Don't give up next time!");
                            isGameOver = true;
                        }
                        case "gameover" -> {
                            gameOverMode = switch(args[1]){
                                case "1" -> 1;
                                case "2" -> 2;
                                case "3" -> 3;
                                default -> throw new IllegalStateException("Unexpected value: " + args[1]);
                            };
                            System.out.println("Gameover change to: " + args[1]);
                        }
                        default -> System.err.println("No command found");
                    }
                }
                if (!isGameOver) {
                    isGameOver = switch(gameOverMode){
                        case 1 -> gameOver1();
                        case 2 -> gameOver2();
                        case 3 -> gameOver3();
                        default -> throw new IllegalStateException("Unexpected value: " + gameOverMode);
                    };
                }
                System.out.println("Flags left: " + flagsLeft);
            }else {
                System.out.println("Invalid syntax.");
            }
        }

        revealField();
        if (isWinner)
            System.out.println("You win!");
        else
            System.out.println("You lose!");
    }

    private String generateRegex(){
        String rowLimit = "[0-" + (getNumRows() - 1) + "]";
        String colLimit = "[0-" + (getNumCols() - 1) + "]";
        return "((r|f|uf) ((" + rowLimit + ")+,(" + colLimit + ")+( )?)+)|p|q|n|reset|auto|(gameover [1-3])";
    }


    private boolean areAllMinesFlagged(){
        MineBlock[] mineCoords = getMineCoords();
        for (MineBlock mine : mineCoords){
            if (!mine.isFlag()) {
                return false;
            }
        }
        return true;
    }

    private boolean gameOver1(){ // game over condition is all blocks are revealed and all mines are flagged - this will eliminate guessing (in a way)
        Block[][] field = getField();
        for (Block[] row : field){
            for (Block block : row){
                if ((block.isMine() && !block.isFlag()) || (!block.isMine() && !block.isReveal())){
                    return false;
                }
            }
        }
        flagsLeft = 0;
        isWinner = true;
        return true;
    }

    private boolean gameOver2(){ // if all flags are used and all mines are flagged
        if (flagsLeft == 0 && areAllMinesFlagged()) {
            isWinner = true;
            return true;
        }
        return false;
    }

    private boolean gameOver3(){ // game over condition is all blocks are revealed
        Block[][] field = getField();
        for (Block[] row : field){
            for (Block block : row){
                if (!block.isMine() && !block.isReveal()) return false;
            }
        }
        flagsLeft = 0;
        isWinner = true;
        return true;
    }

    private void autoPlay(){
        Block[][] field = getField();
        for (Block[] row : field){
            for (Block block : row){
                if (block.isMine()) {
                    block.flag();
                } else {
                    block.reveal();
                }
            }
        }
        flagsLeft = 0;
    }

    private void resetGame(){
        Block[][] field = getField(); // make this a instance var
        for (Block[] row : field){
            for (Block block : row){
                block.forceUnreveal();
            }
        }
        flagsLeft = getNumMines();
    }

    public int getFlagsLeft() {
        return flagsLeft;
    }

    public void setFlagsLeft(int newAmount){
        this.flagsLeft = newAmount;
    }

    public void gameOverLoser(){
        isWinner = false;
        isGameOver = true;
    }
}
