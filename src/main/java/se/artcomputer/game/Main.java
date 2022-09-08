package se.artcomputer.game;

import java.util.Random;
import java.util.Scanner;

import static se.artcomputer.game.GameState.STOPPED;

public class Main {
    public static void main(String[] args) {
        long seed = 123456789;
        Game game = new Game(new GameInputImpl(), new Random(seed));
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

}