import java.util.ArrayList;
import java.util.Scanner;

public class MinesweeperGame {

    Field game;
    int numMines;
    boolean isGameOver;
    boolean isWinner;
    int flagsLeft;

    public MinesweeperGame(int numMines, int numRows, int numCols){
        game = new Field(numMines, numRows, numCols);
        game.generateField();
        this.numMines = numMines;
        isGameOver = false;
        isWinner = true;
        flagsLeft = numMines;
    }

    public void playMinesweeper(){
        Scanner s = new Scanner(System.in);

        while(!isGameOver){
            game.printField();
            String c = s.nextLine();
            //"(r|f|uf) ((\\d)+,(\\d)+( )?)+"
            if (c.matches(generateRegex()) || c.equals("p") || c.equals("q") || c.equals("auto")){
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
                        game.printField();
                    }
                    case "q" -> {
                        game.revealField();
                        System.out.println("Don't give up next time!");
                        isGameOver = true;
                    }
                    case "auto" -> {
                        autoPlay();
                    }
                    default -> System.out.println("This command is not supported yet.");
                }
                if (!isGameOver) {
                    isGameOver = realGameOver();
                }
                System.out.println("Flags left: " + flagsLeft);
            }else {
                System.out.println("Invalid command.");
            }
        }

        game.revealField();
        if (isWinner)
            System.out.println("You win!");
        else
            System.out.println("You lose!");
    }

    private String generateRegex(){
        String rowLimit = "[0-" + (game.getNumRows() - 1) + "]";
        String colLimit = "[0-" + (game.getNumCols() - 1) + "]";
        return "(r|f|uf) ((" + rowLimit + ")+,(" + colLimit + ")+( )?)+";
    }

    private void revealPositions(String[] args){
        for (int i = 1; i < args.length; i++){
            String[] coords = args[i].split(",");
            Block s = game.getBlock(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]));
            if (s.isMine() && !s.isFlag()){ // not working
                isWinner = false;
                isGameOver = true;
                break;
            }else {
                s.reveal();
                ArrayList<Block> revealedBlocks = new ArrayList<Block>();
                revealedBlocks.add(s); // see if i can directly initialize
                revealSurroundingBlanks(s, revealedBlocks);
            }
        }
    }

    private void flagPositions(String[] args){
        for (int i = 1; i < args.length; i++){
            String[] coords = args[i].split(",");
            Block s = game.getBlock(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]));
            s.flag();
            flagsLeft--;
        }
    }

    private void unflagPositions(String[] args){
        for (int i = 1; i < args.length; i++){
            String[] coords = args[i].split(",");
            Block s = game.getBlock(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]));
            s.unflag();
            flagsLeft++;
        }
    }

    //maybe use an array of already explored squares
    private void revealSurroundingBlanks(Block s, ArrayList<Block> revealedSquares){
        int[][] indexes = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 0}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};

        for (int[] pair : indexes) {
            try {
                Block adjacentBlock = game.getBlock(s.getRow()+pair[0], s.getCol()+pair[1]);
                if (!revealedSquares.contains(adjacentBlock) && adjacentBlock.isBlankBlock()){ // is already revealed, then skip - add this function
                    adjacentBlock.reveal();
                    revealAdjacentBlock(s); //to show numbers
                    revealedSquares.add(adjacentBlock);
                    revealSurroundingBlanks(adjacentBlock, revealedSquares);
                }
            } catch (Exception e) {
                //nothing
            }
        }
    }

    private void revealAdjacentBlock(Block s){
        int[][] indexes = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 0}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};

        for (int[] pair : indexes) {
            try {
                Block adjacentSquare = game.getBlock(s.getRow()+pair[0], s.getCol()+pair[1]);
                if (!adjacentSquare.isMine()) {
                    adjacentSquare.reveal();
                }
            } catch (Exception e) {
                //nothing
            }
        }
    }

    private boolean areAllMinesFlagged(){
        MineBlock[] mineCoords = game.getMineCoords();
        for (MineBlock mine : mineCoords){
            if (mine.isFlag()) {
                return false;
            }
        }
        return true;
    }

    public boolean realGameOver(){ // game over condition is all blocks are revealed and all mines are flagged - this will eliminate guessing (in a way)
        Block[][] field = game.getField();
        for (Block[] row : field){
            for (Block block : row){
                if ((block.isMine() && !block.isFlag()) || (!block.isMine() && !block.isReveal())){
                    return false;
                }
            }
        }
        return true;
    }

    public void autoPlay(){
        Block[][] field = game.getField();
        for (Block[] row : field){
            for (Block block : row){
                if (block.isMine()) {
                    block.flag();
                    flagsLeft--;
                } else {
                    block.reveal();
                }
            }
        }
    }

    private boolean isGameOver(){
        return flagsLeft == 0 && areAllMinesFlagged();
        // if all flags are used and all mines are flagged
    }

}
