import build.Block;
import build.Field;
import build.Level;
import build.MineBlock;

import java.util.ArrayList;
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
            //"(r|f|uf) ((\\d)+,(\\d)+( )?)+"
//            if (c.matches(generateRegex())){ // make a command class?
            if (c.matches("((r|f|uf) (\\d+,\\d+( )?)+)|p|q|n|reset|auto|(gameover [1-3])")){ // will have index error if digits aren't in bound
                String[] args = c.split(" ");
                switch(args[0]){
                    case "r" -> {
                        revealPositions(args);
                    }
                    case "f" -> {
                        flagPositions(args);
                    }
                    case "uf" -> {
                        unflagPositions(args);
                    }
                    case "p" -> {
                        printField();
                    }
                    case "q" -> {
                        revealField();
                        System.out.println("Don't give up next time!");
                        isGameOver = true;
                    }
                    case "auto" -> {
                        autoPlay();
                    }
                    case "reset" -> {
                        resetGame();
                    }
                    case "gameover" -> {
                        gameOverMode = switch(args[1]){ // no need for try because regex filters out invalid commands
                            case "1" -> 1;
                            case "2" -> 2;
                            case "3" -> 3;
                            default -> throw new IllegalStateException("Unexpected value: " + args[1]);
                        };
                        System.out.println("Gameover change to: " + args[1]);
                    }
                    default -> System.out.println("This command is not supported yet.");
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
                System.out.println("Invalid command.");
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

    private void revealPositions(String[] args){
        for (int i = 1; i < args.length; i++){
            String[] coords = args[i].split(",");

            if (!isValidCoord(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]))){
                System.err.println("Invalid coordinates");
            }else{
                Block s = getBlock(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]));
                if (s.isMine() && !s.isFlag()) { // not working
                    isWinner = false;
                    isGameOver = true;
                    break;
                } else {
                    s.reveal();
                    ArrayList<Block> revealedBlocks = new ArrayList<Block>();
                    revealedBlocks.add(s); // see if i can directly initialize
                    revealSurroundingBlanks(s, revealedBlocks);
                }
            }

        }
    }

    private void flagPositions(String[] args){
        if (flagsLeft <= 0){
            System.out.println("All flags are used.");
        }else{
            for (int i = 1; i < args.length; i++) {
                String[] coords = args[i].split(",");

                if (!isValidCoord(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]))){
                    System.err.println("Invalid coordinates");
                }else{
                    Block s = getBlock(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]));
                    s.flag();
                    if (flagsLeft > 0 && !s.isReveal()) // not already revealed
                        flagsLeft--;
                    else
                        break;
                }

            }
        }
    }

    private void unflagPositions(String[] args){
        for (int i = 1; i < args.length; i++){
            String[] coords = args[i].split(",");

            if (!isValidCoord(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]))){
                System.err.println("Invalid coordinates");
            }else{
                Block s = getBlock(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]));
                s.unflag();
                flagsLeft++;
            }
        }
    }

    //util
    private void revealSurroundingBlanks(Block s, ArrayList<Block> revealedSquares){
        int[][] indexes = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 0}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};

        for (int[] pair : indexes) {

            if (isValidCoord(s.getRow()+pair[0], s.getCol()+pair[1])){
                Block adjacentBlock = getBlock(s.getRow()+pair[0], s.getCol()+pair[1]);
                if (!revealedSquares.contains(adjacentBlock) && adjacentBlock.isBlankBlock()){ // is already revealed, then skip - add this function
                    adjacentBlock.reveal();
                    revealAdjacentBlock(s); //to show numbers
                    revealedSquares.add(adjacentBlock);
                    revealSurroundingBlanks(adjacentBlock, revealedSquares);
                }
            }
        }
    }

    // util method for reveal surrounding blocks
    private void revealAdjacentBlock(Block s){
        int[][] indexes = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 0}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};

        for (int[] pair : indexes) {
            if (isValidCoord(s.getRow()+pair[0], s.getCol()+pair[1])){
                Block adjacentSquare = getBlock(s.getRow()+pair[0], s.getCol()+pair[1]);
                if (!adjacentSquare.isMine()) {
                    adjacentSquare.reveal();
                }
            }
        }
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



}
