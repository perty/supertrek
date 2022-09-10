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
import static se.artcomputer.game.Condition.RED;
import static se.artcomputer.game.GameState.RUNNING;
import static se.artcomputer.game.GameState.STOPPED;

class GameTest {

    private GameInputImpl scanner;
    private Game game;

    @BeforeEach
    void setUp() {
        scanner = new GameInputImpl();
    }

    /**
     * Here we get a bad start with damages, so we limp to the base and request reparation.
     */
    @Test
    void scenario1337() {
        game = new Game(scanner, new Random(1337));
        command("");
        assertEquals(RUNNING, game.gameState);
        int klingons = game.totalKlingons();
        command("NAV", "2", "1");
        command("COM", "2");
        command("TOR", "4");
        assertEquals(klingons - 1, game.totalKlingons());
        Position position = game.currentSector();
        command("NAV", "2", "1");
        assertEquals(position, game.currentSector());
        for (int i = 0; i < 8; i++) {
            command("NAV", "4", "0.2");
        }
        assertEquals(new Position(3, 6), game.currentQuadrant());
        command("NAV", "3", "0.2");
        command("NAV", "3", "0.2");
        command("NAV", "5", "0.1");
        assertEquals(DOCKED, game.condition());
        command("DAM", "Y");
        command("SRS");
    }

    /**
     * We find a Klingon and use phasers to destroy it, succeeding at second attempt.
     * Our shields take some damage when the enemy fires back at us.
     */
    @Test
    void scenario123456789() {
        game = new Game(scanner, new Random(123456789));
        command("");
        command("LRS");
        command("NAV", "3", "1");
        command("SHE", "1000");
        int klingons = game.totalKlingons();
        command("PHA", "100");
        float shields = game.shields();
        assertTrue(shields < 1000);
        command("PHA", "100");
        assertEquals(klingons - 1, game.totalKlingons());
    }

    /**
     * We start at far right middle quadrant: 4,8. Roam around the galaxy.
     */
    @Test
    void scenario471108() {
        game = new Game(scanner, new Random(471108));
        command("");
        assertEquals(new Position(4, 8), game.currentQuadrant());
        command("SHE", "1000");
        command("LRS");
        command("COM", "0");
        command("NAV", "5", "3");
        assertEquals(new Position(4, 5), game.currentQuadrant());
        command("LRS");
        command("NAV", "5", "3");
        assertEquals(new Position(4, 2), game.currentQuadrant());
        command("LRS");
        command("NAV", "3", "2");
        assertEquals(new Position(2, 2), game.currentQuadrant());
        command("LRS");
        command("NAV", "3", "0.2");
        command("NAV", "1", "3");
        assertEquals(new Position(2, 5), game.currentQuadrant());
        command("LRS");
        command("NAV", "7", "0.2");
        command("NAV", "8", "0.2");
        command("NAV", "7", "0.5");
        command("NAV", "9", "2");
        assertEquals(new Position(2, 7), game.currentQuadrant());
        command("LRS");
        command("PHA", "2000", "1500"); // Too much
        command("NAV", "7", "5");
        assertEquals(new Position(7, 7), game.currentQuadrant());
        command("LRS");
        command("NAV", "5", "3");
        assertEquals(new Position(7, 4), game.currentQuadrant());
        command("LRS");
        command("NAV", "5", "2");
        assertEquals(new Position(7, 2), game.currentQuadrant());
        command("LRS");
        command("COM", "0");
        command("COM", "1");
        command("NAV", "5", "1");
        assertEquals(new Position(7, 1), game.currentQuadrant());
        command("NAV", "3", "0.1");
        command("NAV", "1", "4");
        assertEquals(new Position(7, 5), game.currentQuadrant());
        command("NAV", "2", "0.5");
        command("NAV", "3", "0.2");
        command("TOR", "5", "no"); // Starbase blows up
        assertEquals(STOPPED, game.gameState);
    }

    @Test
    void scenario666() {
        game = new Game(scanner, new Random(666));
        command("");
        assertEquals(3000, game.totalEnergy());
        command("LRS");
        command("NAV", "5", "1");
        assertEquals(RED, game.condition());
        command("NAV", "4", "0.1", "AYE"); // Die and restart'
        command("");
        assertEquals(0, game.shields(), "Restore shields");
        assertEquals(3000, game.totalEnergy(), "Restore energy");
    }

    private void command(String... command) {
        scanner.setLines(command);
        game.step();
    }

    private static class GameInputImpl implements GameInput {
        private Queue<String> line = new LinkedList<>();

        @Override
        public String nextLine() {
            if (line.isEmpty()) {
                throw new RuntimeException("Empty buffer.");
            }
            return line.poll();
        }

        public void setLines(String... lines) {
            this.line = new LinkedList<>(List.of(lines));
        }
    }

}