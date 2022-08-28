package se.artcomputer.game;

import static se.artcomputer.game.GameState.STOPPED;

public class Main {
    public static void main(String[] args) {
        Game game = new Game();
        while (game.gameState != STOPPED) {
            game.run();
        }
    }
}