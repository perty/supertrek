package se.artcomputer.game;

import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static se.artcomputer.game.Condition.DOCKED;
import static se.artcomputer.game.GameState.RUNNING;

class GameTest {

    @Test
    void run() {
        GameInputImpl scanner = new GameInputImpl();
        Game game = new Game(scanner, new Random(1337));
        game.step();
        assertEquals(RUNNING, game.gameState);
        int klingons = game.totalKlingons();
        scanner.setLines("NAV", "2", "1");
        game.step();
        scanner.setLines("DAM");
        game.step();
        scanner.setLines("SHE", "1000");
        game.step();
        scanner.setLines("TOR", "4");
        game.step();
        assertEquals(klingons - 1, game.totalKlingons());
        scanner.setLines("LRS");
        game.step();
        Position position = game.currentSector();
        scanner.setLines("NAV", "2", "1");
        game.step();
        assertEquals(position, game.currentSector());
        for (int i = 0; i < 8; i++) {
            scanner.setLines("NAV", "4", "0.2");
            game.step();
        }
        assertEquals(new Position(3,6), game.currentQuadrant());
        scanner.setLines("NAV", "3", "0.2");
        game.step();
        scanner.setLines("NAV", "3", "0.2");
        game.step();
        scanner.setLines("NAV", "5", "0.1");
        game.step();
        assertEquals(DOCKED, game.condition());
        scanner.setLines("DAM", "Y");
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