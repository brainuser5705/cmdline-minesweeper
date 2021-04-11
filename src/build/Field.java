package build;

public class Field {

    private final int numMines;
    private final int numRows;
    private final int numCols;
    private final Block[][] field;
    private final MineBlock[] mineCoords;

    public Field(int mines, int rows, int cols){
        numMines = mines;
        numRows = rows;
        numCols = cols;
        field = new Block[rows][cols];
        mineCoords = new MineBlock[mines]; // coordinates of mines

        generateField();
    }

    // can either make an empty Square field and cast in generateField()
    // or I can build it in generateField

    private void placeMines(){
        int count = 0;
        while(count != numMines){
            int r = (int) (Math.random() * numRows);
            int c = (int) (Math.random() * numCols);
            if (!(field[r][c] instanceof MineBlock)) { // not already a mine
                field[r][c] = new MineBlock(r, c);
                mineCoords[count] = (MineBlock) getBlock(r,c); // reference issue?
                count++;
            }
        }
    }

    /*
    Instead of doing this, you can just take the mine coords and get the surrounding numbers from there instead of looping
     */

//    private void generateNumbers2(){
//        int[][] indexes = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 0}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};
//        for (MineBlock mine : mineCoords){
//            for (int[] pair : indexes) {
//                try {
//                    Block adjacentSquare = getBlock(mine.getRow()+pair[0], mine.getCol()+pair[1]);
//                    adjacentSquare.
//                } catch (Exception e) {
//                    //nothing
//                }
//            }
//        }
//    }

    private void generateNumbers(){
        for (int r = 0; r < numRows; r++){
            for (int c = 0; c < numCols; c++){
                if (!(field[r][c] instanceof MineBlock)) {
                    int countMines = countSurroundingMines(r, c);
                    field[r][c] = new NumBlock(countMines, r, c);
                }
            }
        }
    }

    // util method for generateNumbers
    private int countSurroundingMines(int r, int c) {
        int count = 0;
        int[][] indexes = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 0}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};

        for (int[] pair : indexes) {
            if (isValidCoord(r + pair[0], c + pair[1])){
                if (field[r + pair[0]][c + pair[1]] != null && field[r + pair[0]][c + pair[1]].isMine())
                    count++;
            }
        }
        return count;
    }

    private void generateField(){
        placeMines();
        generateNumbers();
    }

    public void printField(){

        System.out.print(getSpaces(0, false) + " "); // space before columns start
        for (int col = 0; col < numCols; col++){
            System.out.print(getSpaces(col,true) + col);
        }
        System.out.println();

        // prints the row number and field
        int row = 0;
        for (Block[] r : field){
            System.out.print(getSpaces(row, false) + row);
            row++;
            for (Block s : r){
                System.out.print(s);
            }
            System.out.print("\n");
        }

    }

    public void revealField(){
        for (Block[] r : field){
            for (Block s : r){
                s.forceReveal();
            }
        }
    }

    public Block getBlock(int row, int col){
        return field[row][col];
    }

    public MineBlock[] getMineCoords(){ return mineCoords; }

    public Block[][] getField(){
        return field;
    }

    public int getNumRows(){
        return numRows;
    }

    public int getNumCols(){
        return numCols;
    }

    public int getNumMines() { return numMines; }

    private String getSpaces(int num, boolean isCol){ // if isCol, it counts the space needed for column
        assert numRows > num;
        int numDigits = 0;
        int maxDigits = 0;

        if (num == 0){ // based on the algorithm, zero is a special case
            numDigits = 1;
        }else{
            while (num > 0){
                num /= 10;
                numDigits++;
            }
        }

        if (isCol){
            maxDigits = 3; // max 3 spaces for columns (for now), might need to code some dynamic spacing
        }else{
            int rows = numRows;
            while (rows > 0){
                rows /= 10;
                maxDigits++;
            }
        }

        String spaceString = "";
        for (int i = 0; i < maxDigits - numDigits; i++){
            spaceString += "\s";
        }

        return spaceString;
    }

    public boolean isValidCoord(int row, int col){
        return 0 <= row && row <= getNumRows() - 1 && 0 <= col && col <= getNumCols() - 1;
    }


}
