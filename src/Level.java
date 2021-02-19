public enum Level {

    BEGINNER (10, 8, 8),
    INTERMEDIATE(40, 16, 16),
    EXPERT(99, 16, 30);

    private final Field game;
    private final int numMines;

    Level(int mines, int rows, int cols){
        game = new Field(mines, rows, cols);
        numMines = mines;
    }

    public Field getGame(){
        return game;
    }

    public int getNumMines(){
        return numMines;
    }
}
