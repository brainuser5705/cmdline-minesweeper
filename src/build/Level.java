package build;

public enum Level {

    BEGINNER (10, 8, 8),
    INTERMEDIATE(40, 16, 16),
    EXPERT(99, 16, 30);

    private final Field game;
    private final int numMines;
    private final int numRows;
    private final int numCols;

    Level(int mines, int rows, int cols){
        game = new Field(mines, rows, cols);
        numMines = mines;
        numRows = rows;
        numCols = cols;
    }

    public Field getGame(){
        return game;
    }

    public int getNumMines(){
        return numMines;
    }

    public int getNumRows() { return numRows; }

    public int getNumCols() { return numCols; }
}
