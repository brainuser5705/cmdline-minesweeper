package build;

public class MineBlock extends Block {

    public MineBlock(int row, int col){
        super("m", row, col);
    }

    public boolean isMine() {
        return true;
    }
}
