/**
 * A class representing mine blocks in Minesweeper.
 * @author brainuser5705
 */
public class MineSquare implements Square{

    /** in-game display state of square */
    private String display;
    /** if the square is reveal */
    private boolean reveal;
    /** if the square is flag */
    private boolean flag;
    private int row;
    private int col;

    /**
     * Constructs an unrevealed, unflagged blank mine block
     */
    public MineSquare(int row, int col){
        reveal = false;
        flag = false;
        display = " ";
        this.row = row;
        this.col = col;
    }

    /**
     * Returns true since it is a mine
     * @return true
     */
    public boolean isMine(){
        return true;
    }

    /**
     * Flags the square if not already flagged and not revealed
     * (changes display to mine)
     */
    public void reveal(){
        if (!flag) {
            reveal = true;
            display = "\033[1mm\033[0m";
        }
    }

    /**
     * Flags the block if not already flagged or not revealed
     * (changes display to flag)
     */
    public void flag(){
        if(!flag && !reveal){
            flag = true;
            display = "\033[95mF\033[0m";
        }
    }

    /**
     * Unflags the square if flagged
     * (changes display back to blank)
     */
    public void unflag(){
        if(flag){
            flag = false;
            display = " ";
        }

    }

    /**
     * Force reveals the mine
     * @return string of mine
     */
    public String trueReveal(){
        return "[\033[1mm\033[0m]";
    }

    @Override
    public int getValue() {
        return -1;
    }

    @Override
    public int getCol() {
        return col;
    }

    @Override
    public int getRow() {
        return row;
    }

    @Override
    public void setCol(int col) {
        this.col = col;
    }

    @Override
    public void setRow(int row) {
        this.row = row;
    }

    public boolean isFlagged(){
        return flag;
    }
    /**
     * Returns in-game display of square
     * @return string of mine's display state
     */
    public String toString(){
        return "[" + display + "]";
    }

}
