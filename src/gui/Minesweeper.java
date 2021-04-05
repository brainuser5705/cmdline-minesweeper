package gui;

import build.Block;
import build.Field;
import build.Level;
import build.MineBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Minesweeper extends Field{

    private int flagsLeft;
    private int gameOverMode = 1;

    private boolean isGameOver;
    private boolean isWinner;

    private List<Observer<Minesweeper>> observers = new ArrayList<>();;

    public Minesweeper(int numMines, int numRows, int numCols){
        super(numMines, numRows, numCols);
        isGameOver = false;
        isWinner = true;
        flagsLeft = numMines;
    }

    public Minesweeper(Level level){
        super(level.getNumMines(), level.getNumRows(), level.getNumCols());
        flagsLeft = level.getNumMines();
    }


    private void reveal(Block block){

        if (block.isMine() && !block.isFlag()) { // not working
            isWinner = false;
            isGameOver = true;
        } else {
            block.reveal();
            ArrayList<Block> revealedBlocks = new ArrayList<Block>();
            revealedBlocks.add(block); // see if i can directly initialize
            revealSurroundingBlanks(block, revealedBlocks);
        }

        notifyObservers();

    }

    private void flag(Block block){
        if (flagsLeft <= 0){
            System.out.println("All flags are used.");
        }else{
            block.flag();
            if (flagsLeft > 0 && !block.isReveal()) // not already revealed
                flagsLeft--;
        }

        notifyObservers();

    }

    private void unflag(Block block){
        block.unflag();
        flagsLeft++;

        notifyObservers();

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

    //util
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

        notifyObservers();

    }

    private void resetGame(){
        Block[][] field = getField(); // make this a instance var
        for (Block[] row : field){
            for (Block block : row){
                block.forceUnreveal();
            }
        }
        flagsLeft = getNumMines();

        notifyObservers();

    }

    public int getFlagsLeft(){
        return flagsLeft;
    }

    public void addObserver(Observer<Minesweeper> observer){
        this.observers.add(observer);
    }

    public void notifyObservers(){
        for (Observer<Minesweeper> observer : observers){
            observer.update(this);
        }
    }


}