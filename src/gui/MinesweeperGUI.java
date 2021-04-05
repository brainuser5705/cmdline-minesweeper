package gui;

import build.Block;
import build.Level;
import build.NumBlock;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Scanner;

public class MinesweeperGUI extends Application implements Observer<Minesweeper> {

    Image ONE = new Image(getClass().getResourceAsStream("resources/one.png"));
    Image TWO = new Image(getClass().getResourceAsStream("resources/two.png"));
    Image THREE = new Image(getClass().getResourceAsStream("resources/three.png"));
    Image FOUR = new Image(getClass().getResourceAsStream("resources/four.png"));
    Image FIVE = new Image(getClass().getResourceAsStream("resources/five.png"));
    Image SIX = new Image(getClass().getResourceAsStream("resources/six.png"));
    Image SEVEN = new Image(getClass().getResourceAsStream("resources/seven.png"));
    Image EIGHT = new Image(getClass().getResourceAsStream("resources/eight.png"));
    Image MINE = new Image(getClass().getResourceAsStream("resources/gowon_mine.jpg"));
    Image OOPS = new Image(getClass().getResourceAsStream("resources/oops.jpg"));
    Image BLANK = new Image(getClass().getResourceAsStream("resources/blank.png"));

    Minesweeper game;

    GridPane mineGrid = new GridPane();
    Label numMines = new Label();

    public static void main(String[] args){
        Application.launch( args );
    }

    @Override
    public void init(){
        game = new Minesweeper(Level.EXPERT);
        game.addObserver(this);
        System.out.println("Initialized model and added an observer!");
    }

    @Override
    public void start(Stage stage) throws Exception {

        VBox mainPane = new VBox();

        numMines.setText(game.getFlagsLeft() + " flags left");

        for(int row = 0; row < game.getNumRows(); row++){
            for (int col = 0; col < game.getNumCols(); col++){
                Button button = new Button();
                Block block = game.getBlock(row,col);
                if (block instanceof NumBlock){
                    int num = ((NumBlock) block).getNum();
                    button.setGraphic(new ImageView(
                            switch (num) {
                                case 0 -> BLANK;
                                case 1 -> ONE;
                                case 2 -> TWO;
                                case 3 -> THREE;
                                case 4 -> FOUR;
                                case 5 -> FIVE;
                                case 6 -> SIX;
                                case 7 -> SEVEN;
                                case 8 -> EIGHT;
                                default -> OOPS;
                            }
                    ));
                }else{
                    button.setGraphic(new ImageView(MINE));
                }
                mineGrid.add(button, col, row);
            }
        }

        mainPane.getChildren().addAll(numMines, mineGrid);

        stage.setScene(new Scene(mainPane));
        stage.setTitle("Minesweeper");
        stage.show();
    }

    @Override
    public void update(Minesweeper minesweeper) {

    }

}
