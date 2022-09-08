package se.artcomputer.game;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static se.artcomputer.game.GameState.RUNNING;

class GameTest {

    @Test
    void run() {
        GameInputImpl scanner = new GameInputImpl();
        Game game = new Game(scanner, new Random(1337));
        game.step();
        assertEquals(RUNNING, game.gameState);
        scanner.setLines("NAV", "2", "1");
        game.step();
    }

    private static class GameInputImpl implements GameInput {
        private Queue<String> line = new LinkedList<>();

        @Override
        public String nextLine() {
            if (line.isEmpty()) {
                return "";
            }
            return line.poll();
        }

        public void setLines(String... lines) {
            this.line = new LinkedList<>(List.of(lines));
        }
    }

}