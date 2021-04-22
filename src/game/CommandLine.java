package game;

import build.Block;
import build.NumBlock;

import java.util.ArrayList;
import java.util.HashMap;


public class CommandLine{

    MinesweeperGame game;
    String[] args; // the arguments passed in
    String[] coords; // the coordinates arguments

    int[][] ADJACENT_INDEXES = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 0}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};

    public CommandLine(MinesweeperGame field) {
        this.game = field;
    }

    public void executeCommand(Command c){
        if (c instanceof GameCommand){
            System.out.println(c.toString());
            c.run();
        } else {
            for (int i = 1; i < args.length; i++) {
                coords = args[i].split(",");
                System.out.print(c.toString());

                if (!game.isValidCoord(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]))) {
                    System.err.println("Invalid coordinates: " + coords[0] + "," + coords[1]);
                } else {
                    c.run();
                }
            }
        }
    }

    public Command commandBuilder(String[] input){
        HashMap<String, Command> commandMap= new HashMap<>(){{
            put("r", new Reveal());
            put("f", new Flag());
            put("uf", new Unflag());
            put("q", new Quit());
            put("auto", new Auto()); // can't be execute command because no params
            put("reset", new Reset());
            put("gmode", new GameOver());
            put("chord", new Chord());
        }};
        this.args = input;
        return commandMap.get(input[0]);
    }

    public String coordToString(String[] coords){
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < coords.length; i += 2){
            result.append("(").append(coords[i]).append(",").append(coords[i + 1]).append(")").append("\n");
        }
        return result.toString();
    }

    public interface Command extends Runnable {

        @Override
        void run();

        String toString();

    }

    /**
     * This is to differentiate commands that have params and don't have params in {@code executeCommand()}
     */
    public interface GameCommand extends Command{

    }


    /** The commands **/

    private class Reveal implements Command{

        @Override
        public void run() {
            Block s = game.getBlock(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]));
            if (s.isMine() && !s.isFlag()) { // not working
                game.setGameOver(); // won't break out of loop... might need an external variable
            } else {
                s.reveal();
                ArrayList<Block> revealedBlocks = new ArrayList<Block>();
                revealedBlocks.add(s);
                revealSurroundingBlanks(s, revealedBlocks);
            }
        }

        private void revealSurroundingBlanks(Block s, ArrayList<Block> revealedSquares){
            for (int[] pair : ADJACENT_INDEXES) {

                if (game.isValidCoord(s.getRow()+pair[0], s.getCol()+pair[1])){
                    Block adjacentBlock = game.getBlock(s.getRow()+pair[0], s.getCol()+pair[1]);
                    if (!revealedSquares.contains(adjacentBlock) && adjacentBlock.isBlankBlock()){ // is already revealed, then skip - add this function
                        adjacentBlock.reveal();
                        revealedSquares.add(adjacentBlock);
                        revealSurroundingBlanks(adjacentBlock, revealedSquares); //recursion with "accumulator" list
                    }
                }
            }
            for (Block revealedBlock : revealedSquares){
                if (!revealedBlock.equals(s)) // skip the clicked block
                    revealAdjacentBlock(revealedBlock);
            }
        }

        // util method for reveal surrounding blocks
        private void revealAdjacentBlock(Block s){

            for (int[] pair : ADJACENT_INDEXES) {
                if (game.isValidCoord(s.getRow()+pair[0], s.getCol()+pair[1])){
                    Block adjacentSquare = game.getBlock(s.getRow()+pair[0], s.getCol()+pair[1]);
                    if (!adjacentSquare.isMine()) {
                        adjacentSquare.reveal();
                    }
                }
            }

        }

        public String toString(){
            return "Revealing block: " + coordToString(coords);
        }

    }

    private class Flag implements Command{

        @Override
        public void run() {
            if (game.getFlagsLeft() <= 0){
                System.out.println("All flags are used.");
            }else {
                Block s = game.getBlock(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]));
                s.flag();
                if (game.getFlagsLeft() > 0 && !s.isReveal()) // not already revealed
                    game.setFlagsLeft(game.getFlagsLeft() - 1);
            }
        }

        public String toString(){
            return "Flagging block: " + coordToString(coords);
        }

    }

    private class Unflag implements Command{

        @Override
        public void run() {
            Block s = game.getBlock(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]));
            s.unflag();
            game.setFlagsLeft(game.getFlagsLeft() + 1);
        }

        public String toString(){
            return "Unflagging block: " + coordToString(coords);
        }

    }

    private class Auto implements GameCommand{

        @Override
        public void run() {
            for (Block[] row : game.getField()){
                for (Block block : row){
                    if (block.isMine()) {
                        block.flag();
                    } else {
                        block.reveal();
                    }
                }
            }
            game.setFlagsLeft(0);
        }

        public String toString(){
            return "Auto play...";
        }

    }

    private class Reset implements GameCommand{

        @Override
        public void run() {
            Block[][] blocks = game.getField(); // make this a instance var
            for (Block[] row : blocks){
                for (Block block : row){
                    block.forceUnreveal();
                }
            }
            game.setFlagsLeft(game.getNumMines());
        }

        public String toString(){
            return "Resetting game...";
        }

    }

    private class Quit implements GameCommand{

        @Override
        public void run() {
            game.revealField();
            System.out.println("\033[36mDon't give up next time!\033[0m");
            game.setGameOver();
        }

        public String toString(){
            return "Quitting game...";
        }

    }

    private class GameOver implements GameCommand{

        @Override
        public void run() {
            int gamemode = Integer.parseInt(args[1]);
            game.setGameOverMode(gamemode);
        }

        public String toString(){
            return "Changing game over mode to: " + args[1];
        }

    }

    private class Chord implements Command{ // make this an inner class of reveal since it uses reveal methods?

        @Override
        public void run() {
            Block block = game.getBlock(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]));
            // check that it has valid number of surrounding flags
            // if yes, then reveal surrounding non-flagged cells and their adjacent cells
            //make sure that the block is revealed
            if (!(block instanceof NumBlock)) {
                System.err.println("Cannot chord this block: " + coordToString(coords));
            }else {
                if (!block.isBlankBlock() && block.isReveal() && hasValidFlags((NumBlock) block)) {
                    // call the reveal methods...
                    System.out.println(this);
                    ArrayList<Block> revealedBlocks = new ArrayList<Block>();
                    revealedBlocks.add(block); // see if i can directly initialize
                    revealSurroundingBlanks(block, revealedBlocks);
                    // good but does not reveal flags there should be a parameter.
                }else{
                    System.out.println("Cannot cord this block: " + coordToString(coords));
                }
            }

        }

        private void revealSurroundingBlanks(Block s, ArrayList<Block> revealedSquares){

            for (int[] pair : ADJACENT_INDEXES) {

                if (game.isValidCoord(s.getRow()+pair[0], s.getCol()+pair[1])){
                    Block adjacentBlock = game.getBlock(s.getRow()+pair[0], s.getCol()+pair[1]);
                    if (!revealedSquares.contains(adjacentBlock) && adjacentBlock.isBlankBlock()){ // is already revealed, then skip - add this function
                        adjacentBlock.reveal();
                        revealAdjacentBlock(s); //to show numbers
                        revealedSquares.add(adjacentBlock);
                        revealSurroundingBlanks(adjacentBlock, revealedSquares); //recursion with "accumulator" list
                    }
                }
            }
        }

        // util method for reveal surrounding blocks
        private void revealAdjacentBlock(Block s){

            for (int[] pair : ADJACENT_INDEXES) {
                if (game.isValidCoord(s.getRow()+pair[0], s.getCol()+pair[1])){
                    Block adjacentSquare = game.getBlock(s.getRow()+pair[0], s.getCol()+pair[1]);
                    if (!adjacentSquare.isMine()) {
                        adjacentSquare.reveal();
                    }else{
                        game.setGameOver();
                        break;
                    }
                }
            }
        }

        public String toString(){
            return "Chording blocks: " + coordToString(coords);
        }

        public boolean hasValidFlags(NumBlock block){

            int row = block.getRow();
            int col = block.getCol();

            int flags = 0;

            for (int[] pair : ADJACENT_INDEXES){
                if (game.isValidCoord(row + pair[0], col + pair[1])){
                    if (game.getBlock(row + pair[0], col + pair[1]).isFlag()){
                        flags++;
                    }
                }
            }

            System.out.println(flags);
            System.out.println(block.getNum());

            return flags == block.getNum();

        }

    }
}


