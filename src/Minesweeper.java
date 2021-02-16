import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Minesweeper {

    Field game;
    int numMines;
    boolean isGameOver;
    boolean isWinner;
    int flagsLeft;

    public Minesweeper(int numMines, int numRows, int numCols){
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
            if (c.matches(generateRegex()) || c.equals("p") || c.equals("q")){
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
                        System.exit(0);
                    }
                    default -> System.out.println("This command is not supported yet.");
                }
                if (!isGameOver) {
                    isGameOver = isGameOver();
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
        String rowLimit = "[0-" + (game.numRows - 1) + "]";
        String colLimit = "[0-" + (game.numCols - 1) + "]";
        return "(r|f|uf) ((" + rowLimit + ")+,(" + colLimit + ")+( )?)+";
    }

    private void revealPositions(String[] args){
        for (int i = 1; i < args.length; i++){
            String[] coords = args[i].split(",");
            Square s = game.getSquare(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]));
            if (s.isMine()){ // not working
                System.out.println(s.isMine());
                isWinner = false;
                isGameOver = true;
                break;
            }else {
                s.reveal();
                ArrayList<Square> revealedSquares = new ArrayList<Square>();
                revealedSquares.add(s); // see if i can directly initialize
                revealSurroundingBlanks(s, revealedSquares);
            }
        }
    }

    private void flagPositions(String[] args){
        for (int i = 1; i < args.length; i++){
            String[] coords = args[i].split(",");
            Square s = game.getSquare(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]));
            s.flag();
            flagsLeft--;
        }
    }

    private void unflagPositions(String[] args){
        for (int i = 1; i < args.length; i++){
            String[] coords = args[i].split(",");
            Square s = game.getSquare(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]));
            s.unflag();
            flagsLeft++;
        }
    }

    //maybe use an array of already explored squares
    private void revealSurroundingBlanks(Square s, ArrayList<Square> revealedSquares){
        int[][] indexes = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 0}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};

        for (int[] pair : indexes) {
            try {
                Square adjacentSquare = game.getSquare(s.getRow()+pair[0], s.getCol()+pair[1]);
                if (!revealedSquares.contains(adjacentSquare) && adjacentSquare.getValue() == 0){ // is already revealed, then skip - add this function
                    adjacentSquare.reveal();
                    revealAdjacentSquares(s); //to show numbers
                    revealedSquares.add(adjacentSquare);
                    revealSurroundingBlanks(adjacentSquare, revealedSquares);
                }
            } catch (Exception e) {
                //nothing
            }
        }
    }

    private void revealAdjacentSquares(Square s){
        int[][] indexes = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 0}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};

        for (int[] pair : indexes) {
            try {
                Square adjacentSquare = game.getSquare(s.getRow()+pair[0], s.getCol()+pair[1]);
                if (!adjacentSquare.isMine()) {
                    adjacentSquare.reveal();
                }
            } catch (Exception e) {
                //nothing
            }
        }
    }

    private boolean areAllMinesFlagged(){
        MineSquare[] mineCoords = game.getMineCoords();
        for (MineSquare mine : mineCoords){
            if (!mine.isFlagged()) {
                //System.out.println("Row: " + mine.getRow() + ", Col: " + mine.getCol());
                return false;
            }
        }
        return true;
    }

    private boolean isGameOver(){
        return flagsLeft == 0 && areAllMinesFlagged();
        // if all flags are used and all mines are flagged
    }

    public static void main(String[] args){
        Minesweeper game = new Minesweeper(10, 9, 9);
        System.out.println("\033[36mCommand-line Minesweeper\033[0m");
        game.playMinesweeper();
    }

    // reveal multiple blocks - all surrounding blank blocks are reveal
    // mine countdown
    // when board is clear, winner is set true
    // custom error messages
    // timer
    //change from row/col params to square parameter with getter methods for row and col
    //display coordinates for easy gameplay
    //add color - colors don't work, need to find another way
    //multi command input
    //change from interface to abstract classes
    //add row and col numbers
}
