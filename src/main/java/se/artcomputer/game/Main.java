package se.artcomputer.game;

import java.util.Random;
import java.util.Scanner;

import static se.artcomputer.game.GameState.STOPPED;

public class Main {
    public static void main(String[] args) {
        long seed = 471108;
        Game game = new Game(new GameInputImpl(), new GameRandomImpl(seed));
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