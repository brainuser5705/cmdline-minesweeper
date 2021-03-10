package build;

public class NumBlock extends Block {

    private int value;

    public NumBlock(int value, int row, int col){
        super(String.valueOf(value), row, col);
        this.value = value;
    }

    public boolean isMine() {
        return false;
    }

    public int getNum(){
        return value;
    }

}
