package build;

public abstract class Block {

    private String display;
    private final String content;
    private final int row;
    private final int col;

    private boolean isReveal;
    private boolean isFlag;

    public Block(String content, int row, int col){
        this.content = content;
        this.row = row;
        this.col = col;
        display = " ";
        isReveal = false;
        isFlag = false;
    }

    public abstract boolean isMine();

    public void reveal(){
        if (!isFlag){
            isReveal = true;
            display = content;
        }
    }

    public void flag(){
        if (!isFlag && !isReveal){
            isFlag = true;
            display = "F";
        }
    }

    public void unflag(){
        if (isFlag){
            isFlag = false;
            display = " ";
        }
    }

    public boolean isFlag(){
        return isFlag;
    }

    public boolean isReveal() { return isReveal; }

    public int getRow(){
        return row;
    }

    public int getCol(){
        return col;
    }

    public String forceReveal(){
        isReveal = true;
        return "[" + content + "]";
    }

    public void forceUnreveal() {
        isFlag = false;
        isReveal = false;
        display = " ";
    }

    public String toString(){ // display
        return "[" + display + "]";
    }

    public boolean isBlankBlock(){
        return content.equals("0");
    }


}
