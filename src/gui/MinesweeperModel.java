package gui;

import build.*;

import java.util.ArrayList;
import java.util.List;

public class MinesweeperModel extends Field{

    private int flagsLeft;

    private boolean isLoser;

    private List<Observer<MinesweeperModel, Object>> observers = new ArrayList<>();;

    public MinesweeperModel(int numMines, int numRows, int numCols){
        super(numMines, numRows, numCols);
        isLoser = false;
        flagsLeft = numMines;
    }

    public MinesweeperModel(Level level){
        super(level.getNumMines(), level.getNumRows(), level.getNumCols());
        flagsLeft = level.getNumMines();
    }

    public void isGameOver(){
        if (checkWinningGame()){
            revealField();
            notifyObservers("winner");
        }else if (isLoser){
            revealField();
            notifyObservers("death");
        }
    }

    private boolean checkWinningGame(){ // game over condition is all blocks are revealed and all mines are flagged - this will eliminate guessing (in a way)
        Block[][] field = getField();
        for (Block[] row : field){
            for (Block block : row){
                if ((block.isMine() && !block.isFlag()) || (!block.isMine() && !block.isReveal())){
                    return false;
                }
            }
        }
        flagsLeft = 0;
        return true;
    }

    public void reveal(Block block){

        block.reveal();

        if (!block.isFlag()){
            if (block.isMine()) {
                isLoser = true;
            } else {
                ArrayList<Block> revealedBlocks = new ArrayList<>();
                revealedBlocks.add(block);
                revealSurroundingBlanks(block, revealedBlocks);
            }
        }

        notifyObservers(null);

    }

    public void toggleFlag(Block block){
        if (block.isFlag()){
            block.unflag();
            flagsLeft++;
        }else if (!block.isFlag()){
            if (flagsLeft <= 0){
                System.out.println("All flags are used.");
            }else{
                block.flag();
                if (flagsLeft > 0 && !block.isReveal()) // not already revealed
                    flagsLeft--;
            }
        }
        notifyObservers(null);
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

    public void autoPlay(){
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

        notifyObservers(null);
    }

    public void resetGame(){
        Block[][] field = getField(); // make this a instance var
        for (Block[] row : field){
            for (Block block : row){
                block.forceUnreveal();
            }
        }
        flagsLeft = getNumMines();

        notifyObservers(null);

    }

    public int getFlagsLeft(){
        return flagsLeft;
    }

    public void addObserver(Observer<MinesweeperModel, Object> observer){
        this.observers.add(observer);
    }

    public void notifyObservers(String args){
        for (var observer : observers){
            observer.update(this, args);
        }
    }


}