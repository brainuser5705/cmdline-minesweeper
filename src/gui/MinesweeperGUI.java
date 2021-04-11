package gui;

import build.Block;
import build.Level;
import build.NumBlock;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class MinesweeperGUI extends Application implements Observer<MinesweeperModel, String> {

    Image ONE = new Image(getClass().getResourceAsStream("resources/one.png"));
    Image TWO = new Image(getClass().getResourceAsStream("resources/two.png"));
    Image THREE = new Image(getClass().getResourceAsStream("resources/three.png"));
    Image FOUR = new Image(getClass().getResourceAsStream("resources/four.png"));
    Image FIVE = new Image(getClass().getResourceAsStream("resources/five.png"));
    Image SIX = new Image(getClass().getResourceAsStream("resources/six.png"));
    Image SEVEN = new Image(getClass().getResourceAsStream("resources/seven.png"));
    Image EIGHT = new Image(getClass().getResourceAsStream("resources/eight.png"));
    Image MINE = new Image(getClass().getResourceAsStream("resources/mine.png"));
    Image FLAG = new Image(getClass().getResourceAsStream("resources/flag.png"));
    Image BLANK = new Image(getClass().getResourceAsStream("resources/blank.png"));
    Image WIN = new Image(getClass().getResourceAsStream("resources/rose_win.png"));
    Image LOSE = new Image(getClass().getResourceAsStream("resources/rose_lose.png"));
    Image CLICK = new Image(getClass().getResourceAsStream("resources/rose_click.png"));
    Image PLAYING = new Image(getClass().getResourceAsStream("resources/rose_playing.png"));

    MinesweeperModel game;

    GridPane mineGrid;
    Label numMinesLabel = new Label();
    Button newGameButton = new Button();
    MenuBar menuBar;

    boolean isPlaying = true; // so button events doesn't execute at the last button release after game over

    int guiGameOverMode = 1; // the game over mode set in the GUI

    int numRows = 8;
    int numCols = 8;
    int numMines = 10;

    Stage mainStage;

    public static void main(String[] args){
        Application.launch( args );
    }

    @Override
    public void init(){
        isPlaying = true;
        game = new MinesweeperModel(numMines, numRows, numCols, guiGameOverMode);
        game.addObserver(this);
    }

    @Override
    public void start(Stage stage){

        mainStage = stage;
        mainStage.setResizable(false);

        VBox mainPane = new VBox();

        menuBar =  new MenuBar();
        Menu settings = new Menu("Settings");
        settings.getItems().addAll(makeGameOverMenu(), makeLevelMenu());
        menuBar.getMenus().addAll(settings);

        StackPane stackPane = new StackPane();
        numMinesLabel.setText(String.valueOf(game.getFlagsLeft()));
        numMinesLabel.setFont(new Font(20));
        numMinesLabel.setTranslateX( (double) (-numCols/2) * 38 + 30);
        newGameButton.setOnAction(e -> {
            refreshGrid();
        });
        newGameButton.setGraphic(new ImageView(PLAYING));
        stackPane.getChildren().addAll(numMinesLabel, newGameButton);

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
                            if (isPlaying) newGameButton.setGraphic(new ImageView(CLICK));
                        }
                );
                button.setOnMouseReleased(
                        mouseEvent -> {
                            if (isPlaying) newGameButton.setGraphic(new ImageView(PLAYING));
                        }
                );
                mineGrid.add(button, col, row);
            }
        }

        mainPane.getChildren().addAll(menuBar, stackPane, mineGrid);

        stage.setScene(new Scene(mainPane));
        stage.setTitle("Minesweeper");

        stage.show();

        // moving the numMinesLabel to the left corner
        numMinesLabel.setTranslateX(-(stage.getWidth() / 2) + 30);

    }

    private void refreshGrid(){
        init();
        start(mainStage);
    }

    private Menu makeGameOverMenu(){

        Menu gameOverMenu = new Menu("Game Over Mode");

        ToggleGroup toggleGroup = new ToggleGroup();
        RadioMenuItem gomOne = new RadioMenuItem("All blocks must be marked");
        gomOne.setOnAction(e -> {
            guiGameOverMode = 1;
            game.setGameOverMode(1);
        });
        gomOne.setToggleGroup(toggleGroup);
        RadioMenuItem gomTwo = new RadioMenuItem("Just flags needed");
        gomTwo.setOnAction(e -> {
            guiGameOverMode = 2;
            game.setGameOverMode(2);
        });
        gomTwo.setToggleGroup(toggleGroup);
        RadioMenuItem gomThree = new RadioMenuItem("No flags needed");
        gomThree.setOnAction(e -> {
            guiGameOverMode = 3;
            game.setGameOverMode(3);
        });
        gomThree.setToggleGroup(toggleGroup);

        switch(guiGameOverMode){
            case 1 -> gomOne.setSelected(true);
            case 2 -> gomTwo.setSelected(true);
            case 3 -> gomThree.setSelected(true);
        }

        gameOverMenu.getItems().addAll(gomOne, gomTwo, gomThree);

        return gameOverMenu;
    }

    private Menu makeLevelMenu(){

        Menu levelMenu = new Menu("Dimensions");

        MenuItem beginner = new MenuItem("Beginner (8x8, 10)");
        beginner.setOnAction(e ->{
            numRows = Level.BEGINNER.getNumRows();
            numCols = Level.BEGINNER.getNumCols();
            numMines = Level.BEGINNER.getNumMines();
            refreshGrid();
        });

        MenuItem intermediate = new MenuItem("Intermediate (16x16, 40)");
        intermediate.setOnAction(e ->{
            numRows = Level.INTERMEDIATE.getNumRows();
            numCols = Level.INTERMEDIATE.getNumCols();
            numMines = Level.INTERMEDIATE.getNumMines();
            refreshGrid();
        });

        MenuItem expert = new MenuItem("Expert (16x24, 99)");
        expert.setOnAction(e ->{
            numRows = Level.EXPERT.getNumRows();
            numCols = Level.EXPERT.getNumCols();
            numMines = Level.EXPERT.getNumMines();
            refreshGrid();
        });

        MenuItem custom = new MenuItem("Custom");
        custom.setOnAction(e ->{
            changeDimensions();
        });

        levelMenu.getItems().addAll(beginner, intermediate, expert, custom);

        return levelMenu;
    }

    private void changeDimensions(){
        Stage dimensionSettings = new Stage();
        dimensionSettings.setWidth(200);

        VBox inputFields = new VBox();

        Label message = new Label("Change dimensions");

        Label row = new Label("Number of rows: ");
        TextField rowInput = new TextField();
        rowInput.setMaxWidth(50);
        Label col = new Label("Number of columns: ");
        TextField colInput = new TextField();
        colInput.setMaxWidth(50);
        Label mine = new Label("Number of mines: ");
        TextField mineInput = new TextField();
        mineInput.setMaxWidth(50);

        Button update = new Button("Update");
        update.setOnAction(e -> {
            if (rowInput.getCharacters().toString().matches("^[1-9][0-9]*") &&
                    colInput.getCharacters().toString().matches("^[1-9][0-9]*") &&
                    mineInput.getCharacters().toString().matches("^[1-9][0-9]*")) {
                numRows = Integer.parseInt(rowInput.getCharacters().toString());
                numCols = Integer.parseInt(colInput.getCharacters().toString());
                numMines = Integer.parseInt(mineInput.getCharacters().toString());
                refreshGrid();
                dimensionSettings.close();
            }else{
                message.setTextFill(Color.RED);
                message.setText("Please set positive integer values.");
            }

        });

        inputFields.getChildren().addAll(message, row, rowInput, col, colInput, mine, mineInput, update);

        dimensionSettings.setScene(new Scene(inputFields));
        dimensionSettings.show();
    }

    @Override
    public void update(MinesweeperModel minesweeperModel, String specialCommand){

        numMinesLabel.setText(String.valueOf(game.getFlagsLeft()));

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
                                    default -> throw new IllegalStateException("Unexpected value: " + num);
                                }
                        ));
                    }else{
                        button.setGraphic(new ImageView(MINE));
                    }
                }else if (block.isFlag()){
                    button.setGraphic(new ImageView(FLAG));
                }else{
                    // for when flags are unflagged
                    button.setGraphic(new ImageView(BLANK));
                }

            }
        }

        if (specialCommand != null){
            isPlaying = false;
            if (specialCommand.equals("loser")) {
                newGameButton.setGraphic(new ImageView(LOSE));
            }else if (specialCommand.equals("winner")){
                newGameButton.setGraphic(new ImageView(WIN));
            }
        }else {
            game.isGameOver();
        }

    }

}