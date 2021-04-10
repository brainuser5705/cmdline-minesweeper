package build;

import game.MinesweeperGame;

import java.util.concurrent.Callable;

public interface GameOver extends Callable<Boolean>{

    class GameOverModeOne implements GameOver{

        MinesweeperGame game;

        // can i use consumer instead of making a constructor?
        public GameOverModeOne(MinesweeperGame game){
            this.game = game;
        }

        // is there another runnable class that can accept parameters instead of making a constructor to assign variables
        @Override
        public Boolean call() {
            Block[][] field = game.getField();
            for (Block[] row : field){
                for (Block block : row){
                    if ((block.isMine() && !block.isFlag()) || (!block.isMine() && !block.isReveal())){ // not working?
                        return false;
                    }
                }
            }
            return true;
        }

    }

    class GameOverModeTwo implements GameOver{

        MinesweeperGame game;

        // can i use consumer instead of making a constructor?
        public GameOverModeTwo(MinesweeperGame game){
            this.game = game;
        }

        // is there another runnable class that can accept parameters instead of making a constructor to assign variables
        @Override
        public Boolean call() {
            if (game.getFlagsLeft() == 0 && areAllMinesFlagged()) {
                return true;
            }
            return false;
        }

        // util class for gameover mode 2
        private boolean areAllMinesFlagged(){
            MineBlock[] mineCoords = game.getMineCoords();
            for (MineBlock mine : mineCoords){
                if (!mine.isFlag()) {
                    return false;
                }
            }
            return true;
        }

    }

    class GameOverModeThree implements GameOver{

        MinesweeperGame game;

        // can i use consumer instead of making a constructor?
        public GameOverModeThree(MinesweeperGame game){
            this.game = game;
        }

        // is there another runnable class that can accept parameters instead of making a constructor to assign variables
        @Override
        public Boolean call() {
            Block[][] field = game.getField();
            for (Block[] row : field){
                for (Block block : row){
                    if (!block.isMine() && !block.isReveal()) return false;
                }
            }
            game.setFlagsLeft(0);
            return true;
        }

    }

    enum Mode {

        GAME_OVER_ONE(new GameOver.GameOverModeOne(MinesweeperGame.getInstance())),
        GAME_OVER_TWO(new GameOver.GameOverModeTwo(MinesweeperGame.getInstance())),
        GAME_OVER_THREE(new GameOver.GameOverModeThree(MinesweeperGame.getInstance()));

        Callable<Boolean> checkMethod;

        Mode(Callable<Boolean> checkMethod) {
            this.checkMethod = checkMethod;
        }

        public boolean check() {
            boolean value = false;
            try{
                value = checkMethod.call();
            }catch (Exception e){
                System.out.println("Error occur in game over mode");
            }
            return value;
        }

    }

}
