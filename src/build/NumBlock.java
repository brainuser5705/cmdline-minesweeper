package build;

public class NumBlock extends Block {

    public NumBlock(int value, int row, int col){
        super(String.valueOf(value), row, col);
    }

    public boolean isMine() {
        return false;
    }

}
