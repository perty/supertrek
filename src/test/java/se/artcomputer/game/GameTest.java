package se.artcomputer.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static se.artcomputer.game.Condition.*;
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
        game = new Game(scanner, new GameRandomImpl(1337));
        command("");
        assertEquals(RUNNING, game.gameState);
        int klingons = game.totalKlingons();
        command("NAV", "2", "1");
        command("?"); // Invoke help
        command("COM", "9"); // Invoke help
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
        game = new Game(scanner, new GameRandomImpl(123456789L));
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
     * Roam the galaxy until we run out of energy.
     */
    @Test
    void roaming() {
        GameRandom random = new GameStaticImpl(new int[]
                {5, 1, 7, 2, 0, 6, 1, 0, 7, 2, 6, 1, 6, 1, 5, 5, 4},
                new float[]
                        {0.5f, 0.9f, 0.8f, 0.7f, 0.01f, 0.3f, 0.7f, 0.2f}
        );
        game = new Game(scanner, random);
        command("");
        command("LRS");
        command("SHE", "1000");
        int klingons1 = game.totalKlingons();
        command("PHA", "9999", "1000");
        assertEquals(klingons1 - 1, game.totalKlingons());
        command("COM", "0");
        command("NAV", "3", "3");
        command("LRS");
        command("NAV", "3", "1");
        assertEquals(new Position(2, 2), game.currentQuadrant());
        command("LRS");
        command("NAV", "1", "3");
        assertEquals(new Position(2, 5), game.currentQuadrant());
        command("LRS");
        int klingons2 = game.totalKlingons();
        command("COM", "2");
        command("TOR", "4");
        command("COM", "2");
        assertEquals(klingons2 - 1, game.totalKlingons());
        command("NAV", "1", "2");
        assertEquals(new Position(2, 7), game.currentQuadrant());
        command("LRS");
        command("NAV", "7", "3");
        assertEquals(new Position(5, 7), game.currentQuadrant());
        command("LRS");
        command("NAV", "7", "2");
        assertEquals(new Position(7, 7), game.currentQuadrant());
        command("LRS");
        command("NAV", "5", "3");
        assertEquals(new Position(7, 4), game.currentQuadrant());
        command("LRS");
        command("COM"); // Computer has randomly broken down
        command("NAV", "4", "1");
        command("NAV", "5", "1");
        assertEquals(new Position(6, 2), game.currentQuadrant());
        command("NAV", "4", "0.1");
        assertEquals(DOCKED, game.condition());
        command("DAM", "Y");
        command("COM", "0");
        command("NAV", "7", "1");
        command("LRS");
        command("NAV", "5", "1");
        command("LRS");
        command("SRS");
        command("PHA", "2000"); // Spend lots of energy
        command("NAV", "7", "1");
        command("SHE", "900");
        command("TOR", "2");
        command("NAV", "2", "1");
        assertEquals(YELLOW, game.condition());
        command("NAV", "6", "1");
        command("COM", "4", "8", "1", "5", "4");
        command("NAV", "1", "0.2");
        command("NAV", "2", "3");
        assertEquals(new Position(5, 4), game.currentQuadrant());
        command("LRS");
        command("NAV", "1", "1");
        assertEquals(new Position(5, 5), game.currentQuadrant());
        command("NAV", "1", "1");
        assertEquals(new Position(5, 6), game.currentQuadrant());
        command("NAV", "1", "1");
        assertEquals(new Position(5, 7), game.currentQuadrant());
        command("NAV", "5", "1");
        assertEquals(new Position(5, 6), game.currentQuadrant());
        command("NAV", "5", "1");
        assertEquals(new Position(5, 5), game.currentQuadrant());
        command("SHE", "0");
        command("PHA", "700");
        command("NAV", "1", "1");
        assertEquals(new Position(5, 6), game.currentQuadrant());
        command("NAV", "1", "1");
        assertEquals(new Position(5, 7), game.currentQuadrant());
        command("AYE");  // We have spent all energy.
        command("LRS");
    }

    /**
     * We start at far right middle quadrant: 4,8. Roam around the galaxy.
     */
    @Test
    void scenario471108() {
        game = new Game(scanner, new GameRandomImpl(471108));
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
        command("COM", "5");
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

    /**
     * We do a catastrophic start and lose. Try again.
     */
    @Test
    void scenario666() {
        GameRandom random = new GameStaticImpl(new int[]
                {5, 1, 7, 2, 0, 6, 1, 0, 7, 2, 6, 1, 6, 1, 5, 5, 4},
                new float[]
                        {0.69F, 0.449F, 0.123F, 0.77F, 0.3F, 0.99F, 0.75F, 0.019F, 0.63F, 0.315F, 0.38F, 0.53F, 0.096F, 0.89F, 0.29F, 0.97F, 0.79F, 0.44F, 0.44F, 0.19F, 0.49F, 0.44F, 0.22F, 0.79F, 0.53F, 0.37F, 0.42F, 0.17F, 0.85F, 0.186F, 0.398F, 0.896F, 0.1039F, 0.707F, 0.211F, 0.077F, 0.438F, 0.655F, 0.129F, 0.672F, 0.174F, 0.289F, 0.80F, 0.F, 0.036F, 0.43F, 0.54F, 0.51F, 0.802F, 0.189F, 0.843F, 0.272F, 0.55F, 0.2F, 0.333F, 0.067F, 0.324F, 0.396F, 0.311F, 0.69F, 0.252F, 0.8F, 0.43F, 0.195F, 0.82F, 0.328F, 0.199F, 0.120F, 0.261F, 0.451F, 0.5F, 0.498F, 0.01212F, 0.57F, 0.61F, 0.522F, 0.808F, 0.284F, 0.513F, 0.074F, 0.92F, 0.62F, 0.903F, 0.785F, 0.692F, 0.211F, 0.203F, 0.76F, 0.16F, 0.84F, 0.61F, 0.0701F, 0.62F, 0.903F, 0.62F, 0.479F, 0.209F, 0.7F, 0.143F, 0.698F, 0.8F, 0.525F, 0.97F, 0.234F, 0.0832F, 0.960F, 0.65F, 0.42F, 0.121F, 0.458F, 0.44F, 0.355F, 0.443F, 0.38F, 0.80F, 0.147F, 0.0554F, 0.464F, 0.45F, 0.96F, 0.51F, 0.64F, 0.73F, 0.6F, 0.300F, 0.F, 0.289F, 0.F, 0.195F, 0.31F, 0.47F, 0.7F, 0.271F, 0.296F, 0.574F, 0.994F, 0.86F, 0.82F, 0.00151F, 0.66F, 0.518F, 0.52F, 0.14F, 0.58F, 0.65F, 0.233F, 0.73F, 0.251F, 0.216F, 0.047F, 0.463F, 0.933F, 0.304F, 0.045F, 0.736F, 0.904F, 0.715F, 0.153F, 0.88F, 0.748F, 0.59F, 0.336F, 0.53F, 0.389F, 0.56F, 0.99F, 0.85F, 0.52F, 0.104F, 0.1018F, 0.57F, 0.155F, 0.59F, 0.164F, 0.475F, 0.114F, 0.922F, 0.169F, 0.059F, 0.134F, 0.756F, 0.503F, 0.914F, 0.94F, 0.60F, 0.515F, 0.472F, 0.81F, 0.177F, 0.12F, 0.296F, 0.228F, 0.316F, 0.68F, 0.839F, 0.62F, 0.86F, 0.972F, 0.206F, 0.78F, 0.709F, 0.141F, 0.0360F, 0.83F, 0.28F, 0.249F, 0.178F, 0.197F, 0.35F, 0.210F, 0.278F, 0.390F, 0.73F, 0.386F, 0.161F, 0.721F, 0.0988F, 0.239F, 0.82F, 0.292F, 0.628F, 0.307F, 0.52F, 0.188F, 0.213F, 0.56F, 0.0440F, 0.79F, 0.350F, 0.834F, 0.392F, 0.658F, 0.0286F, 0.547F, 0.97F, 0.691F, 0.736F, 0.818F, 0.65F, 0.439F, 0.716F, 0.938F, 0.379F, 0.655F, 0.88F, 0.74F, 0.64F, 0.7F, 0.388F, 0.59F, 0.80F, 0.584F, 0.639F, 0.5F, 0.381F, 0.58F, 0.168F, 0.756F, 0.490F, 0.392F, 0.525F, 0.074F, 0.30F, 0.88F, 0.87F, 0.86F, 0.27F, 0.7687722F}
        );
        game = new Game(scanner, random);
        command("");
        assertEquals(3000, game.totalEnergy());
        command("LRS");
        command("NAV", "0", "1");  // Incorrect
        command("NAV", "5", "1");
        assertEquals(RED, game.condition());
        command("NAV", "4", "0.1", "AYE"); // Die and restart'
        command("");
        assertEquals(0, game.shields(), "Restore shields");
        assertEquals(10, game.torpedoes(), "Restore torpedoes");
        assertEquals(3000, game.totalEnergy(), "Restore energy");
    }

    /**
     *  Give up
     */
    @Test
    void resign() {
        game = new Game(scanner, new GameRandomImpl(1337));
        command("");
        command("XXX", "no");
    }

    /**
     * We hunt down the last Klingon and win!
     */
    @Test
    void ftw() {
        GameRandom random = new GameStaticImpl(new int[]
                {5, 1, 7, 2, 0, 6, 1, 0, 7, 2, 6, 1, 6, 1, 5, 5, 4},
                new float[]
                        {0.5f, 0.9f, 0.8f, 0.7f, 0.01f, 0.3f, 0.7f, 0.2f}
        );
        game = new Game(scanner, random);
        command("");
        command("LRS");
        command("COM", "2");
        command("COM", "1");
        command("COM", "3");
        command("COM", "4", "8", "3", "1", "7");
        command("TOR", "2.34");
        command("NAV", "7", "1");
        command("LRS");
        command("NAV", "6", "1");
        assertEquals(new Position(8, 1), game.currentQuadrant());
        command("LRS");
        command("SHE", "500");
        command("PHA", "320");
        command("NAV", "3", "1");
        assertEquals(new Position(7, 1), game.currentQuadrant());
        command("LRS");
        command("PHA", "320");
        command("NAV", "3", "1");
        assertEquals(new Position(6, 1), game.currentQuadrant());
        command("LRS");
        command("PHA", "320");
        command("PHA", "100");
        command("NAV", "3", "1");
        assertEquals(new Position(5, 1), game.currentQuadrant());
        command("LRS");
        command("PHA", "320");
        command("PHA", "100");
        command("NAV", "3", "1");
        assertEquals(new Position(4, 1), game.currentQuadrant());
        command("LRS");
        command("PHA", "320");
        command("NAV", "3", "1");
        assertEquals(new Position(3, 1), game.currentQuadrant());
        command("LRS");
        command("PHA", "320");
        command("NAV", "8", "1");
        assertEquals(new Position(4, 2), game.currentQuadrant());
        command("LRS");
        command("NAV", "7", "2");
        assertEquals(new Position(6, 2), game.currentQuadrant());
        command("LRS");
        command("NAV", "2", "0.6");
        assertEquals(DOCKED, game.condition());
        command("DAM", "Y");
        command("COM", "0");
        command("NAV", "3", "4");
        assertEquals(new Position(2, 2), game.currentQuadrant());
        command("LRS");
        command("NAV", "4", "1");
        assertEquals(new Position(1, 1), game.currentQuadrant());
        command("TOR", "6");
        command("NAV", "8.5", "3");
        assertEquals(new Position(2, 4), game.currentQuadrant());
        command("LRS");
        command("NAV", "2", "1");
        assertEquals(new Position(1, 5), game.currentQuadrant());
        command("LRS");
        command("SHE", "500");
        command("PHA", "500");
        command("NAV", "7", "1");
        assertEquals(new Position(2, 5), game.currentQuadrant());
        command("LRS");
        command("TOR", "5");
        command("DAM");
        command("NAV", "7", "0.2");
        assertEquals(new Position(3, 5), game.currentQuadrant());
        command("LRS");
        command("TOR", "6"); // Miss
        command("PHA", "300");
        command("NAV", "7", "0.2");
        command("NAV", "7", "0.2");
        command("NAV", "7", "0.2");
        command("NAV", "7", "0.2");
        assertEquals(new Position(4, 5), game.currentQuadrant());
        command("LRS");
        command("TOR", "5");
        command("NAV", "6", "0.1");
        command("NAV", "7", "0.2");
        command("NAV", "7", "0.2");
        command("NAV", "7", "0.2");
        command("NAV", "7", "0.2");
        assertEquals(new Position(5, 5), game.currentQuadrant());
        command("LRS");
        command("COM", "4", "2", "7", "3", "1");
        command("TOR", "5.21");
        command("DAM");
        command("NAV", "7", "0.2");
        command("NAV", "7", "0.2");
        command("NAV", "7", "0.2");
        command("NAV", "7", "0.2");
        assertEquals(new Position(6, 5), game.currentQuadrant());
        command("LRS");
        command("TOR", "7");
        command("NAV", "7", "0.2");
        command("NAV", "7", "0.2");
        command("NAV", "7", "0.2");
        command("NAV", "7", "0.2");
        assertEquals(new Position(7, 5), game.currentQuadrant());
        command("LRS");
        command("TOR", "7");
        command("NAV", "7", "0.2");
        command("NAV", "7", "0.2");
        command("NAV", "7", "0.2");
        command("NAV", "7", "0.2");
        assertEquals(new Position(8, 5), game.currentQuadrant());
        command("LRS");
        command("TOR", "5");
        command("NAV", "2", "0.2");
        assertEquals(new Position(7, 6), game.currentQuadrant());
        command("LRS");
        command("NAV", "2", "0.2");
        command("NAV", "1", "0.2");
        command("NAV", "1", "0.2");
        command("NAV", "1", "0.2");
        assertEquals(new Position(7, 7), game.currentQuadrant());
        command("LRS");
        command("NAV", "1", "0.2");
        command("NAV", "3", "0.2");
        command("NAV", "3", "0.2");
        command("NAV", "3", "0.2");
        assertEquals(new Position(6, 7), game.currentQuadrant());
        command("LRS");
        command("NAV", "3", "0.2");
        command("NAV", "3", "0.2");
        command("NAV", "3", "0.2");
        command("NAV", "3", "0.2");
        assertEquals(new Position(5, 7), game.currentQuadrant());
        command("LRS");
        command("NAV", "3", "0.2");
        command("NAV", "3", "0.2");
        command("NAV", "3", "0.2");
        command("NAV", "3", "0.2");
        assertEquals(new Position(4, 7), game.currentQuadrant());
        command("LRS");
        command("NAV", "3", "0.2");
        command("NAV", "3", "0.2");
        command("NAV", "3", "0.2");
        command("NAV", "3", "0.2");
        assertEquals(new Position(3, 7), game.currentQuadrant());
        command("LRS");
        command("NAV", "3", "0.2");
        command("NAV", "3", "0.2");
        command("NAV", "3", "0.2");
        command("NAV", "3", "0.2");
        assertEquals(new Position(2, 7), game.currentQuadrant());
        command("DAM");
        command("NAV", "5", "6");
        assertEquals(new Position(2, 1), game.currentQuadrant());
        command("PHA", "500", "no");  // Victory
    }

    @Test
    void leaveGalaxy() {
        GameRandom random = new GameStaticImpl(new int[]
                {5, 1, 7, 2, 0, 6, 1, 0, 7, 2, 6, 1, 6, 1, 5, 5, 4},
                new float[]
                        {0.5f, 0.9f, 0.8f, 0.7f, 0.01f, 0.3f, 0.7f, 0.2f}
        );
        game = new Game(scanner, random);
        command("");
        command("SHE", "1000");
        assertEquals(new Position(6, 2), game.currentQuadrant());
        command("NAV", "5", "3");
        assertEquals(new Position(6, 1), game.currentQuadrant());
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

    private static class GameRandomImpl implements GameRandom {
        private final Random random;

        private final List<Integer> intSerie = new ArrayList<>();
        private final List<Float> floatSerie = new ArrayList<>();

        private GameRandomImpl(long seed) {
            this.random = new Random(seed);
        }

        @Override
        public int nextInt(int max) {
            int i = random.nextInt(max);
            intSerie.add(i);
            return i;
        }

        @Override
        public float nextFloat() {
            float v = random.nextFloat();
            floatSerie.add(v);
            return v;
        }

        @Override
        public String toString() {
            return "GameRandomImpl{" +
                    "intSerie=" + intSerie +
                    ", floatSerie=" + floatSerie +
                    '}';
        }
    }


}