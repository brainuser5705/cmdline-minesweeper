import java.util.Scanner;

public class MinesweeperTest {

    public static void main(String[] args){
        System.out.println("\033[36mCommand-line Minesweeper\033[0m");
        System.out.println("Levels: \n 1 - Beginner (10 mines, 8X8) \n 2 - Intermediate (40 mines, 16X16) \n 3 - Expert (99 mines, 16X24)");
        System.out.println("For custom levels, input '4 - <mine> <# of rows> <# of cols>'");
        Scanner s = new Scanner(System.in);

        MinesweeperGame game = null;
        switch (s.nextInt()) {
            case 1 -> game = new MinesweeperGame(Level.BEGINNER);
            case 2 -> game = new MinesweeperGame(Level.INTERMEDIATE);
            case 3 -> game = new MinesweeperGame(Level.EXPERT);
            case 4 -> game = new MinesweeperGame(s.nextInt(), s.nextInt(), s.nextInt());
            default -> System.out.println("Invalid input. Try again.");
        }

        assert game != null;
        game.playMinesweeper();

    }

}
