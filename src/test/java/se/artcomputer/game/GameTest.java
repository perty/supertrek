package se.artcomputer.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static se.artcomputer.game.Condition.DOCKED;
import static se.artcomputer.game.GameState.RUNNING;

class GameTest {

    private GameInputImpl scanner;

    @BeforeEach
    void setUp() {
        scanner = new GameInputImpl();
    }

    /**
     * Here we get a bad start with damages, so we limp to the base and request reparation.
     */
    @Test
    void scenario1337() {
        Game game = new Game(scanner, new Random(1337));
        game.step();
        assertEquals(RUNNING, game.gameState);
        int klingons = game.totalKlingons();
        command(game,"NAV", "2", "1");
        command(game, "TOR", "4");
        assertEquals(klingons - 1, game.totalKlingons());
        Position position = game.currentSector();
        command(game,"NAV", "2", "1");
        assertEquals(position, game.currentSector());
        for (int i = 0; i < 8; i++) {
            command(game,"NAV", "4", "0.2");
        }
        assertEquals(new Position(3,6), game.currentQuadrant());
        command(game,"NAV", "3", "0.2");
        command(game,"NAV", "3", "0.2");
        command(game,"NAV", "5", "0.1");
        assertEquals(DOCKED, game.condition());
        command(game, "DAM", "Y");
        command(game,"SRS");
    }

    /**
     * We find a Klingon and use phasers to destroy it, succeeding at second attempt.
     * Our shields take some damage when the enemy fires back at us.
     */
    @Test
    void scenario123456789() {
        Game game = new Game(scanner, new Random(123456789));
        game.step();
        command(game,"LRS");
        command(game,"NAV", "3", "1");
        command(game, "SHE", "1000");
        int klingons = game.totalKlingons();
        command(game, "PHA", "100");
        float shields = game.shields();
        assertTrue(shields < 1000);
        command(game, "PHA", "100");
        assertEquals(klingons - 1, game.totalKlingons());
    }

    private void command(Game game, String... command) {
        scanner.setLines(command);
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