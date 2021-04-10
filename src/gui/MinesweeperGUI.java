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

public class MinesweeperGUI extends Application implements Observer<MinesweeperModel, Object> {

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

    MinesweeperModel game;

    GridPane mineGrid;
    Label numMines = new Label();
    Button resetButton = new Button("Reset game");
    Button newGameButton = new Button("New game");


    public static void main(String[] args){
        Application.launch( args );
    }

    @Override
    public void init(){
        game = new MinesweeperModel(Level.EXPERT);
        game.resetGame();
        game.addObserver(this);
        System.out.println("Initialized model and added an observer!");
    }

    @Override
    public void start(Stage stage){

        VBox mainPane = new VBox();

        numMines.setText(game.getFlagsLeft() + " flags left");

        resetButton.setOnAction(e -> game.resetGame());

        newGameButton.setOnAction(e -> {
            init();
            start(stage);
        });

        // have to declare a new grid pane for every new game
        mineGrid = new GridPane();

        for(int row = 0; row < game.getNumRows(); row++){
            for (int col = 0; col < game.getNumCols(); col++){
                Button button = new Button();
                Block block = game.getBlock(row,col);
                button.setGraphic(new ImageView(BLANK));
                button.setOnMousePressed(
                        mouseEvent -> {
                            if (mouseEvent.isPrimaryButtonDown()){
                                game.reveal(block);
                            }else if (mouseEvent.isSecondaryButtonDown()){
                                game.toggleFlag(block);
                            }
                        }
                );
                mineGrid.add(button, col, row);
            }
        }

        mainPane.getChildren().addAll(numMines, resetButton, newGameButton, mineGrid);

        stage.setScene(new Scene(mainPane));
        stage.setTitle("Minesweeper");
        stage.show();
    }

    @Override
    public void update(MinesweeperModel minesweeperModel, Object specialCommand){
        game.printField();

        numMines.setText(game.getFlagsLeft() + " flags left");

        for (int row = 0; row < game.getNumRows(); row++) {
            for (int col = 0; col < game.getNumCols(); col++) {

                Block block = game.getBlock(row, col);

                int pos = col + (row * game.getNumCols());
                Button button = (Button) mineGrid.getChildren().get(pos);

                if (block.isReveal()) {
                    button.setDisable(true);
                    if (block instanceof NumBlock) {
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
                }else if (block.isFlag()){
                    button.setGraphic(new ImageView(OOPS));
                }else{
                    button.setDisable(false);
                    button.setGraphic(new ImageView(BLANK));
                }

            }
        }

        if (specialCommand instanceof String){
            String s = (String) specialCommand;
            if (s.equals("death")) {
                System.out.println("You lose");
                //System.exit(0);
                // different method for ending game...
            }else if (s.equals("winner")){
                System.out.println("You win!");
            }
        }else {
            game.isGameOver();
        }

    }

}