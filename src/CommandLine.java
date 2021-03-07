import build.Block;

import java.util.ArrayList;
import java.util.HashMap;


public class CommandLine{

    MinesweeperGame game;
    String[] args; // the arguments passed in
    String[] coords; // the coordinates arguments

    public CommandLine(MinesweeperGame field) {
        this.game = field;
    }

    public void executeCommand(Command c){
        System.out.println(c);
        if (c instanceof GameCommand){
            c.run();
        } else {
            for (int i = 1; i < args.length; i++) {
                coords = args[i].split(",");

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
        }};
        this.args = input;
        return commandMap.get(input[0]);
    }

    public static interface Command extends Runnable {

        @Override
        public abstract void run();

    }

    /**
     * This is to differentiate commands that have params and don't have params in {@code executeCommand()}
     */
    public static interface GameCommand extends Command{

    }


    /** The commands **/

    private class Reveal implements Command{

        @Override
        public void run() {
            Block s = game.getBlock(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]));
            if (s.isMine() && !s.isFlag()) { // not working
                game.gameOverLoser(); // won't break out of loop... might need an external variable
            } else {
                s.reveal();
                ArrayList<Block> revealedBlocks = new ArrayList<Block>();
                revealedBlocks.add(s); // see if i can directly initialize
                revealSurroundingBlanks(s, revealedBlocks);
            }
        }

        private void revealSurroundingBlanks(Block s, ArrayList<Block> revealedSquares){
            int[][] indexes = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 0}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};

            for (int[] pair : indexes) {

                if (game.isValidCoord(s.getRow()+pair[0], s.getCol()+pair[1])){
                    Block adjacentBlock = game.getBlock(s.getRow()+pair[0], s.getCol()+pair[1]);
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
                if (game.isValidCoord(s.getRow()+pair[0], s.getCol()+pair[1])){
                    Block adjacentSquare = game.getBlock(s.getRow()+pair[0], s.getCol()+pair[1]);
                    if (!adjacentSquare.isMine()) {
                        adjacentSquare.reveal();
                    }
                }
            }
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

    }

    private class Unflag implements Command{

        @Override
        public void run() {
            Block s = game.getBlock(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]));
            s.unflag();
            game.setFlagsLeft(game.getFlagsLeft() + 1);
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

    }

    private class Quit implements GameCommand{

        @Override
        public void run() {
            game.revealField();
            System.out.println("\033[36mDon't give up next time!\033[0m");
            game.gameOverLoser();
        }

    }

    private class GameOver implements GameCommand{

        @Override
        public void run() {
            game.setGameOverMode(

                switch(args[1]){ // no need for try because regex filters out invalid commands
                    case "1" -> 1;
                    case "2" -> 2;
                    case "3" -> 3;
                    default -> throw new IllegalStateException("Invalid gameover mode: " + args[1]);

            });

            System.out.println("Gameover change to: " + args[1]);
        }

    }
}


