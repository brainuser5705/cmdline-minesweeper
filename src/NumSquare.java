import java.util.HashMap;

public class NumSquare implements Square{

    int num;
    String display;
    boolean reveal;
    boolean flag;
    int row;
    int col;

    HashMap<Integer, String> COLORS = new HashMap<Integer, String>(){{
        put(0, "37"); //black
        put(1, "34"); // blue
        put(2, "32"); //green
        put(3, "31"); //red
        put(4, "94"); //dark (rn light) blue
        put(5, "33"); //brown (rn yellow)
        put(6, "36"); //cyan
        put(7, "30"); //black (rn dark gray) - might need to change for dark cmd line
        put(8, "90"); //grey (light)
    }};

    public NumSquare(int num, int row, int col){
        this.num = num;
        display = " ";
        reveal = false;
        flag = false;
        this.row = row;
        this.col = col;
    }

    public boolean isMine(){
        return false;
    }

    public void reveal(){
        if (!flag) {
            reveal = true;
            display = "\033[" + COLORS.get(num) + "m" + num + "\033[0m";
        }
    }

    public void flag(){
        if (!flag && !reveal) { // reveal and not 0
            flag = true;
            display = "\033[35mF\033[m"; // figure out how to put multiple colors
        }
    }

    public int getValue(){
        return num;
    }

    public void unflag(){
        if (flag){
            flag = false;
            display = " ";
        }
    }

    public String trueReveal(){
        return "[" + "\033[" + COLORS.get(num) + "m" + num + "\033[0m" + "]";
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

    public String toString(){
        return "[" + display + "]";
    }

    // boolean return so it knows when to print error message, or learn how to use assert or some other thing
}
