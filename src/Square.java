/**
 * An interface for different types of blocks in Minesweeper.
 * @author brainsuer5705
 */
public interface Square {

    /**
     * Returns if the square is a mine
     * @return true if the square is a mine, false if not
     */
    public boolean isMine();

    /**
     * Reveals the content of the square if square if not flagged
     */
    public void reveal();

    /**
     * Flags the square if not already flagged and not revealed
     */
    public void flag();

    /**
     * Unflags the square if flagged
     */
    public void unflag();

    /**
     * Force reveals the content of the square (for end game purposes)
     * @return string of square's content
     */
    public String trueReveal();

    /**
     * Returns the integer rank of the square
     * @return -1 if square is mine, otherwise its number value
     */
    public int getValue();

    public void setRow(int row);

    public void setCol(int col);

    public int getRow();

    public int getCol();

    /**
     * Returns in-game display of square (flagged, unflagged, or revealed)
     * @return string of square's display state
     */
    public String toString();

    //could also just display through an if else with toString and have a boolean value for isDisplay
}
