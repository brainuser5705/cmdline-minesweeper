public class MinesweeperTest {

    public static void main(String[] args){
        MinesweeperGame game = new MinesweeperGame(10, 8, 8);
        System.out.println("\033[36mCommand-line Minesweeper\033[0m");
        game.playMinesweeper();

        //options for different levels - enum type: beginner, intermediate, expert, custom which would use user input
        // minesweeper game would be enum type
        // commands: reveal entire rows or from groups of connected blocks
        // color: highlight revealed blocks etc. - dynamic coloring
        // admin commands : correct answer ex, boolean equations  check block is flag or something

    }

}
