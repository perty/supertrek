package se.artcomputer.game;

import java.util.Random;

import static se.artcomputer.game.GameState.STOPPED;

public class Main {
    public static void main(String[] args) {
        long seed = 1337;
        Game game = new Game(new Random(seed));
        while (game.gameState != STOPPED) {
            game.run();
        }
    }
}