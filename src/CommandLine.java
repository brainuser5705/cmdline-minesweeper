import build.Block;
import build.Field;

import java.util.ArrayList;


public class CommandLine{

    private static MinesweeperGame field;

    public static void executeCommand(Command c){
        for (int i = 1; i < c.args.length; i++) {
            c.coords = c.args[i].split(",");

            if (!c.field.isValidCoord(Integer.parseInt(c.coords[0]), Integer.parseInt(c.coords[1]))) {
                System.err.println("Invalid coordinates: " + c.coords[0] + "," + c.coords[1]);
            } else {
                c.run();
            }
        }
    }

    public abstract static class Command implements Runnable {

        MinesweeperGame field;
        String[] args; // the arguments passed in
        String[] coords; // the coordinates arguments

        public Command(MinesweeperGame field, String[] args) {
            this.field = field;
            this.args = args;
        }

        @Override
        public abstract void run();

        public void executeCommand() {
            for (int i = 1; i < args.length; i++) {
                coords = args[i].split(",");

                if (!field.isValidCoord(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]))) {
                    System.err.println("Invalid coordinates: " + coords[0] + "," + coords[1]);
                } else {
                    run();
                }
            }
        }

    }

    /** The commands **/

    public static class Reveal extends Command{

        public Reveal(MinesweeperGame field, String[] coords) {
            super(field, coords);
        }

        @Override
        public void run() {
            Block s = field.getBlock(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]));
            if (s.isMine() && !s.isFlag()) { // not working
                field.gameOverLoser(); // won't break out of loop... might need an external variable
            } else {
                s.reveal();
                ArrayList<Block> revealedBlocks = new ArrayList<>();
                revealedBlocks.add(s); // see if i can directly initialize
                revealSurroundingBlanks(s, revealedBlocks);
            }
        }

        private void revealSurroundingBlanks(Block s, ArrayList<Block> revealedSquares){
            int[][] indexes = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 0}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};

            for (int[] pair : indexes) {

                if (field.isValidCoord(s.getRow()+pair[0], s.getCol()+pair[1])){
                    Block adjacentBlock = field.getBlock(s.getRow()+pair[0], s.getCol()+pair[1]);
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
                if (field.isValidCoord(s.getRow()+pair[0], s.getCol()+pair[1])){
                    Block adjacentSquare = field.getBlock(s.getRow()+pair[0], s.getCol()+pair[1]);
                    if (!adjacentSquare.isMine()) {
                        adjacentSquare.reveal();
                    }
                }
            }
        }

    }


    public static class Flag extends Command{

        public Flag(MinesweeperGame field, String[] coords) {
            super(field, coords);
        }

        @Override
        public void run() {
            if (field.getFlagsLeft() <= 0){
                System.out.println("All flags are used.");
            }else {
                Block s = field.getBlock(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]));
                s.flag();
                if (field.getFlagsLeft() > 0 && !s.isReveal()) // not already revealed
                    field.setFlagsLeft(field.getFlagsLeft() - 1);
            }
        }

    }

    public static class Unflag extends Command{

        public Unflag(MinesweeperGame field, String[] coords) {
            super(field, coords);
        }

        @Override
        public void run() {
            Block s = field.getBlock(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]));
            s.unflag();
            field.setFlagsLeft(field.getFlagsLeft() + 1);
        }
    }

    // useless command - should probably delete this
    public static class Print extends Command{

        public Print(MinesweeperGame field, String[] coords){
            super(field, coords);
        }

        @Override
        public void run() {
            field.printField();
        }

    }

    public static class Auto extends Command{

        public Auto(MinesweeperGame field, String[] args) {
            super(field, args);
        }

        @Override
        public void run() {
            Block[][] blocks = field.getField();
            for (Block[] row : blocks){
                for (Block block : row){
                    if (block.isMine()) {
                        block.flag();
                    } else {
                        block.reveal();
                    }
                }
            }
            field.setFlagsLeft(0);
        }
    }

    public static class Reset extends Command{
        public Reset(MinesweeperGame field, String[] args){
            super(field, args);
        }

        @Override
        public void run() {
            Block[][] blocks = field.getField(); // make this a instance var
            for (Block[] row : blocks){
                for (Block block : row){
                    block.forceUnreveal();
                }
            }
            field.setFlagsLeft(field.getNumMines());
        }

    }

}


