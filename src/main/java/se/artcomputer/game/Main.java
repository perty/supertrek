package se.artcomputer.game;

import java.util.Random;
import java.util.Scanner;

import static se.artcomputer.game.GameState.STOPPED;

public class Main {
    public static void main(String[] args) {
        long seed = 471108;
        GameRandom random = new GameStaticImpl(new int[]
                {5, 1, 7, 2, 0, 6, 1, 0, 7, 2, 6, 1, 6, 1, 5, 5, 4},
                new float[]
                        {0.5f, 0.9f, 0.8f, 0.7f, 0.01f, 0.3f, 0.7f, 0.2f}
        );
        Game game = new Game(new GameInputImpl(), random);
        while (game.gameState != STOPPED) {
            game.step();
        }
    }

    private static class GameInputImpl implements GameInput {
        private final Scanner scanner = new Scanner(System.in);

        @Override
        public String nextLine() {
            return scanner.nextLine();
        }
    }

    private static class GameRandomImpl implements GameRandom {
        private final Random random;

        private GameRandomImpl(long seed) {
            this.random = new Random(seed);
        }

        @Override
        public int nextInt(int max) {
            return random.nextInt(max);
        }

        @Override
        public float nextFloat() {
            return random.nextFloat();
        }
    }
}