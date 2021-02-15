public class Field {

    int numMines;
    int numRows;
    int numCols;
    Square[][] field;

    public Field(int mines, int rows, int cols){
        numMines = mines;
        numRows = rows;
        numCols = cols;
        field = new Square[rows][cols];
    }

    // can either make an empty Square field and cast in generateField()
    // or I can build it in generateField

    private void placeMines(){
        int count = 0;
        while(count != numMines){
            int r = (int) (Math.random() * numRows);
            int c = (int) (Math.random() * numCols);
            if (!(field[r][c] instanceof MineSquare)) {
                field[r][c] = new MineSquare(r, c);
                count++;
            }
        }
    }

    private void generateNumbers(){
        for (int r = 0; r < numRows; r++){
            for (int c = 0; c < numCols; c++){
                if (!(field[r][c] instanceof MineSquare)) {
                    int countMines = countSurroundingMines(r, c);
                    field[r][c] = new NumSquare(countMines, r, c);
                }
            }
        }
    }

    private int countSurroundingMines(int r, int c) {
        int count = 0;
        int[][] indexes = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 0}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};

        for (int[] pair : indexes) {
            try {
                if (field[r + pair[0]][c + pair[1]].isMine())
                    count++;
            } catch (Exception e) {
                //nothing
            }
        }
        return count;
    }

    public void generateField(){
        placeMines();
        generateNumbers();
    }

    public void printField(){
        for (Square[] r : field){
            for (Square s : r){
                System.out.print(s);
            }
            System.out.print("\n");
        }
    }

    public void revealField(){
        for (Square[] r : field){
            for (Square s : r){
                System.out.print(s.trueReveal());
            }
            System.out.print("\n");
        }
    }

    public Square getSquare(int row, int col){
        return field[row][col];
    }

}
