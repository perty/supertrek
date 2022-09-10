package se.artcomputer.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

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
        game = new Game(scanner, new GameRandomImpl(1337));
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
        GameRandom random = new GameStaticImpl();
        game = new Game(scanner, random);
        command("");
        assertEquals(3000, game.totalEnergy());
        command("LRS");
        command("NAV", "5", "1");
        assertEquals(RED, game.condition());
        command("NAV", "4", "0.1", "AYE"); // Die and restart'
        command("");
        assertEquals(0, game.shields(), "Restore shields");
        assertEquals(10, game.torpedoes(), "Restore torpedoes");
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

    private static class GameRandomImpl implements GameRandom {
        private final Random random;

        private List<Integer> intSerie = new ArrayList<>();
        private List<Float> floatSerie = new ArrayList<>();

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

    private static class GameStaticImpl implements GameRandom {
        private final int[] intSerie;
        int intIndex = 0;
        private final float[] floatSerie;
        int floatIndex = 0;


        private GameStaticImpl() {
            intSerie = new int[]
                    {5, 1, 7, 2, 0, 6, 1, 0, 7, 2, 6, 1, 6, 1, 5, 5, 4};
            floatSerie = new float[]
                    {0.69746435F, 0.4494903F, 0.12317139F, 0.7728827F, 0.3042043F, 0.99736434F, 0.7500327F, 0.01985526F, 0.6313651F, 0.3158186F, 0.38277715F, 0.5313312F, 0.09699732F, 0.89311F, 0.29491454F, 0.9767721F, 0.7919281F, 0.43776292F, 0.42682332F, 0.19410717F, 0.4941737F, 0.44373834F, 0.22057855F, 0.7979292F, 0.5360546F, 0.37094784F, 0.41733837F, 0.17070818F, 0.8502881F, 0.18683225F, 0.39838427F, 0.89684564F, 0.103982806F, 0.70750123F, 0.21154153F, 0.07778102F, 0.43857723F, 0.65539396F, 0.12983912F, 0.67268825F, 0.17423093F, 0.28940177F, 0.8089972F, 0.89241F, 0.03645587F, 0.4353758F, 0.5450497F, 0.5130649F, 0.80246836F, 0.18973655F, 0.84320164F, 0.27297616F, 0.5568574F, 0.277052F, 0.33311975F, 0.06745446F, 0.32417488F, 0.39607692F, 0.31121665F, 0.6926265F, 0.25266463F, 0.800995F, 0.4369923F, 0.19543666F, 0.8208922F, 0.32811397F, 0.19932169F, 0.12016165F, 0.26191437F, 0.45130074F, 0.508779F, 0.49830323F, 0.0121282935F, 0.5775894F, 0.6193893F, 0.52257043F, 0.80847347F, 0.28401166F, 0.51357543F, 0.07451761F, 0.9261741F, 0.6254739F, 0.90357435F, 0.78550756F, 0.69230276F, 0.21183568F, 0.20359403F, 0.7678109F, 0.1624921F, 0.8483863F, 0.6196246F, 0.070121944F, 0.6204344F, 0.90353066F, 0.6205176F, 0.47975594F, 0.20957166F, 0.749389F, 0.14319342F, 0.69854146F, 0.878965F, 0.52589226F, 0.9795065F, 0.23416287F, 0.083218515F, 0.96039087F, 0.6563895F, 0.4221537F, 0.12165707F, 0.45818102F, 0.4473828F, 0.35564268F, 0.44354665F, 0.3841611F, 0.8047179F, 0.14708167F, 0.055455625F, 0.46496832F, 0.4511469F, 0.9656499F, 0.5112215F, 0.6400143F, 0.7307143F, 0.624841F, 0.30007762F, 0.21756F, 0.28900194F, 0.72205F, 0.19589901F, 0.3120377F, 0.4769929F, 0.702805F, 0.27103484F, 0.29688996F, 0.57483894F, 0.99406344F, 0.8627974F, 0.8232066F, 0.0015163422F, 0.6697341F, 0.51899505F, 0.5269334F, 0.1455282F, 0.5869727F, 0.6558099F, 0.23300242F, 0.7341475F, 0.25144643F, 0.21646953F, 0.04797077F, 0.46393824F, 0.93303746F, 0.30409294F, 0.04542035F, 0.73642266F, 0.90429455F, 0.71543443F, 0.15375775F, 0.8815106F, 0.74858004F, 0.5941601F, 0.33681762F, 0.5358744F, 0.38965857F, 0.5673068F, 0.9951484F, 0.8566227F, 0.5298048F, 0.10444963F, 0.101826906F, 0.5719037F, 0.15567166F, 0.5918182F, 0.16430157F, 0.47564828F, 0.11422831F, 0.92288095F, 0.16996115F, 0.05908811F, 0.13452917F, 0.75653297F, 0.50385445F, 0.91476184F, 0.9449041F, 0.6072892F, 0.51568604F, 0.47234905F, 0.8150352F, 0.17733067F, 0.1281271F, 0.29655492F, 0.22881567F, 0.31660032F, 0.6859924F, 0.83974975F, 0.6226613F, 0.8637975F, 0.97231764F, 0.20649296F, 0.7884202F, 0.70948064F, 0.14106911F, 0.036071062F, 0.8399101F, 0.2819031F, 0.24922174F, 0.17881495F, 0.19700325F, 0.3572538F, 0.21023452F, 0.27859962F, 0.39086163F, 0.7324881F, 0.38615817F, 0.16126537F, 0.72123265F, 0.098879874F, 0.23980135F, 0.8233649F, 0.29249722F, 0.62862635F, 0.30723614F, 0.5232433F, 0.18858695F, 0.21309483F, 0.5639345F, 0.044012606F, 0.7981922F, 0.35038912F, 0.83445996F, 0.39212567F, 0.65806365F, 0.028627276F, 0.54760873F, 0.9796006F, 0.69146883F, 0.73633933F, 0.81890845F, 0.6566184F, 0.43900704F, 0.71686023F, 0.93814176F, 0.37924677F, 0.65588975F, 0.8835155F, 0.7407021F, 0.6423291F, 0.798656F, 0.38835233F, 0.5960104F, 0.8024873F, 0.58443314F, 0.63988805F, 0.590907F, 0.38103312F, 0.5821222F, 0.16813433F, 0.75616723F, 0.49056816F, 0.39275855F, 0.52532434F, 0.07402313F, 0.3083942F, 0.8813374F, 0.8743778F, 0.8691504F, 0.2761854F, 0.7687722F};
        }

        @Override
        public int nextInt(int max) {
            int i = intSerie[intIndex++];
            intIndex = intIndex % intSerie.length;
            return i;
        }

        @Override
        public float nextFloat() {
            float v = floatSerie[floatIndex++];
            floatIndex = floatIndex % floatSerie.length;
            return v;
        }

    }
}